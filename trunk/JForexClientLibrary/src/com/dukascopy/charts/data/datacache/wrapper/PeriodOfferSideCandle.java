/*    */ package com.dukascopy.charts.data.datacache.wrapper;
/*    */ 
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ 
/*    */ public class PeriodOfferSideCandle
/*    */ {
/*    */   private Period period;
/*    */   private OfferSide offerSide;
/*    */   private CandleData candleData;
/*    */ 
/*    */   public PeriodOfferSideCandle(Period period, OfferSide offerSide, CandleData candleData)
/*    */   {
/* 20 */     this.period = period;
/* 21 */     this.offerSide = offerSide;
/* 22 */     this.candleData = candleData;
/*    */   }
/*    */ 
/*    */   public Period getPeriod() {
/* 26 */     return this.period;
/*    */   }
/*    */ 
/*    */   public void setPeriod(Period period) {
/* 30 */     this.period = period;
/*    */   }
/*    */ 
/*    */   public OfferSide getOfferSide() {
/* 34 */     return this.offerSide;
/*    */   }
/*    */ 
/*    */   public void setOfferSide(OfferSide offerSide) {
/* 38 */     this.offerSide = offerSide;
/*    */   }
/*    */ 
/*    */   public CandleData getCandleData() {
/* 42 */     return this.candleData;
/*    */   }
/*    */ 
/*    */   public void setCandleData(CandleData candleData) {
/* 46 */     this.candleData = candleData;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.wrapper.PeriodOfferSideCandle
 * JD-Core Version:    0.6.0
 */