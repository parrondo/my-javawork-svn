/*     */ package com.dukascopy.charts.data.datacache.dhl;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class DailyHighLowManager
/*     */   implements IDailyHighLowManager, LiveFeedListener
/*     */ {
/*  28 */   private final Map<Instrument, List<IHighLowListener>> listenersMap = new HashMap();
/*  29 */   private final Map<Instrument, HighLow> highLowMap = new HashMap();
/*     */ 
/*  31 */   private final Period TARGET_PERIOD = Period.DAILY;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */ 
/*     */   public DailyHighLowManager(IFeedDataProvider feedDataProvider)
/*     */   {
/*  37 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   public void addDailyHighLowListener(Instrument instrument, IHighLowListener listener)
/*     */   {
/*  42 */     synchronized (this) {
/*  43 */       List list = (List)this.listenersMap.get(instrument);
/*     */ 
/*  45 */       if (list == null) {
/*  46 */         list = new ArrayList();
/*  47 */         this.listenersMap.put(instrument, list);
/*     */       }
/*  49 */       if (list.isEmpty()) {
/*  50 */         this.feedDataProvider.addInProgressCandleListener(instrument, this.TARGET_PERIOD, OfferSide.ASK, this);
/*  51 */         this.feedDataProvider.addInProgressCandleListener(instrument, this.TARGET_PERIOD, OfferSide.BID, this);
/*     */       }
/*  53 */       list.add(listener);
/*     */ 
/*  55 */       HighLow highLow = (HighLow)this.highLowMap.get(instrument);
/*     */ 
/*  57 */       if (highLow == null) {
/*  58 */         CandleData bidCandle = this.feedDataProvider.getInProgressCandle(instrument, this.TARGET_PERIOD, OfferSide.BID);
/*  59 */         CandleData askCandle = this.feedDataProvider.getInProgressCandle(instrument, this.TARGET_PERIOD, OfferSide.ASK);
/*     */ 
/*  61 */         if ((bidCandle != null) && (askCandle != null)) {
/*  62 */           highLow = new HighLow(bidCandle.time, Math.max(bidCandle.high, askCandle.high), Math.max(bidCandle.low, askCandle.low));
/*     */         }
/*  68 */         else if (bidCandle != null) {
/*  69 */           highLow = new HighLow(bidCandle.time, bidCandle.high, bidCandle.low);
/*     */         }
/*  75 */         else if (askCandle != null) {
/*  76 */           highLow = new HighLow(askCandle.time, askCandle.high, askCandle.low);
/*     */         }
/*     */ 
/*  83 */         if (highLow != null) {
/*  84 */           this.highLowMap.put(instrument, highLow);
/*     */         }
/*     */       }
/*     */ 
/*  88 */       if (highLow != null) {
/*  89 */         fireHighUpdated(listener, instrument, this.TARGET_PERIOD, highLow.getHigh());
/*  90 */         fireLowUpdated(listener, instrument, this.TARGET_PERIOD, highLow.getLow());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map<Instrument, List<IHighLowListener>> getDailyHighLowListeners()
/*     */   {
/*  97 */     synchronized (this) {
/*  98 */       Map copy = new HashMap();
/*  99 */       copy.putAll(this.listenersMap);
/* 100 */       return copy;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeDailyHighLowListener(IHighLowListener listener)
/*     */   {
/* 106 */     synchronized (this) {
/* 107 */       Set entrySet = this.listenersMap.entrySet();
/* 108 */       List valuesToRemove = new ArrayList();
/*     */ 
/* 110 */       for (Map.Entry entry : entrySet) {
/* 111 */         if (entry.getValue() != null) {
/* 112 */           for (IHighLowListener l : (List)entry.getValue()) {
/* 113 */             if (l.equals(listener)) {
/* 114 */               valuesToRemove.add(l);
/*     */             }
/*     */           }
/*     */ 
/* 118 */           ((List)entry.getValue()).removeAll(valuesToRemove);
/* 119 */           valuesToRemove.clear();
/*     */         }
/*     */       }
/*     */ 
/* 123 */       for (Instrument instrument : this.listenersMap.keySet()) {
/* 124 */         List list = (List)this.listenersMap.get(instrument);
/* 125 */         if (ObjectUtils.isNullOrEmpty(list)) {
/* 126 */           this.feedDataProvider.removeInProgressCandleListener(instrument, this.TARGET_PERIOD, OfferSide.ASK, this);
/* 127 */           this.feedDataProvider.removeInProgressCandleListener(instrument, this.TARGET_PERIOD, OfferSide.BID, this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireHighUpdated(Instrument instrument, Period period, double high)
/*     */   {
/* 138 */     synchronized (this) {
/* 139 */       List list = (List)this.listenersMap.get(instrument);
/* 140 */       for (IHighLowListener l : list)
/* 141 */         fireHighUpdated(l, instrument, period, high);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireLowUpdated(Instrument instrument, Period period, double low)
/*     */   {
/* 151 */     synchronized (this) {
/* 152 */       List list = (List)this.listenersMap.get(instrument);
/* 153 */       for (IHighLowListener l : list)
/* 154 */         fireLowUpdated(l, instrument, period, low);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fireHighUpdated(IHighLowListener listener, Instrument instrument, Period period, double high)
/*     */   {
/* 165 */     listener.highUpdated(instrument, period, high);
/*     */   }
/*     */ 
/*     */   private void fireLowUpdated(IHighLowListener listener, Instrument instrument, Period period, double low)
/*     */   {
/* 174 */     listener.lowUpdated(instrument, period, low);
/*     */   }
/*     */ 
/*     */   public List<IHighLowListener> getDailyHighLowListeners(Instrument instrument)
/*     */   {
/* 179 */     synchronized (this) {
/* 180 */       List list = (List)this.listenersMap.get(instrument);
/* 181 */       if (list == null) {
/* 182 */         return new ArrayList();
/*     */       }
/*     */ 
/* 185 */       return Collections.unmodifiableList(list);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */   {
/* 197 */     if (this.TARGET_PERIOD.equals(period))
/*     */     {
/* 199 */       synchronized (this) {
/* 200 */         HighLow highLow = (HighLow)this.highLowMap.get(instrument);
/*     */ 
/* 202 */         if ((highLow != null) && 
/* 203 */           (highLow.getTime() != time)) {
/* 204 */           highLow = null;
/*     */         }
/*     */ 
/* 208 */         if (highLow == null) {
/* 209 */           highLow = new HighLow(time, high, low);
/* 210 */           this.highLowMap.put(instrument, highLow);
/*     */ 
/* 212 */           fireHighUpdated(instrument, period, high);
/* 213 */           fireLowUpdated(instrument, period, low);
/*     */         }
/*     */         else {
/* 216 */           if (highLow.getHigh() < high) {
/* 217 */             highLow.setHigh(high);
/* 218 */             fireHighUpdated(instrument, period, high);
/*     */           }
/*     */ 
/* 221 */           if (highLow.getLow() > low) {
/* 222 */             highLow.setLow(low);
/* 223 */             fireLowUpdated(instrument, period, low);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAllListeners()
/*     */   {
/* 234 */     synchronized (this) {
/* 235 */       for (Instrument instrument : this.listenersMap.keySet()) {
/* 236 */         this.feedDataProvider.removeInProgressCandleListener(instrument, this.TARGET_PERIOD, OfferSide.ASK, this);
/* 237 */         this.feedDataProvider.removeInProgressCandleListener(instrument, this.TARGET_PERIOD, OfferSide.BID, this);
/*     */       }
/* 239 */       this.listenersMap.clear();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.dhl.DailyHighLowManager
 * JD-Core Version:    0.6.0
 */