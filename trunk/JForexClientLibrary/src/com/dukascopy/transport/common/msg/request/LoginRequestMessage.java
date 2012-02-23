/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class LoginRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "login";
/*    */ 
/*    */   public LoginRequestMessage()
/*    */   {
/* 20 */     setType("login");
/*    */   }
/*    */ 
/*    */   public LoginRequestMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("login");
/*    */ 
/* 33 */     setUsername(message.getString("username"));
/* 34 */     setPasswordHash(message.getString("passwordHash"));
/* 35 */     setMode(message.getInteger("mode"));
/*    */   }
/*    */ 
/*    */   public void setUsername(String username) {
/* 39 */     put("username", username);
/*    */   }
/*    */ 
/*    */   public String getUsername() {
/* 43 */     return getString("username");
/*    */   }
/*    */ 
/*    */   public void setPasswordHash(String passwordHash) {
/* 47 */     put("passwordHash", passwordHash);
/*    */   }
/*    */ 
/*    */   public String getPasswordHash() {
/* 51 */     return getString("passwordHash");
/*    */   }
/*    */ 
/*    */   public void setMode(Integer mode) {
/* 55 */     put("mode", mode);
/*    */   }
/*    */ 
/*    */   public Integer getMode() {
/* 59 */     return getInteger("mode");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.LoginRequestMessage
 * JD-Core Version:    0.6.0
 */