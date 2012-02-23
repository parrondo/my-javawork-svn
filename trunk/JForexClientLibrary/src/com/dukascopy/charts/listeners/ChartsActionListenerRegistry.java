/*     */ package com.dukascopy.charts.listeners;
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
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ChartsActionListenerRegistry
/*     */   implements DDSChartsActionListener
/*     */ {
/*  21 */   private volatile List<DDSChartsActionListener> listeners = new ArrayList();
/*     */ 
/*     */   public void registerListener(DDSChartsActionListener listener) {
/*  24 */     this.listeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void indicatorAdded(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*  29 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  30 */       ddsChartsActionListener.indicatorAdded(indicatorWrapper, subChartId);
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*  36 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  37 */       ddsChartsActionListener.indicatorChanged(indicatorWrapper, subChartId);
/*     */   }
/*     */ 
/*     */   public void indicatorRemoved(IndicatorWrapper indicatorWrapper)
/*     */   {
/*  43 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  44 */       ddsChartsActionListener.indicatorRemoved(indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public void indicatorsRemoved(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*  50 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  51 */       ddsChartsActionListener.indicatorsRemoved(indicatorWrappers);
/*     */   }
/*     */ 
/*     */   public void periodChanged(Period newPeriod)
/*     */   {
/*  57 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  58 */       ddsChartsActionListener.periodChanged(newPeriod);
/*     */   }
/*     */ 
/*     */   public void offerSideChanged(OfferSide newOfferSide)
/*     */   {
/*  64 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  65 */       ddsChartsActionListener.offerSideChanged(newOfferSide);
/*     */   }
/*     */ 
/*     */   public void filterChanged(Filter selectedFilter)
/*     */   {
/*  71 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  72 */       ddsChartsActionListener.filterChanged(selectedFilter);
/*     */   }
/*     */ 
/*     */   public void drawingAdded(IChartObject drawing)
/*     */   {
/*  78 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  79 */       ddsChartsActionListener.drawingAdded(drawing);
/*     */   }
/*     */ 
/*     */   public void drawingAdded(int indicatorId, IChartObject chartObject)
/*     */   {
/*  85 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  86 */       ddsChartsActionListener.drawingAdded(indicatorId, chartObject);
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(IChartObject drawing)
/*     */   {
/*  92 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/*  93 */       ddsChartsActionListener.drawingRemoved(drawing);
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(int indicatorId, IChartObject iChartObject)
/*     */   {
/*  99 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 100 */       ddsChartsActionListener.drawingRemoved(indicatorId, iChartObject);
/*     */   }
/*     */ 
/*     */   public void drawingsRemoved(List<IChartObject> chartObjects)
/*     */   {
/* 106 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 107 */       ddsChartsActionListener.drawingsRemoved(chartObjects);
/*     */   }
/*     */ 
/*     */   public void drawingChanged(IChartObject drawing)
/*     */   {
/* 113 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 114 */       ddsChartsActionListener.drawingChanged(drawing);
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(long startTime, long endTime, boolean isAutoShiftActive, int chartShiftInPx)
/*     */   {
/* 120 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 121 */       ddsChartsActionListener.timeFrameMoved(startTime, endTime, isAutoShiftActive, chartShiftInPx);
/*     */   }
/*     */ 
/*     */   public void candleTypeChanged(DataType.DataPresentationType selectedLineType)
/*     */   {
/* 127 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 128 */       ddsChartsActionListener.candleTypeChanged(selectedLineType);
/*     */   }
/*     */ 
/*     */   public void tickTypeChanged(DataType.DataPresentationType selectedTickType)
/*     */   {
/* 134 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 135 */       ddsChartsActionListener.tickTypeChanged(selectedTickType);
/*     */   }
/*     */ 
/*     */   public void gridVisibilityChanged(boolean isVisible)
/*     */   {
/* 141 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 142 */       ddsChartsActionListener.gridVisibilityChanged(isVisible);
/*     */   }
/*     */ 
/*     */   public void mouseCursorVisibilityChanged(boolean isVisible)
/*     */   {
/* 148 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 149 */       ddsChartsActionListener.mouseCursorVisibilityChanged(isVisible);
/*     */   }
/*     */ 
/*     */   public void lastCandleVisibilityChanged(boolean isVisible)
/*     */   {
/* 155 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 156 */       ddsChartsActionListener.lastCandleVisibilityChanged(isVisible);
/*     */   }
/*     */ 
/*     */   public void verticalChartMovementChanged(boolean isEnabled)
/*     */   {
/* 162 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 163 */       ddsChartsActionListener.verticalChartMovementChanged(isEnabled);
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(boolean isChartShiftActive)
/*     */   {
/* 169 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 170 */       ddsChartsActionListener.timeFrameMoved(isChartShiftActive);
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(boolean isChartShiftActive, int unitsMoved)
/*     */   {
/* 176 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 177 */       ddsChartsActionListener.timeFrameMoved(isChartShiftActive, unitsMoved);
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(int draggedFromPx, int draggedToPx, int draggedYFrom, int draggedYTo, boolean isNewDragging)
/*     */   {
/* 183 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 184 */       ddsChartsActionListener.timeFrameMoved(draggedFromPx, draggedToPx, draggedYFrom, draggedYTo, isNewDragging);
/*     */   }
/*     */ 
/*     */   public void zoomOutEnabled(boolean enabled)
/*     */   {
/* 191 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 192 */       ddsChartsActionListener.zoomOutEnabled(enabled);
/*     */   }
/*     */ 
/*     */   public void zoomInEnabled(boolean enabled)
/*     */   {
/* 198 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 199 */       ddsChartsActionListener.zoomInEnabled(enabled);
/*     */   }
/*     */ 
/*     */   public void instrumentChanged(Instrument selectedInstrument)
/*     */   {
/* 205 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 206 */       ddsChartsActionListener.instrumentChanged(selectedInstrument);
/*     */   }
/*     */ 
/*     */   public void chartObjectCreatedForNewDrawing(IChartObject chartObject)
/*     */   {
/* 212 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 213 */       ddsChartsActionListener.chartObjectCreatedForNewDrawing(chartObject);
/*     */   }
/*     */ 
/*     */   public void dataTypeChanged(DataType newDataType)
/*     */   {
/* 219 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 220 */       ddsChartsActionListener.dataTypeChanged(newDataType);
/*     */   }
/*     */ 
/*     */   public void priceRangesPresentationTypeChanged(DataType.DataPresentationType priceRangesPresentationType)
/*     */   {
/* 226 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 227 */       ddsChartsActionListener.priceRangesPresentationTypeChanged(priceRangesPresentationType);
/*     */   }
/*     */ 
/*     */   public void priceRangeChanged(PriceRange priceRange)
/*     */   {
/* 233 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 234 */       ddsChartsActionListener.priceRangeChanged(priceRange);
/*     */   }
/*     */ 
/*     */   public void jForexPeriodChanged(JForexPeriod jForexPeriod)
/*     */   {
/* 240 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 241 */       ddsChartsActionListener.jForexPeriodChanged(jForexPeriod);
/*     */   }
/*     */ 
/*     */   public void reversalAmountChanged(ReversalAmount reversalAmount)
/*     */   {
/* 247 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 248 */       ddsChartsActionListener.reversalAmountChanged(reversalAmount);
/*     */   }
/*     */ 
/*     */   public void pointAndFigurePresentationTypeChanged(DataType.DataPresentationType pointAndFigurePresentationType)
/*     */   {
/* 255 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 256 */       ddsChartsActionListener.pointAndFigurePresentationTypeChanged(pointAndFigurePresentationType);
/*     */   }
/*     */ 
/*     */   public void tickBarPresentationTypeChanged(DataType.DataPresentationType tickBarPresentationType)
/*     */   {
/* 262 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 263 */       ddsChartsActionListener.tickBarPresentationTypeChanged(tickBarPresentationType);
/*     */   }
/*     */ 
/*     */   public void renkoPresentationTypeChanged(DataType.DataPresentationType renkoPresentationType)
/*     */   {
/* 268 */     for (DDSChartsActionListener ddsChartsActionListener : this.listeners)
/* 269 */       ddsChartsActionListener.renkoPresentationTypeChanged(renkoPresentationType);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.ChartsActionListenerRegistry
 * JD-Core Version:    0.6.0
 */