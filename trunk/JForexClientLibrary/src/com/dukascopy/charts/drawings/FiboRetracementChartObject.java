/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IFiboRetracementChartObject;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FiboRetracementChartObject extends HorizontalRetracementChartObject
/*     */   implements IFiboRetracementChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   private static final String CHART_OBJECT_NAME = "Fibo Retracement";
/*  21 */   private boolean fixedLevelsRightSide = false;
/*     */ 
/*     */   public FiboRetracementChartObject() {
/*  24 */     super(IChart.Type.FIBO);
/*     */   }
/*     */ 
/*     */   public FiboRetracementChartObject(String key, long time1, double price1, long time2, double price2) {
/*  28 */     super(key, IChart.Type.FIBO, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public FiboRetracementChartObject(String key) {
/*  32 */     super(key, IChart.Type.FIBO);
/*     */   }
/*     */ 
/*     */   public FiboRetracementChartObject(FiboRetracementChartObject chartObject) {
/*  36 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   public boolean isFixedLevelsRightSide()
/*     */   {
/*  42 */     return this.fixedLevelsRightSide;
/*     */   }
/*     */ 
/*     */   public void setFixedLevelsRightSide(boolean fixedLevelsRightSide) {
/*  46 */     this.fixedLevelsRightSide = fixedLevelsRightSide;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  52 */     GeneralPath drawingPath = getPath();
/*  53 */     drawingPath.reset();
/*     */ 
/*  55 */     this.lines.clear();
/*     */ 
/*  57 */     int x1 = mapper.xt(this.times[0]);
/*  58 */     int x2 = mapper.xt(this.times[1]);
/*     */ 
/*  60 */     if (!isValidTime(1)) {
/*  61 */       x2 = x1;
/*     */     }
/*     */ 
/*  64 */     int leftX = Math.min(x1, x2);
/*     */     int rightX;
/*     */     int rightX;
/*  67 */     if (isFixedLevelsRightSide())
/*  68 */       rightX = Math.max(x1, x2);
/*     */     else {
/*  70 */       rightX = mapper.getWidth();
/*     */     }
/*     */ 
/*  73 */     drawLevels((Graphics2D)g, mapper, formattersManager.getValueFormatter(), drawingPath, leftX, rightX, this.prices[0], this.prices[1]);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  90 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/*  91 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/*  93 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public List<Object[]> getDefaults()
/*     */   {
/* 101 */     return ChartProperties.createDefaultLevelsFiboRetracements();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 106 */     return "Fibo Retracement";
/*     */   }
/*     */ 
/*     */   public FiboRetracementChartObject clone()
/*     */   {
/* 111 */     return new FiboRetracementChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 116 */     return "item.fibonacci.retracements";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 125 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.FiboRetracementChartObject
 * JD-Core Version:    0.6.0
 */