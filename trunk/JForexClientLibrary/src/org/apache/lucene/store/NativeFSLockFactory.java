/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class NativeFSLockFactory extends FSLockFactory
/*     */ {
/*     */   public NativeFSLockFactory()
/*     */     throws IOException
/*     */   {
/*  67 */     this((File)null);
/*     */   }
/*     */ 
/*     */   public NativeFSLockFactory(String lockDirName)
/*     */     throws IOException
/*     */   {
/*  77 */     this(new File(lockDirName));
/*     */   }
/*     */ 
/*     */   public NativeFSLockFactory(File lockDir)
/*     */     throws IOException
/*     */   {
/*  87 */     setLockDir(lockDir);
/*     */   }
/*     */ 
/*     */   public synchronized Lock makeLock(String lockName)
/*     */   {
/*  92 */     if (this.lockPrefix != null)
/*  93 */       lockName = this.lockPrefix + "-" + lockName;
/*  94 */     return new NativeFSLock(this.lockDir, lockName);
/*     */   }
/*     */ 
/*     */   public void clearLock(String lockName)
/*     */     throws IOException
/*     */   {
/* 103 */     if (this.lockDir.exists())
/*     */     {
/* 110 */       makeLock(lockName).release();
/*     */ 
/* 112 */       if (this.lockPrefix != null) {
/* 113 */         lockName = this.lockPrefix + "-" + lockName;
/*     */       }
/*     */ 
/* 117 */       new File(this.lockDir, lockName).delete();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.NativeFSLockFactory
 * JD-Core Version:    0.6.0
 */