/*    */ package com.dukascopy.charts.data.datacache.customperiod.candle;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class CustomPeriodCandleLoadingProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   private final LoadingProgressListener originalLoadingProgressListener;
/*    */   private final CustomPeriodCandleLiveFeedListener liveFeedListener;
/*    */ 
/*    */   public CustomPeriodCandleLoadingProgressListener(LoadingProgressListener originalLoadingProgressListener, CustomPeriodCandleLiveFeedListener liveFeedListener)
/*    */   {
/* 18 */     this.originalLoadingProgressListener = originalLoadingProgressListener;
/* 19 */     this.liveFeedListener = liveFeedListener;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*    */   {
/* 24 */     getOriginalLoadingProgressListener().dataLoaded(startTime, endTime, currentTime, information);
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*    */   {
/* 29 */     getLiveFeedListener().finishLoading(allDataLoaded, startTime, endTime, currentTime);
/* 30 */     getOriginalLoadingProgressListener().loadingFinished(allDataLoaded, startTime, endTime, currentTime, e);
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 35 */     return getOriginalLoadingProgressListener().stopJob();
/*    */   }
/*    */ 
/*    */   public LoadingProgressListener getOriginalLoadingProgressListener() {
/* 39 */     return this.originalLoadingProgressListener;
/*    */   }
/*    */ 
/*    */   public CustomPeriodCandleLiveFeedListener getLiveFeedListener() {
/* 43 */     return this.liveFeedListener;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodCandleLoadingProgressListener
 * JD-Core Version:    0.6.0
 */