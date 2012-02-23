/*    */ package com.dukascopy.charts.mappers.value;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ 
/*    */ public class ValueToYMapper
/*    */   implements IValueToYMapper
/*    */ {
/*    */   private static final float MIN_VALUE_HEIGHT = 1.0E-010F;
/*    */   final ValueFrame valueFrame;
/*    */   final ChartState chartState;
/*    */   int paneHeight;
/*    */   float valueHeightInPx;
/*    */   final double minPadding;
/* 19 */   double padding = 0.0D;
/*    */ 
/*    */   public ValueToYMapper(ChartState chartState, ValueFrame valueFrame, double minPadding, int previousHeight) {
/* 22 */     this(chartState, valueFrame, minPadding, minPadding);
/* 23 */     this.paneHeight = previousHeight;
/*    */   }
/*    */ 
/*    */   public ValueToYMapper(ChartState chartState, ValueFrame valueFrame, double minPadding, double padding) {
/* 27 */     this.chartState = chartState;
/* 28 */     this.valueFrame = valueFrame;
/* 29 */     this.minPadding = minPadding;
/* 30 */     this.padding = padding;
/*    */   }
/*    */ 
/*    */   public void computeGeometry(int panelHeight) {
/* 34 */     this.paneHeight = panelHeight;
/* 35 */     computeGeometry();
/*    */   }
/*    */ 
/*    */   public void computeGeometry(double min, double max) {
/* 39 */     this.valueFrame.changeMinMax(min, max);
/* 40 */     computeGeometry();
/*    */   }
/*    */ 
/*    */   public void computeGeometry() {
/* 44 */     float valueHeight = (float)((this.paneHeight - this.paneHeight * this.padding * 2.0D) / this.valueFrame.getValueDiff());
/* 45 */     valueHeight = Math.max(valueHeight, 1.0E-010F);
/* 46 */     this.valueHeightInPx = valueHeight;
/*    */   }
/*    */ 
/*    */   public final int yv(double value) {
/* 50 */     double relativeValue = this.valueFrame.getRelativeValue(value);
/*    */ 
/* 52 */     double valueToRound = this.valueHeightInPx * relativeValue + this.paneHeight * this.padding + 0.5D;
/* 53 */     int roundedValue = (int)valueToRound;
/* 54 */     return roundedValue;
/*    */   }
/*    */ 
/*    */   public final double vy(int y) {
/* 58 */     double relativePrice = (y - this.paneHeight * this.padding) / this.valueHeightInPx;
/* 59 */     return this.valueFrame.getAbsoluteValue(relativePrice);
/*    */   }
/*    */ 
/*    */   public int getHeight() {
/* 63 */     return this.paneHeight;
/*    */   }
/*    */ 
/*    */   public boolean isYOutOfRange(int y) {
/* 67 */     return (y < 0) || (y > getHeight());
/*    */   }
/*    */ 
/*    */   public float getValuesInOnePixel() {
/* 71 */     return 1.0F / this.valueHeightInPx;
/*    */   }
/*    */ 
/*    */   public void setPadding(double padding) {
/* 75 */     if ((padding >= this.minPadding) && (padding <= 0.45D))
/* 76 */       this.padding = padding;
/*    */   }
/*    */ 
/*    */   public double getPadding()
/*    */   {
/* 81 */     return this.padding;
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument()
/*    */   {
/* 86 */     return this.chartState.getInstrument();
/*    */   }
/*    */ 
/*    */   public double getValueFrameMaxValue() {
/* 90 */     return this.valueFrame.getMaxValue();
/*    */   }
/*    */ 
/*    */   public double getValueFrameMinValue() {
/* 94 */     return this.valueFrame.getMinValue();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.value.ValueToYMapper
 * JD-Core Version:    0.6.0
 */