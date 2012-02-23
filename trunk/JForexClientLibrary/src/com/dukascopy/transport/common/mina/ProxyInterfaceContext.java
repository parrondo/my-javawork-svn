/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ public class ProxyInterfaceContext
/*    */ {
/*    */   private Object proxyImplementation;
/*    */   private ProxyInvocationHandler handler;
/*    */ 
/*    */   public ProxyInterfaceContext(Object proxyImplementation, ProxyInvocationHandler handler)
/*    */   {
/* 13 */     this.proxyImplementation = proxyImplementation;
/* 14 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */   public Object getProxyImplementation()
/*    */   {
/* 21 */     return this.proxyImplementation;
/*    */   }
/*    */ 
/*    */   public void setProxyImplementation(Object proxyImplementation)
/*    */   {
/* 28 */     this.proxyImplementation = proxyImplementation;
/*    */   }
/*    */ 
/*    */   public ProxyInvocationHandler getHandler()
/*    */   {
/* 35 */     return this.handler;
/*    */   }
/*    */ 
/*    */   public void setHandler(ProxyInvocationHandler h)
/*    */   {
/* 42 */     this.handler = h;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ProxyInterfaceContext
 * JD-Core Version:    0.6.0
 */