/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ 
/*    */ class EditCustomIndicatorAction extends CustomIndicatorAction
/*    */ {
/*    */   EditCustomIndicatorAction(WorkspaceJTree workspaceJTree, IChartTabsAndFramesController chartTabAndFramesController, WorkspaceNodeFactory workspaceNodeFactory)
/*    */   {
/* 12 */     super(chartTabAndFramesController, workspaceJTree, workspaceNodeFactory);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 16 */     WorkspaceTreeNode treeNode = (WorkspaceTreeNode)param;
/* 17 */     if ((treeNode instanceof CustIndTreeNode)) {
/* 18 */       CustIndTreeNode custIndTreeNode = (CustIndTreeNode)treeNode;
/* 19 */       edit(custIndTreeNode, custIndTreeNode.getServiceWrapper().isNewUnsaved());
/*    */     }
/* 21 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.EditCustomIndicatorAction
 * JD-Core Version:    0.6.0
 */