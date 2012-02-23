/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class LoginResponseMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "login_resp";
/*    */ 
/*    */   public LoginResponseMessage()
/*    */   {
/* 20 */     setType("login_resp");
/*    */   }
/*    */ 
/*    */   public LoginResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("login_resp");
/*    */ 
/* 33 */     setUsername(message.getString("username"));
/* 34 */     setResult(message.getString("result"));
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
/*    */   public void setResult(String result) {
/* 47 */     put("result", result);
/*    */   }
/*    */ 
/*    */   public String getResult() {
/* 51 */     return getString("result");
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
 * Qualified Name:     com.dukascopy.transport.common.msg.response.LoginResponseMessage
 * JD-Core Version:    0.6.0
 */