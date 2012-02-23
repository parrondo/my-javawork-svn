/*     */ package com.dukascopy.charts.data.datacache.pnf;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
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
/*     */ public class LoadPointAndFigureTimeIntervalAction extends AbstractLoadPriceAggregationByTimeRangeAction<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener, IPointAndFigureCreator>
/*     */ {
/*     */   private final PriceRange priceRange;
/*     */   private final ReversalAmount reversalAmount;
/*     */ 
/*     */   public LoadPointAndFigureTimeIntervalAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long fromTime, long toTime, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  39 */     super(feedDataProvider, instrument, offerSide, fromTime, toTime, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  50 */     this.priceRange = priceRange;
/*  51 */     this.reversalAmount = reversalAmount;
/*     */ 
/*  53 */     validateFromToTime(fromTime, toTime);
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData[] createArray(int size)
/*     */   {
/*  58 */     return new PointAndFigureData[size];
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener, IPointAndFigureCreator> createLiveFeedListener(IPointAndFigureCreator creator, long lastPossibleTime)
/*     */   {
/*  66 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  67 */       return new TickLiveFeedListenerForPointAndFigure(creator, lastPossibleTime);
/*     */     }
/*     */ 
/*  70 */     return new CandleLiveFeedListenerForPointAndFigure(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected PointAndFigureCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  81 */     return new PointAndFigureCreator(getInstrument(), getPriceRange(), getReversalAmount(), getOfferSide(), rangeBarsCount, directOrder, liveCreation, (IPointAndFigureLiveFeedListener)getPriceAggregationLiveFeedListener());
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData getInProgressBar()
/*     */   {
/*  95 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressPointAndFigure(getInstrument(), getOfferSide(), getPriceRange(), getReversalAmount());
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/*  99 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 103 */     return this.reversalAmount;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 108 */     return getSuitablePeriod(getPriceRange().getPipCount(), getReversalAmount().getAmount());
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*     */     LoadDataAction loadDataAction;
/*     */     LoadDataAction loadDataAction;
/* 120 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 121 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */     else
/*     */     {
/* 135 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBarsBasedOnCandlesPeriod(), getOfferSide(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */ 
/* 151 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 156 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getPriceRange() + " " + getReversalAmount();
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 161 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressPointAndFigureLoadingNow(getInstrument(), getOfferSide(), getPriceRange(), getReversalAmount());
/*     */   }
/*     */ 
/*     */   protected PointAndFigureCreator createFlowPriceAggregationCreator()
/*     */   {
/* 166 */     return new FlowPointAndFigureFromTicksCreator(getInstrument(), getPriceRange(), getReversalAmount(), getOfferSide());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.LoadPointAndFigureTimeIntervalAction
 * JD-Core Version:    0.6.0
 */