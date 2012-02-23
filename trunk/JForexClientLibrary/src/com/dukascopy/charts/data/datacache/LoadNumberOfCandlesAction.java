/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*     */ import com.dukascopy.charts.data.datacache.wrapper.TimeInterval;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadNumberOfCandlesAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */   protected static final SimpleDateFormat DATE_FORMAT;
/*     */   private final Instrument instrument;
/*     */   private final Period period;
/*     */   private final OfferSide side;
/*     */   protected final int numberOfCandlesBefore;
/*     */   protected final int numberOfCandlesAfter;
/*     */   protected int safetyNumberBefore;
/*     */   protected int safetyNumberAfter;
/*     */   protected final long time;
/*     */   private final Filter filter;
/*     */   private final LiveFeedListener listener;
/*     */   private final StackTraceElement[] stackTraceElements;
/*  45 */   private double previousClose = (-1.0D / 0.0D);
/*     */   private final List<CandleData> filteredDataBefore;
/*     */   private CandleData filteredData;
/*     */   private final List<CandleData> filteredDataAfter;
/*  49 */   private int candlesCount = 0;
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private final LoadCustomPeriodNumberOfCandlesAction loadCustomPeriodDataAction;
/*     */ 
/*     */   public LoadNumberOfCandlesAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, int numberOfCandlesBefore, int numberOfCandlesAfter, long time, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, StackTraceElement[] stackTraceElements)
/*     */     throws DataCacheException
/*     */   {
/*  58 */     super(loadingProgress);
/*  59 */     this.instrument = instrument;
/*  60 */     this.period = period;
/*  61 */     this.side = side;
/*  62 */     this.numberOfCandlesBefore = numberOfCandlesBefore;
/*  63 */     this.numberOfCandlesAfter = numberOfCandlesAfter;
/*  64 */     if (filter == Filter.ALL_FLATS) {
/*  65 */       this.safetyNumberBefore = numberOfCandlesBefore;
/*  66 */       if ((this.safetyNumberBefore != 0) && (this.safetyNumberBefore < 30)) {
/*  67 */         this.safetyNumberBefore = 30;
/*     */       }
/*  69 */       this.safetyNumberAfter = numberOfCandlesAfter;
/*  70 */       if ((this.safetyNumberAfter != 0) && (this.safetyNumberAfter < 30)) {
/*  71 */         this.safetyNumberAfter = 30;
/*     */       }
/*     */     }
/*  74 */     this.time = time;
/*  75 */     this.filter = filter;
/*  76 */     this.listener = candleListener;
/*  77 */     this.stackTraceElements = stackTraceElements;
/*  78 */     this.feedDataProvider = feedDataProvider;
/*  79 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/*  82 */     if ((instrument == null) || (period == null) || (loadingProgress == null) || (period == Period.TICK)) {
/*  83 */       throw new DataCacheException("Wrong parameters: instrument=" + instrument + " / period=" + period + " / " + time + " / " + loadingProgress);
/*     */     }
/*     */ 
/*  87 */     if (DataCacheUtils.getCandleStart(period, time) != time) {
/*  88 */       throw new DataCacheException("Time is not valid candle start time for period requested");
/*     */     }
/*     */ 
/*  91 */     if ((numberOfCandlesBefore < 0) || (numberOfCandlesAfter < 0) || ((numberOfCandlesBefore == 0) && (numberOfCandlesAfter == 0))) {
/*  92 */       throw new DataCacheException("Number of candles requested should be positive integer (numberOfCandles > 0)");
/*     */     }
/*     */ 
/*  95 */     this.filteredDataBefore = new ArrayList(numberOfCandlesBefore);
/*  96 */     this.filteredDataAfter = new ArrayList(numberOfCandlesAfter);
/*     */ 
/*  98 */     if (Period.isPeriodBasic(period) == null) {
/*  99 */       this.loadCustomPeriodDataAction = new LoadCustomPeriodNumberOfCandlesAction(feedDataProvider, instrument, period, side, filter, numberOfCandlesBefore, numberOfCandlesAfter, time, this.listener, loadingProgress, stackTraceElements, intraperiodExistsPolicy);
/*     */     }
/*     */     else
/*     */     {
/* 115 */       this.loadCustomPeriodDataAction = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public LoadNumberOfCandlesAction(FeedDataProvider feedDataProvider, Instrument instrument, int numberOfSecondsBefore, int numberOfSecondsAfter, long time, Filter filter, LiveFeedListener tickListener, LoadingProgressListener loadingProgress, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, StackTraceElement[] stackTrace)
/*     */     throws DataCacheException
/*     */   {
/* 122 */     super(loadingProgress);
/* 123 */     this.instrument = instrument;
/* 124 */     this.period = Period.TICK;
/* 125 */     this.side = null;
/* 126 */     this.numberOfCandlesBefore = numberOfSecondsBefore;
/* 127 */     this.numberOfCandlesAfter = numberOfSecondsAfter;
/* 128 */     this.time = time;
/* 129 */     this.filter = filter;
/* 130 */     this.listener = tickListener;
/* 131 */     this.stackTraceElements = stackTrace;
/* 132 */     this.feedDataProvider = feedDataProvider;
/* 133 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/* 135 */     if ((filter != Filter.NO_FILTER) && (filter != Filter.WEEKENDS)) {
/* 136 */       throw new DataCacheException("Cannot apply filter on ticks");
/*     */     }
/*     */ 
/* 139 */     if ((instrument == null) || (loadingProgress == null)) {
/* 140 */       throw new DataCacheException("Wrong parameters: " + instrument + " / " + loadingProgress);
/*     */     }
/*     */ 
/* 143 */     if (DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time) != time) {
/* 144 */       throw new DataCacheException("Time is not valid candle start time for period requested");
/*     */     }
/*     */ 
/* 147 */     if ((this.numberOfCandlesBefore < 0) || (this.numberOfCandlesAfter < 0) || (this.numberOfCandlesBefore + this.numberOfCandlesAfter == 0)) {
/* 148 */       throw new DataCacheException("Number of tick seconds requested should be positive integer (numberOfSeconds > 0)");
/*     */     }
/*     */ 
/* 151 */     this.filteredDataBefore = new ArrayList(this.numberOfCandlesBefore);
/* 152 */     this.filteredDataAfter = new ArrayList(this.numberOfCandlesAfter);
/*     */ 
/* 154 */     this.loadCustomPeriodDataAction = null;
/*     */   }
/*     */ 
/*     */   public void run() {
/* 158 */     if (this.loadingProgress.stopJob()) {
/* 159 */       this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, null);
/*     */ 
/* 161 */       return;
/*     */     }
/*     */ 
/* 164 */     if (Period.isPeriodBasic(this.period) == null) {
/* 165 */       this.loadCustomPeriodDataAction.run();
/*     */     }
/* 168 */     else if ((this.period != Period.TICK) && ((this.filter == Filter.NO_FILTER) || (this.period.getInterval() > Period.DAILY.getInterval()))) {
/* 169 */       long from = DataCacheUtils.getTimeForNCandlesBackFast(this.period, this.time, this.numberOfCandlesBefore == 0 ? 1 : this.numberOfCandlesBefore);
/* 170 */       long to = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, this.time, this.numberOfCandlesAfter == 0 ? 1 : this.numberOfCandlesAfter + 1);
/*     */       try {
/* 172 */         LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, this.instrument, this.period, this.side, from, to, this.listener, this.loadingProgress, this.stackTraceElements, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 174 */         loadDataAction.run();
/*     */       } catch (Exception e) {
/* 176 */         LOGGER.error(e.getMessage(), e);
/* 177 */         this.loadingProgress.loadingFinished(false, from, to, from, e);
/*     */       }
/* 179 */     } else if ((this.period == Period.TICK) && (this.filter == Filter.NO_FILTER))
/*     */     {
/* 182 */       long from = DataCacheUtils.getTimeForNCandlesBackFast(Period.ONE_SEC, this.time, this.numberOfCandlesBefore == 0 ? 1 : this.numberOfCandlesBefore);
/* 183 */       long to = DataCacheUtils.getTimeForNCandlesForwardFast(Period.ONE_SEC, this.time, this.numberOfCandlesAfter == 0 ? 1 : this.numberOfCandlesAfter + 1) + 999L;
/* 184 */       long allignedFrom = DataCacheUtils.getCandleStartFast(Period.ONE_MIN, from);
/*     */       try {
/* 186 */         TicksLiveFeedListener ticksLiveFeedListener = new TicksLiveFeedListener(allignedFrom, from, to, null);
/* 187 */         LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, this.instrument, from, to, ticksLiveFeedListener, new TicksLoadingProgressListener(ticksLiveFeedListener), this.stackTraceElements, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 190 */         loadDataAction.run();
/*     */       } catch (Exception e) {
/* 192 */         LOGGER.error(e.getMessage(), e);
/* 193 */         this.loadingProgress.loadingFinished(false, from, to, from, e);
/*     */       }
/*     */     }
/*     */     else {
/*     */       try {
/* 198 */         int attempts = 0;
/*     */         do {
/* 200 */           List weekends = new ArrayList();
/* 201 */           long[] fromTo = calculateResultingFromTo(weekends);
/* 202 */           if (fromTo == null) {
/* 203 */             return;
/*     */           }
/*     */ 
/* 207 */           if (this.loadingProgress.stopJob()) {
/* 208 */             this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, null);
/*     */ 
/* 210 */             return;
/*     */           }
/*     */ 
/* 213 */           WrappingLoadingProgressListener loadingProgressToUse = new WrappingLoadingProgressListener(null);
/*     */           long lastTickTime;
/*     */           long lastTickTime;
/* 216 */           if (this.period == Period.TICK)
/* 217 */             lastTickTime = this.feedDataProvider.getLastTickTime(this.instrument);
/*     */           else {
/* 219 */             lastTickTime = this.feedDataProvider.getCurrentTime(this.instrument);
/*     */           }
/* 221 */           long correctedFrom = fromTo[0];
/*     */ 
/* 223 */           long timeOfFirstCandle = this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period);
/* 224 */           if (timeOfFirstCandle == 9223372036854775807L)
/*     */           {
/* 226 */             this.loadingProgress.dataLoaded(fromTo[0], fromTo[1], fromTo[1], "Data loaded!");
/* 227 */             this.loadingProgress.loadingFinished(true, fromTo[0], fromTo[1], fromTo[1], null);
/* 228 */             return;
/*     */           }
/*     */           long correctedTo;
/* 230 */           if (this.period == Period.TICK) {
/* 231 */             if (correctedFrom < timeOfFirstCandle) {
/* 232 */               correctedFrom = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, timeOfFirstCandle);
/*     */             }
/* 234 */             long correctedTo = fromTo[1];
/* 235 */             if (lastTickTime > correctedTo) {
/* 236 */               if (lastTickTime < correctedTo + 999L)
/* 237 */                 correctedTo = lastTickTime;
/*     */               else
/* 239 */                 correctedTo += 999L;
/*     */             }
/*     */             else
/* 242 */               correctedTo += 999L;
/*     */           }
/*     */           else {
/* 245 */             correctedTo = fromTo[1];
/* 246 */             if (correctedFrom < timeOfFirstCandle) {
/* 247 */               correctedFrom = DataCacheUtils.getCandleStartFast(this.period, correctedFrom);
/* 248 */               while (correctedFrom < timeOfFirstCandle) {
/* 249 */                 correctedFrom = DataCacheUtils.getNextCandleStartFast(this.period, correctedFrom);
/*     */               }
/*     */             }
/* 252 */             if (lastTickTime != -9223372036854775808L) {
/* 253 */               long lastTickCandleStartTime = DataCacheUtils.getCandleStartFast(this.period, lastTickTime);
/* 254 */               if (correctedTo >= lastTickCandleStartTime) {
/* 255 */                 correctedTo = DataCacheUtils.getPreviousCandleStartFast(this.period, lastTickCandleStartTime);
/*     */               }
/*     */             }
/*     */           }
/* 259 */           if (correctedFrom > correctedTo) {
/* 260 */             this.loadingProgress.dataLoaded(fromTo[0], fromTo[1], fromTo[1], "Data loaded!");
/* 261 */             this.loadingProgress.loadingFinished(true, fromTo[0], fromTo[1], fromTo[1], null);
/* 262 */             return;
/*     */           }
/*     */ 
/* 265 */           if (this.period == Period.TICK) {
/* 266 */             long allignedFrom = DataCacheUtils.getCandleStartFast(Period.ONE_MIN, correctedFrom);
/* 267 */             TicksLiveFeedListener ticksLiveFeedListener = new TicksLiveFeedListener(allignedFrom, correctedFrom, correctedTo, weekends);
/* 268 */             LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, this.instrument, correctedFrom, correctedTo, ticksLiveFeedListener, new TicksLoadingProgressListener(ticksLiveFeedListener), this.stackTraceElements, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 270 */             loadDataAction.run();
/*     */           }
/* 272 */           else if (((this.filter == Filter.WEEKENDS) || (this.filter == Filter.ALL_FLATS)) && (this.period.getInterval() < Period.ONE_HOUR.getInterval()) && (weekends.size() > 0))
/*     */           {
/* 274 */             TimeInterval weekend = null;
/* 275 */             long currentCorrectedFrom = correctedFrom;
/* 276 */             for (Iterator iterator = weekends.iterator(); (iterator.hasNext()) && (currentCorrectedFrom <= correctedTo); ) {
/* 277 */               weekend = (TimeInterval)iterator.next();
/* 278 */               if (currentCorrectedFrom < weekend.getStart()) {
/* 279 */                 long to = Math.min(correctedTo, DataCacheUtils.getPreviousCandleStartFast(this.period, weekend.getStart()));
/* 280 */                 LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, this.instrument, this.period, this.side, currentCorrectedFrom, to, new CandlesLiveFeedListener(weekends), loadingProgressToUse, this.stackTraceElements, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 283 */                 loadDataAction.run();
/*     */ 
/* 285 */                 if ((this.loadingProgress.stopJob()) || (!loadingProgressToUse.allDataLoaded)) {
/* 286 */                   if (this.filter == Filter.ALL_FLATS) {
/* 287 */                     this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, loadingProgressToUse.exception);
/*     */                   }
/*     */                   else {
/* 290 */                     this.loadingProgress.loadingFinished(false, correctedFrom, to, to, loadingProgressToUse.exception);
/*     */                   }
/* 292 */                   return;
/*     */                 }
/*     */               }
/* 295 */               currentCorrectedFrom = Math.max(DataCacheUtils.getNextCandleStartFast(this.period, weekend.getEnd()), correctedFrom);
/* 296 */               this.previousClose = (-1.0D / 0.0D);
/*     */             }
/* 298 */             assert (weekend != null);
/* 299 */             if (correctedTo >= currentCorrectedFrom) {
/* 300 */               LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, this.instrument, this.period, this.side, currentCorrectedFrom, correctedTo, new CandlesLiveFeedListener(weekends), loadingProgressToUse, this.stackTraceElements, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 303 */               loadDataAction.run();
/*     */ 
/* 305 */               if ((this.loadingProgress.stopJob()) || (!loadingProgressToUse.allDataLoaded)) {
/* 306 */                 if (this.filter == Filter.ALL_FLATS)
/* 307 */                   this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, loadingProgressToUse.exception);
/*     */                 else {
/* 309 */                   this.loadingProgress.loadingFinished(false, correctedFrom, correctedTo, correctedTo, loadingProgressToUse.exception);
/*     */                 }
/* 311 */                 return;
/*     */               }
/*     */             }
/*     */           } else {
/* 315 */             LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, this.instrument, this.period, this.side, correctedFrom, correctedTo, new CandlesLiveFeedListener(weekends), loadingProgressToUse, this.stackTraceElements, false, this.intraperiodExistsPolicy, false);
/*     */ 
/* 318 */             loadDataAction.run();
/*     */           }
/*     */ 
/* 321 */           int filteredDataBeforeCount = this.filteredDataBefore.size() + (this.filteredData == null ? 0 : 1);
/* 322 */           int filteredDataAfterCount = this.filteredDataAfter.size() + ((this.filteredData == null) || (this.numberOfCandlesBefore > 0) ? 0 : 1);
/* 323 */           if (this.filter != Filter.ALL_FLATS) {
/* 324 */             if ((this.loadingProgress.stopJob()) || (!loadingProgressToUse.allDataLoaded))
/* 325 */               this.loadingProgress.loadingFinished(false, correctedFrom, correctedTo, correctedFrom, loadingProgressToUse.exception);
/*     */             else {
/* 327 */               this.loadingProgress.loadingFinished(true, correctedFrom, correctedTo, correctedTo, loadingProgressToUse.exception);
/*     */             }
/* 329 */             return;
/* 330 */           }if ((filteredDataBeforeCount < this.numberOfCandlesBefore) && (correctedFrom > timeOfFirstCandle))
/*     */           {
/* 332 */             if ((this.loadingProgress.stopJob()) || (!loadingProgressToUse.allDataLoaded)) {
/* 333 */               this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, loadingProgressToUse.exception);
/*     */ 
/* 335 */               return;
/*     */             }
/* 337 */             double percent = filteredDataBeforeCount / this.candlesCount;
/* 338 */             if (percent > 0.9D)
/* 339 */               percent = 0.9D;
/* 340 */             else if (percent < 0.3D) {
/* 341 */               percent = 0.3D;
/*     */             }
/* 343 */             int numberOfCandlesToAdd = (int)((this.numberOfCandlesBefore - filteredDataBeforeCount) / percent);
/*     */ 
/* 346 */             if (numberOfCandlesToAdd < (attempts + 1) * (432 - (20 - attempts) * 19)) {
/* 347 */               numberOfCandlesToAdd = (attempts + 1) * (432 - (20 - attempts) * 19);
/*     */             }
/* 349 */             this.safetyNumberBefore += numberOfCandlesToAdd;
/* 350 */             if (LOGGER.isDebugEnabled()) {
/* 351 */               DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
/* 352 */               format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 353 */               LOGGER.debug("Attempt to load candles while filtering all flats missed. [" + this.numberOfCandlesBefore + "] candles was requested before time [" + format.format(new Date(this.time)) + "]" + ". Attempt to load was from [" + format.format(new Date(correctedFrom)) + "] to [" + format.format(new Date(correctedTo)) + "] with [" + weekends.size() + "] weekends in between, but only [" + filteredDataBeforeCount + "] candles was loaded. Doing new attempt by adding [" + numberOfCandlesToAdd + "] candles to before number, total number of requested candles will be [" + this.safetyNumberBefore + "] before and [" + this.safetyNumberAfter + "] after");
/*     */             }
/*     */ 
/* 361 */             this.filteredDataBefore.clear();
/* 362 */             this.filteredDataAfter.clear();
/* 363 */             this.filteredData = null;
/* 364 */             this.candlesCount = 0;
/* 365 */             this.previousClose = (-1.0D / 0.0D);
/* 366 */             attempts++;
/* 367 */           } else if ((filteredDataAfterCount < this.numberOfCandlesAfter) && ((this.period != Period.TICK) || (correctedTo != lastTickTime)) && (correctedTo != DataCacheUtils.getPreviousCandleStartFast(this.period, DataCacheUtils.getCandleStartFast(this.period, lastTickTime))))
/*     */           {
/* 369 */             if ((this.loadingProgress.stopJob()) || (!loadingProgressToUse.allDataLoaded)) {
/* 370 */               this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, loadingProgressToUse.exception);
/*     */ 
/* 372 */               return;
/*     */             }
/* 374 */             double percent = filteredDataAfterCount / this.candlesCount;
/* 375 */             if (percent > 0.9D)
/* 376 */               percent = 0.9D;
/* 377 */             else if (percent < 0.3D) {
/* 378 */               percent = 0.3D;
/*     */             }
/* 380 */             int numberOfCandlesToAdd = (int)((this.numberOfCandlesAfter - filteredDataAfterCount) / percent);
/*     */ 
/* 383 */             if (numberOfCandlesToAdd < (attempts + 1) * (432 - (20 - attempts) * 19)) {
/* 384 */               numberOfCandlesToAdd = (attempts + 1) * (432 - (20 - attempts) * 19);
/*     */             }
/* 386 */             this.safetyNumberAfter += numberOfCandlesToAdd;
/* 387 */             if (LOGGER.isDebugEnabled()) {
/* 388 */               DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
/* 389 */               format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 390 */               LOGGER.debug("Attempt to load candles while filtering all flats missed. [" + this.numberOfCandlesAfter + "] candles was requested after time [" + format.format(new Date(this.time)) + "]" + ". Attempt to load was from [" + format.format(new Date(correctedFrom)) + "] to [" + format.format(new Date(correctedTo)) + "] with [" + weekends.size() + "] weekends in between, but only [" + filteredDataAfterCount + "] candles was loaded. Doing new attempt by adding [" + numberOfCandlesToAdd + "] candles to after number, total number of requested candles will be [" + this.safetyNumberBefore + "] before and [" + this.safetyNumberAfter + "] after");
/*     */             }
/*     */ 
/* 398 */             this.filteredDataBefore.clear();
/* 399 */             this.filteredDataAfter.clear();
/* 400 */             this.filteredData = null;
/* 401 */             this.candlesCount = 0;
/* 402 */             this.previousClose = (-1.0D / 0.0D);
/* 403 */             attempts++; } else {
/* 404 */             if (this.listener != null) {
/* 405 */               if (!loadingProgressToUse.allDataLoaded) {
/* 406 */                 this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, loadingProgressToUse.exception);
/*     */ 
/* 408 */                 return;
/*     */               }
/*     */               List toSend;
/*     */               List toSend;
/* 411 */               if (this.numberOfCandlesBefore > 0)
/*     */               {
/*     */                 List toSend;
/* 412 */                 if (this.filteredDataBefore.size() > this.numberOfCandlesBefore)
/* 413 */                   toSend = this.filteredDataBefore.subList(this.filteredDataBefore.size() - (this.numberOfCandlesBefore - (this.filteredData == null ? 0 : 1)), this.filteredDataBefore.size());
/*     */                 else
/* 415 */                   toSend = this.filteredDataBefore;
/*     */               }
/*     */               else {
/* 418 */                 toSend = new ArrayList(this.numberOfCandlesAfter);
/*     */               }
/* 420 */               if (this.filteredData != null) {
/* 421 */                 toSend.add(this.filteredData);
/*     */               }
/* 423 */               if (this.numberOfCandlesAfter > 0) {
/* 424 */                 if (this.filteredDataAfter.size() >= this.numberOfCandlesAfter)
/* 425 */                   toSend.addAll(this.filteredDataAfter.subList(0, this.numberOfCandlesAfter - ((this.filteredData == null) || (this.numberOfCandlesBefore > 0) ? 0 : 1)));
/*     */                 else {
/* 427 */                   toSend.addAll(this.filteredDataAfter);
/*     */                 }
/*     */               }
/*     */ 
/* 431 */               for (CandleData data : toSend) {
/* 432 */                 this.listener.newCandle(this.instrument, this.period, this.side, data.time, data.open, data.close, data.low, data.high, data.vol);
/*     */               }
/* 434 */               if (this.loadingProgress.stopJob()) {
/* 435 */                 this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, null);
/*     */ 
/* 437 */                 return;
/*     */               }
/* 439 */               this.loadingProgress.loadingFinished(true, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, null);
/* 440 */               return;
/*     */             }
/* 442 */             if ((this.loadingProgress.stopJob()) || (!loadingProgressToUse.allDataLoaded)) {
/* 443 */               this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, loadingProgressToUse.exception);
/*     */ 
/* 445 */               return;
/*     */             }
/* 447 */             this.loadingProgress.loadingFinished(true, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, null);
/* 448 */             return;
/*     */           }
/*     */         }
/* 450 */         while (attempts < 22);
/*     */ 
/* 452 */         this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, this.numberOfCandlesBefore + this.numberOfCandlesAfter, new Exception("Attempt to load candles while filtering all flats missed"));
/*     */       }
/*     */       catch (Exception e) {
/* 455 */         LOGGER.error(e.getMessage(), e);
/* 456 */         this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, 0L, e);
/*     */       } catch (Throwable t) {
/* 458 */         LOGGER.error(t.getMessage(), t);
/* 459 */         this.loadingProgress.loadingFinished(false, 0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, 0L, null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private long[] calculateResultingFromTo(List<TimeInterval> weekends)
/*     */     throws DataCacheException
/*     */   {
/*     */     long expectedTo;
/*     */     long expectedFrom;
/*     */     long expectedTo;
/* 468 */     if (this.period == Period.TICK) {
/* 469 */       long expectedFrom = this.time - ((this.numberOfCandlesBefore == 0 ? 1 : this.numberOfCandlesBefore) - 1) * Period.ONE_SEC.getInterval();
/* 470 */       expectedTo = this.time + ((this.numberOfCandlesAfter == 0 ? 1 : this.numberOfCandlesAfter) - 1) * Period.ONE_SEC.getInterval();
/*     */     } else {
/* 472 */       expectedFrom = DataCacheUtils.getTimeForNCandlesBackFast(this.period, this.time, (this.numberOfCandlesBefore == 0 ? 1 : this.numberOfCandlesBefore) + this.safetyNumberBefore);
/* 473 */       expectedTo = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, this.time, (this.numberOfCandlesAfter == 0 ? 1 : this.numberOfCandlesAfter) + this.safetyNumberAfter);
/* 474 */       if ((this.numberOfCandlesBefore == 0) && (this.filter == Filter.ALL_FLATS)) {
/* 475 */         expectedFrom = DataCacheUtils.getPreviousCandleStartFast(this.period, this.time);
/*     */       }
/*     */     }
/*     */ 
/* 479 */     int numberOfDays = DataCacheUtils.getCandlesCountBetween(Period.DAILY, DataCacheUtils.getCandleStart(Period.DAILY, expectedFrom), DataCacheUtils.getCandleStart(Period.DAILY, expectedTo));
/*     */ 
/* 481 */     expectedFrom -= numberOfDays / 7 * Period.DAILY.getInterval();
/* 482 */     expectedTo += numberOfDays / 7 * Period.DAILY.getInterval();
/* 483 */     if (this.period == Period.TICK)
/* 484 */       expectedTo += Period.ONE_SEC.getInterval();
/*     */     else {
/* 486 */       expectedTo = DataCacheUtils.getNextCandleStartFast(this.period, expectedTo);
/*     */     }
/*     */ 
/* 489 */     this.loadingProgress.dataLoaded(0L, this.numberOfCandlesBefore + this.numberOfCandlesAfter, 0L, "Calculating weekends start and end times...");
/* 490 */     long from = (this.period != Period.TICK) && (this.numberOfCandlesBefore == 0) && (this.filter == Filter.ALL_FLATS) ? DataCacheUtils.getPreviousCandleStartFast(this.period, this.time) : this.time;
/*     */ 
/* 492 */     long to = this.time;
/*     */     while (true)
/*     */     {
/* 495 */       List calculatedWeekends = this.feedDataProvider.getFilterManager().calculateWeekends(this.period, this.numberOfCandlesBefore + this.numberOfCandlesAfter, expectedFrom, expectedTo, this.loadingProgress);
/*     */ 
/* 503 */       if (calculatedWeekends == null) {
/* 504 */         return null;
/*     */       }
/*     */ 
/* 507 */       weekends.clear();
/* 508 */       weekends.addAll(calculatedWeekends);
/*     */ 
/* 512 */       if (this.numberOfCandlesBefore > 0) {
/* 513 */         from = calculateFromTo(weekends, this.time, false, this.numberOfCandlesBefore, this.safetyNumberBefore);
/*     */       }
/* 515 */       if (this.numberOfCandlesAfter > 0) {
/* 516 */         to = calculateFromTo(weekends, this.time, true, this.numberOfCandlesAfter, this.safetyNumberAfter);
/*     */       }
/*     */ 
/* 521 */       if ((expectedTo >= to) && (expectedFrom <= from)) break;
/* 522 */       if (expectedTo < to)
/*     */       {
/* 525 */         if (expectedTo >= this.feedDataProvider.getCurrentTime() + 60000L)
/*     */         {
/*     */           break;
/*     */         }
/* 529 */         expectedTo = to + 259200000L;
/*     */       }
/* 531 */       if (expectedFrom > from)
/*     */       {
/* 534 */         if (expectedFrom <= this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period))
/*     */         {
/*     */           break;
/*     */         }
/* 538 */         expectedFrom = from - 259200000L;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 545 */     return new long[] { from, to };
/*     */   }
/*     */ 
/*     */   long calculateFromTo(List<TimeInterval> weekends, long fromTo, boolean isAfter, int numberOfCandles, int safetyNumber)
/*     */   {
/* 551 */     Period calcPeriod = this.period;
/*     */     long from;
/*     */     long to;
/* 552 */     if (this.period == Period.TICK)
/*     */     {
/*     */       long to;
/*     */       long to;
/* 553 */       if (isAfter) {
/* 554 */         long from = fromTo;
/* 555 */         to = fromTo + (numberOfCandles - 1) * 1000;
/*     */       } else {
/* 557 */         long from = fromTo - (numberOfCandles - 1) * 1000;
/* 558 */         to = fromTo;
/*     */       }
/* 560 */       calcPeriod = Period.ONE_SEC;
/*     */     }
/*     */     else
/*     */     {
/*     */       long to;
/* 562 */       if (isAfter) {
/* 563 */         long from = fromTo;
/* 564 */         to = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, fromTo, numberOfCandles + safetyNumber);
/*     */       } else {
/* 566 */         from = DataCacheUtils.getTimeForNCandlesBackFast(this.period, fromTo, numberOfCandles + safetyNumber);
/* 567 */         to = fromTo;
/*     */       }
/*     */     }
/* 570 */     if ((isAfter) && 
/* 571 */       (this.numberOfCandlesBefore > 0) && (this.numberOfCandlesAfter > 0))
/*     */     {
/* 573 */       boolean timeInWeekend = false;
/* 574 */       if (this.filter == Filter.WEEKENDS) {
/* 575 */         for (TimeInterval weekend : weekends) {
/* 576 */           if ((this.time >= weekend.getStart()) && (this.time <= weekend.getEnd())) {
/* 577 */             timeInWeekend = true;
/* 578 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 582 */       if (!timeInWeekend) {
/* 583 */         if (this.period == Period.TICK)
/* 584 */           to += Period.ONE_SEC.getInterval();
/*     */         else {
/* 586 */           to = DataCacheUtils.getNextCandleStartFast(this.period, to);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 591 */     ListIterator iterator = weekends.listIterator(isAfter ? 0 : weekends.size());
/* 592 */     while (((isAfter) && (iterator.hasNext())) || ((!isAfter) && (iterator.hasPrevious())))
/*     */     {
/*     */       TimeInterval weekendTimes;
/*     */       TimeInterval weekendTimes;
/* 594 */       if (isAfter)
/* 595 */         weekendTimes = (TimeInterval)iterator.next();
/*     */       else {
/* 597 */         weekendTimes = (TimeInterval)iterator.previous();
/*     */       }
/*     */ 
/* 600 */       if (isAfter) {
/* 601 */         if ((weekendTimes.getStart() <= to) && (weekendTimes.getEnd() >= from))
/*     */         {
/* 603 */           if ((to > weekendTimes.getEnd()) && (from <= weekendTimes.getEnd()) && (from >= weekendTimes.getStart()))
/*     */           {
/* 605 */             to = DataCacheUtils.getTimeForNCandlesForwardFast(calcPeriod, to, DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, from, weekendTimes.getEnd()) + 1);
/*     */           }
/* 607 */           else if ((to >= weekendTimes.getEnd()) && (from < weekendTimes.getStart()))
/*     */           {
/* 609 */             to = DataCacheUtils.getTimeForNCandlesForwardFast(calcPeriod, to, DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, weekendTimes.getStart(), weekendTimes.getEnd()) + 1);
/*     */           }
/* 611 */           else if ((from >= weekendTimes.getStart()) && (to <= weekendTimes.getEnd()))
/*     */           {
/* 613 */             to = DataCacheUtils.getTimeForNCandlesForwardFast(calcPeriod, weekendTimes.getEnd(), DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, from, to) + 1);
/*     */           }
/* 615 */           else if ((to < weekendTimes.getEnd()) && (from <= weekendTimes.getStart()))
/*     */           {
/* 617 */             to = DataCacheUtils.getTimeForNCandlesForwardFast(calcPeriod, weekendTimes.getEnd(), DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, weekendTimes.getStart(), to) + 1);
/*     */           }
/*     */         }
/*     */       }
/* 621 */       else if ((weekendTimes.getStart() <= to) && (weekendTimes.getEnd() >= from))
/*     */       {
/* 623 */         if ((from < weekendTimes.getStart()) && (to >= weekendTimes.getStart()) && (to <= weekendTimes.getEnd()))
/*     */         {
/* 625 */           from = DataCacheUtils.getTimeForNCandlesBackFast(calcPeriod, from, DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, weekendTimes.getStart(), to) + 1);
/*     */         }
/* 627 */         else if ((from <= weekendTimes.getStart()) && (to > weekendTimes.getEnd()))
/*     */         {
/* 629 */           from = DataCacheUtils.getTimeForNCandlesBackFast(calcPeriod, from, DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, weekendTimes.getStart(), weekendTimes.getEnd()) + 1);
/*     */         }
/* 631 */         else if ((from >= weekendTimes.getStart()) && (to <= weekendTimes.getEnd()))
/*     */         {
/* 633 */           from = DataCacheUtils.getTimeForNCandlesBackFast(calcPeriod, weekendTimes.getStart(), DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, from, to) + 1);
/*     */         }
/* 635 */         else if ((from > weekendTimes.getStart()) && (to >= weekendTimes.getEnd()))
/*     */         {
/* 637 */           from = DataCacheUtils.getTimeForNCandlesBackFast(calcPeriod, weekendTimes.getStart(), DataCacheUtils.getCandlesCountBetweenFast(calcPeriod, from, weekendTimes.getEnd()) + 1);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 642 */     if (isAfter) {
/* 643 */       return to;
/*     */     }
/* 645 */     return from;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  27 */     LOGGER = LoggerFactory.getLogger(LoadNumberOfCandlesAction.class);
/*     */ 
/*  29 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*     */ 
/*  31 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ 
/*     */   private class TicksLoadingProgressListener
/*     */     implements LoadingProgressListener
/*     */   {
/*     */     private final LoadNumberOfCandlesAction.TicksLiveFeedListener ticksLiveFeedListener;
/*     */ 
/*     */     public TicksLoadingProgressListener(LoadNumberOfCandlesAction.TicksLiveFeedListener ticksLiveFeedListener)
/*     */     {
/* 948 */       this.ticksLiveFeedListener = ticksLiveFeedListener;
/*     */     }
/*     */ 
/*     */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */     {
/* 953 */       LoadNumberOfCandlesAction.this.loadingProgress.dataLoaded(startTime, endTime, currentTime, information);
/*     */     }
/*     */ 
/*     */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*     */     {
/* 958 */       if (allDataLoaded)
/*     */       {
/* 960 */         this.ticksLiveFeedListener.theEnd();
/*     */       }
/* 962 */       LoadNumberOfCandlesAction.this.loadingProgress.loadingFinished(allDataLoaded, startTime, endTime, currentTime, e);
/*     */     }
/*     */ 
/*     */     public boolean stopJob()
/*     */     {
/* 967 */       return LoadNumberOfCandlesAction.this.loadingProgress.stopJob();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SaveCandlesLoadingProgressListener
/*     */     implements LoadingProgressListener
/*     */   {
/* 929 */     public boolean loadedSuccessfully = false;
/*     */     public Exception exception;
/*     */ 
/*     */     private SaveCandlesLoadingProgressListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*     */     {
/* 935 */       this.loadedSuccessfully = true;
/* 936 */       this.exception = e;
/*     */     }
/*     */ 
/*     */     public boolean stopJob() {
/* 940 */       return LoadNumberOfCandlesAction.this.loadingProgress.stopJob();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SaveCandlesLiveFeedListener
/*     */     implements LiveFeedListener
/*     */   {
/* 917 */     public List<CandleData> savedCandles = new ArrayList(5);
/*     */ 
/*     */     public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */     {
/* 921 */       this.savedCandles.add(new CandleData(time, open, close, low, high, vol));
/*     */     }
/*     */ 
/*     */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class WrappingLoadingProgressListener
/*     */     implements LoadingProgressListener
/*     */   {
/* 897 */     private boolean allDataLoaded = false;
/*     */     private Exception exception;
/*     */ 
/*     */     private WrappingLoadingProgressListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */     {
/* 901 */       if (LoadNumberOfCandlesAction.this.filter != Filter.ALL_FLATS)
/* 902 */         LoadNumberOfCandlesAction.this.loadingProgress.dataLoaded(startTime, endTime, currentTime, information);
/*     */     }
/*     */ 
/*     */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*     */     {
/* 907 */       this.allDataLoaded = allDataLoaded;
/* 908 */       this.exception = e;
/*     */     }
/*     */ 
/*     */     public boolean stopJob() {
/* 912 */       return LoadNumberOfCandlesAction.this.loadingProgress.stopJob();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CandlesLiveFeedListener
/*     */     implements LiveFeedListener
/*     */   {
/*     */     private Iterator<TimeInterval> weekendsIterator;
/*     */     private TimeInterval weekend;
/*     */ 
/*     */     public CandlesLiveFeedListener()
/*     */     {
/* 852 */       this.weekendsIterator = weekends.iterator();
/*     */     }
/*     */ 
/*     */     public void newCandle(Instrument instrument, Period period, OfferSide side, long candleTime, double open, double close, double low, double high, double vol)
/*     */     {
/* 857 */       if ((this.weekendsIterator != null) && ((this.weekend == null) || (this.weekend.getEnd() < candleTime))) {
/* 858 */         if (this.weekendsIterator.hasNext()) {
/* 859 */           this.weekend = ((TimeInterval)this.weekendsIterator.next());
/*     */         } else {
/* 861 */           this.weekendsIterator = null;
/* 862 */           this.weekend = null;
/*     */         }
/*     */       }
/* 865 */       if ((this.weekend == null) || (candleTime < this.weekend.getStart()) || (candleTime > this.weekend.getEnd())) {
/* 866 */         if (LoadNumberOfCandlesAction.this.listener != null) {
/* 867 */           if (LoadNumberOfCandlesAction.this.filter == Filter.ALL_FLATS) {
/* 868 */             if (((LoadNumberOfCandlesAction.this.numberOfCandlesBefore > 0) || ((LoadNumberOfCandlesAction.this.numberOfCandlesBefore == 0) && (candleTime >= LoadNumberOfCandlesAction.this.time))) && (
/* 869 */               ((LoadNumberOfCandlesAction.this.previousClose != (-1.0D / 0.0D)) && (open != LoadNumberOfCandlesAction.this.previousClose)) || (open != close) || (close != low) || (low != high)))
/*     */             {
/* 871 */               CandleData candle = new CandleData(candleTime, open, close, low, high, vol);
/* 872 */               if (candleTime < LoadNumberOfCandlesAction.this.time)
/* 873 */                 LoadNumberOfCandlesAction.this.filteredDataBefore.add(candle);
/* 874 */               else if (candleTime == LoadNumberOfCandlesAction.this.time)
/* 875 */                 LoadNumberOfCandlesAction.access$1402(LoadNumberOfCandlesAction.this, candle);
/*     */               else {
/* 877 */                 LoadNumberOfCandlesAction.this.filteredDataAfter.add(candle);
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 882 */             LoadNumberOfCandlesAction.this.listener.newCandle(instrument, period, side, candleTime, open, close, low, high, vol);
/*     */           }
/*     */         }
/* 885 */         LoadNumberOfCandlesAction.access$1604(LoadNumberOfCandlesAction.this);
/*     */       } else {
/* 887 */         LoadNumberOfCandlesAction.access$1202(LoadNumberOfCandlesAction.this, (-1.0D / 0.0D));
/*     */       }
/* 889 */       LoadNumberOfCandlesAction.access$1202(LoadNumberOfCandlesAction.this, close);
/*     */     }
/*     */ 
/*     */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TicksLiveFeedListener
/*     */     implements LiveFeedListener
/*     */   {
/*     */     private long allignedFrom;
/*     */     private long from;
/*     */     private long to;
/*     */     private List<TimeInterval> weekends;
/*     */     private Iterator<TimeInterval> weekendsIterator;
/*     */     private TimeInterval weekend;
/*     */     private CandleData currentAskCandle;
/*     */     private CandleData currentBidCandle;
/*     */ 
/*     */     public TicksLiveFeedListener(long arg3, long arg5, List<TimeInterval> arg7)
/*     */     {
/* 661 */       this.allignedFrom = allignedFrom;
/* 662 */       this.from = from;
/* 663 */       this.to = to;
/* 664 */       this.weekends = weekends;
/* 665 */       if (weekends != null)
/* 666 */         this.weekendsIterator = weekends.iterator();
/*     */     }
/*     */ 
/*     */     public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */     {
/* 673 */       if ((this.weekendsIterator != null) && ((this.weekend == null) || (this.weekend.getEnd() < time))) {
/* 674 */         if (this.weekendsIterator.hasNext()) {
/* 675 */           this.weekend = ((TimeInterval)this.weekendsIterator.next());
/*     */         } else {
/* 677 */           this.weekendsIterator = null;
/* 678 */           this.weekend = null;
/*     */         }
/*     */       }
/* 681 */       if ((this.weekend == null) || (time < this.weekend.getStart()) || (time >= this.weekend.getEnd() + 1000L)) {
/* 682 */         if ((LoadNumberOfCandlesAction.this.listener != null) && (time >= this.from)) {
/* 683 */           LoadNumberOfCandlesAction.this.listener.newTick(instrument, time, ask, bid, askVol, bidVol);
/*     */         }
/*     */       }
/*     */       else {
/* 687 */         return;
/*     */       }
/*     */ 
/* 691 */       if (time < this.from)
/*     */       {
/* 694 */         if (this.currentAskCandle == null) {
/* 695 */           this.currentAskCandle = new CandleData();
/* 696 */           this.currentBidCandle = new CandleData();
/*     */         }
/* 698 */         this.currentAskCandle.time = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time);
/* 699 */         this.currentBidCandle.time = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time);
/* 700 */         this.currentAskCandle.close = ask;
/* 701 */         this.currentBidCandle.close = bid;
/* 702 */         return;
/*     */       }
/* 704 */       if (this.currentAskCandle == null)
/*     */       {
/* 706 */         this.currentAskCandle = createCurrentCandleFromTenSec(OfferSide.ASK, ask);
/* 707 */         this.currentBidCandle = createCurrentCandleFromTenSec(OfferSide.BID, bid);
/*     */       }
/* 709 */       if (this.currentAskCandle.time / 1000L == time / 1000L)
/*     */       {
/* 711 */         addTickAtTheEnd(this.currentAskCandle, ask, askVol);
/* 712 */         addTickAtTheEnd(this.currentBidCandle, bid, bidVol);
/*     */       }
/*     */       else {
/* 715 */         long currentCandleTime = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, time);
/* 716 */         long previousNextCandleTime = DataCacheUtils.getNextCandleStartFast(Period.ONE_SEC, this.currentAskCandle.time);
/* 717 */         while (previousNextCandleTime < currentCandleTime)
/*     */         {
/* 719 */           sendCurrentCandle();
/* 720 */           double priceAsk = this.currentAskCandle.close;
/* 721 */           double priceBid = this.currentBidCandle.close;
/*     */ 
/* 723 */           this.currentAskCandle.time = previousNextCandleTime;
/* 724 */           this.currentAskCandle.open = priceAsk;
/* 725 */           this.currentAskCandle.close = priceAsk;
/* 726 */           this.currentAskCandle.low = priceAsk;
/* 727 */           this.currentAskCandle.high = priceAsk;
/* 728 */           this.currentAskCandle.vol = 0.0D;
/*     */ 
/* 730 */           this.currentBidCandle.time = previousNextCandleTime;
/* 731 */           this.currentBidCandle.open = priceBid;
/* 732 */           this.currentBidCandle.close = priceBid;
/* 733 */           this.currentBidCandle.low = priceBid;
/* 734 */           this.currentBidCandle.high = priceBid;
/* 735 */           this.currentBidCandle.vol = 0.0D;
/*     */ 
/* 737 */           previousNextCandleTime = DataCacheUtils.getNextCandleStartFast(Period.ONE_SEC, previousNextCandleTime);
/*     */         }
/* 739 */         sendCurrentCandle();
/* 740 */         newCandleFromTick(this.currentAskCandle, currentCandleTime, ask, askVol);
/* 741 */         newCandleFromTick(this.currentBidCandle, currentCandleTime, bid, bidVol);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void newCandleFromTick(CandleData candle, long time, double price, double vol) {
/* 746 */       candle.time = time;
/* 747 */       candle.open = price;
/* 748 */       candle.close = price;
/* 749 */       candle.high = price;
/* 750 */       candle.low = price;
/* 751 */       candle.vol = vol;
/*     */     }
/*     */ 
/*     */     private void sendCurrentCandle() {
/* 755 */       if ((LoadNumberOfCandlesAction.this.listener != null) && (this.currentAskCandle.time >= this.from) && ((LoadNumberOfCandlesAction.this.filter == Filter.NO_FILTER) || (!weekendsCandle(this.currentAskCandle.time)))) {
/* 756 */         LoadNumberOfCandlesAction.this.listener.newCandle(LoadNumberOfCandlesAction.this.instrument, Period.ONE_SEC, OfferSide.ASK, this.currentAskCandle.time, this.currentAskCandle.open, this.currentAskCandle.close, this.currentAskCandle.low, this.currentAskCandle.high, this.currentAskCandle.vol);
/*     */ 
/* 758 */         LoadNumberOfCandlesAction.this.listener.newCandle(LoadNumberOfCandlesAction.this.instrument, Period.ONE_SEC, OfferSide.BID, this.currentBidCandle.time, this.currentBidCandle.open, this.currentBidCandle.close, this.currentBidCandle.low, this.currentBidCandle.high, this.currentBidCandle.vol);
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean weekendsCandle(long time)
/*     */     {
/* 764 */       for (TimeInterval weekend : this.weekends) {
/* 765 */         if ((time >= weekend.getStart()) && (time <= weekend.getEnd())) {
/* 766 */           return true;
/*     */         }
/*     */       }
/* 769 */       return false;
/*     */     }
/*     */ 
/*     */     private void addTickAtTheEnd(CandleData candle, double price, double vol) {
/* 773 */       candle.close = price;
/* 774 */       candle.high = (candle.high < candle.close ? candle.close : candle.high);
/* 775 */       candle.low = (candle.low > candle.close ? candle.close : candle.low);
/* 776 */       candle.vol = StratUtils.round(candle.vol + vol, 2);
/*     */     }
/*     */ 
/*     */     private CandleData createCurrentCandleFromTenSec(OfferSide side, double defaultPrice) {
/* 780 */       CandleData candle = createCurrentCandleFromTenSec(side);
/* 781 */       if (candle == null) {
/* 782 */         if (!LoadNumberOfCandlesAction.this.loadingProgress.stopJob()) {
/* 783 */           LoadNumberOfCandlesAction.LOGGER.error("Cannot get previous close price to create flats, using open price of the next candle");
/*     */         }
/* 785 */         candle = new CandleData();
/* 786 */         candle.time = DataCacheUtils.getPreviousCandleStartFast(Period.ONE_SEC, this.from);
/* 787 */         candle.close = defaultPrice;
/*     */       }
/* 789 */       return candle;
/*     */     }
/*     */ 
/*     */     private CandleData createCurrentCandleFromTenSec(OfferSide side) {
/*     */       try {
/* 794 */         LoadNumberOfCandlesAction.SaveCandlesLiveFeedListener liveFeedListener = new LoadNumberOfCandlesAction.SaveCandlesLiveFeedListener(null);
/* 795 */         LoadNumberOfCandlesAction.SaveCandlesLoadingProgressListener loadingProgressListener = new LoadNumberOfCandlesAction.SaveCandlesLoadingProgressListener(LoadNumberOfCandlesAction.this, null);
/* 796 */         long previousCandleTime = DataCacheUtils.getPreviousCandleStartFast(Period.ONE_MIN, this.allignedFrom);
/* 797 */         LoadDataAction load10SecCandleAction = new LoadDataAction(LoadNumberOfCandlesAction.this.feedDataProvider, LoadNumberOfCandlesAction.this.instrument, Period.ONE_MIN, side, previousCandleTime, previousCandleTime, liveFeedListener, loadingProgressListener, LoadNumberOfCandlesAction.this.stackTraceElements, false, LoadNumberOfCandlesAction.this.intraperiodExistsPolicy, false);
/*     */ 
/* 800 */         load10SecCandleAction.run();
/* 801 */         if ((loadingProgressListener.loadedSuccessfully) && (liveFeedListener.savedCandles.size() == 1)) {
/* 802 */           CandleData tenSecCandle = (CandleData)liveFeedListener.savedCandles.get(0);
/* 803 */           CandleData candle = new CandleData();
/* 804 */           candle.time = DataCacheUtils.getPreviousCandleStartFast(Period.ONE_SEC, this.from);
/* 805 */           candle.close = tenSecCandle.close;
/* 806 */           return candle;
/*     */         }
/* 808 */         return null;
/*     */       } catch (DataCacheException e) {
/* 810 */         LoadNumberOfCandlesAction.LOGGER.error(e.getMessage(), e);
/* 811 */       }return null;
/*     */     }
/*     */ 
/*     */     public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void theEnd()
/*     */     {
/* 820 */       if (this.currentAskCandle == null)
/*     */       {
/* 822 */         this.currentAskCandle = createCurrentCandleFromTenSec(OfferSide.ASK);
/* 823 */         this.currentBidCandle = createCurrentCandleFromTenSec(OfferSide.BID);
/* 824 */         if ((this.currentAskCandle == null) || (this.currentBidCandle == null)) {
/* 825 */           LoadNumberOfCandlesAction.LOGGER.error("Cannot get previous close price to create flats");
/* 826 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 831 */       long previousNextCandleTime = DataCacheUtils.getNextCandleStartFast(Period.ONE_SEC, this.currentAskCandle.time);
/* 832 */       long candleTo = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, this.to);
/* 833 */       while (previousNextCandleTime < candleTo)
/*     */       {
/* 835 */         sendCurrentCandle();
/* 836 */         double priceAsk = this.currentAskCandle.close;
/* 837 */         double priceBid = this.currentBidCandle.close;
/*     */ 
/* 839 */         this.currentAskCandle = new CandleData(previousNextCandleTime, priceAsk, priceAsk, priceAsk, priceAsk, 0.0D);
/* 840 */         this.currentBidCandle = new CandleData(previousNextCandleTime, priceBid, priceBid, priceBid, priceBid, 0.0D);
/* 841 */         previousNextCandleTime = DataCacheUtils.getNextCandleStartFast(Period.ONE_SEC, previousNextCandleTime);
/*     */       }
/* 843 */       sendCurrentCandle();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadNumberOfCandlesAction
 * JD-Core Version:    0.6.0
 */