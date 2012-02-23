/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.transport.client.TransportClient;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class TransportClientImpl
/*    */   implements ITransportClient
/*    */ {
/*    */   private TransportClient transportClient;
/*    */ 
/*    */   public TransportClientImpl(TransportClient transportClient)
/*    */   {
/* 17 */     this.transportClient = transportClient;
/*    */   }
/*    */ 
/*    */   public ProtocolMessage controlRequest(ProtocolMessage message)
/*    */   {
/* 22 */     return this.transportClient.controlRequest(message);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.TransportClientImpl
 * JD-Core Version:    0.6.0
 */