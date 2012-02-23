/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class MainChartPanelDynamicDrawingsDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final IDrawingsManagerContainer drawingsContainer;
/*    */ 
/*    */   MainChartPanelDynamicDrawingsDisplayableDataPart(ChartState chartState, IDrawingsManagerContainer drawingsContainer)
/*    */   {
/* 20 */     super(LoggerFactory.getLogger(MainChartPanelDynamicDrawingsDisplayableDataPart.class), chartState);
/* 21 */     this.drawingsContainer = drawingsContainer;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 26 */     this.drawingsContainer.drawDynamicChartObjects(g, -1);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.MainChartPanelDynamicDrawingsDisplayableDataPart
 * JD-Core Version:    0.6.0
 */