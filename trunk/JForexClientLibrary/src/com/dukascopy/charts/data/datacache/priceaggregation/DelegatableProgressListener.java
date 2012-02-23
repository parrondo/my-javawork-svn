/*    */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class DelegatableProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   private final LoadingProgressListener loadingProgressListenerDelegate;
/*    */ 
/*    */   public DelegatableProgressListener(LoadingProgressListener loadingProgressListenerDelegate)
/*    */   {
/* 13 */     this.loadingProgressListenerDelegate = loadingProgressListenerDelegate;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 41 */     return getLoadingProgressListenerDelegate().stopJob();
/*    */   }
/*    */ 
/*    */   public LoadingProgressListener getLoadingProgressListenerDelegate() {
/* 45 */     return this.loadingProgressListenerDelegate;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.DelegatableProgressListener
 * JD-Core Version:    0.6.0
 */