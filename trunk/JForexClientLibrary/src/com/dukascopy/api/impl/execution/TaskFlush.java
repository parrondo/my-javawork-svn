/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ public class TaskFlush
/*    */   implements Task<Object>
/*    */ {
/*    */   private Object notifyObject;
/*    */ 
/*    */   public TaskFlush(Object notifyObject)
/*    */   {
/* 14 */     this.notifyObject = notifyObject;
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 19 */     return Task.Type.CUSTOM;
/*    */   }
/*    */ 
/*    */   public Object call() throws Exception
/*    */   {
/* 24 */     synchronized (this.notifyObject) {
/* 25 */       this.notifyObject.notifyAll();
/*    */     }
/* 27 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskFlush
 * JD-Core Version:    0.6.0
 */