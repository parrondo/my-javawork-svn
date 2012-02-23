/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LoadLastAvailableDataAction extends LoadProgressingAction
/*    */   implements Runnable
/*    */ {
/* 19 */   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*    */   private static final Logger LOGGER;
/*    */   private final Instrument instrument;
/*    */   private final Period period;
/*    */   private final OfferSide side;
/*    */   private final long from;
/*    */   private final long to;
/*    */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*    */   private final LiveFeedListener listener;
/*    */   private final StackTraceElement[] stackTrace;
/*    */   private final FeedDataProvider feedDataProvider;
/*    */ 
/*    */   public LoadLastAvailableDataAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, long from, long to, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace)
/*    */     throws DataCacheException
/*    */   {
/* 40 */     super(loadingProgress);
/* 41 */     this.instrument = instrument;
/* 42 */     this.period = period;
/* 43 */     this.side = side;
/* 44 */     this.from = from;
/* 45 */     this.to = to;
/* 46 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/* 47 */     this.listener = candleListener;
/* 48 */     this.stackTrace = stackTrace;
/* 49 */     this.feedDataProvider = feedDataProvider;
/*    */ 
/* 51 */     if ((instrument == null) || (period == null) || (from > to) || (loadingProgress == null) || (period == Period.TICK)) {
/* 52 */       throw new DataCacheException("Wrong parameters: instrument=" + instrument + " / period=" + period + " / " + from + ", " + to + " / " + loadingProgress);
/*    */     }
/*    */ 
/* 55 */     if (!DataCacheUtils.isIntervalValid(period, from, to))
/* 56 */       throw new DataCacheException("Time interval[" + formatter.format(Long.valueOf(from)) + ", " + formatter.format(Long.valueOf(to)) + "] is not valid for period requested");
/*    */   }
/*    */ 
/*    */   public LoadLastAvailableDataAction(FeedDataProvider feedDataProvider, Instrument instrument, long from, long to, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LiveFeedListener tickListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace)
/*    */     throws NoDataForPeriodException, DataCacheException
/*    */   {
/* 64 */     super(loadingProgress);
/* 65 */     this.instrument = instrument;
/* 66 */     this.period = Period.TICK;
/* 67 */     this.side = null;
/* 68 */     this.from = from;
/* 69 */     this.to = to;
/* 70 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/* 71 */     this.listener = tickListener;
/* 72 */     this.stackTrace = stackTrace;
/* 73 */     this.feedDataProvider = feedDataProvider;
/*    */ 
/* 75 */     if ((instrument == null) || (from > to) || (loadingProgress == null))
/* 76 */       throw new DataCacheException("Wrong parameters: " + instrument + " / " + from + ", " + to + " / " + loadingProgress);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 81 */     if (this.loadingProgress.stopJob()) {
/* 82 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 83 */       return;
/*    */     }
/* 85 */     this.loadingProgress.dataLoaded(this.from, this.to, this.from, "Downloading data into cache...");
/*    */     try
/*    */     {
/* 88 */       long lastTickTime = this.feedDataProvider.getCurrentTime(this.instrument);
/* 89 */       long correctedFrom = this.from;
/* 90 */       long correctedTo = this.to;
/* 91 */       long timeOfFirstCandle = this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period);
/* 92 */       if (timeOfFirstCandle == 9223372036854775807L)
/*    */       {
/* 94 */         this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 95 */         this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/* 96 */         return;
/*    */       }
/* 98 */       if (this.period == Period.TICK) {
/* 99 */         if (correctedFrom < timeOfFirstCandle) {
/* 100 */           correctedFrom = timeOfFirstCandle;
/*    */         }
/* 102 */         if ((lastTickTime != -9223372036854775808L) && (correctedTo > lastTickTime + 500L))
/* 103 */           correctedTo = lastTickTime + 500L;
/*    */       }
/*    */       else {
/* 106 */         if (correctedFrom < timeOfFirstCandle) {
/* 107 */           correctedFrom = DataCacheUtils.getCandleStartFast(this.period, correctedFrom);
/* 108 */           while (correctedFrom < timeOfFirstCandle) {
/* 109 */             correctedFrom = DataCacheUtils.getNextCandleStartFast(this.period, correctedFrom);
/*    */           }
/*    */         }
/* 112 */         if (lastTickTime != -9223372036854775808L) {
/* 113 */           long lastTickCandleStartTime = DataCacheUtils.getCandleStartFast(this.period, lastTickTime);
/* 114 */           if (correctedTo >= lastTickCandleStartTime) {
/* 115 */             correctedTo = DataCacheUtils.getPreviousCandleStartFast(this.period, lastTickCandleStartTime);
/*    */           }
/*    */         }
/*    */       }
/* 119 */       if (correctedFrom > correctedTo) {
/* 120 */         this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 121 */         this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/* 122 */         return;
/*    */       }
/*    */ 
/* 125 */       if (this.listener != null) {
/* 126 */         this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Reading data from cache...");
/* 127 */         LocalCacheManager localCacheManager = this.feedDataProvider.getLocalCacheManager();
/* 128 */         localCacheManager.readLastAvailableData(this.instrument, this.period, this.side, correctedFrom, correctedTo, this.intraperiodExistsPolicy, new DailyFilterLastAvailableListener(this.listener), this.loadingProgress, this.feedDataProvider.getFeedCommissionManager());
/*    */       }
/*    */ 
/* 140 */       if (this.loadingProgress.stopJob()) {
/* 141 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 142 */         return;
/*    */       }
/* 144 */       this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 145 */       this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/*    */     } catch (Exception e) {
/* 147 */       LOGGER.error(e.getMessage(), e);
/* 148 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, e);
/*    */     } catch (Throwable t) {
/* 150 */       LOGGER.error(t.getMessage(), t);
/* 151 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, null);
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 22 */     formatter.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*    */ 
/* 25 */     LOGGER = LoggerFactory.getLogger(LoadLastAvailableDataAction.class);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadLastAvailableDataAction
 * JD-Core Version:    0.6.0
 */