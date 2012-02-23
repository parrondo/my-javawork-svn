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
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadNumberOfPriceAggregationAction;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*     */ 
/*     */ public class LoadNumberOfTickBarAction extends AbstractLoadNumberOfPriceAggregationAction<TickBarData, TickData, ITickBarLiveFeedListener, ITickBarCreator>
/*     */ {
/*     */   private final TickBarSize tickBarSize;
/*     */ 
/*     */   public LoadNumberOfTickBarAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int barsNumberBefore, long toTime, int basrsNumberAfter, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  37 */     super(feedDataProvider, instrument, offerSide, barsNumberBefore, toTime, basrsNumberAfter, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  49 */     this.tickBarSize = tickBarSize;
/*     */ 
/*  51 */     validateToTime(getToTime());
/*     */   }
/*     */ 
/*     */   protected TickBarData[] createArray(int size)
/*     */   {
/*  56 */     return new TickBarData[size];
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<TickBarData, TickData, ITickBarLiveFeedListener, ITickBarCreator> createLiveFeedListener(ITickBarCreator creator, long lastPossibleTime)
/*     */   {
/*  64 */     return new TickLiveFeedListenerForTickBar(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*  74 */     LoadDataAction loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */ 
/*  87 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   protected TickBarCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  97 */     return new TickBarCreator(getInstrument(), getTickBarSize(), getOfferSide(), rangeBarsCount, directOrder, liveCreation, (ITickBarLiveFeedListener)getPriceAggregationLiveFeedListener());
/*     */   }
/*     */ 
/*     */   protected TickBarData getInProgressBar()
/*     */   {
/* 110 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressTickBar(getInstrument(), getOfferSide(), this.tickBarSize);
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 115 */     return Period.TICK;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 120 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getTickBarSize();
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize() {
/* 124 */     return this.tickBarSize;
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 129 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressTickBarLoadingNow(getInstrument(), getOfferSide(), getTickBarSize());
/*     */   }
/*     */ 
/*     */   protected TickBarCreator createFlowPriceAggregationCreator()
/*     */   {
/* 134 */     return new FlowTickBarCreator(getInstrument(), getTickBarSize(), getOfferSide());
/*     */   }
/*     */ 
/*     */   protected int getExtraBarsCount()
/*     */   {
/* 139 */     return 5;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.LoadNumberOfTickBarAction
 * JD-Core Version:    0.6.0
 */