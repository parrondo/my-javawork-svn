/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.indicators.IndicatorsManagerImpl;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class MainChartPanelDrawingsHandlersDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final IDrawingsManagerContainer drawingsContainer;
/*    */   private final IndicatorsManagerImpl indicatorsManager;
/*    */ 
/*    */   MainChartPanelDrawingsHandlersDisplayableDataPart(ChartState chartState, IDrawingsManagerContainer drawingsContainer, IndicatorsManagerImpl indicatorsManager)
/*    */   {
/* 21 */     super(LoggerFactory.getLogger(MainChartPanelDrawingsHandlersDisplayableDataPart.class), chartState);
/* 22 */     this.drawingsContainer = drawingsContainer;
/* 23 */     this.indicatorsManager = indicatorsManager;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent) {
/* 27 */     this.drawingsContainer.drawHandlers(g, -1);
/* 28 */     this.indicatorsManager.drawHandles(g);
/* 29 */     this.indicatorsManager.drawMouseCrossHandler(g);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.MainChartPanelDrawingsHandlersDisplayableDataPart
 * JD-Core Version:    0.6.0
 */