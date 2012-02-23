/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IEllipseChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.util.List;
/*     */ 
/*     */ public class EllipseChartObject extends AbstractFillableChartObject
/*     */   implements IEllipseChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Ellipse";
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public EllipseChartObject(String key)
/*     */   {
/*  28 */     super(key, IChart.Type.ELLIPSE);
/*     */   }
/*     */ 
/*     */   public EllipseChartObject() {
/*  32 */     super(null, IChart.Type.ELLIPSE);
/*  33 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public EllipseChartObject(String key, long time1, double price1, long time2, double price2) {
/*  37 */     super(key, IChart.Type.ELLIPSE);
/*  38 */     this.times[0] = time1;
/*  39 */     this.prices[0] = price1;
/*  40 */     this.times[1] = time2;
/*  41 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public EllipseChartObject(EllipseChartObject chartObject) {
/*  45 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  50 */     getPath().reset();
/*     */ 
/*  52 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  54 */     int x1 = mapper.xt(this.times[0]);
/*  55 */     int x2 = mapper.xt(this.times[1]);
/*  56 */     int y1 = mapper.yv(this.prices[0]);
/*  57 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  59 */     int x = Math.min(x1, x2);
/*  60 */     int y = Math.min(y1, y2);
/*  61 */     int width = Math.abs(x2 - x1);
/*  62 */     int height = Math.abs(y2 - y1);
/*     */ 
/*  64 */     GraphicHelper.drawEllipse(getPath(), x, y, width, height, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  66 */     float fillAlpha = getFillOpacity();
/*  67 */     if (0.0F != fillAlpha)
/*     */     {
/*  69 */       g2d.setColor(getFillColor());
/*  70 */       Composite composite = g2d.getComposite();
/*  71 */       g2d.setComposite(AlphaComposite.getInstance(3, fillAlpha));
/*  72 */       g2d.fill(getPath());
/*     */ 
/*  75 */       g2d.setColor(getColor());
/*  76 */       g2d.setComposite(composite);
/*     */     }
/*     */ 
/*  79 */     g2d.draw(getPath());
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  87 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/*  88 */       Point verticalHandler = calculateHandlerPoint(0, mapper);
/*  89 */       Point horizontalHandler = calculateHandlerPoint(1, mapper);
/*  90 */       long time = mapper.tx(verticalHandler.x);
/*  91 */       double price1 = mapper.vy(verticalHandler.y);
/*  92 */       double price2 = mapper.vy(horizontalHandler.y);
/*     */ 
/*  94 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, time, price1, Double.compare(price1, price2) >= 0);
/*     */     }
/*  96 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath()
/*     */   {
/* 101 */     if (this.path == null) {
/* 102 */       this.path = new GeneralPath();
/*     */     }
/*     */ 
/* 105 */     return this.path;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/* 110 */     switch (this.currentPoint) {
/*     */     case 0:
/* 112 */       break;
/*     */     case 1:
/* 114 */       this.times[1] = mapper.tx(point.x);
/* 115 */       this.prices[1] = mapper.vy(point.y);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 122 */     long newTime = mapper.tx(point.x);
/* 123 */     double newPrice = mapper.vy(point.y);
/*     */ 
/* 125 */     if (this.selectedHandlerIndex == 0) {
/* 126 */       this.prices[1] = newPrice;
/*     */     }
/* 128 */     else if (this.selectedHandlerIndex == 1)
/* 129 */       this.times[1] = newTime;
/*     */     else
/* 131 */       super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 137 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/* 138 */     handlerMiddlePoints.add(calculateHandlerPoint(0, mapper));
/* 139 */     handlerMiddlePoints.add(calculateHandlerPoint(1, mapper));
/* 140 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   private Point calculateHandlerPoint(int handlerIndex, IMapper mapper) {
/* 144 */     int x1 = mapper.xt(this.times[0]);
/* 145 */     int x2 = mapper.xt(this.times[1]);
/* 146 */     int y1 = mapper.yv(this.prices[0]);
/* 147 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/* 149 */     switch (handlerIndex) {
/*     */     case 0:
/* 151 */       return new Point(x1 + (x2 - x1) / 2, y2);
/*     */     case 1:
/* 153 */       return new Point(x2, y1 + (y2 - y1) / 2);
/*     */     }
/* 155 */     throw new IllegalArgumentException("Handler index is out of bounds : " + handlerIndex);
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 161 */     if (!isDrawable(mapper)) {
/* 162 */       return false;
/*     */     }
/*     */ 
/* 165 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*     */   {
/* 170 */     Point handlerPoint = null;
/* 171 */     switch (handlerIndex) {
/*     */     case 0:
/* 173 */       handlerPoint = calculateHandlerPoint(0, mapper);
/* 174 */       break;
/*     */     case 1:
/* 177 */       handlerPoint = calculateHandlerPoint(1, mapper);
/* 178 */       break;
/*     */     default:
/* 182 */       throw new IllegalArgumentException("Handler index is out of range : " + handlerIndex);
/*     */     }
/*     */ 
/* 185 */     return isInRange(handlerPoint.x, handlerPoint.y, point, range);
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 190 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 195 */     return "Ellipse";
/*     */   }
/*     */ 
/*     */   public EllipseChartObject clone()
/*     */   {
/* 200 */     return new EllipseChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 205 */     return "item.ellipse";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 213 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.EllipseChartObject
 * JD-Core Version:    0.6.0
 */