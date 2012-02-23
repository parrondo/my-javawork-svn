/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.feed.FeedDescriptor;
/*     */ import com.dukascopy.api.feed.IFeedDescriptor;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarLiveFeedAdapter;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.LoadDataProgressListener;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataProvider;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class TickBarDataProvider extends AbstractPriceAggregationDataProvider<TickBarDataSequence, TickBarData, ITickBarLiveFeedListener>
/*     */ {
/*     */   protected IPriceAggregationCreator<TickBarData, TickData, ITickBarLiveFeedListener> latestTickBarCreator;
/*     */   private TickBarSize tickBarSize;
/*     */ 
/*     */   public TickBarDataProvider(Instrument instrument, OfferSide side, TickBarSize tickBarSize, int maxNumberOfCandles, int bufferSizeMultiplier, long lastTime, Filter filter, IFeedDataProvider feedDataProvider)
/*     */   {
/*  42 */     super(instrument, Period.TICK, side, maxNumberOfCandles, bufferSizeMultiplier, lastTime, filter, feedDataProvider);
/*     */ 
/*  52 */     this.tickBarSize = tickBarSize;
/*     */ 
/*  54 */     this.inProgressBarListener = new TickBarLiveFeedAdapter()
/*     */     {
/*     */       public void newPriceData(TickBarData tickBar) {
/*  57 */         TickBarDataProvider.this.inProgressBarUpdated(tickBar);
/*     */       }
/*     */     };
/*  61 */     this.latestBarNotificationListener = new TickBarLiveFeedAdapter()
/*     */     {
/*     */       public void newPriceData(TickBarData tickBar) {
/*  64 */         TickBarDataProvider.this.latestBarArrived(tickBar);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected TickBarData[] createArray(int size) {
/*  71 */     return new TickBarData[size];
/*     */   }
/*     */ 
/*     */   protected TickBarData[] createArray(TickBarData data)
/*     */   {
/*  76 */     return new TickBarData[] { data };
/*     */   }
/*     */ 
/*     */   protected TickBarDataSequence createDataSequence(long from, long to, int extraBefore, int extraAfter, TickBarData[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*     */   {
/*  91 */     return new TickBarDataSequence(from, to, extraBefore, extraAfter, data, formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(int numOfCandlesBefore, long reqTime, int numOfCandlesAfter)
/*     */   {
/* 110 */     TickBarLiveFeedAndProgressListener listener = createLiveFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 112 */     this.feedDataProvider.getPriceAggregationDataProvider().loadTickBarData(getInstrument(), getOfferSide(), this.tickBarSize, numOfCandlesBefore, reqTime, numOfCandlesAfter, listener, listener);
/*     */   }
/*     */ 
/*     */   private TickBarLiveFeedAndProgressListener createLiveFeedAndProgressListener(AbstractDataProvider<TickBarData, TickBarDataSequence>.LoadDataProgressListener loadingProgressListener)
/*     */   {
/* 125 */     return new TickBarLiveFeedAndProgressListener(loadingProgressListener)
/*     */     {
/*     */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 128 */         TickBarDataProvider.this.historicalBarsArived(getResult());
/* 129 */         super.loadingFinished(allDataLoaded, start, end, currentPosition, e);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void setFilter(Filter filter)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument) {
/* 140 */     setParams(instrument, this.period, this.filter, this.side, this.tickBarSize);
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide)
/*     */   {
/* 145 */     setParams(this.instrument, this.period, this.filter, offerSide, this.tickBarSize);
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period)
/*     */   {
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize()
/*     */   {
/* 154 */     return this.tickBarSize;
/*     */   }
/*     */ 
/*     */   public void setParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, TickBarSize tickBarSize)
/*     */   {
/* 164 */     if (LOGGER.isDebugEnabled()) {
/* 165 */       LOGGER.debug("Setting filter " + filter + " for [" + instrument + "] [" + period + "] [" + offerSide + "] provider");
/*     */     }
/*     */ 
/* 168 */     if (this.parentDataProvider != null)
/*     */     {
/* 170 */       synchronized (this.parentDataProvider) {
/* 171 */         synchronized (this) {
/* 172 */           synchronizeParams(instrument, period, filter, offerSide, tickBarSize);
/*     */         }
/*     */       }
/*     */     }
/* 176 */     else synchronized (this) {
/* 177 */         synchronizeParams(instrument, period, filter, offerSide, tickBarSize);
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isAnyParameterChanged(Instrument instrument, Period period, Filter filter, OfferSide side, TickBarSize tickBarSize)
/*     */   {
/* 189 */     return (!this.period.equals(period)) || (!this.side.equals(side)) || (!this.filter.equals(filter)) || (!this.tickBarSize.equals(tickBarSize)) || (!this.instrument.equals(instrument));
/*     */   }
/*     */ 
/*     */   private void synchronizeParams(Instrument instrument, Period period, Filter filter, OfferSide offerSide, TickBarSize tickBarSize)
/*     */   {
/* 204 */     boolean isAnyParamChanged = isAnyParameterChanged(instrument, period, filter, offerSide, tickBarSize);
/* 205 */     this.tickBarSize = tickBarSize;
/* 206 */     super.synchronizeParams(instrument, period, filter, offerSide, isAnyParamChanged);
/*     */   }
/*     */ 
/*     */   protected void removeInProgressBarListeners()
/*     */   {
/* 211 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressTickBarListener((ITickBarLiveFeedListener)this.inProgressBarListener);
/* 212 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeTickBarNotificationListener((ITickBarLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected void addInProgressBarListeners()
/*     */   {
/* 217 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressTickBarListener(this.instrument, this.side, this.tickBarSize, (ITickBarLiveFeedListener)this.inProgressBarListener);
/* 218 */     this.feedDataProvider.getIntraperiodBarsGenerator().addTickBarNotificationListener(this.instrument, this.side, this.tickBarSize, (ITickBarLiveFeedListener)this.latestBarNotificationListener);
/*     */   }
/*     */ 
/*     */   protected long getMaxTimeIntervalBetweenTwoBars()
/*     */   {
/* 223 */     return 60000L;
/*     */   }
/*     */ 
/*     */   protected TickBarData getInProgressBar()
/*     */   {
/* 228 */     TickBarData bar = this.feedDataProvider.getIntraperiodBarsGenerator().getInProgressTickBar(this.instrument, this.side, this.tickBarSize);
/* 229 */     return bar;
/*     */   }
/*     */ 
/*     */   protected Period getBarsBasedOnCandlesPeriod() {
/* 233 */     return Period.TICK;
/*     */   }
/*     */ 
/*     */   protected TickBarDataSequence createNullDataSequence()
/*     */   {
/* 238 */     return new NullTickBarDataSequence();
/*     */   }
/*     */ 
/*     */   protected TickBarDataSequence createDataSequence(TickBarData[] data, boolean includesLatestData)
/*     */   {
/*     */     TickBarDataSequence result;
/*     */     TickBarDataSequence result;
/* 247 */     if ((data == null) || (data.length <= 0)) {
/* 248 */       result = new NullTickBarDataSequence();
/*     */     }
/*     */     else {
/* 251 */       result = new TickBarDataSequence(data[0].getTime(), data[(data.length - 1)].getTime(), 0, 0, data, null, null, includesLatestData, includesLatestData);
/*     */     }
/*     */ 
/* 263 */     return result;
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 268 */     return DataType.TICK_BAR;
/*     */   }
/*     */ 
/*     */   protected IFeedDescriptor getFeedDescriptor()
/*     */   {
/* 273 */     IFeedDescriptor result = new FeedDescriptor();
/* 274 */     result.setDataType(getDataType());
/* 275 */     result.setInstrument(getInstrument());
/* 276 */     result.setPeriod(getPeriod());
/* 277 */     result.setTickBarSize(getTickBarSize());
/* 278 */     return result;
/*     */   }
/*     */ 
/*     */   public long getFirstKnownTime()
/*     */   {
/* 283 */     return this.feedDataProvider.getTimeOfFirstBar(this.instrument, this.tickBarSize);
/*     */   }
/*     */ 
/*     */   protected void performDataLoad(long from, long to)
/*     */   {
/* 288 */     TickBarLiveFeedAndProgressListener listener = createLiveFeedAndProgressListener(this.loadingProgressListener);
/*     */ 
/* 290 */     this.feedDataProvider.getPriceAggregationDataProvider().loadTickBarTimeInterval(getInstrument(), getOfferSide(), this.tickBarSize, from, to, listener, listener);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataProvider
 * JD-Core Version:    0.6.0
 */