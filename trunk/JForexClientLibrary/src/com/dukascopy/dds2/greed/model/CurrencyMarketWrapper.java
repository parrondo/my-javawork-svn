/*     */ package com.dukascopy.dds2.greed.model;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CurrencyMarketWrapper
/*     */ {
/*  20 */   private CurrencyMarket currencyMarket = null;
/*  21 */   private CurrencyOffer bestOfferAsk = null;
/*  22 */   private CurrencyOffer bestOfferBid = null;
/*     */   private Date timestamp;
/*     */ 
/*     */   public static CurrencyMarketWrapper valueOf(CurrencyMarket currencyMarket)
/*     */   {
/*  28 */     if (currencyMarket == null) {
/*  29 */       return null;
/*     */     }
/*  31 */     return new CurrencyMarketWrapper(currencyMarket);
/*     */   }
/*     */ 
/*     */   private CurrencyMarketWrapper(CurrencyMarket currencyMarket) {
/*  35 */     this.currencyMarket = currencyMarket;
/*     */   }
/*     */ 
/*     */   public List<CurrencyOffer> getAsks() {
/*  39 */     return this.currencyMarket.getAsks();
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getBestOffer(OfferSide side) {
/*  43 */     if (side.equals(OfferSide.ASK)) {
/*  44 */       if (this.bestOfferAsk == null) {
/*  45 */         this.bestOfferAsk = this.currencyMarket.getBestOffer(side);
/*     */       }
/*  47 */       return this.bestOfferAsk;
/*  48 */     }if (side.equals(OfferSide.BID)) {
/*  49 */       if (this.bestOfferBid == null) {
/*  50 */         this.bestOfferBid = this.currencyMarket.getBestOffer(side);
/*     */       }
/*  52 */       return this.bestOfferBid;
/*     */     }
/*  54 */     return null;
/*     */   }
/*     */ 
/*     */   public List<CurrencyOffer> getBids()
/*     */   {
/*  59 */     return this.currencyMarket.getBids();
/*     */   }
/*     */ 
/*     */   public Long getCreationTimestamp() {
/*  63 */     return this.currencyMarket.getCreationTimestamp();
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary() {
/*  67 */     return this.currencyMarket.getCurrencyPrimary();
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary() {
/*  71 */     return this.currencyMarket.getCurrencySecondary();
/*     */   }
/*     */ 
/*     */   public Money getMoney(String name) {
/*  75 */     return this.currencyMarket.getMoney(name);
/*     */   }
/*     */ 
/*     */   public long getPriority() {
/*  79 */     return this.currencyMarket.getPriority();
/*     */   }
/*     */ 
/*     */   public Date getTimestamp() {
/*  83 */     if (this.timestamp == null) {
/*  84 */       this.timestamp = this.currencyMarket.getTimestamp();
/*     */     }
/*  86 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public Long getTimeSyncMs() {
/*  90 */     return this.currencyMarket.getTimeSyncMs();
/*     */   }
/*     */ 
/*     */   public void setAsks(List<CurrencyOffer> asks) {
/*  94 */     this.currencyMarket.setAsks(asks);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  98 */     return this.currencyMarket.getInstrument().trim();
/*     */   }
/*     */ 
/*     */   public Long getLong(String name) {
/* 102 */     return this.currencyMarket.getLong(name);
/*     */   }
/*     */ 
/*     */   public CurrencyMarket getCurrencyMarket() {
/* 106 */     return this.currencyMarket;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.CurrencyMarketWrapper
 * JD-Core Version:    0.6.0
 */