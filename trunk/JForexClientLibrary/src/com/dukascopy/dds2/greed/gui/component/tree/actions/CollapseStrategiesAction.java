/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ class CollapseStrategiesAction extends CollapseTreeAction
/*    */ {
/*    */   CollapseStrategiesAction(WorkspaceJTree workspaceJTree)
/*    */   {
/* 10 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected boolean shouldBeCollapsed(WorkspaceJTree workspaceJTree, int row) {
/* 14 */     TreePath path = workspaceJTree.getPathForRow(row);
/*    */ 
/* 16 */     if (path == null) {
/* 17 */       return false;
/*    */     }
/* 19 */     int size = path.getPathCount();
/*    */ 
/* 21 */     for (int i = 0; i < size; i++) {
/* 22 */       if ((path.getPathComponent(i) instanceof StrategiesNode)) {
/* 23 */         return true;
/*    */       }
/*    */     }
/*    */ 
/* 27 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.CollapseStrategiesAction
 * JD-Core Version:    0.6.0
 */