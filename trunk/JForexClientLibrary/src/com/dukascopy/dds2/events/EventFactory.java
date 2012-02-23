/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class EventFactory
/*     */ {
/*     */   private String serviceId;
/*  21 */   private String targetServiceId = null;
/*     */ 
/*     */   public EventFactory(String serviceId)
/*     */   {
/*  25 */     this.serviceId = serviceId;
/*     */   }
/*     */ 
/*     */   public EventFactory(String serviceId, String targetServiceId)
/*     */   {
/*  30 */     this.serviceId = serviceId;
/*  31 */     this.targetServiceId = targetServiceId;
/*     */   }
/*     */ 
/*     */   public String getServiceId() {
/*  35 */     return this.serviceId;
/*     */   }
/*     */ 
/*     */   public String getTargetServiceId() {
/*  39 */     return this.targetServiceId;
/*     */   }
/*     */ 
/*     */   public NotificationEvent createNotification(NotificationEvent.Level level, String message)
/*     */   {
/*  52 */     NotificationEvent event = new NotificationEvent(level, message);
/*  53 */     return (NotificationEvent)eventWorkaround(event);
/*     */   }
/*     */ 
/*     */   public Event addClientNotification(Event event, int userId, NotificationEvent.Level level, String notificationMessage)
/*     */   {
/*  65 */     return eventWorkaround(event.append(new NotificationEvent(level, notificationMessage)).append(new ClientEvent(userId, true)));
/*     */   }
/*     */ 
/*     */   public NotificationEvent createClientNotification(int userId, NotificationEvent.Level level, String notificationMessage)
/*     */   {
/*  80 */     NotificationEvent notificationEvent = new NotificationEvent(level, notificationMessage);
/*  81 */     notificationEvent.append(new ClientEvent(userId, true));
/*  82 */     return (NotificationEvent)eventWorkaround(notificationEvent);
/*     */   }
/*     */ 
/*     */   public Event addOrderRelation(Event event, String orderId)
/*     */   {
/*  95 */     return eventWorkaround(event.append(new OrderRelatedEvent(orderId)));
/*     */   }
/*     */ 
/*     */   public Event generateOnOrderReceiveEvent(OrderMessage order)
/*     */   {
/* 105 */     Event event = null;
/* 106 */     OrderState state = order.getOrderState();
/*     */ 
/* 108 */     if (OrderState.CREATED == state)
/* 109 */       event = createSubmitOrderEvent(order, getServiceId());
/* 110 */     else if (OrderState.CANCELLED == state)
/* 111 */       event = createOrderStateChangeEvent(order, null);
/* 112 */     else if ((OrderState.EXECUTING == state) || (OrderState.PENDING == state)) {
/* 113 */       event = createChangeOrderConditionEvent(order.getOrderId(), null, null, null, null, order.getPriceStop() == null ? null : order.getPriceStop().getValue(), order.getPriceTrailingLimit() == null ? null : order.getPriceTrailingLimit().getValue(), order.getExecTimeoutMillis(), order.getStopDirection());
/*     */     }
/*     */ 
/* 117 */     return event;
/*     */   }
/*     */ 
/*     */   public LoginEvent createClientLoginEvent(String sessionId, int userId)
/*     */   {
/* 128 */     Date now = getUTC();
/*     */ 
/* 130 */     LoginEvent loginEvent = new LoginEvent();
/* 131 */     ClientEvent clientEvent = new ClientEvent(userId, now, sessionId);
/*     */ 
/* 133 */     loginEvent.append(clientEvent);
/* 134 */     loginEvent.setTimestamp(now);
/*     */ 
/* 136 */     return (LoginEvent)eventWorkaround(loginEvent);
/*     */   }
/*     */ 
/*     */   public SubmitOrderEvent createClientSubmitOrderEvent(String sessionId, int userId, OrderGroupMessage orderGroup)
/*     */   {
/* 149 */     Date now = getUTC();
/*     */ 
/* 151 */     SubmitOrderEvent submitOrderEvent = new SubmitOrderEvent(orderGroup);
/* 152 */     ClientEvent clientEvent = new ClientEvent(userId, now, sessionId);
/*     */ 
/* 154 */     submitOrderEvent.append(clientEvent);
/* 155 */     submitOrderEvent.setTimestamp(now);
/*     */ 
/* 157 */     return (SubmitOrderEvent)eventWorkaround(submitOrderEvent);
/*     */   }
/*     */ 
/*     */   public SubmitOrderEvent createClientSubmitOrderEvent(String sessionId, int userId, OrderMessage order)
/*     */   {
/* 170 */     Date now = getUTC();
/*     */ 
/* 172 */     SubmitOrderEvent submitOrderEvent = new SubmitOrderEvent(order);
/* 173 */     ClientEvent clientEvent = new ClientEvent(userId, now, sessionId);
/*     */ 
/* 175 */     submitOrderEvent.append(clientEvent);
/* 176 */     submitOrderEvent.setTimestamp(now);
/*     */ 
/* 178 */     return (SubmitOrderEvent)eventWorkaround(submitOrderEvent);
/*     */   }
/*     */ 
/*     */   public SubmitOrderEvent createSubmitOrderEvent(OrderMessage order, String targetServiceId)
/*     */   {
/* 191 */     SubmitOrderEvent submitOrderEvent = new SubmitOrderEvent(order);
/*     */ 
/* 194 */     addOrderRelationIfNeeded(submitOrderEvent, order.getOrderId(), order.getParentOrderId());
/*     */ 
/* 196 */     submitOrderEvent = (SubmitOrderEvent)eventWorkaround(submitOrderEvent);
/* 197 */     submitOrderEvent.setTargetServiceId(targetServiceId);
/*     */ 
/* 199 */     return submitOrderEvent;
/*     */   }
/*     */ 
/*     */   public OrderStateChangeEvent createOrderStateChangeEvent(OrderMessage order, String reason)
/*     */   {
/* 210 */     OrderStateChangeEvent stateChangeEvent = new OrderStateChangeEvent(order.getOrderId(), order.getOrderState(), reason);
/* 211 */     addOrderRelationIfNeeded(stateChangeEvent, order.getOrderId(), order.getParentOrderId());
/* 212 */     return (OrderStateChangeEvent)eventWorkaround(stateChangeEvent);
/*     */   }
/*     */ 
/*     */   public ChangeOrderConditionEvent createChangeOrderConditionEvent(String orderId, BigDecimal oldPriceStop, BigDecimal oldPriceTrailingLimit, Long oldExecTimeoutMillis, StopDirection oldStopDirection, BigDecimal priceStop, BigDecimal priceTrailingLimit, Long execTimeoutMillis, StopDirection stopDirection)
/*     */   {
/* 229 */     return (ChangeOrderConditionEvent)eventWorkaround(new ChangeOrderConditionEvent(orderId, oldPriceStop, oldPriceTrailingLimit, oldExecTimeoutMillis, oldStopDirection, priceStop, priceTrailingLimit, execTimeoutMillis, stopDirection));
/*     */   }
/*     */ 
/*     */   protected Event addOrderRelationIfNeeded(Event event, String orderId, String parentOrderId)
/*     */   {
/* 245 */     if ((null != parentOrderId) && (!orderId.equals(parentOrderId))) {
/* 246 */       event.append(new OrderRelatedEvent(parentOrderId));
/*     */     }
/* 248 */     return event;
/*     */   }
/*     */ 
/*     */   protected Event eventWorkaround(Event event)
/*     */   {
/* 258 */     event.setServiceId(this.serviceId);
/* 259 */     event.setTargetServiceId(this.targetServiceId);
/* 260 */     if (null == event.getTimestamp()) {
/* 261 */       event.setTimestamp(getUTC());
/*     */     }
/* 263 */     return event;
/*     */   }
/*     */ 
/*     */   protected Date getUTC()
/*     */   {
/* 274 */     return new Date();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.EventFactory
 * JD-Core Version:    0.6.0
 */