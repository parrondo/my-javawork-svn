/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.HashSet;
/*    */ 
/*    */ class SingleInstanceLock extends Lock
/*    */ {
/*    */   String lockName;
/*    */   private HashSet<String> locks;
/*    */ 
/*    */   public SingleInstanceLock(HashSet<String> locks, String lockName)
/*    */   {
/* 62 */     this.locks = locks;
/* 63 */     this.lockName = lockName;
/*    */   }
/*    */ 
/*    */   public boolean obtain() throws IOException
/*    */   {
/* 68 */     synchronized (this.locks) {
/* 69 */       return this.locks.add(this.lockName);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void release()
/*    */   {
/* 75 */     synchronized (this.locks) {
/* 76 */       this.locks.remove(this.lockName);
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isLocked()
/*    */   {
/* 82 */     synchronized (this.locks) {
/* 83 */       return this.locks.contains(this.lockName);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 89 */     return super.toString() + ": " + this.lockName;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.SingleInstanceLock
 * JD-Core Version:    0.6.0
 */