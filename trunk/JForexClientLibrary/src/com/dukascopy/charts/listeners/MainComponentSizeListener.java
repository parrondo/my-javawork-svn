/*    */ package com.dukascopy.charts.listeners;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.GeometryOperationManagerListener;
/*    */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*    */ import java.awt.Component;
/*    */ import java.awt.event.ComponentAdapter;
/*    */ import java.awt.event.ComponentEvent;
/*    */ import java.awt.event.HierarchyBoundsListener;
/*    */ import java.awt.event.HierarchyEvent;
/*    */ 
/*    */ public class MainComponentSizeListener extends ComponentAdapter
/*    */   implements HierarchyBoundsListener
/*    */ {
/*    */   private final GuiRefresher guiRefresher;
/*    */   private final GeometryOperationManagerListener geometryOperationManagerListener;
/* 16 */   private int width = -1;
/* 17 */   private int height = -1;
/*    */ 
/*    */   public MainComponentSizeListener(GuiRefresher guiRefresher, GeometryOperationManagerListener geometryOperationManagerListener)
/*    */   {
/* 22 */     this.guiRefresher = guiRefresher;
/* 23 */     this.geometryOperationManagerListener = geometryOperationManagerListener;
/*    */   }
/*    */ 
/*    */   public void componentResized(ComponentEvent event) {
/* 27 */     int newWidth = event.getComponent().getWidth();
/* 28 */     int newHeight = event.getComponent().getHeight();
/*    */ 
/* 30 */     resize(newWidth, newHeight);
/*    */   }
/*    */ 
/*    */   private void resize(int newWidth, int newHeight) {
/* 34 */     if ((newWidth <= 0) || (newHeight <= 0)) {
/* 35 */       return;
/*    */     }
/*    */ 
/* 38 */     if ((newWidth == this.width) && (newHeight == this.height)) {
/* 39 */       return;
/*    */     }
/*    */ 
/* 42 */     this.geometryOperationManagerListener.componentSizeChanged(newWidth, newHeight);
/* 43 */     this.guiRefresher.refreshAllContent();
/*    */ 
/* 45 */     this.width = newWidth;
/* 46 */     this.height = newHeight;
/*    */   }
/*    */ 
/*    */   public void ancestorMoved(HierarchyEvent e)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void ancestorResized(HierarchyEvent e)
/*    */   {
/* 57 */     int newWidth = e.getComponent().getWidth();
/* 58 */     int newHeight = e.getComponent().getHeight();
/*    */ 
/* 60 */     resize(newWidth, newHeight);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.MainComponentSizeListener
 * JD-Core Version:    0.6.0
 */