/*     */ package com.dukascopy.charts.main;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsActionListener;
/*     */ import com.dukascopy.charts.persistence.IndicatorBean;
/*     */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DDSChartsActionAdapter
/*     */   implements DDSChartsActionListener
/*     */ {
/*     */   public static List<IndicatorBean> convertToIndicatorBeans(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*  26 */     List result = new ArrayList();
/*  27 */     if ((indicatorWrappers != null) && (!indicatorWrappers.isEmpty())) {
/*  28 */       for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/*  29 */         IndicatorBean indicatorBean = convertToIndicatorBean(indicatorWrapper);
/*  30 */         result.add(indicatorBean);
/*     */       }
/*     */     }
/*  33 */     return result;
/*     */   }
/*     */ 
/*     */   public static IndicatorBean convertToIndicatorBean(IndicatorWrapper indicatorWrapper, Integer subchartId) {
/*  37 */     IndicatorBean indicatorBean = new IndicatorBean(indicatorWrapper.getId(), indicatorWrapper.getName(), indicatorWrapper.getOfferSidesForTicks(), indicatorWrapper.getAppliedPricesForCandles(), indicatorWrapper.getOptParams(), indicatorWrapper.getOutputColors(), indicatorWrapper.getOutputColors2(), indicatorWrapper.getShowValuesOnChart(), indicatorWrapper.getShowOutputs(), indicatorWrapper.getOpacityAlphas(), indicatorWrapper.getDrawingStyles(), indicatorWrapper.getLineWidths(), indicatorWrapper.getOutputShifts(), subchartId, indicatorWrapper.getChartObjects(), indicatorWrapper.getLevelInfoList(), indicatorWrapper.isRecalculateOnNewCandleOnly());
/*     */ 
/*  55 */     return indicatorBean;
/*     */   }
/*     */ 
/*     */   public static IndicatorBean convertToIndicatorBean(IndicatorWrapper indicatorWrapper) {
/*  59 */     return convertToIndicatorBean(indicatorWrapper, indicatorWrapper.getSubPanelId());
/*     */   }
/*     */ 
/*     */   public static List<IndicatorWrapper> convertToIndicatorWrapers(List<IndicatorBean> indicatorBeans) {
/*  63 */     List result = new ArrayList();
/*  64 */     if ((indicatorBeans != null) && (!indicatorBeans.isEmpty())) {
/*  65 */       for (IndicatorBean indicatorBean : indicatorBeans) {
/*  66 */         IndicatorWrapper indicatorWrapper = convertToIndicatorWraper(indicatorBean);
/*  67 */         result.add(indicatorWrapper);
/*     */       }
/*     */     }
/*  70 */     return result;
/*     */   }
/*     */ 
/*     */   public static IndicatorWrapper convertToIndicatorWraper(IndicatorBean indicatorBean) {
/*  74 */     IndicatorWrapper indicatorWrapper = new IndicatorWrapper(indicatorBean.getName(), indicatorBean.getId().intValue(), indicatorBean.getSidesForTicks(), indicatorBean.getAppliedPricesForCandles(), indicatorBean.getOptParams(), indicatorBean.getOutputColors(), indicatorBean.getOutputColors2(), indicatorBean.getShowValuesOnChart(), indicatorBean.getShowOutputs(), indicatorBean.getOpacityAlphas(), indicatorBean.getDrawingStyles(), indicatorBean.getLineWidths(), indicatorBean.getOutputShifts(), indicatorBean.getSubPanelId(), indicatorBean.getChartObjects(), indicatorBean.getLevelInfoList());
/*     */ 
/*  95 */     IIndicator indicator = indicatorWrapper.getIndicator();
/*  96 */     for (int i = 0; i < indicator.getIndicatorInfo().getNumberOfOutputs(); i++) {
/*  97 */       OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(i);
/*  98 */       outputParameterInfo.setShowOutput(indicatorWrapper.showOutput(i));
/*  99 */       outputParameterInfo.setOpacityAlpha(indicatorWrapper.getOpacityAlphas()[i]);
/* 100 */       outputParameterInfo.setDrawingStyle(indicatorWrapper.getDrawingStyles()[i]);
/* 101 */       outputParameterInfo.setColor(indicatorWrapper.getOutputColors()[i]);
/* 102 */       outputParameterInfo.setColor2(indicatorWrapper.getOutputColors2()[i]);
/* 103 */       outputParameterInfo.setShift(indicatorWrapper.getOutputShifts()[i]);
/*     */     }
/*     */ 
/* 106 */     indicatorWrapper.setRecalculateOnNewCandleOnly(indicatorBean.isRecalculateOnNewCandleOnly());
/*     */ 
/* 108 */     return indicatorWrapper;
/*     */   }
/*     */ 
/*     */   public static LastUsedIndicatorBean convertToLastUsedIndicatorBean(IndicatorWrapper indicatorWrapper) {
/* 112 */     LastUsedIndicatorBean lastUsedIndicatorBean = new LastUsedIndicatorBean(indicatorWrapper.getId(), indicatorWrapper.getName(), indicatorWrapper.getOfferSidesForTicks(), indicatorWrapper.getAppliedPricesForCandles(), indicatorWrapper.getOptParams(), indicatorWrapper.getOutputColors(), indicatorWrapper.getOutputColors2(), indicatorWrapper.getShowValuesOnChart(), indicatorWrapper.getShowOutputs(), indicatorWrapper.getOpacityAlphas(), indicatorWrapper.getDrawingStyles(), indicatorWrapper.getLineWidths(), indicatorWrapper.getOutputShifts(), indicatorWrapper.getChartObjects(), indicatorWrapper.getLevelInfoList(), indicatorWrapper.isRecalculateOnNewCandleOnly());
/*     */ 
/* 130 */     return lastUsedIndicatorBean;
/*     */   }
/*     */ 
/*     */   public void indicatorAdded(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void indicatorRemoved(IndicatorWrapper indicatorWrapper)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void indicatorsRemoved(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void periodChanged(Period newPeriod)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void offerSideChanged(OfferSide newOfferSide)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void filterChanged(Filter selectedFilter)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void drawingAdded(IChartObject drawing)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void drawingAdded(int indicatorId, IChartObject drawing)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(IChartObject drawing)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(int indicatorId, IChartObject iChartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void drawingsRemoved(List<IChartObject> chartObjects)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void drawingChanged(IChartObject drawing)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(long startTime, long toTime, boolean isAutoShiftActive, int chartShiftInPx)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(int draggedFromPx, int draggedToPx, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void candleTypeChanged(DataType.DataPresentationType selectedCandleType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void tickTypeChanged(DataType.DataPresentationType selectedTickType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void gridVisibilityChanged(boolean visible)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseCursorVisibilityChanged(boolean visible)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void lastCandleVisibilityChanged(boolean visible)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void verticalChartMovementChanged(boolean enabled)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(boolean isChartShiftActive)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(boolean isChartShiftActive, int unitsMoved)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void zoomOutEnabled(boolean enabled)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void zoomInEnabled(boolean enabled)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void instrumentChanged(Instrument selectedInstrument)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void chartObjectCreatedForNewDrawing(IChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void dataTypeChanged(DataType newDataType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void priceRangeChanged(PriceRange priceRange)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void priceRangesPresentationTypeChanged(DataType.DataPresentationType priceRangesPresentationType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void jForexPeriodChanged(JForexPeriod jForexPeriod)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reversalAmountChanged(ReversalAmount reversalAmount)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void pointAndFigurePresentationTypeChanged(DataType.DataPresentationType pointAndFigurePresentationType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void tickBarPresentationTypeChanged(DataType.DataPresentationType tickBarPresentationType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void renkoPresentationTypeChanged(DataType.DataPresentationType renkoPresentationType)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.main.DDSChartsActionAdapter
 * JD-Core Version:    0.6.0
 */