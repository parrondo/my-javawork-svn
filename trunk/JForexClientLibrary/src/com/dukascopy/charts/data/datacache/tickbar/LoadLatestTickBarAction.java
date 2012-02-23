/*    */ package com.dukascopy.charts.data.datacache.tickbar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.TickBarSize;
/*    */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LoadLatestTickBarAction extends LoadNumberOfTickBarAction
/*    */ {
/*    */   public LoadLatestTickBarAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*    */   {
/* 30 */     super(feedDataProvider, instrument, offerSide, tickBarSize, 1, -9223372036854775808L, 1, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*    */   }
/*    */ 
/*    */   protected long getToTime()
/*    */   {
/* 46 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*    */   }
/*    */ 
/*    */   protected TickBarData[] extractRequestedBars(TickBarData[] target)
/*    */   {
/* 51 */     TickBarData lastBar = (TickBarData)extractLastBar(target);
/* 52 */     if (lastBar == null) {
/* 53 */       return null;
/*    */     }
/* 55 */     return new TickBarData[] { lastBar };
/*    */   }
/*    */ 
/*    */   protected long updateToTime(long toTime)
/*    */   {
/* 60 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*    */   }
/*    */ 
/*    */   protected long getLastKnownTime()
/*    */   {
/* 65 */     return getToTime();
/*    */   }
/*    */ 
/*    */   protected boolean inProgressBarExists()
/*    */   {
/* 70 */     return true;
/*    */   }
/*    */ 
/*    */   protected List<TickBarData> performDirectBarsLoadForTimeInterval(long fromTime, long toTime, boolean livePriceRangesCreation, boolean checkAllDataMustBeLoaded)
/*    */     throws DataCacheException
/*    */   {
/* 80 */     return super.performDirectBarsLoadForTimeInterval(fromTime, toTime, livePriceRangesCreation, false);
/*    */   }
/*    */ 
/*    */   protected AbstractPriceAggregationLiveFeedListener<TickBarData, TickData, ITickBarLiveFeedListener, ITickBarCreator> createLiveFeedListener(ITickBarCreator creator, long lastPossibleTime)
/*    */   {
/* 88 */     return new TickLiveFeedListenerForTickBar(creator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.LoadLatestTickBarAction
 * JD-Core Version:    0.6.0
 */