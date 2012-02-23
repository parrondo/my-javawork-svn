/*     */ package com.dukascopy.calculator.graph;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Transformation
/*     */ {
/*     */   protected View view;
/*     */   protected double xScale;
/*     */   protected double yScale;
/*     */   protected double xOrigin;
/*     */   protected double yOrigin;
/*     */   protected double xMajorUnit;
/*     */   protected double xMinorUnit;
/*     */   protected double yMajorUnit;
/*     */   protected double yMinorUnit;
/*     */ 
/*     */   public Transformation(View view)
/*     */   {
/*  13 */     this.view = view;
/*  14 */     setScale(10.0D);
/*  15 */     setOrigin(0.0D, 0.0D);
/*  16 */     setXMajorUnit(5.0D);
/*  17 */     setXMinorUnit(1.0D);
/*  18 */     setYMajorUnit(5.0D);
/*  19 */     setYMinorUnit(1.0D);
/*     */   }
/*     */ 
/*     */   public void setScale(double scale)
/*     */   {
/*  28 */     if (scale <= 0.0D) {
/*  29 */       System.err.println("Ignoring attempt to set invalid scale in Transformation.");
/*     */ 
/*  31 */       return;
/*     */     }
/*  33 */     this.xScale = (this.yScale = scale);
/*     */   }
/*     */ 
/*     */   public void setScaleY(double scale)
/*     */   {
/*  43 */     if (scale <= 0.0D) {
/*  44 */       System.err.println("Ignoring attempt to set invalid scale in Transformation.");
/*     */ 
/*  46 */       return;
/*     */     }
/*  48 */     this.yScale = scale;
/*     */   }
/*     */ 
/*     */   public void setScaleX(double scale)
/*     */   {
/*  59 */     if (scale <= 0.0D) {
/*  60 */       System.err.println("Ignoring attempt to set invalid scale in Transformation.");
/*     */ 
/*  62 */       return;
/*     */     }
/*  64 */     this.xScale = scale;
/*     */   }
/*     */ 
/*     */   public void setOrigin(double x, double y)
/*     */   {
/*  74 */     this.xOrigin = x;
/*  75 */     this.yOrigin = y;
/*     */   }
/*     */ 
/*     */   public void setOriginX(double x)
/*     */   {
/*  83 */     this.xOrigin = x;
/*     */   }
/*     */ 
/*     */   public void setOriginY(double y)
/*     */   {
/*  91 */     this.yOrigin = y;
/*     */   }
/*     */ 
/*     */   double getOriginX()
/*     */   {
/*  98 */     return this.xOrigin;
/*     */   }
/*     */ 
/*     */   double getOriginY()
/*     */   {
/* 105 */     return this.yOrigin;
/*     */   }
/*     */ 
/*     */   public double toModelX(double x)
/*     */   {
/* 114 */     return this.xOrigin + (x - this.view.getWidth() / 2.0D) / this.xScale;
/*     */   }
/*     */ 
/*     */   public double toModelY(double y)
/*     */   {
/* 122 */     return this.yOrigin + (this.view.getHeight() / 2.0D - y) / this.yScale;
/*     */   }
/*     */ 
/*     */   public double toViewX(double x)
/*     */   {
/* 130 */     return (x - this.xOrigin) * this.xScale + this.view.getWidth() / 2.0D;
/*     */   }
/*     */ 
/*     */   public double toViewY(double y)
/*     */   {
/* 138 */     return this.view.getHeight() / 2.0D + (this.yOrigin - y) * this.yScale;
/*     */   }
/*     */ 
/*     */   double getXMajorUnit()
/*     */   {
/* 145 */     return this.xMajorUnit;
/*     */   }
/*     */ 
/*     */   void setXMajorUnit(double unit)
/*     */   {
/* 152 */     this.xMajorUnit = unit;
/*     */   }
/*     */ 
/*     */   double getXMinorUnit()
/*     */   {
/* 159 */     return this.xMinorUnit;
/*     */   }
/*     */ 
/*     */   void setXMinorUnit(double unit)
/*     */   {
/* 166 */     this.xMinorUnit = unit;
/*     */   }
/*     */ 
/*     */   double getYMajorUnit()
/*     */   {
/* 173 */     return this.yMajorUnit;
/*     */   }
/*     */ 
/*     */   void setYMajorUnit(double unit)
/*     */   {
/* 180 */     this.yMajorUnit = unit;
/*     */   }
/*     */ 
/*     */   double getYMinorUnit()
/*     */   {
/* 187 */     return this.yMinorUnit;
/*     */   }
/*     */ 
/*     */   void setYMinorUnit(double unit)
/*     */   {
/* 194 */     this.yMinorUnit = unit;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.Transformation
 * JD-Core Version:    0.6.0
 */