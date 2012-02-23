/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.ILabelChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Double;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LabelChartObject extends AbstractStickablePointsChartObject
/*     */   implements ILabelChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Label";
/*     */   private static final int BORDER_OFFSET = 2;
/*     */   transient Rectangle2D stringBounds;
/*     */   transient Rectangle2D stringBorder;
/*     */ 
/*     */   public LabelChartObject()
/*     */   {
/*  26 */     super(null, IChart.Type.LABEL);
/*  27 */     setUnderEdit(true);
/*  28 */     setText("please input some text");
/*     */   }
/*     */ 
/*     */   public LabelChartObject(String key) {
/*  32 */     super(key, IChart.Type.LABEL);
/*     */   }
/*     */ 
/*     */   public LabelChartObject(String key, long time1, double price1) {
/*  36 */     super(key, IChart.Type.LABEL);
/*  37 */     this.times[0] = time1;
/*  38 */     this.prices[0] = price1;
/*     */   }
/*     */ 
/*     */   public LabelChartObject(LabelChartObject chartObject) {
/*  42 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  47 */     int bottomLeftX = mapper.xt(this.times[0]);
/*  48 */     int bottomLeftY = mapper.yv(this.prices[0]);
/*     */ 
/*  50 */     Graphics2D g2 = (Graphics2D)g;
/*  51 */     g2.drawString(getText(), bottomLeftX, bottomLeftY);
/*     */ 
/*  53 */     this.stringBounds = g2.getFontMetrics().getStringBounds(getText(), g);
/*     */   }
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/*  58 */     if (this.stringBounds == null) {
/*  59 */       return;
/*     */     }
/*     */ 
/*  62 */     if ((!isHighlighted()) && (!isHandlerSelected())) {
/*  63 */       return;
/*     */     }
/*     */ 
/*  66 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/*  68 */     int bottomLeftX = mapper.xt(this.times[0]);
/*  69 */     int bottomLeftY = mapper.yv(this.prices[0]);
/*  70 */     this.stringBorder = new Rectangle2D.Double(bottomLeftX - 2, bottomLeftY - this.stringBounds.getHeight(), this.stringBounds.getWidth() + 4.0D, this.stringBounds.getHeight() + 4.0D);
/*     */ 
/*  77 */     g2.draw(this.stringBorder);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/*  82 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/*  84 */     if (this.stringBounds == null) {
/*  85 */       return handlerMiddlePoints;
/*     */     }
/*     */ 
/*  88 */     int x = mapper.xt(this.times[0]);
/*  89 */     int y = mapper.yv(this.prices[0]);
/*  90 */     handlerMiddlePoints.add(new Point(x - 2, (int)(y - this.stringBounds.getHeight())));
/*  91 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/*  96 */     if (this.stringBounds == null) {
/*  97 */       return false;
/*     */     }
/*  99 */     int time1X = mapper.xt(this.times[0]);
/* 100 */     int price1Y = mapper.yv(this.prices[0]);
/* 101 */     Rectangle2D.Double rectangle = new Rectangle2D.Double(time1X, price1Y - this.stringBounds.getHeight(), this.stringBounds.getWidth(), this.stringBounds.getHeight());
/* 102 */     return rectangle.intersects(point.x - range, point.y - range, range * 2, range * 2);
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 112 */     this.times[0] = time;
/* 113 */     this.prices[0] = price;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 118 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 123 */     return "Label";
/*     */   }
/*     */ 
/*     */   public LabelChartObject clone()
/*     */   {
/* 128 */     return new LabelChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 133 */     return "label";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.LabelChartObject
 * JD-Core Version:    0.6.0
 */