/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
/*    */ 
/*    */ public class RenkoTimeToXMapper extends AbstractPriceAggregationTimeToXMapper<RenkoDataSequence, RenkoData>
/*    */ {
/*    */   public RenkoTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 23 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.RenkoTimeToXMapper
 * JD-Core Version:    0.6.0
 */