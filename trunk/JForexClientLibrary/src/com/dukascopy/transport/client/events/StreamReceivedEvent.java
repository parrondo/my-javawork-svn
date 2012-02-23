/*    */ package com.dukascopy.transport.client.events;
/*    */ 
/*    */ import com.dukascopy.transport.client.BlockingBinaryStream;
/*    */ import com.dukascopy.transport.client.StreamListener;
/*    */ import com.dukascopy.transport.client.TransportClient;
/*    */ 
/*    */ public class StreamReceivedEvent extends EventTask
/*    */ {
/*    */   BlockingBinaryStream stream;
/*    */ 
/*    */   public StreamReceivedEvent(TransportClient client, BlockingBinaryStream stream)
/*    */   {
/* 11 */     super(client);
/* 12 */     this.stream = stream;
/*    */   }
/*    */ 
/*    */   public void execute()
/*    */   {
/* 17 */     if (this.client.getStreamListener() != null)
/* 18 */       this.client.getStreamListener().handleStream(this.stream.getStreamId(), this.stream);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.events.StreamReceivedEvent
 * JD-Core Version:    0.6.0
 */