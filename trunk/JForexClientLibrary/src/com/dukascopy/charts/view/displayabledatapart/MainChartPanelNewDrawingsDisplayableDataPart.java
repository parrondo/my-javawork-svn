/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class MainChartPanelNewDrawingsDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final IDrawingsManagerContainer drawingsContainer;
/*    */ 
/*    */   MainChartPanelNewDrawingsDisplayableDataPart(ChartState chartState, IDrawingsManagerContainer drawingsContainer)
/*    */   {
/* 15 */     super(LoggerFactory.getLogger(MainChartPanelNewDrawingsDisplayableDataPart.class), chartState);
/* 16 */     this.drawingsContainer = drawingsContainer;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 21 */     this.drawingsContainer.drawNewDrawing(g, -1);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.MainChartPanelNewDrawingsDisplayableDataPart
 * JD-Core Version:    0.6.0
 */