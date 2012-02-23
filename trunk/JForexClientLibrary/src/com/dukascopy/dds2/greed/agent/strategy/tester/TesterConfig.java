/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IConsole;
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IDataService;
/*     */ import com.dukascopy.api.IDownloadableStrategies;
/*     */ import com.dukascopy.api.IEngine;
/*     */ import com.dukascopy.api.IHistory;
/*     */ import com.dukascopy.api.IIndicators;
/*     */ import com.dukascopy.api.IStrategies;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.IUserInterface;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.JFUtils;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.feed.IBarFeedListener;
/*     */ import com.dukascopy.api.feed.IPointAndFigureFeedListener;
/*     */ import com.dukascopy.api.feed.IRangeBarFeedListener;
/*     */ import com.dukascopy.api.feed.IRenkoBarFeedListener;
/*     */ import com.dukascopy.api.feed.ITickBarFeedListener;
/*     */ import com.dukascopy.api.impl.DownloadableStrategies;
/*     */ import com.dukascopy.api.impl.NotificationConsoleImpl;
/*     */ import com.dukascopy.api.impl.connect.JForexDataService;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.wrapper.DelegatableChartWrapper;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.IStrategyRateDataNotificationFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.StrategyRateDataNotificationFactory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.candle.IStrategyCandleNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.pnf.IStrategyPointAndFigureNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.pr.IStrategyPriceRangeNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.renko.IStrategyRenkoNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.IStrategyTickBarNotificationManager;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.JForexUtils;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.FutureTask;
/*     */ 
/*     */ public class TesterConfig
/*     */   implements IContext
/*     */ {
/*  62 */   private static final FutureTask<?>[] EMPTY_DELAYED_TASKS = new FutureTask[0];
/*     */   private TesterHistory testerHistory;
/*     */   private IEngine engine;
/*     */   private IIndicators indicators;
/*     */   private boolean stop;
/*     */   private LoadingProgressListener progressListener;
/*     */   private INotificationUtils notificationUtils;
/*     */   private boolean isFullAccessGranted;
/*     */   private IConsole console;
/*  72 */   private List<FutureTask<?>> delayedTasks = new ArrayList();
/*     */   private Map<Instrument, TesterChartData> chartPanels;
/*     */   private File filesDir;
/*     */   private ExecutionControl executionControl;
/*     */   private IStrategyRunner strategyRunner;
/*     */   private IAccount account;
/*     */   private IStrategy strategy;
/*     */   private JForexUtils jfUtils;
/*     */   private IDataService dataService;
/*     */   private IDownloadableStrategies downloadableStrategies;
/*  87 */   private final Map<IChart, DelegatableChartWrapper> delegatableChartWrappers = new HashMap();
/*     */ 
/*     */   public TesterConfig(TesterCustodian engine, INotificationUtils notificationUtils, boolean isFullAccessGranted, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, TesterOrdersProvider ordersProvider, LoadingProgressListener progressListener, IStrategyRunner strategyRunner, IAccount account, IStrategy strategy, IFeedDataProvider feedDataProvider)
/*     */   {
/* 103 */     this.strategyRunner = strategyRunner;
/* 104 */     this.testerHistory = new TesterHistory(ordersProvider, strategyRunner, account.getCurrency(), engine.getCurrencyConverter());
/* 105 */     this.indicators = new TesterIndicators(this.testerHistory, strategyRunner);
/* 106 */     this.engine = engine;
/* 107 */     this.notificationUtils = notificationUtils;
/* 108 */     this.isFullAccessGranted = isFullAccessGranted;
/* 109 */     this.console = new NotificationConsoleImpl(notificationUtils);
/* 110 */     this.chartPanels = chartPanels;
/* 111 */     this.filesDir = FilePathManager.getInstance().getFilesForStrategiesDir();
/* 112 */     this.executionControl = executionControl;
/* 113 */     this.progressListener = progressListener;
/* 114 */     this.account = account;
/* 115 */     this.strategy = strategy;
/* 116 */     this.jfUtils = new JForexUtils(this.testerHistory, this);
/* 117 */     this.dataService = new JForexDataService(feedDataProvider);
/* 118 */     this.downloadableStrategies = new DownloadableStrategies();
/*     */   }
/*     */ 
/*     */   public TesterConfig(TesterCustodian engine, INotificationUtils notificationUtils, boolean isFullAccessGranted, Map<Instrument, TesterChartData> chartPanels, ExecutionControl executionControl, TesterOrdersProvider ordersProvider, LoadingProgressListener progressListener, IStrategyRunner strategyRunner, IAccount account, IStrategy strategy, TesterHistory history)
/*     */   {
/* 134 */     this.strategyRunner = strategyRunner;
/* 135 */     this.testerHistory = history;
/* 136 */     this.indicators = new TesterIndicators(this.testerHistory, strategyRunner);
/* 137 */     this.engine = engine;
/* 138 */     this.notificationUtils = notificationUtils;
/* 139 */     this.isFullAccessGranted = isFullAccessGranted;
/* 140 */     this.console = new NotificationConsoleImpl(notificationUtils);
/* 141 */     this.chartPanels = chartPanels;
/* 142 */     this.filesDir = FilePathManager.getInstance().getFilesForStrategiesDir();
/* 143 */     this.executionControl = executionControl;
/* 144 */     this.progressListener = progressListener;
/* 145 */     this.account = account;
/* 146 */     this.strategy = strategy;
/* 147 */     this.jfUtils = new JForexUtils(this.testerHistory, this);
/* 148 */     this.downloadableStrategies = new DownloadableStrategies();
/*     */   }
/*     */ 
/*     */   public IChart getChart(Instrument instrument) {
/* 152 */     if (this.chartPanels != null) {
/* 153 */       TesterChartData chartObjects = (TesterChartData)this.chartPanels.get(instrument);
/* 154 */       if (chartObjects != null) {
/* 155 */         return getOrCreate(chartObjects.chart);
/*     */       }
/*     */     }
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   public Set<IChart> getCharts(Instrument instrument) {
/* 162 */     if (this.chartPanels != null) {
/* 163 */       TesterChartData chartObjects = (TesterChartData)this.chartPanels.get(instrument);
/* 164 */       if (chartObjects != null) {
/* 165 */         return Collections.singleton(getOrCreate(chartObjects.chart));
/*     */       }
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   public IChart getLastActiveChart()
/*     */   {
/* 174 */     for (Instrument instr : this.chartPanels.keySet()) {
/* 175 */       if (this.chartPanels.get(instr) != null)
/* 176 */         return ((TesterChartData)this.chartPanels.get(instr)).chart;
/*     */     }
/* 178 */     return null;
/*     */   }
/*     */ 
/*     */   public IUserInterface getUserInterface() {
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */   public IConsole getConsole() {
/* 186 */     return this.console;
/*     */   }
/*     */ 
/*     */   public IEngine getEngine() {
/* 190 */     return this.engine;
/*     */   }
/*     */ 
/*     */   public IHistory getHistory() {
/* 194 */     return this.testerHistory;
/*     */   }
/*     */ 
/*     */   public <T> Future<T> executeTask(Callable<T> callable) {
/* 198 */     FutureTask futureTask = new FutureTask(callable);
/* 199 */     synchronized (this.delayedTasks) {
/* 200 */       this.delayedTasks.add(futureTask);
/*     */     }
/* 202 */     return futureTask;
/*     */   }
/*     */ 
/*     */   public FutureTask<?>[] getDelayedTasks() {
/* 206 */     synchronized (this.delayedTasks) {
/* 207 */       if (this.delayedTasks.isEmpty()) {
/* 208 */         return EMPTY_DELAYED_TASKS;
/*     */       }
/* 210 */       return (FutureTask[])this.delayedTasks.toArray(new FutureTask[this.delayedTasks.size()]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop() {
/* 215 */     this.stop = true;
/*     */   }
/*     */ 
/*     */   public IIndicators getIndicators() {
/* 219 */     return this.indicators;
/*     */   }
/*     */ 
/*     */   public IDownloadableStrategies getDownloadableStrategies()
/*     */   {
/* 224 */     return this.downloadableStrategies;
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessGranted() {
/* 228 */     return this.isFullAccessGranted;
/*     */   }
/*     */ 
/*     */   public boolean isStopped() {
/* 232 */     return (this.stop) || (this.progressListener.stopJob());
/*     */   }
/*     */ 
/*     */   public File getFilesDir()
/*     */   {
/* 237 */     return this.filesDir;
/*     */   }
/*     */ 
/*     */   public void pause() {
/* 241 */     this.executionControl.pause();
/*     */   }
/*     */ 
/*     */   public void setSubscribedInstruments(Set<Instrument> instruments)
/*     */   {
/* 246 */     if (!this.strategyRunner.getInstruments().containsAll(instruments)) {
/* 247 */       this.console.getErr().println("One of the instruments requested by the strategy is not selected");
/* 248 */       this.stop = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSubscribedInstruments()
/*     */   {
/* 254 */     return Collections.unmodifiableSet(this.strategyRunner.getInstruments());
/*     */   }
/*     */ 
/*     */   public IAccount getAccount()
/*     */   {
/* 259 */     return this.account;
/*     */   }
/*     */ 
/*     */   public void subscribeToBarsFeed(Instrument instrument, Period period, OfferSide offerSide, IBarFeedListener listener)
/*     */   {
/* 269 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, period);
/*     */     try
/*     */     {
/* 275 */       AccessController.doPrivileged(new PrivilegedExceptionAction(period, instrument, jForexPeriod, offerSide, listener) {
/*     */         public Object run() throws Exception {
/* 277 */           TesterConfig.this.testerHistory.addInProgressCandlePeriod(this.val$period);
/*     */ 
/* 279 */           StrategyRateDataNotificationFactory.getIsntance().getCandleNotificationManager().subscribeToBarsFeedForHistoricalTester(TesterConfig.this.strategy, TesterConfig.this.strategyRunner, this.val$instrument, this.val$jForexPeriod, this.val$offerSide, this.val$listener, null, null, null, TesterConfig.this.notificationUtils);
/*     */ 
/* 291 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/* 295 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromBarsFeed(IBarFeedListener listener)
/*     */   {
/* 303 */     StrategyRateDataNotificationFactory.getIsntance().getCandleNotificationManager().unsubscribeFromBarsFeedForHistoricalTester(this.strategy, this.strategyRunner, listener);
/*     */   }
/*     */ 
/*     */   public IStrategies getStrategies()
/*     */   {
/* 312 */     return new IStrategies()
/*     */     {
/*     */       public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException {
/* 315 */         throw new JFException("Not supported by tester");
/*     */       }
/*     */ 
/*     */       public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException
/*     */       {
/* 320 */         throw new JFException("Not supported by tester");
/*     */       }
/*     */ 
/*     */       public void stopStrategy(long strategyId) throws JFException
/*     */       {
/* 325 */         throw new JFException("Not supported by tester");
/*     */       }
/*     */ 
/*     */       public void stopAll()
/*     */         throws JFException
/*     */       {
/* 333 */         throw new JFException("Not supported by tester");
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void subscribeToPointAndFigureFeed(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, IPointAndFigureFeedListener listener)
/*     */   {
/* 346 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE, Period.TICK, priceRange, reversalAmount);
/*     */     try
/*     */     {
/* 354 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, jForexPeriod, offerSide, listener) {
/*     */         public Object run() throws Exception {
/* 356 */           StrategyRateDataNotificationFactory.getIsntance().getPointAndFigureNotificationManager().subscribeToBarsFeedForHistoricalTester(TesterConfig.this.strategy, TesterConfig.this.strategyRunner, this.val$instrument, this.val$jForexPeriod, this.val$offerSide, this.val$listener, null, null, null, TesterConfig.this.notificationUtils);
/*     */ 
/* 368 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/* 372 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void subscribeToRangeBarFeed(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IRangeBarFeedListener listener)
/*     */   {
/* 383 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.PRICE_RANGE_AGGREGATION, Period.TICK, priceRange);
/*     */     try
/*     */     {
/* 390 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, jForexPeriod, offerSide, listener) {
/*     */         public Object run() throws Exception {
/* 392 */           StrategyRateDataNotificationFactory.getIsntance().getPriceRangeNotificationManager().subscribeToBarsFeedForHistoricalTester(TesterConfig.this.strategy, TesterConfig.this.strategyRunner, this.val$instrument, this.val$jForexPeriod, this.val$offerSide, this.val$listener, null, null, null, TesterConfig.this.notificationUtils);
/*     */ 
/* 404 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/* 408 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void subscribeToRenkoBarFeed(Instrument instrument, OfferSide offerSide, PriceRange brickSize, IRenkoBarFeedListener listener)
/*     */   {
/* 419 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.RENKO, Period.TICK, brickSize);
/*     */     try
/*     */     {
/* 426 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, jForexPeriod, offerSide, listener) {
/*     */         public Object run() throws Exception {
/* 428 */           StrategyRateDataNotificationFactory.getIsntance().getRenkoNotificationManager().subscribeToBarsFeedForHistoricalTester(TesterConfig.this.strategy, TesterConfig.this.strategyRunner, this.val$instrument, this.val$jForexPeriod, this.val$offerSide, this.val$listener, null, null, null, TesterConfig.this.notificationUtils);
/*     */ 
/* 440 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/* 444 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void subscribeToTickBarFeed(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarFeedListener listener)
/*     */   {
/* 457 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.TICK_BAR, Period.TICK, null, null, tickBarSize);
/*     */     try
/*     */     {
/* 467 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instrument, jForexPeriod, offerSide, listener) {
/*     */         public Object run() throws Exception {
/* 469 */           StrategyRateDataNotificationFactory.getIsntance().getTickBarNotificationManager().subscribeToBarsFeedForHistoricalTester(TesterConfig.this.strategy, TesterConfig.this.strategyRunner, this.val$instrument, this.val$jForexPeriod, this.val$offerSide, this.val$listener, null, null, null, TesterConfig.this.notificationUtils);
/*     */ 
/* 481 */           return null;
/*     */         } } );
/*     */     } catch (PrivilegedActionException e) {
/* 485 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromPointAndFigureFeed(IPointAndFigureFeedListener listener)
/*     */   {
/* 491 */     StrategyRateDataNotificationFactory.getIsntance().getPointAndFigureNotificationManager().unsubscribeFromBarsFeedForHistoricalTester(this.strategy, this.strategyRunner, listener);
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromRangeBarFeed(IRangeBarFeedListener listener)
/*     */   {
/* 500 */     StrategyRateDataNotificationFactory.getIsntance().getPriceRangeNotificationManager().unsubscribeFromBarsFeedForHistoricalTester(this.strategy, this.strategyRunner, listener);
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromTickBarFeed(ITickBarFeedListener listener)
/*     */   {
/* 509 */     StrategyRateDataNotificationFactory.getIsntance().getTickBarNotificationManager().unsubscribeFromBarsFeedForHistoricalTester(this.strategy, this.strategyRunner, listener);
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromRenkoBarFeed(IRenkoBarFeedListener listener)
/*     */   {
/* 518 */     StrategyRateDataNotificationFactory.getIsntance().getRenkoNotificationManager().unsubscribeFromBarsFeedForHistoricalTester(this.strategy, this.strategyRunner, listener);
/*     */   }
/*     */ 
/*     */   private IChart getOrCreate(IChart chart)
/*     */   {
/* 527 */     DelegatableChartWrapper wrapper = (DelegatableChartWrapper)this.delegatableChartWrappers.get(chart);
/* 528 */     if (wrapper == null)
/*     */     {
/* 532 */       wrapper = new DelegatableChartWrapper(chart, this.strategy.getClass().getName());
/*     */ 
/* 536 */       this.delegatableChartWrappers.put(chart, wrapper);
/*     */     }
/* 538 */     return wrapper;
/*     */   }
/*     */ 
/*     */   public void addConfigurationChangeListener(String parameter, PropertyChangeListener listener)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void removeConfigurationChangeListener(String parameter, PropertyChangeListener listener)
/*     */   {
/*     */   }
/*     */ 
/*     */   public JFUtils getUtils()
/*     */   {
/* 563 */     return this.jfUtils;
/*     */   }
/*     */ 
/*     */   public IDataService getDataService()
/*     */   {
/* 571 */     return this.dataService;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterConfig
 * JD-Core Version:    0.6.0
 */