/*    */ package com.dukascopy.charts.view.drawingstrategies.tick;
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
/*    */ class TickRawDataStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   final ChartState chartState;
/* 17 */   final Map<DataType.DataPresentationType, IVisualisationDrawingStrategy> strategies = new EnumMap(DataType.DataPresentationType.class);
/*    */ 
/*    */   public TickRawDataStrategy(ChartState chartState, IVisualisationDrawingStrategy tickLineRawDataStrategy, IVisualisationDrawingStrategy tickBarRawDataStrategy)
/*    */   {
/* 24 */     this.chartState = chartState;
/*    */ 
/* 26 */     this.strategies.put(DataType.DataPresentationType.LINE, tickLineRawDataStrategy);
/* 27 */     this.strategies.put(DataType.DataPresentationType.BAR, tickBarRawDataStrategy);
/* 28 */     this.strategies.put(DataType.DataPresentationType.TABLE, new IVisualisationDrawingStrategy() {
/*    */       public void draw(Graphics g, JComponent jComponent) {
/* 30 */         g.drawString("Table has to be shown instead!", 10, 10);
/*    */       } } );
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent) {
/* 36 */     ((IVisualisationDrawingStrategy)this.strategies.get(this.chartState.getTickType())).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.TickRawDataStrategy
 * JD-Core Version:    0.6.0
 */