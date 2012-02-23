/*    */ package com.dukascopy.charts.data.datacache.preloader;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*    */ 
/*    */ public abstract class DataCachePreloadControl
/*    */ {
/* 16 */   private boolean stopped = false;
/*    */ 
/*    */   public void stop()
/*    */   {
/* 23 */     synchronized (this) {
/* 24 */       this.stopped = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isStopped() {
/* 29 */     synchronized (this) {
/* 30 */       return this.stopped;
/*    */     }
/*    */   }
/*    */ 
/*    */   public abstract void loadingProcessStarted();
/*    */ 
/*    */   public abstract void loadingProcessFinished(boolean paramBoolean);
/*    */ 
/*    */   public abstract void exceptionThrown(Instrument paramInstrument, TimeInterval paramTimeInterval, Throwable paramThrowable);
/*    */ 
/*    */   public abstract void loadingStarted(Instrument paramInstrument, TimeInterval paramTimeInterval);
/*    */ 
/*    */   public abstract void loadingFinished(Instrument paramInstrument, TimeInterval paramTimeInterval, boolean paramBoolean);
/*    */ 
/*    */   public abstract void loadingProcessed(double paramDouble);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.preloader.DataCachePreloadControl
 * JD-Core Version:    0.6.0
 */