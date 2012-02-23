/*    */ package com.dukascopy.transport.client.events;
/*    */ 
/*    */ import com.dukascopy.transport.client.ClientListener;
/*    */ import com.dukascopy.transport.client.TransportClient;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class FeedbackMessageReceivedEvent extends EventTask
/*    */ {
/*    */   private ProtocolMessage message;
/* 10 */   private long creationTime = System.currentTimeMillis();
/*    */ 
/*    */   public FeedbackMessageReceivedEvent(TransportClient client, ProtocolMessage message)
/*    */   {
/* 14 */     super(client);
/* 15 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public long getCreationTime()
/*    */   {
/* 21 */     return this.creationTime;
/*    */   }
/*    */ 
/*    */   public ProtocolMessage getMessage()
/*    */   {
/* 30 */     return this.message;
/*    */   }
/*    */ 
/*    */   public void setMessage(ProtocolMessage message)
/*    */   {
/* 38 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public void execute()
/*    */   {
/* 43 */     this.listener.feedbackMessageReceived(this.client, this.message);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.events.FeedbackMessageReceivedEvent
 * JD-Core Version:    0.6.0
 */