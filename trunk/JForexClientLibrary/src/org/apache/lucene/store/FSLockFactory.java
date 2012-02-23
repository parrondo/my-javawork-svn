/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class FSLockFactory extends LockFactory
/*    */ {
/* 31 */   protected File lockDir = null;
/*    */ 
/*    */   protected final void setLockDir(File lockDir)
/*    */   {
/* 41 */     if (this.lockDir != null)
/* 42 */       throw new IllegalStateException("You can set the lock directory for this factory only once.");
/* 43 */     this.lockDir = lockDir;
/*    */   }
/*    */ 
/*    */   public File getLockDir()
/*    */   {
/* 50 */     return this.lockDir;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.FSLockFactory
 * JD-Core Version:    0.6.0
 */