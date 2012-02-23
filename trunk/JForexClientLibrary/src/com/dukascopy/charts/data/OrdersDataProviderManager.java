/*     */ package com.dukascopy.charts.data;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.IOrdersDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.OrdersDataChangeListener;
/*     */ import com.dukascopy.charts.math.dataprovider.OrdersDataChangeListenerAdapter;
/*     */ import com.dukascopy.charts.math.dataprovider.OrdersDataProvider;
/*     */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
/*     */ import com.dukascopy.dds2.greed.util.IOrderUtils;
/*     */ 
/*     */ public class OrdersDataProviderManager
/*     */   implements IOrdersDataProviderManager
/*     */ {
/*     */   private final ChartState chartState;
/*     */   private final IOrdersDataProvider ordersDataProvider;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private AbstractDataSequenceProvider<TickDataSequence, TickData> ticksDataSequenceProvider;
/*     */   private AbstractDataSequenceProvider<CandleDataSequence, CandleData> candlesDataSequenceProvider;
/*     */   private AbstractDataSequenceProvider<PriceRangeDataSequence, PriceRangeData> priceRangeDataSequenceProvider;
/*     */   private AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> pointAndFigureDataSequenceProvider;
/*     */   private AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> tickBarDataSequenceProvider;
/*     */   private AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> renkoDataSequenceProvider;
/*  43 */   private long cachedStartTime = -1L;
/*  44 */   private long cachedEndTime = -1L;
/*     */   private OrderHistoricalData[] cachedOrdersData;
/*  46 */   private final OrderHistoricalData[] nullOrdersData = new OrderHistoricalData[0];
/*     */ 
/*  48 */   OrdersDataChangeListener ordersDataChangeListener = new OrdersDataChangeListenerAdapter()
/*     */   {
/*     */     public void dataChanged(Instrument instrument, long from, long to) {
/*  51 */       if (((from < OrdersDataProviderManager.this.cachedStartTime) && (to >= OrdersDataProviderManager.this.cachedStartTime)) || ((from >= OrdersDataProviderManager.this.cachedStartTime) && (from <= OrdersDataProviderManager.this.cachedEndTime)) || (from == 9223372036854775807L))
/*  52 */         OrdersDataProviderManager.access$202(OrdersDataProviderManager.this, null);
/*     */     }
/*  48 */   };
/*     */ 
/*     */   public OrdersDataProviderManager(Instrument instrument, ChartState chartState, IFeedDataProvider feedDataProvider)
/*     */   {
/*  62 */     this.chartState = chartState;
/*  63 */     this.ordersDataProvider = new OrdersDataProvider(instrument, ChartProperties.MAX_VISIBLE_ORDERS_TIME_INTERVAL, feedDataProvider);
/*  64 */     this.feedDataProvider = feedDataProvider;
/*  65 */     this.ordersDataProvider.addOrdersDataChangeListener(this.ordersDataChangeListener);
/*     */   }
/*     */ 
/*     */   public void setTicksDataSequenceProvider(AbstractDataSequenceProvider<TickDataSequence, TickData> ticksDataSequenceProvider) {
/*  69 */     this.ticksDataSequenceProvider = ticksDataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void setCandlesDataSequenceProvicer(AbstractDataSequenceProvider<CandleDataSequence, CandleData> candlesDataSequenceProvider) {
/*  73 */     this.candlesDataSequenceProvider = candlesDataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void setPriceRangeDataSequenceProvicer(AbstractDataSequenceProvider<PriceRangeDataSequence, PriceRangeData> priceRangeDataSequenceProvider) {
/*  77 */     this.priceRangeDataSequenceProvider = priceRangeDataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void setPointAndFigureDataSequenceProvider(AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> pointAndFigureDataSequenceProvider) {
/*  81 */     this.pointAndFigureDataSequenceProvider = pointAndFigureDataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void setTickBarDataSequenceProvider(AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> tickBarDataSequenceProvider) {
/*  85 */     this.tickBarDataSequenceProvider = tickBarDataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void addOrdersDataChangeListener(OrdersDataChangeListener ordersDataChangeListener) {
/*  89 */     this.ordersDataProvider.addOrdersDataChangeListener(ordersDataChangeListener);
/*     */   }
/*     */ 
/*     */   public OrderHistoricalData[] getOrdersData()
/*     */   {
/*  96 */     return getOrdersDataFor(getStartTime(), getEndTime());
/*     */   }
/*     */ 
/*     */   public IOrderUtils getOrderUtils()
/*     */   {
/* 101 */     return this.feedDataProvider.getOrderUtils();
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 106 */     this.ordersDataProvider.start();
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 111 */     this.ordersDataProvider.dispose();
/*     */   }
/*     */ 
/*     */   OrderHistoricalData[] getOrdersDataFor(long startTime, long endTime)
/*     */   {
/* 118 */     if ((startTime == -1L) || (endTime == -1L)) {
/* 119 */       this.cachedOrdersData = null;
/* 120 */       return this.nullOrdersData;
/* 121 */     }if ((this.cachedOrdersData == null) || (this.cachedStartTime != startTime) || (this.cachedEndTime != endTime)) {
/* 122 */       this.cachedOrdersData = this.ordersDataProvider.getOrdersData(startTime, endTime, this.chartState.isChartShiftActive());
/* 123 */       this.cachedStartTime = startTime;
/* 124 */       this.cachedEndTime = endTime;
/*     */     }
/* 126 */     return this.cachedOrdersData;
/*     */   }
/*     */ 
/*     */   public long getStartTime()
/*     */   {
/* 131 */     DataType dataType = this.chartState.getDataType();
/* 132 */     switch (2.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*     */     case 1:
/* 134 */       CandleData[] candlesAsk = ((TickDataSequence)this.ticksDataSequenceProvider.getDataSequence()).getOneSecCandlesAsk();
/* 135 */       if (candlesAsk.length > 0) {
/* 136 */         return candlesAsk[((TickDataSequence)this.ticksDataSequenceProvider.getDataSequence()).getOneSecExtraBefore()].time;
/*     */       }
/* 138 */       return -1L;
/*     */     case 2:
/* 142 */       CandleDataSequence sequence = (CandleDataSequence)this.candlesDataSequenceProvider.getDataSequence();
/* 143 */       return getStartTime(sequence);
/*     */     case 3:
/* 146 */       PriceRangeDataSequence sequence = (PriceRangeDataSequence)this.priceRangeDataSequenceProvider.getDataSequence();
/* 147 */       return getStartTime(sequence);
/*     */     case 4:
/* 150 */       PointAndFigureDataSequence sequence = (PointAndFigureDataSequence)this.pointAndFigureDataSequenceProvider.getDataSequence();
/* 151 */       return getStartTime(sequence);
/*     */     case 5:
/* 154 */       TickBarDataSequence sequence = (TickBarDataSequence)this.tickBarDataSequenceProvider.getDataSequence();
/* 155 */       return getStartTime(sequence);
/*     */     case 6:
/* 158 */       RenkoDataSequence sequence = (RenkoDataSequence)this.renkoDataSequenceProvider.getDataSequence();
/* 159 */       return getStartTime(sequence);
/*     */     }
/* 161 */     throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*     */   }
/*     */ 
/*     */   public long getEndTime()
/*     */   {
/* 167 */     DataType dataType = this.chartState.getDataType();
/* 168 */     switch (2.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*     */     case 1:
/* 170 */       CandleData[] candlesAsk = ((TickDataSequence)this.ticksDataSequenceProvider.getDataSequence()).getOneSecCandlesAsk();
/* 171 */       if (candlesAsk.length > 0) {
/* 172 */         return candlesAsk[(candlesAsk.length - ((TickDataSequence)this.ticksDataSequenceProvider.getDataSequence()).getOneSecExtraAfter() - 1)].time + 999L;
/*     */       }
/* 174 */       return -1L;
/*     */     case 2:
/* 178 */       CandleData[] candles = (CandleData[])((CandleDataSequence)this.candlesDataSequenceProvider.getDataSequence()).getData();
/* 179 */       if (candles.length > 0) {
/* 180 */         return DataCacheUtils.getNextCandleStartFast(this.chartState.getPeriod(), candles[(candles.length - ((CandleDataSequence)this.candlesDataSequenceProvider.getDataSequence()).getExtraAfter() - 1)].time);
/*     */       }
/* 182 */       return -1L;
/*     */     case 3:
/* 186 */       PriceRangeDataSequence sequence = (PriceRangeDataSequence)this.priceRangeDataSequenceProvider.getDataSequence();
/* 187 */       return getEndTime(sequence);
/*     */     case 4:
/* 190 */       PointAndFigureDataSequence sequence = (PointAndFigureDataSequence)this.pointAndFigureDataSequenceProvider.getDataSequence();
/* 191 */       return getEndTime(sequence);
/*     */     case 5:
/* 195 */       TickBarDataSequence sequence = (TickBarDataSequence)this.tickBarDataSequenceProvider.getDataSequence();
/* 196 */       return getEndTime(sequence);
/*     */     case 6:
/* 199 */       RenkoDataSequence sequence = (RenkoDataSequence)this.renkoDataSequenceProvider.getDataSequence();
/* 200 */       return getEndTime(sequence);
/*     */     }
/* 202 */     throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*     */   }
/*     */ 
/*     */   private <D extends Data> long getEndTime(AbstractDataSequence<D> sequence)
/*     */   {
/* 207 */     if (!sequence.isEmpty()) {
/* 208 */       return sequence.getLastData().getTime();
/*     */     }
/* 210 */     return -1L;
/*     */   }
/*     */ 
/*     */   private <D extends Data> long getStartTime(AbstractDataSequence<D> sequence)
/*     */   {
/* 215 */     if (!sequence.isEmpty()) {
/* 216 */       return sequence.getData()[sequence.getExtraBefore()].getTime();
/*     */     }
/* 218 */     return -1L;
/*     */   }
/*     */ 
/*     */   public void changeInstrument(Instrument instrument)
/*     */   {
/* 224 */     this.ordersDataProvider.changeInstrument(instrument);
/*     */   }
/*     */ 
/*     */   public void setRenkoDataSequenceProvider(AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> renkoDataSequenceProvider) {
/* 228 */     this.renkoDataSequenceProvider = renkoDataSequenceProvider;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.data.OrdersDataProviderManager
 * JD-Core Version:    0.6.0
 */