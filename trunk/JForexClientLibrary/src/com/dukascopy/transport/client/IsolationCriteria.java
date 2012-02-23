/*    */ package com.dukascopy.transport.client;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public abstract class IsolationCriteria
/*    */ {
/*    */   private Class messageClass;
/*    */ 
/*    */   public IsolationCriteria(Class messageClass)
/*    */   {
/* 17 */     this.messageClass = messageClass;
/*    */   }
/*    */ 
/*    */   public Class<ProtocolMessage> getMessageClass()
/*    */   {
/* 26 */     return this.messageClass;
/*    */   }
/*    */ 
/*    */   public void setMessageClass(Class<ProtocolMessage> messageClass)
/*    */   {
/* 35 */     this.messageClass = messageClass;
/*    */   }
/*    */ 
/*    */   public abstract Object getCheckParameter(ProtocolMessage paramProtocolMessage);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.IsolationCriteria
 * JD-Core Version:    0.6.0
 */