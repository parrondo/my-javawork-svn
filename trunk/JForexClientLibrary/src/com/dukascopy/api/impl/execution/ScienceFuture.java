/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.FutureTask;
/*    */ 
/*    */ public class ScienceFuture<T> extends FutureTask<T>
/*    */ {
/* 10 */   private Task task = null;
/*    */ 
/*    */   public ScienceFuture(Callable<T> callable) {
/* 13 */     super(callable);
/* 14 */     this.task = ((Task)callable);
/*    */   }
/*    */ 
/*    */   public Task getTask() {
/* 18 */     return this.task;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.ScienceFuture
 * JD-Core Version:    0.6.0
 */