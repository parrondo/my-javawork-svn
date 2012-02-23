/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import java.awt.Point;
/*     */ 
/*     */ class MouseControllerMetaDrawingsStateImpl
/*     */   implements MouseControllerMetaDrawingsState
/*     */ {
/*     */   Point firstPointZoomingToArea;
/*     */   Point secondFloatingPointZoomingToArea;
/*     */   Point secondPointZoomingToArea;
/*     */   Point firstPointMeasuringCandleLine;
/*     */   Point secondFloatingPointSecondMeasuringCandleLine;
/*     */   Point secondPointMeasuringCandleLine;
/*     */   boolean isMeasuringCandles;
/*     */   boolean isChartShiftHandlerBeeingShifted;
/*     */   boolean isZoomingToArea;
/*     */ 
/*     */   public void setIsZoomingToArea(boolean isZoomingToArea)
/*     */   {
/*  22 */     this.isZoomingToArea = isZoomingToArea;
/*  23 */     if (!isZoomingToArea) {
/*  24 */       this.firstPointZoomingToArea = null;
/*  25 */       this.secondPointZoomingToArea = null;
/*  26 */       this.secondFloatingPointZoomingToArea = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isZoomingToArea() {
/*  31 */     return this.isZoomingToArea;
/*     */   }
/*     */ 
/*     */   public Point getFirstZoomingToAreaPoint() {
/*  35 */     return this.firstPointZoomingToArea;
/*     */   }
/*     */ 
/*     */   public Point getSecondZoomingToAreaPoint() {
/*  39 */     return this.secondFloatingPointZoomingToArea;
/*     */   }
/*     */ 
/*     */   public void setFirstPointZoomingToArea(Point point) {
/*  43 */     this.firstPointZoomingToArea = point;
/*     */   }
/*     */ 
/*     */   public void setSecondPointZoomingToArea(Point point) {
/*  47 */     this.secondPointZoomingToArea = point;
/*     */   }
/*     */ 
/*     */   public void setSecondFloatingPointZoomingToArea(Point point) {
/*  51 */     this.secondFloatingPointZoomingToArea = point;
/*  52 */     this.secondFloatingPointZoomingToArea = point;
/*     */   }
/*     */ 
/*     */   public void setIsMeasuringCandles(boolean isMeasuringCandles)
/*     */   {
/*  60 */     this.isMeasuringCandles = isMeasuringCandles;
/*  61 */     if (!isMeasuringCandles) {
/*  62 */       this.firstPointMeasuringCandleLine = null;
/*  63 */       this.secondFloatingPointSecondMeasuringCandleLine = null;
/*  64 */       this.secondPointMeasuringCandleLine = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isMeasuringCandlesLine() {
/*  69 */     return this.isMeasuringCandles;
/*     */   }
/*     */ 
/*     */   public Point get1MeasuringCandlesLinePoint() {
/*  73 */     return this.firstPointMeasuringCandleLine;
/*     */   }
/*     */ 
/*     */   public Point get2MeasuringCandlesLinePoint() {
/*  77 */     return this.secondPointMeasuringCandleLine;
/*     */   }
/*     */ 
/*     */   public void set1PointMeasuringCandleLine(Point point) {
/*  81 */     this.firstPointMeasuringCandleLine = point;
/*     */   }
/*     */ 
/*     */   public void set2PointMeasuringCandleLine(Point point) {
/*  85 */     this.secondPointMeasuringCandleLine = point;
/*     */   }
/*     */ 
/*     */   public void set2FloatingPointMeasuringCandleLine(Point point) {
/*  89 */     this.secondFloatingPointSecondMeasuringCandleLine = point;
/*  90 */     this.secondPointMeasuringCandleLine = point;
/*     */   }
/*     */ 
/*     */   public boolean isChartShiftHandlerBeeingShifted()
/*     */   {
/*  97 */     return this.isChartShiftHandlerBeeingShifted;
/*     */   }
/*     */ 
/*     */   public void setChartShiftHandlerBeeingShifted(boolean isBeeingShifted) {
/* 101 */     this.isChartShiftHandlerBeeingShifted = isBeeingShifted;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsStateImpl
 * JD-Core Version:    0.6.0
 */