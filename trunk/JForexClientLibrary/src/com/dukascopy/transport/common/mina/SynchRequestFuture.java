/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class SynchRequestFuture
/*    */ {
/*    */   private ProtocolMessage response;
/*    */   private byte[] data;
/* 11 */   private boolean requestInProcess = false;
/*    */ 
/* 13 */   private Long lastResponseTime = Long.valueOf(System.currentTimeMillis());
/*    */ 
/* 15 */   private boolean continueProcess = false;
/*    */ 
/*    */   public ProtocolMessage getResponse()
/*    */   {
/* 21 */     return this.response;
/*    */   }
/*    */ 
/*    */   public void setResponse(ProtocolMessage response)
/*    */   {
/* 29 */     this.response = response;
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 36 */     return this.data;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] data)
/*    */   {
/* 44 */     this.data = data;
/*    */   }
/*    */ 
/*    */   public boolean isRequestInProcess()
/*    */   {
/* 53 */     return this.requestInProcess;
/*    */   }
/*    */ 
/*    */   public void setRequestInProcess(boolean requestInProcess)
/*    */   {
/* 60 */     this.requestInProcess = requestInProcess;
/*    */   }
/*    */ 
/*    */   public Long getLastResponseTime()
/*    */   {
/* 67 */     return this.lastResponseTime;
/*    */   }
/*    */ 
/*    */   public void setLastResponseTime(Long lastResponseTime)
/*    */   {
/* 75 */     this.lastResponseTime = lastResponseTime;
/*    */   }
/*    */ 
/*    */   public boolean isContinueProcess()
/*    */   {
/* 82 */     return this.continueProcess;
/*    */   }
/*    */ 
/*    */   public void setContinueProcess(boolean continueProcess)
/*    */   {
/* 89 */     this.continueProcess = continueProcess;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.SynchRequestFuture
 * JD-Core Version:    0.6.0
 */