/*    */ package com.dukascopy.transport.common.msg.executor;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.OrderSide;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class OrderCancelMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "ordCancel";
/*    */   private static final String ORDER_ID = "orderId";
/*    */   private static final String INSTRUMENT = "instr";
/*    */   private static final String SIDE = "side";
/*    */ 
/*    */   public OrderCancelMessage()
/*    */   {
/* 18 */     setType("ordCancel");
/*    */   }
/*    */ 
/*    */   public OrderCancelMessage(ProtocolMessage message) {
/* 22 */     super(message);
/* 23 */     setType("ordCancel");
/* 24 */     put("orderId", message.getString("orderId"));
/* 25 */     put("instr", message.getString("instr"));
/* 26 */     put("side", message.getString("side"));
/*    */   }
/*    */ 
/*    */   public void setOrderId(String orderId) {
/* 30 */     if (orderId != null)
/* 31 */       put("orderId", orderId);
/*    */   }
/*    */ 
/*    */   public String getOrderId()
/*    */   {
/* 36 */     return getString("orderId");
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 40 */     if (instrument != null)
/* 41 */       put("instr", instrument);
/*    */   }
/*    */ 
/*    */   public String getInstrument()
/*    */   {
/* 46 */     return getString("instr");
/*    */   }
/*    */ 
/*    */   public void setSide(OrderSide side) {
/* 50 */     if (side != null)
/* 51 */       put("side", side.asString());
/*    */   }
/*    */ 
/*    */   public OrderSide getOrderSide()
/*    */   {
/* 56 */     String sideString = getString("side");
/* 57 */     if (sideString != null) {
/* 58 */       return OrderSide.fromString(sideString);
/*    */     }
/* 60 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.executor.OrderCancelMessage
 * JD-Core Version:    0.6.0
 */