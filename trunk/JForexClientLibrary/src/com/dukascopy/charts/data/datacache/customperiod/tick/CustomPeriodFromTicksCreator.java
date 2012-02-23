/*     */ package com.dukascopy.charts.data.datacache.customperiod.tick;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.AbstractCustomPeriodCreator;
/*     */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class CustomPeriodFromTicksCreator extends AbstractCustomPeriodCreator
/*     */ {
/*  22 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private final int desiredCandlesCount;
/*     */   private final boolean inverseOrder;
/*     */   private final Filter filter;
/*     */   private Long desiredFirstDataTime;
/*     */   private final Double firstDataValue;
/*     */   private String desiredFirstDataTimeStr;
/*     */   private CandleData currentCandleDataUnderAnalysis;
/*     */   private CandleData previouslyAnalysedCandleData;
/*     */   protected CandleData[] result;
/*     */   private int lastElementIndex;
/*     */   private Long firstTime;
/*     */   private Long lastTime;
/*     */   private int loadedCandlesCount;
/*     */   private final IFilterManager filterManager;
/*     */ 
/*     */   public CustomPeriodFromTicksCreator(Instrument instrument, OfferSide offerSide, int desiredCandlesCount, Period desiredPeriod, Filter filter, boolean inverseOrder, Long desiredFirstDataTime, Double firstDataValue, IFilterManager filterManager)
/*     */   {
/*  63 */     super(instrument, offerSide, desiredPeriod);
/*     */ 
/*  24 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */ 
/*  69 */     this.desiredCandlesCount = desiredCandlesCount;
/*  70 */     this.inverseOrder = inverseOrder;
/*  71 */     this.filter = filter;
/*  72 */     this.firstDataValue = firstDataValue;
/*  73 */     this.desiredFirstDataTime = (desiredFirstDataTime != null ? new Long(DataCacheUtils.getCandleStartFast(desiredPeriod, desiredFirstDataTime.longValue())) : null);
/*     */ 
/*  78 */     this.filterManager = filterManager;
/*     */ 
/*  80 */     reset();
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  84 */     if (this.desiredFirstDataTime != null) {
/*  85 */       this.desiredFirstDataTimeStr = DATE_FORMAT.format(this.desiredFirstDataTime);
/*     */     }
/*     */ 
/*  88 */     this.lastElementIndex = -1;
/*  89 */     this.firstTime = null;
/*  90 */     this.lastTime = null;
/*  91 */     this.loadedCandlesCount = 0;
/*  92 */     this.result = new CandleData[this.desiredCandlesCount];
/*     */   }
/*     */ 
/*     */   public boolean analyseTickData(TickData data)
/*     */   {
/*  97 */     if (data == null) {
/*  98 */       return false;
/*     */     }
/*     */ 
/* 101 */     long candleStartTime = getCandleStartFast(data);
/*     */ 
/* 103 */     if (this.firstTime == null) {
/* 104 */       this.firstTime = new Long(candleStartTime);
/*     */     }
/*     */ 
/* 107 */     this.lastTime = new Long(candleStartTime);
/*     */ 
/* 109 */     if (this.currentCandleDataUnderAnalysis == null) {
/* 110 */       if ((this.desiredFirstDataTime != null) && (timesBelongToDifferentCandles(candleStartTime, this.desiredFirstDataTime.longValue())))
/*     */       {
/* 114 */         double price = getFirstDataValue() != null ? getFirstDataValue().doubleValue() : getPrice(data, getOfferSide());
/*     */ 
/* 119 */         this.currentCandleDataUnderAnalysis = new CandleData(this.desiredFirstDataTime.longValue(), price, price, price, price, 0.0D);
/*     */ 
/* 128 */         boolean result = finishCurrentCandleCreation(data, candleStartTime, this.previouslyAnalysedCandleData);
/*     */ 
/* 134 */         if (result)
/* 135 */           return true;
/*     */       }
/*     */       else
/*     */       {
/* 139 */         this.currentCandleDataUnderAnalysis = startNewCandleData(data, candleStartTime, true);
/*     */       }
/*     */     }
/* 142 */     else if (timesBelongToDifferentCandles(candleStartTime, this.currentCandleDataUnderAnalysis.getTime())) {
/* 143 */       boolean result = finishCurrentCandleCreation(data, candleStartTime, this.previouslyAnalysedCandleData);
/*     */ 
/* 149 */       if (result)
/* 150 */         return true;
/*     */     }
/*     */     else
/*     */     {
/* 154 */       continueCurrentCandleAnalysis(data);
/*     */     }
/*     */ 
/* 157 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean finishCurrentCandleCreation(TickData data, long candleStartTime, CandleData previouslyAnalysedCandleData)
/*     */   {
/* 165 */     CandleData[] flats = null;
/* 166 */     if (!Filter.ALL_FLATS.equals(getFilter()))
/*     */     {
/* 171 */       flats = checkPreviousFlatsIfAny(data, this.currentCandleDataUnderAnalysis, getDesiredPeriod());
/*     */     }
/*     */ 
/* 174 */     CandleData prevCandle = previouslyAnalysedCandleData;
/* 175 */     CandleData candleDataResult = finishCurrentCandleAnalysis();
/*     */ 
/* 177 */     if ((!Filter.ALL_FLATS.equals(getFilter())) || (!isFlat(candleDataResult, prevCandle)))
/*     */     {
/* 183 */       if ((!Filter.WEEKENDS.equals(getFilter())) || (!this.filterManager.isWeekendTime(candleDataResult.getTime(), Period.TICK)))
/*     */       {
/* 189 */         addCompletedCandle(candleDataResult);
/*     */       }
/*     */ 
/* 192 */       if (flats != null) {
/* 193 */         for (int i = 0; i < flats.length; i++) {
/* 194 */           if (isItEnoughCandles(this.loadedCandlesCount))
/*     */           {
/* 198 */             return true;
/*     */           }
/* 200 */           CandleData flat = flats[i];
/*     */ 
/* 202 */           if ((Filter.WEEKENDS.equals(getFilter())) && (this.filterManager.isWeekendTime(flat.getTime(), Period.TICK)))
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 208 */           addCompletedCandle(flat);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 214 */     if (isItEnoughCandles(this.loadedCandlesCount))
/*     */     {
/* 218 */       return true;
/*     */     }
/*     */ 
/* 224 */     this.currentCandleDataUnderAnalysis = startNewCandleData(data, candleStartTime, false);
/*     */ 
/* 226 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean timesBelongToDifferentCandles(long time1, long time2) {
/* 230 */     boolean value = Math.abs(time1 - time2) >= getDesiredPeriod().getInterval();
/* 231 */     return value;
/*     */   }
/*     */ 
/*     */   private long getCandleStartFast(TickData tickData) {
/* 235 */     long time = tickData.getTime();
/* 236 */     long candleStartTime = DataCacheUtils.getCandleStartFast(getDesiredPeriod(), time);
/* 237 */     return candleStartTime;
/*     */   }
/*     */ 
/*     */   protected void addCompletedCandle(CandleData candleData) {
/* 241 */     this.loadedCandlesCount += 1;
/* 242 */     this.lastElementIndex += 1;
/* 243 */     this.result[this.lastElementIndex] = candleData;
/*     */ 
/* 245 */     fireNewCandle(candleData);
/*     */   }
/*     */ 
/*     */   private CandleData[] checkPreviousFlatsIfAny(TickData data, CandleData currentCandleDataUnderAnalysis, Period period)
/*     */   {
/* 253 */     if (currentCandleDataUnderAnalysis == null) {
/* 254 */       return null;
/*     */     }
/*     */ 
/* 257 */     long targetTime = getCandleStartFast(data);
/* 258 */     long currentTime = currentCandleDataUnderAnalysis.getTime();
/*     */ 
/* 260 */     long candlesCountInTimePeriod = Math.abs(targetTime - currentTime) / period.getInterval();
/* 261 */     candlesCountInTimePeriod -= 1L;
/*     */ 
/* 263 */     if (candlesCountInTimePeriod > 0L) {
/* 264 */       CandleData[] result = new CandleData[(int)candlesCountInTimePeriod];
/* 265 */       long time = 0L;
/* 266 */       double value = -1.0D;
/*     */ 
/* 268 */       if (!getInverseOrder()) {
/* 269 */         if ((getFirstDataValue() != null) && (getLoadedCandleCount() == 0) && (this.firstTime != null) && (this.firstTime.longValue() != currentCandleDataUnderAnalysis.time))
/*     */         {
/* 275 */           value = getFirstDataValue().doubleValue();
/*     */         }
/*     */         else {
/* 278 */           value = currentCandleDataUnderAnalysis.getClose();
/*     */         }
/*     */       }
/*     */       else {
/* 282 */         value = getPrice(data, getOfferSide());
/*     */       }
/*     */ 
/* 285 */       for (int i = 0; i < result.length; i++) {
/* 286 */         if (!getInverseOrder())
/*     */         {
/* 290 */           time = currentTime + (i + 1) * period.getInterval();
/*     */         }
/*     */         else
/*     */         {
/* 296 */           time = currentTime - (i + 1) * period.getInterval();
/*     */         }
/* 298 */         CandleData candleData = createFlat(value, time);
/* 299 */         result[i] = candleData;
/*     */       }
/* 301 */       return result;
/*     */     }
/*     */ 
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */   private CandleData createFlat(double value, long time)
/*     */   {
/* 309 */     CandleData candleData = new CandleData();
/* 310 */     candleData.setClose(value);
/* 311 */     candleData.setOpen(value);
/* 312 */     candleData.setHigh(value);
/* 313 */     candleData.setLow(value);
/* 314 */     candleData.setVolume(0.0D);
/* 315 */     candleData.setTime(time);
/* 316 */     return candleData;
/*     */   }
/*     */ 
/*     */   protected boolean isFlat(CandleData candleDataResult, CandleData previousCandleData) {
/* 320 */     boolean isFlat = (candleDataResult.getOpen() == candleDataResult.getClose()) && (candleDataResult.getHigh() == candleDataResult.getLow()) && (candleDataResult.getOpen() == candleDataResult.getLow()) && (candleDataResult.getVolume() <= 0.0D);
/*     */ 
/* 325 */     if ((!isFlat) || (previousCandleData == null)) {
/* 326 */       return isFlat;
/*     */     }
/*     */ 
/* 332 */     double previousCandleEndValue = !getInverseOrder() ? previousCandleData.getClose() : previousCandleData.getOpen();
/*     */ 
/* 336 */     boolean value = previousCandleEndValue == candleDataResult.getOpen();
/* 337 */     return value;
/*     */   }
/*     */ 
/*     */   private void continueCurrentCandleAnalysis(TickData data)
/*     */   {
/* 342 */     this.currentCandleDataUnderAnalysis.setVolume(round(this.currentCandleDataUnderAnalysis.getVolume() + getVolume(data, getOfferSide())));
/*     */ 
/* 344 */     double value = getPrice(data, getOfferSide());
/*     */ 
/* 346 */     if (getInverseOrder()) {
/* 347 */       this.currentCandleDataUnderAnalysis.setOpen(value);
/*     */     }
/*     */     else {
/* 350 */       this.currentCandleDataUnderAnalysis.setClose(value);
/*     */     }
/*     */ 
/* 354 */     if (this.currentCandleDataUnderAnalysis.getLow() > value) {
/* 355 */       this.currentCandleDataUnderAnalysis.setLow(value);
/*     */     }
/* 357 */     if (this.currentCandleDataUnderAnalysis.getHigh() < value)
/* 358 */       this.currentCandleDataUnderAnalysis.setHigh(value);
/*     */   }
/*     */ 
/*     */   private CandleData finishCurrentCandleAnalysis()
/*     */   {
/* 363 */     this.previouslyAnalysedCandleData = this.currentCandleDataUnderAnalysis;
/* 364 */     this.currentCandleDataUnderAnalysis = null;
/*     */ 
/* 366 */     return this.previouslyAnalysedCandleData;
/*     */   }
/*     */ 
/*     */   private CandleData startNewCandleData(TickData data, long time, boolean firstTime) {
/* 370 */     return startNewCandleData(time, getPrice(data, getOfferSide()), getVolume(data, getOfferSide()), firstTime);
/*     */   }
/*     */ 
/*     */   private CandleData startNewCandleData(long time, double value, double volume, boolean firstTime) {
/* 374 */     CandleData candleData = new CandleData();
/*     */ 
/* 376 */     if ((firstTime) && (getDesiredFirstDataTime() != null)) {
/* 377 */       time = getDesiredFirstDataTime().longValue();
/*     */     }
/*     */ 
/* 380 */     candleData.setTime(time);
/* 381 */     candleData.setOpen(value);
/* 382 */     candleData.setClose(value);
/* 383 */     candleData.setHigh(value);
/* 384 */     candleData.setLow(value);
/* 385 */     candleData.setVolume(volume);
/*     */ 
/* 387 */     return candleData;
/*     */   }
/*     */ 
/*     */   protected boolean isItEnoughCandles(int loadedCandleCount)
/*     */   {
/* 392 */     return loadedCandleCount == getDesiredCandlesCount();
/*     */   }
/*     */ 
/*     */   private double getVolume(TickData tickData, OfferSide offerSide) {
/* 396 */     if (OfferSide.ASK.equals(offerSide)) {
/* 397 */       return tickData.getAskVolume();
/*     */     }
/*     */ 
/* 400 */     return tickData.getBidVolume();
/*     */   }
/*     */ 
/*     */   private double getPrice(TickData tickData, OfferSide offerSide)
/*     */   {
/* 405 */     if (OfferSide.ASK.equals(offerSide)) {
/* 406 */       return tickData.getAsk();
/*     */     }
/*     */ 
/* 409 */     return tickData.getBid();
/*     */   }
/*     */ 
/*     */   public int getDesiredCandlesCount()
/*     */   {
/* 418 */     return this.desiredCandlesCount;
/*     */   }
/*     */ 
/*     */   public boolean allDesiredDataLoaded() {
/* 422 */     return getLoadedCandleCount() == getDesiredCandlesCount();
/*     */   }
/*     */ 
/*     */   public Long getFirstTime() {
/* 426 */     return this.firstTime;
/*     */   }
/*     */ 
/*     */   public Long getLastTime() {
/* 430 */     return this.lastTime;
/*     */   }
/*     */ 
/*     */   public int getLoadedCandleCount() {
/* 434 */     return this.loadedCandlesCount;
/*     */   }
/*     */ 
/*     */   public boolean getInverseOrder() {
/* 438 */     return this.inverseOrder;
/*     */   }
/*     */ 
/*     */   public CandleData[] getResult() {
/* 442 */     return this.result;
/*     */   }
/*     */ 
/*     */   protected int getLastElementIndex()
/*     */   {
/* 447 */     return this.lastElementIndex;
/*     */   }
/*     */ 
/*     */   protected void setLastElementIndex(int lastElementIndex) {
/* 451 */     this.lastElementIndex = lastElementIndex;
/*     */   }
/*     */ 
/*     */   protected Filter getFilter() {
/* 455 */     return this.filter;
/*     */   }
/*     */ 
/*     */   protected Long getDesiredFirstDataTime() {
/* 459 */     return this.desiredFirstDataTime;
/*     */   }
/*     */ 
/*     */   protected Double getFirstDataValue() {
/* 463 */     return this.firstDataValue;
/*     */   }
/*     */ 
/*     */   private double round(double value) {
/* 467 */     return StratUtils.round(value, 6);
/*     */   }
/*     */ 
/*     */   public CandleData getLastData() {
/* 471 */     if ((getResult() != null) && (getLastElementIndex() > -1) && (getLastElementIndex() < getResult().length))
/*     */     {
/* 476 */       return getResult()[getLastElementIndex()];
/*     */     }
/* 478 */     return null;
/*     */   }
/*     */ 
/*     */   public CandleData getCurrentCandleDataUnderAnalysis() {
/* 482 */     return this.currentCandleDataUnderAnalysis;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.tick.CustomPeriodFromTicksCreator
 * JD-Core Version:    0.6.0
 */