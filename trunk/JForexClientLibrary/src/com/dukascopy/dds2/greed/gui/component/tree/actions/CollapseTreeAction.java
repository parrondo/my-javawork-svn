/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ 
/*    */ abstract class CollapseTreeAction extends TreeAction
/*    */ {
/*    */   protected CollapseTreeAction(WorkspaceJTree workspaceJTree)
/*    */   {
/*  8 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 12 */     for (int row = 0; row < this.workspaceJTree.getRowCount(); row++) {
/* 13 */       if (shouldBeCollapsed(this.workspaceJTree, row)) {
/* 14 */         this.workspaceJTree.collapseRow(row);
/*    */       }
/*    */     }
/* 17 */     return null;
/*    */   }
/*    */ 
/*    */   protected abstract boolean shouldBeCollapsed(WorkspaceJTree paramWorkspaceJTree, int paramInt);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.CollapseTreeAction
 * JD-Core Version:    0.6.0
 */