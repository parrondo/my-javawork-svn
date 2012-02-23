/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class SessionDeactivatedResponseMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "sessiondeactivated";
/*    */ 
/*    */   public SessionDeactivatedResponseMessage()
/*    */   {
/* 28 */     setType("sessiondeactivated");
/*    */   }
/*    */ 
/*    */   public SessionDeactivatedResponseMessage(ProtocolMessage message) {
/* 32 */     super(message);
/* 33 */     setType("sessiondeactivated");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.SessionDeactivatedResponseMessage
 * JD-Core Version:    0.6.0
 */