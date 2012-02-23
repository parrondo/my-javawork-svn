/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.FocusListener;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.JFrame;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class EditorRegistryImpl
/*    */   implements EditorRegistry
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(EditorRegistryImpl.class);
/*    */ 
/* 26 */   private Map<Integer, Editor> openFiles = new HashMap();
/*    */ 
/*    */   public EditorRegistryImpl(JFrame parentForDialogs)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Editor openEditor(int editorId, File file, EditorStatusBar statusBar, ActionListener actionListener, ServiceSourceType serviceSourceType, String clientMode, String syntaxStyle) {
/* 33 */     Editor editor = getEditor(editorId);
/* 34 */     if (editor != null) {
/* 35 */       editor = (Editor)this.openFiles.get(file.getAbsolutePath());
/* 36 */       editor.focus();
/*    */     } else {
/* 38 */       editor = new EditorImpl(this, statusBar, actionListener, serviceSourceType, clientMode, syntaxStyle);
/*    */       try
/*    */       {
/* 41 */         editor.open(file);
/* 42 */         this.openFiles.put(Integer.valueOf(editorId), editor);
/*    */       } catch (IOException e) {
/* 44 */         LOGGER.error(e.getMessage(), e);
/*    */       }
/*    */     }
/* 47 */     return editor;
/*    */   }
/*    */ 
/*    */   public Editor openEditor(int editorId, File file, EditorStatusBar statusBar, ActionListener actionListener, FocusListener focusListener, ServiceSourceType serviceSourceType, String clientMode, String syntaxStyle) {
/* 51 */     Editor editor = getEditor(editorId);
/* 52 */     if (editor != null) {
/* 53 */       editor = (Editor)this.openFiles.get(file.getAbsolutePath());
/*    */     } else {
/* 55 */       editor = new EditorImpl(this, statusBar, actionListener, serviceSourceType, focusListener, clientMode, syntaxStyle);
/*    */       try
/*    */       {
/* 58 */         editor.open(file);
/* 59 */         this.openFiles.put(Integer.valueOf(editorId), editor);
/*    */       } catch (IOException e) {
/* 61 */         LOGGER.error(e.getMessage(), e);
/*    */       }
/*    */     }
/*    */ 
/* 65 */     return editor;
/*    */   }
/*    */   public Editor getEditor(int editorId) {
/* 68 */     return (Editor)this.openFiles.get(Integer.valueOf(editorId));
/*    */   }
/*    */ 
/*    */   public void removeReference(int editorId) {
/* 72 */     this.openFiles.remove(Integer.valueOf(editorId));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorRegistryImpl
 * JD-Core Version:    0.6.0
 */