/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Enumeration;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.TreeNode;
/*    */ 
/*    */ public class WorkspaceRootNode extends WorkspaceTreeNode
/*    */ {
/*    */   private final ChartsNode chartsNode;
/*    */   private final StrategiesNode strategiesTreeNode;
/*    */   private final IndicatorsNode indicatorsTreeNode;
/* 20 */   private final List<WorkspaceTreeNode> childList = new ArrayList();
/*    */ 
/*    */   WorkspaceRootNode() {
/* 23 */     super(true, "tree.node.charts");
/*    */ 
/* 25 */     this.strategiesTreeNode = new StrategiesNode(this);
/* 26 */     this.indicatorsTreeNode = new IndicatorsNode(this);
/* 27 */     this.chartsNode = new ChartsNode(this);
/*    */ 
/* 29 */     this.childList.add(this.strategiesTreeNode);
/* 30 */     this.childList.add(this.indicatorsTreeNode);
/* 31 */     this.childList.add(this.chartsNode);
/*    */   }
/*    */ 
/*    */   public void remove(int childIndex) {
/* 35 */     if (childIndex < 0)
/* 36 */       throw new ArrayIndexOutOfBoundsException("childIndex < 0");
/* 37 */     if (childIndex == 0)
/* 38 */       throw new UnsupportedOperationException("chartsNode cannot be removed");
/* 39 */     if (childIndex == 1) {
/* 40 */       if ((getChildAt(1) instanceof ServicesTreeNode))
/* 41 */         this.childList.remove(childIndex);
/*    */     }
/*    */     else
/* 44 */       throw new ArrayIndexOutOfBoundsException("childIndex is bigger than child count");
/*    */   }
/*    */ 
/*    */   public Enumeration<WorkspaceTreeNode> children()
/*    */   {
/* 49 */     return Collections.enumeration(this.childList);
/*    */   }
/*    */ 
/*    */   public WorkspaceTreeNode getChildAt(int childIndex) {
/* 53 */     return (WorkspaceTreeNode)this.childList.get(childIndex);
/*    */   }
/*    */ 
/*    */   public int getChildCount() {
/* 57 */     return this.childList.size();
/*    */   }
/*    */ 
/*    */   public int getIndex(TreeNode node) {
/* 61 */     return this.childList.indexOf(node);
/*    */   }
/*    */ 
/*    */   public boolean isLeaf() {
/* 65 */     return false;
/*    */   }
/*    */ 
/*    */   public StrategiesNode getStrategiesTreeNode() {
/* 69 */     return this.strategiesTreeNode;
/*    */   }
/*    */ 
/*    */   public IndicatorsNode getIndicatorsTreeNode() {
/* 73 */     return this.indicatorsTreeNode;
/*    */   }
/*    */ 
/*    */   public ChartsNode getChartsNode() {
/* 77 */     return this.chartsNode;
/*    */   }
/*    */ 
/*    */   public WorkspaceTreeNode getServiceByPanelId(int panelId)
/*    */   {
/* 82 */     WorkspaceTreeNode serviceTreeNode = getStrategiesTreeNode().getServiceByPanelId(panelId);
/* 83 */     if (serviceTreeNode == null) {
/* 84 */       serviceTreeNode = getIndicatorsTreeNode().getServiceByPanelId(panelId);
/*    */     }
/* 86 */     return serviceTreeNode;
/*    */   }
/*    */ 
/*    */   public WorkspaceTreeNode getServiceBySourceFile(File sourceFile) {
/* 90 */     WorkspaceTreeNode serviceTreeNode = getStrategiesTreeNode().getServiceBySourceFile(sourceFile);
/* 91 */     if (serviceTreeNode == null) {
/* 92 */       serviceTreeNode = getIndicatorsTreeNode().getServiceBySourceFile(sourceFile);
/*    */     }
/* 94 */     return serviceTreeNode;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 99 */     return LocalizationManager.getText(super.getName());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode
 * JD-Core Version:    0.6.0
 */