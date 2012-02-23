/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class HaloResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "challenge";
/*    */ 
/*    */   public HaloResponseMessage()
/*    */   {
/* 20 */     setType("challenge");
/*    */   }
/*    */ 
/*    */   public HaloResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("challenge");
/*    */ 
/* 33 */     setSessionId(message.getString("sessionId"));
/* 34 */     setChallenge(message.getString("challenge"));
/*    */   }
/*    */ 
/*    */   public void setSessionId(String sessionId) {
/* 38 */     put("sessionId", sessionId);
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 42 */     return getString("sessionId");
/*    */   }
/*    */ 
/*    */   public void setChallenge(String challenge) {
/* 46 */     put("challenge", challenge);
/*    */   }
/*    */ 
/*    */   public String getChallenge() {
/* 50 */     return getString("challenge");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.HaloResponseMessage
 * JD-Core Version:    0.6.0
 */