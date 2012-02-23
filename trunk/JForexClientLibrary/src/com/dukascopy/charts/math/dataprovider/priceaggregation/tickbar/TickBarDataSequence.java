/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class TickBarDataSequence extends AbstractPriceAggregationDataSequence<TickBarData>
/*    */ {
/*    */   public TickBarDataSequence(long from, long to, int extraBefore, int extraAfter, TickBarData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*    */   {
/* 27 */     super(Period.TICK, from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence
 * JD-Core Version:    0.6.0
 */