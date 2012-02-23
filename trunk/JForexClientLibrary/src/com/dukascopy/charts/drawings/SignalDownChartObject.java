/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.ISignalDownChartObject;
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
/*     */ public class SignalDownChartObject extends AbstractStickablePointsChartObject
/*     */   implements ISignalDownChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Signal Down";
/*     */   private static final int BOUNDS_OFFSET = 2;
/*     */   private static final int OFFSET_FROM_ARROW = 3;
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public SignalDownChartObject(String key)
/*     */   {
/*  31 */     super(key, IChart.Type.SIGNAL_DOWN);
/*  32 */     setColor(Color.RED);
/*     */   }
/*     */ 
/*     */   public SignalDownChartObject() {
/*  36 */     super(null, IChart.Type.SIGNAL_DOWN);
/*  37 */     setColor(Color.RED);
/*  38 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public SignalDownChartObject(String key, long time1, double price1) {
/*  42 */     super(key, IChart.Type.SIGNAL_DOWN);
/*  43 */     setColor(Color.RED);
/*  44 */     this.times[0] = time1;
/*  45 */     this.prices[0] = price1;
/*     */   }
/*     */ 
/*     */   public SignalDownChartObject(SignalDownChartObject chartObject) {
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
/*  66 */     this.path.lineTo(x - stepPx * 2, y - stepPx * 2);
/*  67 */     this.path.lineTo(x - stepPx, y - stepPx * 2);
/*  68 */     this.path.lineTo(x - stepPx, y - stepPx * 4);
/*  69 */     this.path.lineTo(x + stepPx, y - stepPx * 4);
/*  70 */     this.path.lineTo(x + stepPx, y - stepPx * 2);
/*  71 */     this.path.lineTo(x + stepPx * 2, y - stepPx * 2);
/*  72 */     this.path.lineTo(x, y);
/*     */ 
/*  74 */     Graphics2D g2 = (Graphics2D)g;
/*  75 */     g2.fill(this.path);
/*     */   }
/*     */ 
/*     */   private int getStepPx(IMapper mapper) {
/*  79 */     int stepPx = mapper.getBarWidth() / 4;
/*  80 */     if (stepPx < 4) {
/*  81 */       stepPx = 4;
/*     */     }
/*  83 */     return stepPx;
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager generalFormatter, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  88 */     if ((this.text == null) || (this.text.length() == 0)) {
/*  89 */       return ZERO_RECTANGLE;
/*     */     }
/*     */ 
/*  92 */     int stepPx = getStepPx(mapper);
/*     */ 
/*  94 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  95 */     String label = getAdjustedLabel(this.text);
/*  96 */     Rectangle2D stringBounds = fontMetrics.getStringBounds(label, g);
/*  97 */     int stringStartX = (int)(mapper.xt(this.times[0]) - stringBounds.getWidth() / 2.0D);
/*  98 */     g.drawString(label, stringStartX, mapper.yv(this.prices[0]) - stepPx * 4 - 2 - 3);
/*  99 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/* 104 */     int x = mapper.xt(this.times[0]);
/* 105 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/* 107 */     int stepPx = getStepPx(mapper);
/*     */ 
/* 109 */     int leftUpperX = x - stepPx * 2 - 2;
/* 110 */     int leftUpperY = y - stepPx * 4 - 2;
/* 111 */     int width = stepPx * 4 + 4;
/* 112 */     int height = stepPx * 4 + 4;
/* 113 */     g.drawRect(leftUpperX, leftUpperY, width, height);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 118 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 120 */     int x = mapper.xt(this.times[0]);
/* 121 */     int y = mapper.yv(this.prices[0]);
/* 122 */     handlerMiddlePoints.add(new Point(x, y + 2));
/*     */ 
/* 124 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 129 */     int x = mapper.xt(this.times[0]);
/* 130 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/* 132 */     int stepPx = getStepPx(mapper);
/*     */ 
/* 134 */     Rectangle2D.Double bounds = new Rectangle2D.Double(x - stepPx * 2, y - stepPx * 4, stepPx * 4, stepPx * 4);
/* 135 */     return bounds.intersects(new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 140 */     this.times[0] = mapper.tx(point.x);
/* 141 */     this.prices[0] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 146 */     this.times[0] = time;
/* 147 */     this.prices[0] = price;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 152 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 157 */     return "Signal Down";
/*     */   }
/*     */ 
/*     */   public SignalDownChartObject clone()
/*     */   {
/* 162 */     return new SignalDownChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 167 */     return "item.signal.down";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 175 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.SignalDownChartObject
 * JD-Core Version:    0.6.0
 */