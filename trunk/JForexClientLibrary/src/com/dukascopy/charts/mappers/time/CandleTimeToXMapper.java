/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ 
/*    */ public class CandleTimeToXMapper extends AbstractTimeToXMapper<CandleDataSequence, CandleData>
/*    */ {
/*    */   public CandleTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 19 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ 
/*    */   public long tx(int x)
/*    */   {
/* 24 */     return DataCacheUtils.getCandleStartFast(this.chartState.getPeriod(), super.tx(x));
/*    */   }
/*    */ 
/*    */   public int xt(long time)
/*    */   {
/* 29 */     return super.xt(DataCacheUtils.getCandleStartFast(this.chartState.getPeriod(), time)) + getBarWidth() / 2;
/*    */   }
/*    */ 
/*    */   public long getInterval()
/*    */   {
/* 34 */     return this.chartState.getPeriod().getInterval();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.CandleTimeToXMapper
 * JD-Core Version:    0.6.0
 */