/*    */ package com.dukascopy.charts.view.drawingstrategies.pnf;
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
/*    */ public class PointAndFigureRawDataStrategy
/*    */   implements IDrawingStrategy
/*    */ {
/*    */   private final ChartState chartState;
/* 21 */   private final Map<DataType.DataPresentationType, IVisualisationDrawingStrategy> strategies = new EnumMap(DataType.DataPresentationType.class);
/*    */ 
/*    */   public PointAndFigureRawDataStrategy(ChartState chartState, IVisualisationDrawingStrategy boxVisualisationDrawingStrategy, IVisualisationDrawingStrategy barVisualisationDrawingStrategy)
/*    */   {
/* 29 */     this.chartState = chartState;
/*    */ 
/* 31 */     this.strategies.put(DataType.DataPresentationType.BOX, boxVisualisationDrawingStrategy);
/* 32 */     this.strategies.put(DataType.DataPresentationType.BAR, barVisualisationDrawingStrategy);
/*    */   }
/*    */ 
/*    */   public void draw(Graphics g, JComponent jComponent)
/*    */   {
/* 38 */     ((IVisualisationDrawingStrategy)this.strategies.get(this.chartState.getPointAndFigurePresentationType())).draw(g, jComponent);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureRawDataStrategy
 * JD-Core Version:    0.6.0
 */