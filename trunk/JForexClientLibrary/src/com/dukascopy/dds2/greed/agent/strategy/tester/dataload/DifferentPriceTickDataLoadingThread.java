/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class DifferentPriceTickDataLoadingThread extends TickDataLoadingThread
/*    */ {
/*    */   private TickData previousTick;
/*    */ 
/*    */   public DifferentPriceTickDataLoadingThread(String name, Instrument instrument, BlockingQueue<TickData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 32 */     super(name, instrument, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   protected void tickReceived(Instrument instrument, TickData tickData)
/*    */   {
/* 47 */     if (this.previousTick == null) {
/* 48 */       putTickToQueue(tickData);
/*    */     }
/* 50 */     else if ((this.previousTick.getAsk() != tickData.getAsk()) || (this.previousTick.getBid() != tickData.getBid())) {
/* 51 */       putTickToQueue(tickData);
/*    */     }
/* 53 */     this.previousTick = tickData;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.DifferentPriceTickDataLoadingThread
 * JD-Core Version:    0.6.0
 */