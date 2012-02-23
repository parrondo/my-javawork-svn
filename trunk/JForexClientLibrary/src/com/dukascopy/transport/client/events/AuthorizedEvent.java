/*    */ package com.dukascopy.transport.client.events;
/*    */ 
/*    */ import com.dukascopy.transport.client.ClientListener;
/*    */ import com.dukascopy.transport.client.TransportClient;
/*    */ 
/*    */ public class AuthorizedEvent extends EventTask
/*    */ {
/*    */   public AuthorizedEvent(TransportClient client)
/*    */   {
/* 12 */     super(client);
/*    */   }
/*    */ 
/*    */   public void execute()
/*    */   {
/* 17 */     this.listener.authorized(this.client);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.events.AuthorizedEvent
 * JD-Core Version:    0.6.0
 */