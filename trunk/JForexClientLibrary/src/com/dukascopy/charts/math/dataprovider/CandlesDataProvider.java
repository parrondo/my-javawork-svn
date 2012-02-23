/*      */ package com.dukascopy.charts.math.dataprovider;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.feed.FeedDescriptor;
/*      */ import com.dukascopy.api.feed.IFeedDescriptor;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TimeZone;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class CandlesDataProvider extends AbstractDataProvider<CandleData, CandleDataSequence>
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final int MIN_SHIFT = 5;
/*      */   protected CandleData[] buffer;
/*   46 */   protected int lastIndex = -1;
/*   47 */   protected long[][] gaps = new long[0][];
/*      */   protected long loadedNumberOfCandles;
/*      */   protected long loadedTo;
/*      */   protected DataCacheRequestData dataCacheRequestData;
/*      */   private boolean requestAtFirstTick;
/*      */   private CandleData lastData;
/*   57 */   protected long firstTickTime = -9223372036854775808L;
/*      */   private CandleData lastSundayCandle;
/*   61 */   private long weekendStart = -9223372036854775808L;
/*   62 */   private long weekendStartCandle = -9223372036854775808L;
/*   63 */   private long weekendEnd = -9223372036854775808L;
/*   64 */   private long weekendEndCandle = -9223372036854775808L;
/*      */   private LiveFeedListener firstDataListener;
/*      */   private LiveFeedListener tickListener;
/*      */   private LiveFeedListener candleInProgressListener;
/*      */ 
/*      */   public CandlesDataProvider(Instrument instrument, Period period, OfferSide side, int maxNumberOfCandles, int bufferSizeMultiplier, boolean requestAtFirstTick, Filter filter, IFeedDataProvider feedDataProvider)
/*      */   {
/*   75 */     super(instrument, period, side, maxNumberOfCandles, bufferSizeMultiplier, filter, feedDataProvider);
/*   76 */     LOGGER.debug(new StringBuilder().append("Creating data provider for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], maxNumberOfCandles [").append(maxNumberOfCandles).append("]").toString());
/*      */ 
/*   78 */     this.requestAtFirstTick = requestAtFirstTick;
/*   79 */     this.buffer = new CandleData[maxNumberOfCandles * bufferSizeMultiplier];
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*      */     try {
/*   85 */       if ((assertionsEnabled()) && (!this.feedDataProvider.isSubscribedToInstrument(this.instrument))) {
/*   86 */         throw new RuntimeException("Trying to create data provider for instrument, that is not subscribed");
/*      */       }
/*      */ 
/*   89 */       this.firstDataListener = new Object()
/*      */       {
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */         {
/*   94 */           boolean dataChanged = false;
/*   95 */           long dataChangedFrom = 9223372036854775807L;
/*   96 */           long dataChangedTo = -9223372036854775808L;
/*   97 */           synchronized (CandlesDataProvider.this) {
/*   98 */             if ((!period.equals(CandlesDataProvider.this.period)) || (side != CandlesDataProvider.this.side))
/*      */             {
/*  100 */               return;
/*      */             }
/*  102 */             boolean add = true;
/*  103 */             if ((CandlesDataProvider.this.firstData != null) && (((CandleData)CandlesDataProvider.this.firstData).time > time)) {
/*  104 */               if ((open != close) || (close != low) || (low != high) || (vol != 0.0D))
/*      */               {
/*  106 */                 Exception notThrownException = new Exception("[" + (((CandleData)CandlesDataProvider.this.firstData).time - time) + "] Received candle has older time than pervious candle, ignoring");
/*      */ 
/*  108 */                 CandlesDataProvider.LOGGER.error(notThrownException.getMessage(), notThrownException);
/*      */               }
/*      */ 
/*  111 */               return;
/*      */             }
/*  113 */             CandleData candleData = new CandleData(time, open, close, low, high, vol);
/*  114 */             if (CandlesDataProvider.this.tickListener != null) {
/*  115 */               CandlesDataProvider.this.feedDataProvider.unsubscribeFromLiveFeed(instrument, CandlesDataProvider.this.tickListener);
/*  116 */               CandlesDataProvider.access$102(CandlesDataProvider.this, null);
/*  117 */               CandlesDataProvider.this.firstTickTime = -9223372036854775808L;
/*      */             }
/*  119 */             if ((period == Period.DAILY) && (CandlesDataProvider.this.dailyFilterPeriod != Period.DAILY)) {
/*  120 */               CandlesDataProvider.this.cal.setTimeInMillis(time);
/*  121 */               if (CandlesDataProvider.this.cal.get(7) == 1) {
/*  122 */                 CandlesDataProvider.access$202(CandlesDataProvider.this, candleData.clone());
/*      */               }
/*      */             }
/*  125 */             if ((CandlesDataProvider.this.filter != Filter.NO_FILTER) && (period.getInterval() <= Period.DAILY.getInterval()))
/*      */             {
/*  127 */               if ((CandlesDataProvider.this.weekendEndCandle == -9223372036854775808L) || (time > CandlesDataProvider.this.weekendEndCandle)) {
/*  128 */                 CandlesDataProvider.this.cal.setTimeInMillis(time);
/*      */ 
/*  130 */                 CandlesDataProvider.this.cal.set(7, 6);
/*  131 */                 CandlesDataProvider.this.cal.set(11, 22);
/*  132 */                 CandlesDataProvider.this.cal.set(12, 0);
/*  133 */                 CandlesDataProvider.this.cal.set(13, 0);
/*  134 */                 CandlesDataProvider.this.cal.set(14, 0);
/*  135 */                 CandlesDataProvider.access$402(CandlesDataProvider.this, CandlesDataProvider.this.cal.getTimeInMillis());
/*  136 */                 CandlesDataProvider.this.cal.set(7, 1);
/*  137 */                 CandlesDataProvider.this.cal.set(11, 21);
/*  138 */                 CandlesDataProvider.access$502(CandlesDataProvider.this, CandlesDataProvider.this.cal.getTimeInMillis());
/*      */ 
/*  140 */                 CandlesDataProvider.access$602(CandlesDataProvider.this, DataCacheUtils.getCandleStartFast(period, CandlesDataProvider.this.weekendStart));
/*  141 */                 if (CandlesDataProvider.this.weekendStartCandle < CandlesDataProvider.this.weekendStart)
/*      */                 {
/*  144 */                   CandlesDataProvider.access$602(CandlesDataProvider.this, DataCacheUtils.getNextCandleStartFast(period, CandlesDataProvider.this.weekendStartCandle));
/*      */                 }
/*      */ 
/*  147 */                 if ((period == Period.DAILY) && (CandlesDataProvider.this.dailyFilterPeriod != Period.DAILY))
/*  148 */                   CandlesDataProvider.access$302(CandlesDataProvider.this, DataCacheUtils.getCandleStartFast(period, CandlesDataProvider.this.weekendEnd));
/*      */                 else {
/*  150 */                   CandlesDataProvider.access$302(CandlesDataProvider.this, DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, CandlesDataProvider.this.weekendEnd)));
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*  155 */               if ((time >= CandlesDataProvider.this.weekendStartCandle) && (time <= CandlesDataProvider.this.weekendEndCandle))
/*      */               {
/*  157 */                 add = false;
/*  158 */               } else if ((CandlesDataProvider.this.filter == Filter.ALL_FLATS) && (open == close) && (close == high) && (high == low) && ((CandlesDataProvider.this.lastData == null) || (CandlesDataProvider.this.lastData.close == open)))
/*      */               {
/*  160 */                 add = false;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  166 */             CandlesDataProvider.access$702(CandlesDataProvider.this, candleData);
/*  167 */             if (add)
/*      */             {
/*  169 */               Data oldFirstData = CandlesDataProvider.this.firstData;
/*  170 */               if ((CandlesDataProvider.this.firstData != null) && (((CandleData)CandlesDataProvider.this.firstData).time == candleData.time))
/*      */               {
/*  172 */                 CandleData firstDataCandle = (CandleData)CandlesDataProvider.this.firstData;
/*  173 */                 firstDataCandle.open = candleData.open;
/*  174 */                 firstDataCandle.close = candleData.close;
/*  175 */                 firstDataCandle.high = candleData.high;
/*  176 */                 firstDataCandle.low = candleData.low;
/*  177 */                 firstDataCandle.vol = candleData.vol;
/*  178 */                 CandleData inProgressCandle = CandlesDataProvider.this.feedDataProvider.getInProgressCandle(instrument, period, side);
/*  179 */                 if ((inProgressCandle != null) && (DataCacheUtils.getNextCandleStartFast(period, ((CandleData)CandlesDataProvider.this.firstData).time) == inProgressCandle.time))
/*      */                 {
/*  181 */                   CandlesDataProvider.this.firstData = inProgressCandle;
/*  182 */                   if (CandlesDataProvider.this.addFirstDataIfNeeded(oldFirstData.time)) {
/*  183 */                     dataChanged = true;
/*  184 */                     dataChangedFrom = Math.min(dataChangedFrom, oldFirstData.time);
/*  185 */                     dataChangedTo = Math.max(dataChangedTo, ((CandleData)CandlesDataProvider.this.firstData).time);
/*      */                   }
/*  187 */                   if (CandlesDataProvider.this.lastIndex > 0)
/*  188 */                     CandlesDataProvider.this.recalculateIndicatorsOnNewCandleOnly(CandlesDataProvider.this.lastIndex - 1, CandlesDataProvider.this.lastIndex);
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  193 */                 CandlesDataProvider.this.firstData = candleData;
/*      */ 
/*  195 */                 if (CandlesDataProvider.this.addFirstDataIfNeeded(oldFirstData == null ? -9223372036854775808L : oldFirstData.time)) {
/*  196 */                   dataChanged = true;
/*  197 */                   dataChangedFrom = Math.min(dataChangedFrom, oldFirstData == null ? ((CandleData)CandlesDataProvider.this.firstData).time : oldFirstData.time);
/*  198 */                   dataChangedTo = Math.max(dataChangedTo, ((CandleData)CandlesDataProvider.this.firstData).time);
/*      */                 }
/*      */ 
/*  201 */                 oldFirstData = CandlesDataProvider.this.firstData;
/*  202 */                 CandleData inProgressCandle = CandlesDataProvider.this.feedDataProvider.getInProgressCandle(instrument, period, side);
/*  203 */                 if ((inProgressCandle != null) && (DataCacheUtils.getNextCandleStartFast(period, ((CandleData)CandlesDataProvider.this.firstData).time) == inProgressCandle.time)) {
/*  204 */                   CandlesDataProvider.this.firstData = inProgressCandle;
/*  205 */                   if (CandlesDataProvider.this.addFirstDataIfNeeded(oldFirstData == null ? -9223372036854775808L : oldFirstData.time)) {
/*  206 */                     dataChanged = true;
/*  207 */                     dataChangedFrom = Math.min(dataChangedFrom, oldFirstData == null ? ((CandleData)CandlesDataProvider.this.firstData).time : oldFirstData.time);
/*  208 */                     dataChangedTo = Math.max(dataChangedTo, ((CandleData)CandlesDataProvider.this.firstData).time);
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/*      */             }
/*  214 */             else if ((CandlesDataProvider.this.lastIndex != -1) && (CandlesDataProvider.this.buffer[CandlesDataProvider.this.lastIndex].time == candleData.time)) {
/*  215 */               CandlesDataProvider.this.lastIndex -= 1;
/*  216 */               CandlesDataProvider.this.loadedNumberOfCandles -= 1L;
/*      */ 
/*  219 */               if (CandlesDataProvider.this.lastIndex != -1)
/*  220 */                 CandlesDataProvider.this.firstData = CandlesDataProvider.this.buffer[CandlesDataProvider.this.lastIndex];
/*      */               else {
/*  222 */                 CandlesDataProvider.this.firstData = null;
/*      */               }
/*      */ 
/*  226 */               Data oldFirstData = CandlesDataProvider.this.firstData;
/*  227 */               CandleData inProgressCandle = CandlesDataProvider.this.feedDataProvider.getInProgressCandle(instrument, period, side);
/*  228 */               if ((inProgressCandle != null) && ((CandlesDataProvider.this.firstData == null) || (DataCacheUtils.getNextCandleStartFast(period, ((CandleData)CandlesDataProvider.this.firstData).time) == inProgressCandle.time))) {
/*  229 */                 CandlesDataProvider.this.firstData = inProgressCandle;
/*  230 */                 CandlesDataProvider.this.addFirstDataIfNeeded(oldFirstData == null ? -9223372036854775808L : oldFirstData.time);
/*  231 */               } else if (CandlesDataProvider.this.lastIndex != -1) {
/*  232 */                 if ((CandlesDataProvider.this.gaps.length > 0) && (CandlesDataProvider.this.gaps[(CandlesDataProvider.this.gaps.length - 1)][0] >= CandlesDataProvider.this.buffer[CandlesDataProvider.this.lastIndex].time))
/*      */                 {
/*  234 */                   CandlesDataProvider.this.gaps = ((long[][])Arrays.copyOf(CandlesDataProvider.this.gaps, CandlesDataProvider.this.gaps.length - 1));
/*      */                 }
/*      */ 
/*  237 */                 CandlesDataProvider.this.recalculateIndicators(CandlesDataProvider.this.lastIndex, CandlesDataProvider.this.lastIndex, false);
/*  238 */                 CandlesDataProvider.this.checkConsistency();
/*      */               }
/*  240 */               long to = CandlesDataProvider.this.firstData == null ? CandlesDataProvider.this.loadedTo : ((CandleData)CandlesDataProvider.this.firstData).time;
/*  241 */               dataChanged = true;
/*  242 */               dataChangedFrom = Math.min(dataChangedFrom, candleData.time > to ? to : candleData.time);
/*  243 */               dataChangedTo = Math.max(dataChangedTo, to);
/*      */             }
/*      */ 
/*  247 */             if ((CandlesDataProvider.this.filter != Filter.NO_FILTER) && (period.getInterval() <= Period.DAILY.getInterval())) {
/*  248 */               long weekendEndNextHourTime = CandlesDataProvider.this.weekendEnd + 3600000L;
/*  249 */               long firstCandleAfterWeekendEnd = DataCacheUtils.getCandleStartFast(period, weekendEndNextHourTime);
/*  250 */               if (firstCandleAfterWeekendEnd == time) {
/*  251 */                 CandlesDataProvider.this.setFilter(CandlesDataProvider.this.filter);
/*      */               }
/*      */             }
/*  254 */             if ((period == Period.DAILY) && (CandlesDataProvider.this.dailyFilterPeriod != Period.DAILY)) {
/*  255 */               CandlesDataProvider.this.cal.setTimeInMillis(time);
/*      */ 
/*  257 */               if ((CandlesDataProvider.this.cal.get(7) == 1) || ((CandlesDataProvider.this.cal.get(7) == 2) && (CandlesDataProvider.this.dailyFilterPeriod == Period.DAILY_SUNDAY_IN_MONDAY)))
/*      */               {
/*  259 */                 CandlesDataProvider.this.setFilter(CandlesDataProvider.this.filter);
/*      */               }
/*      */             }
/*      */           }
/*  263 */           if (dataChanged) {
/*  264 */             if (CandlesDataProvider.this.sparceIndicatorAttached()) {
/*  265 */               dataChangedFrom = CandlesDataProvider.this.buffer[0].time;
/*  266 */               dataChangedTo = CandlesDataProvider.this.buffer[CandlesDataProvider.this.lastIndex].time;
/*  267 */             } else if (CandlesDataProvider.this.formulasMinShift != 0) {
/*  268 */               dataChangedFrom = DataCacheUtils.getTimeForNCandlesBackFast(period, dataChangedFrom, -CandlesDataProvider.this.formulasMinShift + 1);
/*      */             }
/*  270 */             CandlesDataProvider.this.fireDataChanged(dataChangedFrom, dataChangedTo, true, false);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */         {
/*      */         }
/*      */       };
/*  279 */       this.firstTickTime = getLatestDataTime();
/*  280 */       if (this.firstTickTime != -9223372036854775808L) {
/*  281 */         synchronized (this) {
/*  282 */           if (this.requestAtFirstTick) {
/*  283 */             this.requestAtFirstTick = false;
/*      */ 
/*  288 */             long firstCandleTime = DataCacheUtils.getCandleStartFast(this.period, this.firstTickTime);
/*  289 */             int numberOfCandles = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*  290 */             long to = firstCandleTime;
/*      */ 
/*  292 */             if (this.feedDataProvider != null) {
/*  293 */               DataCacheRequestData requestData = new DataCacheRequestData();
/*  294 */               requestData.numberOfCandlesBefore = 0;
/*  295 */               requestData.numberOfCandlesAfter = 0;
/*  296 */               requestData.time = to;
/*  297 */               requestData.mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*  298 */               requestData.cancel = false;
/*  299 */               if (assertionsEnabled()) {
/*  300 */                 requestData.requestState = new HashMap();
/*  301 */                 requestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  302 */                 CandleData[] bufferCopy = new CandleData[this.buffer.length];
/*  303 */                 System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  304 */                 requestData.requestState.put("buffer", bufferCopy);
/*  305 */                 requestData.requestState.put("firstData", this.firstData);
/*      */               }
/*      */               try {
/*  308 */                 AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, requestData);
/*  309 */                 if (LOGGER.isDebugEnabled()) {
/*  310 */                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  311 */                   dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  312 */                   LOGGER.debug(new StringBuilder().append("Requesting last available candles for instrument [").append(this.instrument).append("] period [").append(this.period).append("] side [").append(this.side).append("] numberOfCandles [").append(numberOfCandles).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */                 }
/*      */ 
/*  316 */                 this.feedDataProvider.loadLastAvailableNumberOfCandlesDataSynched(this.instrument, getFilteredPeriod(), this.side, numberOfCandles, to, this.filter, new LoadDataListener(requestData, true), loadDataProgressListener);
/*      */               }
/*      */               catch (DataCacheException e) {
/*  319 */                 LOGGER.error(e.getMessage(), e);
/*      */               }
/*  321 */               if (this.lastIndex != -1) {
/*  322 */                 numberOfCandles -= this.lastIndex;
/*  323 */                 this.loadedNumberOfCandles = (this.lastIndex + 1);
/*  324 */                 to = DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[0].time);
/*      */               }
/*      */             }
/*  327 */             if ((this.lastIndex == -1) || (this.lastIndex + 1 < this.maxNumberOfCandles * this.bufferSizeMultiplier))
/*      */             {
/*  329 */               requestHistoryData(numberOfCandles, 0, to, AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING, this.maxNumberOfCandles * this.bufferSizeMultiplier, firstCandleTime, 0);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  334 */       this.feedDataProvider.subscribeToPeriodNotifications(this.instrument, this.period, this.side, this.firstDataListener);
/*  335 */       this.tickListener = new Object()
/*      */       {
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */         {
/*      */         }
/*      */ 
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */         {
/*  343 */           if (CandlesDataProvider.this.parentDataProvider != null)
/*  344 */             synchronized (CandlesDataProvider.this.parentDataProvider) {
/*  345 */               synchronized (CandlesDataProvider.this) {
/*  346 */                 CandlesDataProvider.this.processNewTick(instrument, time);
/*      */               }
/*      */             }
/*      */         }
/*      */       };
/*  352 */       this.feedDataProvider.subscribeToLiveFeed(this.instrument, this.tickListener);
/*  353 */       this.candleInProgressListener = new Object()
/*      */       {
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */         {
/*  358 */           boolean dataChanged = false;
/*  359 */           long dataChangedFrom = 9223372036854775807L;
/*  360 */           long dataChangedTo = -9223372036854775808L;
/*  361 */           synchronized (CandlesDataProvider.this) {
/*  362 */             if ((period != CandlesDataProvider.this.period) || (side != CandlesDataProvider.this.side))
/*      */             {
/*  364 */               return;
/*      */             }
/*      */ 
/*  367 */             if ((time < CandlesDataProvider.this.weekendStartCandle) || (time > CandlesDataProvider.this.weekendEndCandle)) {
/*  368 */               if (CandlesDataProvider.this.firstData == null)
/*      */               {
/*  370 */                 CandlesDataProvider.this.firstData = new CandleData(time, open, close, low, high, vol);
/*  371 */                 CandlesDataProvider.this.applyDailyFilterCorrection(null, CandlesDataProvider.this.firstData);
/*  372 */                 if (CandlesDataProvider.this.addFirstDataIfNeeded(-9223372036854775808L)) {
/*  373 */                   dataChanged = true;
/*  374 */                   dataChangedFrom = Math.min(dataChangedFrom, ((CandleData)CandlesDataProvider.this.firstData).time);
/*  375 */                   dataChangedTo = Math.max(dataChangedTo, ((CandleData)CandlesDataProvider.this.firstData).time);
/*      */                 }
/*  377 */               } else if (((CandleData)CandlesDataProvider.this.firstData).time != time)
/*      */               {
/*  379 */                 Data oldFirstData = CandlesDataProvider.this.firstData;
/*  380 */                 CandlesDataProvider.this.firstData = new CandleData(time, open, close, low, high, vol);
/*  381 */                 CandlesDataProvider.this.applyDailyFilterCorrection(oldFirstData, CandlesDataProvider.this.firstData);
/*  382 */                 if (CandlesDataProvider.this.addFirstDataIfNeeded(oldFirstData.time)) {
/*  383 */                   dataChanged = true;
/*  384 */                   dataChangedFrom = Math.min(dataChangedFrom, oldFirstData.time);
/*  385 */                   dataChangedTo = Math.max(dataChangedTo, ((CandleData)CandlesDataProvider.this.firstData).time);
/*      */                 }
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  391 */                 CandleData firstCandleData = (CandleData)CandlesDataProvider.this.firstData;
/*  392 */                 firstCandleData.open = open;
/*  393 */                 firstCandleData.close = close;
/*  394 */                 firstCandleData.low = low;
/*  395 */                 firstCandleData.high = high;
/*  396 */                 firstCandleData.vol = vol;
/*      */ 
/*  398 */                 if ((CandlesDataProvider.this.lastIndex != -1) && (CandlesDataProvider.this.buffer[CandlesDataProvider.this.lastIndex].time == ((CandleData)CandlesDataProvider.this.firstData).time))
/*      */                 {
/*  400 */                   CandlesDataProvider.this.applyDailyFilterCorrection(CandlesDataProvider.this.lastIndex > 0 ? CandlesDataProvider.this.buffer[(CandlesDataProvider.this.lastIndex - 1)] : null, CandlesDataProvider.this.firstData);
/*  401 */                   CandlesDataProvider.this.recalculateIndicators(CandlesDataProvider.this.lastIndex, CandlesDataProvider.this.lastIndex, true);
/*  402 */                   dataChanged = true;
/*  403 */                   dataChangedFrom = Math.min(dataChangedFrom, time);
/*  404 */                   dataChangedTo = Math.max(dataChangedTo, time);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*  409 */           if (dataChanged) {
/*  410 */             if (CandlesDataProvider.this.sparceIndicatorAttached()) {
/*  411 */               dataChangedFrom = CandlesDataProvider.this.buffer[0].time;
/*  412 */               dataChangedTo = CandlesDataProvider.this.buffer[CandlesDataProvider.this.lastIndex].time;
/*  413 */             } else if (CandlesDataProvider.this.formulasMinShift != 0) {
/*  414 */               dataChangedFrom = DataCacheUtils.getTimeForNCandlesBackFast(period, dataChangedFrom, -CandlesDataProvider.this.formulasMinShift + 1);
/*      */             }
/*  416 */             CandlesDataProvider.this.fireDataChanged(dataChangedFrom, dataChangedTo, true, true);
/*      */           }
/*      */ 
/*  419 */           CandlesDataProvider.this.fireLastKnownDataChanged(new CandleData(time, open, close, low, high, vol));
/*      */         }
/*      */ 
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */         {
/*      */         }
/*      */       };
/*  426 */       this.feedDataProvider.addInProgressCandleListener(this.instrument, this.period, this.side, this.candleInProgressListener);
/*  427 */       this.cacheDataUpdatedListener = new AbstractDataProvider.CacheDataUpdatedListener(this);
/*  428 */       this.feedDataProvider.addCacheDataUpdatedListener(this.instrument, this.cacheDataUpdatedListener);
/*      */     } catch (DataCacheException e) {
/*  430 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void applyDailyFilterCorrection(Data previousCandleData, Data newCandleData) {
/*  435 */     if ((this.period == Period.DAILY) && (this.dailyFilterPeriod != Period.DAILY)) {
/*  436 */       CandleData previousCandle = (CandleData)previousCandleData;
/*  437 */       CandleData newCandle = (CandleData)newCandleData;
/*  438 */       this.cal.setTimeInMillis(newCandle.time);
/*  439 */       if (this.cal.get(7) == 1)
/*      */       {
/*  441 */         if (previousCandle != null) {
/*  442 */           newCandle.open = previousCandle.close;
/*  443 */           newCandle.high = previousCandle.close;
/*  444 */           newCandle.low = previousCandle.close;
/*  445 */           newCandle.close = previousCandle.close;
/*  446 */           newCandle.vol = 0.0D;
/*      */         } else {
/*  448 */           newCandle.high = newCandle.open;
/*  449 */           newCandle.low = newCandle.open;
/*  450 */           newCandle.close = newCandle.open;
/*  451 */           newCandle.vol = 0.0D;
/*      */         }
/*      */       }
/*  454 */       if (this.cal.get(7) == 2) {
/*  455 */         long sundayCandleTime = DataCacheUtils.getPreviousCandleStartFast(Period.DAILY, newCandleData.time);
/*  456 */         if ((this.lastSundayCandle != null) && (this.lastSundayCandle.time == sundayCandleTime) && (this.lastSundayCandle.open != 0.0D)) {
/*  457 */           newCandle.open = this.lastSundayCandle.open;
/*  458 */           newCandle.high = Math.max(this.lastSundayCandle.high, newCandle.high);
/*  459 */           newCandle.low = Math.min(this.lastSundayCandle.low, newCandle.low);
/*  460 */           newCandle.vol = StratUtils.roundHalfEven(newCandle.vol + this.lastSundayCandle.vol, 7);
/*  461 */         } else if ((this.lastSundayCandle == null) || (this.lastSundayCandle.time != sundayCandleTime)) {
/*  462 */           this.lastSundayCandle = new CandleData(sundayCandleTime, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
/*      */           try {
/*  464 */             this.feedDataProvider.loadCandlesData(this.instrument, Period.DAILY, this.side, sundayCandleTime, sundayCandleTime, new Object()
/*      */             {
/*      */               public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */               {
/*      */               }
/*      */ 
/*      */               public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  471 */                 synchronized (CandlesDataProvider.this) {
/*  472 */                   if (CandlesDataProvider.this.lastSundayCandle.time == time) {
/*  473 */                     CandlesDataProvider.this.lastSundayCandle.open = open;
/*  474 */                     CandlesDataProvider.this.lastSundayCandle.close = close;
/*  475 */                     CandlesDataProvider.this.lastSundayCandle.low = low;
/*  476 */                     CandlesDataProvider.this.lastSundayCandle.high = high;
/*  477 */                     CandlesDataProvider.this.lastSundayCandle.vol = vol;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             , new Object()
/*      */             {
/*      */               public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*      */               {
/*      */               }
/*      */ 
/*      */               public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */               {
/*  488 */                 boolean dataChanged = false;
/*  489 */                 long from = 0L;
/*  490 */                 long to = 0L;
/*  491 */                 synchronized (CandlesDataProvider.this) {
/*  492 */                   if ((CandlesDataProvider.this.lastSundayCandle.time == startTime) && ((!allDataLoaded) || (CandlesDataProvider.this.lastSundayCandle.open == 0.0D))) {
/*  493 */                     CandlesDataProvider.LOGGER.error("Failed to load last sunday candle");
/*  494 */                     CandlesDataProvider.access$202(CandlesDataProvider.this, null);
/*      */                   }
/*      */                   else {
/*  497 */                     CandlesDataProvider.this.applyDailyFilterCorrection(null, CandlesDataProvider.this.firstData);
/*  498 */                     dataChanged = true;
/*  499 */                     from = to = ((CandleData)CandlesDataProvider.this.firstData).getTime();
/*      */                   }
/*      */                 }
/*  502 */                 if (dataChanged)
/*  503 */                   CandlesDataProvider.this.fireDataChanged(from, to, true, false);
/*      */               }
/*      */ 
/*      */               public boolean stopJob()
/*      */               {
/*  509 */                 return false;
/*      */               } } );
/*      */           } catch (DataCacheException e) {
/*  513 */             LOGGER.error(e.getMessage(), e);
/*  514 */             this.lastSundayCandle = null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processNewTick(Instrument instrument, long time) {
/*  522 */     if (this.firstTickTime == -9223372036854775808L) {
/*  523 */       this.firstTickTime = time;
/*  524 */       if (this.requestAtFirstTick) {
/*  525 */         this.requestAtFirstTick = false;
/*  526 */         if (this.firstData == null)
/*      */         {
/*  531 */           long firstCandleTime = DataCacheUtils.getCandleStartFast(this.period, getLatestDataTime());
/*  532 */           int numberOfCandles = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*  533 */           long to = firstCandleTime;
/*  534 */           DataCacheRequestData requestData = new DataCacheRequestData();
/*  535 */           requestData.numberOfCandlesBefore = 0;
/*  536 */           requestData.numberOfCandlesAfter = 0;
/*  537 */           requestData.time = to;
/*  538 */           requestData.mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*  539 */           requestData.cancel = false;
/*  540 */           if (assertionsEnabled()) {
/*  541 */             requestData.requestState = new HashMap();
/*  542 */             requestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  543 */             CandleData[] bufferCopy = new CandleData[this.buffer.length];
/*  544 */             System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  545 */             requestData.requestState.put("buffer", bufferCopy);
/*  546 */             requestData.requestState.put("firstData", this.firstData);
/*      */           }
/*      */           try {
/*  549 */             AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, requestData);
/*  550 */             if (LOGGER.isDebugEnabled()) {
/*  551 */               SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  552 */               dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  553 */               LOGGER.debug(new StringBuilder().append("Requesting last available candles for instrument [").append(instrument).append("] period [").append(this.period).append("] side [").append(this.side).append("] numberOfCandles [").append(numberOfCandles).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */             }
/*      */ 
/*  557 */             this.feedDataProvider.loadLastAvailableNumberOfCandlesDataSynched(instrument, getFilteredPeriod(), this.side, numberOfCandles, to, this.filter, new LoadDataListener(requestData, true), loadDataProgressListener);
/*      */           }
/*      */           catch (DataCacheException e) {
/*  560 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*  562 */           if (this.lastIndex != -1) {
/*  563 */             numberOfCandles -= this.lastIndex;
/*  564 */             this.loadedNumberOfCandles = (this.lastIndex + 1);
/*  565 */             to = DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[0].time);
/*      */           }
/*  567 */           if ((this.lastIndex == -1) || (this.lastIndex + 1 < this.maxNumberOfCandles * this.bufferSizeMultiplier)) {
/*  568 */             requestHistoryData(numberOfCandles, 0, to, AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING, this.maxNumberOfCandles * this.bufferSizeMultiplier, firstCandleTime, 0);
/*      */           }
/*      */         }
/*      */ 
/*  572 */         if (this.tickListener != null) {
/*  573 */           this.feedDataProvider.unsubscribeFromLiveFeed(instrument, this.tickListener);
/*  574 */           this.tickListener = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized CandleDataSequence getDataSequence(int numberOfCandlesBefore, long to, int numberOfCandlesAfter)
/*      */   {
/*  586 */     if ((this.parentDataProvider == null) && (numberOfCandlesBefore + numberOfCandlesAfter > this.maxNumberOfCandles)) {
/*  587 */       throw new IllegalArgumentException(new StringBuilder().append("Requested items count: ").append(numberOfCandlesBefore + numberOfCandlesAfter).append(" is bigger than maxNumberOfCandles[").append(this.maxNumberOfCandles).append("] specified in constructor").toString());
/*      */     }
/*      */ 
/*  590 */     if ((numberOfCandlesBefore <= 0) && (numberOfCandlesAfter <= 0)) {
/*  591 */       throw new IllegalArgumentException(new StringBuilder().append("Negative or zero number of candles requested [").append(numberOfCandlesBefore).append("],[").append(numberOfCandlesAfter).append("]").toString());
/*      */     }
/*  593 */     if (!this.active) {
/*  594 */       throw new IllegalStateException("DataProvider is not active, activate it first");
/*      */     }
/*  596 */     if (assertionsEnabled()) {
/*  597 */       if ((to == -9223372036854775808L) || (to == 9223372036854775807L)) {
/*  598 */         throw new IllegalStateException(new StringBuilder().append("DataProvider is not ready yet, waiting for first tick. Requested time [").append(to).append("]").toString());
/*      */       }
/*  600 */       if (to > getLatestDataTime()) {
/*  601 */         throw new IllegalStateException(new StringBuilder().append("Time is greater than latest data time for this DataProvider. Requested time [").append(to).append("], latestDataTime [").append(getLatestDataTime()).append("]").toString());
/*      */       }
/*  603 */       if (to != DataCacheUtils.getCandleStartFast(this.period, to)) {
/*  604 */         SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  605 */         df.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  606 */         throw new IllegalStateException(new StringBuilder().append("to time [").append(df.format(Long.valueOf(to))).append("] is not a start of a candle with period [").append(this.period).append("]").toString());
/*      */       }
/*      */     }
/*  609 */     int[] intervals = { -2147483648, -2147483648, 0, 0 };
/*  610 */     boolean dataExists = calculateInterval(numberOfCandlesBefore, to, numberOfCandlesAfter, intervals, this.buffer, this.lastIndex);
/*      */     CandleData[] timeData;
/*  612 */     if (dataExists) {
/*  613 */       CandleData[] timeData = new CandleData[intervals[1] - intervals[0] + 1];
/*  614 */       System.arraycopy(this.buffer, intervals[0], timeData, 0, intervals[1] - intervals[0] + 1);
/*      */     } else {
/*  616 */       timeData = new CandleData[0];
/*      */     }
/*  618 */     Map formulaOutputs = null;
/*  619 */     Map indicators = null;
/*  620 */     for (Map.Entry entry : this.formulas.entrySet()) {
/*  621 */       AbstractDataProvider.IndicatorData formulaData = (AbstractDataProvider.IndicatorData)entry.getValue();
/*  622 */       if (formulaData.disabledIndicator) {
/*      */         continue;
/*      */       }
/*  625 */       IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/*  626 */       if (formulaOutputs == null) {
/*  627 */         formulaOutputs = new HashMap();
/*  628 */         indicators = new HashMap();
/*      */       }
/*  630 */       indicators.put(entry.getKey(), formulaData.indicatorWrapper);
/*  631 */       Object[] outputs = new Object[formulaData.getOutputDataInt().length];
/*  632 */       formulaOutputs.put(entry.getKey(), outputs);
/*  633 */       for (int i = 0; i < outputs.length; i++) {
/*  634 */         switch (6.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[indicator.getOutputParameterInfo(i).getType().ordinal()]) {
/*      */         case 1:
/*  636 */           if (dataExists) {
/*  637 */             outputs[i] = new int[intervals[1] - intervals[0] + 1];
/*      */ 
/*  639 */             System.arraycopy(formulaData.getOutputDataInt()[i], intervals[0], outputs[i], 0, intervals[1] - intervals[0] + 1);
/*      */           } else {
/*  641 */             outputs[i] = new int[0];
/*      */           }
/*  643 */           break;
/*      */         case 2:
/*  645 */           if (dataExists) {
/*  646 */             outputs[i] = new double[intervals[1] - intervals[0] + 1];
/*      */ 
/*  648 */             System.arraycopy(formulaData.getOutputDataDouble()[i], intervals[0], outputs[i], 0, intervals[1] - intervals[0] + 1);
/*      */           } else {
/*  650 */             outputs[i] = new double[0];
/*      */           }
/*  652 */           break;
/*      */         case 3:
/*  654 */           if (dataExists) {
/*  655 */             outputs[i] = new Object[intervals[1] - intervals[0] + 1];
/*      */ 
/*  657 */             System.arraycopy(formulaData.getOutputDataObject()[i], intervals[0], outputs[i], 0, intervals[1] - intervals[0] + 1);
/*      */           } else {
/*  659 */             outputs[i] = new Object[0];
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  664 */       if (formulaData.inputDataProviders != null) {
/*  665 */         for (AbstractDataProvider indicatorDataProvider : formulaData.inputDataProviders) {
/*  666 */           if (indicatorDataProvider != null) {
/*  667 */             Period indicatorPeriod = indicatorDataProvider.period == Period.TICK ? Period.ONE_SEC : indicatorDataProvider.period;
/*  668 */             long indicatorTo = DataCacheUtils.getCandleStartFast(indicatorPeriod, to);
/*  669 */             int indicatorBefore = 1;
/*  670 */             int indicatorAfter = numberOfCandlesAfter > 0 ? 1 : 0;
/*  671 */             if (dataExists) {
/*  672 */               long indicatorFrom = DataCacheUtils.getCandleStartFast(indicatorPeriod, timeData[intervals[2]].time);
/*  673 */               indicatorBefore = DataCacheUtils.getCandlesCountBetweenFast(indicatorPeriod, indicatorFrom, indicatorTo);
/*  674 */               if (indicatorBefore < 0) {
/*  675 */                 indicatorBefore = 1;
/*      */               }
/*  677 */               long indicatorAfterTo = DataCacheUtils.getCandleStartFast(indicatorPeriod, timeData[(timeData.length - intervals[3] - 1)].time);
/*  678 */               indicatorAfter = DataCacheUtils.getCandlesCountBetweenFast(indicatorPeriod, DataCacheUtils.getNextCandleStartFast(indicatorPeriod, indicatorTo), indicatorAfterTo);
/*  679 */               if (indicatorAfter < 0) {
/*  680 */                 indicatorAfter = 0;
/*      */               }
/*      */             }
/*      */ 
/*  684 */             synchronized (indicatorDataProvider) {
/*  685 */               indicatorDataProvider.doHistoryRequests(indicatorBefore, indicatorTo, indicatorAfter);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  691 */     if (this.parentDataProvider == null)
/*      */     {
/*  693 */       doHistoryRequests(numberOfCandlesBefore, to, numberOfCandlesAfter);
/*      */     }
/*      */ 
/*  696 */     long resultFrom = !dataExists ? to : timeData[intervals[2]].time;
/*  697 */     long resultTo = !dataExists ? to : timeData[(timeData.length - intervals[3] - 1)].time;
/*  698 */     boolean latestDataVisible = (timeData.length > 0) && (this.firstData != null) && (intervals[3] == 0) && (timeData[(timeData.length - 1)].time == ((CandleData)this.firstData).time);
/*  699 */     boolean includesLatestData = (timeData.length > 0) && (this.firstData != null) && (timeData[(timeData.length - 1)].time == ((CandleData)this.firstData).time);
/*      */ 
/*  701 */     CandleDataSequence seq = new CandleDataSequence(this.period, resultFrom, resultTo, intervals[2], intervals[3], timeData, this.gaps, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*      */ 
/*  715 */     return seq;
/*      */   }
/*      */ 
/*      */   public void setFilter(Filter filter)
/*      */   {
/*  720 */     setParams(this.instrument, this.period, filter, this.side);
/*      */   }
/*      */ 
/*      */   public void setPeriod(Period period)
/*      */   {
/*  725 */     setParams(this.instrument, period, this.filter, this.side);
/*      */   }
/*      */ 
/*      */   public void setOfferSide(OfferSide offerSide)
/*      */   {
/*  730 */     setParams(this.instrument, this.period, this.filter, offerSide);
/*      */   }
/*      */ 
/*      */   public Filter getFilter()
/*      */   {
/*  735 */     return this.filter;
/*      */   }
/*      */ 
/*      */   public void setInstrument(Instrument instrument)
/*      */   {
/*  740 */     setParams(instrument, this.period, this.filter, this.side);
/*      */   }
/*      */ 
/*      */   public void setParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide)
/*      */   {
/*  749 */     setParams(instrument, period, filter, offerSide, null);
/*      */   }
/*      */ 
/*      */   public void setParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, ISynchronizeIndicators synchronizeIndicators)
/*      */   {
/*  759 */     if (LOGGER.isDebugEnabled()) {
/*  760 */       LOGGER.debug(new StringBuilder().append("Setting filter ").append(filter).append(" for [").append(instrument).append("] [").append(period).append("] [").append(offerSide).append("] provider").toString());
/*      */     }
/*      */ 
/*  763 */     if (this.parentDataProvider != null)
/*      */     {
/*  765 */       synchronized (this.parentDataProvider) {
/*  766 */         synchronized (this) {
/*  767 */           setParamsSynchronized(instrument, period, filter, offerSide, synchronizeIndicators);
/*      */         }
/*      */       }
/*      */     }
/*  771 */     else synchronized (this) {
/*  772 */         setParamsSynchronized(instrument, period, filter, offerSide, synchronizeIndicators);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void setParamsSynchronized(Instrument instrument, Period period, Filter filter, OfferSide side)
/*      */   {
/*  779 */     setParamsSynchronized(instrument, period, filter, side, null);
/*      */   }
/*      */ 
/*      */   protected void setParamsSynchronized(Instrument instrument, Period period, Filter filter, OfferSide side, ISynchronizeIndicators synchronizeIndicators)
/*      */   {
/*  784 */     if (period == Period.TICK) {
/*  785 */       throw new IllegalArgumentException("Incorrect period set for candles provider");
/*      */     }
/*  787 */     if (this.dataCacheRequestData != null) {
/*  788 */       this.dataCacheRequestData.cancel = true;
/*  789 */       if (LOGGER.isDebugEnabled()) {
/*  790 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  791 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  792 */         LOGGER.debug(new StringBuilder().append("Canceling request for instrument [").append(this.instrument).append("], period [").append(this.period).append("], side [").append(this.side).append("] numberOfCandlesBefore [").append(this.dataCacheRequestData.numberOfCandlesBefore).append("], numberOfCandlesAfter [").append(this.dataCacheRequestData.numberOfCandlesAfter).append("] time [").append(dateFormat.format(new Date(this.dataCacheRequestData.time))).append("] as a result for request to change instrument/period/side/filter").toString());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  797 */     this.feedDataProvider.unsubscribeFromPeriodNotifications(this.instrument, this.period, this.side, this.firstDataListener);
/*  798 */     this.feedDataProvider.removeInProgressCandleListener(this.instrument, this.period, this.side, this.candleInProgressListener);
/*  799 */     this.period = period;
/*  800 */     this.side = side;
/*  801 */     this.filter = filter;
/*  802 */     this.instrument = instrument;
/*  803 */     this.lastSundayCandle = null;
/*  804 */     this.firstData = null;
/*  805 */     this.lastIndex = -1;
/*  806 */     this.loadedNumberOfCandles = 0L;
/*  807 */     this.loadedTo = -9223372036854775808L;
/*  808 */     this.weekendStartCandle = -9223372036854775808L;
/*  809 */     this.weekendEndCandle = -9223372036854775808L;
/*  810 */     for (AbstractDataProvider.IndicatorData indicatorData : this.formulas.values()) {
/*  811 */       IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/*  812 */       int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfInputs(); i < j; i++) {
/*  813 */         InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/*  814 */         if ((inputParameterInfo.getOfferSide() == null) && (inputParameterInfo.getPeriod() == null) && (inputParameterInfo.getInstrument() == null))
/*      */           continue;
/*  816 */         initIndicatorInputs(indicatorData);
/*  817 */         break;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  822 */       this.feedDataProvider.subscribeToPeriodNotifications(instrument, period, side, this.firstDataListener);
/*  823 */       this.feedDataProvider.addInProgressCandleListener(instrument, period, side, this.candleInProgressListener);
/*      */     } catch (DataCacheException e) {
/*  825 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  827 */     long firstTickTime = getLatestDataTime();
/*  828 */     if (firstTickTime != -9223372036854775808L) {
/*  829 */       if (this.requestAtFirstTick) {
/*  830 */         this.requestAtFirstTick = false;
/*      */       }
/*      */ 
/*  835 */       long firstCandleTime = DataCacheUtils.getCandleStartFast(period, firstTickTime);
/*  836 */       int numberOfCandles = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*  837 */       long to = firstCandleTime;
/*  838 */       DataCacheRequestData requestData = new DataCacheRequestData();
/*  839 */       requestData.numberOfCandlesBefore = 0;
/*  840 */       requestData.numberOfCandlesAfter = 0;
/*  841 */       requestData.time = to;
/*  842 */       requestData.mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*  843 */       requestData.cancel = false;
/*  844 */       if (assertionsEnabled()) {
/*  845 */         requestData.requestState = new HashMap();
/*  846 */         requestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/*  847 */         CandleData[] bufferCopy = new CandleData[this.buffer.length];
/*  848 */         System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/*  849 */         requestData.requestState.put("buffer", bufferCopy);
/*  850 */         requestData.requestState.put("firstData", this.firstData);
/*      */       }
/*      */       try
/*      */       {
/*  854 */         AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, requestData, synchronizeIndicators);
/*  855 */         if (LOGGER.isDebugEnabled()) {
/*  856 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  857 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  858 */           LOGGER.debug(new StringBuilder().append("Requesting last available candles for instrument [").append(instrument).append("] period [").append(period).append("] side [").append(side).append("] numberOfCandles [").append(numberOfCandles).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */         }
/*      */ 
/*  862 */         this.feedDataProvider.loadLastAvailableNumberOfCandlesDataSynched(instrument, getFilteredPeriod(), side, numberOfCandles, to, filter, new LoadDataListener(requestData, true), loadDataProgressListener);
/*      */       }
/*      */       catch (DataCacheException e) {
/*  865 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  867 */       if (this.lastIndex != -1) {
/*  868 */         numberOfCandles -= this.lastIndex;
/*  869 */         this.loadedNumberOfCandles = (this.lastIndex + 1);
/*  870 */         to = DataCacheUtils.getPreviousCandleStartFast(period, this.buffer[0].time);
/*      */       }
/*  872 */       if ((this.lastIndex == -1) || (this.lastIndex + 1 < this.maxNumberOfCandles))
/*      */       {
/*  874 */         requestHistoryData(numberOfCandles, 0, to, AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING, this.maxNumberOfCandles * this.bufferSizeMultiplier, firstCandleTime, 0, synchronizeIndicators);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void initIndicatorDataOutputBuffers(AbstractDataProvider.IndicatorData formulaData)
/*      */   {
/*  881 */     IndicatorInfo indicatorInfo = formulaData.indicatorWrapper.getIndicator().getIndicatorInfo();
/*  882 */     for (int i = 0; i < indicatorInfo.getNumberOfOutputs(); i++) {
/*  883 */       OutputParameterInfo outputParameterInfo = formulaData.indicatorWrapper.getIndicator().getOutputParameterInfo(i);
/*  884 */       switch (6.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */       case 1:
/*  886 */         formulaData.getOutputDataInt()[i] = new int[this.buffer.length];
/*  887 */         break;
/*      */       case 2:
/*  889 */         formulaData.getOutputDataDouble()[i] = new double[this.buffer.length];
/*  890 */         break;
/*      */       case 3:
/*  892 */         formulaData.getOutputDataObject()[i] = new Object[this.buffer.length];
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators()
/*      */   {
/*  900 */     recalculateIndicators(0, this.lastIndex, false);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicator(AbstractDataProvider.IndicatorData indicatorData, boolean latestData, boolean sameCandle)
/*      */   {
/*  905 */     if ((indicatorData.indicatorWrapper.isRecalculateOnNewCandleOnly()) && (sameCandle)) {
/*  906 */       return;
/*      */     }
/*  908 */     Collection indicators = new ArrayList(1);
/*  909 */     indicators.add(indicatorData);
/*  910 */     if (latestData) {
/*  911 */       recalculateIndicators(this.lastIndex, this.lastIndex, indicators, this.lastIndex, this.buffer, null);
/*      */     }
/*      */     else
/*  914 */       recalculateIndicators(0, this.lastIndex, indicators, this.lastIndex, this.buffer, null);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators(int from, int to, boolean sameCandle)
/*      */   {
/*  919 */     if (!this.active) {
/*  920 */       return;
/*      */     }
/*  922 */     boolean split = false;
/*  923 */     for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/*  924 */       if ((formulaData.indicatorWrapper.getIndicator().getIndicatorInfo().isRecalculateAll()) || (formulaData.indicatorWrapper.isRecalculateOnNewCandleOnly()))
/*      */       {
/*  926 */         split = true;
/*  927 */         break;
/*      */       }
/*      */     }
/*  930 */     if (split) {
/*  931 */       Collection recalculateAllFormulas = new ArrayList(this.formulas.size());
/*  932 */       Collection restOfTheFormulas = new ArrayList(this.formulas.size());
/*  933 */       for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/*  934 */         if ((formulaData.indicatorWrapper.isRecalculateOnNewCandleOnly()) && (sameCandle)) {
/*      */           continue;
/*      */         }
/*  937 */         if (formulaData.indicatorWrapper.getIndicator().getIndicatorInfo().isRecalculateAll())
/*  938 */           recalculateAllFormulas.add(formulaData);
/*      */         else {
/*  940 */           restOfTheFormulas.add(formulaData);
/*      */         }
/*      */       }
/*  943 */       if (!recalculateAllFormulas.isEmpty()) {
/*  944 */         recalculateIndicators(0, this.lastIndex, recalculateAllFormulas, this.lastIndex, this.buffer, null);
/*      */       }
/*  946 */       if (!restOfTheFormulas.isEmpty()) {
/*  947 */         recalculateIndicators(from, to, restOfTheFormulas, this.lastIndex, this.buffer, null);
/*      */       }
/*      */     }
/*  950 */     else if (!this.formulas.isEmpty()) {
/*  951 */       recalculateIndicators(from, to, this.formulas.values(), this.lastIndex, this.buffer, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicatorsOnNewCandleOnly(int from, int to)
/*      */   {
/*  957 */     Collection indicators = new ArrayList();
/*  958 */     for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/*  959 */       if (formulaData.indicatorWrapper.isRecalculateOnNewCandleOnly()) {
/*  960 */         indicators.add(formulaData);
/*      */       }
/*      */     }
/*  963 */     if (!indicators.isEmpty())
/*  964 */       recalculateIndicators(from, to, indicators, this.lastIndex, this.buffer, null);
/*      */   }
/*      */ 
/*      */   protected boolean loadingNeeded(int numberOfCandlesBefore, long to, int numberOfCandlesAfter)
/*      */   {
/*  971 */     if (this.lastIndex == -1) {
/*  972 */       return (numberOfCandlesBefore > 0) || (numberOfCandlesAfter > 0);
/*      */     }
/*      */ 
/*  975 */     int safeCandlesAmountBefore = (this.maxNumberOfCandles * this.bufferSizeMultiplier - this.maxNumberOfCandles) / 4;
/*  976 */     int safeCandlesAmountAfter = safeCandlesAmountBefore;
/*      */ 
/*  978 */     if (this.firstData != null) {
/*  979 */       if (to > ((CandleData)this.firstData).time)
/*      */       {
/*  981 */         to = ((CandleData)this.firstData).time;
/*  982 */         numberOfCandlesAfter = 0;
/*      */       }
/*  984 */       int maxCandlesAfter = DataCacheUtils.getCandlesCountBetweenFast(this.period, DataCacheUtils.getNextCandleStartFast(this.period, to), ((CandleData)this.firstData).time);
/*  985 */       if (numberOfCandlesAfter > maxCandlesAfter) {
/*  986 */         numberOfCandlesAfter = maxCandlesAfter;
/*      */       }
/*  988 */       if (numberOfCandlesAfter + safeCandlesAmountAfter > maxCandlesAfter)
/*  989 */         safeCandlesAmountAfter = maxCandlesAfter - numberOfCandlesAfter;
/*      */     }
/*  991 */     else if (this.firstTickTime != -9223372036854775808L) {
/*  992 */       long firstDataTime = DataCacheUtils.getCandleStartFast(this.period, this.firstTickTime);
/*  993 */       if (to > firstDataTime)
/*      */       {
/*  995 */         to = firstDataTime;
/*  996 */         numberOfCandlesAfter = 0;
/*      */       }
/*  998 */       int maxCandlesAfter = DataCacheUtils.getCandlesCountBetweenFast(this.period, DataCacheUtils.getNextCandleStartFast(this.period, to), firstDataTime);
/*  999 */       if (numberOfCandlesAfter > maxCandlesAfter) {
/* 1000 */         numberOfCandlesAfter = maxCandlesAfter;
/*      */       }
/* 1002 */       if (numberOfCandlesAfter + safeCandlesAmountAfter > maxCandlesAfter) {
/* 1003 */         safeCandlesAmountAfter = maxCandlesAfter - numberOfCandlesAfter;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1008 */     if (to <= this.loadedTo) {
/* 1009 */       int numberOfCandlesBetweenTimes = DataCacheUtils.getCandlesCountBetweenFast(this.period, to, this.loadedTo) - 1;
/* 1010 */       if ((numberOfCandlesAfter + safeCandlesAmountAfter <= numberOfCandlesBetweenTimes) && (numberOfCandlesBefore + safeCandlesAmountBefore < this.loadedNumberOfCandles - numberOfCandlesBetweenTimes))
/*      */       {
/* 1012 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1017 */     int numberOfCandlesBetweenStartAndTo = 0;
/* 1018 */     int numberOfCandlesBetweenToAndEnd = 0;
/*      */ 
/* 1021 */     int ei = this.lastIndex;
/*      */ 
/* 1023 */     int toIndex = findStart(to, 0, ei, this.buffer);
/* 1024 */     numberOfCandlesBetweenToAndEnd += ei - toIndex;
/* 1025 */     if ((toIndex >= 0) && (toIndex <= ei) && (this.buffer[0].time == to)) {
/* 1026 */       toIndex++;
/*      */     }
/*      */ 
/* 1029 */     numberOfCandlesBetweenStartAndTo = (int)(numberOfCandlesBetweenStartAndTo + (toIndex + 1 + (this.lastIndex + 1 - this.loadedNumberOfCandles)));
/*      */ 
/* 1031 */     if ((this.firstData != null) && (this.buffer[this.lastIndex].time != ((CandleData)this.firstData).time))
/*      */     {
/* 1033 */       if (numberOfCandlesBetweenToAndEnd < safeCandlesAmountAfter + numberOfCandlesAfter) {
/* 1034 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1038 */     return numberOfCandlesBetweenStartAndTo < safeCandlesAmountBefore + numberOfCandlesBefore;
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider<CandleData, CandleDataSequence>.LoadDataProgressListener doHistoryRequests(int numOfCandlesBefore, long time, int numOfCandlesAfter)
/*      */   {
/* 1044 */     if (loadingNeeded(numOfCandlesBefore, time, numOfCandlesAfter))
/*      */     {
/* 1046 */       int numberOfCandlesBefore = this.maxNumberOfCandles * this.bufferSizeMultiplier / 2 + (numOfCandlesBefore + numOfCandlesAfter) / 2;
/* 1047 */       int numberOfCandlesAfter = this.maxNumberOfCandles * this.bufferSizeMultiplier / 2 - (numOfCandlesBefore + numOfCandlesAfter) / 2;
/* 1048 */       AbstractDataProvider.RequestMode mode = AbstractDataProvider.RequestMode.OVERWRITE;
/*      */ 
/* 1050 */       long firstDataTime = -9223372036854775808L;
/* 1051 */       if (this.firstData != null)
/* 1052 */         firstDataTime = ((CandleData)this.firstData).time;
/* 1053 */       else if (this.firstTickTime != -9223372036854775808L)
/* 1054 */         firstDataTime = DataCacheUtils.getCandleStartFast(this.period, this.firstTickTime);
/*      */       else {
/* 1056 */         firstDataTime = getLatestDataTime();
/*      */       }
/*      */ 
/* 1059 */       if (firstDataTime != -9223372036854775808L) {
/* 1060 */         if (time >= firstDataTime)
/*      */         {
/* 1062 */           time = DataCacheUtils.getCandleStartFast(this.period, firstDataTime);
/* 1063 */           numberOfCandlesAfter = 0;
/* 1064 */           numberOfCandlesBefore = this.maxNumberOfCandles * this.bufferSizeMultiplier;
/* 1065 */           mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/*      */ 
/* 1067 */           if ((this.lastIndex != -1) && (this.firstData != null) && (this.buffer[this.lastIndex].time != ((CandleData)this.firstData).time)) {
/* 1068 */             this.lastIndex = 0;
/* 1069 */             this.buffer[this.lastIndex] = ((CandleData)this.firstData);
/* 1070 */             this.gaps = new long[0][];
/* 1071 */             this.loadedNumberOfCandles = 1L;
/* 1072 */             this.loadedTo = ((CandleData)this.firstData).time;
/*      */ 
/* 1074 */             recalculateIndicators();
/* 1075 */             checkConsistency();
/* 1076 */           } else if ((this.lastIndex != -1) && (this.firstData == null) && (this.firstTickTime != -9223372036854775808L) && (this.buffer[this.lastIndex].time != DataCacheUtils.getPreviousCandleStartFast(this.period, firstDataTime)) && (this.buffer[this.lastIndex].time != firstDataTime))
/*      */           {
/* 1079 */             this.lastIndex = -1;
/* 1080 */             this.gaps = new long[0][];
/* 1081 */             this.loadedNumberOfCandles = 0L;
/* 1082 */             this.loadedTo = 0L;
/* 1083 */             checkConsistency();
/* 1084 */           } else if (this.lastIndex != -1)
/*      */           {
/* 1087 */             time = this.buffer[0].time;
/* 1088 */             numberOfCandlesBefore -= this.lastIndex;
/*      */           }
/* 1090 */         } else if ((this.lastIndex != -1) && (this.firstData != null) && (this.buffer[this.lastIndex].time == ((CandleData)this.firstData).time) && (DataCacheUtils.getTimeForNCandlesForwardFast(this.period, time, numberOfCandlesAfter + 2) >= ((CandleData)this.firstData).time))
/*      */         {
/* 1094 */           mode = AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING;
/* 1095 */           time = this.buffer[0].time;
/* 1096 */           numberOfCandlesAfter = 0;
/* 1097 */           numberOfCandlesBefore = this.maxNumberOfCandles * this.bufferSizeMultiplier - this.lastIndex;
/*      */         } else {
/* 1099 */           long expectedLastCandleTime = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, time, numberOfCandlesAfter + 1);
/* 1100 */           if (expectedLastCandleTime > firstDataTime) {
/* 1101 */             int numberOfCandlesBetween = DataCacheUtils.getCandlesCountBetweenFast(this.period, firstDataTime, expectedLastCandleTime) - 1;
/* 1102 */             numberOfCandlesAfter -= numberOfCandlesBetween;
/* 1103 */             numberOfCandlesBefore += numberOfCandlesBetween;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 1108 */       else if (assertionsEnabled()) {
/* 1109 */         throw new RuntimeException("There is no first data time known to the provider, but still we got the request for data... something is completely wrong");
/*      */       }
/*      */ 
/* 1113 */       return requestHistoryData(numberOfCandlesBefore, numberOfCandlesAfter, time, mode, numOfCandlesBefore, time, numOfCandlesAfter);
/* 1114 */     }if (this.dataCacheRequestData != null) {
/* 1115 */       if (LOGGER.isDebugEnabled()) {
/* 1116 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1117 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1118 */         LOGGER.debug(new StringBuilder().append("Canceling request for instrument [").append(this.instrument).append("], period [").append(this.period).append("], side [").append(this.side).append("] number of candles before [").append(this.dataCacheRequestData.numberOfCandlesBefore).append("] number of candles after [").append(this.dataCacheRequestData.numberOfCandlesAfter).append("] to [").append(dateFormat.format(new Date(this.dataCacheRequestData.time))).append("], buffer already has the required data").toString());
/*      */       }
/*      */ 
/* 1122 */       this.dataCacheRequestData.cancel = true;
/* 1123 */       this.dataCacheRequestData = null;
/* 1124 */       fireLoadingFinished();
/*      */     }
/* 1126 */     return null;
/*      */   }
/*      */ 
/*      */   protected void shiftLeft(int numberOfElements) {
/* 1130 */     if (numberOfElements < this.lastIndex + 1)
/*      */     {
/* 1132 */       for (int ind = 0; (ind < this.gaps.length) && 
/* 1133 */         (this.gaps[ind][0] <= this.buffer[numberOfElements].time); ind++);
/* 1137 */       if (ind > 0) {
/* 1138 */         long[][] newGaps = new long[this.gaps.length - ind][];
/* 1139 */         if (newGaps.length > 0) {
/* 1140 */           System.arraycopy(this.gaps, ind, newGaps, 0, this.gaps.length - ind);
/*      */         }
/* 1142 */         this.gaps = newGaps;
/*      */       }
/* 1144 */       System.arraycopy(this.buffer, numberOfElements, this.buffer, 0, this.lastIndex + 1 - numberOfElements);
/* 1145 */       for (AbstractDataProvider.IndicatorData indicatorData : this.formulas.values()) {
/* 1146 */         IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/* 1147 */         int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); i < j; i++)
/*      */         {
/*      */           Object array;
/* 1149 */           switch (6.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[indicator.getOutputParameterInfo(i).getType().ordinal()]) {
/*      */           case 2:
/* 1151 */             array = indicatorData.getOutputDataDouble()[i];
/* 1152 */             break;
/*      */           case 1:
/* 1154 */             array = indicatorData.getOutputDataInt()[i];
/* 1155 */             break;
/*      */           case 3:
/* 1157 */             array = indicatorData.getOutputDataObject()[i];
/* 1158 */             break;
/*      */           default:
/* 1160 */             break;
/*      */           }
/*      */ 
/* 1163 */           System.arraycopy(array, numberOfElements, array, 0, this.lastIndex + 1 - numberOfElements);
/*      */         }
/*      */       }
/* 1166 */       this.lastIndex -= numberOfElements;
/* 1167 */       this.loadedNumberOfCandles = (this.lastIndex + 1);
/* 1168 */       this.loadedTo = this.buffer[this.lastIndex].time;
/* 1169 */     } else if (numberOfElements >= this.lastIndex + 1) {
/* 1170 */       this.lastIndex = -1;
/* 1171 */       this.loadedNumberOfCandles = 0L;
/* 1172 */       this.loadedTo = -9223372036854775808L;
/* 1173 */       this.gaps = new long[0][];
/*      */     }
/*      */   }
/*      */ 
/*      */   protected AbstractDataProvider<CandleData, CandleDataSequence>.LoadDataProgressListener requestHistoryData(int numberOfCandlesBefore, int numberOfCandlesAfter, long time, AbstractDataProvider.RequestMode mode, int requestedNumberOfCandlesBefore, long requestedTo, int requestedNumberOfCandlesAfter)
/*      */   {
/* 1180 */     return requestHistoryData(numberOfCandlesBefore, numberOfCandlesAfter, time, mode, requestedNumberOfCandlesBefore, requestedTo, requestedNumberOfCandlesAfter, null);
/*      */   }
/*      */ 
/*      */   protected AbstractDataProvider<CandleData, CandleDataSequence>.LoadDataProgressListener requestHistoryData(int numberOfCandlesBefore, int numberOfCandlesAfter, long time, AbstractDataProvider.RequestMode mode, int requestedNumberOfCandlesBefore, long requestedTo, int requestedNumberOfCandlesAfter, ISynchronizeIndicators synchronizeIndicators)
/*      */   {
/* 1187 */     if ((this.feedDataProvider != null) && (time < this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period)))
/*      */     {
/* 1189 */       return null;
/*      */     }
/*      */ 
/* 1192 */     if ((mode != AbstractDataProvider.RequestMode.OVERWRITE) && (mode != AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING))
/*      */     {
/* 1194 */       throw new RuntimeException(new StringBuilder().append("Request mode [").append(mode).append("] not supported").toString());
/*      */     }
/*      */ 
/* 1197 */     if ((mode == AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING) && (this.lastIndex != -1) && (this.firstData != null) && (time > this.buffer[0].time))
/*      */     {
/* 1199 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */ 
/* 1202 */     if (numberOfCandlesBefore + numberOfCandlesAfter == 0) {
/* 1203 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */ 
/* 1206 */     if ((this.dataCacheRequestData != null) && (!this.dataCacheRequestData.cancel))
/*      */     {
/* 1210 */       if ((this.dataCacheRequestData.time == time) && (this.dataCacheRequestData.numberOfCandlesBefore == numberOfCandlesBefore) && (this.dataCacheRequestData.numberOfCandlesAfter == numberOfCandlesAfter) && (this.dataCacheRequestData.mode == mode))
/*      */       {
/* 1213 */         return null;
/*      */       }
/*      */ 
/* 1216 */       long firstDataTime = -9223372036854775808L;
/* 1217 */       if (this.firstData != null)
/* 1218 */         firstDataTime = ((CandleData)this.firstData).time;
/* 1219 */       else if (this.firstTickTime != -9223372036854775808L)
/* 1220 */         firstDataTime = DataCacheUtils.getCandleStartFast(this.period, this.firstTickTime);
/*      */       long expectedBufferTimeEnd;
/* 1225 */       if (this.dataCacheRequestData.mode == AbstractDataProvider.RequestMode.OVERWRITE) {
/* 1226 */         long expectedBufferTimeStart = DataCacheUtils.getTimeForNCandlesBackFast(this.period, this.dataCacheRequestData.time, this.dataCacheRequestData.numberOfCandlesBefore);
/*      */ 
/* 1228 */         expectedBufferTimeEnd = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, DataCacheUtils.getNextCandleStartFast(this.period, this.dataCacheRequestData.time), this.dataCacheRequestData.numberOfCandlesAfter);
/*      */       }
/*      */       else
/*      */       {
/*      */         long expectedBufferTimeStart;
/* 1230 */         if (this.dataCacheRequestData.mode == AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING) {
/* 1231 */           long expectedBufferTimeEnd = firstDataTime;
/* 1232 */           expectedBufferTimeStart = DataCacheUtils.getTimeForNCandlesBackFast(this.period, this.dataCacheRequestData.time, this.dataCacheRequestData.numberOfCandlesBefore + (DataCacheUtils.getCandlesCountBetweenFast(this.period, time, firstDataTime) - 1));
/*      */         }
/*      */         else
/*      */         {
/* 1236 */           throw new RuntimeException(new StringBuilder().append("Request mode [").append(this.dataCacheRequestData.mode).append("] not supported").toString());
/*      */         }
/*      */       }
/*      */       long expectedBufferTimeEnd;
/*      */       long expectedBufferTimeStart;
/* 1239 */       long requestedFrom = DataCacheUtils.getTimeForNCandlesBackFast(this.period, requestedTo, requestedNumberOfCandlesBefore + (this.maxNumberOfCandles * this.bufferSizeMultiplier - this.maxNumberOfCandles) / 4);
/*      */ 
/* 1241 */       long requestedToCorrected = requestedTo + DataCacheUtils.getTimeForNCandlesForwardFast(this.period, DataCacheUtils.getNextCandleStartFast(this.period, requestedTo), requestedNumberOfCandlesAfter + (this.maxNumberOfCandles * this.bufferSizeMultiplier - this.maxNumberOfCandles) / 4);
/*      */ 
/* 1244 */       if (requestedToCorrected > firstDataTime)
/*      */       {
/* 1246 */         requestedToCorrected = firstDataTime;
/*      */       }
/* 1248 */       if ((requestedFrom >= expectedBufferTimeStart) && (requestedToCorrected <= expectedBufferTimeEnd)) {
/* 1249 */         return null;
/*      */       }
/*      */ 
/* 1252 */       if (LOGGER.isDebugEnabled()) {
/* 1253 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1254 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1255 */         LOGGER.debug(new StringBuilder().append("Canceling request for instrument [").append(this.instrument).append("], period [").append(this.period).append("], side [").append(this.side).append("] numberOfCandlesBefore [").append(this.dataCacheRequestData.numberOfCandlesBefore).append("], numberOfCandlesAfter [").append(this.dataCacheRequestData.numberOfCandlesAfter).append("] time [").append(dateFormat.format(new Date(this.dataCacheRequestData.time))).append("], request no longer contain required data").toString());
/*      */       }
/*      */ 
/* 1259 */       this.dataCacheRequestData.cancel = true;
/* 1260 */       this.dataCacheRequestData = null;
/*      */     }
/* 1262 */     fireLoadingStarted();
/* 1263 */     this.dataCacheRequestData = new DataCacheRequestData();
/* 1264 */     this.dataCacheRequestData.numberOfCandlesBefore = numberOfCandlesBefore;
/* 1265 */     this.dataCacheRequestData.numberOfCandlesAfter = numberOfCandlesAfter;
/* 1266 */     this.dataCacheRequestData.time = time;
/* 1267 */     this.dataCacheRequestData.mode = mode;
/* 1268 */     this.dataCacheRequestData.cancel = false;
/* 1269 */     if (assertionsEnabled()) {
/* 1270 */       this.dataCacheRequestData.requestState = new HashMap();
/* 1271 */       this.dataCacheRequestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/* 1272 */       CandleData[] bufferCopy = new CandleData[this.buffer.length];
/* 1273 */       System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/* 1274 */       this.dataCacheRequestData.requestState.put("buffer", bufferCopy);
/* 1275 */       this.dataCacheRequestData.requestState.put("firstData", this.firstData);
/* 1276 */       if (numberOfCandlesBefore + numberOfCandlesAfter <= 0) {
/* 1277 */         throw new RuntimeException("Trying to send empty request");
/*      */       }
/* 1279 */       if (!this.feedDataProvider.isSubscribedToInstrument(this.instrument)) {
/* 1280 */         throw new RuntimeException("Requesting data to instrument, that is not subscribed");
/*      */       }
/*      */     }
/* 1283 */     if (this.feedDataProvider != null) {
/*      */       try
/*      */       {
/* 1286 */         AbstractDataProvider.LoadDataProgressListener loadDataProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, this.dataCacheRequestData, synchronizeIndicators);
/* 1287 */         if (LOGGER.isDebugEnabled()) {
/* 1288 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1289 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1290 */           LOGGER.debug(new StringBuilder().append("Requesting candles for instrument [").append(this.instrument).append("], period [").append(this.period).append("], side [").append(this.side).append("] loading [").append(numberOfCandlesBefore).append("] candles before and [").append(numberOfCandlesAfter).append("] after time [").append(dateFormat.format(new Date(time))).append("] as a result to request for [").append(requestedNumberOfCandlesBefore).append("] candles before time [").append(dateFormat.format(new Date(requestedTo))).append("] and [").append(requestedNumberOfCandlesAfter).append("] candles after time. FirstData time is - ").append(this.firstData == null ? "null" : dateFormat.format(Long.valueOf(((CandleData)this.firstData).time))).append(", mode - ").append(mode).toString());
/*      */         }
/*      */ 
/* 1296 */         this.feedDataProvider.loadCandlesDataBeforeAfter(this.instrument, getFilteredPeriod(), this.side, numberOfCandlesBefore, numberOfCandlesAfter, time, this.filter, new LoadDataListener(this.dataCacheRequestData, false), loadDataProgressListener);
/*      */ 
/* 1298 */         return loadDataProgressListener;
/*      */       } catch (DataCacheException e) {
/* 1300 */         LOGGER.error(e.getMessage(), e);
/* 1301 */         return null;
/*      */       }
/*      */     }
/* 1304 */     return null;
/*      */   }
/*      */ 
/*      */   protected void dataLoaded(boolean allDataLoaded, AbstractDataProvider.AbstractDataCacheRequestData abstractRequestData, Exception e) {
/* 1308 */     dataLoaded(allDataLoaded, abstractRequestData, e, null);
/*      */   }
/*      */ 
/*      */   protected void dataLoaded(boolean allDataLoaded, AbstractDataProvider.AbstractDataCacheRequestData abstractRequestData, Exception e, ISynchronizeIndicators synchronizeIndicators)
/*      */   {
/* 1315 */     boolean dataChanged = false;
/* 1316 */     boolean loadingFinished = false;
/* 1317 */     long dataChangedFrom = 9223372036854775807L;
/* 1318 */     long dataChangedTo = -9223372036854775808L;
/* 1319 */     boolean dataChangedFirstData = true;
/* 1320 */     synchronized (this) {
/* 1321 */       DataCacheRequestData requestData = (DataCacheRequestData)abstractRequestData;
/* 1322 */       if (LOGGER.isTraceEnabled()) {
/* 1323 */         LOGGER.trace(new StringBuilder().append("dataLoaded, period [").append(this.period).append("], success [").append(allDataLoaded).append("]").toString());
/*      */       }
/* 1325 */       if (this.dataCacheRequestData == requestData) {
/* 1326 */         loadingFinished = true;
/* 1327 */         this.dataCacheRequestData = null;
/* 1328 */         fireLoadingFinished();
/*      */       }
/* 1330 */       if ((!requestData.cancel) && (allDataLoaded)) {
/* 1331 */         if (LOGGER.isDebugEnabled()) {
/* 1332 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1333 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1334 */           LOGGER.debug(new StringBuilder().append("Loaded [").append(requestData.dataLoaded.size()).append("] data for instrument [").append(this.instrument).append("], period [").append(this.period).append("], side [").append(this.side).append("] requestedCandlesBefore [").append(requestData.numberOfCandlesBefore).append("], time [").append(dateFormat.format(new Date(requestData.time))).append("], requestedCandlesAfter [").append(requestData.numberOfCandlesAfter).append("]").toString());
/*      */         }
/*      */ 
/* 1338 */         if (assertionsEnabled()) {
/* 1339 */           requestData.requestState = new HashMap();
/* 1340 */           requestData.requestState.put("lastIndex", Integer.valueOf(this.lastIndex));
/* 1341 */           CandleData[] bufferCopy = new CandleData[this.buffer.length];
/* 1342 */           System.arraycopy(this.buffer, 0, bufferCopy, 0, this.buffer.length);
/* 1343 */           requestData.requestState.put("buffer", bufferCopy);
/* 1344 */           requestData.requestState.put("firstData", this.firstData);
/*      */         }
/* 1346 */         if (requestData.mode == AbstractDataProvider.RequestMode.OVERWRITE) {
/* 1347 */           if (!requestData.dataLoaded.isEmpty()) {
/* 1348 */             int i = -1;
/* 1349 */             this.buffer[0] = null;
/*      */ 
/* 1351 */             long prevTime = -9223372036854775808L;
/* 1352 */             List gapsList = new ArrayList();
/* 1353 */             for (CandleData dataElement : requestData.dataLoaded) {
/* 1354 */               if ((prevTime != -9223372036854775808L) && (DataCacheUtils.getNextCandleStartFast(this.period, prevTime) != dataElement.time)) {
/* 1355 */                 long firstGapCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, prevTime);
/* 1356 */                 long lastGapCandleTime = DataCacheUtils.getPreviousCandleStartFast(this.period, dataElement.time);
/* 1357 */                 gapsList.add(new long[] { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.period, firstGapCandleTime, lastGapCandleTime) });
/*      */               }
/* 1359 */               prevTime = dataElement.time;
/* 1360 */               if (i < this.buffer.length - 1) {
/* 1361 */                 i++;
/* 1362 */                 this.buffer[i] = dataElement;
/*      */               } else {
/* 1364 */                 throw new RuntimeException("Loaded data has size bigger than maximum size of the data provider");
/*      */               }
/*      */             }
/* 1367 */             this.lastIndex = i;
/* 1368 */             this.loadedTo = (this.buffer[this.lastIndex].time > requestData.time ? this.buffer[this.lastIndex].time : requestData.time);
/* 1369 */             this.loadedNumberOfCandles = (requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter);
/* 1370 */             this.gaps = ((long[][])gapsList.toArray(new long[gapsList.size()][]));
/*      */ 
/* 1372 */             CandleData lastElement = (CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1);
/* 1373 */             CandleData inProgressCandle = this.feedDataProvider.getInProgressCandle(this.instrument, this.period, this.side);
/* 1374 */             if (((inProgressCandle != null) && (this.firstData != null) && (DataCacheUtils.getNextCandleStartFast(this.period, lastElement.time) == inProgressCandle.time) && (inProgressCandle.time == ((CandleData)this.firstData).time)) || ((this.firstData != null) && (requestData.dataLoaded.size() < requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter) && (((CandleData)requestData.dataLoaded.get(0)).time > this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period))) || ((this.firstData != null) && (requestData.time == DataCacheUtils.getPreviousCandleStartFast(this.period, ((CandleData)this.firstData).time))))
/*      */             {
/* 1384 */               if (this.lastIndex + 1 >= this.buffer.length) {
/* 1385 */                 shiftLeft(1);
/* 1386 */                 this.loadedNumberOfCandles -= 1L;
/*      */               }
/* 1388 */               this.lastIndex += 1;
/* 1389 */               this.loadedNumberOfCandles += 1L;
/* 1390 */               this.loadedTo = ((CandleData)this.firstData).time;
/* 1391 */               this.buffer[this.lastIndex] = ((CandleData)this.firstData);
/* 1392 */               long firstGapCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, this.buffer[(this.lastIndex - 1)].time);
/* 1393 */               if (firstGapCandleTime != this.buffer[this.lastIndex].time) {
/* 1394 */                 this.gaps = ((long[][])Arrays.copyOf(this.gaps, this.gaps.length + 1));
/* 1395 */                 long lastGapCandleTime = DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[this.lastIndex].time);
/* 1396 */                 this.gaps[(this.gaps.length - 1)] = { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.period, firstGapCandleTime, lastGapCandleTime) };
/*      */               }
/*      */             }
/*      */ 
/* 1400 */             recalculateIndicators();
/* 1401 */             dataChanged = true;
/* 1402 */             dataChangedFrom = Math.min(dataChangedFrom, this.buffer[0].time);
/* 1403 */             dataChangedTo = Math.max(dataChangedTo, this.buffer[this.lastIndex].time);
/* 1404 */             dataChangedFirstData = false;
/*      */           } else {
/* 1406 */             this.lastIndex = -1;
/* 1407 */             this.gaps = new long[0][];
/* 1408 */             this.loadedNumberOfCandles = (requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter);
/* 1409 */             if (requestData.numberOfCandlesBefore == 0) {
/* 1410 */               this.loadedTo = DataCacheUtils.getNextCandleStartFast(this.period, requestData.time);
/*      */             }
/* 1412 */             if (requestData.numberOfCandlesAfter > 0) {
/* 1413 */               this.loadedTo = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, requestData.time, requestData.numberOfCandlesAfter);
/*      */             }
/* 1415 */             dataChanged = true;
/* 1416 */             dataChangedFrom = Math.min(dataChangedFrom, DataCacheUtils.getTimeForNCandlesBackFast(this.period, requestData.time, requestData.numberOfCandlesBefore));
/* 1417 */             dataChangedTo = Math.max(dataChangedTo, requestData.time);
/* 1418 */             dataChangedFirstData = false;
/*      */           }
/* 1420 */         } else if (requestData.mode == AbstractDataProvider.RequestMode.APPEND_AT_START_NOT_OVERWRITING) {
/* 1421 */           boolean breakInterrupted = false;
/* 1422 */           if (!requestData.dataLoaded.isEmpty()) {
/* 1423 */             if (this.firstData == null)
/*      */             {
/* 1425 */               this.firstData = ((Data)requestData.dataLoaded.remove(requestData.dataLoaded.size() - 1));
/* 1426 */               if (this.tickListener != null) {
/* 1427 */                 this.feedDataProvider.unsubscribeFromLiveFeed(this.instrument, this.tickListener);
/* 1428 */                 this.tickListener = null;
/* 1429 */                 this.firstTickTime = -9223372036854775808L;
/*      */               }
/* 1431 */               this.lastIndex = 0;
/* 1432 */               this.loadedNumberOfCandles = 1L;
/* 1433 */               this.loadedTo = ((CandleData)this.firstData).time;
/* 1434 */               this.buffer[this.lastIndex] = ((CandleData)this.firstData);
/* 1435 */               this.gaps = new long[0][];
/*      */ 
/* 1437 */               CandleData inProgressCandle = this.feedDataProvider.getInProgressCandle(this.instrument, this.period, this.side);
/* 1438 */               if ((inProgressCandle != null) && (DataCacheUtils.getNextCandleStartFast(this.period, ((CandleData)this.firstData).time) == inProgressCandle.time)) {
/* 1439 */                 this.firstData = inProgressCandle;
/* 1440 */                 this.lastIndex = 1;
/* 1441 */                 this.loadedNumberOfCandles = 2L;
/* 1442 */                 this.loadedTo = ((CandleData)this.firstData).time;
/* 1443 */                 this.buffer[this.lastIndex] = ((CandleData)this.firstData);
/* 1444 */                 long firstGapCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, this.buffer[(this.lastIndex - 1)].time);
/* 1445 */                 if (firstGapCandleTime != this.buffer[this.lastIndex].time) {
/* 1446 */                   this.gaps = ((long[][])Arrays.copyOf(this.gaps, this.gaps.length + 1));
/* 1447 */                   long lastGapCandleTime = DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[this.lastIndex].time);
/* 1448 */                   this.gaps[(this.gaps.length - 1)] = { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.period, firstGapCandleTime, lastGapCandleTime) };
/*      */                 }
/*      */               }
/* 1451 */             } else if ((this.filter == Filter.NO_FILTER) && (this.lastIndex >= 0) && (!requestData.dataLoaded.isEmpty()) && (DataCacheUtils.getNextCandleStartFast(this.period, ((CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).time) < this.buffer[0].time))
/*      */             {
/* 1454 */               if (assertionsEnabled()) {
/* 1455 */                 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 1456 */                 format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1457 */                 throw new RuntimeException(new StringBuilder().append("Data loaded not up to last data in buffer, instrument [").append(this.instrument).append("], period [").append(this.period).append("], requested number before [").append(requestData.numberOfCandlesBefore).append("] time [").append(requestData.time).append("](").append(format.format(Long.valueOf(requestData.time))).append(") number after [").append(requestData.numberOfCandlesAfter).append("], returned last data time [").append(((CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).time).append("](").append(format.format(Long.valueOf(((CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).time))).append("), expected data time [").append(DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[0].time)).append("](").append(format.format(Long.valueOf(DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[0].time)))).append(")").toString());
/*      */               }
/*      */ 
/* 1467 */               long nextCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, ((CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).time);
/* 1468 */               while (nextCandleTime < this.buffer[0].time) {
/* 1469 */                 double price = ((CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).close;
/* 1470 */                 requestData.dataLoaded.add(new CandleData(nextCandleTime, price, price, price, price, 0.0D));
/* 1471 */                 nextCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, nextCandleTime);
/*      */               }
/*      */             }
/*      */ 
/* 1475 */             if (!requestData.dataLoaded.isEmpty()) {
/* 1476 */               this.loadedNumberOfCandles = (this.lastIndex + 1);
/* 1477 */               int ignoreFirst = 0;
/* 1478 */               if (this.buffer[0].time == ((CandleData)requestData.dataLoaded.get(requestData.dataLoaded.size() - 1)).time) {
/* 1479 */                 ignoreFirst = 1;
/*      */               }
/* 1481 */               if (requestData.dataLoaded.size() - ignoreFirst > this.buffer.length - (this.lastIndex + 1)) {
/* 1482 */                 requestData.dataLoaded = requestData.dataLoaded.subList(requestData.dataLoaded.size() - (this.buffer.length - ignoreFirst - (this.lastIndex + 1)), requestData.dataLoaded.size());
/* 1483 */                 breakInterrupted = true;
/*      */               }
/*      */ 
/* 1486 */               if ((requestData.dataLoaded.isEmpty()) || (this.buffer.length - (this.lastIndex + 1) == 0)) {
/* 1487 */                 recalculateIndicators();
/*      */               } else {
/* 1489 */                 int sizeToCopy = this.buffer.length - requestData.dataLoaded.size() >= this.lastIndex + 1 - ignoreFirst ? this.lastIndex + 1 - ignoreFirst : this.buffer.length - requestData.dataLoaded.size();
/* 1490 */                 System.arraycopy(this.buffer, ignoreFirst, this.buffer, requestData.dataLoaded.size(), sizeToCopy);
/* 1491 */                 for (AbstractDataProvider.IndicatorData indicatorData : this.formulas.values()) {
/* 1492 */                   IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/* 1493 */                   int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); i < j; i++)
/*      */                   {
/*      */                     Object array;
/* 1495 */                     switch (6.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[indicator.getOutputParameterInfo(i).getType().ordinal()]) {
/*      */                     case 2:
/* 1497 */                       array = indicatorData.getOutputDataDouble()[i];
/* 1498 */                       break;
/*      */                     case 1:
/* 1500 */                       array = indicatorData.getOutputDataInt()[i];
/* 1501 */                       break;
/*      */                     case 3:
/* 1503 */                       array = indicatorData.getOutputDataObject()[i];
/* 1504 */                       break;
/*      */                     default:
/* 1506 */                       break;
/*      */                     }
/*      */ 
/* 1509 */                     System.arraycopy(array, ignoreFirst, array, requestData.dataLoaded.size(), sizeToCopy);
/*      */                   }
/*      */                 }
/* 1512 */                 this.lastIndex = (requestData.dataLoaded.size() - 1 + sizeToCopy);
/* 1513 */                 this.loadedTo = this.buffer[this.lastIndex].time;
/*      */ 
/* 1516 */                 System.arraycopy(requestData.dataLoaded.toArray(), 0, this.buffer, 0, requestData.dataLoaded.size());
/*      */ 
/* 1518 */                 long prevTime = -9223372036854775808L;
/* 1519 */                 List gapsList = new ArrayList();
/* 1520 */                 for (int i = 0; i <= this.lastIndex; i++) {
/* 1521 */                   CandleData dataElement = this.buffer[i];
/* 1522 */                   if ((prevTime != -9223372036854775808L) && (DataCacheUtils.getNextCandleStartFast(this.period, prevTime) != dataElement.time)) {
/* 1523 */                     long firstGapCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, prevTime);
/* 1524 */                     long lastGapCandleTime = DataCacheUtils.getPreviousCandleStartFast(this.period, dataElement.time);
/* 1525 */                     gapsList.add(new long[] { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.period, firstGapCandleTime, lastGapCandleTime) });
/*      */                   }
/* 1527 */                   prevTime = dataElement.time;
/*      */                 }
/* 1529 */                 this.gaps = ((long[][])gapsList.toArray(new long[gapsList.size()][]));
/*      */ 
/* 1531 */                 recalculateIndicators();
/*      */               }
/*      */             }
/*      */           }
/* 1535 */           if (!breakInterrupted) {
/* 1536 */             this.loadedNumberOfCandles = (this.loadedNumberOfCandles + requestData.numberOfCandlesBefore + requestData.numberOfCandlesAfter);
/* 1537 */             if (this.loadedNumberOfCandles > this.buffer.length)
/* 1538 */               this.loadedNumberOfCandles = this.buffer.length;
/*      */           }
/*      */           else {
/* 1541 */             this.loadedNumberOfCandles = (this.lastIndex + 1);
/*      */           }
/* 1543 */           if (!requestData.dataLoaded.isEmpty()) {
/* 1544 */             dataChanged = true;
/* 1545 */             dataChangedFrom = Math.min(dataChangedFrom, this.buffer[0].time);
/* 1546 */             dataChangedTo = Math.max(dataChangedTo, this.buffer[this.lastIndex].time);
/* 1547 */             dataChangedFirstData = false;
/*      */           }
/*      */         }
/* 1550 */         else if (!$assertionsDisabled) { throw new AssertionError("unknown request mode");
/*      */         }
/* 1552 */         checkConsistency();
/* 1553 */       } else if ((!allDataLoaded) && (e != null)) {
/* 1554 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/* 1557 */     if (dataChanged) {
/* 1558 */       if ((dataChangedFirstData) && (sparceIndicatorAttached())) {
/* 1559 */         dataChangedFrom = this.buffer[0].time;
/* 1560 */         dataChangedTo = this.buffer[this.lastIndex].time;
/*      */       }
/* 1562 */       fireDataChanged(dataChangedFrom, dataChangedTo, dataChangedFirstData, false);
/*      */     }
/*      */ 
/* 1565 */     if ((synchronizeIndicators != null) && (loadingFinished))
/* 1566 */       synchronizeIndicators.synchronizeIndicators();
/*      */   }
/*      */ 
/*      */   private void checkConsistency()
/*      */   {
/* 1572 */     if (assertionsEnabled()) {
/* 1573 */       if (this.lastIndex == -1)
/*      */       {
/* 1575 */         return;
/*      */       }
/*      */ 
/* 1578 */       if (this.lastIndex >= this.buffer.length) {
/* 1579 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/*      */ 
/* 1582 */       long firstCandleStart = this.buffer[0].time;
/* 1583 */       checkConsistency(firstCandleStart);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkConsistency(long candleStart) {
/* 1588 */     long prevTime = -9223372036854775808L;
/* 1589 */     List gapsList = new ArrayList();
/*      */ 
/* 1591 */     for (int i = 0; i <= this.lastIndex; i++) {
/* 1592 */       Data dataElement = this.buffer[i];
/* 1593 */       if (dataElement == null) {
/* 1594 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/* 1596 */       if ((prevTime != -9223372036854775808L) && (DataCacheUtils.getNextCandleStartFast(this.period, prevTime) != dataElement.time)) {
/* 1597 */         long firstGapCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, prevTime);
/* 1598 */         long lastGapCandleTime = DataCacheUtils.getPreviousCandleStartFast(this.period, dataElement.time);
/* 1599 */         gapsList.add(new long[] { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.period, firstGapCandleTime, lastGapCandleTime) });
/*      */       }
/* 1601 */       prevTime = dataElement.time;
/* 1602 */       if (this.filter == Filter.NO_FILTER) {
/* 1603 */         if (dataElement.time != candleStart) {
/* 1604 */           throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */         }
/* 1606 */         candleStart = DataCacheUtils.getNextCandleStartFast(this.period, candleStart);
/*      */       } else {
/* 1608 */         if (dataElement.time < candleStart) {
/* 1609 */           throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */         }
/* 1611 */         candleStart = DataCacheUtils.getNextCandleStartFast(this.period, dataElement.time);
/*      */       }
/*      */     }
/*      */ 
/* 1615 */     if (this.gaps.length != gapsList.size()) {
/* 1616 */       throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/* 1618 */     for (int i = 0; i < this.gaps.length; i++) {
/* 1619 */       long[] gap = (long[])gapsList.get(i);
/* 1620 */       if ((this.gaps[i][0] != gap[0]) || (this.gaps[i][1] != gap[1])) {
/* 1621 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */       }
/* 1623 */       if ((this.filter == Filter.WEEKENDS) && (this.period.getInterval() < Period.DAILY.getInterval()) && (this.gaps[i][1] <= 1L))
/* 1624 */         throw new RuntimeException("DataProviderImpl consistency check failed!!!");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean addFirstDataIfNeeded(long oldTime)
/*      */   {
/* 1631 */     long firstDataTime = ((CandleData)this.firstData).time;
/* 1632 */     if (oldTime == -9223372036854775808L)
/*      */     {
/* 1634 */       this.lastIndex = 0;
/* 1635 */       this.loadedNumberOfCandles = 1L;
/* 1636 */       this.loadedTo = firstDataTime;
/* 1637 */       this.buffer[this.lastIndex] = ((CandleData)this.firstData);
/* 1638 */       this.gaps = new long[0][];
/* 1639 */       recalculateIndicators();
/* 1640 */       checkConsistency();
/* 1641 */       return true;
/* 1642 */     }if ((this.lastIndex != -1) && (this.buffer[this.lastIndex].time == oldTime)) {
/* 1643 */       long lastElementTime = this.buffer[this.lastIndex].time;
/* 1644 */       if ((DataCacheUtils.getNextCandleStartFast(this.period, lastElementTime) < firstDataTime) && ((this.filter == Filter.NO_FILTER) || ((this.filter == Filter.WEEKENDS) && (!isWeekendsBetween(lastElementTime, firstDataTime)))))
/*      */       {
/* 1647 */         long nextCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, lastElementTime);
/* 1648 */         while (nextCandleTime < firstDataTime) {
/* 1649 */           double price = this.buffer[this.lastIndex].close;
/* 1650 */           if (this.lastIndex + 1 >= this.buffer.length) {
/* 1651 */             shiftLeft(5);
/*      */           }
/* 1653 */           this.lastIndex += 1;
/* 1654 */           this.buffer[this.lastIndex] = new CandleData(nextCandleTime, price, price, price, price, 0.0D);
/* 1655 */           this.loadedTo = nextCandleTime;
/* 1656 */           this.loadedNumberOfCandles = (this.lastIndex + 1);
/* 1657 */           nextCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, nextCandleTime);
/*      */         }
/*      */       }
/* 1660 */       if (this.lastIndex + 1 >= this.buffer.length) {
/* 1661 */         shiftLeft(5);
/*      */       }
/* 1663 */       this.lastIndex += 1;
/* 1664 */       this.buffer[this.lastIndex] = ((CandleData)this.firstData);
/* 1665 */       this.loadedTo = firstDataTime;
/* 1666 */       this.loadedNumberOfCandles = (this.lastIndex + 1);
/* 1667 */       if ((this.gaps.length > 0) && (this.gaps[(this.gaps.length - 1)][0] >= lastElementTime))
/*      */       {
/* 1669 */         this.gaps = ((long[][])Arrays.copyOf(this.gaps, this.gaps.length - 1));
/*      */       }
/* 1671 */       long firstGapCandleTime = DataCacheUtils.getNextCandleStartFast(this.period, this.buffer[(this.lastIndex - 1)].time);
/* 1672 */       if (firstGapCandleTime != this.buffer[this.lastIndex].time)
/*      */       {
/* 1674 */         this.gaps = ((long[][])Arrays.copyOf(this.gaps, this.gaps.length + 1));
/* 1675 */         long lastGapCandleTime = DataCacheUtils.getPreviousCandleStartFast(this.period, this.buffer[this.lastIndex].time);
/* 1676 */         this.gaps[(this.gaps.length - 1)] = { firstGapCandleTime, DataCacheUtils.getCandlesCountBetweenFast(this.period, firstGapCandleTime, lastGapCandleTime) };
/*      */       }
/*      */ 
/* 1679 */       recalculateIndicators(this.lastIndex, this.lastIndex, false);
/* 1680 */       checkConsistency();
/* 1681 */       return true;
/*      */     }
/* 1683 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized long getLastLoadedDataTime()
/*      */   {
/* 1688 */     if (this.lastIndex == -1) {
/* 1689 */       return -9223372036854775808L;
/*      */     }
/* 1691 */     return this.buffer[this.lastIndex].time;
/*      */   }
/*      */ 
/*      */   public Period getFilteredPeriod()
/*      */   {
/* 1696 */     if (this.period == Period.DAILY) {
/* 1697 */       return this.dailyFilterPeriod;
/*      */     }
/* 1699 */     return this.period;
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/* 1705 */     if (this.tickListener != null) {
/* 1706 */       this.feedDataProvider.unsubscribeFromLiveFeed(this.instrument, this.tickListener);
/* 1707 */       this.tickListener = null;
/* 1708 */       this.firstTickTime = -9223372036854775808L;
/*      */     }
/* 1710 */     if (this.firstDataListener != null) {
/* 1711 */       this.feedDataProvider.unsubscribeFromPeriodNotifications(this.instrument, this.period, this.side, this.firstDataListener);
/*      */     }
/* 1713 */     if (this.candleInProgressListener != null) {
/* 1714 */       this.feedDataProvider.removeInProgressCandleListener(this.instrument, this.period, this.side, this.candleInProgressListener);
/*      */     }
/* 1716 */     if (this.dataCacheRequestData != null) {
/* 1717 */       this.dataCacheRequestData.cancel = true;
/* 1718 */       this.dataCacheRequestData = null;
/*      */     }
/* 1720 */     super.dispose();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1776 */     return new StringBuilder().append("CandlesDataProvider(").append(this.instrument).append(", ").append(this.period).append(", ").append(this.side).append(")").toString();
/*      */   }
/*      */ 
/*      */   public DataType getDataType()
/*      */   {
/* 1781 */     return DataType.TIME_PERIOD_AGGREGATION;
/*      */   }
/*      */ 
/*      */   protected CandleData[] getAllBufferedData()
/*      */   {
/* 1786 */     if (this.buffer == null) {
/* 1787 */       return null;
/*      */     }
/* 1789 */     int bufferSize = this.lastIndex + 1;
/* 1790 */     CandleData[] copy = new CandleData[bufferSize];
/* 1791 */     System.arraycopy(this.buffer, 0, copy, 0, bufferSize);
/* 1792 */     return copy;
/*      */   }
/*      */ 
/*      */   protected CandleDataSequence createNullDataSequence()
/*      */   {
/* 1797 */     return new NullCandleDataSequence();
/*      */   }
/*      */ 
/*      */   protected CandleDataSequence createDataSequence(CandleData[] data, boolean includesLatestData)
/*      */   {
/*      */     CandleDataSequence result;
/*      */     CandleDataSequence result;
/* 1806 */     if ((data == null) || (data.length <= 0)) {
/* 1807 */       result = createNullDataSequence();
/*      */     }
/*      */     else {
/* 1810 */       result = new CandleDataSequence(this.dailyFilterPeriod, data[0].getTime(), data[(data.length - 1)].getTime(), 0, 0, data, (long[][])null, null, null, includesLatestData, includesLatestData);
/*      */     }
/*      */ 
/* 1825 */     return result;
/*      */   }
/*      */ 
/*      */   protected CandleData[] createArray(int size)
/*      */   {
/* 1830 */     return new CandleData[size];
/*      */   }
/*      */ 
/*      */   protected IFeedDescriptor getFeedDescriptor()
/*      */   {
/* 1835 */     IFeedDescriptor result = new FeedDescriptor();
/* 1836 */     result.setDataType(getDataType());
/* 1837 */     result.setInstrument(getInstrument());
/* 1838 */     result.setFilter(getFilter());
/* 1839 */     result.setOfferSide(getOfferSide());
/* 1840 */     result.setPeriod(getPeriod());
/* 1841 */     return result;
/*      */   }
/*      */ 
/*      */   protected CandleDataSequence doGetDataSequence(long from, long to)
/*      */   {
/* 1846 */     Period period = getPeriod();
/*      */ 
/* 1848 */     if (from != DataCacheUtils.getCandleStartFast(period, from)) {
/* 1849 */       throw new IllegalArgumentException(new StringBuilder().append(DATE_FORMAT.format(Long.valueOf(from))).append(" is not ").append(period).append(" candle start").toString());
/*      */     }
/* 1851 */     if (to != DataCacheUtils.getCandleStartFast(period, to)) {
/* 1852 */       throw new IllegalArgumentException(new StringBuilder().append(DATE_FORMAT.format(Long.valueOf(to))).append(" is not ").append(period).append(" candle start").toString());
/*      */     }
/*      */ 
/* 1855 */     int numberOfCandlesBefore = DataCacheUtils.getCandlesCountBetweenFast(period, from, to);
/* 1856 */     CandleDataSequence seq = getDataSequence(numberOfCandlesBefore, to, 0);
/* 1857 */     return seq;
/*      */   }
/*      */ 
/*      */   public long getFirstKnownTime()
/*      */   {
/* 1862 */     return this.feedDataProvider.getTimeOfFirstCandle(getInstrument(), getPeriod());
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider<CandleData, CandleDataSequence>.LoadDataProgressListener doHistoryRequests(long from, long to)
/*      */   {
/* 1867 */     Period period = getPeriod();
/*      */ 
/* 1869 */     if (from != DataCacheUtils.getCandleStartFast(period, from)) {
/* 1870 */       throw new IllegalArgumentException(new StringBuilder().append(DATE_FORMAT.format(Long.valueOf(from))).append(" is not ").append(period).append(" candle start").toString());
/*      */     }
/* 1872 */     if (to != DataCacheUtils.getCandleStartFast(period, to)) {
/* 1873 */       throw new IllegalArgumentException(new StringBuilder().append(DATE_FORMAT.format(Long.valueOf(to))).append(" is not ").append(period).append(" candle start").toString());
/*      */     }
/*      */ 
/* 1876 */     int numberOfCandlesBefore = DataCacheUtils.getCandlesCountBetweenFast(period, from, to);
/* 1877 */     return doHistoryRequests(numberOfCandlesBefore, to, 0);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   41 */     LOGGER = LoggerFactory.getLogger(CandlesDataProvider.class);
/*      */   }
/*      */ 
/*      */   protected static class LoadDataListener
/*      */     implements LiveFeedListener
/*      */   {
/*      */     private CandlesDataProvider.DataCacheRequestData dataCacheRequestData;
/* 1739 */     long prevTime = -9223372036854775808L;
/*      */     private boolean fromEnd;
/*      */ 
/*      */     public LoadDataListener(CandlesDataProvider.DataCacheRequestData dataCacheRequestData, boolean fromEnd)
/*      */     {
/* 1743 */       this.dataCacheRequestData = dataCacheRequestData;
/* 1744 */       this.fromEnd = fromEnd;
/* 1745 */       if (fromEnd)
/* 1746 */         this.prevTime = 9223372036854775807L;
/*      */     }
/*      */ 
/*      */     public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */     {
/* 1752 */       if (!this.dataCacheRequestData.cancel) {
/* 1753 */         if (((!this.fromEnd) && (this.prevTime > time)) || ((this.fromEnd) && (this.prevTime < time))) {
/* 1754 */           throw new RuntimeException("Received candles from data cache are not in ascending order");
/*      */         }
/* 1756 */         if (!this.fromEnd)
/* 1757 */           this.dataCacheRequestData.dataLoaded.add(new CandleData(time, open, close, low, high, vol));
/*      */         else {
/* 1759 */           this.dataCacheRequestData.dataLoaded.add(0, new CandleData(time, open, close, low, high, vol));
/*      */         }
/*      */ 
/* 1762 */         this.prevTime = time;
/*      */       }
/*      */       else {
/* 1765 */         this.dataCacheRequestData.dataLoaded = null;
/* 1766 */         this.dataCacheRequestData.progressListener = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static class DataCacheRequestData extends AbstractDataProvider.AbstractDataCacheRequestData
/*      */   {
/* 1724 */     public List<CandleData> dataLoaded = new ArrayList();
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1728 */       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 1729 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1730 */       StringBuilder stamp = new StringBuilder();
/* 1731 */       stamp.append(this.numberOfCandlesBefore).append(" - ").append(format.format(Long.valueOf(this.time))).append(" - ");
/* 1732 */       stamp.append(this.numberOfCandlesAfter).append(" loadedSize - ").append(this.dataLoaded.size());
/* 1733 */       return stamp.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.CandlesDataProvider
 * JD-Core Version:    0.6.0
 */