/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class SessionListRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "session_list";
/*    */ 
/*    */   public SessionListRequestMessage()
/*    */   {
/* 17 */     setType("session_list");
/*    */   }
/*    */ 
/*    */   public SessionListRequestMessage(ProtocolMessage message) {
/* 21 */     super(message);
/* 22 */     setType("session_list");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.SessionListRequestMessage
 * JD-Core Version:    0.6.0
 */