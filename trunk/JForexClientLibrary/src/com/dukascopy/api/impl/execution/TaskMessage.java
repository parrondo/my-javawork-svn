/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IMessage;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyEventsListener;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskMessage
/*    */   implements Task<Object>
/*    */ {
/* 21 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskMessage.class);
/*    */   private final JForexTaskManager taskManager;
/*    */   private final IStrategy strategy;
/*    */   private final IMessage message;
/*    */   private final IStrategyExceptionHandler exceptionHandler;
/* 27 */   private boolean release = false;
/*    */   private StrategyEventsListener strategyEventsListener;
/*    */ 
/*    */   public TaskMessage(JForexTaskManager taskManager, IStrategy strategy, IMessage message, IStrategyExceptionHandler exceptionHandler)
/*    */   {
/* 31 */     this.strategy = strategy;
/* 32 */     this.message = message;
/* 33 */     this.exceptionHandler = exceptionHandler;
/* 34 */     this.taskManager = taskManager;
/* 35 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 40 */     return Task.Type.MESSAGE;
/*    */   }
/*    */ 
/*    */   public boolean release() {
/* 44 */     return this.release;
/*    */   }
/*    */ 
/*    */   public void setRelease(boolean release) {
/* 48 */     this.release = release;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception
/*    */   {
/* 53 */     if (this.taskManager.isStrategyStopping()) {
/* 54 */       return null;
/*    */     }
/*    */     try
/*    */     {
/* 58 */       this.strategy.onMessage(this.message);
/* 59 */       if (this.strategyEventsListener != null)
/* 60 */         this.strategyEventsListener.onMessage(this.message);
/*    */     }
/*    */     catch (Throwable t) {
/* 63 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 64 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 65 */       LOGGER.error(t.getMessage(), t);
/* 66 */       this.exceptionHandler.onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_MESSAGE, t);
/*    */     }
/* 68 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskMessage
 * JD-Core Version:    0.6.0
 */