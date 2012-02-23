/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class TicksWithTimeIntervalDataLoadingThread extends TickDataLoadingThread
/*    */ {
/*    */   private final long timeIntervalBetweenTicks;
/*    */   private TickData previousTick;
/*    */ 
/*    */   public TicksWithTimeIntervalDataLoadingThread(String name, Instrument instrument, long timeIntervalBetweenTicks, BlockingQueue<TickData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 22 */     super(name, instrument, queue, from, to, feedDataProvider);
/*    */ 
/* 31 */     this.timeIntervalBetweenTicks = timeIntervalBetweenTicks;
/*    */   }
/*    */ 
/*    */   protected void tickReceived(Instrument instrument, TickData tickData)
/*    */   {
/* 39 */     if (this.previousTick == null) {
/* 40 */       this.previousTick = tickData;
/* 41 */       putTickToQueue(tickData);
/* 42 */       return;
/*    */     }
/*    */ 
/* 45 */     long timeDiff = tickData.getTime() - this.previousTick.getTime();
/* 46 */     if (timeDiff >= this.timeIntervalBetweenTicks) {
/* 47 */       putTickToQueue(tickData);
/* 48 */       this.previousTick = tickData;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TicksWithTimeIntervalDataLoadingThread
 * JD-Core Version:    0.6.0
 */