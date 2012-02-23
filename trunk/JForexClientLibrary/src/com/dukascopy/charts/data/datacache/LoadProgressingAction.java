/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ public abstract class LoadProgressingAction
/*    */ {
/*    */   protected final LoadingProgressListener loadingProgress;
/*    */ 
/*    */   protected LoadProgressingAction()
/*    */   {
/* 10 */     this.loadingProgress = null;
/*    */   }
/*    */ 
/*    */   protected LoadProgressingAction(LoadingProgressListener loadingProgress) {
/* 14 */     this.loadingProgress = new LoadingProgressWrapper(loadingProgress, null);
/*    */   }
/*    */ 
/*    */   public void cancel() {
/* 18 */     ((LoadingProgressWrapper)this.loadingProgress).cancel();
/*    */   }
/*    */ 
/*    */   public LoadingProgressListener getLoadingProgress()
/*    */   {
/* 50 */     return this.loadingProgress;
/*    */   }
/*    */ 
/*    */   private static class LoadingProgressWrapper
/*    */     implements LoadingProgressListener
/*    */   {
/*    */     private final LoadingProgressListener loadingProgress;
/*    */     private boolean canceled;
/*    */ 
/*    */     private LoadingProgressWrapper(LoadingProgressListener loadingProgress)
/*    */     {
/* 26 */       this.loadingProgress = loadingProgress;
/*    */     }
/*    */ 
/*    */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*    */     {
/* 31 */       this.loadingProgress.dataLoaded(startTime, endTime, currentTime, information);
/*    */     }
/*    */ 
/*    */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*    */     {
/* 36 */       this.loadingProgress.loadingFinished(allDataLoaded, startTime, endTime, currentTime, e);
/*    */     }
/*    */ 
/*    */     public boolean stopJob()
/*    */     {
/* 41 */       return (this.canceled) || (this.loadingProgress.stopJob());
/*    */     }
/*    */ 
/*    */     public void cancel() {
/* 45 */       this.canceled = true;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadProgressingAction
 * JD-Core Version:    0.6.0
 */