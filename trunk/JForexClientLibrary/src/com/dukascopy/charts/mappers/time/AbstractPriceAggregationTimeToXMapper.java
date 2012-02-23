/*    */ package com.dukascopy.charts.mappers.time;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ 
/*    */ public abstract class AbstractPriceAggregationTimeToXMapper<S extends AbstractPriceAggregationDataSequence<D>, D extends AbstractPriceAggregationData> extends AbstractTimeToXMapper<S, D>
/*    */ {
/*    */   public AbstractPriceAggregationTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<S, D> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*    */   {
/* 25 */     super(chartState, dataSequenceProvider, geometryCalculator);
/*    */   }
/*    */ 
/*    */   public long getInterval()
/*    */   {
/* 30 */     return 1L;
/*    */   }
/*    */ 
/*    */   protected long time(long startTime, int x)
/*    */   {
/* 35 */     int priceRangeIndex = x / getBarWidth();
/* 36 */     AbstractPriceAggregationDataSequence dataSequence = (AbstractPriceAggregationDataSequence)this.dataSequenceProvider.getDataSequence();
/*    */ 
/* 38 */     if (dataSequence.isEmpty()) {
/* 39 */       return -1L;
/*    */     }
/*    */ 
/* 42 */     AbstractPriceAggregationData data = (AbstractPriceAggregationData)dataSequence.getData(priceRangeIndex);
/* 43 */     if (data == null) {
/* 44 */       return -1L;
/*    */     }
/* 46 */     return data.getTime();
/*    */   }
/*    */ 
/*    */   public int xt(long time)
/*    */   {
/* 51 */     AbstractPriceAggregationDataSequence dataSequence = (AbstractPriceAggregationDataSequence)this.dataSequenceProvider.getDataSequence();
/* 52 */     int curentIndex = dataSequence.indexOf(time);
/*    */ 
/* 56 */     int x = getBarWidth() * curentIndex + getBarWidth() / 2;
/* 57 */     return x;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.AbstractPriceAggregationTimeToXMapper
 * JD-Core Version:    0.6.0
 */