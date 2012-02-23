/*     */ package com.dukascopy.transport.common.msg.pojo;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.group.TradeMessage;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Trade
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 200706201017L;
/*     */   private Integer tradeId;
/*     */   private String orderId;
/*     */   private String marketPlace;
/*     */   private String orderGroupId;
/*     */   private String initiator;
/*     */   private String counterParty;
/*     */   private Money amountPrimary;
/*     */   private Money amountSecondary;
/*     */   private Money price;
/*     */   private String instrument;
/*     */   private Money referencePrice;
/*     */ 
/*     */   public Trade(Integer tradeId, String orderId, String marketPlace, String currencyPrimary, String currencySecondary, String initiator, String counterParty, Money amountPrimary, Money amountSecondary, Money price)
/*     */   {
/*  71 */     this.tradeId = tradeId;
/*  72 */     this.orderId = orderId;
/*  73 */     this.marketPlace = marketPlace;
/*  74 */     this.instrument = (currencyPrimary + "/" + currencySecondary);
/*  75 */     this.initiator = initiator;
/*  76 */     this.counterParty = counterParty;
/*  77 */     this.amountPrimary = amountPrimary;
/*  78 */     this.amountSecondary = amountSecondary;
/*  79 */     this.price = price;
/*     */   }
/*     */ 
/*     */   public Trade(TradeMessage tradeMessage) {
/*  83 */     this.tradeId = tradeMessage.getTradeId();
/*  84 */     this.orderId = tradeMessage.getOrderId();
/*  85 */     this.marketPlace = tradeMessage.getMarketPlace();
/*  86 */     this.instrument = tradeMessage.getInstrument();
/*  87 */     this.initiator = tradeMessage.getInitiator();
/*  88 */     this.counterParty = tradeMessage.getCounterParty();
/*  89 */     this.amountPrimary = tradeMessage.getAmountPrimary();
/*  90 */     this.amountSecondary = tradeMessage.getAmountSecondary();
/*  91 */     this.price = tradeMessage.getPrice();
/*  92 */     this.referencePrice = tradeMessage.getReferencePrice();
/*  93 */     this.orderGroupId = tradeMessage.getOrderGroupId();
/*     */   }
/*     */ 
/*     */   public Trade(Trade trade) {
/*  97 */     this.tradeId = trade.getTradeId();
/*  98 */     this.orderId = trade.getOrderId();
/*  99 */     this.marketPlace = trade.getMarketPlace();
/* 100 */     this.instrument = trade.getInstrument();
/* 101 */     this.initiator = trade.getInitiator();
/* 102 */     this.counterParty = trade.getCounterParty();
/* 103 */     this.amountPrimary = trade.getAmountPrimary();
/* 104 */     this.amountSecondary = trade.getAmountSecondary();
/* 105 */     this.price = trade.getPrice();
/* 106 */     this.referencePrice = trade.getReferencePrice();
/* 107 */     this.orderGroupId = trade.getOrderGroupId();
/*     */   }
/*     */ 
/*     */   public TradeMessage toTradeMessage() {
/* 111 */     TradeMessage tm = new TradeMessage(this.tradeId, this.orderId, this.marketPlace, getCurrencyPrimary(), getCurrencySecondary(), getInitiator(), getCounterParty(), getAmountPrimary(), getAmountSecondary(), getPrice());
/*     */ 
/* 114 */     if (getReferencePrice() != null) {
/* 115 */       tm.setReferencePrice(getReferencePrice());
/*     */     }
/* 117 */     tm.setOrderGroupId(getOrderGroupId());
/* 118 */     return tm;
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 128 */     return this.instrument.substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/* 138 */     return this.instrument.substring(4);
/*     */   }
/*     */ 
/*     */   public Integer getTradeId()
/*     */   {
/* 145 */     return this.tradeId;
/*     */   }
/*     */ 
/*     */   public void setTradeId(Integer tradeId)
/*     */   {
/* 153 */     this.tradeId = tradeId;
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/* 160 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/* 168 */     this.orderId = orderId;
/*     */   }
/*     */ 
/*     */   public String getMarketPlace()
/*     */   {
/* 175 */     return this.marketPlace;
/*     */   }
/*     */ 
/*     */   public void setMarketPlace(String marketPlace)
/*     */   {
/* 183 */     this.marketPlace = marketPlace;
/*     */   }
/*     */ 
/*     */   public String getOrderGroupId()
/*     */   {
/* 190 */     return this.orderGroupId;
/*     */   }
/*     */ 
/*     */   public void setOrderGroupId(String orderGroupId)
/*     */   {
/* 198 */     this.orderGroupId = orderGroupId;
/*     */   }
/*     */ 
/*     */   public String getInitiator()
/*     */   {
/* 205 */     return this.initiator;
/*     */   }
/*     */ 
/*     */   public void setInitiator(String initiator)
/*     */   {
/* 213 */     this.initiator = initiator;
/*     */   }
/*     */ 
/*     */   public String getCounterParty()
/*     */   {
/* 220 */     return this.counterParty;
/*     */   }
/*     */ 
/*     */   public void setCounterParty(String counterParty)
/*     */   {
/* 228 */     this.counterParty = counterParty;
/*     */   }
/*     */ 
/*     */   public Money getAmountPrimary()
/*     */   {
/* 235 */     return this.amountPrimary;
/*     */   }
/*     */ 
/*     */   public void setAmountPrimary(Money amountPrimary)
/*     */   {
/* 243 */     this.amountPrimary = amountPrimary;
/*     */   }
/*     */ 
/*     */   public Money getAmountSecondary()
/*     */   {
/* 250 */     return this.amountSecondary;
/*     */   }
/*     */ 
/*     */   public void setAmountSecondary(Money amountSecondary)
/*     */   {
/* 258 */     this.amountSecondary = amountSecondary;
/*     */   }
/*     */ 
/*     */   public Money getPrice()
/*     */   {
/* 265 */     return this.price;
/*     */   }
/*     */ 
/*     */   public void setPrice(Money price)
/*     */   {
/* 273 */     this.price = price;
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 280 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument)
/*     */   {
/* 288 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public Money getReferencePrice()
/*     */   {
/* 295 */     return this.referencePrice;
/*     */   }
/*     */ 
/*     */   public void setReferencePrice(Money referencePrice)
/*     */   {
/* 303 */     this.referencePrice = referencePrice;
/*     */   }
/*     */ 
/*     */   public void strip()
/*     */   {
/* 310 */     this.referencePrice = null;
/* 311 */     this.initiator = null;
/* 312 */     this.counterParty = null;
/* 313 */     this.marketPlace = null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.pojo.Trade
 * JD-Core Version:    0.6.0
 */