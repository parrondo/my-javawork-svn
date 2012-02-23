/*    */ package com.dukascopy.charts.data.datacache.rangebar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ 
/*    */ public class CandleLiveFeedListenerForRangeBars extends TickLiveFeedListenerForRangeBars
/*    */ {
/*    */   public CandleLiveFeedListenerForRangeBars(IPriceRangeCreator priceRangeCreator)
/*    */   {
/* 15 */     super(priceRangeCreator);
/*    */   }
/*    */ 
/*    */   public CandleLiveFeedListenerForRangeBars(IPriceRangeCreator priceRangeCreator, long lastPossibleTime)
/*    */   {
/* 22 */     super(priceRangeCreator, lastPossibleTime);
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 37 */     TickData[] ticks = splitCandle(period, time, open, close, low, high, vol);
/* 38 */     for (TickData tick : ticks)
/* 39 */       newTick(instrument, tick.getTime(), tick.getAsk(), tick.getBid(), tick.getAskVolume(), tick.getBidVolume());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.CandleLiveFeedListenerForRangeBars
 * JD-Core Version:    0.6.0
 */