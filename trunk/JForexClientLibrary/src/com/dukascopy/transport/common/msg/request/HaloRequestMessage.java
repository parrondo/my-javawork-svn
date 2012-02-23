/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class HaloRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "halo";
/*    */ 
/*    */   public HaloRequestMessage()
/*    */   {
/* 19 */     setType("halo");
/* 20 */     setUseragent("notDef");
/*    */   }
/*    */ 
/*    */   public HaloRequestMessage(ProtocolMessage message)
/*    */   {
/* 30 */     super(message);
/* 31 */     setType("halo");
/* 32 */     setUseragent(message.getString("useragent"));
/* 33 */     setPingable(message.getBoolean("ping"));
/*    */   }
/*    */ 
/*    */   public void setUseragent(String username) {
/* 37 */     put("useragent", username);
/*    */   }
/*    */ 
/*    */   public String getUseragent() {
/* 41 */     return getString("useragent");
/*    */   }
/*    */ 
/*    */   public void setPingable(boolean pingable) {
/* 45 */     put("ping", pingable);
/*    */   }
/*    */ 
/*    */   public boolean isPingable() {
/* 49 */     return getBoolean("ping");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.HaloRequestMessage
 * JD-Core Version:    0.6.0
 */