/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.apache.mina.common.IoSession;
/*    */ 
/*    */ public class RemoteCallSupport
/*    */ {
/* 10 */   private Map<String, Object> exportedInterfaces = new HashMap();
/*    */ 
/* 12 */   private Map<Class, ProxyInterfaceContext> interfaceCache = new HashMap();
/*    */   private RemoteInterfaceFactory proxyFactory;
/* 16 */   private Map<Long, InvocationResponseReceiveFuture> pendingInvocationRequest = new HashMap();
/*    */ 
/*    */   public RemoteCallSupport(RemoteInterfaceFactory proxyFactory)
/*    */   {
/* 20 */     this.proxyFactory = proxyFactory;
/* 21 */     this.proxyFactory.setRemoteCallSupport(this);
/*    */   }
/*    */ 
/*    */   public void exportInterface(Class interfaceClass, Object interfaceImpl) {
/* 25 */     synchronized (this.exportedInterfaces) {
/* 26 */       this.exportedInterfaces.put(interfaceClass.getCanonicalName(), interfaceImpl);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Object getInterfaceImplementation(String className) {
/* 31 */     return this.exportedInterfaces.get(className);
/*    */   }
/*    */ 
/*    */   public void setSession(IoSession session) {
/* 35 */     synchronized (this.interfaceCache) {
/* 36 */       for (ProxyInterfaceContext pic : this.interfaceCache.values())
/* 37 */         pic.getHandler().setSession(session);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Object getRemoteInterface(Class remoteInterfaceClass, IoSession session, Long requestTimeout)
/*    */     throws IllegalArgumentException
/*    */   {
/* 45 */     Object proxy = null;
/* 46 */     synchronized (this.interfaceCache) {
/* 47 */       ProxyInterfaceContext pi = (ProxyInterfaceContext)this.interfaceCache.get(remoteInterfaceClass);
/* 48 */       if (pi == null) {
/* 49 */         if (session == null) {
/* 50 */           throw new IllegalArgumentException("Session is null");
/*    */         }
/* 52 */         pi = this.proxyFactory.buidRemoteInterface(session, remoteInterfaceClass, requestTimeout);
/* 53 */         this.interfaceCache.put(remoteInterfaceClass, pi);
/*    */       }
/* 55 */       proxy = pi.getProxyImplementation();
/*    */     }
/* 57 */     return proxy;
/*    */   }
/*    */ 
/*    */   public Object getRemoteInterfaceNoCache(Class remoteInterfaceClass, IoSession session, Long requestTimeout) throws IllegalArgumentException
/*    */   {
/* 62 */     if (session == null) {
/* 63 */       throw new IllegalArgumentException("Session is null");
/*    */     }
/* 65 */     ProxyInterfaceContext proxy = null;
/* 66 */     proxy = this.proxyFactory.buidRemoteInterface(session, remoteInterfaceClass, requestTimeout);
/* 67 */     return proxy.getProxyImplementation();
/*    */   }
/*    */ 
/*    */   public InvocationResponseReceiveFuture addPendingRequest(InvocationRequest request) {
/* 71 */     synchronized (this.pendingInvocationRequest) {
/* 72 */       InvocationResponseReceiveFuture irrf = new InvocationResponseReceiveFuture();
/* 73 */       this.pendingInvocationRequest.put(request.getRequestId(), irrf);
/* 74 */       return irrf;
/*    */     }
/*    */   }
/*    */ 
/*    */   public InvocationResponseReceiveFuture removePendingRequest(InvocationRequest request) {
/* 79 */     synchronized (this.pendingInvocationRequest) {
/* 80 */       InvocationResponseReceiveFuture irrf = (InvocationResponseReceiveFuture)this.pendingInvocationRequest.get(request.getRequestId());
/* 81 */       return irrf;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void invocationResultReceived(InvocationResult result) {
/* 86 */     InvocationResponseReceiveFuture irrf = null;
/* 87 */     synchronized (this.pendingInvocationRequest) {
/* 88 */       irrf = (InvocationResponseReceiveFuture)this.pendingInvocationRequest.get(result.getRequestId());
/*    */     }
/* 90 */     if (irrf != null)
/* 91 */       synchronized (irrf) {
/* 92 */         irrf.setResponse(result);
/* 93 */         irrf.notifyAll();
/*    */       }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.RemoteCallSupport
 * JD-Core Version:    0.6.0
 */