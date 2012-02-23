/*    */ package com.dukascopy.charts.data.datacache.listener;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SaveCandlesLiveFeedListener
/*    */   implements LiveFeedListener
/*    */ {
/* 21 */   private List<CandleData> savedCandles = new ArrayList(5);
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 34 */     this.savedCandles.add(new CandleData(time, open, close, low, high, vol));
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/*    */   }
/*    */ 
/*    */   public List<CandleData> getSavedCandles()
/*    */   {
/* 49 */     return this.savedCandles;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.listener.SaveCandlesLiveFeedListener
 * JD-Core Version:    0.6.0
 */