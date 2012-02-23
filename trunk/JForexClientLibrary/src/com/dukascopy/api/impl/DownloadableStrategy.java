/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IDownloadableStrategy;
/*     */ import com.dukascopy.api.IDownloadableStrategy.ComponentType;
/*     */ import com.dukascopy.api.IEngine;
/*     */ import com.dukascopy.api.IEngine.StrategyMode;
/*     */ import com.dukascopy.api.IEngine.Type;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.ISignal;
/*     */ import com.dukascopy.api.ISignalsProcessor;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.connect.FacelessUserInterface;
/*     */ import com.dukascopy.api.impl.connect.InternalStrategyController;
/*     */ import com.dukascopy.api.impl.connect.JForexContextImpl;
/*     */ import com.dukascopy.api.impl.connect.JForexEngineImpl;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.SignalsProcessorImpl;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.impl.util.ComponentDownloader;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.MinTradableAmounts;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterConfig;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterCustodian;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterHistory;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrdersProvider;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DownloadableStrategy
/*     */   implements IDownloadableStrategy
/*     */ {
/*     */   protected final ComponentDownloader downloader;
/*     */   private IContext mainContext;
/*     */   private IStrategy strategy;
/*     */   private InternalStrategyController internalStrategyController;
/*     */   private IEngine.StrategyMode mode;
/*     */   private String name;
/*     */   private String id;
/*     */   private IDownloadableStrategy.ComponentType type;
/*     */   private Map<String, Object> configurables;
/*     */   private byte[] srcCode;
/*     */ 
/*     */   public DownloadableStrategy(String id, String name, IContext context, IDownloadableStrategy.ComponentType type, IEngine.StrategyMode mode, Map<String, Object> configurables)
/*     */     throws JFException
/*     */   {
/*  61 */     this.name = name;
/*  62 */     this.id = id;
/*  63 */     this.mainContext = context;
/*  64 */     this.type = type;
/*  65 */     this.mode = mode;
/*  66 */     this.configurables = configurables;
/*  67 */     this.downloader = ComponentDownloader.getInstance();
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  71 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  76 */     return this.id;
/*     */   }
/*     */ 
/*     */   public IDownloadableStrategy.ComponentType getComponentType()
/*     */   {
/*  81 */     return this.type;
/*     */   }
/*     */ 
/*     */   public IEngine.StrategyMode getStrategyMode() {
/*  85 */     return this.mode;
/*     */   }
/*     */ 
/*     */   public List<ISignal> onTick(Instrument instrument, ITick tick) throws JFException
/*     */   {
/*  90 */     return this.internalStrategyController.onTick(instrument, tick);
/*     */   }
/*     */ 
/*     */   public List<ISignal> onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar)
/*     */     throws JFException
/*     */   {
/*  96 */     return this.internalStrategyController.onBar(instrument, period, askBar, bidBar);
/*     */   }
/*     */ 
/*     */   public List<ISignal> onMessage(IMessage message) throws JFException
/*     */   {
/* 101 */     return this.internalStrategyController.onMessage(message);
/*     */   }
/*     */ 
/*     */   public void onAccount(IAccount account) throws JFException
/*     */   {
/* 106 */     this.internalStrategyController.onAccount(account);
/*     */   }
/*     */ 
/*     */   public void onStop() throws JFException
/*     */   {
/* 111 */     this.internalStrategyController.onStop();
/*     */   }
/*     */ 
/*     */   public void start() throws JFException
/*     */   {
/*     */     try
/*     */     {
/* 118 */       if (this.type == IDownloadableStrategy.ComponentType.BLOCK_STRATEGY)
/* 119 */         this.srcCode = this.downloader.getFileSource(this.id, null, "http://arizona.rix.dukascopy.com:8080/strategystorageserver/getCompiledFile?id=");
/* 120 */       else if (this.type == IDownloadableStrategy.ComponentType.BLOCK_OWN_STRATEGY)
/* 121 */         this.srcCode = this.downloader.getFileSource(this.id, null, "http://arizona.rix.dukascopy.com:8080/strategystorageserver/getUserStrategy?id=");
/*     */     }
/*     */     catch (IOException e) {
/* 124 */       throw new JFException(MessageFormat.format("Unable to download component {0}", new Object[] { this.id }), e);
/*     */     }
/*     */     JFXPack jfxPack;
/*     */     try {
/* 129 */       jfxPack = JFXPack.loadFromPack(this.srcCode);
/*     */     } catch (Exception e) {
/* 131 */       throw new JFException(MessageFormat.format("Unable to init component {0}", new Object[] { this.id }), e);
/*     */     }
/*     */ 
/* 134 */     this.strategy = ((IStrategy)jfxPack.getTarget());
/*     */ 
/* 136 */     IEngine mainEngine = this.mainContext.getEngine();
/* 137 */     ISignalsProcessor signalsProcessor = new SignalsProcessorImpl();
/* 138 */     if ((mainEngine instanceof JForexEngineImpl))
/* 139 */       setupRealtime((JForexEngineImpl)mainEngine, this.configurables, signalsProcessor);
/* 140 */     else if ((mainEngine instanceof TesterCustodian))
/* 141 */       setupTest((TesterCustodian)mainEngine, this.configurables, signalsProcessor);
/*     */     else
/* 143 */       throw new JFException(MessageFormat.format("Unable to init component {0}", new Object[] { this.id }));
/*     */   }
/*     */ 
/*     */   private void setupRealtime(JForexEngineImpl mainEngine, Map<String, Object> configurables, ISignalsProcessor signalsProcessor) throws JFException
/*     */   {
/* 148 */     JForexTaskManager taskManager = mainEngine.getTaskManager();
/* 149 */     IEngine engine = new JForexEngineImpl(taskManager, signalsProcessor, this.mode, mainEngine.getAccount(), mainEngine.getType() == IEngine.Type.LIVE);
/* 150 */     StrategyProcessor strategyProcessor = new StrategyProcessor(taskManager, this.strategy, true);
/* 151 */     IContext context = new JForexContextImpl(strategyProcessor, engine, (History)this.mainContext.getHistory(), this.mainContext.getConsole(), null, new FacelessUserInterface(), this.mainContext.getStrategies());
/*     */ 
/* 153 */     this.internalStrategyController = new InternalStrategyController(this.strategy, context, this.mode == IEngine.StrategyMode.SIGNALS, mainEngine);
/* 154 */     this.internalStrategyController.startStrategy(configurables);
/* 155 */     this.internalStrategyController.onAccount(taskManager.getAccount());
/*     */   }
/*     */ 
/*     */   private void setupTest(TesterCustodian mainEngine, Map<String, Object> configurables, ISignalsProcessor signalsProcessor) throws JFException {
/* 159 */     StrategyRunner strategyRunner = (StrategyRunner)mainEngine.getStrategyRunner();
/*     */ 
/* 161 */     Map minTradableAmounts = new MinTradableAmounts(Double.valueOf(1000.0D));
/*     */ 
/* 163 */     minTradableAmounts.put(Instrument.XAUUSD, Double.valueOf(1.0D));
/* 164 */     minTradableAmounts.put(Instrument.XAGUSD, Double.valueOf(50.0D));
/*     */ 
/* 166 */     TesterOrdersProvider testerOrdersProvider = mainEngine.getTesterOrdersProvider();
/*     */ 
/* 168 */     IStrategyExceptionHandler strategyExceptionHandler = new IStrategyExceptionHandler()
/*     */     {
/*     */       public void onException(long strategyId, IStrategyExceptionHandler.Source source, Throwable t)
/*     */       {
/*     */       }
/*     */     };
/* 176 */     Map firstTicks = new HashMap();
/* 177 */     for (Instrument instrument : this.mainContext.getSubscribedInstruments()) {
/* 178 */       firstTicks.put(instrument, mainEngine.getLastTicks()[instrument.ordinal()]);
/*     */     }
/*     */ 
/* 181 */     TesterReportData testerReportData = new TesterReportData();
/* 182 */     testerReportData.initialDeposit = strategyRunner.getAccount().getDeposit();
/*     */ 
/* 184 */     TesterCustodian engine = new TesterCustodian(strategyRunner.getInstruments(), minTradableAmounts, NotificationUtilsProvider.getNotificationUtils(), strategyRunner.getFrom(), strategyRunner.getAccount(), firstTicks, testerReportData, strategyRunner, testerOrdersProvider, strategyExceptionHandler);
/*     */ 
/* 188 */     engine.setStrategy(this.strategy);
/* 189 */     engine.setSignalsProcessor(signalsProcessor);
/*     */ 
/* 191 */     IContext context = new TesterConfig(engine, NotificationUtilsProvider.getNotificationUtils(), true, new HashMap(0), new ExecutionControl()
/*     */     {
/*     */       public void pause()
/*     */       {
/*     */       }
/*     */ 
/*     */       public void setSpeed(int value)
/*     */       {
/*     */       }
/*     */     }
/*     */     , testerOrdersProvider, new LoadingProgressListener()
/*     */     {
/*     */       public boolean stopJob()
/*     */       {
/* 207 */         return false;
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void dataLoaded(long start, long end, long currentPosition, String information)
/*     */       {
/*     */       }
/*     */     }
/*     */     , strategyRunner, strategyRunner.getAccount(), this.strategy, (TesterHistory)this.mainContext.getHistory());
/*     */ 
/* 222 */     this.internalStrategyController = new InternalStrategyController(this.strategy, context, this.mode == IEngine.StrategyMode.SIGNALS, strategyRunner.getEngine());
/* 223 */     this.internalStrategyController.startStrategy(configurables);
/* 224 */     this.internalStrategyController.onAccount(strategyRunner.getAccount());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.DownloadableStrategy
 * JD-Core Version:    0.6.0
 */