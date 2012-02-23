/*    */ package com.dukascopy.charts.data.datacache.customperiod.tick;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ 
/*    */ public class CustomPeriodTickLiveFeedListener
/*    */   implements LiveFeedListener
/*    */ {
/*    */   private final CustomPeriodFromTicksCreator customPeriodFromTicksCreator;
/*    */   private boolean customPeriodsCreationFinished;
/* 22 */   private List<TickData> collectedTickDatas = new ArrayList();
/*    */ 
/*    */   public CustomPeriodTickLiveFeedListener(CustomPeriodFromTicksCreator customPeriodFromTicksCreator) {
/* 25 */     this.customPeriodFromTicksCreator = customPeriodFromTicksCreator;
/*    */   }
/*    */ 
/*    */   public void analyseTickDataPortion() {
/* 29 */     if (this.customPeriodFromTicksCreator.getInverseOrder()) {
/* 30 */       reverseCollectedDatas();
/*    */     }
/*    */ 
/* 33 */     for (TickData tickData : this.collectedTickDatas) {
/* 34 */       if (this.customPeriodsCreationFinished) {
/*    */         break;
/*    */       }
/* 37 */       this.customPeriodsCreationFinished = this.customPeriodFromTicksCreator.analyseTickData(tickData);
/*    */     }
/*    */ 
/* 40 */     this.collectedTickDatas.clear();
/*    */   }
/*    */ 
/*    */   public void reverseCollectedDatas() {
/* 44 */     Collections.reverse(this.collectedTickDatas);
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/* 71 */     this.collectedTickDatas.add(new TickData(time, ask, bid, askVol, bidVol));
/*    */   }
/*    */ 
/*    */   public CustomPeriodFromTicksCreator getCustomPeriodFromTicksCreator() {
/* 75 */     return this.customPeriodFromTicksCreator;
/*    */   }
/*    */ 
/*    */   public boolean isCustomPeriodsCreationFinished() {
/* 79 */     return this.customPeriodsCreationFinished;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.tick.CustomPeriodTickLiveFeedListener
 * JD-Core Version:    0.6.0
 */