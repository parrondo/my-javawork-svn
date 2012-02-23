/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.util.CollectionUtil;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ public class ConcurrentMergeScheduler extends MergeScheduler
/*     */ {
/*  48 */   private int mergeThreadPriority = -1;
/*     */ 
/*  50 */   protected List<MergeThread> mergeThreads = new ArrayList();
/*     */ 
/*  60 */   private int maxThreadCount = Math.max(1, Math.min(3, Runtime.getRuntime().availableProcessors() / 2));
/*     */ 
/*  64 */   private int maxMergeCount = this.maxThreadCount + 2;
/*     */   protected Directory dir;
/*     */   private volatile boolean closed;
/*     */   protected IndexWriter writer;
/*     */   protected int mergeThreadCount;
/*     */   protected static final Comparator<MergeThread> compareByMergeDocCount;
/*     */   static boolean anyExceptions;
/*     */   private boolean suppressExceptions;
/*     */   private static List<ConcurrentMergeScheduler> allInstances;
/*     */ 
/*     */   public ConcurrentMergeScheduler()
/*     */   {
/*  73 */     if (allInstances != null)
/*     */     {
/*  75 */       addMyself();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaxThreadCount(int count)
/*     */   {
/*  83 */     if (count < 1) {
/*  84 */       throw new IllegalArgumentException("count should be at least 1");
/*     */     }
/*  86 */     if (count > this.maxMergeCount) {
/*  87 */       throw new IllegalArgumentException("count should be <= maxMergeCount (= " + this.maxMergeCount + ")");
/*     */     }
/*  89 */     this.maxThreadCount = count;
/*     */   }
/*     */ 
/*     */   public int getMaxThreadCount()
/*     */   {
/*  94 */     return this.maxThreadCount;
/*     */   }
/*     */ 
/*     */   public void setMaxMergeCount(int count)
/*     */   {
/* 104 */     if (count < 1) {
/* 105 */       throw new IllegalArgumentException("count should be at least 1");
/*     */     }
/* 107 */     if (count < this.maxThreadCount) {
/* 108 */       throw new IllegalArgumentException("count should be >= maxThreadCount (= " + this.maxThreadCount + ")");
/*     */     }
/* 110 */     this.maxMergeCount = count;
/*     */   }
/*     */ 
/*     */   public int getMaxMergeCount()
/*     */   {
/* 115 */     return this.maxMergeCount;
/*     */   }
/*     */ 
/*     */   public synchronized int getMergeThreadPriority()
/*     */   {
/* 123 */     initMergeThreadPriority();
/* 124 */     return this.mergeThreadPriority;
/*     */   }
/*     */ 
/*     */   public synchronized void setMergeThreadPriority(int pri)
/*     */   {
/* 134 */     if ((pri > 10) || (pri < 1))
/* 135 */       throw new IllegalArgumentException("priority must be in range 1 .. 10 inclusive");
/* 136 */     this.mergeThreadPriority = pri;
/* 137 */     updateMergeThreads();
/*     */   }
/*     */ 
/*     */   protected synchronized void updateMergeThreads()
/*     */   {
/* 163 */     List activeMerges = new ArrayList();
/*     */ 
/* 165 */     int threadIdx = 0;
/* 166 */     while (threadIdx < this.mergeThreads.size()) {
/* 167 */       MergeThread mergeThread = (MergeThread)this.mergeThreads.get(threadIdx);
/* 168 */       if (!mergeThread.isAlive())
/*     */       {
/* 170 */         this.mergeThreads.remove(threadIdx);
/* 171 */         continue;
/*     */       }
/* 173 */       if (mergeThread.getCurrentMerge() != null) {
/* 174 */         activeMerges.add(mergeThread);
/*     */       }
/* 176 */       threadIdx++;
/*     */     }
/*     */ 
/* 180 */     CollectionUtil.mergeSort(activeMerges, compareByMergeDocCount);
/*     */ 
/* 182 */     int pri = this.mergeThreadPriority;
/* 183 */     int activeMergeCount = activeMerges.size();
/* 184 */     for (threadIdx = 0; threadIdx < activeMergeCount; threadIdx++) {
/* 185 */       MergeThread mergeThread = (MergeThread)activeMerges.get(threadIdx);
/* 186 */       MergePolicy.OneMerge merge = mergeThread.getCurrentMerge();
/* 187 */       if (merge == null)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 192 */       boolean doPause = threadIdx < activeMergeCount - this.maxThreadCount;
/*     */ 
/* 194 */       if ((verbose()) && 
/* 195 */         (doPause != merge.getPause())) {
/* 196 */         if (doPause)
/* 197 */           message("pause thread " + mergeThread.getName());
/*     */         else {
/* 199 */           message("unpause thread " + mergeThread.getName());
/*     */         }
/*     */       }
/*     */ 
/* 203 */       if (doPause != merge.getPause()) {
/* 204 */         merge.setPause(doPause);
/*     */       }
/*     */ 
/* 207 */       if (!doPause) {
/* 208 */         if (verbose()) {
/* 209 */           message("set priority of merge thread " + mergeThread.getName() + " to " + pri);
/*     */         }
/* 211 */         mergeThread.setThreadPriority(pri);
/* 212 */         pri = Math.min(10, 1 + pri);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean verbose()
/*     */   {
/* 228 */     return (this.writer != null) && (this.writer.verbose());
/*     */   }
/*     */ 
/*     */   protected void message(String message)
/*     */   {
/* 236 */     this.writer.message("CMS: " + message);
/*     */   }
/*     */ 
/*     */   private synchronized void initMergeThreadPriority() {
/* 240 */     if (this.mergeThreadPriority == -1)
/*     */     {
/* 243 */       this.mergeThreadPriority = (1 + Thread.currentThread().getPriority());
/* 244 */       if (this.mergeThreadPriority > 10)
/* 245 */         this.mergeThreadPriority = 10;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 251 */     this.closed = true;
/* 252 */     sync();
/*     */   }
/*     */ 
/*     */   public void sync()
/*     */   {
/*     */     while (true) {
/* 258 */       MergeThread toSync = null;
/* 259 */       synchronized (this) {
/* 260 */         for (MergeThread t : this.mergeThreads) {
/* 261 */           if (t.isAlive()) {
/* 262 */             toSync = t;
/* 263 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 267 */       if (toSync == null) break;
/*     */       try {
/* 269 */         toSync.join();
/*     */       } catch (InterruptedException ie) {
/* 271 */         throw new ThreadInterruptedException(ie);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized int mergeThreadCount()
/*     */   {
/* 284 */     int count = 0;
/* 285 */     for (MergeThread mt : this.mergeThreads) {
/* 286 */       if ((mt.isAlive()) && (mt.getCurrentMerge() != null)) {
/* 287 */         count++;
/*     */       }
/*     */     }
/* 290 */     return count;
/*     */   }
/*     */ 
/*     */   public void merge(IndexWriter writer)
/*     */     throws IOException
/*     */   {
/* 296 */     assert (!Thread.holdsLock(writer));
/*     */ 
/* 298 */     this.writer = writer;
/*     */ 
/* 300 */     initMergeThreadPriority();
/*     */ 
/* 302 */     this.dir = writer.getDirectory();
/*     */ 
/* 311 */     if (verbose()) {
/* 312 */       message("now merge");
/* 313 */       message("  index: " + writer.segString());
/*     */     }
/*     */ 
/*     */     while (true)
/*     */     {
/* 320 */       synchronized (this) {
/* 321 */         long startStallTime = 0L;
/* 322 */         if (mergeThreadCount() >= 1 + this.maxMergeCount) {
/* 323 */           startStallTime = System.currentTimeMillis();
/* 324 */           if (verbose())
/* 325 */             message("    too many merges; stalling...");
/*     */           try
/*     */           {
/* 328 */             wait();
/*     */           } catch (InterruptedException ie) {
/* 330 */             throw new ThreadInterruptedException(ie);
/*     */           }
/*     */         }
/*     */ 
/* 334 */         if ((!verbose()) || 
/* 335 */           (startStallTime == 0L)) continue;
/* 336 */         message("  stalled for " + (System.currentTimeMillis() - startStallTime) + " msec");
/*     */       }
/*     */ 
/* 345 */       MergePolicy.OneMerge merge = writer.getNextMerge();
/* 346 */       if (merge == null) {
/* 347 */         if (verbose())
/* 348 */           message("  no more merges pending; now return");
/* 349 */         return;
/*     */       }
/*     */ 
/* 354 */       writer.mergeInit(merge);
/*     */ 
/* 356 */       boolean success = false;
/*     */       try {
/* 358 */         synchronized (this) {
/* 359 */           message("  consider merge " + merge.segString(this.dir));
/*     */ 
/* 363 */           MergeThread merger = getMergeThread(writer, merge);
/* 364 */           this.mergeThreads.add(merger);
/* 365 */           if (verbose()) {
/* 366 */             message("    launch new thread [" + merger.getName() + "]");
/*     */           }
/*     */ 
/* 369 */           merger.start();
/*     */ 
/* 374 */           updateMergeThreads();
/*     */ 
/* 376 */           success = true;
/*     */         }
/*     */       } finally {
/* 379 */         if (!success)
/* 380 */           writer.mergeFinish(merge);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void doMerge(MergePolicy.OneMerge merge)
/*     */     throws IOException
/*     */   {
/* 388 */     this.writer.merge(merge);
/*     */   }
/*     */ 
/*     */   protected synchronized MergeThread getMergeThread(IndexWriter writer, MergePolicy.OneMerge merge) throws IOException
/*     */   {
/* 393 */     MergeThread thread = new MergeThread(writer, merge);
/* 394 */     thread.setThreadPriority(this.mergeThreadPriority);
/* 395 */     thread.setDaemon(true);
/* 396 */     thread.setName("Lucene Merge Thread #" + this.mergeThreadCount++);
/* 397 */     return thread;
/*     */   }
/*     */ 
/*     */   protected void handleMergeException(Throwable exc)
/*     */   {
/*     */     try
/*     */     {
/* 513 */       Thread.sleep(250L);
/*     */     } catch (InterruptedException ie) {
/* 515 */       throw new ThreadInterruptedException(ie);
/*     */     }
/* 517 */     throw new MergePolicy.MergeException(exc, this.dir);
/*     */   }
/*     */ 
/*     */   public static boolean anyUnhandledExceptions()
/*     */   {
/* 524 */     if (allInstances == null) {
/* 525 */       throw new RuntimeException("setTestMode() was not called; often this is because your test case's setUp method fails to call super.setUp in LuceneTestCase");
/*     */     }
/* 527 */     synchronized (allInstances) {
/* 528 */       int count = allInstances.size();
/*     */ 
/* 531 */       for (int i = 0; i < count; i++)
/* 532 */         ((ConcurrentMergeScheduler)allInstances.get(i)).sync();
/* 533 */       boolean v = anyExceptions;
/* 534 */       anyExceptions = false;
/* 535 */       return v;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void clearUnhandledExceptions() {
/* 540 */     synchronized (allInstances) {
/* 541 */       anyExceptions = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addMyself()
/*     */   {
/* 547 */     synchronized (allInstances) {
/* 548 */       int size = allInstances.size();
/* 549 */       int upto = 0;
/* 550 */       for (int i = 0; i < size; i++) {
/* 551 */         ConcurrentMergeScheduler other = (ConcurrentMergeScheduler)allInstances.get(i);
/* 552 */         if ((other.closed) && (0 == other.mergeThreadCount())) {
/*     */           continue;
/*     */         }
/* 555 */         allInstances.set(upto++, other);
/*     */       }
/* 557 */       allInstances.subList(upto, allInstances.size()).clear();
/* 558 */       allInstances.add(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setSuppressExceptions()
/*     */   {
/* 566 */     this.suppressExceptions = true;
/*     */   }
/*     */ 
/*     */   void clearSuppressExceptions()
/*     */   {
/* 571 */     this.suppressExceptions = false;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void setTestMode()
/*     */   {
/* 580 */     allInstances = new ArrayList();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 141 */     compareByMergeDocCount = new Comparator() {
/*     */       public int compare(ConcurrentMergeScheduler.MergeThread t1, ConcurrentMergeScheduler.MergeThread t2) {
/* 143 */         MergePolicy.OneMerge m1 = t1.getCurrentMerge();
/* 144 */         MergePolicy.OneMerge m2 = t2.getCurrentMerge();
/*     */ 
/* 146 */         int c1 = m1 == null ? 2147483647 : m1.totalDocCount;
/* 147 */         int c2 = m2 == null ? 2147483647 : m2.totalDocCount;
/*     */ 
/* 149 */         return c2 - c1;
/*     */       }
/*     */     };
/* 520 */     anyExceptions = false;
/*     */   }
/*     */ 
/*     */   protected class MergeThread extends Thread
/*     */   {
/*     */     IndexWriter tWriter;
/*     */     MergePolicy.OneMerge startMerge;
/*     */     MergePolicy.OneMerge runningMerge;
/*     */     private volatile boolean done;
/*     */ 
/*     */     public MergeThread(IndexWriter writer, MergePolicy.OneMerge startMerge)
/*     */       throws IOException
/*     */     {
/* 408 */       this.tWriter = writer;
/* 409 */       this.startMerge = startMerge;
/*     */     }
/*     */ 
/*     */     public synchronized void setRunningMerge(MergePolicy.OneMerge merge) {
/* 413 */       this.runningMerge = merge;
/*     */     }
/*     */ 
/*     */     public synchronized MergePolicy.OneMerge getRunningMerge() {
/* 417 */       return this.runningMerge;
/*     */     }
/*     */ 
/*     */     public synchronized MergePolicy.OneMerge getCurrentMerge() {
/* 421 */       if (this.done)
/* 422 */         return null;
/* 423 */       if (this.runningMerge != null) {
/* 424 */         return this.runningMerge;
/*     */       }
/* 426 */       return this.startMerge;
/*     */     }
/*     */ 
/*     */     public void setThreadPriority(int pri)
/*     */     {
/*     */       try {
/* 432 */         setPriority(pri);
/*     */       }
/*     */       catch (NullPointerException npe)
/*     */       {
/*     */       }
/*     */       catch (SecurityException se)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 447 */       MergePolicy.OneMerge merge = this.startMerge;
/*     */       try
/*     */       {
/* 451 */         if (ConcurrentMergeScheduler.this.verbose())
/* 452 */           ConcurrentMergeScheduler.this.message("  merge thread: start");
/*     */         while (true)
/*     */         {
/* 455 */           setRunningMerge(merge);
/* 456 */           ConcurrentMergeScheduler.this.doMerge(merge);
/*     */ 
/* 460 */           merge = this.tWriter.getNextMerge();
/* 461 */           if (merge == null) break;
/* 462 */           this.tWriter.mergeInit(merge);
/* 463 */           ConcurrentMergeScheduler.this.updateMergeThreads();
/* 464 */           if (ConcurrentMergeScheduler.this.verbose()) {
/* 465 */             ConcurrentMergeScheduler.this.message("  merge thread: do another merge " + merge.segString(ConcurrentMergeScheduler.this.dir));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 471 */         if (ConcurrentMergeScheduler.this.verbose()) {
/* 472 */           ConcurrentMergeScheduler.this.message("  merge thread: done");
/*     */         }
/*     */       }
/*     */       catch (Throwable exc)
/*     */       {
/* 477 */         if ((!(exc instanceof MergePolicy.MergeAbortedException)) && 
/* 478 */           (!ConcurrentMergeScheduler.this.suppressExceptions))
/*     */         {
/* 481 */           ConcurrentMergeScheduler.anyExceptions = true;
/* 482 */           ConcurrentMergeScheduler.this.handleMergeException(exc);
/*     */         }
/*     */       }
/*     */       finally {
/* 486 */         this.done = true;
/* 487 */         synchronized (ConcurrentMergeScheduler.this) {
/* 488 */           ConcurrentMergeScheduler.this.updateMergeThreads();
/* 489 */           ConcurrentMergeScheduler.this.notifyAll();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 496 */       MergePolicy.OneMerge merge = getRunningMerge();
/* 497 */       if (merge == null)
/* 498 */         merge = this.startMerge;
/* 499 */       return "merge thread: " + merge.segString(ConcurrentMergeScheduler.this.dir);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ConcurrentMergeScheduler
 * JD-Core Version:    0.6.0
 */