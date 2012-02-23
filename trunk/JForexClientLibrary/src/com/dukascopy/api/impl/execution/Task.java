/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public abstract interface Task<T> extends Callable<T>
/*    */ {
/*    */   public abstract Type getType();
/*    */ 
/*    */   public static enum Type
/*    */   {
/* 14 */     TICK, ACCOUNT, START, STOP, BAR, MESSAGE, PARAMETER, CUSTOM;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.Task
 * JD-Core Version:    0.6.0
 */