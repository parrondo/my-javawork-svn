/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ 
/*    */ public class TickTimeToXMapper extends AbstractTimeToXMapper<TickDataSequence, TickData>
/*    */ {
/*    */   public TickTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 19 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ 
/*    */   public long getInterval()
/*    */   {
/* 24 */     return Period.ONE_SEC.getInterval();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.TickTimeToXMapper
 * JD-Core Version:    0.6.0
 */