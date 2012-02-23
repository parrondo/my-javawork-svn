/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Enumeration;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.MutableTreeNode;
/*    */ import javax.swing.tree.TreeNode;
/*    */ 
/*    */ public class ChartsNode extends WorkspaceTreeNode
/*    */ {
/* 18 */   public final List<ChartTreeNode> chartNodes = new ArrayList();
/*    */ 
/*    */   ChartsNode(WorkspaceRootNode parent) {
/* 21 */     super(true, "tree.node.charts");
/* 22 */     if (parent == null) throw new IllegalArgumentException("Parent of ChartsNode cannot be null!");
/* 23 */     setParent(parent);
/*    */   }
/*    */ 
/*    */   public TreeNode getChildAt(int chartIndex)
/*    */   {
/* 28 */     return (TreeNode)this.chartNodes.get(chartIndex);
/*    */   }
/*    */ 
/*    */   public int getChildCount() {
/* 32 */     return this.chartNodes.size();
/*    */   }
/*    */ 
/*    */   public int getIndex(TreeNode child) {
/* 36 */     if (!(child instanceof ChartTreeNode)) return -1;
/* 37 */     ChartTreeNode instrumentNode = (ChartTreeNode)child;
/* 38 */     return this.chartNodes.indexOf(instrumentNode);
/*    */   }
/*    */ 
/*    */   public Enumeration<ChartTreeNode> children() {
/* 42 */     return Collections.enumeration(this.chartNodes);
/*    */   }
/*    */ 
/*    */   public boolean isLeaf() {
/* 46 */     return this.chartNodes.isEmpty();
/*    */   }
/*    */ 
/*    */   public void insert(MutableTreeNode child, int index) {
/* 50 */     this.chartNodes.add(index, (ChartTreeNode)child);
/*    */   }
/*    */ 
/*    */   public void remove(int index) {
/* 54 */     this.chartNodes.remove(index);
/*    */   }
/*    */ 
/*    */   public void removeAll(Collection<ChartTreeNode> childs) {
/* 58 */     this.chartNodes.removeAll(childs);
/*    */   }
/*    */ 
/*    */   public int addInstrumentChartTreeNode(ChartTreeNode child) {
/* 62 */     this.chartNodes.add(child);
/* 63 */     return this.chartNodes.size() - 1;
/*    */   }
/*    */ 
/*    */   public ChartTreeNode getChartTreeNodeByChartPanelId(Integer chartPanelId) {
/* 67 */     if (chartPanelId == null) {
/* 68 */       return null;
/*    */     }
/* 70 */     for (ChartTreeNode instrumentChartTreeNode : this.chartNodes) {
/* 71 */       if (chartPanelId.equals(Integer.valueOf(instrumentChartTreeNode.getChartPanelId()))) {
/* 72 */         return instrumentChartTreeNode;
/*    */       }
/*    */     }
/* 75 */     return null;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 80 */     return LocalizationManager.getText(super.getName());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode
 * JD-Core Version:    0.6.0
 */