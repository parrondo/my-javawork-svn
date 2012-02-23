/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ public class NoLockFactory extends LockFactory
/*    */ {
/* 33 */   private static NoLock singletonLock = new NoLock();
/* 34 */   private static NoLockFactory singleton = new NoLockFactory();
/*    */ 
/*    */   public static NoLockFactory getNoLockFactory()
/*    */   {
/* 46 */     return singleton;
/*    */   }
/*    */ 
/*    */   public Lock makeLock(String lockName)
/*    */   {
/* 51 */     return singletonLock;
/*    */   }
/*    */ 
/*    */   public void clearLock(String lockName)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.NoLockFactory
 * JD-Core Version:    0.6.0
 */