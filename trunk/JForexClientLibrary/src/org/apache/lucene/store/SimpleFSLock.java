/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ 
/*     */ class SimpleFSLock extends Lock
/*     */ {
/*     */   File lockFile;
/*     */   File lockDir;
/*     */ 
/*     */   public SimpleFSLock(File lockDir, String lockFileName)
/*     */   {
/* 111 */     this.lockDir = lockDir;
/* 112 */     this.lockFile = new File(lockDir, lockFileName);
/*     */   }
/*     */ 
/*     */   public boolean obtain()
/*     */     throws IOException
/*     */   {
/* 119 */     if (!this.lockDir.exists()) {
/* 120 */       if (!this.lockDir.mkdirs())
/* 121 */         throw new IOException("Cannot create directory: " + this.lockDir.getAbsolutePath());
/*     */     }
/* 123 */     else if (!this.lockDir.isDirectory()) {
/* 124 */       throw new IOException("Found regular file where directory expected: " + this.lockDir.getAbsolutePath());
/*     */     }
/*     */ 
/* 127 */     return this.lockFile.createNewFile();
/*     */   }
/*     */ 
/*     */   public void release() throws LockReleaseFailedException
/*     */   {
/* 132 */     if ((this.lockFile.exists()) && (!this.lockFile.delete()))
/* 133 */       throw new LockReleaseFailedException("failed to delete " + this.lockFile);
/*     */   }
/*     */ 
/*     */   public boolean isLocked()
/*     */   {
/* 138 */     return this.lockFile.exists();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 143 */     return "SimpleFSLock@" + this.lockFile;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.SimpleFSLock
 * JD-Core Version:    0.6.0
 */