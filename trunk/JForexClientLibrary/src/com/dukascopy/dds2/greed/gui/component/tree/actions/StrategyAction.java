/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ 
/*    */ abstract class StrategyAction extends ServiceAction
/*    */ {
/*    */   protected StrategyAction(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory)
/*    */   {
/* 11 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */   }
/*    */ 
/*    */   protected void edit(StrategyTreeNode strategyTreeNode, boolean newStrategy) {
/* 15 */     edit(strategyTreeNode, newStrategy, ServiceSourceType.STRATEGY);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.StrategyAction
 * JD-Core Version:    0.6.0
 */