/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor;
/*    */ 
/*    */ public class RemoteStrategyRunResponseAction extends AbstractRemoteStrategyAction
/*    */ {
/*    */   private static final long serialVersionUID = 479276962546449021L;
/*    */   private StrategyProcessDescriptor descriptor;
/*    */ 
/*    */   public RemoteStrategyRunResponseAction(Object source, StrategyProcessDescriptor descriptor)
/*    */   {
/* 29 */     super(source);
/* 30 */     this.descriptor = descriptor;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 38 */     StrategiesContentPane strategiesContentPane = getStrategiesContentPane();
/* 39 */     if (strategiesContentPane != null)
/* 40 */       strategiesContentPane.onRemoteStrategyRunResponse(this.descriptor);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.RemoteStrategyRunResponseAction
 * JD-Core Version:    0.6.0
 */