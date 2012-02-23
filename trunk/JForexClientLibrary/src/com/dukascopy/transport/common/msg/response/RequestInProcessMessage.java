/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class RequestInProcessMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "reqInProc";
/*    */ 
/*    */   public RequestInProcessMessage()
/*    */   {
/* 20 */     setType("reqInProc");
/*    */   }
/*    */ 
/*    */   public RequestInProcessMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/* 30 */     setType("reqInProc");
/*    */   }
/*    */ 
/*    */   public RequestInProcessMessage(String requestId)
/*    */   {
/* 40 */     setType("reqInProc");
/* 41 */     put("reqid", requestId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.RequestInProcessMessage
 * JD-Core Version:    0.6.0
 */