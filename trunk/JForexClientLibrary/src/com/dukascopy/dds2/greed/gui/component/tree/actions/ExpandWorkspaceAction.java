/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ 
/*    */ class ExpandWorkspaceAction extends ExpandTreeAction
/*    */ {
/*    */   ExpandWorkspaceAction(WorkspaceJTree workspaceJTree)
/*    */   {
/*  8 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 12 */     for (int row = 0; row < this.workspaceJTree.getRowCount(); row++) {
/* 13 */       if (shouldBeExpanded(this.workspaceJTree, row)) {
/* 14 */         this.workspaceJTree.expandRow(row);
/*    */       }
/*    */     }
/* 17 */     return null;
/*    */   }
/*    */ 
/*    */   protected boolean shouldBeExpanded(WorkspaceJTree workspaceJTree, int row) {
/* 21 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ExpandWorkspaceAction
 * JD-Core Version:    0.6.0
 */