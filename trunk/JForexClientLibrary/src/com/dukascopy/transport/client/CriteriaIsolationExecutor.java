/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.client.events.FeedbackMessageReceivedEvent;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ public class CriteriaIsolationExecutor
/*     */ {
/*  30 */   private int poolSize = 30;
/*     */   private ThreadPoolExecutor eventExecutor;
/*  34 */   private Map<Class<ProtocolMessage>, ThreadIsolationCriteriaProcessor> criterias = new HashMap();
/*     */ 
/*  36 */   private Timer t = new Timer();
/*     */ 
/*     */   public CriteriaIsolationExecutor(int poolSize) {
/*  39 */     this.poolSize = poolSize;
/*  40 */     this.eventExecutor = new ThreadPoolExecutor(this.poolSize, this.poolSize, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
/*     */ 
/*  43 */     this.eventExecutor.setThreadFactory(new ThreadFactory() {
/*  44 */       volatile int counter = 0;
/*     */ 
/*     */       public Thread newThread(Runnable r)
/*     */       {
/*  53 */         this.counter += 1;
/*  54 */         Thread t = new Thread(r, "(" + hashCode() + ") Client listeners invocation thread - " + this.counter);
/*     */ 
/*  56 */         return t;
/*     */       }
/*     */     });
/*  60 */     this.t.schedule(new TimerTask()
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try {
/*  65 */           CriteriaIsolationExecutor.this.monitorThreads();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */     , 60000L, 60000L);
/*     */   }
/*     */ 
/*     */   private void monitorThreads()
/*     */   {
/*  74 */     for (ThreadIsolationCriteriaProcessor e : this.criterias.values())
/*  75 */       e.monitor();
/*     */   }
/*     */ 
/*     */   public void executeFeedbackEvent(FeedbackMessageReceivedEvent event)
/*     */   {
/*     */     try {
/*  81 */       ProtocolMessage message = event.getMessage();
/*  82 */       Set keys = this.criterias.keySet();
/*  83 */       Class assignable = null;
/*  84 */       for (Class cl : keys) {
/*  85 */         if (cl.isAssignableFrom(message.getClass())) {
/*  86 */           assignable = cl;
/*  87 */           ThreadIsolationCriteriaProcessor criteriaProcessor = null;
/*  88 */           criteriaProcessor = (ThreadIsolationCriteriaProcessor)this.criterias.get(assignable);
/*  89 */           if (criteriaProcessor != null)
/*     */           {
/*  91 */             if (criteriaProcessor.execute(event)) {
/*  92 */               return;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*  97 */       execute(event);
/*  98 */       return;
/*     */     } catch (Exception e) {
/* 100 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addIsolationCriteria(IsolationCriteria criteria) {
/* 105 */     ThreadIsolationCriteriaProcessor processor = new ThreadIsolationCriteriaProcessor(criteria);
/*     */ 
/* 107 */     this.criterias.put(criteria.getMessageClass(), processor);
/*     */   }
/*     */ 
/*     */   public void clearIsolationCriterias() {
/* 111 */     this.criterias.clear();
/*     */   }
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 124 */     return this.eventExecutor.awaitTermination(timeout, unit);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 133 */     return this.eventExecutor.equals(obj);
/*     */   }
/*     */ 
/*     */   public void execute(Runnable command)
/*     */   {
/* 141 */     this.eventExecutor.execute(command);
/*     */   }
/*     */ 
/*     */   public int getActiveCount()
/*     */   {
/* 149 */     return this.eventExecutor.getActiveCount();
/*     */   }
/*     */ 
/*     */   public long getCompletedTaskCount()
/*     */   {
/* 157 */     return this.eventExecutor.getCompletedTaskCount();
/*     */   }
/*     */ 
/*     */   public int getCorePoolSize()
/*     */   {
/* 165 */     return this.eventExecutor.getCorePoolSize();
/*     */   }
/*     */ 
/*     */   public long getKeepAliveTime(TimeUnit unit)
/*     */   {
/* 174 */     return this.eventExecutor.getKeepAliveTime(unit);
/*     */   }
/*     */ 
/*     */   public int getLargestPoolSize()
/*     */   {
/* 182 */     return this.eventExecutor.getLargestPoolSize();
/*     */   }
/*     */ 
/*     */   public int getMaximumPoolSize()
/*     */   {
/* 190 */     return this.eventExecutor.getMaximumPoolSize();
/*     */   }
/*     */ 
/*     */   public int getPoolSize()
/*     */   {
/* 198 */     return this.eventExecutor.getPoolSize();
/*     */   }
/*     */ 
/*     */   public BlockingQueue<Runnable> getQueue()
/*     */   {
/* 206 */     return this.eventExecutor.getQueue();
/*     */   }
/*     */ 
/*     */   public int getScheduledSize() {
/* 210 */     int scheduledSize = 0;
/* 211 */     for (ThreadIsolationCriteriaProcessor ticp : this.criterias.values()) {
/* 212 */       scheduledSize += ticp.getScheduledCount();
/*     */     }
/* 214 */     scheduledSize += getQueue().size();
/* 215 */     return scheduledSize;
/*     */   }
/*     */ 
/*     */   public RejectedExecutionHandler getRejectedExecutionHandler()
/*     */   {
/* 223 */     return this.eventExecutor.getRejectedExecutionHandler();
/*     */   }
/*     */ 
/*     */   public long getTaskCount()
/*     */   {
/* 231 */     return this.eventExecutor.getTaskCount();
/*     */   }
/*     */ 
/*     */   public ThreadFactory getThreadFactory()
/*     */   {
/* 239 */     return this.eventExecutor.getThreadFactory();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 247 */     return this.eventExecutor.hashCode();
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 262 */     return this.eventExecutor.invokeAll(tasks, timeout, unit);
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks)
/*     */     throws InterruptedException
/*     */   {
/* 274 */     return this.eventExecutor.invokeAll(tasks);
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 292 */     return this.eventExecutor.invokeAny(tasks, timeout, unit);
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<Callable<T>> tasks)
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 305 */     return this.eventExecutor.invokeAny(tasks);
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/* 313 */     return this.eventExecutor.isShutdown();
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/* 321 */     return this.eventExecutor.isTerminated();
/*     */   }
/*     */ 
/*     */   public boolean isTerminating()
/*     */   {
/* 329 */     return this.eventExecutor.isTerminating();
/*     */   }
/*     */ 
/*     */   public int prestartAllCoreThreads()
/*     */   {
/* 337 */     return this.eventExecutor.prestartAllCoreThreads();
/*     */   }
/*     */ 
/*     */   public boolean prestartCoreThread()
/*     */   {
/* 345 */     return this.eventExecutor.prestartCoreThread();
/*     */   }
/*     */ 
/*     */   public void purge()
/*     */   {
/* 353 */     this.eventExecutor.purge();
/*     */   }
/*     */ 
/*     */   public boolean remove(Runnable task)
/*     */   {
/* 362 */     return this.eventExecutor.remove(task);
/*     */   }
/*     */ 
/*     */   public void setCorePoolSize(int corePoolSize)
/*     */   {
/* 370 */     this.eventExecutor.setCorePoolSize(corePoolSize);
/*     */   }
/*     */ 
/*     */   public void setKeepAliveTime(long time, TimeUnit unit)
/*     */   {
/* 380 */     this.eventExecutor.setKeepAliveTime(time, unit);
/*     */   }
/*     */ 
/*     */   public void setMaximumPoolSize(int maximumPoolSize)
/*     */   {
/* 388 */     this.eventExecutor.setMaximumPoolSize(maximumPoolSize);
/*     */   }
/*     */ 
/*     */   public void setRejectedExecutionHandler(RejectedExecutionHandler handler)
/*     */   {
/* 396 */     this.eventExecutor.setRejectedExecutionHandler(handler);
/*     */   }
/*     */ 
/*     */   public void setThreadFactory(ThreadFactory threadFactory)
/*     */   {
/* 404 */     this.eventExecutor.setThreadFactory(threadFactory);
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 412 */     for (ThreadIsolationCriteriaProcessor queue : this.criterias.values()) {
/* 413 */       queue.terminate();
/*     */     }
/* 415 */     this.eventExecutor.shutdown();
/*     */   }
/*     */ 
/*     */   public List<Runnable> shutdownNow()
/*     */   {
/* 423 */     return this.eventExecutor.shutdownNow();
/*     */   }
/*     */ 
/*     */   public <T> Future<T> submit(Callable<T> task)
/*     */   {
/* 433 */     return this.eventExecutor.submit(task);
/*     */   }
/*     */ 
/*     */   public <T> Future<T> submit(Runnable task, T result)
/*     */   {
/* 445 */     return this.eventExecutor.submit(task, result);
/*     */   }
/*     */ 
/*     */   public Future<?> submit(Runnable task)
/*     */   {
/* 454 */     return this.eventExecutor.submit(task);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.CriteriaIsolationExecutor
 * JD-Core Version:    0.6.0
 */