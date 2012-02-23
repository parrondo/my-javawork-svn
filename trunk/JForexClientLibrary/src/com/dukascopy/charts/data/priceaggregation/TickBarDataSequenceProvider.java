/*     */ package com.dukascopy.charts.data.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.NullTickBarDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class TickBarDataSequenceProvider extends AbstractPriceAggregationDataSequenceProvider<TickBarData, TickBarDataSequence>
/*     */ {
/*  26 */   private static final TickBarDataSequence NULL_DATA_SEQUENCE = new NullTickBarDataSequence();
/*     */   private TickBarSize tickBarSize;
/*     */ 
/*     */   public TickBarDataSequenceProvider(Instrument instrument, OfferSide offerSide, long time, int maxCandlesCount, TickBarSize tickBarSize, IFeedDataProvider feedDataProvider)
/*     */   {
/*  40 */     super(instrument, Period.TICK, offerSide, time, maxCandlesCount, feedDataProvider);
/*     */ 
/*  42 */     this.tickBarSize = tickBarSize;
/*     */   }
/*     */ 
/*     */   protected IDataProvider<TickBarData, TickBarDataSequence> createDataProvider()
/*     */   {
/*  47 */     return new TickBarDataProvider(getInstrument(), getOfferSide(), getTickBarSize(), getMaxSequenceSize(), 2, getTime(), getFilter(), getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected TickBarDataSequence getNullDataSequence()
/*     */   {
/*  61 */     return NULL_DATA_SEQUENCE;
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod)
/*     */   {
/*  66 */     super.setJForexPeriod(jForexPeriod);
/*  67 */     this.tickBarSize = jForexPeriod.getTickBarSize();
/*     */   }
/*     */ 
/*     */   protected void synchronizeDataProviderState(ISynchronizeIndicators synchronizeIndicators)
/*     */   {
/*  72 */     TickBarDataProvider dataProvider = (TickBarDataProvider)getDataProvider();
/*  73 */     Filter filter = getFilter();
/*  74 */     Period period = getPeriod();
/*  75 */     OfferSide offerSide = getOfferSide();
/*  76 */     Instrument instrument = getInstrument();
/*  77 */     TickBarSize tickBarSize = getTickBarSize();
/*  78 */     dataProvider.setParams(instrument, period, filter, offerSide, tickBarSize);
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize()
/*     */   {
/*  88 */     return this.tickBarSize;
/*     */   }
/*     */ 
/*     */   public void setTickBarSize(TickBarSize tickBarSize) {
/*  92 */     if (tickBarSize == null) {
/*  93 */       throw new IllegalArgumentException("tickBarSize is null");
/*     */     }
/*     */ 
/*  96 */     if (LOGGER.isTraceEnabled()) {
/*  97 */       LOGGER.trace("Set tickBarSize : " + tickBarSize);
/*     */     }
/*     */ 
/* 100 */     this.sequence = getNullDataSequence();
/* 101 */     if (getTickBarSize() != tickBarSize) {
/* 102 */       this.tickBarSize = tickBarSize;
/* 103 */       activate();
/* 104 */     } else if (!isActive()) {
/* 105 */       activate();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.priceaggregation.TickBarDataSequenceProvider
 * JD-Core Version:    0.6.0
 */