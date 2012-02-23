/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ExecutorLockedMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "exec_locked";
/*    */ 
/*    */   public ExecutorLockedMessage(Boolean isLocked)
/*    */   {
/* 14 */     setType("exec_locked");
/* 15 */     put("locked", isLocked);
/*    */   }
/*    */ 
/*    */   public ExecutorLockedMessage(ProtocolMessage message)
/*    */   {
/* 24 */     super(message);
/* 25 */     setType("exec_locked");
/* 26 */     put("locked", message.getBoolean("locked"));
/*    */   }
/*    */ 
/*    */   public void setLocked(Boolean isLocked)
/*    */   {
/* 31 */     put("locked", isLocked);
/*    */   }
/*    */ 
/*    */   public boolean isLocked() {
/* 35 */     return getBoolean("locked");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ExecutorLockedMessage
 * JD-Core Version:    0.6.0
 */