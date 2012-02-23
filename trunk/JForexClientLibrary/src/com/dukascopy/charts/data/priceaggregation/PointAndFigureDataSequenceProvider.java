/*     */ package com.dukascopy.charts.data.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.NullPointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class PointAndFigureDataSequenceProvider extends AbstractPriceAggregationDataSequenceProvider<PointAndFigureData, PointAndFigureDataSequence>
/*     */ {
/*  23 */   private static final NullPointAndFigureDataSequence NULL_DATA_SEQUENCE = new NullPointAndFigureDataSequence();
/*     */   private PriceRange priceRange;
/*     */   private ReversalAmount reversalAmount;
/*     */ 
/*     */   public PointAndFigureDataSequenceProvider(Instrument instrument, Period period, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long time, int maxCandlesCount, IFeedDataProvider feedDataProvider)
/*     */   {
/*  38 */     super(instrument, period, offerSide, time, maxCandlesCount, feedDataProvider);
/*     */ 
/*  47 */     this.reversalAmount = reversalAmount;
/*  48 */     this.priceRange = priceRange;
/*     */   }
/*     */ 
/*     */   protected IDataProvider<PointAndFigureData, PointAndFigureDataSequence> createDataProvider()
/*     */   {
/*  53 */     return new PointAndFigureDataProvider(getInstrument(), getOfferSide(), getPriceRange(), getReversalAmount(), getMaxSequenceSize(), 2, getTime(), getFilter(), getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected PointAndFigureDataSequence getNullDataSequence()
/*     */   {
/*  68 */     return NULL_DATA_SEQUENCE;
/*     */   }
/*     */ 
/*     */   public final void setPriceRange(PriceRange priceRange) {
/*  72 */     if (priceRange == null) {
/*  73 */       throw new IllegalArgumentException("PriceRange is null");
/*     */     }
/*     */ 
/*  76 */     if (LOGGER.isTraceEnabled()) {
/*  77 */       LOGGER.trace("Set priceRange : " + priceRange);
/*     */     }
/*     */ 
/*  80 */     this.sequence = getNullDataSequence();
/*  81 */     if (getPriceRange() != priceRange) {
/*  82 */       this.priceRange = priceRange;
/*  83 */       activate();
/*  84 */     } else if (!isActive()) {
/*  85 */       activate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final PriceRange getPriceRange()
/*     */   {
/*  91 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public void setReversalAmount(ReversalAmount reversalAmount) {
/*  95 */     if (reversalAmount == null) {
/*  96 */       throw new IllegalArgumentException("ReversalAmount is null");
/*     */     }
/*     */ 
/*  99 */     if (LOGGER.isTraceEnabled()) {
/* 100 */       LOGGER.trace("Set reversalAmount : " + reversalAmount);
/*     */     }
/*     */ 
/* 103 */     this.sequence = getNullDataSequence();
/* 104 */     if (this.reversalAmount != reversalAmount) {
/* 105 */       this.reversalAmount = reversalAmount;
/* 106 */       activate();
/* 107 */     } else if (!isActive()) {
/* 108 */       activate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount() {
/* 113 */     return this.reversalAmount;
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod)
/*     */   {
/* 118 */     super.setJForexPeriod(jForexPeriod);
/* 119 */     this.priceRange = jForexPeriod.getPriceRange();
/* 120 */     this.reversalAmount = jForexPeriod.getReversalAmount();
/*     */   }
/*     */ 
/*     */   protected void synchronizeDataProviderState(ISynchronizeIndicators synchronizeIndicators)
/*     */   {
/* 125 */     PointAndFigureDataProvider dataProvider = (PointAndFigureDataProvider)getDataProvider();
/* 126 */     Filter filter = getFilter();
/* 127 */     Period period = getPeriod();
/* 128 */     OfferSide offerSide = getOfferSide();
/* 129 */     Instrument instrument = getInstrument();
/* 130 */     PriceRange priceRange = getPriceRange();
/* 131 */     ReversalAmount reversalAmount = getReversalAmount();
/* 132 */     dataProvider.setParams(instrument, period, filter, offerSide, priceRange, reversalAmount);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.priceaggregation.PointAndFigureDataSequenceProvider
 * JD-Core Version:    0.6.0
 */