/*    */ package com.dukascopy.transport.common.msg.state;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class SessionStatusMessage extends ProtocolMessage
/*    */ {
/*    */   public String getIpAddress()
/*    */   {
/*  8 */     return getString("ipAddress");
/*    */   }
/*    */ 
/*    */   public void setIpAddress(String ipAddress) {
/* 12 */     put("ipAddress", ipAddress);
/*    */   }
/*    */ 
/*    */   public String getUsername() {
/* 16 */     return getString("username");
/*    */   }
/*    */ 
/*    */   public void setUsername(String username) {
/* 20 */     put("username", username);
/*    */   }
/*    */ 
/*    */   public Long getLoginTime() {
/* 24 */     return getLong("loginTime");
/*    */   }
/*    */ 
/*    */   public void setLoginTime(long loginTime) {
/* 28 */     put("loginTime", loginTime);
/*    */   }
/*    */ 
/*    */   public String getUserAgent() {
/* 32 */     return getString("userAgent");
/*    */   }
/*    */ 
/*    */   public void setUserAgent(String userAgent)
/*    */   {
/* 37 */     put("userAgent", userAgent);
/*    */   }
/*    */ 
/*    */   public Long getSinceLastHeartbeat()
/*    */   {
/* 42 */     return getLong("sinceLastHeartbeat");
/*    */   }
/*    */ 
/*    */   public void setSinceLastHeartbeat(long sinceLastHeartbeat) {
/* 46 */     put("sinceLastHeartbeat", sinceLastHeartbeat);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.state.SessionStatusMessage
 * JD-Core Version:    0.6.0
 */