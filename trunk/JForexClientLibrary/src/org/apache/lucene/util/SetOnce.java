/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ 
/*    */ public final class SetOnce<T>
/*    */ {
/* 40 */   private volatile T obj = null;
/*    */   private final AtomicBoolean set;
/*    */ 
/*    */   public SetOnce()
/*    */   {
/* 48 */     this.set = new AtomicBoolean(false);
/*    */   }
/*    */ 
/*    */   public SetOnce(T obj)
/*    */   {
/* 60 */     this.obj = obj;
/* 61 */     this.set = new AtomicBoolean(true);
/*    */   }
/*    */ 
/*    */   public final void set(T obj)
/*    */   {
/* 66 */     if (this.set.compareAndSet(false, true))
/* 67 */       this.obj = obj;
/*    */     else
/* 69 */       throw new AlreadySetException();
/*    */   }
/*    */ 
/*    */   public final T get()
/*    */   {
/* 75 */     return this.obj;
/*    */   }
/*    */ 
/*    */   public static final class AlreadySetException extends RuntimeException
/*    */   {
/*    */     public AlreadySetException()
/*    */     {
/* 36 */       super();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.SetOnce
 * JD-Core Version:    0.6.0
 */