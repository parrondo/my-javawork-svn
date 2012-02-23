/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IFiboArcChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.Arc2D.Float;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Line2D;
/*     */ import java.awt.geom.Line2D.Float;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ 
/*     */ public class FiboArcChartObject extends AbstractStickablePointsChartObject
/*     */   implements IFiboArcChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Fibo Arc";
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public FiboArcChartObject(String key)
/*     */   {
/*  27 */     super(key, IChart.Type.FIBOARC);
/*     */   }
/*     */ 
/*     */   public FiboArcChartObject() {
/*  31 */     super(null, IChart.Type.FIBOARC);
/*  32 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public FiboArcChartObject(String key, long time1, double price1, long time2, double price2) {
/*  36 */     super(key, IChart.Type.FIBOARC);
/*  37 */     this.times[0] = time1;
/*  38 */     this.prices[0] = price1;
/*  39 */     this.times[1] = time2;
/*  40 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public FiboArcChartObject(FiboArcChartObject chartObject) {
/*  44 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  49 */     Arc2D.Float[] arcs = createArcs(mapper);
/*     */ 
/*  51 */     getPath().reset();
/*     */ 
/*  53 */     for (Arc2D.Float arc : arcs) {
/*  54 */       drawArc(arc, mapper);
/*     */     }
/*     */ 
/*  57 */     Graphics2D g2 = (Graphics2D)g;
/*  58 */     g2.draw(getPath());
/*     */   }
/*     */ 
/*     */   private Arc2D.Float[] createArcs(IMapper mapper) {
/*  62 */     Arc2D.Float[] arcs = new Arc2D.Float[4];
/*     */ 
/*  64 */     int middleX = mapper.xt(this.times[0]);
/*  65 */     int outerX = mapper.xt(this.times[1]);
/*  66 */     int middleY = mapper.yv(this.prices[0]);
/*  67 */     int outerY = mapper.yv(this.prices[1]);
/*     */ 
/*  69 */     float radius = (float)Math.sqrt(Math.pow(Math.abs(middleX - outerX), 2.0D) + Math.pow(Math.abs(middleY - outerY), 2.0D));
/*     */ 
/*  71 */     arcs[0] = new Arc2D.Float(outerX - radius * 0.382F, outerY - radius * 0.382F, radius * 0.382F * 2.0F, radius * 0.382F * 2.0F, 0.0F, 360.0F, 0);
/*  72 */     arcs[1] = new Arc2D.Float(outerX - radius * 0.5F, outerY - radius * 0.5F, radius * 0.5F * 2.0F, radius * 0.5F * 2.0F, 0.0F, 360.0F, 0);
/*  73 */     arcs[2] = new Arc2D.Float(outerX - radius * 0.618F, outerY - radius * 0.618F, radius * 0.618F * 2.0F, radius * 0.618F * 2.0F, 0.0F, 360.0F, 0);
/*  74 */     arcs[3] = new Arc2D.Float(outerX - radius * 0.763F, outerY - radius * 0.763F, radius * 0.763F * 2.0F, radius * 0.763F * 2.0F, 0.0F, 360.0F, 0);
/*     */ 
/*  76 */     return arcs;
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  84 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/*  85 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(1), getPrice(1), Double.compare(getPrice(1), getPrice(0)) >= 0);
/*     */     }
/*  87 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/*  94 */     int x1 = mapper.xt(this.times[0]);
/*  95 */     int x2 = mapper.xt(this.times[1]);
/*  96 */     int y1 = mapper.yv(this.prices[0]);
/*  97 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  99 */     if (!isValidTime(0)) {
/* 100 */       x2 = x1;
/*     */     }
/*     */ 
/* 103 */     if (!isValidPrice(0)) {
/* 104 */       y2 = y1;
/*     */     }
/*     */ 
/* 107 */     GraphicHelper.drawSegmentLine(g, x1, y1, x2, y2, mapper.getWidth(), mapper.getHeight());
/*     */   }
/*     */ 
/*     */   private void drawArc(Arc2D.Float arc, IMapper mapper) {
/* 111 */     GraphicHelper.drawShape(getPath(), arc, mapper.getWidth(), mapper.getHeight());
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*     */   {
/* 121 */     if ((!isValidPoint(0)) && (this.selectedHandlerIndex == 0)) {
/* 122 */       return;
/*     */     }
/*     */ 
/* 125 */     this.times[1] = mapper.tx(point.x);
/* 126 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 131 */     int x1 = mapper.xt(this.times[1]);
/* 132 */     int x2 = mapper.xt(this.times[0]);
/* 133 */     int y1 = mapper.yv(this.prices[1]);
/* 134 */     int y2 = mapper.yv(this.prices[0]);
/*     */ 
/* 136 */     Rectangle2D.Float intersectionRectForArcs = new Rectangle2D.Float(point.x, point.y, range / 2, range / 2);
/* 137 */     Rectangle2D.Float intersectionRectForHandler = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/*     */ 
/* 139 */     Line2D handlerDiagonalLine = new Line2D.Float(x1, y1, x2, y2);
/* 140 */     return (GraphicHelper.intersects(getPath(), intersectionRectForArcs)) || (handlerDiagonalLine.intersects(intersectionRectForHandler));
/*     */   }
/*     */ 
/*     */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*     */   {
/* 145 */     Rectangle2D.Float pointerRect = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 146 */     int middleX = mapper.xt(this.times[handlerIndex]);
/* 147 */     int middleY = mapper.yv(this.prices[handlerIndex]);
/* 148 */     Rectangle2D.Float handlerRect = new Rectangle2D.Float(middleX - range, middleY - range, range * 2, range * 2);
/* 149 */     return handlerRect.intersects(pointerRect);
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 154 */     return 2;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 159 */     return "Fibo Arc";
/*     */   }
/*     */ 
/*     */   public FiboArcChartObject clone()
/*     */   {
/* 164 */     return new FiboArcChartObject(this);
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath() {
/* 168 */     if (this.path == null) {
/* 169 */       this.path = new GeneralPath();
/*     */     }
/* 171 */     return this.path;
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 176 */     return arePointsValid();
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 181 */     return "item.fibonacci.fan.arcs";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 189 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.FiboArcChartObject
 * JD-Core Version:    0.6.0
 */