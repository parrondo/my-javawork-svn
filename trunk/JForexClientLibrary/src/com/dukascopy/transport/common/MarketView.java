/*     */ package com.dukascopy.transport.common;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MarketView
/*     */ {
/*  19 */   private Map<String, CurrencyMarket> currencyMarkets = Collections.synchronizedMap(new HashMap());
/*     */ 
/*  21 */   private Map<String, InstrumentStatusUpdateMessage> instrumentStates = Collections.synchronizedMap(new HashMap());
/*     */ 
/*  23 */   private Map<String, Date> lastReceivedTimes = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public void onMarketState(CurrencyMarket marketState)
/*     */   {
/*  38 */     String instrument = marketState.getInstrument();
/*  39 */     this.currencyMarkets.put(instrument, marketState);
/*  40 */     this.lastReceivedTimes.put(instrument, new Date());
/*     */   }
/*     */ 
/*     */   public CurrencyMarket getLastMarketState(String instrument)
/*     */   {
/*  50 */     return (CurrencyMarket)this.currencyMarkets.get(instrument);
/*     */   }
/*     */ 
/*     */   public Set<String> getInstruments()
/*     */   {
/*  59 */     return this.currencyMarkets.keySet();
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getBestOffer(String instrument, OfferSide side)
/*     */   {
/*  70 */     CurrencyMarket currencyMarket = (CurrencyMarket)this.currencyMarkets.get(instrument);
/*  71 */     if (currencyMarket != null) {
/*  72 */       return currencyMarket.getBestOffer(side);
/*     */     }
/*  74 */     return null;
/*     */   }
/*     */ 
/*     */   public void setLastReceivedTime(String instrument, Date date)
/*     */   {
/*  85 */     this.lastReceivedTimes.put(instrument, date);
/*     */   }
/*     */ 
/*     */   public Date getLastReceivedTime(String instrument)
/*     */   {
/*  95 */     return (Date)this.lastReceivedTimes.get(instrument);
/*     */   }
/*     */ 
/*     */   public void setMarketState(String instrument, CurrencyMarket marketState)
/*     */   {
/* 105 */     this.currencyMarkets.put(instrument, marketState);
/*     */   }
/*     */ 
/*     */   public void setInstrumentState(InstrumentStatusUpdateMessage state)
/*     */   {
/* 114 */     this.instrumentStates.put(state.getInstrument(), state);
/*     */   }
/*     */ 
/*     */   public InstrumentStatusUpdateMessage getInstrumentState(String instrument)
/*     */   {
/* 124 */     return (InstrumentStatusUpdateMessage)this.instrumentStates.get(instrument);
/*     */   }
/*     */ 
/*     */   public void cleanCurrencyMarkets() {
/* 128 */     this.currencyMarkets.clear();
/*     */   }
/*     */ 
/*     */   public Set<String> getAvailableInstruments()
/*     */   {
/* 136 */     Set instrumentSet = new HashSet();
/* 137 */     for (String instrument : this.instrumentStates.keySet()) {
/* 138 */       InstrumentStatusUpdateMessage state = (InstrumentStatusUpdateMessage)this.instrumentStates.get(instrument);
/* 139 */       if (state.getTradable() == 0) {
/* 140 */         instrumentSet.add(instrument);
/*     */       }
/*     */     }
/* 143 */     return instrumentSet;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.MarketView
 * JD-Core Version:    0.6.0
 */