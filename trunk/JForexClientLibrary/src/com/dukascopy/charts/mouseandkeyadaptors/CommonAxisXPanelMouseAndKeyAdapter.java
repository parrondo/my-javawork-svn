/*    */ package com.dukascopy.charts.mouseandkeyadaptors;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*    */ import java.awt.Component;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.event.MouseEvent;
/*    */ 
/*    */ class CommonAxisXPanelMouseAndKeyAdapter extends ChartsMouseAndKeyAdapter
/*    */ {
/*    */   IMainOperationManager mainOperationManager;
/*    */   int draggedFrom;
/* 14 */   Cursor axisXCursor = new Cursor(10);
/*    */   Cursor savedCursor;
/*    */ 
/*    */   public CommonAxisXPanelMouseAndKeyAdapter(IMainOperationManager mainOperationManager)
/*    */   {
/* 18 */     this.mainOperationManager = mainOperationManager;
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent mouseEvent) {
/* 22 */     super.mouseEntered(mouseEvent);
/* 23 */     Component component = mouseEvent.getComponent();
/* 24 */     this.savedCursor = component.getCursor();
/* 25 */     component.setCursor(this.axisXCursor);
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent mouseEvent) {
/* 29 */     super.mouseExited(mouseEvent);
/* 30 */     mouseEvent.getComponent().setCursor(this.savedCursor);
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent mouseEvent) {
/* 34 */     this.draggedFrom = mouseEvent.getX();
/*    */   }
/*    */ 
/*    */   public void mouseDragged(MouseEvent mouseEvent) {
/* 38 */     if (!this.mouseOverPane) {
/* 39 */       return;
/*    */     }
/*    */ 
/* 42 */     int draggingDiffPx = mouseEvent.getX() - this.draggedFrom;
/* 43 */     this.mainOperationManager.scaleTimeFrame(draggingDiffPx);
/* 44 */     this.draggedFrom = mouseEvent.getX();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.CommonAxisXPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */