/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IEngine;
/*     */ import com.dukascopy.api.IEngine.StrategyMode;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.ISignal;
/*     */ import com.dukascopy.api.ISignalsProcessor;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterCustodian;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class InternalStrategyController
/*     */ {
/*  25 */   private boolean isStarted = false;
/*     */   private IContext context;
/*     */   private IStrategy strategy;
/*     */   private ISignalsProcessor signalsProcessor;
/*     */   private IEngine engine;
/*     */   private IEngine mainEngine;
/*     */ 
/*     */   public InternalStrategyController(IStrategy strategy, IContext context, boolean generateSignals, IEngine mainEngine)
/*     */   {
/*  33 */     this.engine = context.getEngine();
/*  34 */     if (generateSignals) {
/*  35 */       this.engine.setStrategyMode(IEngine.StrategyMode.SIGNALS);
/*     */     }
/*     */ 
/*  38 */     this.context = context;
/*  39 */     this.strategy = strategy;
/*  40 */     this.signalsProcessor = context.getEngine().getSignalsProcessor();
/*  41 */     this.mainEngine = mainEngine;
/*     */   }
/*     */ 
/*     */   public void startStrategy(Map<String, Object> configurableParameters) throws JFException {
/*  45 */     if (this.strategy == null) {
/*  46 */       throw new JFException("Strategy not initialized");
/*     */     }
/*  48 */     setParameters(configurableParameters);
/*  49 */     this.strategy.onStart(this.context);
/*  50 */     this.isStarted = true;
/*     */   }
/*     */ 
/*     */   public List<ISignal> onTick(Instrument instrument, ITick tick) throws JFException {
/*  54 */     if ((this.engine instanceof TesterCustodian)) {
/*  55 */       ((TesterCustodian)this.engine).onTick(instrument, tick);
/*     */     }
/*  57 */     this.strategy.onTick(instrument, tick);
/*  58 */     List signals = this.signalsProcessor.retrieve();
/*  59 */     processDelayedTasks();
/*  60 */     return signals;
/*     */   }
/*     */ 
/*     */   public List<ISignal> onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
/*  64 */     if ((this.engine instanceof TesterCustodian)) {
/*  65 */       ((TesterCustodian)this.engine).updateLastTicks(((TesterCustodian)this.mainEngine).getLastTicks(), instrument);
/*     */     }
/*  67 */     this.strategy.onBar(instrument, period, askBar, bidBar);
/*  68 */     List signals = this.signalsProcessor.retrieve();
/*  69 */     processDelayedTasks();
/*  70 */     return signals;
/*     */   }
/*     */ 
/*     */   public List<ISignal> onMessage(IMessage message) throws JFException {
/*  74 */     this.strategy.onMessage(message);
/*  75 */     List signals = this.signalsProcessor.retrieve();
/*  76 */     processDelayedTasks();
/*  77 */     return signals;
/*     */   }
/*     */ 
/*     */   public void onAccount(IAccount account) throws JFException {
/*  81 */     this.strategy.onAccount(account);
/*  82 */     processDelayedTasks();
/*     */   }
/*     */ 
/*     */   public void onStop() throws JFException {
/*  86 */     this.isStarted = false;
/*  87 */     this.strategy.onStop();
/*  88 */     processDelayedTasks();
/*     */   }
/*     */ 
/*     */   public boolean isStarted() throws JFException {
/*  92 */     return this.isStarted;
/*     */   }
/*     */ 
/*     */   public void setParameters(Map<String, Object> parameterMap) throws JFException {
/*  96 */     if (parameterMap != null) {
/*  97 */       Field[] fields = this.strategy.getClass().getFields();
/*  98 */       for (Field field : fields) {
/*  99 */         Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 100 */         if (configurable == null) continue;
/*     */         try {
/* 102 */           if (parameterMap.containsKey(field.getName()))
/* 103 */             field.set(this.strategy, parameterMap.get(field.getName()));
/*     */         }
/*     */         catch (Exception e) {
/* 106 */           throw new JFException("Error while setting value for the field [" + field.getName() + "]", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processDelayedTasks()
/*     */   {
/* 114 */     if ((this.engine instanceof TesterCustodian))
/* 115 */       ((TesterCustodian)this.engine).doDelayedTasks();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.InternalStrategyController
 * JD-Core Version:    0.6.0
 */