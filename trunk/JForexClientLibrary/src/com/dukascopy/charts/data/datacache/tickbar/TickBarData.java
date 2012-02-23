/*    */ package com.dukascopy.charts.data.datacache.tickbar;
/*    */ 
/*    */ import com.dukascopy.api.feed.ITickBar;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ 
/*    */ public class TickBarData extends AbstractPriceAggregationData
/*    */   implements ITickBar
/*    */ {
/*    */   public TickBarData()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TickBarData(long time, long endTime, double open, double close, double low, double high, double vol, long formedElementsCount)
/*    */   {
/* 26 */     super(time, endTime, open, close, low, high, vol, formedElementsCount);
/*    */   }
/*    */ 
/*    */   public TickBarData clone()
/*    */   {
/* 31 */     return (TickBarData)super.clone();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.TickBarData
 * JD-Core Version:    0.6.0
 */