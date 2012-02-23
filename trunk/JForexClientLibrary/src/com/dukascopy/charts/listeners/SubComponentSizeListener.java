/*    */ package com.dukascopy.charts.listeners;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.chartbuilder.GeometryOperationManagerListener;
/*    */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import java.awt.Component;
/*    */ import java.awt.event.ComponentAdapter;
/*    */ import java.awt.event.ComponentEvent;
/*    */ import java.util.List;
/*    */ 
/*    */ class SubComponentSizeListener extends ComponentAdapter
/*    */ {
/*    */   final GuiRefresher guiRefresher;
/*    */   final GeometryOperationManagerListener geometryOperationManagerListener;
/*    */   final SubIndicatorGroup subIndicatorGroup;
/* 29 */   boolean widthChanged = false;
/* 30 */   boolean heightChanged = false;
/*    */ 
/* 32 */   int width = -1;
/* 33 */   int height = -1;
/*    */ 
/*    */   SubComponentSizeListener(GuiRefresher guiRefresher, GeometryOperationManagerListener geometryOperationManagerListener, SubIndicatorGroup subIndicatorGroup)
/*    */   {
/* 23 */     this.guiRefresher = guiRefresher;
/* 24 */     this.geometryOperationManagerListener = geometryOperationManagerListener;
/* 25 */     this.subIndicatorGroup = subIndicatorGroup;
/*    */   }
/*    */ 
/*    */   public void componentResized(ComponentEvent event)
/*    */   {
/* 37 */     int newHeight = event.getComponent().getHeight();
/*    */ 
/* 39 */     if (newHeight == 0) {
/* 40 */       return;
/*    */     }
/*    */ 
/* 43 */     this.heightChanged = (this.height != newHeight);
/*    */ 
/* 45 */     if (this.heightChanged) {
/* 46 */       List indicatorWrappers = this.subIndicatorGroup.getSubIndicators();
/* 47 */       for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 48 */         this.geometryOperationManagerListener.subComponentHeightChanged(indicatorWrapper, event.getComponent().getHeight());
/*    */       }
/* 50 */       this.guiRefresher.refreshSubContentBySubViewId(this.subIndicatorGroup.getSubWindowId());
/*    */     }
/*    */ 
/* 53 */     if (this.heightChanged)
/* 54 */       this.height = newHeight;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.SubComponentSizeListener
 * JD-Core Version:    0.6.0
 */