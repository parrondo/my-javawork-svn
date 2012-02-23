/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.renko;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationLiveFeedAndProgressListener;
/*    */ 
/*    */ public class RenkoLiveFeedAndProgressListener extends AbstractPriceAggregationLiveFeedAndProgressListener<RenkoData>
/*    */   implements IRenkoLiveFeedListener
/*    */ {
/*    */   public RenkoLiveFeedAndProgressListener(LoadingProgressListener delegate)
/*    */   {
/* 18 */     super(delegate);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoLiveFeedAndProgressListener
 * JD-Core Version:    0.6.0
 */