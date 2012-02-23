/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class CustodianOnlineMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "cust_online";
/*    */ 
/*    */   public CustodianOnlineMessage(Boolean isOnline)
/*    */   {
/* 21 */     setType("cust_online");
/* 22 */     put("online_flag", isOnline);
/*    */   }
/*    */ 
/*    */   public CustodianOnlineMessage(ProtocolMessage message)
/*    */   {
/* 31 */     super(message);
/* 32 */     setType("cust_online");
/* 33 */     put("online_flag", message.getBoolean("online_flag"));
/*    */   }
/*    */ 
/*    */   public void setOnline(Boolean isOnline)
/*    */   {
/* 38 */     put("online_flag", isOnline);
/*    */   }
/*    */ 
/*    */   public boolean isOnline() {
/* 42 */     return getBoolean("online_flag");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CustodianOnlineMessage
 * JD-Core Version:    0.6.0
 */