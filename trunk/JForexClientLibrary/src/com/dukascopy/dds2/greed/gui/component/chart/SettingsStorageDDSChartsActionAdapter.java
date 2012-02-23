/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
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
/*     */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.IndicatorBean;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.util.List;
/*     */ 
/*     */ class SettingsStorageDDSChartsActionAdapter extends DDSChartsActionAdapter
/*     */ {
/*     */   private int chartPanelId;
/*     */   private final ClientSettingsStorage clientSettingsStorage;
/*     */ 
/*     */   SettingsStorageDDSChartsActionAdapter(Integer chartPanelId, ClientSettingsStorage clientSettingsStorage)
/*     */   {
/*  28 */     this.clientSettingsStorage = clientSettingsStorage;
/*  29 */     this.chartPanelId = chartPanelId.intValue();
/*     */   }
/*     */ 
/*     */   private ClientSettingsStorage getClientSettingsStorage() {
/*  33 */     return this.clientSettingsStorage;
/*     */   }
/*     */ 
/*     */   public void indicatorAdded(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*  39 */     IndicatorBean indicatorBean = convertToIndicatorBean(indicatorWrapper, Integer.valueOf(subChartId));
/*  40 */     getClientSettingsStorage().saveIndicatorBean(Integer.valueOf(this.chartPanelId), indicatorBean);
/*     */   }
/*     */ 
/*     */   public void indicatorChanged(IndicatorWrapper indicatorWrapper, int subChartId)
/*     */   {
/*  45 */     IndicatorBean indicatorBean = convertToIndicatorBean(indicatorWrapper, Integer.valueOf(subChartId));
/*  46 */     getClientSettingsStorage().saveIndicatorBean(Integer.valueOf(this.chartPanelId), indicatorBean);
/*     */   }
/*     */ 
/*     */   public void indicatorRemoved(IndicatorWrapper indicatorWrapper)
/*     */   {
/*  51 */     getClientSettingsStorage().removeIndicator(Integer.valueOf(this.chartPanelId), Integer.valueOf(indicatorWrapper.getId()));
/*     */   }
/*     */ 
/*     */   public void indicatorsRemoved(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/*  56 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers)
/*  57 */       getClientSettingsStorage().removeIndicator(Integer.valueOf(this.chartPanelId), Integer.valueOf(indicatorWrapper.getId()));
/*     */   }
/*     */ 
/*     */   public void drawingAdded(IChartObject chartObject)
/*     */   {
/*  63 */     getClientSettingsStorage().save(Integer.valueOf(this.chartPanelId), chartObject);
/*     */   }
/*     */ 
/*     */   public void drawingAdded(int indicatorId, IChartObject drawing)
/*     */   {
/*  68 */     getClientSettingsStorage().save(this.chartPanelId, indicatorId, drawing);
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(IChartObject chartObject)
/*     */   {
/*  73 */     getClientSettingsStorage().removeDrawing(Integer.valueOf(this.chartPanelId), chartObject);
/*     */   }
/*     */ 
/*     */   public void drawingRemoved(int indicatorId, IChartObject iChartObject)
/*     */   {
/*  78 */     getClientSettingsStorage().removeDrawing(this.chartPanelId, indicatorId, iChartObject);
/*     */   }
/*     */ 
/*     */   public void drawingsRemoved(List<IChartObject> chartObjects)
/*     */   {
/*  83 */     for (IChartObject chartObject : chartObjects)
/*  84 */       getClientSettingsStorage().removeDrawing(Integer.valueOf(this.chartPanelId), chartObject);
/*     */   }
/*     */ 
/*     */   public void drawingChanged(IChartObject chartObject)
/*     */   {
/*  90 */     getClientSettingsStorage().save(Integer.valueOf(this.chartPanelId), chartObject);
/*     */   }
/*     */ 
/*     */   public void periodChanged(Period newPeriod)
/*     */   {
/*  95 */     ChartBean chartBean = createNullChartBean();
/*  96 */     chartBean.setPeriod(newPeriod);
/*  97 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void offerSideChanged(OfferSide newOfferSide)
/*     */   {
/* 102 */     ChartBean chartBean = createNullChartBean();
/* 103 */     chartBean.setOfferSide(newOfferSide);
/* 104 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void filterChanged(Filter selectedFilter)
/*     */   {
/* 109 */     ChartBean chartBean = createNullChartBean();
/* 110 */     chartBean.setFilter(selectedFilter);
/* 111 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void candleTypeChanged(DataType.DataPresentationType candleType)
/*     */   {
/* 116 */     ChartBean chartBean = createNullChartBean();
/* 117 */     chartBean.setTimePeriodPresentationType(candleType);
/* 118 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void tickTypeChanged(DataType.DataPresentationType tickType)
/*     */   {
/* 123 */     ChartBean chartBean = createNullChartBean();
/* 124 */     chartBean.setTicksPresentationType(tickType);
/* 125 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void gridVisibilityChanged(boolean isGridVisible)
/*     */   {
/* 130 */     ChartBean chartBean = createNullChartBean();
/* 131 */     chartBean.setGridVisible(isGridVisible ? 1 : 0);
/* 132 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void mouseCursorVisibilityChanged(boolean isMouseCursorVisible)
/*     */   {
/* 137 */     ChartBean chartBean = createNullChartBean();
/* 138 */     chartBean.setMouseCursorVisible(isMouseCursorVisible ? 1 : 0);
/* 139 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void lastCandleVisibilityChanged(boolean isLastCandleVisible)
/*     */   {
/* 144 */     ChartBean chartBean = createNullChartBean();
/* 145 */     chartBean.setLastCandleVisible(isLastCandleVisible ? 1 : 0);
/* 146 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void verticalChartMovementChanged(boolean isVerticalChartMovementEnabled)
/*     */   {
/* 151 */     ChartBean chartBean = createNullChartBean();
/* 152 */     chartBean.setVerticalMovementEnabledAsBoolean(isVerticalChartMovementEnabled);
/* 153 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void timeFrameMoved(long startTime, long endTime, boolean isAutoShiftActive, int chartShiftInPx)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void instrumentChanged(Instrument selectedInstrument)
/*     */   {
/* 163 */     ChartBean chartBean = createNullChartBean();
/* 164 */     chartBean.setInstrument(selectedInstrument);
/* 165 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void chartObjectCreatedForNewDrawing(IChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void priceRangeChanged(PriceRange priceRange)
/*     */   {
/* 174 */     ChartBean chartBean = createNullChartBean();
/* 175 */     chartBean.setPriceRange(priceRange);
/* 176 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void priceRangesPresentationTypeChanged(DataType.DataPresentationType priceRangesPresentationType)
/*     */   {
/* 181 */     ChartBean chartBean = createNullChartBean();
/* 182 */     chartBean.setPriceRangePresentationType(priceRangesPresentationType);
/* 183 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void dataTypeChanged(DataType newDataType)
/*     */   {
/* 188 */     ChartBean chartBean = createNullChartBean();
/* 189 */     chartBean.setDataType(newDataType);
/* 190 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void reversalAmountChanged(ReversalAmount reversalAmount)
/*     */   {
/* 195 */     ChartBean chartBean = createNullChartBean();
/* 196 */     chartBean.setReversalAmount(reversalAmount);
/* 197 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void pointAndFigurePresentationTypeChanged(DataType.DataPresentationType pointAndFigurePresentationType)
/*     */   {
/* 202 */     ChartBean chartBean = createNullChartBean();
/* 203 */     chartBean.setPointAndFigurePresentationType(pointAndFigurePresentationType);
/* 204 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   public void tickBarPresentationTypeChanged(DataType.DataPresentationType tickBarPresentationType)
/*     */   {
/* 209 */     ChartBean chartBean = createNullChartBean();
/* 210 */     chartBean.setTickBarPresentationType(tickBarPresentationType);
/* 211 */     getClientSettingsStorage().save(chartBean);
/*     */   }
/*     */ 
/*     */   private ChartBean createNullChartBean() {
/* 215 */     ChartBean chartBean = new ChartBean(this.chartPanelId, null, null, null, null, null, null, null, null, null, null);
/*     */ 
/* 217 */     chartBean.setGridVisible(-1);
/* 218 */     chartBean.setMouseCursorVisible(-1);
/* 219 */     chartBean.setLastCandleVisible(-1);
/* 220 */     chartBean.setVerticalMovementEnabled(-1);
/* 221 */     chartBean.setEndTime(-1L);
/* 222 */     chartBean.setMinPrice(-1.0D);
/* 223 */     chartBean.setMaxPrice(-1.0D);
/* 224 */     chartBean.setAutoShiftActive(-1);
/* 225 */     chartBean.setChartShiftInPx(-1);
/* 226 */     chartBean.setYAxisPadding(-1.0D);
/* 227 */     chartBean.setDataUnitWidth(-1);
/*     */ 
/* 229 */     return chartBean;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.SettingsStorageDDSChartsActionAdapter
 * JD-Core Version:    0.6.0
 */