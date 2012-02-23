/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IContext;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ 
/*    */ public class TaskStart
/*    */   implements Task
/*    */ {
/* 15 */   private IContext context = null;
/* 16 */   private IStrategy strategy = null;
/*    */ 
/* 18 */   public TaskStart(IContext context, IStrategy strategy) { this.context = context;
/* 19 */     this.strategy = strategy; }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 23 */     return Task.Type.START;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception {
/* 27 */     this.strategy.onStart(this.context);
/* 28 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskStart
 * JD-Core Version:    0.6.0
 */