/*    */ package com.dukascopy.dds2.greed.agent.strategy.objects;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.history.HTick;
/*    */ import java.util.Date;
/*    */ import java.util.List;
/*    */ 
/*    */ public class Market
/*    */ {
/* 19 */   private HTick tick = null;
/*    */ 
/* 21 */   private List<Offer> asks = null;
/*    */ 
/* 23 */   private List<Offer> bids = null;
/*    */ 
/* 25 */   private String instrument = null;
/*    */ 
/*    */   public Market(String instrument, HTick tick, List<Offer> asks, List<Offer> bids) {
/* 28 */     this.tick = tick;
/* 29 */     this.asks = asks;
/* 30 */     this.bids = bids;
/* 31 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public Date getTime() {
/* 35 */     return new Date(this.tick.time);
/*    */   }
/*    */ 
/*    */   public String getSymbol() {
/* 39 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public double getAsk() {
/* 43 */     return this.tick.ask;
/*    */   }
/*    */ 
/*    */   public double getBid() {
/* 47 */     return this.tick.bid;
/*    */   }
/*    */ 
/*    */   public double getBidVolume() {
/* 51 */     return this.tick.bidVol;
/*    */   }
/*    */ 
/*    */   public double getAskVolume() {
/* 55 */     return this.tick.askVol;
/*    */   }
/*    */ 
/*    */   public List<Offer> getAskDepth()
/*    */   {
/* 60 */     return this.asks;
/*    */   }
/*    */ 
/*    */   public List<Offer> getBidDepth()
/*    */   {
/* 65 */     return this.bids;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 71 */     return "market " + this.instrument + " Ask " + getAsk() + " (" + getAskVolume() + ") / Bid " + getBid() + " (" + getBidVolume() + ")";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.objects.Market
 * JD-Core Version:    0.6.0
 */