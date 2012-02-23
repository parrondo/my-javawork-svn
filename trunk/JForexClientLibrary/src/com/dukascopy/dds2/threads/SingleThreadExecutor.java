/*    */ package com.dukascopy.dds2.threads;
/*    */ 
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class SingleThreadExecutor extends BaseExecutor
/*    */ {
/* 20 */   private static int threadCounter = 0;
/*    */ 
/*    */   public SingleThreadExecutor() {
/* 23 */     this("Single");
/*    */   }
/*    */ 
/*    */   public SingleThreadExecutor(String caption) {
/* 27 */     this.queue = new LinkedBlockingQueue();
/* 28 */     this.delegateExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, this.queue, new SingleThreadFactory(caption));
/* 29 */     ((ThreadPoolExecutor)this.delegateExecutor).prestartAllCoreThreads();
/*    */   }
/*    */ 
/*    */   class SingleThreadFactory implements ThreadFactory {
/* 33 */     String caption = "Single";
/*    */ 
/*    */     SingleThreadFactory(String caption) {
/* 36 */       this.caption = caption;
/*    */     }
/*    */ 
/*    */     public Thread newThread(Runnable r) {
/* 40 */       Thread thread = new Thread(r);
/* 41 */       thread.setName(this.caption + "-" + SingleThreadExecutor.access$008());
/* 42 */       return thread;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.threads.SingleThreadExecutor
 * JD-Core Version:    0.6.0
 */