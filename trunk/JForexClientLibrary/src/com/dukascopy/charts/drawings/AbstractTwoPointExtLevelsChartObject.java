/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import java.awt.Point;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Line2D.Double;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class AbstractTwoPointExtLevelsChartObject extends AbstractLeveledChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected transient GeneralPath path;
/*  24 */   protected transient int selectedLevelLineIndx = -1;
/*     */   private List<Object[]> levels;
/*  27 */   protected List<Line2D.Double> lines = new ArrayList();
/*     */ 
/*     */   protected AbstractTwoPointExtLevelsChartObject(String key, IChart.Type type)
/*     */   {
/*  31 */     super(key, type);
/*     */   }
/*     */ 
/*     */   protected AbstractTwoPointExtLevelsChartObject(IChart.Type type) {
/*  35 */     super(null, type);
/*     */   }
/*     */ 
/*     */   protected AbstractTwoPointExtLevelsChartObject(AbstractTwoPointExtLevelsChartObject chartObj) {
/*  39 */     super(chartObj);
/*     */   }
/*     */ 
/*     */   protected AbstractTwoPointExtLevelsChartObject(String key, IChart.Type type, long time1, double price1, long time2, double price2) {
/*  43 */     super(key, type);
/*     */ 
/*  45 */     this.times[0] = time1;
/*  46 */     this.prices[0] = price1;
/*  47 */     this.times[1] = time2;
/*  48 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/*  54 */     return 2;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/*  59 */     if (this.currentPoint == 1) {
/*  60 */       this.times[1] = mapper.tx(point.x);
/*  61 */       this.prices[1] = mapper.vy(point.y);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  67 */     return isValidPoint(0);
/*     */   }
/*     */ 
/*     */   protected GeneralPath getPath() {
/*  71 */     if (this.path == null) {
/*  72 */       this.path = new GeneralPath();
/*     */     }
/*  74 */     return this.path;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/*  79 */     Rectangle2D.Float pointerRectangle = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/*  80 */     return GraphicHelper.intersects(getPath(), pointerRectangle);
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/*  85 */     if (this.selectedLevelLineIndx != -1) {
/*  86 */       double newLevelValue = calculateNewValue(mapper, point);
/*  87 */       if ((0.0D / 0.0D) != newLevelValue)
/*  88 */         ((Object[])getLevels().get(this.selectedLevelLineIndx))[1] = Double.valueOf(newLevelValue);
/*     */     }
/*     */     else {
/*  91 */       super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract double calculateNewValue(IMapper paramIMapper, Point paramPoint);
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 102 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 104 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[0]), mapper.yv(this.prices[0])));
/* 105 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[1]), mapper.yv(this.prices[1])));
/* 106 */     for (int i = 0; i < this.lines.size(); i++) {
/* 107 */       Line2D.Double line = (Line2D.Double)this.lines.get(i);
/*     */ 
/* 109 */       handlerMiddlePoints.add(getHandlerPointForLevelIndex(line));
/*     */     }
/* 111 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   protected abstract Point getHandlerPointForLevelIndex(Line2D.Double paramDouble);
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range) {
/* 118 */     super.updateSelectedHandler(point, mapper, range);
/*     */ 
/* 120 */     if (-1 == this.selectedHandlerIndex) {
/* 121 */       Rectangle2D.Float pointRect = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 122 */       this.selectedLevelLineIndx = isLevelLineHandlerSelected(mapper, pointRect);
/*     */     } else {
/* 124 */       this.selectedLevelLineIndx = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isPointSelected(int pointIndex, IMapper mapper, int range, Rectangle2D.Float pointerRect) {
/* 129 */     int x = mapper.xt(this.times[pointIndex]);
/* 130 */     int y = mapper.yv(this.prices[pointIndex]);
/* 131 */     Rectangle2D.Float handlerRect = new Rectangle2D.Float(x - range / 2, y - range / 2, range, range);
/* 132 */     return handlerRect.intersects(pointerRect);
/*     */   }
/*     */ 
/*     */   protected int isLevelLineHandlerSelected(IMapper mapper, Rectangle2D.Float pointRect) {
/* 136 */     for (int i = 0; i < this.lines.size(); i++) {
/* 137 */       Line2D.Double line = (Line2D.Double)this.lines.get(i);
/* 138 */       if (line.intersects(pointRect)) {
/* 139 */         return i;
/*     */       }
/*     */     }
/* 142 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean isLevelValuesInPercents()
/*     */   {
/* 147 */     return true;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 156 */     in.defaultReadObject();
/* 157 */     if (this.levels == null)
/* 158 */       this.levels = this.levels;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AbstractTwoPointExtLevelsChartObject
 * JD-Core Version:    0.6.0
 */