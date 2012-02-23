/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.drawings.IPolyLineChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PolyLineChartObject extends AbstractStickablePointsChartObject
/*     */   implements IPolyLineChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Polygon";
/*     */   transient GeneralPath path;
/*  28 */   List<ValuePoint> valuePoints = new ArrayList();
/*  29 */   List<Period> pointPeriods = new ArrayList();
/*     */   transient ValuePoint selectedValuePoint;
/*     */   transient ValuePoint lastValuePoint;
/*     */ 
/*     */   public PolyLineChartObject(String key)
/*     */   {
/*  35 */     super(key, IChart.Type.POLY_LINE);
/*     */   }
/*     */ 
/*     */   public PolyLineChartObject() {
/*  39 */     super(null, IChart.Type.POLY_LINE);
/*  40 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   PolyLineChartObject(PolyLineChartObject chartObject) {
/*  44 */     super(chartObject);
/*     */ 
/*  46 */     for (ValuePoint valuePoint : chartObject.valuePoints) {
/*  47 */       this.valuePoints.add(new ValuePoint(valuePoint.time, valuePoint.value));
/*     */     }
/*     */ 
/*  50 */     for (Period period : chartObject.pointPeriods)
/*  51 */       this.pointPeriods.add(period);
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  57 */     if (this.valuePoints.isEmpty()) {
/*  58 */       return false;
/*     */     }
/*     */ 
/*  61 */     for (ValuePoint valuePoint : this.valuePoints) {
/*  62 */       if (!valuePoint.isValid()) {
/*  63 */         return false;
/*     */       }
/*     */ 
/*  66 */       if (mapper.xt(valuePoint.time) == -1) {
/*  67 */         return false;
/*     */       }
/*     */     }
/*     */ 
/*  71 */     return true;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  76 */     GeneralPath drawingPath = getPath();
/*  77 */     drawingPath.reset();
/*     */ 
/*  79 */     ValuePoint lastDrawnPoint = null;
/*     */ 
/*  81 */     int width = mapper.getWidth();
/*  82 */     int height = mapper.getHeight();
/*     */ 
/*  84 */     if (this.valuePoints.size() == 1) {
/*  85 */       lastDrawnPoint = (ValuePoint)this.valuePoints.get(0);
/*     */     }
/*     */ 
/*  88 */     for (int i = 0; i < this.valuePoints.size() - 1; i++) {
/*  89 */       ValuePoint currentVp = (ValuePoint)this.valuePoints.get(i);
/*  90 */       ValuePoint nextVp = (ValuePoint)this.valuePoints.get(i + 1);
/*  91 */       lastDrawnPoint = nextVp;
/*     */ 
/*  93 */       int x1 = mapper.xt(currentVp.time);
/*  94 */       int x2 = mapper.xt(nextVp.time);
/*  95 */       int y1 = mapper.yv(currentVp.value);
/*  96 */       int y2 = mapper.yv(nextVp.value);
/*     */ 
/*  98 */       GraphicHelper.drawSegmentLine(drawingPath, x1, y1, x2, y2, width, height);
/*     */     }
/*     */ 
/* 101 */     if ((this.lastValuePoint != null) && (lastDrawnPoint != null)) {
/* 102 */       int x1 = mapper.xt(lastDrawnPoint.time);
/* 103 */       int x2 = mapper.xt(this.lastValuePoint.time);
/* 104 */       int y1 = mapper.yv(lastDrawnPoint.value);
/* 105 */       int y2 = mapper.yv(this.lastValuePoint.value);
/*     */ 
/* 107 */       GraphicHelper.drawSegmentLine(drawingPath, x1, y1, x2, y2, width, height);
/*     */     }
/*     */ 
/* 110 */     ((Graphics2D)g).draw(drawingPath);
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath() {
/* 114 */     if (this.path == null) {
/* 115 */       this.path = new GeneralPath();
/*     */     }
/*     */ 
/* 118 */     return this.path;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*     */   {
/* 123 */     if (this.valuePoints.isEmpty()) {
/* 124 */       return;
/*     */     }
/*     */ 
/* 127 */     this.lastValuePoint.time = mapper.tx(point.x);
/* 128 */     this.lastValuePoint.value = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public boolean addNewPoint(Point point, IMapper mapper)
/*     */   {
/* 133 */     if (this.valuePoints.size() >= 150) {
/* 134 */       return true;
/*     */     }
/*     */ 
/* 137 */     long newTime = mapper.tx(point.x);
/* 138 */     double price = mapper.vy(point.y);
/* 139 */     this.valuePoints.add(new ValuePoint(newTime, price));
/* 140 */     this.pointPeriods.add(Period.TICK);
/*     */ 
/* 142 */     this.lastValuePoint = new ValuePoint(newTime, price);
/*     */ 
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 157 */     if ((!ObjectUtils.isNullOrEmpty(this.valuePoints)) && ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT))) {
/* 158 */       ValuePoint valuePoint = (ValuePoint)this.valuePoints.get(0);
/* 159 */       boolean downgrade = true;
/* 160 */       if (this.valuePoints.size() > 1) {
/* 161 */         ValuePoint secondValuePoint = (ValuePoint)this.valuePoints.get(1);
/* 162 */         downgrade = Double.compare(valuePoint.value, secondValuePoint.value) >= 0;
/*     */       }
/* 164 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, valuePoint.time, valuePoint.value, downgrade);
/*     */     }
/* 166 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public void finishDrawing()
/*     */   {
/* 172 */     this.lastValuePoint = null;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 177 */     long newTime = mapper.tx(point.x);
/* 178 */     double newPrice = mapper.vy(point.y);
/*     */     long timeDiff;
/*     */     double priceDiff;
/* 180 */     if (this.selectedValuePoint != null) {
/* 181 */       this.selectedValuePoint.time = newTime;
/* 182 */       this.selectedValuePoint.value = newPrice;
/*     */     } else {
/* 184 */       if (prevPoint == null) {
/* 185 */         return;
/*     */       }
/*     */ 
/* 188 */       timeDiff = newTime - mapper.tx(prevPoint.x);
/* 189 */       priceDiff = newPrice - mapper.vy(prevPoint.y);
/*     */ 
/* 191 */       for (ValuePoint valuePoint : this.valuePoints) {
/* 192 */         valuePoint.time += timeDiff;
/* 193 */         valuePoint.value += priceDiff;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 200 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 202 */     for (ValuePoint valuePoint : this.valuePoints) {
/* 203 */       int x = mapper.xt(valuePoint.time);
/* 204 */       int y = mapper.yv(valuePoint.value);
/* 205 */       handlerMiddlePoints.add(new Point(x, y));
/*     */     }
/*     */ 
/* 208 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 213 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/* 218 */     this.selectedHandlerIndex = -1;
/* 219 */     this.selectedValuePoint = null;
/*     */ 
/* 221 */     int index = 0;
/* 222 */     for (ValuePoint valuePoint : this.valuePoints) {
/* 223 */       int valuePointX = mapper.xt(valuePoint.time);
/* 224 */       int valuePointY = mapper.yv(valuePoint.value);
/* 225 */       if (isInRange(valuePointX, valuePointY, point, range)) {
/* 226 */         this.selectedHandlerIndex = index;
/* 227 */         this.selectedValuePoint = valuePoint;
/* 228 */         return;
/*     */       }
/* 230 */       index++;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*     */   {
/* 236 */     if (handlerIndex >= this.valuePoints.size()) {
/* 237 */       return false;
/*     */     }
/*     */ 
/* 240 */     ValuePoint valuePoint = (ValuePoint)this.valuePoints.get(handlerIndex);
/*     */ 
/* 242 */     return isInRange(mapper.xt(valuePoint.time), mapper.yv(valuePoint.value), point, range);
/*     */   }
/*     */ 
/*     */   public void moveLeft(IMapper mapper)
/*     */   {
/* 252 */     for (ValuePoint valuePoint : this.valuePoints)
/* 253 */       valuePoint.time = mapper.tx(mapper.xt(valuePoint.time) - mapper.getBarWidth());
/*     */   }
/*     */ 
/*     */   public void moveRight(IMapper mapper)
/*     */   {
/* 259 */     for (ValuePoint valuePoint : this.valuePoints)
/* 260 */       valuePoint.time = mapper.tx(mapper.xt(valuePoint.time) + mapper.getBarWidth());
/*     */   }
/*     */ 
/*     */   public void moveDown(IMapper mapper)
/*     */   {
/* 266 */     for (ValuePoint valuePoint : this.valuePoints)
/* 267 */       valuePoint.value -= mapper.getValuesInOnePixel();
/*     */   }
/*     */ 
/*     */   public void moveUp(IMapper mapper)
/*     */   {
/* 273 */     for (ValuePoint valuePoint : this.valuePoints)
/* 274 */       valuePoint.value += mapper.getValuesInOnePixel();
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 280 */     return this.valuePoints.size();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 285 */     return "Polygon";
/*     */   }
/*     */ 
/*     */   public PolyLineChartObject clone()
/*     */   {
/* 290 */     return new PolyLineChartObject(this);
/*     */   }
/*     */ 
/*     */   public long getTime(int index)
/*     */   {
/* 295 */     return ((ValuePoint)this.valuePoints.get(index)).time;
/*     */   }
/*     */ 
/*     */   public double getPrice(int index)
/*     */   {
/* 300 */     return ((ValuePoint)this.valuePoints.get(index)).value;
/*     */   }
/*     */ 
/*     */   public void setTime(int index, long time)
/*     */   {
/* 305 */     if ((index >= 0) && (index < getPointsCount()))
/* 306 */       ((ValuePoint)this.valuePoints.get(index)).time = time;
/*     */   }
/*     */ 
/*     */   public void setPrice(int pointIndex, double value)
/*     */   {
/* 312 */     if ((pointIndex >= 0) && (pointIndex < getPointsCount()))
/* 313 */       ((ValuePoint)this.valuePoints.get(pointIndex)).value = value;
/*     */   }
/*     */ 
/*     */   public Period getPeriodOnPointsEdited(int index)
/*     */   {
/* 319 */     return (Period)this.pointPeriods.get(index);
/*     */   }
/*     */ 
/*     */   public void setPeriodOnPointsEdited(int index, Period period) {
/* 323 */     this.pointPeriods.set(index, period);
/*     */   }
/*     */ 
/*     */   public void setPeriodToAllPoints(Period period) {
/* 327 */     for (int i = 0; i < getPointsCount(); i++)
/* 328 */       setPeriodOnPointsEdited(i, period);
/*     */   }
/*     */ 
/*     */   public boolean addNewPoint(long time, double price)
/*     */   {
/* 334 */     if (this.valuePoints.size() >= 150) {
/* 335 */       return false;
/*     */     }
/*     */ 
/* 338 */     this.valuePoints.add(new ValuePoint(time, price));
/* 339 */     this.pointPeriods.add(Period.TICK);
/*     */ 
/* 345 */     return true;
/*     */   }
/*     */ 
/*     */   public void removePoint(int index)
/*     */   {
/* 350 */     this.valuePoints.remove(index);
/* 351 */     this.pointPeriods.remove(index);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 356 */     return "item.poly.line";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 364 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.PolyLineChartObject
 * JD-Core Version:    0.6.0
 */