/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class HaloResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "challenge";
/*    */ 
/*    */   public HaloResponseMessage()
/*    */   {
/* 19 */     setType("challenge");
/*    */   }
/*    */ 
/*    */   public HaloResponseMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("challenge");
/*    */ 
/* 32 */     setSessionId(message.getString("sessionId"));
/* 33 */     setChallenge(message.getString("challenge"));
/*    */   }
/*    */ 
/*    */   public void setSessionId(String sessionId) {
/* 37 */     put("sessionId", sessionId);
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 41 */     return getString("sessionId");
/*    */   }
/*    */ 
/*    */   public void setChallenge(String challenge) {
/* 45 */     put("challenge", challenge);
/*    */   }
/*    */ 
/*    */   public String getChallenge() {
/* 49 */     return getString("challenge");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.HaloResponseMessage
 * JD-Core Version:    0.6.0
 */