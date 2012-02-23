/*    */ package com.dukascopy.charts.data.datacache.listener;
/*    */ 
/*    */ import com.dukascopy.api.Filter;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*    */ 
/*    */ public class FilteredDataLiveFeedListener
/*    */   implements LiveFeedListener
/*    */ {
/*    */   private final Filter filter;
/*    */   private final LiveFeedListener originalLiveFeedListener;
/*    */   private final IFilterManager filterManager;
/*    */ 
/*    */   public FilteredDataLiveFeedListener(IFilterManager filterManager, Filter filter, LiveFeedListener originalLiveFeedListener)
/*    */   {
/* 29 */     this.filter = filter;
/* 30 */     this.originalLiveFeedListener = originalLiveFeedListener;
/* 31 */     this.filterManager = filterManager;
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 46 */     boolean matchedFilter = this.filterManager.matchedFilter(period, this.filter, time, open, close, low, high, vol);
/*    */ 
/* 48 */     if (matchedFilter)
/* 49 */       this.originalLiveFeedListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/* 62 */     this.originalLiveFeedListener.newTick(instrument, time, ask, bid, askVol, bidVol);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.listener.FilteredDataLiveFeedListener
 * JD-Core Version:    0.6.0
 */