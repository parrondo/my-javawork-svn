/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ 
/*    */ abstract class CustomIndicatorAction extends ServiceAction
/*    */ {
/*    */   protected CustomIndicatorAction(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory)
/*    */   {
/* 12 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */   }
/*    */ 
/*    */   protected void edit(CustIndTreeNode custIndTreeNode, boolean newCustInd) {
/* 16 */     edit(custIndTreeNode, newCustInd, ServiceSourceType.INDICATOR);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.CustomIndicatorAction
 * JD-Core Version:    0.6.0
 */