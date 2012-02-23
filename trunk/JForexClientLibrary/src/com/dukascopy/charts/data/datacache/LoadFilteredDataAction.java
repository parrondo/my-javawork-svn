/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import com.dukascopy.api.Filter;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.listener.FilteredDataLiveFeedListener;
/*    */ 
/*    */ public class LoadFilteredDataAction
/*    */   implements Runnable
/*    */ {
/*    */   private final LoadDataAction loadDataAction;
/*    */   private final FilteredDataLiveFeedListener filteredDataLiveFeedListener;
/*    */ 
/*    */   public LoadFilteredDataAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, boolean blocking, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart)
/*    */     throws DataCacheException
/*    */   {
/* 38 */     this.filteredDataLiveFeedListener = new FilteredDataLiveFeedListener(feedDataProvider.getFilterManager(), filter, candleListener);
/*    */ 
/* 44 */     this.loadDataAction = new LoadDataAction(feedDataProvider, instrument, period, side, from, to, this.filteredDataLiveFeedListener, loadingProgress, null, blocking, intraperiodExistsPolicy, loadFromChunkStart);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 63 */     this.loadDataAction.run();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadFilteredDataAction
 * JD-Core Version:    0.6.0
 */