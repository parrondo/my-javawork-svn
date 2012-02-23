/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class MarketStateRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "market_state";
/*    */ 
/*    */   public MarketStateRequestMessage()
/*    */   {
/* 20 */     setType("market_state");
/*    */   }
/*    */ 
/*    */   public MarketStateRequestMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("market_state");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.MarketStateRequestMessage
 * JD-Core Version:    0.6.0
 */