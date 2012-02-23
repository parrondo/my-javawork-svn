/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodCandleLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodFromCandlesCreator;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.tick.LoadCandlesFromTicksAction;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadCustomPeriodNumberOfCandlesAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(LoadCustomPeriodNumberOfCandlesAction.class);
/*     */ 
/*  28 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private final Instrument instrument;
/*     */   private final OfferSide offerSide;
/*     */   private final Filter filter;
/*     */   private final Period desiredPeriod;
/*     */   private final int numberOfCandlesBefore;
/*     */   private final int numberOfCandlesAfter;
/*     */   private final long time;
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private final LiveFeedListener originalLiveFeedListener;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final LoadingProgressListener originalLoadingProgress;
/*     */   private final Period basicPeriod;
/*     */ 
/*     */   public LoadCustomPeriodNumberOfCandlesAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide offerSide, Filter filter, int numberOfCandlesBefore, int numberOfCandlesAfter, long time, LiveFeedListener originalLiveFeedListener, LoadingProgressListener originalLoadingProgress, StackTraceElement[] stackTrace, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  30 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  66 */     this.instrument = instrument;
/*  67 */     this.desiredPeriod = period;
/*  68 */     this.offerSide = offerSide;
/*  69 */     this.filter = filter;
/*  70 */     this.numberOfCandlesBefore = numberOfCandlesBefore;
/*  71 */     this.numberOfCandlesAfter = numberOfCandlesAfter;
/*  72 */     this.time = time;
/*  73 */     this.originalLiveFeedListener = originalLiveFeedListener;
/*  74 */     this.stackTrace = stackTrace;
/*  75 */     this.feedDataProvider = feedDataProvider;
/*  76 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*  77 */     this.originalLoadingProgress = originalLoadingProgress;
/*     */ 
/*  79 */     if (Period.isPeriodBasic(this.desiredPeriod) != null) {
/*  80 */       throw new IllegalArgumentException("Passed period '" + this.desiredPeriod + "' is basic period, " + getClass().getSimpleName() + " can work only with NOT basic periods");
/*     */     }
/*     */ 
/*  83 */     if (!Period.isPeriodCompliant(this.desiredPeriod))
/*     */     {
/*  87 */       throw new IllegalArgumentException("Passed period '" + this.desiredPeriod + "' is not compliant");
/*     */     }
/*     */ 
/*  90 */     this.basicPeriod = Period.getBasicPeriodForCustom(this.desiredPeriod);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  95 */     if (getOriginalLoadingProgress().stopJob()) {
/*  96 */       getOriginalLoadingProgress().loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, null);
/*     */ 
/*  98 */       return;
/*     */     }
/*     */ 
/* 104 */     if (Period.TICK == this.basicPeriod)
/* 105 */       loadCandlesFromTicks();
/*     */     else
/*     */       try
/*     */       {
/* 109 */         loadCandlesFromCandles();
/*     */       } catch (DataCacheException e) {
/* 111 */         LOGGER.error(e.getLocalizedMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void loadCandlesFromTicks()
/*     */   {
/* 118 */     Runnable loadNumberOfCandlesAction = new LoadCandlesFromTicksAction(getFeedDataProvider(), getInstrument(), getOfferSide(), getFilter(), getDesiredPeriod(), getIntraperiodExistsPolicy(), getOriginalLoadingProgress(), getOriginalLiveFeedListener(), getNumberOfCandlesBefore(), getNumberOfCandlesAfter(), getTime());
/*     */ 
/* 131 */     loadNumberOfCandlesAction.run();
/*     */   }
/*     */ 
/*     */   private void loadCandlesFromCandles() throws DataCacheException
/*     */   {
/* 136 */     int beforeNumberOfCandlesForBasicPeriod = getNumberOfCandlesForBasicPeriod(getNumberOfCandlesBefore(), getBasicPeriod(), getDesiredPeriod());
/* 137 */     int afterNumberOfCandlesForBasicPeriod = getNumberOfCandlesForBasicPeriod(getNumberOfCandlesAfter(), getBasicPeriod(), getDesiredPeriod());
/*     */ 
/* 139 */     long time = getTime();
/*     */ 
/* 152 */     if (beforeNumberOfCandlesForBasicPeriod > 0) {
/* 153 */       time = DataCacheUtils.getNextCandleStartFast(getDesiredPeriod(), time) - getBasicPeriod().getInterval();
/*     */     }
/*     */ 
/* 156 */     long afterTime = beforeNumberOfCandlesForBasicPeriod > 0 ? time + getBasicPeriod().getInterval() : time;
/*     */ 
/* 161 */     long firstCandleTime = getFeedDataProvider().getTimeOfFirstCandle(this.instrument, this.basicPeriod);
/* 162 */     long lastCandleTime = getLastCandleTime(this.instrument, this.desiredPeriod);
/*     */ 
/* 167 */     List dataAfterTime = loadDataBeforeOrAfterTime(getFeedDataProvider(), getInstrument(), getBasicPeriod(), getDesiredPeriod(), getOfferSide(), 0, afterNumberOfCandlesForBasicPeriod, 0, getNumberOfCandlesAfter(), afterTime, getFilter(), this.intraperiodExistsPolicy);
/*     */ 
/* 186 */     List dataBeforeTime = loadDataBeforeOrAfterTime(getFeedDataProvider(), getInstrument(), getBasicPeriod(), getDesiredPeriod(), getOfferSide(), beforeNumberOfCandlesForBasicPeriod, 0, getNumberOfCandlesBefore(), 0, time, getFilter(), this.intraperiodExistsPolicy);
/*     */ 
/* 201 */     List result = new ArrayList(dataAfterTime.size() + dataBeforeTime.size());
/* 202 */     result.addAll(dataBeforeTime);
/* 203 */     result.addAll(dataAfterTime);
/*     */ 
/* 205 */     boolean allPossibleDataLoaded = isAllPossibleDataLoaded(time, getNumberOfCandlesBefore(), getNumberOfCandlesAfter(), DataCacheUtils.getCandleStartFast(this.desiredPeriod, firstCandleTime), DataCacheUtils.getCandleStartFast(this.desiredPeriod, lastCandleTime), dataBeforeTime, dataAfterTime);
/*     */ 
/* 215 */     fireDataCreated(result, allPossibleDataLoaded);
/*     */   }
/*     */ 
/*     */   private boolean isAllPossibleDataLoaded(long time, int desiredCandleNumberBefore, int desiredCandleNumberAfter, long firstCandleTime, long lastCandleTime, List<CandleData> dataBeforeTime, List<CandleData> dataAfterTime)
/*     */   {
/* 235 */     if ((dataBeforeTime.size() >= desiredCandleNumberBefore) && (dataAfterTime.size() >= desiredCandleNumberAfter))
/*     */     {
/* 239 */       return true;
/*     */     }
/*     */ 
/* 242 */     int beforeDataSize = dataBeforeTime.size();
/* 243 */     boolean allDataBeforeLoaded = false;
/*     */ 
/* 245 */     if (beforeDataSize >= desiredCandleNumberBefore) {
/* 246 */       allDataBeforeLoaded = true;
/*     */     }
/* 248 */     else if ((beforeDataSize > 0) && (((CandleData)dataBeforeTime.get(0)).getTime() >= firstCandleTime))
/*     */     {
/* 255 */       allDataBeforeLoaded = true;
/*     */     }
/*     */ 
/* 258 */     int afterDataSize = dataAfterTime.size();
/* 259 */     boolean allDataAfterLoaded = false;
/*     */ 
/* 261 */     if (afterDataSize >= desiredCandleNumberAfter) {
/* 262 */       allDataAfterLoaded = true;
/*     */     }
/* 264 */     else if ((afterDataSize > 0) && (((CandleData)dataAfterTime.get(dataAfterTime.size() - 1)).getTime() >= lastCandleTime))
/*     */     {
/* 271 */       allDataAfterLoaded = true;
/*     */     }
/* 273 */     else if ((afterDataSize <= 0) && (desiredCandleNumberBefore > 0) && (desiredCandleNumberAfter > 0) && (time >= lastCandleTime))
/*     */     {
/* 283 */       allDataAfterLoaded = true;
/*     */     }
/* 285 */     return (allDataBeforeLoaded) && (allDataAfterLoaded);
/*     */   }
/*     */ 
/*     */   private void fireDataCreated(List<CandleData> result, boolean allDataLoaded)
/*     */   {
/* 290 */     for (CandleData data : result) {
/* 291 */       getOriginalLiveFeedListener().newCandle(getInstrument(), getDesiredPeriod(), getOfferSide(), data.getTime(), data.getOpen(), data.getClose(), data.getLow(), data.getHigh(), data.getVolume());
/*     */     }
/*     */ 
/* 304 */     getOriginalLoadingProgress().loadingFinished(allDataLoaded, ((CandleData)result.get(0)).getTime(), ((CandleData)result.get(result.size() - 1)).getTime(), ((CandleData)result.get(result.size() - 1)).getTime(), null);
/*     */   }
/*     */ 
/*     */   private List<CandleData> loadDataBeforeOrAfterTime(FeedDataProvider feedDataProvider, Instrument instrument, Period basicPeriod, Period desiredPeriod, OfferSide offerSide, int beforeNumberOfCandlesForBasicPeriod, int afterNumberOfCandlesForBasicPeriod, int beforeNumberOfCandlesForDesiredPeriod, int afterNumberOfCandlesForDesiredPeriod, long time, Filter filter, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */     throws DataCacheException
/*     */   {
/* 330 */     if ((beforeNumberOfCandlesForBasicPeriod != 0) && (afterNumberOfCandlesForBasicPeriod != 0)) {
/* 331 */       throw new IllegalArgumentException("Data can be loaded before time OR after time, not both");
/*     */     }
/*     */ 
/* 334 */     if ((beforeNumberOfCandlesForDesiredPeriod != 0) && (afterNumberOfCandlesForDesiredPeriod != 0)) {
/* 335 */       throw new IllegalArgumentException("Data can be loaded before time OR after time, not both");
/*     */     }
/*     */ 
/* 338 */     int desiredDataSize = beforeNumberOfCandlesForBasicPeriod + afterNumberOfCandlesForBasicPeriod;
/*     */ 
/* 340 */     boolean continueToLoadData = desiredDataSize > 0;
/* 341 */     List result = new ArrayList(desiredDataSize);
/*     */ 
/* 343 */     while (continueToLoadData)
/*     */     {
/* 345 */       CustomPeriodFromCandlesCreator customPeriodFromCandlesCreator = new CustomPeriodFromCandlesCreator(instrument, desiredPeriod, basicPeriod, offerSide);
/*     */ 
/* 352 */       CustomPeriodCandleLiveFeedListener customPeriodCandleLiveFeedListener = new CustomPeriodCandleLiveFeedListener(instrument, offerSide, customPeriodFromCandlesCreator, getOriginalLiveFeedListener(), getFeedDataProvider(), beforeNumberOfCandlesForBasicPeriod, afterNumberOfCandlesForBasicPeriod, time);
/*     */ 
/* 363 */       Runnable loadNumberOfCandlesAction = new LoadNumberOfCandlesAction(feedDataProvider, instrument, basicPeriod, offerSide, beforeNumberOfCandlesForBasicPeriod, afterNumberOfCandlesForBasicPeriod, time, filter, customPeriodCandleLiveFeedListener, new LoadingProgressAdapter()
/*     */       {
/*     */       }
/*     */       , intraperiodExistsPolicy, getStackTrace());
/*     */ 
/* 378 */       loadNumberOfCandlesAction.run();
/*     */ 
/* 380 */       List loadedData = customPeriodCandleLiveFeedListener.getCollectedDatas();
/*     */ 
/* 382 */       continueToLoadData = needToLoadMoreData(loadedData, beforeNumberOfCandlesForDesiredPeriod, afterNumberOfCandlesForDesiredPeriod);
/*     */ 
/* 388 */       if (beforeNumberOfCandlesForBasicPeriod != 0)
/*     */       {
/* 393 */         if (loadedData.isEmpty()) {
/* 394 */           time = DataCacheUtils.getPreviousCandleStartFast(desiredPeriod, time);
/*     */         }
/*     */         else {
/* 397 */           time = DataCacheUtils.getPreviousCandleStartFast(desiredPeriod, ((CandleData)loadedData.get(0)).getTime());
/*     */         }
/*     */ 
/* 400 */         if (continueToLoadData)
/* 401 */           continueToLoadData = canLoadMoreDataBefore(loadedData, feedDataProvider.getTimeOfFirstCandle(instrument, basicPeriod), time);
/*     */         List dataToAdd;
/*     */         List dataToAdd;
/* 409 */         if (beforeNumberOfCandlesForDesiredPeriod < loadedData.size())
/*     */         {
/* 413 */           dataToAdd = loadedData.subList(loadedData.size() - beforeNumberOfCandlesForDesiredPeriod, loadedData.size());
/*     */         }
/*     */         else {
/* 416 */           dataToAdd = loadedData;
/*     */         }
/*     */ 
/* 419 */         if (continueToLoadData) {
/* 420 */           beforeNumberOfCandlesForDesiredPeriod -= loadedData.size();
/* 421 */           beforeNumberOfCandlesForBasicPeriod = getNumberOfCandlesForBasicPeriod(beforeNumberOfCandlesForDesiredPeriod, basicPeriod, desiredPeriod);
/*     */         }
/*     */ 
/* 424 */         result.addAll(0, dataToAdd);
/*     */       }
/*     */       else
/*     */       {
/* 430 */         if (loadedData.isEmpty()) {
/* 431 */           time = DataCacheUtils.getNextCandleStartFast(desiredPeriod, time);
/*     */         }
/*     */         else {
/* 434 */           time = DataCacheUtils.getNextCandleStartFast(desiredPeriod, ((CandleData)loadedData.get(loadedData.size() - 1)).getTime());
/*     */         }
/*     */ 
/* 437 */         if (continueToLoadData) {
/* 438 */           long latestTime = getLastCandleTime(instrument, basicPeriod);
/*     */ 
/* 440 */           continueToLoadData = canLoadMoreDataAfter(loadedData, latestTime, time);
/*     */         }
/*     */         List dataToAdd;
/*     */         List dataToAdd;
/* 448 */         if (afterNumberOfCandlesForDesiredPeriod < loadedData.size())
/*     */         {
/* 452 */           dataToAdd = loadedData.subList(0, afterNumberOfCandlesForDesiredPeriod);
/*     */         }
/*     */         else {
/* 455 */           dataToAdd = loadedData;
/*     */         }
/*     */ 
/* 458 */         if (continueToLoadData) {
/* 459 */           afterNumberOfCandlesForDesiredPeriod -= loadedData.size();
/* 460 */           afterNumberOfCandlesForBasicPeriod = getNumberOfCandlesForBasicPeriod(afterNumberOfCandlesForDesiredPeriod, basicPeriod, desiredPeriod);
/*     */         }
/*     */ 
/* 464 */         result.addAll(dataToAdd);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 469 */     return result;
/*     */   }
/*     */ 
/*     */   private long getLastCandleTime(Instrument instrument, Period period) {
/* 473 */     long lastTickTime = this.feedDataProvider.getLastTickTime(instrument);
/*     */ 
/* 478 */     lastTickTime = lastTickTime < 0L ? System.currentTimeMillis() : lastTickTime;
/*     */ 
/* 480 */     long latestTime = DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, lastTickTime));
/*     */ 
/* 484 */     return latestTime;
/*     */   }
/*     */ 
/*     */   private boolean canLoadMoreDataAfter(List<CandleData> loadedData, long timeOfTheLastCandle, long forTime)
/*     */   {
/* 492 */     if ((loadedData == null) || (loadedData.isEmpty())) {
/* 493 */       return forTime <= timeOfTheLastCandle;
/*     */     }
/*     */ 
/* 496 */     long latestTime = ((CandleData)loadedData.get(loadedData.size() - 1)).getTime();
/* 497 */     boolean value = latestTime < timeOfTheLastCandle;
/*     */ 
/* 499 */     return value;
/*     */   }
/*     */ 
/*     */   private boolean canLoadMoreDataBefore(List<CandleData> loadedData, long firstCandleTime, long forTime)
/*     */   {
/* 507 */     if ((loadedData == null) || (loadedData.isEmpty())) {
/* 508 */       return forTime >= firstCandleTime;
/*     */     }
/*     */ 
/* 511 */     long earliestTime = ((CandleData)loadedData.get(0)).getTime();
/* 512 */     boolean value = earliestTime > firstCandleTime;
/*     */ 
/* 514 */     return value;
/*     */   }
/*     */ 
/*     */   private boolean needToLoadMoreData(List<CandleData> loadedData, int beforeNumberOfCandles, int afterNumberOfCandles)
/*     */   {
/* 522 */     boolean value = (loadedData == null) || (loadedData.size() < beforeNumberOfCandles + afterNumberOfCandles);
/* 523 */     return value;
/*     */   }
/*     */ 
/*     */   private int getNumberOfCandlesForBasicPeriod(int candlesCount, Period basicPeriod, Period desiredPeriod) {
/* 527 */     int result = new Long(candlesCount * desiredPeriod.getInterval() / basicPeriod.getInterval()).intValue();
/*     */ 
/* 529 */     long firstTime = this.feedDataProvider.getTimeOfFirstCandle(getInstrument(), getBasicPeriod());
/* 530 */     long calculatedFirstTime = DataCacheUtils.getTimeForNCandlesBackFast(getBasicPeriod(), getTime(), result);
/*     */ 
/* 532 */     if (calculatedFirstTime < firstTime)
/*     */     {
/* 536 */       result = DataCacheUtils.getCandlesCountBetweenFast(getBasicPeriod(), firstTime, DataCacheUtils.getCandleStartFast(getBasicPeriod(), getFeedDataProvider().getCurrentTime(getInstrument())));
/*     */     }
/*     */ 
/* 543 */     return result;
/*     */   }
/*     */ 
/*     */   private Instrument getInstrument()
/*     */   {
/* 549 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   private OfferSide getOfferSide() {
/* 553 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   private Period getDesiredPeriod() {
/* 557 */     return this.desiredPeriod;
/*     */   }
/*     */ 
/*     */   private long getTime() {
/* 561 */     return this.time;
/*     */   }
/*     */ 
/*     */   private FeedDataProvider getFeedDataProvider() {
/* 565 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   private CurvesDataLoader.IntraperiodExistsPolicy getIntraperiodExistsPolicy() {
/* 569 */     return this.intraperiodExistsPolicy;
/*     */   }
/*     */ 
/*     */   private LiveFeedListener getOriginalLiveFeedListener() {
/* 573 */     return this.originalLiveFeedListener;
/*     */   }
/*     */ 
/*     */   private StackTraceElement[] getStackTrace() {
/* 577 */     return this.stackTrace;
/*     */   }
/*     */ 
/*     */   private Period getBasicPeriod() {
/* 581 */     return this.basicPeriod;
/*     */   }
/*     */ 
/*     */   protected Filter getFilter() {
/* 585 */     return this.filter;
/*     */   }
/*     */ 
/*     */   protected int getNumberOfCandlesBefore() {
/* 589 */     return this.numberOfCandlesBefore;
/*     */   }
/*     */ 
/*     */   protected int getNumberOfCandlesAfter() {
/* 593 */     return this.numberOfCandlesAfter;
/*     */   }
/*     */ 
/*     */   public LoadingProgressListener getOriginalLoadingProgress() {
/* 597 */     return this.originalLoadingProgress;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadCustomPeriodNumberOfCandlesAction
 * JD-Core Version:    0.6.0
 */