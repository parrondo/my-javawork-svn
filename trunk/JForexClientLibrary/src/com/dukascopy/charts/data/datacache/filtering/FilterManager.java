/*     */ package com.dukascopy.charts.data.datacache.filtering;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadDataAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressAdapter;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.listener.SaveCandlesLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.listener.SaveCandlesLoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class FilterManager
/*     */   implements IFilterManager
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(FilterManager.class);
/*     */ 
/*  40 */   private static final Calendar calendarForWeekendsDetection = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*     */ 
/*  42 */   private final WeekendBuffer weekendBuffer = new WeekendBuffer();
/*     */   private final FeedDataProvider feedDataProvider;
/*  44 */   private final Deque<TimeInterval> cachedWeekends = new LinkedList();
/*     */ 
/*     */   public FilterManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  48 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public boolean isWeekendTime(long time, Period period)
/*     */   {
/*  53 */     if (period.getInterval() > Period.DAILY.getInterval())
/*     */     {
/*  58 */       return false;
/*     */     }
/*     */ 
/*  61 */     long periodEnd = time;
/*  62 */     if (Period.TICK.equals(period)) {
/*  63 */       periodEnd = time;
/*     */     }
/*     */     else {
/*  66 */       periodEnd = DataCacheUtils.getNextCandleStartFast(period, time) - 1L;
/*     */     }
/*     */ 
/*  69 */     boolean coversTime = this.weekendBuffer.coversInterval(time, periodEnd);
/*     */ 
/*  71 */     if (!coversTime) {
/*  72 */       fillWeekendsBuffer(Math.min(this.weekendBuffer.getFrom(), time), Math.max(this.weekendBuffer.getTo(), periodEnd));
/*     */     }
/*     */ 
/*  75 */     boolean result = this.weekendBuffer.isWeekendTime(time);
/*     */ 
/*  77 */     if ((result) && (time != periodEnd)) {
/*  78 */       result &= this.weekendBuffer.isWeekendTime(periodEnd);
/*     */     }
/*     */ 
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   private void fillWeekendsBuffer(long from, long to)
/*     */   {
/*  88 */     long ONE_WEEK = 604800000L;
/*  89 */     to += 604800000L;
/*  90 */     from -= 604800000L;
/*     */ 
/*  92 */     boolean coversInterval = this.weekendBuffer.coversInterval(from, to);
/*     */ 
/*  94 */     if (!coversInterval)
/*     */       try {
/*  96 */         List weekends = calculateWeekends(from, to);
/*  97 */         if ((weekends != null) && (!weekends.isEmpty()))
/*  98 */           this.weekendBuffer.set(weekends);
/*     */       }
/*     */       catch (DataCacheException e) {
/* 101 */         LOGGER.error("Unable to calculate weekends " + e.getLocalizedMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   private List<TimeInterval> calculateWeekends(long expectedFrom, long expectedTo)
/*     */     throws DataCacheException
/*     */   {
/* 111 */     return calculateWeekends(Period.TEN_SECS, 1, expectedFrom, expectedTo, new LoadingProgressAdapter()
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   public List<TimeInterval> calculateWeekends(Period period, int expectedNumberOfCandles, long expectedFrom, long expectedTo, LoadingProgressListener loadingProgress)
/*     */     throws DataCacheException
/*     */   {
/* 122 */     Instrument instrument = Instrument.EURUSD;
/* 123 */     List weekends = getApproximateWeekends(expectedFrom, expectedTo);
/*     */     SaveCandlesLoadingProgressListener c10MinProgressListener;
/*     */     SaveCandlesLiveFeedListener c10MinLiveFeedListener;
/* 125 */     if (period.getInterval() < Period.FOUR_HOURS.getInterval()) {
/* 126 */       List weekendsCopy = new ArrayList(weekends);
/*     */       Iterator iterator;
/*     */       TimeInterval weekend;
/*     */       Iterator cachedWeekendIterator;
/* 129 */       synchronized (this.cachedWeekends) {
/* 130 */         for (iterator = weekendsCopy.iterator(); iterator.hasNext(); ) {
/* 131 */           weekend = (TimeInterval)iterator.next();
/* 132 */           for (cachedWeekendIterator = this.cachedWeekends.iterator(); cachedWeekendIterator.hasNext(); ) {
/* 133 */             TimeInterval cachedWeekend = (TimeInterval)cachedWeekendIterator.next();
/* 134 */             if ((cachedWeekend.getStart() <= weekend.getStart()) && (cachedWeekend.getEnd() >= weekend.getEnd())) {
/* 135 */               weekend.setStart(cachedWeekend.getStart());
/* 136 */               weekend.setEnd(cachedWeekend.getEnd());
/* 137 */               iterator.remove();
/*     */ 
/* 140 */               cachedWeekendIterator.remove();
/* 141 */               this.cachedWeekends.addFirst(cachedWeekend);
/* 142 */               break;
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 150 */       c10MinProgressListener = new SaveCandlesLoadingProgressListener(loadingProgress);
/* 151 */       c10MinLiveFeedListener = new SaveCandlesLiveFeedListener();
/* 152 */       for (TimeInterval weekend : weekendsCopy) {
/* 153 */         if (loadingProgress.stopJob()) {
/* 154 */           loadingProgress.loadingFinished(false, 0L, expectedNumberOfCandles, expectedNumberOfCandles, null);
/* 155 */           return null;
/*     */         }
/* 157 */         c10MinLiveFeedListener.getSavedCandles().clear();
/* 158 */         LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, instrument, Period.TEN_MINS, OfferSide.BID, weekend.getStart() - 3000000L, weekend.getStart() - 600000L, c10MinLiveFeedListener, c10MinProgressListener, null, false, this.feedDataProvider.getIntraperiodExistsPolicy(), false);
/*     */ 
/* 172 */         loadDataAction.run();
/* 173 */         if (!c10MinProgressListener.isLoadedSuccessfully()) {
/* 174 */           loadingProgress.loadingFinished(false, 0L, expectedNumberOfCandles, expectedNumberOfCandles, c10MinProgressListener.getException());
/*     */ 
/* 181 */           return null;
/*     */         }
/* 183 */         boolean allFlats = true;
/* 184 */         boolean cache = true;
/* 185 */         if (c10MinLiveFeedListener.getSavedCandles().size() < 5) {
/* 186 */           cache = false;
/* 187 */           allFlats = false;
/*     */         } else {
/* 189 */           for (CandleData candle : c10MinLiveFeedListener.getSavedCandles()) {
/* 190 */             if ((candle.open != candle.close) || (candle.close != candle.low) || (candle.low != candle.high) || (candle.vol != 0.0D)) {
/* 191 */               allFlats = false;
/* 192 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 196 */         if (allFlats) {
/* 197 */           weekend.setStart(weekend.getStart() - 3600000L);
/*     */         }
/*     */ 
/* 200 */         if (loadingProgress.stopJob()) {
/* 201 */           loadingProgress.loadingFinished(false, 0L, expectedNumberOfCandles, expectedNumberOfCandles, null);
/* 202 */           return null;
/*     */         }
/* 204 */         c10MinLiveFeedListener.getSavedCandles().clear();
/* 205 */         loadDataAction = new LoadDataAction(this.feedDataProvider, instrument, Period.TEN_MINS, OfferSide.BID, weekend.getEnd(), weekend.getEnd() + 2400000L, c10MinLiveFeedListener, c10MinProgressListener, null, false, this.feedDataProvider.getIntraperiodExistsPolicy(), false);
/*     */ 
/* 219 */         loadDataAction.run();
/* 220 */         if (!c10MinProgressListener.isLoadedSuccessfully()) {
/* 221 */           loadingProgress.loadingFinished(false, 0L, expectedNumberOfCandles, expectedNumberOfCandles, c10MinProgressListener.getException());
/*     */ 
/* 228 */           return null;
/*     */         }
/* 230 */         allFlats = true;
/* 231 */         if (c10MinLiveFeedListener.getSavedCandles().size() < 5) {
/* 232 */           cache = false;
/* 233 */           allFlats = false;
/*     */         } else {
/* 235 */           for (CandleData candle : c10MinLiveFeedListener.getSavedCandles()) {
/* 236 */             if ((candle.open != candle.close) || (candle.close != candle.low) || (candle.low != candle.high) || (candle.vol != 0.0D)) {
/* 237 */               allFlats = false;
/* 238 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 242 */         if (allFlats) {
/* 243 */           weekend.setEnd(weekend.getEnd() + 3600000L);
/*     */         }
/*     */ 
/* 247 */         if (cache) {
/* 248 */           synchronized (this.cachedWeekends) {
/* 249 */             this.cachedWeekends.addFirst(new TimeInterval(weekend.getStart(), weekend.getEnd()));
/* 250 */             while (this.cachedWeekends.size() > 40) {
/* 251 */               this.cachedWeekends.removeLast();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 258 */     Period calcPeriod = period;
/* 259 */     if (period == Period.TICK) {
/* 260 */       calcPeriod = Period.ONE_SEC;
/*     */     }
/* 262 */     for (Iterator iterator = weekends.iterator(); iterator.hasNext(); ) {
/* 263 */       TimeInterval weekendTimes = (TimeInterval)iterator.next();
/*     */ 
/* 266 */       long weekendStartCandle = DataCacheUtils.getCandleStartFast(calcPeriod, weekendTimes.getStart());
/* 267 */       if (weekendStartCandle < weekendTimes.getStart())
/*     */       {
/* 270 */         weekendStartCandle = DataCacheUtils.getNextCandleStartFast(calcPeriod, weekendStartCandle);
/*     */       }
/*     */       long weekendEndCandle;
/*     */       long weekendEndCandle;
/* 274 */       if ((period == Period.DAILY_SKIP_SUNDAY) || (period == Period.DAILY_SUNDAY_IN_MONDAY))
/*     */       {
/* 276 */         weekendEndCandle = DataCacheUtils.getCandleStartFast(calcPeriod, weekendTimes.getEnd());
/*     */       }
/*     */       else
/*     */       {
/* 281 */         weekendEndCandle = DataCacheUtils.getCandleStartFast(calcPeriod, weekendTimes.getEnd());
/*     */       }
/*     */ 
/* 284 */       if (weekendStartCandle > weekendEndCandle)
/*     */       {
/* 286 */         iterator.remove();
/* 287 */         continue;
/*     */       }
/* 289 */       if (DataCacheUtils.getNextCandleStartFast(calcPeriod, weekendStartCandle) > weekendTimes.getEnd())
/*     */       {
/* 291 */         iterator.remove();
/* 292 */         continue;
/*     */       }
/* 294 */       weekendTimes.setStart(weekendStartCandle);
/* 295 */       weekendTimes.setEnd(weekendEndCandle);
/*     */     }
/* 297 */     return weekends;
/*     */   }
/*     */ 
/*     */   public List<TimeInterval> getApproximateWeekends(long expectedFrom, long expectedTo)
/*     */   {
/* 343 */     List result = new ArrayList();
/*     */ 
/* 345 */     calendarForWeekendsDetection.setFirstDayOfWeek(2);
/* 346 */     calendarForWeekendsDetection.setTimeInMillis(expectedFrom);
/* 347 */     calendarForWeekendsDetection.set(11, 22);
/* 348 */     calendarForWeekendsDetection.set(12, 0);
/* 349 */     calendarForWeekendsDetection.set(13, 0);
/* 350 */     calendarForWeekendsDetection.set(14, 0);
/* 351 */     calendarForWeekendsDetection.set(7, 1);
/* 352 */     if (calendarForWeekendsDetection.getTimeInMillis() < expectedFrom) {
/* 353 */       calendarForWeekendsDetection.add(4, 1);
/*     */     }
/*     */ 
/* 356 */     calendarForWeekendsDetection.set(7, 6);
/* 357 */     calendarForWeekendsDetection.set(11, 21);
/*     */ 
/* 359 */     long time = calendarForWeekendsDetection.getTimeInMillis();
/*     */ 
/* 361 */     long ONE_HOUR = 3600000L;
/* 362 */     long TWO_DAYS = 172800000L;
/* 363 */     long FIVE_DAYS = 432000000L;
/*     */ 
/* 365 */     while (time <= expectedTo)
/*     */     {
/* 367 */       time += 3600000L;
/*     */ 
/* 369 */       TimeInterval weekend = new TimeInterval();
/* 370 */       weekend.setStart(time);
/*     */ 
/* 372 */       time += 172800000L;
/* 373 */       time -= 3600000L;
/*     */ 
/* 375 */       weekend.setEnd(time);
/* 376 */       result.add(weekend);
/*     */ 
/* 378 */       time += 432000000L;
/*     */     }
/*     */ 
/* 381 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isFlat(CandleData candle)
/*     */   {
/* 386 */     boolean result = isFlat(candle.open, candle.close, candle.low, candle.high, candle.vol);
/* 387 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isFlat(double open, double close, double low, double high, double vol)
/*     */   {
/* 392 */     boolean result = (open == close) && (close == high) && (high == low) && (vol <= 0.0D);
/* 393 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean matchedFilter(Period period, Filter filter, CandleData candle)
/*     */   {
/* 398 */     boolean result = matchedFilter(period, filter, candle.time, candle.open, candle.close, candle.low, candle.high, candle.vol);
/* 399 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean matchedFilter(Period period, Filter filter, long time, double open, double close, double low, double high, double vol)
/*     */   {
/* 404 */     if ((filter == null) || (Filter.NO_FILTER.equals(filter))) {
/* 405 */       return true;
/*     */     }
/* 407 */     if (Filter.ALL_FLATS.equals(filter)) {
/* 408 */       boolean result = !isFlat(open, close, low, high, vol);
/* 409 */       return result;
/*     */     }
/* 411 */     if (Filter.WEEKENDS.equals(filter)) {
/* 412 */       boolean result = !isWeekendTime(time, period);
/* 413 */       return result;
/*     */     }
/*     */ 
/* 416 */     throw new IllegalArgumentException("Unsupported filter - " + filter);
/*     */   }
/*     */ 
/*     */   public TimeInterval getWeekend(long time)
/*     */   {
/* 422 */     boolean coversTime = this.weekendBuffer.coversInterval(time, time);
/*     */ 
/* 424 */     if (!coversTime) {
/* 425 */       fillWeekendsBuffer(Math.min(this.weekendBuffer.getFrom(), time), Math.max(this.weekendBuffer.getTo(), time));
/*     */     }
/*     */ 
/* 428 */     TimeInterval weekend = this.weekendBuffer.getWeekend(time);
/*     */ 
/* 430 */     if ((weekend != null) && (weekend.isInIntervalForWeekends(time))) {
/* 431 */       return weekend;
/*     */     }
/*     */ 
/* 434 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.filtering.FilterManager
 * JD-Core Version:    0.6.0
 */