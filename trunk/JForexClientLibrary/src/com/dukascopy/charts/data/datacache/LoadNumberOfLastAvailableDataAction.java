/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadNumberOfLastAvailableDataAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  25 */   private static final Logger LOGGER = LoggerFactory.getLogger(LoadNumberOfLastAvailableDataAction.class);
/*     */ 
/*  27 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*     */   private final Instrument instrument;
/*     */   private final Period period;
/*     */   private final OfferSide side;
/*     */   private final int numberOfCandles;
/*     */   private final long to;
/*     */   private final LiveFeedListener listener;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private final Filter filter;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private int loadedNumberOfCandles;
/*     */   private boolean gapDetected;
/*     */   private int candlesFiltered;
/*     */   private final LoadCustomPeriodNumberOfLastAvailableDataAction loadCustomPeriodNumberOfLastAvailableDataAction;
/*     */ 
/*     */   public LoadNumberOfLastAvailableDataAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, int numberOfCandles, long to, Filter filter, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace)
/*     */     throws DataCacheException
/*     */   {
/*  54 */     super(loadingProgress);
/*  55 */     this.instrument = instrument;
/*  56 */     this.period = period;
/*  57 */     this.side = side;
/*  58 */     this.numberOfCandles = numberOfCandles;
/*  59 */     this.to = to;
/*  60 */     this.listener = candleListener;
/*  61 */     this.stackTrace = stackTrace;
/*  62 */     this.feedDataProvider = feedDataProvider;
/*  63 */     this.filter = filter;
/*  64 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/*  66 */     if ((instrument == null) || (period == null) || (this.numberOfCandles <= 0) || (loadingProgress == null) || (period == Period.TICK)) {
/*  67 */       throw new DataCacheException("Wrong parameters: instrument=" + instrument + " / period=" + period + " / " + numberOfCandles + " before " + to + " / " + loadingProgress);
/*     */     }
/*     */ 
/*  70 */     if (Period.isPeriodBasic(period) == null) {
/*  71 */       this.loadCustomPeriodNumberOfLastAvailableDataAction = new LoadCustomPeriodNumberOfLastAvailableDataAction(feedDataProvider, instrument, period, side, numberOfCandles, to, filter, intraperiodExistsPolicy, candleListener, loadingProgress, stackTrace);
/*     */     }
/*     */     else
/*     */     {
/*  86 */       this.loadCustomPeriodNumberOfLastAvailableDataAction = null;
/*     */     }
/*     */ 
/*  89 */     if (DataCacheUtils.getCandleStart(period, to) != to) {
/*  90 */       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*  91 */       formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  92 */       throw new DataCacheException("Time [" + formatter.format(Long.valueOf(to)) + "] is not valid for period requested");
/*     */     }
/*     */   }
/*     */ 
/*     */   public LoadNumberOfLastAvailableDataAction(FeedDataProvider feedDataProvider, Instrument instrument, int numberOfCandles, long to, Filter filter, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LiveFeedListener tickListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace)
/*     */     throws DataCacheException
/*     */   {
/* 100 */     super(loadingProgress);
/* 101 */     this.instrument = instrument;
/* 102 */     this.period = Period.TICK;
/* 103 */     this.side = null;
/* 104 */     this.numberOfCandles = numberOfCandles;
/* 105 */     this.to = to;
/* 106 */     this.listener = tickListener;
/* 107 */     this.stackTrace = stackTrace;
/* 108 */     this.feedDataProvider = feedDataProvider;
/* 109 */     this.filter = filter;
/* 110 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/* 112 */     if ((instrument == null) || (numberOfCandles <= 0) || (loadingProgress == null)) {
/* 113 */       throw new DataCacheException("Wrong parameters: " + instrument + " / " + numberOfCandles + " before " + to + " / " + loadingProgress);
/*     */     }
/*     */ 
/* 116 */     this.loadCustomPeriodNumberOfLastAvailableDataAction = null;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 121 */     long fakeFrom = DataCacheUtils.getTimeForNCandlesBackFast(this.period == Period.TICK ? Period.ONE_SEC : this.period, this.to, this.numberOfCandles);
/* 122 */     if (this.loadingProgress.stopJob()) {
/* 123 */       this.loadingProgress.loadingFinished(false, fakeFrom, this.to, this.to, null);
/* 124 */       return;
/*     */     }
/* 126 */     if (Period.isPeriodBasic(this.period) == null) {
/* 127 */       this.loadCustomPeriodNumberOfLastAvailableDataAction.run();
/*     */     }
/*     */     else {
/* 130 */       this.loadingProgress.dataLoaded(fakeFrom, this.to, fakeFrom, "Downloading data into cache...");
/*     */       try
/*     */       {
/* 133 */         long currentTime = this.feedDataProvider.getCurrentTime(this.instrument);
/* 134 */         long correctedTo = this.to;
/* 135 */         long timeOfFirstCandle = this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period);
/* 136 */         if (timeOfFirstCandle == 9223372036854775807L)
/*     */         {
/* 138 */           this.loadingProgress.dataLoaded(fakeFrom, this.to, this.to, "Data loaded!");
/* 139 */           this.loadingProgress.loadingFinished(true, fakeFrom, this.to, this.to, null);
/* 140 */           return;
/*     */         }
/* 142 */         if ((this.period != Period.TICK) && 
/* 143 */           (currentTime != -9223372036854775808L)) {
/* 144 */           long lastTickCandleStartTime = DataCacheUtils.getCandleStartFast(this.period, currentTime);
/* 145 */           if (correctedTo >= lastTickCandleStartTime) {
/* 146 */             correctedTo = DataCacheUtils.getPreviousCandleStartFast(this.period, lastTickCandleStartTime);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 151 */         if (this.listener != null) {
/* 152 */           this.loadingProgress.dataLoaded(fakeFrom, this.to, this.to, "Reading data from cache...");
/* 153 */           LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 154 */           if (this.period == Period.TICK) {
/* 155 */             long lastTickTime = this.feedDataProvider.getLastTickTime(this.instrument);
/* 156 */             if (lastTickTime > correctedTo) {
/* 157 */               if (lastTickTime < correctedTo + 999L)
/* 158 */                 correctedTo = lastTickTime;
/*     */               else
/* 160 */                 correctedTo += 999L;
/*     */             }
/*     */             else {
/* 163 */               correctedTo += 999L;
/*     */             }
/*     */           }
/* 166 */           localCacheManager.readLastAvailableData(this.instrument, this.period, this.side, -9223372036854775808L, correctedTo, this.intraperiodExistsPolicy, new DailyFilterLastAvailableListener(new FilteringLiveFeedListener(correctedTo)), new FilteringLoadingProgressListener(null), this.feedDataProvider.getFeedCommissionManager());
/*     */         }
/*     */ 
/* 178 */         if (this.loadingProgress.stopJob()) {
/* 179 */           this.loadingProgress.loadingFinished(false, fakeFrom, this.to, this.to, null);
/* 180 */           return;
/*     */         }
/* 182 */         this.loadingProgress.dataLoaded(fakeFrom, this.to, this.to, "Data loaded!");
/* 183 */         this.loadingProgress.loadingFinished(true, fakeFrom, this.to, this.to, null);
/*     */       } catch (Exception e) {
/* 185 */         LOGGER.error(e.getMessage(), e);
/* 186 */         this.loadingProgress.loadingFinished(false, fakeFrom, this.to, fakeFrom, e);
/*     */       } catch (Throwable t) {
/* 188 */         LOGGER.error(t.getMessage(), t);
/* 189 */         this.loadingProgress.loadingFinished(false, fakeFrom, this.to, fakeFrom, null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  29 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ 
/*     */   private class FilteringLoadingProgressListener
/*     */     implements LoadingProgressListener
/*     */   {
/*     */     private FilteringLoadingProgressListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */     {
/* 388 */       LoadNumberOfLastAvailableDataAction.this.loadingProgress.dataLoaded(startTime, endTime, currentTime, information);
/*     */     }
/*     */ 
/*     */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*     */     {
/* 393 */       LoadNumberOfLastAvailableDataAction.this.loadingProgress.loadingFinished(allDataLoaded, startTime, endTime, currentTime, e);
/*     */     }
/*     */ 
/*     */     public boolean stopJob()
/*     */     {
/* 398 */       return (LoadNumberOfLastAvailableDataAction.this.loadedNumberOfCandles >= LoadNumberOfLastAvailableDataAction.this.numberOfCandles) || (LoadNumberOfLastAvailableDataAction.this.gapDetected);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FilteringLiveFeedListener
/*     */     implements LiveFeedListener
/*     */   {
/*     */     private final long toTime;
/*     */     private CandleData currentAskCandle;
/*     */     private CandleData currentBidCandle;
/*     */     private boolean process;
/*     */     private CandleData mondayCandle;
/*     */     private CandleData sundayCandle;
/*     */     private List<TickData> ticks;
/*     */     private long closestWeekendsEnd;
/*     */ 
/*     */     public FilteringLiveFeedListener(long toTime)
/*     */     {
/* 208 */       this.toTime = toTime;
/* 209 */       if (LoadNumberOfLastAvailableDataAction.this.period == Period.TICK) {
/* 210 */         this.ticks = new ArrayList();
/*     */       }
/*     */ 
/* 213 */       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 214 */       cal.setFirstDayOfWeek(2);
/* 215 */       cal.setTimeInMillis(toTime);
/* 216 */       cal.set(7, 6);
/* 217 */       cal.set(11, 21);
/* 218 */       cal.set(12, 0);
/* 219 */       cal.set(13, 0);
/* 220 */       cal.set(14, 0);
/* 221 */       long closestWeekendStart = cal.getTimeInMillis();
/* 222 */       cal.set(7, 1);
/* 223 */       cal.set(11, 22);
/* 224 */       this.closestWeekendsEnd = cal.getTimeInMillis();
/* 225 */       if ((toTime > closestWeekendStart) && (toTime < this.closestWeekendsEnd))
/*     */       {
/* 227 */         LoadNumberOfLastAvailableDataAction.access$202(LoadNumberOfLastAvailableDataAction.this, true);
/*     */       }
/* 229 */       if (this.closestWeekendsEnd > toTime) {
/* 230 */         cal.add(4, -1);
/*     */       }
/* 232 */       this.closestWeekendsEnd = cal.getTimeInMillis();
/*     */     }
/*     */ 
/*     */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */     {
/* 237 */       if (((this.currentAskCandle != null) && (time + TimeUnit.MINUTES.toMillis(5L) < this.currentAskCandle.time)) || ((this.currentAskCandle == null) && (time + TimeUnit.MINUTES.toMillis(5L) < this.toTime)) || (time < this.closestWeekendsEnd))
/*     */       {
/* 240 */         LoadNumberOfLastAvailableDataAction.access$202(LoadNumberOfLastAvailableDataAction.this, true);
/* 241 */         return;
/*     */       }
/* 243 */       if ((this.currentAskCandle == null ? this.toTime : this.currentAskCandle.time) / 1000L == time / 1000L)
/*     */       {
/* 245 */         if (this.currentAskCandle == null) {
/* 246 */           this.currentAskCandle = new CandleData();
/* 247 */           newCandleFromTick(this.currentAskCandle, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time), ask, askVol);
/* 248 */           this.currentBidCandle = new CandleData();
/* 249 */           newCandleFromTick(this.currentBidCandle, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time), bid, bidVol);
/*     */         } else {
/* 251 */           addTickAtTheStart(this.currentAskCandle, ask, askVol);
/* 252 */           addTickAtTheStart(this.currentBidCandle, bid, bidVol);
/*     */         }
/*     */       }
/*     */       else {
/* 256 */         if (this.currentAskCandle == null)
/*     */         {
/* 258 */           this.currentAskCandle = new CandleData();
/* 259 */           newCandleFromTick(this.currentAskCandle, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, this.toTime), ask, askVol);
/* 260 */           this.currentBidCandle = new CandleData();
/* 261 */           newCandleFromTick(this.currentBidCandle, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, this.toTime), bid, bidVol);
/*     */         }
/* 263 */         sendCurrentCandle(true);
/*     */ 
/* 266 */         long currentNextCandleTime = DataCacheUtils.getNextCandleStartFast(Period.ONE_SEC, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time));
/* 267 */         long previousPreviousCandleTime = DataCacheUtils.getPreviousCandleStartFast(Period.ONE_SEC, this.currentAskCandle.time);
/* 268 */         while (currentNextCandleTime <= previousPreviousCandleTime)
/*     */         {
/* 270 */           this.currentAskCandle.time = previousPreviousCandleTime;
/* 271 */           this.currentAskCandle.open = ask;
/* 272 */           this.currentAskCandle.close = ask;
/* 273 */           this.currentAskCandle.low = ask;
/* 274 */           this.currentAskCandle.high = ask;
/* 275 */           this.currentAskCandle.vol = 0.0D;
/*     */ 
/* 277 */           this.currentBidCandle.time = previousPreviousCandleTime;
/* 278 */           this.currentBidCandle.open = bid;
/* 279 */           this.currentBidCandle.close = bid;
/* 280 */           this.currentBidCandle.low = bid;
/* 281 */           this.currentBidCandle.high = bid;
/* 282 */           this.currentBidCandle.vol = 0.0D;
/*     */ 
/* 284 */           sendCurrentCandle(true);
/*     */ 
/* 286 */           previousPreviousCandleTime = DataCacheUtils.getPreviousCandleStartFast(Period.ONE_SEC, previousPreviousCandleTime);
/*     */         }
/*     */ 
/* 289 */         newCandleFromTick(this.currentAskCandle, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time), ask, askVol);
/* 290 */         newCandleFromTick(this.currentBidCandle, DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time), bid, bidVol);
/*     */       }
/*     */ 
/* 293 */       this.ticks.add(new TickData(time, ask, bid, askVol, bidVol));
/*     */     }
/*     */ 
/*     */     private void newCandleFromTick(CandleData candle, long time, double price, double vol) {
/* 297 */       candle.time = time;
/* 298 */       candle.open = price;
/* 299 */       candle.close = price;
/* 300 */       candle.high = price;
/* 301 */       candle.low = price;
/* 302 */       candle.vol = vol;
/*     */     }
/*     */ 
/*     */     private void addTickAtTheStart(CandleData candle, double price, double vol) {
/* 306 */       candle.open = price;
/* 307 */       candle.high = (candle.high < candle.open ? candle.open : candle.high);
/* 308 */       candle.low = (candle.low > candle.open ? candle.open : candle.low);
/* 309 */       candle.vol = StratUtils.round(candle.vol + vol, 2);
/*     */     }
/*     */ 
/*     */     private void sendCurrentCandle(boolean tickPeriod) {
/* 313 */       if ((LoadNumberOfLastAvailableDataAction.this.loadedNumberOfCandles < LoadNumberOfLastAvailableDataAction.this.numberOfCandles) && (!LoadNumberOfLastAvailableDataAction.this.gapDetected) && (LoadNumberOfLastAvailableDataAction.this.listener != null)) {
/* 314 */         if (tickPeriod)
/*     */         {
/* 316 */           for (TickData tick : this.ticks) {
/* 317 */             LoadNumberOfLastAvailableDataAction.this.listener.newTick(LoadNumberOfLastAvailableDataAction.this.instrument, tick.time, tick.ask, tick.bid, tick.askVol, tick.bidVol);
/*     */           }
/* 319 */           this.ticks.clear();
/*     */ 
/* 321 */           LoadNumberOfLastAvailableDataAction.this.listener.newCandle(LoadNumberOfLastAvailableDataAction.this.instrument, Period.ONE_SEC, OfferSide.ASK, this.currentAskCandle.time, this.currentAskCandle.open, this.currentAskCandle.close, this.currentAskCandle.low, this.currentAskCandle.high, this.currentAskCandle.vol);
/*     */ 
/* 323 */           LoadNumberOfLastAvailableDataAction.this.listener.newCandle(LoadNumberOfLastAvailableDataAction.this.instrument, Period.ONE_SEC, OfferSide.BID, this.currentBidCandle.time, this.currentBidCandle.open, this.currentBidCandle.close, this.currentBidCandle.low, this.currentBidCandle.high, this.currentBidCandle.vol);
/*     */         }
/*     */         else {
/* 326 */           LoadNumberOfLastAvailableDataAction.this.listener.newCandle(LoadNumberOfLastAvailableDataAction.this.instrument, LoadNumberOfLastAvailableDataAction.this.period, LoadNumberOfLastAvailableDataAction.this.side, this.currentAskCandle.time, this.currentAskCandle.open, this.currentAskCandle.close, this.currentAskCandle.low, this.currentAskCandle.high, this.currentAskCandle.vol);
/*     */         }
/*     */ 
/* 330 */         LoadNumberOfLastAvailableDataAction.access$304(LoadNumberOfLastAvailableDataAction.this);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */     {
/*     */       long previousPreviousCandleTime;
/*     */       long previousPreviousCandleTime;
/* 337 */       if (this.currentAskCandle == null)
/* 338 */         previousPreviousCandleTime = this.toTime;
/*     */       else {
/* 340 */         previousPreviousCandleTime = DataCacheUtils.getPreviousCandleStartFast(period, this.currentAskCandle.time);
/*     */       }
/* 342 */       if ((time != previousPreviousCandleTime) || ((period.getInterval() <= Period.DAILY.getInterval()) && (time < this.closestWeekendsEnd))) {
/* 343 */         LoadNumberOfLastAvailableDataAction.access$202(LoadNumberOfLastAvailableDataAction.this, true);
/* 344 */         return;
/*     */       }
/* 346 */       if ((LoadNumberOfLastAvailableDataAction.this.filter == Filter.NO_FILTER) || (LoadNumberOfLastAvailableDataAction.this.filter == Filter.WEEKENDS))
/*     */       {
/* 348 */         if (this.currentAskCandle == null) {
/* 349 */           this.currentAskCandle = new CandleData();
/*     */         }
/* 351 */         this.currentAskCandle.time = time;
/* 352 */         this.currentAskCandle.open = open;
/* 353 */         this.currentAskCandle.close = close;
/* 354 */         this.currentAskCandle.high = high;
/* 355 */         this.currentAskCandle.low = low;
/* 356 */         this.currentAskCandle.vol = vol;
/* 357 */         sendCurrentCandle(false);
/*     */       }
/* 360 */       else if (this.currentAskCandle == null) {
/* 361 */         this.currentAskCandle = new CandleData();
/* 362 */         this.currentAskCandle.time = time;
/* 363 */         this.currentAskCandle.open = open;
/* 364 */         this.currentAskCandle.close = close;
/* 365 */         this.currentAskCandle.high = high;
/* 366 */         this.currentAskCandle.low = low;
/* 367 */         this.currentAskCandle.vol = vol;
/*     */       } else {
/* 369 */         if ((close != this.currentAskCandle.open) || (this.currentAskCandle.open != this.currentAskCandle.close) || (this.currentAskCandle.close != this.currentAskCandle.high) || (this.currentAskCandle.high != this.currentAskCandle.low))
/*     */         {
/* 372 */           sendCurrentCandle(false);
/*     */         }
/* 374 */         this.currentAskCandle.time = time;
/* 375 */         this.currentAskCandle.open = open;
/* 376 */         this.currentAskCandle.close = close;
/* 377 */         this.currentAskCandle.high = high;
/* 378 */         this.currentAskCandle.low = low;
/* 379 */         this.currentAskCandle.vol = vol;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadNumberOfLastAvailableDataAction
 * JD-Core Version:    0.6.0
 */