/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import java.util.List;
/*    */ 
/*    */ public class RateDataIndicatorCalculationResultWraper
/*    */ {
/*    */   private Object[] indicatorCalculationResult;
/*    */   private List<IBar> sourceData;
/*    */ 
/*    */   public Object[] getIndicatorCalculationResult()
/*    */   {
/* 21 */     return this.indicatorCalculationResult;
/*    */   }
/*    */ 
/*    */   public void setIndicatorCalculationResult(Object[] indicatorCalculationResult) {
/* 25 */     this.indicatorCalculationResult = indicatorCalculationResult;
/*    */   }
/*    */ 
/*    */   public List<IBar> getSourceData() {
/* 29 */     return this.sourceData;
/*    */   }
/*    */ 
/*    */   public void setSourceData(List<IBar> sourceData) {
/* 33 */     this.sourceData = sourceData;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.RateDataIndicatorCalculationResultWraper
 * JD-Core Version:    0.6.0
 */