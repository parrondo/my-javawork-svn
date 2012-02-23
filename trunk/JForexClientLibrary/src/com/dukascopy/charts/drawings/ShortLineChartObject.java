/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IShortLineChartObject;
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
/*     */ public class ShortLineChartObject extends AbstractStickablePointsChartObject
/*     */   implements IShortLineChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Short Line";
/*     */   private transient GeneralPath path;
/*     */ 
/*     */   public ShortLineChartObject(String key)
/*     */   {
/*  25 */     super(key, IChart.Type.SHORT_LINE);
/*     */   }
/*     */ 
/*     */   public ShortLineChartObject() {
/*  29 */     super(null, IChart.Type.SHORT_LINE);
/*  30 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public ShortLineChartObject(String key, long time1, double price1, long time2, double price2) {
/*  34 */     super(key, IChart.Type.SHORT_LINE);
/*  35 */     this.times[0] = time1;
/*  36 */     this.prices[0] = price1;
/*  37 */     this.times[1] = time2;
/*  38 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public ShortLineChartObject(ShortLineChartObject chartObject) {
/*  42 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  47 */     GeneralPath generalPath = getPath();
/*  48 */     generalPath.reset();
/*     */ 
/*  50 */     int x1 = mapper.xt(this.times[0]);
/*  51 */     int x2 = mapper.xt(this.times[1]);
/*  52 */     int y1 = mapper.yv(this.prices[0]);
/*  53 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  55 */     GraphicHelper.drawSegmentLine(generalPath, x1, y1, x2, y2, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  65 */     ((Graphics2D)g).draw(generalPath);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  73 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/*  74 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/*  76 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/*  82 */     this.times[1] = mapper.tx(point.x);
/*  83 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/*  88 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Double(point.x - range / 2, point.y - range / 2, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/*  93 */     long timeDiff = this.times[0] - time;
/*  94 */     double priceDiff = this.prices[0] - price;
/*     */ 
/*  96 */     this.times[0] += timeDiff;
/*  97 */     this.prices[0] += priceDiff;
/*  98 */     this.times[1] += timeDiff;
/*  99 */     this.prices[1] += priceDiff;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 104 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 109 */     return "Short Line";
/*     */   }
/*     */ 
/*     */   public ShortLineChartObject clone()
/*     */   {
/* 114 */     return new ShortLineChartObject(this);
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 119 */     return arePointsValid();
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath() {
/* 123 */     if (this.path == null) {
/* 124 */       this.path = new GeneralPath();
/*     */     }
/*     */ 
/* 127 */     return this.path;
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 132 */     return "item.short.line";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 140 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.ShortLineChartObject
 * JD-Core Version:    0.6.0
 */