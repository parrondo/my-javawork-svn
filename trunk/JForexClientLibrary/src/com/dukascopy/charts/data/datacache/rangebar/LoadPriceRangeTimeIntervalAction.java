/*     */ package com.dukascopy.charts.data.datacache.rangebar;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadDataAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadPriceAggregationByTimeRangeAction;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*     */ 
/*     */ public class LoadPriceRangeTimeIntervalAction extends AbstractLoadPriceAggregationByTimeRangeAction<PriceRangeData, TickData, IPriceRangeLiveFeedListener, IPriceRangeCreator>
/*     */ {
/*     */   private final PriceRange priceRange;
/*     */ 
/*     */   public LoadPriceRangeTimeIntervalAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime, IPriceRangeLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  36 */     super(feedDataProvider, instrument, offerSide, fromTime, toTime, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  47 */     this.priceRange = priceRange;
/*     */ 
/*  49 */     validateFromToTime(fromTime, toTime);
/*     */   }
/*     */ 
/*     */   protected PriceRangeData[] createArray(int size)
/*     */   {
/*  54 */     return new PriceRangeData[size];
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<PriceRangeData, TickData, IPriceRangeLiveFeedListener, IPriceRangeCreator> createLiveFeedListener(IPriceRangeCreator creator, long lastPossibleTime)
/*     */   {
/*  62 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  63 */       return new TickLiveFeedListenerForRangeBars(creator, lastPossibleTime);
/*     */     }
/*     */ 
/*  66 */     return new CandleLiveFeedListenerForRangeBars(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected PriceRangeCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  77 */     return new PriceRangeCreator(getInstrument(), getPriceRange(), getOfferSide(), rangeBarsCount, liveCreation, directOrder, (IPriceRangeLiveFeedListener)getPriceAggregationLiveFeedListener());
/*     */   }
/*     */ 
/*     */   protected PriceRangeData getInProgressBar()
/*     */   {
/*  90 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressPriceRange(getInstrument(), getOfferSide(), getPriceRange());
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/*  94 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/*  99 */     return getSuitablePeriod(getPriceRange().getPipCount());
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*     */     LoadDataAction loadDataAction;
/*     */     LoadDataAction loadDataAction;
/* 111 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 112 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */     else
/*     */     {
/* 126 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBarsBasedOnCandlesPeriod(), getOfferSide(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */ 
/* 142 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 147 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getPriceRange();
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 152 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressPriceRangeLoadingNow(getInstrument(), getOfferSide(), getPriceRange());
/*     */   }
/*     */ 
/*     */   protected PriceRangeCreator createFlowPriceAggregationCreator()
/*     */   {
/* 157 */     return new FlowPriceRangeCreator(getInstrument(), getOfferSide(), getPriceRange());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.LoadPriceRangeTimeIntervalAction
 * JD-Core Version:    0.6.0
 */