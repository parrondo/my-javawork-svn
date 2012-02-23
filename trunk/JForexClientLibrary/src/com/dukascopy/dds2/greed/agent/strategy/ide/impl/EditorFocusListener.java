/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*    */ 
/*    */ import java.awt.event.FocusEvent;
/*    */ import java.awt.event.FocusListener;
/*    */ import java.io.File;
/*    */ import javax.swing.text.JTextComponent;
/*    */ 
/*    */ public class EditorFocusListener
/*    */   implements FocusListener
/*    */ {
/*    */   JTextComponent textComponent;
/*    */   EditorFileHandler editorFileHandler;
/*    */   EditorDocumentListener editorDocumentListener;
/*    */   EditorDialogManager editorDialogManager;
/* 16 */   long prevLastModifiedTimeStamp = -1L;
/*    */ 
/*    */   public EditorFocusListener(JTextComponent textComponent, EditorFileHandler editorFileHandler, EditorDialogManager editorDialogManager)
/*    */   {
/* 20 */     this.textComponent = textComponent;
/* 21 */     this.editorFileHandler = editorFileHandler;
/* 22 */     this.editorDialogManager = editorDialogManager;
/*    */   }
/*    */ 
/*    */   public void focusLost(FocusEvent e) {
/* 26 */     if ((this.editorFileHandler != null) && (this.editorFileHandler.getFile() != null))
/* 27 */       this.prevLastModifiedTimeStamp = this.editorFileHandler.getFile().lastModified();
/*    */   }
/*    */ 
/*    */   public void focusGained(FocusEvent e)
/*    */   {
/* 32 */     if (this.editorFileHandler.getFile() == null) {
/* 33 */       return;
/*    */     }
/* 35 */     long curLastModifiedTimeStamp = this.editorFileHandler.getFile().lastModified();
/*    */ 
/* 37 */     if (this.prevLastModifiedTimeStamp == -1L) {
/* 38 */       this.prevLastModifiedTimeStamp = curLastModifiedTimeStamp;
/* 39 */       return;
/*    */     }
/*    */ 
/* 42 */     if (this.prevLastModifiedTimeStamp >= curLastModifiedTimeStamp) {
/* 43 */       return;
/*    */     }
/*    */ 
/* 46 */     int answer = this.editorDialogManager.showRefreshTextArea();
/* 47 */     if (answer == 0) {
/* 48 */       String reloadedContent = this.editorFileHandler.reloadFile();
/* 49 */       this.textComponent.setText(reloadedContent);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 54 */     this.prevLastModifiedTimeStamp = this.editorFileHandler.getFile().lastModified();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorFocusListener
 * JD-Core Version:    0.6.0
 */