/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class HeartbeatRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "hb";
/*    */ 
/*    */   public HeartbeatRequestMessage()
/*    */   {
/* 20 */     setType("hb");
/*    */   }
/*    */ 
/*    */   public HeartbeatRequestMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("hb");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.HeartbeatRequestMessage
 * JD-Core Version:    0.6.0
 */