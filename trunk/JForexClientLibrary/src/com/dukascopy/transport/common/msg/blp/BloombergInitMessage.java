/*    */ package com.dukascopy.transport.common.msg.blp;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class BloombergInitMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "blinitmsg";
/*    */   public static final String BLOOMBERG_TYPE = "bloomberg_type";
/*    */   public static final String START_TIMESTAMP = "start_timestamp";
/*    */   public static final String END_TIMESTAMP = "end_timestamp";
/*    */ 
/*    */   public BloombergInitMessage()
/*    */   {
/* 13 */     setType("blinitmsg");
/*    */   }
/*    */ 
/*    */   public BloombergInitMessage(ProtocolMessage message) {
/* 17 */     super(message);
/* 18 */     setType("blinitmsg");
/*    */   }
/*    */ 
/*    */   public void setStartTimestamp(Long startTimestamp) {
/* 22 */     if (null != startTimestamp)
/* 23 */       put("start_timestamp", startTimestamp);
/*    */     else
/* 25 */       put("start_timestamp", null);
/*    */   }
/*    */ 
/*    */   public Long getStartTimestamp()
/*    */   {
/* 30 */     return getLong("start_timestamp");
/*    */   }
/*    */ 
/*    */   public void setEndTimestamp(Long endTimestamp) {
/* 34 */     if (null != endTimestamp)
/* 35 */       put("end_timestamp", endTimestamp);
/*    */     else
/* 37 */       put("end_timestamp", null);
/*    */   }
/*    */ 
/*    */   public Long getEndTimestamp()
/*    */   {
/* 42 */     return getLong("end_timestamp");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergInitMessage
 * JD-Core Version:    0.6.0
 */