/*     */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public abstract class AbstractFantomablePriceAggregationCreator<D extends AbstractPriceAggregationData, SD extends Data, L extends IPriceAggregationLiveFeedListener<D>> extends AbstractPriceAggregationCreator<D, SD, L>
/*     */ {
/*     */   public AbstractFantomablePriceAggregationCreator(Instrument instrument, OfferSide offerSide, L listener, int desiredBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  28 */     super(instrument, offerSide, listener, desiredBarsCount, liveCreation, directOrder);
/*     */   }
/*     */ 
/*     */   public AbstractFantomablePriceAggregationCreator(Instrument instrument, OfferSide offerSide, int desiredBarsCount, boolean liveCreation, boolean directOrder)
/*     */   {
/*  45 */     this(instrument, offerSide, null, desiredBarsCount, liveCreation, directOrder);
/*     */   }
/*     */ 
/*     */   protected D[] checkAndCreatePhantomBars(D previousBar, TickData tickData, double range)
/*     */   {
/*  63 */     double tickPrice = getPrice(tickData);
/*     */ 
/*  67 */     previousBar = fillIncompleteBarWithPhantomPrice(previousBar, tickPrice, tickData.getTime(), range);
/*     */ 
/*  72 */     double phantomOpenPrice = getPhantomOpenPrice(previousBar, tickPrice, range);
/*     */ 
/*  78 */     double difference = round(Math.abs(phantomOpenPrice - tickPrice));
/*     */ 
/*  80 */     if (previousBar.getTime() == previousBar.getEndTime()) {
/*  81 */       previousBar = setCloseTime(previousBar, appendTime(getCloseTime(previousBar), 1L));
/*     */     }
/*     */ 
/*  84 */     if (difference < range)
/*     */     {
/*  88 */       return createArray(previousBar);
/*     */     }
/*     */ 
/*  91 */     long phantomCount = Math.round(difference / range - 0.5D) + 1L;
/*     */ 
/*  93 */     AbstractPriceAggregationData[] result = createArray((int)phantomCount);
/*     */ 
/*  95 */     result[0] = previousBar;
/*  96 */     long time = getCloseTime(previousBar);
/*  97 */     int sign = tickPrice > phantomOpenPrice ? 1 : -1;
/*     */ 
/*  99 */     for (int i = 1; i < phantomCount; i++) {
/* 100 */       time = appendTime(time, 1L);
/*     */ 
/* 102 */       if (time == tickData.getTime())
/*     */       {
/* 106 */         result = (AbstractPriceAggregationData[])Arrays.copyOf(result, i);
/* 107 */         break;
/*     */       }
/*     */ 
/* 112 */       phantomOpenPrice = round(phantomOpenPrice);
/*     */ 
/* 116 */       AbstractPriceAggregationData prd = createBar(phantomOpenPrice, 0.0D, time, 0L, result[(i - 1)]);
/*     */ 
/* 120 */       phantomOpenPrice += sign * range;
/*     */ 
/* 124 */       phantomOpenPrice = round(phantomOpenPrice);
/*     */ 
/* 128 */       time = appendTime(time, 1L);
/* 129 */       prd = continueBar(prd, phantomOpenPrice, new Double(0.0D), new Long(time), 0L);
/* 130 */       result[i] = prd;
/*     */     }
/*     */ 
/* 133 */     return result;
/*     */   }
/*     */ 
/*     */   protected double getPhantomOpenPrice(D previousBar, double tickPrice, double range)
/*     */   {
/* 142 */     double openPrice = getOpenPrice(previousBar);
/* 143 */     double closePrice = getClosePrice(previousBar);
/*     */ 
/* 145 */     double priceDiff = Math.abs(openPrice - closePrice);
/* 146 */     double rangeCorrection = 0.0D;
/*     */ 
/* 148 */     if (priceDiff < range) {
/* 149 */       rangeCorrection = range - priceDiff;
/*     */     }
/*     */ 
/* 152 */     if (openPrice > closePrice) {
/* 153 */       if (closePrice < tickPrice)
/*     */       {
/* 159 */         return openPrice + rangeCorrection;
/*     */       }
/*     */ 
/* 168 */       return closePrice - rangeCorrection;
/*     */     }
/*     */ 
/* 172 */     if (closePrice < tickPrice)
/*     */     {
/* 178 */       return closePrice + rangeCorrection;
/*     */     }
/*     */ 
/* 187 */     return openPrice - rangeCorrection;
/*     */   }
/*     */ 
/*     */   private long appendTime(long time, long timeToAppend)
/*     */   {
/* 193 */     return isDirectOrder() ? time + timeToAppend : time - timeToAppend;
/*     */   }
/*     */ 
/*     */   protected D fillIncompleteBarWithPhantomPrice(D previousBar, double price, long time, double range)
/*     */   {
/* 209 */     Double phantomPrice = null;
/*     */ 
/* 211 */     if (previousBar.getLow() == previousBar.getHigh()) {
/* 212 */       if (previousBar.getLow() > price) {
/* 213 */         phantomPrice = new Double(Math.max(previousBar.getLow() - range, price));
/*     */       }
/*     */       else {
/* 216 */         phantomPrice = new Double(Math.min(previousBar.getLow() + range, price));
/*     */       }
/*     */ 
/*     */     }
/* 220 */     else if (Math.abs(previousBar.getLow() - previousBar.getHigh()) < range) {
/* 221 */       if (previousBar.getLow() > price) {
/* 222 */         phantomPrice = new Double(previousBar.getHigh() - range);
/*     */       }
/*     */       else {
/* 225 */         phantomPrice = new Double(previousBar.getLow() + range);
/*     */       }
/*     */     }
/*     */ 
/* 229 */     if (phantomPrice != null) {
/* 230 */       double roundedPhantomPrice = round(phantomPrice.doubleValue());
/* 231 */       boolean theSameTradingSession = DataCacheUtils.isTheSameTradingSession(previousBar.getTime(), time);
/* 232 */       if (theSameTradingSession) {
/* 233 */         previousBar = continueBar(previousBar, roundedPhantomPrice, null, null, 0L);
/*     */       }
/*     */     }
/*     */ 
/* 237 */     return previousBar;
/*     */   }
/*     */ 
/*     */   private double getClosePrice(D bar) {
/* 241 */     return isDirectOrder() ? bar.getClose() : bar.getOpen();
/*     */   }
/*     */ 
/*     */   private double getOpenPrice(D bar) {
/* 245 */     return isDirectOrder() ? bar.getOpen() : bar.getClose();
/*     */   }
/*     */ 
/*     */   private long getCloseTime(D bar) {
/* 249 */     return isDirectOrder() ? bar.getEndTime() : bar.getTime();
/*     */   }
/*     */ 
/*     */   private D setCloseTime(D previousBar, long time) {
/* 253 */     if (isDirectOrder()) {
/* 254 */       previousBar.setEndTime(time);
/*     */     }
/*     */     else {
/* 257 */       previousBar.setTime(time);
/*     */     }
/* 259 */     return previousBar;
/*     */   }
/*     */ 
/*     */   protected abstract D createBar(double paramDouble1, double paramDouble2, long paramLong1, long paramLong2, D paramD);
/*     */ 
/*     */   protected abstract D continueBar(D paramD, double paramDouble, Double paramDouble1, Long paramLong, long paramLong1);
/*     */ 
/*     */   protected abstract D[] createArray(D paramD);
/*     */ 
/*     */   protected abstract D[] createArray(int paramInt);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.AbstractFantomablePriceAggregationCreator
 * JD-Core Version:    0.6.0
 */