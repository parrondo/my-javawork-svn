/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.renko;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class RenkoDataSequence extends AbstractPriceAggregationDataSequence<RenkoData>
/*    */ {
/*    */   public RenkoDataSequence(long from, long to, int extraBefore, int extraAfter, RenkoData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*    */   {
/* 30 */     super(Period.TICK, from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*    */   }
/*    */ 
/*    */   protected double getHigh(RenkoData bar)
/*    */   {
/* 46 */     double high = bar.getHigh();
/* 47 */     if ((bar.getInProgressRenko() != null) && (high < bar.getInProgressRenko().getHigh())) {
/* 48 */       high = bar.getInProgressRenko().getHigh();
/*    */     }
/* 50 */     return high;
/*    */   }
/*    */ 
/*    */   protected double getLow(RenkoData bar)
/*    */   {
/* 55 */     double low = bar.getLow();
/* 56 */     if ((bar.getInProgressRenko() != null) && (low > bar.getInProgressRenko().getLow())) {
/* 57 */       low = bar.getInProgressRenko().getLow();
/*    */     }
/* 59 */     return low;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence
 * JD-Core Version:    0.6.0
 */