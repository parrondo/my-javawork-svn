/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ class CollapseChartsAction extends CollapseTreeAction
/*    */ {
/*    */   CollapseChartsAction(WorkspaceJTree workspaceJTree)
/*    */   {
/*  8 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected boolean shouldBeCollapsed(WorkspaceJTree workspaceJTree, int row) {
/* 12 */     return workspaceJTree.getPathForRow(row).getPathComponent(1) instanceof ChartsNode;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.CollapseChartsAction
 * JD-Core Version:    0.6.0
 */