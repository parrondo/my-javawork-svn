/*    */ package com.dukascopy.transport.common.msg.api.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class ChangePositionRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "change_position";
/*    */   public static final String STOP_LOSS = "SL";
/*    */   public static final String TAKE_PROFIT = "TP";
/*    */ 
/*    */   public ChangePositionRequestMessage()
/*    */   {
/* 23 */     setType("change_position");
/*    */   }
/*    */ 
/*    */   public ChangePositionRequestMessage(ProtocolMessage message)
/*    */   {
/* 32 */     super(message);
/*    */ 
/* 34 */     setType("change_position");
/*    */ 
/* 36 */     setPositionId(message.getString("positionId"));
/* 37 */     setParamName(message.getString("paramName"));
/* 38 */     if (message.get("newPrice") != null)
/* 39 */       setNewPrice(message.getBigDecimal("newPrice"));
/*    */   }
/*    */ 
/*    */   public void setPositionId(String positionId)
/*    */   {
/* 44 */     put("positionId", positionId);
/*    */   }
/*    */ 
/*    */   public String getPositionId() {
/* 48 */     return getString("positionId");
/*    */   }
/*    */ 
/*    */   public void setParamName(String paramName) {
/* 52 */     put("paramName", paramName);
/*    */   }
/*    */ 
/*    */   public String getParamName() {
/* 56 */     return getString("paramName");
/*    */   }
/*    */ 
/*    */   public void setNewPrice(BigDecimal newPrice) {
/* 60 */     put("newPrice", newPrice);
/*    */   }
/*    */ 
/*    */   public BigDecimal getNewPrice() {
/* 64 */     return getBigDecimal("newPrice");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.request.ChangePositionRequestMessage
 * JD-Core Version:    0.6.0
 */