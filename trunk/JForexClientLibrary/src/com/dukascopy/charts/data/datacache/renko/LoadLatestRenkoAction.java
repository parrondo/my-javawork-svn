/*     */ package com.dukascopy.charts.data.datacache.renko;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LoadLatestRenkoAction extends LoadNumberOfRenkoAction
/*     */ {
/*     */   public LoadLatestRenkoAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange brickSize, IRenkoLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  34 */     super(feedDataProvider, instrument, offerSide, brickSize, 1, -9223372036854775808L, 1, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */   }
/*     */ 
/*     */   protected long getToTime()
/*     */   {
/*  50 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*     */   }
/*     */ 
/*     */   protected RenkoData[] extractRequestedBars(RenkoData[] target)
/*     */   {
/*  55 */     RenkoData lastBar = (RenkoData)extractLastBar(target);
/*  56 */     if (lastBar == null) {
/*  57 */       return null;
/*     */     }
/*  59 */     return new RenkoData[] { lastBar };
/*     */   }
/*     */ 
/*     */   protected long updateToTime(long toTime)
/*     */   {
/*  64 */     return getFeedDataProvider().getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*     */   }
/*     */ 
/*     */   protected long getLastKnownTime()
/*     */   {
/*  69 */     return getToTime();
/*     */   }
/*     */ 
/*     */   protected boolean inProgressBarExists()
/*     */   {
/*  74 */     return true;
/*     */   }
/*     */ 
/*     */   protected IRenkoLiveFeedListener createDirectBarsLoadForTimeIntervalListener(long fromTime, long toTime, boolean livePriceRangesCreation, List<RenkoData> loadedData, List<RenkoData> lastArrivedData, IRenkoLiveFeedListener originalPriceAggregationLiveFeedListener)
/*     */   {
/*  88 */     IRenkoLiveFeedListener listener = new IRenkoLiveFeedListener(loadedData, lastArrivedData)
/*     */     {
/*     */       public void newPriceData(RenkoData renko) {
/*  91 */         if (this.val$loadedData.size() <= 0) {
/*  92 */           this.val$loadedData.add(renko);
/*  93 */           this.val$lastArrivedData.add(renko);
/*     */         }
/*     */         else {
/*  96 */           this.val$loadedData.set(0, renko);
/*  97 */           this.val$lastArrivedData.set(0, renko);
/*     */         }
/*     */       }
/*     */     };
/* 102 */     return listener;
/*     */   }
/*     */ 
/*     */   protected List<RenkoData> performDirectBarsLoadForTimeInterval(long fromTime, long toTime, boolean livePriceRangesCreation, boolean checkAllDataMustBeLoaded)
/*     */     throws DataCacheException
/*     */   {
/* 112 */     return super.performDirectBarsLoadForTimeInterval(fromTime, toTime, livePriceRangesCreation, false);
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<RenkoData, TickData, IRenkoLiveFeedListener, IRenkoCreator> createLiveFeedListener(IRenkoCreator creator, long lastPossibleTime)
/*     */   {
/* 120 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 121 */       return new TickLiveFeedListenerForRenko(creator);
/*     */     }
/*     */ 
/* 124 */     return new CandleLiveFeedListenerForRenko(creator);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.LoadLatestRenkoAction
 * JD-Core Version:    0.6.0
 */