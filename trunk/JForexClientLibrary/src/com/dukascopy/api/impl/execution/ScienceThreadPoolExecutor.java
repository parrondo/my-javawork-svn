/*      */ package com.dukascopy.api.impl.execution;
/*      */ 
/*      */ import com.dukascopy.api.IContext;
/*      */ import com.dukascopy.api.JFException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.ConcurrentModificationException;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.AbstractExecutorService;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.Executors;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.RunnableFuture;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ 
/*      */ public class ScienceThreadPoolExecutor extends AbstractExecutorService
/*      */ {
/*  311 */   private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");
/*      */   volatile int runState;
/*      */   static final int RUNNING = 0;
/*      */   static final int SHUTDOWN = 1;
/*      */   static final int STOP = 2;
/*      */   static final int TERMINATED = 3;
/*      */   private final BlockingQueue<Runnable> workQueue;
/*  381 */   private final ReentrantLock mainLock = new ReentrantLock();
/*      */ 
/*  386 */   private final Condition termination = this.mainLock.newCondition();
/*      */ 
/*  392 */   private final HashSet<Worker> workers = new HashSet();
/*      */   private volatile long keepAliveTime;
/*      */   private volatile boolean allowCoreThreadTimeOut;
/*      */   private volatile int corePoolSize;
/*      */   private volatile int maximumPoolSize;
/*      */   private volatile int poolSize;
/*      */   private volatile ScienceRejectedExecutionHandler handler;
/*      */   private volatile ThreadFactory threadFactory;
/*      */   private int largestPoolSize;
/*      */   private long completedTaskCount;
/*  460 */   private static final ScienceRejectedExecutionHandler defaultHandler = new ScienceRejectedExecutionHandler(null)
/*      */   {
/*      */     public void rejectedExecution(Runnable r, ScienceThreadPoolExecutor executor)
/*      */     {
/*  464 */       throw new RejectedExecutionException();
/*      */     }
/*  460 */   };
/*      */ 
/*      */   public ScienceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
/*      */   {
/*  498 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultHandler);
/*      */   }
/*      */ 
/*      */   public ScienceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory)
/*      */   {
/*  532 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
/*      */   }
/*      */ 
/*      */   public ScienceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ScienceRejectedExecutionHandler handler)
/*      */   {
/*  566 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
/*      */   }
/*      */ 
/*      */   public ScienceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, ScienceRejectedExecutionHandler handler)
/*      */   {
/*  603 */     if ((corePoolSize < 0) || (maximumPoolSize <= 0) || (maximumPoolSize < corePoolSize) || (keepAliveTime < 0L))
/*      */     {
/*  607 */       throw new IllegalArgumentException();
/*  608 */     }if ((workQueue == null) || (threadFactory == null) || (handler == null))
/*  609 */       throw new NullPointerException();
/*  610 */     this.corePoolSize = corePoolSize;
/*  611 */     this.maximumPoolSize = maximumPoolSize;
/*  612 */     this.workQueue = workQueue;
/*  613 */     this.keepAliveTime = unit.toNanos(keepAliveTime);
/*  614 */     this.threadFactory = threadFactory;
/*  615 */     this.handler = handler;
/*      */   }
/*      */ 
/*      */   public void execute(Runnable command)
/*      */   {
/*  668 */     if (command == null)
/*  669 */       throw new NullPointerException();
/*  670 */     if ((this.poolSize >= this.corePoolSize) || (!addIfUnderCorePoolSize(command)))
/*  671 */       if ((this.runState == 0) && (this.workQueue.offer(command))) {
/*  672 */         if ((this.runState != 0) || (this.poolSize == 0))
/*  673 */           ensureQueuedTaskHandled(command);
/*      */       }
/*  675 */       else if (!addIfUnderMaximumPoolSize(command))
/*  676 */         reject(command);
/*      */   }
/*      */ 
/*      */   private Thread addThread(Runnable firstTask)
/*      */   {
/*  689 */     Worker w = new Worker(firstTask);
/*  690 */     Thread t = this.threadFactory.newThread(w);
/*  691 */     if (t != null) {
/*  692 */       w.thread = t;
/*  693 */       this.workers.add(w);
/*  694 */       int nt = ++this.poolSize;
/*  695 */       if (nt > this.largestPoolSize)
/*  696 */         this.largestPoolSize = nt;
/*      */     }
/*  698 */     return t;
/*      */   }
/*      */ 
/*      */   private boolean addIfUnderCorePoolSize(Runnable firstTask)
/*      */   {
/*  710 */     Thread t = null;
/*  711 */     ReentrantLock mainLock = this.mainLock;
/*  712 */     mainLock.lock();
/*      */     try {
/*  714 */       if ((this.poolSize < this.corePoolSize) && (this.runState == 0))
/*  715 */         t = addThread(firstTask);
/*      */     } finally {
/*  717 */       mainLock.unlock();
/*      */     }
/*  719 */     if (t == null)
/*  720 */       return false;
/*  721 */     t.start();
/*  722 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean addIfUnderMaximumPoolSize(Runnable firstTask)
/*      */   {
/*  734 */     Thread t = null;
/*  735 */     ReentrantLock mainLock = this.mainLock;
/*  736 */     mainLock.lock();
/*      */     try {
/*  738 */       if ((this.poolSize < this.maximumPoolSize) && (this.runState == 0))
/*  739 */         t = addThread(firstTask);
/*      */     } finally {
/*  741 */       mainLock.unlock();
/*      */     }
/*  743 */     if (t == null)
/*  744 */       return false;
/*  745 */     t.start();
/*  746 */     return true;
/*      */   }
/*      */ 
/*      */   private void ensureQueuedTaskHandled(Runnable command)
/*      */   {
/*  760 */     ReentrantLock mainLock = this.mainLock;
/*  761 */     mainLock.lock();
/*  762 */     boolean reject = false;
/*  763 */     Thread t = null;
/*      */     try {
/*  765 */       int state = this.runState;
/*  766 */       if ((state != 0) && (this.workQueue.remove(command)))
/*  767 */         reject = true;
/*  768 */       else if ((state < 2) && (this.poolSize < Math.max(this.corePoolSize, 1)) && (!this.workQueue.isEmpty()))
/*      */       {
/*  771 */         t = addThread(null);
/*      */       }
/*      */     } finally {
/*  773 */       mainLock.unlock();
/*      */     }
/*  775 */     if (reject)
/*  776 */       reject(command);
/*  777 */     else if (t != null)
/*  778 */       t.start();
/*      */   }
/*      */ 
/*      */   void reject(Runnable command)
/*      */   {
/*  785 */     this.handler.rejectedExecution(command, this);
/*      */   }
/*      */ 
/*      */   public final void runExceptTicksAndBars(ScienceWaitForUpdate waitForUpdate, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  936 */     long startTime = System.currentTimeMillis();
/*      */ 
/*  938 */     long timeoutMillis = unit.toMillis(timeout);
/*      */     Runnable task;
/*  939 */     while ((task = getTask(timeoutMillis - (System.currentTimeMillis() - startTime))) != null)
/*      */     {
/*  948 */       if ((this.runState < 2) && (Thread.interrupted()) && (this.runState >= 2))
/*      */       {
/*  951 */         Thread.currentThread().interrupt();
/*      */       }
/*  953 */       if ((!(task instanceof ScienceFuture)) || ((((ScienceFuture)task).getTask().getType() != Task.Type.TICK) && (((ScienceFuture)task).getTask().getType() != Task.Type.BAR)))
/*      */       {
/*  961 */         boolean ran = false;
/*  962 */         beforeExecute(Thread.currentThread(), task);
/*      */         try {
/*  964 */           task.run();
/*  965 */           ran = true;
/*  966 */           afterExecute(task, null);
/*  967 */           ReentrantLock mainLock = this.mainLock;
/*  968 */           mainLock.lock();
/*      */           try {
/*  970 */             this.completedTaskCount += 1L;
/*      */           } finally {
/*  972 */             mainLock.unlock();
/*      */           }
/*      */         } catch (RuntimeException ex) {
/*  975 */           if (!ran)
/*  976 */             afterExecute(task, ex);
/*  977 */           throw ex;
/*      */         }
/*      */       }
/*      */ 
/*  981 */       if ((waitForUpdate.updated()) || (System.currentTimeMillis() - startTime > timeoutMillis))
/*  982 */         return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void runExceptTicksAndBars(ScienceWaitForUpdate waitForUpdate, long timeout, TimeUnit unit, String[] states)
/*      */     throws InterruptedException, JFException
/*      */   {
/*  989 */     long startTime = System.currentTimeMillis();
/*      */ 
/*  991 */     long timeoutMillis = unit.toMillis(timeout);
/*      */     Runnable task;
/*  992 */     while ((task = getTask(timeoutMillis - (System.currentTimeMillis() - startTime))) != null)
/*      */     {
/* 1001 */       if ((this.runState < 2) && (Thread.interrupted()) && (this.runState >= 2))
/*      */       {
/* 1004 */         Thread.currentThread().interrupt();
/*      */       }
/* 1006 */       if ((!(task instanceof ScienceFuture)) || ((((ScienceFuture)task).getTask().getType() != Task.Type.TICK) && (((ScienceFuture)task).getTask().getType() != Task.Type.BAR)))
/*      */       {
/* 1014 */         boolean ran = false;
/* 1015 */         beforeExecute(Thread.currentThread(), task);
/*      */         try {
/* 1017 */           task.run();
/* 1018 */           ran = true;
/* 1019 */           afterExecute(task, null);
/* 1020 */           ReentrantLock mainLock = this.mainLock;
/* 1021 */           mainLock.lock();
/*      */           try {
/* 1023 */             this.completedTaskCount += 1L;
/*      */           } finally {
/* 1025 */             mainLock.unlock();
/*      */           }
/*      */         } catch (RuntimeException ex) {
/* 1028 */           if (!ran)
/* 1029 */             afterExecute(task, ex);
/* 1030 */           throw ex;
/*      */         }
/*      */       }
/*      */ 
/* 1034 */       if ((waitForUpdate.updated(states)) || (System.currentTimeMillis() - startTime > timeoutMillis))
/* 1035 */         return;
/*      */     }
/*      */   }
/*      */ 
/*      */   Runnable getTask()
/*      */   {
/*      */     while (true)
/*      */       try
/*      */       {
/* 1062 */         int state = this.runState;
/* 1063 */         if (state > 1)
/* 1064 */           return null;
/*      */         Runnable r;
/*      */         Runnable r;
/* 1066 */         if (state == 1) {
/* 1067 */           r = (Runnable)this.workQueue.poll();
/*      */         }
/*      */         else
/*      */         {
/*      */           Runnable r;
/* 1068 */           if ((this.poolSize > this.corePoolSize) || (this.allowCoreThreadTimeOut))
/* 1069 */             r = (Runnable)this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS);
/*      */           else
/* 1071 */             r = (Runnable)this.workQueue.take(); 
/*      */         }
/* 1072 */         if (r != null)
/* 1073 */           return r;
/* 1074 */         if (workerCanExit()) {
/* 1075 */           if (this.runState >= 1)
/* 1076 */             interruptIdleWorkers();
/* 1077 */           return null;
/*      */         }
/*      */ 
/* 1082 */         continue;
/*      */       } catch (InterruptedException ie) {
/*      */       }
/*      */   }
/*      */ 
/*      */   protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
/* 1088 */     return new ScienceFuture(callable);
/*      */   }
/*      */ 
/*      */   Runnable getTask(long timeout)
/*      */   {
/*      */     try {
/* 1094 */       int state = this.runState;
/* 1095 */       if (state > 1)
/* 1096 */         return null;
/*      */       Runnable r;
/*      */       Runnable r;
/* 1098 */       if (state == 1)
/* 1099 */         r = (Runnable)this.workQueue.poll();
/*      */       else
/* 1101 */         r = (Runnable)this.workQueue.poll(timeout, TimeUnit.MILLISECONDS);
/* 1102 */       if (r != null)
/* 1103 */         return r;
/* 1104 */       if (workerCanExit()) {
/* 1105 */         if (this.runState >= 1)
/* 1106 */           interruptIdleWorkers();
/* 1107 */         return null;
/*      */       }
/*      */     }
/*      */     catch (InterruptedException ie) {
/*      */     }
/* 1112 */     return null;
/*      */   }
/*      */   private boolean workerCanExit() {
/* 1123 */     ReentrantLock mainLock = this.mainLock;
/* 1124 */     mainLock.lock();
/*      */     boolean canExit;
/*      */     try { canExit = (this.runState >= 2) || (this.workQueue.isEmpty()) || ((this.allowCoreThreadTimeOut) && (this.poolSize > Math.max(1, this.corePoolSize)));
/*      */     }
/*      */     finally
/*      */     {
/* 1132 */       mainLock.unlock();
/*      */     }
/* 1134 */     return canExit;
/*      */   }
/*      */ 
/*      */   void interruptIdleWorkers()
/*      */   {
/* 1143 */     ReentrantLock mainLock = this.mainLock;
/* 1144 */     mainLock.lock();
/*      */     try {
/* 1146 */       for (Worker w : this.workers)
/* 1147 */         w.interruptIfIdle();
/*      */     } finally {
/* 1149 */       mainLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   void workerDone(Worker w)
/*      */   {
/* 1158 */     ReentrantLock mainLock = this.mainLock;
/* 1159 */     mainLock.lock();
/*      */     try {
/* 1161 */       this.completedTaskCount += w.completedTasks;
/* 1162 */       this.workers.remove(w);
/* 1163 */       if (--this.poolSize == 0)
/* 1164 */         tryTerminate();
/*      */     } finally {
/* 1166 */       mainLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void tryTerminate()
/*      */   {
/* 1184 */     if (this.poolSize == 0) {
/* 1185 */       int state = this.runState;
/* 1186 */       if ((state < 2) && (!this.workQueue.isEmpty())) {
/* 1187 */         state = 0;
/* 1188 */         Thread t = addThread(null);
/* 1189 */         if (t != null)
/* 1190 */           t.start();
/*      */       }
/* 1192 */       if ((state == 2) || (state == 1)) {
/* 1193 */         this.runState = 3;
/* 1194 */         this.termination.signalAll();
/* 1195 */         terminated();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void kill() {
/* 1201 */     ReentrantLock mainLock = this.mainLock;
/* 1202 */     mainLock.lock();
/*      */     try {
/* 1204 */       if (this.poolSize != 0) {
/* 1205 */         SecurityManager security = System.getSecurityManager();
/* 1206 */         if (security != null) {
/* 1207 */           for (Worker w : this.workers) {
/* 1208 */             security.checkAccess(w.thread);
/*      */           }
/*      */         }
/* 1211 */         if (this.runState < 2) {
/* 1212 */           this.runState = 2;
/*      */         }
/* 1214 */         drainQueue();
/*      */ 
/* 1216 */         for (Worker w : this.workers) {
/* 1217 */           w.thread.stop();
/*      */         }
/*      */ 
/* 1220 */         this.runState = 3;
/* 1221 */         this.termination.signalAll();
/* 1222 */         terminated();
/*      */       } else {
/* 1224 */         shutdownNow();
/*      */       }
/*      */     } finally {
/* 1227 */       mainLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void shutdown()
/*      */   {
/* 1276 */     SecurityManager security = System.getSecurityManager();
/* 1277 */     if (security != null) {
/* 1278 */       security.checkPermission(shutdownPerm);
/*      */     }
/* 1280 */     ReentrantLock mainLock = this.mainLock;
/* 1281 */     mainLock.lock();
/*      */     try {
/* 1283 */       if (security != null) {
/* 1284 */         for (Worker w : this.workers) {
/* 1285 */           security.checkAccess(w.thread);
/*      */         }
/*      */       }
/* 1288 */       int state = this.runState;
/* 1289 */       if (state < 1)
/* 1290 */         this.runState = 1;
/*      */       try
/*      */       {
/* 1293 */         for (Worker w : this.workers)
/* 1294 */           w.interruptIfIdle();
/*      */       }
/*      */       catch (SecurityException se) {
/* 1297 */         this.runState = state;
/*      */ 
/* 1299 */         throw se;
/*      */       }
/*      */ 
/* 1302 */       tryTerminate();
/*      */     } finally {
/* 1304 */       mainLock.unlock();
/*      */     }
/* 1306 */     if ((this.workQueue instanceof ScienceQueue))
/* 1307 */       ((ScienceQueue)this.workQueue).stop();
/*      */   }
/*      */ 
/*      */   public List<Runnable> shutdownNow()
/*      */   {
/* 1336 */     SecurityManager security = System.getSecurityManager();
/* 1337 */     if (security != null) {
/* 1338 */       security.checkPermission(shutdownPerm);
/*      */     }
/* 1340 */     ReentrantLock mainLock = this.mainLock;
/* 1341 */     mainLock.lock();
/*      */     try {
/* 1343 */       if (security != null) {
/* 1344 */         for (Worker w : this.workers) {
/* 1345 */           security.checkAccess(w.thread);
/*      */         }
/*      */       }
/* 1348 */       int state = this.runState;
/* 1349 */       if (state < 2)
/* 1350 */         this.runState = 2;
/*      */       try
/*      */       {
/* 1353 */         for (i$ = this.workers.iterator(); i$.hasNext(); ) { w = (Worker)i$.next();
/* 1354 */           w.interruptNow();
/*      */         }
/*      */       }
/*      */       catch (SecurityException se)
/*      */       {
/*      */         Iterator i$;
/* 1357 */         this.runState = state;
/*      */ 
/* 1359 */         throw se;
/*      */       }
/*      */ 
/* 1362 */       List tasks = drainQueue();
/* 1363 */       tryTerminate();
/* 1364 */       Worker w = tasks;
/*      */       return w; } finally { mainLock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   private List<Runnable> drainQueue()
/*      */   {
/* 1375 */     List taskList = new ArrayList();
/* 1376 */     this.workQueue.drainTo(taskList);
/*      */ 
/* 1384 */     while (!this.workQueue.isEmpty()) {
/* 1385 */       Iterator it = this.workQueue.iterator();
/*      */       try {
/* 1387 */         if (it.hasNext()) {
/* 1388 */           Runnable r = (Runnable)it.next();
/* 1389 */           if (this.workQueue.remove(r))
/* 1390 */             taskList.add(r);
/*      */         }
/*      */       } catch (ConcurrentModificationException ignore) {
/*      */       }
/*      */     }
/* 1395 */     return taskList;
/*      */   }
/*      */ 
/*      */   public boolean isShutdown() {
/* 1399 */     return this.runState != 0;
/*      */   }
/*      */ 
/*      */   public boolean isTerminating()
/*      */   {
/* 1413 */     int state = this.runState;
/* 1414 */     return (state == 1) || (state == 2);
/*      */   }
/*      */ 
/*      */   public boolean isTerminated() {
/* 1418 */     return this.runState == 3;
/*      */   }
/*      */ 
/*      */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
/*      */   {
/* 1423 */     long nanos = unit.toNanos(timeout);
/* 1424 */     ReentrantLock mainLock = this.mainLock;
/* 1425 */     mainLock.lock();
/*      */     try
/*      */     {
/*      */       while (true)
/*      */       {
/*      */         int i;
/* 1428 */         if (this.runState == 3) {
/* 1429 */           i = 1;
/*      */           return i;
/*      */         }
/* 1430 */         if (nanos <= 0L) {
/* 1431 */           i = 0;
/*      */           return i;
/*      */         }
/* 1432 */         nanos = this.termination.awaitNanos(nanos);
/*      */       }
/*      */     } finally {
/* 1435 */       mainLock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */   {
/* 1444 */     shutdown();
/*      */   }
/*      */ 
/*      */   public void setThreadFactory(ThreadFactory threadFactory)
/*      */   {
/* 1457 */     if (threadFactory == null)
/* 1458 */       throw new NullPointerException();
/* 1459 */     this.threadFactory = threadFactory;
/*      */   }
/*      */ 
/*      */   public ThreadFactory getThreadFactory()
/*      */   {
/* 1469 */     return this.threadFactory;
/*      */   }
/*      */ 
/*      */   public void setRejectedExecutionHandler(ScienceRejectedExecutionHandler handler)
/*      */   {
/* 1480 */     if (handler == null)
/* 1481 */       throw new NullPointerException();
/* 1482 */     this.handler = handler;
/*      */   }
/*      */ 
/*      */   public ScienceRejectedExecutionHandler getRejectedExecutionHandler()
/*      */   {
/* 1492 */     return this.handler;
/*      */   }
/*      */ 
/*      */   public void setCorePoolSize(int corePoolSize)
/*      */   {
/* 1508 */     if (corePoolSize < 0)
/* 1509 */       throw new IllegalArgumentException();
/* 1510 */     ReentrantLock mainLock = this.mainLock;
/* 1511 */     mainLock.lock();
/*      */     try {
/* 1513 */       int extra = this.corePoolSize - corePoolSize;
/* 1514 */       this.corePoolSize = corePoolSize;
/* 1515 */       if (extra < 0) {
/* 1516 */         int n = this.workQueue.size();
/* 1517 */         while ((extra++ < 0) && (n-- > 0) && (this.poolSize < corePoolSize)) {
/* 1518 */           Thread t = addThread(null);
/* 1519 */           if (t == null) break;
/* 1520 */           t.start();
/*      */         }
/*      */ 
/*      */       }
/* 1525 */       else if ((extra > 0) && (this.poolSize > corePoolSize)) {
/*      */         try {
/* 1527 */           Iterator it = this.workers.iterator();
/*      */ 
/* 1531 */           while ((it.hasNext()) && (extra-- > 0) && (this.poolSize > corePoolSize) && (this.workQueue.remainingCapacity() == 0))
/* 1532 */             ((Worker)it.next()).interruptIfIdle();
/*      */         } catch (SecurityException ignore) {
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1538 */       mainLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getCorePoolSize()
/*      */   {
/* 1549 */     return this.corePoolSize;
/*      */   }
/*      */ 
/*      */   public boolean prestartCoreThread()
/*      */   {
/* 1560 */     return addIfUnderCorePoolSize(null);
/*      */   }
/*      */ 
/*      */   public int prestartAllCoreThreads()
/*      */   {
/* 1570 */     int n = 0;
/* 1571 */     while (addIfUnderCorePoolSize(null))
/* 1572 */       n++;
/* 1573 */     return n;
/*      */   }
/*      */ 
/*      */   public boolean allowsCoreThreadTimeOut()
/*      */   {
/* 1589 */     return this.allowCoreThreadTimeOut;
/*      */   }
/*      */ 
/*      */   public void allowCoreThreadTimeOut(boolean value)
/*      */   {
/* 1609 */     if ((value) && (this.keepAliveTime <= 0L)) {
/* 1610 */       throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
/*      */     }
/* 1612 */     this.allowCoreThreadTimeOut = value;
/*      */   }
/*      */ 
/*      */   public void setMaximumPoolSize(int maximumPoolSize)
/*      */   {
/* 1628 */     if ((maximumPoolSize <= 0) || (maximumPoolSize < this.corePoolSize))
/* 1629 */       throw new IllegalArgumentException();
/* 1630 */     ReentrantLock mainLock = this.mainLock;
/* 1631 */     mainLock.lock();
/*      */     try {
/* 1633 */       int extra = this.maximumPoolSize - maximumPoolSize;
/* 1634 */       this.maximumPoolSize = maximumPoolSize;
/* 1635 */       if ((extra > 0) && (this.poolSize > maximumPoolSize))
/*      */         try {
/* 1637 */           Iterator it = this.workers.iterator();
/*      */ 
/* 1640 */           while ((it.hasNext()) && (extra > 0) && (this.poolSize > maximumPoolSize)) {
/* 1641 */             ((Worker)it.next()).interruptIfIdle();
/* 1642 */             extra--;
/*      */           }
/*      */         }
/*      */         catch (SecurityException ignore) {
/*      */         }
/*      */     }
/*      */     finally {
/* 1649 */       mainLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getMaximumPoolSize()
/*      */   {
/* 1660 */     return this.maximumPoolSize;
/*      */   }
/*      */ 
/*      */   public void setKeepAliveTime(long time, TimeUnit unit)
/*      */   {
/* 1677 */     if (time < 0L)
/* 1678 */       throw new IllegalArgumentException();
/* 1679 */     if ((time == 0L) && (allowsCoreThreadTimeOut()))
/* 1680 */       throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
/* 1681 */     this.keepAliveTime = unit.toNanos(time);
/*      */   }
/*      */ 
/*      */   public long getKeepAliveTime(TimeUnit unit)
/*      */   {
/* 1694 */     return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
/*      */   }
/*      */ 
/*      */   public BlockingQueue<Runnable> getQueue()
/*      */   {
/* 1708 */     return this.workQueue;
/*      */   }
/*      */ 
/*      */   public boolean remove(Runnable task)
/*      */   {
/* 1728 */     return getQueue().remove(task);
/*      */   }
/*      */ 
/*      */   public void purge()
/*      */   {
/*      */     try
/*      */     {
/* 1744 */       Iterator it = getQueue().iterator();
/* 1745 */       while (it.hasNext()) {
/* 1746 */         Runnable r = (Runnable)it.next();
/* 1747 */         if ((r instanceof Future)) {
/* 1748 */           Future c = (Future)r;
/* 1749 */           if (c.isCancelled())
/* 1750 */             it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (ConcurrentModificationException ex) {
/* 1755 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getPoolSize()
/*      */   {
/* 1767 */     return this.poolSize;
/*      */   }
/*      */ 
/*      */   public int getActiveCount()
/*      */   {
/* 1777 */     ReentrantLock mainLock = this.mainLock;
/* 1778 */     mainLock.lock();
/*      */     try {
/* 1780 */       int n = 0;
/* 1781 */       for (Worker w : this.workers) {
/* 1782 */         if (w.isActive())
/* 1783 */           n++;
/*      */       }
/* 1785 */       ??? = n;
/*      */       return ???; } finally { mainLock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public int getLargestPoolSize()
/*      */   {
/* 1798 */     ReentrantLock mainLock = this.mainLock;
/* 1799 */     mainLock.lock();
/*      */     try {
/* 1801 */       int i = this.largestPoolSize;
/*      */       return i; } finally { mainLock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public long getTaskCount()
/*      */   {
/* 1816 */     ReentrantLock mainLock = this.mainLock;
/* 1817 */     mainLock.lock();
/*      */     try {
/* 1819 */       long n = this.completedTaskCount;
/* 1820 */       for (Worker w : this.workers) {
/* 1821 */         n += w.completedTasks;
/* 1822 */         if (w.isActive())
/* 1823 */           n += 1L;
/*      */       }
/* 1825 */       ??? = n + this.workQueue.size();
/*      */       return ???; } finally { mainLock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public long getCompletedTaskCount()
/*      */   {
/* 1841 */     ReentrantLock mainLock = this.mainLock;
/* 1842 */     mainLock.lock();
/*      */     try {
/* 1844 */       long n = this.completedTaskCount;
/* 1845 */       for (Worker w : this.workers)
/* 1846 */         n += w.completedTasks;
/* 1847 */       ??? = n;
/*      */       return ???; } finally { mainLock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   protected void beforeExecute(Thread t, Runnable r)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void afterExecute(Runnable r, Throwable t)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void terminated()
/*      */   {
/*      */   }
/*      */ 
/*      */   private final class Worker
/*      */     implements Runnable
/*      */   {
/*  827 */     private final ReentrantLock runLock = new ReentrantLock();
/*      */     private Runnable firstTask;
/*      */     volatile long completedTasks;
/*      */     Thread thread;
/*      */ 
/*      */     Worker(Runnable firstTask)
/*      */     {
/*  847 */       this.firstTask = firstTask;
/*      */     }
/*      */ 
/*      */     boolean isActive() {
/*  851 */       return this.runLock.isLocked();
/*      */     }
/*      */ 
/*      */     void interruptIfIdle()
/*      */     {
/*  858 */       ReentrantLock runLock = this.runLock;
/*  859 */       if (runLock.tryLock())
/*      */         try {
/*  861 */           if (this.thread != Thread.currentThread())
/*  862 */             this.thread.interrupt();
/*      */         } finally {
/*  864 */           runLock.unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     void interruptNow()
/*      */     {
/*  873 */       this.thread.interrupt();
/*      */     }
/*      */ 
/*      */     private void runTask(Runnable task)
/*      */     {
/*  880 */       ReentrantLock runLock = this.runLock;
/*  881 */       runLock.lock();
/*      */       try
/*      */       {
/*  890 */         if ((ScienceThreadPoolExecutor.this.runState < 2) && (Thread.interrupted()) && (ScienceThreadPoolExecutor.this.runState >= 2))
/*      */         {
/*  893 */           this.thread.interrupt();
/*      */         }
/*      */ 
/*  901 */         boolean ran = false;
/*  902 */         ScienceThreadPoolExecutor.this.beforeExecute(this.thread, task);
/*      */         try {
/*  904 */           task.run();
/*  905 */           ran = true;
/*  906 */           ScienceThreadPoolExecutor.this.afterExecute(task, null);
/*  907 */           this.completedTasks += 1L;
/*      */         } catch (RuntimeException ex) {
/*  909 */           if (!ran)
/*  910 */             ScienceThreadPoolExecutor.this.afterExecute(task, ex);
/*  911 */           throw ex;
/*      */         }
/*      */       } finally {
/*  914 */         runLock.unlock();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/*  923 */         Runnable task = this.firstTask;
/*  924 */         this.firstTask = null;
/*  925 */         while ((task != null) || ((task = ScienceThreadPoolExecutor.this.getTask()) != null)) {
/*  926 */           runTask(task);
/*  927 */           task = null;
/*      */         }
/*      */       } finally {
/*  930 */         ScienceThreadPoolExecutor.this.workerDone(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.ScienceThreadPoolExecutor
 * JD-Core Version:    0.6.0
 */