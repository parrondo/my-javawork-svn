/*    */ package com.dukascopy.charts.listeners.lock;
/*    */ 
/*    */ import com.dukascopy.charts.listener.ChartModeChangeListener;
/*    */ import com.dukascopy.charts.listener.ChartModeChangeListener.ChartMode;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ChartModeChangeListenersRegistry
/*    */ {
/*    */   private List<ChartModeChangeListener> listeners;
/* 17 */   private ChartModeChangeListener.ChartMode chartMode = null;
/*    */ 
/*    */   public void changeMode(ChartModeChangeListener.ChartMode chartMode) {
/* 20 */     setChartMode(chartMode);
/* 21 */     fireModeChanged(chartMode);
/*    */   }
/*    */ 
/*    */   private void fireModeChanged(ChartModeChangeListener.ChartMode chartMode) {
/* 25 */     for (ChartModeChangeListener listener : getListeners())
/* 26 */       listener.chartModeChanged(chartMode);
/*    */   }
/*    */ 
/*    */   public void addListener(ChartModeChangeListener listener)
/*    */   {
/* 31 */     getListeners().add(listener);
/*    */   }
/*    */ 
/*    */   public void removeListener(ChartModeChangeListener listener) {
/* 35 */     getListeners().remove(listener);
/*    */   }
/*    */ 
/*    */   private List<ChartModeChangeListener> getListeners() {
/* 39 */     if (this.listeners == null) {
/* 40 */       this.listeners = new ArrayList();
/*    */     }
/* 42 */     return this.listeners;
/*    */   }
/*    */ 
/*    */   public ChartModeChangeListener.ChartMode getChartMode() {
/* 46 */     return this.chartMode;
/*    */   }
/*    */ 
/*    */   public void setChartMode(ChartModeChangeListener.ChartMode chartMode) {
/* 50 */     this.chartMode = chartMode;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.lock.ChartModeChangeListenersRegistry
 * JD-Core Version:    0.6.0
 */