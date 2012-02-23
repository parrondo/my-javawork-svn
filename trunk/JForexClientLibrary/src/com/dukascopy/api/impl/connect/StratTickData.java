/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ 
/*    */ public class StratTickData extends TickData
/*    */ {
/*    */   private double totalAskVolume;
/*    */   private double totalBidVolume;
/*    */ 
/*    */   public StratTickData(TickData tickData, double totalAskVolume, double totalBidVolume)
/*    */   {
/* 17 */     this.time = tickData.time;
/* 18 */     this.ask = tickData.ask;
/* 19 */     this.bid = tickData.bid;
/* 20 */     this.askVol = tickData.askVol;
/* 21 */     this.bidVol = tickData.bidVol;
/* 22 */     this.asks = tickData.asks;
/* 23 */     this.bids = tickData.bids;
/* 24 */     this.askVolumes = tickData.askVolumes;
/* 25 */     this.bidVolumes = tickData.bidVolumes;
/* 26 */     this.totalAskVolume = totalAskVolume;
/* 27 */     this.totalBidVolume = totalBidVolume;
/*    */   }
/*    */ 
/*    */   public void setTotalAskVolume(double totalAskVolume) {
/* 31 */     this.totalAskVolume = totalAskVolume;
/*    */   }
/*    */ 
/*    */   public void setTotalBidVolume(double totalBidVolume) {
/* 35 */     this.totalBidVolume = totalBidVolume;
/*    */   }
/*    */ 
/*    */   public double getTotalAskVolume()
/*    */   {
/* 40 */     return this.totalAskVolume;
/*    */   }
/*    */ 
/*    */   public double getTotalBidVolume()
/*    */   {
/* 45 */     return this.totalBidVolume;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.StratTickData
 * JD-Core Version:    0.6.0
 */