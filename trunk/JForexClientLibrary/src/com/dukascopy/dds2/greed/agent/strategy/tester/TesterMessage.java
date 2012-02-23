/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.IMessage;
/*    */ import com.dukascopy.api.IMessage.Type;
/*    */ import com.dukascopy.api.IOrder;
/*    */ 
/*    */ public class TesterMessage
/*    */   implements IMessage
/*    */ {
/*    */   private String content;
/*    */   private IMessage.Type messageType;
/*    */   private IOrder order;
/*    */   private long creationTime;
/*    */ 
/*    */   public TesterMessage(String content, IMessage.Type messageType, IOrder order, long creationTime)
/*    */   {
/* 17 */     this.content = content;
/* 18 */     this.messageType = messageType;
/* 19 */     this.order = order;
/* 20 */     this.creationTime = creationTime;
/*    */   }
/*    */ 
/*    */   public String getContent() {
/* 24 */     return this.content;
/*    */   }
/*    */ 
/*    */   public IMessage.Type getType() {
/* 28 */     return this.messageType;
/*    */   }
/*    */ 
/*    */   public IOrder getOrder() {
/* 32 */     return this.order;
/*    */   }
/*    */ 
/*    */   public long getCreationTime()
/*    */   {
/* 37 */     return this.creationTime;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 42 */     return this.messageType.name() + " - " + this.content + " order: " + this.order;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterMessage
 * JD-Core Version:    0.6.0
 */