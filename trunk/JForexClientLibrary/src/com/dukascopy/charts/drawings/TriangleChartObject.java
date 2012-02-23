/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.ITriangleChartObject;
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
/*     */ 
/*     */ public class TriangleChartObject extends AbstractFillableChartObject
/*     */   implements ITriangleChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Triangle";
/*     */   transient GeneralPath path;
/*     */ 
/*     */   public TriangleChartObject(String key)
/*     */   {
/*  27 */     super(key, IChart.Type.TRIANGLE);
/*     */   }
/*     */ 
/*     */   public TriangleChartObject() {
/*  31 */     super(null, IChart.Type.TRIANGLE);
/*  32 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public TriangleChartObject(String key, long time1, double price1, long time2, double price2, long time3, double price3) {
/*  36 */     super(key, IChart.Type.TRIANGLE);
/*  37 */     this.times[0] = time1;
/*  38 */     this.prices[0] = price1;
/*  39 */     this.times[1] = time2;
/*  40 */     this.prices[1] = price2;
/*  41 */     this.times[2] = time3;
/*  42 */     this.prices[2] = price3;
/*     */   }
/*     */ 
/*     */   public TriangleChartObject(TriangleChartObject chartObject) {
/*  46 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  51 */     return (isValidPoint(0)) && (isValidPoint(1));
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  56 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  58 */     int x1 = mapper.xt(this.times[0]);
/*  59 */     int x2 = mapper.xt(this.times[1]);
/*  60 */     int y1 = mapper.yv(this.prices[0]);
/*  61 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  63 */     GeneralPath drawingPath = getPath();
/*  64 */     drawingPath.reset();
/*     */ 
/*  66 */     if (isValidPoint(2)) {
/*  67 */       int x3 = mapper.xt(this.times[2]);
/*  68 */       int y3 = mapper.yv(this.prices[2]);
/*  69 */       GraphicHelper.drawTriangle(drawingPath, x1, y1, x2, y2, x3, y3, mapper.getWidth(), mapper.getHeight());
/*     */ 
/*  73 */       float alpha = getFillOpacity();
/*  74 */       if (alpha != 0.0F)
/*     */       {
/*  76 */         g2d.setColor(getFillColor());
/*  77 */         Composite composite = g2d.getComposite();
/*  78 */         g2d.setComposite(AlphaComposite.getInstance(3, alpha));
/*  79 */         g2d.fill(drawingPath);
/*     */ 
/*  82 */         g2d.setColor(getColor());
/*  83 */         g2d.setComposite(composite);
/*     */       }
/*     */     }
/*     */     else {
/*  87 */       drawingPath.moveTo(x1, y1);
/*  88 */       drawingPath.lineTo(x2, y2);
/*     */     }
/*     */ 
/*  91 */     g2d.draw(drawingPath);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  99 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 100 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(1), getPrice(1), Double.compare(getPrice(1), getPrice(2)) >= 0);
/*     */     }
/* 102 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath()
/*     */   {
/* 108 */     if (this.path == null) {
/* 109 */       this.path = new GeneralPath();
/*     */     }
/* 111 */     return this.path;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/* 116 */     switch (this.currentPoint) {
/*     */     case 0:
/* 118 */       break;
/*     */     case 1:
/* 120 */       this.times[1] = mapper.tx(point.x);
/* 121 */       this.prices[1] = mapper.vy(point.y);
/* 122 */       break;
/*     */     case 2:
/* 124 */       this.times[2] = mapper.tx(point.x);
/* 125 */       this.prices[2] = mapper.vy(point.y);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 132 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Double(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 140 */     return 3;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 145 */     return "Triangle";
/*     */   }
/*     */ 
/*     */   public TriangleChartObject clone()
/*     */   {
/* 150 */     return new TriangleChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 155 */     return "item.triangle";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 163 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.TriangleChartObject
 * JD-Core Version:    0.6.0
 */