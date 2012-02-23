/*     */ package com.dukascopy.api.impl.execution;
/*     */ 
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.impl.StrategyEventsListener;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.OrdersInternalCollection;
/*     */ import com.dukascopy.api.impl.connect.PlatformOrderImpl;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TaskOrderGroupUpdate
/*     */   implements Task
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskOrderGroupUpdate.class);
/*     */   private IStrategy strategy;
/*     */   private OrderGroupMessage orderGroupMessage;
/*     */   private JForexTaskManager taskManager;
/*     */   private StrategyEventsListener strategyEventsListener;
/*     */ 
/*     */   public TaskOrderGroupUpdate(JForexTaskManager taskManager, IStrategy strategy, OrderGroupMessage orderGroupMessage)
/*     */   {
/*  36 */     this.strategy = strategy;
/*  37 */     this.orderGroupMessage = orderGroupMessage;
/*  38 */     this.taskManager = taskManager;
/*  39 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*     */   }
/*     */ 
/*     */   public Task.Type getType()
/*     */   {
/*  44 */     return Task.Type.MESSAGE;
/*     */   }
/*     */ 
/*     */   public Object call() throws Exception
/*     */   {
/*  49 */     if (this.taskManager.isStrategyStopping()) {
/*  50 */       return null;
/*     */     }
/*  52 */     if (LOGGER.isDebugEnabled())
/*  53 */       LOGGER.debug("Starting processing of order group message [" + this.orderGroupMessage + "]");
/*     */     try
/*     */     {
/*  56 */       OrdersInternalCollection ordersInternalCollection = this.taskManager.getOrdersInternalCollection();
/*  57 */       PlatformOrderImpl platformOrderImpl = null;
/*  58 */       String orderGroupId = this.orderGroupMessage.getOrderGroupId();
/*  59 */       if (orderGroupId != null) {
/*  60 */         platformOrderImpl = ordersInternalCollection.getOrderById(orderGroupId);
/*     */       }
/*     */ 
/*  63 */       String label = PlatformOrderImpl.extractLabel(this.orderGroupMessage);
/*  64 */       if ((platformOrderImpl == null) && (label != null)) {
/*  65 */         platformOrderImpl = ordersInternalCollection.getOrderByLabel(label);
/*  66 */         if ((platformOrderImpl != null) && (platformOrderImpl.getState() != IOrder.State.CREATED)) {
/*  67 */           LOGGER.warn("Getting order by label [" + label + "] instead of order id");
/*     */         }
/*     */       }
/*     */ 
/*  71 */       if (platformOrderImpl == null) {
/*  72 */         if (label == null) {
/*  73 */           if (!this.taskManager.isGlobal()) {
/*  74 */             LOGGER.warn("Order group message received that doesn't have assigned external id. Order group id [" + orderGroupId + "]");
/*  75 */             this.orderGroupMessage.setExternalSysId(orderGroupId);
/*     */           }
/*  77 */           label = orderGroupId;
/*     */         }
/*  79 */         if (this.orderGroupMessage.getOrders().size() > 0) {
/*  80 */           platformOrderImpl = new PlatformOrderImpl(this.taskManager);
/*     */           try {
/*  82 */             ordersInternalCollection.put(label, platformOrderImpl, false);
/*     */           } catch (JFException e) {
/*  84 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*  86 */         } else if ((this.taskManager.isGlobal()) && (this.orderGroupMessage.getAmount() != null) && (this.orderGroupMessage.getAmount().getValue().compareTo(BigDecimal.ZERO) > 0)) {
/*  87 */           platformOrderImpl = new PlatformOrderImpl(this.taskManager);
/*     */           try {
/*  89 */             ordersInternalCollection.put(label, platformOrderImpl, false);
/*     */           } catch (JFException e) {
/*  91 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*  93 */         } else if ((this.orderGroupMessage.isOcoMerge()) && (this.orderGroupMessage.getAmount() != null) && (this.orderGroupMessage.getAmount().getValue().compareTo(BigDecimal.ZERO) == 0))
/*     */         {
/*  95 */           platformOrderImpl = new PlatformOrderImpl(this.taskManager);
/*     */           try {
/*  97 */             ordersInternalCollection.put(label, platformOrderImpl, false);
/*     */           } catch (JFException e) {
/*  99 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*     */         else {
/* 103 */           return null;
/*     */         }
/*     */       }
/*     */ 
/* 107 */       IMessage platformMessageImpl = platformOrderImpl.update(this.orderGroupMessage);
/*     */ 
/* 109 */       if (platformMessageImpl != null) {
/* 110 */         this.strategy.onMessage(platformMessageImpl);
/* 111 */         if (this.strategyEventsListener != null)
/* 112 */           this.strategyEventsListener.onMessage(platformMessageImpl);
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/* 116 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 117 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 118 */       LOGGER.error(t.getMessage(), t);
/* 119 */       this.taskManager.getExceptionHandler().onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_MESSAGE, t);
/*     */     }
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 126 */     return "TaskOrderGroupUpdate [" + this.orderGroupMessage + "]";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskOrderGroupUpdate
 * JD-Core Version:    0.6.0
 */