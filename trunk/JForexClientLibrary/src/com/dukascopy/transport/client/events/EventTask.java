/*    */ package com.dukascopy.transport.client.events;
/*    */ 
/*    */ import com.dukascopy.transport.client.ClientListener;
/*    */ import com.dukascopy.transport.client.TransportClient;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class EventTask
/*    */   implements Runnable
/*    */ {
/*    */   protected TransportClient client;
/*    */   protected ClientListener listener;
/* 24 */   private static final Logger log = LoggerFactory.getLogger(EventTask.class);
/*    */ 
/*    */   public EventTask(TransportClient client)
/*    */   {
/* 28 */     this.client = client;
/* 29 */     this.listener = client.getListener();
/*    */   }
/*    */ 
/*    */   public void run() {
/*    */     try {
/* 34 */       execute();
/*    */     } catch (Exception e) {
/* 36 */       log.error("Runnable execution error", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public abstract void execute();
/*    */ 
/*    */   public TransportClient getClient()
/*    */   {
/* 46 */     return this.client;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.events.EventTask
 * JD-Core Version:    0.6.0
 */