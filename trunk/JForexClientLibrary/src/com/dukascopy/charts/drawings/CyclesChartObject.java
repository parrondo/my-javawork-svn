/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.drawings.ICyclesChartObject;
/*    */ import com.dukascopy.charts.mappers.IMapper;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.geom.GeneralPath;
/*    */ 
/*    */ public class CyclesChartObject extends VerticalRetracementChartObject
/*    */   implements ICyclesChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final String CHART_OBJECT_NAME = "Cycles";
/*    */ 
/*    */   public CyclesChartObject(String key)
/*    */   {
/* 18 */     super(key, IChart.Type.CYCLES);
/*    */   }
/*    */ 
/*    */   public CyclesChartObject() {
/* 22 */     super(IChart.Type.CYCLES);
/*    */   }
/*    */ 
/*    */   public CyclesChartObject(String key, long time1, long timeStep) {
/* 26 */     super(key, IChart.Type.CYCLES, time1, timeStep);
/*    */   }
/*    */ 
/*    */   public CyclesChartObject(CyclesChartObject chartObject) {
/* 30 */     super(chartObject);
/*    */   }
/*    */ 
/*    */   protected void plotLines(GeneralPath path, IMapper mapper)
/*    */   {
/* 35 */     int stepWidth = getStepWidth(mapper);
/*    */ 
/* 37 */     int x = mapper.xt(this.times[0]);
/* 38 */     if (x < 0) {
/* 39 */       x = stepWidth + x % stepWidth;
/*    */     }
/*    */ 
/* 42 */     for (; !mapper.isXOutOfRange(x); x += stepWidth) {
/* 43 */       this.path.moveTo(x, 0.0F);
/* 44 */       this.path.lineTo(x, mapper.getHeight());
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void drawLabels(Graphics2D g2, IMapper mapper)
/*    */   {
/* 50 */     FontMetrics fontMetrics = g2.getFontMetrics();
/* 51 */     int fontHeight = fontMetrics.getHeight();
/* 52 */     int lineNumber = 0;
/* 53 */     int stepWidth = getStepWidth(mapper);
/*    */ 
/* 55 */     int x = mapper.xt(this.times[0]);
/* 56 */     if (x < 0) {
/* 57 */       lineNumber = -(x / stepWidth) + 1;
/* 58 */       x = stepWidth + x % stepWidth;
/*    */     }
/* 60 */     for (; !mapper.isXOutOfRange(x); x += stepWidth)
/* 61 */       g2.drawString(String.valueOf(lineNumber++), x + 3, fontHeight + 3);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 71 */     return "Cycles";
/*    */   }
/*    */ 
/*    */   public CyclesChartObject clone()
/*    */   {
/* 76 */     return new CyclesChartObject(this);
/*    */   }
/*    */ 
/*    */   public String getLocalizationKey()
/*    */   {
/* 81 */     return "item.periods";
/*    */   }
/*    */ 
/*    */   public void setTime(int pointIndex, long timeValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setPrice(int pointIndex, double priceValue)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.CyclesChartObject
 * JD-Core Version:    0.6.0
 */