/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class MainChartPanelEditedDrawingsDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final IDrawingsManagerContainer drawingsContainer;
/*    */ 
/*    */   MainChartPanelEditedDrawingsDisplayableDataPart(ChartState chartState, IDrawingsManagerContainer drawingsContainer)
/*    */   {
/* 15 */     super(LoggerFactory.getLogger(MainChartPanelEditedDrawingsDisplayableDataPart.class), chartState);
/* 16 */     this.drawingsContainer = drawingsContainer;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 21 */     this.drawingsContainer.drawEditedDrawing(g, -1);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.MainChartPanelEditedDrawingsDisplayableDataPart
 * JD-Core Version:    0.6.0
 */