/*    */ package com.dukascopy.charts.dialogs.indicators.listener;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.api.indicators.IIndicator;
/*    */ import com.dukascopy.api.indicators.IndicatorInfo;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*    */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*    */ 
/*    */ public class IndicatorWrapperChangeListener extends DDSChartsActionAdapter
/*    */ {
/*    */   public void indicatorChanged(IndicatorWrapper indicatorWrapper, int subChartId)
/*    */   {
/*  9 */     refreshIndicatorWrapperDrawingStyles(indicatorWrapper);
/*    */   }
/*    */ 
/*    */   private void refreshIndicatorWrapperDrawingStyles(IndicatorWrapper indicatorWrapper) {
/* 13 */     IIndicator indicator = indicatorWrapper.getIndicator();
/* 14 */     for (int i = 0; i < indicator.getIndicatorInfo().getNumberOfOutputs(); i++)
/* 15 */       indicatorWrapper.getDrawingStyles()[i] = indicator.getOutputParameterInfo(i).getDrawingStyle();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.listener.IndicatorWrapperChangeListener
 * JD-Core Version:    0.6.0
 */