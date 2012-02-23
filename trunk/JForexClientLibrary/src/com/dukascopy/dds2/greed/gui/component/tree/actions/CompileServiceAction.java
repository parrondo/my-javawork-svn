/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.ServiceWrapper;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.CompileAndRunAction;
/*    */ import com.dukascopy.dds2.greed.actions.CompileStrategyAction;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import java.io.File;
/*    */ 
/*    */ class CompileServiceAction extends TreeAction
/*    */ {
/*    */   IChartTabsAndFramesController chartTabsAndFramesController;
/*    */ 
/*    */   CompileServiceAction(WorkspaceJTree workspaceJTree, IChartTabsAndFramesController chartTabsAndFramesController)
/*    */   {
/* 22 */     super(workspaceJTree);
/* 23 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 28 */     WorkspaceTreeNode workspaceTreeNode = (WorkspaceTreeNode)param;
/* 29 */     if (!(workspaceTreeNode instanceof AbstractServiceTreeNode)) {
/* 30 */       return null;
/*    */     }
/*    */ 
/* 33 */     AbstractServiceTreeNode abstractServiceTreeNode = (AbstractServiceTreeNode)workspaceTreeNode;
/* 34 */     if (!save(abstractServiceTreeNode)) {
/* 35 */       return null;
/*    */     }
/* 37 */     File sourceFile = abstractServiceTreeNode.getServiceWrapper().getSourceFile();
/* 38 */     executeCompilation(sourceFile, abstractServiceTreeNode);
/*    */ 
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   private void executeCompilation(File sourceFile, AbstractServiceTreeNode selectedNode) {
/* 44 */     ServiceWrapper serviceWrapper = selectedNode.getServiceWrapper();
/* 45 */     serviceWrapper.setSourceFile(sourceFile);
/*    */ 
/* 47 */     if ((selectedNode instanceof StrategyTreeNode)) {
/* 48 */       CompileStrategyAction compileAction = new CompileStrategyAction(this, ((StrategyTreeNode)selectedNode).getStrategy());
/* 49 */       GreedContext.publishEvent(compileAction);
/* 50 */     } else if ((selectedNode instanceof CustIndTreeNode)) {
/* 51 */       CompileAndRunAction compileAction = new CompileAndRunAction(this, serviceWrapper, selectedNode, false, null);
/* 52 */       GreedContext.publishEvent(compileAction);
/*    */     }
/*    */   }
/*    */ 
/*    */   private boolean save(AbstractServiceTreeNode abstractServiceTreeNode) {
/* 57 */     ServiceWrapper serviceWrapper = abstractServiceTreeNode.getServiceWrapper();
/* 58 */     ServiceSourceEditorPanel serviceSourceEditorPanel = this.chartTabsAndFramesController.getEditorPanel(serviceWrapper);
/* 59 */     return (serviceSourceEditorPanel == null) || (serviceSourceEditorPanel.save());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.CompileServiceAction
 * JD-Core Version:    0.6.0
 */