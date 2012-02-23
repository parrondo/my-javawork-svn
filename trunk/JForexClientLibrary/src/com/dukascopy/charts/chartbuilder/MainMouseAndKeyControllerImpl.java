/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.charts.main.interfaces.IChartController;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ class MainMouseAndKeyControllerImpl
/*     */   implements MainMouseAndKeyController
/*     */ {
/*     */   final ChartState chartState;
/*     */   final IMainOperationManager mainOperationManager;
/*     */   final IChartController chartController;
/*  14 */   int draggedXFrom = -1;
/*  15 */   int draggedYFrom = -1;
/*  16 */   boolean isNewDragging = false;
/*     */ 
/*     */   public MainMouseAndKeyControllerImpl(ChartState chartState, IMainOperationManager mainOperationManager, IChartController chartController)
/*     */   {
/*  23 */     this.chartState = chartState;
/*  24 */     this.mainOperationManager = mainOperationManager;
/*  25 */     this.chartController = chartController;
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e) {
/*  29 */     if (e.isConsumed()) {
/*  30 */       return;
/*     */     }
/*  32 */     if (SwingUtilities.isMiddleMouseButton(e)) {
/*  33 */       this.chartController.setMouseCursorVisible(!this.chartState.isMouseCrossCursorVisible());
/*  34 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*  40 */     this.draggedXFrom = e.getX();
/*  41 */     this.draggedYFrom = e.getY();
/*  42 */     this.isNewDragging = true;
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e)
/*     */   {
/*  47 */     this.draggedXFrom = e.getX();
/*  48 */     this.draggedYFrom = e.getY();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/*  52 */     this.isNewDragging = true;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/*  56 */     if (e.isConsumed()) {
/*  57 */       return;
/*     */     }
/*  59 */     if (SwingUtilities.isRightMouseButton(e)) {
/*  60 */       return;
/*     */     }
/*     */ 
/*  63 */     this.mainOperationManager.moveTimeFrame(this.draggedXFrom, e.getX(), this.draggedYFrom, e.getY(), this.isNewDragging);
/*  64 */     this.draggedXFrom = e.getX();
/*  65 */     this.draggedYFrom = e.getY();
/*  66 */     this.isNewDragging = false;
/*     */ 
/*  68 */     e.consume();
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent e) {
/*  72 */     if (e.isConsumed()) {
/*  73 */       return;
/*     */     }
/*     */ 
/*  76 */     if ((e.getWheelRotation() > 0) && ((e.getModifiers() & 0x1) != 0)) {
/*  77 */       this.mainOperationManager.moveTimeFrame(100, 0, 0, 0, false);
/*  78 */       e.consume();
/*  79 */     } else if ((e.getWheelRotation() < 0) && ((e.getModifiers() & 0x1) != 0)) {
/*  80 */       this.mainOperationManager.moveTimeFrame(0, 100, 0, 0, false);
/*  81 */       e.consume();
/*  82 */     } else if ((e.getWheelRotation() > 0) && ((e.getModifiers() & 0x2) != 0)) {
/*  83 */       this.mainOperationManager.scaleTimeFrame(10);
/*  84 */       e.consume();
/*  85 */     } else if ((e.getWheelRotation() < 0) && ((e.getModifiers() & 0x2) != 0)) {
/*  86 */       this.mainOperationManager.scaleTimeFrame(-10);
/*  87 */       e.consume();
/*  88 */     } else if (e.getWheelRotation() > 0) {
/*  89 */       this.mainOperationManager.moveTimeFrame(e.getWheelRotation());
/*  90 */       e.consume();
/*  91 */     } else if (e.getWheelRotation() < 0) {
/*  92 */       this.mainOperationManager.moveTimeFrame(e.getWheelRotation());
/*  93 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/*  98 */     if (e.isConsumed()) {
/*  99 */       return;
/*     */     }
/* 101 */     int pressedKeyCode = e.getKeyCode();
/* 102 */     if ((37 == pressedKeyCode) && ((e.getModifiers() & 0x2) != 0)) {
/* 103 */       this.mainOperationManager.scaleTimeFrame(10);
/* 104 */       e.consume();
/* 105 */     } else if ((39 == pressedKeyCode) && ((e.getModifiers() & 0x2) != 0)) {
/* 106 */       this.mainOperationManager.scaleTimeFrame(-10);
/* 107 */       e.consume();
/* 108 */     } else if (40 == pressedKeyCode) {
/* 109 */       this.mainOperationManager.scaleMainChartViewIn(e.getComponent().getParent().getHeight());
/* 110 */       e.consume();
/* 111 */     } else if (38 == pressedKeyCode) {
/* 112 */       this.mainOperationManager.scaleMainChartViewOut(e.getComponent().getParent().getHeight());
/* 113 */       e.consume();
/* 114 */     } else if ((37 == pressedKeyCode) && ((e.getModifiers() & 0x1) != 0)) {
/* 115 */       this.mainOperationManager.moveTimeFrame(-10);
/* 116 */       e.consume();
/* 117 */     } else if ((39 == pressedKeyCode) && ((e.getModifiers() & 0x1) != 0)) {
/* 118 */       this.mainOperationManager.moveTimeFrame(10);
/* 119 */       e.consume();
/* 120 */     } else if (37 == pressedKeyCode) {
/* 121 */       this.mainOperationManager.moveTimeFrame(-1);
/* 122 */       e.consume();
/* 123 */     } else if (39 == pressedKeyCode) {
/* 124 */       this.mainOperationManager.moveTimeFrame(1);
/* 125 */       e.consume();
/* 126 */     } else if (36 == pressedKeyCode) {
/* 127 */       this.mainOperationManager.shiftChartToFront();
/* 128 */       e.consume();
/* 129 */     } else if (107 == pressedKeyCode) {
/* 130 */       this.mainOperationManager.zoomIn();
/* 131 */       e.consume();
/* 132 */     } else if (109 == pressedKeyCode) {
/* 133 */       this.mainOperationManager.zoomOut();
/* 134 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MainMouseAndKeyControllerImpl
 * JD-Core Version:    0.6.0
 */