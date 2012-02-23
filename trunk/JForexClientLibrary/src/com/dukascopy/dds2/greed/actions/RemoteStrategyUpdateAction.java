/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyStateMessage;
/*    */ 
/*    */ public class RemoteStrategyUpdateAction extends AbstractRemoteStrategyAction
/*    */ {
/*    */   private static final long serialVersionUID = 305298545316751651L;
/*    */   private StrategyStateMessage stateMessage;
/*    */ 
/*    */   public RemoteStrategyUpdateAction(Object source, StrategyStateMessage stateMessage)
/*    */   {
/* 27 */     super(source);
/* 28 */     this.stateMessage = stateMessage;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 36 */     StrategiesContentPane strategiesContentPane = getStrategiesContentPane();
/* 37 */     if (strategiesContentPane != null)
/* 38 */       strategiesContentPane.onRemoteStrategyUpdateMessage(this.stateMessage);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.RemoteStrategyUpdateAction
 * JD-Core Version:    0.6.0
 */