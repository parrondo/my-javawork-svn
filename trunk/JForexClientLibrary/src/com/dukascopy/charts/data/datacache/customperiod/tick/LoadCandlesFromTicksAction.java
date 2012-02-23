/*     */ package com.dukascopy.charts.data.datacache.customperiod.tick;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadCustomPeriodNumberOfCandlesAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadDataAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressAdapter;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadCandlesFromTicksAction
/*     */   implements Runnable
/*     */ {
/*     */   private static final long ONE_MINUTE = 60000L;
/*  35 */   private static final Logger LOGGER = LoggerFactory.getLogger(LoadCustomPeriodNumberOfCandlesAction.class);
/*     */ 
/*  37 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private final Instrument instrument;
/*     */   private final OfferSide offerSide;
/*     */   private final Filter filter;
/*     */   private final Period desiredPeriod;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private final LoadingProgressListener originalLoadingProgress;
/*     */   private final LiveFeedListener originalLiveFeedListener;
/*     */   private final int numberOfCandlesBefore;
/*     */   private final int numberOfCandlesAfter;
/*     */   private final long fromTime;
/*     */   private final long toTime;
/*     */   private final long time;
/*     */ 
/*     */   public LoadCandlesFromTicksAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, Filter filter, Period desiredPeriod, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LoadingProgressListener originalLoadingProgress, LiveFeedListener originalLiveFeedListener, int numberOfCandlesBefore, int numberOfCandlesAfter, long fromTime, long toTime, long time)
/*     */   {
/*  39 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  72 */     this.feedDataProvider = feedDataProvider;
/*  73 */     this.instrument = instrument;
/*  74 */     this.desiredPeriod = desiredPeriod;
/*  75 */     this.offerSide = offerSide;
/*  76 */     this.filter = filter;
/*  77 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*  78 */     this.originalLoadingProgress = originalLoadingProgress;
/*  79 */     this.originalLiveFeedListener = originalLiveFeedListener;
/*     */ 
/*  81 */     this.numberOfCandlesBefore = numberOfCandlesBefore;
/*  82 */     this.numberOfCandlesAfter = numberOfCandlesAfter;
/*  83 */     this.time = time;
/*  84 */     this.fromTime = fromTime;
/*  85 */     this.toTime = toTime;
/*     */   }
/*     */ 
/*     */   public LoadCandlesFromTicksAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, Filter filter, Period desiredPeriod, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LoadingProgressListener originalLoadingProgress, LiveFeedListener originalLiveFeedListener, int numberOfCandlesBefore, int numberOfCandlesAfter, long time)
/*     */   {
/* 101 */     this(feedDataProvider, instrument, offerSide, filter, desiredPeriod, intraperiodExistsPolicy, originalLoadingProgress, originalLiveFeedListener, numberOfCandlesBefore, numberOfCandlesAfter, -1L, -1L, time);
/*     */   }
/*     */ 
/*     */   public LoadCandlesFromTicksAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, Filter filter, Period desiredPeriod, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LoadingProgressListener originalLoadingProgress, LiveFeedListener originalLiveFeedListener, long fromTime, long toTime)
/*     */   {
/* 130 */     this(feedDataProvider, instrument, offerSide, filter, desiredPeriod, intraperiodExistsPolicy, originalLoadingProgress, originalLiveFeedListener, -1, -1, fromTime, toTime, -1L);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 150 */     if ((getFromTime() > -1L) && (getToTime() > -1L)) {
/* 151 */       long correctedToTheNextCandleToTime = DataCacheUtils.getNextCandleStartFast(getDesiredPeriod(), getToTime());
/* 152 */       performLoadingForFromToTimes(getFromTime(), correctedToTheNextCandleToTime);
/*     */     }
/* 154 */     else if ((getTime() > -1L) && (getNumberOfCandlesAfter() > -1) && (getNumberOfCandlesBefore() > -1)) {
/* 155 */       performLoadingForCandlesCount();
/*     */     }
/*     */     else {
/* 158 */       throw new IllegalArgumentException("Wrong params are set");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void performLoadingForFromToTimes(long fromTime, long toTime) {
/* 163 */     Exception exception = null;
/* 164 */     CustomPeriodFromTicksCreator customPeriodFromTicksCreator = null;
/* 165 */     boolean loadingProcessStopped = false;
/*     */     try
/*     */     {
/* 168 */       customPeriodFromTicksCreator = new FlowCustomPeriodFromTicksCreator(getInstrument(), getOfferSide(), getDesiredPeriod(), getFilter(), false, new Long(fromTime), null, this.feedDataProvider.getFilterManager());
/*     */ 
/* 179 */       customPeriodFromTicksCreator.addListener(getOriginalLiveFeedListener());
/*     */ 
/* 181 */       CustomPeriodTickLiveFeedListener ticLiveFeedListener = new CustomPeriodTickLiveFeedListener(customPeriodFromTicksCreator);
/* 182 */       TickLoadingProgressListener tickAfterLoadingProgressListener = createTickLoadingProgressListener(getOriginalLoadingProgress());
/*     */ 
/* 184 */       long timeOfFirstCandle = getFeedDataProvider().getTimeOfFirstCandle(getInstrument(), Period.TICK);
/* 185 */       long startTime = fromTime > timeOfFirstCandle ? fromTime : timeOfFirstCandle;
/*     */ 
/* 187 */       while (startTime < toTime) {
/* 188 */         if (getOriginalLoadingProgress().stopJob()) { loadingProcessStopped = true;
/*     */           return;
/*     */         }
/* 193 */         long endTime = startTime + getIncreaseTimeInterval();
/* 194 */         endTime = endTime < toTime ? endTime : toTime;
/*     */ 
/* 196 */         loadDataForTimeInterval(startTime, endTime, ticLiveFeedListener, tickAfterLoadingProgressListener);
/* 197 */         startTime = endTime;
/*     */       }
/*     */ 
/* 200 */       CandleData lastCandle = customPeriodFromTicksCreator.getLastData();
/* 201 */       CandleData notFinishedCandle = customPeriodFromTicksCreator.getCurrentCandleDataUnderAnalysis();
/*     */ 
/* 203 */       if ((lastCandle != null) && (lastCandle.getTime() < getToTime()))
/*     */       {
/* 207 */         processFakeTickToCompleteLastCandle(customPeriodFromTicksCreator, lastCandle.getClose(), toTime);
/*     */       }
/* 209 */       else if ((lastCandle == null) && (notFinishedCandle != null) && (notFinishedCandle.getTime() <= getToTime()))
/*     */       {
/* 213 */         processFakeTickToCompleteLastCandle(customPeriodFromTicksCreator, notFinishedCandle.getClose(), toTime);
/*     */       }
/* 215 */       else if ((lastCandle == null) && (notFinishedCandle == null))
/*     */       {
/* 219 */         if (Filter.NO_FILTER.equals(this.filter))
/*     */         {
/* 223 */           TickData lastBeforeTimeTick = getLastTickBeforeTime(toTime, this.desiredPeriod);
/* 224 */           if (lastBeforeTimeTick != null) {
/* 225 */             double price = OfferSide.ASK.equals(this.offerSide) ? lastBeforeTimeTick.getAsk() : lastBeforeTimeTick.getBid();
/*     */ 
/* 228 */             processFakeTickToCompleteLastCandle(customPeriodFromTicksCreator, price, toTime);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 234 */       exception = e;
/* 235 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 242 */         postFinishDataLoading(customPeriodFromTicksCreator, null, exception, loadingProcessStopped);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 248 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processFakeTickToCompleteLastCandle(CustomPeriodFromTicksCreator customPeriodFromTicksCreator, double price, long time)
/*     */   {
/* 258 */     TickData fakeTick = new TickData(time, price, price, 0.0D, 0.0D);
/* 259 */     customPeriodFromTicksCreator.analyseTickData(fakeTick);
/*     */   }
/*     */ 
/*     */   private void performLoadingForCandlesCount()
/*     */   {
/* 264 */     Exception exception = null;
/*     */ 
/* 266 */     CustomPeriodFromTicksCreator customPeriodAfterCreator = null;
/* 267 */     CustomPeriodFromTicksCreator customPeriodBeforeCreator = null;
/*     */     try
/*     */     {
/* 273 */       long timeForBeforeCreator = getTime();
/* 274 */       if (this.numberOfCandlesBefore > 0) {
/* 275 */         customPeriodBeforeCreator = loadDataBeforeTime(this.instrument, this.offerSide, this.filter, this.desiredPeriod, this.numberOfCandlesBefore, timeForBeforeCreator, this.feedDataProvider.getFilterManager());
/*     */       }
/*     */ 
/* 289 */       long timeForAfterCreator = getTime();
/* 290 */       if (this.numberOfCandlesBefore > 0) {
/* 291 */         timeForAfterCreator = DataCacheUtils.getNextCandleStart(this.desiredPeriod, getTime());
/*     */       }
/* 293 */       if (this.numberOfCandlesAfter > 0) {
/* 294 */         customPeriodAfterCreator = loadDataAfterTime(this.instrument, this.offerSide, this.filter, this.desiredPeriod, this.numberOfCandlesAfter, timeForAfterCreator, this.feedDataProvider.getFilterManager());
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 306 */       exception = e;
/* 307 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 312 */         postData(customPeriodBeforeCreator, customPeriodAfterCreator, exception);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 318 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private CustomPeriodFromTicksCreator loadDataBeforeTime(Instrument instrument, OfferSide offerSide, Filter filter, Period desiredPeriod, int numberOfCandlesBefore, long time, IFilterManager filterManager)
/*     */     throws DataCacheException
/*     */   {
/* 333 */     if (numberOfCandlesBefore <= 0)
/* 334 */       return null;
/*     */     long from;
/*     */     long from;
/* 339 */     if ((filter == null) || (Filter.NO_FILTER.equals(filter))) {
/* 340 */       from = DataCacheUtils.getTimeForNCandlesBackFast(desiredPeriod, time, numberOfCandlesBefore);
/*     */     }
/*     */     else
/*     */     {
/* 344 */       long to = DataCacheUtils.getNextCandleStartFast(desiredPeriod, time) - 1L;
/*     */ 
/* 346 */       CustomPeriodFromTicksCreator customPeriodBeforeInverseCreator = new CustomPeriodFromTicksCreator(instrument, offerSide, numberOfCandlesBefore, desiredPeriod, filter, true, Long.valueOf(to), null, filterManager);
/*     */ 
/* 358 */       CustomPeriodTickLiveFeedListener tickInverseLiveFeedListener = new CustomPeriodTickLiveFeedListener(customPeriodBeforeInverseCreator);
/* 359 */       TickLoadingProgressListener tickInverseLoadingProgressListener = createTickLoadingProgressListener(getOriginalLoadingProgress());
/*     */ 
/* 361 */       long firstCandleTime = getFeedDataProvider().getTimeOfFirstCandle(getInstrument(), Period.TICK);
/* 362 */       loadDataBeforeTime(to, firstCandleTime, tickInverseLiveFeedListener, tickInverseLoadingProgressListener);
/*     */       long from;
/* 364 */       if (customPeriodBeforeInverseCreator.getLoadedCandleCount() > 0) {
/* 365 */         from = customPeriodBeforeInverseCreator.getLastData().time;
/*     */       }
/*     */       else {
/* 368 */         from = DataCacheUtils.getTimeForNCandlesBackFast(desiredPeriod, time, numberOfCandlesBefore);
/*     */       }
/*     */     }
/*     */ 
/* 372 */     CustomPeriodFromTicksCreator creator = loadDataAfterTime(instrument, offerSide, filter, desiredPeriod, numberOfCandlesBefore, from, filterManager);
/*     */ 
/* 382 */     return creator;
/*     */   }
/*     */ 
/*     */   private TickData getLastTickBeforeTime(long timeTo, Period period) throws DataCacheException
/*     */   {
/* 387 */     long from = timeTo;
/*     */ 
/* 389 */     LoadingProgressListener loadingProgressListener = new LoadingProgressAdapter()
/*     */     {
/*     */     };
/* 390 */     FirstTickLiveFeedListener firstTickLoadingProgressListener = new FirstTickLiveFeedListener();
/*     */ 
/* 392 */     long to = DataCacheUtils.getNextCandleStartFast(period, timeTo) - 1L;
/*     */ 
/* 394 */     LoadDataAction action = createLoadDataAction(from, to, firstTickLoadingProgressListener, loadingProgressListener);
/*     */ 
/* 401 */     action.run();
/*     */ 
/* 403 */     if (firstTickLoadingProgressListener.getFirstTick() != null) {
/* 404 */       return firstTickLoadingProgressListener.getFirstTick();
/*     */     }
/*     */ 
/* 408 */     long firstCandleTime = this.feedDataProvider.getTimeOfFirstCandle(getInstrument(), Period.TICK);
/*     */ 
/* 410 */     long HOUR = 3600000L;
/*     */ 
/* 412 */     loadingProgressListener = new LoadingProgressAdapter()
/*     */     {
/*     */     };
/* 413 */     LastTickLiveFeedListener lastTickLoadingProgressListener = new LastTickLiveFeedListener();
/*     */ 
/* 415 */     to = timeTo;
/*     */ 
/* 419 */     while ((firstCandleTime < timeTo) && (lastTickLoadingProgressListener.getLastTick() == null))
/*     */     {
/* 422 */       from = to - 3600000L;
/*     */ 
/* 424 */       action = createLoadDataAction(from, to, lastTickLoadingProgressListener, loadingProgressListener);
/*     */ 
/* 431 */       action.run();
/*     */ 
/* 433 */       to = from;
/*     */     }
/*     */ 
/* 436 */     return lastTickLoadingProgressListener.getLastTick();
/*     */   }
/*     */ 
/*     */   private void postData(CustomPeriodFromTicksCreator customPeriodBeforeCreator, CustomPeriodFromTicksCreator customPeriodAfterCreator, Exception exception)
/*     */   {
/* 444 */     CandleData[] consequentCustomPeriodArray = constructConsequentCustomPeriodArray(customPeriodAfterCreator, customPeriodBeforeCreator);
/*     */ 
/* 447 */     if (consequentCustomPeriodArray != null) {
/* 448 */       for (int i = 0; i < consequentCustomPeriodArray.length; i++) {
/* 449 */         CandleData data = consequentCustomPeriodArray[i];
/* 450 */         if (data == null) {
/*     */           continue;
/*     */         }
/* 453 */         getOriginalLiveFeedListener().newCandle(getInstrument(), getDesiredPeriod(), getOfferSide(), data.getTime(), data.getOpen(), data.getClose(), data.getLow(), data.getHigh(), data.getVolume());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 467 */     postFinishDataLoading(customPeriodBeforeCreator, customPeriodAfterCreator, exception, false);
/*     */   }
/*     */ 
/*     */   private void postFinishDataLoading(CustomPeriodFromTicksCreator customPeriodBeforeCreator, CustomPeriodFromTicksCreator customPeriodAfterCreator, Exception exception, boolean loadingProcessStopped)
/*     */   {
/* 480 */     long startTime = getTime();
/* 481 */     if ((customPeriodBeforeCreator != null) && (customPeriodBeforeCreator.getLastTime() != null)) {
/* 482 */       startTime = customPeriodBeforeCreator.getLastTime().longValue();
/*     */     }
/* 484 */     else if (customPeriodBeforeCreator == null) {
/* 485 */       startTime = getFromTime();
/*     */     }
/* 487 */     else if ((customPeriodAfterCreator != null) && (customPeriodAfterCreator.getFirstTime() != null)) {
/* 488 */       startTime = customPeriodAfterCreator.getFirstTime().longValue();
/*     */     }
/*     */ 
/* 491 */     long endTime = getTime();
/* 492 */     if ((customPeriodAfterCreator != null) && (customPeriodAfterCreator.getLastTime() != null)) {
/* 493 */       endTime = customPeriodAfterCreator.getLastTime().longValue();
/*     */     }
/* 495 */     else if ((customPeriodBeforeCreator != null) && (customPeriodBeforeCreator.getFirstTime() != null)) {
/* 496 */       endTime = customPeriodBeforeCreator.getFirstTime().longValue();
/*     */     }
/* 498 */     else if (customPeriodBeforeCreator == null) {
/* 499 */       endTime = getToTime();
/*     */     }
/*     */ 
/* 505 */     getOriginalLoadingProgress().loadingFinished((exception == null) && (!loadingProcessStopped), startTime, endTime, endTime, exception);
/*     */   }
/*     */ 
/*     */   private CandleData[] constructConsequentCustomPeriodArray(CustomPeriodFromTicksCreator customPeriodAfterCreator, CustomPeriodFromTicksCreator customPeriodBeforeCreator)
/*     */   {
/* 520 */     if ((customPeriodAfterCreator != null) && (customPeriodBeforeCreator != null)) {
/* 521 */       CandleData[] result = new CandleData[customPeriodBeforeCreator.getLoadedCandleCount() + customPeriodAfterCreator.getLoadedCandleCount()];
/*     */ 
/* 523 */       CandleData[] afterArray = customPeriodAfterCreator.getResult();
/* 524 */       CandleData[] beforeArray = customPeriodBeforeCreator.getResult();
/*     */ 
/* 529 */       if (customPeriodBeforeCreator.getInverseOrder()) {
/* 530 */         beforeArray = reverse(beforeArray);
/*     */       }
/* 532 */       if (customPeriodAfterCreator.getInverseOrder()) {
/* 533 */         afterArray = reverse(afterArray);
/*     */       }
/*     */ 
/* 536 */       TimeDataUtils.copyArray(beforeArray, 0, result, 0, customPeriodBeforeCreator.getLoadedCandleCount());
/* 537 */       TimeDataUtils.copyArray(afterArray, 0, result, customPeriodBeforeCreator.getLoadedCandleCount(), customPeriodAfterCreator.getLoadedCandleCount());
/*     */ 
/* 539 */       return result;
/*     */     }
/* 541 */     if (customPeriodAfterCreator != null) {
/* 542 */       CandleData[] result = new CandleData[customPeriodAfterCreator.getLoadedCandleCount()];
/* 543 */       CandleData[] afterArray = customPeriodAfterCreator.getResult();
/*     */ 
/* 545 */       if (customPeriodAfterCreator.getInverseOrder())
/*     */       {
/* 549 */         afterArray = reverse(afterArray);
/*     */       }
/*     */ 
/* 552 */       TimeDataUtils.copyArray(afterArray, 0, result, 0, customPeriodAfterCreator.getLoadedCandleCount());
/* 553 */       return result;
/*     */     }
/* 555 */     if (customPeriodBeforeCreator != null) {
/* 556 */       CandleData[] result = new CandleData[customPeriodBeforeCreator.getLoadedCandleCount()];
/*     */ 
/* 558 */       CandleData[] beforeArray = customPeriodBeforeCreator.getResult();
/*     */ 
/* 560 */       if (customPeriodBeforeCreator.getInverseOrder())
/*     */       {
/* 564 */         beforeArray = reverse(beforeArray);
/*     */       }
/*     */ 
/* 567 */       TimeDataUtils.copyArray(beforeArray, 0, result, 0, customPeriodBeforeCreator.getLoadedCandleCount());
/*     */ 
/* 569 */       return result;
/*     */     }
/*     */ 
/* 572 */     return null;
/*     */   }
/*     */ 
/*     */   private CandleData[] reverse(CandleData[] array) {
/* 576 */     CandleData[] reversedArray = new CandleData[array.length];
/* 577 */     TimeDataUtils.reverseArray(array, reversedArray);
/* 578 */     return reversedArray;
/*     */   }
/*     */ 
/*     */   private void loadDataBeforeTime(long time, long firstCandleTime, CustomPeriodTickLiveFeedListener tickLiveFeedListener, TickLoadingProgressListener tickLoadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 588 */     if (tickLiveFeedListener.getCustomPeriodFromTicksCreator().getDesiredCandlesCount() <= 0) {
/* 589 */       return;
/*     */     }
/*     */ 
/* 592 */     long fromTime = time;
/* 593 */     boolean customPeriodsConstructed = false;
/*     */     do
/*     */     {
/* 597 */       if (tickLoadingProgressListener.stopJob())
/*     */       {
/*     */         break;
/*     */       }
/* 601 */       long toTime = fromTime;
/* 602 */       fromTime -= getIncreaseTimeInterval();
/*     */ 
/* 604 */       if ((fromTime > firstCandleTime) && (toTime < firstCandleTime)) {
/* 605 */         toTime = firstCandleTime;
/*     */       }
/*     */ 
/* 608 */       if (fromTime < firstCandleTime)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 615 */       loadDataForTimeInterval(fromTime, toTime, tickLiveFeedListener, tickLoadingProgressListener);
/*     */     }
/* 617 */     while ((!tickLiveFeedListener.isCustomPeriodsCreationFinished()) && 
/* 622 */       (!customPeriodsConstructed));
/*     */   }
/*     */ 
/*     */   private CustomPeriodFromTicksCreator loadDataAfterTime(Instrument instrument, OfferSide offerSide, Filter filter, Period desiredPeriod, int numberOfCandlesAfter, long time, IFilterManager filterManager)
/*     */     throws DataCacheException
/*     */   {
/* 640 */     if (Filter.WEEKENDS.equals(filter))
/*     */     {
/* 644 */       TimeInterval weekend = filterManager.getWeekend(time);
/* 645 */       if (weekend != null) {
/* 646 */         time = weekend.getEnd();
/*     */       }
/*     */     }
/*     */ 
/* 650 */     TickData tick = getLastTickBeforeTime(time, desiredPeriod);
/*     */ 
/* 652 */     Double firstDesiredCandleValue = null;
/* 653 */     if (tick != null) {
/* 654 */       double value = OfferSide.ASK.equals(getOfferSide()) ? tick.getAsk() : tick.getBid();
/*     */ 
/* 658 */       firstDesiredCandleValue = new Double(value);
/*     */     }
/*     */ 
/* 661 */     CustomPeriodFromTicksCreator customPeriodAfterCreator = new CustomPeriodFromTicksCreator(instrument, offerSide, numberOfCandlesAfter, desiredPeriod, filter, false, Long.valueOf(time), firstDesiredCandleValue, filterManager);
/*     */ 
/* 672 */     CustomPeriodTickLiveFeedListener tickAfterLiveFeedListener = new CustomPeriodTickLiveFeedListener(customPeriodAfterCreator);
/* 673 */     TickLoadingProgressListener tickAfterLoadingProgressListener = createTickLoadingProgressListener(getOriginalLoadingProgress());
/*     */ 
/* 675 */     loadDataAfterTime(time, tickAfterLiveFeedListener, tickAfterLoadingProgressListener);
/*     */ 
/* 677 */     return customPeriodAfterCreator;
/*     */   }
/*     */ 
/*     */   private void loadDataAfterTime(long time, CustomPeriodTickLiveFeedListener tickLiveFeedListener, TickLoadingProgressListener tickLoadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 686 */     if (tickLiveFeedListener.getCustomPeriodFromTicksCreator().getDesiredCandlesCount() <= 0) {
/* 687 */       return;
/*     */     }
/*     */ 
/* 690 */     long toTime = time;
/* 691 */     boolean customPeriodsConstructed = false;
/*     */     do
/*     */     {
/* 694 */       if (tickLoadingProgressListener.stopJob())
/*     */       {
/*     */         break;
/*     */       }
/* 698 */       long fromTime = toTime;
/* 699 */       toTime += getIncreaseTimeInterval();
/* 700 */       long currentSystemTime = System.currentTimeMillis();
/*     */ 
/* 702 */       if ((fromTime < currentSystemTime) && (toTime > currentSystemTime)) {
/* 703 */         toTime = currentSystemTime;
/*     */       }
/*     */ 
/* 706 */       if (fromTime > currentSystemTime - 60000L)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 714 */       loadDataForTimeInterval(fromTime, toTime, tickLiveFeedListener, tickLoadingProgressListener);
/*     */     }
/* 716 */     while ((!tickLiveFeedListener.isCustomPeriodsCreationFinished()) && 
/* 721 */       (!customPeriodsConstructed));
/*     */   }
/*     */ 
/*     */   private void loadDataForTimeInterval(long fromTime, long toTime, CustomPeriodTickLiveFeedListener tickLiveFeedListener, TickLoadingProgressListener tickLoadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 733 */     LoadDataAction loadDataAction = createLoadDataAction(fromTime, toTime, tickLiveFeedListener, tickLoadingProgressListener);
/* 734 */     loadDataAction.run();
/*     */ 
/* 736 */     tickLiveFeedListener.analyseTickDataPortion();
/* 737 */     getOriginalLoadingProgress().dataLoaded(fromTime, toTime, toTime, "");
/*     */   }
/*     */ 
/*     */   private LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 747 */     LoadDataAction loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 759 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   private long getIncreaseTimeInterval()
/*     */   {
/* 765 */     return 3600000L;
/*     */   }
/*     */ 
/*     */   private TickLoadingProgressListener createTickLoadingProgressListener(LoadingProgressListener delegate) {
/* 769 */     TickLoadingProgressListener tickLoadingProgressListener = new TickLoadingProgressListener(delegate);
/* 770 */     return tickLoadingProgressListener;
/*     */   }
/*     */ 
/*     */   protected Instrument getInstrument() {
/* 774 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   protected OfferSide getOfferSide() {
/* 778 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   protected Filter getFilter() {
/* 782 */     return this.filter;
/*     */   }
/*     */ 
/*     */   protected Period getDesiredPeriod() {
/* 786 */     return this.desiredPeriod;
/*     */   }
/*     */ 
/*     */   protected int getNumberOfCandlesBefore() {
/* 790 */     return this.numberOfCandlesBefore;
/*     */   }
/*     */ 
/*     */   protected int getNumberOfCandlesAfter() {
/* 794 */     return this.numberOfCandlesAfter;
/*     */   }
/*     */ 
/*     */   protected long getTime() {
/* 798 */     return this.time;
/*     */   }
/*     */ 
/*     */   protected IFeedDataProvider getFeedDataProvider() {
/* 802 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   protected CurvesDataLoader.IntraperiodExistsPolicy getIntraperiodExistsPolicy()
/*     */   {
/* 808 */     return this.intraperiodExistsPolicy;
/*     */   }
/*     */ 
/*     */   protected LoadingProgressListener getOriginalLoadingProgress()
/*     */   {
/* 814 */     return this.originalLoadingProgress;
/*     */   }
/*     */ 
/*     */   protected LiveFeedListener getOriginalLiveFeedListener()
/*     */   {
/* 820 */     return this.originalLiveFeedListener;
/*     */   }
/*     */ 
/*     */   protected long getFromTime() {
/* 824 */     return this.fromTime;
/*     */   }
/*     */ 
/*     */   protected long getToTime() {
/* 828 */     return this.toTime;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.tick.LoadCandlesFromTicksAction
 * JD-Core Version:    0.6.0
 */