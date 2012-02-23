/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class StrategyStateMessage extends ProtocolMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "strategy.state.message";
/*    */   private static final String PROCESS_DESCRIPTOR = "process.descriptor";
/*    */   private static final String STATE = "state";
/*    */   private static final String COMMENTS = "comments";
/*    */ 
/*    */   public StrategyStateMessage()
/*    */   {
/* 18 */     setType("strategy.state.message");
/*    */   }
/*    */ 
/*    */   public StrategyStateMessage(ProtocolMessage msg) {
/* 22 */     super(msg);
/* 23 */     setType("strategy.state.message");
/*    */ 
/* 25 */     setAccountName(msg.getString("account_name"));
/* 26 */     setStrategyProcessDescriptor(new StrategyProcessDescriptor(msg.getJSONObject("process.descriptor")));
/* 27 */     put("state", msg.getString("state"));
/* 28 */     put("comments", msg.getString("comments"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String accountName) {
/* 32 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/* 33 */       put("account_name", accountName);
/*    */   }
/*    */ 
/*    */   public String getAccountName()
/*    */   {
/* 38 */     return getString("account_name");
/*    */   }
/*    */ 
/*    */   public void setStrategyProcessDescriptor(StrategyProcessDescriptor strategyProcessDescriptor) {
/* 42 */     if (strategyProcessDescriptor == null) {
/* 43 */       throw new NullPointerException("Strategy process descriptor");
/*    */     }
/* 45 */     put("process.descriptor", strategyProcessDescriptor);
/*    */   }
/*    */ 
/*    */   public StrategyProcessDescriptor getStrategyProcessDescriptor() {
/* 49 */     return new StrategyProcessDescriptor(getJSONObject("process.descriptor"));
/*    */   }
/*    */ 
/*    */   public void setStrategyState(StrategyState strategyState) {
/* 53 */     if (strategyState != null)
/* 54 */       put("state", strategyState.name());
/*    */     else
/* 56 */       throw new NullPointerException("Strategy state");
/*    */   }
/*    */ 
/*    */   public StrategyState getStrategyState()
/*    */   {
/* 61 */     if (has("state")) {
/* 62 */       return StrategyState.valueOf(getString("state"));
/*    */     }
/* 64 */     return null;
/*    */   }
/*    */ 
/*    */   public void setComments(String comments)
/*    */   {
/* 69 */     if (comments != null)
/* 70 */       put("comments", comments);
/*    */   }
/*    */ 
/*    */   public String getComments()
/*    */   {
/* 75 */     return getString("comments");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyStateMessage
 * JD-Core Version:    0.6.0
 */