/*    */ package com.dukascopy.charts.data.datacache.listener;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class SaveCandlesLoadingProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   private boolean loadedSuccessfully;
/*    */   private Exception exception;
/*    */   private final LoadingProgressListener loadingProgress;
/*    */ 
/*    */   public SaveCandlesLoadingProgressListener(LoadingProgressListener loadingProgress)
/*    */   {
/* 21 */     this.loadingProgress = loadingProgress;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 28 */     this.loadedSuccessfully = true;
/* 29 */     this.exception = e;
/*    */   }
/*    */ 
/*    */   public boolean stopJob() {
/* 33 */     return this.loadingProgress.stopJob();
/*    */   }
/*    */ 
/*    */   public boolean isLoadedSuccessfully() {
/* 37 */     return this.loadedSuccessfully;
/*    */   }
/*    */ 
/*    */   public Exception getException() {
/* 41 */     return this.exception;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.listener.SaveCandlesLoadingProgressListener
 * JD-Core Version:    0.6.0
 */