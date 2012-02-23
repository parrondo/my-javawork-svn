/*    */ package com.dukascopy.charts.data.datacache.priceaggregation.dataprovider;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader;
/*    */ 
/*    */ public class BarsLoadingProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   private final LoadingProgressListener loadingProgressListener;
/*    */   private final IBarsWithInProgressBarCheckLoader<?> loader;
/*    */ 
/*    */   public BarsLoadingProgressListener(LoadingProgressListener loadingProgressListener, IBarsWithInProgressBarCheckLoader<?> loader)
/*    */   {
/* 22 */     this.loadingProgressListener = loadingProgressListener;
/* 23 */     this.loader = loader;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long start, long end, long currentPosition, String information)
/*    */   {
/* 28 */     this.loadingProgressListener.dataLoaded(start, end, currentPosition, information);
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*    */   {
/* 33 */     this.loader.removeInProgressListener();
/* 34 */     this.loadingProgressListener.loadingFinished(allDataLoaded, start, end, currentPosition, e);
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 39 */     return this.loadingProgressListener.stopJob();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.BarsLoadingProgressListener
 * JD-Core Version:    0.6.0
 */