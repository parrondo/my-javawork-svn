/*    */ package com.dukascopy.charts.listeners;
/*    */ 
/*    */ import com.dukascopy.charts.listeners.lock.ChartModeChangeListenersRegistry;
/*    */ import com.dukascopy.charts.listeners.lock.DisableEnableListenersRegistry;
/*    */ import com.dukascopy.charts.listeners.verticalmover.VerticalMoverListenerRegistry;
/*    */ 
/*    */ public class ChartSystemListenerManager
/*    */ {
/*    */   private final DisableEnableListenersRegistry disableEnableListenersRegistry;
/*    */   private final ChartsActionListenerRegistry chartsActionListenerRegistry;
/*    */   private final VerticalMoverListenerRegistry verticalMoverListenerRegistry;
/*    */   private final ChartModeChangeListenersRegistry chartModeChangeListenersRegistry;
/*    */ 
/*    */   public ChartSystemListenerManager()
/*    */   {
/* 16 */     this.disableEnableListenersRegistry = new DisableEnableListenersRegistry();
/* 17 */     this.verticalMoverListenerRegistry = new VerticalMoverListenerRegistry();
/* 18 */     this.chartsActionListenerRegistry = new ChartsActionListenerRegistry();
/* 19 */     this.chartModeChangeListenersRegistry = new ChartModeChangeListenersRegistry();
/*    */   }
/*    */ 
/*    */   public DisableEnableListenersRegistry getDisableEnableListenersRegistry() {
/* 23 */     return this.disableEnableListenersRegistry;
/*    */   }
/*    */ 
/*    */   public VerticalMoverListenerRegistry getVerticalMoverListenerRegistry() {
/* 27 */     return this.verticalMoverListenerRegistry;
/*    */   }
/*    */ 
/*    */   public ChartsActionListenerRegistry getChartsActionListenerRegistry() {
/* 31 */     return this.chartsActionListenerRegistry;
/*    */   }
/*    */ 
/*    */   public ChartModeChangeListenersRegistry getChartModeChangeListenersRegistry() {
/* 35 */     return this.chartModeChangeListenersRegistry;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.ChartSystemListenerManager
 * JD-Core Version:    0.6.0
 */