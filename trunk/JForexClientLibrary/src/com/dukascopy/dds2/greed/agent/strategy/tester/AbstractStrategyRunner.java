/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.feed.IPointAndFigure;
/*     */ import com.dukascopy.api.feed.IRangeBar;
/*     */ import com.dukascopy.api.feed.IRenkoBar;
/*     */ import com.dukascopy.api.feed.ITickBar;
/*     */ import com.dukascopy.api.impl.StrategyEventsListener;
/*     */ import com.dukascopy.api.impl.TimedData;
/*     */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*     */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IntraPeriodCandleData;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.IStrategyRateDataNotificationFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.StrategyRateDataNotificationFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.candle.IStrategyCandleNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.pnf.IStrategyPointAndFigureNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.pr.IStrategyPriceRangeNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.renko.IStrategyRenkoNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.IStrategyTickBarNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.CandleDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.DifferentPriceInPipsTickDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.DifferentPriceTickDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.PivotTickDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TickDataFromCandlesLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TickDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TicksWithTimeIntervalDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.listener.LastTickLiveFeedListener;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.listener.LastTickProgressListener;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataItem;
/*     */ import java.text.DateFormat;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractStrategyRunner
/*     */   implements Runnable, IStrategyRunner
/*     */ {
/*  72 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategyRunner.class);
/*     */   private static final int SPREAD_IN_PIPS = 2;
/*     */   private Period period;
/*     */   private OfferSide offerSide;
/*     */   private ITesterClient.InterpolationMethod interpolationMethod;
/*     */   protected ITesterClient.DataLoadingMethod dataLoadingMethod;
/*     */   protected long from;
/*     */   protected long to;
/*     */   protected IFeedDataProvider feedDataProvider;
/*     */   protected Set<Instrument> instruments;
/*     */   protected LoadingProgressListener progressListener;
/*     */   protected Set<Instrument> instrumentsForConversion;
/*     */   private Thread strategyThread;
/*  89 */   protected boolean canceled = false;
/*     */   protected final DateFormat format;
/*     */   protected final DecimalFormat decFormat;
/*     */   protected final DecimalFormatSymbols decimalFormatSymbols;
/*     */   protected long lastTickTime;
/*  94 */   protected Locale locale = Locale.ENGLISH;
/*  95 */   String[] shortWeekdays = null;
/*  96 */   protected Object sleepObject = new Object();
/*     */ 
/*  99 */   protected final Object dataLoadingThreadsLock = new Object();
/*     */   protected DataLoadingThreadsContainer<TimedData>[] dataLoadingThreads;
/*     */   protected StrategyEventsListener strategyEventsListener;
/*     */ 
/*     */   public AbstractStrategyRunner(Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to, IFeedDataProvider feedDataProvider, Set<Instrument> instruments, LoadingProgressListener progressListener, Set<Instrument> instrumentsForConversion)
/*     */   {
/* 120 */     this.period = period;
/* 121 */     this.offerSide = offerSide;
/* 122 */     this.interpolationMethod = interpolationMethod;
/*     */ 
/* 124 */     this.dataLoadingMethod = dataLoadingMethod;
/* 125 */     this.from = from;
/* 126 */     this.lastTickTime = from;
/* 127 */     this.to = to;
/*     */ 
/* 129 */     if (feedDataProvider == null)
/* 130 */       this.feedDataProvider = FeedDataProvider.getDefaultInstance();
/*     */     else {
/* 132 */       this.feedDataProvider = feedDataProvider;
/*     */     }
/*     */ 
/* 135 */     this.instruments = instruments;
/* 136 */     this.progressListener = progressListener;
/* 137 */     this.instrumentsForConversion = instrumentsForConversion;
/*     */ 
/* 139 */     if (this.instrumentsForConversion == null) {
/* 140 */       this.instrumentsForConversion = new HashSet();
/*     */     }
/*     */ 
/* 143 */     this.format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
/* 144 */     this.format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 145 */     this.decimalFormatSymbols = new DecimalFormatSymbols();
/* 146 */     this.decimalFormatSymbols.setDecimalSeparator('.');
/* 147 */     this.decimalFormatSymbols.setGroupingSeparator(',');
/* 148 */     this.decFormat = new DecimalFormat("###,###.00", this.decimalFormatSymbols);
/*     */ 
/* 150 */     DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(this.locale);
/* 151 */     this.shortWeekdays = dateFormatSymbols.getShortWeekdays();
/* 152 */     this.strategyEventsListener = null;
/*     */   }
/*     */ 
/*     */   protected abstract void setupTicksForInstrumentReportData(ITick paramITick1, ITick paramITick2, Instrument paramInstrument);
/*     */ 
/*     */   protected abstract void handleException(Throwable paramThrowable, String paramString);
/*     */ 
/*     */   public IFeedDataProvider getFeedDataProvider()
/*     */   {
/* 169 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public long getFrom()
/*     */   {
/* 174 */     return this.from;
/*     */   }
/*     */ 
/*     */   public ITesterClient.DataLoadingMethod getDataLoadingMethod()
/*     */   {
/* 179 */     return this.dataLoadingMethod;
/*     */   }
/*     */ 
/*     */   public long getTo()
/*     */   {
/* 184 */     return this.to;
/*     */   }
/*     */ 
/*     */   private void addDataLoadingThreadsContainer(DataLoadingThreadsContainer dataLoadingThreadsContainer) {
/* 188 */     synchronized (this.dataLoadingThreadsLock) {
/* 189 */       if (this.dataLoadingThreads == null) {
/* 190 */         this.dataLoadingThreads = new DataLoadingThreadsContainer[1];
/* 191 */         this.dataLoadingThreads[0] = dataLoadingThreadsContainer;
/*     */       }
/*     */       else {
/* 194 */         this.dataLoadingThreads = ((DataLoadingThreadsContainer[])Arrays.copyOf(this.dataLoadingThreads, this.dataLoadingThreads.length + 1));
/* 195 */         this.dataLoadingThreads[(this.dataLoadingThreads.length - 1)] = dataLoadingThreadsContainer;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public <TD extends TimedData> void addDataLoadingThread(IDataLoadingThread<TD> dataLoadingThread)
/*     */   {
/* 202 */     DataLoadingThreadsContainer container = new DataLoadingThreadsContainer(null);
/* 203 */     if (OfferSide.ASK.equals(dataLoadingThread.getOfferSide())) {
/* 204 */       container.askThread = dataLoadingThread;
/* 205 */       container.askThreadTimedData = new ArrayDeque(505);
/* 206 */       container.bidThread = null;
/* 207 */       container.bidThreadTimedData = null;
/*     */     }
/*     */     else {
/* 210 */       container.askThread = null;
/* 211 */       container.askThreadTimedData = null;
/* 212 */       container.bidThread = dataLoadingThread;
/* 213 */       container.bidThreadTimedData = new ArrayDeque(505);
/*     */     }
/*     */ 
/* 216 */     addDataLoadingThreadsContainer(container);
/*     */   }
/*     */ 
/*     */   public <TD extends TimedData> void removeDataLoadingThread(IDataLoadingThread<TD> dataLoadingThread)
/*     */   {
/* 221 */     synchronized (this.dataLoadingThreadsLock) {
/* 222 */       if ((this.dataLoadingThreads != null) && (dataLoadingThread != null))
/*     */       {
/* 226 */         int index = -1;
/* 227 */         for (int i = 0; i < this.dataLoadingThreads.length; i++) {
/* 228 */           DataLoadingThreadsContainer threadContainer = this.dataLoadingThreads[i];
/* 229 */           if (containsDataThread(threadContainer, dataLoadingThread)) {
/* 230 */             index = i;
/* 231 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 235 */         if (index > -1)
/*     */         {
/* 239 */           this.dataLoadingThreads = removeElementByIndex((DataLoadingThreadsContainer[])this.dataLoadingThreads, index);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private <TD extends TimedData> boolean containsDataThread(DataLoadingThreadsContainer<TD> threadContainer, IDataLoadingThread<TD> dataLoadingThread)
/*     */   {
/* 249 */     return ((threadContainer.askThread != null) && (threadContainer.askThread.equals(dataLoadingThread))) || ((threadContainer.bidThread != null) && (threadContainer.bidThread.equals(dataLoadingThread)));
/*     */   }
/*     */ 
/*     */   public <TD extends TimedData> boolean containsDataLoadingThread(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide)
/*     */   {
/* 259 */     synchronized (this.dataLoadingThreadsLock) {
/* 260 */       for (int i = 0; i < this.dataLoadingThreads.length; i++) {
/* 261 */         DataLoadingThreadsContainer threadContainer = this.dataLoadingThreads[i];
/*     */ 
/* 263 */         boolean askEquals = false;
/* 264 */         boolean bidEquals = false;
/* 265 */         if ((threadContainer.askThread != null) && 
/* 266 */           (threadContainer.askThread.getInstrument().equals(instrument))) {
/* 267 */           if (TickDataLoadingThread.class.isAssignableFrom(threadContainer.askThread.getClass()))
/* 268 */             askEquals = true;
/* 269 */           else if ((threadContainer.askThread.getJForexPeriod().equals(jForexPeriod)) && (OfferSide.ASK.equals(offerSide)))
/*     */           {
/* 271 */             askEquals = true;
/*     */           }
/*     */         }
/*     */ 
/* 275 */         if ((threadContainer.bidThread != null) && 
/* 276 */           (threadContainer.bidThread.getInstrument().equals(instrument))) {
/* 277 */           if (TickDataLoadingThread.class.isAssignableFrom(threadContainer.bidThread.getClass()))
/* 278 */             bidEquals = true;
/* 279 */           else if ((threadContainer.bidThread.getJForexPeriod().equals(jForexPeriod)) && (OfferSide.BID.equals(offerSide)))
/*     */           {
/* 281 */             bidEquals = true;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 286 */         if ((askEquals) || (bidEquals)) {
/* 287 */           return true;
/*     */         }
/*     */       }
/* 290 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Map<Instrument, ITick> getFirstTicks()
/*     */   {
/* 296 */     Map firstTicks = new HashMap();
/* 297 */     for (Instrument instrument : this.instruments)
/*     */     {
/* 299 */       LastTickLiveFeedListener lastTickLiveFeedListener = new LastTickLiveFeedListener();
/* 300 */       LastTickProgressListener lastTickProgressListener = new LastTickProgressListener(this.progressListener);
/*     */       try {
/* 302 */         this.feedDataProvider.loadTicksDataBeforeSynched(instrument, 1, this.from, Filter.NO_FILTER, lastTickLiveFeedListener, lastTickProgressListener);
/*     */       } catch (DataCacheException e) {
/* 304 */         LOGGER.error(e.getMessage(), e);
/* 305 */       }continue;
/*     */ 
/* 307 */       if ((lastTickProgressListener.allDataLoaded) && ((lastTickLiveFeedListener.lastAskBar != null) || (lastTickLiveFeedListener.lastBidBar != null))) {
/* 308 */         TickData lastTick = new TickData(this.from - 1L, 0.0D, 0.0D, 0.0D, 0.0D, null, null, new double[] { 0.0D }, new double[] { 0.0D });
/* 309 */         if (lastTickLiveFeedListener.lastAskBar != null)
/* 310 */           lastTick.ask = lastTickLiveFeedListener.lastAskBar.getClose();
/*     */         else {
/* 312 */           lastTick.ask = StratUtils.round(lastTickLiveFeedListener.lastBidBar.getClose() + 2.0D * instrument.getPipValue(), 5);
/*     */         }
/* 314 */         lastTick.asks = new double[] { lastTick.ask };
/* 315 */         if (lastTickLiveFeedListener.lastBidBar != null)
/* 316 */           lastTick.bid = lastTickLiveFeedListener.lastBidBar.getClose();
/*     */         else {
/* 318 */           lastTick.bid = StratUtils.round(lastTickLiveFeedListener.lastAskBar.getClose() - 2.0D * instrument.getPipValue(), 5);
/*     */         }
/* 320 */         lastTick.bids = new double[] { lastTick.bid };
/*     */ 
/* 322 */         if (!this.instrumentsForConversion.contains(instrument)) {
/* 323 */           setupTicksForInstrumentReportData(lastTick, lastTick, instrument);
/*     */         }
/*     */ 
/* 326 */         firstTicks.put(instrument, lastTick);
/* 327 */       } else if ((!lastTickProgressListener.allDataLoaded) && (lastTickProgressListener.exception != null)) {
/* 328 */         LOGGER.error(lastTickProgressListener.exception.getMessage(), lastTickProgressListener.exception);
/*     */       }
/*     */     }
/*     */ 
/* 332 */     return firstTicks;
/*     */   }
/*     */ 
/*     */   protected <TD extends TimedData> List<DataLoadingThreadsContainer<TD>> createDataLoadingThreads() {
/* 336 */     List result = new ArrayList();
/*     */ 
/* 338 */     for (Iterator i$ = this.instruments.iterator(); i$.hasNext(); ) { instrument = (Instrument)i$.next();
/* 339 */       ArrayBlockingQueue queue = new ArrayBlockingQueue(500, true);
/*     */ 
/* 341 */       String name = "Strategy tester data loading thread - " + instrument.toString();
/* 342 */       IDataLoadingThread dataLoadThread = null;
/*     */ 
/* 345 */       if ((this.interpolationMethod != null) && (this.dataLoadingMethod != null)) {
/* 346 */         throw new IllegalArgumentException("interpolationMethod and dataLoadingMethod are both not null");
/*     */       }
/* 348 */       if ((this.interpolationMethod == null) && (this.dataLoadingMethod == null)) {
/* 349 */         throw new IllegalArgumentException("interpolationMethod and dataLoadingMethod are both null");
/*     */       }
/*     */ 
/* 353 */       if (this.interpolationMethod != null) {
/* 354 */         dataLoadThread = new TickDataFromCandlesLoadingThread(name, instrument, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, this.period), this.offerSide, queue, this.from, this.to, this.feedDataProvider, this.interpolationMethod);
/*     */       }
/*     */ 
/* 367 */       if (this.dataLoadingMethod != null) {
/* 368 */         switch (1.$SwitchMap$com$dukascopy$api$system$ITesterClient$DataLoadingMethod[this.dataLoadingMethod.ordinal()]) {
/*     */         case 1:
/* 370 */           dataLoadThread = new TickDataLoadingThread(name, instrument, queue, this.from, this.to, this.feedDataProvider);
/*     */ 
/* 378 */           break;
/*     */         case 2:
/* 381 */           dataLoadThread = new DifferentPriceTickDataLoadingThread(name, instrument, queue, this.from, this.to, this.feedDataProvider);
/*     */ 
/* 389 */           break;
/*     */         case 3:
/* 392 */           dataLoadThread = new PivotTickDataLoadingThread(name, instrument, queue, this.from, this.to, this.feedDataProvider);
/*     */ 
/* 400 */           break;
/*     */         case 4:
/* 403 */           dataLoadThread = new DifferentPriceInPipsTickDataLoadingThread(name, instrument, this.dataLoadingMethod.getPriceDifferenceInPips(), queue, this.from, this.to, this.feedDataProvider);
/*     */ 
/* 412 */           break;
/*     */         case 5:
/* 415 */           dataLoadThread = new TicksWithTimeIntervalDataLoadingThread(name, instrument, this.dataLoadingMethod.getTimeIntervalBetweenTicks(), queue, this.from, this.to, this.feedDataProvider);
/*     */ 
/* 424 */           break;
/*     */         default:
/* 426 */           throw new IllegalArgumentException("Unsupported dataLoadingMethod " + this.dataLoadingMethod);
/*     */         }
/*     */       }
/*     */ 
/* 430 */       result.add(new DataLoadingThreadsContainer(dataLoadThread));
/*     */ 
/* 432 */       launch(dataLoadThread);
/*     */ 
/* 434 */       List periods = DataCacheUtils.getOldBasicPeriods();
/*     */ 
/* 436 */       for (Period period : periods)
/* 437 */         if (!Period.TICK.equals(period)) {
/* 438 */           IDataLoadingThread askCandleDataLoadThread = createCandleDataLoadingThread(instrument, period, OfferSide.ASK);
/* 439 */           IDataLoadingThread bidCandleDataLoadThread = createCandleDataLoadingThread(instrument, period, OfferSide.BID);
/* 440 */           result.add(new DataLoadingThreadsContainer(askCandleDataLoadThread, bidCandleDataLoadThread));
/*     */ 
/* 442 */           launch(askCandleDataLoadThread);
/* 443 */           launch(bidCandleDataLoadThread);
/*     */         }
/*     */     }
/*     */     Instrument instrument;
/* 448 */     return result;
/*     */   }
/*     */ 
/*     */   private IDataLoadingThread<CandleData> createCandleDataLoadingThread(Instrument instrument, Period period, OfferSide offerSide)
/*     */   {
/* 456 */     ArrayBlockingQueue candleDataQueue = new ArrayBlockingQueue(100, true);
/* 457 */     String name = "Strategy tester data loading thread - " + instrument.toString() + ", " + period + ", " + offerSide;
/* 458 */     IDataLoadingThread dataLoadThread = new CandleDataLoadingThread(name, instrument, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, period), offerSide, candleDataQueue, this.from, this.to, this.feedDataProvider);
/*     */ 
/* 468 */     return dataLoadThread;
/*     */   }
/*     */ 
/*     */   private void launch(Runnable runnable) {
/* 472 */     new Thread(runnable).start();
/*     */   }
/*     */ 
/*     */   protected Map<Instrument, ITick> updateFirstTicks(Map<Instrument, ITick> firstTicks) {
/* 476 */     for (DataLoadingThreadsContainer dataLoadingThread : this.dataLoadingThreads) {
/* 477 */       Instrument instrument = dataLoadingThread.askThread.getInstrument();
/* 478 */       if (firstTicks.get(instrument) == null) {
/* 479 */         BlockingQueue blockingQueue = dataLoadingThread.askThread.getQueue();
/*     */         try {
/* 481 */           if (DataType.TICKS.equals(dataLoadingThread.askThread.getJForexPeriod().getDataType())) {
/* 482 */             TimedData queueTick = null;
/* 483 */             while ((queueTick == null) && (!this.progressListener.stopJob())) {
/* 484 */               queueTick = (TimedData)blockingQueue.poll(20L, TimeUnit.MILLISECONDS);
/*     */             }
/* 486 */             if (this.progressListener.stopJob()) {
/* 487 */               this.progressListener.loadingFinished(true, this.from, this.to, this.from, null);
/* 488 */               stopThreads();
/* 489 */               return null;
/*     */             }
/* 491 */             dataLoadingThread.askThreadTimedData.offer(queueTick);
/* 492 */             if (queueTick.getTime() != -9223372036854775808L) {
/* 493 */               if (!this.instrumentsForConversion.contains(instrument)) {
/* 494 */                 setupTicksForInstrumentReportData((ITick)queueTick, (ITick)queueTick, instrument);
/*     */               }
/*     */ 
/* 497 */               firstTicks.put(instrument, (ITick)queueTick);
/*     */             }
/*     */           }
/*     */         } catch (InterruptedException e) {
/* 501 */           handleException(e, "Error while launching strategy");
/*     */ 
/* 503 */           this.progressListener.loadingFinished(false, this.from, this.to, this.from, e);
/* 504 */           stopThreads();
/*     */ 
/* 506 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 510 */     return firstTicks;
/*     */   }
/*     */ 
/*     */   protected void stopThreads()
/*     */   {
/* 515 */     for (DataLoadingThreadsContainer dataLoadingThread : this.dataLoadingThreads) {
/* 516 */       if (dataLoadingThread.askThread != null) {
/* 517 */         dataLoadingThread.askThread.stopThread();
/* 518 */         dataLoadingThread.askThread = null;
/*     */       }
/* 520 */       if (dataLoadingThread.bidThread != null) {
/* 521 */         dataLoadingThread.bidThread.stopThread();
/* 522 */         dataLoadingThread.bidThread = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isStrategyThread()
/*     */   {
/* 529 */     return this.strategyThread == Thread.currentThread();
/*     */   }
/*     */ 
/*     */   public void startSynched() {
/* 533 */     String oldName = Thread.currentThread().getName();
/* 534 */     this.strategyThread = Thread.currentThread();
/* 535 */     this.strategyThread.setName("StrategyRunner Thread");
/* 536 */     run();
/* 537 */     this.strategyThread.setName(oldName);
/*     */   }
/*     */ 
/*     */   public void start() {
/* 541 */     this.strategyThread = new Thread(this, "StrategyRunner Thread");
/* 542 */     this.strategyThread.start();
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getInstruments()
/*     */   {
/* 547 */     return this.instruments;
/*     */   }
/*     */ 
/*     */   public boolean wasCanceled()
/*     */   {
/* 552 */     return this.canceled;
/*     */   }
/*     */ 
/*     */   protected DataLoadingThreadsContainer<TimedData>[] removeElementByIndex(DataLoadingThreadsContainer<TimedData>[] array, int index) {
/* 556 */     DataLoadingThreadsContainer[] dataLoadingThreadsNew = new DataLoadingThreadsContainer[array.length - 1];
/* 557 */     System.arraycopy(array, 0, dataLoadingThreadsNew, 0, index);
/* 558 */     System.arraycopy(array, index + 1, dataLoadingThreadsNew, index, array.length - index - 1);
/* 559 */     array = dataLoadingThreadsNew;
/* 560 */     return array;
/*     */   }
/*     */ 
/*     */   public long getLastTickTime()
/*     */   {
/* 565 */     return this.lastTickTime;
/*     */   }
/*     */ 
/*     */   protected void historicalTickReceived(SortedDataItem dataItem, IStrategy strategy, TesterFeedDataProvider testerFeedDataProvider, TesterHistory history, TesterCustodian engine, ITesterReport testerReportData, Set<Instrument> instrumentsForConversion, boolean postTickToStrategy)
/*     */     throws JFException
/*     */   {
/* 579 */     Instrument instrument = dataItem.getInstrument();
/* 580 */     ITick tick = (ITick)dataItem.getAskBar();
/*     */ 
/* 582 */     if (!instrumentsForConversion.contains(instrument)) {
/* 583 */       testerReportData.setLastTick(instrument, tick);
/*     */     }
/*     */ 
/* 586 */     if (testerFeedDataProvider != null) {
/* 587 */       long perfStatTimeStart = perfStartTime();
/*     */       try {
/* 589 */         testerFeedDataProvider.tickReceived(instrument, tick.getTime(), tick.getAsk(), tick.getBid(), tick.getAskVolume(), tick.getBidVolume());
/*     */       } finally {
/* 591 */         perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.WRITE_DATA);
/*     */       }
/*     */     }
/*     */ 
/* 595 */     history.addTick(instrument, tick);
/* 596 */     engine.onTick(instrument, tick);
/*     */ 
/* 598 */     if ((postTickToStrategy) && (!instrumentsForConversion.contains(instrument))) {
/* 599 */       strategy.onTick(instrument, tick);
/* 600 */       if (this.strategyEventsListener != null)
/* 601 */         this.strategyEventsListener.onTick(instrument, tick);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void historicalCandleReceived(SortedDataItem dataItem, IStrategy strategy, TesterFeedDataProvider testerFeedDataProvider, TesterHistory history, TesterCustodian engine, Set<Instrument> instrumentsForConversion, boolean implementsOnBar, boolean postCandleToStrategy)
/*     */     throws JFException
/*     */   {
/* 618 */     IntraPeriodCandleData askBar = (IntraPeriodCandleData)dataItem.getAskBar();
/* 619 */     IntraPeriodCandleData bidBar = (IntraPeriodCandleData)dataItem.getBidBar();
/*     */ 
/* 621 */     Period period = dataItem.getJForexPeriod().getPeriod();
/* 622 */     Instrument instrument = dataItem.getInstrument();
/*     */ 
/* 624 */     if (testerFeedDataProvider != null) {
/* 625 */       long perfStatTimeStart = perfStartTime();
/*     */       try {
/* 627 */         testerFeedDataProvider.barsReceived(instrument, period, askBar, bidBar);
/*     */       } finally {
/* 629 */         perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.WRITE_DATA);
/*     */       }
/*     */     }
/*     */ 
/* 633 */     long currentCandleTime = DataCacheUtils.getNextCandleStartFast(dataItem.getJForexPeriod().getPeriod(), dataItem.getAskOrBidBarTime());
/* 634 */     engine.addCurrentTime(currentCandleTime);
/*     */ 
/* 636 */     if (askBar != null) {
/* 637 */       history.addCandle(instrument, period, OfferSide.ASK, askBar);
/*     */     }
/* 639 */     if (bidBar != null) {
/* 640 */       history.addCandle(instrument, period, OfferSide.BID, bidBar);
/*     */     }
/*     */ 
/* 643 */     if ((postCandleToStrategy) && (!instrumentsForConversion.contains(instrument))) {
/* 644 */       if ((implementsOnBar) && 
/* 645 */         (askBar != null) && (bidBar != null)) {
/* 646 */         strategy.onBar(instrument, period, askBar, bidBar);
/* 647 */         if (this.strategyEventsListener != null) {
/* 648 */           this.strategyEventsListener.onBar(instrument, period, askBar, bidBar);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 653 */       if (askBar != null) {
/* 654 */         StrategyRateDataNotificationFactory.getIsntance().getCandleNotificationManager().historicalBarReceived(strategy, instrument, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, period), OfferSide.ASK, askBar);
/*     */       }
/*     */ 
/* 662 */       if (bidBar != null) {
/* 663 */         StrategyRateDataNotificationFactory.getIsntance().getCandleNotificationManager().historicalBarReceived(strategy, instrument, new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, period), OfferSide.BID, bidBar);
/*     */       }
/*     */ 
/* 671 */       if ((askBar == null) && (bidBar == null))
/* 672 */         throw new NullPointerException("Both Ask and Bid bars could not be nulls!");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void historicalPriceRangeReceived(SortedDataItem dataItem, IStrategy strategy, Set<Instrument> instrumentsForConversion, boolean postBarToStrategy)
/*     */   {
/* 683 */     IRangeBar askBar = (IRangeBar)dataItem.getAskBar();
/* 684 */     IRangeBar bidBar = (IRangeBar)dataItem.getBidBar();
/* 685 */     Instrument instrument = dataItem.getInstrument();
/*     */ 
/* 687 */     if ((postBarToStrategy) && (!instrumentsForConversion.contains(instrument))) {
/* 688 */       if (askBar != null) {
/* 689 */         StrategyRateDataNotificationFactory.getIsntance().getPriceRangeNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.ASK, askBar);
/*     */       }
/*     */ 
/* 697 */       if (bidBar != null) {
/* 698 */         StrategyRateDataNotificationFactory.getIsntance().getPriceRangeNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.BID, bidBar);
/*     */       }
/*     */ 
/* 706 */       if ((askBar == null) && (bidBar == null))
/* 707 */         throw new NullPointerException("Both Ask and Bid bars could not be nulls!");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void historicalRenkoReceived(SortedDataItem dataItem, IStrategy strategy, Set<Instrument> instrumentsForConversion, boolean postBarToStrategy)
/*     */   {
/* 718 */     IRenkoBar askBar = (IRenkoBar)dataItem.getAskBar();
/* 719 */     IRenkoBar bidBar = (IRenkoBar)dataItem.getBidBar();
/* 720 */     Instrument instrument = dataItem.getInstrument();
/*     */ 
/* 722 */     if ((postBarToStrategy) && (!instrumentsForConversion.contains(instrument))) {
/* 723 */       if (askBar != null) {
/* 724 */         StrategyRateDataNotificationFactory.getIsntance().getRenkoNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.ASK, askBar);
/*     */       }
/*     */ 
/* 732 */       if (bidBar != null) {
/* 733 */         StrategyRateDataNotificationFactory.getIsntance().getRenkoNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.BID, bidBar);
/*     */       }
/*     */ 
/* 741 */       if ((askBar == null) && (bidBar == null))
/* 742 */         throw new NullPointerException("Both Ask and Bid bars could not be nulls!");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void historicalPointAndFigureReceived(SortedDataItem dataItem, IStrategy strategy, Set<Instrument> instrumentsForConversion, boolean postBarToStrategy)
/*     */   {
/* 753 */     IPointAndFigure askBar = (IPointAndFigure)dataItem.getAskBar();
/* 754 */     IPointAndFigure bidBar = (IPointAndFigure)dataItem.getBidBar();
/* 755 */     Instrument instrument = dataItem.getInstrument();
/*     */ 
/* 757 */     if ((postBarToStrategy) && (!instrumentsForConversion.contains(instrument))) {
/* 758 */       if (askBar != null) {
/* 759 */         StrategyRateDataNotificationFactory.getIsntance().getPointAndFigureNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.ASK, askBar);
/*     */       }
/*     */ 
/* 767 */       if (bidBar != null) {
/* 768 */         StrategyRateDataNotificationFactory.getIsntance().getPointAndFigureNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.BID, bidBar);
/*     */       }
/*     */ 
/* 776 */       if ((askBar == null) && (bidBar == null))
/* 777 */         throw new NullPointerException("Both Ask and Bid bars could not be nulls!");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void historicalTickBarReceived(SortedDataItem dataItem, IStrategy strategy, Set<Instrument> instrumentsForConversion, boolean postBarToStrategy)
/*     */   {
/* 789 */     ITickBar askBar = (ITickBar)dataItem.getAskBar();
/* 790 */     ITickBar bidBar = (ITickBar)dataItem.getBidBar();
/* 791 */     Instrument instrument = dataItem.getInstrument();
/*     */ 
/* 793 */     if ((postBarToStrategy) && (!instrumentsForConversion.contains(instrument))) {
/* 794 */       if (askBar != null) {
/* 795 */         StrategyRateDataNotificationFactory.getIsntance().getTickBarNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.ASK, askBar);
/*     */       }
/*     */ 
/* 803 */       if (bidBar != null) {
/* 804 */         StrategyRateDataNotificationFactory.getIsntance().getTickBarNotificationManager().historicalBarReceived(strategy, instrument, dataItem.getJForexPeriod(), OfferSide.BID, bidBar);
/*     */       }
/*     */ 
/* 812 */       if ((askBar == null) && (bidBar == null))
/* 813 */         throw new NullPointerException("Both Ask and Bid bars could not be nulls!");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getShortWeekDayName(long time)
/*     */   {
/* 820 */     String shortWeekDayName = "";
/*     */     try {
/* 822 */       Calendar calendar = Calendar.getInstance();
/* 823 */       Date dateOfLastTickTime = new Date(time);
/* 824 */       calendar.setTime(dateOfLastTickTime);
/* 825 */       int weekDay = calendar.get(7);
/* 826 */       shortWeekDayName = this.shortWeekdays[weekDay];
/*     */     } catch (Exception e) {
/* 828 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 831 */     return shortWeekDayName;
/*     */   }
/*     */ 
/*     */   protected long getNextTime(TimedData data, JForexPeriod jForexPeriod) {
/* 835 */     long result = data.getTime();
/*     */ 
/* 837 */     if (DataType.TIME_PERIOD_AGGREGATION.equals(jForexPeriod.getDataType())) {
/* 838 */       result = DataCacheUtils.getNextCandleStartFast(jForexPeriod.getPeriod(), result);
/*     */     }
/* 840 */     else if ((data instanceof AbstractPriceAggregationData)) {
/* 841 */       result = ((AbstractPriceAggregationData)data).getEndTime();
/*     */     }
/*     */ 
/* 844 */     return result;
/*     */   }
/*     */ 
/*     */   public void breakRemainingPause() {
/* 848 */     synchronized (this.sleepObject) {
/* 849 */       this.sleepObject.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addStrategyEventsListener(StrategyEventsListener strategyEventsListener)
/*     */   {
/* 855 */     this.strategyEventsListener = strategyEventsListener;
/*     */   }
/*     */ 
/*     */   public void removeStrategyEventsListener()
/*     */   {
/* 860 */     this.strategyEventsListener = null;
/*     */   }
/*     */ 
/*     */   public StrategyEventsListener getStrategyEventsListener()
/*     */   {
/* 865 */     return this.strategyEventsListener;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.AbstractStrategyRunner
 * JD-Core Version:    0.6.0
 */