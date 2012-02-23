/*    */ package com.dukascopy.charts.indicators;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ 
/*    */ public class TicksIndicatorDrawingSupport extends AbstractIndicatorDrawingSupport<TickDataSequence, TickData>
/*    */ {
/*    */   public TicksIndicatorDrawingSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 14 */     super(geometryCalculator);
/*    */   }
/*    */ 
/*    */   public IBar[] getCandles()
/*    */   {
/* 19 */     return ((TickDataSequence)this.dataSequence).getOneSecCandlesAsk();
/*    */   }
/*    */ 
/*    */   protected int getExtraBefore()
/*    */   {
/* 24 */     return ((TickDataSequence)this.dataSequence).getOneSecExtraBefore();
/*    */   }
/*    */ 
/*    */   public int getNumberOfCandlesOnScreen()
/*    */   {
/* 29 */     TickDataSequence tickDataSequence = (TickDataSequence)this.dataSequence;
/* 30 */     return tickDataSequence.getOneSecCandlesAsk().length - tickDataSequence.getOneSecExtraBefore() - tickDataSequence.getOneSecExtraAfter();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.indicators.TicksIndicatorDrawingSupport
 * JD-Core Version:    0.6.0
 */