/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class StrategyRunResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "strategy_run_resp";
/*    */   private static final String PROCESS_DESCRIPTOR = "process_descriptor";
/*    */ 
/*    */   public StrategyRunResponseMessage()
/*    */   {
/* 17 */     setType("strategy_run_resp");
/*    */   }
/*    */ 
/*    */   public StrategyRunResponseMessage(ProtocolMessage message) {
/* 21 */     super(message);
/* 22 */     setType("strategy_run_resp");
/*    */ 
/* 24 */     setStrategyProcessDescriptor(new StrategyProcessDescriptor(message.getJSONObject("process_descriptor")));
/*    */   }
/*    */ 
/*    */   public void setStrategyProcessDescriptor(StrategyProcessDescriptor strategyProcessDescriptor) {
/* 28 */     if (strategyProcessDescriptor == null) {
/* 29 */       throw new NullPointerException("Strategy process descriptor");
/*    */     }
/* 31 */     put("process_descriptor", strategyProcessDescriptor);
/*    */   }
/*    */ 
/*    */   public StrategyProcessDescriptor getStrategyProcessDescriptor() {
/* 35 */     return new StrategyProcessDescriptor(getJSONObject("process_descriptor"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyRunResponseMessage
 * JD-Core Version:    0.6.0
 */