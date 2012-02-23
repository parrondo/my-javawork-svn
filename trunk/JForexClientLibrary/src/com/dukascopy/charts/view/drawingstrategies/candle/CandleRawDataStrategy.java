/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
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
/*    */ class CandleRawDataStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   final ChartState chartState;
/* 17 */   final Map<DataType.DataPresentationType, IVisualisationDrawingStrategy> strategies = new EnumMap(DataType.DataPresentationType.class);
/*    */ 
/*    */   public CandleRawDataStrategy(ChartState chartState, IVisualisationDrawingStrategy candleLineRawDataStrategy, IVisualisationDrawingStrategy candleBarRawDataStrategy, IVisualisationDrawingStrategy candleRawDataStrategy)
/*    */   {
/* 25 */     this.chartState = chartState;
/* 26 */     this.strategies.put(DataType.DataPresentationType.LINE, candleLineRawDataStrategy);
/* 27 */     this.strategies.put(DataType.DataPresentationType.BAR, candleBarRawDataStrategy);
/* 28 */     this.strategies.put(DataType.DataPresentationType.CANDLE, candleRawDataStrategy);
/* 29 */     this.strategies.put(DataType.DataPresentationType.TABLE, new IVisualisationDrawingStrategy() {
/*    */       public void draw(Graphics g, JComponent jComponent) {
/* 31 */         g.drawString("Table has to be shown instead!", 10, 10);
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent) {
/* 37 */     ((IVisualisationDrawingStrategy)this.strategies.get(this.chartState.getCandleType())).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.CandleRawDataStrategy
 * JD-Core Version:    0.6.0
 */