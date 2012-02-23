/*    */ package com.dukascopy.charts.data.datacache.rangebar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LoadLatestPriceRangeAction extends LoadNumberOfPriceRangeAction
/*    */ {
/*    */   public LoadLatestPriceRangeAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, IPriceRangeLiveFeedListener priceRangeLiveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*    */   {
/* 31 */     super(feedDataProvider, instrument, offerSide, priceRange, 100, -9223372036854775808L, 100, priceRangeLiveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*    */   }
/*    */ 
/*    */   protected long getToTime()
/*    */   {
/* 48 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*    */   }
/*    */ 
/*    */   protected PriceRangeData[] extractRequestedBars(PriceRangeData[] target)
/*    */   {
/* 53 */     PriceRangeData lastBar = (PriceRangeData)extractLastBar(target);
/* 54 */     if (lastBar == null) {
/* 55 */       return null;
/*    */     }
/* 57 */     return new PriceRangeData[] { lastBar };
/*    */   }
/*    */ 
/*    */   protected long updateToTime(long toTime)
/*    */   {
/* 62 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*    */   }
/*    */ 
/*    */   protected long getLastKnownTime()
/*    */   {
/* 67 */     return getToTime();
/*    */   }
/*    */ 
/*    */   protected boolean inProgressBarExists()
/*    */   {
/* 72 */     return true;
/*    */   }
/*    */ 
/*    */   protected List<PriceRangeData> performDirectBarsLoadForTimeInterval(long fromTime, long toTime, boolean livePriceRangesCreation, boolean checkAllDataMustBeLoaded)
/*    */     throws DataCacheException
/*    */   {
/* 82 */     return super.performDirectBarsLoadForTimeInterval(fromTime, toTime, livePriceRangesCreation, false);
/*    */   }
/*    */ 
/*    */   protected AbstractPriceAggregationLiveFeedListener<PriceRangeData, TickData, IPriceRangeLiveFeedListener, IPriceRangeCreator> createLiveFeedListener(IPriceRangeCreator creator, long lastPossibleTime)
/*    */   {
/* 90 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 91 */       return new TickLiveFeedListenerForRangeBars(creator);
/*    */     }
/*    */ 
/* 94 */     return new CandleLiveFeedListenerForRangeBars(creator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.LoadLatestPriceRangeAction
 * JD-Core Version:    0.6.0
 */