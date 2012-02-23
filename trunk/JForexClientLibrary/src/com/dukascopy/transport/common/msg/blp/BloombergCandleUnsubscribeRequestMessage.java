/*    */ package com.dukascopy.transport.common.msg.blp;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class BloombergCandleUnsubscribeRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "bloomberg_candle_unsubsc";
/*    */   public static final String REQUEST_ID = "id";
/*    */ 
/*    */   public BloombergCandleUnsubscribeRequestMessage()
/*    */   {
/* 12 */     setType("bloomberg_candle_unsubsc");
/*    */   }
/*    */ 
/*    */   public BloombergCandleUnsubscribeRequestMessage(ProtocolMessage message) {
/* 16 */     super(message);
/* 17 */     setType("bloomberg_candle_unsubsc");
/* 18 */     setRequestId(message.getInteger("id"));
/*    */   }
/*    */ 
/*    */   public void setRequestId(Integer count) {
/* 22 */     put("id", count);
/*    */   }
/*    */ 
/*    */   public Integer getRequestId() {
/* 26 */     return getInteger("id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergCandleUnsubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */