/*    */ package com.dukascopy.dds2.greed.gui.component.tree;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.DefaultTreeSelectionModel;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ public class WorkspaceJTreeSelectionModel extends DefaultTreeSelectionModel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public WorkspaceJTreeSelectionModel()
/*    */   {
/* 18 */     setSelectionMode(4);
/*    */   }
/*    */ 
/*    */   public void setSelectionPaths(TreePath[] pPaths)
/*    */   {
/* 23 */     if (pPaths == null) return;
/* 24 */     if (pPaths.length > 1) {
/* 25 */       pPaths = filterSelectionPaths(pPaths);
/*    */     }
/* 27 */     super.setSelectionPaths(pPaths);
/*    */   }
/*    */ 
/*    */   public void addSelectionPath(TreePath path) {
/* 31 */     if (shouldBeFiltered(path)) {
/* 32 */       return;
/*    */     }
/* 34 */     super.addSelectionPath(path);
/*    */   }
/*    */ 
/*    */   public void addSelectionPaths(TreePath[] paths) {
/* 38 */     super.addSelectionPaths(filterSelectionPaths(paths));
/*    */   }
/*    */ 
/*    */   private TreePath[] filterSelectionPaths(TreePath[] pPaths) {
/* 42 */     List tmpTreePath = new LinkedList();
/* 43 */     for (TreePath pPath : pPaths) {
/* 44 */       if (!shouldBeFiltered(pPath)) {
/* 45 */         tmpTreePath.add(pPath);
/*    */       }
/*    */     }
/* 48 */     return (TreePath[])tmpTreePath.toArray(new TreePath[tmpTreePath.size()]);
/*    */   }
/*    */ 
/*    */   private boolean shouldBeFiltered(TreePath pPath) {
/* 52 */     WorkspaceTreeNode workspaceTreeNode = (WorkspaceTreeNode)pPath.getLastPathComponent();
/* 53 */     return (!(workspaceTreeNode instanceof IndicatorTreeNode)) && (!(workspaceTreeNode instanceof DrawingTreeNode));
/*    */   }
/*    */ 
/*    */   public TreePath[] getSelectionPaths() {
/* 57 */     TreePath[] selectionPaths = super.getSelectionPaths();
/* 58 */     if (selectionPaths == null) {
/* 59 */       return selectionPaths;
/*    */     }
/* 61 */     TreePath[] filteredPaths = selectionPaths;
/* 62 */     if (selectionPaths.length > 1) {
/* 63 */       filteredPaths = filterSelectionPaths(selectionPaths);
/*    */     }
/* 65 */     return filteredPaths;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTreeSelectionModel
 * JD-Core Version:    0.6.0
 */