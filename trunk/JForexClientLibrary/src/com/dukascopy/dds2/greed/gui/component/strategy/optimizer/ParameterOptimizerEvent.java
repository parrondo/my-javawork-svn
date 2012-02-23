/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class ParameterOptimizerEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ParameterOptimizerEvent(ParameterOptimizer source)
/*    */   {
/* 22 */     super(source);
/*    */   }
/*    */ 
/*    */   public ParameterOptimizer getSource()
/*    */   {
/* 27 */     return (ParameterOptimizer)super.getSource();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParameterOptimizerEvent
 * JD-Core Version:    0.6.0
 */