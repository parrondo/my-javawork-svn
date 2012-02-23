/*     */ package com.dukascopy.charts.data.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.NullPriceRangeDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class PriceRangeDataSequenceProvider extends AbstractPriceAggregationDataSequenceProvider<PriceRangeData, PriceRangeDataSequence>
/*     */ {
/*  26 */   private static final PriceRangeDataSequence NULL_DATA_SEQUENCE = new NullPriceRangeDataSequence();
/*     */   private PriceRange priceRange;
/*     */ 
/*     */   public PriceRangeDataSequenceProvider(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long time, int maxCandlesCount, IFeedDataProvider feedDataProvider)
/*     */   {
/*  39 */     super(instrument, Period.TICK, offerSide, time, maxCandlesCount, feedDataProvider);
/*     */ 
/*  41 */     this.priceRange = priceRange;
/*     */   }
/*     */ 
/*     */   protected IDataProvider<PriceRangeData, PriceRangeDataSequence> createDataProvider()
/*     */   {
/*  46 */     return new PriceRangeDataProvider(getInstrument(), getOfferSide(), getPriceRange(), getMaxSequenceSize(), 2, getTime(), getFilter(), getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected PriceRangeDataSequence getNullDataSequence()
/*     */   {
/*  60 */     return NULL_DATA_SEQUENCE;
/*     */   }
/*     */ 
/*     */   public final void setPriceRange(PriceRange priceRange) {
/*  64 */     if (priceRange == null) {
/*  65 */       throw new IllegalArgumentException("PriceRange is null");
/*     */     }
/*     */ 
/*  68 */     if (LOGGER.isTraceEnabled()) {
/*  69 */       LOGGER.trace("Set priceRange : " + priceRange);
/*     */     }
/*     */ 
/*  72 */     this.sequence = getNullDataSequence();
/*  73 */     if (getPriceRange() != priceRange) {
/*  74 */       this.priceRange = priceRange;
/*  75 */       activate();
/*  76 */     } else if (!isActive()) {
/*  77 */       activate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final PriceRange getPriceRange()
/*     */   {
/*  83 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod)
/*     */   {
/*  88 */     super.setJForexPeriod(jForexPeriod);
/*  89 */     this.priceRange = jForexPeriod.getPriceRange();
/*     */   }
/*     */ 
/*     */   protected void synchronizeDataProviderState(ISynchronizeIndicators synchronizeIndicators)
/*     */   {
/*  94 */     PriceRangeDataProvider dataProvider = (PriceRangeDataProvider)getDataProvider();
/*  95 */     Filter filter = getFilter();
/*  96 */     Period period = getPeriod();
/*  97 */     OfferSide offerSide = getOfferSide();
/*  98 */     Instrument instrument = getInstrument();
/*  99 */     PriceRange priceRange = getPriceRange();
/* 100 */     dataProvider.setParams(instrument, period, filter, offerSide, priceRange);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.priceaggregation.PriceRangeDataSequenceProvider
 * JD-Core Version:    0.6.0
 */