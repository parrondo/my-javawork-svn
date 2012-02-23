/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IRectangleChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RectangleChartObject extends AbstractFillableChartObject
/*     */   implements IRectangleChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Rectangle";
/*     */ 
/*     */   public RectangleChartObject(String key)
/*     */   {
/*  25 */     super(key, IChart.Type.RECTANGLE);
/*     */   }
/*     */ 
/*     */   public RectangleChartObject(String key, IChart.Type type) {
/*  29 */     super(key, type);
/*  30 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public RectangleChartObject() {
/*  34 */     super(null, IChart.Type.RECTANGLE);
/*  35 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public RectangleChartObject(String key, long time1, double price1, long time2, double price2) {
/*  39 */     super(key, IChart.Type.RECTANGLE);
/*  40 */     this.times[0] = time1;
/*  41 */     this.prices[0] = price1;
/*  42 */     this.times[1] = time2;
/*  43 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public RectangleChartObject(RectangleChartObject chartObject) {
/*  47 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  52 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  54 */     int time1X = mapper.xt(this.times[0]);
/*  55 */     int time2X = mapper.xt(this.times[1]);
/*  56 */     int price1Y = mapper.yv(this.prices[0]);
/*  57 */     int price2Y = mapper.yv(this.prices[1]);
/*     */ 
/*  59 */     int x = Math.min(time1X, time2X);
/*  60 */     int y = Math.min(price1Y, price2Y);
/*  61 */     int width = Math.abs(time1X - time2X);
/*  62 */     int height = Math.abs(price1Y - price2Y);
/*     */ 
/*  64 */     Rectangle rectangle = GraphicHelper.getRectangle(x, y, width, height, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  66 */     if (rectangle != null) {
/*  67 */       float fillAlpha = getFillOpacity();
/*  68 */       if (0.0F != fillAlpha)
/*     */       {
/*  70 */         g2d.setColor(getFillColor());
/*  71 */         Composite composite = g2d.getComposite();
/*  72 */         g2d.setComposite(AlphaComposite.getInstance(3, fillAlpha));
/*  73 */         g2d.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
/*     */ 
/*  76 */         g2d.setColor(getColor());
/*  77 */         g2d.setComposite(composite);
/*     */       }
/*     */ 
/*  80 */       g2d.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Rectangle drawFormattedLabel(Graphics2D g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  86 */     int time1X = mapper.xt(this.times[0]);
/*  87 */     int time2X = mapper.xt(this.times[1]);
/*  88 */     int price1Y = mapper.yv(this.prices[0]);
/*  89 */     int price2Y = mapper.yv(this.prices[1]);
/*     */ 
/*  91 */     int unitsDiff = Math.abs(time1X - time2X) / mapper.getBarWidth();
/*     */ 
/*  93 */     String priceDiff = formattersManager.getValueFormatter().formatValueDiff(Math.abs(this.prices[0] - this.prices[1]));
/*     */ 
/*  95 */     int x = Math.max(time1X, time2X) + 5;
/*  96 */     int y = Math.max(price1Y, price2Y);
/*  97 */     StringBuilder text = new StringBuilder();
/*  98 */     if (!ObjectUtils.isNullOrEmpty(getText())) {
/*  99 */       text.append(getText()).append("  ");
/*     */     }
/* 101 */     text.append(unitsDiff).append(" / ").append(priceDiff);
/* 102 */     return drawingsLabelHelper.drawLabel(g, text.toString(), x, y);
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*     */   {
/* 107 */     this.times[1] = mapper.tx(point.x);
/* 108 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 113 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 115 */     int leftX = mapper.xt(this.times[0]);
/* 116 */     int rightX = mapper.xt(this.times[1]);
/* 117 */     int upperY = mapper.yv(this.prices[0]);
/* 118 */     int bottomY = mapper.yv(this.prices[1]);
/*     */ 
/* 120 */     int width = Math.abs(leftX - rightX);
/* 121 */     int height = Math.abs(upperY - bottomY);
/* 122 */     if ((width <= 2) || (height <= 2)) {
/* 123 */       return handlerMiddlePoints;
/*     */     }
/*     */ 
/* 126 */     handlerMiddlePoints.add(new Point(leftX, upperY));
/* 127 */     handlerMiddlePoints.add(new Point(rightX, bottomY));
/* 128 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 133 */     int x1 = mapper.xt(this.times[0]);
/* 134 */     int x2 = mapper.xt(this.times[1]);
/* 135 */     int y1 = mapper.yv(this.prices[0]);
/* 136 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/* 138 */     if (x2 < x1) {
/* 139 */       int xTmp = x2;
/* 140 */       x2 = x1;
/* 141 */       x1 = xTmp;
/*     */     }
/*     */ 
/* 144 */     if (y2 < y1) {
/* 145 */       int yTmp = y2;
/* 146 */       y2 = y1;
/* 147 */       y1 = yTmp;
/*     */     }
/*     */ 
/* 150 */     boolean isOutsideRectangle = (point.x + range < x1) || (point.x - range > x2) || (point.y + range < y1) || (point.y - range > y2);
/* 151 */     boolean isInsideRectangle = (point.x - range > x1) && (point.x + range < x2) && (point.y - range > y1) && (point.y + range < y2);
/*     */ 
/* 153 */     return (!isOutsideRectangle) && (!isInsideRectangle);
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 158 */     long timeDiff = this.times[1] - this.times[0];
/* 159 */     double priceDiff = this.prices[1] - this.prices[0];
/*     */ 
/* 161 */     this.times[0] = time;
/* 162 */     this.prices[0] = price;
/* 163 */     this.times[1] = (time + timeDiff);
/* 164 */     this.prices[1] = (price + priceDiff);
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 169 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 174 */     return "Rectangle";
/*     */   }
/*     */ 
/*     */   public RectangleChartObject clone()
/*     */   {
/* 179 */     return new RectangleChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 184 */     return "item.rectangle";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 192 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.RectangleChartObject
 * JD-Core Version:    0.6.0
 */