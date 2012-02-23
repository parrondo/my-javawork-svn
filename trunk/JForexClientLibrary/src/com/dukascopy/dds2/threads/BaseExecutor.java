/*    */ package com.dukascopy.dds2.threads;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Future;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public abstract class BaseExecutor
/*    */   implements Executor
/*    */ {
/* 16 */   protected ExecutorService delegateExecutor = null;
/*    */   protected BlockingQueue<Runnable> queue;
/*    */ 
/*    */   public void execute(Runnable command)
/*    */   {
/* 21 */     this.delegateExecutor.execute(command);
/*    */   }
/*    */ 
/*    */   public Future submit(Runnable command) {
/* 25 */     return this.delegateExecutor.submit(command);
/*    */   }
/*    */ 
/*    */   public Future submit(Callable command) {
/* 29 */     return this.delegateExecutor.submit(command);
/*    */   }
/*    */ 
/*    */   public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
/* 33 */     return this.delegateExecutor.invokeAll(tasks, timeout, unit);
/*    */   }
/*    */ 
/*    */   public int getWorkQueueSize() {
/* 37 */     return this.queue.size();
/*    */   }
/*    */ 
/*    */   public void shutdown() {
/* 41 */     this.delegateExecutor.shutdown();
/*    */   }
/*    */ 
/*    */   public BlockingQueue<Runnable> getQueue() {
/* 45 */     return this.queue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.threads.BaseExecutor
 * JD-Core Version:    0.6.0
 */