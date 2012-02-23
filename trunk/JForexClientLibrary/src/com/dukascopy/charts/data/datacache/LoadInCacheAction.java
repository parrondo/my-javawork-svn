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
/*    */ class LoadInCacheAction extends LoadProgressingAction
/*    */   implements Runnable
/*    */ {
/* 22 */   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*    */   private static final Logger LOGGER;
/*    */   private final Instrument instrument;
/*    */   private final Period period;
/*    */   private final OfferSide side;
/*    */   private final long from;
/*    */   private final long to;
/*    */   private final StackTraceElement[] stackTrace;
/*    */   private final FeedDataProvider feedDataProvider;
/*    */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*    */   private final boolean loadFromChunkStart;
/*    */   private final ChunkLoadingListener chunkLoadingListener;
/*    */   private final boolean loadFromDFSIfFailedFromHTTP;
/*    */ 
/*    */   public LoadInCacheAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, long from, long to, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart, boolean loadFromDFSIfFailedFromHTTP, ChunkLoadingListener chunkLoadingListener)
/*    */   {
/* 56 */     super(loadingProgress);
/* 57 */     this.instrument = instrument;
/* 58 */     this.period = period;
/* 59 */     this.side = side;
/* 60 */     this.from = from;
/* 61 */     this.to = to;
/* 62 */     this.chunkLoadingListener = chunkLoadingListener;
/* 63 */     this.stackTrace = stackTrace;
/* 64 */     this.feedDataProvider = feedDataProvider;
/* 65 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/* 66 */     this.loadFromChunkStart = loadFromChunkStart;
/* 67 */     this.loadFromDFSIfFailedFromHTTP = loadFromDFSIfFailedFromHTTP;
/*    */   }
/*    */ 
/*    */   public void run() {
/* 71 */     if (this.loadingProgress.stopJob()) {
/* 72 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 73 */       return;
/*    */     }
/*    */     try {
/* 76 */       CurvesDataLoader curvesDataLoader = this.feedDataProvider.getCurvesDataLoader();
/*    */ 
/* 80 */       curvesDataLoader.loadInCache(this.instrument, this.period, this.side, this.from, this.to, this.loadingProgress, this.intraperiodExistsPolicy, this.loadFromChunkStart, this.loadFromDFSIfFailedFromHTTP, this.chunkLoadingListener, this.feedDataProvider.getFeedCommissionManager());
/*    */ 
/* 93 */       if (this.loadingProgress.stopJob()) {
/* 94 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 95 */         return;
/*    */       }
/* 97 */       if (this.loadingProgress.stopJob()) {
/* 98 */         this.loadingProgress.loadingFinished(false, this.from, this.to, this.to, null);
/* 99 */         return;
/*    */       }
/* 101 */       this.loadingProgress.dataLoaded(this.from, this.to, this.to, "Data loaded!");
/* 102 */       this.loadingProgress.loadingFinished(true, this.from, this.to, this.to, null);
/*    */     } catch (Exception e) {
/* 104 */       LOGGER.error(e.getMessage(), e);
/* 105 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, e);
/*    */     } catch (Throwable t) {
/* 107 */       LOGGER.error(t.getMessage(), t);
/* 108 */       this.loadingProgress.loadingFinished(false, this.from, this.to, this.from, null);
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 25 */     formatter.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*    */ 
/* 28 */     LOGGER = LoggerFactory.getLogger(LoadDataAction.class);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadInCacheAction
 * JD-Core Version:    0.6.0
 */