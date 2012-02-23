/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class CancelOrderRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "cancel_order";
/*    */ 
/*    */   public CancelOrderRequestMessage()
/*    */   {
/* 20 */     setType("cancel_order");
/*    */   }
/*    */ 
/*    */   public CancelOrderRequestMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("cancel_order");
/*    */ 
/* 33 */     setOrderId(message.getInteger("orderId"));
/*    */   }
/*    */ 
/*    */   public void setOrderId(Integer orderId) {
/* 37 */     put("orderId", orderId);
/*    */   }
/*    */ 
/*    */   public Integer getOrderId() {
/* 41 */     return getInteger("orderId");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CancelOrderRequestMessage
 * JD-Core Version:    0.6.0
 */