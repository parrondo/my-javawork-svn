/*    */ package com.dukascopy.charts.mappers.value;
/*    */ 
/*    */ public class ValueFrame
/*    */ {
/*    */   private double maxValue;
/*    */   private double minValue;
/*    */ 
/*    */   public ValueFrame(double minValue, double maxValue)
/*    */   {
/*  9 */     this.maxValue = maxValue;
/* 10 */     this.minValue = minValue;
/*    */   }
/*    */ 
/*    */   public void changeMinMax(double min, double max) {
/* 14 */     this.minValue = min;
/* 15 */     this.maxValue = max;
/*    */   }
/*    */ 
/*    */   public double getMaxValue()
/*    */   {
/* 20 */     return this.maxValue;
/*    */   }
/*    */ 
/*    */   public double getMinValue() {
/* 24 */     return this.minValue;
/*    */   }
/*    */ 
/*    */   public double getValueDiff() {
/* 28 */     return Math.abs(this.maxValue - this.minValue);
/*    */   }
/*    */ 
/*    */   public double getRelativeValue(double absoluteValue) {
/* 32 */     return -(absoluteValue - this.maxValue);
/*    */   }
/*    */ 
/*    */   public double getAbsoluteValue(double relativeValue) {
/* 36 */     return this.maxValue - relativeValue;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 43 */     return "maxValue=" + this.maxValue + " minValue=" + this.minValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.value.ValueFrame
 * JD-Core Version:    0.6.0
 */