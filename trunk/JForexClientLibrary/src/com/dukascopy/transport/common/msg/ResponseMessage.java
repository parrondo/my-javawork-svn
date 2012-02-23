/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ import java.text.ParseException;
/*    */ 
/*    */ public class ResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public ResponseMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ResponseMessage(ProtocolMessage message)
/*    */   {
/* 24 */     super(message);
/*    */   }
/*    */ 
/*    */   public ResponseMessage(String s)
/*    */     throws ParseException
/*    */   {
/* 34 */     super(s);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.ResponseMessage
 * JD-Core Version:    0.6.0
 */