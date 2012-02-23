/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.StrategyEventsListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskOnBar extends AbstractPostDataTask<Void>
/*    */ {
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskOnBar.class);
/*    */ 
/* 25 */   private StrategyProcessor strategyProcessor = null;
/* 26 */   private Instrument instrument = null;
/* 27 */   private Period period = null;
/* 28 */   private IBar askBar = null;
/* 29 */   private IBar bidBar = null;
/*    */   private StrategyEventsListener strategyEventsListener;
/*    */ 
/*    */   public TaskOnBar(JForexTaskManager taskManager, StrategyProcessor strategyProcessor, Instrument instrument, Period period, IBar askBar, IBar bidBar, IStrategyExceptionHandler exceptionHandler)
/*    */   {
/* 41 */     super(taskManager, strategyProcessor.getStrategy(), exceptionHandler);
/* 42 */     this.strategyProcessor = strategyProcessor;
/* 43 */     this.instrument = instrument;
/* 44 */     this.period = period;
/* 45 */     this.askBar = askBar;
/* 46 */     this.bidBar = bidBar;
/* 47 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 52 */     return Task.Type.BAR;
/*    */   }
/*    */ 
/*    */   public Void call() throws Exception
/*    */   {
/* 57 */     if (this.taskManager.isStrategyStopping()) {
/* 58 */       return null;
/*    */     }
/* 60 */     if (this.strategyProcessor.isOnBarImplemented()) {
/*    */       try {
/* 62 */         postData();
/*    */       } catch (AbstractMethodError abstractMethodError) {
/* 64 */         this.strategyProcessor.setOnBarImplemented(false);
/*    */       } catch (Throwable t) {
/* 66 */         handleError(t);
/*    */       }
/*    */     }
/* 69 */     return null;
/*    */   }
/*    */ 
/*    */   protected Logger getLogger()
/*    */   {
/* 74 */     return LOGGER;
/*    */   }
/*    */ 
/*    */   protected IStrategyExceptionHandler.Source getSource()
/*    */   {
/* 79 */     return IStrategyExceptionHandler.Source.ON_BAR;
/*    */   }
/*    */ 
/*    */   protected void postData() throws Throwable
/*    */   {
/* 84 */     this.strategyProcessor.getStrategy().onBar(this.instrument, this.period, this.askBar, this.bidBar);
/* 85 */     if (this.strategyEventsListener != null)
/* 86 */       this.strategyEventsListener.onBar(this.instrument, this.period, this.askBar, this.bidBar);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskOnBar
 * JD-Core Version:    0.6.0
 */