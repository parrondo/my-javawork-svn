/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.Strategies;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ 
/*    */ public class StopAllTasksAction extends TreeAction
/*    */ {
/*    */   StopAllTasksAction(WorkspaceJTree workspaceJTree)
/*    */   {
/*  9 */     super(workspaceJTree);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param) {
/* 13 */     Strategies.get().stopAll();
/* 14 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.StopAllTasksAction
 * JD-Core Version:    0.6.0
 */