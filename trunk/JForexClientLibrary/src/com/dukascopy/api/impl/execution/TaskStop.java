/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ 
/*    */ public class TaskStop
/*    */   implements Task
/*    */ {
/* 14 */   private IStrategy strategy = null;
/*    */ 
/* 16 */   public TaskStop(IStrategy strategy) { this.strategy = strategy;
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 22 */     return Task.Type.STOP;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception
/*    */   {
/* 27 */     if (this.strategy != null) {
/* 28 */       this.strategy.onStop();
/*    */     }
/* 30 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskStop
 * JD-Core Version:    0.6.0
 */