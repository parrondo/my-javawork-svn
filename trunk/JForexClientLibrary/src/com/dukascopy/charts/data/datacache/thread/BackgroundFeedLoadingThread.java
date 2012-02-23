/*     */ package com.dukascopy.charts.data.datacache.thread;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.LocalCacheManager;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class BackgroundFeedLoadingThread extends Thread
/*     */ {
/*  33 */   private static Logger LOGGER = LoggerFactory.getLogger(BackgroundFeedLoadingThread.class);
/*     */ 
/*  35 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/*     */   private boolean finished;
/*     */   private final FeedDataProvider feedDataProvider;
/*  44 */   private Map<Instrument, Map<Period, Long>> lastLoadedTimes = new HashMap();
/*     */ 
/*  47 */   private final long TICK_LOADING_PERIOD = 345600000L;
/*  48 */   private final long ONE_MINUTE_CANDLE_LOADING_PERIOD = 518400000L;
/*     */ 
/*  50 */   private final long WAIT_BEFORE_START = 30000L;
/*  51 */   private final long DELAY_INTERVAL_BEFORE_CHECK_ALL_INSTRUMENTS = 10000L;
/*  52 */   private final long DELAY_INTERVAL_BETWEEN_EACH_DATA_LOAD = 10000L;
/*     */ 
/*     */   public BackgroundFeedLoadingThread(FeedDataProvider feedDataProvider)
/*     */   {
/*  57 */     super("BackgroundFeedLoadingThread");
/*  58 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*  68 */       Thread.sleep(30000L);
/*     */     } catch (InterruptedException e) {
/*  70 */       LOGGER.error(e.getLocalizedMessage(), e);
/*     */     }
/*     */ 
/*  73 */     int exceptionsAntispam = 10;
/*     */ 
/*  75 */     while (!isFinished())
/*     */     {
/*     */       try
/*     */       {
/*  79 */         boolean dataLoadWasNeeded = false;
/*     */         Period period;
/*  81 */         for (period : Period.valuesForIndicator())
/*     */         {
/*  83 */           if (isFinished()) {
/*  84 */             return;
/*     */           }
/*     */ 
/*  87 */           for (Instrument instrument : this.feedDataProvider.getInstrumentsCurrentlySubscribed())
/*     */           {
/*  89 */             if (isFinished()) {
/*  90 */               return;
/*     */             }
/*     */ 
/*  93 */             long lastLoadedTime = getLastLoadedTime(instrument, period);
/*  94 */             if (lastLoadedTime > 0L) {
/*  95 */               long loadFrom = getFromTime(instrument, period);
/*  96 */               if (loadFrom > 0L)
/*     */               {
/*  98 */                 dataLoadWasNeeded = true;
/*     */ 
/* 100 */                 if (Period.TICK.equals(period)) {
/* 101 */                   loadData(instrument, period, null, loadFrom, lastLoadedTime);
/*     */                 }
/*     */                 else {
/* 104 */                   for (OfferSide offerSide : OfferSide.values()) {
/* 105 */                     loadData(instrument, period, offerSide, loadFrom, lastLoadedTime);
/*     */                   }
/*     */                 }
/*     */ 
/* 109 */                 setLastLoadedTime(instrument, period, loadFrom);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 115 */         if (!dataLoadWasNeeded)
/*     */         {
/* 120 */           Thread.sleep(10000L);
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 124 */         if (exceptionsAntispam > 0) {
/* 125 */           LOGGER.error(t.getLocalizedMessage(), t);
/*     */         }
/*     */         else
/*     */         {
/* 131 */           return;
/*     */         }
/*     */       }
/* 133 */       exceptionsAntispam--;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void loadData(Instrument instrument, Period period, OfferSide offerSide, long from, long to)
/*     */     throws DataCacheException, InterruptedException
/*     */   {
/* 146 */     if ((DataCacheUtils.getCandleStartFast(Period.DAILY, System.currentTimeMillis()) <= from) || (DataCacheUtils.getCandleStartFast(Period.DAILY, System.currentTimeMillis()) <= to) || (!this.feedDataProvider.getLocalCacheManager().isDataCached(instrument, period, offerSide, from, to)))
/*     */     {
/* 158 */       Thread.sleep(10000L);
/*     */ 
/* 160 */       if (Period.TICK.equals(period)) {
/* 161 */         this.feedDataProvider.loadHistoryDataInCacheFromCFGSynched(instrument, from, to, createEmptyLoadingProgressListener());
/*     */       }
/*     */       else
/*     */       {
/* 169 */         this.feedDataProvider.loadHistoryDataInCacheFromCFGSynched(instrument, period, offerSide, from, to, createEmptyLoadingProgressListener());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setLastLoadedTime(Instrument instrument, Period period, long time)
/*     */   {
/* 183 */     ((Map)this.lastLoadedTimes.get(instrument)).put(period, new Long(time));
/*     */   }
/*     */ 
/*     */   private long getLastLoadedTime(Instrument instrument, Period period) {
/* 187 */     if (!this.lastLoadedTimes.containsKey(instrument)) {
/* 188 */       this.lastLoadedTimes.put(instrument, new HashMap());
/*     */     }
/* 190 */     Map periodTimeMap = (Map)this.lastLoadedTimes.get(instrument);
/* 191 */     Long lastLoadedFromTime = (Long)periodTimeMap.get(period);
/*     */ 
/* 193 */     if (lastLoadedFromTime == null) {
/* 194 */       long lastTickTime = this.feedDataProvider.getLastTickTime(instrument);
/* 195 */       if (lastTickTime > 0L) {
/* 196 */         lastLoadedFromTime = new Long(this.feedDataProvider.getLastTickTime(instrument));
/* 197 */         if (!Period.TICK.equals(period)) {
/* 198 */           lastLoadedFromTime = new Long(DataCacheUtils.getCandleStartFast(period, lastLoadedFromTime.longValue()));
/*     */         }
/* 200 */         periodTimeMap.put(period, lastLoadedFromTime);
/*     */       }
/*     */       else {
/* 203 */         lastLoadedFromTime = new Long(9223372036854775807L);
/*     */       }
/*     */     }
/*     */ 
/* 207 */     return lastLoadedFromTime.longValue();
/*     */   }
/*     */ 
/*     */   private long getFromTime(Instrument instrument, Period period) {
/* 211 */     long lastLoadedFromTime = getLastLoadedTime(instrument, period);
/* 212 */     long lastKnownTime = this.feedDataProvider.getLastTickTime(instrument);
/*     */ 
/* 214 */     if ((lastLoadedFromTime > 0L) && (lastKnownTime > 0L)) {
/* 215 */       if (Period.TICK.equals(period)) {
/* 216 */         if (lastKnownTime - lastLoadedFromTime < 345600000L)
/* 217 */           return lastLoadedFromTime - 345600000L;
/*     */       }
/*     */       else
/*     */       {
/* 221 */         lastKnownTime = DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, lastKnownTime));
/* 222 */         long firstTime = this.feedDataProvider.getTimeOfFirstCandle(instrument, period);
/*     */ 
/* 224 */         if (Period.ONE_MIN.equals(period)) {
/* 225 */           if (lastLoadedFromTime > firstTime) {
/* 226 */             return lastLoadedFromTime - 518400000L;
/*     */           }
/*     */         }
/* 229 */         else if ((Period.ONE_HOUR.equals(period)) || (Period.DAILY.equals(period)))
/*     */         {
/* 233 */           if (lastLoadedFromTime > firstTime) {
/* 234 */             return firstTime;
/*     */           }
/*     */         }
/*     */         else {
/* 238 */           throw new IllegalArgumentException("Unsupported period for feed loading " + period);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 243 */     return -9223372036854775808L;
/*     */   }
/*     */ 
/*     */   public boolean isFinished() {
/* 247 */     synchronized (this) {
/* 248 */       return this.finished;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFinished(boolean finished) {
/* 253 */     synchronized (this) {
/* 254 */       this.finished = finished;
/*     */     }
/*     */   }
/*     */ 
/*     */   private LoadingProgressListener createEmptyLoadingProgressListener() {
/* 259 */     return new LoadingProgressListener()
/*     */     {
/*     */       public void dataLoaded(long start, long end, long currentPosition, String information) {
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/*     */       }
/*     */ 
/*     */       public boolean stopJob() {
/* 268 */         return false;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  37 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.thread.BackgroundFeedLoadingThread
 * JD-Core Version:    0.6.0
 */