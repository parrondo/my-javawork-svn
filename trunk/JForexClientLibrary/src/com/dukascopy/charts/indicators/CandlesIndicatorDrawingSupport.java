/*    */ package com.dukascopy.charts.indicators;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ 
/*    */ public class CandlesIndicatorDrawingSupport extends AbstractIndicatorDrawingSupport<CandleDataSequence, CandleData>
/*    */ {
/*    */   public CandlesIndicatorDrawingSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 13 */     super(geometryCalculator);
/*    */   }
/*    */ 
/*    */   public IBar[] getCandles()
/*    */   {
/* 18 */     return (IBar[])((CandleDataSequence)this.dataSequence).getData();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.indicators.CandlesIndicatorDrawingSupport
 * JD-Core Version:    0.6.0
 */