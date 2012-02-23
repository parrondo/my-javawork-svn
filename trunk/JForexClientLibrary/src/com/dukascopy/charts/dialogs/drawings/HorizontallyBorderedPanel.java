/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class HorizontallyBorderedPanel extends JPanel
/*     */ {
/*     */   public Insets getInsets()
/*     */   {
/* 453 */     return new Insets(0, 5, 0, 5);
/*     */   }
/*     */ 
/*     */   public HorizontallyBorderedPanel(LayoutManager layout) {
/* 457 */     super(layout);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.HorizontallyBorderedPanel
 * JD-Core Version:    0.6.0
 */