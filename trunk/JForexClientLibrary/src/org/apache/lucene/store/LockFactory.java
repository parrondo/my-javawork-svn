/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class LockFactory
/*    */ {
/* 38 */   protected String lockPrefix = null;
/*    */ 
/*    */   public void setLockPrefix(String lockPrefix)
/*    */   {
/* 51 */     this.lockPrefix = lockPrefix;
/*    */   }
/*    */ 
/*    */   public String getLockPrefix()
/*    */   {
/* 58 */     return this.lockPrefix;
/*    */   }
/*    */ 
/*    */   public abstract Lock makeLock(String paramString);
/*    */ 
/*    */   public abstract void clearLock(String paramString)
/*    */     throws IOException;
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.LockFactory
 * JD-Core Version:    0.6.0
 */