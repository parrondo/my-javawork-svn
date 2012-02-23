/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class AbstractPriceAggregationLiveFeedAndProgressListener<D extends AbstractPriceAggregationData>
/*    */   implements IPriceAggregationLiveFeedListener<D>, LoadingProgressListener
/*    */ {
/*    */   private final LoadingProgressListener delegate;
/* 20 */   private List<D> result = new ArrayList();
/*    */ 
/*    */   public AbstractPriceAggregationLiveFeedAndProgressListener(LoadingProgressListener delegate) {
/* 23 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long start, long end, long currentPosition, String information)
/*    */   {
/* 28 */     this.delegate.dataLoaded(start, end, currentPosition, information);
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*    */   {
/* 33 */     this.delegate.loadingFinished(allDataLoaded, start, end, currentPosition, e);
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 38 */     return this.delegate.stopJob();
/*    */   }
/*    */ 
/*    */   public List<D> getResult() {
/* 42 */     return this.result;
/*    */   }
/*    */ 
/*    */   public void newPriceData(D data)
/*    */   {
/* 47 */     this.result.add(data);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationLiveFeedAndProgressListener
 * JD-Core Version:    0.6.0
 */