/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IRayLineChartObject;
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
/*     */ public class RayLineChartObject extends AbstractStickablePointsChartObject
/*     */   implements IRayLineChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Ray Line";
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public RayLineChartObject(String key)
/*     */   {
/*  28 */     super(key, IChart.Type.RAY_LINE);
/*     */   }
/*     */ 
/*     */   public RayLineChartObject() {
/*  32 */     super(null, IChart.Type.RAY_LINE);
/*  33 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public RayLineChartObject(String key, long time1, double price1, long time2, double price2) {
/*  37 */     super(key, IChart.Type.RAY_LINE);
/*  38 */     this.times[0] = time1;
/*  39 */     this.prices[0] = price1;
/*  40 */     this.times[1] = time2;
/*  41 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public RayLineChartObject(RayLineChartObject chartObject) {
/*  45 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  50 */     GeneralPath drawingPath = getPath();
/*  51 */     drawingPath.reset();
/*     */ 
/*  53 */     int x1 = mapper.xt(this.times[0]);
/*  54 */     int x2 = mapper.xt(this.times[1]);
/*  55 */     int y1 = mapper.yv(this.prices[0]);
/*  56 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  58 */     GraphicHelper.drawBeamLine(drawingPath, x1, y1, x2, y2, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  60 */     ((Graphics2D)g).draw(drawingPath);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  69 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/*  70 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(1), getPrice(1), Double.compare(getPrice(1), getPrice(0)) >= 0);
/*     */     }
/*  72 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath()
/*     */   {
/*  77 */     if (this.path == null) {
/*  78 */       this.path = new GeneralPath();
/*     */     }
/*  80 */     return this.path;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/*  85 */     this.times[1] = mapper.tx(point.x);
/*  86 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/*  91 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/*  96 */     long timeDiff = this.times[0] - time;
/*  97 */     double priceDiff = this.prices[0] - price;
/*     */ 
/*  99 */     this.times[0] += timeDiff;
/* 100 */     this.prices[0] += priceDiff;
/* 101 */     this.times[1] += timeDiff;
/* 102 */     this.prices[1] += priceDiff;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 107 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 112 */     return "Ray Line";
/*     */   }
/*     */ 
/*     */   public RayLineChartObject clone()
/*     */   {
/* 117 */     return new RayLineChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 122 */     return "item.ray.line";
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 127 */     return arePointsValid();
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 135 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.RayLineChartObject
 * JD-Core Version:    0.6.0
 */