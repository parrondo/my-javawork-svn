/*      */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.IOrder.State;
/*      */ import com.dukascopy.api.IStrategy;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.impl.StrategyWrapper;
/*      */ import com.dukascopy.api.impl.TimedData;
/*      */ import com.dukascopy.api.impl.execution.ScienceWaitForUpdate;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*      */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*      */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataComparator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataItem;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import java.text.DateFormat;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.FutureTask;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import javax.swing.JFrame;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class StrategyRunner extends AbstractStrategyRunner
/*      */   implements ExecutionControlListener
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final long PROCESS_INTERVAL = 600000L;
/*      */   private final Map<Instrument, Double> minTradableAmounts;
/*      */   private IStrategy strategy;
/*      */   private boolean isFullAccessGranted;
/*   59 */   private boolean implementsOnBar = true;
/*      */   private JFrame parentFrame;
/*   61 */   private List<SortedDataItem> sortedData = new ArrayList();
/*      */   private INotificationUtils notificationUtils;
/*      */   private Map<Instrument, TesterChartData> chartPanels;
/*      */   private TesterFeedDataProvider testerFeedDataProvider;
/*      */   private TesterOrdersProvider testerOrdersProvider;
/*      */   private TesterAccount account;
/*      */   private TesterConfig context;
/*      */   private TesterCustodian engine;
/*      */   private IStrategyDataStorage dataStorage;
/*      */   private TesterHistory history;
/*   72 */   private TesterReportData testerReportData = new TesterReportData();
/*      */   private ExecutionControl executionControl;
/*   74 */   private volatile boolean waitingThreads = false;
/*      */   private IStrategyExceptionHandler exceptionHandler;
/*      */   private long progressBarLastUpdate;
/*   78 */   private long progressBarUpdateInterval = 1800000L;
/*   79 */   private long lastTickLocalTime = -9223372036854775808L;
/*   80 */   private List<String[]> strategyParameters = null;
/*      */ 
/*      */   public StrategyRunner(JFrame parentFrame, String strategyName, IStrategy strategy, boolean isFullAccessGranted, Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to, Set<Instrument> instruments, LoadingProgressListener progressListener, INotificationUtils notificationUtils, Map<Instrument, Double> minTradableAmounts, TesterAccount account, TesterFeedDataProvider testerFeedDataProvider, TesterOrdersProvider testerOrdersProvider, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, boolean perfStats, IStrategyExceptionHandler exceptionHandler, IStrategyDataStorage storage)
/*      */   {
/*  106 */     this(parentFrame, strategyName, strategy, isFullAccessGranted, period, offerSide, interpolationMethod, dataLoadingMethod, from, to, instruments, null, progressListener, notificationUtils, minTradableAmounts, account, FeedDataProvider.getDefaultInstance(), testerFeedDataProvider, testerOrdersProvider, chartPanels, executionControl, perfStats, exceptionHandler, storage, null);
/*      */   }
/*      */ 
/*      */   public StrategyRunner(JFrame parentFrame, String strategyName, IStrategy strategy, boolean isFullAccessGranted, ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to, Set<Instrument> instruments, LoadingProgressListener progressListener, INotificationUtils notificationUtils, Map<Instrument, Double> minTradableAmounts, TesterAccount account, TesterFeedDataProvider testerFeedDataProvider, TesterOrdersProvider testerOrdersProvider, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, boolean perfStats, IStrategyExceptionHandler exceptionHandler)
/*      */   {
/*  153 */     this(parentFrame, strategyName, strategy, isFullAccessGranted, null, null, null, dataLoadingMethod, from, to, instruments, null, progressListener, notificationUtils, minTradableAmounts, account, FeedDataProvider.getDefaultInstance(), testerFeedDataProvider, testerOrdersProvider, chartPanels, executionControl, perfStats, exceptionHandler, null, null);
/*      */   }
/*      */ 
/*      */   public StrategyRunner(JFrame parentFrame, String strategyName, IStrategy strategy, boolean isFullAccessGranted, Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to, Set<Instrument> instruments, Set<Instrument> instrumentsForConversion, LoadingProgressListener progressListener, INotificationUtils notificationUtils, Map<Instrument, Double> minTradableAmounts, TesterAccount account, TesterFeedDataProvider testerFeedDataProvider, TesterOrdersProvider testerOrdersProvider, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, boolean perfStats, IStrategyExceptionHandler exceptionHandler, IStrategyDataStorage storage, List<String[]> strategyParameters)
/*      */   {
/*  208 */     this(parentFrame, strategyName, strategy, isFullAccessGranted, period, offerSide, interpolationMethod, dataLoadingMethod, from, to, instruments, instrumentsForConversion, progressListener, notificationUtils, minTradableAmounts, account, FeedDataProvider.getDefaultInstance(), testerFeedDataProvider, testerOrdersProvider, chartPanels, executionControl, perfStats, exceptionHandler, storage, strategyParameters);
/*      */   }
/*      */ 
/*      */   public StrategyRunner(JFrame parentFrame, String strategyName, IStrategy strategy, boolean isFullAccessGranted, ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to, Set<Instrument> instruments, LoadingProgressListener progressListener, INotificationUtils notificationUtils, Map<Instrument, Double> minTradableAmounts, TesterAccount account, IFeedDataProvider feedDataProvider, TesterFeedDataProvider testerFeedDataProvider, TesterOrdersProvider testerOrdersProvider, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, boolean perfStats, IStrategyExceptionHandler exceptionHandler)
/*      */   {
/*  258 */     this(parentFrame, strategyName, strategy, isFullAccessGranted, null, null, null, dataLoadingMethod, from, to, instruments, null, progressListener, notificationUtils, minTradableAmounts, account, feedDataProvider, testerFeedDataProvider, testerOrdersProvider, chartPanels, executionControl, perfStats, exceptionHandler, null, null);
/*      */   }
/*      */ 
/*      */   public StrategyRunner(JFrame parentFrame, String strategyName, IStrategy strategy, boolean isFullAccessGranted, Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to, Set<Instrument> instruments, Set<Instrument> instrumentsForConversion, LoadingProgressListener progressListener, INotificationUtils notificationUtils, Map<Instrument, Double> minTradableAmounts, TesterAccount account, IFeedDataProvider feedDataProvider, TesterFeedDataProvider testerFeedDataProvider, TesterOrdersProvider testerOrdersProvider, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, boolean perfStats, IStrategyExceptionHandler exceptionHandler, IStrategyDataStorage storage, List<String[]> strategyParameters)
/*      */   {
/*  318 */     super(period, offerSide, interpolationMethod, dataLoadingMethod, from, to, feedDataProvider, instruments, progressListener, instrumentsForConversion);
/*      */ 
/*  332 */     this.testerReportData.strategyName = strategyName;
/*  333 */     this.testerReportData.from = from;
/*  334 */     this.testerReportData.to = to;
/*      */ 
/*  336 */     if (perfStats) {
/*  337 */       this.testerReportData.perfStats = new long[ITesterReport.PerfStats.values().length];
/*  338 */       this.testerReportData.perfStatCounts = new int[ITesterReport.PerfStats.values().length];
/*      */     }
/*  340 */     this.strategy = strategy;
/*  341 */     this.dataStorage = storage;
/*  342 */     this.isFullAccessGranted = isFullAccessGranted;
/*  343 */     this.parentFrame = parentFrame;
/*  344 */     this.notificationUtils = notificationUtils;
/*  345 */     this.minTradableAmounts = minTradableAmounts;
/*  346 */     this.account = account;
/*  347 */     this.testerFeedDataProvider = testerFeedDataProvider;
/*  348 */     this.testerOrdersProvider = testerOrdersProvider;
/*  349 */     this.chartPanels = chartPanels;
/*  350 */     this.executionControl = executionControl;
/*  351 */     this.executionControl.addExecutionControlListener(this);
/*  352 */     speedChanged(null);
/*  353 */     this.exceptionHandler = exceptionHandler;
/*  354 */     assert (executionControl != null);
/*  355 */     this.strategyParameters = strategyParameters;
/*      */   }
/*      */ 
/*      */   public TesterCustodian getEngine() {
/*  359 */     return this.engine;
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*  364 */     this.progressListener.dataLoaded(this.from, this.to, this.from, "Getting prices before start time");
/*  365 */     Map firstTicks = getFirstTicks();
/*      */ 
/*  367 */     if (this.progressListener.stopJob()) {
/*  368 */       this.progressListener.loadingFinished(true, this.from, this.to, this.from, null);
/*  369 */       return;
/*      */     }
/*      */ 
/*  372 */     this.progressListener.dataLoaded(this.from, this.to, this.from, "Creating data loading threads");
/*      */ 
/*  375 */     List dataThreads = createDataLoadingThreads();
/*  376 */     this.dataLoadingThreads = ((DataLoadingThreadsContainer[])dataThreads.toArray(new DataLoadingThreadsContainer[dataThreads.size()]));
/*      */ 
/*  378 */     this.progressListener.dataLoaded(this.from, this.to, this.from, "Getting prices before start time");
/*  379 */     firstTicks = updateFirstTicks(firstTicks);
/*      */ 
/*  381 */     if (firstTicks == null)
/*      */     {
/*  385 */       return;
/*      */     }
/*      */ 
/*  388 */     this.testerReportData.initialDeposit = this.account.getDeposit();
/*      */     try {
/*  390 */       this.testerReportData.parameterValues = this.strategyParameters;
/*  391 */       this.engine = new TesterCustodian(this.instruments, this.minTradableAmounts, this.notificationUtils, this.from, this.account, firstTicks, this.testerReportData, this, this.testerOrdersProvider, this.exceptionHandler);
/*      */ 
/*  393 */       this.context = new TesterConfig(this.engine, this.notificationUtils, this.isFullAccessGranted, this.chartPanels, this.executionControl, this.testerOrdersProvider, this.progressListener, this, this.account, this.strategy, this.feedDataProvider);
/*      */ 
/*  406 */       this.history = ((TesterHistory)this.context.getHistory());
/*  407 */       this.history.setFirstTicks(firstTicks);
/*  408 */       this.progressListener.dataLoaded(this.from, this.to, this.from, "Filling in-progress candles");
/*      */       try {
/*  410 */         this.history.fillCurrentCandles(this.instruments, this.feedDataProvider);
/*      */       } catch (DataCacheException e) {
/*  412 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  414 */       this.engine.setStrategy(this.strategy);
/*      */ 
/*  416 */       this.progressListener.dataLoaded(this.from, this.to, this.from, "Executing onStart");
/*  417 */       long perfStatTimeStart = perfStartTime();
/*      */       try {
/*  419 */         this.strategy.onStart(this.context);
/*      */       } catch (Throwable t) {
/*  421 */         handleException(t, "Strategy tester");
/*  422 */         this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_START, t);
/*  423 */         this.progressListener.loadingFinished(false, this.from, this.to, this.from, (t instanceof Exception) ? (Exception)t : null);
/*  424 */         stopThreads();
/*      */ 
/*  427 */         perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_START);
/*      */ 
/*  558 */         this.testerReportData.finishDeposit = this.account.getRealizedEquityWithCommissions();
/*  559 */         stopThreads();
/*  560 */         if ((this.testerFeedDataProvider == null) || 
/*  563 */           (this.executionControl != null))
/*  564 */           this.executionControl.removeExecutionControlListener(this); return;
/*      */       }
/*      */       finally
/*      */       {
/*  427 */         perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_START);
/*      */       }
/*  429 */       this.progressListener.dataLoaded(this.from, this.to, this.from, "Running");
/*      */       try {
/*  431 */         if (this.progressListener.stopJob()) {
/*  432 */           this.progressListener.loadingFinished(true, this.from, this.to, this.from, null);
/*  433 */           stopThreads();
/*      */ 
/*  542 */           perfStatTimeStart = perfStartTime();
/*      */           try {
/*  544 */             this.strategy.onStop();
/*      */           } catch (Throwable t) {
/*  546 */             handleException(t, "Strategy tester");
/*  547 */             this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */           } finally {
/*  549 */             perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_STOP);
/*      */           }
/*  551 */           this.engine.doDelayedTasks();
/*  552 */           this.engine.calculateTurnoverAndCommission();
/*      */ 
/*  558 */           this.testerReportData.finishDeposit = this.account.getRealizedEquityWithCommissions();
/*  559 */           stopThreads();
/*  560 */           if ((this.testerFeedDataProvider == null) || 
/*  563 */             (this.executionControl != null))
/*  564 */             this.executionControl.removeExecutionControlListener(this); return;
/*      */         }
/*  438 */         this.progressBarLastUpdate = this.from;
/*  439 */         this.lastTickTime = this.from;
/*  440 */         while (!this.context.isStopped()) {
/*  441 */           SortedDataItem dataItem = getNextThreadsWithTimedData(this.lastTickTime);
/*  442 */           if (dataItem == null) break;
/*  443 */           long tickProcessingPerfStatTimeStart = perfStartTime();
/*      */           try {
/*  445 */             long currentCandleTime = dataItem.getAskOrBidBarTime();
/*  446 */             currentCandleTime = getNextTime(dataItem.getAskOrBidData(), dataItem.getJForexPeriod());
/*      */ 
/*  448 */             perfStatTimeStart = perfStartTime();
/*  449 */             this.engine.addCurrentTime(currentCandleTime);
/*  450 */             this.lastTickTime = currentCandleTime;
/*      */ 
/*  452 */             IStrategyExceptionHandler.Source source = IStrategyExceptionHandler.Source.ON_BAR;
/*  453 */             ITesterReport.PerfStats perfStats = ITesterReport.PerfStats.ON_BAR;
/*      */             try
/*      */             {
/*  456 */               switch (1.$SwitchMap$com$dukascopy$api$DataType[dataItem.getJForexPeriod().getDataType().ordinal()]) {
/*      */               case 1:
/*  458 */                 source = IStrategyExceptionHandler.Source.ON_TICK;
/*  459 */                 perfStats = ITesterReport.PerfStats.ON_TICK;
/*  460 */                 historicalTickReceived(dataItem, this.strategy, this.testerFeedDataProvider, this.history, this.engine, this.testerReportData, this.instrumentsForConversion, true);
/*  461 */                 break;
/*      */               case 2:
/*  464 */                 historicalCandleReceived(dataItem, this.strategy, this.testerFeedDataProvider, this.history, this.engine, this.instrumentsForConversion, this.implementsOnBar, true);
/*  465 */                 break;
/*      */               case 3:
/*  468 */                 historicalPriceRangeReceived(dataItem, this.strategy, this.instrumentsForConversion, true);
/*  469 */                 break;
/*      */               case 4:
/*  472 */                 historicalPointAndFigureReceived(dataItem, this.strategy, this.instrumentsForConversion, true);
/*  473 */                 break;
/*      */               case 5:
/*  476 */                 historicalTickBarReceived(dataItem, this.strategy, this.instrumentsForConversion, true);
/*  477 */                 break;
/*      */               case 6:
/*  480 */                 historicalRenkoReceived(dataItem, this.strategy, this.instrumentsForConversion, true);
/*  481 */                 break;
/*      */               default:
/*  484 */                 throw new IllegalArgumentException("Unsupported Data Type " + dataItem.getJForexPeriod().getDataType());
/*      */               }
/*      */             }
/*      */             catch (Throwable t) {
/*  488 */               handleException(t, "Strategy tester");
/*  489 */               this.exceptionHandler.onException(1L, source, t);
/*      */             } finally {
/*  491 */               perfStopTime(perfStatTimeStart, perfStats);
/*      */             }
/*      */ 
/*  494 */             checkAndFireDataLoaded();
/*      */           } finally {
/*  496 */             perfStopTime(tickProcessingPerfStatTimeStart, ITesterReport.PerfStats.TICK_BAR_PROCESSING);
/*      */           }
/*      */ 
/*  499 */           this.engine.doDelayedTasks();
/*  500 */           if (this.dataStorage != null) {
/*  501 */             perfStatTimeStart = perfStartTime();
/*      */             try {
/*  503 */               this.dataStorage.put(this.lastTickTime, this.account.getDeposit(), this.account.getTotalProfitLoss(), this.account.getEquityActual());
/*      */             } finally {
/*  505 */               perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.WRITE_DATA);
/*      */             }
/*      */           }
/*  508 */           FutureTask[] tasks = this.context.getDelayedTasks();
/*  509 */           for (FutureTask futureTask : tasks) {
/*  510 */             perfStatTimeStart = perfStartTime();
/*      */             try {
/*  512 */               futureTask.run();
/*      */             } finally {
/*  514 */               perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.USER_TASKS);
/*      */             }
/*      */           }
/*  517 */           if (this.waitingThreads) {
/*  518 */             synchronized (this)
/*      */             {
/*  520 */               notifyAll();
/*      */             }
/*      */           }
/*  523 */           if (this.progressListener.stopJob()) {
/*  524 */             this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*  525 */             stopThreads();
/*      */ 
/*  542 */             perfStatTimeStart = perfStartTime();
/*      */             try {
/*  544 */               this.strategy.onStop();
/*      */             } catch (Throwable t) {
/*  546 */               handleException(t, "Strategy tester");
/*  547 */               this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */             } finally {
/*  549 */               perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_STOP);
/*      */             }
/*  551 */             this.engine.doDelayedTasks();
/*  552 */             this.engine.calculateTurnoverAndCommission();
/*      */ 
/*  558 */             this.testerReportData.finishDeposit = this.account.getRealizedEquityWithCommissions();
/*  559 */             stopThreads();
/*  560 */             if ((this.testerFeedDataProvider == null) || 
/*  563 */               (this.executionControl != null))
/*  564 */               this.executionControl.removeExecutionControlListener(this); return;
/*      */           }
/*      */         }
/*  533 */         this.engine.sendAccountInfo();
/*      */ 
/*  535 */         if (this.progressListener.stopJob()) {
/*  536 */           this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*  537 */           stopThreads();
/*      */ 
/*  542 */           perfStatTimeStart = perfStartTime();
/*      */           try {
/*  544 */             this.strategy.onStop();
/*      */           } catch (Throwable t) {
/*  546 */             handleException(t, "Strategy tester");
/*  547 */             this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */           } finally {
/*  549 */             perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_STOP);
/*      */           }
/*  551 */           this.engine.doDelayedTasks();
/*  552 */           this.engine.calculateTurnoverAndCommission();
/*      */ 
/*  558 */           this.testerReportData.finishDeposit = this.account.getRealizedEquityWithCommissions();
/*  559 */           stopThreads();
/*  560 */           if ((this.testerFeedDataProvider == null) || 
/*  563 */             (this.executionControl != null))
/*  564 */             this.executionControl.removeExecutionControlListener(this); return;
/*      */         }
/*  540 */         this.progressListener.dataLoaded(this.from, this.to, this.to, "Executing onStop");
/*      */       } finally {
/*  542 */         perfStatTimeStart = perfStartTime();
/*      */         try {
/*  544 */           this.strategy.onStop();
/*      */         } catch (Throwable t) {
/*  546 */           handleException(t, "Strategy tester");
/*  547 */           this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */         } finally {
/*  549 */           perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.ON_STOP);
/*      */         }
/*  551 */         this.engine.doDelayedTasks();
/*  552 */         this.engine.calculateTurnoverAndCommission();
/*      */       }
/*      */ 
/*  555 */       this.testerReportData.finishDeposit = this.account.getRealizedEquityWithCommissions();
/*  556 */       this.progressListener.loadingFinished(true, this.from, this.to, this.to, null);
/*      */     } finally {
/*  558 */       this.testerReportData.finishDeposit = this.account.getRealizedEquityWithCommissions();
/*  559 */       stopThreads();
/*  560 */       if ((this.testerFeedDataProvider == null) || 
/*  563 */         (this.executionControl != null))
/*  564 */         this.executionControl.removeExecutionControlListener(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void speedChanged(ExecutionControlEvent event)
/*      */   {
/*  571 */     switch (this.executionControl.getSpeed())
/*      */     {
/*      */     case 0:
/*  574 */       this.progressBarUpdateInterval = 1L;
/*  575 */       break;
/*      */     case 1:
/*  578 */       this.progressBarUpdateInterval = 1000L;
/*  579 */       break;
/*      */     case 2:
/*  582 */       this.progressBarUpdateInterval = 2000L;
/*  583 */       break;
/*      */     case 3:
/*  586 */       this.progressBarUpdateInterval = 5000L;
/*  587 */       break;
/*      */     case 4:
/*  590 */       this.progressBarUpdateInterval = 10000L;
/*  591 */       break;
/*      */     case 5:
/*  594 */       this.progressBarUpdateInterval = 100000L;
/*  595 */       break;
/*      */     case 6:
/*  598 */       this.progressBarUpdateInterval = 500000L;
/*  599 */       break;
/*      */     case 7:
/*      */     default:
/*  603 */       this.progressBarUpdateInterval = 1800000L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void stateChanged(ExecutionControlEvent event)
/*      */   {
/*      */   }
/*      */ 
/*      */   private SortedDataItem getNextThreadsWithTimedData(long startTime)
/*      */   {
/*  613 */     long perfStatTimeStart = perfStartTime();
/*      */     try {
/*  615 */       this.executionControl.waitForResume(this.progressListener);
/*  616 */       if (this.sortedData.isEmpty()) {
/*  617 */         boolean moreDataAvailable = false;
/*      */         do
/*      */         {
/*  620 */           boolean firstThreads = true;
/*  621 */           long untilTime = -9223372036854775808L;
/*  622 */           for (int j = 0; j < this.dataLoadingThreads.length; j++) {
/*  623 */             DataLoadingThreadsContainer dataLoadingThread = this.dataLoadingThreads[j];
/*      */ 
/*  625 */             BlockingQueue askBlockingQueue = dataLoadingThread.askThread != null ? dataLoadingThread.askThread.getQueue() : null;
/*  626 */             BlockingQueue bidBlockingQueue = dataLoadingThread.bidThread != null ? dataLoadingThread.bidThread.getQueue() : null;
/*      */ 
/*  628 */             BlockingQueue askOrBidBlockingQueue = askBlockingQueue != null ? askBlockingQueue : bidBlockingQueue;
/*  629 */             ArrayDeque askOrBidThreadTimeData = dataLoadingThread.askThread != null ? dataLoadingThread.askThreadTimedData : dataLoadingThread.bidThreadTimedData;
/*  630 */             IDataLoadingThread askOrBidDataLoadingThread = dataLoadingThread.askThread != null ? dataLoadingThread.askThread : dataLoadingThread.bidThread;
/*      */ 
/*  632 */             JForexPeriod jForexPeriod = askOrBidDataLoadingThread.getJForexPeriod();
/*      */ 
/*  634 */             if (firstThreads) { firstThreads = false;
/*  636 */               int i = 0;
/*      */               long time;
/*      */               do { if (dataLoadingThread.askThreadTimedData.isEmpty())
/*      */                 {
/*  642 */                   int read = askBlockingQueue.drainTo(dataLoadingThread.askThreadTimedData);
/*  643 */                   if (read == 0) {
/*      */                     TimedData timedData;
/*      */                     do
/*      */                       try {
/*  648 */                         timedData = (TimedData)askBlockingQueue.take();
/*      */                       }
/*      */                       catch (InterruptedException e) {
/*  651 */                         timedData = null;
/*      */                       }
/*  653 */                     while ((timedData == null) && (!this.progressListener.stopJob()));
/*      */ 
/*  655 */                     if ((timedData == null) && (this.progressListener.stopJob())) {
/*  656 */                       e = null;
/*      */                       return e;
/*      */                     }
/*  660 */                     dataLoadingThread.askThreadTimedData.offer(timedData);
/*  661 */                     if (timedData.getTime() > startTime + 600000L)
/*      */                     {
/*  663 */                       moreDataAvailable = true;
/*  664 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */ 
/*  669 */                 assert (dataLoadingThread.askThreadTimedData.peek() != null) : "something wrong here";
/*      */ 
/*  671 */                 if (((TimedData)dataLoadingThread.askThreadTimedData.peek()).getTime() > startTime + 600000L)
/*      */                 {
/*  673 */                   moreDataAvailable = true;
/*  674 */                   break;
/*      */                 }
/*  676 */                 TimedData askQueueTimedData = (TimedData)dataLoadingThread.askThreadTimedData.poll();
/*      */                 TimedData bidQueueTimedData;
/*      */                 TimedData bidQueueTimedData;
/*  679 */                 if (bidBlockingQueue != null) {
/*  680 */                   if (dataLoadingThread.bidThreadTimedData.isEmpty())
/*      */                   {
/*  682 */                     int read = bidBlockingQueue.drainTo(dataLoadingThread.bidThreadTimedData);
/*  683 */                     if (read == 0) {
/*      */                       TimedData timedData;
/*      */                       do
/*      */                         try {
/*  688 */                           timedData = (TimedData)bidBlockingQueue.take();
/*      */                         }
/*      */                         catch (InterruptedException e) {
/*  691 */                           timedData = null;
/*      */                         }
/*  693 */                       while ((timedData == null) && (!this.progressListener.stopJob()));
/*  694 */                       if ((timedData == null) && (this.progressListener.stopJob())) {
/*  695 */                         e = null;
/*      */                         return e;
/*      */                       }
/*  699 */                       dataLoadingThread.bidThreadTimedData.offer(timedData);
/*      */                     }
/*      */                   }
/*      */ 
/*  703 */                   bidQueueTimedData = (TimedData)dataLoadingThread.bidThreadTimedData.poll();
/*      */                 } else {
/*  705 */                   bidQueueTimedData = null;
/*      */                 }
/*  707 */                 time = askQueueTimedData.getTime();
/*  708 */                 if (time != -9223372036854775808L) {
/*  709 */                   this.sortedData.add(new SortedDataItem(dataLoadingThread.askThread.getInstrument(), jForexPeriod, askQueueTimedData, bidQueueTimedData));
/*      */                 } else {
/*  711 */                   this.dataLoadingThreads = removeElementByIndex(this.dataLoadingThreads, j);
/*  712 */                   j--;
/*      */                 }
/*  714 */                 i++; }
/*  715 */               while ((time != -9223372036854775808L) && (i < 100));
/*  716 */               if (!this.sortedData.isEmpty()) {
/*  717 */                 SortedDataItem dataItem = (SortedDataItem)this.sortedData.get(this.sortedData.size() - 1);
/*  718 */                 Period lastThreadPeriod = dataItem.getJForexPeriod().getPeriod();
/*  719 */                 untilTime = dataItem.getAskBar().getTime();
/*  720 */                 if (lastThreadPeriod != Period.TICK) {
/*  721 */                   untilTime = DataCacheUtils.getNextCandleStartFast(lastThreadPeriod, untilTime);
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  726 */                 untilTime = startTime + 600000L;
/*      */               }
/*      */             }
/*  729 */             else if ((askBlockingQueue != null) && (bidBlockingQueue != null))
/*      */             {
/*      */               long time;
/*      */               do
/*      */               {
/*  735 */                 if (dataLoadingThread.askThreadTimedData.isEmpty())
/*      */                 {
/*  737 */                   int read = askBlockingQueue.drainTo(dataLoadingThread.askThreadTimedData);
/*  738 */                   if (read == 0) {
/*      */                     TimedData timedData;
/*      */                     do
/*      */                       try {
/*  743 */                         timedData = (TimedData)askBlockingQueue.take();
/*      */                       }
/*      */                       catch (InterruptedException e) {
/*  746 */                         timedData = null;
/*      */                       }
/*  748 */                     while ((timedData == null) && (!this.progressListener.stopJob()));
/*      */ 
/*  750 */                     if ((timedData == null) && (this.progressListener.stopJob())) {
/*  751 */                       e = null;
/*      */                       return e;
/*      */                     }
/*  755 */                     dataLoadingThread.askThreadTimedData.offer(timedData);
/*      */                   }
/*      */                 }
/*  758 */                 TimedData data = (TimedData)dataLoadingThread.askThreadTimedData.peek();
/*  759 */                 time = data.getTime();
/*  760 */                 if (time != -9223372036854775808L) {
/*  761 */                   time = getNextTime(data, jForexPeriod);
/*  762 */                   if (time <= untilTime) {
/*  763 */                     TimedData askQueueTimedData = (TimedData)dataLoadingThread.askThreadTimedData.poll();
/*      */                     TimedData bidQueueTimedData;
/*      */                     TimedData bidQueueTimedData;
/*  766 */                     if (bidBlockingQueue != null) {
/*  767 */                       if (dataLoadingThread.bidThreadTimedData.isEmpty())
/*      */                       {
/*  769 */                         int read = bidBlockingQueue.drainTo(dataLoadingThread.bidThreadTimedData);
/*  770 */                         if (read == 0) {
/*      */                           TimedData timedData;
/*      */                           do
/*      */                             try {
/*  775 */                               timedData = (TimedData)bidBlockingQueue.take();
/*      */                             }
/*      */                             catch (InterruptedException e) {
/*  778 */                               timedData = null;
/*      */                             }
/*  780 */                           while ((timedData == null) && (!this.progressListener.stopJob()));
/*  781 */                           if ((timedData == null) && (this.progressListener.stopJob())) {
/*  782 */                             e = null;
/*      */                             return e;
/*      */                           }
/*  786 */                           dataLoadingThread.bidThreadTimedData.offer(timedData);
/*      */                         }
/*      */                       }
/*      */ 
/*  790 */                       bidQueueTimedData = (TimedData)dataLoadingThread.bidThreadTimedData.poll();
/*      */                     } else {
/*  792 */                       bidQueueTimedData = null;
/*      */                     }
/*      */ 
/*  795 */                     this.sortedData.add(new SortedDataItem(dataLoadingThread.askThread.getInstrument(), jForexPeriod, askQueueTimedData, bidQueueTimedData));
/*      */                   }
/*      */                   else {
/*  798 */                     moreDataAvailable = true;
/*      */                   }
/*      */                 } else {
/*  801 */                   this.dataLoadingThreads = removeElementByIndex(this.dataLoadingThreads, j);
/*  802 */                   j--;
/*      */                 }
/*      */               }
/*  804 */               while ((time != -9223372036854775808L) && (time <= untilTime));
/*      */             }
/*      */             else
/*      */             {
/*      */               long time;
/*      */               do
/*      */               {
/*  812 */                 if (askOrBidThreadTimeData.isEmpty())
/*      */                 {
/*  814 */                   int read = askOrBidBlockingQueue.drainTo(askOrBidThreadTimeData);
/*  815 */                   if (read == 0) {
/*      */                     TimedData timedData;
/*      */                     do
/*      */                       try {
/*  820 */                         timedData = (TimedData)askOrBidBlockingQueue.take();
/*      */                       }
/*      */                       catch (InterruptedException e) {
/*  823 */                         timedData = null;
/*      */                       }
/*  825 */                     while ((timedData == null) && (!this.progressListener.stopJob()));
/*      */ 
/*  827 */                     if ((timedData == null) && (this.progressListener.stopJob())) {
/*  828 */                       e = null;
/*      */                       return e;
/*      */                     }
/*  832 */                     askOrBidThreadTimeData.offer(timedData);
/*      */                   }
/*      */                 }
/*      */ 
/*  836 */                 TimedData data = (TimedData)askOrBidThreadTimeData.peek();
/*  837 */                 time = data.getTime();
/*  838 */                 if (time != -9223372036854775808L) {
/*  839 */                   time = getNextTime(data, jForexPeriod);
/*  840 */                   if (time <= untilTime) {
/*  841 */                     TimedData askQueueTimedData = (TimedData)askOrBidThreadTimeData.poll();
/*  842 */                     TimedData bidQueueTimedData = null;
/*      */ 
/*  844 */                     if (askBlockingQueue == null)
/*      */                     {
/*  848 */                       bidQueueTimedData = askQueueTimedData;
/*  849 */                       askQueueTimedData = null;
/*      */                     }
/*      */ 
/*  852 */                     this.sortedData.add(new SortedDataItem(askOrBidDataLoadingThread.getInstrument(), jForexPeriod, askQueueTimedData, bidQueueTimedData));
/*      */                   }
/*      */                   else {
/*  855 */                     moreDataAvailable = true;
/*      */                   }
/*      */                 } else {
/*  858 */                   this.dataLoadingThreads = removeElementByIndex(this.dataLoadingThreads, j);
/*  859 */                   j--;
/*      */                 }
/*      */               }
/*  861 */               while ((time != -9223372036854775808L) && (time <= untilTime));
/*      */             }
/*      */           }
/*  864 */           startTime += 600000L;
/*  865 */         }while ((this.sortedData.isEmpty()) && (moreDataAvailable));
/*  866 */         Collections.sort(this.sortedData, new SortedDataComparator());
/*      */       }
/*      */     } finally {
/*  869 */       perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.READ_DATA);
/*      */     }
/*      */ 
/*  872 */     if (this.sortedData.isEmpty()) {
/*  873 */       return null;
/*      */     }
/*      */ 
/*  876 */     SortedDataItem dataItem = (SortedDataItem)this.sortedData.remove(this.sortedData.size() - 1);
/*      */ 
/*  878 */     if (this.executionControl.isPaused()) {
/*  879 */       if ((dataItem != null) && (DataType.TICKS.equals(dataItem.getJForexPeriod().getDataType())))
/*  880 */         this.executionControl.tickProcessed();
/*      */     }
/*      */     else {
/*  883 */       this.executionControl.tickProcessed();
/*      */     }
/*      */ 
/*  886 */     if ((this.executionControl.getSpeed() != 7) && (!this.executionControl.isPaused())) {
/*  887 */       perfStatTimeStart = perfStartTime();
/*      */       try
/*      */       {
/*  890 */         while (!this.executionControl.isPaused())
/*      */         {
/*  894 */           long nanoTime = System.nanoTime();
/*  895 */           if (this.lastTickLocalTime == -9223372036854775808L) {
/*  896 */             this.lastTickLocalTime = (nanoTime - 10000000000L);
/*      */           }
/*  898 */           long thisTickTime = dataItem.getAskOrBidBarTime();
/*  899 */           long timeToSleep = -1L;
/*  900 */           switch (this.executionControl.getSpeed()) {
/*      */           case 0:
/*  902 */             timeToSleep = (thisTickTime - this.lastTickTime) * 2L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  903 */             break;
/*      */           case 1:
/*  905 */             timeToSleep = (thisTickTime - this.lastTickTime) * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  906 */             break;
/*      */           case 2:
/*  908 */             timeToSleep = (thisTickTime - this.lastTickTime) / 2L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  909 */             break;
/*      */           case 3:
/*  911 */             timeToSleep = (thisTickTime - this.lastTickTime) / 5L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  912 */             break;
/*      */           case 4:
/*  914 */             timeToSleep = (thisTickTime - this.lastTickTime) / 10L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  915 */             break;
/*      */           case 5:
/*  917 */             timeToSleep = (thisTickTime - this.lastTickTime) / 100L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  918 */             break;
/*      */           case 6:
/*  920 */             timeToSleep = (thisTickTime - this.lastTickTime) / 500L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*      */           }
/*      */ 
/*  923 */           if (timeToSleep > 0L) {
/*  924 */             if (timeToSleep <= 1000000000L) {
/*      */               try {
/*  926 */                 synchronized (this.sleepObject) {
/*  927 */                   this.sleepObject.wait(timeToSleep / 1000000L, (int)(timeToSleep % 1000000L));
/*      */                 }
/*      */               }
/*      */               catch (InterruptedException e) {
/*      */               }
/*  932 */               this.lastTickLocalTime = System.nanoTime();
/*  933 */               break;
/*      */             }
/*      */             try {
/*  936 */               synchronized (this.sleepObject) {
/*  937 */                 this.sleepObject.wait(1000L, 0);
/*      */               }
/*      */             }
/*      */             catch (InterruptedException e) {
/*      */             }
/*  942 */             if (this.progressListener.stopJob())
/*  943 */               break;
/*      */           }
/*      */           else
/*      */           {
/*  947 */             this.lastTickLocalTime = nanoTime;
/*      */ 
/*  949 */             break;
/*      */           }
/*      */         }
/*      */       } finally {
/*  953 */         perfStopTime(perfStatTimeStart, null);
/*      */       }
/*      */     }
/*  956 */     return dataItem;
/*      */   }
/*      */ 
/*      */   public void runUntilChange(ScienceWaitForUpdate waitForUpdate, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  963 */     long startTime = this.engine.getCurrentTime();
/*  964 */     long timeoutMillis = unit.toMillis(timeout);
/*      */ 
/*  966 */     long totalPerfStatTimeStart = perfStartTime();
/*      */     try {
/*  968 */       if (!isStrategyThread()) {
/*  969 */         if ((waitForUpdate.updated()) || (this.engine.getCurrentTime() - startTime > timeoutMillis)) {
/*  970 */           if (this.engine.getCurrentTime() - startTime > timeoutMillis) {
/*  971 */             LOGGER.debug("Exiting by timeout");
/*      */           }
/*      */           return;
/*      */         }
/*  976 */         while ((!this.context.isStopped()) && (!waitForUpdate.updated()) && (this.engine.getCurrentTime() - startTime < timeoutMillis)) {
/*  977 */           synchronized (this) {
/*  978 */             this.waitingThreads = true;
/*  979 */             waitForUpdate.wait();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  985 */       this.engine.doDelayedTasks();
/*  986 */       FutureTask[] tasks = this.context.getDelayedTasks();
/*  987 */       for (FutureTask futureTask : tasks) {
/*  988 */         long perfStatTimeStart = perfStartTime();
/*      */         try {
/*  990 */           futureTask.run();
/*      */         } finally {
/*  992 */           perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.USER_TASKS);
/*      */         }
/*      */       }
/*      */ 
/*  996 */       if ((waitForUpdate.updated()) || (this.engine.getCurrentTime() - startTime > timeoutMillis)) {
/*  997 */         if (this.engine.getCurrentTime() - startTime > timeoutMillis) {
/*  998 */           LOGGER.debug("Exiting by timeout");
/*      */         }
/*      */         return;
/*      */       }
/* 1003 */       while ((!this.progressListener.stopJob()) && (!this.context.isStopped()) && (this.engine.getCurrentTime() - startTime < timeoutMillis)) {
/* 1004 */         SortedDataItem dataItem = getNextThreadsWithTimedData(this.lastTickTime);
/* 1005 */         if (dataItem == null) break;
/* 1006 */         long tickProcessingPerfStatTimeStart = perfStartTime();
/*      */         try {
/*      */           try {
/* 1009 */             switch (1.$SwitchMap$com$dukascopy$api$DataType[dataItem.getJForexPeriod().getDataType().ordinal()]) {
/*      */             case 1:
/* 1011 */               historicalTickReceived(dataItem, this.strategy, this.testerFeedDataProvider, this.history, this.engine, this.testerReportData, this.instrumentsForConversion, false);
/* 1012 */               break;
/*      */             case 2:
/* 1015 */               historicalCandleReceived(dataItem, this.strategy, this.testerFeedDataProvider, this.history, this.engine, this.instrumentsForConversion, this.implementsOnBar, false);
/* 1016 */               break;
/*      */             case 3:
/* 1019 */               historicalPriceRangeReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1020 */               break;
/*      */             case 4:
/* 1023 */               historicalPointAndFigureReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1024 */               break;
/*      */             case 5:
/* 1027 */               historicalTickBarReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1028 */               break;
/*      */             case 6:
/* 1031 */               historicalRenkoReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1032 */               break;
/*      */             default:
/* 1035 */               throw new IllegalArgumentException("Unsupported Data Type - " + dataItem.getJForexPeriod().getDataType());
/*      */             }
/*      */ 
/* 1039 */             this.lastTickTime = dataItem.getAskOrBidBarTime();
/* 1040 */             checkAndFireDataLoaded();
/*      */           }
/*      */           catch (JFException e)
/*      */           {
/* 1045 */             handleException(e, "");
/*      */           }
/*      */         } finally {
/* 1048 */           perfStopTime(tickProcessingPerfStatTimeStart, ITesterReport.PerfStats.TICK_BAR_PROCESSING);
/*      */         }
/* 1050 */         this.engine.doDelayedTasks();
/* 1051 */         if (this.dataStorage != null) {
/* 1052 */           long perfStatTimeStart = perfStartTime();
/*      */           try {
/* 1054 */             this.dataStorage.put(this.lastTickTime, this.account.getDeposit(), this.account.getTotalProfitLoss(), this.account.getEquityActual());
/*      */           } finally {
/* 1056 */             perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.WRITE_DATA);
/*      */           }
/*      */         }
/* 1059 */         tasks = this.context.getDelayedTasks();
/* 1060 */         for (FutureTask futureTask : tasks) {
/* 1061 */           long perfStatTimeStart = perfStartTime();
/*      */           try {
/* 1063 */             futureTask.run();
/*      */           } finally {
/* 1065 */             perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.USER_TASKS);
/*      */           }
/*      */         }
/* 1068 */         if (this.waitingThreads) {
/* 1069 */           synchronized (this)
/*      */           {
/* 1071 */             notifyAll();
/*      */           }
/*      */         }
/* 1074 */         if (this.progressListener.stopJob()) { this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*      */           return; }
/* 1078 */         if (waitForUpdate.updated())
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1086 */       perfStopTime(totalPerfStatTimeStart, null);
/*      */     }
/*      */ 
/* 1089 */     if (this.engine.getCurrentTime() - startTime > timeoutMillis)
/* 1090 */       LOGGER.debug("Exiting by timeout");
/*      */   }
/*      */ 
/*      */   public void runUntilChange(ScienceWaitForUpdate waitForUpdate, long timeout, TimeUnit unit, IOrder.State[] expectedStates)
/*      */     throws InterruptedException, JFException
/*      */   {
/* 1100 */     List states = new ArrayList();
/* 1101 */     if ((expectedStates != null) && (expectedStates.length > 0)) {
/* 1102 */       for (IOrder.State expectedState : expectedStates) {
/* 1103 */         states.add(expectedState.name());
/*      */       }
/*      */     }
/* 1106 */     String[] statesToWait = (String[])states.toArray(new String[states.size()]);
/*      */ 
/* 1108 */     long startTime = this.engine.getCurrentTime();
/* 1109 */     long timeoutMillis = unit.toMillis(timeout);
/*      */ 
/* 1111 */     long totalPerfStatTimeStart = perfStartTime();
/*      */     try {
/* 1113 */       if (!isStrategyThread()) {
/* 1114 */         if ((waitForUpdate.updated(statesToWait)) || (this.engine.getCurrentTime() - startTime > timeoutMillis)) {
/* 1115 */           if (this.engine.getCurrentTime() - startTime > timeoutMillis) {
/* 1116 */             LOGGER.debug("Exiting by timeout");
/*      */           }
/*      */           return;
/*      */         }
/* 1121 */         while ((!this.context.isStopped()) && (!waitForUpdate.updated(statesToWait)) && (this.engine.getCurrentTime() - startTime < timeoutMillis)) {
/* 1122 */           synchronized (this) {
/* 1123 */             this.waitingThreads = true;
/* 1124 */             waitForUpdate.wait();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1130 */       this.engine.doDelayedTasks();
/* 1131 */       FutureTask[] tasks = this.context.getDelayedTasks();
/* 1132 */       for (FutureTask futureTask : tasks) {
/* 1133 */         long perfStatTimeStart = perfStartTime();
/*      */         try {
/* 1135 */           futureTask.run();
/*      */         } finally {
/* 1137 */           perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.USER_TASKS);
/*      */         }
/*      */       }
/*      */ 
/* 1141 */       if ((waitForUpdate.updated(statesToWait)) || (this.engine.getCurrentTime() - startTime > timeoutMillis)) {
/* 1142 */         if (this.engine.getCurrentTime() - startTime > timeoutMillis) {
/* 1143 */           LOGGER.debug("Exiting by timeout");
/*      */         }
/*      */         return;
/*      */       }
/* 1148 */       while ((!this.progressListener.stopJob()) && (!this.context.isStopped()) && (this.engine.getCurrentTime() - startTime < timeoutMillis)) {
/* 1149 */         SortedDataItem dataItem = getNextThreadsWithTimedData(this.lastTickTime);
/* 1150 */         if (dataItem == null) break;
/* 1151 */         long tickProcessingPerfStatTimeStart = perfStartTime();
/*      */         try {
/*      */           try {
/* 1154 */             switch (1.$SwitchMap$com$dukascopy$api$DataType[dataItem.getJForexPeriod().getDataType().ordinal()]) {
/*      */             case 1:
/* 1156 */               historicalTickReceived(dataItem, this.strategy, this.testerFeedDataProvider, this.history, this.engine, this.testerReportData, this.instrumentsForConversion, false);
/* 1157 */               break;
/*      */             case 2:
/* 1160 */               historicalCandleReceived(dataItem, this.strategy, this.testerFeedDataProvider, this.history, this.engine, this.instrumentsForConversion, this.implementsOnBar, false);
/* 1161 */               break;
/*      */             case 3:
/* 1164 */               historicalPriceRangeReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1165 */               break;
/*      */             case 4:
/* 1168 */               historicalPointAndFigureReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1169 */               break;
/*      */             case 5:
/* 1172 */               historicalTickBarReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1173 */               break;
/*      */             case 6:
/* 1176 */               historicalRenkoReceived(dataItem, this.strategy, this.instrumentsForConversion, false);
/* 1177 */               break;
/*      */             default:
/* 1180 */               throw new IllegalArgumentException("Unsupported Data Type - " + dataItem.getJForexPeriod().getDataType());
/*      */             }
/*      */ 
/* 1184 */             this.lastTickTime = dataItem.getAskOrBidBarTime();
/* 1185 */             checkAndFireDataLoaded();
/*      */           }
/*      */           catch (JFException e)
/*      */           {
/* 1190 */             handleException(e, "");
/*      */           }
/*      */         } finally {
/* 1193 */           perfStopTime(tickProcessingPerfStatTimeStart, ITesterReport.PerfStats.TICK_BAR_PROCESSING);
/*      */         }
/* 1195 */         this.engine.doDelayedTasks();
/* 1196 */         if (this.dataStorage != null) {
/* 1197 */           long perfStatTimeStart = perfStartTime();
/*      */           try {
/* 1199 */             this.dataStorage.put(this.lastTickTime, this.account.getDeposit(), this.account.getTotalProfitLoss(), this.account.getEquityActual());
/*      */           } finally {
/* 1201 */             perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.WRITE_DATA);
/*      */           }
/*      */         }
/* 1204 */         tasks = this.context.getDelayedTasks();
/* 1205 */         for (FutureTask futureTask : tasks) {
/* 1206 */           long perfStatTimeStart = perfStartTime();
/*      */           try {
/* 1208 */             futureTask.run();
/*      */           } finally {
/* 1210 */             perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.USER_TASKS);
/*      */           }
/*      */         }
/* 1213 */         if (this.waitingThreads) {
/* 1214 */           synchronized (this)
/*      */           {
/* 1216 */             notifyAll();
/*      */           }
/*      */         }
/* 1219 */         if (this.progressListener.stopJob()) { this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*      */           return; }
/* 1223 */         if (waitForUpdate.updated(statesToWait))
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1231 */       perfStopTime(totalPerfStatTimeStart, null);
/*      */     }
/*      */ 
/* 1234 */     if (this.engine.getCurrentTime() - startTime > timeoutMillis)
/* 1235 */       LOGGER.debug("Exiting by timeout");
/*      */   }
/*      */ 
/*      */   public TesterReportData getReportData()
/*      */   {
/* 1240 */     return this.testerReportData;
/*      */   }
/*      */ 
/*      */   public long perfStartTime()
/*      */   {
/* 1245 */     long perfStatTimeStart = 0L;
/* 1246 */     if (this.testerReportData.perfStats != null) {
/* 1247 */       perfStatTimeStart = System.nanoTime();
/* 1248 */       this.testerReportData.perfStackIndex += 1;
/* 1249 */       if (this.testerReportData.perfStackIndex < this.testerReportData.perfStack.length) {
/* 1250 */         this.testerReportData.perfStack[this.testerReportData.perfStackIndex] = 0L;
/*      */       }
/*      */     }
/* 1253 */     return perfStatTimeStart;
/*      */   }
/*      */ 
/*      */   public void perfStopTime(long perfStatTimeStart, ITesterReport.PerfStats perfStats)
/*      */   {
/* 1258 */     if (this.testerReportData.perfStats != null) {
/* 1259 */       long diff = System.nanoTime() - perfStatTimeStart + (this.testerReportData.perfStackIndex < this.testerReportData.perfStack.length ? this.testerReportData.perfStack[this.testerReportData.perfStackIndex] : 0L);
/* 1260 */       if (perfStats != null) {
/* 1261 */         this.testerReportData.perfStats[perfStats.ordinal()] += diff;
/* 1262 */         this.testerReportData.perfStatCounts[perfStats.ordinal()] += 1;
/*      */       }
/* 1264 */       this.testerReportData.perfStackIndex -= 1;
/* 1265 */       if ((this.testerReportData.perfStackIndex >= 0) && (this.testerReportData.perfStackIndex < this.testerReportData.perfStack.length))
/* 1266 */         this.testerReportData.perfStack[this.testerReportData.perfStackIndex] -= diff;
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getCurrentTime()
/*      */   {
/* 1272 */     return this.engine.getCurrentTime();
/*      */   }
/*      */ 
/*      */   public IStrategyDataStorage getTesterIndicatorStorage()
/*      */   {
/* 1281 */     return this.dataStorage;
/*      */   }
/*      */ 
/*      */   protected void setupTicksForInstrumentReportData(ITick firstTick, ITick lastTick, Instrument instrument)
/*      */   {
/* 1290 */     InstrumentReportData instrumentReportData = this.testerReportData.instrumentReportData[instrument.ordinal()];
/* 1291 */     if (instrumentReportData == null) {
/* 1292 */       instrumentReportData = new InstrumentReportData();
/* 1293 */       this.testerReportData.instrumentReportData[instrument.ordinal()] = instrumentReportData;
/*      */     }
/* 1295 */     instrumentReportData.firstTick = firstTick;
/* 1296 */     instrumentReportData.lastTick = lastTick;
/*      */   }
/*      */ 
/*      */   protected void handleException(Throwable e, String message)
/*      */   {
/* 1301 */     LOGGER.error(e.getMessage(), e);
/* 1302 */     String error = StrategyWrapper.representError(this.strategy, e);
/* 1303 */     this.notificationUtils.postErrorMessage(message + ": " + error, e, true);
/* 1304 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1305 */     event.type = TesterReportData.TesterEvent.EventType.EXCEPTION;
/* 1306 */     event.time = this.from;
/* 1307 */     event.text = error;
/* 1308 */     this.testerReportData.eventLog.add(event);
/*      */   }
/*      */ 
/*      */   private void checkAndFireDataLoaded() {
/* 1312 */     Date dateOfLastTickTime = new Date(this.lastTickTime);
/*      */ 
/* 1314 */     if (this.progressBarLastUpdate + this.progressBarUpdateInterval < this.lastTickTime) {
/* 1315 */       this.progressListener.dataLoaded(this.from, this.to, this.lastTickTime, "Running, " + getShortWeekDayName(this.lastTickTime) + " " + this.format.format(dateOfLastTickTime) + " Eq: " + this.decFormat.format(this.account.getEquityActual()) + " UoL: " + this.decFormat.format(this.account.getUseOfLeverageActual()) + "%");
/*      */ 
/* 1317 */       this.progressBarLastUpdate = (this.lastTickTime - this.lastTickTime % this.progressBarUpdateInterval);
/*      */     }
/*      */   }
/*      */ 
/*      */   public TesterAccount getAccount() {
/* 1322 */     return this.account;
/*      */   }
/*      */ 
/*      */   public TesterConfig getContext() {
/* 1326 */     return this.context;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   51 */     LOGGER = LoggerFactory.getLogger(StrategyRunner.class);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.StrategyRunner
 * JD-Core Version:    0.6.0
 */