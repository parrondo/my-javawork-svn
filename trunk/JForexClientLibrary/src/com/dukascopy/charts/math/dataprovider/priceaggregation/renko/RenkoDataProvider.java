/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.renko;
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
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.LoadDataProgressListener;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataProvider;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class RenkoDataProvider extends AbstractPriceAggregationDataProvider<RenkoDataSequence, RenkoData, IRenkoLiveFeedListener>
/*     */ {
/*     */   private PriceRange brickSize;
/*     */ 
/*     */   public RenkoDataProvider(Instrument instrument, OfferSide side, PriceRange brickSize, int maxNumberOfCandles, int bufferSizeMultiplier, long lastTime, IFeedDataProvider feedDataProvider)
/*     */   {
/*  40 */     super(instrument, Period.TICK, side, maxNumberOfCandles, bufferSizeMultiplier, lastTime, Filter.NO_FILTER, feedDataProvider);
/*     */ 
/*  51 */     this.brickSize = brickSize;
/*     */ 
/*  53 */     this.inProgressBarListener = new IRenkoLiveFeedListener()
/*     */     {
/*     */       public void newPriceData(RenkoData renko) {
/*  56 */         RenkoDataProvider.this.inProgressBarUpdated(renko);
/*     */       }
/*     */     };
/*  60 */     this.latestBarNotificationListener = new IRenkoLiveFeedListener()
/*     */     {
/*     */       public void newPriceData(RenkoData renko) {
/*  63 */         RenkoDataProvider.this.latestBarArrived(renko);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void addInProgressBarListeners()
/*     */   {
/*  71 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressRenkoListener(this.instrument, this.side, this.brickSize, (IRenkoLiveFeedListener)this.inProgressBarListener);
/*  72 */     this.feedDataProvider.getIntraperiodBarsGenerator().addRenkoNotificationListener(this.instrument, this.side, this.brickSize, (IRenkoLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected RenkoData[] createArray(RenkoData data)
/*     */   {
/*  77 */     RenkoData[] result = { data };
/*  78 */     return result;
/*     */   }
/*     */ 
/*     */   protected RenkoDataSequence createDataSequence(long from, long to, int extraBefore, int extraAfter, RenkoData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*     */   {
/*  93 */     return new RenkoDataSequence(from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod()
/*     */   {
/* 107 */     return TimeDataUtils.getSuitablePeriod(getBrickSize());
/*     */   }
/*     */ 
/*     */   protected RenkoData getInProgressBar()
/*     */   {
/* 112 */     RenkoData bar = this.feedDataProvider.getIntraperiodBarsGenerator().getInProgressRenko(this.instrument, this.side, this.brickSize);
/* 113 */     return bar;
/*     */   }
/*     */ 
/*     */   protected long getMaxTimeIntervalBetweenTwoBars()
/*     */   {
/* 118 */     Period period = getBarsBasedOnCandlesPeriod();
/* 119 */     long result = getMaxTimeIntervalBetweenTwoBars(period);
/* 120 */     return result;
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(int numOfCandlesBefore, long reqTime, int numOfCandlesAfter)
/*     */   {
/* 129 */     RenkoLiveFeedAndProgressListener listener = createFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 131 */     this.feedDataProvider.getPriceAggregationDataProvider().loadRenkoData(getInstrument(), getOfferSide(), getBrickSize(), numOfCandlesBefore, reqTime, numOfCandlesAfter, listener, listener);
/*     */   }
/*     */ 
/*     */   private RenkoLiveFeedAndProgressListener createFeedAndProgressListener(AbstractDataProvider<RenkoData, RenkoDataSequence>.LoadDataProgressListener loadingProgressListener)
/*     */   {
/* 145 */     return new RenkoLiveFeedAndProgressListener(loadingProgressListener)
/*     */     {
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 148 */         RenkoDataProvider.this.historicalBarsArived(getResult());
/* 149 */         super.loadingFinished(allDataLoaded, start, end, currentPosition, e);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void removeInProgressBarListeners() {
/* 156 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressRenkoListener((IRenkoLiveFeedListener)this.inProgressBarListener);
/* 157 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeRenkoNotificationListener((IRenkoLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected RenkoData[] createArray(int size)
/*     */   {
/* 162 */     return new RenkoData[size];
/*     */   }
/*     */ 
/*     */   protected RenkoDataSequence createDataSequence(RenkoData[] data, boolean includesLatestData)
/*     */   {
/*     */     RenkoDataSequence result;
/*     */     RenkoDataSequence result;
/* 171 */     if ((data == null) || (data.length <= 0)) {
/* 172 */       result = createNullDataSequence();
/*     */     }
/*     */     else {
/* 175 */       result = new RenkoDataSequence(data[0].getTime(), data[(data.length - 1)].getTime(), 0, 0, data, null, null, includesLatestData, includesLatestData);
/*     */     }
/*     */ 
/* 187 */     return result;
/*     */   }
/*     */ 
/*     */   protected RenkoDataSequence createNullDataSequence()
/*     */   {
/* 192 */     return new NullRenkoDataSequence();
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 197 */     return DataType.RENKO;
/*     */   }
/*     */ 
/*     */   protected IFeedDescriptor getFeedDescriptor()
/*     */   {
/* 202 */     IFeedDescriptor descriptor = new FeedDescriptor();
/*     */ 
/* 204 */     descriptor.setDataType(getDataType());
/* 205 */     descriptor.setFilter(getFilter());
/* 206 */     descriptor.setInstrument(getInstrument());
/* 207 */     descriptor.setOfferSide(getOfferSide());
/* 208 */     descriptor.setPeriod(getPeriod());
/* 209 */     descriptor.setPriceRange(getBrickSize());
/*     */ 
/* 211 */     return descriptor;
/*     */   }
/*     */ 
/*     */   public void setFilter(Filter filter)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/* 221 */     setParams(instrument, this.period, this.filter, this.side, this.brickSize);
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide)
/*     */   {
/* 226 */     setParams(this.instrument, this.period, this.filter, offerSide, this.brickSize);
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period)
/*     */   {
/*     */   }
/*     */ 
/*     */   public PriceRange getBrickSize()
/*     */   {
/* 235 */     return this.brickSize;
/*     */   }
/*     */ 
/*     */   public void setBrickSize(PriceRange brickSize) {
/* 239 */     setParams(this.instrument, this.period, this.filter, this.side, brickSize);
/*     */   }
/*     */ 
/*     */   public void setParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, PriceRange priceRange)
/*     */   {
/* 249 */     if (LOGGER.isDebugEnabled()) {
/* 250 */       LOGGER.debug("Setting filter " + filter + " for [" + instrument + "] [" + period + "] [" + offerSide + "] provider");
/*     */     }
/*     */ 
/* 253 */     if (this.parentDataProvider != null)
/*     */     {
/* 255 */       synchronized (this.parentDataProvider) {
/* 256 */         synchronized (this) {
/* 257 */           synchronizeParams(instrument, period, filter, offerSide, priceRange);
/*     */         }
/*     */       }
/*     */     }
/* 261 */     else synchronized (this) {
/* 262 */         synchronizeParams(instrument, period, filter, offerSide, priceRange);
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isAnyParameterChanged(Instrument instrument, Period period, Filter filter, OfferSide side, PriceRange brickSize)
/*     */   {
/* 274 */     return (!this.period.equals(period)) || (!this.side.equals(side)) || (!this.filter.equals(filter)) || (!this.brickSize.equals(brickSize)) || (!this.instrument.equals(instrument));
/*     */   }
/*     */ 
/*     */   private void synchronizeParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, PriceRange brickSize)
/*     */   {
/* 289 */     boolean isAnyParamChanged = isAnyParameterChanged(instrument, period, filter, offerSide, brickSize);
/* 290 */     this.brickSize = brickSize;
/* 291 */     super.synchronizeParams(instrument, period, filter, offerSide, isAnyParamChanged);
/*     */   }
/*     */ 
/*     */   public long getFirstKnownTime()
/*     */   {
/* 296 */     return this.feedDataProvider.getTimeOfFirstBar(this.instrument, this.brickSize);
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(long from, long to)
/*     */   {
/* 301 */     RenkoLiveFeedAndProgressListener listener = createFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 303 */     this.feedDataProvider.getPriceAggregationDataProvider().loadRenkoTimeInterval(getInstrument(), getOfferSide(), getBrickSize(), from, to, listener, listener);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataProvider
 * JD-Core Version:    0.6.0
 */