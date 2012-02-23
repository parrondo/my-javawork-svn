/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.FileChooserDialogHelper;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceNodeFactory;
/*    */ 
/*    */ class AddCustomIndicatorAction extends CustomIndicatorAction
/*    */ {
/*    */   WorkspaceNodeFactory workspaceNodeFactory;
/*    */ 
/*    */   AddCustomIndicatorAction(WorkspaceJTree workspaceJTree, WorkspaceNodeFactory workspaceNodeFactory, IChartTabsAndFramesController chartTabsAndFramesController)
/*    */   {
/* 20 */     super(chartTabsAndFramesController, workspaceJTree, workspaceNodeFactory);
/* 21 */     this.workspaceNodeFactory = workspaceNodeFactory;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 25 */     new Thread()
/*    */     {
/*    */       public void run() {
/* 28 */         AddCustomIndicatorAction.this.createNew();
/*    */       }
/*    */     }
/* 25 */     .start();
/*    */ 
/* 31 */     return null;
/*    */   }
/*    */ 
/*    */   private void createNew() {
/* 35 */     CustIndicatorWrapper indicatorWrapper = new CustIndicatorWrapper();
/* 36 */     indicatorWrapper.setSourceFile(FileChooserDialogHelper.findOrCreateFileFor("ind"));
/* 37 */     indicatorWrapper.setNewUnsaved(true);
/*    */ 
/* 39 */     CustIndTreeNode custIndTreeNode = this.workspaceNodeFactory.createServiceTreeNodeFrom(indicatorWrapper);
/*    */ 
/* 41 */     this.chartTabsAndFramesController.addServiceSourceEditor(custIndTreeNode.getId(), indicatorWrapper.getName(), indicatorWrapper.getSourceFile(), ServiceSourceType.INDICATOR, false);
/*    */ 
/* 47 */     this.workspaceJTree.addCustIndTreeNode(custIndTreeNode);
/* 48 */     this.workspaceJTree.selectNode(custIndTreeNode);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.AddCustomIndicatorAction
 * JD-Core Version:    0.6.0
 */