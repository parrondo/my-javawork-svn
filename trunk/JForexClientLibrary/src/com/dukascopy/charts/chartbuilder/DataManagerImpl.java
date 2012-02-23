/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.CandlesDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.TicksDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.priceaggregation.PointAndFigureDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.priceaggregation.PriceRangeDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.priceaggregation.RenkoDataSequenceProvider;
/*     */ import com.dukascopy.charts.listeners.ChartSystemListenerManager;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.listeners.datachange.MainDataChangeListener;
/*     */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class DataManagerImpl
/*     */   implements IDataManagerAndIndicatorsContainer
/*     */ {
/*  33 */   private static final Logger LOGGER = LoggerFactory.getLogger(DataManagerImpl.class);
/*     */   private final ChartState chartState;
/*     */   private final GuiRefresher guiRefresher;
/*     */   private MainDataChangeListener mainDataChangeListner;
/*     */   private final ChartSystemListenerManager chartSystemListenerManager;
/*  42 */   private final List<IndicatorWrapper> indicators = new ArrayList();
/*     */   private final Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> providersMap;
/*     */ 
/*     */   public DataManagerImpl(GuiRefresher guiRefresher, ChartState chartState, ChartSystemListenerManager chartSystemListenerManager, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allProvidersMap)
/*     */   {
/*  52 */     this.guiRefresher = guiRefresher;
/*  53 */     this.chartState = chartState;
/*  54 */     this.chartSystemListenerManager = chartSystemListenerManager;
/*     */ 
/*  56 */     this.providersMap = allProvidersMap;
/*     */   }
/*     */ 
/*     */   public void addIndicator(IndicatorWrapper indicatorWrapper, int subChartId) throws Exception
/*     */   {
/*  61 */     if (LOGGER.isTraceEnabled())
/*  62 */       LOGGER.trace(new StringBuffer("adding new indicator id: ").append(indicatorWrapper.getId()).append(", name: ").append(indicatorWrapper.getName()).toString());
/*     */     try
/*     */     {
/*  65 */       indicatorWrapper.setSubPanelId(new Integer(subChartId));
/*  66 */       getDataSequenceProvider().addIndicator(indicatorWrapper);
/*  67 */       this.indicators.add(indicatorWrapper);
/*  68 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().indicatorAdded(indicatorWrapper, subChartId);
/*     */     } catch (Exception e) {
/*  70 */       this.indicators.remove(indicatorWrapper);
/*  71 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().indicatorRemoved(indicatorWrapper);
/*  72 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(e.getMessage(), true);
/*  73 */       throw new Exception("Failed to add indicator to data manager...", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void editIndicator(IndicatorWrapper indicatorWrapper, int subChartId, boolean isLightweightEditing)
/*     */   {
/*  79 */     if (LOGGER.isTraceEnabled())
/*  80 */       LOGGER.trace(new StringBuffer("editing indicator id: ").append(indicatorWrapper.getId()).append(", name: ").append(indicatorWrapper.getName()).toString());
/*     */     try
/*     */     {
/*  83 */       if (!this.indicators.contains(indicatorWrapper)) {
/*  84 */         return;
/*     */       }
/*  86 */       if (!isLightweightEditing)
/*     */       {
/*  90 */         for (AbstractDataSequenceProvider provider : this.providersMap.values()) {
/*  91 */           if (provider.containsIndicator(indicatorWrapper)) {
/*  92 */             provider.editIndicator(indicatorWrapper);
/*     */           }
/*     */         }
/*     */       }
/*  96 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().indicatorChanged(indicatorWrapper, subChartId);
/*     */     } catch (Exception e) {
/*  98 */       LOGGER.warn("Failed to edit indicator [" + indicatorWrapper.getName() + "] due : " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteIndicator(IndicatorWrapper indicatorWrapper) throws Exception
/*     */   {
/* 104 */     if (LOGGER.isTraceEnabled())
/* 105 */       LOGGER.trace(new StringBuffer("deleting indicator id: ").append(indicatorWrapper.getId()).append(", name: ").append(indicatorWrapper.getName()).toString());
/*     */     try
/*     */     {
/* 108 */       getDataSequenceProvider().removeIndicator(indicatorWrapper);
/*     */     } finally {
/* 110 */       this.indicators.remove(indicatorWrapper);
/* 111 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().indicatorRemoved(indicatorWrapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteIndicators(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 117 */     if (LOGGER.isTraceEnabled()) {
/* 118 */       StringBuffer logMessage = new StringBuffer("deleting indicators id: ");
/* 119 */       for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 120 */         logMessage.append("[").append(indicatorWrapper.getId());
/* 121 */         logMessage.append(", name: ").append(indicatorWrapper.getName()).append("] ");
/*     */       }
/* 123 */       LOGGER.trace(logMessage.toString());
/*     */     }
/*     */     try {
/* 126 */       getDataSequenceProvider().removeIndicators(indicatorWrappers);
/* 127 */       this.guiRefresher.deleteSubChartViewsIfNecessary(indicatorWrappers);
/* 128 */       this.indicators.removeAll(indicatorWrappers);
/* 129 */       this.chartSystemListenerManager.getChartsActionListenerRegistry().indicatorsRemoved(indicatorWrappers);
/*     */     } catch (Exception e) {
/* 131 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deleteAllIndicators()
/*     */   {
/* 137 */     for (AbstractDataSequenceProvider provider : this.providersMap.values()) {
/* 138 */       provider.removeAllIndicators();
/*     */     }
/*     */ 
/* 141 */     this.indicators.clear();
/*     */   }
/*     */ 
/*     */   public void addMainDataChangeListener(MainDataChangeListener dataChangeListener)
/*     */   {
/* 146 */     for (AbstractDataSequenceProvider provider : this.providersMap.values()) {
/* 147 */       provider.addDataChangeListener(dataChangeListener);
/*     */     }
/*     */ 
/* 150 */     this.mainDataChangeListner = dataChangeListener;
/*     */   }
/*     */ 
/*     */   public void addProgressListener(ProgressListener progressListener)
/*     */   {
/* 155 */     this.mainDataChangeListner.addProgressListener(progressListener);
/*     */   }
/*     */ 
/*     */   public double getMinPriceFor(Integer indicatorId)
/*     */   {
/* 160 */     if (indicatorId == null)
/*     */     {
/* 164 */       return ((AbstractDataSequence)getDataSequenceProvider().getDataSequence()).getMin();
/*     */     }
/* 166 */     return ((AbstractDataSequence)getDataSequenceProvider().getDataSequence()).getFormulasMinFor(indicatorId);
/*     */   }
/*     */ 
/*     */   public double getMaxPriceFor(Integer indicatorId)
/*     */   {
/* 171 */     if (indicatorId == null)
/*     */     {
/* 175 */       return ((AbstractDataSequence)getDataSequenceProvider().getDataSequence()).getMax();
/*     */     }
/* 177 */     return ((AbstractDataSequence)getDataSequenceProvider().getDataSequence()).getFormulasMaxFor(indicatorId);
/*     */   }
/*     */ 
/*     */   public void setSequenceSize(int sequenceSize)
/*     */   {
/* 182 */     getDataSequenceProvider().setSequenceSize(sequenceSize);
/*     */   }
/*     */ 
/*     */   public int getSequenceSize()
/*     */   {
/* 187 */     return getDataSequenceProvider().getSequenceSize();
/*     */   }
/*     */ 
/*     */   public void setTime(long time)
/*     */   {
/* 192 */     getDataSequenceProvider().setTime(time);
/*     */   }
/*     */ 
/*     */   public long getTime()
/*     */   {
/* 197 */     return getDataSequenceProvider().getTime();
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period oldPeriod, Period newPeriod)
/*     */   {
/* 202 */     CandlesDataSequenceProvider candlesDataSequenceProvider = (CandlesDataSequenceProvider)this.providersMap.get(DataType.TIME_PERIOD_AGGREGATION);
/* 203 */     TicksDataSequenceProvider ticksDataSequenceProvider = (TicksDataSequenceProvider)this.providersMap.get(DataType.TICKS);
/*     */ 
/* 205 */     candlesDataSequenceProvider.setPeriod(newPeriod);
/* 206 */     if (this.chartState.isChartShiftActive())
/* 207 */       candlesDataSequenceProvider.shiftToLastTick(ticksDataSequenceProvider.getMargin());
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 213 */     return this.chartState.getPeriod();
/*     */   }
/*     */ 
/*     */   public void shift(int shiftValue)
/*     */   {
/* 218 */     getDataSequenceProvider().shift(shiftValue);
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 223 */     getDataSequenceProvider().setActive(true);
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 228 */     for (AbstractDataSequenceProvider provider : this.providersMap.values())
/* 229 */       provider.dispose();
/*     */   }
/*     */ 
/*     */   public long getMinimalTime(Period period)
/*     */   {
/* 235 */     return getDataSequenceProvider().getMinimalTime(period);
/*     */   }
/*     */ 
/*     */   private AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data> getDataSequenceProvider() {
/* 239 */     DataType dataType = this.chartState.getDataType();
/* 240 */     AbstractDataSequenceProvider provider = (AbstractDataSequenceProvider)this.providersMap.get(dataType);
/* 241 */     if (provider == null) {
/* 242 */       throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*     */     }
/* 244 */     return provider;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 249 */     return this.chartState.getInstrument();
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument oldInstrument, Instrument newInstrument)
/*     */   {
/* 254 */     if (oldInstrument != newInstrument)
/*     */     {
/* 256 */       for (AbstractDataSequenceProvider provider : this.providersMap.values()) {
/* 257 */         setInstrument(provider, newInstrument);
/*     */       }
/*     */ 
/* 260 */       this.mainDataChangeListner.setInstrument(newInstrument);
/* 261 */       this.chartState.setInstrument(newInstrument);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setInstrument(AbstractDataSequenceProvider<?, ?> provider, Instrument instrument)
/*     */   {
/* 269 */     if (provider.isActive()) {
/* 270 */       provider.setInstrument(instrument);
/*     */     }
/*     */     else
/* 273 */       provider.justSetInstrument(instrument);
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 279 */     return this.chartState.getDataType();
/*     */   }
/*     */ 
/*     */   public void changeDataType(DataType oldDataType, DataType newDataType)
/*     */   {
/* 284 */     if (oldDataType != newDataType) {
/* 285 */       this.chartState.setDataType(newDataType);
/*     */ 
/* 287 */       for (DataType key : this.providersMap.keySet()) {
/* 288 */         AbstractDataSequenceProvider provider = (AbstractDataSequenceProvider)this.providersMap.get(key);
/* 289 */         if (provider == null) {
/* 290 */           throw new IllegalArgumentException("Unsupported Data Type - " + newDataType);
/*     */         }
/* 292 */         boolean active = newDataType.equals(key);
/* 293 */         provider.setActive(active);
/*     */       }
/*     */ 
/* 296 */       if (DataType.TIME_PERIOD_AGGREGATION.equals(newDataType)) {
/* 297 */         CandlesDataSequenceProvider candlesDataSequenceProvider = (CandlesDataSequenceProvider)this.providersMap.get(DataType.TIME_PERIOD_AGGREGATION);
/* 298 */         this.chartState.setPeriod(candlesDataSequenceProvider.getPeriod());
/*     */       }
/*     */       else {
/* 301 */         this.chartState.setPeriod(Period.TICK);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void priceRangeChanged(PriceRange priceRange)
/*     */   {
/* 308 */     PriceRange oldPriceRange = this.chartState.getPriceRange();
/* 309 */     if (oldPriceRange != priceRange) {
/* 310 */       this.chartState.setPriceRange(priceRange);
/* 311 */       ((PriceRangeDataSequenceProvider)this.providersMap.get(DataType.PRICE_RANGE_AGGREGATION)).setPriceRange(priceRange);
/* 312 */       ((PointAndFigureDataSequenceProvider)this.providersMap.get(DataType.POINT_AND_FIGURE)).setPriceRange(priceRange);
/* 313 */       ((RenkoDataSequenceProvider)this.providersMap.get(DataType.RENKO)).setBrickSize(priceRange);
/*     */     }
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange()
/*     */   {
/* 319 */     return this.chartState.getPriceRange();
/*     */   }
/*     */ 
/*     */   public void reversalAmountChanged(ReversalAmount reversalAmount)
/*     */   {
/* 324 */     ReversalAmount oldReversalAmount = this.chartState.getReversalAmount();
/* 325 */     if (oldReversalAmount != reversalAmount) {
/* 326 */       this.chartState.setReversalAmount(reversalAmount);
/* 327 */       ((PointAndFigureDataSequenceProvider)this.providersMap.get(DataType.POINT_AND_FIGURE)).setReversalAmount(reversalAmount);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod)
/*     */   {
/* 333 */     for (AbstractDataSequenceProvider provider : this.providersMap.values()) {
/* 334 */       provider.setActive(false);
/*     */     }
/*     */ 
/* 337 */     this.chartState.setPeriod(jForexPeriod.getPeriod());
/* 338 */     this.chartState.setDataType(jForexPeriod.getDataType());
/* 339 */     this.chartState.setPriceRange(jForexPeriod.getPriceRange());
/* 340 */     this.chartState.setReversalAmount(jForexPeriod.getReversalAmount());
/* 341 */     this.chartState.setTickBarSize(jForexPeriod.getTickBarSize());
/*     */ 
/* 343 */     AbstractDataSequenceProvider dataSequenceProvider = getDataSequenceProvider();
/*     */ 
/* 345 */     dataSequenceProvider.setJForexPeriod(jForexPeriod);
/* 346 */     if (dataSequenceProvider.getOfferSide() != null) {
/* 347 */       this.chartState.setOfferSide(dataSequenceProvider.getOfferSide());
/*     */     }
/* 349 */     dataSequenceProvider.setActive(true);
/*     */   }
/*     */ 
/*     */   public void reactivate()
/*     */   {
/* 354 */     AbstractDataSequenceProvider dataSequenceProvider = getDataSequenceProvider();
/*     */ 
/* 356 */     dataSequenceProvider.setActive(false);
/* 357 */     dataSequenceProvider.setActive(true);
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount()
/*     */   {
/* 362 */     return this.chartState.getReversalAmount();
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize()
/*     */   {
/* 367 */     return this.chartState.getTickBarSize();
/*     */   }
/*     */ 
/*     */   public final List<IndicatorWrapper> getIndicators()
/*     */   {
/* 375 */     return Collections.unmodifiableList(this.indicators);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.DataManagerImpl
 * JD-Core Version:    0.6.0
 */