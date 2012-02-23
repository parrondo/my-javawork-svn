/*    */ package com.dukascopy.transport.common.msg.api.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ClosePositionRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "close_position";
/*    */ 
/*    */   public ClosePositionRequestMessage()
/*    */   {
/* 19 */     setType("close_position");
/*    */   }
/*    */ 
/*    */   public ClosePositionRequestMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("close_position");
/*    */ 
/* 32 */     setPositionId(message.getString("positionId"));
/*    */   }
/*    */ 
/*    */   public void setPositionId(String positionId) {
/* 36 */     put("positionId", positionId);
/*    */   }
/*    */ 
/*    */   public String getPositionId() {
/* 40 */     return getString("positionId");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.request.ClosePositionRequestMessage
 * JD-Core Version:    0.6.0
 */