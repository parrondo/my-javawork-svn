/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TimeRangeChartObject extends RectangleChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 9191527882689013627L;
/*     */   private static final String CHART_OBJECT_NAME = "TimeRange";
/*     */ 
/*     */   public TimeRangeChartObject()
/*     */   {
/*  30 */     super(null, IChart.Type.TIMERANGE);
/*     */   }
/*     */ 
/*     */   public TimeRangeChartObject(String key, long timeFromMs, long timeToMs) {
/*  34 */     super(key, IChart.Type.TIMERANGE);
/*  35 */     setTime(0, timeFromMs);
/*  36 */     setTime(1, timeToMs);
/*  37 */     setPrice(0, 0.0D);
/*  38 */     setPrice(1, 0.0D);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  43 */     int time1X = mapper.xt(this.times[0]);
/*  44 */     int time2X = mapper.xt(this.times[1]);
/*     */ 
/*  46 */     int x = Math.min(time1X, time2X);
/*  47 */     int width = Math.abs(time1X - time2X);
/*     */ 
/*  49 */     Rectangle rectangle = GraphicHelper.getRectangle(x, 0.0D, width, mapper.getHeight(), mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  51 */     if (rectangle != null)
/*     */     {
/*  54 */       Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  56 */       g2d.setColor(getFillColor());
/*  57 */       Composite composite = g2d.getComposite();
/*  58 */       g2d.setComposite(AlphaComposite.getInstance(3, getFillOpacity()));
/*  59 */       g2d.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
/*     */ 
/*  62 */       g2d.setColor(getColor());
/*  63 */       g2d.setComposite(composite);
/*     */ 
/*  65 */       g2d.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Rectangle drawFormattedLabel(Graphics2D g, IMapper mapper, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  71 */     int time1X = mapper.xt(this.times[0]);
/*  72 */     int time2X = mapper.xt(this.times[1]);
/*     */ 
/*  74 */     int unitsDiff = Math.abs(time1X - time2X) / mapper.getBarWidth();
/*     */ 
/*  76 */     int x = Math.max(time1X, time2X);
/*     */ 
/*  78 */     long timeFrom = Math.min(this.times[0], this.times[1]);
/*  79 */     long timeTo = Math.max(this.times[0], this.times[1]);
/*     */ 
/*  81 */     String formattedTimeFrom = formattersManager.getDateFormatter().formatTimeMarkerTime(timeFrom);
/*  82 */     String formattedTimeTo = formattersManager.getDateFormatter().formatTimeMarkerTime(timeTo);
/*     */ 
/*  84 */     String text = formattedTimeFrom + "/" + formattedTimeTo + ": " + unitsDiff;
/*     */ 
/*  86 */     FontMetrics fm = g.getFontMetrics();
/*  87 */     int stringWidth = fm.stringWidth(text);
/*     */ 
/*  89 */     float lineWidth = getLineWidth();
/*     */ 
/*  91 */     if (x + 3 + lineWidth + stringWidth >= mapper.getWidth())
/*  92 */       x = (int)(x - (3.0F + lineWidth + stringWidth));
/*     */     else {
/*  94 */       x = (int)(x + (3.0F + lineWidth));
/*     */     }
/*     */ 
/*  98 */     return drawingsLabelHelper.drawLabelSmart(g, mapper, this, text, timeTo);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 103 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 105 */     int leftX = mapper.xt(this.times[0]);
/* 106 */     int rightX = mapper.xt(this.times[1]);
/* 107 */     int height = mapper.getHeight();
/*     */ 
/* 109 */     handlerMiddlePoints.add(new Point(leftX, StratUtils.div2(height)));
/* 110 */     handlerMiddlePoints.add(new Point(rightX, StratUtils.div2(height)));
/* 111 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 116 */     int x1 = mapper.xt(this.times[0]);
/* 117 */     int x2 = mapper.xt(this.times[1]);
/*     */ 
/* 119 */     if (x2 < x1) {
/* 120 */       int xTmp = x2;
/* 121 */       x2 = x1;
/* 122 */       x1 = xTmp;
/*     */     }
/*     */ 
/* 125 */     boolean isOutsideRectangle = (point.x + range < x1) || (point.x - range > x2);
/* 126 */     boolean isInsideRectangle = (point.x - range > x1) && (point.x + range < x2);
/*     */ 
/* 128 */     return (!isOutsideRectangle) && (!isInsideRectangle);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 133 */     return "TimeRange";
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 138 */     long timeDiff = this.times[1] - this.times[0];
/*     */ 
/* 140 */     this.times[0] = time;
/* 141 */     this.times[1] = (time + timeDiff);
/*     */   }
/*     */ 
/*     */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*     */   {
/* 146 */     if (!isValidPoint(handlerIndex)) {
/* 147 */       return false;
/*     */     }
/* 149 */     int timeX = mapper.xt(this.times[handlerIndex]);
/* 150 */     int priceY = StratUtils.div2(mapper.getHeight());
/* 151 */     return isInRange(timeX, priceY, point, range);
/*     */   }
/*     */ 
/*     */   public boolean isGlobal()
/*     */   {
/* 156 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 161 */     return false;
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 166 */     return "item.timerange";
/*     */   }
/*     */ 
/*     */   public RectangleChartObject clone()
/*     */   {
/* 171 */     return new TimeRangeChartObject(null, this.times[0], this.times[1]);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.TimeRangeChartObject
 * JD-Core Version:    0.6.0
 */