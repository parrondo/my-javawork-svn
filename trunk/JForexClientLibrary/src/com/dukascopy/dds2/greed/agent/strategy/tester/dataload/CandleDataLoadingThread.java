/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.IntraPeriodCandleData;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public class CandleDataLoadingThread extends AbstractDataLoadingThread<CandleData>
/*    */ {
/*    */   public CandleDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<CandleData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 32 */     super(name, instrument, jForexPeriod, offerSide, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 47 */     LiveFeedListener feedListener = new LiveFeedListener() {
/*    */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/* 49 */         if (!CandleDataLoadingThread.this.isStop())
/* 50 */           CandleDataLoadingThread.this.putDataToQueue(new IntraPeriodCandleData(false, time, open, close, low, high, vol));
/*    */       }
/*    */ 
/*    */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */       {
/*    */       }
/*    */     };
/* 57 */     LoadingProgressListener loadingProgressListener = createLoadingProgressListener();
/*    */ 
/* 59 */     Period period = getJForexPeriod().getPeriod();
/*    */ 
/* 61 */     long firstCandle = DataCacheUtils.getCandleStartFast(period, getFrom());
/* 62 */     long lastCandle = DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, getTo()));
/*    */ 
/* 64 */     if (firstCandle <= lastCandle)
/*    */       try {
/* 66 */         getFeedDataProvider().loadCandlesDataBlockingSynched(getInstrument(), period, getOfferSide(), firstCandle, lastCandle, feedListener, loadingProgressListener);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 76 */         LOGGER.error(e.getLocalizedMessage(), e);
/*    */       }
/*    */     else
/* 79 */       putDataToQueue(createEmptyBar());
/*    */   }
/*    */ 
/*    */   protected IntraPeriodCandleData createEmptyBar()
/*    */   {
/* 85 */     return new IntraPeriodCandleData(true, -9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, -1.0D);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.CandleDataLoadingThread
 * JD-Core Version:    0.6.0
 */