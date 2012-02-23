/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import javax.swing.tree.TreeNode;
/*    */ 
/*    */ class ClearChartAction extends TreeAction
/*    */ {
/*    */   final DDSChartsController ddsChartsController;
/*    */ 
/*    */   ClearChartAction(WorkspaceJTree workspaceJTree, DDSChartsController ddsChartsController)
/*    */   {
/* 17 */     super(workspaceJTree);
/* 18 */     this.ddsChartsController = ddsChartsController;
/*    */   }
/*    */ 
/*    */   public Object executeInternal(Object param) {
/* 22 */     WorkspaceTreeNode workspaceTreeNode = (WorkspaceTreeNode)param;
/*    */ 
/* 24 */     if (!(workspaceTreeNode instanceof ChartTreeNode)) {
/* 25 */       return null;
/*    */     }
/* 27 */     ChartTreeNode chartTreeNode = (ChartTreeNode)workspaceTreeNode;
/*    */ 
/* 29 */     int childCount = chartTreeNode.getChildCount();
/*    */ 
/* 31 */     for (int i = childCount - 1; i >= 0; i--) {
/* 32 */       TreeNode treeNode = chartTreeNode.getChildAt(i);
/*    */ 
/* 34 */       if ((treeNode instanceof IndicatorTreeNode)) {
/* 35 */         IndicatorTreeNode indicatorTreeNode = (IndicatorTreeNode)treeNode;
/* 36 */         this.ddsChartsController.deleteIndicator(Integer.valueOf(chartTreeNode.getChartPanelId()), indicatorTreeNode.getIndicator());
/*    */       }
/* 38 */       else if ((treeNode instanceof DrawingTreeNode)) {
/* 39 */         DrawingTreeNode drawingTreeNode = (DrawingTreeNode)treeNode;
/* 40 */         this.ddsChartsController.remove(Integer.valueOf(chartTreeNode.getChartPanelId()), drawingTreeNode.getDrawing());
/*    */       }
/*    */     }
/*    */ 
/* 44 */     this.ddsChartsController.setVerticalChartMovementEnabled(Integer.valueOf(chartTreeNode.getChartPanelId()), false);
/*    */ 
/* 46 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ClearChartAction
 * JD-Core Version:    0.6.0
 */