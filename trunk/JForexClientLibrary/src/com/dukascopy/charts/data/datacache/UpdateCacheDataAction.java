/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class UpdateCacheDataAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  15 */   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCacheDataAction.class);
/*     */   private final long disconnectTime;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private boolean cancel;
/*     */ 
/*     */   public UpdateCacheDataAction(FeedDataProvider feedDataProvider, long disconnectTime, StackTraceElement[] stackTrace)
/*     */   {
/*  23 */     this.feedDataProvider = feedDataProvider;
/*  24 */     this.disconnectTime = disconnectTime;
/*  25 */     this.stackTrace = stackTrace;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  30 */     long currentTime = this.feedDataProvider.getCurrentTime();
/*  31 */     if (this.disconnectTime > currentTime)
/*     */     {
/*  33 */       return;
/*     */     }
/*  35 */     while ((currentTime == -9223372036854775808L) || (currentTime == this.disconnectTime))
/*     */     {
/*     */       try {
/*  38 */         Thread.sleep(100L);
/*     */       }
/*     */       catch (InterruptedException e) {
/*     */       }
/*  42 */       currentTime = this.feedDataProvider.getCurrentTime();
/*     */     }
/*  44 */     for (Instrument instrument : this.feedDataProvider.getInstrumentsCurrentlySubscribed()) {
/*  45 */       for (Period period : Period.valuesForIndicator()) {
/*  46 */         if (period == Period.TICK) {
/*  47 */           long from = DataCacheUtils.getChunkStartFast(period, this.disconnectTime);
/*     */           try {
/*  49 */             LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, instrument, from, currentTime, null, new LoadingProgressListener()
/*     */             {
/*     */               public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */               {
/*     */               }
/*     */ 
/*     */               public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  56 */                 if ((!allDataLoaded) && (e != null))
/*  57 */                   UpdateCacheDataAction.LOGGER.error(e.getMessage(), e);
/*     */               }
/*     */ 
/*     */               public boolean stopJob()
/*     */               {
/*  63 */                 return UpdateCacheDataAction.this.cancel;
/*     */               }
/*     */             }
/*     */             , this.stackTrace, false, CurvesDataLoader.IntraperiodExistsPolicy.FORCE_DATA_UPDATE, false);
/*     */ 
/*  66 */             loadDataAction.run();
/*     */           }
/*     */           catch (DataCacheException e) {
/*  69 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         } else {
/*  72 */           long from = DataCacheUtils.getCandleStartFast(period, this.disconnectTime);
/*  73 */           long to = DataCacheUtils.getCandleStartFast(period, currentTime);
/*  74 */           for (OfferSide offerSide : OfferSide.values()) {
/*     */             try {
/*  76 */               LoadDataAction loadDataAction = new LoadDataAction(this.feedDataProvider, instrument, period, offerSide, from, to, null, new LoadingProgressListener()
/*     */               {
/*     */                 public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */                 {
/*     */                 }
/*     */ 
/*     */                 public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  83 */                   if ((!allDataLoaded) && (e != null))
/*  84 */                     UpdateCacheDataAction.LOGGER.error(e.getMessage(), e);
/*     */                 }
/*     */ 
/*     */                 public boolean stopJob()
/*     */                 {
/*  90 */                   return UpdateCacheDataAction.this.cancel;
/*     */                 }
/*     */               }
/*     */               , this.stackTrace, false, CurvesDataLoader.IntraperiodExistsPolicy.FORCE_DATA_UPDATE, false);
/*     */ 
/*  93 */               loadDataAction.run();
/*     */             }
/*     */             catch (DataCacheException e) {
/*  96 */               LOGGER.error(e.getMessage(), e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 102 */       this.feedDataProvider.fireCacheDataChanged(instrument, this.disconnectTime, currentTime);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 109 */     this.cancel = true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.UpdateCacheDataAction
 * JD-Core Version:    0.6.0
 */