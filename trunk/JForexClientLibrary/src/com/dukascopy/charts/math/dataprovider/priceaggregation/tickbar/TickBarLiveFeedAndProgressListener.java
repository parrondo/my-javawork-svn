/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationLiveFeedAndProgressListener;
/*    */ 
/*    */ public class TickBarLiveFeedAndProgressListener extends AbstractPriceAggregationLiveFeedAndProgressListener<TickBarData>
/*    */   implements ITickBarLiveFeedListener
/*    */ {
/*    */   public TickBarLiveFeedAndProgressListener(LoadingProgressListener delegate)
/*    */   {
/* 18 */     super(delegate);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarLiveFeedAndProgressListener
 * JD-Core Version:    0.6.0
 */