/*     */ package com.dukascopy.charts.data;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.CandlesDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import com.dukascopy.charts.math.dataprovider.NullCandleDataSequence;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CandlesDataSequenceProvider extends AbstractDataSequenceProvider<CandleDataSequence, CandleData>
/*     */ {
/*  27 */   private static final Logger LOGGER = LoggerFactory.getLogger(CandlesDataSequenceProvider.class);
/*     */ 
/*  29 */   private static final CandleDataSequence NULL_DATA_SEQUENCE = new NullCandleDataSequence();
/*     */ 
/*     */   public CandlesDataSequenceProvider(Instrument instrument, Period period, OfferSide offerSide, long time, int maxCandlesCount, IFeedDataProvider feedDataProvider)
/*     */   {
/*  39 */     super(instrument, period, offerSide, time, maxCandlesCount, feedDataProvider);
/*     */ 
/*  41 */     if (offerSide == null) {
/*  42 */       throw new IllegalArgumentException("OfferSide is null");
/*     */     }
/*     */ 
/*  45 */     if (LOGGER.isTraceEnabled())
/*  46 */       LOGGER.trace("Created : " + toString());
/*     */   }
/*     */ 
/*     */   protected void validate(Period period)
/*     */   {
/*  52 */     if (Period.TICK == period)
/*  53 */       throw new IllegalArgumentException("Tick period is not supported");
/*     */   }
/*     */ 
/*     */   protected CandleDataSequence getNullDataSequence()
/*     */   {
/*  59 */     return NULL_DATA_SEQUENCE;
/*     */   }
/*     */ 
/*     */   protected Filter getFilter()
/*     */   {
/*  64 */     return (Filter)ChartSettings.get(ChartSettings.Option.FILTER);
/*     */   }
/*     */ 
/*     */   protected Period getDailyFilterPeriod()
/*     */   {
/*     */     Period filterPeriod;
/*  70 */     switch (1.$SwitchMap$com$dukascopy$charts$settings$ChartSettings$DailyFilter[((com.dukascopy.charts.settings.ChartSettings.DailyFilter)ChartSettings.get(ChartSettings.Option.DAILYFILTER)).ordinal()]) {
/*     */     case 1:
/*  72 */       filterPeriod = Period.DAILY_SKIP_SUNDAY;
/*  73 */       break;
/*     */     case 2:
/*  75 */       filterPeriod = Period.DAILY_SUNDAY_IN_MONDAY;
/*  76 */       break;
/*     */     case 3:
/*     */     default:
/*  79 */       filterPeriod = Period.DAILY;
/*     */     }
/*  81 */     return filterPeriod;
/*     */   }
/*     */ 
/*     */   protected synchronized IDataProvider<CandleData, CandleDataSequence> getDataProvider()
/*     */   {
/*  86 */     if (LOGGER.isTraceEnabled()) {
/*  87 */       LOGGER.trace("Get data provider @ " + getPeriod().toString());
/*     */     }
/*     */ 
/*  90 */     IDataProvider dataProvider = (IDataProvider)this.dataProviders.get(CandleDataSequence.class);
/*     */ 
/*  92 */     Filter filter = getFilter();
/*     */ 
/*  94 */     if (dataProvider == null) {
/*  95 */       dataProvider = new CandlesDataProvider(this.instrument, getPeriod(), this.offerSide, this.maxSequenceSize, 2, true, filter, this.feedDataProvider);
/*     */ 
/* 106 */       dataProvider.addDataChangeListener(this);
/* 107 */       dataProvider.start();
/* 108 */       this.dataProviders.put(CandleDataSequence.class, dataProvider);
/*     */     } else {
/* 110 */       dataProvider.addDataChangeListener(this);
/*     */     }
/*     */ 
/* 113 */     return dataProvider;
/*     */   }
/*     */ 
/*     */   protected void synchronizeDataProviderState(ISynchronizeIndicators synchronizeIndicators)
/*     */   {
/* 118 */     CandlesDataProvider dataProvider = (CandlesDataProvider)getDataProvider();
/* 119 */     Filter filter = getFilter();
/* 120 */     Period period = getPeriod();
/* 121 */     OfferSide offerSide = getOfferSide();
/* 122 */     Instrument instrument = getInstrument();
/*     */ 
/* 124 */     dataProvider.setParams(instrument, period, filter, offerSide, synchronizeIndicators);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.CandlesDataSequenceProvider
 * JD-Core Version:    0.6.0
 */