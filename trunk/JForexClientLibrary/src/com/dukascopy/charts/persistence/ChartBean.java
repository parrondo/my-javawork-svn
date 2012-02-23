/*     */ package com.dukascopy.charts.persistence;
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
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ChartBean
/*     */ {
/*     */   private int id;
/*     */   private Instrument instrument;
/*     */   private JForexPeriod jForexPeriod;
/*     */   private OfferSide offerSide;
/*     */   private Filter filter;
/*     */   private DataType.DataPresentationType timePeriodPresentationType;
/*     */   private DataType.DataPresentationType ticksPresentationType;
/*     */   private DataType.DataPresentationType priceRangePresentationType;
/*     */   private DataType.DataPresentationType pointAndFigurePresentationType;
/*     */   private DataType.DataPresentationType tickBarPresentationType;
/*     */   private DataType.DataPresentationType renkoPresentationType;
/*     */   private int gridVisible;
/*     */   private int mouseCursorVisible;
/*     */   private int lastCandleVisible;
/*     */   private int verticalMovementEnabled;
/*     */   private ITheme theme;
/*     */   private long endTime;
/*     */   private double minPrice;
/*     */   private double maxPrice;
/*     */   private int autoShiftActive;
/*     */   private int chartShiftInPx;
/*     */   private double yAxisPadding;
/*     */   private int dataUnitWidth;
/*     */   private List<IndicatorWrapper> indicatorWrappers;
/*     */   private List<IChartObject> chartObjects;
/*     */   private IFeedDataProvider feedDataProvider;
/*     */   private Runnable startLoadingDataRunnable;
/*     */   private boolean readOnly;
/*     */   private boolean historicalTesterChart;
/*  59 */   private boolean needCreateDefaultChartObjects = false;
/*     */ 
/*     */   public ChartBean(int chartId, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide)
/*     */   {
/*  68 */     this(chartId, instrument, jForexPeriod, offerSide, Filter.ALL_FLATS, DataType.DataPresentationType.CANDLE, DataType.DataPresentationType.LINE, DataType.DataPresentationType.RANGE_BAR, DataType.DataPresentationType.BOX, DataType.DataPresentationType.BAR, DataType.DataPresentationType.BRICK);
/*     */   }
/*     */ 
/*     */   public ChartBean(int chartId, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, Filter filter, DataType.DataPresentationType candleType, DataType.DataPresentationType tickType, DataType.DataPresentationType priceRangeType, DataType.DataPresentationType pointAndFigurePresentationType, DataType.DataPresentationType tickBarPresentationType, DataType.DataPresentationType renkoPresentationType)
/*     */   {
/*  96 */     this.id = chartId;
/*  97 */     this.instrument = instrument;
/*  98 */     this.jForexPeriod = jForexPeriod;
/*  99 */     this.offerSide = offerSide;
/* 100 */     this.filter = filter;
/* 101 */     this.timePeriodPresentationType = candleType;
/* 102 */     this.ticksPresentationType = tickType;
/* 103 */     this.priceRangePresentationType = priceRangeType;
/* 104 */     this.pointAndFigurePresentationType = pointAndFigurePresentationType;
/* 105 */     this.tickBarPresentationType = tickBarPresentationType;
/* 106 */     this.renkoPresentationType = renkoPresentationType;
/*     */ 
/* 108 */     this.gridVisible = 1;
/* 109 */     this.mouseCursorVisible = 0;
/* 110 */     this.lastCandleVisible = 1;
/* 111 */     this.verticalMovementEnabled = 0;
/* 112 */     this.endTime = -1L;
/* 113 */     this.minPrice = -1.0D;
/* 114 */     this.maxPrice = -1.0D;
/* 115 */     this.autoShiftActive = 1;
/* 116 */     this.chartShiftInPx = -1;
/* 117 */     this.yAxisPadding = -1.0D;
/* 118 */     this.dataUnitWidth = -1;
/*     */   }
/*     */ 
/*     */   public int getId() {
/* 122 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(int id) {
/* 126 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/* 130 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument) {
/* 134 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public Period getPeriod() {
/* 138 */     return getJForexPeriod().getPeriod();
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period) {
/* 142 */     getJForexPeriod().setPeriod(period);
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide() {
/* 146 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide) {
/* 150 */     this.offerSide = offerSide;
/*     */   }
/*     */ 
/*     */   public Filter getFilter() {
/* 154 */     return this.filter;
/*     */   }
/*     */ 
/*     */   public void setFilter(Filter filter) {
/* 158 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getTimePeriodPresentationType() {
/* 162 */     return this.timePeriodPresentationType;
/*     */   }
/*     */ 
/*     */   public void setTimePeriodPresentationType(DataType.DataPresentationType timePeriodPresentationType) {
/* 166 */     this.timePeriodPresentationType = timePeriodPresentationType;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getTicksPresentationType() {
/* 170 */     return this.ticksPresentationType;
/*     */   }
/*     */ 
/*     */   public void setTicksPresentationType(DataType.DataPresentationType ticksPresentationType) {
/* 174 */     this.ticksPresentationType = ticksPresentationType;
/*     */   }
/*     */   public int getGridVisible() {
/* 177 */     return this.gridVisible;
/*     */   }
/*     */ 
/*     */   public void setGridVisible(int gridVisible) {
/* 181 */     this.gridVisible = gridVisible;
/*     */   }
/*     */ 
/*     */   public int getMouseCursorVisible() {
/* 185 */     return this.mouseCursorVisible;
/*     */   }
/*     */ 
/*     */   public void setMouseCursorVisible(int mouseCursorVisible) {
/* 189 */     this.mouseCursorVisible = mouseCursorVisible;
/*     */   }
/*     */ 
/*     */   public int getLastCandleVisible() {
/* 193 */     return this.lastCandleVisible;
/*     */   }
/*     */ 
/*     */   public void setLastCandleVisible(int lastCandleVisible) {
/* 197 */     this.lastCandleVisible = lastCandleVisible;
/*     */   }
/*     */ 
/*     */   public int getVerticalMovementEnabled() {
/* 201 */     return this.verticalMovementEnabled;
/*     */   }
/*     */ 
/*     */   public void setVerticalMovementEnabled(int verticalMovementEnabled) {
/* 205 */     this.verticalMovementEnabled = verticalMovementEnabled;
/*     */   }
/*     */ 
/*     */   public boolean getVerticalMovementEnabledAsBoolean() {
/* 209 */     return this.verticalMovementEnabled != 0;
/*     */   }
/*     */ 
/*     */   public void setVerticalMovementEnabledAsBoolean(boolean verticalMovementEnabled) {
/* 213 */     this.verticalMovementEnabled = (verticalMovementEnabled ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public long getEndTime() {
/* 217 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public void setEndTime(long endTime) {
/* 221 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public double getMinPrice() {
/* 225 */     return this.minPrice;
/*     */   }
/*     */ 
/*     */   public void setMinPrice(double minPrice) {
/* 229 */     this.minPrice = minPrice;
/*     */   }
/*     */ 
/*     */   public double getMaxPrice() {
/* 233 */     return this.maxPrice;
/*     */   }
/*     */ 
/*     */   public void setMaxPrice(double maxPrice) {
/* 237 */     this.maxPrice = maxPrice;
/*     */   }
/*     */ 
/*     */   public int getChartShiftInPx() {
/* 241 */     return this.chartShiftInPx;
/*     */   }
/*     */ 
/*     */   public void setChartShiftInPx(int chartShiftInPx) {
/* 245 */     this.chartShiftInPx = chartShiftInPx;
/*     */   }
/*     */ 
/*     */   public int getDataUnitWidth() {
/* 249 */     return this.dataUnitWidth;
/*     */   }
/*     */ 
/*     */   public void setDataUnitWidth(int dataUnitWidth) {
/* 253 */     this.dataUnitWidth = dataUnitWidth;
/*     */   }
/*     */ 
/*     */   public double getYAxisPadding() {
/* 257 */     return this.yAxisPadding;
/*     */   }
/*     */ 
/*     */   public void setYAxisPadding(double yAxisPadding) {
/* 261 */     this.yAxisPadding = yAxisPadding;
/*     */   }
/*     */ 
/*     */   public int getAutoShiftActive() {
/* 265 */     return this.autoShiftActive;
/*     */   }
/*     */ 
/*     */   public boolean getAutoShiftActiveAsBoolean() {
/* 269 */     return getAutoShiftActive() == 1;
/*     */   }
/*     */ 
/*     */   public void setAutoShiftActive(int autoShiftActive) {
/* 273 */     this.autoShiftActive = autoShiftActive;
/*     */   }
/*     */ 
/*     */   public List<IndicatorWrapper> getIndicatorWrappers() {
/* 277 */     return this.indicatorWrappers;
/*     */   }
/*     */ 
/*     */   public void setIndicatorWrappers(List<IndicatorWrapper> indicatorWrappers) {
/* 281 */     this.indicatorWrappers = indicatorWrappers;
/*     */   }
/*     */ 
/*     */   public void addIndicatorWrapper(IndicatorWrapper wrapper) {
/* 285 */     if (this.indicatorWrappers == null) {
/* 286 */       this.indicatorWrappers = new LinkedList();
/*     */     }
/* 288 */     this.indicatorWrappers.add(wrapper);
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getChartObjects() {
/* 292 */     return this.chartObjects;
/*     */   }
/*     */ 
/*     */   public void setChartObjects(List<IChartObject> chartObjects) {
/* 296 */     this.chartObjects = chartObjects;
/*     */   }
/*     */ 
/*     */   public IFeedDataProvider getFeedDataProvider() {
/* 300 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void setFeedDataProvider(IFeedDataProvider feedDataProvider) {
/* 304 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public Runnable getStartLoadingDataRunnable() {
/* 308 */     return this.startLoadingDataRunnable;
/*     */   }
/*     */ 
/*     */   public void setStartLoadingDataRunnable(Runnable startLoadingDataRunnable) {
/* 312 */     this.startLoadingDataRunnable = startLoadingDataRunnable;
/*     */   }
/*     */ 
/*     */   public boolean getReadOnly() {
/* 316 */     return this.readOnly;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly) {
/* 320 */     this.readOnly = readOnly;
/*     */   }
/*     */ 
/*     */   public boolean isHistoricalTesterChart() {
/* 324 */     return this.historicalTesterChart;
/*     */   }
/*     */ 
/*     */   public void setHistoricalTesterChart(boolean historicalTesterChart) {
/* 328 */     this.historicalTesterChart = historicalTesterChart;
/*     */   }
/*     */ 
/*     */   public DataType getDataType() {
/* 332 */     return getJForexPeriod().getDataType();
/*     */   }
/*     */ 
/*     */   public void setDataType(DataType dataType) {
/* 336 */     getJForexPeriod().setDataType(dataType);
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getPriceRangePresentationType() {
/* 340 */     return this.priceRangePresentationType;
/*     */   }
/*     */ 
/*     */   public void setPriceRangePresentationType(DataType.DataPresentationType priceRangePresentationType) {
/* 344 */     this.priceRangePresentationType = priceRangePresentationType;
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 348 */     return getJForexPeriod().getPriceRange();
/*     */   }
/*     */ 
/*     */   public void setPriceRange(PriceRange priceRange) {
/* 352 */     getJForexPeriod().setPriceRange(priceRange);
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getPointAndFigurePresentationType() {
/* 356 */     return this.pointAndFigurePresentationType;
/*     */   }
/*     */ 
/*     */   public void setPointAndFigurePresentationType(DataType.DataPresentationType pointAndFigurePresentationType)
/*     */   {
/* 361 */     this.pointAndFigurePresentationType = pointAndFigurePresentationType;
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 365 */     return getJForexPeriod().getReversalAmount();
/*     */   }
/*     */ 
/*     */   public void setReversalAmount(ReversalAmount reversalAmount) {
/* 369 */     getJForexPeriod().setReversalAmount(reversalAmount);
/*     */   }
/*     */ 
/*     */   public JForexPeriod getJForexPeriod() {
/* 373 */     if (this.jForexPeriod == null) {
/* 374 */       this.jForexPeriod = new JForexPeriod();
/*     */     }
/* 376 */     return this.jForexPeriod;
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod) {
/* 380 */     this.jForexPeriod = jForexPeriod;
/*     */   }
/*     */ 
/*     */   public boolean isNeedCreateDefaultChartObjects() {
/* 384 */     return this.needCreateDefaultChartObjects;
/*     */   }
/*     */ 
/*     */   public void setNeedCreateDefaultChartObjects(boolean needCreateDefaultChartObjects) {
/* 388 */     this.needCreateDefaultChartObjects = needCreateDefaultChartObjects;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getTickBarPresentationType() {
/* 392 */     return this.tickBarPresentationType;
/*     */   }
/*     */ 
/*     */   public void setTickBarPresentationType(DataType.DataPresentationType tickBarPresentationType) {
/* 396 */     this.tickBarPresentationType = tickBarPresentationType;
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize() {
/* 400 */     return getJForexPeriod().getTickBarSize();
/*     */   }
/*     */ 
/*     */   public void setTickBarSize(TickBarSize tickBarSize) {
/* 404 */     getJForexPeriod().setTickBarSize(tickBarSize);
/*     */   }
/*     */ 
/*     */   public ITheme getTheme() {
/* 408 */     return this.theme;
/*     */   }
/*     */   public void setTheme(ITheme theme) {
/* 411 */     this.theme = theme;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getRenkoPresentationType() {
/* 415 */     return this.renkoPresentationType;
/*     */   }
/*     */ 
/*     */   public void setRenkoPresentationType(DataType.DataPresentationType renkoPresentationType) {
/* 419 */     this.renkoPresentationType = renkoPresentationType;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.ChartBean
 * JD-Core Version:    0.6.0
 */