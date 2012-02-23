/*    */ package com.dukascopy.transport.common.msg.executor;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.OrderState;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ExecutionResultMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "execResult";
/*    */   private static final String DEAL_TYPE = "dealType";
/*    */   private static final String ORDER_ID = "orderId";
/*    */   private static final String ORDER_STATE = "state";
/*    */   private static final String NOTES = "notes";
/*    */ 
/*    */   public ExecutionResultMessage()
/*    */   {
/* 20 */     setType("execResult");
/*    */   }
/*    */ 
/*    */   public ExecutionResultMessage(ProtocolMessage message) {
/* 24 */     super(message);
/* 25 */     setType("execResult");
/* 26 */     put("dealType", message.getString("dealType"));
/* 27 */     put("orderId", message.getString("orderId"));
/* 28 */     put("state", message.getString("state"));
/* 29 */     put("notes", message.getString("notes"));
/*    */   }
/*    */ 
/*    */   public void setDealType(DealType dealType) {
/* 33 */     if (dealType != null)
/* 34 */       put("dealType", dealType.toString());
/*    */   }
/*    */ 
/*    */   public DealType getDealType()
/*    */   {
/* 39 */     String type = getString("dealType");
/* 40 */     if (type != null) {
/* 41 */       return DealType.valueOf(type);
/*    */     }
/* 43 */     return null;
/*    */   }
/*    */ 
/*    */   public void setOrderId(String orderId) {
/* 47 */     if (orderId != null)
/* 48 */       put("orderId", orderId);
/*    */   }
/*    */ 
/*    */   public String getOrderId()
/*    */   {
/* 53 */     return getString("orderId");
/*    */   }
/*    */ 
/*    */   public void setOrderState(OrderState state) {
/* 57 */     if (state != null)
/* 58 */       put("state", state.asString());
/*    */   }
/*    */ 
/*    */   public OrderState getOrderState()
/*    */   {
/* 63 */     String stateString = getString("state");
/* 64 */     if (stateString != null) {
/* 65 */       return OrderState.fromString(stateString);
/*    */     }
/* 67 */     return null;
/*    */   }
/*    */ 
/*    */   public void setNotes(String notes) {
/* 71 */     if (notes != null) {
/* 72 */       notes = notes.replaceAll("\\[|\\{|\"|\\}|\\]", "");
/* 73 */       put("notes", notes);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getNotes() {
/* 78 */     return getString("notes");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.executor.ExecutionResultMessage
 * JD-Core Version:    0.6.0
 */