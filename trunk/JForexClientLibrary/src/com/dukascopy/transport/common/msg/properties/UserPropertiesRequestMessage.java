/*    */ package com.dukascopy.transport.common.msg.properties;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.strategy.IStrategyMessage;
/*    */ 
/*    */ public class UserPropertiesRequestMessage extends ProtocolMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "user.properties.request";
/*    */ 
/*    */   public UserPropertiesRequestMessage()
/*    */   {
/* 15 */     setType("user.properties.request");
/*    */   }
/*    */ 
/*    */   public UserPropertiesRequestMessage(ProtocolMessage msg) {
/* 19 */     super(msg);
/* 20 */     setType("user.properties.request");
/* 21 */     setAccountName(msg.getString("account_name"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String accountName) {
/* 25 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/* 26 */       put("account_name", accountName);
/*    */   }
/*    */ 
/*    */   public String getAccountName()
/*    */   {
/* 31 */     return getString("account_name");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.properties.UserPropertiesRequestMessage
 * JD-Core Version:    0.6.0
 */