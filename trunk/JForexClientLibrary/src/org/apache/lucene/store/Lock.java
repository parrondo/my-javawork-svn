/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.util.ThreadInterruptedException;
/*     */ 
/*     */ public abstract class Lock
/*     */ {
/*  38 */   public static long LOCK_POLL_INTERVAL = 1000L;
/*     */   public static final long LOCK_OBTAIN_WAIT_FOREVER = -1L;
/*     */   protected Throwable failureReason;
/*     */ 
/*     */   public abstract boolean obtain()
/*     */     throws IOException;
/*     */ 
/*     */   public boolean obtain(long lockWaitTimeout)
/*     */     throws LockObtainFailedException, IOException
/*     */   {
/*  71 */     this.failureReason = null;
/*  72 */     boolean locked = obtain();
/*  73 */     if ((lockWaitTimeout < 0L) && (lockWaitTimeout != -1L)) {
/*  74 */       throw new IllegalArgumentException("lockWaitTimeout should be LOCK_OBTAIN_WAIT_FOREVER or a non-negative number (got " + lockWaitTimeout + ")");
/*     */     }
/*  76 */     long maxSleepCount = lockWaitTimeout / LOCK_POLL_INTERVAL;
/*  77 */     long sleepCount = 0L;
/*  78 */     while (!locked) {
/*  79 */       if ((lockWaitTimeout != -1L) && (sleepCount++ >= maxSleepCount)) {
/*  80 */         String reason = "Lock obtain timed out: " + toString();
/*  81 */         if (this.failureReason != null) {
/*  82 */           reason = reason + ": " + this.failureReason;
/*     */         }
/*  84 */         LockObtainFailedException e = new LockObtainFailedException(reason);
/*  85 */         if (this.failureReason != null) {
/*  86 */           e.initCause(this.failureReason);
/*     */         }
/*  88 */         throw e;
/*     */       }
/*     */       try {
/*  91 */         Thread.sleep(LOCK_POLL_INTERVAL);
/*     */       } catch (InterruptedException ie) {
/*  93 */         throw new ThreadInterruptedException(ie);
/*     */       }
/*  95 */       locked = obtain();
/*     */     }
/*  97 */     return locked;
/*     */   }
/*     */ 
/*     */   public abstract void release()
/*     */     throws IOException;
/*     */ 
/*     */   public abstract boolean isLocked() throws IOException;
/*     */ 
/*     */   public static abstract class With
/*     */   {
/*     */     private Lock lock;
/*     */     private long lockWaitTimeout;
/*     */ 
/*     */     public With(Lock lock, long lockWaitTimeout)
/*     */     {
/* 116 */       this.lock = lock;
/* 117 */       this.lockWaitTimeout = lockWaitTimeout;
/*     */     }
/*     */ 
/*     */     protected abstract Object doBody()
/*     */       throws IOException;
/*     */ 
/*     */     public Object run()
/*     */       throws LockObtainFailedException, IOException
/*     */     {
/* 132 */       boolean locked = false;
/*     */       try {
/* 134 */         locked = this.lock.obtain(this.lockWaitTimeout);
/* 135 */         localObject1 = doBody();
/*     */       }
/*     */       finally
/*     */       {
/*     */         Object localObject1;
/* 137 */         if (locked)
/* 138 */           this.lock.release();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.Lock
 * JD-Core Version:    0.6.0
 */