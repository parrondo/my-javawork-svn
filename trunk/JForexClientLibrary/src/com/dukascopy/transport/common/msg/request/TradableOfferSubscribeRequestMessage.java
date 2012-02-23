/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class TradableOfferSubscribeRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "offer_subsc";
/*    */ 
/*    */   public TradableOfferSubscribeRequestMessage()
/*    */   {
/* 18 */     setType("offer_subsc");
/*    */   }
/*    */ 
/*    */   public TradableOfferSubscribeRequestMessage(ProtocolMessage message)
/*    */   {
/* 27 */     super(message);
/* 28 */     setType("offer_subsc");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.TradableOfferSubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */