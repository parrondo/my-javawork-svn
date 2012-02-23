/*     */ package com.dukascopy.charts.data.datacache.customperiod.candle;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CustomPeriodCandleLiveFeedListener
/*     */   implements LiveFeedListener
/*     */ {
/*     */   private List<CandleData> collectedDatas;
/*     */   private CustomPeriodFromCandlesCreator customPeriodCreator;
/*     */   private final Instrument instrument;
/*     */   private final OfferSide offerSide;
/*     */   private final LiveFeedListener originalLiveFeedListener;
/*     */   private final int beforeNumberOfCandles;
/*     */   private final int afterNumberOfCandles;
/*     */   private final long dataSequenceStartTime;
/*     */   private final long dataSequenceEndTime;
/*     */   private final long time;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */ 
/*     */   public CustomPeriodCandleLiveFeedListener(Instrument instrument, OfferSide offerSide, CustomPeriodFromCandlesCreator customPeriodFromCandlesCreator, LiveFeedListener originalLiveFeedListener, IFeedDataProvider feedDataProvider, long dataSequenceStartTime, long dataSequenceEndTime, long time)
/*     */   {
/*  48 */     this(instrument, offerSide, customPeriodFromCandlesCreator, originalLiveFeedListener, feedDataProvider, dataSequenceStartTime, dataSequenceEndTime, -1, -1, time);
/*     */   }
/*     */ 
/*     */   public CustomPeriodCandleLiveFeedListener(Instrument instrument, OfferSide offerSide, CustomPeriodFromCandlesCreator customPeriodFromCandlesCreator, LiveFeedListener originalLiveFeedListener, IFeedDataProvider feedDataProvider, int beforeNumberOfCandles, int afterNumberOfCandles, long time)
/*     */   {
/*  72 */     this(instrument, offerSide, customPeriodFromCandlesCreator, originalLiveFeedListener, feedDataProvider, -1L, -1L, beforeNumberOfCandles, afterNumberOfCandles, time);
/*     */   }
/*     */ 
/*     */   public CustomPeriodCandleLiveFeedListener(Instrument instrument, OfferSide offerSide, CustomPeriodFromCandlesCreator customPeriodFromCandlesCreator, LiveFeedListener originalLiveFeedListener, IFeedDataProvider feedDataProvider, long dataSequenceStartTime, long dataSequenceEndTime, int beforeNumberOfCandles, int afterNumberOfCandles, long time)
/*     */   {
/*  98 */     this.instrument = instrument;
/*  99 */     this.offerSide = offerSide;
/*     */ 
/* 101 */     this.customPeriodCreator = customPeriodFromCandlesCreator;
/* 102 */     this.originalLiveFeedListener = originalLiveFeedListener;
/*     */ 
/* 104 */     this.feedDataProvider = feedDataProvider;
/*     */ 
/* 106 */     this.beforeNumberOfCandles = beforeNumberOfCandles;
/* 107 */     this.afterNumberOfCandles = afterNumberOfCandles;
/*     */ 
/* 109 */     this.dataSequenceStartTime = dataSequenceStartTime;
/* 110 */     this.dataSequenceEndTime = dataSequenceEndTime;
/*     */ 
/* 112 */     this.time = time;
/*     */   }
/*     */ 
/*     */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */   {
/* 127 */     CandleData candleData = new CandleData(time, open, close, low, high, vol);
/* 128 */     CandleData analysedCandle = getCustomPeriodCreator().analyse(candleData);
/*     */ 
/* 130 */     if ((analysedCandle != null) && (
/* 131 */       (getDataSequenceEndTime() <= -1L) || (getDataSequenceStartTime() <= -1L)))
/*     */     {
/* 140 */       desiredPeriodDataCreated(analysedCandle);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void fireNewData(CandleData data)
/*     */   {
/* 158 */     getOriginalLiveFeedListener().newCandle(getInstrument(), getCustomPeriodCreator().getDesiredPeriod(), getOfferSide(), data.getTime(), data.getOpen(), data.getClose(), data.getLow(), data.getHigh(), data.getVolume());
/*     */   }
/*     */ 
/*     */   protected void desiredPeriodDataCreated(CandleData data)
/*     */   {
/* 172 */     if (data != null)
/* 173 */       getCollectedDatas().add(data);
/*     */   }
/*     */ 
/*     */   public List<CandleData> getCollectedDatas()
/*     */   {
/* 181 */     if (this.collectedDatas == null) {
/* 182 */       this.collectedDatas = new ArrayList(4500);
/*     */     }
/* 184 */     return this.collectedDatas;
/*     */   }
/*     */ 
/*     */   protected CustomPeriodFromCandlesCreator getCustomPeriodCreator() {
/* 188 */     return this.customPeriodCreator;
/*     */   }
/*     */ 
/*     */   protected Instrument getInstrument() {
/* 192 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   protected OfferSide getOfferSide() {
/* 196 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   protected LiveFeedListener getOriginalLiveFeedListener() {
/* 200 */     return this.originalLiveFeedListener;
/*     */   }
/*     */ 
/*     */   protected int getBeforeNumberOfCandles() {
/* 204 */     return this.beforeNumberOfCandles;
/*     */   }
/*     */ 
/*     */   protected int getAfterNumberOfCandles() {
/* 208 */     return this.afterNumberOfCandles;
/*     */   }
/*     */ 
/*     */   protected long getDataSequenceStartTime() {
/* 212 */     return this.dataSequenceStartTime;
/*     */   }
/*     */ 
/*     */   protected long getDataSequenceEndTime() {
/* 216 */     return this.dataSequenceEndTime;
/*     */   }
/*     */ 
/*     */   protected long getTime() {
/* 220 */     return this.time;
/*     */   }
/*     */ 
/*     */   protected IFeedDataProvider getFeedDataProvider() {
/* 224 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void finishLoading(boolean allDataLoaded, long startTime, long endTime, long currentTime)
/*     */   {
/* 236 */     CandleData candleDataInProgress = this.feedDataProvider.getInProgressCandle(getInstrument(), getCustomPeriodCreator().getDesiredPeriod(), getOfferSide());
/* 237 */     CandleData[] lastData = getCustomPeriodCreator().completeAnalysis();
/* 238 */     if ((lastData != null) && (candleDataInProgress != null)) {
/* 239 */       for (CandleData data : lastData) {
/* 240 */         if (data == null) {
/*     */           continue;
/*     */         }
/* 243 */         if ((candleDataInProgress != null) && (DataCacheUtils.getPreviousCandleStartFast(getCustomPeriodCreator().getDesiredPeriod(), candleDataInProgress.getTime()) == data.getTime())) {
/* 244 */           desiredPeriodDataCreated(data);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 253 */     if ((getBeforeNumberOfCandles() > -1) && (getAfterNumberOfCandles() > -1)) {
/* 254 */       sendDataForNumberOfCandles();
/*     */     }
/* 256 */     else if ((getDataSequenceEndTime() <= -1L) || (getDataSequenceStartTime() <= -1L))
/*     */     {
/* 264 */       throw new IllegalArgumentException("We can work only with candle concrete numbers or with data sequence times!");
/*     */     }
/*     */ 
/* 267 */     getCollectedDatas().clear();
/*     */   }
/*     */ 
/*     */   private void sendDataForDataSequenceTimes() {
/* 271 */     CandleData[] datas = (CandleData[])getCollectedDatas().toArray(new CandleData[getCollectedDatas().size()]);
/* 272 */     for (int i = datas.length - 1; i >= 0; i--) {
/* 273 */       CandleData data = datas[i];
/* 274 */       if ((getDataSequenceStartTime() <= data.getTime()) && (data.getTime() <= getDataSequenceEndTime()))
/* 275 */         fireNewData(data);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sendDataForNumberOfCandles()
/*     */   {
/* 281 */     int dataTotalCount = getCollectedDatas().size();
/*     */ 
/* 283 */     if (dataTotalCount < 0) {
/* 284 */       return;
/*     */     }
/*     */ 
/* 287 */     int dataIndex = 0;
/* 288 */     int timeIndex = -1;
/* 289 */     CandleData prevData = null;
/*     */ 
/* 291 */     CandleData[] datas = (CandleData[])getCollectedDatas().toArray(new CandleData[getCollectedDatas().size()]);
/*     */ 
/* 296 */     for (int i = 0; i < datas.length; i++) {
/* 297 */       CandleData data = datas[i];
/* 298 */       if ((getTime() == data.getTime()) || ((prevData != null) && (prevData.getTime() <= getTime()) && (getTime() <= data.getTime()))) {
/* 299 */         timeIndex = dataIndex;
/*     */       }
/* 301 */       dataIndex++;
/* 302 */       prevData = data;
/*     */     }
/*     */ 
/* 305 */     if (timeIndex < 0)
/*     */     {
/* 309 */       timeIndex = datas.length - 1;
/*     */     }
/*     */ 
/* 312 */     int startIndex = timeIndex - getBeforeNumberOfCandles();
/* 313 */     startIndex = startIndex < 0 ? 0 : startIndex;
/*     */ 
/* 315 */     int datasCount = startIndex + getBeforeNumberOfCandles() + getAfterNumberOfCandles() + 1;
/* 316 */     if (datasCount > dataTotalCount) {
/* 317 */       datasCount = dataTotalCount;
/* 318 */       startIndex = dataTotalCount - (getBeforeNumberOfCandles() + getAfterNumberOfCandles());
/* 319 */       startIndex = startIndex < 0 ? 0 : startIndex;
/*     */     }
/*     */ 
/* 322 */     for (int i = startIndex; i < datasCount; i++) {
/* 323 */       CandleData candleData = datas[i];
/* 324 */       fireNewData(candleData);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodCandleLiveFeedListener
 * JD-Core Version:    0.6.0
 */