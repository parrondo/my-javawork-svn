/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class PivotTickDataLoadingThread extends TickDataLoadingThread
/*    */ {
/*    */   private TickData previousTick;
/*    */   private boolean priceUp;
/* 24 */   private int tickCount = 0;
/*    */ 
/*    */   public PivotTickDataLoadingThread(String name, Instrument instrument, BlockingQueue<TickData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 33 */     super(name, instrument, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   protected void tickReceived(Instrument instrument, TickData tickData)
/*    */   {
/* 48 */     if (this.tickCount == 0)
/*    */     {
/* 50 */       this.tickCount += 1;
/*    */     }
/* 52 */     else if (this.tickCount == 1)
/*    */     {
/* 54 */       if ((this.previousTick.getAsk() != tickData.getAsk()) || (this.previousTick.getBid() != tickData.getBid()))
/*    */       {
/* 58 */         this.priceUp = (((this.previousTick.getAsk() < tickData.getAsk()) && (this.previousTick.getBid() < tickData.getBid())) || ((this.previousTick.getAsk() <= tickData.getAsk()) && (this.previousTick.getBid() < tickData.getBid())) || ((this.previousTick.getAsk() < tickData.getAsk()) && (this.previousTick.getBid() <= tickData.getBid())));
/*    */ 
/* 61 */         this.tickCount += 1;
/*    */       }
/*    */     }
/* 64 */     else if (isPriceMovingDirectionChanged(tickData)) {
/* 65 */       this.priceUp = (!this.priceUp);
/* 66 */       putTickToQueue(this.previousTick);
/*    */     }
/*    */ 
/* 69 */     this.previousTick = tickData;
/*    */   }
/*    */ 
/*    */   private boolean isPriceMovingDirectionChanged(TickData tickData) {
/* 73 */     return ((this.priceUp) && ((this.previousTick.getAsk() > tickData.getAsk()) || (this.previousTick.getBid() > tickData.getBid()))) || ((!this.priceUp) && ((this.previousTick.getAsk() < tickData.getAsk()) || (this.previousTick.getBid() < tickData.getBid())));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.PivotTickDataLoadingThread
 * JD-Core Version:    0.6.0
 */