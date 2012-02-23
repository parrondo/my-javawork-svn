/*    */ package com.dukascopy.transport.common.msg.api.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class PositionHistoryRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "pos_history";
/*    */ 
/*    */   public PositionHistoryRequestMessage()
/*    */   {
/* 21 */     setType("pos_history");
/*    */   }
/*    */ 
/*    */   public PositionHistoryRequestMessage(ProtocolMessage message)
/*    */   {
/* 30 */     super(message);
/*    */ 
/* 32 */     setType("pos_history");
/*    */ 
/* 34 */     setStartTimestamp(message.getDate("startTimestamp"));
/* 35 */     setEndTimestamp(message.getDate("endTimestamp"));
/*    */   }
/*    */ 
/*    */   public void setStartTimestamp(Date startTimestamp) {
/* 39 */     putDate("startTimestamp", startTimestamp);
/*    */   }
/*    */ 
/*    */   public Date getStartTimestamp() {
/* 43 */     return getDate("startTimestamp");
/*    */   }
/*    */ 
/*    */   public void setEndTimestamp(Date endTimestamp) {
/* 47 */     putDate("endTimestamp", endTimestamp);
/*    */   }
/*    */ 
/*    */   public Date getEndTimestamp() {
/* 51 */     return getDate("endTimestamp");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.request.PositionHistoryRequestMessage
 * JD-Core Version:    0.6.0
 */