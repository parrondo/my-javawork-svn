/*     */ package com.dukascopy.charts.utils;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import java.awt.Color;
/*     */ import java.awt.Point;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class PathHelper
/*     */ {
/*  19 */   private final Map<IndicatorWrapper, List<Shape>[]> indicatorPaths = new HashMap();
/*  20 */   private final Map<IndicatorWrapper, Map<Color, List<Point>>> indicatorHandlesPaths = new HashMap();
/*  21 */   private final Map<IndicatorWrapper, Map<LevelInfo, Shape>> indicatorLevelsPaths = new HashMap();
/*     */ 
/*  24 */   private final float[] middlePoints = new float[6000];
/*  25 */   private final float[] highPoints = new float[6000];
/*  26 */   private final float[] lowPoints = new float[6000];
/*  27 */   private final float[] openPoints = new float[6000];
/*  28 */   private final float[] closePoints = new float[6000];
/*     */ 
/*     */   public void addIndicatorPathsFor(IndicatorWrapper indicatorWrapper, List<Shape>[] indicatorPaths) {
/*  31 */     if (indicatorPaths == null) {
/*  32 */       return;
/*     */     }
/*  34 */     this.indicatorPaths.put(indicatorWrapper, indicatorPaths);
/*     */   }
/*     */ 
/*     */   public void addIndicatorHandlePathsFor(IndicatorWrapper indicatorWrapper, Map<Color, List<Point>> indicatorHandlesPath) {
/*  38 */     if (indicatorHandlesPath == null) {
/*  39 */       return;
/*     */     }
/*  41 */     this.indicatorHandlesPaths.put(indicatorWrapper, indicatorHandlesPath);
/*     */   }
/*     */ 
/*     */   public void addIndicatorLevelsPath(IndicatorWrapper indicatorWrapper, Map<LevelInfo, Shape> indicatorLevelsPath) {
/*  45 */     if (this.indicatorLevelsPaths == null) {
/*  46 */       return;
/*     */     }
/*  48 */     this.indicatorLevelsPaths.put(indicatorWrapper, indicatorLevelsPath);
/*     */   }
/*     */ 
/*     */   public void resetIndicatorPaths() {
/*  52 */     this.indicatorPaths.clear();
/*  53 */     this.indicatorHandlesPaths.clear();
/*  54 */     this.indicatorLevelsPaths.clear();
/*     */   }
/*     */ 
/*     */   public Map<IndicatorWrapper, List<Shape>[]> getIndicatorPaths() {
/*  58 */     return this.indicatorPaths;
/*     */   }
/*     */ 
/*     */   public Map<IndicatorWrapper, Map<Color, List<Point>>> getIndicatorHandlesPaths() {
/*  62 */     return this.indicatorHandlesPaths;
/*     */   }
/*     */ 
/*     */   public Map<IndicatorWrapper, Map<LevelInfo, Shape>> getIndicatorLevelsPaths() {
/*  66 */     return this.indicatorLevelsPaths;
/*     */   }
/*     */ 
/*     */   public void resetPoints() {
/*  70 */     Arrays.fill(this.middlePoints, -1.0F);
/*  71 */     Arrays.fill(this.highPoints, -1.0F);
/*  72 */     Arrays.fill(this.lowPoints, -1.0F);
/*  73 */     Arrays.fill(this.openPoints, -1.0F);
/*  74 */     Arrays.fill(this.closePoints, -1.0F);
/*     */   }
/*     */ 
/*     */   public void savePoints(int indx, float middle, float highPriceY, float lowPriceY, float openPriceY, float closePriceY)
/*     */   {
/*  85 */     this.middlePoints[indx] = middle;
/*  86 */     this.highPoints[indx] = highPriceY;
/*  87 */     this.lowPoints[indx] = lowPriceY;
/*  88 */     this.openPoints[indx] = openPriceY;
/*  89 */     this.closePoints[indx] = closePriceY;
/*     */   }
/*     */ 
/*     */   public float[] getMiddlePoints() {
/*  93 */     return this.middlePoints;
/*     */   }
/*     */ 
/*     */   public float[] getHighs() {
/*  97 */     return this.highPoints;
/*     */   }
/*     */ 
/*     */   public float[] getLows() {
/* 101 */     return this.lowPoints;
/*     */   }
/*     */ 
/*     */   public float[] getOpenPoints() {
/* 105 */     return this.openPoints;
/*     */   }
/*     */ 
/*     */   public float[] getClosePoints() {
/* 109 */     return this.closePoints;
/*     */   }
/*     */ 
/*     */   public static GeneralPath getVisiblePart(GeneralPath path, double minX, double maxX) {
/* 113 */     PathIterator pi = path.getPathIterator(null);
/*     */ 
/* 115 */     GeneralPath pathVisible = new GeneralPath();
/*     */ 
/* 117 */     boolean visibleEmpty = true;
/* 118 */     while (!pi.isDone()) {
/* 119 */       double[] coords = new double[6];
/* 120 */       pi.currentSegment(coords);
/*     */ 
/* 122 */       if ((coords[0] > minX) && (coords[0] < maxX)) {
/* 123 */         pathVisible.append(pi, !visibleEmpty);
/* 124 */         visibleEmpty = false;
/*     */       }
/* 126 */       if (pi.isDone())
/*     */         break;
/* 128 */       pi.next();
/*     */     }
/* 130 */     return pathVisible;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.PathHelper
 * JD-Core Version:    0.6.0
 */