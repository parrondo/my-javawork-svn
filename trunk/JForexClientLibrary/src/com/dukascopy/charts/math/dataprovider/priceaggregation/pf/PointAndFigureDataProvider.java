/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.pf;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.feed.FeedDescriptor;
/*     */ import com.dukascopy.api.feed.IFeedDescriptor;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureLiveFeedAdapter;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.LoadDataProgressListener;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataProvider;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class PointAndFigureDataProvider extends AbstractPriceAggregationDataProvider<PointAndFigureDataSequence, PointAndFigureData, IPointAndFigureLiveFeedListener>
/*     */ {
/*     */   private ReversalAmount reversalAmount;
/*     */   private PriceRange priceRange;
/*     */ 
/*     */   public PointAndFigureDataProvider(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, int maxSequenceSize, int bufferSizeMultiplier, long lastTime, Filter filter, IFeedDataProvider feedDataProvider)
/*     */   {
/*  42 */     super(instrument, Period.TICK, offerSide, maxSequenceSize, bufferSizeMultiplier, lastTime, filter, feedDataProvider);
/*     */ 
/*  53 */     this.reversalAmount = reversalAmount;
/*  54 */     this.priceRange = priceRange;
/*     */ 
/*  56 */     this.latestBarNotificationListener = new PointAndFigureLiveFeedAdapter()
/*     */     {
/*     */       public void newPriceData(PointAndFigureData pointAndFigure) {
/*  59 */         PointAndFigureDataProvider.this.latestBarArrived(pointAndFigure);
/*     */       }
/*     */     };
/*  63 */     this.inProgressBarListener = new PointAndFigureLiveFeedAdapter()
/*     */     {
/*     */       public void newPriceData(PointAndFigureData pointAndFigure) {
/*  66 */         PointAndFigureDataProvider.this.inProgressBarUpdated(pointAndFigure);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData[] createArray(int size) {
/*  73 */     return new PointAndFigureData[size];
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData[] createArray(PointAndFigureData data)
/*     */   {
/*  78 */     PointAndFigureData[] result = { data };
/*  79 */     return result;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureDataSequence createDataSequence(long from, long to, int extraBefore, int extraAfter, PointAndFigureData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*     */   {
/*  96 */     return new PointAndFigureDataSequence(from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*     */   }
/*     */ 
/*     */   public void performDataLoad(int numOfCandlesBefore, long reqTime, int numOfCandlesAfter)
/*     */   {
/* 116 */     PointAndFigureLiveFeedAndProgressListener listener = createLiveFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 118 */     this.feedDataProvider.getPriceAggregationDataProvider().loadPointAndFigureData(this.instrument, getOfferSide(), getPriceRange(), getReversalAmount(), numOfCandlesBefore, reqTime, numOfCandlesAfter, listener, listener);
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount()
/*     */   {
/* 132 */     return this.reversalAmount;
/*     */   }
/*     */ 
/*     */   public void setReversalAmount(ReversalAmount reversalAmount) {
/* 136 */     setParams(this.instrument, this.period, this.filter, this.side, this.priceRange, reversalAmount);
/*     */   }
/*     */ 
/*     */   public void setFilter(Filter filter)
/*     */   {
/* 141 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/* 146 */     setParams(instrument, this.period, this.filter, this.side, this.priceRange, this.reversalAmount);
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide)
/*     */   {
/* 151 */     setParams(this.instrument, this.period, this.filter, offerSide, this.priceRange, this.reversalAmount);
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period)
/*     */   {
/* 156 */     setParams(this.instrument, period, this.filter, this.side, this.priceRange, this.reversalAmount);
/*     */   }
/*     */ 
/*     */   public void setPriceRange(PriceRange priceRange) {
/* 160 */     setParams(this.instrument, this.period, this.filter, this.side, priceRange, this.reversalAmount);
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 164 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   private boolean isAnyParameterChanged(Instrument instrument, Period period, Filter filter, OfferSide side, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/* 176 */     return (!this.period.equals(period)) || (!this.side.equals(side)) || (!this.filter.equals(filter)) || (!this.priceRange.equals(priceRange)) || (!this.instrument.equals(instrument)) || (!this.reversalAmount.equals(reversalAmount));
/*     */   }
/*     */ 
/*     */   public void setParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/* 193 */     if (LOGGER.isDebugEnabled()) {
/* 194 */       LOGGER.debug("Setting filter " + filter + " for [" + instrument + "] [" + period + "] [" + offerSide + "] provider");
/*     */     }
/*     */ 
/* 197 */     if (this.parentDataProvider != null)
/*     */     {
/* 199 */       synchronized (this.parentDataProvider) {
/* 200 */         synchronized (this) {
/* 201 */           synchronizeParams(instrument, period, filter, offerSide, priceRange, reversalAmount);
/*     */         }
/*     */       }
/*     */     }
/* 205 */     else synchronized (this) {
/* 206 */         synchronizeParams(instrument, period, filter, offerSide, priceRange, reversalAmount);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void synchronizeParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/* 219 */     boolean isAnyParamChanged = isAnyParameterChanged(instrument, period, filter, offerSide, priceRange, reversalAmount);
/*     */ 
/* 221 */     this.priceRange = priceRange;
/* 222 */     this.reversalAmount = reversalAmount;
/* 223 */     super.synchronizeParams(instrument, period, filter, offerSide, isAnyParamChanged);
/*     */   }
/*     */ 
/*     */   protected void addInProgressBarListeners()
/*     */   {
/* 228 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressPointAndFigureListener(this.instrument, this.side, this.priceRange, this.reversalAmount, (IPointAndFigureLiveFeedListener)this.inProgressBarListener);
/* 229 */     this.feedDataProvider.getIntraperiodBarsGenerator().addPointAndFigureNotificationListener(this.instrument, this.side, this.priceRange, this.reversalAmount, (IPointAndFigureLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected void removeInProgressBarListeners()
/*     */   {
/* 234 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressPointAndFigureListener((IPointAndFigureLiveFeedListener)this.inProgressBarListener);
/* 235 */     this.feedDataProvider.getIntraperiodBarsGenerator().removePointAndFigureNotificationListener((IPointAndFigureLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected long getMaxTimeIntervalBetweenTwoBars()
/*     */   {
/* 240 */     Period period = getBarsBasedOnCandlesPeriod();
/* 241 */     long result = getMaxTimeIntervalBetweenTwoBars(period);
/* 242 */     return result;
/*     */   }
/*     */ 
/*     */   protected PointAndFigureData getInProgressBar()
/*     */   {
/* 247 */     PointAndFigureData bar = this.feedDataProvider.getIntraperiodBarsGenerator().getInProgressPointAndFigure(this.instrument, this.side, this.priceRange, this.reversalAmount);
/* 248 */     return bar;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod() {
/* 252 */     return TimeDataUtils.getSuitablePeriod(getPriceRange(), getReversalAmount());
/*     */   }
/*     */ 
/*     */   protected PointAndFigureDataSequence createNullDataSequence()
/*     */   {
/* 257 */     return new NullPointAndFigureDataSequence();
/*     */   }
/*     */ 
/*     */   protected PointAndFigureDataSequence createDataSequence(PointAndFigureData[] data, boolean includesLatestData)
/*     */   {
/*     */     PointAndFigureDataSequence result;
/*     */     PointAndFigureDataSequence result;
/* 266 */     if ((data == null) || (data.length <= 0)) {
/* 267 */       result = new NullPointAndFigureDataSequence();
/*     */     }
/*     */     else {
/* 270 */       result = new PointAndFigureDataSequence(data[0].getTime(), data[(data.length - 1)].getTime(), 0, 0, data, null, null, includesLatestData, includesLatestData);
/*     */     }
/*     */ 
/* 282 */     return result;
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 287 */     return DataType.POINT_AND_FIGURE;
/*     */   }
/*     */ 
/*     */   protected IFeedDescriptor getFeedDescriptor()
/*     */   {
/* 292 */     IFeedDescriptor result = new FeedDescriptor();
/* 293 */     result.setDataType(getDataType());
/* 294 */     result.setInstrument(getInstrument());
/* 295 */     result.setPeriod(getPeriod());
/* 296 */     result.setPriceRange(getPriceRange());
/* 297 */     result.setReversalAmount(getReversalAmount());
/* 298 */     return result;
/*     */   }
/*     */ 
/*     */   public long getFirstKnownTime()
/*     */   {
/* 303 */     return this.feedDataProvider.getTimeOfFirstBar(this.instrument, this.priceRange, this.reversalAmount);
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(long from, long to)
/*     */   {
/* 308 */     PointAndFigureLiveFeedAndProgressListener listener = createLiveFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 310 */     this.feedDataProvider.getPriceAggregationDataProvider().loadPointAndFigureTimeInterval(this.instrument, getOfferSide(), getPriceRange(), getReversalAmount(), from, to, listener, listener);
/*     */   }
/*     */ 
/*     */   private PointAndFigureLiveFeedAndProgressListener createLiveFeedAndProgressListener(AbstractDataProvider<PointAndFigureData, PointAndFigureDataSequence>.LoadDataProgressListener loadingProgressListener)
/*     */   {
/* 323 */     return new PointAndFigureLiveFeedAndProgressListener(loadingProgressListener)
/*     */     {
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 326 */         PointAndFigureDataProvider.this.historicalBarsArived(getResult());
/* 327 */         super.loadingFinished(allDataLoaded, start, end, currentPosition, e);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataProvider
 * JD-Core Version:    0.6.0
 */