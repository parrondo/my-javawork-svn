/*     */ package com.dukascopy.charts.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class IndicatorsManagerImpl
/*     */ {
/*     */   private final PathHelper pathHelper;
/*     */   private final ChartsActionListenerRegistry chartsActionListenerRegistry;
/*     */   private final IndicatorTooltipManager indicatorTooltipManager;
/*     */   private IndicatorWrapper highlightedIndicator;
/*     */   private LevelInfo highlightedLevel;
/*     */   private IndicatorWrapper selectedIndicator;
/*     */   private int highlightedOutputIdx;
/*     */   private Point intersectionPoint;
/*     */ 
/*     */   public IndicatorsManagerImpl(ChartsActionListenerRegistry chartsActionListenerRegistry, PathHelper pathHelper, IndicatorTooltipManager indicatorTooltipManager)
/*     */   {
/*  34 */     this.chartsActionListenerRegistry = chartsActionListenerRegistry;
/*  35 */     this.pathHelper = pathHelper;
/*  36 */     this.indicatorTooltipManager = indicatorTooltipManager;
/*     */   }
/*     */ 
/*     */   public void drawHandles(Graphics g2) {
/*  40 */     if ((!isSomeIndicatorHighlighted()) && (!isSomeIndicatorSelected())) {
/*  41 */       return;
/*     */     }
/*     */ 
/*  44 */     Map indicatorHandlesPaths = this.pathHelper.getIndicatorHandlesPaths();
/*  45 */     Map selectedIndicatorHandlesPaths = (Map)indicatorHandlesPaths.get(this.selectedIndicator);
/*  46 */     Map highlightedIndicatorHandlesPaths = (Map)indicatorHandlesPaths.get(this.highlightedIndicator);
/*     */ 
/*  48 */     if (((this.selectedIndicator != null) && (this.highlightedIndicator == null)) || (this.highlightedIndicator == this.selectedIndicator)) {
/*  49 */       Map selectedIndicatorHandlesPath = plotSelectedIndicatorHandlesPath(selectedIndicatorHandlesPaths);
/*  50 */       drawPaths(g2, selectedIndicatorHandlesPath, true);
/*  51 */     } else if (this.selectedIndicator == null) {
/*  52 */       Map highlightedIndicatorHandlesPath = plotHighlightedIndicatorHandlesPath(highlightedIndicatorHandlesPaths);
/*  53 */       drawPaths(g2, highlightedIndicatorHandlesPath, false);
/*     */     } else {
/*  55 */       Map selectedIndicatorHandlesPath = plotSelectedIndicatorHandlesPath(selectedIndicatorHandlesPaths);
/*  56 */       Map highlightedIndicatorHandlesPath = plotHighlightedIndicatorHandlesPath(highlightedIndicatorHandlesPaths);
/*  57 */       drawPaths(g2, selectedIndicatorHandlesPath, true);
/*  58 */       drawPaths(g2, highlightedIndicatorHandlesPath, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void drawMouseCrossHandler(Graphics g) {
/*  63 */     if ((!isSomeIndicatorHighlighted()) || (this.highlightedLevel != null)) {
/*  64 */       return;
/*     */     }
/*  66 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  68 */     IndicatorTooltipManager.TooltipData tooltipData = this.indicatorTooltipManager.getIndicatorTooltipAtPoint(this.intersectionPoint, this.highlightedIndicator, this.highlightedOutputIdx);
/*  69 */     if (tooltipData == null) {
/*  70 */       return;
/*     */     }
/*     */ 
/*  73 */     int x = tooltipData.getX();
/*  74 */     int y = tooltipData.getY();
/*     */ 
/*  76 */     int pointRadius = tooltipData.getLineWidth() + 2;
/*     */ 
/*  78 */     g2d.setColor(tooltipData.getHandlerColor());
/*  79 */     Object oldAntialiasing = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/*  80 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/*  82 */     g.fillOval(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2);
/*     */ 
/*  84 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialiasing);
/*     */ 
/*  86 */     String strIndName = tooltipData.getIndName();
/*  87 */     String strTime = "Time: " + tooltipData.getTime();
/*  88 */     String strValue = "Value: " + tooltipData.getPrice();
/*     */ 
/*  90 */     int fontHeight = g.getFontMetrics().getHeight();
/*     */ 
/*  92 */     int INSET = 2;
/*     */ 
/*  94 */     int tooltipWidth = g.getFontMetrics().stringWidth(strIndName);
/*  95 */     tooltipWidth = Math.max(tooltipWidth, g.getFontMetrics().stringWidth(strTime));
/*  96 */     tooltipWidth = Math.max(tooltipWidth, g.getFontMetrics().stringWidth(strValue));
/*  97 */     tooltipWidth += 6;
/*  98 */     int tooltipHeight = fontHeight * 3 + 4;
/*     */ 
/* 100 */     g.setColor(tooltipData.getBackgroundColor());
/* 101 */     g.fillRoundRect(x + pointRadius, y - tooltipHeight - pointRadius, tooltipWidth, tooltipHeight, 5, 5);
/*     */ 
/* 103 */     g.setColor(tooltipData.getBorderColor());
/* 104 */     g.drawRoundRect(x + pointRadius, y - tooltipHeight - pointRadius, tooltipWidth, tooltipHeight, 5, 5);
/*     */ 
/* 106 */     g.setColor(tooltipData.getFontColor());
/* 107 */     g.drawString(strIndName, x + pointRadius + 4, y - fontHeight * 2 - pointRadius - 4);
/* 108 */     g.drawString(strTime, x + pointRadius + 4, y - fontHeight - pointRadius - 4);
/* 109 */     g.drawString(strValue, x + pointRadius + 4, y - pointRadius - 4);
/*     */   }
/*     */ 
/*     */   public boolean tryToSelectIndicator(Point point)
/*     */   {
/* 115 */     Rectangle2D handlerRect = new Rectangle2D.Double(point.x - 3, point.y - 3, 6.0D, 6.0D);
/* 116 */     Map indicatorPaths = this.pathHelper.getIndicatorPaths();
/*     */ 
/* 118 */     for (Map.Entry indicatorEntry : indicatorPaths.entrySet()) {
/* 119 */       for (int outpIdx = 0; outpIdx < ((List[])indicatorEntry.getValue()).length; outpIdx++) {
/* 120 */         List outputPath = ((List[])indicatorEntry.getValue())[outpIdx];
/* 121 */         if (outputPath == null) {
/*     */           continue;
/*     */         }
/* 124 */         for (Shape indicatorShape : outputPath) {
/* 125 */           if (GraphicHelper.intersects((GeneralPath)indicatorShape, handlerRect)) {
/* 126 */             this.selectedIndicator = ((IndicatorWrapper)indicatorEntry.getKey());
/* 127 */             this.highlightedIndicator = ((IndicatorWrapper)indicatorEntry.getKey());
/* 128 */             this.highlightedOutputIdx = outpIdx;
/* 129 */             this.chartsActionListenerRegistry.indicatorChanged(this.selectedIndicator, -1);
/* 130 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 136 */     this.selectedIndicator = null;
/* 137 */     this.highlightedIndicator = null;
/* 138 */     this.highlightedOutputIdx = -1;
/* 139 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean triggerHighlighting(Point point)
/*     */   {
/* 144 */     Rectangle2D handlerRect = new Rectangle2D.Double(point.x - 3, point.y - 3, 6.0D, 6.0D);
/*     */ 
/* 146 */     boolean someActionPerformed = false;
/*     */ 
/* 148 */     Map indicatorPaths = this.pathHelper.getIndicatorPaths();
/* 149 */     Map indicatorLevelsPaths = this.pathHelper.getIndicatorLevelsPaths();
/*     */ 
/* 151 */     for (Map.Entry indicatorEntry : indicatorPaths.entrySet()) {
/* 152 */       boolean intersects = false;
/* 153 */       for (int outpIdx = 0; outpIdx < ((List[])indicatorEntry.getValue()).length; outpIdx++) {
/* 154 */         List outputPaths = ((List[])indicatorEntry.getValue())[outpIdx];
/* 155 */         if (outputPaths == null) {
/*     */           continue;
/*     */         }
/* 158 */         for (Shape path : outputPaths) {
/* 159 */           if (GraphicHelper.intersects((GeneralPath)path, handlerRect)) {
/* 160 */             this.highlightedOutputIdx = outpIdx;
/* 161 */             this.intersectionPoint = point;
/*     */ 
/* 163 */             intersects = true;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 168 */       LevelInfo highlightedLevel = null;
/* 169 */       if (!intersects)
/*     */       {
/* 171 */         Map levelsPaths = (Map)indicatorLevelsPaths.get(indicatorEntry.getKey());
/* 172 */         for (Map.Entry levelEntry : levelsPaths.entrySet()) {
/* 173 */           if (GraphicHelper.intersects((GeneralPath)levelEntry.getValue(), handlerRect)) {
/* 174 */             intersects = true;
/* 175 */             highlightedLevel = (LevelInfo)levelEntry.getKey();
/* 176 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 180 */       if ((intersects) && (this.highlightedIndicator == indicatorEntry.getKey()) && (this.highlightedLevel == highlightedLevel)) {
/* 181 */         someActionPerformed = false;
/* 182 */         break;
/*     */       }
/* 184 */       someActionPerformed |= checkForActions((IndicatorWrapper)indicatorEntry.getKey(), highlightedLevel, intersects);
/*     */     }
/* 186 */     return someActionPerformed;
/*     */   }
/*     */ 
/*     */   public boolean isSomeIndicatorHighlighted()
/*     */   {
/* 191 */     return this.highlightedIndicator != null;
/*     */   }
/*     */ 
/*     */   public boolean isSomeIndicatorSelected() {
/* 195 */     return this.selectedIndicator != null;
/*     */   }
/*     */ 
/*     */   public IndicatorWrapper getHighlightedIndicator() {
/* 199 */     return this.highlightedIndicator;
/*     */   }
/*     */ 
/*     */   public LevelInfo getHighlightedLevel() {
/* 203 */     return this.highlightedLevel;
/*     */   }
/*     */ 
/*     */   public IndicatorWrapper getSelectedIndicator() {
/* 207 */     return this.selectedIndicator;
/*     */   }
/*     */ 
/*     */   public void selectHighlightedIndicator() {
/* 211 */     this.selectedIndicator = this.highlightedIndicator;
/*     */   }
/*     */ 
/*     */   public void unseletSelectedIndicator() {
/* 215 */     this.selectedIndicator = null;
/*     */   }
/*     */ 
/*     */   public void dehighlightHighlightedIndicator() {
/* 219 */     this.highlightedIndicator = null;
/*     */   }
/*     */ 
/*     */   Map<Shape, Color> plotSelectedIndicatorHandlesPath(Map<Color, List<Point>> handlerMiddlePoints)
/*     */   {
/* 224 */     Map shapes = new HashMap();
/* 225 */     if (handlerMiddlePoints == null) {
/* 226 */       return shapes;
/*     */     }
/* 228 */     for (Map.Entry entry : handlerMiddlePoints.entrySet()) {
/* 229 */       List points = (List)entry.getValue();
/* 230 */       GeneralPath tmpHandlesPath = new GeneralPath();
/* 231 */       for (Point point : points) {
/* 232 */         int x = point.x;
/* 233 */         int y = point.y;
/* 234 */         int rad = 3;
/* 235 */         tmpHandlesPath.moveTo(x, y - rad);
/* 236 */         tmpHandlesPath.quadTo(x + rad - 1, y - rad - 1, x + rad, y);
/* 237 */         tmpHandlesPath.quadTo(x + rad - 1, y + rad - 1, x, y + rad);
/* 238 */         tmpHandlesPath.quadTo(x - rad - 1, y + rad - 1, x - rad, y);
/* 239 */         tmpHandlesPath.quadTo(x - rad - 1, y - rad - 1, x, y - rad);
/*     */       }
/* 241 */       shapes.put(tmpHandlesPath, entry.getKey());
/*     */     }
/* 243 */     return shapes;
/*     */   }
/*     */ 
/*     */   Map<Shape, Color> plotHighlightedIndicatorHandlesPath(Map<Color, List<Point>> handlerMiddlePoints) {
/* 247 */     if (handlerMiddlePoints == null) {
/* 248 */       return Collections.emptyMap();
/*     */     }
/* 250 */     Map shapes = new HashMap();
/* 251 */     for (Map.Entry entry : handlerMiddlePoints.entrySet()) {
/* 252 */       List points = (List)entry.getValue();
/* 253 */       GeneralPath tmpHandlesPath = new GeneralPath();
/* 254 */       for (Point point : points) {
/* 255 */         int x = point.x;
/* 256 */         int y = point.y;
/* 257 */         tmpHandlesPath.moveTo(x - 2, y - 2);
/* 258 */         tmpHandlesPath.lineTo(x + 2, y - 2);
/* 259 */         tmpHandlesPath.lineTo(x + 2, y + 2);
/* 260 */         tmpHandlesPath.lineTo(x - 2, y + 2);
/* 261 */         tmpHandlesPath.lineTo(x - 2, y - 2);
/*     */       }
/* 263 */       shapes.put(tmpHandlesPath, entry.getKey());
/*     */     }
/* 265 */     return shapes;
/*     */   }
/*     */ 
/*     */   void drawPaths(Graphics g2, Map<Shape, Color> entry, boolean fill) {
/* 269 */     for (Map.Entry shapes : entry.entrySet()) {
/* 270 */       Color prevColor = g2.getColor();
/* 271 */       g2.setColor((Color)shapes.getValue());
/* 272 */       if (fill)
/* 273 */         ((Graphics2D)g2).fill((Shape)shapes.getKey());
/*     */       else {
/* 275 */         ((Graphics2D)g2).draw((Shape)shapes.getKey());
/*     */       }
/* 277 */       g2.setColor(prevColor);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean checkForActions(IndicatorWrapper newIndicator, LevelInfo newLevel, boolean intersects)
/*     */   {
/* 286 */     if (intersects) {
/* 287 */       if (this.highlightedIndicator != newIndicator) {
/* 288 */         this.highlightedIndicator = newIndicator;
/* 289 */         this.highlightedLevel = newLevel;
/* 290 */         return true;
/* 291 */       }if (this.highlightedLevel != newLevel) {
/* 292 */         this.highlightedLevel = newLevel;
/* 293 */         return true;
/*     */       }
/* 295 */       return false;
/*     */     }
/*     */ 
/* 298 */     if (this.highlightedIndicator == null) {
/* 299 */       return false;
/*     */     }
/* 301 */     if (this.highlightedIndicator == newIndicator) {
/* 302 */       this.highlightedIndicator = null;
/* 303 */       this.highlightedLevel = null;
/*     */     }
/* 305 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.indicators.IndicatorsManagerImpl
 * JD-Core Version:    0.6.0
 */