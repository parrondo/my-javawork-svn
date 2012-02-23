/*     */ package com.dukascopy.charts.data.datacache.renko;
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
/*     */ public class LoadRenkoTimeIntervalAction extends AbstractLoadPriceAggregationByTimeRangeAction<RenkoData, TickData, IRenkoLiveFeedListener, IRenkoCreator>
/*     */ {
/*     */   private final PriceRange brickSize;
/*     */ 
/*     */   public LoadRenkoTimeIntervalAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange brickSize, long fromTime, long toTime, IRenkoLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  39 */     super(feedDataProvider, instrument, offerSide, fromTime, toTime, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  50 */     this.brickSize = brickSize;
/*     */ 
/*  52 */     validateFromToTime(fromTime, toTime);
/*     */   }
/*     */ 
/*     */   protected RenkoData[] createArray(int size)
/*     */   {
/*  57 */     return new RenkoData[size];
/*     */   }
/*     */ 
/*     */   protected IRenkoCreator createFlowPriceAggregationCreator()
/*     */   {
/*  62 */     return new FlowRenkoCreator(getInstrument(), getOfferSide(), getBrickSize());
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<RenkoData, TickData, IRenkoLiveFeedListener, IRenkoCreator> createLiveFeedListener(IRenkoCreator creator, long lastPossibleTime)
/*     */   {
/*  74 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  75 */       return new TickLiveFeedListenerForRenko(creator, lastPossibleTime);
/*     */     }
/*     */ 
/*  78 */     return new CandleLiveFeedListenerForRenko(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*     */     LoadDataAction loadDataAction;
/*     */     LoadDataAction loadDataAction;
/*  91 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  92 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */     else
/*     */     {
/* 106 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBarsBasedOnCandlesPeriod(), getOfferSide(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */ 
/* 122 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   protected IRenkoCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/* 132 */     return new RenkoCreator(getInstrument(), getOfferSide(), getBrickSize(), rangeBarsCount, directOrder, liveCreation);
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 144 */     return getSuitablePeriod(getBrickSize().getPipCount());
/*     */   }
/*     */ 
/*     */   protected RenkoData getInProgressBar()
/*     */   {
/* 149 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressRenko(getInstrument(), getOfferSide(), getBrickSize());
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 154 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressRenkoLoadingNow(getInstrument(), getOfferSide(), getBrickSize());
/*     */   }
/*     */ 
/*     */   public PriceRange getBrickSize() {
/* 158 */     return this.brickSize;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.LoadRenkoTimeIntervalAction
 * JD-Core Version:    0.6.0
 */