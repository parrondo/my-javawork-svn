/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import java.awt.Color;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class LevelInfo
/*     */   implements Comparable<LevelInfo>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 364569280248232454L;
/*     */   private String label;
/*     */   private double value;
/*  26 */   private OutputParameterInfo.DrawingStyle drawingStyle = OutputParameterInfo.DrawingStyle.DASH_LINE;
/*  27 */   private Color color = Color.BLACK;
/*  28 */   private float opacityAlpha = 1.0F;
/*  29 */   private int lineWidth = 1;
/*     */ 
/*     */   public LevelInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LevelInfo(double value) {
/*  36 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public LevelInfo(String label, double value, OutputParameterInfo.DrawingStyle drawingStyle, Color color, int width, float alpha)
/*     */   {
/*  50 */     this.label = label;
/*  51 */     this.value = value;
/*  52 */     this.drawingStyle = drawingStyle;
/*  53 */     this.color = color;
/*  54 */     this.lineWidth = width;
/*  55 */     this.opacityAlpha = alpha;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/*  61 */     return this.label;
/*     */   }
/*     */ 
/*     */   public void setLabel(String label)
/*     */   {
/*  67 */     this.label = label;
/*     */   }
/*     */ 
/*     */   public double getValue()
/*     */   {
/*  74 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setValue(double value)
/*     */   {
/*  80 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public OutputParameterInfo.DrawingStyle getDrawingStyle()
/*     */   {
/*  86 */     return this.drawingStyle;
/*     */   }
/*     */ 
/*     */   public void setDrawingStyle(OutputParameterInfo.DrawingStyle drawingStyle)
/*     */   {
/*  92 */     this.drawingStyle = drawingStyle;
/*     */   }
/*     */ 
/*     */   public Color getColor()
/*     */   {
/*  98 */     if (this.color == null) {
/*  99 */       return Color.BLACK;
/*     */     }
/* 101 */     return this.color;
/*     */   }
/*     */ 
/*     */   public void setColor(Color color)
/*     */   {
/* 107 */     this.color = color;
/*     */   }
/*     */ 
/*     */   public float getOpacityAlpha()
/*     */   {
/* 113 */     return this.opacityAlpha;
/*     */   }
/*     */ 
/*     */   public void setOpacityAlpha(float opacityAlpha)
/*     */   {
/* 119 */     this.opacityAlpha = opacityAlpha;
/*     */   }
/*     */ 
/*     */   public int getLineWidth()
/*     */   {
/* 126 */     return this.lineWidth;
/*     */   }
/*     */ 
/*     */   public void setLineWidth(int lineWidth)
/*     */   {
/* 132 */     this.lineWidth = lineWidth;
/*     */   }
/*     */ 
/*     */   public int compareTo(LevelInfo other)
/*     */   {
/* 139 */     if ((other == null) || (getValue() < other.getValue())) {
/* 140 */       return -1;
/*     */     }
/* 142 */     return 1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.LevelInfo
 * JD-Core Version:    0.6.0
 */