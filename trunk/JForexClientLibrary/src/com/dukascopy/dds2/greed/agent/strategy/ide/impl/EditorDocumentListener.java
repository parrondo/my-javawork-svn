/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*    */ 
/*    */ import javax.swing.event.DocumentEvent;
/*    */ import javax.swing.event.DocumentListener;
/*    */ 
/*    */ public class EditorDocumentListener
/*    */   implements DocumentListener
/*    */ {
/*  8 */   boolean fileChanged = false;
/*    */ 
/*    */   public void changedUpdate(DocumentEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void insertUpdate(DocumentEvent e) {
/* 15 */     this.fileChanged = true;
/*    */   }
/*    */ 
/*    */   public void removeUpdate(DocumentEvent e) {
/* 19 */     this.fileChanged = true;
/*    */   }
/*    */ 
/*    */   public boolean fileIsInModifiedState() {
/* 23 */     return this.fileChanged;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 27 */     this.fileChanged = false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorDocumentListener
 * JD-Core Version:    0.6.0
 */