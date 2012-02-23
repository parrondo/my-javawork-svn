/*     */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadDataAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadProgressingAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractLoadNumberOfPriceAggregationAction<D extends AbstractPriceAggregationData, SD extends Data, L extends IPriceAggregationLiveFeedListener<D>, C extends IPriceAggregationCreator<D, SD, L>> extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLoadNumberOfPriceAggregationAction.class);
/*     */   private static final int MIN_INACCURACY_INTERVAL_FOR_THE_LAST_BAR_LOADING = 1000;
/*  41 */   protected final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
/*     */   private final Instrument instrument;
/*     */   private final OfferSide offerSide;
/*     */   private final int numberOfPriceRangesBefore;
/*     */   private final long toTime;
/*     */   private final int numberOfPriceRangesAfter;
/*     */   private final L liveFeedListener;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*  51 */   protected final int DEFAULT_EXTRA_BARS_COUNT = 5;
/*     */ 
/*  53 */   protected static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
/*     */ 
/*     */   public AbstractLoadNumberOfPriceAggregationAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, int numberOfPriceRangesBefore, long toTime, int numberOfPriceRangesAfter, L liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  76 */     super(loadingProgressListener);
/*     */ 
/*  55 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/*  78 */     this.feedDataProvider = feedDataProvider;
/*  79 */     this.instrument = instrument;
/*  80 */     this.offerSide = offerSide;
/*     */ 
/*  82 */     this.numberOfPriceRangesBefore = numberOfPriceRangesBefore;
/*  83 */     this.numberOfPriceRangesAfter = numberOfPriceRangesAfter;
/*  84 */     this.liveFeedListener = liveFeedListener;
/*  85 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/*  87 */     this.toTime = toTime;
/*     */   }
/*     */ 
/*     */   protected abstract C createPriceAggregationCreator(long paramLong, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
/*     */ 
/*     */   protected abstract C createFlowPriceAggregationCreator();
/*     */ 
/*     */   protected abstract D[] createArray(int paramInt);
/*     */ 
/*     */   protected abstract AbstractPriceAggregationLiveFeedListener<D, SD, L, C> createLiveFeedListener(C paramC, long paramLong);
/*     */ 
/*     */   protected abstract LoadDataAction createLoadDataAction(long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
/*     */     throws DataCacheException;
/*     */ 
/*     */   protected abstract D getInProgressBar();
/*     */ 
/*     */   protected abstract Period getBarsBasedOnCandlesPeriod();
/*     */ 
/*     */   protected abstract boolean isInProgressBarLoadingNow();
/*     */ 
/*     */   public void run()
/*     */   {
/* 124 */     if (getLoadingProgress().stopJob()) {
/* 125 */       return;
/*     */     }
/*     */ 
/* 128 */     Throwable exception = null;
/* 129 */     IPriceAggregationCreator afterCreator = null;
/* 130 */     IPriceAggregationCreator beforeCreator = null;
/* 131 */     AbstractPriceAggregationData[] consequentPriceRangesArray = null;
/*     */     try
/*     */     {
/* 135 */       if (!inProgressBarExists()) {
/* 136 */         if (getLoadingProgress().stopJob())
/*     */         {
/*     */           return;
/*     */         }
/*     */ 
/* 142 */         throw new IllegalStateException("Unable to retrieve in progress bar for " + toString());
/*     */       }
/*     */ 
/* 152 */       int extraBarsCount = getExtraBarsCount();
/* 153 */       afterCreator = performDirectRangeBarsLoad(getToTime(), getLastKnownTime(), getNumberOfPriceRangesAfter() + extraBarsCount, false);
/*     */ 
/* 155 */       if (getLoadingProgress().stopJob())
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/* 162 */       int numberBeforeToLoad = getNumberOfPriceRangesBefore() + extraBarsCount;
/* 163 */       int afterLoadedDiff = getNumberOfPriceRangesAfter() - afterCreator.getLoadedElementsNumber();
/* 164 */       if (afterLoadedDiff > 0)
/*     */       {
/* 168 */         numberBeforeToLoad += afterLoadedDiff;
/*     */       }
/*     */ 
/* 171 */       beforeCreator = performBackwardRangeBarsLoad(getToTime(), getFirstKnownTime(), numberBeforeToLoad, false);
/*     */ 
/* 178 */       if (getLoadingProgress().stopJob())
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/* 185 */       long fromTime = calculateFromTime(beforeCreator, afterCreator);
/*     */ 
/* 190 */       long toTime = calculateToTime(beforeCreator, afterCreator);
/*     */ 
/* 197 */       long lastKnownTime = getLastKnownTime();
/* 198 */       toTime = toTime > lastKnownTime ? DataCacheUtils.getPreviousPriceAggregationBarStart(lastKnownTime) : toTime;
/*     */ 
/* 200 */       List resultList = performDirectBarsLoadForTimeInterval(fromTime, toTime, false, true);
/* 201 */       consequentPriceRangesArray = (AbstractPriceAggregationData[])resultList.toArray(createArray(resultList.size()));
/*     */ 
/* 206 */       consequentPriceRangesArray = extractRequestedBars(consequentPriceRangesArray);
/*     */ 
/* 208 */       fireDataCreationOneByOneBar(consequentPriceRangesArray);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 212 */       exception = e;
/*     */ 
/* 214 */       if (!getLoadingProgress().stopJob())
/* 215 */         LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 221 */         if (!getLoadingProgress().stopJob()) {
/* 222 */           postData(consequentPriceRangesArray, beforeCreator, afterCreator, exception);
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 234 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int getExtraBarsCount() {
/* 240 */     if (this.instrument.getPipScale() <= 2) {
/* 241 */       return (int)((getNumberOfPriceRangesAfter() + getNumberOfPriceRangesBefore()) * 0.2D);
/*     */     }
/*     */ 
/* 244 */     return 5;
/*     */   }
/*     */ 
/*     */   private void fireDataCreationOneByOneBar(D[] consequentPriceRangesArray)
/*     */   {
/* 256 */     if (consequentPriceRangesArray != null)
/* 257 */       for (AbstractPriceAggregationData data : consequentPriceRangesArray)
/* 258 */         getPriceAggregationLiveFeedListener().newPriceData(data);
/*     */   }
/*     */ 
/*     */   private long calculateToTime(C beforeCreator, C afterCreator)
/*     */   {
/*     */     long toTime;
/*     */     long toTime;
/* 265 */     if (afterCreator.getLastData() != null) {
/* 266 */       toTime = afterCreator.getLastData().getEndTime();
/*     */     }
/*     */     else
/*     */     {
/*     */       long toTime;
/* 268 */       if (beforeCreator.getFirstData() != null) {
/* 269 */         toTime = beforeCreator.getFirstData().getEndTime();
/*     */       }
/*     */       else
/* 272 */         toTime = getLastKnownTime();
/*     */     }
/* 274 */     return toTime;
/*     */   }
/*     */ 
/*     */   private long calculateFromTime(C beforeCreator, C afterCreator)
/*     */   {
/*     */     long fromTime;
/*     */     long fromTime;
/* 279 */     if (beforeCreator.getLastData() != null) {
/* 280 */       fromTime = beforeCreator.getLastData().getTime();
/*     */     }
/*     */     else
/*     */     {
/*     */       long fromTime;
/* 282 */       if (afterCreator.getFirstData() != null) {
/* 283 */         fromTime = afterCreator.getFirstData().getTime();
/*     */       }
/*     */       else
/* 286 */         fromTime = getFirstKnownTime();
/*     */     }
/* 288 */     return fromTime;
/*     */   }
/*     */ 
/*     */   protected boolean inProgressBarExists() {
/* 292 */     AbstractPriceAggregationData inProgressBar = getInProgressBar();
/* 293 */     if (inProgressBar == null) {
/* 294 */       boolean isInProgressBarLoadingNow = isInProgressBarLoadingNow();
/* 295 */       if (!isInProgressBarLoadingNow) {
/* 296 */         LOGGER.warn("There is no in progress bar subscribtion for - " + toString());
/* 297 */         return false;
/*     */       }
/*     */ 
/* 300 */       int tryCount = 0;
/*     */       do {
/* 302 */         if (getLoadingProgress().stopJob()) {
/* 303 */           return false;
/*     */         }
/*     */ 
/* 306 */         isInProgressBarLoadingNow = isInProgressBarLoadingNow();
/*     */ 
/* 308 */         inProgressBar = getInProgressBar();
/* 309 */         tryCount++;
/* 310 */         if (inProgressBar != null) continue;
/*     */         try {
/* 312 */           Thread.sleep(100L);
/*     */         } catch (InterruptedException e) {
/* 314 */           LOGGER.error(e.getLocalizedMessage(), e);
/*     */         }
/*     */       }
/* 317 */       while ((isInProgressBarLoadingNow) && (inProgressBar == null) && (tryCount < 1000));
/*     */ 
/* 319 */       if (inProgressBar == null) {
/* 320 */         LOGGER.warn("Can not wait so much time for in prgress bar is being created for - " + toString());
/* 321 */         return false;
/*     */       }
/*     */ 
/* 324 */       return true;
/*     */     }
/*     */ 
/* 329 */     return true;
/*     */   }
/*     */ 
/*     */   protected D[] extractRequestedBars(D[] target)
/*     */   {
/* 334 */     int desiredBarsCount = getNumberOfPriceRangesBefore() + getNumberOfPriceRangesAfter();
/* 335 */     int toIndex = getNotNullElementLastIndex(target);
/* 336 */     int loadedBarsCount = toIndex + 1;
/*     */ 
/* 338 */     AbstractPriceAggregationData[] result = createArray(Math.min(desiredBarsCount, loadedBarsCount));
/*     */ 
/* 340 */     int fromIndex = toIndex - result.length + 1;
/*     */ 
/* 342 */     int timeIndex = TimeDataUtils.timeIndex(target, getToTime());
/*     */ 
/* 344 */     if (timeIndex >= 0) {
/* 345 */       if (getNumberOfPriceRangesBefore() == 0) {
/* 346 */         if (timeIndex + desiredBarsCount < toIndex) {
/* 347 */           fromIndex = timeIndex;
/* 348 */           toIndex = fromIndex + desiredBarsCount;
/*     */         }
/*     */       }
/*     */       else {
/* 352 */         toIndex = timeIndex + getNumberOfPriceRangesAfter() <= toIndex ? timeIndex + getNumberOfPriceRangesAfter() : toIndex;
/* 353 */         fromIndex = toIndex - result.length + 1;
/* 354 */         fromIndex = fromIndex < 0 ? 0 : fromIndex;
/*     */       }
/*     */     }
/*     */ 
/* 358 */     TimeDataUtils.copyArray(target, fromIndex, result, 0, result.length);
/*     */ 
/* 360 */     return result;
/*     */   }
/*     */ 
/*     */   protected D extractLastBar(D[] target) {
/* 364 */     if (target == null) {
/* 365 */       return null;
/*     */     }
/*     */ 
/* 368 */     int index = getNotNullElementLastIndex(target);
/* 369 */     if (index < 0) {
/* 370 */       return null;
/*     */     }
/*     */ 
/* 373 */     return target[index];
/*     */   }
/*     */ 
/*     */   protected int getNotNullElementLastIndex(Object[] array)
/*     */   {
/* 378 */     if ((array == null) || (array.length <= 0)) {
/* 379 */       return -1;
/*     */     }
/*     */ 
/* 382 */     int first = 0;
/* 383 */     int upto = array.length;
/*     */ 
/* 385 */     while (first < upto) {
/* 386 */       int mid = (first + upto) / 2;
/*     */ 
/* 388 */       int next = mid + 1;
/* 389 */       int previous = mid - 1;
/*     */ 
/* 391 */       Object data = array[mid];
/* 392 */       Object nextData = (next >= 0) && (next < array.length) ? array[next] : null;
/* 393 */       Object previousData = (previous >= 0) && (previous < array.length) ? array[previous] : null;
/*     */ 
/* 395 */       if ((data != null) && (nextData == null)) {
/* 396 */         return mid;
/*     */       }
/* 398 */       if ((previousData != null) && (data == null)) {
/* 399 */         return previous;
/*     */       }
/* 401 */       if (data != null) {
/* 402 */         first = mid + 1;
/*     */       }
/*     */       else {
/* 405 */         upto = mid;
/*     */       }
/*     */     }
/*     */ 
/* 409 */     return -1;
/*     */   }
/*     */ 
/*     */   private void postData(D[] consequentPriceRangesArray, C priceRangeBeforeCreator, C priceRangeAfterCreator, Throwable exception)
/*     */   {
/* 423 */     long startTime = getToTime();
/* 424 */     long endTime = getToTime();
/*     */ 
/* 426 */     if ((consequentPriceRangesArray != null) && (consequentPriceRangesArray.length > 0)) {
/* 427 */       startTime = consequentPriceRangesArray[0].getTime();
/* 428 */       endTime = consequentPriceRangesArray[(consequentPriceRangesArray.length - 1)].getTime();
/*     */     }
/*     */ 
/* 434 */     Exception e = null;
/* 435 */     if (exception != null) {
/* 436 */       if ((exception instanceof Exception)) {
/* 437 */         e = (Exception)exception;
/*     */       }
/*     */       else {
/* 440 */         e = new Exception(exception);
/*     */       }
/*     */     }
/*     */ 
/* 444 */     boolean isAllDataLoaded = false;
/* 445 */     if ((priceRangeAfterCreator != null) && (priceRangeBeforeCreator != null)) {
/* 446 */       isAllDataLoaded = (priceRangeAfterCreator.isAllDesiredDataLoaded()) && (priceRangeBeforeCreator.isAllDesiredDataLoaded());
/*     */     }
/*     */ 
/* 449 */     getLoadingProgress().loadingFinished(isAllDataLoaded, startTime, endTime, getToTime(), e);
/*     */   }
/*     */ 
/*     */   private C performBackwardRangeBarsLoad(long toTime, long firstPossibleTime, int rangeBarsCount, boolean livePriceRangesCreation)
/*     */     throws DataCacheException
/*     */   {
/* 470 */     IPriceAggregationCreator priceRangeBeforeCreator = createPriceAggregationCreator(toTime, rangeBarsCount, livePriceRangesCreation, false);
/*     */ 
/* 477 */     AbstractPriceAggregationLiveFeedListener ticBeforekLiveFeedListener = createLiveFeedListener(priceRangeBeforeCreator, firstPossibleTime);
/* 478 */     DelegatableProgressListener delegatableProgressListener = createDelegatableLoadingProgressListener(getLoadingProgress());
/*     */ 
/* 483 */     loadDataBeforeTime(toTime, getFirstKnownTime(), ticBeforekLiveFeedListener, delegatableProgressListener);
/*     */ 
/* 490 */     return priceRangeBeforeCreator;
/*     */   }
/*     */ 
/*     */   private C performDirectRangeBarsLoad(long startTime, long lastPossibleTime, int rangeBarsCount, boolean livePriceRangesCreation)
/*     */     throws DataCacheException
/*     */   {
/* 505 */     IPriceAggregationCreator priceRangeAfterCreator = createPriceAggregationCreator(startTime, rangeBarsCount, livePriceRangesCreation, true);
/*     */ 
/* 511 */     AbstractPriceAggregationLiveFeedListener ticAfterLiveFeedListener = createLiveFeedListener(priceRangeAfterCreator, lastPossibleTime);
/* 512 */     DelegatableProgressListener delegatableProgressListener = createDelegatableLoadingProgressListener(getLoadingProgress());
/*     */ 
/* 517 */     loadDataAfterTime(startTime, ticAfterLiveFeedListener, delegatableProgressListener);
/*     */ 
/* 519 */     return priceRangeAfterCreator;
/*     */   }
/*     */ 
/*     */   protected List<D> performDirectBarsLoadForTimeInterval(long fromTime, long toTime, boolean livePriceRangesCreation, boolean checkAllDataMustBeLoaded)
/*     */     throws DataCacheException
/*     */   {
/* 533 */     long correctedFromTime = DataCacheUtils.getTradingSessionStart(fromTime);
/* 534 */     correctedFromTime = correctedFromTime < getFirstKnownTime() ? getFirstKnownTime() : correctedFromTime;
/*     */ 
/* 536 */     IPriceAggregationCreator priceRangeCreator = createFlowPriceAggregationCreator();
/*     */ 
/* 538 */     List loadedData = new ArrayList();
/* 539 */     List lastArrivedData = new ArrayList();
/*     */ 
/* 541 */     IPriceAggregationLiveFeedListener listener = createDirectBarsLoadForTimeIntervalListener(fromTime, toTime, livePriceRangesCreation, loadedData, lastArrivedData, getPriceAggregationLiveFeedListener());
/*     */ 
/* 550 */     priceRangeCreator.addListener(listener);
/*     */ 
/* 552 */     AbstractPriceAggregationLiveFeedListener liveFeedListener = createLiveFeedListener(priceRangeCreator, toTime);
/* 553 */     DelegatableProgressListener delegatableProgressListener = createDelegatableLoadingProgressListener(getLoadingProgress());
/*     */ 
/* 556 */     loadDataForInterval(correctedFromTime, toTime, checkAllDataMustBeLoaded, liveFeedListener, delegatableProgressListener);
/*     */ 
/* 568 */     if ((!checkAllDataMustBeLoaded) && (priceRangeCreator.getLastData() != null) && (lastArrivedData.size() > 0) && (!((AbstractPriceAggregationData)lastArrivedData.get(0)).equals(priceRangeCreator.getLastData())))
/*     */     {
/* 574 */       listener.newPriceData(priceRangeCreator.getLastData());
/*     */     }
/*     */ 
/* 577 */     if (livePriceRangesCreation) {
/* 578 */       return null;
/*     */     }
/*     */ 
/* 581 */     return loadedData;
/*     */   }
/*     */ 
/*     */   protected IPriceAggregationLiveFeedListener<D> createDirectBarsLoadForTimeIntervalListener(long fromTime, long toTime, boolean livePriceRangesCreation, List<D> loadedData, List<D> lastArrivedData, L originalPriceAggregationLiveFeedListener)
/*     */   {
/* 594 */     IPriceAggregationLiveFeedListener listener = new IPriceAggregationLiveFeedListener(fromTime, toTime, livePriceRangesCreation, originalPriceAggregationLiveFeedListener, loadedData, lastArrivedData)
/*     */     {
/*     */       public void newPriceData(D data) {
/* 597 */         if ((data.getEndTime() >= this.val$fromTime) && (data.getTime() <= this.val$toTime))
/*     */         {
/* 602 */           if (this.val$livePriceRangesCreation)
/*     */           {
/* 606 */             this.val$originalPriceAggregationLiveFeedListener.newPriceData(data);
/*     */           }
/*     */           else {
/* 609 */             this.val$loadedData.add(data);
/*     */           }
/*     */ 
/* 612 */           if (this.val$lastArrivedData.size() <= 0) {
/* 613 */             this.val$lastArrivedData.add(data);
/*     */           }
/*     */           else
/* 616 */             this.val$lastArrivedData.set(0, data);
/*     */         }
/*     */       }
/*     */     };
/* 622 */     return listener;
/*     */   }
/*     */ 
/*     */   protected void loadDataForInterval(long fromTime, long toTime, boolean checkAllDataMustBeLoaded, AbstractPriceAggregationLiveFeedListener<D, SD, L, C> liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 633 */     if (loadingProgressListener.stopJob()) {
/* 634 */       return;
/*     */     }
/*     */ 
/* 637 */     validateFromToTime(fromTime, toTime);
/*     */ 
/* 639 */     boolean firstIteration = true;
/*     */     do
/*     */     {
/* 642 */       if (loadingProgressListener.stopJob()) {
/* 643 */         return;
/*     */       }
/*     */ 
/* 646 */       toTime = updateToTime(toTime);
/* 647 */       toTime = getCandleStartFast(toTime);
/*     */ 
/* 649 */       if (!firstIteration) {
/* 650 */         fromTime = getNextCandleStartFast(fromTime);
/*     */       }
/*     */ 
/* 653 */       if (fromTime >= toTime)
/*     */       {
/*     */         break;
/*     */       }
/* 657 */       long currentToTime = fromTime + getIncreaseTimeInterval();
/* 658 */       currentToTime = getCandleStartFast(currentToTime);
/*     */ 
/* 660 */       if (currentToTime >= toTime) {
/* 661 */         currentToTime = toTime;
/*     */       }
/*     */ 
/* 664 */       if (fromTime >= currentToTime)
/*     */       {
/*     */         break;
/*     */       }
/* 668 */       loadDataForTimeInterval(fromTime, currentToTime, liveFeedListener, loadingProgressListener);
/*     */ 
/* 672 */       fromTime = currentToTime;
/* 673 */       firstIteration = false;
/*     */ 
/* 675 */       if ((!checkAllDataMustBeLoaded) || (fromTime < toTime) || (fromTime >= getLastKnownTime()) || (liveFeedListener.isPriceDatasCreationFinished()))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 685 */       toTime += getIncreaseTimeInterval();
/*     */     }
/*     */ 
/* 688 */     while ((fromTime < toTime) && (!liveFeedListener.isPriceDatasCreationFinished()));
/*     */   }
/*     */ 
/*     */   protected long updateToTime(long toTime)
/*     */   {
/* 693 */     return toTime;
/*     */   }
/*     */ 
/*     */   private void loadDataAfterTime(long time, AbstractPriceAggregationLiveFeedListener<D, SD, L, C> tickLiveFeedListener, LoadingProgressListener tickLoadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 703 */     if (tickLiveFeedListener.getCreator().getDesiredDatasCount() <= 0) {
/* 704 */       return;
/*     */     }
/*     */ 
/* 707 */     long toTime = time;
/* 708 */     boolean priceRangesConstructed = false;
/*     */     do
/*     */     {
/* 711 */       if (tickLoadingProgressListener.stopJob())
/*     */       {
/*     */         break;
/*     */       }
/* 715 */       long fromTime = toTime;
/* 716 */       toTime += getIncreaseTimeInterval();
/* 717 */       long lastKnownTime = getLastKnownTime();
/*     */ 
/* 719 */       if (toTime > lastKnownTime) {
/* 720 */         toTime = lastKnownTime;
/*     */       }
/*     */ 
/* 723 */       toTime = getCandleStartFast(toTime);
/* 724 */       fromTime = getCandleStartFast(fromTime);
/*     */ 
/* 726 */       if ((fromTime >= toTime) || (Math.abs(fromTime - toTime) <= 1000L))
/*     */       {
/*     */         break;
/*     */       }
/* 730 */       loadDataForTimeInterval(fromTime, toTime, tickLiveFeedListener, tickLoadingProgressListener);
/*     */     }
/* 732 */     while ((!tickLiveFeedListener.isPriceDatasCreationFinished()) && 
/* 737 */       (!priceRangesConstructed));
/*     */   }
/*     */ 
/*     */   private void loadDataBeforeTime(long time, long firstCandleTime, AbstractPriceAggregationLiveFeedListener<D, SD, L, C> tickLiveFeedListener, LoadingProgressListener tickLoadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 748 */     if (tickLiveFeedListener.getCreator().getDesiredDatasCount() <= 0) {
/* 749 */       return;
/*     */     }
/*     */ 
/* 752 */     long fromTime = time;
/* 753 */     boolean priceRangesConstructed = false;
/*     */     do
/*     */     {
/* 757 */       if (tickLoadingProgressListener.stopJob())
/*     */       {
/*     */         break;
/*     */       }
/* 761 */       long toTime = fromTime;
/* 762 */       fromTime -= getIncreaseTimeInterval();
/*     */ 
/* 764 */       if ((fromTime > firstCandleTime) && (toTime < firstCandleTime)) {
/* 765 */         toTime = firstCandleTime;
/*     */       }
/*     */ 
/* 768 */       if (fromTime < firstCandleTime) {
/* 769 */         fromTime = firstCandleTime;
/*     */       }
/*     */ 
/* 772 */       toTime = getCandleStartFast(toTime);
/* 773 */       fromTime = getCandleStartFast(fromTime);
/*     */ 
/* 775 */       if (fromTime >= toTime)
/*     */       {
/*     */         break;
/*     */       }
/* 779 */       loadDataForTimeInterval(fromTime, toTime, tickLiveFeedListener, tickLoadingProgressListener);
/*     */     }
/* 781 */     while ((!tickLiveFeedListener.isPriceDatasCreationFinished()) && 
/* 785 */       (!priceRangesConstructed));
/*     */   }
/*     */ 
/*     */   protected void loadDataForTimeInterval(long fromTime, long toTime, AbstractPriceAggregationLiveFeedListener<D, SD, L, C> liveFeedListener, LoadingProgressListener tickLoadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 795 */     LoadDataAction loadDataAction = createLoadDataAction(fromTime, toTime, liveFeedListener, tickLoadingProgressListener);
/* 796 */     loadDataAction.run();
/*     */ 
/* 798 */     liveFeedListener.analyseCollectedDataPortion();
/* 799 */     getLoadingProgress().dataLoaded(fromTime, toTime, fromTime, "");
/*     */   }
/*     */ 
/*     */   protected long getIncreaseTimeInterval() {
/* 803 */     Period period = getBarsBasedOnCandlesPeriod();
/* 804 */     if (Period.TICK.equals(period)) {
/* 805 */       return 3600000L;
/*     */     }
/*     */ 
/* 808 */     return 1000L * period.getInterval();
/*     */   }
/*     */ 
/*     */   protected long getIntermissionInterval()
/*     */   {
/* 813 */     return 1000L;
/*     */   }
/*     */ 
/*     */   private DelegatableProgressListener createDelegatableLoadingProgressListener(LoadingProgressListener delegate) {
/* 817 */     DelegatableProgressListener tickLoadingProgressListener = new DelegatableProgressListener(delegate);
/* 818 */     return tickLoadingProgressListener;
/*     */   }
/*     */ 
/*     */   protected long getCandleStartFast(long time) {
/* 822 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 823 */       return time;
/*     */     }
/* 825 */     return DataCacheUtils.getCandleStartFast(getBarsBasedOnCandlesPeriod(), time);
/*     */   }
/*     */ 
/*     */   protected long getNextCandleStartFast(long time) {
/* 829 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 830 */       return time;
/*     */     }
/* 832 */     return DataCacheUtils.getNextCandleStartFast(getBarsBasedOnCandlesPeriod(), time);
/*     */   }
/*     */ 
/*     */   protected IFeedDataProvider getFeedDataProvider()
/*     */   {
/* 838 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   protected Instrument getInstrument()
/*     */   {
/* 843 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   protected OfferSide getOfferSide() {
/* 847 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   protected int getNumberOfPriceRangesBefore() {
/* 851 */     return this.numberOfPriceRangesBefore;
/*     */   }
/*     */ 
/*     */   protected long getToTime() {
/* 855 */     return this.toTime;
/*     */   }
/*     */ 
/*     */   protected L getPriceAggregationLiveFeedListener() {
/* 859 */     return this.liveFeedListener;
/*     */   }
/*     */ 
/*     */   protected int getNumberOfPriceRangesAfter() {
/* 863 */     return this.numberOfPriceRangesAfter;
/*     */   }
/*     */ 
/*     */   protected CurvesDataLoader.IntraperiodExistsPolicy getIntraperiodExistsPolicy() {
/* 867 */     return this.intraperiodExistsPolicy;
/*     */   }
/*     */ 
/*     */   protected long getLastKnownTime()
/*     */   {
/* 872 */     AbstractPriceAggregationData bar = getInProgressBar();
/*     */ 
/* 874 */     if (bar == null) {
/* 875 */       LOGGER.warn("Unable to get in progress bar for - " + toString());
/*     */     }
/*     */ 
/* 883 */     long time = bar == null ? -9223372036854775808L : DataCacheUtils.getPreviousPriceAggregationBarStart(bar.getTime());
/*     */ 
/* 885 */     return time;
/*     */   }
/*     */ 
/*     */   protected long getFirstKnownTime() {
/* 889 */     return getFeedDataProvider().getTimeOfFirstCandle(this.instrument, getBarsBasedOnCandlesPeriod());
/*     */   }
/*     */ 
/*     */   protected boolean isFromTimeValid(long time) {
/* 893 */     return time >= getFirstKnownTime();
/*     */   }
/*     */ 
/*     */   protected void validateToTime(long time) {
/* 897 */     long feedDataPoviderTime = this.feedDataProvider.getCurrentTime(getInstrument());
/* 898 */     if ((time > feedDataPoviderTime) && (-9223372036854775808L != feedDataPoviderTime))
/* 899 */       throw new IllegalArgumentException("Passed time " + DATE_FORMAT.format(new Long(time)) + " is later than last tick time " + DATE_FORMAT.format(new Long(feedDataPoviderTime)));
/*     */   }
/*     */ 
/*     */   protected void validateFromTime(long time)
/*     */   {
/* 904 */     if (!isFromTimeValid(time))
/* 905 */       throw new IllegalArgumentException("Passed time " + DATE_FORMAT.format(new Long(time)) + " is earlier than first known time " + DATE_FORMAT.format(new Long(getFirstKnownTime())));
/*     */   }
/*     */ 
/*     */   protected void validateFromToTime(long fromTime, long toTime)
/*     */   {
/* 910 */     if (fromTime > toTime) {
/* 911 */       throw new IllegalArgumentException("From time could not be greater than to time " + DATE_FORMAT.format(new Long(fromTime)) + " > " + DATE_FORMAT.format(new Long(toTime)));
/*     */     }
/* 913 */     long firstKnownTime = getFirstKnownTime();
/* 914 */     if (firstKnownTime > fromTime) {
/* 915 */       throw new IllegalArgumentException("First known time could not be greater than from time " + DATE_FORMAT.format(new Long(firstKnownTime)) + " > " + DATE_FORMAT.format(new Long(fromTime)));
/*     */     }
/* 917 */     long latestKnownTime = getLastKnownTime();
/* 918 */     if (toTime > latestKnownTime)
/* 919 */       throw new IllegalArgumentException("To time could not be greater than latest known time " + DATE_FORMAT.format(new Long(toTime)) + " > " + DATE_FORMAT.format(new Long(latestKnownTime)));
/*     */   }
/*     */ 
/*     */   protected Period getSuitablePeriod(int pipsCount, int reversalAmount)
/*     */   {
/* 924 */     return TimeDataUtils.getSuitablePeriod(pipsCount, reversalAmount);
/*     */   }
/*     */ 
/*     */   protected Period getSuitablePeriod(int pipsCount) {
/* 928 */     return TimeDataUtils.getSuitablePeriod(pipsCount);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 933 */     return getInstrument() + " " + getOfferSide();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadNumberOfPriceAggregationAction
 * JD-Core Version:    0.6.0
 */