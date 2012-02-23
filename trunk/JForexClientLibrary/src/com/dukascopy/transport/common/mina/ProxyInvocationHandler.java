/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.JSonSerializableWrapper;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Method;
/*    */ import java.rmi.RemoteException;
/*    */ import org.apache.mina.common.IoSession;
/*    */ 
/*    */ public abstract class ProxyInvocationHandler
/*    */   implements InvocationHandler
/*    */ {
/*    */   private Class interfaceClass;
/*    */   private IoSession session;
/*    */   private Long requestTimeout;
/*    */ 
/*    */   public ProxyInvocationHandler(Class interfaceClass, IoSession session, Long requestTimeout)
/*    */   {
/* 24 */     this.interfaceClass = interfaceClass;
/* 25 */     this.session = session;
/* 26 */     this.requestTimeout = requestTimeout;
/*    */   }
/*    */ 
/*    */   public IoSession getSession()
/*    */   {
/* 33 */     return this.session;
/*    */   }
/*    */ 
/*    */   public void setSession(IoSession session)
/*    */   {
/* 41 */     this.session = session;
/*    */   }
/*    */ 
/*    */   public Object invoke(Object proxy, Method method, Object[] args)
/*    */     throws Throwable
/*    */   {
/* 51 */     if (this.session == null) {
/* 52 */       throw new IOException("Client session NULL");
/*    */     }
/* 54 */     if (!this.session.isConnected()) {
/* 55 */       this.session = null;
/* 56 */       throw new IOException("Client session disconnected");
/*    */     }
/* 58 */     InvocationRequest ir = new InvocationRequest(this.interfaceClass.getCanonicalName(), args, method.getName());
/* 59 */     ir.setRequestId(getNextRequestId());
/* 60 */     InvocationResponseReceiveFuture irrf = getInvocationFuture(ir);
/* 61 */     JSonSerializableWrapper msg = new JSonSerializableWrapper();
/* 62 */     msg.setData(ir);
/* 63 */     this.session.write(msg);
/* 64 */     synchronized (irrf) {
/* 65 */       irrf.wait(this.requestTimeout.longValue());
/*    */     }
/* 67 */     removeInvocationFuture(ir);
/* 68 */     if (irrf.getResponse() == null) {
/* 69 */       throw new IOException("No response from remote service");
/*    */     }
/* 71 */     if (irrf.getResponse().getState() == 1) {
/* 72 */       System.out.println("Received error response " + irrf.getResponse().getErrorReason());
/* 73 */       throw new RemoteException(irrf.getResponse().getErrorReason());
/*    */     }
/* 75 */     if (irrf.getResponse().getState() == 0) {
/* 76 */       return irrf.getResponse().getResult();
/*    */     }
/* 78 */     return null;
/*    */   }
/*    */ 
/*    */   public abstract Long getNextRequestId();
/*    */ 
/*    */   public abstract InvocationResponseReceiveFuture getInvocationFuture(InvocationRequest paramInvocationRequest);
/*    */ 
/*    */   public abstract InvocationResponseReceiveFuture removeInvocationFuture(InvocationRequest paramInvocationRequest);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ProxyInvocationHandler
 * JD-Core Version:    0.6.0
 */