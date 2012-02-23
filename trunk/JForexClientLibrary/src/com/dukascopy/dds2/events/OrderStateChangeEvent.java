/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class OrderStateChangeEvent extends Event
/*     */ {
/*     */   public static final String ORDER_ID = "order_id";
/*     */   public static final String ORDER_STATE = "order_state";
/*     */   public static final String REASON = "reason";
/*     */   private String orderId;
/*     */   private OrderState orderState;
/*     */   private String reason;
/*     */ 
/*     */   protected OrderStateChangeEvent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public OrderStateChangeEvent(String orderId, OrderState orderState, String reason)
/*     */   {
/*  56 */     this();
/*  57 */     this.orderId = orderId;
/*  58 */     this.orderState = orderState;
/*  59 */     this.reason = reason;
/*     */   }
/*     */ 
/*     */   public OrderStateChangeEvent(String orderId, OrderState orderState)
/*     */   {
/*  71 */     this(orderId, orderState, null);
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/*  78 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/*  86 */     this.orderId = orderId;
/*     */   }
/*     */ 
/*     */   public OrderState getOrderState()
/*     */   {
/*  93 */     return this.orderState;
/*     */   }
/*     */ 
/*     */   public void setOrderState(OrderState orderState)
/*     */   {
/* 101 */     this.orderState = orderState;
/*     */   }
/*     */ 
/*     */   public String getReason()
/*     */   {
/* 108 */     return this.reason;
/*     */   }
/*     */ 
/*     */   public void setReason(String reason)
/*     */   {
/* 116 */     this.reason = reason;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getAttributes()
/*     */   {
/* 121 */     Map attributes = new HashMap();
/* 122 */     attributes.put("event_id", Long.valueOf(super.getId()));
/* 123 */     attributes.put("order_id", this.orderId);
/* 124 */     attributes.put("reason", this.reason);
/* 125 */     return Collections.unmodifiableMap(attributes);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.OrderStateChangeEvent
 * JD-Core Version:    0.6.0
 */