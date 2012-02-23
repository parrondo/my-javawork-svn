/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ 
/*    */ class CollapseWorkspaceAction extends CollapseTreeAction
/*    */ {
/*    */   CollapseWorkspaceAction(WorkspaceJTree workspaceJTree)
/*    */   {
/*  7 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected boolean shouldBeCollapsed(WorkspaceJTree workspaceJTree, int row) {
/* 11 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.CollapseWorkspaceAction
 * JD-Core Version:    0.6.0
 */