/*    */ package com.dukascopy.charts.chartbuilder;
/*    */ 
/*    */ import com.dukascopy.api.IChart.Type;
/*    */ import com.dukascopy.charts.listeners.drawing.DrawingActionListener;
/*    */ import com.dukascopy.charts.listeners.orders.OrdersActionListener;
/*    */ import com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsActionListener;
/*    */ import com.dukascopy.charts.view.staticdynamicdata.ViewMode;
/*    */ import com.dukascopy.charts.view.staticdynamicdata.ViewModeChangeListener;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class ViewModeChangeListenerRegistryImpl
/*    */   implements ViewModeChangeListenerRegistry, DrawingActionListener, OrdersActionListener, MetaDrawingsActionListener
/*    */ {
/* 20 */   private static final Logger LOGGER = LoggerFactory.getLogger(ViewModeChangeListenerRegistryImpl.class);
/*    */ 
/* 22 */   final List<ViewModeChangeListener> listeners = new ArrayList();
/*    */   ViewMode currentViewMode;
/*    */ 
/*    */   ViewModeChangeListenerRegistryImpl(ViewMode initialViewMode)
/*    */   {
/* 27 */     this.currentViewMode = initialViewMode;
/*    */   }
/*    */ 
/*    */   public void registerViewModeChangeListener(ViewModeChangeListener viewModeChangeListener) {
/* 31 */     viewModeChangeListener.viewModeChanged(this.currentViewMode);
/* 32 */     this.listeners.add(viewModeChangeListener);
/*    */   }
/*    */ 
/*    */   void notify(ViewMode newViewMode)
/*    */   {
/* 37 */     this.currentViewMode = newViewMode;
/* 38 */     for (ViewModeChangeListener listener : this.listeners)
/* 39 */       listener.viewModeChanged(newViewMode);
/*    */   }
/*    */ 
/*    */   public void zoomingToAreaStarted()
/*    */   {
/* 46 */     notify(ViewMode.META_DRAWINGS);
/*    */   }
/*    */ 
/*    */   public void zoomingToAreaEnded() {
/* 50 */     finish();
/*    */   }
/*    */ 
/*    */   public void measuringCandlesStarted()
/*    */   {
/* 56 */     notify(ViewMode.META_DRAWINGS);
/*    */   }
/*    */ 
/*    */   public void measuringCandlesEnded() {
/* 60 */     finish();
/*    */   }
/*    */ 
/*    */   public void drawingStarted(IChart.Type drawingType)
/*    */   {
/* 66 */     notify(ViewMode.DRAWING);
/*    */   }
/*    */ 
/*    */   public void drawingEnded() {
/* 70 */     finish();
/*    */   }
/*    */ 
/*    */   public void drawingEditingStarted()
/*    */   {
/* 76 */     notify(ViewMode.DRAWING_EDITING);
/*    */   }
/*    */ 
/*    */   public void drawingEditingEnded() {
/* 80 */     finish();
/*    */   }
/*    */ 
/*    */   public void ordersEditingStarted()
/*    */   {
/* 86 */     notify(ViewMode.ORDER_EDITING);
/*    */   }
/*    */ 
/*    */   public void ordersEditingEnded() {
/* 90 */     finish();
/*    */   }
/*    */ 
/*    */   void finish()
/*    */   {
/* 96 */     notify(ViewMode.ALL_STATIC);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ViewModeChangeListenerRegistryImpl
 * JD-Core Version:    0.6.0
 */