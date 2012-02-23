/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ 
/*    */ public class CurrencyConverter extends AbstractCurrencyConverter
/*    */ {
/*    */   private static CurrencyConverter converter;
/*    */ 
/*    */   public static CurrencyConverter getCurrencyConverter()
/*    */   {
/* 23 */     if (converter == null) {
/* 24 */       converter = new CurrencyConverter();
/*    */     }
/* 26 */     return converter;
/*    */   }
/*    */ 
/*    */   protected double getLastMarketPrice(Instrument instrument, OfferSide side)
/*    */   {
/* 31 */     TickData lastTick = FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/* 32 */     if (lastTick == null) {
/* 33 */       return (0.0D / 0.0D);
/*    */     }
/* 35 */     if (side == OfferSide.ASK)
/* 36 */       return lastTick.getAsk();
/* 37 */     if (side == OfferSide.BID) {
/* 38 */       return lastTick.getBid();
/*    */     }
/* 40 */     return (lastTick.getBid() + lastTick.getAsk()) / 2.0D;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.CurrencyConverter
 * JD-Core Version:    0.6.0
 */