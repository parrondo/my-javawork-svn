/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class OkResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "ok";
/*    */ 
/*    */   public OkResponseMessage()
/*    */   {
/* 20 */     setType("ok");
/*    */   }
/*    */ 
/*    */   public OkResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("ok");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.OkResponseMessage
 * JD-Core Version:    0.6.0
 */