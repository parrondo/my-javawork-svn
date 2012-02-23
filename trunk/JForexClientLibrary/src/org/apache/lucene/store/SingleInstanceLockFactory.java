/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.HashSet;
/*    */ 
/*    */ public class SingleInstanceLockFactory extends LockFactory
/*    */ {
/* 36 */   private HashSet<String> locks = new HashSet();
/*    */ 
/*    */   public Lock makeLock(String lockName)
/*    */   {
/* 43 */     return new SingleInstanceLock(this.locks, lockName);
/*    */   }
/*    */ 
/*    */   public void clearLock(String lockName) throws IOException
/*    */   {
/* 48 */     synchronized (this.locks) {
/* 49 */       if (this.locks.contains(lockName))
/* 50 */         this.locks.remove(lockName);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.SingleInstanceLockFactory
 * JD-Core Version:    0.6.0
 */