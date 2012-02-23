/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class SimpleFSLockFactory extends FSLockFactory
/*    */ {
/*    */   public SimpleFSLockFactory()
/*    */     throws IOException
/*    */   {
/* 64 */     this((File)null);
/*    */   }
/*    */ 
/*    */   public SimpleFSLockFactory(File lockDir)
/*    */     throws IOException
/*    */   {
/* 72 */     setLockDir(lockDir);
/*    */   }
/*    */ 
/*    */   public SimpleFSLockFactory(String lockDirName)
/*    */     throws IOException
/*    */   {
/* 80 */     setLockDir(new File(lockDirName));
/*    */   }
/*    */ 
/*    */   public Lock makeLock(String lockName)
/*    */   {
/* 85 */     if (this.lockPrefix != null) {
/* 86 */       lockName = this.lockPrefix + "-" + lockName;
/*    */     }
/* 88 */     return new SimpleFSLock(this.lockDir, lockName);
/*    */   }
/*    */ 
/*    */   public void clearLock(String lockName) throws IOException
/*    */   {
/* 93 */     if (this.lockDir.exists()) {
/* 94 */       if (this.lockPrefix != null) {
/* 95 */         lockName = this.lockPrefix + "-" + lockName;
/*    */       }
/* 97 */       File lockFile = new File(this.lockDir, lockName);
/* 98 */       if ((lockFile.exists()) && (!lockFile.delete()))
/* 99 */         throw new IOException("Cannot delete " + lockFile);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.SimpleFSLockFactory
 * JD-Core Version:    0.6.0
 */