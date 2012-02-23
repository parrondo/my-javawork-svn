/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import java.awt.Component;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.File;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.text.JTextComponent;
/*     */ 
/*     */ public class EditorDialogManager
/*     */ {
/*     */   private JavaFileFilter javaFileFilter;
/*     */   JTextComponent textComponent;
/*     */   JFileChooser fileChooser;
/*     */   Component parent;
/*     */   File currDir;
/*     */ 
/*     */   public EditorDialogManager(Component parent, JTextComponent textComponent, ServiceSourceType serviceSourceType, ServiceSourceLanguage serviceSourceLanguage)
/*     */   {
/*  37 */     this.textComponent = textComponent;
/*  38 */     this.parent = parent;
/*     */ 
/*  40 */     if (serviceSourceType == ServiceSourceType.STRATEGY) {
/*  41 */       this.currDir = FilePathManager.getInstance().getStrategiesFolder();
/*     */     }
/*  43 */     else if (serviceSourceType == ServiceSourceType.INDICATOR) {
/*  44 */       this.currDir = FilePathManager.getInstance().getIndicatorsFolder();
/*     */     }
/*     */     else {
/*  47 */       throw new IllegalArgumentException("Unsupported service source type : " + serviceSourceType);
/*     */     }
/*     */ 
/*  50 */     this.javaFileFilter = new JavaFileFilter(serviceSourceLanguage);
/*     */   }
/*     */ 
/*     */   public EditorDialogManager(Component parent, JTextComponent textComponent, ServiceSourceType serviceSourceType) {
/*  54 */     this(parent, textComponent, serviceSourceType, ServiceSourceLanguage.JAVA);
/*     */   }
/*     */ 
/*     */   public void showFind() {
/*  58 */     FindReplaceDialog findReplaceDialog = FindReplaceDialog.getSharedInstance(null);
/*  59 */     findReplaceDialog.showFind(this.textComponent);
/*     */   }
/*     */ 
/*     */   public void showReplace() {
/*  63 */     FindReplaceDialog findReplaceDialog = FindReplaceDialog.getSharedInstance(null);
/*  64 */     findReplaceDialog.showReplace(this.textComponent);
/*     */   }
/*     */ 
/*     */   public int showRefreshTextArea()
/*     */   {
/*  69 */     JOptionPane jop = new JOptionPane("This file was modified outside the editor. Do you want to refresh the editor?", 2, 0, null);
/*     */ 
/*  71 */     Point location = this.textComponent.getLocationOnScreen();
/*     */ 
/*  73 */     int x = location.x;
/*  74 */     int y = location.y < 0 ? -location.y : location.y;
/*     */ 
/*  76 */     x += this.textComponent.getBounds().width / 2;
/*  77 */     y += this.textComponent.getBounds().height / 2;
/*     */ 
/*  79 */     Object[] options = { "Yes", "No" };
/*  80 */     jop.setOptions(options);
/*  81 */     jop.setInitialValue(options[0]);
/*     */ 
/*  83 */     JDialog dialog = jop.createDialog(this.parent, "File modification!");
/*  84 */     dialog.setIconImage(null);
/*  85 */     dialog.setIconImages(null);
/*     */ 
/*  88 */     dialog.setVisible(true);
/*     */ 
/*  90 */     Object selectedValue = jop.getValue();
/*  91 */     int counter = 0; for (int maxCounter = options.length; counter < maxCounter; counter++) {
/*  92 */       if (options[counter].equals(selectedValue)) {
/*  93 */         return counter;
/*     */       }
/*     */     }
/*  96 */     return 1;
/*     */   }
/*     */ 
/*     */   public ChooserSelectionWrapper showSaveAsDialog(String fileName, ServiceSourceLanguage serviceSourceLanguage, FileType fileType, String clientMode)
/*     */   {
/* 101 */     String prefFileName = "*";
/*     */ 
/* 103 */     if (fileName != null) {
/* 104 */       prefFileName = fileName;
/*     */     }
/* 106 */     this.fileChooser = new JFileChooser();
/* 107 */     this.fileChooser.setFileSelectionMode(0);
/* 108 */     this.fileChooser.setMultiSelectionEnabled(false);
/* 109 */     this.fileChooser.setDialogTitle("Save file as...");
/* 110 */     this.fileChooser.setFileView(new JfsFileView());
/* 111 */     this.fileChooser.setCurrentDirectory(this.currDir);
/* 112 */     this.fileChooser.removeChoosableFileFilter(this.javaFileFilter);
/* 113 */     this.javaFileFilter = new JavaFileFilter(serviceSourceLanguage);
/* 114 */     this.fileChooser.setFileFilter(this.javaFileFilter);
/*     */ 
/* 116 */     if (serviceSourceLanguage == ServiceSourceLanguage.MQ4)
/* 117 */       this.fileChooser.setSelectedFile(new File(prefFileName + ".mq4"));
/* 118 */     else if (serviceSourceLanguage == ServiceSourceLanguage.MQ5)
/* 119 */       this.fileChooser.setSelectedFile(new File(prefFileName + ".mq5"));
/*     */     else {
/* 121 */       this.fileChooser.setSelectedFile(new File(prefFileName + ".java"));
/*     */     }
/*     */ 
/* 124 */     ChooserSelectionWrapper wrapper = TransportFileChooser.showSaveDialog(fileType, this.fileChooser, this.parent, fileName);
/*     */ 
/* 126 */     return wrapper;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorDialogManager
 * JD-Core Version:    0.6.0
 */