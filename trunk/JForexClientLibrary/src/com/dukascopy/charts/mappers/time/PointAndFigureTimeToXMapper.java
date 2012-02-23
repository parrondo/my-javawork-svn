/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*    */ 
/*    */ public class PointAndFigureTimeToXMapper extends AbstractPriceAggregationTimeToXMapper<PointAndFigureDataSequence, PointAndFigureData>
/*    */ {
/*    */   public PointAndFigureTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 20 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.PointAndFigureTimeToXMapper
 * JD-Core Version:    0.6.0
 */