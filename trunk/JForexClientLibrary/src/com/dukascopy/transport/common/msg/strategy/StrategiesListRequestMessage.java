/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class StrategiesListRequestMessage extends RequestMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "strategies_list";
/*    */ 
/*    */   public StrategiesListRequestMessage()
/*    */   {
/* 15 */     setType("strategies_list");
/*    */   }
/*    */ 
/*    */   public StrategiesListRequestMessage(ProtocolMessage msg) {
/* 19 */     super(msg);
/* 20 */     setType("strategies_list");
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
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategiesListRequestMessage
 * JD-Core Version:    0.6.0
 */