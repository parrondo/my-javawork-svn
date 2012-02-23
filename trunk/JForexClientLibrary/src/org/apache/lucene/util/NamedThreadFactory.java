/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class NamedThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/* 31 */   private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);
/*    */   private final ThreadGroup group;
/* 33 */   private final AtomicInteger threadNumber = new AtomicInteger(1);
/*    */   private static final String NAME_PATTERN = "%s-%d-thread";
/*    */   private final String threadNamePrefix;
/*    */ 
/*    */   public NamedThreadFactory(String threadNamePrefix)
/*    */   {
/* 43 */     SecurityManager s = System.getSecurityManager();
/* 44 */     this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
/*    */ 
/* 46 */     this.threadNamePrefix = String.format("%s-%d-thread", new Object[] { checkPrefix(threadNamePrefix), Integer.valueOf(threadPoolNumber.getAndIncrement()) });
/*    */   }
/*    */ 
/*    */   private static String checkPrefix(String prefix)
/*    */   {
/* 51 */     return (prefix == null) || (prefix.length() == 0) ? "Lucene" : prefix;
/*    */   }
/*    */ 
/*    */   public Thread newThread(Runnable r)
/*    */   {
/* 60 */     Thread t = new Thread(this.group, r, String.format("%s-%d", new Object[] { this.threadNamePrefix, Integer.valueOf(this.threadNumber.getAndIncrement()) }), 0L);
/*    */ 
/* 62 */     t.setDaemon(false);
/* 63 */     t.setPriority(5);
/* 64 */     return t;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.NamedThreadFactory
 * JD-Core Version:    0.6.0
 */