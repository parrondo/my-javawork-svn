/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IAccount;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskAccount
/*    */   implements Task
/*    */ {
/* 20 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskAccount.class);
/*    */ 
/* 22 */   private IStrategy strategy = null;
/* 23 */   private IAccount account = null;
/*    */   private IStrategyExceptionHandler exceptionHandler;
/*    */   private JForexTaskManager taskManager;
/*    */ 
/*    */   public TaskAccount(JForexTaskManager taskManager, IStrategy strategy, IAccount account, IStrategyExceptionHandler exceptionHandler)
/*    */   {
/* 28 */     this.strategy = strategy;
/* 29 */     this.account = account;
/* 30 */     this.exceptionHandler = exceptionHandler;
/* 31 */     this.taskManager = taskManager;
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 36 */     return Task.Type.ACCOUNT;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception
/*    */   {
/* 41 */     if (this.taskManager.isStrategyStopping())
/* 42 */       return null;
/*    */     try
/*    */     {
/* 45 */       this.strategy.onAccount(this.account);
/*    */     } catch (Throwable t) {
/* 47 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 48 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 49 */       LOGGER.error(t.getMessage(), t);
/* 50 */       this.exceptionHandler.onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_ACCOUNT_INFO, t);
/*    */     }
/* 52 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskAccount
 * JD-Core Version:    0.6.0
 */