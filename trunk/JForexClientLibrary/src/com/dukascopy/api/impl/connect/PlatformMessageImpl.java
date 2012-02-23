/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.IMessage;
/*    */ import com.dukascopy.api.IMessage.Type;
/*    */ import com.dukascopy.api.IOrder;
/*    */ 
/*    */ public class PlatformMessageImpl
/*    */   implements IMessage
/*    */ {
/*    */   private final IMessage.Type messageType;
/*    */   private final IOrder relatedOrder;
/*    */   private final String content;
/*    */   private final long creationTime;
/*    */ 
/*    */   public PlatformMessageImpl(String content, IOrder relatedOrder, IMessage.Type messageType, long creationTime)
/*    */   {
/* 21 */     this.messageType = messageType;
/* 22 */     this.relatedOrder = relatedOrder;
/* 23 */     this.content = content;
/* 24 */     this.creationTime = creationTime;
/*    */   }
/*    */ 
/*    */   public String getContent()
/*    */   {
/* 31 */     return this.content;
/*    */   }
/*    */ 
/*    */   public final IOrder getOrder()
/*    */   {
/* 38 */     return this.relatedOrder;
/*    */   }
/*    */ 
/*    */   public final IMessage.Type getType() {
/* 42 */     return this.messageType;
/*    */   }
/*    */ 
/*    */   public long getCreationTime()
/*    */   {
/* 47 */     return this.creationTime;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 52 */     return "MessageType " + getType() + " Text " + getContent() + " Related order " + getOrder();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.PlatformMessageImpl
 * JD-Core Version:    0.6.0
 */