/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class HeartbeatOkResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "hb_ok";
/*    */ 
/*    */   public HeartbeatOkResponseMessage()
/*    */   {
/* 20 */     setType("hb_ok");
/*    */   }
/*    */ 
/*    */   public HeartbeatOkResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("hb_ok");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.HeartbeatOkResponseMessage
 * JD-Core Version:    0.6.0
 */