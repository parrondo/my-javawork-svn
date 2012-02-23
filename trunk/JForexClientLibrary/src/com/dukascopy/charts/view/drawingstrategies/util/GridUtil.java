/*    */ package com.dukascopy.charts.view.drawingstrategies.util;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.settings.ChartSettings;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class GridUtil
/*    */ {
/*    */   private static final int MINIMUM_GRID_SIZE_PX = 20;
/*    */   private static final double MINIMAL_GRID_SIZE_PIP = 0.5D;
/*    */ 
/*    */   public static double getGridSize(ChartState chartState)
/*    */   {
/* 22 */     double gridSize = (0.0D / 0.0D);
/*    */ 
/* 24 */     DataType dataType = chartState.getDataType();
/* 25 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*    */     case 1:
/* 27 */       gridSize = 0.5D;
/* 28 */       break;
/*    */     case 2:
/* 31 */       gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/* 32 */       break;
/*    */     case 3:
/* 35 */       gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/* 36 */       break;
/*    */     case 4:
/* 39 */       gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/* 40 */       break;
/*    */     case 5:
/* 43 */       gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/* 44 */       break;
/*    */     case 6:
/* 47 */       gridSize = ((Integer)ChartSettings.get(ChartSettings.Option.GRID_SIZE)).intValue();
/* 48 */       break;
/*    */     default:
/* 50 */       throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*    */     }
/*    */ 
/* 53 */     if (gridSize < 0.5D) {
/* 54 */       gridSize = 0.5D;
/*    */     }
/*    */ 
/* 57 */     return gridSize;
/*    */   }
/*    */ 
/*    */   public static boolean isValid(double value) {
/* 61 */     return (value != 1.7976931348623157E+308D) && (value != 4.9E-324D) && (!Double.isInfinite(value));
/*    */   }
/*    */ 
/*    */   public static int calculateScale(float valuesInOnePixel, Instrument instrument, double gridSize) {
/* 65 */     double pipValue = instrument.getPipValue();
/* 66 */     return Math.max(1, (int)Math.round(valuesInOnePixel * 20.0F / (pipValue * gridSize)));
/*    */   }
/*    */ 
/*    */   public static double calculateNearest(Instrument instrument, double value, double gridSize) {
/* 70 */     int pipScale = instrument.getPipScale();
/* 71 */     return BigDecimal.valueOf(Math.round(value * Math.pow(10.0D, pipScale) / gridSize) * gridSize * Math.pow(10.0D, -pipScale)).setScale(pipScale, 3).doubleValue();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.util.GridUtil
 * JD-Core Version:    0.6.0
 */