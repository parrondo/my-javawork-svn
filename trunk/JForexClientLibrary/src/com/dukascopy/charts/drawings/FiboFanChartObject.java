/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IFiboFanChartObject;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FiboFanChartObject extends HorizontalRetracementChartObject
/*     */   implements IFiboFanChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 3L;
/*     */   private static final String CHART_OBJECT_NAME = "Fibo Fan";
/*     */   private static final int LABEL_OFFSET = 5;
/*     */ 
/*     */   public FiboFanChartObject(String key)
/*     */   {
/*  28 */     super(key, IChart.Type.FIBOFAN);
/*     */   }
/*     */ 
/*     */   public FiboFanChartObject() {
/*  32 */     super(IChart.Type.FIBOFAN);
/*     */   }
/*     */ 
/*     */   public FiboFanChartObject(String key, long time1, double price1, long time2, double price2)
/*     */   {
/*  37 */     super(key, IChart.Type.FIBOFAN, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public FiboFanChartObject(FiboFanChartObject chartObject) {
/*  41 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  49 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/*  51 */     int x1 = mapper.xt(this.times[0]);
/*  52 */     int x2 = mapper.xt(this.times[1]);
/*  53 */     int y1 = mapper.yv(this.prices[0]);
/*  54 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  56 */     GeneralPath drawingPath = getPath();
/*  57 */     drawingPath.reset();
/*     */ 
/*  59 */     int width = mapper.getWidth();
/*  60 */     int height = mapper.getHeight();
/*     */ 
/*  62 */     GraphicHelper.drawSegmentLine(drawingPath, x1, y1, x2, y2, width, height);
/*     */ 
/*  64 */     g2.draw(drawingPath);
/*     */     float dx;
/*     */     float dy;
/*     */     float xText;
/*     */     ValueFormatter valueFormatter;
/*     */     GeneralPath levelPath;
/*  66 */     if (x1 != x2) {
/*  67 */       dx = x2 - x1;
/*  68 */       dy = y2 - y1;
/*     */ 
/*  70 */       xText = x2 + 5;
/*  71 */       valueFormatter = formattersManager.getValueFormatter();
/*     */ 
/*  73 */       levelPath = new GeneralPath();
/*  74 */       for (Object[] level : getLevels()) {
/*  75 */         levelPath.reset();
/*     */ 
/*  77 */         String label = (String)level[0];
/*  78 */         double value = ((Double)level[1]).doubleValue();
/*  79 */         Color color = (Color)level[2];
/*     */ 
/*  82 */         if ((value == -1.0D) || (value == 0.0D) || (value == 1.0D)) {
/*     */           continue;
/*     */         }
/*  85 */         double k = dy * (1.0D - value) / dx;
/*     */ 
/*  87 */         plotLine(drawingPath, x1, y1, x1, k, dx > 0.0F ? mapper.getWidth() : 0, width, height);
/*  88 */         plotLine(levelPath, x1, y1, x1, k, dx > 0.0F ? mapper.getWidth() : 0, width, height);
/*     */ 
/*  90 */         g2.setColor(color == null ? getColor() : color);
/*  91 */         g2.draw(levelPath);
/*     */ 
/*  93 */         StringBuilder valueLabel = new StringBuilder().append(label).append("  ").append(valueFormatter.formatFibo(value * 100.0D));
/*  94 */         g2.drawString(valueLabel.toString(), xText, (float)(yOfLine(xText, x1, y1, k) + 10.0D));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 105 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 106 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/* 108 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*     */   {
/* 114 */     this.times[1] = mapper.tx(point.x);
/* 115 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 120 */     return getPath().intersects(point.x - range / 2, point.y - range / 2, range * 2, range * 2);
/*     */   }
/*     */ 
/*     */   public List<Object[]> getDefaults()
/*     */   {
/* 126 */     return ChartProperties.createDefaultLevelsFibo();
/*     */   }
/*     */ 
/*     */   void plotLine(GeneralPath drawingPath, double xBase, double yBase, double xBeg, double k, int xEnd, int width, int height)
/*     */   {
/* 139 */     double yBeg = yOfLine(xBeg, xBase, yBase, k);
/* 140 */     double yEnd = yOfLine(xEnd, xBase, yBase, k);
/*     */ 
/* 142 */     plotLineSegment(drawingPath, xBeg, yBeg, xEnd, yEnd, width, height);
/*     */   }
/*     */ 
/*     */   void plotLineSegment(GeneralPath drawingPath, double xBeg, double yBeg, double xEnd, double yEnd, int width, int height)
/*     */   {
/* 154 */     GraphicHelper.drawSegmentLine(drawingPath, xBeg, yBeg, xEnd, yEnd, width, height);
/*     */   }
/*     */ 
/*     */   double yOfLine(double x, double baseX, double baseY, double k) {
/* 158 */     return baseY + (x - baseX) * k;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 163 */     return "Fibo Fan";
/*     */   }
/*     */ 
/*     */   public FiboFanChartObject clone()
/*     */   {
/* 168 */     return new FiboFanChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 173 */     return "item.fibonacci.fan.lines";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 180 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.FiboFanChartObject
 * JD-Core Version:    0.6.0
 */