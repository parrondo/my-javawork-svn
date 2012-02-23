/*    */ package com.dukascopy.charts.view.drawingstrategies.renko;
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
/*    */ public class RenkoRawDataStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   private final ChartState chartState;
/* 24 */   private final Map<DataType.DataPresentationType, IVisualisationDrawingStrategy> strategies = new EnumMap(DataType.DataPresentationType.class);
/*    */ 
/*    */   public RenkoRawDataStrategy(ChartState chartState, IVisualisationDrawingStrategy brickVisualisationDrawingStrategy)
/*    */   {
/* 30 */     this.chartState = chartState;
/* 31 */     this.strategies.put(DataType.DataPresentationType.BRICK, brickVisualisationDrawingStrategy);
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent)
/*    */   {
/* 37 */     ((IVisualisationDrawingStrategy)this.strategies.get(this.chartState.getRenkoPresentationType())).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.renko.RenkoRawDataStrategy
 * JD-Core Version:    0.6.0
 */