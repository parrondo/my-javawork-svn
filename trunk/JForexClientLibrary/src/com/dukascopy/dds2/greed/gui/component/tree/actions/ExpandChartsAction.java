/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartsNode;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ class ExpandChartsAction extends ExpandTreeAction
/*    */ {
/*    */   ExpandChartsAction(WorkspaceJTree workspaceJTree)
/*    */   {
/* 10 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected boolean shouldBeExpanded(WorkspaceJTree workspaceJTree, int row)
/*    */   {
/* 15 */     TreePath path = workspaceJTree.getPathForRow(row);
/* 16 */     if (path == null) {
/* 17 */       return false;
/*    */     }
/*    */ 
/* 20 */     for (Object pathElement : path.getPath()) {
/* 21 */       if ((pathElement instanceof ChartsNode)) {
/* 22 */         return true;
/*    */       }
/*    */     }
/*    */ 
/* 26 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ExpandChartsAction
 * JD-Core Version:    0.6.0
 */