/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import javax.swing.tree.MutableTreeNode;
/*    */ import javax.swing.tree.TreeNode;
/*    */ 
/*    */ public abstract class WorkspaceTreeNode
/*    */   implements MutableTreeNode
/*    */ {
/*    */   private String name;
/*    */   private boolean allowsChildren;
/*    */   private WorkspaceTreeNode parent;
/*    */ 
/*    */   public WorkspaceTreeNode(boolean allowsChildren, String name)
/*    */   {
/* 14 */     this.allowsChildren = allowsChildren;
/* 15 */     this.name = name;
/*    */   }
/*    */ 
/*    */   protected void setName(String name) {
/* 19 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 23 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 27 */     return getName();
/*    */   }
/*    */ 
/*    */   protected void setParent(WorkspaceTreeNode parent) {
/* 31 */     this.parent = parent;
/*    */   }
/*    */ 
/*    */   public TreeNode getParent() {
/* 35 */     return this.parent;
/*    */   }
/*    */ 
/*    */   public boolean getAllowsChildren() {
/* 39 */     return this.allowsChildren;
/*    */   }
/*    */ 
/*    */   public String getToolTipText()
/*    */   {
/* 45 */     return null;
/*    */   }
/*    */ 
/*    */   public void insert(MutableTreeNode child, int index)
/*    */   {
/* 55 */     throw new UnsupportedOperationException("Inserts is not supported for this tree node");
/*    */   }
/*    */ 
/*    */   public void remove(int index) {
/* 59 */     throw new UnsupportedOperationException("remove(int) is not supported for this tree node");
/*    */   }
/*    */ 
/*    */   public void remove(MutableTreeNode node) {
/* 63 */     throw new UnsupportedOperationException("remove(MutableTreeNode) is not supported for this tree node");
/*    */   }
/*    */ 
/*    */   public void setUserObject(Object object) {
/*    */   }
/*    */ 
/*    */   public void removeFromParent() {
/* 70 */     throw new UnsupportedOperationException("removeFromParent() is not supported for this tree node");
/*    */   }
/*    */ 
/*    */   public void setParent(MutableTreeNode newParent) {
/* 74 */     throw new UnsupportedOperationException("setParent(MutableTreeNode) is not supported for this tree node");
/*    */   }
/*    */ 
/*    */   public TreeNode getChildAt(int childIndex)
/*    */   {
/* 79 */     throw new RuntimeException("This node cannot have children!");
/*    */   }
/*    */ 
/*    */   public int getChildCount() {
/* 83 */     return 0;
/*    */   }
/*    */ 
/*    */   public int getIndex(TreeNode node) {
/* 87 */     return -1;
/*    */   }
/*    */ 
/*    */   public boolean isLeaf() {
/* 91 */     return true;
/*    */   }
/*    */ 
/*    */   public Enumeration children() {
/* 95 */     throw new RuntimeException("This node cannot have children!");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode
 * JD-Core Version:    0.6.0
 */