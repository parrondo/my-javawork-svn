/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadDataAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  22 */   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*     */   private static final Logger LOGGER;
/*     */   private final Instrument instrument;
/*     */   private final Period period;
/*     */   private final OfferSide side;
/*     */   private final long from;
/*     */   private final long to;
/*     */   private final LiveFeedListener listener;
/*     */   private final boolean blocking;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private final boolean loadFromChunkStart;
/*     */   private final LoadCustomPeriodDataAction loadCustomPeriodDataAction;
/*     */ 
/*     */   public LoadDataAction(IFeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace, boolean blocking, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart)
/*     */     throws DataCacheException
/*     */   {
/*  58 */     super(loadingProgress);
/*     */ 
/*  60 */     this.instrument = instrument;
/*  61 */     this.period = period;
/*  62 */     this.side = side;
/*  63 */     this.from = from;
/*  64 */     this.to = to;
/*  65 */     this.listener = candleListener;
/*  66 */     this.stackTrace = stackTrace;
/*  67 */     this.blocking = blocking;
/*  68 */     this.feedDataProvider = feedDataProvider;
/*  69 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*  70 */     this.loadFromChunkStart = loadFromChunkStart;
/*     */ 
/*  72 */     if ((instrument == null) || (period == null) || (from > to) || (loadingProgress == null) || (period == Period.TICK)) {
/*  73 */       throw new DataCacheException("Wrong parameters: instrument=" + instrument + " / period=" + period + " / " + from + ", " + to + " / " + loadingProgress);
/*     */     }
/*     */ 
/*  76 */     if (!DataCacheUtils.isIntervalValid(period, from, to)) {
/*  77 */       throw new DataCacheException("Time interval[" + formatter.format(Long.valueOf(from)) + ", " + formatter.format(Long.valueOf(to)) + "] is not valid for period [" + period + "]");
/*     */     }
/*     */ 
/*  80 */     if (from < 0L) {
/*  81 */       throw new DataCacheException("Time interval[" + formatter.format(Long.valueOf(from)) + ", " + formatter.format(Long.valueOf(to)) + "] is invalid. Start time is too early");
/*     */     }
/*     */ 
/*  84 */     if (Period.isPeriodBasic(period) == null) {
/*  85 */       this.loadCustomPeriodDataAction = new LoadCustomPeriodDataAction(feedDataProvider, instrument, period, side, from, to, this.listener, loadingProgress, stackTrace, blocking, intraperiodExistsPolicy, loadFromChunkStart);
/*     */     }
/*     */     else
/*     */     {
/* 101 */       this.loadCustomPeriodDataAction = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public LoadDataAction(IFeedDataProvider feedDataProvider, Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace, boolean blocking, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart)
/*     */     throws DataCacheException
/*     */   {
/* 119 */     super(loadingProgress);
/* 120 */     this.instrument = instrument;
/* 121 */     this.period = Period.TICK;
/* 122 */     this.side = null;
/* 123 */     this.from = from;
/* 124 */     this.to = to;
/* 125 */     this.listener = tickListener;
/* 126 */     this.stackTrace = stackTrace;
/* 127 */     this.blocking = blocking;
/* 128 */     this.feedDataProvider = feedDataProvider;
/* 129 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/* 130 */     this.loadFromChunkStart = loadFromChunkStart;
/*     */ 
/* 132 */     if ((instrument == null) || (from > to) || (loadingProgress == null)) {
/* 133 */       throw new DataCacheException("Wrong parameters: " + instrument + " / " + from + ", " + to + " / " + loadingProgress);
/*     */     }
/*     */ 
/* 136 */     this.loadCustomPeriodDataAction = null;
/*     */   }
/*     */ 
/*     */   public void run() {
/* 140 */     if (Period.isPeriodBasic(this.period) == null) {
/* 141 */       this.loadCustomPeriodDataAction.run();
/*     */     }
/*     */     else {
/* 144 */       if (this.loadingProgress.stopJob()) {
/* 145 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 146 */         return;
/*     */       }
/* 148 */       this.loadingProgress.dataLoaded(this.from, this.to, this.from, "Downloading data into cache...");
/*     */       try
/*     */       {
/* 151 */         CurvesDataLoader curvesDataLoader = this.feedDataProvider.getCurvesDataLoader();
/*     */ 
/* 153 */         long lastTickTime = this.feedDataProvider.getCurrentTime(this.instrument);
/*     */ 
/* 157 */         lastTickTime = lastTickTime == -9223372036854775808L ? System.currentTimeMillis() : lastTickTime;
/*     */ 
/* 162 */         long correctedFrom = this.from;
/* 163 */         long correctedTo = this.to;
/* 164 */         long timeOfFirstCandle = this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period);
/* 165 */         if (timeOfFirstCandle == 9223372036854775807L)
/*     */         {
/* 167 */           this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 168 */           this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/* 169 */           return;
/*     */         }
/* 171 */         if (this.period == Period.TICK) {
/* 172 */           if (correctedFrom < timeOfFirstCandle) {
/* 173 */             correctedFrom = timeOfFirstCandle;
/*     */           }
/* 175 */           if ((lastTickTime != -9223372036854775808L) && (correctedTo > lastTickTime + 500L))
/* 176 */             correctedTo = lastTickTime + 500L;
/*     */         }
/*     */         else {
/* 179 */           if (correctedFrom < timeOfFirstCandle) {
/* 180 */             correctedFrom = DataCacheUtils.getCandleStartFast(this.period, correctedFrom);
/*     */ 
/* 188 */             if (correctedFrom < timeOfFirstCandle) {
/* 189 */               correctedFrom = DataCacheUtils.getCandleStartFast(this.period, timeOfFirstCandle);
/*     */             }
/*     */           }
/* 192 */           if (lastTickTime != -9223372036854775808L) {
/* 193 */             long lastTickCandleStartTime = DataCacheUtils.getCandleStartFast(this.period, lastTickTime);
/* 194 */             if (correctedTo >= lastTickCandleStartTime) {
/* 195 */               correctedTo = DataCacheUtils.getPreviousCandleStartFast(this.period, lastTickCandleStartTime);
/*     */             }
/*     */           }
/*     */         }
/* 199 */         if (correctedFrom > correctedTo)
/*     */         {
/* 203 */           this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 204 */           this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/* 205 */           return;
/*     */         }
/*     */ 
/* 208 */         if ((!this.blocking) || (this.listener == null))
/*     */         {
/* 210 */           long correctedFromFinal = correctedFrom;
/* 211 */           long correctedToFinal = correctedTo;
/* 212 */           ChunkLoadingListener reader = this.listener == null ? null : new ChunkLoadingListener(timeOfFirstCandle, correctedFromFinal, correctedToFinal) {
/* 213 */             CandleData lastNonEmptyElement = null;
/*     */ 
/*     */             public void chunkLoaded(long[] chunk) throws DataCacheException
/*     */             {
/* 217 */               LocalCacheManager localCacheManager = LoadDataAction.this.feedDataProvider.getLocalCacheManager();
/* 218 */               if ((LoadDataAction.this.period == Period.DAILY_SKIP_SUNDAY) || (LoadDataAction.this.period == Period.DAILY_SUNDAY_IN_MONDAY)) {
/* 219 */                 long dailyFilterCorrectedFrom = DailyFilterListener.calculateDailyFilterFromCorrection(this.val$timeOfFirstCandle, LoadDataAction.this.period, this.val$correctedFromFinal);
/* 220 */                 this.lastNonEmptyElement = localCacheManager.readData(LoadDataAction.this.instrument, LoadDataAction.this.period, LoadDataAction.this.side, dailyFilterCorrectedFrom, this.val$correctedToFinal, new DailyFilterListener(LoadDataAction.this.feedDataProvider, LoadDataAction.this.listener, DataCacheUtils.getCandlesCountBetweenFast(LoadDataAction.this.period, dailyFilterCorrectedFrom, this.val$correctedFromFinal) - 1, this.lastNonEmptyElement), LoadDataAction.this.blocking, LoadDataAction.this.intraperiodExistsPolicy, chunk, this.lastNonEmptyElement, LoadDataAction.this.feedDataProvider.getFeedCommissionManager());
/*     */               }
/*     */               else
/*     */               {
/* 243 */                 this.lastNonEmptyElement = localCacheManager.readData(LoadDataAction.this.instrument, LoadDataAction.this.period, LoadDataAction.this.side, this.val$correctedFromFinal, this.val$correctedToFinal, LoadDataAction.this.listener, LoadDataAction.this.blocking, LoadDataAction.this.intraperiodExistsPolicy, chunk, this.lastNonEmptyElement, LoadDataAction.this.feedDataProvider.getFeedCommissionManager());
/*     */               }
/*     */             }
/*     */           };
/* 261 */           curvesDataLoader.loadInCache(this.instrument, this.period, this.side, correctedFrom, correctedTo, this.loadingProgress, this.intraperiodExistsPolicy, this.loadFromChunkStart, reader, this.feedDataProvider.getFeedCommissionManager());
/*     */         }
/*     */         else
/*     */         {
/* 275 */           LinkedBlockingQueue processedChunks = new LinkedBlockingQueue();
/* 276 */           ChunkLoadingListener reader = this.listener == null ? null : new ChunkLoadingListener(processedChunks) {
/* 277 */             int chunks = 0;
/*     */ 
/*     */             public void chunkLoaded(long[] chunk) {
/*     */               try {
/* 281 */                 this.val$processedChunks.put(chunk);
/* 282 */                 this.chunks += 1;
/* 283 */                 if (this.chunks >= 5)
/*     */                 {
/* 285 */                   Thread.currentThread().setPriority(1);
/*     */                 }
/*     */               } catch (InterruptedException e) {
/* 288 */                 LoadDataAction.LOGGER.error(e.getMessage(), e);
/* 289 */                 LoadDataAction.this.loadingProgress.loadingFinished(false, LoadDataAction.this.from, LoadDataAction.this.to, LoadDataAction.this.from, e);
/*     */               }
/*     */             }
/*     */           };
/* 295 */           CacheLoadingProgressListener cacheLoadingProgress = new CacheLoadingProgressListener(processedChunks, this.loadingProgress);
/* 296 */           Thread loadInCacheThread = this.feedDataProvider.loadInCacheAsynch(this.instrument, this.period, this.side, correctedFrom, correctedTo, cacheLoadingProgress, this.intraperiodExistsPolicy, this.loadFromChunkStart, reader);
/*     */ 
/* 298 */           LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 299 */           CandleData lastNonEmptyElement = null;
/* 300 */           while ((!this.loadingProgress.stopJob()) && ((cacheLoadingProgress.done == 0) || ((cacheLoadingProgress.done == 1) && (!processedChunks.isEmpty())))) {
/* 301 */             long[] chunk = (long[])processedChunks.poll(20L, TimeUnit.MILLISECONDS);
/* 302 */             if ((chunk != null) && (chunk.length > 0)) {
/* 303 */               if ((this.period == Period.DAILY_SKIP_SUNDAY) || (this.period == Period.DAILY_SUNDAY_IN_MONDAY)) {
/* 304 */                 long dailyFilterCorrectedFrom = DailyFilterListener.calculateDailyFilterFromCorrection(timeOfFirstCandle, this.period, correctedFrom);
/* 305 */                 lastNonEmptyElement = localCacheManager.readData(this.instrument, this.period, this.side, dailyFilterCorrectedFrom, correctedTo, new DailyFilterListener(this.feedDataProvider, this.listener, DataCacheUtils.getCandlesCountBetweenFast(this.period, dailyFilterCorrectedFrom, correctedFrom) - 1, lastNonEmptyElement), this.blocking, this.intraperiodExistsPolicy, chunk, lastNonEmptyElement, this.feedDataProvider.getFeedCommissionManager());
/*     */               }
/*     */               else
/*     */               {
/* 329 */                 lastNonEmptyElement = localCacheManager.readData(this.instrument, this.period, this.side, correctedFrom, correctedTo, this.listener, this.blocking, this.intraperiodExistsPolicy, chunk, lastNonEmptyElement, this.feedDataProvider.getFeedCommissionManager());
/*     */               }
/*     */ 
/*     */             }
/* 344 */             else if (loadInCacheThread.getPriority() == 1)
/*     */             {
/* 346 */               loadInCacheThread.setPriority(5);
/*     */             }
/*     */           }
/*     */ 
/* 350 */           if (cacheLoadingProgress.done == 2) {
/* 351 */             return;
/*     */           }
/*     */         }
/* 354 */         if (this.loadingProgress.stopJob()) {
/* 355 */           this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 356 */           return;
/*     */         }
/* 358 */         if (this.loadingProgress.stopJob()) {
/* 359 */           this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 360 */           return;
/*     */         }
/* 362 */         this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 363 */         this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/*     */       } catch (Exception e) {
/* 365 */         LOGGER.error(e.getMessage(), e);
/* 366 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, e);
/*     */       } catch (Throwable t) {
/* 368 */         LOGGER.error(t.getMessage(), t);
/* 369 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  25 */     formatter.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  28 */     LOGGER = LoggerFactory.getLogger(LoadDataAction.class);
/*     */   }
/*     */ 
/*     */   private static class CacheLoadingProgressListener
/*     */     implements LoadingProgressListener
/*     */   {
/*     */     public volatile int done;
/*     */     private LoadingProgressListener loadingProgress;
/*     */     private LinkedBlockingQueue<long[]> processedChunks;
/*     */ 
/*     */     public CacheLoadingProgressListener(LinkedBlockingQueue<long[]> processedChunks, LoadingProgressListener loadingProgress)
/*     */     {
/* 380 */       this.processedChunks = processedChunks;
/* 381 */       this.loadingProgress = loadingProgress;
/*     */     }
/*     */ 
/*     */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */     {
/* 386 */       this.loadingProgress.dataLoaded(startTime, endTime, currentTime, information);
/*     */     }
/*     */ 
/*     */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*     */     {
/* 391 */       if (!allDataLoaded)
/*     */       {
/* 393 */         this.loadingProgress.loadingFinished(allDataLoaded, startTime, endTime, currentTime, e);
/* 394 */         this.done = 2;
/*     */       } else {
/* 396 */         this.done = 1;
/*     */       }
/*     */       try {
/* 399 */         this.processedChunks.put(new long[0]);
/*     */       } catch (InterruptedException e1) {
/* 401 */         LoadDataAction.LOGGER.error(e1.getMessage(), e1);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean stopJob()
/*     */     {
/* 407 */       return this.loadingProgress.stopJob();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadDataAction
 * JD-Core Version:    0.6.0
 */