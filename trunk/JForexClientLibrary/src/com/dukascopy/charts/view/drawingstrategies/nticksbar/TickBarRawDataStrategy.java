/*    */ package com.dukascopy.charts.view.drawingstrategies.nticksbar;
/*    */ 
/*    */ import com.dukascopy.api.DataType.DataPresentationType;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*    */ import com.dukascopy.charts.view.drawingstrategies.IVisualisationDrawingStrategy;
/*    */ import java.awt.Graphics;
/*    */ import java.util.EnumMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class TickBarRawDataStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   private final ChartState chartState;
/* 21 */   private final Map<DataType.DataPresentationType, IVisualisationDrawingStrategy> strategies = new EnumMap(DataType.DataPresentationType.class);
/*    */ 
/*    */   public TickBarRawDataStrategy(ChartState chartState, IVisualisationDrawingStrategy barVisualisationDrawingStrategy, IVisualisationDrawingStrategy candleVisualisationDrawingStrategy)
/*    */   {
/* 28 */     this.chartState = chartState;
/* 29 */     this.strategies.put(DataType.DataPresentationType.BAR, barVisualisationDrawingStrategy);
/* 30 */     this.strategies.put(DataType.DataPresentationType.CANDLE, candleVisualisationDrawingStrategy);
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent)
/*    */   {
/* 36 */     ((IVisualisationDrawingStrategy)this.strategies.get(this.chartState.getTickBarPresentationType())).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.nticksbar.TickBarRawDataStrategy
 * JD-Core Version:    0.6.0
 */