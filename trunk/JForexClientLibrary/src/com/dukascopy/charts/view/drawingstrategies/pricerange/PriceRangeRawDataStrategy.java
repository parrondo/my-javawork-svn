/*    */ package com.dukascopy.charts.view.drawingstrategies.pricerange;
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
/*    */ public class PriceRangeRawDataStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   private final ChartState chartState;
/* 21 */   private final Map<DataType.DataPresentationType, IVisualisationDrawingStrategy> strategies = new EnumMap(DataType.DataPresentationType.class);
/*    */ 
/*    */   public PriceRangeRawDataStrategy(ChartState chartState, IVisualisationDrawingStrategy rangeBarVisualisationDrawingStrategy, IVisualisationDrawingStrategy candleVisualisationDrawingStrategy)
/*    */   {
/* 29 */     this.chartState = chartState;
/* 30 */     this.strategies.put(DataType.DataPresentationType.RANGE_BAR, rangeBarVisualisationDrawingStrategy);
/* 31 */     this.strategies.put(DataType.DataPresentationType.CANDLE, candleVisualisationDrawingStrategy);
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent)
/*    */   {
/* 37 */     ((IVisualisationDrawingStrategy)this.strategies.get(this.chartState.getPriceRangesPresentationType())).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pricerange.PriceRangeRawDataStrategy
 * JD-Core Version:    0.6.0
 */