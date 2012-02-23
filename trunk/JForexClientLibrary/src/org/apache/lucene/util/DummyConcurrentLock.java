/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.locks.Condition;
/*    */ import java.util.concurrent.locks.Lock;
/*    */ 
/*    */ public final class DummyConcurrentLock
/*    */   implements Lock
/*    */ {
/* 32 */   public static final DummyConcurrentLock INSTANCE = new DummyConcurrentLock();
/*    */ 
/*    */   public void lock() {
/*    */   }
/*    */   public void lockInterruptibly() {
/*    */   }
/*    */   public boolean tryLock() {
/* 39 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean tryLock(long time, TimeUnit unit) {
/* 43 */     return true;
/*    */   }
/*    */   public void unlock() {
/*    */   }
/*    */ 
/*    */   public Condition newCondition() {
/* 49 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.DummyConcurrentLock
 * JD-Core Version:    0.6.0
 */