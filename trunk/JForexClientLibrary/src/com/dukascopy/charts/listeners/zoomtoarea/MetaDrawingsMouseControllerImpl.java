/*     */ package com.dukascopy.charts.listeners.zoomtoarea;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*     */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Point;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ 
/*     */ public class MetaDrawingsMouseControllerImpl
/*     */   implements MetaDrawingsMouseController
/*     */ {
/*     */   ChartState chartState;
/*     */   MouseControllerMetaDrawingsState mouseControllerMetaDrawingState;
/*     */   MetaDrawingsActionListener metaDrawingsActionListener;
/*     */   IMainOperationManager mainOperationManager;
/*     */ 
/*     */   public MetaDrawingsMouseControllerImpl(IMainOperationManager mainOperationManager, ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, MetaDrawingsActionListener metaDrawingsActionListener)
/*     */   {
/*  26 */     this.mainOperationManager = mainOperationManager;
/*  27 */     this.chartState = chartState;
/*  28 */     this.mouseControllerMetaDrawingState = mouseControllerMetaDrawingsState;
/*  29 */     this.metaDrawingsActionListener = metaDrawingsActionListener;
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e) {
/*  33 */     if (e.isConsumed()) {
/*  34 */       return;
/*     */     }
/*  36 */     if ((!e.isPopupTrigger()) && (this.chartState.isMouseCrossCursorVisible())) {
/*  37 */       this.mouseControllerMetaDrawingState.setIsMeasuringCandles(true);
/*  38 */       this.mouseControllerMetaDrawingState.set1PointMeasuringCandleLine(e.getPoint());
/*  39 */       this.metaDrawingsActionListener.measuringCandlesStarted();
/*  40 */       e.consume();
/*  41 */     } else if (this.mouseControllerMetaDrawingState.isZoomingToArea()) {
/*  42 */       this.mouseControllerMetaDrawingState.setFirstPointZoomingToArea(e.getPoint());
/*  43 */       this.metaDrawingsActionListener.zoomingToAreaStarted();
/*  44 */       e.consume();
/*  45 */     } else if (mousePressedOnChartShiftHandler(e)) {
/*  46 */       this.mouseControllerMetaDrawingState.setChartShiftHandlerBeeingShifted(true);
/*  47 */       this.chartState.setChartShiftActive(true);
/*  48 */       e.getComponent().repaint();
/*  49 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/*  54 */     completeAction(e);
/*     */   }
/*     */ 
/*     */   private void completeAction(MouseEvent e)
/*     */   {
/*  62 */     if (e.isConsumed()) {
/*  63 */       return;
/*     */     }
/*  65 */     Point lastMousePosition = e.getPoint();
/*  66 */     if (this.mouseControllerMetaDrawingState.isMeasuringCandlesLine()) {
/*  67 */       this.mouseControllerMetaDrawingState.set2PointMeasuringCandleLine(lastMousePosition);
/*  68 */     } else if (this.mouseControllerMetaDrawingState.isZoomingToArea()) {
/*  69 */       this.mouseControllerMetaDrawingState.setSecondPointZoomingToArea(lastMousePosition);
/*  70 */       Point firstPoint = this.mouseControllerMetaDrawingState.getFirstZoomingToAreaPoint();
/*  71 */       Point secondPoint = this.mouseControllerMetaDrawingState.getSecondZoomingToAreaPoint();
/*  72 */       if ((firstPoint == null) || (secondPoint == null)) {
/*  73 */         return;
/*     */       }
/*  75 */       this.mainOperationManager.zoomToArea(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
/*  76 */     } else if (this.mouseControllerMetaDrawingState.isChartShiftHandlerBeeingShifted()) {
/*  77 */       this.mainOperationManager.shiftChartToFront();
/*     */     }
/*     */ 
/*  80 */     boolean wasSomeActionFinished = finishAction(e);
/*  81 */     if (wasSomeActionFinished)
/*  82 */       e.consume();
/*     */   }
/*     */ 
/*     */   private boolean finishAction(ComponentEvent e)
/*     */   {
/*  95 */     boolean wasSomeActionFinished = false;
/*     */ 
/*  97 */     if (this.mouseControllerMetaDrawingState.isMeasuringCandlesLine()) {
/*  98 */       this.mouseControllerMetaDrawingState.setIsMeasuringCandles(false);
/*  99 */       e.getComponent().getParent().getParent().repaint();
/* 100 */       this.metaDrawingsActionListener.measuringCandlesEnded();
/* 101 */       wasSomeActionFinished = true;
/*     */     }
/* 103 */     else if (this.mouseControllerMetaDrawingState.isZoomingToArea()) {
/* 104 */       this.mouseControllerMetaDrawingState.setIsZoomingToArea(false);
/* 105 */       this.metaDrawingsActionListener.zoomingToAreaEnded();
/* 106 */       wasSomeActionFinished = true;
/*     */     }
/* 108 */     else if (this.mouseControllerMetaDrawingState.isChartShiftHandlerBeeingShifted()) {
/* 109 */       this.mouseControllerMetaDrawingState.setChartShiftHandlerBeeingShifted(false);
/* 110 */       wasSomeActionFinished = true;
/*     */     }
/*     */ 
/* 113 */     return wasSomeActionFinished;
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/* 117 */     if (e.isConsumed()) {
/* 118 */       return;
/*     */     }
/* 120 */     if (this.mouseControllerMetaDrawingState.isMeasuringCandlesLine()) {
/* 121 */       this.mouseControllerMetaDrawingState.set2FloatingPointMeasuringCandleLine(e.getPoint());
/* 122 */       e.getComponent().getParent().getParent().repaint();
/* 123 */       e.consume();
/* 124 */     } else if (this.mouseControllerMetaDrawingState.isZoomingToArea()) {
/* 125 */       this.mouseControllerMetaDrawingState.setSecondFloatingPointZoomingToArea(e.getPoint());
/* 126 */       e.getComponent().repaint();
/* 127 */       e.consume();
/* 128 */     } else if (this.mouseControllerMetaDrawingState.isChartShiftHandlerBeeingShifted()) {
/* 129 */       int newChartShiftInPx = calculateNewChartShiftHandlerOffset(e);
/* 130 */       this.chartState.setChartShiftHandlerCoordinate(newChartShiftInPx);
/* 131 */       this.mainOperationManager.shiftChartToFront();
/* 132 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/* 137 */     if (e.isConsumed()) {
/* 138 */       return;
/*     */     }
/* 140 */     if ((this.mouseControllerMetaDrawingState.isZoomingToArea()) && 
/* 141 */       (27 == e.getKeyCode())) {
/* 142 */       this.mouseControllerMetaDrawingState.setIsZoomingToArea(false);
/* 143 */       this.metaDrawingsActionListener.zoomingToAreaEnded();
/* 144 */       e.getComponent().repaint();
/* 145 */       e.consume();
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean mousePressedOnChartShiftHandler(MouseEvent e)
/*     */   {
/* 151 */     Polygon triangleHandle = new Polygon();
/* 152 */     int x = e.getComponent().getWidth() - this.chartState.getChartShiftHandlerCoordinate();
/* 153 */     triangleHandle.addPoint(x - 10, 1);
/* 154 */     triangleHandle.addPoint(x + 10, 1);
/* 155 */     triangleHandle.addPoint(x, 11);
/*     */ 
/* 157 */     return triangleHandle.contains(e.getPoint());
/*     */   }
/*     */ 
/*     */   int calculateNewChartShiftHandlerOffset(MouseEvent e) {
/* 161 */     int width = e.getComponent().getWidth();
/* 162 */     int newChartShiftInPx = width - e.getX();
/* 163 */     if (newChartShiftInPx > width / 2) {
/* 164 */       newChartShiftInPx = width / 2;
/*     */     }
/* 166 */     return newChartShiftInPx;
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/* 176 */     finishAction(e);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsMouseControllerImpl
 * JD-Core Version:    0.6.0
 */