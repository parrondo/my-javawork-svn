/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.drawings.IPatternWidgetChartObject;
/*     */ import com.dukascopy.api.drawings.IPatternWidgetChartObject.Pattern;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.swing.AbstractChartWidgetPanel;
/*     */ import com.dukascopy.charts.view.swing.PatternChartWidgetPanel;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class PatternWidgetChartObject extends AbstractWidgetChartObject
/*     */   implements IPatternWidgetChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final float DEFAULT_POSX = 0.5F;
/*  29 */   private static final Color DEFAULT_COLOR = new Color(255, 255, 204);
/*  30 */   private static final Color DEFAULT_BACKGROUND = new Color(64, 64, 64);
/*     */   private static final float DEFAULT_BACKGROUND_OPACITY = 0.6F;
/*  32 */   private static final Dimension DEFAULT_SIZE = new Dimension(300, 160);
/*     */   private transient PatternChartWidgetPanel widgetPanel;
/*  36 */   private Set<IPatternWidgetChartObject.Pattern> patternsToAnalyze = Collections.synchronizedSet(EnumSet.of(IPatternWidgetChartObject.Pattern.ASCENDING_TRIANGLE, IPatternWidgetChartObject.Pattern.DESCENDING_TRIANGLE));
/*     */ 
/*  38 */   private int desiredMinQuality = 50;
/*  39 */   private int desiredMinMagnitude = 20;
/*     */ 
/*  41 */   private int sortPatternsByCriteria = 0;
/*     */ 
/*  43 */   private String pivotPointsPrice = "High/Low";
/*     */ 
/*  45 */   private boolean showAll = false;
/*  46 */   private boolean onlyEmerging = false;
/*     */ 
/*     */   public PatternWidgetChartObject()
/*     */   {
/*  50 */     this((String)null);
/*     */   }
/*     */ 
/*     */   public PatternWidgetChartObject(String key) {
/*  54 */     super(key, IChart.Type.PATTERN_WIDGET);
/*     */ 
/*  56 */     init();
/*     */   }
/*     */ 
/*     */   public PatternWidgetChartObject(PatternWidgetChartObject obj) {
/*  60 */     super(obj);
/*     */ 
/*  62 */     this.desiredMinQuality = obj.desiredMinQuality;
/*  63 */     this.desiredMinMagnitude = obj.desiredMinMagnitude;
/*  64 */     this.sortPatternsByCriteria = obj.sortPatternsByCriteria;
/*  65 */     this.pivotPointsPrice = obj.pivotPointsPrice;
/*  66 */     this.showAll = obj.showAll;
/*  67 */     this.patternsToAnalyze = EnumSet.copyOf(obj.patternsToAnalyze);
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  72 */     this.posX = 0.5F;
/*     */ 
/*  74 */     setColor(DEFAULT_COLOR);
/*  75 */     setFillColor(DEFAULT_BACKGROUND);
/*  76 */     setFillOpacity(0.6F);
/*  77 */     setPreferredSize(DEFAULT_SIZE);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  86 */     this.formattersManager = formattersManager;
/*     */ 
/*  88 */     this.widgetPanel.drawPatterns(g);
/*     */   }
/*     */ 
/*     */   public void setChartWidgetPanel(AbstractChartWidgetPanel panel)
/*     */   {
/*  93 */     super.setChartWidgetPanel(panel);
/*     */ 
/*  95 */     this.widgetPanel = ((PatternChartWidgetPanel)panel);
/*     */   }
/*     */ 
/*     */   public IChartObject clone()
/*     */   {
/* 100 */     return new PatternWidgetChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 105 */     return "item.pattern.widget";
/*     */   }
/*     */ 
/*     */   public void addPattern(IPatternWidgetChartObject.Pattern pattern)
/*     */   {
/* 110 */     this.patternsToAnalyze.add(pattern);
/*     */   }
/*     */ 
/*     */   public void removePattern(IPatternWidgetChartObject.Pattern pattern)
/*     */   {
/* 115 */     this.patternsToAnalyze.remove(pattern);
/*     */   }
/*     */ 
/*     */   public Set<IPatternWidgetChartObject.Pattern> getPatternsToAnalyze()
/*     */   {
/* 122 */     return EnumSet.copyOf(this.patternsToAnalyze);
/*     */   }
/*     */ 
/*     */   public int getDesiredMinQuality() {
/* 126 */     return this.desiredMinQuality;
/*     */   }
/*     */   public void setDesiredMinQuality(int desiredMinQuality) {
/* 129 */     this.desiredMinQuality = desiredMinQuality;
/*     */   }
/*     */ 
/*     */   public int getDesiredMinMagnitude() {
/* 133 */     return this.desiredMinMagnitude;
/*     */   }
/*     */   public void setDesiredMinMagnitude(int desiredMinMagnitude) {
/* 136 */     this.desiredMinMagnitude = desiredMinMagnitude;
/*     */   }
/*     */ 
/*     */   public String getPivotPointsPrice() {
/* 140 */     return this.pivotPointsPrice;
/*     */   }
/*     */   public void setPivotPointsPrice(String pivotPointsPrice) {
/* 143 */     this.pivotPointsPrice = pivotPointsPrice;
/*     */   }
/*     */ 
/*     */   public int getSortPatternsByCriteria() {
/* 147 */     return this.sortPatternsByCriteria;
/*     */   }
/*     */   public void setSortPatternsByCriteria(int criteria) {
/* 150 */     this.sortPatternsByCriteria = criteria;
/*     */   }
/*     */ 
/*     */   public boolean isShowAll() {
/* 154 */     return this.showAll;
/*     */   }
/*     */   public void setShowAll(boolean showAll) {
/* 157 */     this.showAll = showAll;
/*     */   }
/*     */ 
/*     */   public boolean isOnlyEmerging() {
/* 161 */     return this.onlyEmerging;
/*     */   }
/*     */   public void setOnlyEmerging(boolean onlyEmerging) {
/* 164 */     this.onlyEmerging = onlyEmerging;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.PatternWidgetChartObject
 * JD-Core Version:    0.6.0
 */