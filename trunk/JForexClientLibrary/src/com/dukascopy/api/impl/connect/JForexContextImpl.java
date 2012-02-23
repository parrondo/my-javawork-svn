/*     */ package com.dukascopy.api.impl.connect;
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
/*     */ import com.dukascopy.api.IUserInterface;
/*     */ import com.dukascopy.api.Instrument;
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
/*     */ import com.dukascopy.api.impl.History;
/*     */ import com.dukascopy.api.impl.Indicators;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
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
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ 
/*     */ public class JForexContextImpl
/*     */   implements IContext
/*     */ {
/*  60 */   private StrategyProcessor strategyProcessor = null;
/*  61 */   private IEngine forexEngineImpl = null;
/*  62 */   private History history = null;
/*     */   private IConsole console;
/*     */   private DDSChartsController chartsController;
/*     */   private IUserInterface userInterface;
/*     */   private IIndicators indicators;
/*     */   private IStrategies strategiesControl;
/*     */   private JForexUtils jfUtils;
/*     */   private IDataService dataService;
/*     */   private IDownloadableStrategies downloadableStrategies;
/*     */   private File filesDir;
/*  78 */   private final Map<IChart, DelegatableChartWrapper> delegatableChartWrappers = new HashMap();
/*     */ 
/*     */   public JForexContextImpl(StrategyProcessor strategyProcessor, IEngine forexEngineImpl, History history, IConsole console, DDSChartsController chartsController, IUserInterface userInterface, IStrategies strategiesControl)
/*     */   {
/*  89 */     this.strategyProcessor = strategyProcessor;
/*  90 */     this.forexEngineImpl = forexEngineImpl;
/*  91 */     this.history = history;
/*  92 */     this.console = console;
/*  93 */     this.filesDir = FilePathManager.getInstance().getFilesForStrategiesDir();
/*  94 */     this.chartsController = chartsController;
/*  95 */     this.userInterface = userInterface;
/*  96 */     this.strategiesControl = strategiesControl;
/*  97 */     this.jfUtils = new JForexUtils(history, this);
/*  98 */     this.dataService = new JForexDataService(FeedDataProvider.getDefaultInstance());
/*  99 */     this.downloadableStrategies = new DownloadableStrategies();
/*     */   }
/*     */ 
/*     */   public boolean isStopped() {
/* 103 */     return this.strategyProcessor.isStopping();
/*     */   }
/*     */ 
/*     */   public StrategyProcessor getStrategyProcessor() {
/* 107 */     return this.strategyProcessor;
/*     */   }
/*     */ 
/*     */   public IChart getChart(Instrument instrument) {
/* 111 */     if (this.chartsController != null) {
/* 112 */       IChart chart = this.chartsController.getIChartBy(instrument);
/*     */ 
/* 114 */       if (chart == null) {
/* 115 */         return null;
/*     */       }
/*     */ 
/* 118 */       return getOrCreate(chart);
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   private IChart getOrCreate(IChart chart)
/*     */   {
/* 125 */     DelegatableChartWrapper wrapper = (DelegatableChartWrapper)this.delegatableChartWrappers.get(chart);
/* 126 */     if (wrapper == null)
/*     */     {
/* 130 */       wrapper = new DelegatableChartWrapper(chart, this.strategyProcessor.getStrategy().getClass().getName());
/*     */ 
/* 134 */       this.delegatableChartWrappers.put(chart, wrapper);
/*     */     }
/* 136 */     return wrapper;
/*     */   }
/*     */ 
/*     */   public Set<IChart> getCharts(Instrument instrument) {
/* 140 */     if (this.chartsController != null) {
/* 141 */       Set charts = this.chartsController.getICharts(instrument);
/* 142 */       if (charts == null) {
/* 143 */         return null;
/*     */       }
/*     */ 
/* 149 */       Set result = new HashSet();
/*     */ 
/* 151 */       for (IChart chart : charts) {
/* 152 */         IChart wrapper = getOrCreate(chart);
/* 153 */         result.add(wrapper);
/*     */       }
/*     */ 
/* 156 */       return result;
/*     */     }
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   public IChart getLastActiveChart()
/*     */   {
/* 165 */     return this.chartsController.getLastActiveIChart();
/*     */   }
/*     */ 
/*     */   public IUserInterface getUserInterface() {
/* 169 */     return this.userInterface;
/*     */   }
/*     */ 
/*     */   public IConsole getConsole() {
/* 173 */     return this.console;
/*     */   }
/*     */ 
/*     */   public IEngine getEngine() {
/* 177 */     return this.forexEngineImpl;
/*     */   }
/*     */ 
/*     */   public IHistory getHistory() {
/* 181 */     return this.history;
/*     */   }
/*     */ 
/*     */   public IIndicators getIndicators() {
/* 185 */     if (this.indicators == null) {
/* 186 */       this.indicators = new Indicators(this.history);
/*     */     }
/* 188 */     return this.indicators;
/*     */   }
/*     */ 
/*     */   public IStrategies getStrategies()
/*     */   {
/* 193 */     return this.strategiesControl;
/*     */   }
/*     */ 
/*     */   public IDownloadableStrategies getDownloadableStrategies()
/*     */   {
/* 198 */     return this.downloadableStrategies;
/*     */   }
/*     */ 
/*     */   public <T> Future<T> executeTask(Callable<T> callable)
/*     */   {
/* 203 */     return this.strategyProcessor.executeTask(callable, false);
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessGranted() {
/* 207 */     return this.strategyProcessor.isFullAccessGranted();
/*     */   }
/*     */ 
/*     */   public void stop() {
/*     */     try {
/* 212 */       AccessController.doPrivileged(new StopStrategyPrivilegedAction(null));
/*     */     } catch (PrivilegedActionException e) {
/* 214 */       Exception ex = e.getException();
/* 215 */       String error = StrategyWrapper.representError(new Long(this.strategyProcessor.getStrategyId()), ex);
/* 216 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(error, ex, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getFilesDir() {
/* 220 */     return this.filesDir;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 225 */     return "JForex context";
/*     */   }
/*     */ 
/*     */   public void pause()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setSubscribedInstruments(Set<Instrument> instruments)
/*     */   {
/* 241 */     this.strategyProcessor.setSubscribedInstruments(instruments);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSubscribedInstruments()
/*     */   {
/* 246 */     return this.strategyProcessor.getSubscribedInstruments();
/*     */   }
/*     */ 
/*     */   public IAccount getAccount()
/*     */   {
/* 251 */     return this.strategyProcessor.getAccount();
/*     */   }
/*     */ 
/*     */   public void subscribeToBarsFeed(Instrument instrument, Period period, OfferSide offerSide, IBarFeedListener listener)
/*     */   {
/* 261 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, period);
/*     */ 
/* 266 */     StrategyRateDataNotificationFactory.getIsntance().getCandleNotificationManager().subscribeToLiveBarsFeed(this.strategyProcessor.getStrategy(), instrument, jForexPeriod, offerSide, listener, this.strategyProcessor.getTaskManager().getExceptionHandler(), this.strategyProcessor.getTaskManager(), this.strategyProcessor, NotificationUtilsProvider.getNotificationUtils());
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromBarsFeed(IBarFeedListener listener)
/*     */   {
/* 281 */     StrategyRateDataNotificationFactory.getIsntance().getCandleNotificationManager().unsubscribeFromLiveBarsFeed(this.strategyProcessor.getStrategy(), listener);
/*     */   }
/*     */ 
/*     */   public void subscribeToPointAndFigureFeed(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, IPointAndFigureFeedListener listener)
/*     */   {
/* 295 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE, Period.TICK, priceRange, reversalAmount);
/*     */ 
/* 302 */     StrategyRateDataNotificationFactory.getIsntance().getPointAndFigureNotificationManager().subscribeToLiveBarsFeed(this.strategyProcessor.getStrategy(), instrument, jForexPeriod, offerSide, listener, this.strategyProcessor.getTaskManager().getExceptionHandler(), this.strategyProcessor.getTaskManager(), this.strategyProcessor, NotificationUtilsProvider.getNotificationUtils());
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromPointAndFigureFeed(IPointAndFigureFeedListener listener)
/*     */   {
/* 317 */     StrategyRateDataNotificationFactory.getIsntance().getPointAndFigureNotificationManager().unsubscribeFromLiveBarsFeed(this.strategyProcessor.getStrategy(), listener);
/*     */   }
/*     */ 
/*     */   public void subscribeToRangeBarFeed(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IRangeBarFeedListener listener)
/*     */   {
/* 330 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.PRICE_RANGE_AGGREGATION, Period.TICK, priceRange);
/*     */ 
/* 336 */     StrategyRateDataNotificationFactory.getIsntance().getPriceRangeNotificationManager().subscribeToLiveBarsFeed(this.strategyProcessor.getStrategy(), instrument, jForexPeriod, offerSide, listener, this.strategyProcessor.getTaskManager().getExceptionHandler(), this.strategyProcessor.getTaskManager(), this.strategyProcessor, NotificationUtilsProvider.getNotificationUtils());
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromRangeBarFeed(IRangeBarFeedListener listener)
/*     */   {
/* 351 */     StrategyRateDataNotificationFactory.getIsntance().getPriceRangeNotificationManager().unsubscribeFromLiveBarsFeed(this.strategyProcessor.getStrategy(), listener);
/*     */   }
/*     */ 
/*     */   public void subscribeToTickBarFeed(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarFeedListener listener)
/*     */   {
/* 364 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.TICK_BAR, Period.TICK, null, null, tickBarSize);
/*     */ 
/* 373 */     StrategyRateDataNotificationFactory.getIsntance().getTickBarNotificationManager().subscribeToLiveBarsFeed(this.strategyProcessor.getStrategy(), instrument, jForexPeriod, offerSide, listener, this.strategyProcessor.getTaskManager().getExceptionHandler(), this.strategyProcessor.getTaskManager(), this.strategyProcessor, NotificationUtilsProvider.getNotificationUtils());
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromTickBarFeed(ITickBarFeedListener listener)
/*     */   {
/* 388 */     StrategyRateDataNotificationFactory.getIsntance().getTickBarNotificationManager().unsubscribeFromLiveBarsFeed(this.strategyProcessor.getStrategy(), listener);
/*     */   }
/*     */ 
/*     */   public void subscribeToRenkoBarFeed(Instrument instrument, OfferSide offerSide, PriceRange brickSize, IRenkoBarFeedListener listener)
/*     */   {
/* 401 */     JForexPeriod jForexPeriod = new JForexPeriod(DataType.RENKO, Period.TICK, brickSize);
/*     */ 
/* 407 */     StrategyRateDataNotificationFactory.getIsntance().getRenkoNotificationManager().subscribeToLiveBarsFeed(this.strategyProcessor.getStrategy(), instrument, jForexPeriod, offerSide, listener, this.strategyProcessor.getTaskManager().getExceptionHandler(), this.strategyProcessor.getTaskManager(), this.strategyProcessor, NotificationUtilsProvider.getNotificationUtils());
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromRenkoBarFeed(IRenkoBarFeedListener listener)
/*     */   {
/* 423 */     StrategyRateDataNotificationFactory.getIsntance().getRenkoNotificationManager().unsubscribeFromLiveBarsFeed(this.strategyProcessor.getStrategy(), listener);
/*     */   }
/*     */ 
/*     */   public void addConfigurationChangeListener(String parameter, PropertyChangeListener listener)
/*     */   {
/* 435 */     this.strategyProcessor.getTaskManager().addConfigurationChangeListener(parameter, listener);
/*     */   }
/*     */ 
/*     */   public void removeConfigurationChangeListener(String parameter, PropertyChangeListener listener)
/*     */   {
/* 443 */     this.strategyProcessor.getTaskManager().removeConfigurationChangeListener(parameter, listener);
/*     */   }
/*     */ 
/*     */   public JFUtils getUtils()
/*     */   {
/* 448 */     return this.jfUtils;
/*     */   }
/*     */ 
/*     */   public IDataService getDataService()
/*     */   {
/* 456 */     return this.dataService;
/*     */   }
/*     */ 
/*     */   public DDSChartsController getChartsController() {
/* 460 */     return this.chartsController;
/*     */   }
/*     */ 
/*     */   private final class StopStrategyPrivilegedAction
/*     */     implements PrivilegedExceptionAction<Object>
/*     */   {
/*     */     private StopStrategyPrivilegedAction()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object run()
/*     */       throws Exception
/*     */     {
/* 234 */       JForexContextImpl.this.strategyProcessor.getTaskManager().stopStrategy();
/* 235 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.JForexContextImpl
 * JD-Core Version:    0.6.0
 */