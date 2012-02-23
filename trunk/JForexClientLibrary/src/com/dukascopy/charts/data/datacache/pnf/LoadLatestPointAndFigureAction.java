/*    */ package com.dukascopy.charts.data.datacache.pnf;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.ReversalAmount;
/*    */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LoadLatestPointAndFigureAction extends LoadNumberOfPointAndFigureAction
/*    */ {
/*    */   public LoadLatestPointAndFigureAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*    */   {
/* 33 */     super(feedDataProvider, instrument, offerSide, boxSize, reversalAmount, 1, -9223372036854775808L, 1, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*    */   }
/*    */ 
/*    */   protected long getToTime()
/*    */   {
/* 50 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*    */   }
/*    */ 
/*    */   protected PointAndFigureData[] extractRequestedBars(PointAndFigureData[] target)
/*    */   {
/* 55 */     PointAndFigureData lastBar = (PointAndFigureData)extractLastBar(target);
/* 56 */     if (lastBar == null) {
/* 57 */       return null;
/*    */     }
/* 59 */     return new PointAndFigureData[] { lastBar };
/*    */   }
/*    */ 
/*    */   protected long updateToTime(long toTime)
/*    */   {
/* 64 */     return getToTime();
/*    */   }
/*    */ 
/*    */   protected long getLastKnownTime()
/*    */   {
/* 69 */     return getToTime();
/*    */   }
/*    */ 
/*    */   protected boolean inProgressBarExists()
/*    */   {
/* 74 */     return true;
/*    */   }
/*    */ 
/*    */   protected List<PointAndFigureData> performDirectBarsLoadForTimeInterval(long fromTime, long toTime, boolean livePriceRangesCreation, boolean checkAllDataMustBeLoaded)
/*    */     throws DataCacheException
/*    */   {
/* 84 */     return super.performDirectBarsLoadForTimeInterval(fromTime, toTime, livePriceRangesCreation, false);
/*    */   }
/*    */ 
/*    */   protected AbstractPriceAggregationLiveFeedListener<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener, IPointAndFigureCreator> createLiveFeedListener(IPointAndFigureCreator creator, long lastPossibleTime)
/*    */   {
/* 92 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 93 */       return new TickLiveFeedListenerForPointAndFigure(creator);
/*    */     }
/*    */ 
/* 96 */     return new CandleLiveFeedListenerForPointAndFigure(creator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.LoadLatestPointAndFigureAction
 * JD-Core Version:    0.6.0
 */