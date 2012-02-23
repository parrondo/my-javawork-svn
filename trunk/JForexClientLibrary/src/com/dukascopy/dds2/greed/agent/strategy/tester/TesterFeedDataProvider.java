/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*    */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.IntraPeriodCandleData;
/*    */ import com.dukascopy.charts.data.datacache.IntraperiodCandlesGenerator;
/*    */ import com.dukascopy.charts.data.datacache.LoadOrdersAction;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.LocalCacheManager;
/*    */ import com.dukascopy.charts.data.datacache.NoDataForPeriodException;
/*    */ import com.dukascopy.charts.data.datacache.OrdersListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*    */ 
/*    */ public class TesterFeedDataProvider extends FeedDataProvider
/*    */ {
/*    */   public TesterFeedDataProvider(String cacheName, IOrdersProvider ordersProvider)
/*    */     throws DataCacheException
/*    */   {
/* 23 */     super(cacheName, true, ordersProvider, null);
/*    */ 
/* 28 */     this.intraperiodExistsPolicy = CurvesDataLoader.IntraperiodExistsPolicy.USE_INTRAPERIOD_WHEN_POSSIBLE;
/*    */   }
/*    */ 
/*    */   public static FeedDataProvider getDefaultInstance() {
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   public void tickReceived(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 36 */     if (this.firstTickLocalTime == -9223372036854775808L) {
/* 37 */       this.firstTickLocalTime = System.currentTimeMillis();
/*    */     }
/*    */ 
/* 54 */     if ((ask == 0.0D) || (bid == 0.0D))
/*    */     {
/* 56 */       return;
/*    */     }
/*    */ 
/* 59 */     if ((this.lastTicks[instrument.ordinal()] == null) || (DataCacheUtils.getChunkStartFast(Period.TICK, this.lastTicks[instrument.ordinal()].getTime()) < DataCacheUtils.getChunkStartFast(Period.TICK, time)))
/*    */     {
/* 61 */       this.localCacheManager.newTickChunkStart(instrument, time);
/*    */     }
/*    */ 
/* 64 */     this.lastTicks[instrument.ordinal()] = new TickData(time, ask, bid, askVol, bidVol);
/* 65 */     this.currentTimes[instrument.ordinal()] = time;
/* 66 */     this.currentTime = time;
/* 67 */     this.lastAsks[instrument.ordinal()] = ask;
/* 68 */     this.lastBids[instrument.ordinal()] = bid;
/*    */ 
/* 70 */     this.localCacheManager.newTick(instrument, time, ask, bid, askVol, bidVol, true);
/*    */ 
/* 72 */     this.intraperiodCandlesGenerator.newTick(instrument, time, ask, bid, askVol, bidVol);
/*    */ 
/* 74 */     fireNewTick(instrument, time, ask, bid, askVol, bidVol);
/*    */   }
/*    */ 
/*    */   public void barsReceived(Instrument instrument, Period period, IntraPeriodCandleData askBar, IntraPeriodCandleData bidBar) {
/* 78 */     this.intraperiodCandlesGenerator.processCandle(instrument, period, askBar, bidBar);
/*    */   }
/*    */ 
/*    */   protected LoadOrdersAction getLoadOrdersAction(Instrument instrument, long from, long to, OrdersListener ordersListener, LoadingProgressListener loadingProgress) throws NoDataForPeriodException, DataCacheException
/*    */   {
/* 83 */     LoadOrdersAction action = super.getLoadOrdersAction(instrument, from, to, ordersListener, loadingProgress);
/* 84 */     action.setLocalOrdersOnly(true);
/* 85 */     return action;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterFeedDataProvider
 * JD-Core Version:    0.6.0
 */