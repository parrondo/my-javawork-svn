/*     */ package com.dukascopy.charts.data.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractPriceAggregationDataSequenceProvider<D extends AbstractPriceAggregationData, S extends AbstractPriceAggregationDataSequence<D>> extends AbstractDataSequenceProvider<S, D>
/*     */ {
/*  30 */   protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPriceAggregationDataSequenceProvider.class);
/*     */ 
/*     */   public AbstractPriceAggregationDataSequenceProvider(Instrument instrument, Period period, OfferSide offerSide, long time, int maxCandlesCount, IFeedDataProvider feedDataProvider)
/*     */   {
/*  41 */     super(instrument, period, offerSide, (time > feedDataProvider.getLastTickTime(instrument)) && (feedDataProvider.getLastTickTime(instrument) > -9223372036854775808L) ? feedDataProvider.getLastTickTime(instrument) : time, maxCandlesCount, feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected abstract IDataProvider<D, S> createDataProvider();
/*     */ 
/*     */   protected IDataProvider<D, S> getDataProvider()
/*     */   {
/*  58 */     if (LOGGER.isTraceEnabled()) {
/*  59 */       LOGGER.trace("Get data provider @ " + getPeriod().toString());
/*     */     }
/*     */ 
/*  62 */     IDataProvider dataProvider = (IDataProvider)getDataProviders().get(getClass());
/*     */ 
/*  64 */     if (dataProvider == null) {
/*  65 */       dataProvider = createDataProvider();
/*     */ 
/*  67 */       dataProvider.addDataChangeListener(this);
/*  68 */       dataProvider.start();
/*  69 */       getDataProviders().put(getClass(), dataProvider);
/*     */     } else {
/*  71 */       dataProvider.addDataChangeListener(this);
/*     */     }
/*     */ 
/*  74 */     return dataProvider;
/*     */   }
/*     */ 
/*     */   protected long getCandleStartFast()
/*     */   {
/*  79 */     return this.time;
/*     */   }
/*     */ 
/*     */   protected int calculateIntervalsDifference(long requestedTime, S oldSequence, S newSequence)
/*     */   {
/*  84 */     long newSequenceEndTime = newSequence.getTo();
/*  85 */     long oldSequenceEndTime = oldSequence.getTo();
/*     */ 
/*  87 */     if (newSequenceEndTime > oldSequenceEndTime) {
/*  88 */       for (int i = newSequence.size() - 1; i >= 0; i--) {
/*  89 */         AbstractPriceAggregationData data = (AbstractPriceAggregationData)newSequence.getData(i);
/*  90 */         if (data.getTime() <= oldSequenceEndTime) {
/*  91 */           return newSequence.size() - (i + 1);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*  96 */       for (int i = oldSequence.size() - 1; i >= 0; i--) {
/*  97 */         AbstractPriceAggregationData data = (AbstractPriceAggregationData)oldSequence.getData(i);
/*  98 */         if (data.getTime() <= newSequenceEndTime) {
/*  99 */           return oldSequence.size() - (i + 1);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 104 */     return 1;
/*     */   }
/*     */ 
/*     */   protected int getLeftShiftAddition()
/*     */   {
/* 112 */     return 1;
/*     */   }
/*     */ 
/*     */   protected int getRightShiftAddition()
/*     */   {
/* 120 */     return 0;
/*     */   }
/*     */ 
/*     */   protected void validate(Period period)
/*     */   {
/* 126 */     if (Period.TICK != period)
/* 127 */       throw new IllegalArgumentException("Tick period is supported only!");
/*     */   }
/*     */ 
/*     */   protected Period getDailyFilterPeriod()
/*     */   {
/* 133 */     return Period.DAILY;
/*     */   }
/*     */ 
/*     */   protected Filter getFilter()
/*     */   {
/* 138 */     return Filter.NO_FILTER;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.priceaggregation.AbstractPriceAggregationDataSequenceProvider
 * JD-Core Version:    0.6.0
 */