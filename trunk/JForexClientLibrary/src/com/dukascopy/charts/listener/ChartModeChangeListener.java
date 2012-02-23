/*    */ package com.dukascopy.charts.listener;
/*    */ 
/*    */ public abstract interface ChartModeChangeListener
/*    */ {
/*    */   public abstract void chartModeChanged(ChartMode paramChartMode);
/*    */ 
/*    */   public static enum ChartMode
/*    */   {
/*  9 */     CHART, 
/* 10 */     TABLE;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.listener.ChartModeChangeListener
 * JD-Core Version:    0.6.0
 */