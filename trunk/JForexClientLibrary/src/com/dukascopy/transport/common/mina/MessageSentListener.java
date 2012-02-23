/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import org.apache.mina.common.IoFuture;
/*    */ import org.apache.mina.common.IoFutureListener;
/*    */ 
/*    */ public abstract class MessageSentListener
/*    */   implements IoFutureListener
/*    */ {
/*    */   private ProtocolMessage message;
/*    */ 
/*    */   public void setMessage(ProtocolMessage message)
/*    */   {
/* 21 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public void operationComplete(IoFuture future) {
/* 25 */     messageSent(this.message);
/*    */   }
/*    */ 
/*    */   public abstract void messageSent(ProtocolMessage paramProtocolMessage);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.MessageSentListener
 * JD-Core Version:    0.6.0
 */