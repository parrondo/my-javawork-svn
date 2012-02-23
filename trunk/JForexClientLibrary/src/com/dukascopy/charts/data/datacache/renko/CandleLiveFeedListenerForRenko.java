/*    */ package com.dukascopy.charts.data.datacache.renko;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ 
/*    */ public class CandleLiveFeedListenerForRenko extends TickLiveFeedListenerForRenko
/*    */ {
/*    */   public CandleLiveFeedListenerForRenko(IRenkoCreator creator)
/*    */   {
/* 18 */     super(creator);
/*    */   }
/*    */ 
/*    */   public CandleLiveFeedListenerForRenko(IRenkoCreator creator, long lastPossibleTime)
/*    */   {
/* 25 */     super(creator, lastPossibleTime);
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 40 */     TickData[] ticks = splitCandle(period, time, open, close, low, high, vol);
/* 41 */     for (TickData tick : ticks)
/* 42 */       newTick(instrument, tick.getTime(), tick.getAsk(), tick.getBid(), tick.getAskVolume(), tick.getBidVolume());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.CandleLiveFeedListenerForRenko
 * JD-Core Version:    0.6.0
 */