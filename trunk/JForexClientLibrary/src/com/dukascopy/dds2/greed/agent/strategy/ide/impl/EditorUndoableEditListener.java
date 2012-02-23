/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*    */ 
/*    */ import javax.swing.event.UndoableEditEvent;
/*    */ import javax.swing.event.UndoableEditListener;
/*    */ import javax.swing.undo.UndoManager;
/*    */ 
/*    */ public class EditorUndoableEditListener
/*    */   implements UndoableEditListener
/*    */ {
/*    */   UndoManager undoManager;
/*    */ 
/*    */   public EditorUndoableEditListener(UndoManager undoManager)
/*    */   {
/* 12 */     this.undoManager = undoManager;
/*    */   }
/*    */ 
/*    */   public void undoableEditHappened(UndoableEditEvent e) {
/* 16 */     this.undoManager.addEdit(e.getEdit());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorUndoableEditListener
 * JD-Core Version:    0.6.0
 */