/*    */ package com.dukascopy.charts.mouseandkeyadaptors;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import java.awt.Component;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.event.MouseEvent;
/*    */ 
/*    */ class SubAxisYPanelMouseAndKeyAdapter extends ChartsMouseAndKeyAdapter
/*    */ {
/*    */   private final IMainOperationManager mainOperationManager;
/*    */   private final SubIndicatorGroup subIndicatorGroup;
/*    */   private int draggedFrom;
/* 18 */   private Cursor axisYCursor = new Cursor(8);
/*    */   private Cursor savedCursor;
/*    */ 
/*    */   public SubAxisYPanelMouseAndKeyAdapter(SubIndicatorGroup subIndicatorGroup, IMainOperationManager mainOperationManager)
/*    */   {
/* 24 */     this.subIndicatorGroup = subIndicatorGroup;
/* 25 */     this.mainOperationManager = mainOperationManager;
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent mouseEvent)
/*    */   {
/* 33 */     super.mouseEntered(mouseEvent);
/* 34 */     Component component = mouseEvent.getComponent();
/* 35 */     this.savedCursor = component.getCursor();
/* 36 */     component.setCursor(this.axisYCursor);
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent mouseEvent)
/*    */   {
/* 44 */     super.mouseExited(mouseEvent);
/* 45 */     mouseEvent.getComponent().setCursor(this.savedCursor);
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent mouseEvent)
/*    */   {
/* 53 */     this.draggedFrom = mouseEvent.getY();
/*    */   }
/*    */ 
/*    */   public void mouseDragged(MouseEvent mouseEvent)
/*    */   {
/* 61 */     if (!this.mouseOverPane) {
/* 62 */       return;
/*    */     }
/*    */ 
/* 65 */     int diff = this.draggedFrom - mouseEvent.getY();
/* 66 */     if (diff > 0)
/* 67 */       this.mainOperationManager.scaleSubChartViewOut(this.subIndicatorGroup);
/* 68 */     else if (diff < 0) {
/* 69 */       this.mainOperationManager.scaleSubChartViewIn(this.subIndicatorGroup);
/*    */     }
/*    */ 
/* 72 */     this.draggedFrom = mouseEvent.getY();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.SubAxisYPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */