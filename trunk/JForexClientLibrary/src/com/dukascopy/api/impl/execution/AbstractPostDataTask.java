/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public abstract class AbstractPostDataTask<T>
/*    */   implements Task<T>
/*    */ {
/*    */   protected final JForexTaskManager taskManager;
/*    */   protected final IStrategyExceptionHandler exceptionHandler;
/*    */   protected final IStrategy strategy;
/*    */ 
/*    */   public AbstractPostDataTask(JForexTaskManager taskManager, IStrategy strategy, IStrategyExceptionHandler exceptionHandler)
/*    */   {
/* 30 */     this.exceptionHandler = exceptionHandler;
/* 31 */     this.strategy = strategy;
/* 32 */     this.taskManager = taskManager;
/*    */   }
/*    */ 
/*    */   public T call()
/*    */     throws Exception
/*    */   {
/* 38 */     if (this.taskManager.isStrategyStopping()) {
/* 39 */       return null;
/*    */     }
/*    */     try
/*    */     {
/* 43 */       postData();
/*    */     } catch (Throwable t) {
/* 45 */       handleError(t, this.strategy);
/*    */     }
/*    */ 
/* 48 */     return null;
/*    */   }
/*    */ 
/*    */   protected void handleError(Throwable t) {
/* 52 */     handleError(t, this.strategy);
/*    */   }
/*    */ 
/*    */   protected void handleError(Throwable t, Object forObject) {
/* 56 */     String msg = StrategyWrapper.representError(forObject, t);
/* 57 */     NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 58 */     getLogger().error(t.getMessage(), t);
/* 59 */     this.exceptionHandler.onException(this.taskManager.getStrategyId(), getSource(), t);
/*    */   }
/*    */ 
/*    */   protected abstract Logger getLogger();
/*    */ 
/*    */   protected abstract IStrategyExceptionHandler.Source getSource();
/*    */ 
/*    */   protected abstract void postData()
/*    */     throws Throwable;
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.AbstractPostDataTask
 * JD-Core Version:    0.6.0
 */