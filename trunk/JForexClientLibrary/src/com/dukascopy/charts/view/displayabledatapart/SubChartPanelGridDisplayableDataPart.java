/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*    */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.GridType;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.charts.utils.GraphicHelper;
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class SubChartPanelGridDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/* 27 */   final GeneralPath gridPath = new GeneralPath();
/*    */   final MouseControllerMetaDrawingsState mouseControllerMetaDrawingState;
/*    */ 
/*    */   SubChartPanelGridDisplayableDataPart(ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingState)
/*    */   {
/* 34 */     super(LoggerFactory.getLogger(SubChartPanelGridDisplayableDataPart.class.getName()), chartState);
/* 35 */     this.mouseControllerMetaDrawingState = mouseControllerMetaDrawingState;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent) {
/* 39 */     Color color = g.getColor();
/* 40 */     Font font = g.getFont();
/*    */ 
/* 42 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*    */ 
/* 44 */     ChartSettings.GridType gridType = (ChartSettings.GridType)ChartSettings.get(ChartSettings.Option.GRID);
/* 45 */     if (gridType != ChartSettings.GridType.NONE) {
/* 46 */       drawGrid(g, jComponent);
/*    */     }
/*    */ 
/* 49 */     drawChartShiftLine(g, jComponent);
/*    */ 
/* 51 */     g.setFont(font);
/* 52 */     g.setColor(color);
/*    */   }
/*    */ 
/*    */   void drawChartShiftLine(Graphics g, JComponent jComponent) {
/* 56 */     if (!this.mouseControllerMetaDrawingState.isChartShiftHandlerBeeingShifted()) {
/* 57 */       return;
/*    */     }
/*    */ 
/* 60 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.META));
/*    */ 
/* 62 */     int x = jComponent.getWidth() - this.chartState.getChartShiftHandlerCoordinate();
/* 63 */     g.drawLine(x, 0, x, jComponent.getHeight() - 1);
/*    */   }
/*    */ 
/*    */   void drawGrid(Graphics g, JComponent jComponent) {
/* 67 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.GRID));
/* 68 */     this.gridPath.reset();
/* 69 */     BasicStroke gridStroke = this.chartState.getTheme().getStroke(ITheme.StrokeElement.GRID_STROKE);
/*    */ 
/* 71 */     for (int px = jComponent.getWidth() - 30 - 1; px > 0; px -= 30) {
/* 72 */       GraphicHelper.drawVerticalDashedLine(this.gridPath, px, 0.0D, jComponent.getHeight(), gridStroke);
/*    */     }
/*    */ 
/* 75 */     ((Graphics2D)g).draw(this.gridPath);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.SubChartPanelGridDisplayableDataPart
 * JD-Core Version:    0.6.0
 */