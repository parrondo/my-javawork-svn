/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class QuitResponseMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "quit";
/*    */ 
/*    */   public QuitResponseMessage()
/*    */   {
/* 20 */     setType("quit");
/*    */   }
/*    */ 
/*    */   public QuitResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("quit");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.QuitResponseMessage
 * JD-Core Version:    0.6.0
 */