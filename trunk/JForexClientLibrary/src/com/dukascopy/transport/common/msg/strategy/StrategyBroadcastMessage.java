/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.util.Base64;
/*    */ 
/*    */ public class StrategyBroadcastMessage extends ProtocolMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "strategy_broadcast";
/*    */   private static final String TOPIC = "topic";
/*    */   private static final String MESSAGE = "message";
/*    */ 
/*    */   public StrategyBroadcastMessage()
/*    */   {
/* 18 */     setType("strategy_broadcast");
/*    */   }
/*    */ 
/*    */   public StrategyBroadcastMessage(ProtocolMessage msg) {
/* 22 */     super(msg);
/* 23 */     setType("strategy_broadcast");
/*    */ 
/* 25 */     setAccountName(msg.getString("account_name"));
/* 26 */     put("topic", msg.getString("topic"));
/* 27 */     put("message", msg.getString("message"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String accountName) {
/* 31 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/* 32 */       put("account_name", accountName);
/*    */   }
/*    */ 
/*    */   public String getAccountName()
/*    */   {
/* 37 */     return getString("account_name");
/*    */   }
/*    */ 
/*    */   public void setTopic(String topic) {
/* 41 */     setEncoded("topic", topic);
/*    */   }
/*    */ 
/*    */   public String getTopic() {
/* 45 */     return getDecoded("topic");
/*    */   }
/*    */ 
/*    */   public void setMessage(String message) {
/* 49 */     setEncoded("message", message);
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 53 */     return getDecoded("message");
/*    */   }
/*    */ 
/*    */   private void setEncoded(String key, String value)
/*    */   {
/* 59 */     if ((value != null) && (!value.isEmpty()))
/* 60 */       put(key, Base64.encode(value.getBytes()));
/*    */   }
/*    */ 
/*    */   private String getDecoded(String key)
/*    */   {
/* 65 */     String value = getString(key);
/* 66 */     if ((value != null) && (!value.isEmpty())) {
/* 67 */       return new String(Base64.decode(value));
/*    */     }
/* 69 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyBroadcastMessage
 * JD-Core Version:    0.6.0
 */