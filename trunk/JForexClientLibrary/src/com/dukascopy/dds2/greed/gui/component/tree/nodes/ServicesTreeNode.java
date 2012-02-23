/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.impl.ServiceWrapper;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Enumeration;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.MutableTreeNode;
/*    */ import javax.swing.tree.TreeNode;
/*    */ 
/*    */ public class ServicesTreeNode extends WorkspaceTreeNode
/*    */ {
/* 17 */   protected List<AbstractServiceTreeNode> servicies = new ArrayList();
/*    */ 
/*    */   ServicesTreeNode(WorkspaceRootNode parent, String name) {
/* 20 */     super(true, name);
/* 21 */     if (parent == null) throw new IllegalArgumentException("Parent of ServicesTreeNode cannot be null!");
/* 22 */     setParent(parent);
/*    */   }
/*    */ 
/*    */   public TreeNode getChildAt(int strategyIndex) {
/* 26 */     return (TreeNode)this.servicies.get(strategyIndex);
/*    */   }
/*    */ 
/*    */   public int getChildCount() {
/* 30 */     return this.servicies.size();
/*    */   }
/*    */ 
/*    */   public int getIndex(TreeNode child) {
/* 34 */     if ((child instanceof AbstractServiceTreeNode)) {
/* 35 */       return this.servicies.indexOf(child);
/*    */     }
/* 37 */     return -1;
/*    */   }
/*    */ 
/*    */   public Enumeration<AbstractServiceTreeNode> children() {
/* 41 */     return Collections.enumeration(this.servicies);
/*    */   }
/*    */ 
/*    */   public boolean isLeaf() {
/* 45 */     return this.servicies.isEmpty();
/*    */   }
/*    */ 
/*    */   public void insert(MutableTreeNode child, int index) {
/* 49 */     this.servicies.add(index, (AbstractServiceTreeNode)child);
/*    */   }
/*    */ 
/*    */   public void remove(int index) {
/* 53 */     this.servicies.remove(index);
/*    */   }
/*    */ 
/*    */   public void removeAll(List<WorkspaceTreeNode> strategies) {
/* 57 */     this.servicies.removeAll(strategies);
/*    */   }
/*    */ 
/*    */   public WorkspaceTreeNode getServiceBySourceFile(File sourceFile) {
/* 61 */     for (WorkspaceTreeNode service : this.servicies) {
/* 62 */       if (!(service instanceof AbstractServiceTreeNode)) {
/*    */         continue;
/*    */       }
/* 65 */       ServiceWrapper wrapper = ((AbstractServiceTreeNode)service).getServiceWrapper();
/* 66 */       File curServiceSourceFile = wrapper.getSourceFile();
/* 67 */       if ((curServiceSourceFile != null) && (curServiceSourceFile.equals(sourceFile))) {
/* 68 */         return service;
/*    */       }
/*    */     }
/* 71 */     return null;
/*    */   }
/*    */ 
/*    */   public WorkspaceTreeNode getServiceByPanelId(int panelId) {
/* 75 */     for (AbstractServiceTreeNode service : this.servicies) {
/* 76 */       if (service.getId() == panelId) {
/* 77 */         return service;
/*    */       }
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 85 */     return LocalizationManager.getText(super.getName());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.ServicesTreeNode
 * JD-Core Version:    0.6.0
 */