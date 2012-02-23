/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class StrategyStopRequestMessage extends RequestMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "strategy_stop";
/*    */   private static final String PID = "pid";
/*    */   private static final String FILE_ID = "file_id";
/*    */ 
/*    */   public StrategyStopRequestMessage()
/*    */   {
/* 18 */     setType("strategy_stop");
/*    */   }
/*    */ 
/*    */   public StrategyStopRequestMessage(ProtocolMessage msg) {
/* 22 */     super(msg);
/* 23 */     setType("strategy_stop");
/*    */ 
/* 25 */     setAccountName(msg.getString("account_name"));
/* 26 */     setPid(msg.getString("pid"));
/* 27 */     setFileId(msg.getLong("file_id"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String accountName) {
/* 31 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/* 32 */       put("account_name", accountName);
/*    */   }
/*    */ 
/*    */   public String getAccountName()
/*    */   {
/* 37 */     return getString("account_name");
/*    */   }
/*    */ 
/*    */   public void setPid(String pid) {
/* 41 */     if ((pid == null) || (pid.isEmpty())) {
/* 42 */       throw new IllegalArgumentException("PID is empty");
/*    */     }
/* 44 */     put("pid", pid);
/*    */   }
/*    */ 
/*    */   public String getPid() {
/* 48 */     return getString("pid");
/*    */   }
/*    */ 
/*    */   public void setFileId(Long fileId) {
/* 52 */     putOpt("pid", fileId);
/*    */   }
/*    */ 
/*    */   public Long getFileId() {
/* 56 */     return getLong("file_id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyStopRequestMessage
 * JD-Core Version:    0.6.0
 */