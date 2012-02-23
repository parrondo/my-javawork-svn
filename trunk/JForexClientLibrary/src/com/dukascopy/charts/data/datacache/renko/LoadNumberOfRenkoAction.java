/*     */ package com.dukascopy.charts.data.datacache.renko;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadDataAction;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadNumberOfPriceAggregationAction;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoadNumberOfRenkoAction extends AbstractLoadNumberOfPriceAggregationAction<RenkoData, TickData, IRenkoLiveFeedListener, IRenkoCreator>
/*     */ {
/*  33 */   private static final Logger LOGGER = LoggerFactory.getLogger(LoadNumberOfRenkoAction.class);
/*     */   private final PriceRange brickSize;
/*     */ 
/*     */   public LoadNumberOfRenkoAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfPriceRangesBefore, long toTime, int numberOfPriceRangesAfter, IRenkoLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*     */   {
/*  49 */     super(feedDataProvider, instrument, offerSide, numberOfPriceRangesBefore, toTime, numberOfPriceRangesAfter, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*     */ 
/*  61 */     this.brickSize = brickSize;
/*     */   }
/*     */ 
/*     */   protected RenkoData[] createArray(int size)
/*     */   {
/*  66 */     return new RenkoData[size];
/*     */   }
/*     */ 
/*     */   protected IRenkoCreator createFlowPriceAggregationCreator()
/*     */   {
/*  71 */     return new FlowRenkoCreator(getInstrument(), getOfferSide(), getBrickSize());
/*     */   }
/*     */ 
/*     */   protected AbstractPriceAggregationLiveFeedListener<RenkoData, TickData, IRenkoLiveFeedListener, IRenkoCreator> createLiveFeedListener(IRenkoCreator creator, long lastPossibleTime)
/*     */   {
/*  79 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  80 */       return new TickLiveFeedListenerForRenko(creator, lastPossibleTime);
/*     */     }
/*     */ 
/*  83 */     return new CandleLiveFeedListenerForRenko(creator, lastPossibleTime);
/*     */   }
/*     */ 
/*     */   protected LoadDataAction createLoadDataAction(long fromTime, long toTime, LiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*     */     throws DataCacheException
/*     */   {
/*     */     LoadDataAction loadDataAction;
/*     */     LoadDataAction loadDataAction;
/*  96 */     if (Period.TICK.equals(getBarsBasedOnCandlesPeriod())) {
/*  97 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */     else
/*     */     {
/* 111 */       loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBarsBasedOnCandlesPeriod(), getOfferSide(), fromTime, toTime, liveFeedListener, loadingProgressListener, null, false, getIntraperiodExistsPolicy(), false);
/*     */     }
/*     */ 
/* 127 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   protected IRenkoCreator createPriceAggregationCreator(long startTime, int rangeBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/* 137 */     return new RenkoCreator(getInstrument(), getOfferSide(), getBrickSize(), rangeBarsCount, directOrder, liveCreation);
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 149 */     return getSuitablePeriod(getBrickSize().getPipCount());
/*     */   }
/*     */ 
/*     */   protected RenkoData getInProgressBar()
/*     */   {
/* 154 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressRenko(getInstrument(), getOfferSide(), getBrickSize());
/*     */   }
/*     */ 
/*     */   protected boolean isInProgressBarLoadingNow()
/*     */   {
/* 159 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressRenkoLoadingNow(getInstrument(), getOfferSide(), getBrickSize());
/*     */   }
/*     */ 
/*     */   public PriceRange getBrickSize() {
/* 163 */     return this.brickSize;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 168 */     return getInstrument() + " " + getBarsBasedOnCandlesPeriod() + " " + getOfferSide() + " " + getBrickSize();
/*     */   }
/*     */ 
/*     */   protected long getLastKnownTime()
/*     */   {
/* 173 */     RenkoData bar = getInProgressBar();
/*     */ 
/* 175 */     if (bar == null) {
/* 176 */       LOGGER.warn("Unable to get in progress bar for - " + toString());
/*     */     }
/*     */ 
/* 184 */     bar = (bar == null) || (bar.getInProgressBar() == null) ? bar : bar.getInProgressRenko();
/* 185 */     long time = bar == null ? -9223372036854775808L : bar.getEndTime();
/*     */ 
/* 187 */     return time;
/*     */   }
/*     */ 
/*     */   protected IPriceAggregationLiveFeedListener<RenkoData> createDirectBarsLoadForTimeIntervalListener(long fromTime, long toTime, boolean livePriceRangesCreation, List<RenkoData> loadedData, List<RenkoData> lastArrivedData, IRenkoLiveFeedListener originalPriceAggregationLiveFeedListener)
/*     */   {
/* 200 */     RenkoData bar = getInProgressBar();
/*     */ 
/* 202 */     long to = toTime;
/* 203 */     if (bar != null) {
/* 204 */       to = DataCacheUtils.getPreviousPriceAggregationBarStart(bar.getTime());
/*     */     }
/*     */ 
/* 207 */     IPriceAggregationLiveFeedListener listener = super.createDirectBarsLoadForTimeIntervalListener(fromTime, to, livePriceRangesCreation, loadedData, lastArrivedData, originalPriceAggregationLiveFeedListener);
/*     */ 
/* 216 */     return listener;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.LoadNumberOfRenkoAction
 * JD-Core Version:    0.6.0
 */