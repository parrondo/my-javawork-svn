/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class PersonalNotificationMessage extends NotificationMessage
/*    */ {
/*    */   public static final String TYPE = "personal_notification";
/*    */   public static final String TARGET_USER_NAME = "target_user_name";
/*    */ 
/*    */   public PersonalNotificationMessage()
/*    */   {
/* 16 */     setType("personal_notification");
/*    */   }
/*    */ 
/*    */   public PersonalNotificationMessage(ProtocolMessage message) {
/* 20 */     super(message);
/* 21 */     setType("personal_notification");
/*    */   }
/*    */ 
/*    */   public String getUserName() {
/* 25 */     return getString("target_user_name");
/*    */   }
/*    */ 
/*    */   public void setUserName(String userName) {
/* 29 */     put("target_user_name", userName);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.PersonalNotificationMessage
 * JD-Core Version:    0.6.0
 */