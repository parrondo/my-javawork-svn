/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class DifferentPriceInPipsTickDataLoadingThread extends TickDataLoadingThread
/*    */ {
/* 12 */   private int priceDifferenceInPips = 0;
/*    */   private TickData previousTick;
/*    */ 
/*    */   public DifferentPriceInPipsTickDataLoadingThread(String name, Instrument instrument, int priceDifferenceInPips, BlockingQueue<TickData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 24 */     super(name, instrument, queue, from, to, feedDataProvider);
/*    */ 
/* 33 */     this.priceDifferenceInPips = priceDifferenceInPips;
/*    */   }
/*    */ 
/*    */   protected void tickReceived(Instrument instrument, TickData tickData)
/*    */   {
/* 41 */     if (this.previousTick == null) {
/* 42 */       this.previousTick = tickData;
/* 43 */       putTickToQueue(tickData);
/* 44 */       return;
/*    */     }
/*    */ 
/* 47 */     double priceDifference = Math.abs(this.previousTick.getAsk() - tickData.getAsk());
/* 48 */     double currentPriceDifferenceInPips = StratUtils.roundHalfEven(priceDifference / instrument.getPipValue(), 1);
/* 49 */     if (currentPriceDifferenceInPips >= this.priceDifferenceInPips) {
/* 50 */       putTickToQueue(tickData);
/* 51 */       this.previousTick = tickData;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.DifferentPriceInPipsTickDataLoadingThread
 * JD-Core Version:    0.6.0
 */