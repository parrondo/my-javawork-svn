/*    */ package com.dukascopy.charts.data.datacache.pnf;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ 
/*    */ public class CandleLiveFeedListenerForPointAndFigure extends TickLiveFeedListenerForPointAndFigure
/*    */ {
/*    */   public CandleLiveFeedListenerForPointAndFigure(IPointAndFigureCreator creator)
/*    */   {
/* 15 */     super(creator);
/*    */   }
/*    */ 
/*    */   public CandleLiveFeedListenerForPointAndFigure(IPointAndFigureCreator creator, long lastPossibleTime)
/*    */   {
/* 22 */     super(creator, lastPossibleTime);
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
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.CandleLiveFeedListenerForPointAndFigure
 * JD-Core Version:    0.6.0
 */