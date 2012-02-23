/*    */ package com.dukascopy.dds2.greed.threads;
/*    */ 
/*    */ import java.util.concurrent.ArrayBlockingQueue;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class CurrencyWorkerExecutor
/*    */   implements Executor
/*    */ {
/*    */   public String instrument;
/*    */   private static final int MIN_THREAD_COUNT = 1;
/*    */   private static final int MAX_THREAD_COUNT = 1;
/*    */   private static final long KEEP_ALIVE_TIME = 0L;
/* 40 */   private ArrayBlockingQueue<Runnable> actionQueue = new ArrayBlockingQueue(2);
/*    */   private ThreadPoolExecutor delegate;
/*    */ 
/*    */   public CurrencyWorkerExecutor(String instrument)
/*    */   {
/* 19 */     this.instrument = instrument;
/* 20 */     this.delegate = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, this.actionQueue, new NamedThreadFactory(instrument), new ThreadPoolExecutor.DiscardOldestPolicy());
/*    */   }
/*    */ 
/*    */   public void execute(Runnable command)
/*    */   {
/* 45 */     this.delegate.execute(command);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.threads.CurrencyWorkerExecutor
 * JD-Core Version:    0.6.0
 */