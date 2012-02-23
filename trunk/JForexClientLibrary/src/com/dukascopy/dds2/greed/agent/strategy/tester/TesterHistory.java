/*      */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*      */ 
/*      */ import I;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.IBar;
/*      */ import com.dukascopy.api.IOrder;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.LoadingOrdersListener;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.impl.History;
/*      */ import com.dukascopy.api.impl.History.LoadingOrdersListenerWrapper;
/*      */ import com.dukascopy.api.impl.History.LoadingProgressListenerWrapper;
/*      */ import com.dukascopy.api.impl.HistoryOrder;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils.ToLoad;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.LoadProgressingAction;
/*      */ import com.dukascopy.charts.data.datacache.NoDataForPeriodException;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*      */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*      */ import java.math.BigDecimal;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class TesterHistory extends History
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   protected static final SimpleDateFormat DATE_FORMAT;
/*      */   private static final int MIN_SHIFT = 20;
/*      */   private static final int MAX_CACHE_SIZE = 500;
/*      */   private final List<Period> currentPeriods;
/*      */   private final List<Period> initialPeriods;
/*   68 */   private final OfferSide[] offerSides = OfferSide.values();
/*      */ 
/*   71 */   private final Map<Instrument, Map<Period, Map<OfferSide, Data[]>>> bufferMap = new HashMap();
/*      */ 
/*   73 */   private final Map<Instrument, Map<Period, Map<OfferSide, Integer>>> lastIndexesMap = new HashMap();
/*      */ 
/*   75 */   protected TickData[] lastTicks = new TickData[Instrument.values().length];
/*   76 */   protected long[] currentCandleTime = new long[Instrument.values().length];
/*      */ 
/*   78 */   private final Map<Instrument, Map<Period, Map<OfferSide, TesterHistoryGeneratedBar>>> generatedCandlesMap = new HashMap();
/*      */   private IStrategyRunner strategyRunner;
/*      */   private TesterOrdersProvider ordersProvider;
/*      */   private AbstractCurrencyConverter currencyConverter;
/*   85 */   private final Period TICKS_PERIOD = Period.TICK;
/*   86 */   private final OfferSide TICKS_OFFER_SIDE = OfferSide.ASK;
/*      */   private final IFilterManager filterManager;
/*      */ 
/*      */   public TesterHistory(TesterOrdersProvider ordersProvider, IStrategyRunner strategyRunner, Currency accountCurrency, AbstractCurrencyConverter currencyConverter)
/*      */   {
/*  100 */     super(0);
/*  101 */     this.ordersProvider = ordersProvider;
/*  102 */     this.accountCurrency = accountCurrency;
/*  103 */     this.currencyConverter = currencyConverter;
/*  104 */     this.ordersProvider = ordersProvider;
/*  105 */     this.strategyRunner = strategyRunner;
/*      */ 
/*  107 */     this.initialPeriods = Arrays.asList(Period.values());
/*  108 */     this.currentPeriods = new ArrayList(this.initialPeriods);
/*      */ 
/*  110 */     for (int i = 0; i < this.lastTicks.length; i++) {
/*  111 */       this.lastTicks[i] = new TickData(-9223372036854775808L, 0.0D, 0.0D, 0.0D, 0.0D, null, null, null, null);
/*      */     }
/*  113 */     for (int i = 0; i < this.currentCandleTime.length; i++) {
/*  114 */       this.currentCandleTime[i] = -9223372036854775808L;
/*      */     }
/*      */ 
/*  117 */     if (this.feedDataProvider != null) {
/*  118 */       this.filterManager = this.feedDataProvider.getFilterManager();
/*      */     }
/*      */     else
/*      */     {
/*  124 */       this.filterManager = strategyRunner.getFeedDataProvider().getFilterManager();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized long getTimeOfLastTick(Instrument instrument) throws JFException
/*      */   {
/*  130 */     if (!isInstrumentSubscribed(instrument)) {
/*  131 */       throw new JFException("Instrument [" + instrument + "] not opened");
/*      */     }
/*  133 */     long time = this.lastTicks[instrument.ordinal()].time;
/*  134 */     return time == -9223372036854775808L ? -1L : time;
/*      */   }
/*      */ 
/*      */   public synchronized ITick getLastTick(Instrument instrument) throws JFException
/*      */   {
/*  139 */     if (!isInstrumentSubscribed(instrument)) {
/*  140 */       throw new JFException("Instrument [" + instrument + "] not opened");
/*      */     }
/*  142 */     return this.lastTicks[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   protected long getCurrentTime(Instrument instrument)
/*      */   {
/*  147 */     return this.currentCandleTime[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   public synchronized boolean isInstrumentSubscribed(Instrument instrument)
/*      */   {
/*  152 */     return this.currentCandleTime[instrument.ordinal()] > 0L;
/*      */   }
/*      */ 
/*      */   private void fillCacheBuffer(Instrument instrument, Period period, OfferSide aSide) throws JFException {
/*  156 */     int lastIndex = getLastIndex(instrument, period, aSide);
/*  157 */     if ((lastIndex == -1) && (getBuffer(instrument, period, aSide) == null))
/*      */     {
/*  159 */       Data[] buff = new Data[520];
/*  160 */       if (period != Period.TICK)
/*      */       {
/*  162 */         long lastCandleTime = getStartTimeOfCurrentBar(instrument, period);
/*  163 */         long to = DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, lastCandleTime));
/*  164 */         long from = DataCacheUtils.getTimeForNCandlesBackFast(period, to, 500);
/*      */ 
/*  166 */         long firstCandleTime = this.feedDataProvider.getTimeOfFirstCandle(instrument, period);
/*  167 */         firstCandleTime = DataCacheUtils.getCandleStartFast(period, firstCandleTime);
/*      */ 
/*  169 */         if (from < firstCandleTime) {
/*  170 */           from = firstCandleTime;
/*      */         }
/*      */ 
/*  173 */         List bars = super.getBars(instrument, period, aSide, from, to);
/*  174 */         int i = 0; for (int j = bars.size(); i < j; i++) {
/*  175 */           buff[i] = ((CandleData)bars.get(i));
/*      */         }
/*  177 */         if (bars.isEmpty()) {
/*  178 */           return;
/*      */         }
/*  180 */         lastIndex = bars.size() - 1;
/*      */ 
/*  182 */         CandleData inProgressCandle = getCurrentGeneratedBar(instrument, period, aSide);
/*  183 */         if (inProgressCandle == null) {
/*  184 */           if (assertionsEnabled()) {
/*  185 */             throw new RuntimeException("In progress candle doesn't exist");
/*      */           }
/*  187 */           return;
/*      */         }
/*  189 */         if (DataCacheUtils.getNextCandleStartFast(period, buff[lastIndex].getTime()) == inProgressCandle.time)
/*  190 */           lastIndex = addFirstData(buff, lastIndex, period, buff[lastIndex].getTime(), inProgressCandle);
/*      */       }
/*      */       else {
/*  193 */         TickData tick = (TickData)getLastTick(instrument);
/*  194 */         buff[0] = tick;
/*  195 */         lastIndex = 0;
/*      */       }
/*  197 */       setBuffer(instrument, period, aSide, buff);
/*      */ 
/*  199 */       setLastIndex(instrument, period, aSide, lastIndex);
/*  200 */       checkConsistency(period, buff, lastIndex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private TesterHistoryGeneratedBar updateBar(TesterHistoryGeneratedBar bar, long currentTime, long candleEnd)
/*      */   {
/*  209 */     if ((bar != null) && (bar.endTime == currentTime)) {
/*  210 */       bar.time = currentTime;
/*  211 */       bar.endTime = candleEnd;
/*  212 */       bar.open = bar.close;
/*  213 */       bar.high = bar.close;
/*  214 */       bar.low = bar.close;
/*  215 */       bar.vol = 0.0D;
/*  216 */       bar.flat = true;
/*      */     }
/*  218 */     return bar;
/*      */   }
/*      */ 
/*      */   public synchronized void addCandle(Instrument instrument, Period period, OfferSide side, CandleData candleData) {
/*  222 */     long currentTime = DataCacheUtils.getNextCandleStartFast(period, candleData.time);
/*  223 */     if (this.currentCandleTime[instrument.ordinal()] <= currentTime) {
/*  224 */       this.currentCandleTime[instrument.ordinal()] = currentTime;
/*  225 */       List periods = getPeriods(instrument);
/*  226 */       for (Period curPer : periods) {
/*  227 */         if (curPer == Period.TICK)
/*      */         {
/*      */           continue;
/*      */         }
/*  231 */         TesterHistoryGeneratedBar bar = getGeneratedCandle(instrument, curPer, side);
/*  232 */         if ((bar != null) && (bar.endTime == currentTime)) {
/*  233 */           long candleEnd = DataCacheUtils.getNextCandleStartFast(curPer, currentTime);
/*  234 */           bar = updateBar(bar, currentTime, candleEnd);
/*      */ 
/*  236 */           setGeneratedCandle(instrument, curPer, side, bar);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  242 */     Data[] buff = getBuffer(instrument, period, side);
/*  243 */     if (buff != null) {
/*  244 */       int lastIndex = getLastIndex(instrument, period, side);
/*  245 */       CandleData firstData = lastIndex == -1 ? null : (CandleData)buff[lastIndex];
/*  246 */       if ((firstData != null) && (firstData.time > candleData.time))
/*      */       {
/*  248 */         Exception notThrownException = new Exception("[" + (firstData.time - candleData.time) + "] Received candle has older time than pervious candle, ignoring");
/*      */ 
/*  250 */         LOGGER.error(notThrownException.getMessage(), notThrownException);
/*  251 */         return;
/*      */       }
/*      */ 
/*  254 */       if ((firstData != null) && (firstData.time == candleData.time))
/*      */       {
/*  256 */         firstData.open = candleData.open;
/*  257 */         firstData.close = candleData.close;
/*  258 */         firstData.high = candleData.high;
/*  259 */         firstData.low = candleData.low;
/*  260 */         firstData.vol = candleData.vol;
/*  261 */         CandleData inProgressCandle = getCurrentGeneratedBar(instrument, period, side);
/*  262 */         if ((inProgressCandle != null) && (DataCacheUtils.getNextCandleStartFast(period, firstData.time) == inProgressCandle.time))
/*      */         {
/*  264 */           int index = addFirstData(buff, lastIndex, period, firstData.time, inProgressCandle.clone());
/*  265 */           setLastIndex(instrument, period, side, index);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  270 */         int index = addFirstData(buff, lastIndex, period, firstData == null ? -9223372036854775808L : firstData.time, candleData);
/*  271 */         setLastIndex(instrument, period, side, index);
/*  272 */         lastIndex = index;
/*      */ 
/*  274 */         CandleData inProgressCandle = getCurrentGeneratedBar(instrument, period, side);
/*  275 */         if ((inProgressCandle != null) && (DataCacheUtils.getNextCandleStartFast(period, candleData.time) == inProgressCandle.time)) {
/*  276 */           index = addFirstData(buff, lastIndex, period, candleData.time, inProgressCandle.clone());
/*  277 */           setLastIndex(instrument, period, side, index);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void addTick(Instrument instrument, ITick tick) {
/*  284 */     this.lastTicks[instrument.ordinal()] = ((TickData)tick);
/*  285 */     long time = tick.getTime();
/*  286 */     this.currentCandleTime[instrument.ordinal()] = time;
/*      */ 
/*  288 */     updateInProgressCandles(instrument, tick);
/*  289 */     updateBuffers(instrument, tick);
/*      */   }
/*      */ 
/*      */   private void updateBuffers(Instrument instrument, ITick tick) {
/*  293 */     List periods = getPeriods(instrument);
/*  294 */     List offerSides = getOfferSides();
/*  295 */     for (Iterator i$ = periods.iterator(); i$.hasNext(); ) { period = (Period)i$.next();
/*  296 */       for (OfferSide offerSide : offerSides) {
/*  297 */         Data[] buff = getBuffer(instrument, period, offerSide);
/*  298 */         if (buff != null)
/*  299 */           updateBuffer(tick, instrument, period, offerSide, buff);
/*      */       } }
/*      */     Period period;
/*      */   }
/*      */ 
/*      */   private List<OfferSide> getOfferSides() {
/*  306 */     return Arrays.asList(this.offerSides);
/*      */   }
/*      */ 
/*      */   private List<Period> getPeriods(Instrument instrument) {
/*  310 */     List result = null;
/*      */ 
/*  312 */     synchronized (this.currentPeriods) {
/*  313 */       result = new ArrayList(this.currentPeriods);
/*      */     }
/*      */ 
/*  316 */     Map periodsMap = (Map)this.bufferMap.get(instrument);
/*  317 */     if (periodsMap != null) {
/*  318 */       Set periodsSet = periodsMap.keySet();
/*  319 */       if (periodsSet != null) {
/*  320 */         for (Period period : periodsSet) {
/*  321 */           if (!result.contains(period)) {
/*  322 */             result.add(period);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  327 */     return result;
/*      */   }
/*      */ 
/*      */   private void updateBuffer(ITick tick, Instrument instrument, Period period, OfferSide offerSide, Data[] buff)
/*      */   {
/*  337 */     int lastIndex = getLastIndex(instrument, period, offerSide);
/*  338 */     if (Period.TICK.equals(period)) {
/*  339 */       int index = addFirstData(buff, lastIndex, period, 0L, (TickData)tick);
/*  340 */       setLastIndex(instrument, period, offerSide, index);
/*      */     } else {
/*  342 */       CandleData inProgressBar = getCurrentGeneratedBar(instrument, period, offerSide);
/*  343 */       CandleData firstData = lastIndex == -1 ? null : (CandleData)buff[lastIndex];
/*  344 */       if (firstData == null)
/*      */       {
/*  346 */         int index = addFirstData(buff, lastIndex, period, -9223372036854775808L, inProgressBar.clone());
/*  347 */         setLastIndex(instrument, period, offerSide, index);
/*  348 */       } else if (firstData.time != inProgressBar.time)
/*      */       {
/*  350 */         int index = addFirstData(buff, lastIndex, period, firstData.time, inProgressBar.clone());
/*  351 */         setLastIndex(instrument, period, offerSide, index);
/*      */       }
/*  354 */       else if (buff[lastIndex].time == firstData.time)
/*      */       {
/*  356 */         firstData.open = inProgressBar.open;
/*  357 */         firstData.close = inProgressBar.close;
/*  358 */         firstData.low = inProgressBar.low;
/*  359 */         firstData.high = inProgressBar.high;
/*  360 */         firstData.vol = inProgressBar.vol;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateInProgressCandles(Instrument instrument, ITick tick)
/*      */   {
/*  367 */     long time = tick.getTime();
/*  368 */     double ask = tick.getAsk();
/*  369 */     double bid = tick.getBid();
/*  370 */     double askVolume = tick.getAskVolume();
/*  371 */     double bidVolume = tick.getBidVolume();
/*  372 */     List periods = getPeriods(instrument);
/*  373 */     for (Period period : periods) {
/*  374 */       if (period == Period.TICK)
/*      */       {
/*      */         continue;
/*      */       }
/*  378 */       TesterHistoryGeneratedBar askBar = getGeneratedCandle(instrument, period, OfferSide.ASK);
/*  379 */       TesterHistoryGeneratedBar bidBar = getGeneratedCandle(instrument, period, OfferSide.BID);
/*      */ 
/*  381 */       if (askBar == null) {
/*  382 */         long candleStartFast = DataCacheUtils.getCandleStartFast(period, time);
/*  383 */         long candleEnd = DataCacheUtils.getNextCandleStartFast(period, candleStartFast);
/*      */ 
/*  385 */         askBar = new TesterHistoryGeneratedBar(candleStartFast, candleEnd, ask, ask, ask, ask, askVolume);
/*  386 */         bidBar = new TesterHistoryGeneratedBar(candleStartFast, candleEnd, bid, bid, bid, bid, bidVolume);
/*      */ 
/*  388 */         setGeneratedCandle(instrument, period, OfferSide.ASK, askBar);
/*  389 */         setGeneratedCandle(instrument, period, OfferSide.BID, bidBar);
/*      */       }
/*  391 */       else if (askBar.endTime <= time) {
/*  392 */         long candleStartFast = DataCacheUtils.getCandleStartFast(period, time);
/*  393 */         long candleEnd = DataCacheUtils.getNextCandleStartFast(period, candleStartFast);
/*  394 */         askBar.time = candleStartFast;
/*  395 */         askBar.endTime = candleEnd;
/*  396 */         askBar.open = (askBar.close = askBar.high = askBar.low = ask);
/*  397 */         askBar.vol = askVolume;
/*      */ 
/*  399 */         bidBar.time = candleStartFast;
/*  400 */         bidBar.endTime = candleEnd;
/*  401 */         bidBar.open = (bidBar.close = bidBar.high = bidBar.low = bid);
/*  402 */         bidBar.vol = bidVolume;
/*      */       } else {
/*  404 */         if (askBar.flat) {
/*  405 */           askBar.open = ask;
/*  406 */           askBar.high = ask;
/*  407 */           askBar.low = ask;
/*  408 */           askBar.flat = false;
/*      */         }
/*  410 */         askBar.high = (askBar.high < ask ? ask : askBar.high);
/*  411 */         askBar.low = (askBar.low > ask ? ask : askBar.low);
/*      */ 
/*  413 */         askBar.vol = StratUtils.roundHalfEven(askBar.vol + askVolume, 2);
/*      */ 
/*  416 */         askBar.close = ask;
/*      */ 
/*  418 */         if (bidBar.flat) {
/*  419 */           bidBar.open = bid;
/*  420 */           bidBar.high = bid;
/*  421 */           bidBar.low = bid;
/*  422 */           bidBar.flat = false;
/*      */         }
/*  424 */         bidBar.high = (bidBar.high < bid ? bid : bidBar.high);
/*  425 */         bidBar.low = (bidBar.low > bid ? bid : bidBar.low);
/*      */ 
/*  427 */         bidBar.vol = StratUtils.roundHalfEven(bidBar.vol + bidVolume, 2);
/*      */ 
/*  430 */         bidBar.close = bid;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected int addFirstData(Data[] buff, int lastIndex, Period period, long oldTime, Data firstData)
/*      */   {
/*  443 */     if (oldTime == -9223372036854775808L)
/*      */     {
/*  445 */       lastIndex = 0;
/*  446 */       buff[lastIndex] = firstData;
/*  447 */       checkConsistency(period, buff, lastIndex);
/*      */     } else {
/*  449 */       if (lastIndex + 1 >= buff.length) {
/*  450 */         System.arraycopy(buff, 20, buff, 0, lastIndex + 1 - 20);
/*  451 */         lastIndex -= 20;
/*      */       }
/*  453 */       lastIndex++;
/*  454 */       buff[lastIndex] = firstData;
/*  455 */       checkConsistency(period, buff, lastIndex);
/*      */     }
/*  457 */     return lastIndex;
/*      */   }
/*      */ 
/*      */   protected boolean assertionsEnabled()
/*      */   {
/*  462 */     boolean b = false;
/*  463 */     assert ((b = 1) != 0);
/*  464 */     return b;
/*      */   }
/*      */ 
/*      */   private void checkConsistency(Period period, Data[] buffer, int lastIndex)
/*      */   {
/*  469 */     if (assertionsEnabled()) {
/*  470 */       if (lastIndex == -1)
/*      */       {
/*  472 */         return;
/*      */       }
/*      */ 
/*  475 */       if (lastIndex >= buffer.length) {
/*  476 */         throw new RuntimeException("TesterIndicators consistency check failed!!!");
/*      */       }
/*      */ 
/*  479 */       if (Period.TICK.equals(period))
/*      */       {
/*  481 */         return;
/*      */       }
/*      */ 
/*  484 */       long firstCandleStart = buffer[0].time;
/*  485 */       checkConsistency(period, buffer, lastIndex, firstCandleStart);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkConsistency(Period period, Data[] buffer, int lastIndex, long candleStart) {
/*  490 */     for (int i = 0; i <= lastIndex; i++) {
/*  491 */       Data dataElement = buffer[i];
/*  492 */       if (dataElement == null) {
/*  493 */         throw new RuntimeException("TesterIndicators consistency check failed!!!");
/*      */       }
/*  495 */       if (dataElement.time != candleStart) {
/*  496 */         throw new RuntimeException("TesterIndicators consistency check failed!!!");
/*      */       }
/*  498 */       candleStart = DataCacheUtils.getNextCandleStartFast(period, candleStart);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected final int findStart(long from, int fi, int ei, Data[] buffer) {
/*  503 */     int low = fi;
/*  504 */     int high = ei;
/*      */ 
/*  506 */     while (low <= high) {
/*  507 */       int mid = low + high >>> 1;
/*  508 */       long midVal = buffer[mid].time;
/*      */ 
/*  510 */       if (midVal < from)
/*  511 */         low = mid + 1;
/*  512 */       else if (midVal > from)
/*  513 */         high = mid - 1;
/*      */       else {
/*  515 */         return mid;
/*      */       }
/*      */     }
/*      */ 
/*  519 */     return low;
/*      */   }
/*      */ 
/*      */   public IBar getCurrentBar(Instrument instrument, Period period, OfferSide side)
/*      */   {
/*  524 */     CandleData currentBar = getCurrentGeneratedBar(instrument, period, side);
/*  525 */     return currentBar == null ? null : currentBar.clone();
/*      */   }
/*      */ 
/*      */   public CandleData getCurrentGeneratedBar(Instrument instrument, Period period, OfferSide side) {
/*  529 */     TesterHistoryGeneratedBar currentBar = getGeneratedCandle(instrument, period, side);
/*  530 */     return currentBar == null ? null : currentBar.clone();
/*      */   }
/*      */ 
/*      */   public void fillCurrentCandles(Set<Instrument> instruments, IFeedDataProvider feedDataProvider) throws NoDataForPeriodException, DataCacheException
/*      */   {
/*  535 */     for (Iterator i$ = instruments.iterator(); i$.hasNext(); ) { instrument = (Instrument)i$.next();
/*  536 */       tick = this.lastTicks[instrument.ordinal()];
/*  537 */       if (tick.getTime() == -9223372036854775808L) {
/*      */         continue;
/*      */       }
/*  540 */       for (Period period : getPeriods(instrument)) {
/*  541 */         if (period == Period.TICK) {
/*      */           continue;
/*      */         }
/*  544 */         TesterHistoryGeneratedBar askBar = getGeneratedCandle(instrument, period, OfferSide.ASK);
/*  545 */         TesterHistoryGeneratedBar bidBar = getGeneratedCandle(instrument, period, OfferSide.BID);
/*      */ 
/*  547 */         long candleStartFast = DataCacheUtils.getCandleStartFast(period, tick.getTime());
/*  548 */         long candleEnd = DataCacheUtils.getNextCandleStartFast(period, candleStartFast);
/*  549 */         if (askBar == null) {
/*  550 */           askBar = new TesterHistoryGeneratedBar(candleStartFast, candleEnd, tick.getAsk(), tick.getAsk(), tick.getAsk(), tick.getAsk(), 0.0D);
/*  551 */           bidBar = new TesterHistoryGeneratedBar(candleStartFast, candleEnd, tick.getBid(), tick.getBid(), tick.getBid(), tick.getBid(), 0.0D);
/*      */ 
/*  553 */           setGeneratedCandle(instrument, period, OfferSide.ASK, askBar);
/*  554 */           setGeneratedCandle(instrument, period, OfferSide.BID, bidBar);
/*      */         }
/*      */ 
/*  557 */         DataCacheUtils.ToLoad[] intervalsToLoad = DataCacheUtils.getIntervalsToLoadForCandleFilling(period, tick.getTime());
/*  558 */         List candles = new ArrayList();
/*  559 */         List ticks = new ArrayList();
/*  560 */         int[] result = { 0 };
/*  561 */         Exception[] exceptions = new Exception[1];
/*  562 */         com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */           public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */           }
/*      */ 
/*      */           public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  567 */             this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  568 */             this.val$exceptions[0] = e;
/*      */           }
/*      */ 
/*      */           public boolean stopJob() {
/*  572 */             return false;
/*      */           }
/*      */         };
/*  575 */         LiveFeedListener feedListener = new LiveFeedListener(ticks, candles) {
/*      */           public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*  577 */             this.val$ticks.add(new TickData(time, ask, bid, askVol, bidVol));
/*      */           }
/*      */ 
/*      */           public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */           {
/*  582 */             this.val$candles.add(new CandleData(time, open, close, low, high, vol));
/*      */           }
/*      */         };
/*  585 */         boolean firstCandle = true;
/*  586 */         for (DataCacheUtils.ToLoad toLoad : intervalsToLoad) {
/*  587 */           if (toLoad.period != Period.TICK) {
/*  588 */             candles.clear();
/*  589 */             feedDataProvider.loadCandlesDataSynched(instrument, toLoad.period, OfferSide.ASK, toLoad.from, toLoad.to, feedListener, loadingProgressListener);
/*      */ 
/*  592 */             if (result[0] == 2) {
/*  593 */               if (exceptions[0] != null) {
/*  594 */                 LOGGER.error(exceptions[0].getMessage(), exceptions[0]);
/*      */               }
/*  596 */               return;
/*      */             }
/*  598 */             if (candles.isEmpty()) {
/*      */               continue;
/*      */             }
/*  601 */             addCandlesToCandle(askBar, candles, firstCandle);
/*  602 */             result[0] = 0;
/*  603 */             candles.clear();
/*  604 */             feedDataProvider.loadCandlesDataSynched(instrument, toLoad.period, OfferSide.BID, toLoad.from, toLoad.to, feedListener, loadingProgressListener);
/*      */ 
/*  607 */             if (result[0] == 2) {
/*  608 */               if (exceptions[0] != null) {
/*  609 */                 LOGGER.error(exceptions[0].getMessage(), exceptions[0]);
/*      */               }
/*  611 */               return;
/*      */             }
/*  613 */             addCandlesToCandle(bidBar, candles, firstCandle);
/*      */           } else {
/*  615 */             ticks.clear();
/*  616 */             feedDataProvider.loadTicksDataSynched(instrument, toLoad.from, toLoad.to, feedListener, loadingProgressListener);
/*  617 */             if (result[0] == 2) {
/*  618 */               if (exceptions[0] != null) {
/*  619 */                 LOGGER.error(exceptions[0].getMessage(), exceptions[0]);
/*      */               }
/*  621 */               return;
/*      */             }
/*  623 */             if (ticks.isEmpty()) {
/*      */               continue;
/*      */             }
/*  626 */             addTicksToCandle(askBar, ticks, firstCandle, true);
/*  627 */             addTicksToCandle(bidBar, ticks, firstCandle, false);
/*      */           }
/*  629 */           firstCandle = false;
/*      */         }
/*      */       } } Instrument instrument;
/*      */     ITick tick;
/*      */   }
/*      */ 
/*      */   private static final void addCandlesToCandle(CandleData candleData, List<CandleData> candles, boolean firstCandle) {
/*  636 */     for (CandleData candle : candles) {
/*  637 */       if (firstCandle)
/*      */       {
/*  639 */         candleData.open = candle.open;
/*  640 */         candleData.high = candle.high;
/*  641 */         candleData.low = candle.low;
/*      */ 
/*  643 */         firstCandle = false;
/*      */       }
/*      */ 
/*  646 */       candleData.high = (candleData.high < candle.high ? candle.high : candleData.high);
/*  647 */       candleData.low = (candleData.low > candle.low ? candle.low : candleData.low);
/*      */ 
/*  649 */       candleData.vol += candle.vol;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final void addTicksToCandle(CandleData candleData, List<TickData> ticks, boolean firstCandle, boolean ask) {
/*  654 */     for (TickData tick : ticks) {
/*  655 */       double price = ask ? tick.ask : tick.bid;
/*  656 */       if (firstCandle)
/*      */       {
/*  658 */         candleData.open = price;
/*  659 */         candleData.high = price;
/*  660 */         candleData.low = price;
/*      */ 
/*  662 */         firstCandle = false;
/*      */       }
/*      */ 
/*  665 */       candleData.high = (candleData.high < price ? price : candleData.high);
/*  666 */       candleData.low = (candleData.low > price ? price : candleData.low);
/*      */ 
/*  668 */       candleData.vol += (ask ? tick.askVol : tick.bidVol);
/*      */     }
/*  670 */     candleData.vol = StratUtils.roundHalfEven(candleData.vol, 2);
/*      */   }
/*      */ 
/*      */   public void setFirstTicks(Map<Instrument, ITick> firstTicks) {
/*  674 */     for (Map.Entry entry : firstTicks.entrySet()) {
/*  675 */       Instrument instrument = (Instrument)entry.getKey();
/*  676 */       ITick tick = (ITick)entry.getValue();
/*      */ 
/*  678 */       TickData lastTick = this.lastTicks[instrument.ordinal()];
/*  679 */       lastTick.time = (tick.getTime() - 1L);
/*  680 */       lastTick.ask = tick.getAsk();
/*  681 */       lastTick.bid = tick.getBid();
/*  682 */       lastTick.askVol = tick.getAskVolume();
/*  683 */       lastTick.bidVol = tick.getBidVolume();
/*      */ 
/*  685 */       this.currentCandleTime[instrument.ordinal()] = lastTick.time;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ITick getLastTickBeforeSecured(Instrument instrument, long to) throws JFException
/*      */   {
/*  691 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/*  693 */       ITick localITick = super.getLastTickBeforeSecured(instrument, to);
/*      */       return localITick; } finally { this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); } throw localObject;
/*      */   }
/*      */ 
/*      */   public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, long from, long to)
/*      */     throws JFException
/*      */   {
/*  701 */     List result = getBars(instrument, period, side, Filter.NO_FILTER, from, to);
/*  702 */     return result;
/*      */   }
/*      */ 
/*      */   public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to)
/*      */     throws JFException
/*      */   {
/*  715 */     validateIntervalByPeriod(period, from, to);
/*      */ 
/*  717 */     if (period == Period.TICK) {
/*  718 */       throw new JFException("Incorrect period [" + period + "] for getBars function");
/*      */     }
/*  720 */     if (isInstrumentSubscribed(instrument))
/*      */     {
/*  722 */       synchronized (this) {
/*  723 */         timeOfLastCandle = this.currentCandleTime[instrument.ordinal()];
/*      */       }
/*      */ 
/*  726 */       long timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/*  727 */       if (to > timeOfLastCandle) {
/*  728 */         throw new JFException("\"to\" parameter can't be greater than the time of the last formed bar for this instrument");
/*      */       }
/*      */     }
/*  731 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/*  733 */       synchronized (this) {
/*  734 */         boolean dataExists = true;
/*  735 */         int lastIndex = getLastIndex(instrument, period, side);
/*  736 */         if (lastIndex != -1)
/*      */         {
/*  738 */           Data[] buff = getBuffer(instrument, period, side);
/*  739 */           long buffFrom = buff[0].time;
/*  740 */           long buffTo = buff[lastIndex].time;
/*  741 */           if ((from < buffFrom) || (to > buffTo))
/*      */           {
/*  743 */             dataExists = false;
/*      */           }
/*      */         }
/*      */         else {
/*  747 */           dataExists = false;
/*  748 */           long timeOfLastCandle = getStartTimeOfCurrentBar(instrument, period);
/*  749 */           long buffExpectedStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, timeOfLastCandle, 499);
/*  750 */           if ((from >= buffExpectedStartTime) && (to <= timeOfLastCandle))
/*      */           {
/*  752 */             fillCacheBuffer(instrument, period, side);
/*  753 */             lastIndex = getLastIndex(instrument, period, side);
/*  754 */             if (lastIndex != -1) {
/*  755 */               Data[] buff = getBuffer(instrument, period, side);
/*  756 */               long buffFrom = buff[0].time;
/*  757 */               long buffTo = buff[lastIndex].time;
/*  758 */               if ((from >= buffFrom) && (to <= buffTo))
/*      */               {
/*  760 */                 dataExists = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  766 */         if (dataExists) {
/*  767 */           List bars = new ArrayList();
/*  768 */           lastIndex = getLastIndex(instrument, period, side);
/*  769 */           Data[] buff = getBuffer(instrument, period, side);
/*  770 */           long buffFrom = buff[0].time;
/*  771 */           long buffTo = buff[lastIndex].time;
/*      */ 
/*  773 */           int k = DataCacheUtils.getCandlesCountBetweenFast(period, buffFrom, from) - 1;
/*  774 */           for (int l = lastIndex - DataCacheUtils.getCandlesCountBetweenFast(period, to, buffTo) + 1; k <= l; k++) {
/*  775 */             CandleData candle = (CandleData)buff[k];
/*  776 */             boolean matchedFilter = this.filterManager.matchedFilter(period, filter, candle);
/*  777 */             if (matchedFilter) {
/*  778 */               bars.add(candle);
/*      */             }
/*      */           }
/*  781 */           k = bars;
/*      */ 
/*  801 */           this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); return k;
/*      */         }
/*      */       }
/*  785 */       ??? = (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, filter, from, to) {
/*      */         public List<IBar> run() throws Exception {
/*  787 */           return TesterHistory.this.getBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$filter, this.val$from, this.val$to);
/*      */         }
/*      */       });
/*      */       return ???;
/*      */     }
/*      */     catch (PrivilegedActionException e)
/*      */     {
/*  791 */       Exception ex = e.getException();
/*  792 */       if ((ex instanceof JFException))
/*  793 */         throw ((JFException)ex);
/*  794 */       if ((ex instanceof RuntimeException)) {
/*  795 */         throw ((RuntimeException)ex);
/*      */       }
/*  797 */       LOGGER.error(ex.getMessage(), ex);
/*  798 */       throw new JFException(ex);
/*      */     }
/*      */     finally {
/*  801 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); } throw localObject3;
/*      */   }
/*      */ 
/*      */   public List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*      */     throws JFException
/*      */   {
/*  807 */     if (!isIntervalValid(period, numberOfCandlesBefore, time, numberOfCandlesAfter)) {
/*  808 */       throw new JFException("Number of bars to load = 0 or time is not correct time for the period specified");
/*      */     }
/*  810 */     if (period == Period.TICK) {
/*  811 */       throw new JFException("Incorrect period [" + period + "] for getBars function");
/*      */     }
/*  813 */     long timeOfLastCandle = -9223372036854775808L;
/*  814 */     if (isInstrumentSubscribed(instrument)) {
/*  815 */       synchronized (this) {
/*  816 */         timeOfLastCandle = this.currentCandleTime[instrument.ordinal()];
/*      */       }
/*  818 */       timeOfLastCandle = DataCacheUtils.getCandleStartFast(period, timeOfLastCandle);
/*  819 */       if (time > timeOfLastCandle) {
/*  820 */         throw new JFException("\"to\" parameter can't be greater than time of the last formed bar for this instrument");
/*      */       }
/*      */     }
/*  823 */     if ((filter == Filter.NO_FILTER) || (period.getInterval() > Period.DAILY.getInterval()))
/*      */     {
/*  825 */       long from = DataCacheUtils.getTimeForNCandlesBackFast(period, time, numberOfCandlesBefore == 0 ? 1 : numberOfCandlesBefore);
/*  826 */       long to = DataCacheUtils.getTimeForNCandlesForwardFast(period, time, numberOfCandlesAfter == 0 ? 1 : numberOfCandlesAfter + 1);
/*  827 */       if ((timeOfLastCandle > -9223372036854775808L) && (to > timeOfLastCandle)) {
/*  828 */         to = timeOfLastCandle;
/*      */       }
/*  830 */       return getBars(instrument, period, side, from, to);
/*      */     }
/*  832 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */     try {
/*  834 */       synchronized (this) {
/*  835 */         int lastIndex = getLastIndex(instrument, period, side);
/*  836 */         if (lastIndex != -1)
/*      */         {
/*  838 */           Data[] buff = getBuffer(instrument, period, side);
/*  839 */           long buffFrom = buff[0].time;
/*  840 */           long buffTo = buff[lastIndex].time;
/*      */ 
/*  842 */           if ((time >= buffFrom) && (time <= buffTo))
/*      */           {
/*  844 */             int timeIndex = findStart(time, 0, lastIndex, buff);
/*  845 */             if (timeIndex - (numberOfCandlesBefore > 0 ? numberOfCandlesBefore - 1 : 0) >= 0) if ((timeIndex + (numberOfCandlesBefore > 0 ? numberOfCandlesAfter : numberOfCandlesAfter - 1) <= lastIndex) && (buff[timeIndex].time == time))
/*      */               {
/*  871 */                 LinkedList bars = new LinkedList();
/*      */                 int i;
/*  872 */                 if (filter == Filter.WEEKENDS)
/*      */                 {
/*  875 */                   int i = numberOfCandlesBefore; for (int index = timeIndex; (i > 0) && (index >= 0); index--)
/*      */                   {
/*  877 */                     CandleData candle = (CandleData)buff[index];
/*      */ 
/*  883 */                     boolean isWeekendCandle = this.filterManager.isWeekendTime(candle.time, period);
/*      */ 
/*  885 */                     if (!isWeekendCandle) {
/*  886 */                       bars.addFirst(candle);
/*  887 */                       i--;
/*      */                     }
/*      */                   }
/*  890 */                   if (i > 0)
/*      */                   {
/*  892 */                     bars = null;
/*      */                   } else {
/*  894 */                     i = numberOfCandlesAfter; for (index = timeIndex + (numberOfCandlesBefore > 0 ? 1 : 0); (i > 0) && (index <= lastIndex); index++) {
/*  895 */                       CandleData candle = (CandleData)buff[index];
/*      */ 
/*  901 */                       boolean isWeekendCandle = this.filterManager.isWeekendTime(candle.time, period);
/*  902 */                       if (!isWeekendCandle) {
/*  903 */                         bars.addLast(candle);
/*  904 */                         i--;
/*      */                       }
/*      */                     }
/*  907 */                     if (i > 0)
/*      */                     {
/*  909 */                       bars = null;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*  915 */                   i = numberOfCandlesBefore; for (int index = timeIndex; (i > 0) && (index > 0); index--)
/*      */                   {
/*  917 */                     CandleData candle = (CandleData)buff[index];
/*      */ 
/*  923 */                     if (!candle.isFlat()) {
/*  924 */                       bars.addFirst(candle);
/*  925 */                       i--;
/*      */                     }
/*      */                   }
/*  928 */                   if (i > 0)
/*      */                   {
/*  930 */                     bars = null;
/*      */                   } else {
/*  932 */                     i = numberOfCandlesAfter; for (index = timeIndex + (numberOfCandlesBefore > 0 ? 1 : 0); (i > 0) && (index <= lastIndex); index++) {
/*  933 */                       CandleData candle = (CandleData)buff[index];
/*      */ 
/*  939 */                       if ((index != lastIndex) && (candle.isFlat()))
/*      */                       {
/*      */                         continue;
/*      */                       }
/*  943 */                       bars.addLast(candle);
/*  944 */                       i--;
/*      */                     }
/*      */ 
/*  947 */                     if (i > 0)
/*      */                     {
/*  949 */                       bars = null;
/*      */                     }
/*      */                   }
/*      */                 }
/*  953 */                 if (bars != null) {
/*  954 */                   i = bars;
/*      */ 
/*  991 */                   this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); return i;
/*      */                 }
/*      */               }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  961 */           long buffExpectedStartTime = DataCacheUtils.getTimeForNCandlesBackFast(period, timeOfLastCandle, 499);
/*  962 */           long from = DataCacheUtils.getTimeForNCandlesBackFast(period, time, numberOfCandlesBefore == 0 ? 1 : numberOfCandlesBefore);
/*  963 */           long to = DataCacheUtils.getTimeForNCandlesForwardFast(period, time, numberOfCandlesAfter == 0 ? 1 : numberOfCandlesAfter + 1);
/*  964 */           if ((timeOfLastCandle > -9223372036854775808L) && (to > timeOfLastCandle)) {
/*  965 */             to = timeOfLastCandle;
/*      */           }
/*  967 */           if ((from >= buffExpectedStartTime) && (to <= timeOfLastCandle))
/*      */           {
/*  969 */             fillCacheBuffer(instrument, period, side);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  975 */       ??? = (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, period, side, filter, numberOfCandlesBefore, time, numberOfCandlesAfter) {
/*      */         public List<IBar> run() throws Exception {
/*  977 */           return TesterHistory.this.getBarsSecured(this.val$instrument, this.val$period, this.val$side, this.val$filter, this.val$numberOfCandlesBefore, this.val$time, this.val$numberOfCandlesAfter);
/*      */         }
/*      */       });
/*      */       return ???;
/*      */     }
/*      */     catch (PrivilegedActionException e)
/*      */     {
/*  981 */       Exception ex = e.getException();
/*  982 */       if ((ex instanceof JFException))
/*  983 */         throw ((JFException)ex);
/*  984 */       if ((ex instanceof RuntimeException)) {
/*  985 */         throw ((RuntimeException)ex);
/*      */       }
/*  987 */       LOGGER.error(ex.getMessage(), ex);
/*  988 */       throw new JFException(ex);
/*      */     }
/*      */     finally {
/*  991 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); } throw localObject3;
/*      */   }
/*      */ 
/*      */   public List<ITick> getTicks(Instrument instrument, long from, long to)
/*      */     throws JFException
/*      */   {
/*  997 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*  998 */     validateIntervalByPeriod(Period.TICK, from, to);
/*      */ 
/* 1000 */     if (isInstrumentSubscribed(instrument))
/*      */     {
/*      */       long timeOfCurrentCandle;
/* 1002 */       synchronized (this) {
/* 1003 */         timeOfCurrentCandle = this.currentCandleTime[instrument.ordinal()];
/*      */       }
/* 1005 */       if (to > timeOfCurrentCandle)
/* 1006 */         throw new JFException("\"to\" parameter can't be greater than time of the last tick for this instrument");
/*      */     }
/*      */     try
/*      */     {
/* 1010 */       synchronized (this) {
/* 1011 */         boolean dataExists = true;
/* 1012 */         int lastIndex = getLastIndex(instrument, this.TICKS_PERIOD, this.TICKS_OFFER_SIDE);
/* 1013 */         if (lastIndex != -1)
/*      */         {
/* 1015 */           Data[] buff = getBuffer(instrument, this.TICKS_PERIOD, this.TICKS_OFFER_SIDE);
/* 1016 */           long buffFrom = buff[0].time;
/* 1017 */           long buffTo = buff[lastIndex].time;
/* 1018 */           if ((from < buffFrom) || (to > buffTo))
/*      */           {
/* 1020 */             dataExists = false;
/*      */           }
/*      */         }
/*      */         else {
/* 1024 */           dataExists = false;
/* 1025 */           fillCacheBuffer(instrument, Period.TICK, this.TICKS_OFFER_SIDE);
/*      */         }
/*      */ 
/* 1028 */         if (dataExists) {
/* 1029 */           Object ticks = new ArrayList();
/* 1030 */           lastIndex = getLastIndex(instrument, this.TICKS_PERIOD, this.TICKS_OFFER_SIDE);
/* 1031 */           Data[] buff = getBuffer(instrument, this.TICKS_PERIOD, this.TICKS_OFFER_SIDE);
/* 1032 */           int k = 0; for (int l = lastIndex; k <= l; k++) {
/* 1033 */             if ((buff[k].time >= from) && (buff[k].time <= to))
/* 1034 */               ((List)ticks).add((TickData)buff[k]);
/* 1035 */             else if (buff[k].time > to) {
/*      */                 break;
/*      */               }
/*      */           }
/* 1039 */           k = (I)ticks;
/*      */ 
/* 1059 */           this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); return k;
/*      */         }
/*      */       }
/* 1043 */       ??? = (List)AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, from, to) {
/*      */         public List<ITick> run() throws Exception {
/* 1045 */           return TesterHistory.this.getTicksSecured(this.val$instrument, this.val$from, this.val$to);
/*      */         }
/*      */       });
/*      */       return ???;
/*      */     }
/*      */     catch (PrivilegedActionException e)
/*      */     {
/* 1049 */       Exception ex = e.getException();
/* 1050 */       if ((ex instanceof JFException))
/* 1051 */         throw ((JFException)ex);
/* 1052 */       if ((ex instanceof RuntimeException)) {
/* 1053 */         throw ((RuntimeException)ex);
/*      */       }
/* 1055 */       LOGGER.error(ex.getMessage(), ex);
/* 1056 */       throw new JFException(ex);
/*      */     }
/*      */     finally {
/* 1059 */       this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS); } throw localObject3;
/*      */   }
/*      */ 
/*      */   protected List<IOrder> getOrdersHistorySecured(Instrument instrument, long from, long to)
/*      */     throws JFException
/*      */   {
/* 1068 */     throw new JFException("Error in history request");
/*      */   }
/*      */ 
/*      */   public void readOrdersHistory(Instrument instrument, long from, long to, LoadingOrdersListener ordersListener, com.dukascopy.api.LoadingProgressListener loadingProgress) throws JFException
/*      */   {
/* 1073 */     if (from > to) {
/* 1074 */       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1075 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1076 */       throw new JFException("Interval from [" + dateFormat.format(new Date(from)) + "] to [" + dateFormat.format(new Date(to)) + "] GMT is not valid");
/*      */     }
/* 1078 */     if (this.ordersHistoryRequestSent.compareAndSet(false, true))
/*      */       try {
/* 1080 */         long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */         try {
/* 1082 */           FeedDataProvider.getDefaultInstance().runTask(new LoadOrdersAction(this.ordersProvider, instrument, from, to, ordersListener, loadingProgress));
/*      */         } finally {
/* 1084 */           this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS);
/*      */         }
/*      */       } finally {
/* 1087 */         this.ordersHistoryRequestSent.set(false);
/*      */       }
/*      */     else
/* 1090 */       throw new JFException("Only one request for orders history can be sent at one time");
/*      */   }
/*      */ 
/*      */   public List<IOrder> getOrdersHistory(Instrument instrument, long from, long to)
/*      */     throws JFException
/*      */   {
/* 1096 */     if (from > to) {
/* 1097 */       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1098 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1099 */       throw new JFException("Interval from [" + dateFormat.format(new Date(from)) + "] to [" + dateFormat.format(new Date(to)) + "] GMT is not valid");
/*      */     }
/* 1101 */     if (this.ordersHistoryRequestSent.compareAndSet(false, true)) {
/*      */       try
/*      */       {
/* 1104 */         long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*      */         try {
/* 1106 */           List orders = new ArrayList();
/* 1107 */           int[] result = { 0 };
/* 1108 */           Exception[] exceptions = new Exception[1];
/* 1109 */           com.dukascopy.charts.data.datacache.LoadingProgressListener loadingProgressListener = new com.dukascopy.charts.data.datacache.LoadingProgressListener(result, exceptions) {
/*      */             public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */             }
/*      */ 
/*      */             public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 1114 */               this.val$result[0] = (allDataLoaded ? 1 : 2);
/* 1115 */               this.val$exceptions[0] = e;
/*      */             }
/*      */ 
/*      */             public boolean stopJob() {
/* 1119 */               return false;
/*      */             }
/*      */           };
/* 1122 */           this.ordersProvider.getOrdersForInstrument(instrument, from, to, new OrdersListener(from, to, orders)
/*      */           {
/*      */             public void newOrder(Instrument instrument, OrderHistoricalData orderData) {
/* 1125 */               if (!orderData.isClosed()) {
/* 1126 */                 return;
/*      */               }
/* 1128 */               HistoryOrder order = TesterHistory.this.processOrders(instrument, orderData, this.val$from, this.val$to);
/* 1129 */               if (order != null)
/* 1130 */                 this.val$orders.add(order);
/*      */             }
/*      */ 
/*      */             public void orderChange(Instrument instrument, OrderHistoricalData orderData)
/*      */             {
/*      */             }
/*      */ 
/*      */             public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*      */             {
/*      */             }
/*      */ 
/*      */             public void ordersInvalidated(Instrument instrument)
/*      */             {
/*      */             }
/*      */           }
/*      */           , loadingProgressListener);
/*      */ 
/* 1147 */           if (result[0] == 2) {
/* 1148 */             throw new JFException("Error while loading bars", exceptions[0]);
/*      */           }
/* 1150 */           List localList1 = orders;
/*      */ 
/* 1153 */           this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS);
/*      */ 
/* 1156 */           this.ordersHistoryRequestSent.set(false); return localList1;
/*      */         }
/*      */         finally
/*      */         {
/* 1153 */           this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.HISTORY_CALLS);
/*      */         }
/*      */       } finally {
/* 1156 */         this.ordersHistoryRequestSent.set(false);
/*      */       }
/*      */     }
/* 1159 */     throw new JFException("Only one request for orders history can be sent at one time");
/*      */   }
/*      */ 
/*      */   protected HistoryOrder createHistoryOrder(Instrument instrument, OrderHistoricalData orderData, long closeTime, double closePrice)
/*      */   {
/* 1165 */     OrderHistoricalData.OpenData entryOrder = orderData.getEntryOrder();
/* 1166 */     return new TesterHistoryOrder(instrument, entryOrder.getLabel(), orderData.getOrderGroupId(), entryOrder.getFillTime(), closeTime, entryOrder.getSide(), entryOrder.getAmount().divide(ONE_MILLION).doubleValue(), entryOrder.getOpenPrice().doubleValue(), closePrice, entryOrder.getComment(), this.accountCurrency, this.currencyConverter, orderData.getCommission().doubleValue());
/*      */   }
/*      */ 
/*      */   private void setLastIndex(Instrument instrument, Period period, OfferSide offerSide, int index)
/*      */   {
/* 1230 */     Map periodMap = (Map)this.lastIndexesMap.get(instrument);
/* 1231 */     if (periodMap == null) {
/* 1232 */       periodMap = new HashMap();
/* 1233 */       this.lastIndexesMap.put(instrument, periodMap);
/*      */     }
/* 1235 */     Map offerSideMap = (Map)periodMap.get(period);
/* 1236 */     if (offerSideMap == null) {
/* 1237 */       offerSideMap = new HashMap();
/* 1238 */       periodMap.put(period, offerSideMap);
/*      */     }
/* 1240 */     offerSideMap.put(offerSide, new Integer(index));
/*      */   }
/*      */ 
/*      */   private int getLastIndex(Instrument instrument, Period period, OfferSide offerSide)
/*      */   {
/* 1248 */     Map periodMap = (Map)this.lastIndexesMap.get(instrument);
/* 1249 */     if (periodMap == null) {
/* 1250 */       return -1;
/*      */     }
/* 1252 */     Map offerSideMap = (Map)periodMap.get(period);
/* 1253 */     if (offerSideMap == null) {
/* 1254 */       return -1;
/*      */     }
/* 1256 */     Integer index = (Integer)offerSideMap.get(offerSide);
/* 1257 */     if (index == null) {
/* 1258 */       return -1;
/*      */     }
/* 1260 */     return index.intValue();
/*      */   }
/*      */ 
/*      */   private void setBuffer(Instrument instrument, Period period, OfferSide offerSide, Data[] buffer)
/*      */   {
/* 1269 */     Map periodMap = (Map)this.bufferMap.get(instrument);
/* 1270 */     if (periodMap == null) {
/* 1271 */       periodMap = new HashMap();
/* 1272 */       this.bufferMap.put(instrument, periodMap);
/*      */     }
/* 1274 */     Map offerSideMap = (Map)periodMap.get(period);
/* 1275 */     if (offerSideMap == null) {
/* 1276 */       offerSideMap = new HashMap();
/* 1277 */       periodMap.put(period, offerSideMap);
/*      */     }
/* 1279 */     offerSideMap.put(offerSide, buffer);
/*      */   }
/*      */ 
/*      */   private Data[] getBuffer(Instrument instrument, Period period, OfferSide offerSide)
/*      */   {
/* 1287 */     Map periodMap = (Map)this.bufferMap.get(instrument);
/* 1288 */     if (periodMap == null) {
/* 1289 */       return null;
/*      */     }
/* 1291 */     Map offerSideMap = (Map)periodMap.get(period);
/* 1292 */     if (offerSideMap == null) {
/* 1293 */       return null;
/*      */     }
/* 1295 */     Data[] buffer = (Data[])offerSideMap.get(offerSide);
/* 1296 */     return buffer;
/*      */   }
/*      */ 
/*      */   private void setGeneratedCandle(Instrument instrument, Period period, OfferSide offerSide, TesterHistoryGeneratedBar generatedCandle)
/*      */   {
/* 1305 */     Map periodMap = (Map)this.generatedCandlesMap.get(instrument);
/* 1306 */     if (periodMap == null) {
/* 1307 */       periodMap = new HashMap();
/* 1308 */       this.generatedCandlesMap.put(instrument, periodMap);
/*      */     }
/* 1310 */     Map offerSideMap = (Map)periodMap.get(period);
/* 1311 */     if (offerSideMap == null) {
/* 1312 */       offerSideMap = new HashMap();
/* 1313 */       periodMap.put(period, offerSideMap);
/*      */     }
/* 1315 */     offerSideMap.put(offerSide, generatedCandle);
/*      */   }
/*      */ 
/*      */   private TesterHistoryGeneratedBar getGeneratedCandle(Instrument instrument, Period period, OfferSide offerSide)
/*      */   {
/* 1323 */     Map periodMap = (Map)this.generatedCandlesMap.get(instrument);
/* 1324 */     if (periodMap == null) {
/* 1325 */       return null;
/*      */     }
/* 1327 */     Map offerSideMap = (Map)periodMap.get(period);
/* 1328 */     if (offerSideMap == null) {
/* 1329 */       return null;
/*      */     }
/* 1331 */     TesterHistoryGeneratedBar generatedCandle = (TesterHistoryGeneratedBar)offerSideMap.get(offerSide);
/* 1332 */     return generatedCandle;
/*      */   }
/*      */ 
/*      */   public void addInProgressCandlePeriod(Period period) {
/* 1336 */     if (period == null) {
/* 1337 */       return;
/*      */     }
/*      */ 
/* 1340 */     synchronized (this.currentPeriods) {
/* 1341 */       if (!this.currentPeriods.contains(period))
/* 1342 */         this.currentPeriods.add(period);
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   54 */     LOGGER = LoggerFactory.getLogger(TesterHistory.class);
/*      */ 
/*   56 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*      */ 
/*   58 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*      */   }
/*      */ 
/*      */   private static class TesterHistoryGeneratedBar extends CandleData
/*      */   {
/*      */     public long endTime;
/*      */     public boolean flat;
/*      */ 
/*      */     public TesterHistoryGeneratedBar(long time, long endTime, double open, double close, double low, double high, double volume)
/*      */     {
/* 1219 */       super(open, close, low, high, volume);
/* 1220 */       this.endTime = endTime;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class LoadOrdersAction extends LoadProgressingAction
/*      */     implements Runnable
/*      */   {
/*      */     private final TesterOrdersProvider ordersProvider;
/*      */     private final Instrument instrument;
/*      */     private final long from;
/*      */     private final long to;
/*      */     private final LoadingOrdersListener ordersListener;
/*      */     private final com.dukascopy.api.LoadingProgressListener loadingProgress;
/*      */ 
/*      */     public LoadOrdersAction(TesterOrdersProvider ordersProvider, Instrument instrument, long from, long to, LoadingOrdersListener ordersListener, com.dukascopy.api.LoadingProgressListener loadingProgress)
/*      */     {
/* 1181 */       super();
/*      */ 
/* 1200 */       this.ordersProvider = ordersProvider;
/* 1201 */       this.instrument = instrument;
/* 1202 */       this.from = from;
/* 1203 */       this.to = to;
/* 1204 */       this.ordersListener = ordersListener;
/* 1205 */       this.loadingProgress = loadingProgress;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1210 */       this.ordersProvider.getOrdersForInstrument(this.instrument, this.from, this.to, new History.LoadingOrdersListenerWrapper(TesterHistory.this, this.ordersListener, this.from, this.to), new History.LoadingProgressListenerWrapper(TesterHistory.this, this.loadingProgress, true));
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterHistory
 * JD-Core Version:    0.6.0
 */