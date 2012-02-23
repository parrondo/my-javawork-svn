/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ public abstract class LoadingProgressAdapter
/*    */   implements LoadingProgressListener
/*    */ {
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
/* 18 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadingProgressAdapter
 * JD-Core Version:    0.6.0
 */