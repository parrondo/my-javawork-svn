/*    */ package com.dukascopy.transport.common.msg.blp;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class BloombergSwapUnsubscribeRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "blp_swap_unsubsc";
/*    */   public static final String REQUEST_ID = "id";
/*    */ 
/*    */   public BloombergSwapUnsubscribeRequestMessage()
/*    */   {
/* 12 */     setType("blp_swap_unsubsc");
/*    */   }
/*    */ 
/*    */   public BloombergSwapUnsubscribeRequestMessage(ProtocolMessage message) {
/* 16 */     super(message);
/* 17 */     setType("blp_swap_unsubsc");
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
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergSwapUnsubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */