/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.IndicatorsNode;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ class ExpandIndicatorsAction extends ExpandTreeAction
/*    */ {
/*    */   ExpandIndicatorsAction(WorkspaceJTree workspaceJTree)
/*    */   {
/* 10 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected boolean shouldBeExpanded(WorkspaceJTree workspaceJTree, int row) {
/* 14 */     TreePath path = workspaceJTree.getPathForRow(row);
/* 15 */     if (path == null) {
/* 16 */       return false;
/*    */     }
/*    */ 
/* 19 */     for (Object pathElement : path.getPath()) {
/* 20 */       if ((pathElement instanceof IndicatorsNode)) {
/* 21 */         return true;
/*    */       }
/*    */     }
/*    */ 
/* 25 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.ExpandIndicatorsAction
 * JD-Core Version:    0.6.0
 */