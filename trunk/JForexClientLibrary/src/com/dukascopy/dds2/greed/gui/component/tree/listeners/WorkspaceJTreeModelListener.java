/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import javax.swing.event.TreeModelEvent;
/*    */ import javax.swing.event.TreeModelListener;
/*    */ import javax.swing.tree.DefaultTreeModel;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ public class WorkspaceJTreeModelListener
/*    */   implements TreeModelListener
/*    */ {
/*    */   IChartTabsAndFramesController chartTabsAndFramesController;
/*    */ 
/*    */   public WorkspaceJTreeModelListener(IChartTabsAndFramesController chartTabsAndFramesController)
/*    */   {
/* 17 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*    */   }
/*    */ 
/*    */   public void treeNodesChanged(TreeModelEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void treeNodesInserted(TreeModelEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void treeNodesRemoved(TreeModelEvent event) {
/* 29 */     closeFrames(event);
/*    */   }
/*    */ 
/*    */   public void treeStructureChanged(TreeModelEvent event) {
/* 33 */     chageTabTitle(event);
/*    */   }
/*    */ 
/*    */   void closeFrames(TreeModelEvent event) {
/* 37 */     Object[] removedNodes = event.getChildren();
/* 38 */     for (Object node : removedNodes)
/* 39 */       if ((node instanceof ChartTreeNode))
/* 40 */         this.chartTabsAndFramesController.closeChart(((ChartTreeNode)node).getChartPanelId());
/* 41 */       else if (node.getClass().getSuperclass() == AbstractServiceTreeNode.class)
/* 42 */         this.chartTabsAndFramesController.closeServiceEditor(((AbstractServiceTreeNode)node).getId());
/*    */   }
/*    */ 
/*    */   void chageTabTitle(TreeModelEvent event)
/*    */   {
/* 48 */     Object source = event.getSource();
/* 49 */     if (!(source instanceof DefaultTreeModel)) {
/* 50 */       return;
/*    */     }
/*    */ 
/* 53 */     Object treeNode = event.getTreePath().getLastPathComponent();
/* 54 */     if (!(treeNode instanceof ChartTreeNode)) {
/* 55 */       return;
/*    */     }
/*    */ 
/* 58 */     ChartTreeNode chartTreeNode = (ChartTreeNode)treeNode;
/*    */ 
/* 60 */     this.chartTabsAndFramesController.setTabTitle(chartTreeNode.getChartPanelId(), chartTreeNode.getInstrument() + "," + chartTreeNode.getJForexPeriod().getPeriod().toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeModelListener
 * JD-Core Version:    0.6.0
 */