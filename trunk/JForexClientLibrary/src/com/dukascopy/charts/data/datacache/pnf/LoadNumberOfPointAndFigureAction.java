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
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadNumberOfPriceAggregationAction;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*     */ 
/*     */ public class LoadNumberOfPointAndFigureAction extends AbstractLoadNumberOfPriceAggregationAction<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener, IPointAndFigureCreator>
/*     */ {
/*     */   private final ReversalAmount reversalAmount;
/*     */   private final PriceRange priceRange;
/*     */ 
/*     */   public LoadNumberOfPointAndFigureAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  41 */     super(feedDataProvider, instrument, offerSide, beforeTimeCandlesCount, time, afterTimeCandlesCount, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  53 */     this.reversalAmount = reversalAmount;
/*  54 */     this.priceRange = priceRange;
/*     */ 
/*  56 */     validateToTime(time);
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<PointAndFigureData, TickData, IPointAndFigureLiveFeedListener, IPointAndFigureCreator> createLiveFeedListener(IPointAndFigureCreator creator, long lastPossibleTime)
/*     */   {
/*  65 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  66 */       return new TickLiveFeedListenerForPointAndFigure(creator, lastPossibleTime);
/*     */     }
/*     */ 
/*  69 */     return new CandleLiveFeedListenerForPointAndFigure(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected PointAndFigureCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean forwardCreation)
/*     */   {
/*  80 */     return new PointAndFigureCreator(getInstrument(), getPriceRange(), getReversalAmount(), getOfferSide(), rangeBarsCount, forwardCreation, liveCreation, (IPointAndFigureLiveFeedListener)getPriceAggregationLiveFeedListener());
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*     */     LoadDataAction loadDataAction;
/*     */     LoadDataAction loadDataAction;
/* 102 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/* 103 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */     else
/*     */     {
/* 117 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBarsBasedOnCandlesPeriod(), getOfferSide(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */ 
/* 133 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData[] createArray(int size)
/*     */   {
/* 138 */     return new PointAndFigureData[size];
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 142 */     return this.reversalAmount;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData getInProgressBar()
/*     */   {
/* 147 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressPointAndFigure(getInstrument(), getOfferSide(), getPriceRange(), getReversalAmount());
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 151 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 156 */     return getSuitablePeriod(getPriceRange().getPipCount(), getReversalAmount().getAmount());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 161 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getPriceRange() + " " + getReversalAmount();
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 166 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressPointAndFigureLoadingNow(getInstrument(), getOfferSide(), getPriceRange(), getReversalAmount());
/*     */   }
/*     */ 
/*     */   protected PointAndFigureCreator createFlowPriceAggregationCreator()
/*     */   {
/* 171 */     return new FlowPointAndFigureFromTicksCreator(getInstrument(), getPriceRange(), getReversalAmount(), getOfferSide());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.LoadNumberOfPointAndFigureAction
 * JD-Core Version:    0.6.0
 */