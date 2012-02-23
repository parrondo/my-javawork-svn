/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import java.awt.EventQueue;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ public abstract class SwingWorker<V>
/*     */   implements Future<V>, Runnable
/*     */ {
/* 113 */   private static final Executor EXECUTOR = new Executor() {
/*     */     public void execute(Runnable command) {
/* 115 */       new Thread(command, "Swing-Worker").start();
/*     */     }
/* 113 */   };
/*     */   private Executor executor;
/*     */   private boolean started;
/* 170 */   private final FutureTask<V> task = new FutureTask(new Callable()
/*     */   {
/*     */     public V call() throws Exception {
/* 173 */       return SwingWorker.this.construct();
/*     */     }
/*     */   }) {
/*     */     protected void done() {
/* 177 */       EventQueue.invokeLater(new Runnable() {
/*     */         public void run() {
/* 179 */           SwingWorker.this.finished();
/*     */         }
/*     */       });
/*     */     }
/* 170 */   };
/*     */ 
/*     */   public SwingWorker()
/*     */   {
/* 127 */     this(EXECUTOR);
/*     */   }
/*     */ 
/*     */   protected SwingWorker(Executor e)
/*     */   {
/* 135 */     setExecutor(e);
/*     */   }
/*     */ 
/*     */   public synchronized void setExecutor(Executor e)
/*     */   {
/* 143 */     this.executor = e;
/*     */   }
/*     */ 
/*     */   public synchronized Executor getExecutor()
/*     */   {
/* 151 */     return this.executor;
/*     */   }
/*     */ 
/*     */   public synchronized void start()
/*     */   {
/* 159 */     if (!this.started) {
/* 160 */       this.executor.execute(this);
/* 161 */       this.started = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract V construct()
/*     */     throws Exception;
/*     */ 
/*     */   protected void finished()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 200 */     this.task.run();
/*     */   }
/*     */ 
/*     */   public boolean cancel(boolean mayInterruptIfRunning)
/*     */   {
/* 207 */     return this.task.cancel(mayInterruptIfRunning);
/*     */   }
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 214 */     return this.task.isCancelled();
/*     */   }
/*     */ 
/*     */   public boolean isDone()
/*     */   {
/* 219 */     return this.task.isDone();
/*     */   }
/*     */ 
/*     */   public V get()
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 227 */     return this.task.get();
/*     */   }
/*     */ 
/*     */   public V get(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 236 */     return this.task.get(timeout, unit);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.SwingWorker
 * JD-Core Version:    0.6.0
 */