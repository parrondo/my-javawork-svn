/*     */ package com.dukascopy.charts.orders.orderparts;
/*     */ 
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public abstract class ShortLine extends Line
/*     */ {
/*  11 */   private double x1 = (0.0D / 0.0D);
/*  12 */   private double y1 = (0.0D / 0.0D);
/*     */ 
/*  14 */   private double x2 = (0.0D / 0.0D);
/*  15 */   private double y2 = (0.0D / 0.0D);
/*     */ 
/*     */   public ShortLine(String orderGroupId) {
/*  18 */     super(orderGroupId);
/*     */   }
/*     */ 
/*     */   public void setCoordinates(double x1, double y1, double x2, double y2) {
/*  22 */     this.x1 = x1;
/*  23 */     this.y1 = y1;
/*  24 */     this.x2 = x2;
/*  25 */     this.y2 = y2;
/*     */   }
/*     */ 
/*     */   public void plot(Graphics g, int chartX, int chartY, int width, int height)
/*     */   {
/*  30 */     if ((Double.isNaN(this.x1)) || (Double.isNaN(this.y1)) || (Double.isNaN(this.x2)) || (Double.isNaN(this.y2))) {
/*  31 */       this.path.reset();
/*  32 */       return;
/*     */     }
/*     */ 
/*  36 */     boolean intersect = false;
/*  37 */     double[] point = GraphicHelper.getSegmentLinesIntersection(this.x1, this.y1, this.x2, this.y2, chartX, chartY, chartX + width, chartY);
/*  38 */     if (point != null) {
/*  39 */       intersect = true;
/*  40 */       if (this.y2 > this.y1) {
/*  41 */         this.x1 = point[0];
/*  42 */         this.y1 = point[1];
/*     */       } else {
/*  44 */         this.x2 = point[0];
/*  45 */         this.y2 = point[1];
/*     */       }
/*     */     }
/*     */ 
/*  49 */     point = GraphicHelper.getSegmentLinesIntersection(this.x1, this.y1, this.x2, this.y2, chartX, chartY + height, chartX + width, chartY + height);
/*  50 */     if (point != null) {
/*  51 */       intersect = true;
/*  52 */       if (this.y1 > this.y2) {
/*  53 */         this.x1 = point[0];
/*  54 */         this.y1 = point[1];
/*     */       } else {
/*  56 */         this.x2 = point[0];
/*  57 */         this.y2 = point[1];
/*     */       }
/*     */     }
/*     */ 
/*  61 */     point = GraphicHelper.getSegmentLinesIntersection(this.x1, this.y1, this.x2, this.y2, chartX, chartY, chartX, height);
/*  62 */     if (point != null) {
/*  63 */       intersect = true;
/*  64 */       if (this.x2 > this.x1) {
/*  65 */         this.x1 = point[0];
/*  66 */         this.y1 = point[1];
/*     */       } else {
/*  68 */         this.x2 = point[0];
/*  69 */         this.y2 = point[1];
/*     */       }
/*     */     }
/*     */ 
/*  73 */     point = GraphicHelper.getSegmentLinesIntersection(this.x1, this.y1, this.x2, this.y2, chartX + width, chartY, chartX + width, chartY + height);
/*  74 */     if (point != null) {
/*  75 */       intersect = true;
/*  76 */       if (this.x1 > this.x2) {
/*  77 */         this.x1 = point[0];
/*  78 */         this.y1 = point[1];
/*     */       } else {
/*  80 */         this.x2 = point[0];
/*  81 */         this.y2 = point[1];
/*     */       }
/*     */     }
/*     */ 
/*  85 */     if (!intersect)
/*     */     {
/*  88 */       if ((this.x1 < chartX) || (this.x1 > chartX + width)) {
/*  89 */         this.path.reset();
/*  90 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  94 */     this.path.reset();
/*  95 */     this.path.moveTo(this.x1, this.y1);
/*  96 */     this.path.lineTo(this.x2, this.y2);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 100 */     if ((obj instanceof ShortLine)) {
/* 101 */       ShortLine o = (ShortLine)obj;
/* 102 */       return (this.color == null ? this.color == o.color : this.color.equals(o.color)) && (Math.abs((int)(this.x1 + 0.5D) - (int)(o.x1 + 0.5D)) <= 3) && (Math.abs((int)(this.y1 + 0.5D) - (int)(o.y1 + 0.5D)) <= 3) && (Math.abs((int)(this.x2 + 0.5D) - (int)(o.x2 + 0.5D)) <= 3) && (Math.abs((int)(this.y2 + 0.5D) - (int)(o.y2 + 0.5D)) <= 3);
/*     */     }
/*     */ 
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 109 */     return this.color != null ? this.color.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   public double getX1() {
/* 113 */     return this.x1;
/*     */   }
/*     */ 
/*     */   public double getY1() {
/* 117 */     return this.y1;
/*     */   }
/*     */ 
/*     */   public double getX2() {
/* 121 */     return this.x2;
/*     */   }
/*     */ 
/*     */   public double getY2() {
/* 125 */     return this.y2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.ShortLine
 * JD-Core Version:    0.6.0
 */