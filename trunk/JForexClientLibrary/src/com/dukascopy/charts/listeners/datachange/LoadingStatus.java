/*    */ package com.dukascopy.charts.listeners.datachange;
/*    */ 
/*    */ public class LoadingStatus
/*    */ {
/*  5 */   boolean isLoading = false;
/*    */ 
/*    */   public synchronized void loadingStarted() {
/*  8 */     this.isLoading = true;
/*    */   }
/*    */ 
/*    */   public synchronized void loadingFinished() {
/* 12 */     this.isLoading = false;
/*    */   }
/*    */ 
/*    */   public synchronized boolean getProgress() {
/* 16 */     return this.isLoading;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.datachange.LoadingStatus
 * JD-Core Version:    0.6.0
 */