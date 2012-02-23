/*    */ package com.dukascopy.charts.data.datacache.customperiod;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class AbstractCustomPeriodCreator
/*    */ {
/*    */   private final Instrument instrument;
/*    */   private final OfferSide offerSide;
/*    */   private final Period desiredPeriod;
/* 22 */   private final List<LiveFeedListener> listeners = new ArrayList();
/*    */ 
/*    */   public AbstractCustomPeriodCreator(Instrument instrument, OfferSide offerSide, Period desiredPeriod)
/*    */   {
/* 30 */     this.instrument = instrument;
/* 31 */     this.offerSide = offerSide;
/* 32 */     this.desiredPeriod = desiredPeriod;
/*    */   }
/*    */ 
/*    */   protected List<LiveFeedListener> getListeners() {
/* 36 */     return this.listeners;
/*    */   }
/*    */ 
/*    */   public void addListener(LiveFeedListener listener) {
/* 40 */     if (listener == null) {
/* 41 */       throw new NullPointerException();
/*    */     }
/* 43 */     getListeners().add(listener);
/*    */   }
/*    */ 
/*    */   public void removeListener(LiveFeedListener listener) {
/* 47 */     if (listener == null) {
/* 48 */       throw new NullPointerException();
/*    */     }
/* 50 */     getListeners().remove(listener);
/*    */   }
/*    */ 
/*    */   protected void fireNewCandle(CandleData candleData) {
/* 54 */     for (LiveFeedListener listener : getListeners())
/* 55 */       listener.newCandle(getInstrument(), getDesiredPeriod(), getOfferSide(), candleData.getTime(), candleData.getOpen(), candleData.getClose(), candleData.getLow(), candleData.getHigh(), candleData.getVolume());
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument()
/*    */   {
/* 70 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public OfferSide getOfferSide() {
/* 74 */     return this.offerSide;
/*    */   }
/*    */ 
/*    */   public Period getDesiredPeriod() {
/* 78 */     return this.desiredPeriod;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.customperiod.AbstractCustomPeriodCreator
 * JD-Core Version:    0.6.0
 */