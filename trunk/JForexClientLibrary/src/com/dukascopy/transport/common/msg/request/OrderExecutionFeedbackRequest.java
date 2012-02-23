/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class OrderExecutionFeedbackRequest extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "orderExFeedbackRequest";
/*    */   public static final String ORDER_ID = "orderId";
/*    */ 
/*    */   public OrderExecutionFeedbackRequest(String orderId)
/*    */   {
/* 13 */     setType("orderExFeedbackRequest");
/* 14 */     setOrderId(orderId);
/*    */   }
/*    */ 
/*    */   public OrderExecutionFeedbackRequest(ProtocolMessage protocolMessage) {
/* 18 */     super(protocolMessage);
/* 19 */     setType("orderExFeedbackRequest");
/* 20 */     setOrderId(protocolMessage.getString("orderId"));
/*    */   }
/*    */ 
/*    */   public String getOrderId() {
/* 24 */     return getString("orderId");
/*    */   }
/*    */ 
/*    */   public void setOrderId(String orderId) {
/* 28 */     put("orderId", orderId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.OrderExecutionFeedbackRequest
 * JD-Core Version:    0.6.0
 */