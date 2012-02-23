/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public class StrategyOptimizerEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private IStrategy strategy;
/*    */ 
/*    */   public StrategyOptimizerEvent(StrategyOptimizerRunner source, IStrategy strategy)
/*    */   {
/* 22 */     super(source);
/* 23 */     this.strategy = strategy;
/*    */   }
/*    */ 
/*    */   public StrategyOptimizerRunner getSource()
/*    */   {
/* 30 */     return (StrategyOptimizerRunner)super.getSource();
/*    */   }
/*    */ 
/*    */   public IStrategy getStrategy()
/*    */   {
/* 38 */     return this.strategy;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.StrategyOptimizerEvent
 * JD-Core Version:    0.6.0
 */