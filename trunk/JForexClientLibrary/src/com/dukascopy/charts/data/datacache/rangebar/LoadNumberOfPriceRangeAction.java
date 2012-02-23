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
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadNumberOfPriceAggregationAction;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*     */ 
/*     */ public class LoadNumberOfPriceRangeAction extends AbstractLoadNumberOfPriceAggregationAction<PriceRangeData, TickData, IPriceRangeLiveFeedListener, IPriceRangeCreator>
/*     */ {
/*     */   private final PriceRange priceRange;
/*     */ 
/*     */   public LoadNumberOfPriceRangeAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter, IPriceRangeLiveFeedListener priceRangeLiveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  38 */     super(feedDataProvider, instrument, offerSide, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter, priceRangeLiveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  50 */     this.priceRange = priceRange;
/*  51 */     validateToTime(time);
/*     */   }
/*     */ 
/*     */   protected PriceRangeCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean livePriceRangesCreation, boolean forwardCreation)
/*     */   {
/*  61 */     return new PriceRangeCreator(getInstrument(), getPriceRange(), getOfferSide(), rangeBarsCount, livePriceRangesCreation, forwardCreation, (IPriceRangeLiveFeedListener)getPriceAggregationLiveFeedListener());
/*     */   }
/*     */ 
/*     */   protected PriceRangeData[] createArray(int size)
/*     */   {
/*  74 */     return new PriceRangeData[size];
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<PriceRangeData, TickData, IPriceRangeLiveFeedListener, IPriceRangeCreator> createLiveFeedListener(IPriceRangeCreator creator, long lastPossibleTime)
/*     */   {
/*  82 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  83 */       return new TickLiveFeedListenerForRangeBars(creator, lastPossibleTime);
/*     */     }
/*     */ 
/*  86 */     return new CandleLiveFeedListenerForRangeBars(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*     */     LoadDataAction loadDataAction;
/*     */     LoadDataAction loadDataAction;
/* 100 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 101 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */     else
/*     */     {
/* 115 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBarsBasedOnCandlesPeriod(), getOfferSide(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */ 
/* 131 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   protected PriceRangeData getInProgressBar()
/*     */   {
/* 136 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressPriceRange(getInstrument(), getOfferSide(), getPriceRange());
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 140 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 145 */     return getSuitablePeriod(getPriceRange().getPipCount());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 150 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getPriceRange();
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 155 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressPriceRangeLoadingNow(getInstrument(), getOfferSide(), getPriceRange());
/*     */   }
/*     */ 
/*     */   protected PriceRangeCreator createFlowPriceAggregationCreator()
/*     */   {
/* 160 */     return new FlowPriceRangeCreator(getInstrument(), getOfferSide(), getPriceRange());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.LoadNumberOfPriceRangeAction
 * JD-Core Version:    0.6.0
 */