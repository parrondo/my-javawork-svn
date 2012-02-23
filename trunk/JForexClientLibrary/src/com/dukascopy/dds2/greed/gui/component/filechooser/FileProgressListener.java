/*    */ package com.dukascopy.dds2.greed.gui.component.filechooser;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ 
/*    */ public class FileProgressListener
/*    */   implements LoadingProgressListener
/*    */ {
/*  8 */   private boolean cancel = false;
/*    */ 
/* 10 */   private boolean threadStopped = false;
/*    */ 
/* 12 */   private boolean threadStarted = false;
/*    */ 
/*    */   public void dataLoaded(long start, long end, long currentPosition, String information)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void cancelLoading()
/*    */   {
/* 27 */     this.cancel = true;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 31 */     this.threadStopped = false;
/* 32 */     this.threadStarted = false;
/* 33 */     this.cancel = false;
/*    */   }
/*    */ 
/*    */   public boolean isThreadStarted() {
/* 37 */     return this.threadStarted;
/*    */   }
/*    */ 
/*    */   public void setThreadStarted() {
/* 41 */     this.threadStarted = true;
/*    */   }
/*    */ 
/*    */   public boolean isThreadStopped() {
/* 45 */     return this.threadStopped;
/*    */   }
/*    */ 
/*    */   public void setThreadStopped() {
/* 49 */     this.threadStopped = true;
/*    */   }
/*    */ 
/*    */   public boolean stopJob()
/*    */   {
/* 54 */     return this.cancel;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener
 * JD-Core Version:    0.6.0
 */