/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import org.apache.mina.common.IoFuture;
/*    */ import org.apache.mina.common.IoFutureListener;
/*    */ 
/*    */ public class IoFutureListenerImpl
/*    */   implements IoFutureListener
/*    */ {
/*    */   private ProtocolMessage message;
/*    */   private MessageSentListener listener;
/*    */ 
/*    */   public IoFutureListenerImpl(ProtocolMessage message, MessageSentListener listener)
/*    */   {
/* 19 */     this.message = message;
/* 20 */     this.listener = listener;
/*    */   }
/*    */ 
/*    */   public void operationComplete(IoFuture future) {
/* 24 */     this.listener.messageSent(this.message);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.IoFutureListenerImpl
 * JD-Core Version:    0.6.0
 */