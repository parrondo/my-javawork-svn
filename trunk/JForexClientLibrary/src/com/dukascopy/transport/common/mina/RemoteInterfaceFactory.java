/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import org.apache.mina.common.IoSession;
/*    */ 
/*    */ public abstract class RemoteInterfaceFactory
/*    */ {
/*    */   private RemoteCallSupport remoteCallSupport;
/*    */ 
/*    */   public abstract ProxyInterfaceContext buidRemoteInterface(IoSession paramIoSession, Class paramClass, Long paramLong);
/*    */ 
/*    */   public RemoteCallSupport getRemoteCallSupport()
/*    */   {
/* 15 */     return this.remoteCallSupport;
/*    */   }
/*    */ 
/*    */   public void setRemoteCallSupport(RemoteCallSupport rcs)
/*    */   {
/* 23 */     this.remoteCallSupport = rcs;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.RemoteInterfaceFactory
 * JD-Core Version:    0.6.0
 */