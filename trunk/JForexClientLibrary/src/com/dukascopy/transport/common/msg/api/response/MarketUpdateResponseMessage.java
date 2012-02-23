/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class MarketUpdateResponseMessage extends MarketStateResponseMessage
/*    */ {
/*    */   public static final String TYPE = "market_update";
/*    */ 
/*    */   public MarketUpdateResponseMessage()
/*    */   {
/* 19 */     setType("market_update");
/*    */   }
/*    */ 
/*    */   public MarketUpdateResponseMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("market_update");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.MarketUpdateResponseMessage
 * JD-Core Version:    0.6.0
 */