/*     */ package com.dukascopy.dds2.greed.agent;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IConsole;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.IUserInterface;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.StrategyMessages;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.connect.ISystemListenerExtended;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager.Environment;
/*     */ import com.dukascopy.api.impl.connect.PlatformAccountImpl;
/*     */ import com.dukascopy.api.impl.connect.StrategyBroadcastMessageImpl;
/*     */ import com.dukascopy.api.impl.connect.StrategyListener;
/*     */ import com.dukascopy.api.impl.execution.Task;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveCandleListener;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.FullDepthInstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ParametersDialog;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StrategyParameters;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Preset;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.PresetsModel;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.XMLPresets;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.console.StrategyConsoleImpl;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.mt.AgentManager;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderSyncMessage;
/*     */ import com.dukascopy.transport.common.msg.news.NewsStoryMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyBroadcastMessage;
/*     */ import java.awt.Frame;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Strategies
/*     */   implements LiveCandleListener
/*     */ {
/*  71 */   private static final Logger LOGGER = LoggerFactory.getLogger(Strategies.class);
/*     */ 
/*  74 */   private Map<Long, StrategyWrapper> runningStrategies = new HashMap();
/*  75 */   private Map<Long, JForexTaskManager> strategyEngines = new HashMap();
/*     */ 
/*  77 */   private long temporaryKeys = -1L;
/*     */ 
/*  79 */   private AgentManager dllSupport_depricated = null;
/*     */ 
/*  81 */   private static Strategies instance = new Strategies();
/*     */ 
/*     */   private Strategies() {
/*  84 */     this.dllSupport_depricated = ((AgentManager)GreedContext.get("ddsAgent"));
/*     */   }
/*     */ 
/*     */   public static Strategies get() {
/*  88 */     return instance;
/*     */   }
/*     */ 
/*     */   public synchronized void onNewsMessage(NewsStoryMessage newsStoryMessage) {
/*  92 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*  93 */       taskManager.onNewsMessage(newsStoryMessage);
/*     */   }
/*     */ 
/*     */   public synchronized void onNotifyMessage(NotificationMessage notificationMessage)
/*     */   {
/*  98 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*  99 */       taskManager.onNotifyMessage(notificationMessage);
/*     */   }
/*     */ 
/*     */   public synchronized void onOrderGroupReceived(OrderGroupMessage orderGroupMessage)
/*     */   {
/* 104 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 105 */       taskManager.onOrderGroupReceived(orderGroupMessage);
/*     */   }
/*     */ 
/*     */   public synchronized void onOrderReceived(OrderMessage orderMessage)
/*     */   {
/* 110 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 111 */       taskManager.onOrderReceived(orderMessage);
/*     */   }
/*     */ 
/*     */   public synchronized void onOrdersMergedMessage(MergePositionsMessage mergePositionsMessage)
/*     */   {
/* 116 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 117 */       taskManager.onOrdersMergedMessage(mergePositionsMessage);
/*     */   }
/*     */ 
/*     */   public synchronized void updateAccountInfo(AccountInfoMessage protocolMessage)
/*     */   {
/* 122 */     PlatformAccountImpl.updateStaticValues(protocolMessage);
/* 123 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 124 */       taskManager.updateAccountInfo(protocolMessage);
/*     */   }
/*     */ 
/*     */   public synchronized void onMarketState(CurrencyMarket market)
/*     */   {
/* 129 */     ITick tick = null;
/* 130 */     for (JForexTaskManager taskManager : this.strategyEngines.values()) {
/* 131 */       tick = taskManager.onMarketState(market);
/*     */     }
/* 133 */     if (tick != null) {
/* 134 */       Instrument instrument = Instrument.fromString(market.getInstrument());
/* 135 */       this.dllSupport_depricated.onMarketStateImpl_dllsupport_depricated(instrument.name(), tick);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onConnect(boolean value) {
/* 140 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 141 */       taskManager.onConnect(value);
/*     */   }
/*     */ 
/*     */   public synchronized void onInstrumentStatusUpdate(InstrumentStatusUpdateMessage instrumentStatusMessage)
/*     */   {
/* 146 */     Instrument instrument = Instrument.fromString(instrumentStatusMessage.getInstrument());
/* 147 */     boolean tradable = instrumentStatusMessage.getTradable() == 0;
/* 148 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 149 */       taskManager.onIntrumentUpdate(instrument, tradable, instrumentStatusMessage.getTimestamp() == null ? FeedDataProvider.getDefaultInstance().getCurrentTime() : instrumentStatusMessage.getTimestamp().getTime());
/*     */   }
/*     */ 
/*     */   public void startStrategy(Frame frame, StrategyNewBean strategyBean, StrategyListener strategyListener)
/*     */   {
/* 156 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*     */ 
/* 158 */     StrategyWrapper wrapper = new StrategyWrapper();
/* 159 */     wrapper.setBinaryFile(strategyBean.getStrategyBinaryFile());
/*     */ 
/* 162 */     IConsole console = new StrategyConsoleImpl(strategyBean.getId().toString(), strategyBean.getName());
/*     */ 
/* 164 */     StrategyExceptionHandler exceptionHandler = new StrategyExceptionHandler(null);
/*     */ 
/* 166 */     AccountInfoMessage accountState = ((AccountStatement)GreedContext.get("accountStatement")).getLastAccountState();
/* 167 */     if (accountState == null) {
/* 168 */       if (console != null) {
/* 169 */         console.getOut().println("Initialization in process, waiting for account data");
/*     */       }
/* 171 */       return;
/*     */     }
/*     */ 
/* 174 */     JForexTaskManager taskManager = new JForexTaskManager(JForexTaskManager.Environment.LOCAL_JFOREX, GreedContext.isLive(), GreedContext.getAccountName(), console, ((GreedTransportClient)GreedContext.get("transportClient")).getTransportClient(), (DDSChartsController)GreedContext.get("chartsController"), (IUserInterface)GreedContext.get("iUserInterface"), exceptionHandler, accountState, (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*     */ 
/* 187 */     ISystemListenerExtended systemListener = createSystemListenerForStrategy(strategyListener, true);
/* 188 */     taskManager.setSystemListener(systemListener);
/*     */ 
/* 190 */     exceptionHandler.setTaskManager(taskManager);
/*     */ 
/* 192 */     IStrategy strategy = strategyBean.getStrategy();
/*     */ 
/* 194 */     if (strategy == null) {
/* 195 */       if (console != null) {
/* 196 */         console.getErr().println("Class not accepted.");
/*     */       }
/* 198 */       return;
/*     */     }
/*     */ 
/* 201 */     StrategyMessages.startingStrategy(strategy);
/*     */ 
/* 203 */     AppActionEvent action = new AppActionEvent(this, false, false, taskManager, wrapper, strategy, strategyBean, strategyListener)
/*     */     {
/*     */       public void doAction()
/*     */       {
/*     */         long temporaryProcessId;
/* 207 */         synchronized (Strategies.this) {
/* 208 */           temporaryProcessId = Strategies.this.temporaryKeys;
/* 209 */           Strategies.access$122(Strategies.this, 1L);
/* 210 */           Strategies.this.strategyEngines.put(Long.valueOf(temporaryProcessId), this.val$taskManager);
/* 211 */           Strategies.this.runningStrategies.put(Long.valueOf(temporaryProcessId), this.val$wrapper);
/*     */         }
/* 213 */         long processId = this.val$taskManager.startStrategy(this.val$strategy, null, this.val$strategyBean.getStrategyKey(), this.val$strategyBean.isFullAccessGranted());
/* 214 */         if (processId == 0L) {
/* 215 */           synchronized (Strategies.this) {
/* 216 */             Strategies.this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/* 217 */             Strategies.this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*     */           }
/* 219 */           this.val$strategyListener.strategyStartingFailed();
/*     */         } else {
/* 221 */           synchronized (Strategies.this) {
/* 222 */             Strategies.this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/* 223 */             Strategies.this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*     */ 
/* 225 */             Strategies.this.strategyEngines.put(Long.valueOf(processId), this.val$taskManager);
/* 226 */             Strategies.this.runningStrategies.put(Long.valueOf(processId), this.val$wrapper);
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/* 231 */     GreedContext.publishEvent(action);
/*     */   }
/*     */ 
/*     */   public void startStrategy(Frame frame, File binaryFile, StrategyListener strategyListener, String presetName)
/*     */   {
/* 238 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*     */ 
/* 240 */     StrategyWrapper wrapper = new StrategyWrapper();
/* 241 */     wrapper.setBinaryFile(binaryFile);
/*     */ 
/* 244 */     IConsole console = new StrategyConsoleImpl(wrapper.getBinaryFile().getPath(), wrapper.getName());
/*     */ 
/* 246 */     StrategyExceptionHandler exceptionHandler = new StrategyExceptionHandler(null);
/*     */ 
/* 248 */     AccountInfoMessage accountState = ((AccountStatement)GreedContext.get("accountStatement")).getLastAccountState();
/* 249 */     if (accountState == null) {
/* 250 */       if (console != null) {
/* 251 */         console.getOut().println("Initialization in process, waiting for account data");
/*     */       }
/* 253 */       return;
/*     */     }
/*     */ 
/* 256 */     JForexTaskManager taskManager = new JForexTaskManager(JForexTaskManager.Environment.LOCAL_JFOREX, GreedContext.isLive(), GreedContext.getAccountName(), console, ((GreedTransportClient)GreedContext.get("transportClient")).getTransportClient(), (DDSChartsController)GreedContext.get("chartsController"), (IUserInterface)GreedContext.get("iUserInterface"), exceptionHandler, accountState, (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*     */ 
/* 269 */     ISystemListenerExtended systemListener = createSystemListenerForStrategy(strategyListener);
/* 270 */     taskManager.setSystemListener(systemListener);
/*     */ 
/* 272 */     exceptionHandler.setTaskManager(taskManager);
/*     */     IStrategy strategy;
/*     */     try
/*     */     {
/* 276 */       strategy = wrapper.getStrategy(false);
/*     */     } catch (Exception e) {
/* 278 */       LOGGER.error(e.getMessage(), e);
/* 279 */       NotificationUtils.getInstance().postErrorMessage("Error while loading strategy: " + e.getMessage(), true);
/* 280 */       return;
/*     */     }
/*     */ 
/* 283 */     if (strategy == null) {
/* 284 */       if (console != null) {
/* 285 */         console.getErr().println("Class not accepted.");
/*     */       }
/* 287 */       return;
/*     */     }
/*     */ 
/* 290 */     if (!needRunOnStart())
/*     */     {
/* 292 */       boolean canProceed = false;
/*     */ 
/* 294 */       HashMap params = StrategyParameters.getParameters(wrapper.getName());
/* 295 */       Task rc = new ParametersDialog(frame, strategy, false, wrapper.getBinaryFile()).showParam(params);
/* 296 */       if (rc != null) {
/*     */         try {
/* 298 */           canProceed = ((Boolean)rc.call()).booleanValue();
/*     */         } catch (Exception e1) {
/* 300 */           LOGGER.error(e1.getMessage(), e1);
/*     */         }
/*     */       }
/*     */ 
/* 304 */       if (!canProceed) {
/* 305 */         return;
/*     */       }
/*     */     }
/* 308 */     else if (presetName != null) {
/* 309 */       PresetsModel presetsModel = XMLPresets.loadModel(strategy, wrapper.getBinaryFile());
/* 310 */       Preset pres = presetsModel.getPreset(presetName);
/*     */ 
/* 312 */       if (pres == null) {
/* 313 */         LOGGER.warn("No preset found : " + presetName);
/*     */       } else {
/* 315 */         HashMap presetValues = presetsModel.getVariableMap(pres);
/*     */ 
/* 317 */         Field[] fields = strategy.getClass().getFields();
/* 318 */         for (Field field : fields)
/*     */         {
/* 320 */           Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 321 */           if (configurable == null) continue;
/*     */           try {
/* 323 */             field.set(strategy, ((Variable)presetValues.get(field.getName())).getValue());
/*     */           } catch (Exception e) {
/* 325 */             LOGGER.warn(" : " + e.getMessage());
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 334 */     StrategyMessages.startingStrategy(strategy);
/*     */ 
/* 336 */     AppActionEvent action = new AppActionEvent(this, false, false, taskManager, wrapper, strategy, strategyListener)
/*     */     {
/*     */       public void doAction()
/*     */       {
/*     */         long temporaryProcessId;
/* 340 */         synchronized (Strategies.this) {
/* 341 */           temporaryProcessId = Strategies.this.temporaryKeys;
/* 342 */           Strategies.access$122(Strategies.this, 1L);
/* 343 */           Strategies.this.strategyEngines.put(Long.valueOf(temporaryProcessId), this.val$taskManager);
/* 344 */           Strategies.this.runningStrategies.put(Long.valueOf(temporaryProcessId), this.val$wrapper);
/*     */         }
/* 346 */         long processId = this.val$taskManager.startStrategy(this.val$strategy, null, this.val$wrapper.getStrategyKey(), this.val$wrapper.isFullAccessGranted());
/* 347 */         if (processId == 0L) {
/* 348 */           synchronized (Strategies.this) {
/* 349 */             Strategies.this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/* 350 */             Strategies.this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*     */           }
/* 352 */           this.val$strategyListener.strategyStartingFailed();
/*     */         } else {
/* 354 */           synchronized (Strategies.this) {
/* 355 */             Strategies.this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/* 356 */             Strategies.this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*     */ 
/* 358 */             Strategies.this.strategyEngines.put(Long.valueOf(processId), this.val$taskManager);
/* 359 */             Strategies.this.runningStrategies.put(Long.valueOf(processId), this.val$wrapper);
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/* 364 */     GreedContext.publishEvent(action);
/*     */   }
/*     */ 
/*     */   public long startStrategyFromStrategy(File binaryFile, StrategyListener strategyListener, Map<String, Object> configurables, boolean fullAccessGranted, IStrategyListener iStrategyListener)
/*     */     throws JFException
/*     */   {
/* 379 */     StrategyWrapper wrapper = new StrategyWrapper();
/* 380 */     wrapper.setBinaryFile(binaryFile);
/*     */ 
/* 383 */     IConsole console = new StrategyConsoleImpl(wrapper.getBinaryFile().getPath(), wrapper.getName());
/*     */ 
/* 385 */     StrategyExceptionHandler exceptionHandler = new StrategyExceptionHandler(null);
/* 386 */     AccountInfoMessage accountState = ((AccountStatement)GreedContext.get("accountStatement")).getLastAccountState();
/* 387 */     if (accountState == null) {
/* 388 */       throw new JFException("Initialization in process, waiting for account data");
/*     */     }
/*     */ 
/* 390 */     JForexTaskManager taskManager = new JForexTaskManager(JForexTaskManager.Environment.LOCAL_JFOREX, GreedContext.isLive(), GreedContext.getAccountName(), console, ((GreedTransportClient)GreedContext.get("transportClient")).getTransportClient(), (DDSChartsController)GreedContext.get("chartsController"), (IUserInterface)GreedContext.get("iUserInterface"), exceptionHandler, accountState, (String)GreedContext.getConfig("external_ip"), (String)GreedContext.getConfig("local_ip_address"), (String)GreedContext.getConfig("SESSION_ID"));
/*     */ 
/* 403 */     ISystemListenerExtended systemListener = createSystemListenerForStrategy(strategyListener);
/* 404 */     taskManager.setSystemListener(systemListener);
/*     */ 
/* 406 */     exceptionHandler.setTaskManager(taskManager);
/*     */     IStrategy strategy;
/*     */     try
/*     */     {
/* 410 */       strategy = wrapper.getStrategy(false, fullAccessGranted);
/*     */     } catch (Exception e) {
/* 412 */       LOGGER.error(e.getMessage(), e);
/* 413 */       throw new JFException("Error while loading strategy: " + e.getMessage());
/*     */     }
/*     */ 
/* 416 */     if (strategy == null) {
/* 417 */       throw new JFException("Error while loading strategy");
/*     */     }
/*     */ 
/* 420 */     if (configurables != null) {
/* 421 */       Field[] fields = strategy.getClass().getFields();
/* 422 */       for (Field field : fields) {
/* 423 */         Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 424 */         if (configurable == null) continue;
/*     */         try {
/* 426 */           if (configurables.containsKey(field.getName()))
/* 427 */             field.set(strategy, configurables.get(field.getName()));
/*     */         }
/*     */         catch (Exception e) {
/* 430 */           throw new JFException("Error while setting value for the field [" + field.getName() + "]", e);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 436 */     StrategyMessages.startingStrategy(strategy);
/*     */     long temporaryProcessId;
/* 439 */     synchronized (this) {
/* 440 */       temporaryProcessId = this.temporaryKeys;
/* 441 */       this.temporaryKeys -= 1L;
/* 442 */       this.strategyEngines.put(Long.valueOf(temporaryProcessId), taskManager);
/* 443 */       this.runningStrategies.put(Long.valueOf(temporaryProcessId), wrapper);
/*     */     }
/* 445 */     long processId = taskManager.startStrategy(strategy, null, wrapper.getStrategyKey(), fullAccessGranted);
/* 446 */     if (processId == 0L) {
/* 447 */       synchronized (this) {
/* 448 */         this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/* 449 */         this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*     */       }
/* 451 */       strategyListener.strategyStartingFailed();
/*     */     } else {
/* 453 */       synchronized (this) {
/* 454 */         this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/* 455 */         this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*     */ 
/* 457 */         this.strategyEngines.put(Long.valueOf(processId), taskManager);
/* 458 */         this.runningStrategies.put(Long.valueOf(processId), wrapper);
/*     */       }
/*     */     }
/* 461 */     return processId;
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getRequiredInstruments() {
/* 465 */     Set instruments = new HashSet();
/* 466 */     for (JForexTaskManager engine : this.strategyEngines.values()) {
/* 467 */       instruments.addAll(engine.getRequiredInstruments());
/*     */     }
/* 469 */     return instruments;
/*     */   }
/*     */ 
/*     */   private ISystemListenerExtended createSystemListenerForStrategy(StrategyListener strategyListener) {
/* 473 */     return createSystemListenerForStrategy(strategyListener, false);
/*     */   }
/*     */ 
/*     */   private ISystemListenerExtended createSystemListenerForStrategy(StrategyListener strategyListener, boolean forTabVersion)
/*     */   {
/* 478 */     return new Object(strategyListener, forTabVersion)
/*     */     {
/*     */       public void onStart(long processId) {
/* 481 */         this.val$strategyListener.strategyStarted(processId);
/*     */       }
/*     */ 
/*     */       public void onStop(long processId)
/*     */       {
/* 486 */         synchronized (Strategies.this) {
/* 487 */           if (Strategies.this.runningStrategies.containsKey(Long.valueOf(processId))) {
/* 488 */             Strategies.this.strategyEngines.remove(Long.valueOf(processId));
/* 489 */             Strategies.this.runningStrategies.remove(Long.valueOf(processId));
/*     */           }
/*     */         }
/* 492 */         this.val$strategyListener.strategyStopped(processId);
/* 493 */         FullDepthInstrumentSubscribeAction action = new FullDepthInstrumentSubscribeAction(this);
/* 494 */         GreedContext.publishEvent(action);
/*     */       }
/*     */ 
/*     */       public void onConnect()
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onDisconnect() {
/*     */       }
/*     */ 
/*     */       public void subscribeToInstruments(Set<Instrument> instruments) {
/* 505 */         JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 506 */         IWorkspaceHelper workspaceHelper = clientFormLayoutManager.getWorkspaceHelper();
/* 507 */         workspaceHelper.addDependantCurrenciesAndSubscribe(instruments);
/* 508 */         FullDepthInstrumentSubscribeAction action = new FullDepthInstrumentSubscribeAction(this);
/* 509 */         GreedContext.publishEvent(action);
/*     */       }
/*     */ 
/*     */       public Set<Instrument> getSubscribedInstruments()
/*     */       {
/* 514 */         JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 515 */         IWorkspaceHelper workspaceHelper = clientFormLayoutManager.getWorkspaceHelper();
/* 516 */         return workspaceHelper.getSubscribedInstruments();
/*     */       }
/*     */ 
/*     */       public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException
/*     */       {
/* 521 */         JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 523 */         if (this.val$forTabVersion) {
/* 524 */           StrategiesContentPane strategiesContentPane = clientFormLayoutManager.getStrategiesPanel();
/* 525 */           return strategiesContentPane.startStrategyFromAnother(jfxFile, listener, configurables, fullAccess);
/*     */         }
/* 527 */         IWorkspaceHelper workspaceHelper = clientFormLayoutManager.getWorkspaceHelper();
/* 528 */         return workspaceHelper.startStrategy(jfxFile, listener, configurables, fullAccess);
/*     */       }
/*     */ 
/*     */       public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess)
/*     */         throws JFException
/*     */       {
/* 534 */         JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 536 */         if (this.val$forTabVersion) {
/* 537 */           StrategiesContentPane strategiesContentPane = clientFormLayoutManager.getStrategiesPanel();
/* 538 */           return strategiesContentPane.startStrategyFromAnother(strategy, listener, fullAccess);
/*     */         }
/* 540 */         IWorkspaceHelper workspaceHelper = clientFormLayoutManager.getWorkspaceHelper();
/* 541 */         return workspaceHelper.startStrategy(strategy, listener, fullAccess);
/*     */       }
/*     */ 
/*     */       public void stopStrategy(long strategyId)
/*     */       {
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static boolean needRunOnStart() {
/* 552 */     if (GreedContext.getConfig("jnlp.run.strategy.on.start") == null) return false;
/* 553 */     return ((Boolean)GreedContext.getConfig("jnlp.run.strategy.on.start")).booleanValue();
/*     */   }
/*     */ 
/*     */   public synchronized void stopStrategy(long processId) {
/* 557 */     if (!this.runningStrategies.containsKey(Long.valueOf(processId))) {
/* 558 */       return;
/*     */     }
/* 560 */     JForexTaskManager taskManager = (JForexTaskManager)this.strategyEngines.remove(Long.valueOf(processId));
/* 561 */     taskManager.stopStrategy();
/* 562 */     this.runningStrategies.remove(Long.valueOf(processId));
/* 563 */     taskManager = null;
/*     */   }
/*     */ 
/*     */   public synchronized void stopAll() {
/* 567 */     for (Long runKey : new HashSet(this.runningStrategies.keySet()))
/* 568 */       stopStrategy(runKey.longValue());
/*     */   }
/*     */ 
/*     */   public synchronized void stopStrategiesForRequiredInstrument(Instrument instrument)
/*     */   {
/* 573 */     for (Map.Entry entry : new HashMap(this.strategyEngines).entrySet()) {
/* 574 */       JForexTaskManager engine = (JForexTaskManager)entry.getValue();
/* 575 */       if (engine.getRequiredInstruments().contains(instrument)) {
/* 576 */         Long key = (Long)entry.getKey();
/* 577 */         stopStrategy(key.longValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void executeTask(long processId, Task<?> task) {
/* 583 */     JForexTaskManager taskManager = (JForexTaskManager)this.strategyEngines.get(Long.valueOf(processId));
/* 584 */     if (taskManager != null)
/* 585 */       taskManager.executeTask(task);
/*     */   }
/*     */ 
/*     */   public synchronized int getRunningStrategiesCount()
/*     */   {
/* 590 */     return this.runningStrategies.size();
/*     */   }
/*     */ 
/*     */   public synchronized StrategyWrapper getRunningStrategy(long processId) {
/* 594 */     return (StrategyWrapper)this.runningStrategies.get(Long.valueOf(processId));
/*     */   }
/*     */ 
/*     */   public synchronized void newCandle(Instrument instrument, Period period, CandleData askCandle, CandleData bidCandle) {
/* 598 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 599 */       taskManager.newCandle(instrument, period, askCandle, bidCandle);
/*     */   }
/*     */ 
/*     */   public void orderSynch(OrderSyncMessage orderSyncMessage)
/*     */   {
/* 604 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/* 605 */       taskManager.orderSynch(orderSyncMessage);
/*     */   }
/*     */ 
/*     */   public void onStrategyBroadcast(StrategyBroadcastMessage message)
/*     */   {
/* 611 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*     */       try {
/* 613 */         taskManager.onBroadcastMessage(message.getTransactionId(), new StrategyBroadcastMessageImpl(message.getTopic(), message.getMessage(), System.currentTimeMillis()));
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/* 622 */         LOGGER.error("Error while dispatching broadcast", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void fireConfigurationPropertyChange(long processId, PropertyChangeEvent event)
/*     */   {
/* 628 */     JForexTaskManager taskManager = (JForexTaskManager)this.strategyEngines.get(Long.valueOf(processId));
/* 629 */     if (taskManager != null)
/* 630 */       taskManager.fireConfigurationPropertyChange(event); 
/*     */   }
/*     */ 
/*     */   private static class StrategyExceptionHandler implements IStrategyExceptionHandler {
/* 635 */     private static final Logger LOGGER = LoggerFactory.getLogger(StrategyExceptionHandler.class);
/*     */     private JForexTaskManager taskManager;
/*     */ 
/*     */     public void setTaskManager(JForexTaskManager taskManager) {
/* 639 */       this.taskManager = taskManager;
/*     */     }
/*     */ 
/*     */     public void onException(long strategyId, IStrategyExceptionHandler.Source source, Throwable t)
/*     */     {
/* 644 */       LOGGER.error("Exception thrown whiler running " + source + " method: " + t.getMessage(), t);
/* 645 */       if (((ClientSettingsStorage)GreedContext.get("settingsStorage")).getStopStrategyByException())
/* 646 */         this.taskManager.stopStrategy();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.Strategies
 * JD-Core Version:    0.6.0
 */