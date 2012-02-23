/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.charts.mappers.IMapper;
/*    */ import java.awt.Point;
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class AbstractThreePointFiboExtensionsChartObject extends AbstractTwoPointExtLevelsChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   protected AbstractThreePointFiboExtensionsChartObject(String key, IChart.Type type)
/*    */   {
/* 17 */     super(key, type);
/*    */   }
/*    */ 
/*    */   protected AbstractThreePointFiboExtensionsChartObject(IChart.Type type) {
/* 21 */     super(type);
/*    */   }
/*    */ 
/*    */   protected AbstractThreePointFiboExtensionsChartObject(AbstractThreePointFiboExtensionsChartObject chartObj) {
/* 25 */     super(chartObj);
/*    */   }
/*    */ 
/*    */   protected AbstractThreePointFiboExtensionsChartObject(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3) {
/* 29 */     super(key, type, time1, price1, time2, price2);
/*    */ 
/* 31 */     this.times[2] = time3;
/* 32 */     this.prices[2] = price3;
/*    */   }
/*    */ 
/*    */   public int getPointsCount()
/*    */   {
/* 38 */     return 3;
/*    */   }
/*    */ 
/*    */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*    */   {
/* 43 */     super.modifyNewDrawing(point, mapper, defaultRange);
/*    */ 
/* 45 */     if (this.currentPoint == 2) {
/* 46 */       this.times[2] = mapper.tx(point.x);
/* 47 */       this.prices[2] = mapper.vy(point.y);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected boolean isDrawable(IMapper mapper)
/*    */   {
/* 53 */     return isValidPoint(1);
/*    */   }
/*    */ 
/*    */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*    */   {
/* 58 */     List handlerMiddlePoints = super.getHandlerMiddlePoints(mapper);
/*    */ 
/* 60 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[2]), mapper.yv(this.prices[2])));
/*    */ 
/* 62 */     return handlerMiddlePoints;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AbstractThreePointFiboExtensionsChartObject
 * JD-Core Version:    0.6.0
 */