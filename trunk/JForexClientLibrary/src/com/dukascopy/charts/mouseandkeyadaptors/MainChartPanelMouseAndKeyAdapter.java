/*     */ package com.dukascopy.charts.mouseandkeyadaptors;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.MainDrawingsMouseAndKeyController;
/*     */ import com.dukascopy.charts.chartbuilder.MainMouseAndKeyController;
/*     */ import com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsMouseController;
/*     */ import com.dukascopy.charts.orders.OrdersMouseController;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ 
/*     */ class MainChartPanelMouseAndKeyAdapter extends ChartPanelMouseAndKeyAdapter
/*     */ {
/*     */   final MainMouseAndKeyController mainMouseAndKeyController;
/*     */   final MetaDrawingsMouseController metaDrawingsMouseController;
/*     */   final MainDrawingsMouseAndKeyController mainDrawingsMouseAndKeyController;
/*     */   final OrdersMouseController ordersMouseController;
/*     */ 
/*     */   public MainChartPanelMouseAndKeyAdapter(ChartState chartState, GuiRefresher guiRefresher, MainMouseAndKeyController mainMouseAndKeyController, MetaDrawingsMouseController metaDrawingsMouseController, MainDrawingsMouseAndKeyController mainDrawingsMouseAndKeyController, OrdersMouseController ordersMouseController)
/*     */   {
/*  27 */     super(guiRefresher, chartState);
/*  28 */     this.mouseOverPane = true;
/*  29 */     this.mainMouseAndKeyController = mainMouseAndKeyController;
/*  30 */     this.metaDrawingsMouseController = metaDrawingsMouseController;
/*  31 */     this.mainDrawingsMouseAndKeyController = mainDrawingsMouseAndKeyController;
/*  32 */     this.ordersMouseController = ordersMouseController;
/*     */   }
/*     */ 
/*     */   protected int getWindowId() {
/*  36 */     return -1;
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e) {
/*  40 */     super.mouseEntered(e);
/*  41 */     this.mainDrawingsMouseAndKeyController.mouseEntered(e);
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/*  45 */     super.mouseExited(e);
/*  46 */     this.mainDrawingsMouseAndKeyController.mouseExited(e);
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e) {
/*  50 */     e.getComponent().requestFocus();
/*  51 */     this.mainMouseAndKeyController.mouseClicked(e);
/*  52 */     byte drawingCursorValue = this.mainDrawingsMouseAndKeyController.mouseClicked(e);
/*  53 */     byte orderCursorValue = this.ordersMouseController.mouseClicked(e);
/*  54 */     if ((drawingCursorValue == 0) || (orderCursorValue == 0))
/*  55 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(12));
/*  56 */     else if ((drawingCursorValue == 1) || (orderCursorValue == 1))
/*  57 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(1));
/*     */     else {
/*  59 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(0));
/*     */     }
/*  61 */     if (e.isConsumed())
/*  62 */       e.getComponent().getParent().repaint();
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*  67 */     e.getComponent().requestFocus();
/*  68 */     this.mainDrawingsMouseAndKeyController.mousePressed(e);
/*  69 */     this.ordersMouseController.mousePressed(e);
/*  70 */     this.metaDrawingsMouseController.mousePressed(e);
/*  71 */     this.mainMouseAndKeyController.mousePressed(e);
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/*  75 */     e.getComponent().requestFocus();
/*  76 */     this.mainDrawingsMouseAndKeyController.mouseReleased(e);
/*  77 */     this.ordersMouseController.mouseReleased(e);
/*  78 */     this.metaDrawingsMouseController.mouseReleased(e);
/*  79 */     this.mainMouseAndKeyController.mouseReleased(e);
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e) {
/*  83 */     super.mouseMoved(e);
/*  84 */     byte isSomeDrawingHighlighted = this.mainDrawingsMouseAndKeyController.mouseMoved(e);
/*  85 */     byte isSomeOrderHighlighted = this.ordersMouseController.mouseMoved(e);
/*  86 */     if ((isSomeDrawingHighlighted == 0) || (isSomeOrderHighlighted == 0))
/*  87 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(12));
/*  88 */     else if ((isSomeDrawingHighlighted == 1) || (isSomeOrderHighlighted == 1))
/*  89 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(1));
/*     */     else {
/*  91 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(0));
/*     */     }
/*  93 */     if (!e.isConsumed())
/*     */     {
/*  95 */       e.getComponent().getParent().getParent().getParent().repaint();
/*     */     }
/*  97 */     this.mainMouseAndKeyController.mouseMoved(e);
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/* 101 */     if (!this.mouseOverPane) {
/* 102 */       return;
/*     */     }
/* 104 */     super.mouseDragged(e);
/* 105 */     this.metaDrawingsMouseController.mouseDragged(e);
/* 106 */     this.mainDrawingsMouseAndKeyController.mouseDragged(e);
/* 107 */     this.ordersMouseController.mouseDragged(e);
/* 108 */     this.mainMouseAndKeyController.mouseDragged(e);
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent e) {
/* 112 */     this.mainDrawingsMouseAndKeyController.mouseWheelMoved(e);
/* 113 */     this.ordersMouseController.mouseWheelMoved(e);
/* 114 */     this.mainMouseAndKeyController.mouseWheelMoved(e);
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/* 118 */     this.metaDrawingsMouseController.keyPressed(e);
/* 119 */     this.mainDrawingsMouseAndKeyController.keyPressed(e);
/* 120 */     this.ordersMouseController.keyPressed(e);
/* 121 */     this.mainMouseAndKeyController.keyPressed(e);
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/* 127 */     this.mainDrawingsMouseAndKeyController.focusLost(e);
/* 128 */     this.ordersMouseController.focusLost(e);
/* 129 */     this.metaDrawingsMouseController.focusLost(e);
/* 130 */     this.mainMouseAndKeyController.focusLost(e);
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e) {
/* 134 */     this.mainDrawingsMouseAndKeyController.focusGained(e);
/* 135 */     this.ordersMouseController.focusGained(e);
/* 136 */     this.metaDrawingsMouseController.focusGained(e);
/* 137 */     this.mainMouseAndKeyController.focusGained(e);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.MainChartPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */