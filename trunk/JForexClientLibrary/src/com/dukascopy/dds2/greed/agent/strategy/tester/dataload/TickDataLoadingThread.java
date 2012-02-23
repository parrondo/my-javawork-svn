/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public class TickDataLoadingThread extends AbstractDataLoadingThread<TickData>
/*    */ {
/*    */   public TickDataLoadingThread(String name, Instrument instrument, BlockingQueue<TickData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 30 */     super(name, instrument, new JForexPeriod(DataType.TICKS, Period.TICK), null, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 45 */     LiveFeedListener feedListener = new LiveFeedListener() {
/*    */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*    */       }
/*    */ 
/*    */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 50 */         TickDataLoadingThread.this.tickReceived(instrument, new TickData(time, ask, bid, askVol, bidVol, new double[] { ask }, new double[] { bid }, new double[] { askVol }, new double[] { bidVol }));
/*    */       }
/*    */     };
/* 54 */     LoadingProgressListener loadingProgressListener = createLoadingProgressListener();
/*    */     try
/*    */     {
/* 57 */       getFeedDataProvider().loadTicksDataBlockingSynched(getInstrument(), getFrom(), getTo(), feedListener, loadingProgressListener);
/*    */     }
/*    */     catch (DataCacheException e)
/*    */     {
/* 65 */       LOGGER.error(e.getLocalizedMessage(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void tickReceived(Instrument instrument, TickData tickData)
/*    */   {
/* 74 */     putTickToQueue(tickData);
/*    */   }
/*    */ 
/*    */   protected void putTickToQueue(TickData tickData) {
/* 78 */     if (!isStop())
/* 79 */       putDataToQueue(tickData);
/*    */   }
/*    */ 
/*    */   protected TickData createEmptyBar()
/*    */   {
/* 86 */     return new TickData(-9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, null, null, null, null);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TickDataLoadingThread
 * JD-Core Version:    0.6.0
 */