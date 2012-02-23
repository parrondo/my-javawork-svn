/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*     */ 
/*     */ import java.awt.Toolkit;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.filechooser.FileView;
/*     */ 
/*     */ class JfsFileView extends FileView
/*     */ {
/*     */   Icon strategyIcon;
/*     */ 
/*     */   public JfsFileView()
/*     */   {
/* 156 */     this.strategyIcon = createImageIcon("rc/media/tree_strategy_stopped.png");
/*     */   }
/*     */ 
/*     */   public Icon getIcon(File file) {
/* 160 */     if (!file.getName().contains(".jfs")) {
/* 161 */       return super.getIcon(file);
/*     */     }
/* 163 */     return this.strategyIcon;
/*     */   }
/*     */ 
/*     */   private ImageIcon createImageIcon(String path) {
/* 167 */     int MAX_IMAGE_SIZE = 30000;
/* 168 */     int count = 0;
/* 169 */     BufferedInputStream imgStream = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
/*     */ 
/* 171 */     byte[] buf = new byte[MAX_IMAGE_SIZE];
/*     */     try {
/* 173 */       count = imgStream.read(buf);
/*     */     } catch (IOException ieo) {
/*     */     }
/*     */     finally {
/*     */       try {
/* 178 */         imgStream.close();
/*     */       }
/*     */       catch (IOException ieo)
/*     */       {
/*     */       }
/*     */     }
/* 184 */     if (count <= 0) {
/* 185 */       return null;
/*     */     }
/* 187 */     return new ImageIcon(Toolkit.getDefaultToolkit().createImage(buf));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.JfsFileView
 * JD-Core Version:    0.6.0
 */