/*    */ package com.dukascopy.dds2.greed.gui.component.tree.listeners;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrencyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.CustIndTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.DrawingTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import java.awt.event.KeyAdapter;
/*    */ import java.awt.event.KeyEvent;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ public class WorkspaceJTreeKeyListener extends KeyAdapter
/*    */ {
/*    */   TreeActionFactory treeActionFactory;
/*    */   IWorkspaceHelper workspaceHelper;
/*    */ 
/*    */   public WorkspaceJTreeKeyListener(TreeActionFactory treeActionFactory, IWorkspaceHelper workspaceHelper)
/*    */   {
/* 25 */     this.treeActionFactory = treeActionFactory;
/* 26 */     this.workspaceHelper = workspaceHelper;
/*    */   }
/*    */ 
/*    */   public void keyReleased(KeyEvent keyEvent) {
/* 30 */     WorkspaceJTree workspaceJTree = (WorkspaceJTree)keyEvent.getSource();
/*    */ 
/* 32 */     int key = keyEvent.getKeyCode();
/* 33 */     TreePath selectionPath = workspaceJTree.getSelectionPath();
/* 34 */     if ((key == 127) && (selectionPath != null))
/* 35 */       performDeletion(workspaceJTree, selectionPath);
/*    */     else {
/* 37 */       this.workspaceHelper.selectTabForSelectedNode(workspaceJTree);
/*    */     }
/*    */ 
/* 40 */     workspaceJTree.requestFocus();
/*    */   }
/*    */ 
/*    */   void performDeletion(WorkspaceJTree workspaceJTree, TreePath selectionPath) {
/* 44 */     Object lastPathComponent = selectionPath.getLastPathComponent();
/*    */ 
/* 46 */     if (((lastPathComponent instanceof ChartTreeNode)) || ((lastPathComponent instanceof StrategyTreeNode)) || ((lastPathComponent instanceof CustIndTreeNode)) || ((lastPathComponent instanceof CurrencyTreeNode)) || ((lastPathComponent instanceof IndicatorTreeNode)) || ((lastPathComponent instanceof DrawingTreeNode)))
/*    */     {
/* 53 */       this.treeActionFactory.createAction(TreeActionType.DELETE, workspaceJTree).execute(lastPathComponent);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.listeners.WorkspaceJTreeKeyListener
 * JD-Core Version:    0.6.0
 */