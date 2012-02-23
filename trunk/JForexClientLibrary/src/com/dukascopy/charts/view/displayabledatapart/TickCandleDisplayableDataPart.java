/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ class TickCandleDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final Map<DataType, IDrawingStrategy> drawingStrategiesMap;
/*    */ 
/*    */   protected TickCandleDisplayableDataPart(Logger logger, ChartState chartState, Map<DataType, IDrawingStrategy> drawingStrategiesMap)
/*    */   {
/* 22 */     super(logger, chartState);
/*    */ 
/* 24 */     this.drawingStrategiesMap = drawingStrategiesMap;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 29 */     getDrawingStrategy().draw(g, jComponent);
/*    */   }
/*    */ 
/*    */   private IDrawingStrategy getDrawingStrategy() {
/* 33 */     DataType dataType = this.chartState.getDataType();
/* 34 */     IDrawingStrategy drawingStrategy = (IDrawingStrategy)this.drawingStrategiesMap.get(dataType);
/* 35 */     if (drawingStrategy == null) {
/* 36 */       throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*    */     }
/* 38 */     return drawingStrategy;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.TickCandleDisplayableDataPart
 * JD-Core Version:    0.6.0
 */