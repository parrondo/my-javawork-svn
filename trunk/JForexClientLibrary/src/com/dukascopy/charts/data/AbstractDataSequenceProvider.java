/*     */ package com.dukascopy.charts.data;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.math.dataprovider.CandlesDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.DataChangeListener;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.IIndicatorsContainer;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractDataSequenceProvider<DataSequenceClass extends IDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDataSequenceProvider<DataClass, DataSequenceClass>, DataChangeListener, ISynchronizeIndicators
/*     */ {
/*  43 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataSequenceProvider.class);
/*  44 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() {  } ;
/*     */   public static final int SEQUENCE_SIZE_TO_BE_CONSIDERED_AS_EMPTY = 2;
/*     */   protected static final int MIN_SEQUENCE_SIZE = 10;
/*     */   protected static final int MAX_SEQUENCE_SIZE = 2000;
/*     */   protected static final int BUFFER_SIZE_MULTIPLIER = 2;
/*     */   protected final IFeedDataProvider feedDataProvider;
/*  53 */   protected final Map<Class<DataSequenceClass>, IDataProvider<DataClass, DataSequenceClass>> dataProviders = new HashMap();
/*     */ 
/*  55 */   protected volatile DataSequenceClass sequence = null;
/*  56 */   private IDataProvider<DataClass, DataSequenceClass> currentDataProvider = null;
/*  57 */   private final Set<DataChangeListener> dataChangeListeners = Collections.synchronizedSet(new HashSet());
/*     */   protected Instrument instrument;
/*     */   protected Period period;
/*     */   protected OfferSide offerSide;
/*  62 */   protected long time = -9223372036854775808L;
/*     */   protected final int maxSequenceSize;
/*  65 */   private int sequenceSize = 0;
/*  66 */   private int dataUnitsAfter = 0;
/*  67 */   private int margin = 0;
/*     */ 
/*  69 */   private final AtomicBoolean isNeedRequest = new AtomicBoolean(false);
/*     */   private IIndicatorsContainer indicatorsContainer;
/*     */ 
/*  81 */   public AbstractDataSequenceProvider(Instrument instrument, Period period, OfferSide offerSide, long time, int maxCandlesCount, IFeedDataProvider feedDataProvider) { if (instrument == null) {
/*  82 */       throw new IllegalArgumentException("Instrument is null");
/*     */     }
/*  84 */     this.instrument = instrument;
/*     */ 
/*  86 */     if (period == null) {
/*  87 */       throw new IllegalArgumentException("Period is null");
/*     */     }
/*  89 */     this.period = period;
/*     */ 
/*  91 */     this.offerSide = offerSide;
/*     */ 
/*  93 */     this.time = time;
/*     */ 
/*  95 */     validateSequenceSize(maxCandlesCount, 2000);
/*  96 */     this.maxSequenceSize = maxCandlesCount;
/*  97 */     this.sequenceSize = maxCandlesCount;
/*     */ 
/*  99 */     if (feedDataProvider == null) {
/* 100 */       throw new IllegalArgumentException("Feed Data Provider is null");
/*     */     }
/* 102 */     this.feedDataProvider = feedDataProvider;
/*     */ 
/* 104 */     this.sequence = getNullDataSequence();
/*     */   }
/*     */ 
/*     */   public final void setIndicatorsContainer(IIndicatorsContainer indicatorsContainer)
/*     */   {
/* 109 */     this.indicatorsContainer = indicatorsContainer;
/*     */   }
/*     */ 
/*     */   public List<IndicatorWrapper> getIndicators() {
/* 113 */     return this.indicatorsContainer.getIndicators();
/*     */   }
/*     */ 
/*     */   public long getIndicatorLatestTime(int indicatorId)
/*     */   {
/* 118 */     if (this.currentDataProvider == null) {
/* 119 */       return -1L;
/*     */     }
/* 121 */     return this.currentDataProvider.getLatestDataTime(indicatorId);
/*     */   }
/*     */ 
/*     */   public Object getIndicatorLatestValue(int indicatorId, int outputIdx)
/*     */   {
/* 127 */     if (this.currentDataProvider == null) {
/* 128 */       return null;
/*     */     }
/* 130 */     return this.currentDataProvider.getLatestValue(indicatorId, outputIdx); } 
/*     */   protected abstract void validate(Period paramPeriod);
/*     */ 
/*     */   protected abstract IDataProvider<DataClass, DataSequenceClass> getDataProvider();
/*     */ 
/*     */   protected abstract void synchronizeDataProviderState(ISynchronizeIndicators paramISynchronizeIndicators);
/*     */ 
/*     */   protected abstract DataSequenceClass getNullDataSequence();
/*     */ 
/*     */   protected abstract Filter getFilter();
/*     */ 
/*     */   protected abstract Period getDailyFilterPeriod();
/*     */ 
/* 147 */   public final void setPeriod(Period period) { if (period == null) {
/* 148 */       throw new IllegalArgumentException("Period is null");
/*     */     }
/*     */ 
/* 151 */     validate(period);
/*     */ 
/* 153 */     if (LOGGER.isTraceEnabled()) {
/* 154 */       LOGGER.trace("Set period : " + period);
/*     */     }
/*     */ 
/* 157 */     this.sequence = getNullDataSequence();
/* 158 */     if (getPeriod() != period) {
/* 159 */       this.period = period;
/* 160 */       activate();
/* 161 */     } else if (!isActive()) {
/* 162 */       activate();
/*     */     } }
/*     */ 
/*     */   public final Period getPeriod()
/*     */   {
/* 167 */     return this.period;
/*     */   }
/*     */ 
/*     */   public final Instrument getInstrument() {
/* 171 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void justSetInstrument(Instrument instrument) {
/* 175 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public final void setInstrument(Instrument instrument) {
/* 179 */     if (instrument == null) {
/* 180 */       throw new IllegalArgumentException("Instrument is null");
/*     */     }
/*     */ 
/* 183 */     if (LOGGER.isTraceEnabled()) {
/* 184 */       LOGGER.trace("Set instrument : " + instrument);
/*     */     }
/*     */ 
/* 187 */     this.sequence = getNullDataSequence();
/*     */ 
/* 189 */     if (getInstrument() != instrument) {
/* 190 */       this.instrument = instrument;
/* 191 */       activate();
/* 192 */     } else if (!isActive()) {
/* 193 */       activate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void setOfferSide(OfferSide offerSide) {
/* 198 */     if (offerSide == null) {
/* 199 */       throw new IllegalArgumentException("OfferSide is null");
/*     */     }
/*     */ 
/* 202 */     if (LOGGER.isTraceEnabled()) {
/* 203 */       LOGGER.trace("Set offer side : " + offerSide);
/*     */     }
/*     */ 
/* 206 */     this.sequence = getNullDataSequence();
/*     */ 
/* 208 */     if (this.offerSide != offerSide) {
/* 209 */       this.offerSide = offerSide;
/* 210 */       activate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final OfferSide getOfferSide() {
/* 215 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public final synchronized void setTime(long time) {
/* 219 */     if (this.time != time) {
/* 220 */       if (time > getLastTickTime()) {
/* 221 */         if (LOGGER.isTraceEnabled()) {
/* 222 */           LOGGER.trace("Time is after last tick's");
/*     */         }
/*     */ 
/* 225 */         time = getLastTickTime();
/*     */       }
/*     */ 
/* 228 */       State state = new State();
/*     */       try {
/* 230 */         this.sequence = getNullDataSequence();
/* 231 */         this.time = time;
/* 232 */         this.dataUnitsAfter = 0;
/*     */ 
/* 234 */         requestSequence();
/*     */       } catch (Exception ex) {
/* 236 */         state.rollback();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public final synchronized long getTime() {
/* 242 */     return this.time;
/*     */   }
/*     */ 
/*     */   public final long getMinimalTime() {
/* 246 */     return getMinimalTime(getPeriod());
/*     */   }
/*     */ 
/*     */   public final long getMinimalTime(Period period) {
/* 250 */     return this.feedDataProvider.getTimeOfFirstCandle(getInstrument(), period);
/*     */   }
/*     */ 
/*     */   public final void shiftToLastTick(int margin) {
/* 254 */     if (LOGGER.isTraceEnabled()) {
/* 255 */       LOGGER.trace("Shift to last tick with margin : " + margin);
/*     */     }
/*     */ 
/* 258 */     if (margin < 0) {
/* 259 */       throw new IllegalArgumentException("Margin is out of range : " + margin + " < 0");
/*     */     }
/*     */ 
/* 262 */     setMargin(margin);
/* 263 */     setTime(getLastTickTime());
/* 264 */     this.sequence = getNullDataSequence();
/* 265 */     requestSequence();
/*     */   }
/*     */ 
/*     */   public void setMargin(int margin) {
/* 269 */     this.margin = (margin > getMaxMargin() ? getMaxMargin() : margin);
/*     */   }
/*     */ 
/*     */   public int getMargin() {
/* 273 */     return this.margin;
/*     */   }
/*     */ 
/*     */   public synchronized void setCustomRange(int dataUnitsBefore, long time, int dataUnitsAfter)
/*     */   {
/* 278 */     LOGGER.trace("Set custom range : " + dataUnitsBefore + "/" + DATE_FORMAT.format(Long.valueOf(time)) + "/" + dataUnitsAfter);
/*     */ 
/* 281 */     State state = new State();
/*     */     try {
/* 283 */       this.sequence = getNullDataSequence();
/* 284 */       this.margin = 0;
/* 285 */       this.time = time;
/* 286 */       this.dataUnitsAfter = dataUnitsAfter;
/* 287 */       this.sequenceSize = (dataUnitsBefore + dataUnitsAfter);
/*     */ 
/* 289 */       requestSequence();
/*     */     } catch (Exception ex) {
/* 291 */       state.rollback();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int getLeftShiftAddition()
/*     */   {
/* 299 */     return 1;
/*     */   }
/*     */ 
/*     */   protected int getRightShiftAddition()
/*     */   {
/* 306 */     return 0;
/*     */   }
/*     */ 
/*     */   public synchronized boolean shift(int shiftValue) {
/* 310 */     if (LOGGER.isTraceEnabled()) {
/* 311 */       LOGGER.trace("Shift : " + shiftValue);
/*     */     }
/*     */ 
/* 314 */     if (shiftValue == 0) {
/* 315 */       if (LOGGER.isTraceEnabled()) {
/* 316 */         LOGGER.trace("Useless shift : need to be fixed");
/*     */       }
/* 318 */       return false;
/*     */     }
/*     */ 
/* 321 */     int sequenceSize = intervalsCount(this.sequence);
/*     */ 
/* 323 */     if (sequenceSize <= 2) {
/* 324 */       long shiftTime = getInterval() * shiftValue;
/* 325 */       long desiredTime = this.time + shiftTime;
/*     */ 
/* 327 */       if ((desiredTime > getMinimalTime()) || (shiftValue > 0)) {
/* 328 */         this.time = desiredTime;
/*     */       }
/*     */ 
/*     */     }
/* 332 */     else if (shiftValue < 0) {
/* 333 */       if ((this.sequence != null) && (intervalsCount(this.sequence) > 0)) {
/* 334 */         int offset = intervalsCount(this.sequence) - getLeftShiftAddition() - Math.abs(shiftValue);
/*     */ 
/* 336 */         if ((offset < 0) && (this.margin == 0)) {
/* 337 */           this.time = (getData(0).time - getInterval() * offset);
/*     */         } else {
/* 339 */           if (offset > 0) {
/* 340 */             this.time = getData(offset).time;
/* 341 */             this.dataUnitsAfter = 0;
/*     */           }
/*     */ 
/* 344 */           if (this.margin - Math.abs(shiftValue) > 0)
/* 345 */             this.margin -= Math.abs(shiftValue);
/*     */           else {
/* 347 */             this.margin = 0;
/*     */           }
/*     */         }
/*     */ 
/* 351 */         LOGGER.trace("Time shifted to : " + DATE_FORMAT.format(Long.valueOf(this.time)));
/*     */       } else {
/* 353 */         this.time -= getInterval() * Math.abs(shiftValue);
/*     */       }
/*     */     }
/* 356 */     else if (this.sequence.isLatestDataVisible()) {
/* 357 */       this.margin = Math.min(this.margin + shiftValue, getMaxMargin());
/* 358 */       this.dataUnitsAfter = 0;
/*     */     } else {
/* 360 */       int offset = sequenceSize - this.margin - shiftValue;
/* 361 */       if (offset < 0) {
/* 362 */         this.time += getInterval() * Math.abs(offset);
/* 363 */         this.dataUnitsAfter = sequenceSize;
/*     */       } else {
/* 365 */         this.dataUnitsAfter = (shiftValue + getRightShiftAddition());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 371 */     State state = new State();
/*     */     try {
/* 373 */       requestSequence();
/*     */     } catch (Exception ex) {
/* 375 */       ex.printStackTrace();
/* 376 */       LOGGER.warn("Error while shift : " + ex.getMessage());
/* 377 */       state.rollback();
/* 378 */       return false;
/*     */     }
/*     */ 
/* 381 */     return true;
/*     */   }
/*     */ 
/*     */   public int setSequenceSize(int sequenceSize) {
/* 385 */     validateSequenceSize(sequenceSize, this.maxSequenceSize);
/* 386 */     if (LOGGER.isTraceEnabled()) {
/* 387 */       LOGGER.trace("Set sequence size : " + sequenceSize);
/*     */     }
/*     */ 
/* 390 */     State state = new State();
/*     */     try {
/* 392 */       this.sequence = getNullDataSequence();
/* 393 */       this.sequenceSize = sequenceSize;
/*     */ 
/* 395 */       if (this.margin > getMaxMargin()) {
/* 396 */         this.margin = getMaxMargin();
/*     */       }
/*     */ 
/* 399 */       requestSequence();
/*     */     } catch (Exception ex) {
/* 401 */       LOGGER.warn("Error while setting sequence size : " + ex.getMessage());
/* 402 */       state.rollback();
/*     */     }
/*     */ 
/* 405 */     return this.sequenceSize;
/*     */   }
/*     */ 
/*     */   public int getSequenceSize() {
/* 409 */     return this.sequenceSize;
/*     */   }
/*     */ 
/*     */   public final void addDataChangeListener(DataChangeListener dataChangeListener)
/*     */   {
/* 416 */     this.dataChangeListeners.add(dataChangeListener);
/*     */   }
/*     */ 
/*     */   public final void removeDataChangeListener(DataChangeListener dataChangeListener)
/*     */   {
/* 421 */     this.dataChangeListeners.remove(dataChangeListener);
/*     */   }
/*     */ 
/*     */   public final long getLastTickTime()
/*     */   {
/* 426 */     if (this.currentDataProvider == null) {
/* 427 */       return -9223372036854775808L;
/*     */     }
/*     */ 
/* 430 */     return this.currentDataProvider.getLatestDataTime();
/*     */   }
/*     */ 
/*     */   public final DataSequenceClass getDataSequence()
/*     */   {
/* 435 */     if (isActive()) {
/* 436 */       Filter filter = getFilter();
/* 437 */       if (this.currentDataProvider.getFilter() != filter) {
/* 438 */         this.sequence = getNullDataSequence();
/* 439 */         this.currentDataProvider.setFilter(filter);
/*     */       }
/* 441 */       Period dailyFilter = getDailyFilterPeriod();
/* 442 */       if (this.currentDataProvider.getDailyFilterPeriod() != dailyFilter) {
/* 443 */         this.sequence = getNullDataSequence();
/* 444 */         this.currentDataProvider.setDailyFilterPeriod(dailyFilter);
/*     */       }
/*     */     }
/*     */ 
/* 448 */     if (this.isNeedRequest.get()) {
/* 449 */       requestSequence();
/*     */     }
/*     */ 
/* 452 */     return this.sequence;
/*     */   }
/*     */ 
/*     */   public final void setActive(boolean value)
/*     */   {
/* 457 */     if (LOGGER.isTraceEnabled()) {
/* 458 */       LOGGER.trace("Set active : " + value);
/*     */     }
/*     */ 
/* 461 */     if (!value)
/* 462 */       deactivate();
/*     */     else
/* 464 */       activate();
/*     */   }
/*     */ 
/*     */   public final boolean isActive()
/*     */   {
/* 470 */     if (this.currentDataProvider == null) {
/* 471 */       LOGGER.trace("Current data provider is null");
/* 472 */       return false;
/*     */     }
/*     */ 
/* 475 */     return this.currentDataProvider.isActive();
/*     */   }
/*     */ 
/*     */   public final void dispose()
/*     */   {
/* 480 */     this.dataChangeListeners.clear();
/*     */ 
/* 482 */     if (isActive()) {
/* 483 */       this.currentDataProvider.setActive(false);
/*     */     }
/*     */ 
/* 486 */     for (IDataProvider dataProvider : this.dataProviders.values())
/* 487 */       dataProvider.dispose();
/*     */   }
/*     */ 
/*     */   public void addIndicator(IndicatorWrapper indicatorWrapper)
/*     */     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 493 */     if (this.currentDataProvider == null) {
/* 494 */       return;
/*     */     }
/*     */ 
/* 497 */     this.currentDataProvider.addIndicator(indicatorWrapper);
/*     */   }
/*     */ 
/*     */   public boolean containsIndicator(IndicatorWrapper indicatorWrapper)
/*     */   {
/* 502 */     if (this.currentDataProvider == null) {
/* 503 */       return false;
/*     */     }
/*     */ 
/* 506 */     return this.currentDataProvider.containsIndicator(indicatorWrapper.getId());
/*     */   }
/*     */ 
/*     */   public void editIndicator(IndicatorWrapper indicatorWrapper) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
/*     */   {
/* 511 */     if (this.currentDataProvider == null) {
/* 512 */       return;
/*     */     }
/*     */ 
/* 515 */     this.currentDataProvider.editIndicator(indicatorWrapper.getId());
/*     */   }
/*     */ 
/*     */   public final void removeIndicator(IndicatorWrapper indicatorWrapper)
/*     */   {
/* 520 */     if (this.currentDataProvider == null) {
/* 521 */       return;
/*     */     }
/*     */ 
/* 524 */     this.currentDataProvider.removeIndicator(indicatorWrapper.getId());
/*     */   }
/*     */ 
/*     */   public void removeIndicators(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 529 */     if (this.currentDataProvider == null) {
/* 530 */       return;
/*     */     }
/* 532 */     int[] ids = new int[indicatorWrappers.size()];
/* 533 */     for (int i = 0; i < ids.length; i++) {
/* 534 */       ids[i] = ((IndicatorWrapper)indicatorWrappers.get(i)).getId();
/*     */     }
/* 536 */     this.currentDataProvider.removeIndicators(ids);
/*     */   }
/*     */ 
/*     */   public final void removeAllIndicators()
/*     */   {
/* 542 */     if (this.currentDataProvider == null) {
/* 543 */       return;
/*     */     }
/*     */ 
/* 546 */     this.currentDataProvider.removeAllIndicators();
/*     */   }
/*     */ 
/*     */   public final void loadingStarted(Period period, OfferSide offerSide)
/*     */   {
/* 553 */     if (LOGGER.isTraceEnabled()) {
/* 554 */       LOGGER.trace("Loading started @ " + period.toString() + ":" + offerSide);
/*     */     }
/*     */ 
/* 557 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 558 */       dataChangeListener.loadingStarted(period, offerSide);
/*     */   }
/*     */ 
/*     */   public final void loadingFinished(Period period, OfferSide offerSide)
/*     */   {
/* 564 */     if (LOGGER.isTraceEnabled()) {
/* 565 */       LOGGER.trace("Loading finished @ " + period.toString() + ":" + offerSide);
/*     */     }
/*     */ 
/* 568 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 569 */       dataChangeListener.loadingFinished(period, offerSide);
/*     */   }
/*     */ 
/*     */   public final void dataChanged(long from, long to, Period period, OfferSide offerSide)
/*     */   {
/* 575 */     if (!getPeriod().equals(period)) {
/* 576 */       if (LOGGER.isTraceEnabled()) {
/* 577 */         LOGGER.trace("Data changed for another period : ignoring");
/*     */       }
/* 579 */       return;
/*     */     }
/*     */ 
/* 582 */     if ((this.offerSide != null) && (this.offerSide != offerSide)) {
/* 583 */       if (LOGGER.isTraceEnabled()) {
/* 584 */         LOGGER.trace("Data changed for another offer side : ignoring");
/*     */       }
/* 586 */       return;
/*     */     }
/*     */ 
/* 589 */     if ((LOGGER.isTraceEnabled()) && (from < to)) {
/* 590 */       LOGGER.trace("Data changed @ " + period + " : " + DATE_FORMAT.format(Long.valueOf(from)) + " .. " + DATE_FORMAT.format(Long.valueOf(to)));
/*     */     }
/*     */ 
/* 593 */     if (!isActive()) {
/* 594 */       if (LOGGER.isTraceEnabled()) {
/* 595 */         LOGGER.trace("Data provider is not active : ignoring");
/*     */       }
/* 597 */       return;
/*     */     }
/*     */ 
/* 600 */     if ((this.sequence != null) && (this.sequence != getNullDataSequence()) && (this.sequence.size() > 0) && (!this.sequence.isLatestDataVisible()))
/*     */     {
/* 605 */       long sequenceFrom = this.sequence.getFrom();
/* 606 */       long sequenceTo = this.sequence.getTo();
/*     */ 
/* 608 */       if (LOGGER.isTraceEnabled()) {
/* 609 */         LOGGER.trace("[" + DATE_FORMAT.format(Long.valueOf(sequenceFrom)) + " .. " + DATE_FORMAT.format(Long.valueOf(sequenceTo)) + "] [" + DATE_FORMAT.format(Long.valueOf(from)) + " .. " + DATE_FORMAT.format(Long.valueOf(to)) + "]");
/* 610 */         LOGGER.trace("Current sequence : " + this.sequence);
/*     */       }
/*     */ 
/* 613 */       if (sequenceTo < from) {
/* 614 */         if (LOGGER.isTraceEnabled()) {
/* 615 */           LOGGER.trace("Data changed out of sequence range " + DATE_FORMAT.format(Long.valueOf(sequenceTo)) + " < " + DATE_FORMAT.format(Long.valueOf(from)) + " : ignoring");
/*     */         }
/* 617 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 621 */     this.isNeedRequest.set(true);
/* 622 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 623 */       dataChangeListener.dataChanged(from, to, period, offerSide);
/*     */   }
/*     */ 
/*     */   public final void indicatorAdded(Period period, int id)
/*     */   {
/* 629 */     if (!getPeriod().equals(period)) {
/* 630 */       if (LOGGER.isTraceEnabled()) {
/* 631 */         LOGGER.trace("Indicator added for another period : ignoring");
/*     */       }
/* 633 */       return;
/*     */     }
/*     */ 
/* 636 */     if (LOGGER.isTraceEnabled()) {
/* 637 */       LOGGER.trace("Indicator added : " + id + " @ " + period);
/*     */     }
/* 639 */     this.isNeedRequest.set(true);
/* 640 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 641 */       dataChangeListener.indicatorAdded(period, id);
/*     */   }
/*     */ 
/*     */   public final void indicatorChanged(Period period, int id)
/*     */   {
/* 647 */     if (!getPeriod().equals(period)) {
/* 648 */       if (LOGGER.isTraceEnabled()) {
/* 649 */         LOGGER.trace("Indicator changed for another period : ignoring");
/*     */       }
/* 651 */       return;
/*     */     }
/*     */ 
/* 654 */     if (LOGGER.isTraceEnabled()) {
/* 655 */       LOGGER.trace("Indicator changed : " + id + " @ " + period);
/*     */     }
/*     */ 
/* 658 */     this.isNeedRequest.set(true);
/* 659 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 660 */       dataChangeListener.indicatorChanged(period, id);
/*     */   }
/*     */ 
/*     */   public final void indicatorRemoved(Period period, int id)
/*     */   {
/* 666 */     if (!getPeriod().equals(period)) {
/* 667 */       if (LOGGER.isTraceEnabled()) {
/* 668 */         LOGGER.trace("Indicator removed for another period : ignoring");
/*     */       }
/* 670 */       return;
/*     */     }
/*     */ 
/* 673 */     if (LOGGER.isTraceEnabled()) {
/* 674 */       LOGGER.trace("Indicator removed : " + id + " @ " + period);
/*     */     }
/*     */ 
/* 677 */     this.isNeedRequest.set(true);
/* 678 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 679 */       dataChangeListener.indicatorRemoved(period, id);
/*     */   }
/*     */ 
/*     */   public final void indicatorsRemoved(Period period, int[] ids)
/*     */   {
/* 685 */     if (!getPeriod().equals(period)) {
/* 686 */       if (LOGGER.isTraceEnabled()) {
/* 687 */         LOGGER.trace("Indicator removed for another period : ignoring");
/*     */       }
/* 689 */       return;
/*     */     }
/*     */ 
/* 692 */     if (LOGGER.isTraceEnabled()) {
/* 693 */       StringBuffer logMessage = new StringBuffer("Indicators removed : ");
/* 694 */       for (int id : ids) {
/* 695 */         logMessage.append(id).append(", ");
/*     */       }
/* 697 */       logMessage.append(" @ ").append(period);
/*     */ 
/* 699 */       LOGGER.trace(logMessage.toString());
/*     */     }
/*     */ 
/* 702 */     this.isNeedRequest.set(true);
/* 703 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 704 */       dataChangeListener.indicatorsRemoved(period, ids);
/*     */   }
/*     */ 
/*     */   protected final synchronized void activate()
/*     */   {
/* 711 */     IDataProvider dataProvider = getDataProvider();
/*     */ 
/* 714 */     if (this.margin > 0) {
/* 715 */       if (LOGGER.isTraceEnabled()) {
/* 716 */         LOGGER.trace("Reset time");
/*     */       }
/* 718 */       this.time = 0L;
/*     */     }
/*     */ 
/* 721 */     if (dataProvider.isActive()) {
/* 722 */       synchronizeDataProviderState(null);
/*     */ 
/* 724 */       if (LOGGER.isTraceEnabled()) {
/* 725 */         LOGGER.info("Data provider is already active");
/*     */       }
/* 727 */       return;
/*     */     }
/*     */ 
/* 730 */     if (LOGGER.isTraceEnabled()) {
/* 731 */       LOGGER.trace("Activate : " + dataProvider.toString());
/*     */     }
/*     */ 
/* 735 */     deactivate();
/*     */ 
/* 737 */     this.sequence = getNullDataSequence();
/* 738 */     this.currentDataProvider = dataProvider;
/* 739 */     dataProvider.setActive(true);
/*     */ 
/* 742 */     if ((dataProvider instanceof CandlesDataProvider)) {
/* 743 */       removeAllIndicators();
/* 744 */       for (IndicatorWrapper indicatorWrapper : this.indicatorsContainer.getIndicators()) {
/*     */         try {
/* 746 */           addIndicator(indicatorWrapper);
/*     */         } catch (Exception ex) {
/* 748 */           LOGGER.warn("Error while adding indicator [" + indicatorWrapper.getName() + "] to data provider : " + ex.getMessage());
/* 749 */           throw new IllegalStateException(ex);
/*     */         }
/*     */       }
/* 752 */       synchronizeDataProviderState(this);
/*     */     } else {
/* 754 */       synchronizeDataProviderState(null);
/* 755 */       removeAllIndicators();
/* 756 */       for (IndicatorWrapper indicatorWrapper : this.indicatorsContainer.getIndicators()) {
/*     */         try {
/* 758 */           addIndicator(indicatorWrapper);
/*     */         } catch (Exception ex) {
/* 760 */           LOGGER.warn("Error while adding indicator [" + indicatorWrapper.getName() + "] to data provider : " + ex.getMessage());
/* 761 */           throw new IllegalStateException(ex);
/*     */         }
/*     */       }
/* 764 */       requestSequence();
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void synchronizeIndicators()
/*     */   {
/* 786 */     requestSequence();
/*     */   }
/*     */ 
/*     */   public int intervalsCount(DataSequenceClass sequence) {
/* 790 */     return sequence.size();
/*     */   }
/*     */ 
/*     */   protected Data getData(int dataUnitIndex) {
/* 794 */     return this.sequence.getData(dataUnitIndex);
/*     */   }
/*     */ 
/*     */   public long getInterval()
/*     */   {
/* 800 */     return getPeriod() == Period.TICK ? Period.ONE_SEC.getInterval() : getPeriod().getInterval();
/*     */   }
/*     */ 
/*     */   private void validateSequenceSize(int sequenceSize, int maxSize) {
/* 804 */     if (sequenceSize < 10) {
/* 805 */       throw new IllegalArgumentException("Sequence size is out of range : " + sequenceSize + " < " + 10);
/*     */     }
/*     */ 
/* 808 */     if (sequenceSize > maxSize)
/* 809 */       throw new IllegalArgumentException("Sequence size is out of range : " + sequenceSize + " > " + maxSize);
/*     */   }
/*     */ 
/*     */   private int getMaxMargin()
/*     */   {
/* 814 */     return this.sequenceSize / 2;
/*     */   }
/*     */ 
/*     */   private void deactivate() {
/* 818 */     if (isActive()) {
/* 819 */       if (LOGGER.isTraceEnabled()) {
/* 820 */         LOGGER.trace("Deactivate : " + this.currentDataProvider);
/*     */       }
/*     */ 
/* 823 */       this.currentDataProvider.removeDataChangeListener(this);
/* 824 */       this.currentDataProvider.setActive(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long getCandleStartFast() {
/* 829 */     return DataCacheUtils.getCandleStartFast(getPeriod() == Period.TICK ? Period.ONE_SEC : getPeriod(), this.time);
/*     */   }
/*     */ 
/*     */   private synchronized void requestSequence() {
/* 833 */     if (!isActive()) {
/* 834 */       LOGGER.debug("Data provider is not active: " + this.instrument + " @ " + getPeriod() + " : " + this.offerSide);
/* 835 */       return;
/*     */     }
/*     */ 
/* 839 */     long latestTime = this.currentDataProvider.getLatestDataTime();
/* 840 */     if (latestTime <= 0L) {
/* 841 */       LOGGER.trace("Data provider isn't ready");
/* 842 */       return;
/*     */     }
/*     */ 
/* 845 */     if ((this.time <= 0L) || (this.time > latestTime)) {
/* 846 */       if (LOGGER.isTraceEnabled()) {
/* 847 */         LOGGER.trace("Set time to provider's latest : " + DATE_FORMAT.format(Long.valueOf(this.time)) + " >> " + DATE_FORMAT.format(Long.valueOf(latestTime)));
/*     */       }
/* 849 */       this.time = latestTime;
/*     */     }
/*     */ 
/* 852 */     this.time = getCandleStartFast();
/*     */ 
/* 854 */     if (LOGGER.isTraceEnabled()) {
/* 855 */       LOGGER.trace("Request data sequence : " + toString());
/*     */     }
/*     */ 
/* 858 */     int before = this.sequenceSize - this.margin - this.dataUnitsAfter;
/* 859 */     int after = this.margin + this.dataUnitsAfter;
/* 860 */     if ((before < 0) || (after < 0) || (before + after == 0)) {
/* 861 */       if (LOGGER.isTraceEnabled()) {
/* 862 */         LOGGER.trace("Unable to request data sequence : before=" + before + " after=" + after);
/*     */       }
/* 864 */       return;
/*     */     }
/*     */ 
/* 867 */     IDataSequence sequence = this.currentDataProvider.getDataSequence(before, this.time, after);
/*     */ 
/* 869 */     if (LOGGER.isTraceEnabled()) {
/* 870 */       LOGGER.trace("Requested data sequence : " + sequence);
/*     */     }
/*     */ 
/* 873 */     if ((sequence != null) && (intervalsCount(sequence) > 0)) {
/* 874 */       if (LOGGER.isTraceEnabled()) {
/* 875 */         LOGGER.trace("Sequence size : " + intervalsCount(sequence) + " / " + (this.sequenceSize - this.margin));
/*     */       }
/*     */ 
/* 878 */       long sequenceEndTime = sequence.getTo();
/*     */ 
/* 880 */       if (this.time != sequenceEndTime)
/*     */       {
/* 882 */         int intervalsDiff = calculateIntervalsDifference(this.time, this.sequence, sequence);
/*     */ 
/* 884 */         if (intervalsDiff != 0) {
/* 885 */           if (LOGGER.isTraceEnabled()) {
/* 886 */             LOGGER.trace("Set time to : " + DATE_FORMAT.format(Long.valueOf(sequenceEndTime)) + " / " + DATE_FORMAT.format(Long.valueOf(this.time)));
/*     */           }
/* 888 */           if (intervalsCount(sequence) > 2) {
/* 889 */             this.time = sequenceEndTime;
/*     */           }
/*     */ 
/* 892 */           if ((this.margin > 0) && (sequence.isLatestDataVisible()) && (intervalsDiff > 0)) {
/* 893 */             this.margin = Math.max(0, this.margin - intervalsDiff);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 898 */       this.sequence = sequence;
/*     */ 
/* 900 */       this.dataUnitsAfter = 0;
/*     */     }
/* 902 */     this.isNeedRequest.compareAndSet(true, false);
/*     */   }
/*     */ 
/*     */   protected int calculateIntervalsDifference(long requestedTime, DataSequenceClass oldSequence, DataSequenceClass newSequence) {
/* 906 */     long newSequenceEndTime = newSequence.getTo();
/*     */ 
/* 909 */     return (int)((float)((newSequenceEndTime - requestedTime) / getInterval()) + 0.5F);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 916 */     return "instrument : " + this.instrument + " / " + "period : " + getPeriod() + " / " + "offer side : " + this.offerSide + " / " + "providers : " + this.dataProviders.size() + "\n" + "time : " + DATE_FORMAT.format(Long.valueOf(this.time)) + " / " + "sequence size : " + this.sequenceSize + " / " + "data units after : " + this.dataUnitsAfter + " / " + "margin : " + this.margin + "\n" + "sequence : " + this.sequence;
/*     */   }
/*     */ 
/*     */   public IFeedDataProvider getFeedDataProvider()
/*     */   {
/* 959 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public int getMaxSequenceSize() {
/* 963 */     return this.maxSequenceSize;
/*     */   }
/*     */ 
/*     */   public Map<Class<DataSequenceClass>, IDataProvider<DataClass, DataSequenceClass>> getDataProviders() {
/* 967 */     return this.dataProviders;
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod) {
/* 971 */     this.period = jForexPeriod.getPeriod();
/*     */   }
/*     */ 
/*     */   public DataSequenceClass getBufferedDataSequence(int barsNumberBeforeTime, int barsNumberAfterTime)
/*     */   {
/* 976 */     if (this.currentDataProvider == null) {
/* 977 */       return null;
/*     */     }
/* 979 */     return this.currentDataProvider.getBufferedDataSequence(barsNumberBeforeTime, this.time, barsNumberAfterTime);
/*     */   }
/*     */ 
/*     */   public void lastKnownDataChanged(Data data)
/*     */   {
/* 984 */     for (DataChangeListener dataChangeListener : this.dataChangeListeners)
/* 985 */       dataChangeListener.lastKnownDataChanged(data);
/*     */   }
/*     */ 
/*     */   public DataClass getLastKnownData()
/*     */   {
/* 991 */     if (this.currentDataProvider == null) {
/* 992 */       return null;
/*     */     }
/* 994 */     return this.currentDataProvider.getLastKnownData();
/*     */   }
/*     */ 
/*     */   private class State
/*     */   {
/*     */     private final long time;
/*     */     private final int sequenceSize;
/*     */     private final DataSequenceClass dataSequence;
/*     */     private final int dataUnitsAfter;
/*     */     private final int margin;
/*     */ 
/*     */     public State()
/*     */     {
/* 942 */       this.time = AbstractDataSequenceProvider.this.time;
/* 943 */       this.margin = AbstractDataSequenceProvider.this.margin;
/* 944 */       this.sequenceSize = AbstractDataSequenceProvider.this.sequenceSize;
/* 945 */       this.dataSequence = AbstractDataSequenceProvider.this.sequence;
/* 946 */       this.dataUnitsAfter = AbstractDataSequenceProvider.this.dataUnitsAfter;
/*     */     }
/*     */ 
/*     */     public void rollback() {
/* 950 */       AbstractDataSequenceProvider.this.time = this.time;
/* 951 */       AbstractDataSequenceProvider.access$102(AbstractDataSequenceProvider.this, this.sequenceSize);
/* 952 */       AbstractDataSequenceProvider.this.sequence = this.dataSequence;
/* 953 */       AbstractDataSequenceProvider.access$202(AbstractDataSequenceProvider.this, this.dataUnitsAfter);
/* 954 */       AbstractDataSequenceProvider.access$002(AbstractDataSequenceProvider.this, this.margin);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.AbstractDataSequenceProvider
 * JD-Core Version:    0.6.0
 */