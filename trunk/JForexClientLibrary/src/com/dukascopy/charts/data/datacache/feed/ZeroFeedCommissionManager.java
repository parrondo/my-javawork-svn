/*    */ package com.dukascopy.charts.data.datacache.feed;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.transport.common.msg.request.FeedCommission;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ZeroFeedCommissionManager
/*    */   implements IFeedCommissionManager
/*    */ {
/*    */   public void addFeedCommissions(Map<String, FeedCommission> feedCommissions)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void addFeedCommissions(List<IInstrumentFeedCommissionInfo> feedCommissions)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/*    */   }
/*    */ 
/*    */   public double getFeedCommission(Instrument instrument, long time)
/*    */   {
/* 42 */     return 0.0D;
/*    */   }
/*    */ 
/*    */   public double getPriceWithCommission(Instrument instrument, OfferSide side, double price, long time)
/*    */   {
/* 52 */     return price;
/*    */   }
/*    */ 
/*    */   public void setupFeedCommissions(List<IInstrumentFeedCommissionInfo> feedCommissions)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setupFeedCommissionsFromAuthServer(List<String[]> feedCommissions)
/*    */   {
/*    */   }
/*    */ 
/*    */   public CandleData applyFeedCommissionToCandle(Instrument instrument, OfferSide side, CandleData candle)
/*    */   {
/* 67 */     return candle;
/*    */   }
/*    */ 
/*    */   public TickData applyFeedCommissionToTick(Instrument instrument, TickData tick)
/*    */   {
/* 72 */     return tick;
/*    */   }
/*    */ 
/*    */   public Data[] applyFeedCommissionToData(Instrument instrument, Period period, OfferSide offerSide, Data[] data)
/*    */   {
/* 77 */     return data;
/*    */   }
/*    */ 
/*    */   public boolean hasCommission(Instrument instrument)
/*    */   {
/* 82 */     return false;
/*    */   }
/*    */ 
/*    */   public void addFeedCommissions(Map<String, FeedCommission> feedCommissions, Long time)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void addFeedCommissions(Map<String, FeedCommission> feedCommissions, long time)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.feed.ZeroFeedCommissionManager
 * JD-Core Version:    0.6.0
 */