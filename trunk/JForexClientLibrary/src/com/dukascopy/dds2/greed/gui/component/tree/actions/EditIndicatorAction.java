/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*    */ 
/*    */ class EditIndicatorAction extends TreeAction
/*    */ {
/*    */   final DDSChartsController ddsChartsController;
/*    */ 
/*    */   EditIndicatorAction(WorkspaceJTree workspaceJTree, DDSChartsController ddsChartsController)
/*    */   {
/* 13 */     super(workspaceJTree);
/* 14 */     this.ddsChartsController = ddsChartsController;
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 18 */     IndicatorTreeNode indicatorTreeNode = (IndicatorTreeNode)param;
/* 19 */     ChartTreeNode chartTreeNode = (ChartTreeNode)indicatorTreeNode.getParent();
/* 20 */     this.ddsChartsController.editIndicator(Integer.valueOf(chartTreeNode.getChartPanelId()), indicatorTreeNode.getSubPanelId(), indicatorTreeNode.getIndicator());
/* 21 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.EditIndicatorAction
 * JD-Core Version:    0.6.0
 */