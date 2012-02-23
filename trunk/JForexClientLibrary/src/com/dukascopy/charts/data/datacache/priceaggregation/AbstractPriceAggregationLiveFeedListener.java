/*     */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class AbstractPriceAggregationLiveFeedListener<D extends AbstractPriceAggregationData, SD extends Data, L extends IPriceAggregationLiveFeedListener<D>, C extends IPriceAggregationCreator<D, SD, L>>
/*     */   implements LiveFeedListener
/*     */ {
/*     */   private final C creator;
/*  29 */   protected List<SD> collectedDatas = new LinkedList();
/*     */   private boolean priceRangesCreationFinished;
/*     */   private final long lastPossibleTime;
/*     */ 
/*     */   public AbstractPriceAggregationLiveFeedListener(C creator)
/*     */   {
/*  37 */     this(creator, creator.isDirectOrder() ? 9223372036854775807L : -9223372036854775808L);
/*     */   }
/*     */ 
/*     */   public AbstractPriceAggregationLiveFeedListener(C creator, long lastPossibleTime) {
/*  41 */     this.creator = creator;
/*  42 */     this.lastPossibleTime = lastPossibleTime;
/*     */   }
/*     */ 
/*     */   public void analyseCollectedDataPortion() {
/*  46 */     if (!getCreator().isDirectOrder()) {
/*  47 */       Collections.reverse(this.collectedDatas);
/*     */     }
/*     */ 
/*  50 */     for (Data data : this.collectedDatas) {
/*  51 */       if (this.priceRangesCreationFinished) {
/*     */         break;
/*     */       }
/*  54 */       this.priceRangesCreationFinished = analyse(data);
/*     */     }
/*     */ 
/*  57 */     this.collectedDatas.clear();
/*     */   }
/*     */ 
/*     */   public boolean isPriceDatasCreationFinished() {
/*  61 */     return this.priceRangesCreationFinished;
/*     */   }
/*     */ 
/*     */   protected boolean analyse(SD data) {
/*  65 */     boolean isProcessFinished = getCreator().analyse(data);
/*     */ 
/*  67 */     if (getCreator().getLastFiredData() != null) {
/*  68 */       if ((getCreator().isDirectOrder()) && (getCreator().getLastFiredData().getEndTime() >= this.lastPossibleTime))
/*     */       {
/*  72 */         isProcessFinished = true;
/*     */       }
/*  74 */       else if ((!getCreator().isDirectOrder()) && (getCreator().getLastFiredData().getTime() <= this.lastPossibleTime))
/*     */       {
/*  78 */         isProcessFinished = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  83 */     return isProcessFinished;
/*     */   }
/*     */ 
/*     */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */   {
/*     */   }
/*     */ 
/*     */   public C getCreator()
/*     */   {
/* 113 */     return this.creator;
/*     */   }
/*     */ 
/*     */   protected TickData[] splitCandle(Period period, long time, double open, double close, double low, double high, double vol)
/*     */   {
/* 128 */     int TICKS_COUNT = 4;
/*     */ 
/* 130 */     double firstTickPrice = open;
/* 131 */     double secondTickPrice = Math.abs(open - low) > Math.abs(open - high) ? high : low;
/* 132 */     double thirdTickPrice = Math.abs(open - low) < Math.abs(open - high) ? high : low;
/* 133 */     double fourthTickPrice = close;
/*     */ 
/* 135 */     long interval = period.getInterval();
/*     */ 
/* 137 */     TickData tick0 = createTick(time + 0L * interval / 4L, firstTickPrice, 1.0D * vol / 4.0D);
/* 138 */     TickData tick1 = createTick(time + 1L * interval / 4L, secondTickPrice, 2.0D * vol / 4.0D);
/* 139 */     TickData tick2 = createTick(time + 2L * interval / 4L, thirdTickPrice, 3.0D * vol / 4.0D);
/* 140 */     TickData tick3 = createTick(time + 3L * interval / 4L, fourthTickPrice, 4.0D * vol / 4.0D);
/*     */ 
/* 142 */     TickData[] ticks = new TickData[4];
/*     */ 
/* 144 */     ticks[0] = tick0;
/* 145 */     ticks[1] = tick1;
/* 146 */     ticks[2] = tick2;
/* 147 */     ticks[3] = tick3;
/*     */ 
/* 149 */     return ticks;
/*     */   }
/*     */ 
/*     */   private TickData createTick(long time, double price, double vol)
/*     */   {
/* 154 */     return new TickData(time, price, price, vol, vol);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener
 * JD-Core Version:    0.6.0
 */