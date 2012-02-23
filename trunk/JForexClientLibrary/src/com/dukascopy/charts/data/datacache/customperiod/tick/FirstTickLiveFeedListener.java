/*    */ package com.dukascopy.charts.data.datacache.customperiod.tick;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ 
/*    */ public class FirstTickLiveFeedListener
/*    */   implements LiveFeedListener
/*    */ {
/*    */   private TickData firstTick;
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/* 27 */     if (this.firstTick == null)
/* 28 */       this.firstTick = new TickData(time, ask, bid, askVol, bidVol);
/*    */   }
/*    */ 
/*    */   public TickData getFirstTick()
/*    */   {
/* 33 */     return this.firstTick;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.tick.FirstTickLiveFeedListener
 * JD-Core Version:    0.6.0
 */