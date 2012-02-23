/*    */ package com.dukascopy.transport.common.msg.api.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class ChangeOrderRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "change_order";
/*    */   public static final String STOP_LOSS = "SL";
/*    */   public static final String TAKE_PROFIT = "TP";
/*    */   public static final String ENTRY = "ENTRY";
/*    */ 
/*    */   public ChangeOrderRequestMessage()
/*    */   {
/* 24 */     setType("change_order");
/*    */   }
/*    */ 
/*    */   public ChangeOrderRequestMessage(ProtocolMessage message)
/*    */   {
/* 33 */     super(message);
/*    */ 
/* 35 */     setType("change_order");
/*    */ 
/* 37 */     setOrderId(message.getString("orderId"));
/* 38 */     setParamName(message.getString("paramName"));
/* 39 */     if (message.get("newPrice") != null)
/* 40 */       setNewPrice(message.getBigDecimal("newPrice"));
/*    */   }
/*    */ 
/*    */   public void setOrderId(String orderId)
/*    */   {
/* 45 */     put("orderId", orderId);
/*    */   }
/*    */ 
/*    */   public String getOrderId() {
/* 49 */     return getString("orderId");
/*    */   }
/*    */ 
/*    */   public void setParamName(String paramName) {
/* 53 */     put("paramName", paramName);
/*    */   }
/*    */ 
/*    */   public String getParamName() {
/* 57 */     return getString("paramName");
/*    */   }
/*    */ 
/*    */   public void setNewPrice(BigDecimal newPrice) {
/* 61 */     put("newPrice", newPrice);
/*    */   }
/*    */ 
/*    */   public BigDecimal getNewPrice() {
/* 65 */     return getBigDecimal("newPrice");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.request.ChangeOrderRequestMessage
 * JD-Core Version:    0.6.0
 */