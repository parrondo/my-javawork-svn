/*    */ package com.dukascopy.dds2.threads;
/*    */ 
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class ThreadPoolExecutor extends BaseExecutor
/*    */ {
/* 16 */   private static int threadCounter = 0;
/*    */ 
/*    */   public ThreadPoolExecutor(int numberOfThreads) {
/* 19 */     this.queue = new LinkedBlockingQueue();
/* 20 */     this.delegateExecutor = new java.util.concurrent.ThreadPoolExecutor(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS, this.queue, new ThreadFactory() {
/*    */       public Thread newThread(Runnable r) {
/* 22 */         Thread thread = new Thread(r);
/*    */ 
/* 24 */         thread.setName("PoolEx-" + ThreadPoolExecutor.access$008());
/* 25 */         return thread;
/*    */       }
/*    */     });
/* 28 */     ((java.util.concurrent.ThreadPoolExecutor)this.delegateExecutor).prestartAllCoreThreads();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.threads.ThreadPoolExecutor
 * JD-Core Version:    0.6.0
 */