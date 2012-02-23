/*     */ package com.dukascopy.charts.mouseandkeyadaptors;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ class DivisionPanelMouseAndKeyAdapter extends ChartsMouseAndKeyAdapter
/*     */ {
/*     */   final JComponent chartViewContainer;
/*     */   final JComponent mainChartView;
/*  20 */   int draggedYFrom = 0;
/*  21 */   boolean stopDragging = false;
/*  22 */   Cursor originalMouseCursor = Cursor.getPredefinedCursor(0);
/*     */ 
/*     */   public DivisionPanelMouseAndKeyAdapter(JComponent chartViewContainer, JComponent mainChartView)
/*     */   {
/*  16 */     this.chartViewContainer = chartViewContainer;
/*  17 */     this.mainChartView = mainChartView;
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*  25 */     e.getComponent().setCursor(Cursor.getPredefinedCursor(8));
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/*  29 */     if (this.stopDragging)
/*  30 */       e.getComponent().setCursor(this.originalMouseCursor);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*  35 */     this.draggedYFrom = e.getYOnScreen();
/*  36 */     this.stopDragging = false;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/*  40 */     if (this.stopDragging) {
/*  41 */       return;
/*     */     }
/*  43 */     int diff = this.draggedYFrom - e.getYOnScreen();
/*  44 */     this.draggedYFrom = e.getYOnScreen();
/*  45 */     Component divisionPanel = e.getComponent();
/*  46 */     int count = this.chartViewContainer.getComponentCount();
/*  47 */     for (int i = 0; i < count; i++) {
/*  48 */       Component component = this.chartViewContainer.getComponent(i);
/*  49 */       if (component != divisionPanel) {
/*     */         continue;
/*     */       }
/*  52 */       applyNewSizes(e, diff, i);
/*     */     }
/*  54 */     this.chartViewContainer.validate();
/*     */   }
/*     */ 
/*     */   private void applyNewSizes(MouseEvent e, int diff, int i)
/*     */   {
/*  59 */     Component upperComponent = this.chartViewContainer.getComponent(i - 1);
/*  60 */     Component bottomComponent = this.chartViewContainer.getComponent(i + 1);
/*  61 */     Dimension upperCurSize = upperComponent.getSize();
/*  62 */     Dimension bottomCurSize = bottomComponent.getSize();
/*     */ 
/*  64 */     if ("MainChartView".equalsIgnoreCase(upperComponent.getName())) {
/*  65 */       if (upperCurSize.getHeight() <= 100.0D) {
/*  66 */         upperComponent.setSize((int)upperCurSize.getWidth(), 100);
/*  67 */         if (diff > 0) {
/*  68 */           diff = 0;
/*  69 */           this.stopDragging = true;
/*  70 */           e.getComponent().setCursor(this.originalMouseCursor);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*  75 */       if ((bottomCurSize.getHeight() <= 50.0D) && (diff < 0)) {
/*  76 */         diff = 0;
/*     */       }
/*     */ 
/*  79 */       int newHeight = (int)upperCurSize.getHeight() - diff;
/*  80 */       if (newHeight < 50) {
/*  81 */         diff += newHeight - 50;
/*  82 */         if (diff < 0) diff = 0;
/*  83 */         newHeight = 50;
/*  84 */         this.stopDragging = true;
/*     */       }
/*  86 */       else if ((upperCurSize.getHeight() < 50.0D) && (diff >= 0)) {
/*  87 */         newHeight = 50;
/*  88 */         diff = 0;
/*  89 */         this.stopDragging = true;
/*     */       }
/*  91 */       upperComponent.setSize(new Dimension((int)upperCurSize.getWidth(), newHeight));
/*     */     }
/*     */ 
/*  95 */     int newHeight = (int)(bottomCurSize.getHeight() + diff);
/*  96 */     if (((bottomCurSize.getHeight() <= 50.0D) && (diff <= 0)) || (newHeight < 50)) {
/*  97 */       newHeight = 50;
/*  98 */       this.stopDragging = true;
/*     */     }
/* 100 */     bottomComponent.setSize(new Dimension((int)bottomCurSize.getWidth(), newHeight));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.DivisionPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */