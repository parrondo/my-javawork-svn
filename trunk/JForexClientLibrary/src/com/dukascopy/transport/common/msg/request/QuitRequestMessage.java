/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class QuitRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "quitrq";
/*    */ 
/*    */   public QuitRequestMessage()
/*    */   {
/* 20 */     setType("quitrq");
/*    */   }
/*    */ 
/*    */   public QuitRequestMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("quitrq");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.QuitRequestMessage
 * JD-Core Version:    0.6.0
 */