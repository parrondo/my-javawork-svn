/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.IMessage.Type;
/*    */ import com.dukascopy.api.IStrategyBroadcastMessage;
/*    */ 
/*    */ public class StrategyBroadcastMessageImpl extends PlatformMessageImpl
/*    */   implements IStrategyBroadcastMessage
/*    */ {
/*    */   private static final String FORMAT = "Strategy Broadcast Message. Topic : [%1$s] Message : [%2$s]";
/*    */   private final String topic;
/*    */ 
/*    */   public StrategyBroadcastMessageImpl(String topic, String content, long creationTime)
/*    */   {
/* 14 */     super(content, null, IMessage.Type.STRATEGY_BROADCAST, creationTime);
/* 15 */     this.topic = topic;
/*    */   }
/*    */ 
/*    */   public String getTopic()
/*    */   {
/* 20 */     return this.topic;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 25 */     return String.format("Strategy Broadcast Message. Topic : [%1$s] Message : [%2$s]", new Object[] { this.topic, getContent() });
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.StrategyBroadcastMessageImpl
 * JD-Core Version:    0.6.0
 */