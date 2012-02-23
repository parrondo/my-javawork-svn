/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.text.StyledDocument;
/*     */ 
/*     */ class NonWrappingTextPane extends JTextPane
/*     */ {
/*     */   public NonWrappingTextPane()
/*     */   {
/*     */   }
/*     */ 
/*     */   public NonWrappingTextPane(StyledDocument doc)
/*     */   {
/* 437 */     super(doc);
/*     */   }
/*     */ 
/*     */   public boolean getScrollableTracksViewportWidth()
/*     */   {
/* 443 */     Component parent = getParent();
/* 444 */     ComponentUI ui = getUI();
/* 445 */     boolean result = ui.getPreferredSize(this).width <= parent.getSize().width;
/* 446 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.NonWrappingTextPane
 * JD-Core Version:    0.6.0
 */