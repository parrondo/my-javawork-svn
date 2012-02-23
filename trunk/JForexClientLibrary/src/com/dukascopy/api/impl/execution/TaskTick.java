/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.ITick;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.impl.StrategyEventsListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskTick extends AbstractPostDataTask<Void>
/*    */ {
/* 22 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskTick.class);
/*    */   private final Instrument instrument;
/*    */   private final ITick tick;
/*    */   private long addedTime;
/*    */   private StrategyEventsListener strategyEventsListener;
/*    */ 
/*    */   public TaskTick(JForexTaskManager taskManager, IStrategy strategy, Instrument instrument, ITick tick, IStrategyExceptionHandler exceptionHandler)
/*    */   {
/* 36 */     super(taskManager, strategy, exceptionHandler);
/* 37 */     this.instrument = instrument;
/* 38 */     this.tick = tick;
/* 39 */     this.addedTime = System.currentTimeMillis();
/* 40 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*    */   }
/*    */ 
/*    */   public long getAddedTime() {
/* 44 */     return this.addedTime;
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 49 */     return Task.Type.TICK;
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument() {
/* 53 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public ITick getTick() {
/* 57 */     return this.tick;
/*    */   }
/*    */ 
/*    */   protected IStrategyExceptionHandler.Source getSource()
/*    */   {
/* 62 */     return IStrategyExceptionHandler.Source.ON_TICK;
/*    */   }
/*    */ 
/*    */   protected void postData() throws Throwable
/*    */   {
/* 67 */     this.strategy.onTick(this.instrument, this.tick);
/* 68 */     if (this.strategyEventsListener != null)
/* 69 */       this.strategyEventsListener.onTick(this.instrument, this.tick);
/*    */   }
/*    */ 
/*    */   protected Logger getLogger()
/*    */   {
/* 75 */     return LOGGER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskTick
 * JD-Core Version:    0.6.0
 */