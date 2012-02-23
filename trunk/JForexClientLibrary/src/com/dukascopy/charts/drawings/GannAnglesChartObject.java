/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.drawings.IGannAnglesChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Line2D.Double;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.util.List;
/*     */ 
/*     */ public class GannAnglesChartObject extends AbstractStickablePointsChartObject
/*     */   implements IGannAnglesChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int LABEL_OFFSET = 3;
/*  30 */   private double pipsPerBar = 1.0D;
/*     */   private transient GeneralPath path;
/*     */   private transient Line2D.Double mainLine;
/*  35 */   private transient boolean mainLineSelected = false;
/*     */   private transient Point middlePoint;
/*     */ 
/*     */   public GannAnglesChartObject()
/*     */   {
/*  40 */     this((String)null);
/*     */   }
/*     */ 
/*     */   public GannAnglesChartObject(String key) {
/*  44 */     super(key, IChart.Type.GANNFAN);
/*     */ 
/*  46 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public GannAnglesChartObject(GannAnglesChartObject chartObject) {
/*  50 */     super(chartObject);
/*     */ 
/*  52 */     this.pipsPerBar = chartObject.pipsPerBar;
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath()
/*     */   {
/*  57 */     if (this.path == null) {
/*  58 */       this.path = new GeneralPath();
/*     */     }
/*  60 */     return this.path;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/*  66 */     return 1;
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/*  71 */     super.updateSelectedHandler(point, mapper, range);
/*     */ 
/*  73 */     if (-1 == this.selectedHandlerIndex) {
/*  74 */       Rectangle2D.Float pointRect = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/*  75 */       this.mainLineSelected = ((null != this.mainLine) && (this.mainLine.intersects(pointRect)));
/*     */     } else {
/*  77 */       this.mainLineSelected = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/*  84 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/*  86 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[0]), mapper.yv(this.prices[0])));
/*  87 */     if (null != this.middlePoint) {
/*  88 */       handlerMiddlePoints.add(this.middlePoint);
/*     */     }
/*  90 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 100 */     if (Period.TICK.equals(mapper.getPeriod())) {
/* 101 */       return;
/*     */     }
/* 103 */     getPath().reset();
/*     */ 
/* 105 */     double pipsPerBarValue = mapper.getInstrument().getPipValue();
/* 106 */     pipsPerBarValue *= this.pipsPerBar;
/*     */ 
/* 113 */     int x = mapper.xt(this.times[0]);
/* 114 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/* 116 */     int width = mapper.getWidth();
/*     */ 
/* 118 */     long timeOnRightSideOfScreen = mapper.tx(width);
/* 119 */     long timeDif = timeOnRightSideOfScreen - this.times[0];
/* 120 */     double barsCount = timeDif / mapper.getInterval();
/*     */ 
/* 122 */     this.mainLine = drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue);
/* 123 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue / 2.0D);
/* 124 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue * 2.0D);
/* 125 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue / 3.0D);
/* 126 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue * 3.0D);
/* 127 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue / 4.0D);
/* 128 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue * 4.0D);
/* 129 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue / 8.0D);
/* 130 */     drawAngle(mapper, x, y, width, barsCount, pipsPerBarValue * 8.0D);
/*     */ 
/* 132 */     ((Graphics2D)g).draw(getPath());
/* 133 */     String formattedValue = formattersManager.getValueFormatter().formatFibo(this.pipsPerBar);
/*     */ 
/* 135 */     int dx = 0;
/* 136 */     int dy = 0;
/* 137 */     if ((this.mainLine.y2 < 0.0D) || (this.mainLine.y2 > mapper.getHeight())) {
/* 138 */       if (this.mainLine.y2 < 0.0D)
/* 139 */         dy = y / 2;
/*     */       else {
/* 141 */         dy = (mapper.getHeight() + y) / 2;
/*     */       }
/* 143 */       double priceDiff = mapper.vy(dy) - this.prices[0];
/* 144 */       double barsDiff = priceDiff / pipsPerBarValue;
/* 145 */       dx = (int)(x + barsDiff * mapper.getBarWidth());
/*     */     } else {
/* 147 */       dx = x + (width - x) / 2;
/* 148 */       dy = mapper.yv(this.prices[0] + barsCount * pipsPerBarValue / 2.0D);
/*     */     }
/* 150 */     this.middlePoint = new Point(dx, dy);
/* 151 */     g.drawString(formattedValue, dx + 3, dy - 3);
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 159 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 160 */       boolean downgrade = false;
/* 161 */       if (this.middlePoint != null) {
/* 162 */         double price = mapper.vy(this.middlePoint.y);
/* 163 */         downgrade = Double.compare(getPrice(0), price) >= 0;
/*     */       }
/* 165 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), downgrade);
/*     */     }
/* 167 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   private Line2D.Double drawAngle(IMapper mapper, int x, int y, int width, double barsCount, double pipsPerBar)
/*     */   {
/* 173 */     int x2 = width;
/*     */ 
/* 175 */     double y2 = mapper.yv(this.prices[0] + barsCount * pipsPerBar);
/*     */ 
/* 177 */     GraphicHelper.drawSegmentLine(getPath(), x, y, x2, y2, width, mapper.getHeight());
/*     */ 
/* 179 */     return new Line2D.Double(x, y, width, y2);
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 193 */     if (this.mainLineSelected) {
/* 194 */       int x1 = mapper.xt(this.times[0]);
/* 195 */       int x = point.x;
/* 196 */       if (point.x < x1) {
/* 197 */         x = x1;
/*     */       }
/*     */ 
/* 200 */       double pipValue = mapper.getInstrument().getPipValue();
/* 201 */       double pipsDiff = (mapper.vy(point.y) - this.prices[0]) / pipValue;
/* 202 */       double barsDiff = (x - x1) / mapper.getBarWidth();
/*     */ 
/* 204 */       this.pipsPerBar = (pipsDiff / barsDiff);
/*     */     } else {
/* 206 */       super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 212 */     return (!Period.TICK.equals(mapper.getPeriod())) && (isValidPoint(0));
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 217 */     Rectangle2D.Float pointerRectangle = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 218 */     return getPath().intersects(pointerRectangle);
/*     */   }
/*     */ 
/*     */   public IChartObject clone()
/*     */   {
/* 223 */     return new GannAnglesChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 228 */     return "item.gann.angle";
/*     */   }
/*     */ 
/*     */   public double getPipsPerBar()
/*     */   {
/* 233 */     return this.pipsPerBar;
/*     */   }
/*     */ 
/*     */   public void setPipsPerBar(double pipsPerBar)
/*     */   {
/* 238 */     this.pipsPerBar = pipsPerBar;
/* 239 */     this.middlePoint = null;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 247 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.GannAnglesChartObject
 * JD-Core Version:    0.6.0
 */