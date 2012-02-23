/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.ILongLineChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ 
/*     */ public class LongLineChartObject extends AbstractStickablePointsChartObject
/*     */   implements ILongLineChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Long Line";
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public LongLineChartObject(String key)
/*     */   {
/*  25 */     super(key, IChart.Type.LONG_LINE);
/*     */   }
/*     */ 
/*     */   public LongLineChartObject()
/*     */   {
/*  30 */     super(null, IChart.Type.LONG_LINE);
/*  31 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public LongLineChartObject(String key, long time1, double price1, long time2, double price2) {
/*  35 */     super(key, IChart.Type.LONG_LINE);
/*  36 */     this.times[0] = time1;
/*  37 */     this.prices[0] = price1;
/*  38 */     this.times[1] = time2;
/*  39 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public LongLineChartObject(LongLineChartObject chartObject) {
/*  43 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  48 */     GeneralPath drawingPath = getPath();
/*  49 */     drawingPath.reset();
/*     */ 
/*  51 */     int time1X = mapper.xt(this.times[0]);
/*  52 */     int time2X = mapper.xt(this.times[1]);
/*  53 */     int price1Y = mapper.yv(this.prices[0]);
/*  54 */     int price2Y = mapper.yv(this.prices[1]);
/*     */ 
/*  56 */     GraphicHelper.drawInfiniteLine(drawingPath, time1X, price1Y, time2X, price2Y, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  58 */     ((Graphics2D)g).draw(drawingPath);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  66 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/*  67 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/*  69 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  76 */     return arePointsValid();
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath() {
/*  80 */     if (this.path == null) {
/*  81 */       this.path = new GeneralPath();
/*     */     }
/*     */ 
/*  84 */     return this.path;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/*  89 */     this.times[1] = mapper.tx(point.x);
/*  90 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/*  95 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 100 */     long timeDiff = this.times[0] - time;
/* 101 */     double priceDiff = this.prices[0] - price;
/*     */ 
/* 103 */     this.times[0] += timeDiff;
/* 104 */     this.prices[0] += priceDiff;
/* 105 */     this.times[1] += timeDiff;
/* 106 */     this.prices[1] += priceDiff;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 111 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 116 */     return "Long Line";
/*     */   }
/*     */ 
/*     */   public LongLineChartObject clone()
/*     */   {
/* 121 */     return new LongLineChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 126 */     return "item.long.line";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 134 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.LongLineChartObject
 * JD-Core Version:    0.6.0
 */