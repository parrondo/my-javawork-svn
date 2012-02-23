/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class PriceRangeDataSequence extends AbstractPriceAggregationDataSequence<PriceRangeData>
/*    */ {
/*    */   public PriceRangeDataSequence(long from, long to, int extraBefore, int extraAfter, PriceRangeData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*    */   {
/* 27 */     super(Period.TICK, from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence
 * JD-Core Version:    0.6.0
 */