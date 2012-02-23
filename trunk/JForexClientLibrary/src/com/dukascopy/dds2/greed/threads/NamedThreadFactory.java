/*    */ package com.dukascopy.dds2.greed.threads;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class NamedThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/* 17 */   static final AtomicInteger poolNumber = new AtomicInteger(1);
/*    */   final ThreadGroup group;
/* 19 */   final AtomicInteger threadNumber = new AtomicInteger(1);
/*    */   final String namePrefix;
/*    */ 
/*    */   public NamedThreadFactory(String poolName)
/*    */   {
/* 27 */     SecurityManager s = System.getSecurityManager();
/* 28 */     this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
/*    */ 
/* 30 */     this.namePrefix = (poolName + "-" + poolNumber.getAndIncrement() + "-thread-");
/*    */   }
/*    */ 
/*    */   public Thread newThread(Runnable r)
/*    */   {
/* 36 */     Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
/*    */ 
/* 39 */     if (t.isDaemon())
/* 40 */       t.setDaemon(false);
/* 41 */     if (t.getPriority() != 5)
/* 42 */       t.setPriority(5);
/* 43 */     return t;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.threads.NamedThreadFactory
 * JD-Core Version:    0.6.0
 */