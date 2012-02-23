/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.api;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.KeyStroke;
/*    */ import javax.swing.event.CaretListener;
/*    */ 
/*    */ public abstract interface Editor
/*    */ {
/*    */   public abstract void focus();
/*    */ 
/*    */   public abstract void open(File paramFile)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract boolean close();
/*    */ 
/*    */   public abstract boolean save()
/*    */     throws IOException;
/*    */ 
/*    */   public abstract boolean saveAs(File paramFile)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract boolean saveAs(Component paramComponent, String paramString, ServiceSourceLanguage paramServiceSourceLanguage)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract void find();
/*    */ 
/*    */   public abstract void replace();
/*    */ 
/*    */   public abstract boolean contentWasModified();
/*    */ 
/*    */   public abstract File getFile();
/*    */ 
/*    */   public abstract JComponent getGUIComponent();
/*    */ 
/*    */   public abstract void addFileChangeListener(FileChangeListener paramFileChangeListener);
/*    */ 
/*    */   public abstract boolean addCaretListener(CaretListener paramCaretListener);
/*    */ 
/*    */   public abstract void selectLine(int paramInt);
/*    */ 
/*    */   public abstract void setSyntaxStyle(String paramString);
/*    */ 
/*    */   public abstract void setContent(String paramString);
/*    */ 
/*    */   public abstract String getContent();
/*    */ 
/*    */   public abstract boolean isJavaSource();
/*    */ 
/*    */   public abstract boolean isMQLSource();
/*    */ 
/*    */   public abstract void setEditable(boolean paramBoolean);
/*    */ 
/*    */   public abstract void reloadEditor(File paramFile1, File paramFile2);
/*    */ 
/*    */   public abstract void organizeImports();
/*    */ 
/*    */   public static enum Action
/*    */   {
/* 16 */     REDO("control Y"), 
/* 17 */     UNDO("control Z"), 
/* 18 */     FIND("control F"), 
/* 19 */     REPLACE("control R"), 
/* 20 */     SAVE("control S"), 
/* 21 */     COMPILE("F5"), 
/* 22 */     HELP("F1");
/*    */ 
/*    */     private KeyStroke keyStroke;
/*    */ 
/* 27 */     private Action(String keyStroke) { this.keyStroke = KeyStroke.getKeyStroke(keyStroke); }
/*    */ 
/*    */     public KeyStroke getKeyStroke()
/*    */     {
/* 31 */       return this.keyStroke;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.api.Editor
 * JD-Core Version:    0.6.0
 */