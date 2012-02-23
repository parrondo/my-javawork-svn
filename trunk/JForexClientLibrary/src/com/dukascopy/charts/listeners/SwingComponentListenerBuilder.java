/*    */ package com.dukascopy.charts.listeners;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.chartbuilder.GeometryOperationManagerListener;
/*    */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*    */ import com.dukascopy.charts.chartbuilder.ISwingComponentListenerBuilder;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.ComponentAdapter;
/*    */ import java.awt.event.ComponentEvent;
/*    */ import java.awt.event.ComponentListener;
/*    */ 
/*    */ public class SwingComponentListenerBuilder
/*    */   implements ISwingComponentListenerBuilder
/*    */ {
/*    */   final GuiRefresher guiRefresher;
/*    */   final GeometryOperationManagerListener geometryOperationManagerListener;
/*    */   final Runnable taskToBeRunAfterResize;
/*    */ 
/*    */   public SwingComponentListenerBuilder(GuiRefresher guiRefresher, GeometryOperationManagerListener geometryOperationManagerListener, Runnable taskToBeRunAfterResize)
/*    */   {
/* 25 */     this.guiRefresher = guiRefresher;
/* 26 */     this.geometryOperationManagerListener = geometryOperationManagerListener;
/* 27 */     this.taskToBeRunAfterResize = taskToBeRunAfterResize;
/*    */   }
/*    */ 
/*    */   public MainComponentSizeListener createMainComponentListener() {
/* 31 */     return new MainComponentSizeListener(this.guiRefresher, this.geometryOperationManagerListener);
/*    */   }
/*    */ 
/*    */   public ComponentListener createSubComponentListener(SubIndicatorGroup subIndicatorGroup) {
/* 35 */     return new SubComponentSizeListener(this.guiRefresher, this.geometryOperationManagerListener, subIndicatorGroup);
/*    */   }
/*    */ 
/*    */   public void addSubIndicatorToSubChartView(IndicatorWrapper indicatorWrapper, int previousSubHeight) {
/* 39 */     this.geometryOperationManagerListener.subIndicatorAdded(indicatorWrapper, previousSubHeight);
/*    */   }
/*    */ 
/*    */   public int deleteSubIndicatorFromSubChartView(IndicatorWrapper indicatorWrapper) {
/* 43 */     return this.geometryOperationManagerListener.subIndicatorDeleted(indicatorWrapper);
/*    */   }
/*    */ 
/*    */   public ComponentListener createFirstResizeListenerToRunTask() {
/* 47 */     return new ComponentAdapter() {
/*    */       public void componentResized(ComponentEvent event) {
/* 49 */         Dimension componentsSize = event.getComponent().getSize();
/* 50 */         if (componentsSize.getWidth() <= 0.0D) {
/* 51 */           return;
/*    */         }
/* 53 */         SwingComponentListenerBuilder.this.taskToBeRunAfterResize.run();
/* 54 */         event.getComponent().removeComponentListener(this);
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.SwingComponentListenerBuilder
 * JD-Core Version:    0.6.0
 */