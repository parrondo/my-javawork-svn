/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence;
/*    */ 
/*    */ public class PriceRangeTimeToXMapper extends AbstractPriceAggregationTimeToXMapper<PriceRangeDataSequence, PriceRangeData>
/*    */ {
/*    */   public PriceRangeTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<PriceRangeDataSequence, PriceRangeData> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 20 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.PriceRangeTimeToXMapper
 * JD-Core Version:    0.6.0
 */