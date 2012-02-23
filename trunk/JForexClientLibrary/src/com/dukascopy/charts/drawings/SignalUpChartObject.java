/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.ISignalUpChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SignalUpChartObject extends AbstractStickablePointsChartObject
/*     */   implements ISignalUpChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Signal Up";
/*     */   private static final int BOUNDS_OFFSET = 2;
/*     */   private static final int OFFSET_FROM_ARROW = 3;
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public SignalUpChartObject(String key)
/*     */   {
/*  31 */     super(key, IChart.Type.SIGNAL_UP);
/*  32 */     setColor(new Color(0, 128, 0));
/*     */   }
/*     */ 
/*     */   public SignalUpChartObject() {
/*  36 */     super(null, IChart.Type.SIGNAL_UP);
/*  37 */     setColor(new Color(0, 128, 0));
/*  38 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public SignalUpChartObject(String key, long time1, double price1) {
/*  42 */     super(key, IChart.Type.SIGNAL_UP);
/*  43 */     setColor(new Color(0, 128, 0));
/*  44 */     this.times[0] = time1;
/*  45 */     this.prices[0] = price1;
/*     */   }
/*     */ 
/*     */   SignalUpChartObject(SignalUpChartObject chartObject) {
/*  49 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  54 */     if (this.path == null)
/*  55 */       this.path = new GeneralPath();
/*     */     else {
/*  57 */       this.path.reset();
/*     */     }
/*     */ 
/*  60 */     int x = mapper.xt(this.times[0]);
/*  61 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/*  63 */     int stepPx = getStepPx(mapper);
/*     */ 
/*  65 */     this.path.moveTo(x, y);
/*  66 */     this.path.lineTo(x + stepPx * 2, y + stepPx * 2);
/*  67 */     this.path.lineTo(x + stepPx, y + stepPx * 2);
/*  68 */     this.path.lineTo(x + stepPx, y + stepPx * 4);
/*  69 */     this.path.lineTo(x - stepPx, y + stepPx * 4);
/*  70 */     this.path.lineTo(x - stepPx, y + stepPx * 2);
/*  71 */     this.path.lineTo(x - stepPx * 2, y + stepPx * 2);
/*  72 */     this.path.lineTo(x, y);
/*     */ 
/*  74 */     Graphics2D g2 = (Graphics2D)g;
/*  75 */     g2.fill(this.path);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager generalFormatter, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  80 */     if ((this.text == null) || (this.text.length() == 0)) {
/*  81 */       return ZERO_RECTANGLE;
/*     */     }
/*     */ 
/*  84 */     int stepPx = getStepPx(mapper);
/*     */ 
/*  86 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  87 */     String label = getAdjustedLabel(this.text);
/*  88 */     Rectangle2D stringBounds = fontMetrics.getStringBounds(label, g);
/*  89 */     int stringStartingX = (int)(mapper.xt(this.times[0]) - stringBounds.getWidth() / 2.0D);
/*  90 */     g.drawString(label, stringStartingX, mapper.yv(this.prices[0]) + stepPx * 4 + (int)stringBounds.getHeight() + 3);
/*  91 */     return new Rectangle(0, 0, (int)stringBounds.getWidth(), (int)stringBounds.getHeight());
/*     */   }
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/*  96 */     int x = mapper.xt(this.times[0]);
/*  97 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/*  99 */     int stepPx = getStepPx(mapper);
/*     */ 
/* 101 */     g.drawRect(x - stepPx * 2 - 2, y - 2, stepPx * 4 + 4, stepPx * 4 + 4);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 106 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 108 */     int x = mapper.xt(this.times[0]);
/* 109 */     int y = mapper.yv(this.prices[0]);
/* 110 */     handlerMiddlePoints.add(new Point(x, y - 2));
/*     */ 
/* 112 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 117 */     int x = mapper.xt(this.times[0]);
/* 118 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/* 120 */     int stepPx = getStepPx(mapper);
/*     */ 
/* 122 */     Rectangle2D.Double bounds = new Rectangle2D.Double(x - stepPx * 2, y, stepPx * 4, stepPx * 4);
/* 123 */     return bounds.intersects(new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   private int getStepPx(IMapper mapper) {
/* 127 */     int stepPx = mapper.getBarWidth() / 4;
/* 128 */     if (stepPx < 4) {
/* 129 */       stepPx = 4;
/*     */     }
/* 131 */     return stepPx;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 136 */     this.times[0] = mapper.tx(point.x);
/* 137 */     this.prices[0] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 142 */     this.times[0] = time;
/* 143 */     this.prices[0] = price;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 148 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 153 */     return "Signal Up";
/*     */   }
/*     */ 
/*     */   public SignalUpChartObject clone()
/*     */   {
/* 158 */     return new SignalUpChartObject(this);
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 166 */     return true;
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 171 */     return "item.signal.up";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.SignalUpChartObject
 * JD-Core Version:    0.6.0
 */