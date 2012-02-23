/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class TaskCustom<T>
/*    */   implements Task<T>
/*    */ {
/* 16 */   private Callable<T> callable = null;
/*    */   private JForexTaskManager taskManager;
/*    */   private boolean force;
/*    */ 
/*    */   public TaskCustom(JForexTaskManager taskManager, Callable<T> callable, boolean force)
/*    */   {
/* 21 */     this.callable = callable;
/* 22 */     this.taskManager = taskManager;
/* 23 */     this.force = force;
/*    */   }
/*    */ 
/*    */   public Task.Type getType() {
/* 27 */     return Task.Type.CUSTOM;
/*    */   }
/*    */ 
/*    */   public T call() throws Exception {
/* 31 */     if ((this.taskManager.isStrategyStopping()) && (!this.force)) {
/* 32 */       return null;
/*    */     }
/* 34 */     return this.callable.call();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskCustom
 * JD-Core Version:    0.6.0
 */