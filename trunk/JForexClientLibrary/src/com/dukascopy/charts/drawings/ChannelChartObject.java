/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IChannelChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ChannelChartObject extends AbstractStickablePointsChartObject
/*     */   implements IChannelChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Channel";
/*     */   transient GeneralPath path;
/*  27 */   private long deltaTime = 0L;
/*  28 */   private double deltaPrice = -1.0D;
/*     */ 
/*     */   public ChannelChartObject(String key) {
/*  31 */     super(key, IChart.Type.CHANNEL);
/*     */   }
/*     */ 
/*     */   public ChannelChartObject() {
/*  35 */     super(null, IChart.Type.CHANNEL);
/*  36 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public ChannelChartObject(ChannelChartObject chartObject) {
/*  40 */     super(chartObject);
/*     */ 
/*  42 */     this.deltaTime = chartObject.deltaTime;
/*  43 */     this.deltaPrice = chartObject.deltaPrice;
/*     */   }
/*     */ 
/*     */   public ChannelChartObject(String key, long time1, double price1, long time2, double price2, long time3, double price3) {
/*  47 */     super(key, IChart.Type.CHANNEL);
/*  48 */     this.times[0] = time1;
/*  49 */     this.prices[0] = price1;
/*  50 */     this.times[1] = time2;
/*  51 */     this.prices[1] = price2;
/*  52 */     this.times[2] = time3;
/*  53 */     this.prices[2] = price3;
/*     */ 
/*  55 */     this.deltaTime = (this.times[2] - this.times[1]);
/*  56 */     this.deltaPrice = (this.prices[2] - this.prices[1]);
/*     */   }
/*     */ 
/*     */   private void updateThridElements()
/*     */   {
/*  63 */     this.times[2] = (this.times[1] + this.deltaTime);
/*  64 */     this.prices[2] = (this.prices[1] + this.deltaPrice);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  69 */     getPath().reset();
/*     */ 
/*  71 */     renderMainLine(this.path, mapper);
/*  72 */     renderParallelLine(this.path, mapper);
/*     */ 
/*  74 */     ((Graphics2D)g).draw(this.path);
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath() {
/*  78 */     if (this.path == null) {
/*  79 */       this.path = new GeneralPath();
/*     */     }
/*     */ 
/*  82 */     return this.path;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/*  87 */     double newPrice = mapper.vy(point.y);
/*     */ 
/*  89 */     if (this.selectedHandlerIndex == 2) {
/*  90 */       this.deltaTime = getDeltaTime(mapper, point);
/*  91 */       this.deltaPrice += newPrice - mapper.vy(prevPoint.y);
/*     */ 
/*  93 */       updateThridElements();
/*     */     } else {
/*  95 */       super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */     }
/*     */   }
/*     */ 
/*     */   private long getDeltaTime(IMapper mapper, Point point) {
/* 100 */     return mapper.getInterval() * (point.x - mapper.xt(this.times[1])) / mapper.getBarWidth();
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/* 105 */     switch (this.currentPoint) {
/*     */     case 0:
/* 107 */       break;
/*     */     case 1:
/* 109 */       this.times[1] = mapper.tx(point.x);
/* 110 */       this.prices[1] = mapper.vy(point.y);
/*     */ 
/* 112 */       updateThridElements();
/* 113 */       break;
/*     */     case 2:
/* 115 */       double price3 = mapper.vy(point.y);
/* 116 */       this.deltaTime = getDeltaTime(mapper, point);
/* 117 */       this.deltaPrice = (price3 - this.prices[1]);
/*     */ 
/* 119 */       updateThridElements();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean addNewPoint(Point point, IMapper mapper)
/*     */   {
/* 126 */     switch (this.currentPoint) {
/*     */     case 0:
/* 128 */       super.addNewPoint(point, mapper);
/* 129 */       return false;
/*     */     case 1:
/* 131 */       super.addNewPoint(point, mapper);
/* 132 */       this.currentPoint = 2;
/* 133 */       return false;
/*     */     case 2:
/* 135 */       double price3 = mapper.vy(point.y);
/*     */ 
/* 137 */       this.deltaTime = getDeltaTime(mapper, point);
/* 138 */       this.deltaPrice = (price3 - this.prices[1]);
/*     */ 
/* 140 */       updateThridElements();
/*     */     }
/*     */ 
/* 143 */     return true;
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 148 */     List handlerMiddlePoints = super.getHandlerMiddlePoints(mapper);
/*     */ 
/* 150 */     if (2 <= this.currentPoint) {
/* 151 */       handlerMiddlePoints.add(new Point(mapper.xt(this.times[1]) + getDeltaWidth(mapper), mapper.yv(this.prices[1] + this.deltaPrice)));
/*     */     }
/*     */ 
/* 159 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   private int getDeltaWidth(IMapper dataMapper) {
/* 163 */     return (int)(dataMapper.getBarWidth() * this.deltaTime / dataMapper.getInterval());
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 168 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Float(point.x - range / 2, point.y - range / 2, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/* 176 */     updateSelectedHandler(3, point, mapper, range);
/*     */   }
/*     */ 
/*     */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*     */   {
/* 181 */     if (handlerIndex == 2) {
/* 182 */       return isParallelLineSelected(point, mapper, range);
/*     */     }
/*     */ 
/* 185 */     return super.isHandlerSelected(handlerIndex, point, mapper, range);
/*     */   }
/*     */ 
/*     */   boolean isParallelLineSelected(Point point, IMapper dataMapper, int range) {
/* 189 */     if (!ValuePoint.isValid(this.deltaTime, this.deltaPrice)) {
/* 190 */       return true;
/*     */     }
/*     */ 
/* 193 */     getPath().reset();
/* 194 */     renderParallelLine(this.path, dataMapper);
/*     */ 
/* 196 */     Rectangle2D.Double rectangle = new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2);
/*     */ 
/* 198 */     return GraphicHelper.intersects(this.path, rectangle);
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 203 */     long timeDiff = this.times[0] - time;
/* 204 */     double priceDiff = this.prices[0] - price;
/*     */ 
/* 206 */     this.times[0] += timeDiff;
/* 207 */     this.times[1] += timeDiff;
/* 208 */     this.prices[0] += priceDiff;
/* 209 */     this.prices[1] += priceDiff;
/*     */ 
/* 211 */     updateThridElements();
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 216 */     return 2;
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 224 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 225 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/* 227 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   void renderMainLine(GeneralPath path, IMapper mapper)
/*     */   {
/* 236 */     if ((!isValidPoint(0)) || (!isValidPoint(1))) {
/* 237 */       return;
/*     */     }
/*     */ 
/* 240 */     int time1X = mapper.xt(this.times[0]);
/* 241 */     int time2X = mapper.xt(this.times[1]);
/* 242 */     int price1Y = mapper.yv(this.prices[0]);
/* 243 */     int price2Y = mapper.yv(this.prices[1]);
/*     */ 
/* 245 */     GraphicHelper.drawInfiniteLine(path, time1X, price1Y, time2X, price2Y, mapper.getWidth(), mapper.getHeight());
/*     */   }
/*     */ 
/*     */   void renderParallelLine(GeneralPath path, IMapper mapper) {
/* 249 */     if (!ValuePoint.isValid(this.deltaTime, this.deltaPrice)) {
/* 250 */       return;
/*     */     }
/* 252 */     int deltaX = getDeltaWidth(mapper);
/* 253 */     int time1X = mapper.xt(this.times[0]) + deltaX;
/* 254 */     int time3X = mapper.xt(this.times[1]) + deltaX;
/*     */ 
/* 256 */     int price1Y = mapper.yv(this.prices[0]);
/* 257 */     int price2Y = mapper.yv(this.prices[1]);
/* 258 */     int price3Y = mapper.yv(this.prices[1] + this.deltaPrice);
/*     */ 
/* 260 */     int deltaY = price3Y - price2Y;
/*     */ 
/* 262 */     GraphicHelper.drawInfiniteLine(path, time1X, price1Y + deltaY, time3X, price3Y, mapper.getWidth(), mapper.getHeight());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 267 */     return "Channel";
/*     */   }
/*     */ 
/*     */   public ChannelChartObject clone()
/*     */   {
/* 272 */     return new ChannelChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 277 */     return "item.channel.lines";
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 282 */     return arePointsValid();
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 290 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.ChannelChartObject
 * JD-Core Version:    0.6.0
 */