/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IConsole;
/*    */ import com.dukascopy.api.IContext;
/*    */ import java.io.PrintStream;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ScienceRejectedExecutionHandler
/*    */ {
/* 15 */   private static final Logger LOGGER = LoggerFactory.getLogger(ScienceRejectedExecutionHandler.class);
/* 16 */   private IContext context = null;
/*    */   private Thread thread;
/*    */ 
/*    */   public ScienceRejectedExecutionHandler(IContext context, Thread thread)
/*    */   {
/* 20 */     this.context = context;
/* 21 */     this.thread = thread;
/*    */   }
/*    */ 
/*    */   public void rejectedExecution(Runnable r, ScienceThreadPoolExecutor executor) {
/* 25 */     if (this.context != null) {
/* 26 */       if (this.context.getConsole() != null)
/*    */       {
/* 28 */         ScienceQueue queue = (ScienceQueue)executor.getQueue();
/* 29 */         int ticks = 0;
/* 30 */         int bars = 0;
/* 31 */         int tasks = 0;
/* 32 */         for (ScienceFuture future : queue) {
/* 33 */           Task queueTask = future.getTask();
/* 34 */           if (queueTask.getType() == Task.Type.TICK) {
/* 35 */             ticks++;
/* 36 */           } else if (queueTask.getType() == Task.Type.BAR) {
/* 37 */             bars++;
/*    */           } else {
/* 39 */             tasks++;
/* 40 */             this.context.getConsole().getErr().println(queueTask);
/* 41 */             LOGGER.debug(queueTask.getType().toString());
/*    */           }
/*    */         }
/* 44 */         StackTraceElement[] stackTrace = this.thread.getStackTrace();
/* 45 */         this.context.getConsole().getErr().println(new StringBuilder().append("Task rejected. Ticks in queue - ").append(ticks).append(", bars - ").append(bars).append(", other tasks - ").append(tasks).append(", ThreadPoolExecutor.isShutdown() - ").append(executor.isShutdown()).append(" Strategy thread in [").append(stackTrace.length > 0 ? stackTrace[0].toString() : "stack trace element array is empty").append("]").toString());
/*    */ 
/* 47 */         LOGGER.error(new StringBuilder().append("Task rejected. Ticks in queue - ").append(ticks).append(", bars - ").append(bars).append(", other tasks - ").append(tasks).append(", ThreadPoolExecutor.isShutdown - ").append(executor.isShutdown()).toString());
/* 48 */         StringBuilder traceMessage = new StringBuilder("Strategy thread stack trace:\n");
/* 49 */         for (StackTraceElement stackTraceElement : stackTrace) {
/* 50 */           traceMessage.append("\t at ").append(stackTraceElement).append("\n");
/*    */         }
/* 52 */         LOGGER.error(traceMessage.toString());
/* 53 */         traceMessage = new StringBuilder("Rejected thread stack trace:\n");
/* 54 */         stackTrace = Thread.currentThread().getStackTrace();
/* 55 */         for (StackTraceElement stackTraceElement : stackTrace) {
/* 56 */           traceMessage.append("\t at ").append(stackTraceElement).append("\n");
/*    */         }
/* 58 */         LOGGER.error(traceMessage.toString());
/*    */       }
/* 60 */       if (this.context != null)
/* 61 */         this.context.stop();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.ScienceRejectedExecutionHandler
 * JD-Core Version:    0.6.0
 */