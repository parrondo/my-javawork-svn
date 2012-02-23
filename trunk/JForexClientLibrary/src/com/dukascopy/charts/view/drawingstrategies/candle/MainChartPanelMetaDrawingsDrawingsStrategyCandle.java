/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartPanelMetaDrawingsDrawingsStrategyAbstract;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Point;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class MainChartPanelMetaDrawingsDrawingsStrategyCandle extends MainChartPanelMetaDrawingsDrawingsStrategyAbstract
/*    */ {
/*    */   private final GeometryCalculator candlesGeometryCalculator;
/*    */   private final IValueToYMapper candleValueToYMapper;
/*    */ 
/*    */   public MainChartPanelMetaDrawingsDrawingsStrategyCandle(ValueFormatter valueFormatter, ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState, GeometryCalculator candlesGeometryCalculator, IValueToYMapper candleValueToYMapper)
/*    */   {
/* 25 */     super(valueFormatter, chartState, mouseControllerMetaDrawingsState);
/* 26 */     this.candlesGeometryCalculator = candlesGeometryCalculator;
/* 27 */     this.candleValueToYMapper = candleValueToYMapper;
/*    */   }
/*    */ 
/*    */   protected String getInfoMessage(Graphics g, JComponent jComponent, Point p1, Point p2)
/*    */   {
/* 33 */     if ((p1 == null) || (p2 == null)) {
/* 34 */       return "";
/*    */     }
/*    */ 
/* 37 */     int barsDiff = Math.abs(p1.x - p2.x) / getCandlesGeometryCalculator().getDataUnitWidth();
/*    */ 
/* 39 */     double secondPrice = getCandleValueToYMapper().vy(p2.y);
/* 40 */     double valueDiff = Math.abs(secondPrice - getCandleValueToYMapper().vy(p1.y));
/*    */ 
/* 42 */     StringBuilder buffer = new StringBuilder();
/* 43 */     buffer.append(barsDiff).append(" / ");
/* 44 */     buffer.append(getValueFormatter().formatValueDiff(valueDiff)).append(" / ");
/* 45 */     buffer.append(getValueFormatter().formatPrice(secondPrice));
/*    */ 
/* 47 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public GeometryCalculator getCandlesGeometryCalculator()
/*    */   {
/* 52 */     return this.candlesGeometryCalculator;
/*    */   }
/*    */ 
/*    */   public IValueToYMapper getCandleValueToYMapper() {
/* 56 */     return this.candleValueToYMapper;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.MainChartPanelMetaDrawingsDrawingsStrategyCandle
 * JD-Core Version:    0.6.0
 */