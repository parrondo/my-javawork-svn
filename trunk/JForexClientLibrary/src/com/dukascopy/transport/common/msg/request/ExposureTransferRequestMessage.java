/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ExposureTransferRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "exp_trans";
/*    */   private static final String EXECUTOR_ID = "executor";
/*    */ 
/*    */   public ExposureTransferRequestMessage(ProtocolMessage msg)
/*    */   {
/* 18 */     super(msg);
/* 19 */     setType("exp_trans");
/* 20 */     put("executor", msg.getString("executor"));
/*    */   }
/*    */ 
/*    */   public ExposureTransferRequestMessage()
/*    */   {
/* 25 */     setType("exp_trans");
/*    */   }
/*    */ 
/*    */   public void setExecutorId(String executorId) {
/* 29 */     put("executor", executorId);
/*    */   }
/*    */ 
/*    */   public String getExecutorId() {
/* 33 */     return getString("executor");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ExposureTransferRequestMessage
 * JD-Core Version:    0.6.0
 */