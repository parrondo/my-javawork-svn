/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class ExecutionControlEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ExecutionControlEvent(ExecutionControl source)
/*    */   {
/* 13 */     super(source);
/*    */   }
/*    */ 
/*    */   public ExecutionControl getExecutionControl() {
/* 17 */     return (ExecutionControl)getSource();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlEvent
 * JD-Core Version:    0.6.0
 */