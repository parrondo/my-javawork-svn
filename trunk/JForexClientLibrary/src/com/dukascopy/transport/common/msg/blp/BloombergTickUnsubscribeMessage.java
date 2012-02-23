/*    */ package com.dukascopy.transport.common.msg.blp;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class BloombergTickUnsubscribeMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "blp_tick_unsubsc";
/*    */   public static final String REQUEST_ID = "id";
/*    */ 
/*    */   public BloombergTickUnsubscribeMessage()
/*    */   {
/* 11 */     setType("blp_tick_unsubsc");
/*    */   }
/*    */ 
/*    */   public BloombergTickUnsubscribeMessage(ProtocolMessage message) {
/* 15 */     super(message);
/* 16 */     setType("blp_tick_unsubsc");
/* 17 */     setRequestId(message.getInteger("id"));
/*    */   }
/*    */ 
/*    */   public void setRequestId(Integer count) {
/* 21 */     put("id", count);
/*    */   }
/*    */ 
/*    */   public Integer getRequestId() {
/* 25 */     return getInteger("id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergTickUnsubscribeMessage
 * JD-Core Version:    0.6.0
 */