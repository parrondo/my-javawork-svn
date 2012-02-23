/*     */ package com.dukascopy.charts.orders.orderparts;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ 
/*     */ public final class TextSegment
/*     */ {
/*  16 */   protected Color color = ThemeManager.getTheme().getColor(ITheme.ChartElement.DRAWING);
/*     */   protected Rectangle boundingRectangle;
/*     */   private String string;
/*     */   private float x;
/*     */   private float y;
/*     */   private int width;
/*     */   private int height;
/*  28 */   private Color bgColor = null;
/*     */   private Font font;
/*     */   private int rotateToDegree;
/*     */ 
/*     */   public TextSegment()
/*     */   {
/*     */   }
/*     */ 
/*     */   public TextSegment(String string, float x, float y, Color color, Color bgColor)
/*     */   {
/*  40 */     this.string = string;
/*  41 */     this.x = x;
/*  42 */     this.y = y;
/*  43 */     this.color = color;
/*  44 */     this.bgColor = bgColor;
/*     */   }
/*     */ 
/*     */   public TextSegment(String string, float x, float y, Color color, Color bgColor, Font font) {
/*  48 */     this(string, x, y, color, bgColor);
/*  49 */     this.font = font;
/*     */   }
/*     */ 
/*     */   public TextSegment(String string, float x, float y, Color color, Color bgColor, Font font, Rectangle boundingRectangle) {
/*  53 */     this(string, x, y, color, bgColor, font);
/*  54 */     this.boundingRectangle = boundingRectangle;
/*     */   }
/*     */ 
/*     */   public void setColor(Color color) {
/*  58 */     this.color = color;
/*     */   }
/*     */ 
/*     */   public Color getColor() {
/*  62 */     return this.color;
/*     */   }
/*     */ 
/*     */   public void setBoundingRectangle(Rectangle boundingRectangle) {
/*  66 */     this.boundingRectangle = boundingRectangle;
/*     */   }
/*     */ 
/*     */   public void setRotation(int degrees) {
/*  70 */     this.rotateToDegree = degrees;
/*     */   }
/*     */ 
/*     */   public void setString(String string) {
/*  74 */     this.string = string;
/*     */   }
/*     */ 
/*     */   public void render(Graphics g) {
/*  78 */     Graphics2D g2 = (Graphics2D)g;
/*  79 */     Font old = g2.getFont();
/*     */     FontMetrics fm;
/*     */     FontMetrics fm;
/*  81 */     if (null != this.font) {
/*  82 */       g2.setFont(this.font);
/*  83 */       fm = g2.getFontMetrics(this.font);
/*     */     } else {
/*  85 */       fm = g2.getFontMetrics();
/*     */     }
/*  87 */     this.width = (fm.stringWidth(this.string) + 1);
/*  88 */     this.height = (fm.getHeight() + 2);
/*     */ 
/*  90 */     Rectangle rect = new Rectangle(Math.round(this.x), Math.round(this.y) - (this.height - 4), this.width + 1, fm.getHeight() - 1);
/*     */ 
/*  96 */     if ((this.boundingRectangle != null) && (!this.boundingRectangle.intersects(rect))) {
/*  97 */       return;
/*     */     }
/*     */ 
/* 100 */     if (this.bgColor != null) {
/* 101 */       g2.setColor(this.bgColor);
/* 102 */       g2.fillRect(Math.round(this.x), Math.round(this.y) - (this.height - 4), this.width + 1, fm.getHeight() - 1);
/*     */     }
/* 104 */     g2.setColor(this.color);
/*     */ 
/* 106 */     if (this.rotateToDegree != 0)
/* 107 */       rotate(g2);
/*     */     else {
/* 109 */       g2.drawString(this.string, this.x, this.y);
/*     */     }
/* 111 */     g2.setFont(old);
/*     */   }
/*     */ 
/*     */   private void rotate(Graphics2D graphics2D) {
/* 115 */     AffineTransform originalTransform = graphics2D.getTransform();
/* 116 */     AffineTransform rotationTransform = new AffineTransform(originalTransform);
/* 117 */     rotationTransform.rotate(Math.toRadians(this.rotateToDegree));
/* 118 */     graphics2D.setTransform(rotationTransform);
/* 119 */     graphics2D.drawString(this.string, this.x, this.y);
/* 120 */     graphics2D.setTransform(originalTransform);
/*     */   }
/*     */ 
/*     */   public Font getFont() {
/* 124 */     return this.font;
/*     */   }
/*     */ 
/*     */   public void setFont(Font font) {
/* 128 */     this.font = font;
/*     */   }
/*     */ 
/*     */   public float getY() {
/* 132 */     return this.y;
/*     */   }
/*     */ 
/*     */   public void setY(float y) {
/* 136 */     this.y = y;
/*     */   }
/*     */ 
/*     */   public float getX() {
/* 140 */     return this.x;
/*     */   }
/*     */ 
/*     */   public void setX(float x) {
/* 144 */     this.x = x;
/*     */   }
/*     */ 
/*     */   public String getString() {
/* 148 */     return this.string;
/*     */   }
/*     */ 
/*     */   public int getHeight() {
/* 152 */     return this.height;
/*     */   }
/*     */ 
/*     */   public void setHeight(int height) {
/* 156 */     this.height = height;
/*     */   }
/*     */ 
/*     */   public int getWidth() {
/* 160 */     return this.width;
/*     */   }
/*     */ 
/*     */   public void setWidth(int width) {
/* 164 */     this.width = width;
/*     */   }
/*     */ 
/*     */   public boolean containsHitPoint(int x, int y) {
/* 168 */     return (x >= this.x) && (x <= this.x + this.width) && (y >= this.y - this.height) && (y <= this.y);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 172 */     return "x=" + this.x + "y=" + this.y + "str=" + this.string;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.TextSegment
 * JD-Core Version:    0.6.0
 */