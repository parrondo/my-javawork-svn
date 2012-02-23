/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ public class StreamHeaderMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "streamHeader";
/*    */ 
/*    */   public StreamHeaderMessage()
/*    */   {
/* 11 */     setType("streamHeader");
/*    */   }
/*    */ 
/*    */   public StreamHeaderMessage(ProtocolMessage message)
/*    */   {
/* 18 */     super(message);
/* 19 */     setType("streamHeader");
/* 20 */     put("streamId", message.getString("streamId"));
/*    */   }
/*    */ 
/*    */   public StreamHeaderMessage(String streamId)
/*    */   {
/* 27 */     setType("streamHeader");
/* 28 */     put("streamId", streamId);
/*    */   }
/*    */ 
/*    */   public String getStreamId() {
/* 32 */     return getString("streamId");
/*    */   }
/*    */ 
/*    */   public void setStreamId(String binId) {
/* 36 */     put("streamId", binId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.StreamHeaderMessage
 * JD-Core Version:    0.6.0
 */