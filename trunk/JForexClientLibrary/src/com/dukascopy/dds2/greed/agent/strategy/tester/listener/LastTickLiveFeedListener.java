/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.listener;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ 
/*    */ public class LastTickLiveFeedListener
/*    */   implements LiveFeedListener
/*    */ {
/*    */   public IBar lastAskBar;
/*    */   public IBar lastBidBar;
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 24 */     if (side == OfferSide.ASK)
/* 25 */       this.lastAskBar = new CandleData(time, open, close, low, high, vol);
/*    */     else
/* 27 */       this.lastBidBar = new CandleData(time, open, close, low, high, vol);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.listener.LastTickLiveFeedListener
 * JD-Core Version:    0.6.0
 */