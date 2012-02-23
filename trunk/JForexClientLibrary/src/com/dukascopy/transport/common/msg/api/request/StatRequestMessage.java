/*    */ package com.dukascopy.transport.common.msg.api.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class StatRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "stat";
/*    */ 
/*    */   public StatRequestMessage()
/*    */   {
/* 19 */     setType("stat");
/*    */   }
/*    */ 
/*    */   public StatRequestMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("stat");
/*    */ 
/* 32 */     setFlags(message.getString("flags"));
/*    */   }
/*    */ 
/*    */   public void setFlags(String flags) {
/* 36 */     put("flags", flags);
/*    */   }
/*    */ 
/*    */   public String getFlags() {
/* 40 */     return getString("flags");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.request.StatRequestMessage
 * JD-Core Version:    0.6.0
 */