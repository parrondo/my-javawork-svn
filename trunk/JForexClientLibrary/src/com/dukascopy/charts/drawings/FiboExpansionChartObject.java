/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.drawings.IFiboExpansionChartObject;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Line2D.Double;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FiboExpansionChartObject extends AbstractThreePointFiboExtensionsChartObject
/*     */   implements IFiboExpansionChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Fibo Expansion";
/*     */   private static final int LABEL_OFFSET = 5;
/*     */ 
/*     */   public FiboExpansionChartObject(String key)
/*     */   {
/*  34 */     super(key, IChart.Type.EXPANSION);
/*     */   }
/*     */ 
/*     */   public FiboExpansionChartObject() {
/*  38 */     super(IChart.Type.EXPANSION);
/*     */   }
/*     */ 
/*     */   public FiboExpansionChartObject(FiboExpansionChartObject chartObj) {
/*  42 */     super(chartObj);
/*     */   }
/*     */ 
/*     */   public FiboExpansionChartObject(String key, long time1, double price1, long time2, double price2, long time3, double price3) {
/*  46 */     super(key, IChart.Type.EXPANSION, time1, price1, time2, price2, time3, price3);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  57 */     int x1 = mapper.xt(this.times[0]);
/*  58 */     int x2 = mapper.xt(this.times[1]);
/*  59 */     int y1 = mapper.yv(this.prices[0]);
/*  60 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  62 */     GeneralPath drawingPath = getPath();
/*  63 */     drawingPath.reset();
/*     */ 
/*  65 */     g.setColor(getColor());
/*  66 */     GraphicHelper.drawSegmentDashedLine(drawingPath, x1, y1, x2, y2, 5.0D, 5.0D, mapper.getWidth(), mapper.getHeight());
/*     */     int y3;
/*     */     int minX;
/*     */     int dx;
/*     */     int dy;
/*     */     GeneralPath levelPath;
/*     */     int width;
/*     */     int height;
/*  68 */     if (isValidPoint(2)) {
/*  69 */       int x3 = mapper.xt(this.times[2]);
/*  70 */       y3 = mapper.yv(this.prices[2]);
/*     */ 
/*  72 */       GraphicHelper.drawSegmentDashedLine(drawingPath, x2, y2, x3, y3, 5.0D, 5.0D, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  74 */       ((Graphics2D)g).draw(drawingPath);
/*     */ 
/*  76 */       minX = Math.min(x1, x3);
/*  77 */       int maxX = Math.max(x1, x3);
/*  78 */       dx = maxX - minX;
/*  79 */       dy = y2 - y1;
/*     */ 
/*  81 */       this.lines.clear();
/*     */ 
/*  83 */       levelPath = new GeneralPath();
/*  84 */       width = mapper.getWidth();
/*  85 */       height = mapper.getHeight();
/*     */ 
/*  87 */       for (Object[] level : getLevels()) {
/*  88 */         levelPath.reset();
/*     */ 
/*  90 */         String label = (String)level[0];
/*  91 */         double value = ((Double)level[1]).doubleValue();
/*  92 */         Color color = (Color)level[2];
/*     */ 
/*  94 */         double y = y3 + dy * value;
/*  95 */         double xEnd = minX + dx * 3;
/*  96 */         xEnd = mapper.getWidth();
/*     */ 
/*  98 */         g.setColor(color == null ? getColor() : color);
/*     */ 
/* 100 */         GraphicHelper.drawSegmentLine(levelPath, minX, y, xEnd, y, width, height);
/* 101 */         GraphicHelper.drawSegmentLine(drawingPath, minX, y, xEnd, y, width, height);
/*     */ 
/* 104 */         String valueLabel = label + "  " + formattersManager.getValueFormatter().formatFibo(value * 100.0D) + "%";
/*     */ 
/* 110 */         FontMetrics metrics = g.getFontMetrics();
/* 111 */         int stringLength = (int)metrics.getStringBounds(valueLabel, g).getWidth();
/*     */ 
/* 113 */         g.drawString(valueLabel, (int)xEnd - stringLength - 5, (int)y - 5);
/*     */ 
/* 115 */         this.lines.add(new Line2D.Double(minX, y, xEnd, y));
/*     */ 
/* 117 */         ((Graphics2D)g).draw(levelPath);
/*     */       }
/*     */     } else {
/* 120 */       ((Graphics2D)g).draw(drawingPath);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 129 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 130 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/* 132 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public IChartObject clone()
/*     */   {
/* 138 */     return new FiboExpansionChartObject(this);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 143 */     return "Fibo Expansion";
/*     */   }
/*     */ 
/*     */   protected double calculateNewValue(IMapper mapper, Point point)
/*     */   {
/* 148 */     int y1 = mapper.yv(this.prices[0]);
/* 149 */     int y2 = mapper.yv(this.prices[1]);
/* 150 */     int y3 = mapper.yv(this.prices[2]);
/*     */ 
/* 152 */     double newValue = (point.y - y3) / (y2 - y1);
/*     */ 
/* 154 */     if (newValue < ChartProperties.MIN_LEVEL_VALUE.doubleValue() / 100.0D) {
/* 155 */       newValue = ChartProperties.MIN_LEVEL_VALUE.doubleValue() / 100.0D;
/*     */     }
/* 157 */     else if (newValue > ChartProperties.MAX_LEVEL_VALUE.doubleValue() / 100.0D) {
/* 158 */       newValue = ChartProperties.MAX_LEVEL_VALUE.doubleValue() / 100.0D;
/*     */     }
/*     */ 
/* 161 */     return newValue;
/*     */   }
/*     */ 
/*     */   protected Point getHandlerPointForLevelIndex(Line2D.Double line)
/*     */   {
/* 166 */     return new Point((int)(line.x1 + line.x2) / 2, (int)line.y1);
/*     */   }
/*     */ 
/*     */   public List<Object[]> getDefaults()
/*     */   {
/* 171 */     return ChartProperties.createDefaultLevelsFiboExtensions();
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 176 */     return "item.fibonacci.expansion";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 184 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.FiboExpansionChartObject
 * JD-Core Version:    0.6.0
 */