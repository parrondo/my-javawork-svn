/*    */ package com.dukascopy.charts.math.dataprovider;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CandleDataSequence extends AbstractDataSequence<CandleData>
/*    */ {
/*    */   public CandleDataSequence(Period period, long from, long to, int extraBefore, int extraAfter, CandleData[] data, long[][] gaps, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*    */   {
/* 27 */     super(period, from, to, extraBefore, extraAfter, data, gaps, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*    */ 
/* 40 */     calculateMinMax();
/*    */   }
/*    */ 
/*    */   public void calculateMasterDataMinMax()
/*    */   {
/* 45 */     if (((CandleData[])this.data).length == 0) {
/* 46 */       return;
/*    */     }
/*    */ 
/* 49 */     int minMaxIndexStart = this.extraBefore > 0 ? this.extraBefore - 1 : 0;
/* 50 */     int minMaxIndexEnd = ((CandleData[])this.data).length - (this.extraAfter > 0 ? this.extraAfter - 1 : 0);
/* 51 */     this.min = ((CandleData[])this.data)[minMaxIndexStart].low;
/* 52 */     this.max = ((CandleData[])this.data)[minMaxIndexStart].high;
/*    */ 
/* 54 */     for (int i = minMaxIndexStart; i < minMaxIndexEnd; i++) {
/* 55 */       CandleData curChartsCandle = ((CandleData[])this.data)[i];
/*    */ 
/* 57 */       double high = curChartsCandle.high;
/* 58 */       double low = curChartsCandle.low;
/* 59 */       this.max = (this.max > high ? this.max : high);
/* 60 */       this.min = (this.min > low ? low : this.min);
/*    */     }
/*    */ 
/* 63 */     if (this.max != this.min) {
/* 64 */       return;
/*    */     }
/* 66 */     this.max += 1.0D;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.CandleDataSequence
 * JD-Core Version:    0.6.0
 */