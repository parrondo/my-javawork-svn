/*    */ package com.dukascopy.dds2.events;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class OrderRelatedEvent extends Event
/*    */ {
/*    */   public static final String ORDER_ID = "order_id";
/*    */   private String orderId;
/*    */ 
/*    */   protected OrderRelatedEvent()
/*    */   {
/*    */   }
/*    */ 
/*    */   public OrderRelatedEvent(String orderId)
/*    */   {
/* 25 */     this();
/* 26 */     setOrderId(orderId);
/*    */   }
/*    */ 
/*    */   public OrderRelatedEvent(OrderMessage order)
/*    */   {
/* 36 */     this();
/* 37 */     setOrderId(order.getOrderId());
/*    */   }
/*    */ 
/*    */   public void setOrderId(String orderId)
/*    */   {
/* 46 */     this.orderId = orderId;
/*    */   }
/*    */ 
/*    */   public String getOrderId()
/*    */   {
/* 55 */     return this.orderId;
/*    */   }
/*    */ 
/*    */   public Map<String, Object> getAttributes()
/*    */   {
/* 60 */     Map attributes = new HashMap();
/* 61 */     attributes.put("event_id", Long.valueOf(super.getId()));
/* 62 */     attributes.put("order_id", this.orderId);
/* 63 */     return Collections.unmodifiableMap(attributes);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.OrderRelatedEvent
 * JD-Core Version:    0.6.0
 */