/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Point;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class SubChartPanelMouseCursorDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   final SubIndicatorGroup subIndicatorGroup;
/*    */   final MouseControllerMetaDrawingsState mouseControllerMetaDrawingState;
/*    */ 
/*    */   SubChartPanelMouseCursorDisplayableDataPart(SubIndicatorGroup subIndicatorGroup, ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingState)
/*    */   {
/* 25 */     super(LoggerFactory.getLogger(SubChartPanelMouseCursorDisplayableDataPart.class), chartState);
/* 26 */     this.subIndicatorGroup = subIndicatorGroup;
/* 27 */     this.mouseControllerMetaDrawingState = mouseControllerMetaDrawingState;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 32 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.META));
/*    */ 
/* 34 */     if (this.chartState.isMouseCrossCursorVisible()) {
/* 35 */       drawMouseCursor(g, jComponent);
/*    */     }
/*    */ 
/* 38 */     if (this.mouseControllerMetaDrawingState.isMeasuringCandlesLine()) {
/* 39 */       Point p1 = this.mouseControllerMetaDrawingState.get1MeasuringCandlesLinePoint();
/* 40 */       Point p2 = this.mouseControllerMetaDrawingState.get2MeasuringCandlesLinePoint();
/* 41 */       if (p1 != null) {
/* 42 */         g.drawLine(p1.x, 0, p1.x, jComponent.getHeight());
/*    */       }
/* 44 */       if (p2 != null)
/* 45 */         g.drawLine(p2.x, 0, p2.x, jComponent.getHeight());
/*    */     }
/*    */   }
/*    */ 
/*    */   void drawMouseCursor(Graphics g, JComponent jComponent)
/*    */   {
/* 51 */     Point mouseCursorPoint = this.chartState.getMouseCursorPoint();
/*    */ 
/* 53 */     if (mouseCursorPoint.x >= 0) {
/* 54 */       g.drawLine(mouseCursorPoint.x, 0, mouseCursorPoint.x, jComponent.getHeight());
/*    */     }
/*    */ 
/* 57 */     if (this.chartState.isMouseCursorOnWindow(this.subIndicatorGroup.getSubWindowId()))
/* 58 */       g.drawLine(0, mouseCursorPoint.y, jComponent.getWidth(), mouseCursorPoint.y);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.SubChartPanelMouseCursorDisplayableDataPart
 * JD-Core Version:    0.6.0
 */