/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ 
/*    */ class EditStrategyAction extends StrategyAction
/*    */ {
/*    */   EditStrategyAction(WorkspaceJTree workspaceJTree, IChartTabsAndFramesController chartTabAndFramesController, WorkspaceNodeFactory workspaceNodeFactory)
/*    */   {
/* 12 */     super(chartTabAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 16 */     WorkspaceTreeNode treeNode = (WorkspaceTreeNode)param;
/* 17 */     if ((treeNode instanceof StrategyTreeNode)) {
/* 18 */       StrategyTreeNode strategyTreeNode = (StrategyTreeNode)treeNode;
/* 19 */       edit(strategyTreeNode, strategyTreeNode.getServiceWrapper().isNewUnsaved());
/*    */     }
/* 21 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.EditStrategyAction
 * JD-Core Version:    0.6.0
 */