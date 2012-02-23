/*    */ package com.dukascopy.transport.common.msg.state.session;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.state.StatePropertyMessage;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class SessionPropertyStateMessage extends StatePropertyMessage
/*    */ {
/*    */   public static final String TYPE = "state_session";
/*    */   public static final String USER_ID_PROPERTY_NAME = "uid";
/*    */   public static final String SESSION_TYPE_PROPERTY_NAME = "stype";
/*    */   public static final String CREATION_TIME_PROPERTY_NAME = "ctime";
/*    */   public static final String SESSION_TYPE_API = "a";
/*    */   public static final String SESSION_TYPE_TRANSPORT = "t";
/*    */ 
/*    */   public SessionPropertyStateMessage()
/*    */   {
/* 30 */     setType("state_session");
/*    */   }
/*    */ 
/*    */   public SessionPropertyStateMessage(String userID, String sessionType, Date creationTime) {
/* 34 */     this();
/* 35 */     setUserId(userID);
/* 36 */     setSessionType(sessionType);
/* 37 */     setCreationTime(creationTime);
/*    */   }
/*    */ 
/*    */   public SessionPropertyStateMessage(ProtocolMessage msg) {
/* 41 */     super(msg);
/* 42 */     setType("state_session");
/* 43 */     put("uid", msg.getString("uid"));
/* 44 */     put("stype", msg.getString("stype"));
/* 45 */     putDate("ctime", msg.getDate("ctime"));
/*    */   }
/*    */ 
/*    */   public String getUserId() {
/* 49 */     return getString("uid");
/*    */   }
/*    */ 
/*    */   public void setUserId(String userId) {
/* 53 */     put("uid", userId);
/*    */   }
/*    */ 
/*    */   public void setCreationTime(Date creationTime) {
/* 57 */     putDate("ctime", creationTime);
/*    */   }
/*    */ 
/*    */   public Date getCreationTime() {
/* 61 */     return getDate("ctime");
/*    */   }
/*    */ 
/*    */   public String getSessionType() {
/* 65 */     return getString("stype");
/*    */   }
/*    */ 
/*    */   public void setSessionType(String sessionType) {
/* 69 */     put("stype", sessionType);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.state.session.SessionPropertyStateMessage
 * JD-Core Version:    0.6.0
 */