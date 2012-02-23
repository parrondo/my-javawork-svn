/*    */ package com.dukascopy.charts.view.displayabledatapart;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Graphics;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public class MainChartPanelSelectedOrdersDisplayableDataPart extends AbstractDisplayableDataPart
/*    */ {
/*    */   private final Map<DataType, IOrdersDrawingStrategy> ordersDrawingStrategiesMap;
/*    */ 
/*    */   public MainChartPanelSelectedOrdersDisplayableDataPart(Logger logger, ChartState chartState, Map<DataType, IOrdersDrawingStrategy> ordersDrawingStrategiesMap)
/*    */   {
/* 22 */     super(logger, chartState);
/*    */ 
/* 24 */     this.ordersDrawingStrategiesMap = ordersDrawingStrategiesMap;
/*    */   }
/*    */ 
/*    */   protected void drawInternal(Graphics g, JComponent jComponent)
/*    */   {
/* 29 */     getDrawingStrategy().drawSelectedOrders(g, jComponent);
/*    */   }
/*    */ 
/*    */   private IOrdersDrawingStrategy getDrawingStrategy() {
/* 33 */     DataType dataType = this.chartState.getDataType();
/* 34 */     IOrdersDrawingStrategy strategy = (IOrdersDrawingStrategy)this.ordersDrawingStrategiesMap.get(dataType);
/* 35 */     if (strategy == null) {
/* 36 */       throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*    */     }
/* 38 */     return strategy;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.displayabledatapart.MainChartPanelSelectedOrdersDisplayableDataPart
 * JD-Core Version:    0.6.0
 */