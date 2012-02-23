/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Polygon;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class DecoratedChartObject extends ChartObject
/*     */   implements IDecoratedChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int WIDTH = 8;
/*     */   public static final int HEIGHT = 8;
/*     */   private Map<IDecoratedChartObject.Placement, IDecoratedChartObject.Decoration> decorations;
/*     */ 
/*     */   public static Shape getShape(IDecoratedChartObject.Decoration decor)
/*     */   {
/*  22 */     Polygon shape = new Polygon();
/*     */ 
/*  24 */     switch (1.$SwitchMap$com$dukascopy$api$drawings$IDecoratedChartObject$Decoration[decor.ordinal()]) {
/*     */     case 1:
/*  26 */       break;
/*     */     case 2:
/*  28 */       shape.addPoint(8, 0);
/*  29 */       shape.addPoint(0, 4);
/*  30 */       shape.addPoint(8, 8);
/*  31 */       shape.addPoint(3, 4);
/*  32 */       break;
/*     */     case 3:
/*  34 */       shape.addPoint(8, 0);
/*  35 */       shape.addPoint(0, 4);
/*  36 */       shape.addPoint(8, 8);
/*  37 */       break;
/*     */     case 4:
/*  39 */       shape.addPoint(8, 0);
/*  40 */       shape.addPoint(0, 4);
/*  41 */       shape.addPoint(8, 8);
/*  42 */       shape.addPoint(4, 4);
/*  43 */       break;
/*     */     case 5:
/*  45 */       return new Ellipse2D.Float(0.0F, 0.0F, 8.0F, 8.0F);
/*     */     }
/*     */ 
/*  48 */     return shape;
/*     */   }
/*     */ 
/*     */   protected DecoratedChartObject(String key, IChart.Type type)
/*     */   {
/*  54 */     super(key, type);
/*     */   }
/*     */ 
/*     */   protected DecoratedChartObject(DecoratedChartObject chartObject) {
/*  58 */     super(chartObject);
/*     */ 
/*  60 */     if (chartObject.decorations != null)
/*  61 */       this.decorations = new EnumMap(chartObject.decorations);
/*     */   }
/*     */ 
/*     */   protected DecoratedChartObject(String key, long time, IChart.Type type)
/*     */   {
/*  66 */     this(key, type);
/*  67 */     this.times[0] = time;
/*     */   }
/*     */ 
/*     */   public void setDecoration(IDecoratedChartObject.Placement placement, IDecoratedChartObject.Decoration decoration)
/*     */   {
/*  72 */     if (this.decorations == null) {
/*  73 */       this.decorations = new EnumMap(IDecoratedChartObject.Placement.class);
/*     */     }
/*     */ 
/*  76 */     this.decorations.put(placement, decoration);
/*     */   }
/*     */ 
/*     */   public void removeDecorations()
/*     */   {
/*  81 */     if (this.decorations != null)
/*  82 */       this.decorations.clear();
/*     */   }
/*     */ 
/*     */   public void setDecorations(Map<IDecoratedChartObject.Placement, IDecoratedChartObject.Decoration> decorations)
/*     */   {
/*  87 */     this.decorations = decorations;
/*     */   }
/*     */ 
/*     */   public Map<IDecoratedChartObject.Placement, IDecoratedChartObject.Decoration> getDecorations() {
/*  91 */     return this.decorations;
/*     */   }
/*     */ 
/*     */   protected int getLineWidth()
/*     */   {
/*  96 */     BasicStroke stroke = (BasicStroke)getStroke();
/*  97 */     return stroke == null ? 1 : (int)stroke.getLineWidth();
/*     */   }
/*     */ 
/*     */   protected Shape scaleToCurrentStroke(Shape shape) {
/* 101 */     double scaleRatio = Math.max(1, getLineWidth() / 3);
/*     */ 
/* 103 */     AffineTransform transform = new AffineTransform();
/* 104 */     transform.scale(scaleRatio, scaleRatio);
/* 105 */     return transform.createTransformedShape(shape);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.DecoratedChartObject
 * JD-Core Version:    0.6.0
 */