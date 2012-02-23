/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ public class StreamingStatus extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "streamState";
/*    */   public static final String STATE_OK = "s_ack";
/*    */   public static final String STATE_TRANSFER_WAIT = "tran";
/*    */   public static final String STATE_WAIT_ACK = "ack_wait";
/*    */   public static final String STATE_ERROR = "err";
/*    */   public static final String STATE_TIMEOUTED = "tmt";
/*    */ 
/*    */   public StreamingStatus()
/*    */   {
/* 22 */     setType("streamState");
/*    */   }
/*    */ 
/*    */   public StreamingStatus(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/* 30 */     setType("streamState");
/* 31 */     put("state", message.getString("state"));
/* 32 */     put("streamId", message.getString("streamId"));
/*    */   }
/*    */ 
/*    */   public StreamingStatus(String streamId, String state)
/*    */   {
/* 40 */     setType("streamState");
/* 41 */     put("state", state);
/* 42 */     put("streamId", streamId);
/*    */   }
/*    */ 
/*    */   public String getStreamId() {
/* 46 */     return getString("streamId");
/*    */   }
/*    */ 
/*    */   public void setStreamId(String binId) {
/* 50 */     put("streamId", binId);
/*    */   }
/*    */ 
/*    */   public String getState() {
/* 54 */     return getString("state");
/*    */   }
/*    */ 
/*    */   public void setState(String state) {
/* 58 */     put("state", state);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.StreamingStatus
 * JD-Core Version:    0.6.0
 */