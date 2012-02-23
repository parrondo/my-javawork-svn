/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationLiveFeedAndProgressListener;
/*    */ 
/*    */ public class PriceRangeLiveFeedAndProgressListener extends AbstractPriceAggregationLiveFeedAndProgressListener<PriceRangeData>
/*    */   implements IPriceRangeLiveFeedListener
/*    */ {
/*    */   public PriceRangeLiveFeedAndProgressListener(LoadingProgressListener delegate)
/*    */   {
/* 18 */     super(delegate);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeLiveFeedAndProgressListener
 * JD-Core Version:    0.6.0
 */