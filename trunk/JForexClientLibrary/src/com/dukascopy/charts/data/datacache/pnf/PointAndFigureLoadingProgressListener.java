/*    */ package com.dukascopy.charts.data.datacache.pnf;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class PointAndFigureLoadingProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   private final LoadingProgressListener loadingProgressListenerDelegate;
/*    */ 
/*    */   public PointAndFigureLoadingProgressListener(LoadingProgressListener loadingProgressListenerDelegate)
/*    */   {
/* 10 */     this.loadingProgressListenerDelegate = loadingProgressListenerDelegate;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long start, long end, long currentPosition, String information)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 36 */     return getLoadingProgressListenerDelegate().stopJob();
/*    */   }
/*    */ 
/*    */   public LoadingProgressListener getLoadingProgressListenerDelegate() {
/* 40 */     return this.loadingProgressListenerDelegate;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.PointAndFigureLoadingProgressListener
 * JD-Core Version:    0.6.0
 */