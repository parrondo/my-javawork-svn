/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ 
/*    */ public class OpenStrategiesControlPanelAction extends TreeAction
/*    */ {
/*    */   protected OpenStrategiesControlPanelAction(WorkspaceJTree workspaceJTree)
/*    */   {
/* 11 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 17 */     JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 18 */     clientFormLayoutManager.addStrategiesPanel(false, false);
/*    */ 
/* 20 */     if ((param instanceof StrategyTreeNode)) {
/* 21 */       clientFormLayoutManager.getStrategiesPanel().selectStrategy(((StrategyTreeNode)param).getStrategy().getId().intValue());
/*    */     }
/*    */ 
/* 24 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.OpenStrategiesControlPanelAction
 * JD-Core Version:    0.6.0
 */