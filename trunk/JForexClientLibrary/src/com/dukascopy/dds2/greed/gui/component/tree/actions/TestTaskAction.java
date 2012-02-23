/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ 
/*    */ class TestTaskAction extends TreeAction
/*    */ {
/*    */   TestTaskAction(WorkspaceJTree workspaceJTree)
/*    */   {
/* 13 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 17 */     StrategyTreeNode strategyTreeNode = (StrategyTreeNode)param;
/* 18 */     StrategyNewBean strategyBean = strategyTreeNode.getStrategy();
/* 19 */     addStrategyTesterTab(strategyTreeNode.getId(), strategyBean);
/* 20 */     return null;
/*    */   }
/*    */ 
/*    */   public void addStrategyTesterTab(int strategyId, StrategyNewBean strategyBean) {
/* 24 */     JForexClientFormLayoutManager layoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*    */ 
/* 27 */     StrategyTestPanel strategyTestPanel = layoutManager.getStrategyTestPanel(strategyBean.getName(), false);
/* 28 */     if ((strategyTestPanel == null) || (strategyTestPanel.isBusy())) {
/* 29 */       strategyTestPanel = layoutManager.addStrategyTesterPanel(-1, false, true);
/*    */     }
/*    */ 
/* 32 */     strategyTestPanel.selectStrategy(strategyBean);
/* 33 */     layoutManager.selectStrategyTestPanel(strategyTestPanel);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.TestTaskAction
 * JD-Core Version:    0.6.0
 */