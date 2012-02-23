/*    */ package com.dukascopy.transport.client.events;
/*    */ 
/*    */ import com.dukascopy.transport.client.ClientListener;
/*    */ import com.dukascopy.transport.client.DisconnectReason;
/*    */ import com.dukascopy.transport.client.TransportClient;
/*    */ 
/*    */ public class DisconnectedEvent extends EventTask
/*    */ {
/*    */   private DisconnectReason reason;
/*    */ 
/*    */   public DisconnectedEvent(TransportClient client, DisconnectReason reason)
/*    */   {
/* 25 */     super(client);
/* 26 */     this.reason = reason;
/*    */   }
/*    */ 
/*    */   public void execute()
/*    */   {
/*    */     try {
/* 32 */       Thread.sleep(1000L);
/*    */     }
/*    */     catch (InterruptedException e) {
/* 35 */       e.printStackTrace();
/*    */     }
/* 37 */     this.listener.disconnected(this);
/*    */   }
/*    */ 
/*    */   public DisconnectReason getReason()
/*    */   {
/* 44 */     return this.reason;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.events.DisconnectedEvent
 * JD-Core Version:    0.6.0
 */