/*      */ package com.dukascopy.charts.math.dataprovider;
/*      */ 
/*      */ import com.dukascopy.api.ConnectorIndicator;
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.feed.IFeedDescriptor;
/*      */ import com.dukascopy.api.impl.History;
/*      */ import com.dukascopy.api.impl.IndicatorContext;
/*      */ import com.dukascopy.api.impl.IndicatorHolder;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.api.impl.Indicators;
/*      */ import com.dukascopy.api.impl.StrategyWrapper;
/*      */ import com.dukascopy.api.impl.TaLibException;
/*      */ import com.dukascopy.api.indicators.BooleanOptInputDescription;
/*      */ import com.dukascopy.api.indicators.DoubleListDescription;
/*      */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.IndicatorResult;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*      */ import com.dukascopy.api.indicators.IntegerListDescription;
/*      */ import com.dukascopy.api.indicators.IntegerRangeDescription;
/*      */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*      */ import com.dukascopy.charts.data.datacache.CacheDataUpdatedListener;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*      */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public abstract class AbstractDataProvider<D extends Data, S extends IDataSequence<D>>
/*      */   implements IDataProvider<D, S>
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   protected static final SimpleDateFormat DATE_FORMAT;
/*      */   protected final IFeedDataProvider feedDataProvider;
/*      */   protected Instrument instrument;
/*      */   protected Period period;
/*      */   protected OfferSide side;
/*      */   protected Filter filter;
/*   79 */   protected Period dailyFilterPeriod = Period.DAILY;
/*      */   protected int maxNumberOfCandles;
/*      */   protected int bufferSizeMultiplier;
/*      */   protected D firstData;
/*      */   protected volatile boolean active;
/*      */   protected volatile boolean loadingStarted;
/*   90 */   private final Set<DataChangeListener> dataChangeListeners = Collections.synchronizedSet(new HashSet());
/*      */   protected AbstractDataProvider<D, S>.CacheDataUpdatedListener cacheDataUpdatedListener;
/*   92 */   protected Map<Integer, IndicatorData> formulas = new HashMap();
/*      */   protected int formulasMinShift;
/*      */   protected int formulasMaxShift;
/*      */   protected boolean sparceIndicator;
/*      */   protected AbstractDataProvider<D, S> parentDataProvider;
/*      */   protected IndicatorData parentIndicatorData;
/*   99 */   protected Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT0"));
/*      */ 
/*  101 */   private Indicators indicators = null;
/*      */   protected AbstractDataProvider<D, S>.IndicatorRecalculationThread indicatorRecalculationThread;
/*      */ 
/*      */   protected AbstractDataProvider<D, S>.IndicatorRecalculationThread getIndicatorRecalculationThread()
/*      */   {
/*  106 */     return this.indicatorRecalculationThread;
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider(Instrument instrument, Period period, OfferSide side, int maxNumberOfCandles, int bufferSizeMultiplier, Filter filter, IFeedDataProvider feedDataProvider)
/*      */   {
/*  339 */     this.instrument = instrument;
/*  340 */     this.period = period;
/*  341 */     this.side = side;
/*  342 */     this.maxNumberOfCandles = maxNumberOfCandles;
/*  343 */     this.bufferSizeMultiplier = bufferSizeMultiplier;
/*  344 */     this.filter = filter;
/*  345 */     this.feedDataProvider = feedDataProvider;
/*  346 */     this.cal.setFirstDayOfWeek(2);
/*      */   }
/*      */ 
/*      */   public Instrument getInstrument()
/*      */   {
/*  351 */     return this.instrument;
/*      */   }
/*      */ 
/*      */   public Period getPeriod()
/*      */   {
/*  356 */     return this.period;
/*      */   }
/*      */ 
/*      */   public OfferSide getOfferSide()
/*      */   {
/*  361 */     return this.side;
/*      */   }
/*      */ 
/*      */   public synchronized long getLatestDataTime()
/*      */   {
/*  368 */     if (this.period == Period.TICK) {
/*  369 */       return this.firstData == null ? this.feedDataProvider.getLastTickTime(this.instrument) : this.firstData.time;
/*      */     }
/*  371 */     return this.firstData == null ? DataCacheUtils.getPreviousCandleStartFast(this.period, DataCacheUtils.getCandleStartFast(this.period, this.feedDataProvider.getCurrentTime(this.instrument))) : this.feedDataProvider.getCurrentTime(this.instrument) == -9223372036854775808L ? -9223372036854775808L : this.firstData.time;
/*      */   }
/*      */ 
/*      */   public long getLatestDataTime(int indicatorId)
/*      */   {
/*  381 */     IndicatorData formula = (IndicatorData)this.formulas.get(Integer.valueOf(indicatorId));
/*  382 */     if (formula == null) {
/*  383 */       throw new IllegalArgumentException(new StringBuilder().append("Formula with id [").append(indicatorId).append("] not found in this DataProvider").toString());
/*      */     }
/*  385 */     return formula.lastTime;
/*      */   }
/*      */ 
/*      */   public Object getLatestValue(int indicatorId, int outputIdx)
/*      */   {
/*  391 */     IndicatorData formula = (IndicatorData)this.formulas.get(Integer.valueOf(indicatorId));
/*  392 */     if (formula == null)
/*  393 */       throw new IllegalArgumentException(new StringBuilder().append("Formula with id [").append(indicatorId).append("] not found in this DataProvider").toString());
/*  394 */     if (formula.lastValues == null)
/*      */     {
/*  396 */       return null;
/*      */     }
/*  398 */     return formula.lastValues[outputIdx];
/*      */   }
/*      */ 
/*      */   public synchronized void addIndicator(IndicatorWrapper indicatorWrapper)
/*      */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  405 */     if (this.formulas.containsKey(Integer.valueOf(indicatorWrapper.getId()))) {
/*  406 */       throw new IllegalArgumentException(new StringBuilder().append("Formula with id [").append(indicatorWrapper.getId()).append("] already exists in this DataProvider").toString());
/*      */     }
/*  408 */     IndicatorData formulaData = new IndicatorData();
/*  409 */     IIndicator indicator = indicatorWrapper.getIndicator();
/*  410 */     formulaData.indicatorWrapper = indicatorWrapper;
/*  411 */     boolean resetData = initIndicatorInputs(formulaData);
/*      */     try
/*      */     {
/*  414 */       formulaData.lookback = indicator.getLookback();
/*      */     } catch (Throwable t) {
/*  416 */       LOGGER.error(t.getMessage(), t);
/*  417 */       String error = StrategyWrapper.representError(indicator, t);
/*  418 */       throw new RuntimeException(new StringBuilder().append("Error in indicator: ").append(error).toString(), t);
/*      */     }
/*      */     try {
/*  421 */       formulaData.lookforward = indicator.getLookforward();
/*      */     } catch (AbstractMethodError e) {
/*  423 */       formulaData.lookforward = 0;
/*      */     } catch (Throwable t) {
/*  425 */       LOGGER.error(t.getMessage(), t);
/*  426 */       String error = StrategyWrapper.representError(indicator, t);
/*  427 */       throw new RuntimeException(new StringBuilder().append("Error in indicator: ").append(error).toString(), t);
/*      */     }
/*      */ 
/*  432 */     indicatorWrapper.synchronizeDrawingStyles();
/*      */ 
/*  434 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*  435 */     IndicatorData.access$402(formulaData, new double[indicatorInfo.getNumberOfOutputs()][]);
/*  436 */     IndicatorData.access$502(formulaData, new int[indicatorInfo.getNumberOfOutputs()][]);
/*  437 */     IndicatorData.access$602(formulaData, new Object[indicatorInfo.getNumberOfOutputs()][]);
/*      */ 
/*  439 */     initIndicatorDataOutputBuffers(formulaData);
/*  440 */     this.formulas.put(Integer.valueOf(indicatorWrapper.getId()), formulaData);
/*  441 */     calculateMinMaxShiftAndSparceFlag();
/*  442 */     if (resetData) {
/*  443 */       setFilter(this.filter);
/*      */     }
/*  445 */     recalculateIndicators();
/*  446 */     fireIndicatorAdded(indicatorWrapper.getId());
/*      */   }
/*      */ 
/*      */   private void calculateMinMaxShiftAndSparceFlag() {
/*  450 */     this.formulasMinShift = 0;
/*  451 */     this.formulasMaxShift = 0;
/*  452 */     this.sparceIndicator = false;
/*  453 */     for (Map.Entry entry : this.formulas.entrySet()) {
/*  454 */       IIndicator indicator = ((IndicatorData)entry.getValue()).indicatorWrapper.getIndicator();
/*  455 */       if (indicator.getIndicatorInfo().isSparseIndicator()) {
/*  456 */         this.sparceIndicator = true;
/*      */       }
/*  458 */       int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); i < j; i++) {
/*  459 */         int shift = ((IndicatorData)entry.getValue()).indicatorWrapper.getOutputShifts()[i];
/*  460 */         if (shift < 0) {
/*  461 */           if (shift < this.formulasMinShift)
/*  462 */             this.formulasMinShift = shift;
/*      */         } else {
/*  464 */           if ((shift <= 0) || 
/*  465 */             (shift <= this.formulasMaxShift)) continue;
/*  466 */           this.formulasMaxShift = shift; }  }  }  } 
/*      */   protected abstract void dataLoaded(boolean paramBoolean, AbstractDataCacheRequestData paramAbstractDataCacheRequestData, Exception paramException, ISynchronizeIndicators paramISynchronizeIndicators);
/*      */ 
/*      */   protected abstract void initIndicatorDataOutputBuffers(IndicatorData paramIndicatorData);
/*      */ 
/*      */   protected abstract void recalculateIndicators();
/*      */ 
/*      */   protected abstract void recalculateIndicator(IndicatorData paramIndicatorData, boolean paramBoolean1, boolean paramBoolean2);
/*      */ 
/*      */   public abstract AbstractDataProvider<D, S>.LoadDataProgressListener doHistoryRequests(int paramInt1, long paramLong, int paramInt2);
/*      */ 
/*      */   public abstract AbstractDataProvider<D, S>.LoadDataProgressListener doHistoryRequests(long paramLong1, long paramLong2);
/*      */ 
/*      */   public abstract long getLastLoadedDataTime();
/*      */ 
/*      */   protected abstract D[] getAllBufferedData();
/*      */ 
/*      */   protected abstract D[] createArray(int paramInt);
/*      */ 
/*      */   protected abstract S createNullDataSequence();
/*      */ 
/*      */   protected abstract S createDataSequence(D[] paramArrayOfD, boolean paramBoolean);
/*      */ 
/*  495 */   public synchronized boolean containsIndicator(int id) { return this.formulas.containsKey(Integer.valueOf(id)); }
/*      */ 
/*      */   public synchronized void editIndicator(int id)
/*      */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  500 */     if (!this.formulas.containsKey(Integer.valueOf(id))) {
/*  501 */       throw new IllegalArgumentException(new StringBuilder().append("Formula with id [").append(id).append("] doesn't exist").toString());
/*      */     }
/*  503 */     IndicatorData formulaData = (IndicatorData)this.formulas.get(Integer.valueOf(id));
/*  504 */     IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/*  505 */     boolean resetData = initIndicatorInputs(formulaData);
/*      */     try {
/*  507 */       formulaData.lookback = indicator.getLookback();
/*      */     } catch (Throwable t) {
/*  509 */       LOGGER.error(t.getMessage(), t);
/*  510 */       String error = StrategyWrapper.representError(indicator, t);
/*  511 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*  512 */       return;
/*      */     }
/*      */     try {
/*  515 */       formulaData.lookforward = indicator.getLookforward();
/*      */     } catch (AbstractMethodError e) {
/*  517 */       formulaData.lookforward = 0;
/*      */     } catch (Throwable t) {
/*  519 */       LOGGER.error(t.getMessage(), t);
/*  520 */       String error = StrategyWrapper.representError(indicator, t);
/*  521 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*  522 */       return;
/*      */     }
/*      */ 
/*  525 */     calculateMinMaxShiftAndSparceFlag();
/*  526 */     if (resetData) {
/*  527 */       setFilter(this.filter);
/*      */     }
/*  529 */     recalculateIndicators();
/*  530 */     fireIndicatorChanged(id);
/*      */   }
/*      */ 
/*      */   protected boolean initIndicatorInputs(IndicatorData formulaData)
/*      */   {
/*  535 */     IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/*      */ 
/*  537 */     OfferSide[] tickOfferSides = formulaData.indicatorWrapper.getOfferSidesForTicks();
/*  538 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*      */ 
/*  541 */     int i = 0; for (int j = indicatorInfo.getNumberOfOptionalInputs(); i < j; i++) {
/*  542 */       OptInputParameterInfo optInputParameterInfo = indicator.getOptInputParameterInfo(i);
/*  543 */       Object[] optParams = formulaData.indicatorWrapper.getOptParams();
/*  544 */       if (((optInputParameterInfo.getDescription() instanceof IntegerListDescription)) || ((optInputParameterInfo.getDescription() instanceof IntegerRangeDescription)))
/*      */       {
/*  546 */         if ((optParams[i] instanceof Integer))
/*  547 */           indicator.setOptInputParameter(i, optParams[i]);
/*      */         else
/*  549 */           throw new IllegalArgumentException(new StringBuilder().append("Unexpected optional parameter [").append(optParams[i].getClass().getName()).append("]=[").append(optParams[i]).append("] for index [").append(i).append("]").toString());
/*      */       }
/*  551 */       else if (((optInputParameterInfo.getDescription() instanceof DoubleListDescription)) || ((optInputParameterInfo.getDescription() instanceof DoubleRangeDescription)))
/*      */       {
/*  553 */         if ((optParams[i] instanceof Double))
/*  554 */           indicator.setOptInputParameter(i, optParams[i]);
/*      */         else {
/*  556 */           throw new IllegalArgumentException(new StringBuilder().append("Unexpected optional parameter [").append(optParams[i].getClass().getName()).append("]=[").append(optParams[i]).append("] for index [").append(i).append("]").toString());
/*      */         }
/*      */       }
/*  559 */       else if ((optInputParameterInfo.getDescription() instanceof BooleanOptInputDescription)) {
/*  560 */         if ((optParams[i] instanceof Boolean))
/*  561 */           indicator.setOptInputParameter(i, optParams[i]);
/*      */         else {
/*  563 */           throw new IllegalArgumentException(new StringBuilder().append("Unexpected optional parameter [").append(optParams[i].getClass().getName()).append("]=[").append(optParams[i]).append("] for index [").append(i).append("]").toString());
/*      */         }
/*      */       }
/*      */     }
/*  567 */     int numberOfInputs = indicatorInfo.getNumberOfInputs();
/*  568 */     boolean resetData = false;
/*  569 */     formulaData.disabledIndicator = false;
/*  570 */     for (int i = 0; i < numberOfInputs; i++) {
/*  571 */       InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/*      */ 
/*  573 */       if ((inputParameterInfo.getOfferSide() != null) && ((formulaData.inputSides == null) || (inputParameterInfo.getOfferSide() != formulaData.inputSides[i]) || (formulaData.inputDataProviders == null) || (formulaData.inputDataProviders[i] == null))) { if (inputParameterInfo.getOfferSide() != (isTicksDataType() ? tickOfferSides[i] : this.side)); } else if (((inputParameterInfo.getPeriod() == null) || ((formulaData.inputPeriods != null) && (inputParameterInfo.getPeriod() == formulaData.inputPeriods[i]) && (formulaData.inputDataProviders != null) && (formulaData.inputDataProviders[i] != null)) || ((noFilterPeriod(inputParameterInfo.getPeriod()) == this.period) && (dailyFilterPeriod(inputParameterInfo.getPeriod()) == dailyFilterPeriod(this.dailyFilterPeriod)))) && ((inputParameterInfo.getInstrument() == null) || ((formulaData.inputInstruments != null) && (inputParameterInfo.getInstrument() == formulaData.inputInstruments[i]) && (formulaData.inputDataProviders != null) && (formulaData.inputDataProviders[i] != null)) || (inputParameterInfo.getInstrument() == this.instrument)) && ((inputParameterInfo.getFilter() == null) || ((formulaData.inputFilters != null) && (inputParameterInfo.getFilter() == formulaData.inputFilters[i]) && (formulaData.inputDataProviders != null) && (formulaData.inputDataProviders[i] != null)) || (inputParameterInfo.getFilter() == this.filter)))
/*      */         {
/*      */           break label1237;
/*      */         }
/*      */ 
/*  579 */       if (formulaData.inputSides == null) {
/*  580 */         formulaData.inputSides = new OfferSide[numberOfInputs];
/*      */       }
/*  582 */       if (formulaData.inputPeriods == null) {
/*  583 */         formulaData.inputPeriods = new Period[numberOfInputs];
/*      */       }
/*  585 */       if (formulaData.inputInstruments == null) {
/*  586 */         formulaData.inputInstruments = new Instrument[numberOfInputs];
/*      */       }
/*  588 */       if (formulaData.inputFilters == null) {
/*  589 */         formulaData.inputFilters = new Filter[numberOfInputs];
/*      */       }
/*  591 */       if (formulaData.inputDataProviders == null) {
/*  592 */         formulaData.inputDataProviders = new AbstractDataProvider[numberOfInputs];
/*  593 */       } else if (formulaData.inputDataProviders[i] != null) {
/*  594 */         formulaData.inputDataProviders[i].dispose();
/*  595 */         formulaData.inputDataProviders[i] = null;
/*      */       }
/*  597 */       formulaData.inputSides[i] = inputParameterInfo.getOfferSide();
/*  598 */       formulaData.inputPeriods[i] = inputParameterInfo.getPeriod();
/*  599 */       formulaData.inputInstruments[i] = inputParameterInfo.getInstrument();
/*  600 */       formulaData.inputFilters[i] = inputParameterInfo.getFilter();
/*  601 */       OfferSide currentSide = formulaData.inputSides[i] == null ? this.side : this.side == null ? OfferSide.BID : formulaData.inputSides[i];
/*  602 */       Period currentPeriod = formulaData.inputPeriods[i] == null ? this.period : formulaData.inputPeriods[i];
/*  603 */       Instrument currentInstrument = formulaData.inputInstruments[i] == null ? this.instrument : formulaData.inputInstruments[i];
/*  604 */       Filter currentFilter = formulaData.inputFilters[i] == null ? this.filter : formulaData.inputFilters[i];
/*  605 */       if (!this.feedDataProvider.isSubscribedToInstrument(currentInstrument)) {
/*  606 */         throw new RuntimeException(new StringBuilder().append("Instrument ").append(currentInstrument).append(" not subscribed, cannot add indicator").toString());
/*      */       }
/*  608 */       if (currentPeriod.getInterval() >= this.period.getInterval()) {
/*  609 */         if (currentPeriod == Period.TICK) {
/*  610 */           formulaData.inputDataProviders[i] = new TicksDataProvider(currentInstrument, this.maxNumberOfCandles, this.bufferSizeMultiplier, false, currentFilter, this.feedDataProvider);
/*  611 */           formulaData.inputDataProviders[i].start();
/*  612 */           formulaData.inputDataProviders[i].setParentData(this, formulaData);
/*  613 */           formulaData.inputDataProviders[i].setActive(this.active);
/*      */         } else {
/*  615 */           formulaData.inputDataProviders[i] = new CandlesDataProvider(currentInstrument, noFilterPeriod(currentPeriod), currentSide, this.maxNumberOfCandles, this.bufferSizeMultiplier, false, currentFilter, this.feedDataProvider);
/*  616 */           if (dailyFilterPeriod(currentPeriod) != null) {
/*  617 */             formulaData.inputDataProviders[i].dailyFilterPeriod = dailyFilterPeriod(currentPeriod);
/*      */           }
/*  619 */           formulaData.inputDataProviders[i].start();
/*  620 */           formulaData.inputDataProviders[i].setParentData(this, formulaData);
/*  621 */           formulaData.inputDataProviders[i].setActive(this.active);
/*      */         }
/*  623 */         resetData = true;
/*      */       } else {
/*  625 */         formulaData.disabledIndicator = true;
/*      */       }
/*  627 */       label1237: if ((formulaData.inputDataProviders != null) && (formulaData.inputDataProviders[i] != null) && (inputParameterInfo.getOfferSide() != null))
/*      */       {
/*  627 */         OfferSide currentSide;
/*      */         Period currentPeriod;
/*      */         Instrument currentInstrument;
/*      */         Filter currentFilter;
/*  627 */         if (inputParameterInfo.getOfferSide() == (isTicksDataType() ? tickOfferSides[i] : this.side)) if ((inputParameterInfo.getPeriod() != null) && (noFilterPeriod(inputParameterInfo.getPeriod()) == this.period) && (dailyFilterPeriod(inputParameterInfo.getPeriod()) == dailyFilterPeriod(this.dailyFilterPeriod)) && (inputParameterInfo.getInstrument() != null) && (inputParameterInfo.getInstrument() == this.instrument) && (inputParameterInfo.getFilter() != null) && (inputParameterInfo.getFilter() == this.filter))
/*      */           {
/*  632 */             formulaData.inputDataProviders[i].dispose();
/*  633 */             formulaData.inputDataProviders[i] = null; continue;
/*      */           } 
/*      */       }
/*  634 */       if ((formulaData.inputDataProviders == null) || (formulaData.inputDataProviders[i] == null))
/*      */         continue;
/*  636 */       formulaData.inputSides[i] = inputParameterInfo.getOfferSide();
/*  637 */       formulaData.inputPeriods[i] = inputParameterInfo.getPeriod();
/*  638 */       formulaData.inputInstruments[i] = inputParameterInfo.getInstrument();
/*  639 */       formulaData.inputFilters[i] = inputParameterInfo.getFilter();
/*  640 */       currentSide = formulaData.inputSides[i] == null ? this.side : this.side == null ? OfferSide.BID : formulaData.inputSides[i];
/*  641 */       currentPeriod = formulaData.inputPeriods[i] == null ? this.period : formulaData.inputPeriods[i];
/*  642 */       currentInstrument = formulaData.inputInstruments[i] == null ? this.instrument : formulaData.inputInstruments[i];
/*  643 */       currentFilter = formulaData.inputFilters[i] == null ? this.filter : formulaData.inputFilters[i];
/*  644 */       if ((!formulaData.inputDataProviders[i].isTicksDataType()) && (currentSide != formulaData.inputDataProviders[i].getOfferSide())) {
/*  645 */         formulaData.inputDataProviders[i].setOfferSide(currentSide);
/*      */       }
/*  647 */       if ((noFilterPeriod(currentPeriod) != formulaData.inputDataProviders[i].getPeriod()) && (currentPeriod.getInterval() >= this.period.getInterval())) {
/*  648 */         formulaData.inputDataProviders[i].setPeriod(noFilterPeriod(currentPeriod));
/*      */       }
/*  650 */       else if (currentPeriod.getInterval() < this.period.getInterval()) {
/*  651 */         formulaData.disabledIndicator = true;
/*      */       }
/*      */ 
/*  654 */       if (dailyFilterPeriod(currentPeriod) != dailyFilterPeriod(formulaData.inputDataProviders[i].dailyFilterPeriod)) {
/*  655 */         if (dailyFilterPeriod(currentPeriod) != null)
/*  656 */           formulaData.inputDataProviders[i].setDailyFilterPeriod(dailyFilterPeriod(currentPeriod));
/*      */         else {
/*  658 */           formulaData.inputDataProviders[i].setDailyFilterPeriod(Period.DAILY);
/*      */         }
/*      */       }
/*  661 */       if (currentInstrument != formulaData.inputDataProviders[i].getInstrument()) {
/*  662 */         formulaData.inputDataProviders[i].setInstrument(this.instrument);
/*      */       }
/*  664 */       if (currentFilter != formulaData.inputDataProviders[i].getFilter()) {
/*  665 */         formulaData.inputDataProviders[i].setFilter(this.filter);
/*      */       }
/*      */     }
/*      */ 
/*  669 */     return resetData;
/*      */   }
/*      */ 
/*      */   private Period noFilterPeriod(Period period) {
/*  673 */     if (period == null) {
/*  674 */       return null;
/*      */     }
/*  676 */     if ((period == Period.DAILY_SKIP_SUNDAY) || (period == Period.DAILY_SUNDAY_IN_MONDAY)) {
/*  677 */       return Period.DAILY;
/*      */     }
/*  679 */     return period;
/*      */   }
/*      */ 
/*      */   private Period dailyFilterPeriod(Period period)
/*      */   {
/*  685 */     if ((period == Period.DAILY) || (period == Period.DAILY_SKIP_SUNDAY) || (period == Period.DAILY_SUNDAY_IN_MONDAY)) {
/*  686 */       return period;
/*      */     }
/*  688 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized int[] getIndicatorIds()
/*      */   {
/*  694 */     int[] ret = new int[this.formulas.size()];
/*  695 */     int i = 0;
/*  696 */     for (Integer id : this.formulas.keySet()) {
/*  697 */       ret[i] = id.intValue();
/*  698 */       i++;
/*      */     }
/*  700 */     return ret;
/*      */   }
/*      */ 
/*      */   public synchronized void removeIndicator(int id)
/*      */   {
/*  705 */     if (!this.formulas.containsKey(Integer.valueOf(id))) {
/*  706 */       throw new IllegalArgumentException(new StringBuilder().append("Formula with id [").append(id).append("] doesn't exist").toString());
/*      */     }
/*  708 */     IndicatorData indicatorData = (IndicatorData)this.formulas.remove(Integer.valueOf(id));
/*  709 */     if (indicatorData.inputDataProviders != null) {
/*  710 */       for (AbstractDataProvider indicatorDataProvider : indicatorData.inputDataProviders) {
/*  711 */         if (indicatorDataProvider != null) {
/*  712 */           indicatorDataProvider.dispose();
/*      */         }
/*      */       }
/*      */     }
/*  716 */     calculateMinMaxShiftAndSparceFlag();
/*  717 */     fireIndicatorRemoved(id);
/*      */   }
/*      */ 
/*      */   public synchronized void removeIndicators(int[] ids)
/*      */   {
/*  722 */     for (int id : ids) {
/*  723 */       if (!this.formulas.containsKey(Integer.valueOf(id))) {
/*  724 */         throw new IllegalArgumentException(new StringBuilder().append("Formula with id [").append(id).append("] doesn't exist").toString());
/*      */       }
/*      */     }
/*  727 */     for (int id : ids) {
/*  728 */       IndicatorData indicatorData = (IndicatorData)this.formulas.remove(Integer.valueOf(id));
/*  729 */       if (indicatorData.inputDataProviders != null) {
/*  730 */         for (AbstractDataProvider indicatorDataProvider : indicatorData.inputDataProviders) {
/*  731 */           if (indicatorDataProvider != null) {
/*  732 */             indicatorDataProvider.dispose();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  737 */     calculateMinMaxShiftAndSparceFlag();
/*  738 */     fireIndicatorsRemoved(ids);
/*      */   }
/*      */ 
/*      */   public synchronized void removeAllIndicators()
/*      */   {
/*  743 */     for (Iterator iterator = this.formulas.entrySet().iterator(); iterator.hasNext(); ) {
/*  744 */       Map.Entry entry = (Map.Entry)iterator.next();
/*  745 */       int id = ((Integer)entry.getKey()).intValue();
/*  746 */       IndicatorData indicatorData = (IndicatorData)entry.getValue();
/*  747 */       iterator.remove();
/*  748 */       if (indicatorData.inputDataProviders != null) {
/*  749 */         for (AbstractDataProvider indicatorDataProvider : indicatorData.inputDataProviders) {
/*  750 */           if (indicatorDataProvider != null) {
/*  751 */             indicatorDataProvider.dispose();
/*      */           }
/*      */         }
/*      */       }
/*  755 */       fireIndicatorRemoved(id);
/*      */     }
/*  757 */     calculateMinMaxShiftAndSparceFlag();
/*      */   }
/*      */ 
/*      */   protected synchronized boolean sparceIndicatorAttached() {
/*  761 */     for (IndicatorData indicatorData : this.formulas.values()) {
/*  762 */       if (indicatorData.indicatorWrapper.getIndicator().getIndicatorInfo().isSparseIndicator()) {
/*  763 */         return true;
/*      */       }
/*      */     }
/*  766 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean assertionsEnabled() {
/*  770 */     boolean b = false;
/*  771 */     assert ((b = 1) != 0);
/*  772 */     return b;
/*      */   }
/*      */ 
/*      */   protected void fireDataChanged(long from, long to, boolean firstDataChange, boolean sameCandle)
/*      */   {
/*  778 */     assert ((!Thread.holdsLock(this)) || (this.parentDataProvider == null) || ((this.parentDataProvider != null) && (Thread.holdsLock(this)) && (Thread.holdsLock(this.parentDataProvider)))) : "this method should not be called while holding lock on this object, else deadlocks are possible";
/*  779 */     if ((!firstDataChange) && (LOGGER.isDebugEnabled())) {
/*  780 */       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  781 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  782 */       LOGGER.debug(new StringBuilder().append("Data loaded for [").append(this.instrument.toString()).append("] [").append(this.period).append("] [").append(this.side).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").append(isTicksDataType() ? "" : new StringBuilder().append(", expected items count: ").append((to - from) / this.period.getInterval() + 1L).toString()).toString());
/*      */     }
/*      */ 
/*  786 */     if (from > to) {
/*  787 */       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  788 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  789 */       Exception e = new Exception(new StringBuilder().append("from [").append(dateFormat.format(new Date(from))).append("] > to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*  790 */       LOGGER.error(e.getMessage(), e);
/*  791 */       return;
/*      */     }
/*      */ 
/*  794 */     synchronized (this.dataChangeListeners) {
/*  795 */       for (DataChangeListener dataChangeListener : this.dataChangeListeners) {
/*  796 */         dataChangeListener.dataChanged(from, to, this.period, this.side);
/*      */       }
/*      */     }
/*      */ 
/*  800 */     if ((this.parentDataProvider != null) && (this.parentDataProvider.isActive())) {
/*  801 */       synchronized (this.parentDataProvider)
/*      */       {
/*  803 */         if (getInstrument() != this.parentDataProvider.getInstrument()) {
/*  804 */           if (!firstDataChange) {
/*  805 */             this.parentDataProvider.recalculateIndicator(this.parentIndicatorData, false, sameCandle);
/*      */           }
/*      */           else {
/*  808 */             this.parentDataProvider.recalculateIndicator(this.parentIndicatorData, true, sameCandle);
/*      */           }
/*      */         }
/*      */         else {
/*  812 */           this.parentDataProvider.recalculateIndicator(this.parentIndicatorData, false, sameCandle);
/*      */         }
/*      */       }
/*  815 */       this.parentDataProvider.fireDataChanged(from, to, firstDataChange, sameCandle);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void fireLastKnownDataChanged(D data) {
/*  820 */     synchronized (this.dataChangeListeners) {
/*  821 */       for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/*  822 */         dataChangeListener.lastKnownDataChanged(data.clone());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void fireLoadingStarted()
/*      */   {
/*  828 */     if (LOGGER.isTraceEnabled()) {
/*  829 */       LOGGER.trace(new StringBuilder().append("fireLoadingStarted, instrument[").append(this.instrument).append("] period [").append(this.period).append("] offerSide [").append(this.side).append("]").toString());
/*      */     }
/*  831 */     if (!this.loadingStarted) {
/*  832 */       this.loadingStarted = true;
/*      */ 
/*  834 */       DataChangeListener[] listeners = (DataChangeListener[])this.dataChangeListeners.toArray(new DataChangeListener[this.dataChangeListeners.size()]);
/*  835 */       for (DataChangeListener dataChangeListener : listeners) {
/*  836 */         dataChangeListener.loadingStarted(this.period, this.side);
/*      */       }
/*  838 */       if (this.parentDataProvider != null)
/*  839 */         this.parentDataProvider.fireLoadingStarted();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void fireLoadingFinished()
/*      */   {
/*  845 */     if (LOGGER.isTraceEnabled()) {
/*  846 */       LOGGER.trace(new StringBuilder().append("fireLoadingFinished, instrument[").append(this.instrument).append("] period [").append(this.period).append("] offerSide [").append(this.side).append("]").toString());
/*      */     }
/*  848 */     if (this.loadingStarted) {
/*  849 */       for (IndicatorData indicatorData : this.formulas.values()) {
/*  850 */         if (indicatorData.inputDataProviders != null) {
/*  851 */           for (AbstractDataProvider formulaDataProvider : indicatorData.inputDataProviders) {
/*  852 */             if ((formulaDataProvider != null) && 
/*  853 */               (formulaDataProvider.isLoadingStarted()))
/*      */             {
/*  855 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  861 */       this.loadingStarted = false;
/*  862 */       DataChangeListener[] listeners = (DataChangeListener[])this.dataChangeListeners.toArray(new DataChangeListener[this.dataChangeListeners.size()]);
/*  863 */       for (DataChangeListener dataChangeListener : listeners) {
/*  864 */         dataChangeListener.loadingFinished(this.period, this.side);
/*      */       }
/*  866 */       if (this.parentDataProvider != null)
/*  867 */         this.parentDataProvider.fireLoadingFinished();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isLoadingStarted()
/*      */   {
/*  873 */     return this.loadingStarted;
/*      */   }
/*      */ 
/*      */   private void fireIndicatorAdded(int id) {
/*  877 */     DataChangeListener[] listeners = (DataChangeListener[])this.dataChangeListeners.toArray(new DataChangeListener[this.dataChangeListeners.size()]);
/*  878 */     for (DataChangeListener dataChangeListener : listeners)
/*  879 */       dataChangeListener.indicatorAdded(this.period, id);
/*      */   }
/*      */ 
/*      */   private void fireIndicatorChanged(int id)
/*      */   {
/*  884 */     DataChangeListener[] listeners = (DataChangeListener[])this.dataChangeListeners.toArray(new DataChangeListener[this.dataChangeListeners.size()]);
/*  885 */     for (DataChangeListener dataChangeListener : listeners)
/*  886 */       dataChangeListener.indicatorChanged(this.period, id);
/*      */   }
/*      */ 
/*      */   private void fireIndicatorRemoved(int id)
/*      */   {
/*  891 */     DataChangeListener[] listeners = (DataChangeListener[])this.dataChangeListeners.toArray(new DataChangeListener[this.dataChangeListeners.size()]);
/*  892 */     for (DataChangeListener dataChangeListener : listeners)
/*  893 */       dataChangeListener.indicatorRemoved(this.period, id);
/*      */   }
/*      */ 
/*      */   private void fireIndicatorsRemoved(int[] ids)
/*      */   {
/*  898 */     DataChangeListener[] listeners = (DataChangeListener[])this.dataChangeListeners.toArray(new DataChangeListener[this.dataChangeListeners.size()]);
/*  899 */     for (DataChangeListener dataChangeListener : listeners)
/*  900 */       dataChangeListener.indicatorsRemoved(this.period, ids);
/*      */   }
/*      */ 
/*      */   public void addDataChangeListener(DataChangeListener dataChangeListener)
/*      */   {
/*  906 */     synchronized (this.dataChangeListeners) {
/*  907 */       this.dataChangeListeners.add(dataChangeListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeDataChangeListener(DataChangeListener dataChangeListener)
/*      */   {
/*  913 */     synchronized (this.dataChangeListeners) {
/*  914 */       this.dataChangeListeners.remove(dataChangeListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isActive()
/*      */   {
/*  921 */     return this.active;
/*      */   }
/*      */ 
/*      */   public synchronized void setActive(boolean active)
/*      */   {
/*  926 */     this.active = active;
/*  927 */     for (IndicatorData indicatorData : this.formulas.values()) {
/*  928 */       if (indicatorData.inputDataProviders != null) {
/*  929 */         for (AbstractDataProvider indicatorDataProvider : indicatorData.inputDataProviders) {
/*  930 */           if (indicatorDataProvider != null) {
/*  931 */             indicatorDataProvider.setActive(active);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  936 */     recalculateIndicators();
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  941 */     for (IndicatorData indicatorData : this.formulas.values()) {
/*  942 */       if (indicatorData.inputDataProviders != null) {
/*  943 */         for (AbstractDataProvider indicatorDataProvider : indicatorData.inputDataProviders) {
/*  944 */           if (indicatorDataProvider != null) {
/*  945 */             indicatorDataProvider.dispose();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  950 */     if (this.cacheDataUpdatedListener != null) {
/*  951 */       this.feedDataProvider.removeCacheDataUpdatedListener(this.instrument, this.cacheDataUpdatedListener);
/*      */     }
/*      */ 
/*  954 */     synchronized (this.dataChangeListeners) {
/*  955 */       this.dataChangeListeners.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setParentData(AbstractDataProvider<D, S> parentDataProvider, IndicatorData parentIndicatorData) {
/*  960 */     this.parentDataProvider = parentDataProvider;
/*  961 */     this.parentIndicatorData = parentIndicatorData;
/*      */   }
/*      */ 
/*      */   public void setDailyFilterPeriod(Period dailyFilterPeriod)
/*      */   {
/*  966 */     this.dailyFilterPeriod = dailyFilterPeriod;
/*  967 */     setFilter(this.filter);
/*      */   }
/*      */ 
/*      */   public Period getDailyFilterPeriod()
/*      */   {
/*  972 */     return this.dailyFilterPeriod;
/*      */   }
/*      */ 
/*      */   protected final int findStart(long from, int fi, int ei, Data[] buffer)
/*      */   {
/*  977 */     int low = fi;
/*  978 */     int high = ei;
/*      */ 
/*  980 */     while (low <= high) {
/*  981 */       int mid = low + high >>> 1;
/*  982 */       long midVal = buffer[mid].time;
/*      */ 
/*  984 */       if (midVal < from)
/*  985 */         low = mid + 1;
/*  986 */       else if (midVal > from)
/*  987 */         high = mid - 1;
/*      */       else {
/*  989 */         return mid;
/*      */       }
/*      */     }
/*      */ 
/*  993 */     return low;
/*      */   }
/*      */ 
/*      */   protected final boolean calculateInterval(int numberOfCandlesBefore, long time, int numberOfCandlesAfter, int[] intervals, CandleData[] buffer, int lastIndex)
/*      */   {
/* 1005 */     if (lastIndex == -1)
/*      */     {
/* 1007 */       return false;
/*      */     }
/* 1009 */     if (time < buffer[0].time)
/*      */     {
/* 1011 */       return false;
/*      */     }
/*      */ 
/* 1014 */     int timeIndex = findStart(time, 0, lastIndex, buffer);
/*      */ 
/* 1016 */     if (timeIndex <= lastIndex) {
/* 1017 */       if (buffer[timeIndex].time != time)
/*      */       {
/* 1019 */         timeIndex--;
/*      */       }
/* 1021 */       if (this.sparceIndicator)
/*      */       {
/* 1023 */         intervals[0] = 0;
/* 1024 */         intervals[1] = lastIndex;
/* 1025 */         if (timeIndex + 1 > numberOfCandlesBefore)
/*      */         {
/* 1027 */           intervals[2] = (timeIndex + 1 - numberOfCandlesBefore);
/*      */         }
/* 1029 */         else intervals[2] = 0;
/*      */ 
/* 1031 */         if (timeIndex + numberOfCandlesAfter > lastIndex)
/* 1032 */           intervals[3] = 0;
/*      */         else
/* 1034 */           intervals[3] = (lastIndex - (timeIndex + numberOfCandlesAfter));
/*      */       }
/*      */       else
/*      */       {
/* 1038 */         if (timeIndex + 1 > numberOfCandlesBefore + this.formulasMaxShift + 1)
/*      */         {
/* 1041 */           intervals[0] = (timeIndex + 1 - (numberOfCandlesBefore + this.formulasMaxShift + 1));
/*      */ 
/* 1043 */           intervals[2] = (this.formulasMaxShift + 1);
/*      */         }
/*      */         else {
/* 1046 */           intervals[0] = 0;
/*      */ 
/* 1048 */           intervals[2] = (timeIndex + 1 > numberOfCandlesBefore ? timeIndex + 1 - numberOfCandlesBefore : 0);
/*      */         }
/* 1050 */         if (timeIndex + numberOfCandlesAfter + -this.formulasMinShift + 1 > lastIndex)
/*      */         {
/* 1053 */           intervals[1] = lastIndex;
/*      */ 
/* 1055 */           intervals[3] = (lastIndex - timeIndex > numberOfCandlesAfter ? lastIndex - timeIndex - numberOfCandlesAfter : 0);
/*      */         } else {
/* 1057 */           intervals[1] = (timeIndex + numberOfCandlesAfter + -this.formulasMinShift + 1);
/* 1058 */           intervals[3] = (-this.formulasMinShift + 1);
/*      */         }
/*      */       }
/* 1061 */       return true;
/*      */     }
/*      */ 
/* 1064 */     return false;
/*      */   }
/*      */ 
/*      */   protected final CandleData[] putDataInListFromToIndexes(int from, int to, CandleData[] buffer)
/*      */   {
/* 1069 */     CandleData[] data = new CandleData[to - from + 1];
/* 1070 */     System.arraycopy(buffer, from, data, 0, to - from + 1);
/* 1071 */     return data;
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators(int from, int to, Collection<IndicatorData> formulasToRecalculate, int lastIndex, CandleData[] bufferAsk, CandleData[] bufferBid)
/*      */   {
/* 1077 */     if (this.indicators == null) {
/* 1078 */       this.indicators = new Indicators(new History(OrdersProvider.getInstance(), this.instrument.getPrimaryCurrency()));
/*      */     }
/* 1080 */     if (this.indicatorRecalculationThread == null) {
/* 1081 */       this.indicatorRecalculationThread = new IndicatorRecalculationThread();
/* 1082 */       this.indicatorRecalculationThread.start();
/* 1083 */       addDataChangeListener(this.indicatorRecalculationThread);
/*      */     }
/*      */ 
/* 1086 */     this.indicatorRecalculationThread.addTask(new IndicatorRecalculationContext(this.instrument, this.period, this.side, from, to, formulasToRecalculate, lastIndex, bufferAsk, bufferBid, isTicksDataType(), this.maxNumberOfCandles, this.bufferSizeMultiplier, this.firstData));
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators_save(int from, int to, Collection<IndicatorData> formulasToRecalculate, int lastIndex, CandleData[] bufferAsk, CandleData[] bufferBid)
/*      */   {
/* 1103 */     if ((this.firstData == null) || (lastIndex == -1))
/*      */     {
/* 1105 */       return;
/*      */     }
/*      */ 
/* 1109 */     int finalLookback = 0;
/* 1110 */     int finalLookforward = 0;
/* 1111 */     boolean needAsk = false;
/* 1112 */     boolean needBid = false;
/*      */ 
/* 1114 */     for (IndicatorData formulaData : formulasToRecalculate) {
/* 1115 */       if (formulaData.disabledIndicator) {
/*      */         continue;
/*      */       }
/* 1118 */       IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/* 1119 */       int lookback = formulaData.lookback;
/* 1120 */       int lookforward = formulaData.lookforward;
/* 1121 */       if (indicator.getIndicatorInfo().isUnstablePeriod())
/*      */       {
/* 1123 */         lookback += (lookback * 4 < 100 ? 100 : lookback * 4);
/*      */       }
/* 1125 */       if (finalLookback < lookback) {
/* 1126 */         finalLookback = lookback;
/*      */       }
/* 1128 */       if (finalLookforward < lookforward) {
/* 1129 */         finalLookforward = lookforward;
/*      */       }
/*      */ 
/* 1132 */       if ((isTicksDataType()) && ((!needBid) || (!needAsk))) {
/* 1133 */         int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfInputs(); i < j; i++) {
/* 1134 */           OfferSide side = formulaData.indicatorWrapper.getOfferSidesForTicks()[i];
/* 1135 */           if (side == OfferSide.ASK)
/* 1136 */             needAsk = true;
/*      */           else {
/* 1138 */             needBid = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1144 */     if (to != lastIndex)
/*      */     {
/* 1146 */       to = lastIndex - to > finalLookback ? to + finalLookback : lastIndex;
/*      */     }
/* 1148 */     if ((finalLookforward != 0) && (from != 0))
/*      */     {
/* 1150 */       from = from > finalLookforward ? from - finalLookforward : 0;
/*      */     }
/*      */ 
/* 1153 */     int recalculateStart = from > finalLookback ? from - finalLookback : 0;
/* 1154 */     int recalculateEnd = lastIndex - to > finalLookforward ? to + finalLookforward : lastIndex;
/* 1155 */     CandleData[] timeDataAsk = null;
/* 1156 */     CandleData[] timeDataBid = null;
/* 1157 */     if (isTicksDataType()) {
/* 1158 */       if (needAsk) {
/* 1159 */         timeDataAsk = putDataInListFromToIndexes(recalculateStart, recalculateEnd, bufferAsk);
/*      */       }
/* 1161 */       if (needBid)
/* 1162 */         timeDataBid = putDataInListFromToIndexes(recalculateStart, recalculateEnd, bufferBid);
/*      */     }
/*      */     else {
/* 1165 */       timeDataAsk = putDataInListFromToIndexes(recalculateStart, recalculateEnd, bufferAsk);
/*      */     }
/*      */     long lastInputTime;
/*      */     long lastInputTime;
/* 1169 */     if (timeDataAsk != null)
/* 1170 */       lastInputTime = timeDataAsk[(timeDataAsk.length - 1)].time;
/*      */     else {
/* 1172 */       lastInputTime = -1L;
/*      */     }
/* 1174 */     if (timeDataBid != null) {
/* 1175 */       long time = timeDataBid[(timeDataBid.length - 1)].time;
/* 1176 */       if (time > lastInputTime) {
/* 1177 */         lastInputTime = time;
/*      */       }
/*      */     }
/*      */ 
/* 1181 */     double[][][] doubleInputs = new double[OfferSide.values().length][IIndicators.AppliedPrice.values().length];
/* 1182 */     double[][][] priceInput = calculateIndicatorsInputs(formulasToRecalculate, timeDataAsk, timeDataBid, doubleInputs);
/*      */ 
/* 1185 */     for (IndicatorData formulaData : formulasToRecalculate) {
/* 1186 */       if (formulaData.disabledIndicator) {
/*      */         continue;
/*      */       }
/* 1189 */       IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/* 1190 */       IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 1191 */       OfferSide[] tickOfferSides = formulaData.indicatorWrapper.getOfferSidesForTicks();
/* 1192 */       IIndicators.AppliedPrice[] appliedPrices = formulaData.indicatorWrapper.getAppliedPricesForCandles();
/* 1193 */       if ((timeDataAsk == null) && (timeDataBid == null))
/*      */       {
/*      */         continue;
/*      */       }
/* 1197 */       IndicatorContext indicatorContext = formulaData.indicatorWrapper.getIndicatorHolder().getIndicatorContext();
/*      */ 
/* 1199 */       IFeedDescriptor feedDescriptor = getFeedDescriptor();
/* 1200 */       feedDescriptor.setOfferSide(indicatorInfo.getNumberOfInputs() > 0 ? this.side : isTicksDataType() ? tickOfferSides[0] : OfferSide.BID);
/* 1201 */       indicatorContext.setFeedDescriptor(feedDescriptor);
/*      */       try
/*      */       {
/* 1204 */         int dataSize = timeDataAsk == null ? timeDataBid.length : timeDataAsk.length;
/*      */ 
/* 1206 */         int i = 0; for (int j = indicatorInfo.getNumberOfInputs(); ; i++) { if (i >= j) break label1323; InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/* 1208 */           if ((inputParameterInfo.getOfferSide() != null) || (inputParameterInfo.getPeriod() != null) || (inputParameterInfo.getInstrument() != null)) { if (inputParameterInfo.getOfferSide() != null) { if (inputParameterInfo.getOfferSide() != (isTicksDataType() ? tickOfferSides[i] : this.side)); } else if (((inputParameterInfo.getPeriod() != null) && ((noFilterPeriod(inputParameterInfo.getPeriod()) != this.period) || (dailyFilterPeriod(inputParameterInfo.getPeriod()) != dailyFilterPeriod(this.dailyFilterPeriod)))) || ((inputParameterInfo.getInstrument() != null) && (inputParameterInfo.getInstrument() != this.instrument)));
/*      */           } else {
/* 1212 */             switch (1.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[inputParameterInfo.getType().ordinal()]) {
/*      */             case 1:
/*      */               try {
/* 1215 */                 indicator.setInputParameter(i, !isTicksDataType() ? priceInput[0] : priceInput[tickOfferSides[i].ordinal()]);
/*      */               } catch (Throwable t) {
/* 1217 */                 LOGGER.error(t.getMessage(), t);
/* 1218 */                 String error = StrategyWrapper.representError(indicator, t);
/* 1219 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */               }
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); break;
/*      */             case 2:
/*      */               try
/*      */               {
/* 1225 */                 indicator.setInputParameter(i, !isTicksDataType() ? doubleInputs[0][appliedPrices[i].ordinal()] : doubleInputs[tickOfferSides[i].ordinal()][appliedPrices[i].ordinal()]);
/*      */               } catch (Throwable t) {
/* 1227 */                 LOGGER.error(t.getMessage(), t);
/* 1228 */                 String error = StrategyWrapper.representError(indicator, t);
/* 1229 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */               }
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); break;
/*      */             case 3:
/*      */               try
/*      */               {
/* 1235 */                 indicator.setInputParameter(i, (!isTicksDataType()) || (tickOfferSides[i] == OfferSide.ASK) ? timeDataAsk : timeDataBid);
/*      */               } catch (Throwable t) {
/* 1237 */                 LOGGER.error(t.getMessage(), t);
/* 1238 */                 String error = StrategyWrapper.representError(indicator, t);
/* 1239 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */               }
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); break;
/*      */             default:
/* 1242 */               break;
/*      */             }
/*      */           }
/* 1245 */           if (!setIndicatorInputFromDataProvider(formulaData, i, bufferAsk[from].time, bufferAsk[to].time, finalLookback, finalLookforward)) {
/* 1246 */             int k = 0; for (int n = indicatorInfo.getNumberOfOutputs(); k < n; k++) {
/* 1247 */               OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(k);
/* 1248 */               switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */               case 1:
/* 1250 */                 Arrays.fill(formulaData.outputDataInt[k], from, to + 1, -2147483648);
/* 1251 */                 break;
/*      */               case 2:
/* 1253 */                 Arrays.fill(formulaData.outputDataDouble[k], from, to + 1, (0.0D / 0.0D));
/* 1254 */                 break;
/*      */               case 3:
/* 1256 */                 Arrays.fill(formulaData.outputDataObject[k], from, to + 1, null);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1431 */             indicatorContext.setFeedDescriptor(null); break;
/*      */           }
/*      */         }
/* 1266 */         Object[] outArrays = new Object[indicatorInfo.getNumberOfOutputs()];
/*      */         IndicatorResult result;
/*      */         IndicatorResult result;
/* 1268 */         if (dataSize <= formulaData.lookback + formulaData.lookforward) {
/* 1269 */           result = new IndicatorResult(0, 0, 0);
/*      */         }
/*      */         else {
/* 1272 */           int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); ; i++) { if (i >= j) break label1760; OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(i);
/* 1274 */             switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */             case 1:
/* 1276 */               int[] arrayInt = new int[dataSize - (formulaData.lookback + formulaData.lookforward)];
/*      */               try {
/* 1278 */                 indicator.setOutputParameter(i, arrayInt);
/*      */               } catch (Throwable t) {
/* 1280 */                 LOGGER.error(t.getMessage(), t);
/* 1281 */                 String error = StrategyWrapper.representError(indicator, t);
/* 1282 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */               }
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); break;
/*      */ 
/* 1285 */               outArrays[i] = arrayInt;
/* 1286 */               break;
/*      */             case 2:
/* 1288 */               double[] arrayDouble = new double[dataSize - (formulaData.lookback + formulaData.lookforward)];
/*      */               try {
/* 1290 */                 indicator.setOutputParameter(i, arrayDouble);
/*      */               } catch (Throwable t) {
/* 1292 */                 LOGGER.error(t.getMessage(), t);
/* 1293 */                 String error = StrategyWrapper.representError(indicator, t);
/* 1294 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */               }
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); break;
/*      */ 
/* 1297 */               outArrays[i] = arrayDouble;
/* 1298 */               break;
/*      */             case 3:
/* 1300 */               Object[] arrayObject = new Object[dataSize - (formulaData.lookback + formulaData.lookforward)];
/*      */               try {
/* 1302 */                 indicator.setOutputParameter(i, arrayObject);
/*      */               } catch (Throwable t) {
/* 1304 */                 LOGGER.error(t.getMessage(), t);
/* 1305 */                 String error = StrategyWrapper.representError(indicator, t);
/* 1306 */                 NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */               }
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); break;
/*      */ 
/* 1309 */               outArrays[i] = arrayObject;
/*      */             }
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1315 */             result = indicator.calculate(0, dataSize - 1);
/*      */           } catch (TaLibException e) {
/* 1317 */             Throwable t = e.getCause();
/* 1318 */             LOGGER.error(t.getMessage(), t);
/* 1319 */             String error = StrategyWrapper.representError(indicator, t);
/* 1320 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */ 
/* 1431 */             indicatorContext.setFeedDescriptor(null); continue;
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/* 1323 */             LOGGER.error(t.getMessage(), t);
/* 1324 */             String error = StrategyWrapper.representError(indicator, t);
/* 1325 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), t, true);
/*      */           }
/*      */ 
/* 1431 */           indicatorContext.setFeedDescriptor(null); continue;
/*      */ 
/* 1328 */           if (result.getNumberOfElements() < dataSize - formulaData.lookback - formulaData.lookforward) {
/* 1329 */             String error = new StringBuilder().append("calculate() method of indicator [").append(indicatorInfo.getName()).append("] returned less values than expected. Requested from-to [0]-[").append(dataSize - 1).append("], input array size [").append(dataSize).append("], returned first calculated index [").append(result.getFirstValueIndex()).append("], number of calculated values [").append(result.getNumberOfElements()).append("], lookback [").append(formulaData.lookback).append("], lookforward [").append(formulaData.lookforward).append("], expected number of elements is [").append(dataSize - formulaData.lookback - formulaData.lookforward).append("]").toString();
/*      */ 
/* 1335 */             LOGGER.error(error);
/* 1336 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), true);
/*      */ 
/* 1431 */             indicatorContext.setFeedDescriptor(null); continue;
/*      */           }
/* 1339 */           if (result.getFirstValueIndex() + result.getNumberOfElements() > dataSize) {
/* 1340 */             String error = new StringBuilder().append("calculate() method of indicator [").append(indicatorInfo.getName()).append("] returned incorrect values. Requested from-to [0]-[").append(dataSize - 1).append("], input array size [").append(dataSize).append("], returned first calculated index [").append(result.getFirstValueIndex()).append("], number of calculated values [").append(result.getNumberOfElements()).append("], lookback [").append(formulaData.lookback).append("], lookforward [").append(formulaData.lookforward).append("], first index + number of elements cannot be > input array size").toString();
/*      */ 
/* 1346 */             LOGGER.error(error);
/* 1347 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), true);
/*      */ 
/* 1431 */             indicatorContext.setFeedDescriptor(null); continue;
/*      */           }
/* 1350 */           if ((result.getLastValueIndex() == -2147483648) && (result.getNumberOfElements() != 0)) {
/* 1351 */             if (formulaData.lookforward != 0) {
/* 1352 */               String error = new StringBuilder().append("calculate() method of indicator [").append(indicatorInfo.getName()).append("] returned result without lastValueIndex set. This is only allowed when lookforward is equals to zero").toString();
/*      */ 
/* 1354 */               LOGGER.error(error);
/* 1355 */               NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), true);
/*      */ 
/* 1431 */               indicatorContext.setFeedDescriptor(null); continue;
/*      */             }
/* 1358 */             result.setLastValueIndex(dataSize - 1);
/*      */           }
/*      */ 
/* 1361 */           if (result.getLastValueIndex() + 1 - result.getFirstValueIndex() < dataSize - formulaData.lookback - formulaData.lookforward) {
/* 1362 */             String error = new StringBuilder().append("calculate() method of indicator [").append(indicatorInfo.getName()).append("] returned incorrect first value and last value indexes. Requested from-to [0]-[").append(dataSize - 1).append("], input array size [").append(dataSize).append("], returned first calculated index [").append(result.getFirstValueIndex()).append("], number of calculated values [").append(result.getNumberOfElements()).append("], last calculated index (set to max index by default) [").append(result.getLastValueIndex()).append("], lookback [").append(formulaData.lookback).append("], lookforward [").append(formulaData.lookforward).append("]").toString();
/*      */ 
/* 1368 */             LOGGER.error(error);
/* 1369 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("Error in indicator: ").append(error).toString(), true);
/*      */ 
/* 1431 */             indicatorContext.setFeedDescriptor(null); continue;
/*      */           }
/*      */         }
/* 1373 */         int firstValueIndex = result.getFirstValueIndex();
/* 1374 */         int numberOfElements = result.getNumberOfElements();
/*      */ 
/* 1378 */         if ((indicator instanceof ConnectorIndicator)) {
/* 1379 */           firstValueIndex = result.getFirstValueIndex() < 0 ? 0 : result.getFirstValueIndex();
/* 1380 */           numberOfElements = result.getNumberOfElements() < result.getLastValueIndex() - firstValueIndex ? result.getNumberOfElements() : result.getLastValueIndex() - firstValueIndex;
/*      */         }
/*      */ 
/* 1384 */         if ((lastInputTime > 0L) && (formulaData.lastTime != lastInputTime)) {
/* 1385 */           IndicatorData.access$302(formulaData, lastInputTime);
/* 1386 */           formulaData.lastValues = new Object[indicatorInfo.getNumberOfOutputs()];
/*      */         }
/*      */ 
/* 1389 */         int i = 0; for (int j = indicatorInfo.getNumberOfOutputs(); i < j; i++) {
/* 1390 */           OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(i);
/* 1391 */           switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */           case 1:
/* 1393 */             if (numberOfElements == 0)
/* 1394 */               Arrays.fill(formulaData.outputDataInt[i], from, to + 1, -2147483648);
/*      */             else {
/* 1396 */               copyToIndicatorOutput(from, to, recalculateStart, formulaData.outputDataInt[i], firstValueIndex, numberOfElements, outArrays[i], outputParameterInfo.getType(), lastIndex, bufferAsk.length);
/*      */             }
/*      */ 
/* 1400 */             if (formulaData.lastTime != lastInputTime) continue;
/* 1401 */             formulaData.lastValues[i] = Integer.valueOf(IndicatorData.access$500(formulaData)[i][lastIndex]); break;
/*      */           case 2:
/* 1405 */             if (numberOfElements == 0)
/* 1406 */               Arrays.fill(formulaData.outputDataDouble[i], from, to + 1, (0.0D / 0.0D));
/*      */             else {
/* 1408 */               copyToIndicatorOutput(from, to, recalculateStart, formulaData.outputDataDouble[i], firstValueIndex, numberOfElements, outArrays[i], outputParameterInfo.getType(), lastIndex, bufferAsk.length);
/*      */             }
/*      */ 
/* 1412 */             if (formulaData.lastTime != lastInputTime) continue;
/* 1413 */             formulaData.lastValues[i] = Double.valueOf(IndicatorData.access$400(formulaData)[i][lastIndex]); break;
/*      */           case 3:
/* 1417 */             if (numberOfElements == 0)
/* 1418 */               Arrays.fill(formulaData.outputDataObject[i], from, to + 1, null);
/*      */             else {
/* 1420 */               copyToIndicatorOutput(from, to, recalculateStart, formulaData.outputDataObject[i], firstValueIndex, numberOfElements, outArrays[i], outputParameterInfo.getType(), lastIndex, bufferAsk.length);
/*      */             }
/*      */ 
/* 1424 */             if (formulaData.lastTime != lastInputTime) continue;
/* 1425 */             formulaData.lastValues[i] = IndicatorData.access$600(formulaData)[i][lastIndex];
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1431 */         indicatorContext.setFeedDescriptor(null);
/*      */       }
/*      */     }
/* 1434 */     label1323: label1760: return;
/*      */   }
/*      */   protected abstract IFeedDescriptor getFeedDescriptor();
/*      */ 
/* 1439 */   private boolean setIndicatorInputFromDataProvider(IndicatorData indicatorData, int inputIndex, long from, long to, int finalLookback, int finalLookforward) { IIndicator indicator = indicatorData.indicatorWrapper.getIndicator();
/* 1440 */     assert (indicatorData.inputDataProviders[inputIndex] != null) : "Input data provider is null";
/* 1441 */     Period inputPeriod = indicatorData.inputPeriods[inputIndex] == null ? this.period : indicatorData.inputPeriods[inputIndex];
/* 1442 */     Period inputCandlePeriod = inputPeriod == Period.TICK ? Period.ONE_SEC : inputPeriod;
/* 1443 */     long latestTo = to = DataCacheUtils.getCandleStartFast(inputCandlePeriod, to);
/* 1444 */     from = DataCacheUtils.getCandleStartFast(inputCandlePeriod, from);
/*      */ 
/* 1446 */     long latestDataTime = indicatorData.inputDataProviders[inputIndex].getLastLoadedDataTime();
/* 1447 */     if (latestDataTime == -9223372036854775808L) {
/* 1448 */       LOGGER.debug("WARN: Indicator data provider doesn't have any data");
/* 1449 */       return false;
/*      */     }
/* 1451 */     if (to > latestDataTime) {
/* 1452 */       to = DataCacheUtils.getCandleStartFast(inputCandlePeriod, latestDataTime);
/*      */     }
/* 1454 */     if (from > to) {
/* 1455 */       return false;
/*      */     }
/* 1457 */     int candlesBefore = DataCacheUtils.getCandlesCountBetweenFast(inputCandlePeriod, from, to) + finalLookback;
/* 1458 */     if (candlesBefore == 0) {
/* 1459 */       LOGGER.debug("WARN: Nothing to request from indicator data provider");
/* 1460 */       return false;
/*      */     }
/* 1462 */     if (candlesBefore + finalLookforward > this.maxNumberOfCandles * this.bufferSizeMultiplier) {
/* 1463 */       candlesBefore = this.maxNumberOfCandles * this.bufferSizeMultiplier - finalLookforward;
/*      */     }
/* 1465 */     IDataSequence dataSequence = indicatorData.inputDataProviders[inputIndex].getDataSequence(candlesBefore, to, finalLookforward);
/*      */     CandleData[] data;
/* 1467 */     if (inputPeriod == Period.TICK) {
/* 1468 */       TickDataSequence tickSequence = (TickDataSequence)dataSequence;
/* 1469 */       OfferSide offerSide = indicator.getInputParameterInfo(inputIndex).getOfferSide();
/* 1470 */       OfferSide[] tickOfferSides = indicatorData.indicatorWrapper.getOfferSidesForTicks();
/*      */       CandleData[] data;
/*      */       CandleData[] data;
/* 1471 */       if (((offerSide != null) && (offerSide == OfferSide.ASK)) || ((offerSide == null) && (tickOfferSides[inputIndex] == OfferSide.ASK)))
/* 1472 */         data = tickSequence.getOneSecCandlesAsk();
/*      */       else {
/* 1474 */         data = tickSequence.getOneSecCandlesBid();
/*      */       }
/* 1476 */       if ((tickSequence.getOneSecExtraBefore() > 0) || (tickSequence.getOneSecExtraAfter() > 0)) {
/* 1477 */         CandleData[] newData = new CandleData[data.length - tickSequence.getOneSecExtraBefore() - tickSequence.getOneSecExtraAfter()];
/* 1478 */         System.arraycopy(data, tickSequence.getOneSecExtraBefore(), newData, 0, newData.length);
/* 1479 */         data = newData;
/*      */       }
/*      */     } else {
/* 1482 */       CandleDataSequence candleSequence = (CandleDataSequence)dataSequence;
/* 1483 */       data = (CandleData[])candleSequence.getData();
/* 1484 */       if ((dataSequence.getExtraBefore() > 0) || (dataSequence.getExtraAfter() > 0)) {
/* 1485 */         CandleData[] newData = new CandleData[data.length - dataSequence.getExtraBefore() - dataSequence.getExtraAfter()];
/* 1486 */         System.arraycopy(data, dataSequence.getExtraBefore(), newData, 0, newData.length);
/* 1487 */         data = newData;
/*      */       }
/*      */     }
/* 1490 */     if (data.length == 0) {
/* 1491 */       indicator.setInputParameter(inputIndex, getIndicatorInputData(indicatorData.indicatorWrapper.getIndicator().getInputParameterInfo(inputIndex), indicatorData.indicatorWrapper.getAppliedPricesForCandles()[inputIndex], new CandleData[0]));
/*      */ 
/* 1493 */       return true;
/*      */     }
/* 1495 */     int flats = 0;
/* 1496 */     if (latestTo > to)
/* 1497 */       flats = DataCacheUtils.getCandlesCountBetweenFast(inputCandlePeriod, to, latestTo) - 1;
/*      */     CandleData[] correctData;
/* 1500 */     if (flats > 0) {
/* 1501 */       CandleData[] correctData = new CandleData[data.length + flats];
/* 1502 */       System.arraycopy(data, 0, correctData, 0, data.length);
/*      */ 
/* 1505 */       CandleData lastCandle = correctData[(data.length - 1)];
/* 1506 */       int i = 1; for (long time = DataCacheUtils.getNextCandleStartFast(inputCandlePeriod, lastCandle.time); i <= flats; time = DataCacheUtils.getNextCandleStartFast(inputCandlePeriod, time)) {
/* 1507 */         correctData[(data.length - 1 + i)] = new CandleData(time, lastCandle.close, lastCandle.close, lastCandle.close, lastCandle.close, 0.0D);
/*      */ 
/* 1506 */         i++;
/*      */       }
/*      */     }
/*      */     else {
/* 1510 */       correctData = data;
/*      */     }
/*      */ 
/* 1513 */     indicator.setInputParameter(inputIndex, getIndicatorInputData(indicatorData.indicatorWrapper.getIndicator().getInputParameterInfo(inputIndex), indicatorData.indicatorWrapper.getAppliedPricesForCandles()[inputIndex], correctData));
/*      */ 
/* 1515 */     return true;
/*      */   }
/*      */ 
/*      */   private double[][][] calculateIndicatorsInputs(Collection<IndicatorData> formulasToRecalculate, CandleData[] timeDataAsk, CandleData[] timeDataBid, double[][][] doubleInputs)
/*      */   {
/* 1521 */     double[][][] priceInput = (double[][][])null;
/* 1522 */     for (IndicatorData formulaData : formulasToRecalculate) {
/* 1523 */       if (formulaData.disabledIndicator) {
/*      */         continue;
/*      */       }
/* 1526 */       IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/* 1527 */       OfferSide[] tickOfferSides = formulaData.indicatorWrapper.getOfferSidesForTicks();
/* 1528 */       IIndicators.AppliedPrice[] appliedPrices = formulaData.indicatorWrapper.getAppliedPricesForCandles();
/* 1529 */       if ((timeDataAsk == null) && (timeDataBid == null))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/* 1536 */       if ((indicator instanceof ConnectorIndicator)) {
/* 1537 */         ((ConnectorIndicator)indicator).setCurrentInstrument(this.instrument);
/* 1538 */         ((ConnectorIndicator)indicator).setCurrentPeriod(this.period);
/*      */       }
/*      */ 
/* 1541 */       int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfInputs(); i < j; i++) {
/* 1542 */         InputParameterInfo inputParameterInfo = indicator.getInputParameterInfo(i);
/* 1543 */         if ((inputParameterInfo.getOfferSide() != null) || (inputParameterInfo.getPeriod() != null) || (inputParameterInfo.getInstrument() != null)) { if (inputParameterInfo.getOfferSide() != null) if (inputParameterInfo.getOfferSide() != (isTicksDataType() ? tickOfferSides[i] : this.side)) continue; if (((inputParameterInfo.getPeriod() != null) && ((noFilterPeriod(inputParameterInfo.getPeriod()) != this.period) || (dailyFilterPeriod(inputParameterInfo.getPeriod()) != dailyFilterPeriod(this.dailyFilterPeriod)))) || ((inputParameterInfo.getInstrument() != null) && (inputParameterInfo.getInstrument() != this.instrument))) {
/*      */             continue;
/*      */           }
/*      */         }
/* 1547 */         switch (inputParameterInfo.getType()) {
/*      */         case PRICE:
/* 1549 */           if ((priceInput != null) && ((!isTicksDataType()) || (priceInput[tickOfferSides[i].ordinal()] != null))) continue;
/* 1550 */           if (priceInput == null) {
/* 1551 */             priceInput = new double[2][][];
/*      */           }
/* 1553 */           priceInput[(!isTicksDataType() ? 0 : tickOfferSides[i].ordinal())] = ((double[][])(double[][])getIndicatorInputData(inputParameterInfo, appliedPrices[i], (!isTicksDataType()) || (tickOfferSides[i] == OfferSide.ASK) ? timeDataAsk : timeDataBid)); break;
/*      */         case DOUBLE:
/* 1557 */           if (doubleInputs[tickOfferSides[i].ordinal()][appliedPrices[i].ordinal()] != null) continue;
/* 1558 */           doubleInputs[tickOfferSides[i].ordinal()][appliedPrices[i].ordinal()] = ((double[])(double[])getIndicatorInputData(inputParameterInfo, appliedPrices[i], (!isTicksDataType()) || (tickOfferSides[i] == OfferSide.ASK) ? timeDataAsk : timeDataBid));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1565 */     return priceInput;
/*      */   }
/*      */ 
/*      */   private Object getIndicatorInputData(InputParameterInfo inputParameterInfo, IIndicators.AppliedPrice appliedPrice, CandleData[] timeData) {
/* 1569 */     int dataSize = timeData.length;
/*      */     int k;
/* 1570 */     switch (1.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[inputParameterInfo.getType().ordinal()]) {
/*      */     case 1:
/* 1572 */       double[] open = new double[dataSize];
/* 1573 */       double[] high = new double[dataSize];
/* 1574 */       double[] low = new double[dataSize];
/* 1575 */       double[] close = new double[dataSize];
/* 1576 */       double[] volume = new double[dataSize];
/* 1577 */       k = 0;
/* 1578 */       for (CandleData candle : timeData) {
/* 1579 */         open[k] = candle.open;
/* 1580 */         high[k] = candle.high;
/* 1581 */         low[k] = candle.low;
/* 1582 */         close[k] = candle.close;
/* 1583 */         volume[k] = candle.vol;
/* 1584 */         k++;
/*      */       }
/* 1586 */       return new double[][] { open, close, high, low, volume };
/*      */     case 2:
/* 1588 */       double[] data = new double[dataSize];
/* 1589 */       switch (1.$SwitchMap$com$dukascopy$api$IIndicators$AppliedPrice[appliedPrice.ordinal()]) {
/*      */       case 1:
/* 1591 */         k = 0;
/* 1592 */         for (CandleData candle : timeData) {
/* 1593 */           data[k] = candle.close;
/* 1594 */           k++;
/*      */         }
/* 1596 */         break;
/*      */       case 2:
/* 1598 */         k = 0;
/* 1599 */         for (CandleData candle : timeData) {
/* 1600 */           data[k] = candle.high;
/* 1601 */           k++;
/*      */         }
/* 1603 */         break;
/*      */       case 3:
/* 1605 */         k = 0;
/* 1606 */         for (CandleData candle : timeData) {
/* 1607 */           data[k] = candle.low;
/* 1608 */           k++;
/*      */         }
/* 1610 */         break;
/*      */       case 4:
/* 1612 */         k = 0;
/* 1613 */         for (CandleData candle : timeData) {
/* 1614 */           data[k] = candle.open;
/* 1615 */           k++;
/*      */         }
/* 1617 */         break;
/*      */       case 5:
/* 1619 */         k = 0;
/* 1620 */         for (CandleData candle : timeData) {
/* 1621 */           data[k] = ((candle.high + candle.low) / 2.0D);
/* 1622 */           k++;
/*      */         }
/* 1624 */         break;
/*      */       case 6:
/* 1626 */         k = 0;
/* 1627 */         for (CandleData candle : timeData) {
/* 1628 */           data[k] = ((candle.high + candle.low + candle.close) / 3.0D);
/* 1629 */           k++;
/*      */         }
/* 1631 */         break;
/*      */       case 7:
/* 1633 */         k = 0;
/* 1634 */         for (CandleData candle : timeData) {
/* 1635 */           data[k] = ((candle.high + candle.low + candle.close + candle.close) / 4.0D);
/* 1636 */           k++;
/*      */         }
/* 1638 */         break;
/*      */       case 8:
/* 1640 */         k = 0;
/* 1641 */         for (CandleData candle : timeData) {
/* 1642 */           data[k] = candle.time;
/* 1643 */           k++;
/*      */         }
/* 1645 */         break;
/*      */       case 9:
/* 1647 */         k = 0;
/* 1648 */         for (CandleData candle : timeData) {
/* 1649 */           data[k] = candle.vol;
/* 1650 */           k++;
/*      */         }
/*      */       }
/*      */ 
/* 1654 */       return data;
/*      */     case 3:
/* 1656 */       return timeData;
/*      */     }
/* 1658 */     if (!$assertionsDisabled) throw new AssertionError("shouldn't be here");
/* 1659 */     return null;
/*      */   }
/*      */ 
/*      */   protected void copyToIndicatorOutput(int from, int to, int recalculateStart, Object outputData, int firstValueIndex, int numberOfElements, Object inputData, OutputParameterInfo.Type type, int lastIndex, int bufferLength)
/*      */   {
/*      */     int toSkip;
/*      */     int toSkip;
/* 1667 */     if (recalculateStart <= from)
/* 1668 */       toSkip = from - recalculateStart;
/*      */     else {
/* 1670 */       toSkip = bufferLength - recalculateStart + from;
/*      */     }
/*      */ 
/* 1674 */     int valuesNA = firstValueIndex - toSkip;
/*      */     int outputBufferCounter;
/*      */     int inputBufferCounter;
/* 1677 */     if (valuesNA > 0) {
/* 1678 */       int inputBufferCounter = 0;
/*      */       int outputBufferCounter;
/* 1679 */       if (lastIndex - from + 1 >= valuesNA)
/* 1680 */         outputBufferCounter = from + valuesNA;
/*      */       else
/* 1682 */         throw new RuntimeException("Cannot happen");
/*      */       int outputBufferCounter;
/* 1684 */       switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[type.ordinal()]) {
/*      */       case 1:
/* 1686 */         int[] outputDataInt = (int[])(int[])outputData;
/* 1687 */         Arrays.fill(outputDataInt, from, outputBufferCounter, -2147483648);
/* 1688 */         break;
/*      */       case 2:
/* 1690 */         double[] outputDataDouble = (double[])(double[])outputData;
/* 1691 */         Arrays.fill(outputDataDouble, from, outputBufferCounter, (0.0D / 0.0D));
/* 1692 */         break;
/*      */       case 3:
/* 1694 */         Object[] outputDataObject = (Object[])(Object[])outputData;
/* 1695 */         Arrays.fill(outputDataObject, from, outputBufferCounter, null);
/*      */       }
/*      */     }
/*      */     else {
/* 1699 */       outputBufferCounter = from;
/* 1700 */       inputBufferCounter = toSkip - firstValueIndex;
/* 1701 */       numberOfElements -= inputBufferCounter;
/*      */     }
/*      */ 
/* 1705 */     if (lastIndex - outputBufferCounter + 1 >= numberOfElements)
/* 1706 */       System.arraycopy(inputData, inputBufferCounter, outputData, outputBufferCounter, numberOfElements);
/*      */     else {
/* 1708 */       throw new RuntimeException("Cannot happen");
/*      */     }
/*      */ 
/* 1712 */     if (to - (outputBufferCounter + numberOfElements) + 1 > 0)
/* 1713 */       switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[type.ordinal()]) {
/*      */       case 1:
/* 1715 */         int[] outputDataInt = (int[])(int[])outputData;
/* 1716 */         Arrays.fill(outputDataInt, outputBufferCounter + numberOfElements, to + 1, -2147483648);
/* 1717 */         break;
/*      */       case 2:
/* 1719 */         double[] outputDataDouble = (double[])(double[])outputData;
/* 1720 */         Arrays.fill(outputDataDouble, outputBufferCounter + numberOfElements, to + 1, (0.0D / 0.0D));
/* 1721 */         break;
/*      */       case 3:
/* 1723 */         Object[] outputDataObject = (Object[])(Object[])outputData;
/* 1724 */         Arrays.fill(outputDataObject, outputBufferCounter + numberOfElements, to + 1, null);
/*      */       }
/*      */   }
/*      */ 
/*      */   protected boolean isWeekendsBetween(long start, long end)
/*      */   {
/* 1731 */     if (end <= start) {
/* 1732 */       return false;
/*      */     }
/* 1734 */     if (end - start > 604800000L) {
/* 1735 */       return true;
/*      */     }
/*      */ 
/* 1738 */     this.cal.setTimeInMillis(start);
/* 1739 */     this.cal.set(7, 6);
/* 1740 */     this.cal.set(11, 22);
/* 1741 */     this.cal.set(12, 0);
/* 1742 */     this.cal.set(13, 0);
/* 1743 */     this.cal.set(14, 0);
/* 1744 */     long weekendStart = this.cal.getTimeInMillis();
/* 1745 */     return (weekendStart > start) && (weekendStart < end);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1860 */     return new StringBuilder().append("DataProvider(").append(this.instrument).append(", ").append(this.period).append(")").toString();
/*      */   }
/*      */   public abstract DataType getDataType();
/*      */ 
/*      */   public boolean isTicksDataType() {
/* 1866 */     return DataType.TICKS == getDataType();
/*      */   }
/*      */ 
/*      */   public S getBufferedDataSequence(int barsNumberBeforeTime, long time, int barsNumberAfterTime)
/*      */   {
/* 1871 */     Data[] buffer = getAllBufferedData();
/* 1872 */     int timeIndex = TimeDataUtils.approximateTimeIndex(buffer, time);
/*      */     IDataSequence result;
/*      */     IDataSequence result;
/* 1875 */     if (timeIndex < 0) {
/* 1876 */       result = createNullDataSequence();
/*      */     }
/*      */     else {
/* 1879 */       int startIndex = timeIndex - Math.min(timeIndex, barsNumberBeforeTime - 1);
/* 1880 */       int endIndex = Math.min(buffer.length - 1, timeIndex + barsNumberAfterTime);
/*      */ 
/* 1882 */       Data[] resultBuffer = createArray(endIndex - startIndex + 1);
/* 1883 */       System.arraycopy(buffer, startIndex, resultBuffer, 0, resultBuffer.length);
/*      */ 
/* 1885 */       long latestDataTime = getLatestDataTime();
/*      */       boolean includesLatestData;
/*      */       boolean includesLatestData;
/* 1888 */       if (resultBuffer.length > 0) {
/* 1889 */         includesLatestData = resultBuffer[(resultBuffer.length - 1)].getTime() >= latestDataTime;
/*      */       }
/*      */       else {
/* 1892 */         includesLatestData = false;
/*      */       }
/* 1894 */       result = createDataSequence(resultBuffer, includesLatestData);
/*      */     }
/*      */ 
/* 1899 */     return result;
/*      */   }
/*      */ 
/*      */   public D getLastKnownData()
/*      */   {
/* 1904 */     Data result = this.firstData != null ? this.firstData.clone() : null;
/* 1905 */     return result;
/*      */   }
/*      */ 
/*      */   public S getDataSequence(long from, long to)
/*      */   {
/* 1910 */     if (!this.active) {
/* 1911 */       throw new IllegalStateException("DataProvider is not active, activate it first");
/*      */     }
/* 1913 */     if (from > to) {
/* 1914 */       throw new IllegalArgumentException(new StringBuilder().append("Requested time interval is wrong 'from' > 'to' - ").append(DATE_FORMAT.format(Long.valueOf(from))).append(" > ").append(DATE_FORMAT.format(Long.valueOf(to))).toString());
/*      */     }
/* 1916 */     if (from < getFirstKnownTime()) {
/* 1917 */       throw new IllegalArgumentException(new StringBuilder().append("'From' time is earlier than the first known time - ").append(DATE_FORMAT.format(Long.valueOf(from))).append(" < ").append(DATE_FORMAT.format(Long.valueOf(getFirstKnownTime()))).toString());
/*      */     }
/* 1919 */     if (to > getLatestDataTime()) {
/* 1920 */       throw new IllegalArgumentException(new StringBuilder().append("'To' time is later than the last known time - ").append(DATE_FORMAT.format(Long.valueOf(to))).append(" > ").append(DATE_FORMAT.format(Long.valueOf(getLatestDataTime()))).toString());
/*      */     }
/*      */ 
/* 1923 */     return doGetDataSequence(from, to);
/*      */   }
/*      */ 
/*      */   protected abstract S doGetDataSequence(long paramLong1, long paramLong2);
/*      */ 
/*      */   static
/*      */   {
/*   62 */     LOGGER = LoggerFactory.getLogger(AbstractDataProvider.class);
/*      */ 
/*   64 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*      */ 
/*   66 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*      */   }
/*      */ 
/*      */   public static class IndicatorData
/*      */   {
/*      */     public IndicatorWrapper indicatorWrapper;
/*      */     private double[][] outputDataDouble;
/*      */     private int[][] outputDataInt;
/*      */     private Object[][] outputDataObject;
/*      */     public int lookback;
/*      */     public int lookforward;
/*      */     public boolean disabledIndicator;
/*      */     private long lastTime;
/*      */     public Object[] lastValues;
/*      */     public OfferSide[] inputSides;
/*      */     public Period[] inputPeriods;
/*      */     public Instrument[] inputInstruments;
/*      */     public Filter[] inputFilters;
/*      */     public AbstractDataProvider[] inputDataProviders;
/*      */ 
/*      */     public synchronized long getLastTime()
/*      */     {
/* 1829 */       return this.lastTime;
/*      */     }
/*      */     public synchronized void setLastTime(long value) {
/* 1832 */       this.lastTime = value;
/*      */     }
/*      */     public synchronized double[][] getOutputDataDouble() {
/* 1835 */       return this.outputDataDouble;
/*      */     }
/*      */     public synchronized void setOutputDataDouble(double[][] outputDataDouble) {
/* 1838 */       this.outputDataDouble = outputDataDouble;
/*      */     }
/*      */     public synchronized int[][] getOutputDataInt() {
/* 1841 */       return this.outputDataInt;
/*      */     }
/*      */     public synchronized void setOutputDataInt(int[][] outputDataInt) {
/* 1844 */       this.outputDataInt = outputDataInt;
/*      */     }
/*      */     public synchronized Object[][] getOutputDataObject() {
/* 1847 */       return this.outputDataObject;
/*      */     }
/*      */     public synchronized void setOutputDataObject(Object[][] outputDataObject) {
/* 1850 */       this.outputDataObject = outputDataObject;
/*      */     }
/*      */ 
/*      */     public IndicatorData() {
/* 1854 */       this.lastTime = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class LoadDataProgressListener
/*      */     implements LoadingProgressListener
/*      */   {
/*      */     private final AbstractDataProvider.AbstractDataCacheRequestData dataCacheRequestData;
/*      */     private boolean done;
/*      */     private ISynchronizeIndicators synchronizeIndicators;
/*      */ 
/*      */     public LoadDataProgressListener(AbstractDataProvider.AbstractDataCacheRequestData dataCacheRequestData, ISynchronizeIndicators synchronizeIndicators)
/*      */     {
/* 1778 */       this.dataCacheRequestData = dataCacheRequestData;
/* 1779 */       this.synchronizeIndicators = synchronizeIndicators;
/*      */     }
/*      */ 
/*      */     public LoadDataProgressListener(AbstractDataProvider.AbstractDataCacheRequestData dataCacheRequestData) {
/* 1783 */       this.dataCacheRequestData = dataCacheRequestData;
/*      */     }
/*      */ 
/*      */     public boolean done() {
/* 1787 */       return this.done;
/*      */     }
/*      */ 
/*      */     public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */     }
/*      */ 
/*      */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/* 1794 */       this.done = true;
/* 1795 */       synchronized (this) {
/* 1796 */         notifyAll();
/*      */       }
/*      */ 
/* 1800 */       AbstractDataProvider.this.dataLoaded(allDataLoaded, this.dataCacheRequestData, e, this.synchronizeIndicators);
/*      */     }
/*      */ 
/*      */     public boolean stopJob() {
/* 1804 */       return this.dataCacheRequestData.cancel;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class AbstractDataCacheRequestData
/*      */   {
/*      */     public int numberOfCandlesBefore;
/*      */     public int numberOfCandlesAfter;
/* 1760 */     public long time = -9223372036854775808L;
/*      */     public AbstractDataProvider.RequestMode mode;
/*      */     public LoadingProgressListener progressListener;
/*      */     public boolean cancel;
/*      */     public Map<String, Object> requestState;
/*      */     public Map<String, Object> responseState;
/*      */   }
/*      */ 
/*      */   protected class CacheDataUpdatedListener
/*      */     implements CacheDataUpdatedListener
/*      */   {
/*      */     protected CacheDataUpdatedListener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void cacheUpdated(Instrument instrument, long from, long to)
/*      */     {
/* 1753 */       AbstractDataProvider.this.setFilter(AbstractDataProvider.this.filter);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class IndicatorRecalculationThread extends Thread
/*      */     implements DataChangeListener
/*      */   {
/*  201 */     private BlockingQueue<AbstractDataProvider<D, S>.IndicatorRecalculationContext> indicatorRecalculationQueue = new LinkedBlockingQueue();
/*      */     private Period currentPeriod;
/*      */ 
/*      */     protected IndicatorRecalculationThread()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Period getCurrentPeriod()
/*      */     {
/*  207 */       return this.currentPeriod;
/*      */     }
/*      */ 
/*      */     public void setCurrentPeriod(Period currentPeriod) {
/*  211 */       this.currentPeriod = currentPeriod;
/*      */     }
/*      */ 
/*      */     private void addTask(AbstractDataProvider<D, S>.IndicatorRecalculationContext context) {
/*  215 */       if (newTask(context))
/*  216 */         this.indicatorRecalculationQueue.add(context);
/*      */     }
/*      */ 
/*      */     public void removeAllTasks()
/*      */     {
/*  221 */       this.indicatorRecalculationQueue.clear();
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       while (true)
/*      */         try {
/*  228 */           if (AbstractDataProvider.this.isActive())
/*      */             continue;
/*  230 */           this.indicatorRecalculationQueue.clear();
/*      */ 
/*  232 */           AbstractDataProvider.IndicatorRecalculationContext c = (AbstractDataProvider.IndicatorRecalculationContext)this.indicatorRecalculationQueue.take();
/*      */           try {
/*  234 */             recalculate(c);
/*      */ 
/*  236 */             synchronized (AbstractDataProvider.this.dataChangeListeners) {
/*  237 */               Iterator i$ = AbstractDataProvider.this.dataChangeListeners.iterator(); if (!i$.hasNext()) continue; DataChangeListener l = (DataChangeListener)i$.next();
/*  238 */               Iterator i$ = c.getIndicatorData().iterator(); if (!i$.hasNext()) continue; AbstractDataProvider.IndicatorData d = (AbstractDataProvider.IndicatorData)i$.next();
/*  239 */               l.indicatorChanged(AbstractDataProvider.this.period, d.indicatorWrapper.getId()); continue; continue;
/*      */             }
/*      */ 
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*  245 */             AbstractDataProvider.LOGGER.error(t.getMessage(), t);
/*      */           }
/*      */ 
/*  250 */           continue;
/*      */         }
/*      */         catch (InterruptedException e)
/*      */         {
/*  249 */           Thread.currentThread().interrupt();
/*      */         }
/*      */     }
/*      */ 
/*      */     private boolean newTask(AbstractDataProvider<D, S>.IndicatorRecalculationContext context)
/*      */     {
/*  255 */       Iterator queueItereator = this.indicatorRecalculationQueue.iterator();
/*      */ 
/*  257 */       while (queueItereator.hasNext()) {
/*  258 */         AbstractDataProvider.IndicatorRecalculationContext c = (AbstractDataProvider.IndicatorRecalculationContext)queueItereator.next();
/*  259 */         if ((c.getChartInstrument().equals(context.getChartInstrument())) && (c.getIndexFrom() == context.getIndexFrom()) && (c.getIndexTo() == context.getIndexTo()) && (c.getChartPeriod() == context.getChartPeriod()) && (c.getIndicatorData().equals(context.getIndicatorData())))
/*      */         {
/*  262 */           return false;
/*      */         }
/*      */       }
/*  265 */       return true;
/*      */     }
/*      */ 
/*      */     public void dataChanged(long from, long to, Period period, OfferSide offerSide) {
/*  269 */       this.currentPeriod = period;
/*      */     }
/*      */     public void indicatorAdded(Period period, int id) {
/*      */     }
/*      */     public void indicatorChanged(Period period, int id) {
/*      */     }
/*  275 */     public void indicatorRemoved(Period period, int id) { removeObsoleteTasks(new int[] { id }); }
/*      */ 
/*      */     public void indicatorsRemoved(Period period, int[] ids) {
/*  278 */       removeObsoleteTasks(ids);
/*      */     }
/*      */     public void loadingStarted(Period period, OfferSide offerSide) {
/*      */     }
/*      */     public void loadingFinished(Period period, OfferSide offerSide) {
/*      */     }
/*      */ 
/*      */     private void recalculate(AbstractDataProvider<D, S>.IndicatorRecalculationContext context) {
/*  286 */       AbstractDataProvider.this.indicators.calculateIndicators(context.getChartInstrument(), context.getChartPeriod(), context.getChartOfferSide(), AbstractDataProvider.this.filter, context.getIndexFrom(), context.getIndexTo(), context.getIndicatorData(), context.getIndexLast(), context.getAskBuffer(), context.getBidBuffer(), context.isDataTypeTicks(), context.getMaxNumberOfCandles(), context.getMultiplier(), AbstractDataProvider.this.firstData, AbstractDataProvider.this);
/*      */     }
/*      */ 
/*      */     private synchronized void removeObsoleteTasks(int[] indicatorIds)
/*      */     {
/*  305 */       Iterator queueItereator = this.indicatorRecalculationQueue.iterator();
/*  306 */       while (queueItereator.hasNext()) {
/*  307 */         AbstractDataProvider.IndicatorRecalculationContext c = (AbstractDataProvider.IndicatorRecalculationContext)queueItereator.next();
/*      */ 
/*  309 */         if (this.currentPeriod != c.getChartPeriod()) {
/*  310 */           queueItereator.remove();
/*  311 */           continue;
/*      */         }
/*      */ 
/*  314 */         Iterator it = c.getIndicatorData().iterator();
/*  315 */         while (it.hasNext()) {
/*  316 */           AbstractDataProvider.IndicatorData d = (AbstractDataProvider.IndicatorData)it.next();
/*  317 */           if (Arrays.binarySearch(indicatorIds, d.indicatorWrapper.getId()) >= 0)
/*  318 */             it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void lastKnownDataChanged(Data data)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   private class IndicatorRecalculationContext
/*      */   {
/*      */     private Instrument chartInstrument;
/*      */     private Period chartPeriod;
/*      */     private OfferSide chartOfferSide;
/*      */     private int indexFrom;
/*      */     private int indexTo;
/*      */     private ArrayList<AbstractDataProvider.IndicatorData> indicatorData;
/*      */     private int indexLast;
/*      */     private CandleData[] askBuffer;
/*      */     private CandleData[] bidBuffer;
/*      */     private boolean dataTypeTicks;
/*      */     private int maxNumberOfCandles;
/*      */     private int multiplier;
/*      */ 
/*      */     public Instrument getChartInstrument()
/*      */     {
/*  124 */       return this.chartInstrument;
/*      */     }
/*      */ 
/*      */     public Period getChartPeriod() {
/*  128 */       return this.chartPeriod;
/*      */     }
/*      */ 
/*      */     public OfferSide getChartOfferSide() {
/*  132 */       return this.chartOfferSide;
/*      */     }
/*      */ 
/*      */     public int getIndexFrom() {
/*  136 */       return this.indexFrom;
/*      */     }
/*      */ 
/*      */     public int getIndexTo() {
/*  140 */       return this.indexTo;
/*      */     }
/*      */ 
/*      */     public ArrayList<AbstractDataProvider.IndicatorData> getIndicatorData() {
/*  144 */       return this.indicatorData;
/*      */     }
/*      */ 
/*      */     public int getIndexLast() {
/*  148 */       return this.indexLast;
/*      */     }
/*      */ 
/*      */     public CandleData[] getAskBuffer() {
/*  152 */       return this.askBuffer;
/*      */     }
/*      */ 
/*      */     public CandleData[] getBidBuffer() {
/*  156 */       return this.bidBuffer;
/*      */     }
/*      */ 
/*      */     public boolean isDataTypeTicks() {
/*  160 */       return this.dataTypeTicks;
/*      */     }
/*      */ 
/*      */     public int getMaxNumberOfCandles() {
/*  164 */       return this.maxNumberOfCandles;
/*      */     }
/*      */ 
/*      */     public int getMultiplier() {
/*  168 */       return this.multiplier;
/*      */     }
/*      */ 
/*      */     public IndicatorRecalculationContext(Period chartInstrument, OfferSide chartPeriod, int chartOfferSide, int indexFrom, Collection<AbstractDataProvider.IndicatorData> indexTo, int indicatorData, CandleData[] indexLast, CandleData[] askBuffer, boolean bidBuffer, int dataTypeTicks, int maxNumberOfCandles, Data multiplier)
/*      */     {
/*  185 */       this.chartInstrument = chartInstrument;
/*  186 */       this.chartPeriod = chartPeriod;
/*  187 */       this.chartOfferSide = chartOfferSide;
/*  188 */       this.indexFrom = indexFrom;
/*  189 */       this.indexTo = indexTo;
/*  190 */       this.indicatorData = new ArrayList(indicatorData);
/*  191 */       this.indexLast = indexLast;
/*  192 */       this.askBuffer = askBuffer;
/*  193 */       this.bidBuffer = bidBuffer;
/*  194 */       this.dataTypeTicks = dataTypeTicks;
/*  195 */       this.maxNumberOfCandles = maxNumberOfCandles;
/*  196 */       this.multiplier = multiplier;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static enum RequestMode
/*      */   {
/*   70 */     OVERWRITE, APPEND_AT_START_NOT_OVERWRITING;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.AbstractDataProvider
 * JD-Core Version:    0.6.0
 */