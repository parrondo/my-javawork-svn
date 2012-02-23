/*    */ package com.dukascopy.charts.data.datacache.rangebar;
/*    */ 
/*    */ import com.dukascopy.api.feed.IRangeBar;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ 
/*    */ public class PriceRangeData extends AbstractPriceAggregationData
/*    */   implements IRangeBar
/*    */ {
/*    */   public PriceRangeData()
/*    */   {
/*    */   }
/*    */ 
/*    */   public PriceRangeData(long time, long endTime, double open, double close, double low, double high, double vol, long formedElementsCount)
/*    */   {
/* 26 */     super(time, endTime, open, close, low, high, vol, formedElementsCount);
/*    */   }
/*    */ 
/*    */   public PriceRangeData clone()
/*    */   {
/* 31 */     return (PriceRangeData)super.clone();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.PriceRangeData
 * JD-Core Version:    0.6.0
 */