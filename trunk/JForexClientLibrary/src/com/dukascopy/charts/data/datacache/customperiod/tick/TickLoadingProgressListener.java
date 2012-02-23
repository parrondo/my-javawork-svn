/*    */ package com.dukascopy.charts.data.datacache.customperiod.tick;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class TickLoadingProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   private final LoadingProgressListener loadingProgressListenerDelegate;
/*    */ 
/*    */   public TickLoadingProgressListener(LoadingProgressListener loadingProgressListenerDelegate)
/*    */   {
/* 14 */     this.loadingProgressListenerDelegate = loadingProgressListenerDelegate;
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
/* 42 */     return getLoadingProgressListenerDelegate().stopJob();
/*    */   }
/*    */ 
/*    */   public LoadingProgressListener getLoadingProgressListenerDelegate() {
/* 46 */     return this.loadingProgressListenerDelegate;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.tick.TickLoadingProgressListener
 * JD-Core Version:    0.6.0
 */