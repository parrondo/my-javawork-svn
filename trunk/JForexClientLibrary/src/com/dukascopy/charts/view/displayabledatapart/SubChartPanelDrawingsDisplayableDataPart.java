/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class SubChartPanelDrawingsDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final IDrawingsManagerContainer drawingsContainer;
/*    */   private final int subWindowId;
/*    */ 
/*    */   SubChartPanelDrawingsDisplayableDataPart(ChartState chartState, IDrawingsManagerContainer drawingsManagerContainer, int subWindowId)
/*    */   {
/* 17 */     super(LoggerFactory.getLogger(SubChartPanelDrawingsDisplayableDataPart.class), chartState);
/* 18 */     this.drawingsContainer = drawingsManagerContainer;
/* 19 */     this.subWindowId = subWindowId;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent) {
/* 23 */     this.drawingsContainer.drawAllDrawings(g, this.subWindowId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.SubChartPanelDrawingsDisplayableDataPart
 * JD-Core Version:    0.6.0
 */