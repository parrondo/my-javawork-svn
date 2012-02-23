/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*     */ import com.dukascopy.charts.data.datacache.feed.ZeroFeedCommissionManager;
/*     */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*     */ import java.io.File;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CurvesDataLoader
/*     */ {
/*     */   protected static final SimpleDateFormat DATE_FORMAT;
/*     */   private static final DateFormat sharedDateFormat;
/*     */   private static final Logger LOGGER;
/*     */   private FeedDataProvider feedDataProvider;
/*     */   private static final IFeedCommissionManager ZERO_FEED_COMMISSION_MANAGER;
/*     */ 
/*     */   public CurvesDataLoader(FeedDataProvider feedDataProvider)
/*     */   {
/*  58 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void loadInCache(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingProgressListener loadingProgress, IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart, ChunkLoadingListener chunkLoadingListener, IFeedCommissionManager feedCommissionManager)
/*     */     throws DataCacheException
/*     */   {
/*  73 */     loadInCache(instrument, period, side, from, to, loadingProgress, intraperiodExistsPolicy, loadFromChunkStart, true, chunkLoadingListener, feedCommissionManager);
/*     */   }
/*     */ 
/*     */   public void loadInCache(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingProgressListener loadingProgress, IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart, boolean loadFromDFSIfFailedFromHTTP, ChunkLoadingListener chunkLoadingListener, IFeedCommissionManager feedCommissionManager)
/*     */     throws DataCacheException
/*     */   {
/* 113 */     assert ((period == Period.TICK) || (from == DataCacheUtils.getCandleStart(period, from)));
/* 114 */     assert ((period == Period.TICK) || (to == DataCacheUtils.getCandleStart(period, to)));
/*     */ 
/* 116 */     long[][] chunks = DataCacheUtils.separateChunksForCache(period, from, to);
/*     */ 
/* 118 */     if (chunks.length > 0)
/*     */     {
/* 121 */       boolean isDataInCache = false;
/*     */ 
/* 124 */       int i = chunks.length - 1;
/* 125 */       if (isDataChunkExists(instrument, period, side, chunks[i][0])) {
/* 126 */         isDataInCache = true;
/* 127 */         i--;
/*     */       }
/* 129 */       if (!isDataInCache)
/*     */       {
/* 133 */         isDataInCache = isDataInCache(instrument, period, side, DataCacheUtils.getNextChunkStart(period, chunks[i][0]), loadingProgress);
/* 134 */         if (loadingProgress.stopJob()) {
/* 135 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 139 */       for (; i >= 0; i--) {
/* 140 */         if (isDataInCache)
/*     */         {
/* 142 */           CandleData lastData = null;
/* 143 */           for (int j = 0; j <= i; j++) {
/* 144 */             if (loadingProgress.stopJob()) {
/* 145 */               return;
/*     */             }
/*     */ 
/* 148 */             if (!isDataChunkExists(instrument, period, side, chunks[j][0]))
/*     */             {
/* 150 */               if ((intraperiodExistsPolicy != IntraperiodExistsPolicy.FORCE_CHUNK_DOWNLOADING) && (intraperiodExistsPolicy != IntraperiodExistsPolicy.FORCE_DATA_UPDATE)) if (isDataCached(instrument, period, side, from > chunks[j][0] ? from : DataCacheUtils.getFirstCandleInChunkFast(period, chunks[j][0]), chunks[j][1]))
/*     */                 {
/* 153 */                   if (intraperiodExistsPolicy != IntraperiodExistsPolicy.DOWNLOAD_CHUNK_IN_BACKGROUND) break label942;
/* 155 */                   loadChunkDataInBackground(instrument, period, side, chunks[j][0], chunks[j][1]); break label942;
/*     */                 }
/*     */ 
/* 158 */               synchronized (sharedDateFormat) {
/* 159 */                 loadingProgress.dataLoaded(from, to, chunks[(chunks.length - 1 - i + j)][0], new StringBuilder().append("Downloading data interval from ").append(sharedDateFormat.format(new Date(chunks[j][0]))).append(" to ").append(sharedDateFormat.format(new Date(chunks[j][1]))).append("...").toString());
/*     */               }
/*     */ 
/* 163 */               boolean[] isFile = new boolean[1];
/* 164 */               Data[] data = loadChunkData(instrument, period, side, chunks[j][0], chunks[j][1], loadingProgress, isFile, loadFromDFSIfFailedFromHTTP);
/* 165 */               if (data != null) {
/* 166 */                 if ((period != Period.TICK) && (data.length != DataCacheUtils.getCandleCountInChunk(period, chunks[j][0]))) {
/* 167 */                   long expectedCandleStart = DataCacheUtils.getFirstCandleInChunkFast(period, chunks[j][0]);
/* 168 */                   if ((lastData != null) || ((data.length > 0) && (data[0].time == expectedCandleStart)))
/*     */                   {
/* 170 */                     data = fixData(data, lastData, instrument, period, from, to, chunks[j][0], chunks[j][1], expectedCandleStart, isFile[0]);
/*     */ 
/* 172 */                     saveChunkInCache(instrument, period, side, chunks[j][0], data);
/* 173 */                   } else if (data.length == 0)
/*     */                   {
/* 175 */                     saveChunkInCache(instrument, period, side, chunks[j][0], data);
/* 176 */                   } else if (j > 0)
/*     */                   {
/* 178 */                     lastData = getLastCandle(instrument, period, side, chunks[(j - 1)][1]);
/* 179 */                     if (lastData == null)
/*     */                     {
/* 181 */                       double openOfFirstCandle = ((CandleData)data[0]).open;
/* 182 */                       lastData = new CandleData(0L, ((CandleData)data[0]).open, ((CandleData)data[0]).open, ((CandleData)data[0]).open, openOfFirstCandle, 0.0D);
/*     */                     }
/*     */ 
/* 186 */                     data = fixData(data, lastData, instrument, period, from, to, chunks[j][0], chunks[j][1], expectedCandleStart, isFile[0]);
/*     */ 
/* 188 */                     saveChunkInCache(instrument, period, side, chunks[j][0], data);
/*     */                   }
/*     */                   else
/*     */                   {
/* 192 */                     double openOfFirstCandle = ((CandleData)data[0]).open;
/* 193 */                     lastData = new CandleData(0L, ((CandleData)data[0]).open, ((CandleData)data[0]).open, ((CandleData)data[0]).open, openOfFirstCandle, 0.0D);
/*     */ 
/* 196 */                     data = fixData(data, lastData, instrument, period, from, to, chunks[j][0], chunks[j][1], expectedCandleStart, isFile[0]);
/*     */ 
/* 198 */                     saveChunkInCache(instrument, period, side, chunks[j][0], data);
/*     */                   }
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/* 208 */                   checkData(data, chunks[j][0], chunks[j][1], instrument, period, isFile[0]);
/* 209 */                   saveChunkInCache(instrument, period, side, chunks[j][0], data);
/*     */                 }
/* 211 */                 if ((period != Period.TICK) && (data.length > 0)) {
/* 212 */                   lastData = (CandleData)data[(data.length - 1)];
/*     */                 }
/*     */               }
/*     */               else
/*     */               {
/* 217 */                 if ((loadingProgress.stopJob()) || (!loadFromDFSIfFailedFromHTTP)) {
/* 218 */                   return;
/*     */                 }
/* 220 */                 throw new DataCacheException("null data returned from curves protocol handler");
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 225 */             label942: if (chunkLoadingListener != null) {
/* 226 */               chunkLoadingListener.chunkLoaded(chunks[j]);
/*     */             }
/*     */           }
/* 229 */           if (chunkLoadingListener != null) {
/* 230 */             for (int j = i + 1; j < chunks.length; j++) {
/* 231 */               chunkLoadingListener.chunkLoaded(chunks[j]);
/*     */             }
/*     */           }
/* 234 */           return;
/*     */         }
/*     */ 
/* 237 */         synchronized (sharedDateFormat) {
/* 238 */           loadingProgress.dataLoaded(from, to, chunks[(chunks.length - 1 - i)][0], new StringBuilder().append("Downloading data interval from ").append(sharedDateFormat.format(new Date(chunks[i][0]))).append(" to ").append(sharedDateFormat.format(new Date(chunks[i][1]))).append("...").toString());
/*     */         }
/*     */ 
/* 242 */         boolean thereWasSomeData = loadAndSaveIntradayData(instrument, period, side, (from > chunks[i][0]) && (!loadFromChunkStart) ? from : chunks[i][0], chunks[i][1] > to ? to : chunks[i][1], loadingProgress, intraperiodExistsPolicy, feedCommissionManager);
/*     */ 
/* 252 */         if (!loadingProgress.stopJob()) {
/* 253 */           if (thereWasSomeData)
/*     */           {
/* 255 */             isDataInCache = true;
/*     */           } else {
/* 257 */             if (i == 0)
/*     */             {
/*     */               continue;
/*     */             }
/* 261 */             if (isDataChunkExists(instrument, period, side, chunks[(i - 1)][0])) {
/* 262 */               isDataInCache = true; } else {
/* 263 */               if ((intraperiodExistsPolicy != IntraperiodExistsPolicy.FORCE_CHUNK_DOWNLOADING) && (intraperiodExistsPolicy != IntraperiodExistsPolicy.FORCE_DATA_UPDATE)) if (isDataCached(instrument, period, side, from > chunks[(i - 1)][0] ? from : DataCacheUtils.getFirstCandleInChunkFast(period, chunks[(i - 1)][0]), chunks[(i - 1)][1]))
/*     */                 {
/* 269 */                   if (intraperiodExistsPolicy != IntraperiodExistsPolicy.DOWNLOAD_CHUNK_IN_BACKGROUND) continue;
/* 270 */                   loadChunkDataInBackground(instrument, period, side, chunks[(i - 1)][0], chunks[(i - 1)][1]); continue;
/*     */                 }
/*     */ 
/*     */ 
/* 274 */               isDataInCache = isDataInCache(instrument, period, side, chunks[i][0], loadingProgress);
/* 275 */               if (loadingProgress.stopJob()) {
/* 276 */                 return;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 282 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 286 */       if (chunkLoadingListener != null)
/* 287 */         for (int j = i + 1; j < chunks.length; j++)
/* 288 */           chunkLoadingListener.chunkLoaded(chunks[j]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void loadInProgressCandle(Instrument instrument, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress, IFeedCommissionManager feedCommissionManager)
/*     */     throws DataCacheException
/*     */   {
/* 301 */     Data[] data = FeedDataProvider.getCurvesProtocolHandler().loadInProgressCandle(instrument, to, loadingProgress);
/* 302 */     if ((data == null) || (data.length == 0)) {
/* 303 */       return;
/*     */     }
/*     */ 
/* 306 */     boolean feedCommissionExists = feedCommissionManager.hasCommission(instrument);
/*     */ 
/* 308 */     Period[] periods = { Period.MONTHLY, Period.WEEKLY, Period.DAILY, Period.FOUR_HOURS, Period.ONE_HOUR, Period.THIRTY_MINS, Period.FIFTEEN_MINS, Period.TEN_MINS, Period.FIVE_MINS, Period.ONE_MIN, Period.TEN_SECS };
/*     */ 
/* 310 */     for (int i = 0; i < periods.length; i++) {
/* 311 */       CandleData candle = (CandleData)data[(i * 2)];
/* 312 */       if (candle == null) {
/*     */         continue;
/*     */       }
/* 315 */       if (candle.getTime() != DataCacheUtils.getCandleStartFast(periods[i], candle.getTime())) {
/* 316 */         DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 317 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 318 */         throw new DataCacheException(new StringBuilder().append("Received candle for period [").append(periods[i]).append("] has incorrect start time [").append(format.format(Long.valueOf(candle.getTime()))).append("]").toString());
/*     */       }
/* 320 */       if ((candle.open != 0.0D) && (candle.close != 0.0D) && (candle.high != 0.0D) && (candle.low != 0.0D)) {
/* 321 */         if (feedCommissionExists) {
/* 322 */           candle = feedCommissionManager.applyFeedCommissionToCandle(instrument, OfferSide.ASK, candle);
/*     */         }
/* 324 */         listener.newCandle(instrument, periods[i], OfferSide.ASK, candle.getTime(), candle.getOpen(), candle.getClose(), candle.getLow(), candle.getHigh(), candle.getVolume());
/*     */       }
/* 326 */       candle = (CandleData)data[(i * 2 + 1)];
/* 327 */       if ((candle.open != 0.0D) && (candle.close != 0.0D) && (candle.high != 0.0D) && (candle.low != 0.0D)) {
/* 328 */         if (feedCommissionExists) {
/* 329 */           candle = feedCommissionManager.applyFeedCommissionToCandle(instrument, OfferSide.BID, candle);
/*     */         }
/* 331 */         listener.newCandle(instrument, periods[i], OfferSide.BID, candle.getTime(), candle.getOpen(), candle.getClose(), candle.getLow(), candle.getHigh(), candle.getVolume());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void loadOrders(String accountId, Instrument instrument, long from, long to, IntraperiodExistsPolicy intraperiodExistsPolicy, LoadingProgressListener loadingProgress)
/*     */     throws DataCacheException
/*     */   {
/* 339 */     long[][] chunks = DataCacheUtils.separateOrderChunksForCache(from, to);
/*     */ 
/* 341 */     if (chunks.length > 0)
/* 342 */       for (long[] chunk : chunks) {
/* 343 */         if (loadingProgress.stopJob()) {
/* 344 */           return;
/*     */         }
/* 346 */         if (isFullOrderChunkExists(accountId, instrument, chunk[0]))
/*     */           continue;
/* 348 */         long currentTime = this.feedDataProvider.getCurrentTime();
/* 349 */         if (chunk[1] > currentTime)
/*     */         {
/* 351 */           IOrdersProvider ordersProvider = this.feedDataProvider.getOrdersProvider();
/*     */ 
/* 353 */           if (isOrdersDataCached(accountId, instrument, chunk[0]))
/*     */           {
/* 355 */             OrdersChunkData ordersData = readOrdersData(accountId, instrument, chunk[0]);
/* 356 */             assert (ordersData != null);
/* 357 */             if (!isOpenOrderMissing(instrument, ordersData))
/*     */             {
/* 359 */               long lastOrderUpdateTime = this.feedDataProvider.getLocalCacheManager().getLastOrderUpdateTime(instrument);
/* 360 */               if (lastOrderUpdateTime == -9223372036854775808L)
/*     */               {
/* 362 */                 lastOrderUpdateTime = currentTime;
/*     */               }
/* 364 */               if (ordersData.to < lastOrderUpdateTime)
/*     */               {
/* 367 */                 ICurvesProtocolHandler.OrdersDataStruct data = FeedDataProvider.getCurvesProtocolHandler().loadOrders(instrument, ordersData.to, chunk[1], loadingProgress);
/*     */                 Set existentOpenGroupIds;
/*     */                 Iterator i$;
/* 368 */                 if (data != null)
/*     */                 {
/* 370 */                   Collection[] orderDataList = ordersProvider.processHistoricalData(instrument, ordersData.to, chunk[1], data);
/*     */                   Set existentOrderIds;
/* 372 */                   if (orderDataList[0] != null) {
/* 373 */                     existentOrderIds = new HashSet(ordersData.orders.size());
/* 374 */                     for (OrderHistoricalData orderHistoricalData : ordersData.orders) {
/* 375 */                       existentOrderIds.add(orderHistoricalData.getOrderGroupId());
/*     */                     }
/* 377 */                     for (OrderHistoricalData orderHistoricalData : orderDataList[0]) {
/* 378 */                       if (!existentOrderIds.contains(orderHistoricalData.getOrderGroupId())) {
/* 379 */                         ordersData.orders.add(orderHistoricalData);
/*     */                       }
/*     */                     }
/*     */                   }
/* 383 */                   Collections.sort(ordersData.orders, new Comparator()
/*     */                   {
/*     */                     public int compare(OrderHistoricalData o1, OrderHistoricalData o2) {
/* 386 */                       if (o1.getHistoryStart() > o2.getHistoryStart())
/* 387 */                         return 1;
/* 388 */                       if (o1.getHistoryStart() < o2.getHistoryStart()) {
/* 389 */                         return -1;
/*     */                       }
/* 391 */                       return 0;
/*     */                     }
/*     */                   });
/* 394 */                   if (orderDataList[1] != null) {
/* 395 */                     existentOpenGroupIds = new HashSet(ordersData.openGroupsIds);
/*     */ 
/* 397 */                     for (i$ = orderDataList[1].iterator(); i$.hasNext(); ) { Object orderGroupId = i$.next();
/* 398 */                       if (!existentOpenGroupIds.contains(orderGroupId)) {
/* 399 */                         ordersData.openGroupsIds.add((String)orderGroupId);
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/* 404 */                 ordersData.from = chunk[0];
/* 405 */                 ordersData.to = currentTime;
/* 406 */                 ordersData.full = false;
/* 407 */                 saveOrdersData(accountId, instrument, chunk[0], ordersData);
/*     */               }
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 414 */               loadAndSaveOrdersChunk(accountId, instrument, from, to, loadingProgress, chunk);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 419 */             loadAndSaveOrdersChunk(accountId, instrument, from, to, loadingProgress, chunk);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 424 */           ICurvesProtocolHandler.OrdersDataStruct data = FeedDataProvider.getCurvesProtocolHandler().loadOrders(instrument, chunk[0], chunk[1], loadingProgress);
/* 425 */           ArrayList sortedOrderData = new ArrayList(0);
/* 426 */           ArrayList openOrderIds = new ArrayList(0);
/*     */           Iterator i$;
/* 427 */           if (data != null)
/*     */           {
/* 429 */             IOrdersProvider ordersProvider = this.feedDataProvider.getOrdersProvider();
/* 430 */             Collection[] orderDataList = ordersProvider.processHistoricalData(instrument, chunk[0], chunk[1], data);
/*     */ 
/* 432 */             if (orderDataList[0] != null)
/*     */             {
/* 434 */               sortedOrderData = new ArrayList(orderDataList[0]);
/* 435 */               Collections.sort(sortedOrderData, new Comparator()
/*     */               {
/*     */                 public int compare(OrderHistoricalData o1, OrderHistoricalData o2) {
/* 438 */                   if (o1.getHistoryStart() > o2.getHistoryStart())
/* 439 */                     return 1;
/* 440 */                   if (o1.getHistoryStart() < o2.getHistoryStart()) {
/* 441 */                     return -1;
/*     */                   }
/* 443 */                   return 0;
/*     */                 } } );
/*     */             }
/* 447 */             if (orderDataList[1] != null) {
/* 448 */               openOrderIds = new ArrayList();
/* 449 */               for (i$ = orderDataList[1].iterator(); i$.hasNext(); ) { Object orderGroupId = i$.next();
/* 450 */                 openOrderIds.add((String)orderGroupId);
/*     */               }
/*     */             }
/*     */           }
/* 454 */           OrdersChunkData ordersData = new OrdersChunkData();
/* 455 */           ordersData.from = chunk[0];
/* 456 */           ordersData.to = chunk[1];
/* 457 */           ordersData.full = true;
/* 458 */           ordersData.orders = sortedOrderData;
/* 459 */           ordersData.openGroupsIds = openOrderIds;
/* 460 */           saveOrdersData(accountId, instrument, chunk[0], ordersData);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private void loadAndSaveOrdersChunk(String accountId, Instrument instrument, long from, long to, LoadingProgressListener loadingProgress, long[] chunk)
/*     */     throws DataCacheException
/*     */   {
/* 470 */     IOrdersProvider ordersProvider = this.feedDataProvider.getOrdersProvider();
/* 471 */     ICurvesProtocolHandler.OrdersDataStruct data = FeedDataProvider.getCurvesProtocolHandler().loadOrders(instrument, chunk[0], chunk[1], loadingProgress);
/* 472 */     ArrayList sortedOrderData = new ArrayList(0);
/* 473 */     ArrayList openOrderIds = new ArrayList(0);
/*     */     Iterator i$;
/* 474 */     if (data != null)
/*     */     {
/* 476 */       Collection[] orderDataList = ordersProvider.processHistoricalData(instrument, chunk[0], chunk[1], data);
/* 477 */       if (orderDataList[0] != null)
/*     */       {
/* 480 */         sortedOrderData = new ArrayList(orderDataList[0]);
/* 481 */         Collections.sort(sortedOrderData, new Comparator()
/*     */         {
/*     */           public int compare(OrderHistoricalData o1, OrderHistoricalData o2) {
/* 484 */             if (o1.getHistoryStart() > o2.getHistoryStart())
/* 485 */               return 1;
/* 486 */             if (o1.getHistoryStart() < o2.getHistoryStart()) {
/* 487 */               return -1;
/*     */             }
/* 489 */             return 0;
/*     */           } } );
/*     */       }
/* 493 */       if (orderDataList[1] != null) {
/* 494 */         openOrderIds = new ArrayList();
/* 495 */         for (i$ = orderDataList[1].iterator(); i$.hasNext(); ) { Object orderGroupId = i$.next();
/* 496 */           openOrderIds.add((String)orderGroupId);
/*     */         }
/*     */       }
/*     */     }
/* 500 */     OrdersChunkData ordersData = new OrdersChunkData();
/* 501 */     ordersData.from = chunk[0];
/* 502 */     ordersData.to = this.feedDataProvider.getCurrentTime();
/* 503 */     ordersData.full = false;
/* 504 */     ordersData.orders = sortedOrderData;
/* 505 */     ordersData.openGroupsIds = openOrderIds;
/* 506 */     saveOrdersData(accountId, instrument, chunk[0], ordersData);
/*     */   }
/*     */ 
/*     */   private void loadChunkDataInBackground(Instrument instrument, Period period, OfferSide side, long from, long to) throws DataCacheException {
/* 510 */     if (period == Period.TICK)
/* 511 */       this.feedDataProvider.loadTicksDataInCache(instrument, from, to, new LoadingProgressListener() {
/*     */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */         }
/*     */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 515 */           if ((!allDataLoaded) && (e != null))
/* 516 */             CurvesDataLoader.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */ 
/*     */         public boolean stopJob() {
/* 520 */           return false;
/*     */         }
/*     */       });
/* 524 */     else this.feedDataProvider.loadCandlesDataInCache(instrument, period, side, from, to, new LoadingProgressListener() {
/*     */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */         }
/*     */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 528 */           if ((!allDataLoaded) && (e != null))
/* 529 */             CurvesDataLoader.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */ 
/*     */         public boolean stopJob() {
/* 533 */           return false;
/*     */         }
/*     */       }); 
/*     */   }
/*     */ 
/*     */   private void loadOrderDataInBackground(String accountId, Instrument instrument, long from, long to) throws DataCacheException
/*     */   {
/* 540 */     this.feedDataProvider.loadOrdersHistoricalDataInCache(instrument, from, to, new LoadingProgressListener() {
/*     */       public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 545 */         if ((!allDataLoaded) && (e != null))
/* 546 */           CurvesDataLoader.LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */ 
/*     */       public boolean stopJob()
/*     */       {
/* 551 */         return false;
/*     */       } } );
/*     */   }
/*     */ 
/*     */   protected CandleData getLastCandle(Instrument instrument, Period period, OfferSide side, long time) throws DataCacheException {
/* 557 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 558 */     List candle = new ArrayList();
/* 559 */     time = DataCacheUtils.getCandleStart(period, time);
/* 560 */     long[] chunk = { DataCacheUtils.getChunkStartFast(period, time), DataCacheUtils.getChunkEndFast(period, time) };
/* 561 */     File file = localCacheManager.getChunkFile(instrument, period, side, chunk[0]);
/* 562 */     localCacheManager.readCandlesFromChunkFile(file, instrument, period, side, chunk[0], time, time, new LiveFeedListener(candle)
/*     */     {
/*     */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */       {
/* 572 */         this.val$candle.add(new CandleData(time, open, close, low, high, vol));
/*     */       }
/*     */ 
/*     */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */       {
/*     */       }
/*     */     }
/*     */     , null, false, ZERO_FEED_COMMISSION_MANAGER);
/*     */ 
/* 582 */     if (candle.isEmpty()) {
/* 583 */       return null;
/*     */     }
/* 585 */     return (CandleData)candle.get(0);
/*     */   }
/*     */ 
/*     */   protected void checkData(Data[] data, long from, long to, Instrument instrument, Period period, boolean isFile) throws DataCacheException
/*     */   {
/* 590 */     if (period == Period.TICK) {
/* 591 */       long prevTime = from;
/* 592 */       for (Data dataElement : data) {
/* 593 */         long time = dataElement.time;
/* 594 */         if ((time < prevTime) || (time > to)) {
/* 595 */           DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 596 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 597 */           throw new DataCacheException(new StringBuilder().append("Wrong data from ").append(isFile ? "chunk file" : "curves server").append(", received tick with time [").append(format.format(new Date(time))).append("] in chunk for instrument [").append(instrument).append("] from [").append(format.format(new Date(from))).append("] to [").append(format.format(new Date(to))).append("], previous tick time [").append(format.format(new Date(prevTime))).append("]").toString());
/*     */         }
/*     */ 
/* 602 */         prevTime = time;
/*     */       }
/*     */     } else {
/* 605 */       long candleTime = DataCacheUtils.getFirstCandleInChunkFast(period, from);
/* 606 */       for (Data dataElement : data) {
/* 607 */         long time = dataElement.time;
/* 608 */         if (time != candleTime) {
/* 609 */           DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 610 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 611 */           throw new DataCacheException(new StringBuilder().append("Wrong data from ").append(isFile ? "chunk file" : "curves server").append(", received candle with time [").append(format.format(new Date(time))).append("] in chunk for instrument [").append(instrument).append("] period [").append(period).append("] from [").append(format.format(new Date(from))).append("] to [").append(format.format(new Date(to))).append("], expected candle time [").append(format.format(new Date(candleTime))).append("]").toString());
/*     */         }
/*     */ 
/* 616 */         candleTime = DataCacheUtils.getNextCandleStartFast(period, candleTime);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Data[] fixData(Data[] data, CandleData lastData, Instrument instrument, Period period, long from, long to, long chunkStart, long chunkEnd, long candleTime, boolean isFile) throws DataCacheException
/*     */   {
/* 623 */     assert (candleTime == DataCacheUtils.getCandleStart(period, candleTime));
/* 624 */     List newData = new ArrayList();
/* 625 */     long candleCount = DataCacheUtils.getCandleCountInChunk(period, candleTime);
/* 626 */     int i = 0; for (int j = 0; i < candleCount; i++) {
/* 627 */       Data dataElement = j < data.length ? data[j] : null;
/* 628 */       if ((dataElement != null) && (dataElement.time == candleTime)) {
/* 629 */         newData.add(dataElement);
/* 630 */         lastData = (CandleData)dataElement;
/* 631 */         j++; } else {
/* 632 */         if ((dataElement != null) && (dataElement.time < candleTime)) {
/* 633 */           DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 634 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 635 */           throw new DataCacheException(new StringBuilder().append("Wrong data from ").append(isFile ? "chunk file" : "data feed server").append(", time of the data element [").append(dataElement.getTime()).append("], index [").append(j).append("] is less than expected time [").append(candleTime).append("], request - instrument [").append(instrument).append("] period [").append(period).append("] from [").append(format.format(new Date(from))).append("] to [").append(format.format(new Date(to))).append("], checking interval from [").append(format.format(new Date(chunkStart))).append("] to [").append(format.format(new Date(chunkEnd))).append("]").toString());
/*     */         }
/*     */ 
/* 641 */         CandleData candle = new CandleData();
/* 642 */         candle.time = candleTime;
/* 643 */         candle.open = lastData.close;
/* 644 */         candle.close = lastData.close;
/* 645 */         candle.low = lastData.close;
/* 646 */         candle.high = lastData.close;
/* 647 */         candle.vol = 0.0D;
/* 648 */         newData.add(candle);
/*     */       }
/* 650 */       candleTime = DataCacheUtils.getNextCandleStart(period, candleTime);
/*     */     }
/* 652 */     data = (Data[])newData.toArray(new Data[newData.size()]);
/* 653 */     checkStartTimes(data, period);
/* 654 */     return data;
/*     */   }
/*     */ 
/*     */   protected boolean isOpenOrderMissing(Instrument instrument, OrdersChunkData chunkData) {
/* 658 */     Set openOrderIds = this.feedDataProvider.getOrdersProvider().getOrdersForInstrument(instrument).keySet();
/* 659 */     for (String orderGroupId : chunkData.openGroupsIds) {
/* 660 */       if (!openOrderIds.contains(orderGroupId))
/*     */       {
/* 662 */         return true;
/*     */       }
/*     */     }
/* 665 */     return false;
/*     */   }
/*     */ 
/*     */   protected boolean isDataChunkExists(Instrument instrument, Period period, OfferSide side, long from) throws DataCacheException {
/* 669 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 670 */     return localCacheManager.isDataChunkExists(instrument, period, side, from);
/*     */   }
/*     */ 
/*     */   protected boolean isFullOrderChunkExists(String accountId, Instrument instrument, long chunkStart) throws DataCacheException {
/* 674 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 675 */     return localCacheManager.isFullOrderChunkExists(accountId, instrument, chunkStart, this.feedDataProvider.getOrdersProvider().getOrdersForInstrument(instrument).keySet());
/*     */   }
/*     */ 
/*     */   protected boolean isDataCached(Instrument instrument, Period period, OfferSide side, long from, long to) throws DataCacheException {
/* 679 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 680 */     return localCacheManager.isDataCached(instrument, period, side, from, to);
/*     */   }
/*     */ 
/*     */   protected boolean isOrdersDataCached(String accountId, Instrument instrument, long chunkStart) throws DataCacheException {
/* 684 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 685 */     return localCacheManager.isOrderDataCached(accountId, instrument, chunkStart);
/*     */   }
/*     */ 
/*     */   protected boolean isDataInCache(Instrument instrument, Period period, OfferSide side, long from, LoadingProgressListener loadingProgress) throws DataCacheException {
/* 689 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 690 */     long currentTime = this.feedDataProvider.getCurrentTime();
/* 691 */     if ((currentTime != -9223372036854775808L) && (currentTime < from))
/*     */     {
/* 693 */       return false;
/*     */     }
/* 695 */     if (localCacheManager.isAnyDataChunkExistsAfter(instrument, period, side, from))
/*     */     {
/* 697 */       return true;
/*     */     }
/* 699 */     long nextChunkStart = DataCacheUtils.getNextChunkStart(period, from);
/* 700 */     if (currentTime >= nextChunkStart)
/*     */     {
/* 702 */       return true;
/*     */     }
/*     */ 
/* 707 */     if (period != Period.TICK) {
/* 708 */       long fromFixed = DataCacheUtils.getCandleStart(period, from);
/* 709 */       if (fromFixed < from) {
/* 710 */         from = DataCacheUtils.getNextCandleStart(period, from);
/*     */       }
/*     */     }
/* 713 */     Data[] data = FeedDataProvider.getCurvesProtocolHandler().loadData(instrument, period, side, from, period == Period.TICK ? from + 300000L : from, false, loadingProgress);
/* 714 */     return (!loadingProgress.stopJob()) && (data.length > 0);
/*     */   }
/*     */ 
/*     */   protected Data[] loadChunkData(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingProgressListener loadingProgress, boolean[] isFile) throws DataCacheException {
/* 718 */     Data[] result = loadChunkData(instrument, period, side, from, to, loadingProgress, isFile, true);
/* 719 */     return result;
/*     */   }
/*     */ 
/*     */   protected Data[] loadChunkData(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingProgressListener loadingProgress, boolean[] isFile, boolean loadFromDFSIfFailedFromHTTP)
/*     */     throws DataCacheException
/*     */   {
/* 732 */     Data[] data = FeedDataProvider.getCurvesProtocolHandler().loadFile(instrument, period, side, from, loadingProgress);
/* 733 */     if (data == null) {
/* 734 */       if (!loadFromDFSIfFailedFromHTTP) {
/* 735 */         return null;
/*     */       }
/*     */ 
/* 739 */       if (period != Period.TICK) {
/* 740 */         long fromFixed = DataCacheUtils.getCandleStart(period, from);
/* 741 */         if (fromFixed < from) {
/* 742 */           from = DataCacheUtils.getNextCandleStart(period, from);
/*     */         }
/*     */       }
/* 745 */       isFile[0] = false;
/* 746 */       return FeedDataProvider.getCurvesProtocolHandler().loadData(instrument, period, side, from, to, false, loadingProgress);
/*     */     }
/*     */ 
/* 749 */     isFile[0] = true;
/* 750 */     return data;
/*     */   }
/*     */ 
/*     */   protected void saveChunkInCache(Instrument instrument, Period period, OfferSide side, long from, Data[] data) throws DataCacheException
/*     */   {
/* 755 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 756 */     localCacheManager.saveChunkInCache(instrument, period, side, from, data);
/*     */   }
/*     */ 
/*     */   protected void saveOrdersData(String accountId, Instrument instrument, long chunkStart, OrdersChunkData data) throws DataCacheException {
/* 760 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 761 */     localCacheManager.saveOrdersData(accountId, instrument, chunkStart, data);
/*     */   }
/*     */ 
/*     */   private OrdersChunkData readOrdersData(String accountId, Instrument instrument, long chunkStart) throws DataCacheException {
/* 765 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 766 */     return localCacheManager.readOrdersData(accountId, instrument, chunkStart);
/*     */   }
/*     */ 
/*     */   protected boolean loadAndSaveIntradayData(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingProgressListener loadingProgress, IntraperiodExistsPolicy intraperiodExistsPolicy, IFeedCommissionManager feedCommissionManager)
/*     */     throws DataCacheException
/*     */   {
/* 780 */     if (period != Period.TICK) {
/* 781 */       long fromFixed = DataCacheUtils.getCandleStart(period, from);
/* 782 */       if (fromFixed < from) {
/* 783 */         from = DataCacheUtils.getNextCandleStart(period, from);
/*     */       }
/*     */     }
/* 786 */     assert ((period == Period.TICK) || (to == DataCacheUtils.getCandleStart(period, to)));
/* 787 */     boolean someDataLoaded = false;
/* 788 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/*     */     long[][] intervals;
/*     */     long[][] intervals;
/* 790 */     if (intraperiodExistsPolicy == IntraperiodExistsPolicy.FORCE_DATA_UPDATE)
/* 791 */       intervals = new long[][] { { from, to } };
/*     */     else {
/* 793 */       intervals = localCacheManager.getIntraperiodIntervalsToLoad(instrument, period, side, from, to);
/*     */     }
/* 795 */     for (int i = 0; i < intervals.length; i++) {
/* 796 */       assert ((period == Period.TICK) || (intervals[i][0] == DataCacheUtils.getCandleStart(period, intervals[i][0])));
/* 797 */       assert ((period == Period.TICK) || (intervals[i][1] == DataCacheUtils.getCandleStart(period, intervals[i][1])));
/*     */ 
/* 799 */       if ((period == Period.TICK) && (intervals[i][0] - 60000L >= DataCacheUtils.getChunkStartFast(period, intervals[i][0]))) {
/* 800 */         intervals[i][0] -= 60000L;
/*     */       }
/* 802 */       Data[] data = FeedDataProvider.getCurvesProtocolHandler().loadData(instrument, period, side, intervals[i][0], intervals[i][1], true, loadingProgress);
/* 803 */       if (!loadingProgress.stopJob()) {
/* 804 */         if (data.length > 0) {
/* 805 */           someDataLoaded = true;
/*     */         }
/* 807 */         if (period != Period.TICK) {
/* 808 */           data = fixIntraperiodData(data, period);
/* 809 */           if ((LOGGER.isDebugEnabled()) && (data.length > 0) && (data[(data.length - 1)].time < intervals[i][1])) {
/* 810 */             DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 811 */             format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 812 */             LOGGER.debug(new StringBuilder().append("Not all data was loaded for interval [").append(format.format(Long.valueOf(intervals[i][0]))).append("] to [").append(format.format(Long.valueOf(intervals[i][1]))).append("], last loaded data element time is [").append(format.format(Long.valueOf(data[(data.length - 1)].time))).append("]").toString());
/*     */           }
/*     */         }
/*     */ 
/* 816 */         data = feedCommissionManager.applyFeedCommissionToData(instrument, period, side, data);
/* 817 */         saveIntraPeriodData(instrument, period, side, data, intervals[i][0] == DataCacheUtils.getChunkStart(period, intervals[i][0]));
/*     */       } else {
/* 819 */         return false;
/*     */       }
/*     */     }
/* 822 */     return someDataLoaded;
/*     */   }
/*     */ 
/*     */   protected Data[] fixIntraperiodData(Data[] data, Period period) throws DataCacheException {
/* 826 */     if (data.length < 2) {
/* 827 */       checkStartTimes(data, period);
/* 828 */       return data;
/*     */     }
/* 830 */     if (data.length < DataCacheUtils.getCandlesCountBetweenFast(period, data[0].time, data[(data.length - 1)].time)) {
/* 831 */       List newData = new ArrayList();
/* 832 */       IntraPeriodCandleData lastData = (IntraPeriodCandleData)data[0];
/* 833 */       newData.add(lastData);
/* 834 */       long nextTime = DataCacheUtils.getNextCandleStartFast(period, lastData.time);
/* 835 */       for (int i = 1; i < data.length; i++) {
/* 836 */         while (data[i].time > nextTime) {
/* 837 */           IntraPeriodCandleData candle = new IntraPeriodCandleData();
/* 838 */           candle.time = nextTime;
/* 839 */           candle.open = lastData.close;
/* 840 */           candle.close = lastData.close;
/* 841 */           candle.low = lastData.close;
/* 842 */           candle.high = lastData.close;
/* 843 */           candle.vol = 0.0D;
/* 844 */           newData.add(candle);
/* 845 */           lastData = candle;
/* 846 */           nextTime = DataCacheUtils.getNextCandleStartFast(period, lastData.time);
/*     */         }
/* 848 */         newData.add(data[i]);
/* 849 */         lastData = (IntraPeriodCandleData)data[i];
/* 850 */         nextTime = DataCacheUtils.getNextCandleStartFast(period, lastData.time);
/*     */       }
/* 852 */       data = (Data[])newData.toArray(new Data[newData.size()]);
/* 853 */       checkStartTimes(data, period);
/* 854 */       return data;
/*     */     }
/* 856 */     checkStartTimes(data, period);
/* 857 */     return data;
/*     */   }
/*     */ 
/*     */   protected void checkStartTimes(Data[] data, Period period)
/*     */     throws DataCacheException
/*     */   {
/* 863 */     for (Data dataElement : data)
/* 864 */       if (dataElement.time != DataCacheUtils.getCandleStartFast(period, dataElement.time)) {
/* 865 */         DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 866 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 867 */         throw new DataCacheException(new StringBuilder().append("Wrong data from curves server, received candle with time [").append(format.format(Long.valueOf(dataElement.time))).append("] for period [").append(period).append("], expected candle time [").append(format.format(Long.valueOf(DataCacheUtils.getCandleStartFast(period, dataElement.time)))).append("]").toString());
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void saveIntraPeriodData(Instrument instrument, Period period, OfferSide side, Data[] data, boolean dataFromChunkStart)
/*     */     throws DataCacheException
/*     */   {
/* 874 */     LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 875 */     localCacheManager.saveIntraperiodData(instrument, period, side, data, dataFromChunkStart);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  38 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*     */ 
/*  40 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  45 */     LOGGER = LoggerFactory.getLogger(CurvesDataLoader.class);
/*     */ 
/*  50 */     sharedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*  51 */     sharedDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/*  54 */     ZERO_FEED_COMMISSION_MANAGER = new ZeroFeedCommissionManager();
/*     */   }
/*     */ 
/*     */   public static enum IntraperiodExistsPolicy
/*     */   {
/*  35 */     FORCE_CHUNK_DOWNLOADING, DOWNLOAD_CHUNK_IN_BACKGROUND, USE_INTRAPERIOD_WHEN_POSSIBLE, FORCE_DATA_UPDATE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.CurvesDataLoader
 * JD-Core Version:    0.6.0
 */