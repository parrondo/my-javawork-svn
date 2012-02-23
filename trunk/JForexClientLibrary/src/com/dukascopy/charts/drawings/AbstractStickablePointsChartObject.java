/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ 
/*    */ public abstract class AbstractStickablePointsChartObject extends ChartObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Instrument instrument;
/*    */   private OfferSide offerSide;
/*    */   private float precision;
/*    */   private Period[] periods;
/*    */ 
/*    */   public AbstractStickablePointsChartObject(String key, IChart.Type type)
/*    */   {
/* 25 */     super(key, type);
/*    */   }
/*    */ 
/*    */   public AbstractStickablePointsChartObject(AbstractStickablePointsChartObject chartObject) {
/* 29 */     super(chartObject);
/*    */ 
/* 31 */     this.instrument = chartObject.instrument;
/* 32 */     this.offerSide = chartObject.offerSide;
/* 33 */     this.precision = chartObject.precision;
/*    */ 
/* 35 */     Period[] periods = getPeriods();
/* 36 */     periods[0] = chartObject.getPeriods()[0];
/* 37 */     periods[1] = chartObject.getPeriods()[1];
/* 38 */     periods[2] = chartObject.getPeriods()[2];
/*    */   }
/*    */ 
/*    */   public Instrument getInstrumentOnShapeEdited()
/*    */   {
/* 43 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setInstrumentOnShapeEdited(Instrument instrument) {
/* 47 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public void setPrecision(float precision) {
/* 51 */     this.precision = precision;
/*    */   }
/*    */ 
/*    */   public float getPrecision() {
/* 55 */     return this.precision;
/*    */   }
/*    */ 
/*    */   public OfferSide getOfferSideOnShapeEdited() {
/* 59 */     return this.offerSide;
/*    */   }
/*    */   public void setOfferSideOnShapeEdited(OfferSide offerSide) {
/* 62 */     this.offerSide = offerSide;
/*    */   }
/*    */ 
/*    */   public Period getPeriodOnPointsEdited(int index) {
/* 66 */     return getPeriods()[index];
/*    */   }
/*    */   public void setPeriodOnPointsEdited(int index, Period period) {
/* 69 */     getPeriods()[index] = period;
/*    */   }
/*    */ 
/*    */   public void setPeriodToAllPoints(Period period) {
/* 73 */     getPeriods()[0] = period;
/* 74 */     getPeriods()[1] = period;
/* 75 */     getPeriods()[2] = period;
/*    */   }
/*    */ 
/*    */   protected Period[] getPeriods() {
/* 79 */     if (null == this.periods) {
/* 80 */       this.periods = new Period[] { Period.TICK, Period.TICK, Period.TICK };
/*    */     }
/* 82 */     return this.periods;
/*    */   }
/*    */ 
/*    */   public void setTime(int index, long time)
/*    */   {
/* 92 */     validatePointIndex(Integer.valueOf(index));
/* 93 */     this.times[index] = time;
/*    */   }
/*    */ 
/*    */   public void setPrice(int pointIndex, double priceValue) {
/* 97 */     validatePointIndex(Integer.valueOf(pointIndex));
/* 98 */     this.prices[pointIndex] = priceValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AbstractStickablePointsChartObject
 * JD-Core Version:    0.6.0
 */