/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Point;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class MainChartPanelMouseCursorDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   MainChartPanelMouseCursorDisplayableDataPart(ChartState chartState)
/*    */   {
/* 16 */     super(LoggerFactory.getLogger(MainChartPanelMouseCursorDisplayableDataPart.class), chartState);
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 21 */     if (this.chartState.isMouseCrossCursorVisible()) {
/* 22 */       g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.META));
/* 23 */       drawMouseCursor(g, jComponent);
/*    */     }
/*    */   }
/*    */ 
/*    */   void drawMouseCursor(Graphics g, JComponent jComponent) {
/* 28 */     Point mouseCursorPoint = this.chartState.getMouseCursorPoint();
/*    */ 
/* 30 */     if (mouseCursorPoint == null) {
/* 31 */       return;
/*    */     }
/*    */ 
/* 34 */     if (mouseCursorPoint.x > 0) {
/* 35 */       g.drawLine(mouseCursorPoint.x, 1, mouseCursorPoint.x, jComponent.getHeight());
/*    */     }
/*    */ 
/* 38 */     if (this.chartState.isMouseCursorOnWindow(-1))
/* 39 */       g.drawLine(1, mouseCursorPoint.y, jComponent.getWidth(), mouseCursorPoint.y);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.MainChartPanelMouseCursorDisplayableDataPart
 * JD-Core Version:    0.6.0
 */