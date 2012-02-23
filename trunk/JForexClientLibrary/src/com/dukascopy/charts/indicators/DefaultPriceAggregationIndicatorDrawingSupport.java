/*    */ package com.dukascopy.charts.indicators;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ 
/*    */ public class DefaultPriceAggregationIndicatorDrawingSupport<DS extends AbstractPriceAggregationDataSequence<T>, T extends AbstractPriceAggregationData> extends AbstractIndicatorDrawingSupport<DS, T>
/*    */ {
/*    */   public DefaultPriceAggregationIndicatorDrawingSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 15 */     super(geometryCalculator);
/*    */   }
/*    */ 
/*    */   public IBar[] getCandles()
/*    */   {
/* 20 */     return (IBar[])((AbstractPriceAggregationDataSequence)this.dataSequence).getData();
/*    */   }
/*    */ 
/*    */   public float getMiddleOfCandle(int index)
/*    */   {
/* 25 */     IBar[] candles = getCandles();
/*    */ 
/* 27 */     long time = -1L;
/*    */ 
/* 29 */     if (index >= candles.length) {
/* 30 */       time = ((AbstractPriceAggregationDataSequence)this.dataSequence).getTo() + ((AbstractPriceAggregationDataSequence)this.dataSequence).getInterpolatedTimeInterval(index);
/*    */     }
/* 32 */     else if (index < 0) {
/* 33 */       time = ((AbstractPriceAggregationDataSequence)this.dataSequence).getFrom() - ((AbstractPriceAggregationDataSequence)this.dataSequence).getInterpolatedTimeInterval(index);
/*    */     }
/*    */     else {
/* 36 */       time = candles[index].getTime();
/*    */     }
/*    */ 
/* 39 */     return this.timeToXMapper.xt(time);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.indicators.DefaultPriceAggregationIndicatorDrawingSupport
 * JD-Core Version:    0.6.0
 */