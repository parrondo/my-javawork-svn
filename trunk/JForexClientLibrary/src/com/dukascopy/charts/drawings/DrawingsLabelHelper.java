/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.util.Comparator;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class DrawingsLabelHelper
/*     */ {
/*     */   private static final int DIST_BETWEEN_LABELS = 10;
/*     */   private final ITheme theme;
/*  33 */   final SortedSet<Rectangle> timeTextDimensions = new TreeSet(new Comparator()
/*     */   {
/*     */     public int compare(Rectangle rec1, Rectangle rec2) {
/*  36 */       int xDif = rec1.x - rec2.x;
/*  37 */       if (xDif != 0) {
/*  38 */         return xDif;
/*     */       }
/*  40 */       int yDif = rec1.y - rec2.y;
/*  41 */       return yDif;
/*     */     }
/*     */   });
/*     */ 
/*  45 */   final SortedSet<Rectangle> labelDimensions = new TreeSet(new Comparator()
/*     */   {
/*     */     public int compare(Rectangle rec1, Rectangle rec2) {
/*  48 */       int xDif = rec1.x - rec2.x;
/*  49 */       if (xDif != 0) {
/*  50 */         return xDif;
/*     */       }
/*  52 */       int yDif = rec1.y - rec2.y;
/*  53 */       return yDif;
/*     */     }
/*     */   });
/*     */ 
/*  57 */   final SortedSet<Rectangle> priceLabelAndTextDimensions = new TreeSet(new Comparator()
/*     */   {
/*     */     public int compare(Rectangle rec1, Rectangle rec2) {
/*  60 */       int yDif = rec1.y - rec2.y;
/*  61 */       if (yDif != 0) {
/*  62 */         return yDif;
/*     */       }
/*  64 */       int xDif = rec1.x - rec2.x;
/*  65 */       return xDif;
/*     */     }
/*     */   });
/*     */ 
/*     */   public DrawingsLabelHelper(ITheme theme)
/*     */   {
/*  29 */     this.theme = theme;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  70 */     this.timeTextDimensions.clear();
/*  71 */     this.labelDimensions.clear();
/*  72 */     this.priceLabelAndTextDimensions.clear();
/*     */   }
/*     */ 
/*     */   public Rectangle drawPriceMarkerLabelAndTextLabel(Graphics g, ChartObject chartObject, String text, double price, int xOffset, float lineWidth, int yOfPrice, ValueFormatter valueFormatter)
/*     */   {
/*  85 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  86 */     String formattedPrice = valueFormatter.formatHorizontalLinePrice(price);
/*  87 */     int priceLabelWidth = fontMetrics.stringWidth(formattedPrice);
/*  88 */     int priceLabelHeight = fontMetrics.getHeight();
/*     */ 
/*  90 */     int horizontalAlignment = 4;
/*  91 */     if (chartObject != null) {
/*  92 */       horizontalAlignment = chartObject.getHorizontalAlignment();
/*     */     }
/*     */ 
/*  95 */     int x = 0;
/*  96 */     int xLabelOffset = 0;
/*  97 */     switch (horizontalAlignment) {
/*     */     case 4:
/*  99 */       xLabelOffset = 5;
/* 100 */       x = (int)(xOffset - 20 - lineWidth * 2.0F - priceLabelWidth);
/* 101 */       break;
/*     */     case 0:
/* 103 */       xLabelOffset = 3;
/* 104 */       x = xOffset / 2 - priceLabelWidth / 2;
/* 105 */       break;
/*     */     case 2:
/* 107 */       x = (int)(15.0F + lineWidth * 2.0F);
/* 108 */       break;
/*     */     case 1:
/*     */     case 3:
/*     */     default:
/* 110 */       x = (int)(xOffset - 20 - lineWidth * 2.0F - priceLabelWidth);
/*     */     }
/*     */ 
/* 113 */     int y = yOfPrice - priceLabelHeight / 2;
/* 114 */     Rectangle rect = new Rectangle(x, y, priceLabelWidth + 6, priceLabelHeight);
/*     */ 
/* 116 */     Rectangle2D textBounds = new Rectangle2D.Double(0.0D, 0.0D, 0.0D, 0.0D);
/* 117 */     if ((text != null) && (text.length() != 0)) {
/* 118 */       textBounds = fontMetrics.getStringBounds(text, g);
/*     */       Rectangle tmp224_222 = rect; tmp224_222.width = (int)(tmp224_222.width + (textBounds.getWidth() + 10.0D));
/* 120 */       switch (horizontalAlignment)
/*     */       {
/*     */       case 4:
/*     */         Rectangle tmp282_280 = rect; tmp282_280.x = (int)(tmp282_280.x - (textBounds.getWidth() + 10.0D));
/* 123 */         break;
/*     */       case 0:
/* 125 */         rect.x = (xOffset / 2);
/* 126 */         rect.x -= rect.width / 2;
/* 127 */         break;
/*     */       case 2:
/* 129 */         break;
/*     */       case 1:
/*     */       case 3:
/*     */       default:
/*     */         Rectangle tmp338_336 = rect; tmp338_336.x = (int)(tmp338_336.x - (textBounds.getWidth() + 10.0D));
/*     */       }
/*     */     }
/*     */ 
/* 135 */     for (Rectangle curRect : this.priceLabelAndTextDimensions) {
/* 136 */       if ((rect != curRect) && (rect.y + rect.height > curRect.y) && (curRect.y + curRect.height > rect.y)) {
/* 137 */         if (horizontalAlignment == 2)
/* 138 */           curRect.x += curRect.width;
/*     */         else {
/* 140 */           curRect.x -= rect.width;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 145 */     if ((text != null) && (text.length() != 0)) {
/* 146 */       g.drawString(text, rect.x + xLabelOffset, rect.y + rect.height - 3);
/*     */     }
/* 148 */     g.drawString(formattedPrice, (int)(rect.x + xLabelOffset + textBounds.getWidth() + (textBounds.getWidth() == 0.0D ? 0.0D : 10.0D)), rect.y + rect.height - 3);
/*     */ 
/* 150 */     if ((chartObject == null) || (!chartObject.isUnderEdit())) {
/* 151 */       this.priceLabelAndTextDimensions.add(rect);
/*     */     }
/*     */ 
/* 154 */     return rect;
/*     */   }
/*     */ 
/*     */   public Rectangle drawTimeMarkerLabel(Graphics g, IMapper mapper, ChartObject chartObject, long time, float lineWidth, DateFormatter dateFormatter) {
/* 158 */     String formattedTime = dateFormatter.formatTimeMarkerTime(time);
/*     */ 
/* 160 */     return drawLabelSmart(g, mapper, chartObject, formattedTime, time);
/*     */   }
/*     */ 
/*     */   public Rectangle drawLabelSmart(Graphics g, IMapper mapper, ChartObject chartObject, String text, long time)
/*     */   {
/* 165 */     FontMetrics fontMetrics = g.getFontMetrics();
/*     */ 
/* 167 */     int width = fontMetrics.stringWidth(text);
/* 168 */     int height = fontMetrics.getHeight();
/* 169 */     int x = calculateX(mapper, time, chartObject.getLineWidth(), width);
/* 170 */     int y = mapper.getHeight() - 5 - height;
/*     */ 
/* 172 */     Rectangle rect = new Rectangle(x, y, width, height);
/*     */ 
/* 174 */     int highestY = rect.y;
/* 175 */     for (Rectangle curRect : this.labelDimensions) {
/* 176 */       boolean intersect = intersect(rect, curRect);
/* 177 */       if ((rect != curRect) && (intersect) && (curRect.y <= highestY)) {
/* 178 */         curRect.y -= curRect.height;
/* 179 */         highestY = curRect.y;
/*     */       }
/*     */     }
/*     */ 
/* 183 */     Color prevColor = g.getColor();
/* 184 */     g.setColor(this.theme.getColor(ITheme.ChartElement.BACKGROUND));
/* 185 */     g.fillRect(rect.x, rect.y, rect.width, rect.height);
/* 186 */     g.setColor(prevColor);
/*     */ 
/* 188 */     g.drawString(text, rect.x, rect.y + rect.height);
/*     */ 
/* 190 */     if (!chartObject.isUnderEdit()) {
/* 191 */       this.labelDimensions.add(rect);
/*     */     }
/* 193 */     return rect;
/*     */   }
/*     */ 
/*     */   public Rectangle drawLabel(Graphics g, String label, int x, int y)
/*     */   {
/* 202 */     FontMetrics fontMetrics = g.getFontMetrics();
/*     */ 
/* 204 */     int width = fontMetrics.stringWidth(label);
/* 205 */     int height = fontMetrics.getHeight();
/*     */ 
/* 207 */     Rectangle rect = new Rectangle(x, y, width, height);
/*     */ 
/* 209 */     g.drawString(label, rect.x, rect.y + rect.height);
/*     */ 
/* 211 */     return rect;
/*     */   }
/*     */ 
/*     */   public Rectangle drawLabelGeneric(Graphics g, IMapper mapper, ChartObject chartObject, long time, double price, boolean downgrade)
/*     */   {
/* 233 */     String label = chartObject.getText();
/* 234 */     if (ObjectUtils.isNullOrEmpty(label)) {
/* 235 */       return ChartObject.ZERO_RECTANGLE;
/*     */     }
/*     */ 
/* 238 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 239 */     int width = fontMetrics.stringWidth(label);
/* 240 */     int height = fontMetrics.getHeight();
/* 241 */     int x = calculateX(mapper, time, chartObject.getLineWidth(), width);
/* 242 */     int y = calculateY(mapper, price, downgrade, chartObject.getLineWidth(), height);
/*     */ 
/* 244 */     Rectangle rect = new Rectangle(x, y, width, height);
/*     */ 
/* 246 */     g.drawString(label, rect.x, rect.y);
/*     */ 
/* 248 */     return rect;
/*     */   }
/*     */ 
/*     */   public Rectangle drawTimeMarkerText(Graphics g, IMapper mapper, ChartObject chartObject, long time, float lineWidth, String text, Rectangle labelArea) {
/* 252 */     if ((text == null) || (text.length() == 0)) {
/* 253 */       return ChartObject.ZERO_RECTANGLE;
/*     */     }
/*     */ 
/* 256 */     FontMetrics fontMetrics = g.getFontMetrics();
/*     */ 
/* 258 */     int width = fontMetrics.stringWidth(text);
/* 259 */     int height = fontMetrics.getHeight();
/* 260 */     int x = calculateX(mapper, time, lineWidth, width);
/*     */ 
/* 262 */     Rectangle rect = new Rectangle(x, 2, width, height);
/*     */ 
/* 264 */     int lowerestY = rect.y;
/* 265 */     for (Rectangle curRect : this.timeTextDimensions) {
/* 266 */       if ((rect != curRect) && (intersect(rect, curRect)) && (curRect.y >= lowerestY)) {
/* 267 */         curRect.y += curRect.height;
/* 268 */         lowerestY = curRect.y;
/*     */       }
/*     */     }
/*     */ 
/* 272 */     Color prevColor = g.getColor();
/* 273 */     g.setColor(this.theme.getColor(ITheme.ChartElement.BACKGROUND));
/* 274 */     g.setColor(prevColor);
/*     */ 
/* 276 */     g.drawString(text, rect.x, rect.y + rect.height);
/*     */ 
/* 278 */     if (!chartObject.isUnderEdit()) {
/* 279 */       this.timeTextDimensions.add(rect);
/*     */     }
/*     */ 
/* 282 */     return rect;
/*     */   }
/*     */ 
/*     */   boolean intersect(Rectangle rect, Rectangle curRect) {
/* 286 */     return (rect.x + rect.width > curRect.x) && (curRect.x + curRect.width > rect.x);
/*     */   }
/*     */ 
/*     */   int calculateX(IMapper dataMapper, long time, float lineWidth, int width) {
/* 290 */     int x = dataMapper.xt(time);
/* 291 */     if (x + 3 + lineWidth + width >= dataMapper.getWidth())
/* 292 */       x = (int)(x - (3.0F + lineWidth + width));
/*     */     else {
/* 294 */       x = (int)(x + (3.0F + lineWidth));
/*     */     }
/* 296 */     return x;
/*     */   }
/*     */ 
/*     */   int calculateY(IMapper dataMapper, double price, boolean downgrade, float lineWidth, int height) {
/* 300 */     int dy = 3 + (int)lineWidth + height;
/* 301 */     int y = dataMapper.yv(price);
/* 302 */     int offset = ((downgrade) && (y - dy > 0)) || (y + dy >= dataMapper.getHeight()) ? -5 : height;
/*     */ 
/* 304 */     return y + offset;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.DrawingsLabelHelper
 * JD-Core Version:    0.6.0
 */