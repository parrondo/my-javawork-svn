/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class IsDataCachedAction
/*    */   implements Callable<Boolean>
/*    */ {
/*    */   private final Instrument instrument;
/*    */   private final Period period;
/*    */   private final OfferSide side;
/*    */   private final long from;
/*    */   private final long to;
/*    */   private FeedDataProvider feedDataProvider;
/*    */ 
/*    */   public IsDataCachedAction(FeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide side, long from, long to)
/*    */   {
/* 21 */     this.instrument = instrument;
/* 22 */     this.period = period;
/* 23 */     this.from = from;
/* 24 */     this.to = to;
/* 25 */     this.side = side;
/* 26 */     this.feedDataProvider = feedDataProvider;
/*    */   }
/*    */ 
/*    */   public Boolean call() throws NoDataForPeriodException, DataCacheException {
/* 30 */     if ((this.instrument == null) || (this.period == null) || (this.from > this.to) || ((this.period != Period.TICK) && (this.side == null))) {
/* 31 */       throw new DataCacheException("Wrong parameters");
/*    */     }
/*    */ 
/* 36 */     if (this.period != Period.TICK) {
/* 37 */       if (!DataCacheUtils.isCandleBasic(this.period)) {
/* 38 */         throw new NoDataForPeriodException("There is no data in database for requested period");
/*    */       }
/* 40 */       if (!DataCacheUtils.isIntervalValid(this.period, this.from, this.to)) {
/* 41 */         throw new DataCacheException("Time interval is not valid for period requested");
/*    */       }
/*    */     }
/*    */ 
/* 45 */     long lastTickTime = this.feedDataProvider.getCurrentTime(this.instrument);
/* 46 */     long correctedFrom = this.from;
/* 47 */     long correctedTo = this.to;
/* 48 */     long timeOfFirstCandle = this.feedDataProvider.getTimeOfFirstCandle(this.instrument, this.period);
/* 49 */     if (timeOfFirstCandle == 9223372036854775807L)
/*    */     {
/* 51 */       return Boolean.valueOf(true);
/*    */     }
/* 53 */     if (this.period == Period.TICK) {
/* 54 */       if (correctedFrom < timeOfFirstCandle) {
/* 55 */         correctedFrom = timeOfFirstCandle;
/*    */       }
/* 57 */       if ((lastTickTime != -9223372036854775808L) && (correctedTo > lastTickTime + 500L)) {
/* 58 */         correctedTo = lastTickTime + 500L;
/*    */       }
/*    */     }
/* 61 */     else if (correctedFrom < timeOfFirstCandle) {
/* 62 */       correctedFrom = DataCacheUtils.getCandleStartFast(this.period, correctedFrom);
/* 63 */       while (correctedFrom < timeOfFirstCandle) {
/* 64 */         correctedFrom = DataCacheUtils.getNextCandleStartFast(this.period, correctedFrom);
/*    */       }
/* 66 */       if (lastTickTime != -9223372036854775808L) {
/* 67 */         long lastTickCandleStartTime = DataCacheUtils.getCandleStartFast(this.period, lastTickTime);
/* 68 */         if (correctedTo >= lastTickCandleStartTime) {
/* 69 */           correctedTo = DataCacheUtils.getPreviousCandleStartFast(this.period, lastTickCandleStartTime);
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 74 */     if (correctedFrom > correctedTo) {
/* 75 */       return Boolean.valueOf(true);
/*    */     }
/*    */ 
/* 78 */     return Boolean.valueOf(this.feedDataProvider.getLocalCacheManager().isDataCached(this.instrument, this.period, this.side, correctedFrom, correctedTo));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.IsDataCachedAction
 * JD-Core Version:    0.6.0
 */