/*     */ package com.dukascopy.charts.data;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.math.dataprovider.IDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*     */ import com.dukascopy.charts.math.dataprovider.NullTickDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.TicksDataProvider;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TicksDataSequenceProvider extends AbstractDataSequenceProvider<TickDataSequence, TickData>
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(TicksDataSequenceProvider.class);
/*  29 */   private static final TickDataSequence NULL_DATA_SEQUENCE = new NullTickDataSequence();
/*     */ 
/*     */   public TicksDataSequenceProvider(Instrument instrument, long time, int maxSequenceSize, IFeedDataProvider feedDataProvider)
/*     */   {
/*  37 */     super(instrument, Period.TICK, null, time, maxSequenceSize, feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected void validate(Period period)
/*     */   {
/*  42 */     if (Period.TICK != period)
/*  43 */       throw new IllegalArgumentException("Only Tick period is supported");
/*     */   }
/*     */ 
/*     */   protected TickDataSequence getNullDataSequence()
/*     */   {
/*  49 */     return NULL_DATA_SEQUENCE;
/*     */   }
/*     */ 
/*     */   protected Filter getFilter()
/*     */   {
/*  54 */     Filter filter = (Filter)ChartSettings.get(ChartSettings.Option.FILTER);
/*  55 */     if (filter == Filter.ALL_FLATS) {
/*  56 */       filter = Filter.WEEKENDS;
/*     */     }
/*     */ 
/*  59 */     return filter;
/*     */   }
/*     */ 
/*     */   protected Period getDailyFilterPeriod()
/*     */   {
/*  64 */     return Period.DAILY;
/*     */   }
/*     */ 
/*     */   protected synchronized IDataProvider<TickData, TickDataSequence> getDataProvider()
/*     */   {
/*  69 */     if (LOGGER.isTraceEnabled()) {
/*  70 */       LOGGER.trace("Get data provider @ " + getPeriod().toString());
/*     */     }
/*     */ 
/*  73 */     IDataProvider dataProvider = (IDataProvider)this.dataProviders.get(TickDataSequence.class);
/*     */ 
/*  75 */     Filter filter = getFilter();
/*     */ 
/*  77 */     if (dataProvider == null) {
/*  78 */       dataProvider = new TicksDataProvider(this.instrument, this.maxSequenceSize, 2, true, filter, this.feedDataProvider);
/*     */ 
/*  87 */       dataProvider.addDataChangeListener(this);
/*  88 */       dataProvider.start();
/*  89 */       this.dataProviders.put(TickDataSequence.class, dataProvider);
/*     */     } else {
/*  91 */       dataProvider.addDataChangeListener(this);
/*     */     }
/*     */ 
/*  94 */     return dataProvider;
/*     */   }
/*     */ 
/*     */   public int intervalsCount(TickDataSequence sequence)
/*     */   {
/*  99 */     return sequence.getOneSecCandlesAsk().length - sequence.getOneSecExtraBefore() - sequence.getOneSecExtraAfter();
/*     */   }
/*     */ 
/*     */   protected Data getData(int dataUnitIndex)
/*     */   {
/* 106 */     int before = ((TickDataSequence)this.sequence).getOneSecExtraBefore();
/* 107 */     CandleData[] oneSecData = ((TickDataSequence)this.sequence).getOneSecCandlesAsk();
/*     */ 
/* 109 */     return oneSecData[(before + dataUnitIndex)];
/*     */   }
/*     */ 
/*     */   protected void synchronizeDataProviderState(ISynchronizeIndicators synchronizeIndicators)
/*     */   {
/* 114 */     TicksDataProvider dataProvider = (TicksDataProvider)getDataProvider();
/* 115 */     Filter filter = getFilter();
/* 116 */     Period period = getPeriod();
/* 117 */     Instrument instrument = getInstrument();
/* 118 */     dataProvider.setParams(instrument, period, filter);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.TicksDataSequenceProvider
 * JD-Core Version:    0.6.0
 */