/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.feed.FeedDescriptor;
/*     */ import com.dukascopy.api.feed.IFeedDescriptor;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeLiveFeedAdapter;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.LoadDataProgressListener;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataProvider;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class PriceRangeDataProvider extends AbstractPriceAggregationDataProvider<PriceRangeDataSequence, PriceRangeData, IPriceRangeLiveFeedListener>
/*     */ {
/*     */   protected IPriceAggregationCreator<PriceRangeData, TickData, IPriceRangeLiveFeedListener> latestPriceAggregationCreator;
/*     */   private PriceRange priceRange;
/*     */ 
/*     */   public PriceRangeDataProvider(Instrument instrument, OfferSide side, PriceRange priceRange, int maxNumberOfCandles, int bufferSizeMultiplier, long lastTime, Filter filter, IFeedDataProvider feedDataProvider)
/*     */   {
/*  43 */     super(instrument, Period.TICK, side, maxNumberOfCandles, bufferSizeMultiplier, lastTime, filter, feedDataProvider);
/*     */ 
/*  54 */     this.priceRange = priceRange;
/*     */ 
/*  56 */     this.inProgressBarListener = new PriceRangeLiveFeedAdapter()
/*     */     {
/*     */       public void newPriceData(PriceRangeData priceRange) {
/*  59 */         PriceRangeDataProvider.this.inProgressBarUpdated(priceRange);
/*     */       }
/*     */     };
/*  63 */     this.latestBarNotificationListener = new PriceRangeLiveFeedAdapter()
/*     */     {
/*     */       public void newPriceData(PriceRangeData priceRange) {
/*  66 */         PriceRangeDataProvider.this.latestBarArrived(priceRange);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected PriceRangeData[] createArray(int size) {
/*  73 */     return new PriceRangeData[size];
/*     */   }
/*     */ 
/*     */   protected PriceRangeData[] createArray(PriceRangeData data)
/*     */   {
/*  78 */     PriceRangeData[] result = { data };
/*  79 */     return result;
/*     */   }
/*     */ 
/*     */   protected PriceRangeDataSequence createDataSequence(long from, long to, int extraBefore, int extraAfter, PriceRangeData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*     */   {
/*  94 */     return new PriceRangeDataSequence(from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(int numOfCandlesBefore, long reqTime, int numOfCandlesAfter)
/*     */   {
/* 114 */     PriceRangeLiveFeedAndProgressListener listener = createLiveFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 116 */     this.feedDataProvider.getPriceAggregationDataProvider().loadPriceRangeData(getInstrument(), getOfferSide(), getPriceRange(), numOfCandlesBefore, reqTime, numOfCandlesAfter, listener, listener);
/*     */   }
/*     */ 
/*     */   private PriceRangeLiveFeedAndProgressListener createLiveFeedAndProgressListener(AbstractDataProvider<PriceRangeData, PriceRangeDataSequence>.LoadDataProgressListener loadingProgressListener)
/*     */   {
/* 130 */     return new PriceRangeLiveFeedAndProgressListener(loadingProgressListener)
/*     */     {
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 133 */         PriceRangeDataProvider.this.historicalBarsArived(getResult());
/* 134 */         super.loadingFinished(allDataLoaded, start, end, currentPosition, e);
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public void setPriceRange(PriceRange priceRange) {
/* 140 */     setParams(this.instrument, this.period, this.filter, this.side, priceRange);
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange() {
/* 144 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/* 149 */     setParams(instrument, this.period, this.filter, this.side, this.priceRange);
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide)
/*     */   {
/* 154 */     setParams(this.instrument, this.period, this.filter, offerSide, this.priceRange);
/*     */   }
/*     */ 
/*     */   public void setParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, PriceRange priceRange)
/*     */   {
/* 164 */     if (LOGGER.isDebugEnabled()) {
/* 165 */       LOGGER.debug("Setting filter " + filter + " for [" + instrument + "] [" + period + "] [" + offerSide + "] provider");
/*     */     }
/*     */ 
/* 168 */     if (this.parentDataProvider != null)
/*     */     {
/* 170 */       synchronized (this.parentDataProvider) {
/* 171 */         synchronized (this) {
/* 172 */           synchronizeParams(instrument, period, filter, offerSide, priceRange);
/*     */         }
/*     */       }
/*     */     }
/* 176 */     else synchronized (this) {
/* 177 */         synchronizeParams(instrument, period, filter, offerSide, priceRange);
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isAnyParameterChanged(Instrument instrument, Period period, Filter filter, OfferSide side, PriceRange priceRange)
/*     */   {
/* 189 */     return (!this.period.equals(period)) || (!this.side.equals(side)) || (!this.filter.equals(filter)) || (!this.priceRange.equals(priceRange)) || (!this.instrument.equals(instrument));
/*     */   }
/*     */ 
/*     */   private void synchronizeParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, PriceRange priceRange)
/*     */   {
/* 204 */     boolean isAnyParamChanged = isAnyParameterChanged(instrument, period, filter, offerSide, priceRange);
/* 205 */     this.priceRange = priceRange;
/* 206 */     super.synchronizeParams(instrument, period, filter, offerSide, isAnyParamChanged);
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setFilter(Filter filter)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void addInProgressBarListeners()
/*     */   {
/* 221 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressPriceRangeListener(this.instrument, this.side, this.priceRange, (IPriceRangeLiveFeedListener)this.inProgressBarListener);
/* 222 */     this.feedDataProvider.getIntraperiodBarsGenerator().addPriceRangeNotificationListener(this.instrument, this.side, this.priceRange, (IPriceRangeLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected void removeInProgressBarListeners()
/*     */   {
/* 227 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressPriceRangeListener((IPriceRangeLiveFeedListener)this.inProgressBarListener);
/* 228 */     this.feedDataProvider.getIntraperiodBarsGenerator().removePriceRangeNotificationListener((IPriceRangeLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected long getMaxTimeIntervalBetweenTwoBars()
/*     */   {
/* 233 */     Period period = getBarsBasedOnCandlesPeriod();
/* 234 */     long result = getMaxTimeIntervalBetweenTwoBars(period);
/* 235 */     return result;
/*     */   }
/*     */ 
/*     */   protected PriceRangeData getInProgressBar()
/*     */   {
/* 240 */     PriceRangeData bar = this.feedDataProvider.getIntraperiodBarsGenerator().getInProgressPriceRange(this.instrument, this.side, this.priceRange);
/* 241 */     return bar;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod() {
/* 245 */     return TimeDataUtils.getSuitablePeriod(getPriceRange());
/*     */   }
/*     */ 
/*     */   protected PriceRangeDataSequence createNullDataSequence()
/*     */   {
/* 250 */     return new NullPriceRangeDataSequence();
/*     */   }
/*     */ 
/*     */   protected PriceRangeDataSequence createDataSequence(PriceRangeData[] data, boolean includesLatestData)
/*     */   {
/*     */     PriceRangeDataSequence result;
/*     */     PriceRangeDataSequence result;
/* 259 */     if ((data == null) || (data.length <= 0)) {
/* 260 */       result = new NullPriceRangeDataSequence();
/*     */     }
/*     */     else {
/* 263 */       result = new PriceRangeDataSequence(data[0].getTime(), data[(data.length - 1)].getTime(), 0, 0, data, null, null, includesLatestData, includesLatestData);
/*     */     }
/*     */ 
/* 275 */     return result;
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 280 */     return DataType.PRICE_RANGE_AGGREGATION;
/*     */   }
/*     */ 
/*     */   protected IFeedDescriptor getFeedDescriptor()
/*     */   {
/* 285 */     IFeedDescriptor result = new FeedDescriptor();
/* 286 */     result.setDataType(getDataType());
/* 287 */     result.setInstrument(getInstrument());
/* 288 */     result.setPeriod(getPeriod());
/* 289 */     result.setPriceRange(getPriceRange());
/* 290 */     return result;
/*     */   }
/*     */ 
/*     */   public long getFirstKnownTime()
/*     */   {
/* 295 */     return this.feedDataProvider.getTimeOfFirstBar(this.instrument, this.priceRange);
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(long from, long to)
/*     */   {
/* 300 */     PriceRangeLiveFeedAndProgressListener listener = createLiveFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 302 */     this.feedDataProvider.getPriceAggregationDataProvider().loadPriceRangeTimeInterval(getInstrument(), getOfferSide(), getPriceRange(), from, to, listener, listener);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataProvider
 * JD-Core Version:    0.6.0
 */