/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ class NativeFSLock extends Lock
/*     */ {
/*     */   private RandomAccessFile f;
/*     */   private FileChannel channel;
/*     */   private FileLock lock;
/*     */   private File path;
/*     */   private File lockDir;
/* 149 */   private static HashSet<String> LOCK_HELD = new HashSet();
/*     */ 
/*     */   public NativeFSLock(File lockDir, String lockFileName) {
/* 152 */     this.lockDir = lockDir;
/* 153 */     this.path = new File(lockDir, lockFileName);
/*     */   }
/*     */ 
/*     */   private synchronized boolean lockExists() {
/* 157 */     return this.lock != null;
/*     */   }
/*     */ 
/*     */   public synchronized boolean obtain()
/*     */     throws IOException
/*     */   {
/* 163 */     if (lockExists())
/*     */     {
/* 165 */       return false;
/*     */     }
/*     */ 
/* 169 */     if (!this.lockDir.exists()) {
/* 170 */       if (!this.lockDir.mkdirs())
/* 171 */         throw new IOException("Cannot create directory: " + this.lockDir.getAbsolutePath());
/*     */     }
/* 173 */     else if (!this.lockDir.isDirectory()) {
/* 174 */       throw new IOException("Found regular file where directory expected: " + this.lockDir.getAbsolutePath());
/*     */     }
/*     */ 
/* 178 */     String canonicalPath = this.path.getCanonicalPath();
/*     */ 
/* 180 */     boolean markedHeld = false;
/*     */     try
/*     */     {
/* 187 */       synchronized (LOCK_HELD) {
/* 188 */         if (LOCK_HELD.contains(canonicalPath))
/*     */         {
/* 190 */           int i = 0; jsr 224; return i;
/*     */         }
/*     */ 
/* 196 */         LOCK_HELD.add(canonicalPath);
/* 197 */         markedHeld = true;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 214 */         this.channel = this.f.getChannel();
/*     */         try {
/* 216 */           this.lock = this.channel.tryLock();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 227 */           this.failureReason = e;
/*     */         }
/*     */         finally {
/* 229 */           if (this.lock != null);
/*     */         }
/* 233 */         ret;
/*     */       }
/*     */       finally
/*     */       {
/* 238 */         if (this.channel != null);
/*     */       }
/*     */ 
/* 242 */       ret;
/*     */     }
/*     */     finally
/*     */     {
/* 249 */       if ((markedHeld) && (!lockExists())) {
/* 250 */         synchronized (LOCK_HELD) {
/* 251 */           if (LOCK_HELD.contains(canonicalPath)) {
/* 252 */             LOCK_HELD.remove(canonicalPath);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 257 */     return lockExists();
/*     */   }
/*     */ 
/*     */   public synchronized void release() throws IOException
/*     */   {
/* 262 */     if (lockExists()) {
/*     */       try {
/* 264 */         this.lock.release();
/*     */       } finally {
/* 266 */         this.lock = null;
/*     */       }
/*     */ 
/* 277 */       ret; ret;
/*     */ 
/* 284 */       this.path.delete();
/*     */     }
/*     */     else
/*     */     {
/* 291 */       boolean obtained = false;
/*     */       try {
/* 293 */         if (!(obtained = obtain())) {
/* 294 */           throw new LockReleaseFailedException("Cannot forcefully unlock a NativeFSLock which is held by another indexer component: " + this.path);
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 299 */         if (obtained)
/* 300 */           release();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized boolean isLocked()
/*     */   {
/* 311 */     if (lockExists()) return true;
/*     */ 
/* 314 */     if (!this.path.exists()) return false;
/*     */ 
/*     */     try
/*     */     {
/* 318 */       boolean obtained = obtain();
/* 319 */       if (obtained) release();
/* 320 */       return !obtained; } catch (IOException ioe) {
/*     */     }
/* 322 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 328 */     return "NativeFSLock@" + this.path;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.NativeFSLock
 * JD-Core Version:    0.6.0
 */