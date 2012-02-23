/*    */ package com.dukascopy.dds2.greed.gui.component.tree.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ abstract class TreeAction
/*    */   implements ITreeAction
/*    */ {
/*  8 */   private static final Logger LOGGER = LoggerFactory.getLogger(TreeAction.class);
/*    */   protected WorkspaceJTree workspaceJTree;
/*    */ 
/*    */   protected TreeAction(WorkspaceJTree workspaceJTree)
/*    */   {
/* 13 */     this.workspaceJTree = workspaceJTree;
/*    */   }
/*    */ 
/*    */   public final Object execute(Object param)
/*    */   {
/*    */     try
/*    */     {
/* 20 */       return executeInternal(param);
/*    */     } catch (Throwable thr) {
/* 22 */       LOGGER.warn(thr.getMessage(), thr);
/*    */     }
/* 24 */     return null;
/*    */   }
/*    */ 
/*    */   protected abstract Object executeInternal(Object paramObject);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.actions.TreeAction
 * JD-Core Version:    0.6.0
 */