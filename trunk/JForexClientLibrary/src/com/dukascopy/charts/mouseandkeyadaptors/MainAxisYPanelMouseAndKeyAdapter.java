/*    */ package com.dukascopy.charts.mouseandkeyadaptors;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*    */ import com.dukascopy.charts.data.IOrdersDataProviderManager;
/*    */ import com.dukascopy.charts.mappers.IMapper;
/*    */ import java.awt.Component;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.event.MouseEvent;
/*    */ 
/*    */ class MainAxisYPanelMouseAndKeyAdapter extends ChartsMouseAndKeyAdapter
/*    */ {
/*    */   final ChartState chartState;
/*    */   final IMapper mapper;
/*    */   final IMainOperationManager mainOperationManager;
/*    */   final IOrdersDataProviderManager ordersDataProviderManager;
/*    */   int draggedFrom;
/* 21 */   Cursor axisYCursor = new Cursor(8);
/*    */   Cursor savedCursor;
/*    */ 
/*    */   public MainAxisYPanelMouseAndKeyAdapter(ChartState chartState, IMapper mapper, IMainOperationManager mainOperationManager, IOrdersDataProviderManager ordersDataProviderManager)
/*    */   {
/* 30 */     this.chartState = chartState;
/* 31 */     this.mapper = mapper;
/* 32 */     this.mainOperationManager = mainOperationManager;
/* 33 */     this.ordersDataProviderManager = ordersDataProviderManager;
/* 34 */     this.mouseOverPane = false;
/*    */   }
/*    */ 
/*    */   public void mouseEntered(MouseEvent mouseEvent) {
/* 38 */     super.mouseEntered(mouseEvent);
/* 39 */     Component component = mouseEvent.getComponent();
/* 40 */     this.savedCursor = component.getCursor();
/* 41 */     component.setCursor(this.axisYCursor);
/*    */   }
/*    */ 
/*    */   public void mouseExited(MouseEvent mouseEvent) {
/* 45 */     super.mouseExited(mouseEvent);
/* 46 */     mouseEvent.getComponent().setCursor(this.savedCursor);
/*    */   }
/*    */ 
/*    */   public void mousePressed(MouseEvent e) {
/* 50 */     this.draggedFrom = e.getY();
/*    */   }
/*    */ 
/*    */   public void mouseDragged(MouseEvent mouseEvent) {
/* 54 */     if (!this.mouseOverPane) {
/* 55 */       return;
/*    */     }
/*    */ 
/* 58 */     int diff = this.draggedFrom - mouseEvent.getY();
/* 59 */     if (diff > 0)
/* 60 */       this.mainOperationManager.scaleMainChartViewOut(mouseEvent.getComponent().getHeight());
/* 61 */     else if (diff < 0) {
/* 62 */       this.mainOperationManager.scaleMainChartViewIn(mouseEvent.getComponent().getHeight());
/*    */     }
/*    */ 
/* 65 */     this.draggedFrom = mouseEvent.getY();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.MainAxisYPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */