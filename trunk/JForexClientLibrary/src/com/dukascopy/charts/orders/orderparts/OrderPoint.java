/*     */ package com.dukascopy.charts.orders.orderparts;
/*     */ 
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import java.awt.Color;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public abstract class OrderPoint extends AbstractOrderPart
/*     */ {
/*     */   private long time;
/*     */   private final double price;
/*     */   private Color arrowColor;
/*     */   private Color triangleColor;
/*     */   private Color pointColor;
/*     */   private boolean isBuy;
/*     */   private String text;
/*     */   private Shape pointShape;
/*     */   private GeneralPath trianglePath;
/*     */   private TextSegment textSegment;
/*  26 */   private GeneralPath arrowPath = new GeneralPath();
/*     */ 
/*     */   public OrderPoint(String orderGroupId, long time, double price) {
/*  29 */     super(orderGroupId);
/*     */ 
/*  31 */     this.time = time;
/*  32 */     this.price = price;
/*     */   }
/*     */ 
/*     */   public long getTime() {
/*  36 */     return this.time;
/*     */   }
/*     */ 
/*     */   public void setTime(long time) {
/*  40 */     this.time = time;
/*     */   }
/*     */ 
/*     */   public double getPrice() {
/*  44 */     return this.price;
/*     */   }
/*     */ 
/*     */   public Color getArrowColor() {
/*  48 */     return this.arrowColor;
/*     */   }
/*     */ 
/*     */   public void setArrowColor(Color arrowColor) {
/*  52 */     this.arrowColor = arrowColor;
/*     */   }
/*     */ 
/*     */   public Color getTriangleColor() {
/*  56 */     return this.triangleColor;
/*     */   }
/*     */ 
/*     */   public void setTriangleColor(Color triangleColor) {
/*  60 */     this.triangleColor = triangleColor;
/*     */   }
/*     */ 
/*     */   public Color getPointColor() {
/*  64 */     return this.pointColor;
/*     */   }
/*     */ 
/*     */   public void setPointColor(Color pointColor) {
/*  68 */     this.pointColor = pointColor;
/*     */   }
/*     */ 
/*     */   public boolean isBuy() {
/*  72 */     return this.isBuy;
/*     */   }
/*     */ 
/*     */   public void setBuy(boolean isBuy) {
/*  76 */     this.isBuy = isBuy;
/*     */   }
/*     */ 
/*     */   public String getText() {
/*  80 */     return this.text;
/*     */   }
/*     */ 
/*     */   public void setText(String text) {
/*  84 */     this.text = text;
/*     */   }
/*     */ 
/*     */   public Shape getPointShape() {
/*  88 */     return this.pointShape;
/*     */   }
/*     */ 
/*     */   public void setPointShape(Shape pointShape) {
/*  92 */     this.pointShape = pointShape;
/*     */   }
/*     */ 
/*     */   public GeneralPath getTrianglePath() {
/*  96 */     return this.trianglePath;
/*     */   }
/*     */ 
/*     */   public void setTrianglePath(GeneralPath trianglePath) {
/* 100 */     this.trianglePath = trianglePath;
/*     */   }
/*     */ 
/*     */   public TextSegment getTextSegment() {
/* 104 */     return this.textSegment;
/*     */   }
/*     */ 
/*     */   public void setTextSegment(TextSegment textSegment) {
/* 108 */     this.textSegment = textSegment;
/*     */   }
/*     */ 
/*     */   public GeneralPath getArrowPath() {
/* 112 */     return this.arrowPath;
/*     */   }
/*     */ 
/*     */   public void setArrowPath(GeneralPath arrowPath) {
/* 116 */     this.arrowPath = arrowPath;
/*     */   }
/*     */ 
/*     */   public boolean hitPoint(int x, int y) {
/* 120 */     this.hitSquareRect.setLocation(x - 5, y - 5);
/*     */ 
/* 123 */     if ((this.trianglePath != null) && (GraphicHelper.intersects(this.trianglePath, this.hitSquareRect))) {
/* 124 */       return true;
/*     */     }
/* 126 */     if ((this.arrowPath != null) && (this.arrowPath.contains(x, y))) {
/* 127 */       return true;
/*     */     }
/*     */ 
/* 131 */     if ((this.pointShape != null) && (this.pointShape.intersects(this.hitSquareRect))) {
/* 132 */       return true;
/*     */     }
/*     */ 
/* 137 */     return (this.textSegment != null) && (this.textSegment.containsHitPoint(x, y));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.OrderPoint
 * JD-Core Version:    0.6.0
 */