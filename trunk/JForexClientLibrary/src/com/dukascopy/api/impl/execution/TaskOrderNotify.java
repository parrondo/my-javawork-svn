/*     */ package com.dukascopy.api.impl.execution;
/*     */ 
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.impl.StrategyEventsListener;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.OrdersInternalCollection;
/*     */ import com.dukascopy.api.impl.connect.PlatformOrderImpl;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TaskOrderNotify
/*     */   implements Task
/*     */ {
/*  24 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskOrderNotify.class);
/*     */   private JForexTaskManager taskManager;
/*     */   private IStrategy strategy;
/*     */   private NotificationMessage notificationMessage;
/*     */   private StrategyEventsListener strategyEventsListener;
/*     */ 
/*     */   public TaskOrderNotify(JForexTaskManager taskManager, IStrategy strategy, NotificationMessage notificationMessage)
/*     */   {
/*  32 */     this.strategy = strategy;
/*  33 */     this.notificationMessage = notificationMessage;
/*  34 */     this.taskManager = taskManager;
/*  35 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*     */   }
/*     */ 
/*     */   public Task.Type getType()
/*     */   {
/*  40 */     return Task.Type.MESSAGE;
/*     */   }
/*     */ 
/*     */   public Object call() throws Exception
/*     */   {
/*  45 */     if (this.taskManager.isStrategyStopping()) {
/*  46 */       return null;
/*     */     }
/*  48 */     if (LOGGER.isDebugEnabled())
/*  49 */       LOGGER.debug("Starting processing of notify message [" + this.notificationMessage + "]");
/*     */     try
/*     */     {
/*  52 */       OrdersInternalCollection ordersInternalCollection = this.taskManager.getOrdersInternalCollection();
/*  53 */       PlatformOrderImpl platformOrderImpl = null;
/*  54 */       if (this.taskManager.isGlobal())
/*     */       {
/*  56 */         String orderId = this.notificationMessage.getOrderId();
/*  57 */         if (orderId != null) {
/*  58 */           platformOrderImpl = ordersInternalCollection.getOrderByOpeningOrderId(orderId);
/*     */         }
/*     */       }
/*  61 */       else if (platformOrderImpl == null) {
/*  62 */         String positionId = this.notificationMessage.getPositionId();
/*  63 */         if (positionId != null) {
/*  64 */           platformOrderImpl = ordersInternalCollection.getOrderById(positionId);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  69 */       if (platformOrderImpl == null) {
/*  70 */         String label = this.notificationMessage.getExternalSysId();
/*  71 */         platformOrderImpl = ordersInternalCollection.getOrderByLabel(label);
/*     */       }
/*     */ 
/*  74 */       if (platformOrderImpl != null) {
/*  75 */         IMessage platformMessageImpl = platformOrderImpl.update(this.notificationMessage);
/*  76 */         if (platformMessageImpl != null) {
/*  77 */           this.strategy.onMessage(platformMessageImpl);
/*  78 */           if (this.strategyEventsListener != null) {
/*  79 */             this.strategyEventsListener.onMessage(platformMessageImpl);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  85 */         String text = this.notificationMessage.getText();
/*  86 */         if ((text != null) && 
/*  87 */           (text.indexOf("Failed to merge positions") != -1)) {
/*  88 */           for (PlatformOrderImpl impl : ordersInternalCollection.getAllMergeTargets()) {
/*  89 */             this.notificationMessage.setExternalSysId(impl.getLabel());
/*  90 */             IMessage platformMessageImpl = impl.update(this.notificationMessage);
/*  91 */             if (platformMessageImpl != null) {
/*  92 */               this.strategy.onMessage(platformMessageImpl);
/*  93 */               if (this.strategyEventsListener != null)
/*  94 */                 this.strategyEventsListener.onMessage(platformMessageImpl);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 102 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 103 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 104 */       LOGGER.error(t.getMessage(), t);
/* 105 */       this.taskManager.getExceptionHandler().onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_MESSAGE, t);
/*     */     }
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 112 */     return "TaskOrderNotify [" + this.notificationMessage + "]";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskOrderNotify
 * JD-Core Version:    0.6.0
 */