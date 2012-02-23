/*     */ package com.dukascopy.charts.data.datacache.customperiod.candle;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.AbstractCustomPeriodCreator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class CustomPeriodFromCandlesCreator extends AbstractCustomPeriodCreator
/*     */ {
/*  20 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*     */   private final Period basicPeriod;
/*     */   private CandleData currentCandleDataUnderAnalysis;
/*     */   private CandleData previouslyAnalysedCandleData;
/*     */ 
/*     */   public CustomPeriodFromCandlesCreator(Instrument instrument, Period desiredPeriod, Period basicPeriod, OfferSide offerSide)
/*     */   {
/*  36 */     super(instrument, offerSide, desiredPeriod);
/*     */ 
/*  22 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  42 */     this.basicPeriod = basicPeriod;
/*     */ 
/*  44 */     this.currentCandleDataUnderAnalysis = null;
/*  45 */     this.previouslyAnalysedCandleData = null;
/*     */   }
/*     */ 
/*     */   public CandleData analyse(CandleData data) {
/*  49 */     if (this.currentCandleDataUnderAnalysis == null) {
/*  50 */       startNewCandleAnalysis(data);
/*     */     }
/*     */     else {
/*  53 */       long currentCandleStartTimeForData = DataCacheUtils.getCandleStartFast(getDesiredPeriod(), data.getTime());
/*  54 */       long nextCandleStartTimeForData = DataCacheUtils.getCandleStartFast(getDesiredPeriod(), data.getTime() + getBasicPeriod().getInterval());
/*     */ 
/*  56 */       if ((this.currentCandleDataUnderAnalysis.getTime() == currentCandleStartTimeForData) && (nextCandleStartTimeForData > currentCandleStartTimeForData)) {
/*  57 */         continueCurrentCandleAnalysis(data);
/*  58 */         CandleData candleDataResult = finishCurrentCandleAnalysis();
/*  59 */         fireNewCandle(candleDataResult);
/*  60 */         return candleDataResult;
/*     */       }
/*  62 */       if (currentCandleStartTimeForData > this.currentCandleDataUnderAnalysis.getTime())
/*     */       {
/*  66 */         CandleData candleDataResult = finishCurrentCandleAnalysis();
/*  67 */         fireNewCandle(candleDataResult);
/*  68 */         startNewCandleAnalysis(data);
/*  69 */         return candleDataResult;
/*     */       }
/*     */ 
/*  72 */       continueCurrentCandleAnalysis(data);
/*     */     }
/*     */ 
/*  76 */     return null;
/*     */   }
/*     */ 
/*     */   private CandleData finishCurrentCandleAnalysis() {
/*  80 */     if ((this.currentCandleDataUnderAnalysis != null) && (Double.isNaN(this.currentCandleDataUnderAnalysis.getOpen())))
/*     */     {
/*  87 */       this.currentCandleDataUnderAnalysis.setOpen(this.currentCandleDataUnderAnalysis.getClose());
/*     */     }
/*     */ 
/*  90 */     this.previouslyAnalysedCandleData = this.currentCandleDataUnderAnalysis;
/*  91 */     this.currentCandleDataUnderAnalysis = null;
/*  92 */     return this.previouslyAnalysedCandleData;
/*     */   }
/*     */ 
/*     */   private void continueCurrentCandleAnalysis(CandleData data)
/*     */   {
/*  97 */     this.currentCandleDataUnderAnalysis.setVolume(round(this.currentCandleDataUnderAnalysis.getVolume() + data.getVolume()));
/*  98 */     this.currentCandleDataUnderAnalysis.setClose(data.getClose());
/*     */ 
/* 100 */     if (this.currentCandleDataUnderAnalysis.getHigh() < data.getHigh()) {
/* 101 */       this.currentCandleDataUnderAnalysis.setHigh(data.getHigh());
/*     */     }
/*     */ 
/* 104 */     if (this.currentCandleDataUnderAnalysis.getLow() > data.getLow()) {
/* 105 */       this.currentCandleDataUnderAnalysis.setLow(data.getLow());
/*     */     }
/*     */ 
/* 108 */     boolean isFlat = isFlat(data);
/* 109 */     if ((Double.isNaN(this.currentCandleDataUnderAnalysis.getOpen())) && ((!isFlat) || ((isFlat) && (data.getOpen() != this.currentCandleDataUnderAnalysis.getClose()))))
/*     */     {
/* 116 */       this.currentCandleDataUnderAnalysis.setOpen(data.getOpen());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startNewCandleAnalysis(CandleData data)
/*     */   {
/* 126 */     double openPrice = data.getOpen();
/*     */ 
/* 128 */     if (isFlat(data))
/*     */     {
/* 141 */       openPrice = (0.0D / 0.0D);
/*     */     }
/*     */ 
/* 144 */     this.currentCandleDataUnderAnalysis = new CandleData();
/*     */ 
/* 146 */     this.currentCandleDataUnderAnalysis.setClose(data.getClose());
/* 147 */     this.currentCandleDataUnderAnalysis.setHigh(data.getHigh());
/* 148 */     this.currentCandleDataUnderAnalysis.setLow(data.getLow());
/* 149 */     this.currentCandleDataUnderAnalysis.setOpen(openPrice);
/* 150 */     this.currentCandleDataUnderAnalysis.setVolume(data.getVolume());
/*     */ 
/* 152 */     this.currentCandleDataUnderAnalysis.setTime(DataCacheUtils.getCandleStartFast(getDesiredPeriod(), data.getTime()));
/*     */   }
/*     */ 
/*     */   protected boolean isFlat(CandleData data)
/*     */   {
/* 169 */     return (data.getClose() == data.getOpen()) && (data.getOpen() == data.getHigh()) && (data.getHigh() == data.getLow()) && (data.getVolume() == 0.0D);
/*     */   }
/*     */ 
/*     */   public CandleData getCurrentCandleDataUnderAnalysis()
/*     */   {
/* 177 */     return this.currentCandleDataUnderAnalysis;
/*     */   }
/*     */ 
/*     */   public CandleData[] completeAnalysis() {
/* 181 */     CandleData candleDataResult = finishCurrentCandleAnalysis();
/*     */ 
/* 183 */     CandleData[] result = { candleDataResult };
/* 184 */     return result;
/*     */   }
/*     */ 
/*     */   private Period getBasicPeriod() {
/* 188 */     return this.basicPeriod;
/*     */   }
/*     */ 
/*     */   private double round(double value) {
/* 192 */     return StratUtils.round(value, 8);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodFromCandlesCreator
 * JD-Core Version:    0.6.0
 */