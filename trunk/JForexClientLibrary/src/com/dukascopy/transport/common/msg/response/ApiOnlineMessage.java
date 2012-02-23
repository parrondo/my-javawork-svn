/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ApiOnlineMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "api_online";
/*    */ 
/*    */   public ApiOnlineMessage(Boolean isOnline)
/*    */   {
/* 13 */     setType("api_online");
/* 14 */     put("online_flag", isOnline);
/*    */   }
/*    */ 
/*    */   public ApiOnlineMessage(ProtocolMessage message)
/*    */   {
/* 24 */     super(message);
/* 25 */     setType("api_online");
/* 26 */     put("online_flag", message.getBoolean("online_flag"));
/*    */   }
/*    */ 
/*    */   public void setOnline(Boolean isOnline)
/*    */   {
/* 31 */     put("online_flag", isOnline);
/*    */   }
/*    */ 
/*    */   public boolean isOnline() {
/* 35 */     return getBoolean("online_flag");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ApiOnlineMessage
 * JD-Core Version:    0.6.0
 */