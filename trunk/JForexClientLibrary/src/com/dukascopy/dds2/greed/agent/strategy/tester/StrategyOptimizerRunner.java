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
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataComparator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.util.SortedDataItem;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.util.StrategyOptimizerSet;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.LoggerNotificationUtils;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.FutureTask;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class StrategyOptimizerRunner extends AbstractStrategyRunner
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final long PROCESS_INTERVAL = 600000L;
/*      */   private final Map<Instrument, Double> minTradableAmounts;
/*      */   private List<StrategyOptimizerSet> strategyData;
/*      */   private boolean isFullAccessGranted;
/*   65 */   private boolean implementsOnBar = true;
/*   66 */   private List<SortedDataItem> sortedData = new ArrayList();
/*      */   private INotificationUtils notificationUtils;
/*      */   private ExecutionControl executionControl;
/*   70 */   private volatile boolean waitingThreads = false;
/*      */   private IStrategyExceptionHandler exceptionHandler;
/*      */   private long timeForProgressBar;
/*   74 */   private long lastTickLocalTime = -9223372036854775808L;
/*      */   private double dropDown;
/*   77 */   private List<StrategyOptimizerListener> listeners = new LinkedList();
/*      */ 
/*      */   public StrategyOptimizerRunner(List<IStrategy> strategies, boolean isFullAccessGranted, ITesterClient.DataLoadingMethod dataLoadingMethod, Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, long from, long to, Set<Instrument> instruments, Set<Instrument> instrumentsForConversion, LoadingProgressListener progressListener, Map<Instrument, Double> minTradableAmounts, List<? extends TesterAccount> accounts, ExecutionControl executionControl, IStrategyExceptionHandler exceptionHandler, double dropDown, List<? extends TesterReportData> reports)
/*      */   {
/*   98 */     super(period, offerSide, interpolationMethod, dataLoadingMethod, from, to, FeedDataProvider.getDefaultInstance(), instruments, progressListener, instrumentsForConversion);
/*      */ 
/*  111 */     this.strategyData = new ArrayList();
/*      */ 
/*  113 */     for (int i = 0; i < strategies.size(); i++) {
/*  114 */       StrategyOptimizerSet optimizerSet = new StrategyOptimizerSet();
/*  115 */       optimizerSet.strategy = ((IStrategy)strategies.get(i));
/*  116 */       optimizerSet.account = ((TesterAccount)accounts.get(i));
/*  117 */       optimizerSet.testerReport = ((ITesterReport)reports.get(i));
/*  118 */       optimizerSet.testerOrdersProvider = new TesterOrdersProvider();
/*      */ 
/*  120 */       this.strategyData.add(optimizerSet);
/*      */     }
/*      */ 
/*  123 */     this.isFullAccessGranted = isFullAccessGranted;
/*  124 */     this.progressListener = progressListener;
/*  125 */     this.notificationUtils = new LoggerNotificationUtils(LOGGER);
/*  126 */     this.minTradableAmounts = minTradableAmounts;
/*  127 */     this.executionControl = executionControl;
/*  128 */     this.exceptionHandler = exceptionHandler;
/*  129 */     this.dropDown = dropDown;
/*  130 */     assert (executionControl != null);
/*      */   }
/*      */ 
/*      */   public void addListener(StrategyOptimizerListener listener)
/*      */   {
/*  135 */     this.listeners.add(listener);
/*      */   }
/*      */ 
/*      */   public void removeListener(StrategyOptimizerListener listener) {
/*  139 */     this.listeners.remove(listener);
/*      */   }
/*      */ 
/*      */   protected void fireStrategyRemoved(IStrategy strategy) {
/*  143 */     StrategyOptimizerEvent event = new StrategyOptimizerEvent(this, strategy);
/*  144 */     for (StrategyOptimizerListener listener : this.listeners)
/*  145 */       listener.strategyRemoved(event);
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*  151 */     this.progressListener.dataLoaded(this.from, this.to, this.from, "Getting prices before start time");
/*  152 */     Map firstTicks = getFirstTicks();
/*      */ 
/*  154 */     if (this.progressListener.stopJob()) {
/*  155 */       this.progressListener.loadingFinished(true, this.from, this.to, this.from, null);
/*  156 */       return;
/*      */     }
/*      */ 
/*  159 */     this.progressListener.dataLoaded(this.from, this.to, this.from, "Creating data loading threads");
/*      */ 
/*  162 */     List dataThreads = createDataLoadingThreads();
/*  163 */     this.dataLoadingThreads = ((DataLoadingThreadsContainer[])dataThreads.toArray(new DataLoadingThreadsContainer[dataThreads.size()]));
/*      */ 
/*  165 */     this.progressListener.dataLoaded(this.from, this.to, this.from, "Getting prices before start time");
/*  166 */     firstTicks = updateFirstTicks(firstTicks);
/*      */ 
/*  168 */     if (firstTicks == null)
/*      */     {
/*  172 */       return;
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  177 */       for (StrategyOptimizerSet data : this.strategyData) {
/*  178 */         data.engine = new TesterCustodian(this.instruments, this.minTradableAmounts, this.notificationUtils, this.from, data.account, firstTicks, data.testerReport, this, data.testerOrdersProvider, this.exceptionHandler);
/*      */ 
/*  190 */         data.context = new TesterConfig(data.engine, this.notificationUtils, this.isFullAccessGranted, null, this.executionControl, data.testerOrdersProvider, this.progressListener, this, data.account, data.strategy, this.feedDataProvider);
/*      */ 
/*  203 */         data.history = ((TesterHistory)data.context.getHistory());
/*  204 */         data.history.setFirstTicks(firstTicks);
/*      */       }
/*      */ 
/*  207 */       this.progressListener.dataLoaded(this.from, this.to, this.from, "Filling in-progress candles");
/*  208 */       for (StrategyOptimizerSet data : this.strategyData) {
/*      */         try {
/*  210 */           data.history.fillCurrentCandles(this.instruments, this.feedDataProvider);
/*      */         } catch (DataCacheException e) {
/*  212 */           LOGGER.error(e.getMessage(), e);
/*      */         }
/*  214 */         data.engine.setStrategy(data.strategy);
/*      */       }
/*      */ 
/*  217 */       this.progressListener.dataLoaded(this.from, this.to, this.from, "Executing onStart");
/*  218 */       for (StrategyOptimizerSet data : this.strategyData)
/*      */         try
/*      */         {
/*  221 */           data.strategy.onStart(data.context);
/*      */         } catch (Throwable t) {
/*  223 */           handleException(t, "Strategy tester");
/*  224 */           this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_START, t);
/*  225 */           this.progressListener.loadingFinished(false, this.from, this.to, this.from, (t instanceof Exception) ? (Exception)t : null);
/*  226 */           stopThreads();
/*      */ 
/*  393 */           for (StrategyOptimizerSet data : this.strategyData) {
/*  394 */             data.testerReport.setFinishDeposit(data.account.getRealizedEquityWithCommissions());
/*      */           }
/*  396 */           stopThreads(); return;
/*      */         }
/*  231 */       this.progressListener.dataLoaded(this.from, this.to, this.from, "Running");
/*      */       try {
/*  233 */         if (this.progressListener.stopJob()) {
/*  234 */           this.progressListener.loadingFinished(true, this.from, this.to, this.from, null);
/*  235 */           stopThreads();
/*      */ 
/*  379 */           for (StrategyOptimizerSet data : this.strategyData)
/*      */           {
/*      */             try {
/*  382 */               data.strategy.onStop();
/*      */             } catch (Throwable t) {
/*  384 */               handleException(t, "Strategy tester");
/*  385 */               this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */             }
/*  387 */             data.engine.doDelayedTasks();
/*  388 */             data.engine.calculateTurnoverAndCommission();
/*      */           }
/*      */ 
/*  393 */           for (StrategyOptimizerSet data : this.strategyData) {
/*  394 */             data.testerReport.setFinishDeposit(data.account.getRealizedEquityWithCommissions());
/*      */           }
/*  396 */           stopThreads(); return;
/*      */         }
/*  240 */         this.timeForProgressBar = (this.from / 1800000L);
/*  241 */         this.lastTickTime = this.from;
/*  242 */         while (isContextRunning()) {
/*  243 */           SortedDataItem dataItem = getNextThreadsWithTimedData(this.lastTickTime);
/*  244 */           if (dataItem == null) break;
/*  245 */           long currentCandleTime = dataItem.getAskOrBidBarTime();
/*  246 */           currentCandleTime = getNextTime(dataItem.getAskOrBidData(), dataItem.getJForexPeriod());
/*  247 */           if (this.timeForProgressBar < currentCandleTime / 1800000L) {
/*  248 */             this.progressListener.dataLoaded(this.from, this.to, currentCandleTime, "Running");
/*  249 */             this.timeForProgressBar = (currentCandleTime / 1800000L);
/*      */           }
/*      */ 
/*  254 */           for (Iterator iter = this.strategyData.iterator(); iter.hasNext(); ) {
/*  255 */             StrategyOptimizerSet data = (StrategyOptimizerSet)iter.next();
/*      */ 
/*  257 */             IStrategy strategy = data.strategy;
/*  258 */             ITesterReport testerReportData = data.testerReport;
/*  259 */             TesterHistory history = data.history;
/*  260 */             TesterCustodian engine = data.engine;
/*  261 */             TesterConfig context = data.context;
/*      */ 
/*  263 */             boolean canceledByUser = false;
/*  264 */             synchronized (data.testerReport) {
/*  265 */               List events = data.testerReport.getEvents();
/*  266 */               for (TesterReportData.TesterEvent testerEvent : events) {
/*  267 */                 if (testerEvent.type == TesterReportData.TesterEvent.EventType.CANCELED_BY_USER) {
/*  268 */                   testerEvent.time = this.lastTickTime;
/*  269 */                   canceledByUser = true;
/*  270 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  275 */             if (canceledByUser) {
/*  276 */               iter.remove();
/*  277 */               continue;
/*      */             }
/*      */ 
/*  280 */             if (context.isStopped())
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/*  285 */             IStrategyExceptionHandler.Source source = IStrategyExceptionHandler.Source.ON_BAR;
/*      */             try {
/*  287 */               switch (1.$SwitchMap$com$dukascopy$api$DataType[dataItem.getJForexPeriod().getDataType().ordinal()]) {
/*      */               case 1:
/*  289 */                 source = IStrategyExceptionHandler.Source.ON_TICK;
/*  290 */                 historicalTickReceived(dataItem, strategy, null, history, engine, testerReportData, this.instrumentsForConversion, true);
/*  291 */                 break;
/*      */               case 2:
/*  294 */                 historicalCandleReceived(dataItem, strategy, null, history, engine, this.instrumentsForConversion, this.implementsOnBar, true);
/*  295 */                 break;
/*      */               case 3:
/*  298 */                 historicalPriceRangeReceived(dataItem, strategy, this.instrumentsForConversion, true);
/*  299 */                 break;
/*      */               case 4:
/*  302 */                 historicalPointAndFigureReceived(dataItem, strategy, this.instrumentsForConversion, true);
/*  303 */                 break;
/*      */               case 5:
/*  306 */                 historicalTickBarReceived(dataItem, strategy, this.instrumentsForConversion, true);
/*  307 */                 break;
/*      */               case 6:
/*  310 */                 historicalRenkoReceived(dataItem, strategy, this.instrumentsForConversion, true);
/*  311 */                 break;
/*      */               default:
/*  314 */                 throw new IllegalArgumentException("Unsupported Data Type - " + dataItem.getJForexPeriod().getDataType());
/*      */               }
/*      */             }
/*      */             catch (AbstractMethodError e) {
/*  318 */               this.implementsOnBar = false;
/*      */             } catch (Throwable t) {
/*  320 */               handleException(t, "Strategy tester");
/*  321 */               this.exceptionHandler.onException(1L, source, t);
/*      */             }
/*      */ 
/*  324 */             this.lastTickTime = currentCandleTime;
/*      */ 
/*  326 */             engine.doDelayedTasks();
/*  327 */             FutureTask[] tasks = context.getDelayedTasks();
/*  328 */             for (FutureTask futureTask : tasks)
/*      */             {
/*  330 */               futureTask.run();
/*      */             }
/*      */           }
/*      */ 
/*  334 */           if (this.waitingThreads) {
/*  335 */             synchronized (this)
/*      */             {
/*  337 */               notifyAll();
/*      */             }
/*      */           }
/*  340 */           if (this.progressListener.stopJob()) {
/*  341 */             this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*  342 */             stopThreads();
/*      */ 
/*  379 */             for (StrategyOptimizerSet data : this.strategyData)
/*      */             {
/*      */               try {
/*  382 */                 data.strategy.onStop();
/*      */               } catch (Throwable t) {
/*  384 */                 handleException(t, "Strategy tester");
/*  385 */                 this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */               }
/*  387 */               data.engine.doDelayedTasks();
/*  388 */               data.engine.calculateTurnoverAndCommission();
/*      */             }
/*      */ 
/*  393 */             for (StrategyOptimizerSet data : this.strategyData) {
/*  394 */               data.testerReport.setFinishDeposit(data.account.getRealizedEquityWithCommissions());
/*      */             }
/*  396 */             stopThreads(); return;
/*      */           }
/*  348 */           for (int i = this.strategyData.size() - 1; i >= 0; i--) {
/*  349 */             StrategyOptimizerSet data = (StrategyOptimizerSet)this.strategyData.get(i);
/*      */ 
/*  351 */             double equity = data.account.getEquity();
/*  352 */             double deposit = data.account.getDeposit();
/*      */ 
/*  355 */             if (deposit * (1.0D - this.dropDown) <= equity)
/*      */               continue;
/*  357 */             this.strategyData.remove(i);
/*  358 */             fireStrategyRemoved(data.strategy);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  367 */         for (StrategyOptimizerSet data : this.strategyData) {
/*  368 */           data.engine.sendAccountInfo();
/*      */         }
/*      */ 
/*  371 */         if (this.progressListener.stopJob()) {
/*  372 */           this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*  373 */           stopThreads();
/*      */ 
/*  379 */           for (StrategyOptimizerSet data : this.strategyData)
/*      */           {
/*      */             try {
/*  382 */               data.strategy.onStop();
/*      */             } catch (Throwable t) {
/*  384 */               handleException(t, "Strategy tester");
/*  385 */               this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */             }
/*  387 */             data.engine.doDelayedTasks();
/*  388 */             data.engine.calculateTurnoverAndCommission();
/*      */           }
/*      */ 
/*  393 */           for (StrategyOptimizerSet data : this.strategyData) {
/*  394 */             data.testerReport.setFinishDeposit(data.account.getRealizedEquityWithCommissions());
/*      */           }
/*  396 */           stopThreads(); return;
/*      */         }
/*  376 */         this.progressListener.dataLoaded(this.from, this.to, this.to, "Executing onStop");
/*      */       }
/*      */       finally
/*      */       {
/*      */         Iterator i$;
/*      */         StrategyOptimizerSet data;
/*  379 */         for (StrategyOptimizerSet data : this.strategyData)
/*      */         {
/*      */           try {
/*  382 */             data.strategy.onStop();
/*      */           } catch (Throwable t) {
/*  384 */             handleException(t, "Strategy tester");
/*  385 */             this.exceptionHandler.onException(1L, IStrategyExceptionHandler.Source.ON_STOP, t);
/*      */           }
/*  387 */           data.engine.doDelayedTasks();
/*  388 */           data.engine.calculateTurnoverAndCommission();
/*      */         }
/*      */       }
/*  391 */       this.progressListener.loadingFinished(true, this.from, this.to, this.to, null);
/*      */     }
/*      */     finally
/*      */     {
/*      */       Iterator i$;
/*      */       StrategyOptimizerSet data;
/*  393 */       for (StrategyOptimizerSet data : this.strategyData) {
/*  394 */         data.testerReport.setFinishDeposit(data.account.getRealizedEquityWithCommissions());
/*      */       }
/*  396 */       stopThreads();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isContextRunning() {
/*  401 */     for (StrategyOptimizerSet data : this.strategyData) {
/*  402 */       if (!data.context.isStopped()) {
/*  403 */         return true;
/*      */       }
/*      */     }
/*  406 */     return false;
/*      */   }
/*      */ 
/*      */   private SortedDataItem getNextThreadsWithTimedData(long startTime)
/*      */   {
/*  411 */     this.executionControl.waitForResume(this.progressListener);
/*  412 */     this.executionControl.tickProcessed();
/*  413 */     if (this.sortedData.isEmpty()) {
/*  414 */       boolean moreDataAvailable = false;
/*      */       do
/*      */       {
/*  417 */         boolean firstThreads = true;
/*  418 */         long untilTime = -9223372036854775808L;
/*  419 */         for (int j = 0; j < this.dataLoadingThreads.length; j++)
/*      */         {
/*  421 */           DataLoadingThreadsContainer dataLoadingThread = this.dataLoadingThreads[j];
/*      */ 
/*  423 */           BlockingQueue askBlockingQueue = dataLoadingThread.askThread != null ? dataLoadingThread.askThread.getQueue() : null;
/*  424 */           BlockingQueue bidBlockingQueue = dataLoadingThread.bidThread != null ? dataLoadingThread.bidThread.getQueue() : null;
/*      */ 
/*  426 */           BlockingQueue askOrBidBlockingQueue = askBlockingQueue != null ? askBlockingQueue : bidBlockingQueue;
/*  427 */           ArrayDeque askOrBitThreadTimeData = dataLoadingThread.askThread != null ? dataLoadingThread.askThreadTimedData : dataLoadingThread.bidThreadTimedData;
/*  428 */           IDataLoadingThread askOrBidDataLoadingThread = dataLoadingThread.askThread != null ? dataLoadingThread.askThread : dataLoadingThread.bidThread;
/*      */ 
/*  430 */           JForexPeriod jForexPeriod = askOrBidDataLoadingThread.getJForexPeriod();
/*      */ 
/*  432 */           if (firstThreads) { firstThreads = false;
/*  434 */             int i = 0;
/*      */             long time;
/*      */             do { if (dataLoadingThread.askThreadTimedData.isEmpty())
/*      */               {
/*  440 */                 int read = askBlockingQueue.drainTo(dataLoadingThread.askThreadTimedData);
/*  441 */                 if (read == 0) {
/*      */                   TimedData timedData;
/*      */                   do
/*      */                     try {
/*  446 */                       timedData = (TimedData)askBlockingQueue.take();
/*      */                     }
/*      */                     catch (InterruptedException e) {
/*  449 */                       timedData = null;
/*      */                     }
/*  451 */                   while ((timedData == null) && (!this.progressListener.stopJob()));
/*      */ 
/*  453 */                   if ((timedData == null) && (this.progressListener.stopJob())) {
/*  454 */                     return null;
/*      */                   }
/*      */ 
/*  458 */                   dataLoadingThread.askThreadTimedData.offer(timedData);
/*  459 */                   if (timedData.getTime() > startTime + 600000L)
/*      */                   {
/*  461 */                     moreDataAvailable = true;
/*  462 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/*  467 */               assert (dataLoadingThread.askThreadTimedData.peek() != null) : "something wrong here";
/*      */ 
/*  469 */               if (((TimedData)dataLoadingThread.askThreadTimedData.peek()).getTime() > startTime + 600000L)
/*      */               {
/*  471 */                 moreDataAvailable = true;
/*  472 */                 break;
/*      */               }
/*  474 */               TimedData askQueueTimedData = (TimedData)dataLoadingThread.askThreadTimedData.poll();
/*      */               TimedData bidQueueTimedData;
/*      */               TimedData bidQueueTimedData;
/*  477 */               if (bidBlockingQueue != null) {
/*  478 */                 if (dataLoadingThread.bidThreadTimedData.isEmpty())
/*      */                 {
/*  480 */                   int read = bidBlockingQueue.drainTo(dataLoadingThread.bidThreadTimedData);
/*  481 */                   if (read == 0) {
/*      */                     TimedData timedData;
/*      */                     do
/*      */                       try {
/*  486 */                         timedData = (TimedData)bidBlockingQueue.take();
/*      */                       }
/*      */                       catch (InterruptedException e) {
/*  489 */                         timedData = null;
/*      */                       }
/*  491 */                     while ((timedData == null) && (!this.progressListener.stopJob()));
/*  492 */                     if ((timedData == null) && (this.progressListener.stopJob())) {
/*  493 */                       return null;
/*      */                     }
/*      */ 
/*  497 */                     dataLoadingThread.bidThreadTimedData.offer(timedData);
/*      */                   }
/*      */                 }
/*      */ 
/*  501 */                 bidQueueTimedData = (TimedData)dataLoadingThread.bidThreadTimedData.poll();
/*      */               } else {
/*  503 */                 bidQueueTimedData = null;
/*      */               }
/*  505 */               time = askQueueTimedData.getTime();
/*  506 */               if (time != -9223372036854775808L) {
/*  507 */                 this.sortedData.add(new SortedDataItem(dataLoadingThread.askThread.getInstrument(), jForexPeriod, askQueueTimedData, bidQueueTimedData));
/*      */               } else {
/*  509 */                 this.dataLoadingThreads = removeElementByIndex(this.dataLoadingThreads, j);
/*  510 */                 j--;
/*      */               }
/*  512 */               i++; }
/*  513 */             while ((time != -9223372036854775808L) && (i < 100));
/*  514 */             if (!this.sortedData.isEmpty()) {
/*  515 */               SortedDataItem dataItem = (SortedDataItem)this.sortedData.get(this.sortedData.size() - 1);
/*  516 */               untilTime = dataItem.getAskOrBidBarTime();
/*  517 */               untilTime = getNextTime(dataItem.getAskOrBidData(), dataItem.getJForexPeriod());
/*      */             }
/*      */             else
/*      */             {
/*  521 */               untilTime = startTime + 600000L;
/*      */             }
/*      */           }
/*  524 */           else if ((askBlockingQueue != null) && (bidBlockingQueue != null))
/*      */           {
/*      */             long time;
/*      */             do
/*      */             {
/*  530 */               if (dataLoadingThread.askThreadTimedData.isEmpty())
/*      */               {
/*  532 */                 int read = askBlockingQueue.drainTo(dataLoadingThread.askThreadTimedData);
/*  533 */                 if (read == 0) {
/*      */                   TimedData timedData;
/*      */                   do
/*      */                     try {
/*  538 */                       timedData = (TimedData)askBlockingQueue.take();
/*      */                     }
/*      */                     catch (InterruptedException e) {
/*  541 */                       timedData = null;
/*      */                     }
/*  543 */                   while ((timedData == null) && (!this.progressListener.stopJob()));
/*      */ 
/*  545 */                   if ((timedData == null) && (this.progressListener.stopJob())) {
/*  546 */                     return null;
/*      */                   }
/*      */ 
/*  550 */                   dataLoadingThread.askThreadTimedData.offer(timedData);
/*      */                 }
/*      */               }
/*  553 */               TimedData data = (TimedData)dataLoadingThread.askThreadTimedData.peek();
/*  554 */               time = data.getTime();
/*      */ 
/*  556 */               if (time != -9223372036854775808L) {
/*  557 */                 time = getNextTime(data, jForexPeriod);
/*  558 */                 if (time <= untilTime) {
/*  559 */                   TimedData askQueueTimedData = (TimedData)dataLoadingThread.askThreadTimedData.poll();
/*      */                   TimedData bidQueueTimedData;
/*      */                   TimedData bidQueueTimedData;
/*  562 */                   if (bidBlockingQueue != null) {
/*  563 */                     if (dataLoadingThread.bidThreadTimedData.isEmpty())
/*      */                     {
/*  565 */                       int read = bidBlockingQueue.drainTo(dataLoadingThread.bidThreadTimedData);
/*  566 */                       if (read == 0) {
/*      */                         TimedData timedData;
/*      */                         do
/*      */                           try {
/*  571 */                             timedData = (TimedData)bidBlockingQueue.take();
/*      */                           }
/*      */                           catch (InterruptedException e) {
/*  574 */                             timedData = null;
/*      */                           }
/*  576 */                         while ((timedData == null) && (!this.progressListener.stopJob()));
/*  577 */                         if ((timedData == null) && (this.progressListener.stopJob())) {
/*  578 */                           return null;
/*      */                         }
/*      */ 
/*  582 */                         dataLoadingThread.bidThreadTimedData.offer(timedData);
/*      */                       }
/*      */                     }
/*      */ 
/*  586 */                     bidQueueTimedData = (TimedData)dataLoadingThread.bidThreadTimedData.poll();
/*      */                   } else {
/*  588 */                     bidQueueTimedData = null;
/*      */                   }
/*      */ 
/*  591 */                   this.sortedData.add(new SortedDataItem(dataLoadingThread.askThread.getInstrument(), jForexPeriod, askQueueTimedData, bidQueueTimedData));
/*      */                 }
/*      */                 else {
/*  594 */                   moreDataAvailable = true;
/*      */                 }
/*      */               } else {
/*  597 */                 this.dataLoadingThreads = removeElementByIndex(this.dataLoadingThreads, j);
/*  598 */                 j--;
/*      */               }
/*      */             }
/*  600 */             while ((time != -9223372036854775808L) && (time <= untilTime));
/*      */           } else {
/*      */             long time;
/*      */             do {
/*  605 */               if (askOrBitThreadTimeData.isEmpty())
/*      */               {
/*  607 */                 int read = askOrBidBlockingQueue.drainTo(askOrBitThreadTimeData);
/*  608 */                 if (read == 0) {
/*      */                   TimedData timedData;
/*      */                   do
/*      */                     try {
/*  613 */                       timedData = (TimedData)askOrBidBlockingQueue.take();
/*      */                     }
/*      */                     catch (InterruptedException e) {
/*  616 */                       timedData = null;
/*      */                     }
/*  618 */                   while ((timedData == null) && (!this.progressListener.stopJob()));
/*      */ 
/*  620 */                   if ((timedData == null) && (this.progressListener.stopJob())) {
/*  621 */                     return null;
/*      */                   }
/*      */ 
/*  625 */                   askOrBitThreadTimeData.offer(timedData);
/*      */                 }
/*      */               }
/*      */ 
/*  629 */               TimedData data = (TimedData)askOrBitThreadTimeData.peek();
/*  630 */               time = data.getTime();
/*      */ 
/*  632 */               if (time != -9223372036854775808L) {
/*  633 */                 time = getNextTime(data, jForexPeriod);
/*  634 */                 if (time <= untilTime) {
/*  635 */                   TimedData askQueueTimedData = (TimedData)askOrBitThreadTimeData.poll();
/*  636 */                   TimedData bidQueueTimedData = null;
/*      */ 
/*  638 */                   if (askBlockingQueue == null)
/*      */                   {
/*  642 */                     bidQueueTimedData = askQueueTimedData;
/*  643 */                     askQueueTimedData = null;
/*      */                   }
/*      */ 
/*  646 */                   this.sortedData.add(new SortedDataItem(askOrBidDataLoadingThread.getInstrument(), jForexPeriod, askQueueTimedData, bidQueueTimedData));
/*      */                 }
/*      */                 else {
/*  649 */                   moreDataAvailable = true;
/*      */                 }
/*      */               } else {
/*  652 */                 this.dataLoadingThreads = removeElementByIndex(this.dataLoadingThreads, j);
/*  653 */                 j--;
/*      */               }
/*      */             }
/*  655 */             while ((time != -9223372036854775808L) && (time <= untilTime));
/*      */           }
/*      */         }
/*  658 */         startTime += 600000L;
/*  659 */       }while ((this.sortedData.isEmpty()) && (moreDataAvailable));
/*      */ 
/*  661 */       Collections.sort(this.sortedData, new SortedDataComparator());
/*      */     }
/*      */ 
/*  664 */     if (this.sortedData.isEmpty()) {
/*  665 */       return null;
/*      */     }
/*      */ 
/*  668 */     SortedDataItem dataItem = (SortedDataItem)this.sortedData.remove(this.sortedData.size() - 1);
/*      */ 
/*  670 */     if (this.executionControl.getSpeed() != 7)
/*      */     {
/*  672 */       while (!this.executionControl.isPaused())
/*      */       {
/*  676 */         long nanoTime = System.nanoTime();
/*  677 */         if (this.lastTickLocalTime == -9223372036854775808L) {
/*  678 */           this.lastTickLocalTime = (nanoTime - 10000000000L);
/*      */         }
/*  680 */         long thisTickTime = dataItem.getAskOrBidBarTime();
/*  681 */         long timeToSleep = -1L;
/*  682 */         switch (this.executionControl.getSpeed()) {
/*      */         case 0:
/*  684 */           timeToSleep = (thisTickTime - this.lastTickTime) * 2L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  685 */           break;
/*      */         case 1:
/*  687 */           timeToSleep = (thisTickTime - this.lastTickTime) * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  688 */           break;
/*      */         case 2:
/*  690 */           timeToSleep = (thisTickTime - this.lastTickTime) / 2L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  691 */           break;
/*      */         case 3:
/*  693 */           timeToSleep = (thisTickTime - this.lastTickTime) / 5L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  694 */           break;
/*      */         case 4:
/*  696 */           timeToSleep = (thisTickTime - this.lastTickTime) / 10L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  697 */           break;
/*      */         case 5:
/*  699 */           timeToSleep = (thisTickTime - this.lastTickTime) / 100L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*  700 */           break;
/*      */         case 6:
/*  702 */           timeToSleep = (thisTickTime - this.lastTickTime) / 500L * 1000000L - (nanoTime - this.lastTickLocalTime);
/*      */         }
/*      */ 
/*  705 */         if (timeToSleep > 0L) {
/*  706 */           if (timeToSleep <= 1000000000L) {
/*      */             try {
/*  708 */               synchronized (this.sleepObject) {
/*  709 */                 this.sleepObject.wait(timeToSleep / 1000000L, (int)(timeToSleep % 1000000L));
/*      */               }
/*      */             }
/*      */             catch (InterruptedException e) {
/*      */             }
/*  714 */             this.lastTickLocalTime = System.nanoTime();
/*  715 */             break;
/*      */           }
/*      */           try {
/*  718 */             synchronized (this.sleepObject) {
/*  719 */               this.sleepObject.wait(1000L, 0);
/*      */             }
/*      */           }
/*      */           catch (InterruptedException e) {
/*      */           }
/*  724 */           if (this.progressListener.stopJob())
/*  725 */             break;
/*      */         }
/*      */         else
/*      */         {
/*  729 */           this.lastTickLocalTime = nanoTime;
/*      */ 
/*  731 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  735 */     return dataItem;
/*      */   }
/*      */ 
/*      */   public void runUntilChange(ScienceWaitForUpdate waitForUpdate, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  741 */     TesterCustodian engine = ((TesterOrder)waitForUpdate).getEngine();
/*  742 */     TesterConfig context = null;
/*  743 */     for (StrategyOptimizerSet data : this.strategyData) {
/*  744 */       if (data.engine == engine) {
/*  745 */         context = data.context;
/*  746 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  750 */     long startTime = engine.getCurrentTime();
/*  751 */     long timeoutMillis = unit.toMillis(timeout);
/*      */ 
/*  754 */     if (!isStrategyThread()) {
/*  755 */       if ((waitForUpdate.updated()) || (isTimeout(startTime, timeoutMillis))) {
/*  756 */         if (isTimeout(startTime, timeoutMillis)) {
/*  757 */           LOGGER.debug("Exiting by timeout");
/*      */         }
/*  759 */         return;
/*      */       }
/*      */ 
/*  762 */       while ((!context.isStopped()) && (!waitForUpdate.updated()) && (!isTimeout(startTime, timeoutMillis))) {
/*  763 */         synchronized (this) {
/*  764 */           this.waitingThreads = true;
/*  765 */           waitForUpdate.wait();
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  771 */     engine.doDelayedTasks();
/*  772 */     FutureTask[] tasks = context.getDelayedTasks();
/*  773 */     for (FutureTask futureTask : tasks)
/*      */     {
/*  775 */       futureTask.run();
/*      */     }
/*      */ 
/*  778 */     if ((waitForUpdate.updated()) || (isTimeout(startTime, timeoutMillis))) {
/*  779 */       if (isTimeout(startTime, timeoutMillis)) {
/*  780 */         LOGGER.debug("Exiting by timeout");
/*      */       }
/*  782 */       return;
/*      */     }
/*      */ 
/*  785 */     while ((!this.progressListener.stopJob()) && (isContextRunning()) && (!isTimeout(startTime, timeoutMillis))) {
/*  786 */       SortedDataItem dataItem = getNextThreadsWithTimedData(this.lastTickTime);
/*  787 */       if (dataItem == null)
/*      */         break;
/*  789 */       for (StrategyOptimizerSet data : this.strategyData) {
/*  790 */         ITesterReport testerReport = data.testerReport;
/*  791 */         TesterHistory testerHistory = data.history;
/*  792 */         TesterCustodian testerEngine = data.engine;
/*  793 */         TesterConfig testerContext = data.context;
/*  794 */         if (testerContext.isStopped())
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  801 */           DataType dataType = dataItem.getJForexPeriod().getDataType();
/*  802 */           switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*      */           case 1:
/*  804 */             historicalTickReceived(dataItem, null, null, testerHistory, testerEngine, testerReport, this.instrumentsForConversion, false);
/*  805 */             break;
/*      */           case 2:
/*  808 */             historicalCandleReceived(dataItem, null, null, testerHistory, testerEngine, this.instrumentsForConversion, this.implementsOnBar, false);
/*  809 */             break;
/*      */           case 3:
/*  812 */             historicalPriceRangeReceived(dataItem, null, this.instrumentsForConversion, false);
/*  813 */             break;
/*      */           case 4:
/*  816 */             historicalPointAndFigureReceived(dataItem, null, this.instrumentsForConversion, false);
/*  817 */             break;
/*      */           case 5:
/*  820 */             historicalTickBarReceived(dataItem, null, this.instrumentsForConversion, false);
/*  821 */             break;
/*      */           case 6:
/*  824 */             historicalRenkoReceived(dataItem, null, this.instrumentsForConversion, false);
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  836 */           handleException(t, "");
/*      */         }
/*      */ 
/*  839 */         this.lastTickTime = dataItem.getAskOrBidBarTime();
/*  840 */         if (this.timeForProgressBar < this.lastTickTime / 1800000L) {
/*  841 */           this.progressListener.dataLoaded(this.from, this.to, this.lastTickTime, "Running");
/*  842 */           this.timeForProgressBar = (this.lastTickTime / 1800000L);
/*      */         }
/*      */ 
/*  845 */         testerEngine.doDelayedTasks();
/*  846 */         tasks = testerContext.getDelayedTasks();
/*  847 */         for (FutureTask futureTask : tasks) {
/*      */           try
/*      */           {
/*  850 */             futureTask.run();
/*      */           }
/*      */           finally
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*  857 */       if (this.waitingThreads) {
/*  858 */         synchronized (this)
/*      */         {
/*  860 */           notifyAll();
/*      */         }
/*      */       }
/*  863 */       if (this.progressListener.stopJob()) {
/*  864 */         this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/*  865 */         return;
/*      */       }
/*  867 */       if (waitForUpdate.updated())
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  875 */     if (isTimeout(startTime, timeoutMillis))
/*  876 */       LOGGER.debug("Exiting by timeout");
/*      */   }
/*      */ 
/*      */   public void runUntilChange(ScienceWaitForUpdate waitForUpdate, long timeout, TimeUnit unit, IOrder.State[] expectedStates)
/*      */     throws InterruptedException, JFException
/*      */   {
/*  890 */     List states = new ArrayList();
/*  891 */     if ((expectedStates != null) && (expectedStates.length > 0)) {
/*  892 */       for (IOrder.State expectedState : expectedStates) {
/*  893 */         states.add(expectedState.name());
/*      */       }
/*      */     }
/*  896 */     String[] statesToWait = (String[])states.toArray(new String[states.size()]);
/*  897 */     TesterCustodian engine = ((TesterOrder)waitForUpdate).getEngine();
/*  898 */     TesterConfig context = null;
/*  899 */     for (StrategyOptimizerSet data : this.strategyData) {
/*  900 */       if (data.engine == engine) {
/*  901 */         context = data.context;
/*  902 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  906 */     long startTime = engine.getCurrentTime();
/*  907 */     long timeoutMillis = unit.toMillis(timeout);
/*      */ 
/*  910 */     if (!isStrategyThread()) {
/*  911 */       if ((waitForUpdate.updated(statesToWait)) || (isTimeout(startTime, timeoutMillis))) {
/*  912 */         if (isTimeout(startTime, timeoutMillis)) {
/*  913 */           LOGGER.debug("Exiting by timeout");
/*      */         }
/*  915 */         return;
/*      */       }
/*      */ 
/*  918 */       while ((!context.isStopped()) && (!waitForUpdate.updated(statesToWait)) && (!isTimeout(startTime, timeoutMillis))) {
/*  919 */         synchronized (this) {
/*  920 */           this.waitingThreads = true;
/*  921 */           waitForUpdate.wait();
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  927 */     engine.doDelayedTasks();
/*  928 */     FutureTask[] tasks = context.getDelayedTasks();
/*  929 */     for (FutureTask futureTask : tasks)
/*      */     {
/*  931 */       futureTask.run();
/*      */     }
/*      */ 
/*  934 */     if ((waitForUpdate.updated(statesToWait)) || (isTimeout(startTime, timeoutMillis))) {
/*  935 */       if (isTimeout(startTime, timeoutMillis)) {
/*  936 */         LOGGER.debug("Exiting by timeout");
/*      */       }
/*  938 */       return;
/*      */     }
/*      */ 
/*  941 */     while ((!this.progressListener.stopJob()) && (isContextRunning()) && (!isTimeout(startTime, timeoutMillis))) {
/*  942 */       SortedDataItem dataItem = getNextThreadsWithTimedData(this.lastTickTime);
/*  943 */       if (dataItem == null)
/*      */         break;
/*  945 */       for (StrategyOptimizerSet data : this.strategyData) {
/*  946 */         ITesterReport testerReport = data.testerReport;
/*  947 */         TesterHistory testerHistory = data.history;
/*  948 */         TesterCustodian testerEngine = data.engine;
/*  949 */         TesterConfig testerContext = data.context;
/*  950 */         if (testerContext.isStopped())
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  957 */           DataType dataType = dataItem.getJForexPeriod().getDataType();
/*  958 */           switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*      */           case 1:
/*  960 */             historicalTickReceived(dataItem, null, null, testerHistory, testerEngine, testerReport, this.instrumentsForConversion, false);
/*  961 */             break;
/*      */           case 2:
/*  964 */             historicalCandleReceived(dataItem, null, null, testerHistory, testerEngine, this.instrumentsForConversion, this.implementsOnBar, false);
/*  965 */             break;
/*      */           case 3:
/*  968 */             historicalPriceRangeReceived(dataItem, null, this.instrumentsForConversion, false);
/*  969 */             break;
/*      */           case 4:
/*  972 */             historicalPointAndFigureReceived(dataItem, null, this.instrumentsForConversion, false);
/*  973 */             break;
/*      */           case 5:
/*  976 */             historicalTickBarReceived(dataItem, null, this.instrumentsForConversion, false);
/*  977 */             break;
/*      */           case 6:
/*  980 */             historicalRenkoReceived(dataItem, null, this.instrumentsForConversion, false);
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  992 */           handleException(t, "");
/*      */         }
/*      */ 
/*  995 */         this.lastTickTime = dataItem.getAskOrBidBarTime();
/*  996 */         if (this.timeForProgressBar < this.lastTickTime / 1800000L) {
/*  997 */           this.progressListener.dataLoaded(this.from, this.to, this.lastTickTime, "Running");
/*  998 */           this.timeForProgressBar = (this.lastTickTime / 1800000L);
/*      */         }
/*      */ 
/* 1001 */         testerEngine.doDelayedTasks();
/* 1002 */         tasks = testerContext.getDelayedTasks();
/* 1003 */         for (FutureTask futureTask : tasks) {
/*      */           try
/*      */           {
/* 1006 */             futureTask.run();
/*      */           }
/*      */           finally
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/* 1013 */       if (this.waitingThreads) {
/* 1014 */         synchronized (this)
/*      */         {
/* 1016 */           notifyAll();
/*      */         }
/*      */       }
/* 1019 */       if (this.progressListener.stopJob()) {
/* 1020 */         this.progressListener.loadingFinished(true, this.from, this.to, this.lastTickTime, null);
/* 1021 */         return;
/*      */       }
/* 1023 */       if (waitForUpdate.updated(statesToWait))
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1031 */     if (isTimeout(startTime, timeoutMillis))
/* 1032 */       LOGGER.debug("Exiting by timeout");
/*      */   }
/*      */ 
/*      */   private boolean isTimeout(long startTime, long timeOut)
/*      */   {
/* 1037 */     if (this.strategyData.size() > 0) {
/* 1038 */       TesterCustodian engine = ((StrategyOptimizerSet)this.strategyData.get(0)).engine;
/* 1039 */       return engine.getCurrentTime() - startTime > timeOut;
/*      */     }
/* 1041 */     return true;
/*      */   }
/*      */ 
/*      */   public long perfStartTime()
/*      */   {
/* 1047 */     return 0L;
/*      */   }
/*      */ 
/*      */   public void perfStopTime(long perfStatTimeStart, ITesterReport.PerfStats perfStats)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void setupTicksForInstrumentReportData(ITick firstTick, ITick lastTick, Instrument instrument)
/*      */   {
/* 1061 */     for (StrategyOptimizerSet data : this.strategyData) {
/* 1062 */       InstrumentReportData instrumentReportData = data.testerReport.getOrCreateInstrumentReportData(instrument);
/* 1063 */       instrumentReportData.firstTick = lastTick;
/* 1064 */       instrumentReportData.lastTick = lastTick;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleException(Throwable e, String message)
/*      */   {
/* 1070 */     LOGGER.error(e.getMessage(), e);
/* 1071 */     StrategyOptimizerSet data = (StrategyOptimizerSet)this.strategyData.get(0);
/* 1072 */     String error = StrategyWrapper.representError(data.strategy, e);
/* 1073 */     this.notificationUtils.postErrorMessage(message + ": " + error, e, true);
/* 1074 */     TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 1075 */     event.type = TesterReportData.TesterEvent.EventType.EXCEPTION;
/* 1076 */     event.time = this.from;
/* 1077 */     event.text = error;
/* 1078 */     data.testerReport.addEvent(event);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   56 */     LOGGER = LoggerFactory.getLogger(StrategyOptimizerRunner.class);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.StrategyOptimizerRunner
 * JD-Core Version:    0.6.0
 */