/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.pf;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class PointAndFigureDataSequence extends AbstractPriceAggregationDataSequence<PointAndFigureData>
/*    */ {
/*    */   public PointAndFigureDataSequence(long from, long to, int extraBefore, int extraAfter, PointAndFigureData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*    */   {
/* 27 */     super(Period.TICK, from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence
 * JD-Core Version:    0.6.0
 */