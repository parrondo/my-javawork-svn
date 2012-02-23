/*     */ package com.dukascopy.charts.data.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.NullRenkoDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class RenkoDataSequenceProvider extends AbstractPriceAggregationDataSequenceProvider<RenkoData, RenkoDataSequence>
/*     */ {
/*  26 */   private static final RenkoDataSequence NULL_DATA_SEQUENCE = new NullRenkoDataSequence();
/*     */   private PriceRange brickSize;
/*     */ 
/*     */   public RenkoDataSequenceProvider(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long time, int maxCandlesCount, IFeedDataProvider feedDataProvider)
/*     */   {
/*  38 */     super(instrument, Period.TICK, offerSide, time, maxCandlesCount, feedDataProvider);
/*     */ 
/*  47 */     this.brickSize = brickSize;
/*     */   }
/*     */ 
/*     */   protected IDataProvider<RenkoData, RenkoDataSequence> createDataProvider()
/*     */   {
/*  52 */     return new RenkoDataProvider(getInstrument(), getOfferSide(), getBrickSize(), getMaxSequenceSize(), 2, getTime(), getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected RenkoDataSequence getNullDataSequence()
/*     */   {
/*  65 */     return NULL_DATA_SEQUENCE;
/*     */   }
/*     */ 
/*     */   protected void synchronizeDataProviderState(ISynchronizeIndicators synchronizeIndicators)
/*     */   {
/*  70 */     RenkoDataProvider dataProvider = (RenkoDataProvider)getDataProvider();
/*  71 */     Filter filter = getFilter();
/*  72 */     Period period = getPeriod();
/*  73 */     OfferSide offerSide = getOfferSide();
/*  74 */     Instrument instrument = getInstrument();
/*  75 */     PriceRange brickSize = getBrickSize();
/*  76 */     dataProvider.setParams(instrument, period, filter, offerSide, brickSize);
/*     */   }
/*     */ 
/*     */   public PriceRange getBrickSize()
/*     */   {
/*  86 */     return this.brickSize;
/*     */   }
/*     */ 
/*     */   public void setBrickSize(PriceRange brickSize) {
/*  90 */     if (brickSize == null) {
/*  91 */       throw new IllegalArgumentException("brickSize is null");
/*     */     }
/*     */ 
/*  94 */     if (LOGGER.isTraceEnabled()) {
/*  95 */       LOGGER.trace("Set brickSize : " + brickSize);
/*     */     }
/*     */ 
/*  98 */     this.sequence = getNullDataSequence();
/*  99 */     if (getBrickSize() != brickSize) {
/* 100 */       this.brickSize = brickSize;
/* 101 */       activate();
/* 102 */     } else if (!isActive()) {
/* 103 */       activate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod)
/*     */   {
/* 110 */     super.setJForexPeriod(jForexPeriod);
/* 111 */     this.brickSize = jForexPeriod.getPriceRange();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.priceaggregation.RenkoDataSequenceProvider
 * JD-Core Version:    0.6.0
 */