/*      */ package com.dukascopy.api.impl;
/*      */ 
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.IBar;
/*      */ import com.dukascopy.api.IHistory;
/*      */ import com.dukascopy.api.IOrder;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.LoadingDataListener;
/*      */ import com.dukascopy.api.LoadingOrdersListener;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.ReversalAmount;
/*      */ import com.dukascopy.api.TickBarSize;
/*      */ import com.dukascopy.api.feed.IPointAndFigure;
/*      */ import com.dukascopy.api.feed.IPointAndFigureFeedListener;
/*      */ import com.dukascopy.api.feed.IRangeBar;
/*      */ import com.dukascopy.api.feed.IRangeBarFeedListener;
/*      */ import com.dukascopy.api.feed.IRenkoBar;
/*      */ import com.dukascopy.api.feed.IRenkoBarFeedListener;
/*      */ import com.dukascopy.api.feed.ITickBar;
/*      */ import com.dukascopy.api.feed.ITickBarFeedListener;
/*      */ import com.dukascopy.api.impl.util.HistoryUtils;
/*      */ import com.dukascopy.api.impl.util.HistoryUtils.Loadable;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressAdapter;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.CloseData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*      */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*      */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*      */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*      */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*      */ import java.math.BigDecimal;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class History
/*      */   implements IHistory
/*      */ {
/*   70 */   private static final Logger LOGGER = LoggerFactory.getLogger(History.class);
/*      */ 
/*   72 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*      */   protected static final BigDecimal ONE_MILLION;
/*   80 */   protected AtomicBoolean ordersHistoryRequestSent = new AtomicBoolean();
/*      */   protected IOrdersProvider ordersProvider;
/*      */   protected Currency accountCurrency;
/*   83 */   protected IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/*      */   protected IPriceAggregationDataProvider priceAggregationDataProvider;
/*      */ 
/*      */   public History(IOrdersProvider ordersProvider, Currency accountCurrency)
/*      */   {
/*   87 */     this.ordersProvider = ordersProvider;
/*   88 */     this.accountCurrency = accountCurrency;
/*      */ 
/*   90 */     if (this.feedDataProvider != null)
/*   91 */       this.priceAggregationDataProvider = this.feedDataProvider.getPriceAggregationDataProvider();
/*      */   }
/*      */ 
/*      */   protected History(int noNeedToInitializeAnythingForTests)
/*      */   {
/*   96 */     this(null, null);
/*      */   }
/*      */ 
/*      */   public long getTimeOfLastTick(Instrument instrument) throws JFException {
/*  100 */     if (!isInstrumentSubscribed(instrument)) {
/*  101 */       throw new JFException("Instrument [" + instrument + "] not opened");
/*      */     }
/*  103 */     long time = this.feedDataProvider.getLastTickTime(instrument);
/*  104 */     return time == -9223372036854775808L ? -1L : time;
/*      */   }
/*      */ 
/*      */   public ITick getLastTick(Instrument instrument) throws JFException {
/*  108 */     if (!isInstrumentSubscribed(instrument)) {
/*  109 */       throw new JFException("Instrument [" + instrument + "] not opened");
/*      */     }
/*  111 */     TickData tick = this.feedDataProvider.getLastTick(instrument);
/*  112 */     if (tick != null) {
/*  113 */       return new TickData(tick.time, tick.ask, tick.bid, tick.askVol, tick.bidVol, null, null, null, null);
/*      */     }
/*  115 */     return null;
/*      */   }
/*      */ 
/*      */   protected long getCurrentTime(Instrument instrument)
/*      */   {
/*  120 */     return this.feedDataProvider.getCurrentTime(instrument);
/*      */   }
/*      */ 
/*      */   protected long getCurrentTimeBlocking(Instrument instrument) {
/*  124 */     long timeout = 120000L;
/*  125 */     long currentTime = -9223372036854775808L;
/*  126 */     long start = System.currentTimeMillis();
/*      */ 
/*  128 */     while (start + timeout > System.currentTimeMillis()) {
/*  129 */       currentTime = getCurrentTime(instrument);
/*  130 */       if (currentTime != -9223372036854775808L) {
/*      */         break;
/*      */       }
/*      */       try
/*      */       {
/*  135 */         Thread.sleep(500L);
/*      */       }
/*      */       catch (InterruptedException e)
/*      */       {
/*      */       }
/*      */     }
/*  141 */     return currentTime;
/*      */   }
/*      */ 
/*      */   public long getStartTimeOfCurrentBar(Instrument instrument, Period period) throws JFException {
/*  145 */     if (!isInstrumentSubscribed(instrument)) {
/*  146 */       throw new JFException("Instrument [" + instrument + "] not opened");
/*      */     }
/*  148 */     long timeOfCurrentCandle = getCurrentTime(instrument);
/*  149 */     return timeOfCurrentCandle == -9223372036854775808L ? -1L : DataCacheUtils.getCandleStartFast(period, timeOfCurrentCandle);
/*      */   }
/*      */ 
/*      */   public IBar getBar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException {
/*  153 */     if (!isInstrumentSubscribed(instrument))
/*  154 */       throw new JFException("Instrument [" + instrument + "] not opened");
/*      */     try
/*      */     {
/*  157 */       if (shift < 0) {
/*  158 */         throw new JFException("Parameter 'shift' is < 0");
/*      */       }
/*  160 */       if (shift == 0) {
/*  161 */         return getCurrentBar(instrument, period, side);
/*      */       }
/*      */ 
/*  164 */       return getHistoryBarBlocking(instrument, period, side, shift); } catch (DataCacheException e) {
/*      */     }
/*  166 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public IBar getCurrentBar(Instrument instrument, Period period, OfferSide side) throws DataCacheException
/*      */   {
/*  171 */     CandleData candle = this.feedDataProvider.getInProgressCandleBlocking(instrument, period, side);
/*  172 */     if (candle == null) {
/*  173 */       return null;
/*      */     }
/*  175 */     return new CandleData(candle.time, candle.open, candle.close, candle.low, candle.high, candle.vol);
/*      */   }
/*      */ 
/*      */   public IBar getHistoryBarBlocking(Instrument instrument, Period period, OfferSide side, int shift) throws JFException
/*      */   {
/*      */     try {
/*  181 */       if (shift <= 0) {
/*  182 */         throw new JFException("Parameter 'shift' must be > 0");
/*      */       }
/*      */ 
/*  185 */       long timeOfCurrentCandle = getCurrentTimeBlocking(instrument);
/*  186 */       if (timeOfCurrentCandle == -9223372036854775808L) {
/*  187 */         return null;
/*      */       }
/*  189 */       long currentBarStartTime = DataCacheUtils.getCandleStart(period, timeOfCurrentCandle);
/*  190 */       long requestedBarStartTime = DataCacheUtils.getTimeForNCandlesBack(period, DataCacheUtils.getPreviousCandleStart(period, currentBarStartTime), shift);
/*      */ 
/*  194 */       List bars = getBars(instrument, period, side, requestedBarStartTime, requestedBarStartTime);
/*  195 */       if (bars.isEmpty()) {
/*  196 */         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  197 */         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  198 */         throw new JFException("Could not load bar for instrument [" + instrument + "], period [" + period + "], side [" + side + "], start time [" + dateFormat.format(new Date(requestedBarStartTime)) + "], current bar start time [" + dateFormat.format(new Date(currentBarStartTime)) + "]");
/*      */       }
/*  200 */       return (IBar)bars.get(0);
/*      */     } catch (DataCacheException e) {
/*      */     }
/*  203 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public void readTicks(Instrument instrument, long from, long to, LoadingDataListener tickListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException
/*      */   {
/*  208 */     validateIntervalByPeriod(Period.TICK, from, to);
/*      */ 
/*  210 */     if (isInstrumentSubscribed(instrument)) {
/*  211 */       long timeOfCurrentCandle = getCurrentTime(instrument);
/*  212 */       if ((timeOfCurrentCandle != -9223372036854775808L) && (to > timeOfCurrentCandle))
/*  213 */         throw new JFException("\"to\" parameter can't be greater than time of the last tick for this instrument");
/*      */     }
/*      */     try
/*      */     {
/*  217 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, from, to, tickListener, loadingProgress) {
/*      */         public Object run() throws Exception {
/*  219 */           History.this.readTicksSecured(this.val$instrument, this.val$from, this.val$to, this.val$tickListener, this.val$loadingProgress);
/*  220 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  224 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readTicksSecured(Instrument instrument, long from, long to, LoadingDataListener tickListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException {
/*      */     try {
/*  230 */       this.feedDataProvider.loadTicksData(instrument, from, to, new LiveFeedListenerWrapper(tickListener), new LoadingProgressListenerWrapper(loadingProgress, false));
/*      */     } catch (DataCacheException e) {
/*  232 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readBars(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingDataListener barListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException {
/*  237 */     validateIntervalByPeriod(period, from, to);
/*      */ 
/*  239 */     if (isInstrumentSubscribed(instrument)) {
/*  240 */       long timeOfLastCandle = getCurrentTime(instrument);
/*  241 */       if (timeOfLastCandle != -9223372036854775808L) {
/*  242 */         timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/*  243 */         if (to >= timeOfLastCandle)
/*  244 */           throw new JFException("\"to\" parameter can't be greater than time of the last formed bar for this instrument");
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  249 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, from, to, barListener, loadingProgress) {
/*      */         public Object run() throws Exception {
/*  251 */           History.this.readBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$from, this.val$to, this.val$barListener, this.val$loadingProgress);
/*  252 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  256 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readBarsSecured(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingDataListener barListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException {
/*      */     try {
/*  262 */       this.feedDataProvider.loadCandlesData(instrument, period, side, from, to, new LiveFeedListenerWrapper(barListener), new LoadingProgressListenerWrapper(loadingProgress, false));
/*      */     } catch (DataCacheException e) {
/*  264 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter, LoadingDataListener barListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException {
/*  269 */     if (!isIntervalValid(period, numberOfCandlesBefore, time, numberOfCandlesAfter)) {
/*  270 */       throw new JFException("Number of bars to load = 0 or time is not correct time for the period specified");
/*      */     }
/*  272 */     if (isInstrumentSubscribed(instrument)) {
/*  273 */       long timeOfLastCandle = getCurrentTime(instrument);
/*  274 */       if (timeOfLastCandle != -9223372036854775808L) {
/*  275 */         timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/*  276 */         if (time > timeOfLastCandle)
/*  277 */           throw new JFException("\"to\" parameter can't be greater than time of the last formed bar for this instrument");
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  282 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, filter, numberOfCandlesBefore, time, numberOfCandlesAfter, barListener, loadingProgress) {
/*      */         public Object run() throws Exception {
/*  284 */           History.this.readBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$filter, this.val$numberOfCandlesBefore, this.val$time, this.val$numberOfCandlesAfter, this.val$barListener, this.val$loadingProgress);
/*  285 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  289 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readBarsSecured(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter, LoadingDataListener barListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException {
/*      */     try {
/*  295 */       this.feedDataProvider.loadCandlesDataBeforeAfter(instrument, period, side, numberOfCandlesBefore, numberOfCandlesAfter, time, filter, new LiveFeedListenerWrapper(barListener), new LoadingProgressListenerWrapper(loadingProgress, false));
/*      */     } catch (DataCacheException e) {
/*  297 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<ITick> getTicks(Instrument instrument, long from, long to) throws JFException {
/*  302 */     validateIntervalByPeriod(Period.TICK, from, to);
/*      */ 
/*  304 */     if (isInstrumentSubscribed(instrument)) {
/*  305 */       long timeOfCurrentCandle = getCurrentTime(instrument);
/*  306 */       if ((timeOfCurrentCandle != -9223372036854775808L) && 
/*  307 */         (to > timeOfCurrentCandle)) {
/*  308 */         throw new JFException("\"to\" parameter can't be greater than time of the last tick for this instrument");
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  313 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, from, to) {
/*      */         public List<ITick> run() throws Exception {
/*  315 */           return History.this.getTicksSecured(this.val$instrument, this.val$from, this.val$to);
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  319 */       HistoryUtils.throwJFException(e);
/*  320 */     }return null;
/*      */   }
/*      */ 
/*      */   protected List<ITick> getTicksSecured(Instrument instrument, long from, long to) throws JFException
/*      */   {
/*      */     try {
/*  326 */       List ticks = new ArrayList();
/*  327 */       int[] result = { 0 };
/*  328 */       Exception[] exceptions = new Exception[1];
/*  329 */       com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  334 */           this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  335 */           this.val$exceptions[0] = e;
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  339 */           return false;
/*      */         }
/*      */       };
/*  342 */       this.feedDataProvider.loadTicksDataSynched(instrument, from, to, new LiveFeedListener(ticks) {
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*  344 */           this.val$ticks.add(new TickData(time, ask, bid, askVol, bidVol, null, null, null, null));
/*      */         }
/*      */ 
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */         {
/*      */         }
/*      */       }
/*      */       , loadingProgressListener);
/*      */ 
/*  351 */       if (result[0] == 2) {
/*  352 */         throw new JFException("Error while loading ticks", exceptions[0]);
/*      */       }
/*  354 */       return ticks;
/*      */     } catch (DataCacheException e) {
/*      */     }
/*  357 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public ITick getLastTickBefore(Instrument instrument, long to) throws JFException
/*      */   {
/*  362 */     if (isInstrumentSubscribed(instrument)) {
/*  363 */       long timeOfCurrentCandle = getCurrentTime(instrument);
/*  364 */       if ((timeOfCurrentCandle != -9223372036854775808L) && (to > timeOfCurrentCandle))
/*  365 */         throw new JFException("\"to\" parameter can't be greater than time of the last tick for this instrument");
/*      */     }
/*      */     try
/*      */     {
/*  369 */       return (ITick)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, to) {
/*      */         public ITick run() throws Exception {
/*  371 */           return History.this.getLastTickBeforeSecured(this.val$instrument, this.val$to);
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  375 */       HistoryUtils.throwJFException(e);
/*  376 */     }return null;
/*      */   }
/*      */ 
/*      */   protected ITick getLastTickBeforeSecured(Instrument instrument, long to) throws JFException
/*      */   {
/*      */     try {
/*  382 */       long interval = 15000L;
/*  383 */       long from = to - interval;
/*  384 */       TickData[] tick = new TickData[1];
/*  385 */       int daysAdded = 0;
/*  386 */       while ((tick[0] == null) && (daysAdded < 5)) {
/*  387 */         int[] result = { 0 };
/*  388 */         Exception[] exceptions = new Exception[1];
/*  389 */         com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */           public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */           }
/*      */ 
/*      */           public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  394 */             this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  395 */             this.val$exceptions[0] = e;
/*      */           }
/*      */ 
/*      */           public boolean stopJob() {
/*  399 */             return false;
/*      */           }
/*      */         };
/*  402 */         this.feedDataProvider.loadTicksDataSynched(instrument, from, to, new LiveFeedListener(tick) {
/*      */           public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*  404 */             if (this.val$tick[0] == null) {
/*  405 */               this.val$tick[0] = new TickData(time, ask, bid, askVol, bidVol, null, null, null, null);
/*      */             } else {
/*  407 */               this.val$tick[0].time = time;
/*  408 */               this.val$tick[0].ask = ask;
/*  409 */               this.val$tick[0].bid = bid;
/*  410 */               this.val$tick[0].askVol = askVol;
/*  411 */               this.val$tick[0].bidVol = bidVol;
/*      */             }
/*      */           }
/*      */ 
/*      */           public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */           {
/*      */           }
/*      */         }
/*      */         , loadingProgressListener);
/*      */ 
/*  419 */         if (result[0] == 2) {
/*  420 */           throw new JFException("Error while loading ticks", exceptions[0]);
/*      */         }
/*  422 */         if (from < this.feedDataProvider.getTimeOfFirstCandle(instrument, Period.TICK)) {
/*  423 */           return null;
/*      */         }
/*  425 */         if (interval * 2L > 86400000L) {
/*  426 */           interval += 86400000L;
/*  427 */           daysAdded++;
/*      */         } else {
/*  429 */           interval *= 2L;
/*      */         }
/*  431 */         from = to - interval;
/*      */       }
/*  433 */       return tick[0]; } catch (DataCacheException e) {
/*      */     }
/*  435 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException
/*      */   {
/*  440 */     validateIntervalByPeriod(period, from, to);
/*      */ 
/*  442 */     if (isInstrumentSubscribed(instrument)) {
/*  443 */       long timeOfLastCandle = getCurrentTime(instrument);
/*  444 */       if (timeOfLastCandle != -9223372036854775808L) {
/*  445 */         timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/*  446 */         if (to > timeOfLastCandle)
/*  447 */           throw new JFException("\"to\" parameter can't be greater than the time of the last formed bar for this instrument");
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  452 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, from, to) {
/*      */         public List<IBar> run() throws Exception {
/*  454 */           return History.this.getBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$from, this.val$to);
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  458 */       HistoryUtils.throwJFException(e);
/*  459 */     }return null;
/*      */   }
/*      */ 
/*      */   protected List<IBar> getBarsSecured(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException
/*      */   {
/*      */     try {
/*  465 */       List bars = new ArrayList();
/*  466 */       int[] result = { 0 };
/*  467 */       Exception[] exceptions = new Exception[1];
/*  468 */       IBar currentBar = getCurrentBar(instrument, period, side);
/*  469 */       com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  474 */           this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  475 */           this.val$exceptions[0] = e;
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  479 */           return false;
/*      */         }
/*      */       };
/*  482 */       this.feedDataProvider.loadCandlesDataSynched(instrument, period, side, from, to, new LiveFeedListener(bars) {
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*      */         }
/*      */ 
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  487 */           this.val$bars.add(new CandleData(time, open, close, low, high, vol));
/*      */         }
/*      */       }
/*      */       , loadingProgressListener);
/*      */ 
/*  491 */       if (result[0] == 2) {
/*  492 */         throw new JFException("Error while loading bars", exceptions[0]);
/*      */       }
/*  494 */       if (currentBar != null)
/*      */       {
/*  496 */         if ((to == currentBar.getTime()) && ((bars.isEmpty()) || (((IBar)bars.get(bars.size() - 1)).getTime() <= currentBar.getTime()))) {
/*  497 */           if ((bars.size() > 0) && (((IBar)bars.get(bars.size() - 1)).getTime() == currentBar.getTime()))
/*      */           {
/*  499 */             bars.remove(bars.size() - 1);
/*      */           }
/*  501 */           bars.add(currentBar);
/*      */         }
/*      */       }
/*  504 */       return bars;
/*      */     } catch (DataCacheException e) {
/*      */     }
/*  507 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException
/*      */   {
/*  512 */     if (!isIntervalValid(period, numberOfCandlesBefore, time, numberOfCandlesAfter)) {
/*  513 */       throw new JFException("Number of bars to load = 0 or time is not correct time for the period specified");
/*      */     }
/*  515 */     if (isInstrumentSubscribed(instrument)) {
/*  516 */       long timeOfLastCandle = getCurrentTime(instrument);
/*  517 */       if (timeOfLastCandle != -9223372036854775808L) {
/*  518 */         timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/*  519 */         if (time > timeOfLastCandle)
/*  520 */           throw new JFException("\"to\" parameter can't be greater than time of the last formed bar for this instrument"); 
/*      */       }
/*      */     }
/*      */     Exception ex;
/*      */     try {
/*  525 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, filter, numberOfCandlesBefore, time, numberOfCandlesAfter) {
/*      */         public List<IBar> run() throws Exception {
/*  527 */           return History.this.getBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$filter, this.val$numberOfCandlesBefore, this.val$time, this.val$numberOfCandlesAfter);
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  531 */       ex = e.getException();
/*  532 */       if ((ex instanceof JFException))
/*  533 */         throw ((JFException)ex);
/*  534 */       if ((ex instanceof RuntimeException)) {
/*  535 */         throw ((RuntimeException)ex);
/*      */       }
/*  537 */       LOGGER.error(ex.getMessage(), ex);
/*  538 */     }throw new JFException(ex);
/*      */   }
/*      */ 
/*      */   protected List<IBar> getBarsSecured(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*      */     throws JFException
/*      */   {
/*      */     try
/*      */     {
/*  546 */       IBar currentBar = getCurrentBar(instrument, period, side);
/*  547 */       List bars = new ArrayList();
/*  548 */       int[] result = { 0 };
/*  549 */       Exception[] exceptions = new Exception[1];
/*  550 */       com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  555 */           this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  556 */           this.val$exceptions[0] = e;
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  560 */           return false;
/*      */         }
/*      */       };
/*  563 */       this.feedDataProvider.loadCandlesDataBeforeAfterSynched(instrument, period, side, numberOfCandlesBefore, numberOfCandlesAfter, time, filter, new LiveFeedListener(bars) {
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*      */         }
/*      */ 
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  568 */           this.val$bars.add(new CandleData(time, open, close, low, high, vol));
/*      */         }
/*      */       }
/*      */       , loadingProgressListener);
/*      */ 
/*  572 */       if ((currentBar != null) && (!bars.isEmpty()))
/*      */       {
/*      */         ListIterator iterator;
/*  573 */         if (((IBar)bars.get(bars.size() - 1)).getTime() > currentBar.getTime()) {
/*  574 */           for (iterator = bars.listIterator(bars.size()); iterator.hasPrevious(); ) {
/*  575 */             IBar bar = (IBar)iterator.previous();
/*  576 */             if (bar.getTime() <= currentBar.getTime()) break;
/*  577 */             iterator.remove();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  583 */         if (((IBar)bars.get(bars.size() - 1)).getTime() == currentBar.getTime())
/*      */         {
/*  585 */           bars.remove(bars.size() - 1);
/*  586 */           bars.add(currentBar);
/*      */         }
/*      */       }
/*  589 */       if (bars.size() < numberOfCandlesBefore + numberOfCandlesAfter) {
/*  590 */         if (currentBar != null)
/*      */         {
/*  592 */           if ((time == currentBar.getTime()) && (numberOfCandlesBefore != 0) && ((bars.isEmpty()) || (((IBar)bars.get(bars.size() - 1)).getTime() < currentBar.getTime())))
/*  593 */             bars.add(currentBar);
/*  594 */           else if ((time == currentBar.getTime()) && (numberOfCandlesBefore == 0) && (numberOfCandlesAfter > 0) && (bars.isEmpty()))
/*  595 */             bars.add(currentBar);
/*  596 */           else if ((time < currentBar.getTime()) && (numberOfCandlesAfter > 0) && ((bars.isEmpty()) || (((IBar)bars.get(bars.size() - 1)).getTime() < currentBar.getTime())))
/*      */           {
/*  598 */             if (((IBar)bars.get(0)).getTime() != this.feedDataProvider.getTimeOfFirstCandle(instrument, period))
/*  599 */               bars.add(currentBar);
/*      */           }
/*      */         }
/*      */       }
/*  603 */       else if ((currentBar != null) && (time == currentBar.getTime())) {
/*  604 */         IBar lastBar = (IBar)bars.get(bars.size() - 1);
/*  605 */         if (lastBar.getTime() != currentBar.getTime())
/*      */         {
/*  607 */           bars.remove(0);
/*  608 */           bars.add(currentBar);
/*      */         }
/*      */       }
/*  611 */       if (result[0] == 2) {
/*  612 */         throw new JFException("Error while loading bars", exceptions[0]);
/*      */       }
/*  614 */       return bars;
/*      */     } catch (DataCacheException e) {
/*      */     }
/*  617 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public void readOrdersHistory(Instrument instrument, long from, long to, LoadingOrdersListener ordersListener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/*  624 */     if (from > to) {
/*  625 */       throw new JFException("Interval from [" + DATE_FORMAT.format(new Date(from)) + "] to [" + DATE_FORMAT.format(new Date(to)) + "] GMT is not valid");
/*      */     }
/*  627 */     if (this.ordersHistoryRequestSent.compareAndSet(false, true))
/*      */       try {
/*      */         try {
/*  630 */           AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, from, to, ordersListener, loadingProgress) {
/*      */             public Object run() throws Exception {
/*  632 */               History.this.readOrdersHistorySecured(this.val$instrument, this.val$from, this.val$to, this.val$ordersListener, this.val$loadingProgress);
/*  633 */               return null;
/*      */             } } );
/*      */         } catch (PrivilegedActionException e) {
/*  637 */           Exception ex = e.getException();
/*  638 */           if ((ex instanceof JFException))
/*  639 */             throw ((JFException)ex);
/*  640 */           if ((ex instanceof RuntimeException)) {
/*  641 */             throw ((RuntimeException)ex);
/*      */           }
/*  643 */           LOGGER.error(ex.getMessage(), ex);
/*  644 */           throw new JFException(ex);
/*      */         }
/*      */       }
/*      */       finally {
/*  648 */         this.ordersHistoryRequestSent.set(false);
/*      */       }
/*      */     else
/*  651 */       throw new JFException("Only one request for orders history can be sent at one time");
/*      */   }
/*      */ 
/*      */   private void readOrdersHistorySecured(Instrument instrument, long from, long to, LoadingOrdersListener ordersListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException
/*      */   {
/*      */     try {
/*  657 */       this.feedDataProvider.loadOrdersHistoricalData(instrument, from, to, new LoadingOrdersListenerWrapper(ordersListener, from, to), new LoadingProgressListenerWrapper(loadingProgress, true));
/*      */     } catch (DataCacheException e) {
/*  659 */       throw new JFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<IOrder> getOrdersHistory(Instrument instrument, long from, long to) throws JFException
/*      */   {
/*  665 */     if (from > to) {
/*  666 */       throw new JFException("Interval from [" + DATE_FORMAT.format(new Date(from)) + "] to [" + DATE_FORMAT.format(new Date(to)) + "] GMT is not valid");
/*      */     }
/*  668 */     if (this.ordersHistoryRequestSent.compareAndSet(false, true)) {
/*      */       try
/*      */       {
/*  671 */         List localList = (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, from, to) {
/*      */           public List<IOrder> run() throws Exception {
/*  673 */             return History.this.getOrdersHistorySecured(this.val$instrument, this.val$from, this.val$to);
/*      */           }
/*      */         });
/*      */         return localList;
/*      */       }
/*      */       catch (PrivilegedActionException e)
/*      */       {
/*  677 */         Exception ex = e.getException();
/*  678 */         if ((ex instanceof JFException))
/*  679 */           throw ((JFException)ex);
/*  680 */         if ((ex instanceof RuntimeException)) {
/*  681 */           throw ((RuntimeException)ex);
/*      */         }
/*  683 */         LOGGER.error(ex.getMessage(), ex);
/*  684 */         throw new JFException(ex);
/*      */       }
/*      */       finally
/*      */       {
/*  688 */         this.ordersHistoryRequestSent.set(false);
/*      */       }
/*      */     }
/*  691 */     throw new JFException("Only one request for orders history can be sent at one time");
/*      */   }
/*      */ 
/*      */   protected List<IOrder> getOrdersHistorySecured(Instrument instrument, long from, long to) throws JFException
/*      */   {
/*      */     try {
/*  697 */       List orders = new ArrayList();
/*  698 */       int[] result = { 0 };
/*  699 */       Exception[] exceptions = new Exception[1];
/*  700 */       com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  705 */           this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  706 */           this.val$exceptions[0] = e;
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  710 */           return false;
/*      */         }
/*      */       };
/*  713 */       this.feedDataProvider.loadOrdersHistoricalDataSynched(instrument, from, to, new OrdersListener(from, to, orders)
/*      */       {
/*      */         public void newOrder(Instrument instrument, OrderHistoricalData orderData) {
/*  716 */           if (!orderData.isClosed()) {
/*  717 */             return;
/*      */           }
/*  719 */           HistoryOrder order = History.this.processOrders(instrument, orderData, this.val$from, this.val$to);
/*  720 */           if (order != null)
/*  721 */             this.val$orders.add(order);
/*      */         }
/*      */ 
/*      */         public void orderChange(Instrument instrument, OrderHistoricalData orderData)
/*      */         {
/*      */         }
/*      */ 
/*      */         public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*      */         {
/*      */         }
/*      */ 
/*      */         public void ordersInvalidated(Instrument instrument)
/*      */         {
/*      */         }
/*      */       }
/*      */       , loadingProgressListener);
/*      */ 
/*  738 */       if (result[0] == 2) {
/*  739 */         throw new JFException("Error while loading orders history", exceptions[0]);
/*      */       }
/*  741 */       return orders;
/*      */     } catch (DataCacheException e) {
/*      */     }
/*  744 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   protected HistoryOrder processOrders(Instrument instrument, OrderHistoricalData orderData, long from, long to)
/*      */   {
/*  749 */     long openTime = orderData.getEntryOrder().getFillTime();
/*  750 */     OrderHistoricalData.CloseData[] closeDatas = (OrderHistoricalData.CloseData[])orderData.getCloseDataMap().values().toArray(new OrderHistoricalData.CloseData[orderData.getCloseDataMap().size()]);
/*  751 */     BigDecimal closePriceMulAmount = BigDecimal.ZERO;
/*  752 */     BigDecimal sumClosedAmounts = BigDecimal.ZERO;
/*  753 */     long closeTime = orderData.getMergedToTime() == -9223372036854775808L ? orderData.getHistoryEnd() : orderData.getMergedToTime();
/*  754 */     for (OrderHistoricalData.CloseData closeData : closeDatas) {
/*  755 */       closeTime = closeData.getCloseTime();
/*  756 */       closePriceMulAmount = closePriceMulAmount.add(closeData.getClosePrice().multiply(closeData.getAmount()));
/*  757 */       sumClosedAmounts = sumClosedAmounts.add(closeData.getAmount());
/*      */     }
/*  759 */     if ((to >= openTime) && (from <= closeTime)) {
/*  760 */       double closePrice = 0.0D;
/*  761 */       if (sumClosedAmounts.compareTo(BigDecimal.ZERO) != 0) {
/*  762 */         closePrice = closePriceMulAmount.divide(sumClosedAmounts, 7, 6).doubleValue();
/*      */       }
/*  764 */       return createHistoryOrder(instrument, orderData, closeTime, closePrice);
/*      */     }
/*  766 */     return null;
/*      */   }
/*      */ 
/*      */   protected HistoryOrder createHistoryOrder(Instrument instrument, OrderHistoricalData orderData, long closeTime, double closePrice) {
/*  770 */     OrderHistoricalData.OpenData entryOrder = orderData.getEntryOrder();
/*  771 */     return new HistoryOrder(instrument, entryOrder.getLabel(), orderData.getOrderGroupId(), entryOrder.getFillTime(), closeTime, entryOrder.getSide(), entryOrder.getAmount().divide(ONE_MILLION).doubleValue(), entryOrder.getOpenPrice().doubleValue(), closePrice, entryOrder.getComment(), this.accountCurrency, orderData.getCommission().doubleValue());
/*      */   }
/*      */ 
/*      */   public void validateIntervalByPeriod(Period period, long from, long to)
/*      */     throws JFException
/*      */   {
/*  777 */     if (!isIntervalValid(period, from, to))
/*  778 */       throw new JFException("Interval from [" + DATE_FORMAT.format(new Date(from)) + "] to [" + DATE_FORMAT.format(new Date(to)) + "] GMT is not valid for period [" + period + "]");
/*      */   }
/*      */ 
/*      */   public boolean isIntervalValid(Period period, long from, long to)
/*      */   {
/*  783 */     boolean ret = from <= to;
/*  784 */     if ((ret) && (period != Period.TICK)) {
/*      */       try {
/*  786 */         ret = (ret) && (DataCacheUtils.isIntervalValid(period, from, to));
/*      */       } catch (DataCacheException e) {
/*  788 */         LOGGER.debug(e.getMessage(), e);
/*  789 */         ret = false;
/*      */       }
/*      */     }
/*  792 */     return ret;
/*      */   }
/*      */ 
/*      */   protected boolean isIntervalValid(Period period, int before, long time, int after) {
/*  796 */     return (period != Period.TICK) && (DataCacheUtils.getCandleStartFast(period, time) == time) && ((before > 0) || (after > 0));
/*      */   }
/*      */ 
/*      */   public long getBarStart(Period period, long time) throws JFException {
/*      */     try {
/*  801 */       return DataCacheUtils.getCandleStart(period, time); } catch (DataCacheException e) {
/*      */     }
/*  803 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public long getNextBarStart(Period period, long barTime) throws JFException
/*      */   {
/*      */     try {
/*  809 */       return DataCacheUtils.getNextCandleStart(period, barTime); } catch (DataCacheException e) {
/*      */     }
/*  811 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public long getPreviousBarStart(Period period, long barTime) throws JFException
/*      */   {
/*      */     try {
/*  817 */       return DataCacheUtils.getPreviousCandleStart(period, barTime); } catch (DataCacheException e) {
/*      */     }
/*  819 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public long getTimeForNBarsBack(Period period, long to, int numberOfBars) throws JFException
/*      */   {
/*      */     try {
/*  825 */       return DataCacheUtils.getTimeForNCandlesBack(period, to, numberOfBars); } catch (DataCacheException e) {
/*      */     }
/*  827 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public long getTimeForNBarsForward(Period period, long from, int numberOfBars) throws JFException
/*      */   {
/*      */     try {
/*  833 */       return DataCacheUtils.getTimeForNCandlesForward(period, from, numberOfBars); } catch (DataCacheException e) {
/*      */     }
/*  835 */     throw new JFException(e);
/*      */   }
/*      */ 
/*      */   public boolean isInstrumentSubscribed(Instrument instrument)
/*      */   {
/*  840 */     return this.feedDataProvider.isSubscribedToInstrument(instrument);
/*      */   }
/*      */ 
/*      */   public double getEquity()
/*      */   {
/*  845 */     return this.ordersProvider.recalculateEquity();
/*      */   }
/*      */ 
/*      */   public List<IPointAndFigure> getPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long from, long to)
/*      */     throws JFException
/*      */   {
/*  937 */     HistoryUtils.validatePointAndFigureParams(instrument, offerSide, priceRange, reversalAmount);
/*  938 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, TimeDataUtils.getSuitablePeriod(priceRange, reversalAmount), from, to);
/*      */ 
/*  940 */     long correctedFrom = HistoryUtils.correctRequestTime(from, priceRange, reversalAmount);
/*  941 */     long correctedTo = HistoryUtils.correctRequestTime(to, priceRange, reversalAmount);
/*      */     try
/*      */     {
/*  944 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, reversalAmount, correctedFrom, correctedTo) {
/*      */         public List<IPointAndFigure> run() throws Exception {
/*  946 */           List data = History.this.priceAggregationDataProvider.loadPointAndFigureTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, this.val$correctedFrom, this.val$correctedTo, true);
/*  947 */           List result = HistoryUtils.convert(data);
/*  948 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  952 */       HistoryUtils.throwJFException(e);
/*  953 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<ITickBar> getTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long from, long to)
/*      */     throws JFException
/*      */   {
/*  967 */     HistoryUtils.validateTickBarParams(instrument, offerSide, tickBarSize);
/*  968 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, from, to);
/*      */     try
/*      */     {
/*  971 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, tickBarSize, from, to) {
/*      */         public List<ITickBar> run() throws Exception {
/*  973 */           List data = History.this.priceAggregationDataProvider.loadTickBarTimeInterval(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$from, this.val$to, true);
/*  974 */           List result = HistoryUtils.convert(data);
/*  975 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/*  979 */       HistoryUtils.throwJFException(e);
/*  980 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<IRangeBar> getRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long from, long to)
/*      */     throws JFException
/*      */   {
/*  993 */     HistoryUtils.validateRangeBarParams(instrument, offerSide, priceRange);
/*  994 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, TimeDataUtils.getSuitablePeriod(priceRange), from, to);
/*      */ 
/*  996 */     long correctedFrom = HistoryUtils.correctRequestTime(from, priceRange);
/*  997 */     long correctedTo = HistoryUtils.correctRequestTime(to, priceRange);
/*      */     try
/*      */     {
/* 1000 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, correctedFrom, correctedTo) {
/*      */         public List<IRangeBar> run() throws Exception {
/* 1002 */           List data = History.this.priceAggregationDataProvider.loadPriceRangeTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$correctedFrom, this.val$correctedTo, true);
/* 1003 */           List result = HistoryUtils.convert(data);
/*      */ 
/* 1005 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1009 */       HistoryUtils.throwJFException(e);
/* 1010 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<IPointAndFigure> getPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */     throws JFException
/*      */   {
/* 1025 */     HistoryUtils.validatePointAndFigureParams(instrument, offerSide, priceRange, reversalAmount);
/* 1026 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1029 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, reversalAmount, numberOfBarsBefore, time, numberOfBarsAfter) {
/*      */         public List<IPointAndFigure> run() throws Exception {
/* 1031 */           List data = History.this.priceAggregationDataProvider.loadPointAndFigureData(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, true);
/*      */ 
/* 1042 */           List result = HistoryUtils.convert(data);
/* 1043 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1047 */       HistoryUtils.throwJFException(e);
/* 1048 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<IRangeBar> getRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */     throws JFException
/*      */   {
/* 1063 */     HistoryUtils.validateRangeBarParams(instrument, offerSide, priceRange);
/* 1064 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1067 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, numberOfBarsBefore, time, numberOfBarsAfter) {
/*      */         public List<IRangeBar> run() throws Exception {
/* 1069 */           List data = History.this.priceAggregationDataProvider.loadPriceRangeData(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, true);
/*      */ 
/* 1079 */           List result = HistoryUtils.convert(data);
/* 1080 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1084 */       HistoryUtils.throwJFException(e);
/* 1085 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<ITickBar> getTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */     throws JFException
/*      */   {
/* 1100 */     HistoryUtils.validateTickBarParams(instrument, offerSide, tickBarSize);
/* 1101 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1104 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, tickBarSize, numberOfBarsBefore, time, numberOfBarsAfter) {
/*      */         public List<ITickBar> run() throws Exception {
/* 1106 */           List data = History.this.priceAggregationDataProvider.loadTickBarData(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, true);
/*      */ 
/* 1116 */           List result = HistoryUtils.convert(data);
/* 1117 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1121 */       HistoryUtils.throwJFException(e);
/* 1122 */     }return null;
/*      */   }
/*      */ 
/*      */   public void readPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long from, long to, IPointAndFigureFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1138 */     HistoryUtils.validatePointAndFigureParams(instrument, offerSide, priceRange, reversalAmount, listener, loadingProgress);
/* 1139 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, TimeDataUtils.getSuitablePeriod(priceRange, reversalAmount), from, to);
/*      */ 
/* 1141 */     long correctedFrom = HistoryUtils.correctRequestTime(from, priceRange, reversalAmount);
/* 1142 */     long correctedTo = HistoryUtils.correctRequestTime(to, priceRange, reversalAmount);
/*      */     try
/*      */     {
/* 1145 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, reversalAmount, correctedFrom, correctedTo, listener, loadingProgress) {
/*      */         public Void run() throws Exception {
/* 1147 */           History.this.priceAggregationDataProvider.loadPointAndFigureTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, this.val$correctedFrom, this.val$correctedTo, new IPointAndFigureLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(PointAndFigureData pointAndFigure)
/*      */             {
/* 1157 */               History.26.this.val$listener.onBar(History.26.this.val$instrument, History.26.this.val$offerSide, History.26.this.val$priceRange, History.26.this.val$reversalAmount, pointAndFigure);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1163 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1167 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, int numberOfBarsBefore, long time, int numberOfBarsAfter, IPointAndFigureFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1184 */     HistoryUtils.validatePointAndFigureParams(instrument, offerSide, priceRange, reversalAmount, listener, loadingProgress);
/* 1185 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1188 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, reversalAmount, numberOfBarsBefore, time, numberOfBarsAfter, listener, loadingProgress) {
/*      */         public Void run() throws Exception {
/* 1190 */           History.this.priceAggregationDataProvider.loadPointAndFigureData(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, new IPointAndFigureLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(PointAndFigureData pointAndFigure)
/*      */             {
/* 1201 */               History.27.this.val$listener.onBar(History.27.this.val$instrument, History.27.this.val$offerSide, History.27.this.val$priceRange, History.27.this.val$reversalAmount, pointAndFigure);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1207 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1211 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long from, long to, ITickBarFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1227 */     HistoryUtils.validateTickBarParams(instrument, offerSide, tickBarSize, listener, loadingProgress);
/* 1228 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, from, to);
/*      */     try
/*      */     {
/* 1231 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, tickBarSize, from, to, listener, loadingProgress) {
/*      */         public Void run() throws Exception {
/* 1233 */           History.this.priceAggregationDataProvider.loadTickBarTimeInterval(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$from, this.val$to, new ITickBarLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(TickBarData tickBar)
/*      */             {
/* 1242 */               History.28.this.val$listener.onBar(History.28.this.val$instrument, History.28.this.val$offerSide, History.28.this.val$tickBarSize, tickBar);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1248 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1252 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, ITickBarFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1268 */     HistoryUtils.validateTickBarParams(instrument, offerSide, tickBarSize, listener, loadingProgress);
/* 1269 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1272 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, tickBarSize, numberOfBarsBefore, time, numberOfBarsAfter, listener, loadingProgress) {
/*      */         public Void run() throws Exception {
/* 1274 */           History.this.priceAggregationDataProvider.loadTickBarData(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, new ITickBarLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(TickBarData bar)
/*      */             {
/* 1284 */               History.29.this.val$listener.onBar(History.29.this.val$instrument, History.29.this.val$offerSide, History.29.this.val$tickBarSize, bar);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1290 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1294 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long from, long to, IRangeBarFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1309 */     HistoryUtils.validateRangeBarParams(instrument, offerSide, priceRange, listener, loadingProgress);
/* 1310 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, TimeDataUtils.getSuitablePeriod(priceRange), from, to);
/*      */ 
/* 1312 */     long correctedFrom = HistoryUtils.correctRequestTime(from, priceRange);
/* 1313 */     long correctedTo = HistoryUtils.correctRequestTime(to, priceRange);
/*      */     try
/*      */     {
/* 1316 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, correctedFrom, correctedTo, listener, loadingProgress)
/*      */       {
/*      */         public Void run() throws Exception {
/* 1319 */           History.this.priceAggregationDataProvider.loadPriceRangeTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$correctedFrom, this.val$correctedTo, new IPriceRangeLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(PriceRangeData bar)
/*      */             {
/* 1328 */               History.30.this.val$listener.onBar(History.30.this.val$instrument, History.30.this.val$offerSide, History.30.this.val$priceRange, bar);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1335 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1339 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRangeBarFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1356 */     HistoryUtils.validateRangeBarParams(instrument, offerSide, priceRange, listener, loadingProgress);
/* 1357 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1360 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, numberOfBarsBefore, time, numberOfBarsAfter, listener, loadingProgress)
/*      */       {
/*      */         public Void run() throws Exception {
/* 1363 */           History.this.priceAggregationDataProvider.loadPriceRangeData(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, new IPriceRangeLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(PriceRangeData bar)
/*      */             {
/* 1373 */               History.31.this.val$listener.onBar(History.31.this.val$instrument, History.31.this.val$offerSide, History.31.this.val$priceRange, bar);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1380 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1384 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private ITick getTickSecured(Instrument instrument, int shift) throws JFException
/*      */   {
/* 1390 */     ITick lastTick = this.feedDataProvider.getLastTick(instrument);
/* 1391 */     if (lastTick == null) {
/* 1392 */       return null;
/*      */     }
/* 1394 */     if (shift == 0) {
/* 1395 */       return lastTick;
/*      */     }
/*      */     try
/*      */     {
/* 1399 */       HistoryUtils.Loadable loadable = new HistoryUtils.Loadable(instrument)
/*      */       {
/*      */         public List<ITick> load(long from, long to) throws Exception {
/* 1402 */           List bars = new ArrayList(3600);
/* 1403 */           History.this.feedDataProvider.loadTicksDataSynched(this.val$instrument, from, to, new LiveFeedListener(bars)
/*      */           {
/*      */             public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */             {
/* 1410 */               this.val$bars.add(new TickData(time, ask, bid, askVol, bidVol));
/*      */             }
/*      */ 
/*      */             public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */             {
/*      */             }
/*      */           }
/*      */           , new LoadingProgressAdapter()
/*      */           {
/*      */           });
/* 1418 */           return bars;
/*      */         }
/*      */ 
/*      */         public long correctTime(long time) {
/* 1422 */           return time;
/*      */         }
/*      */ 
/*      */         public long getStep() {
/* 1426 */           return 3600000L;
/*      */         }
/*      */       };
/* 1430 */       return (ITick)HistoryUtils.getByShift(loadable, lastTick.getTime(), shift);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */     }
/*      */ 
/* 1436 */     throw new JFException(t);
/*      */   }
/*      */ 
/*      */   public ITick getTick(Instrument instrument, int shift)
/*      */     throws JFException
/*      */   {
/* 1443 */     if (instrument == null) {
/* 1444 */       throw new JFException("Instrument is null");
/*      */     }
/*      */ 
/* 1447 */     HistoryUtils.validateShift(shift);
/*      */     try
/*      */     {
/* 1450 */       return (ITick)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, shift) {
/*      */         public ITick run() throws Exception {
/* 1452 */           ITick tick = History.this.getTickSecured(this.val$instrument, this.val$shift);
/* 1453 */           return tick;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1457 */       HistoryUtils.throwJFException(e);
/* 1458 */     }return null;
/*      */   }
/*      */ 
/*      */   public IPointAndFigure getPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int shift)
/*      */     throws JFException
/*      */   {
/* 1472 */     HistoryUtils.validatePointAndFigureParams(instrument, offerSide, boxSize, reversalAmount);
/* 1473 */     HistoryUtils.validateShift(shift);
/*      */     try
/*      */     {
/* 1476 */       return (IPointAndFigure)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, boxSize, reversalAmount, shift) {
/*      */         public IPointAndFigure run() throws Exception {
/* 1478 */           IPointAndFigure inProgressBar = History.this.feedDataProvider.getIntraperiodBarsGenerator().getOrLoadInProgressPointAndFigure(this.val$instrument, this.val$offerSide, this.val$boxSize, this.val$reversalAmount);
/* 1479 */           if (inProgressBar == null) {
/* 1480 */             return null;
/*      */           }
/* 1482 */           if (this.val$shift == 0) {
/* 1483 */             return inProgressBar;
/*      */           }
/*      */ 
/* 1486 */           HistoryUtils.Loadable loadable = new HistoryUtils.Loadable()
/*      */           {
/*      */             public List<PointAndFigureData> load(long from, long to) throws Exception {
/* 1489 */               List result = History.this.priceAggregationDataProvider.loadPointAndFigureTimeInterval(History.34.this.val$instrument, History.34.this.val$offerSide, History.34.this.val$boxSize, History.34.this.val$reversalAmount, from, to, true);
/*      */ 
/* 1498 */               return result;
/*      */             }
/*      */ 
/*      */             public long correctTime(long time) {
/* 1502 */               return HistoryUtils.correctRequestTime(time, History.34.this.val$boxSize, History.34.this.val$reversalAmount);
/*      */             }
/*      */ 
/*      */             public long getStep() {
/* 1506 */               return 86400000L;
/*      */             }
/*      */           };
/* 1509 */           return (IPointAndFigure)HistoryUtils.getByShift(loadable, inProgressBar.getTime(), this.val$shift);
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 1514 */       HistoryUtils.throwJFException(e);
/* 1515 */     }return null;
/*      */   }
/*      */ 
/*      */   public ITickBar getTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int shift)
/*      */     throws JFException
/*      */   {
/* 1528 */     HistoryUtils.validateTickBarParams(instrument, offerSide, tickBarSize);
/* 1529 */     HistoryUtils.validateShift(shift);
/*      */     try
/*      */     {
/* 1532 */       return (ITickBar)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, tickBarSize, shift) {
/*      */         public ITickBar run() throws Exception {
/* 1534 */           ITickBar inProgressBar = History.this.feedDataProvider.getIntraperiodBarsGenerator().getOrLoadInProgressTickBar(this.val$instrument, this.val$offerSide, this.val$tickBarSize);
/* 1535 */           if (inProgressBar == null) {
/* 1536 */             return null;
/*      */           }
/* 1538 */           if (this.val$shift == 0) {
/* 1539 */             return inProgressBar;
/*      */           }
/*      */ 
/* 1542 */           HistoryUtils.Loadable loadable = new HistoryUtils.Loadable()
/*      */           {
/*      */             public List<TickBarData> load(long from, long to) throws Exception {
/* 1545 */               List result = History.this.priceAggregationDataProvider.loadTickBarTimeInterval(History.35.this.val$instrument, History.35.this.val$offerSide, History.35.this.val$tickBarSize, from, to, true);
/*      */ 
/* 1553 */               return result;
/*      */             }
/*      */ 
/*      */             public long correctTime(long time) {
/* 1557 */               return time;
/*      */             }
/*      */ 
/*      */             public long getStep() {
/* 1561 */               return History.35.this.val$tickBarSize.getSize() * 60 * 60 * 1000;
/*      */             }
/*      */           };
/* 1564 */           return (ITickBar)HistoryUtils.getByShift(loadable, inProgressBar.getTime(), this.val$shift);
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 1569 */       HistoryUtils.throwJFException(e);
/* 1570 */     }return null;
/*      */   }
/*      */ 
/*      */   public IRangeBar getRangeBar(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int shift)
/*      */     throws JFException
/*      */   {
/* 1581 */     HistoryUtils.validateRangeBarParams(instrument, offerSide, priceRange);
/* 1582 */     HistoryUtils.validateShift(shift);
/*      */     try
/*      */     {
/* 1585 */       return (IRangeBar)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, priceRange, shift) {
/*      */         public IRangeBar run() throws Exception {
/* 1587 */           IRangeBar inProgressBar = History.this.feedDataProvider.getIntraperiodBarsGenerator().getOrLoadInProgressPriceRange(this.val$instrument, this.val$offerSide, this.val$priceRange);
/* 1588 */           if (inProgressBar == null) {
/* 1589 */             return null;
/*      */           }
/* 1591 */           if (this.val$shift == 0) {
/* 1592 */             return inProgressBar;
/*      */           }
/*      */ 
/* 1595 */           HistoryUtils.Loadable loadable = new HistoryUtils.Loadable()
/*      */           {
/*      */             public List<PriceRangeData> load(long from, long to) throws Exception {
/* 1598 */               List result = History.this.priceAggregationDataProvider.loadPriceRangeTimeInterval(History.36.this.val$instrument, History.36.this.val$offerSide, History.36.this.val$priceRange, from, to, true);
/*      */ 
/* 1606 */               return result;
/*      */             }
/*      */ 
/*      */             public long correctTime(long time) {
/* 1610 */               return HistoryUtils.correctRequestTime(time, History.36.this.val$priceRange);
/*      */             }
/*      */ 
/*      */             public long getStep() {
/* 1614 */               return 86400000L;
/*      */             }
/*      */           };
/* 1617 */           return (IRangeBar)HistoryUtils.getByShift(loadable, inProgressBar.getTime(), this.val$shift);
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 1622 */       HistoryUtils.throwJFException(e);
/* 1623 */     }return null;
/*      */   }
/*      */ 
/*      */   public void validateTimeInterval(Instrument instrument, long from, long to) throws JFException
/*      */   {
/* 1628 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, from, to);
/*      */   }
/*      */ 
/*      */   public void validateBeforeTimeAfter(Instrument instrument, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */     throws JFException
/*      */   {
/* 1637 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */   }
/*      */ 
/*      */   public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to)
/*      */     throws JFException
/*      */   {
/* 1651 */     HistoryUtils.validate(instrument, period, side, filter);
/* 1652 */     validateTimeInterval(instrument, from, to);
/* 1653 */     validateIntervalByPeriod(period, from, to);
/* 1654 */     validateInstrumentSubscribed(instrument, period, to);
/*      */     try
/*      */     {
/* 1657 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, filter, from, to) {
/*      */         public List<IBar> run() throws Exception {
/* 1659 */           return History.this.getBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$filter, this.val$from, this.val$to);
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1663 */       HistoryUtils.throwJFException(e);
/* 1664 */     }return null;
/*      */   }
/*      */ 
/*      */   private void validateInstrumentSubscribed(Instrument instrument, Period period, long to) throws JFException
/*      */   {
/* 1669 */     if (isInstrumentSubscribed(instrument)) {
/* 1670 */       long timeOfLastCandle = getCurrentTime(instrument);
/* 1671 */       if (timeOfLastCandle != -9223372036854775808L) {
/* 1672 */         timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/* 1673 */         if (to > timeOfLastCandle)
/* 1674 */           throw new JFException("\"to\" parameter can't be greater than the time of the last formed bar for this instrument");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected List<IBar> getBarsSecured(Instrument instrument, Period period, OfferSide offerSide, Filter filter, long from, long to)
/*      */     throws JFException
/*      */   {
/* 1689 */     if ((filter == null) || (Filter.NO_FILTER.equals(filter))) {
/* 1690 */       List candles = getBarsSecured(instrument, period, offerSide, from, to);
/* 1691 */       return candles;
/*      */     }
/*      */ 
/* 1695 */     List candles = new ArrayList();
/*      */ 
/* 1697 */     LiveFeedListener candleListener = new LiveFeedListener(candles)
/*      */     {
/*      */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*      */       }
/*      */ 
/*      */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/* 1703 */         CandleData candle = new CandleData(time, open, close, low, high, vol);
/* 1704 */         this.val$candles.add(candle);
/*      */       }
/*      */     };
/* 1707 */     List exception = new ArrayList();
/* 1708 */     com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgress = new com.dukascopy.charts.data.datacache.LoadingProgressListener(exception)
/*      */     {
/*      */       public void dataLoaded(long start, long end, long currentPosition, String information) {
/*      */       }
/*      */ 
/*      */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 1714 */         if (e != null)
/* 1715 */           this.val$exception.add(e);
/*      */       }
/*      */ 
/*      */       public boolean stopJob()
/*      */       {
/* 1720 */         return false;
/*      */       }
/*      */     };
/*      */     try
/*      */     {
/* 1726 */       this.feedDataProvider.loadCandlesFromToSynched(instrument, period, offerSide, filter, from, to, candleListener, loadingProgress);
/*      */     } catch (DataCacheException e) {
/* 1728 */       throw new JFException("Error while bars loading", e);
/*      */     }
/*      */ 
/* 1731 */     if (!exception.isEmpty()) {
/* 1732 */       throw new JFException((Throwable)exception.get(0));
/*      */     }
/*      */ 
/* 1735 */     return candles;
/*      */   }
/*      */ 
/*      */   public void readBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to, LoadingDataListener barListener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1754 */     HistoryUtils.validate(instrument, period, side, filter);
/* 1755 */     validateTimeInterval(instrument, from, to);
/* 1756 */     validateIntervalByPeriod(period, from, to);
/* 1757 */     validateInstrumentSubscribed(instrument, period, to);
/*      */     try
/*      */     {
/* 1760 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, filter, from, to, barListener, loadingProgress) {
/*      */         public Object run() throws Exception {
/* 1762 */           History.this.readBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$filter, this.val$from, this.val$to, this.val$barListener, this.val$loadingProgress);
/* 1763 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1767 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void readBarsSecured(Instrument instrument, Period period, OfferSide offerSide, Filter filter, long from, long to, LoadingDataListener barListener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1783 */     if ((filter == null) || (Filter.NO_FILTER.equals(filter)))
/* 1784 */       readBarsSecured(instrument, period, offerSide, from, to, barListener, loadingProgress);
/*      */     else
/*      */       try
/*      */       {
/* 1788 */         this.feedDataProvider.loadCandlesFromTo(instrument, period, offerSide, filter, from, to, new LiveFeedListenerWrapper(barListener), new LoadingProgressListenerWrapper(loadingProgress, false));
/*      */       } catch (DataCacheException e) {
/* 1790 */         throw new JFException(e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public IRenkoBar getRenkoBar(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int shift)
/*      */     throws JFException
/*      */   {
/* 1804 */     HistoryUtils.validateRenkoBarParams(instrument, offerSide, brickSize);
/* 1805 */     HistoryUtils.validateShift(shift);
/*      */     try
/*      */     {
/* 1808 */       return (IRenkoBar)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, brickSize, shift) {
/*      */         public IRenkoBar run() throws Exception {
/* 1810 */           IRenkoBar inProgressBar = History.this.feedDataProvider.getIntraperiodBarsGenerator().getOrLoadInProgressRenko(this.val$instrument, this.val$offerSide, this.val$brickSize);
/* 1811 */           if (inProgressBar == null) {
/* 1812 */             return null;
/*      */           }
/* 1814 */           if (this.val$shift == 0) {
/* 1815 */             return inProgressBar;
/*      */           }
/*      */ 
/* 1818 */           HistoryUtils.Loadable loadable = new HistoryUtils.Loadable()
/*      */           {
/*      */             public List<RenkoData> load(long from, long to) throws Exception {
/* 1821 */               List result = History.this.priceAggregationDataProvider.loadRenkoTimeInterval(History.41.this.val$instrument, History.41.this.val$offerSide, History.41.this.val$brickSize, from, to, true);
/*      */ 
/* 1829 */               return result;
/*      */             }
/*      */ 
/*      */             public long correctTime(long time) {
/* 1833 */               return HistoryUtils.correctRequestTime(time, History.41.this.val$brickSize);
/*      */             }
/*      */ 
/*      */             public long getStep() {
/* 1837 */               return 86400000L;
/*      */             }
/*      */           };
/* 1840 */           return (IRenkoBar)HistoryUtils.getByShift(loadable, inProgressBar.getTime(), this.val$shift);
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException e) {
/* 1845 */       HistoryUtils.throwJFException(e);
/* 1846 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<IRenkoBar> getRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to)
/*      */     throws JFException
/*      */   {
/* 1858 */     HistoryUtils.validateRenkoBarParams(instrument, offerSide, brickSize);
/* 1859 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, TimeDataUtils.getSuitablePeriod(brickSize), from, to);
/*      */ 
/* 1861 */     long correctedFrom = HistoryUtils.correctRequestTime(from, brickSize);
/* 1862 */     long correctedTo = HistoryUtils.correctRequestTime(to, brickSize);
/*      */     try
/*      */     {
/* 1865 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, brickSize, correctedFrom, correctedTo) {
/*      */         public List<IRenkoBar> run() throws Exception {
/* 1867 */           List data = History.this.priceAggregationDataProvider.loadRenkoTimeInterval(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$correctedFrom, this.val$correctedTo, true);
/* 1868 */           List result = HistoryUtils.convert(data);
/*      */ 
/* 1870 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1874 */       HistoryUtils.throwJFException(e);
/* 1875 */     }return null;
/*      */   }
/*      */ 
/*      */   public List<IRenkoBar> getRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */     throws JFException
/*      */   {
/* 1888 */     HistoryUtils.validateRenkoBarParams(instrument, offerSide, brickSize);
/* 1889 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1892 */       return (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, brickSize, numberOfBarsBefore, time, numberOfBarsAfter) {
/*      */         public List<IRenkoBar> run() throws Exception {
/* 1894 */           List data = History.this.priceAggregationDataProvider.loadRenkoData(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, true);
/*      */ 
/* 1904 */           List result = HistoryUtils.convert(data);
/* 1905 */           return result;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1909 */       HistoryUtils.throwJFException(e);
/* 1910 */     }return null;
/*      */   }
/*      */ 
/*      */   public void readRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoBarFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1924 */     HistoryUtils.validateRenkoBarParams(instrument, offerSide, brickSize, listener, loadingProgress);
/* 1925 */     HistoryUtils.validateTimeInterval(this.feedDataProvider, instrument, TimeDataUtils.getSuitablePeriod(brickSize), from, to);
/*      */ 
/* 1927 */     long correctedFrom = HistoryUtils.correctRequestTime(from, brickSize);
/* 1928 */     long correctedTo = HistoryUtils.correctRequestTime(to, brickSize);
/*      */     try
/*      */     {
/* 1931 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, brickSize, correctedFrom, correctedTo, listener, loadingProgress)
/*      */       {
/*      */         public Void run() throws Exception {
/* 1934 */           History.this.priceAggregationDataProvider.loadRenkoTimeInterval(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$correctedFrom, this.val$correctedTo, new IRenkoLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(RenkoData bar)
/*      */             {
/* 1943 */               History.44.this.val$listener.onBar(History.44.this.val$instrument, History.44.this.val$offerSide, History.44.this.val$brickSize, bar);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1950 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1954 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void readRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRenkoBarFeedListener listener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     throws JFException
/*      */   {
/* 1969 */     HistoryUtils.validateRenkoBarParams(instrument, offerSide, brickSize, listener, loadingProgress);
/* 1970 */     HistoryUtils.validateBeforeTimeAfter(this.feedDataProvider, instrument, numberOfBarsBefore, time, numberOfBarsAfter);
/*      */     try
/*      */     {
/* 1973 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, offerSide, brickSize, numberOfBarsBefore, time, numberOfBarsAfter, listener, loadingProgress)
/*      */       {
/*      */         public Void run() throws Exception {
/* 1976 */           History.this.priceAggregationDataProvider.loadRenkoData(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter, new IRenkoLiveFeedListener()
/*      */           {
/*      */             public void newPriceData(RenkoData renko)
/*      */             {
/* 1986 */               History.45.this.val$listener.onBar(History.45.this.val$instrument, History.45.this.val$offerSide, History.45.this.val$brickSize, renko);
/*      */             }
/*      */           }
/*      */           , new History.LoadingProgressListenerWrapper(History.this, this.val$loadingProgress, false), true);
/*      */ 
/* 1994 */           return null;
/*      */         } } );
/*      */     } catch (PrivilegedActionException e) {
/* 1998 */       HistoryUtils.throwJFException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   74 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */ 
/*   78 */     ONE_MILLION = BigDecimal.valueOf(1000000L);
/*      */   }
/*      */ 
/*      */   protected class LoadingProgressListenerWrapper
/*      */     implements com.dukascopy.charts.data.datacache.LoadingProgressListener
/*      */   {
/*      */     private com.dukascopy.api.LoadingProgressListener loadingProgressListener;
/*      */     private boolean ordersHistoryRequest;
/*      */ 
/*      */     public LoadingProgressListenerWrapper(com.dukascopy.api.LoadingProgressListener loadingProgressListener, boolean ordersHistoryRequest)
/*      */     {
/*  904 */       this.loadingProgressListener = loadingProgressListener;
/*  905 */       this.ordersHistoryRequest = ordersHistoryRequest;
/*      */     }
/*      */ 
/*      */     public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*  909 */       this.loadingProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */     }
/*      */ 
/*      */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  913 */       if (e != null) {
/*  914 */         History.LOGGER.error(e.getMessage(), e);
/*      */       }
/*  916 */       this.loadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, currentTime);
/*  917 */       if (this.ordersHistoryRequest)
/*  918 */         History.this.ordersHistoryRequestSent.set(false);
/*      */     }
/*      */ 
/*      */     public boolean stopJob()
/*      */     {
/*  923 */       return this.loadingProgressListener.stopJob();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class LiveFeedListenerWrapper
/*      */     implements LiveFeedListener
/*      */   {
/*      */     private LoadingDataListener loadingDataListener;
/*      */ 
/*      */     public LiveFeedListenerWrapper(LoadingDataListener loadingDataListener)
/*      */     {
/*  887 */       this.loadingDataListener = loadingDataListener;
/*      */     }
/*      */ 
/*      */     public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  891 */       this.loadingDataListener.newBar(instrument, period, side, time, open, close, low, high, vol);
/*      */     }
/*      */ 
/*      */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*  895 */       this.loadingDataListener.newTick(instrument, time, ask, bid, askVol, bidVol);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class LoadingOrdersListenerWrapper
/*      */     implements OrdersListener
/*      */   {
/*      */     private LoadingOrdersListener listener;
/*      */     private long from;
/*      */     private long to;
/*      */ 
/*      */     public LoadingOrdersListenerWrapper(LoadingOrdersListener listener, long from, long to)
/*      */     {
/*  854 */       this.listener = listener;
/*  855 */       this.from = from;
/*  856 */       this.to = to;
/*      */     }
/*      */ 
/*      */     public void newOrder(Instrument instrument, OrderHistoricalData orderData)
/*      */     {
/*  861 */       if (!orderData.isClosed()) {
/*  862 */         return;
/*      */       }
/*  864 */       HistoryOrder order = History.this.processOrders(instrument, orderData, this.from, this.to);
/*  865 */       if (order != null)
/*  866 */         this.listener.newOrder(instrument, order);
/*      */     }
/*      */ 
/*      */     public void orderChange(Instrument instrument, OrderHistoricalData orderData)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void ordersInvalidated(Instrument instrument)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.History
 * JD-Core Version:    0.6.0
 */