/*    */ package com.dukascopy.dds2.greed.threads;
/*    */ 
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class SingleThreadExecutorFactory
/*    */ {
/*    */   public static ExecutorService newSingleThreadExecutor(String name)
/*    */   {
/* 12 */     return Executors.unconfigurableExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new NamedThreadFactory(name)));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.threads.SingleThreadExecutorFactory
 * JD-Core Version:    0.6.0
 */