/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ 
/*    */ abstract class ExpandTreeAction extends TreeAction
/*    */ {
/*    */   protected ExpandTreeAction(WorkspaceJTree workspaceJTree)
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
/*    */   protected abstract boolean shouldBeExpanded(WorkspaceJTree paramWorkspaceJTree, int paramInt);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ExpandTreeAction
 * JD-Core Version:    0.6.0
 */