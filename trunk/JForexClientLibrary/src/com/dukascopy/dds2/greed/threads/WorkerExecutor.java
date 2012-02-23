/*    */ package com.dukascopy.dds2.greed.threads;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class WorkerExecutor
/*    */   implements Executor
/*    */ {
/*    */   private static final int MIN_THREAD_COUNT = 3;
/*    */   private static final int MAX_THREAD_COUNT = 10;
/*    */   private static final long KEEP_ALIVE_TIME = 100L;
/* 28 */   private LinkedBlockingQueue<Runnable> actionQueue = new LinkedBlockingQueue(100);
/*    */   private ThreadPoolExecutor delegate;
/*    */ 
/*    */   public WorkerExecutor(String name)
/*    */   {
/* 33 */     this.delegate = new ThreadPoolExecutor(3, 10, 100L, TimeUnit.MILLISECONDS, this.actionQueue, new NamedThreadFactory(name), new ThreadPoolExecutor.CallerRunsPolicy());
/*    */   }
/*    */ 
/*    */   public void execute(Runnable command)
/*    */   {
/* 84 */     this.delegate.execute(command);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.threads.WorkerExecutor
 * JD-Core Version:    0.6.0
 */