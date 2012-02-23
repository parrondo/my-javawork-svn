/*     */ package com.dukascopy.charts.data.datacache.tickbar;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.TickBarSize;
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
/*     */ public class LoadTickBarTimeIntervalAction extends AbstractLoadPriceAggregationByTimeRangeAction<TickBarData, TickData, ITickBarLiveFeedListener, ITickBarCreator>
/*     */ {
/*     */   private final TickBarSize tickBarSize;
/*     */ 
/*     */   public LoadTickBarTimeIntervalAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  36 */     super(feedDataProvider, instrument, offerSide, fromTime, toTime, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  47 */     this.tickBarSize = tickBarSize;
/*     */ 
/*  49 */     validateFromToTime(fromTime, toTime);
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize() {
/*  53 */     return this.tickBarSize;
/*     */   }
/*     */ 
/*     */   protected TickBarData[] createArray(int size)
/*     */   {
/*  58 */     return new TickBarData[size];
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<TickBarData, TickData, ITickBarLiveFeedListener, ITickBarCreator> createLiveFeedListener(ITickBarCreator creator, long lastPossibleTime)
/*     */   {
/*  66 */     return new TickLiveFeedListenerForTickBar(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected TickBarCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  76 */     return new TickBarCreator(getInstrument(), getTickBarSize(), getOfferSide(), rangeBarsCount, directOrder, liveCreation, (ITickBarLiveFeedListener)getPriceAggregationLiveFeedListener());
/*     */   }
/*     */ 
/*     */   protected TickBarData getInProgressBar()
/*     */   {
/*  89 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressTickBar(getInstrument(), getOfferSide(), getTickBarSize());
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/*  94 */     return Period.TICK;
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/* 105 */     LoadDataAction loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */ 
/* 118 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 123 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getTickBarSize();
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 128 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressTickBarLoadingNow(getInstrument(), getOfferSide(), getTickBarSize());
/*     */   }
/*     */ 
/*     */   protected TickBarCreator createFlowPriceAggregationCreator()
/*     */   {
/* 133 */     return new FlowTickBarCreator(getInstrument(), getTickBarSize(), getOfferSide());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.LoadTickBarTimeIntervalAction
 * JD-Core Version:    0.6.0
 */