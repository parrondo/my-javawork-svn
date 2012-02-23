/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class PlaceOrderOkResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "place_order_ok";
/*    */ 
/*    */   public PlaceOrderOkResponseMessage()
/*    */   {
/* 19 */     setType("place_order_ok");
/*    */   }
/*    */ 
/*    */   public PlaceOrderOkResponseMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("place_order_ok");
/* 31 */     setOrderId(message.getString("orderId"));
/*    */   }
/*    */ 
/*    */   public void setOrderId(String orderId) {
/* 35 */     put("orderId", orderId);
/*    */   }
/*    */ 
/*    */   public String getOrderId() {
/* 39 */     return getString("orderId");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.PlaceOrderOkResponseMessage
 * JD-Core Version:    0.6.0
 */