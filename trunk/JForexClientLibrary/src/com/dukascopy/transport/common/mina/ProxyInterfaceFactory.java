/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import java.lang.reflect.Proxy;
/*    */ import org.apache.mina.common.IoSession;
/*    */ 
/*    */ public class ProxyInterfaceFactory extends RemoteInterfaceFactory
/*    */ {
/* 10 */   private Long requestId = Long.valueOf(0L);
/*    */ 
/*    */   private Long getNextId()
/*    */   {
/* 17 */     synchronized (this.requestId) {
/* 18 */       Long localLong1 = this.requestId; Long localLong2 = this.requestId = Long.valueOf(this.requestId.longValue() + 1L);
/* 19 */       return this.requestId;
/*    */     }
/*    */   }
/*    */ 
/*    */   public ProxyInterfaceContext buidRemoteInterface(IoSession session, Class interfaceClass, Long requestTimeout)
/*    */   {
/* 31 */     ProxyInvocationHandler h = new ProxyInvocationHandler(interfaceClass, session, requestTimeout)
/*    */     {
/*    */       public InvocationResponseReceiveFuture getInvocationFuture(InvocationRequest request)
/*    */       {
/* 40 */         return ProxyInterfaceFactory.this.getRemoteCallSupport().addPendingRequest(request);
/*    */       }
/*    */ 
/*    */       public Long getNextRequestId()
/*    */       {
/* 50 */         return ProxyInterfaceFactory.this.getNextId();
/*    */       }
/*    */ 
/*    */       public InvocationResponseReceiveFuture removeInvocationFuture(InvocationRequest request)
/*    */       {
/* 60 */         return ProxyInterfaceFactory.this.getRemoteCallSupport().removePendingRequest(request);
/*    */       }
/*    */     };
/* 64 */     Object p = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, h);
/* 65 */     return new ProxyInterfaceContext(p, h);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ProxyInterfaceFactory
 * JD-Core Version:    0.6.0
 */