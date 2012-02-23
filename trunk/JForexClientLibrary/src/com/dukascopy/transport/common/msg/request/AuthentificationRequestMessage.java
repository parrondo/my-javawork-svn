/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class AuthentificationRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "authreq";
/*    */   public static final String USERNAME = "username";
/*    */   public static final String TICKET = "ticket";
/*    */   public static final String CHALLENGE = "challenge";
/*    */   public static final String MANAGER_LOGIN = "mngrlogin";
/*    */   public static final String MANAGER_PSWD_HASH = "mngrpswdhash";
/*    */   public static final String ACTION = "action";
/*    */   public static final String ACTION_LOGIN = "action_login";
/*    */   public static final String ACTION_LOGOFF = "action_logout";
/*    */   public static final String ACTION_INTERRUPTED = "action_iterrupted";
/*    */   public static final String TRANSPORT_SESSION_ID = "sid";
/*    */ 
/*    */   public AuthentificationRequestMessage()
/*    */   {
/* 26 */     setType("authreq");
/*    */   }
/*    */ 
/*    */   public AuthentificationRequestMessage(ProtocolMessage message) {
/* 30 */     super(message);
/* 31 */     setType("authreq");
/* 32 */     put("username", message.getString("username"));
/* 33 */     put("ticket", message.getString("ticket"));
/* 34 */     put("challenge", message.getString("challenge"));
/* 35 */     put("mngrlogin", message.getString("mngrlogin"));
/* 36 */     put("mngrpswdhash", message.getString("mngrpswdhash"));
/* 37 */     put("action", message.getString("action"));
/* 38 */     put("sid", message.getString("sid"));
/*    */   }
/*    */ 
/*    */   public AuthentificationRequestMessage(String username, String ticket, String challenge)
/*    */   {
/* 43 */     setType("authreq");
/* 44 */     put("username", username);
/* 45 */     put("ticket", ticket);
/* 46 */     put("challenge", challenge);
/*    */   }
/*    */ 
/*    */   public String getAction() {
/* 50 */     return getString("action");
/*    */   }
/*    */ 
/*    */   public void setAction(String action) {
/* 54 */     put("action", action);
/*    */   }
/*    */ 
/*    */   public String getUserName() {
/* 58 */     return getString("username");
/*    */   }
/*    */ 
/*    */   public String getTicket() {
/* 62 */     return getString("ticket");
/*    */   }
/*    */ 
/*    */   public String getChallenge() {
/* 66 */     return getString("challenge");
/*    */   }
/*    */ 
/*    */   public String getMngrLogin()
/*    */   {
/* 71 */     return getString("mngrlogin");
/*    */   }
/*    */ 
/*    */   public String getTransportSID() {
/* 75 */     return getString("sid");
/*    */   }
/*    */ 
/*    */   public String getMngrPswdHash() {
/* 79 */     return getString("mngrpswdhash");
/*    */   }
/*    */ 
/*    */   public void setMngrLogin(String login) {
/* 83 */     put("mngrlogin", login);
/*    */   }
/*    */ 
/*    */   public void setMngrPswdHash(String pswdHash) {
/* 87 */     put("mngrpswdhash", pswdHash);
/*    */   }
/*    */ 
/*    */   public boolean isManager() {
/* 91 */     return (has("mngrlogin")) && (has("mngrpswdhash"));
/*    */   }
/*    */ 
/*    */   public void setTransportSID(String transportSid) {
/* 95 */     put("sid", transportSid);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.AuthentificationRequestMessage
 * JD-Core Version:    0.6.0
 */