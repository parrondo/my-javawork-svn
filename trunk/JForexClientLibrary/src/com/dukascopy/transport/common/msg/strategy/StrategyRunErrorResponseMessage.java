/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class StrategyRunErrorResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "strategy_run_error";
/*    */   public static final String RUN_REQUEST_ID = "runRequestId";
/*    */   public static final String REASON = "reason";
/*    */ 
/*    */   public StrategyRunErrorResponseMessage()
/*    */   {
/* 22 */     setType("strategy_run_error");
/*    */   }
/*    */ 
/*    */   public StrategyRunErrorResponseMessage(ProtocolMessage message)
/*    */   {
/* 32 */     super(message);
/* 33 */     setType("strategy_run_error");
/* 34 */     setRunRequestId(message.getString("runRequestId"));
/* 35 */     setReason(message.getString("reason"));
/*    */   }
/*    */ 
/*    */   public void setRunRequestId(String id)
/*    */   {
/* 41 */     put("runRequestId", id);
/*    */   }
/*    */ 
/*    */   public String getRunRequestId() {
/* 45 */     return getString("runRequestId");
/*    */   }
/*    */ 
/*    */   public void setReason(String reason)
/*    */   {
/* 50 */     put("reason", reason);
/*    */   }
/*    */ 
/*    */   public String getReason() {
/* 54 */     return getString("reason");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyRunErrorResponseMessage
 * JD-Core Version:    0.6.0
 */