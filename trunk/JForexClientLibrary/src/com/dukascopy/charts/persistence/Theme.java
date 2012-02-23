/*     */ package com.dukascopy.charts.persistence;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Theme
/*     */   implements ITheme
/*     */ {
/*     */   private String name;
/*  16 */   private final Map<ITheme.ChartElement, Color> colors = new EnumMap(ITheme.ChartElement.class);
/*  17 */   private final Map<ITheme.TextElement, Font> fonts = new EnumMap(ITheme.TextElement.class);
/*  18 */   private final Map<ITheme.StrokeElement, BasicStroke> strokes = new EnumMap(ITheme.StrokeElement.class);
/*     */ 
/*     */   public Theme(String name) {
/*  21 */     if ((name == null) || (name.isEmpty())) {
/*  22 */       throw new IllegalArgumentException("Theme name is empty");
/*     */     }
/*     */ 
/*  25 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void setColor(ITheme.ChartElement chartElement, Color color)
/*     */   {
/*  30 */     if (color == null) {
/*  31 */       throw new IllegalArgumentException("Color of [" + chartElement.name() + "] is null");
/*     */     }
/*     */ 
/*  34 */     if (ITheme.ChartElement.BACKGROUND.equals(chartElement)) {
/*  35 */       Color oldBackground = getColor(ITheme.ChartElement.BACKGROUND);
/*  36 */       Color axisBackground = getColor(ITheme.ChartElement.AXIS_PANEL_BACKGROUND);
/*  37 */       if (oldBackground.equals(axisBackground)) {
/*  38 */         setColor(ITheme.ChartElement.AXIS_PANEL_BACKGROUND, color);
/*     */       }
/*     */     }
/*     */ 
/*  42 */     this.colors.put(chartElement, color);
/*     */   }
/*     */ 
/*     */   public void setFont(ITheme.TextElement textElement, Font font)
/*     */   {
/*  47 */     if (font == null) {
/*  48 */       throw new IllegalArgumentException("Font of [" + textElement.name() + "] is null");
/*     */     }
/*     */ 
/*  51 */     this.fonts.put(textElement, font);
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  56 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  61 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Color getColor(ITheme.ChartElement chartElement)
/*     */   {
/*  66 */     Color color = (Color)this.colors.get(chartElement);
/*     */ 
/*  68 */     if (color == null) {
/*  69 */       return (Color)this.colors.get(ITheme.ChartElement.DEFAULT);
/*     */     }
/*     */ 
/*  72 */     return color;
/*     */   }
/*     */ 
/*     */   public Font getFont(ITheme.TextElement textElement)
/*     */   {
/*  77 */     Font font = (Font)this.fonts.get(textElement);
/*     */ 
/*  79 */     if (font == null) {
/*  80 */       return (Font)this.fonts.get(ITheme.TextElement.DEFAULT);
/*     */     }
/*     */ 
/*  83 */     return font;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/*  88 */     if (super.equals(other)) {
/*  89 */       return true;
/*     */     }
/*     */ 
/*  92 */     if (other == null) {
/*  93 */       return false;
/*     */     }
/*     */ 
/*  96 */     if ((other instanceof Theme)) {
/*  97 */       Theme otherTheme = (Theme)other;
/*     */ 
/*  99 */       if (!this.name.equalsIgnoreCase(otherTheme.getName())) {
/* 100 */         return false;
/*     */       }
/*     */ 
/* 103 */       for (ITheme.ChartElement chartElement : this.colors.keySet()) {
/* 104 */         if (!getColor(chartElement).equals(otherTheme.getColor(chartElement))) {
/* 105 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 109 */       for (ITheme.TextElement textElement : this.fonts.keySet()) {
/* 110 */         if (!getFont(textElement).equals(otherTheme.getFont(textElement))) {
/* 111 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 115 */       for (ITheme.StrokeElement strokeElement : this.strokes.keySet()) {
/* 116 */         if (!getStroke(strokeElement).equals(otherTheme.getStroke(strokeElement))) {
/* 117 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 121 */       return true;
/*     */     }
/*     */ 
/* 124 */     return false;
/*     */   }
/*     */ 
/*     */   public Theme clone()
/*     */   {
/* 129 */     Theme clone = new Theme(this.name);
/* 130 */     clone.colors.putAll(this.colors);
/* 131 */     clone.fonts.putAll(this.fonts);
/* 132 */     clone.strokes.putAll(this.strokes);
/* 133 */     return clone;
/*     */   }
/*     */ 
/*     */   public BasicStroke getStroke(ITheme.StrokeElement strokeElement)
/*     */   {
/* 138 */     BasicStroke stroke = (BasicStroke)this.strokes.get(strokeElement);
/*     */ 
/* 140 */     if (stroke == null) {
/* 141 */       if (!this.strokes.containsKey(ITheme.StrokeElement.DEFAULT)) {
/* 142 */         this.strokes.put(ITheme.StrokeElement.DEFAULT, ITheme.StrokeElement.BASIC_STROKE);
/*     */       }
/* 144 */       return (BasicStroke)this.strokes.get(ITheme.StrokeElement.DEFAULT);
/*     */     }
/*     */ 
/* 147 */     return stroke;
/*     */   }
/*     */ 
/*     */   public void setStroke(ITheme.StrokeElement strokeElement, BasicStroke stroke)
/*     */   {
/* 152 */     if (stroke == null) {
/* 153 */       throw new IllegalArgumentException("Stroke of [" + strokeElement.name() + "] is null");
/*     */     }
/*     */ 
/* 156 */     this.strokes.put(strokeElement, stroke);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.Theme
 * JD-Core Version:    0.6.0
 */