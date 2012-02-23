/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IVerticalRetracementChartObject;
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
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ abstract class VerticalRetracementChartObject extends ChartObject
/*     */   implements IVerticalRetracementChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   protected static final int INFO_OFFSET = 3;
/*     */   transient GeneralPath path;
/*  27 */   long timeStep = -1L;
/*     */ 
/*     */   protected VerticalRetracementChartObject(String key, IChart.Type type) {
/*  30 */     super(key, type);
/*  31 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   protected VerticalRetracementChartObject(IChart.Type type) {
/*  35 */     super(null, type);
/*  36 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   protected VerticalRetracementChartObject(String key, IChart.Type type, long time1, long timeStep)
/*     */   {
/*  41 */     super(key, type);
/*  42 */     this.times[0] = time1;
/*  43 */     this.timeStep = timeStep;
/*     */   }
/*     */ 
/*     */   protected VerticalRetracementChartObject(VerticalRetracementChartObject chartObject)
/*     */   {
/*  48 */     super(chartObject);
/*  49 */     this.timeStep = chartObject.timeStep;
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  59 */     if ((!isValidTime(0)) || (isOutOfRange(0, mapper))) {
/*  60 */       return false;
/*     */     }
/*     */ 
/*  64 */     return mapper.getInterval() <= this.timeStep;
/*     */   }
/*     */ 
/*     */   protected final boolean isOutOfRange(int pointIndex, IMapper dataMapper, int screenCount)
/*     */   {
/*  72 */     int pointX = dataMapper.xt(this.times[pointIndex]);
/*     */ 
/*  74 */     int screenWidth = dataMapper.getWidth();
/*     */ 
/*  76 */     if (Math.abs(pointX) > screenWidth * screenCount) {
/*  77 */       return true;
/*     */     }
/*  79 */     return pointX == -2147483648;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  88 */     GeneralPath drawingPath = getPath();
/*  89 */     drawingPath.reset();
/*     */ 
/*  91 */     plotLines(drawingPath, mapper);
/*     */ 
/*  93 */     Graphics2D g2 = (Graphics2D)g;
/*  94 */     g2.draw(drawingPath);
/*     */ 
/*  96 */     drawLabels(g2, mapper);
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath() {
/* 100 */     if (this.path == null) {
/* 101 */       this.path = new GeneralPath();
/*     */     }
/* 103 */     return this.path;
/*     */   }
/*     */ 
/*     */   protected int getStepWidth(IMapper mapper) {
/* 107 */     return (int)(this.timeStep / mapper.getInterval()) * mapper.getBarWidth();
/*     */   }
/*     */ 
/*     */   protected long getStep(IMapper mapper, Point point) {
/* 111 */     return Math.max(Math.abs(mapper.xt(this.times[0]) - point.x) / mapper.getBarWidth() * mapper.getInterval(), mapper.getInterval());
/*     */   }
/*     */ 
/*     */   protected abstract void plotLines(GeneralPath paramGeneralPath, IMapper paramIMapper);
/*     */ 
/*     */   protected abstract void drawLabels(Graphics2D paramGraphics2D, IMapper paramIMapper);
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/* 123 */     if (!isDrawable(mapper)) {
/* 124 */       return;
/*     */     }
/*     */ 
/* 127 */     int x1 = mapper.xt(this.times[0]);
/* 128 */     int x2 = x1 + getStepWidth(mapper);
/* 129 */     int commonY = mapper.getHeight() / 2;
/*     */ 
/* 131 */     g.drawLine(x1, commonY, x2, commonY);
/* 132 */     StringBuilder text = new StringBuilder();
/* 133 */     if (!ObjectUtils.isNullOrEmpty(getText())) {
/* 134 */       text.append(getText()).append("  ");
/*     */     }
/* 136 */     text.append(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(this.timeStep)));
/* 137 */     g.drawString(text.toString(), x2 + 3, commonY);
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*     */   {
/* 143 */     if (!isValidTime(0)) {
/* 144 */       return;
/*     */     }
/*     */ 
/* 147 */     this.timeStep = getStep(mapper, point);
/*     */   }
/*     */ 
/*     */   public boolean addNewPoint(Point point, IMapper mapper)
/*     */   {
/* 152 */     if (!isValidTime(0)) {
/* 153 */       this.times[0] = mapper.tx(point.x);
/* 154 */       return false;
/*     */     }
/* 156 */     this.timeStep = getStep(mapper, point);
/* 157 */     return true;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 163 */     long newTime = mapper.tx(point.x);
/* 164 */     if (this.selectedHandlerIndex == 0)
/* 165 */       this.times[0] = newTime;
/* 166 */     else if (this.selectedHandlerIndex == 1)
/* 167 */       this.timeStep = getStep(mapper, point);
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 173 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 175 */     if ((!isValidTime(0)) || (this.timeStep < 1L)) {
/* 176 */       return handlerMiddlePoints;
/*     */     }
/*     */ 
/* 179 */     int x1 = mapper.xt(this.times[0]);
/* 180 */     int x2 = x1 + getStepWidth(mapper);
/* 181 */     int commonY = mapper.getHeight() / 2;
/* 182 */     handlerMiddlePoints.add(new Point(x1, commonY));
/* 183 */     handlerMiddlePoints.add(new Point(x2, commonY));
/* 184 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 189 */     if (!isDrawable(mapper)) {
/* 190 */       return false;
/*     */     }
/*     */ 
/* 193 */     return GraphicHelper.intersects(getPath(), new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2));
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/* 199 */     updateSelectedHandler(2, point, mapper, range);
/*     */   }
/*     */ 
/*     */   protected boolean isHandlerSelected(int handlerIndex, Point point, IMapper mapper, int range)
/*     */   {
/* 205 */     Rectangle2D.Float pointerRect = new Rectangle2D.Float(point.x - range, point.y - range / 2, range, range);
/*     */ 
/* 207 */     int x = -1;
/*     */ 
/* 209 */     switch (handlerIndex) {
/*     */     case 0:
/* 211 */       x = mapper.xt(this.times[0]);
/* 212 */       break;
/*     */     case 1:
/* 214 */       x = mapper.xt(this.times[0]) + getStepWidth(mapper);
/* 215 */       break;
/*     */     default:
/* 217 */       throw new IllegalArgumentException(new StringBuilder().append("Handler index is out of range : ").append(handlerIndex).toString());
/*     */     }
/*     */ 
/* 221 */     Rectangle2D.Float handlerRect = new Rectangle2D.Float(x - range, mapper.getHeight() / 2 - range / 2, range, range);
/*     */ 
/* 223 */     return pointerRect.intersects(handlerRect);
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 228 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasPriceValue()
/*     */   {
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 245 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.VerticalRetracementChartObject
 * JD-Core Version:    0.6.0
 */