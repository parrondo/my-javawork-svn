/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class SubChartPanelDrawingsHandlersDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final IDrawingsManagerContainer drawingsContainer;
/*    */   private final int subWindowId;
/*    */ 
/*    */   SubChartPanelDrawingsHandlersDisplayableDataPart(ChartState chartState, IDrawingsManagerContainer drawingsContainer, int subWindowId)
/*    */   {
/* 16 */     super(LoggerFactory.getLogger(SubChartPanelDrawingsHandlersDisplayableDataPart.class), chartState);
/* 17 */     this.drawingsContainer = drawingsContainer;
/* 18 */     this.subWindowId = subWindowId;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent) {
/* 22 */     this.drawingsContainer.drawHandlers(g, this.subWindowId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.SubChartPanelDrawingsHandlersDisplayableDataPart
 * JD-Core Version:    0.6.0
 */