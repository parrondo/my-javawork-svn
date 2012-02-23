/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.Period;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LoadInProgressCandleDataAction extends LoadProgressingAction
/*    */   implements Runnable
/*    */ {
/* 21 */   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*    */   private static final Logger LOGGER;
/*    */   private final Instrument instrument;
/*    */   private final long to;
/*    */   private final LiveFeedListener listener;
/*    */   private final StackTraceElement[] stackTrace;
/*    */   private final FeedDataProvider feedDataProvider;
/*    */ 
/*    */   public LoadInProgressCandleDataAction(FeedDataProvider feedDataProvider, Instrument instrument, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace)
/*    */     throws DataCacheException
/*    */   {
/* 43 */     super(loadingProgress);
/* 44 */     this.instrument = instrument;
/* 45 */     this.to = to;
/* 46 */     this.listener = candleListener;
/* 47 */     this.stackTrace = stackTrace;
/* 48 */     this.feedDataProvider = feedDataProvider;
/*    */ 
/* 50 */     if ((instrument == null) || (this.listener == null) || (loadingProgress == null))
/* 51 */       throw new DataCacheException("Wrong parameters: instrument=" + instrument + " / " + to + " / " + loadingProgress);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 56 */     long candleStart = DataCacheUtils.getCandleStartFast(Period.TEN_SECS, this.to);
/* 57 */     if (this.loadingProgress.stopJob()) {
/* 58 */       this.loadingProgress.loadingFinished(false, candleStart, this.to, candleStart, null);
/* 59 */       return;
/*    */     }
/* 61 */     this.loadingProgress.dataLoaded(candleStart, this.to, candleStart, "Downloading in-progress candle data...");
/*    */     try
/*    */     {
/* 64 */       CurvesDataLoader curvesDataLoader = this.feedDataProvider.getCurvesDataLoader();
/*    */ 
/* 66 */       curvesDataLoader.loadInProgressCandle(this.instrument, this.to, this.listener, this.loadingProgress, this.feedDataProvider.getFeedCommissionManager());
/*    */ 
/* 73 */       this.loadingProgress.dataLoaded(candleStart, this.to, this.to, "Data loaded!");
/* 74 */       this.loadingProgress.loadingFinished(true, candleStart, this.to, this.to, null);
/*    */     } catch (Exception e) {
/* 76 */       LOGGER.error(e.getMessage(), e);
/* 77 */       this.loadingProgress.loadingFinished(false, candleStart, this.to, candleStart, e);
/*    */     } catch (Throwable t) {
/* 79 */       LOGGER.error(t.getMessage(), t);
/* 80 */       this.loadingProgress.loadingFinished(false, candleStart, this.to, candleStart, null);
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 24 */     formatter.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*    */ 
/* 27 */     LOGGER = LoggerFactory.getLogger(LoadDataAction.class);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadInProgressCandleDataAction
 * JD-Core Version:    0.6.0
 */