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
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TaskOrderUpdate
/*     */   implements Task
/*     */ {
/*  31 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskOrderUpdate.class);
/*     */   private IStrategy strategy;
/*     */   private OrderMessage orderMessage;
/*     */   private JForexTaskManager taskManager;
/*     */   private StrategyEventsListener strategyEventsListener;
/*     */ 
/*     */   public TaskOrderUpdate(JForexTaskManager taskManager, IStrategy strategy, OrderMessage orderMessage)
/*     */   {
/*  39 */     this.strategy = strategy;
/*  40 */     this.orderMessage = orderMessage;
/*  41 */     this.taskManager = taskManager;
/*  42 */     this.strategyEventsListener = taskManager.getStrategyEventsListener();
/*     */   }
/*     */ 
/*     */   public Task.Type getType()
/*     */   {
/*  47 */     return Task.Type.MESSAGE;
/*     */   }
/*     */ 
/*     */   public Object call() throws Exception
/*     */   {
/*  52 */     if (this.taskManager.isStrategyStopping()) {
/*  53 */       return null;
/*     */     }
/*  55 */     if (LOGGER.isDebugEnabled()) {
/*  56 */       LOGGER.debug("Starting processing of order message [" + this.orderMessage + "]");
/*     */     }
/*  58 */     if (!this.taskManager.isGlobal()) {
/*  59 */       LOGGER.error("Received OrderMessage for not global account");
/*  60 */       return null;
/*     */     }
/*     */     try {
/*  63 */       OrderGroupMessage orderGroupMessage = new OrderGroupMessage();
/*  64 */       ArrayList orders = new ArrayList();
/*  65 */       orders.add(this.orderMessage);
/*  66 */       orderGroupMessage.setOrders(orders);
/*  67 */       OrdersInternalCollection ordersInternalCollection = this.taskManager.getOrdersInternalCollection();
/*  68 */       PlatformOrderImpl platformOrderImpl = null;
/*  69 */       String parentOrderId = this.orderMessage.getParentOrderId();
/*  70 */       if (parentOrderId != null) {
/*  71 */         platformOrderImpl = ordersInternalCollection.getOrderById(parentOrderId);
/*  72 */         orderGroupMessage.setOrderGroupId(parentOrderId);
/*     */       }
/*     */ 
/*  75 */       String label = PlatformOrderImpl.extractLabel(orderGroupMessage);
/*  76 */       if ((platformOrderImpl == null) && (label != null) && (
/*  77 */         (!this.taskManager.isGlobal()) || (this.orderMessage.getOrderState() != OrderState.CANCELLED)))
/*     */       {
/*  82 */         platformOrderImpl = ordersInternalCollection.getOrderByLabel(label);
/*  83 */         if ((platformOrderImpl != null) && (platformOrderImpl.getState() != IOrder.State.CREATED)) {
/*  84 */           LOGGER.warn("Getting order by label [" + label + "] instead of order id");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  89 */       if (platformOrderImpl == null) {
/*  90 */         if ((this.taskManager.isGlobal()) && (this.orderMessage.getOrderState() == OrderState.CANCELLED))
/*     */         {
/*  92 */           return null;
/*     */         }
/*  94 */         if (label == null) {
/*  95 */           LOGGER.warn("Order message received that doesn't have assigned external id. Parent order id [" + parentOrderId + "]");
/*  96 */           orderGroupMessage.setExternalSysId(parentOrderId);
/*  97 */           label = parentOrderId;
/*     */         }
/*  99 */         if (orderGroupMessage.getOrders().size() > 0) {
/* 100 */           platformOrderImpl = new PlatformOrderImpl(this.taskManager);
/*     */           try {
/* 102 */             ordersInternalCollection.put(label, platformOrderImpl, false);
/*     */           } catch (JFException e) {
/* 104 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/* 106 */         } else if ((orderGroupMessage.isOcoMerge()) && (orderGroupMessage.getAmount() != null) && (orderGroupMessage.getAmount().getValue().compareTo(BigDecimal.ZERO) == 0))
/*     */         {
/* 108 */           platformOrderImpl = new PlatformOrderImpl(this.taskManager);
/*     */           try {
/* 110 */             ordersInternalCollection.put(label, platformOrderImpl, false);
/*     */           } catch (JFException e) {
/* 112 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*     */         else {
/* 116 */           return null;
/*     */         }
/*     */       }
/*     */ 
/* 120 */       IMessage platformMessageImpl = platformOrderImpl.update(orderGroupMessage);
/* 121 */       if ((platformOrderImpl.getState() == IOrder.State.FILLED) && (platformOrderImpl.getAmount() == platformOrderImpl.getRequestedAmount()))
/*     */       {
/* 123 */         this.taskManager.getOrdersInternalCollection().removeById(parentOrderId);
/*     */       }
/*     */ 
/* 126 */       if (platformMessageImpl != null) {
/* 127 */         this.strategy.onMessage(platformMessageImpl);
/* 128 */         if (this.strategyEventsListener != null)
/* 129 */           this.strategyEventsListener.onMessage(platformMessageImpl);
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/* 133 */       String msg = StrategyWrapper.representError(this.strategy, t);
/* 134 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, t, false);
/* 135 */       LOGGER.error(t.getMessage(), t);
/* 136 */       this.taskManager.getExceptionHandler().onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_MESSAGE, t);
/*     */     }
/* 138 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskOrderUpdate
 * JD-Core Version:    0.6.0
 */