/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IMessage;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyEventsListener;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.PlatformOrderImpl;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskOrderError
/*    */   implements Task
/*    */ {
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskOrderError.class);
/*    */   private IStrategy strategy;
/*    */   private ErrorResponseMessage errorResponseMessage;
/*    */   private PlatformOrderImpl order;
/*    */   private IStrategyExceptionHandler exceptionHandler;
/*    */   private JForexTaskManager taskManager;
/*    */   private StrategyEventsListener strategyEventsListener;
/*    */ 
/*    */   public TaskOrderError(JForexTaskManager taskManager, IStrategy strategy, ErrorResponseMessage errorResponseMessage, PlatformOrderImpl order, IStrategyExceptionHandler exceptionHandler)
/*    */   {
/* 33 */     this.strategy = strategy;
/* 34 */     this.errorResponseMessage = errorResponseMessage;
/* 35 */     this.order = order;
/* 36 */     this.exceptionHandler = exceptionHandler;
/* 37 */     this.taskManager = taskManager;
/* 38 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 43 */     return Task.Type.MESSAGE;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception
/*    */   {
/* 48 */     if (this.taskManager.isStrategyStopping()) {
/* 49 */       return null;
/*    */     }
/* 51 */     if (LOGGER.isDebugEnabled())
/* 52 */       LOGGER.debug("Starting order [" + this.order.getLabel() + "] update process, error response message [" + this.errorResponseMessage + "]");
/*    */     try
/*    */     {
/* 55 */       IMessage platformMessageImpl = this.order.update(this.errorResponseMessage);
/*    */ 
/* 57 */       if (platformMessageImpl != null) {
/* 58 */         this.strategy.onMessage(platformMessageImpl);
/* 59 */         if (this.strategyEventsListener != null)
/* 60 */           this.strategyEventsListener.onMessage(platformMessageImpl);
/*    */       }
/*    */     }
/*    */     catch (Throwable t) {
/* 64 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 65 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 66 */       LOGGER.error(t.getMessage(), t);
/* 67 */       this.exceptionHandler.onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_MESSAGE, t);
/*    */     }
/* 69 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskOrderError
 * JD-Core Version:    0.6.0
 */