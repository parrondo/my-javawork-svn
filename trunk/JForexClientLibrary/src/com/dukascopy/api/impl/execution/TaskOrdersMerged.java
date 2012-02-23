/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IMessage;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyEventsListener;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.OrdersInternalCollection;
/*    */ import com.dukascopy.api.impl.connect.PlatformOrderImpl;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskOrdersMerged
/*    */   implements Task
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskOrdersMerged.class);
/*    */   private IStrategy strategy;
/*    */   private MergePositionsMessage mergePositionsMessage;
/*    */   private JForexTaskManager taskManager;
/*    */   private StrategyEventsListener strategyEventsListener;
/*    */ 
/*    */   public TaskOrdersMerged(JForexTaskManager taskManager, IStrategy strategy, MergePositionsMessage mergePositionsMessage)
/*    */   {
/* 32 */     this.strategy = strategy;
/* 33 */     this.mergePositionsMessage = mergePositionsMessage;
/* 34 */     this.taskManager = taskManager;
/* 35 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 40 */     return Task.Type.MESSAGE;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception
/*    */   {
/* 45 */     if (this.taskManager.isStrategyStopping()) {
/* 46 */       return null;
/*    */     }
/* 48 */     if (LOGGER.isDebugEnabled())
/* 49 */       LOGGER.debug("Starting processing of merge message [" + this.mergePositionsMessage + "]");
/*    */     try
/*    */     {
/* 52 */       OrdersInternalCollection ordersInternalCollection = this.taskManager.getOrdersInternalCollection();
/* 53 */       PlatformOrderImpl platformOrderImpl = ordersInternalCollection.getOrderById(this.mergePositionsMessage.getNewOrderGroupId());
/* 54 */       if (platformOrderImpl == null) {
/* 55 */         String label = this.mergePositionsMessage.getExternalSysId();
/* 56 */         platformOrderImpl = ordersInternalCollection.getOrderByLabel(label);
/*    */       }
/*    */ 
/* 59 */       IMessage platformMessageImpl = null;
/* 60 */       if (platformOrderImpl != null) {
/* 61 */         platformMessageImpl = platformOrderImpl.update(this.mergePositionsMessage);
/*    */       }
/*    */ 
/* 64 */       if (platformMessageImpl != null) {
/* 65 */         this.strategy.onMessage(platformMessageImpl);
/* 66 */         if (this.strategyEventsListener != null)
/* 67 */           this.strategyEventsListener.onMessage(platformMessageImpl);
/*    */       }
/*    */     }
/*    */     catch (Throwable t) {
/* 71 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 72 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 73 */       LOGGER.error(t.getMessage(), t);
/* 74 */       this.taskManager.getExceptionHandler().onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_MESSAGE, t);
/*    */     }
/* 76 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskOrdersMerged
 * JD-Core Version:    0.6.0
 */