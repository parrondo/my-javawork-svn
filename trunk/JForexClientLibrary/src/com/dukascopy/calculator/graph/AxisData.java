/*    */ package com.dukascopy.calculator.graph;
/*    */ 
/*    */ public class AxisData
/*    */ {
/*    */   public double minimum;
/*    */   public double maximum;
/*    */   public double majorUnit;
/*    */   public double minorUnit;
/*    */   public boolean majorVisible;
/*    */   public boolean minorVisible;
/*    */ 
/*    */   public AxisData()
/*    */   {
/* 12 */     this.minimum = -10.0D;
/* 13 */     this.maximum = 10.0D;
/* 14 */     this.majorUnit = 5.0D;
/* 15 */     this.minorUnit = 5.0D;
/* 16 */     this.majorVisible = true;
/* 17 */     this.minorVisible = true;
/*    */   }
/*    */ 
/*    */   public void assign(AxisData axisData)
/*    */   {
/* 23 */     this.minimum = axisData.minimum;
/* 24 */     this.maximum = axisData.maximum;
/* 25 */     this.majorUnit = axisData.majorUnit;
/* 26 */     this.minorUnit = axisData.minorUnit;
/* 27 */     this.majorVisible = axisData.majorVisible;
/* 28 */     this.minorVisible = axisData.minorVisible;
/*    */   }
/*    */ 
/*    */   boolean minMaxMatches(AxisData axisData)
/*    */   {
/* 40 */     if (axisData.minimum != this.minimum) return false;
/* 41 */     return axisData.maximum == this.maximum;
/*    */   }
/*    */ 
/*    */   boolean equals(AxisData axisData)
/*    */   {
/* 52 */     if (axisData.minimum != this.minimum) return false;
/* 53 */     if (axisData.maximum != this.maximum) return false;
/* 54 */     if (axisData.majorUnit != this.majorUnit) return false;
/* 55 */     if (axisData.minorUnit != this.minorUnit) return false;
/* 56 */     if (axisData.majorVisible != this.majorVisible) return false;
/* 57 */     return axisData.minorVisible == this.minorVisible;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.AxisData
 * JD-Core Version:    0.6.0
 */