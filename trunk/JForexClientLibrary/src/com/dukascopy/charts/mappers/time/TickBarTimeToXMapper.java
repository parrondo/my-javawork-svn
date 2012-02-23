/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
/*    */ 
/*    */ public class TickBarTimeToXMapper extends AbstractPriceAggregationTimeToXMapper<TickBarDataSequence, TickBarData>
/*    */ {
/*    */   public TickBarTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 20 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.TickBarTimeToXMapper
 * JD-Core Version:    0.6.0
 */