/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.listener;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class LastTickProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*    */   public boolean allDataLoaded;
/*    */   public Exception exception;
/*    */   private LoadingProgressListener progressListener;
/*    */ 
/*    */   public LastTickProgressListener(LoadingProgressListener progressListener)
/*    */   {
/* 16 */     this.progressListener = progressListener;
/*    */   }
/*    */ 
/*    */   public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*    */   {
/* 25 */     this.allDataLoaded = allDataLoaded;
/* 26 */     this.exception = e;
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 31 */     return this.progressListener.stopJob();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.listener.LastTickProgressListener
 * JD-Core Version:    0.6.0
 */