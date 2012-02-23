/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ 
/*     */ public class TradeOffer extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "trade_offer";
/*     */ 
/*     */   public TradeOffer(String requestId, String primaryCurrency, String secondaryCurrency, OfferSide side, Money amount, Money price)
/*     */   {
/*  17 */     setType("trade_offer");
/*  18 */     put("side", side.toString());
/*  19 */     put("price", price.getValue().toPlainString());
/*  20 */     put("requestId", requestId);
/*  21 */     setAmount(amount);
/*  22 */     setInstrument(primaryCurrency + "/" + secondaryCurrency);
/*     */   }
/*     */ 
/*     */   public TradeOffer(ProtocolMessage message) {
/*  26 */     super(message);
/*  27 */     if ("trade_offer".equals(message.getType())) {
/*  28 */       put("side", message.getString("side"));
/*  29 */       put("price", message.getString("price"));
/*  30 */       put("ovner", message.getString("ovner"));
/*  31 */       put("requestId", message.getString("requestId"));
/*  32 */       setInstrument(message.getString("instrument"));
/*  33 */       put("amount", message.getString("amount"));
/*     */     } else {
/*  35 */       throw new IllegalArgumentException("Unable to construct CurrencyOffer from " + message.toProtocolString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public TradeOffer(String s) throws ParseException {
/*  40 */     super(s);
/*     */   }
/*     */ 
/*     */   public TradeOffer(String requestId, String price, String amount, String instrument, OfferSide side) {
/*  44 */     setType("trade_offer");
/*  45 */     put("side", side.asString());
/*  46 */     put("price", price);
/*  47 */     put("amount", amount);
/*  48 */     put("requestId", requestId);
/*  49 */     setInstrument(instrument);
/*     */   }
/*     */ 
/*     */   public void setRequestId(String requestId)
/*     */   {
/*  55 */     put("requestId", requestId);
/*     */   }
/*     */ 
/*     */   public String getRequestId() {
/*  59 */     return getString("requestId");
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide() {
/*  63 */     return OfferSide.fromString(getString("side"));
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide side) {
/*  67 */     put("side", side.toString());
/*     */   }
/*     */ 
/*     */   public String getOfferMarketPlace()
/*     */   {
/*  77 */     return getString("ovner");
/*     */   }
/*     */ 
/*     */   public void setOfferMarketPlace(String marketPlace)
/*     */   {
/*  86 */     put("ovner", marketPlace);
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument)
/*     */   {
/*  97 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 107 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 116 */     return getString("instrument").substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/* 125 */     return getString("instrument").substring(4);
/*     */   }
/*     */ 
/*     */   public OfferSide getSide()
/*     */   {
/* 134 */     return OfferSide.fromString(getString("side"));
/*     */   }
/*     */ 
/*     */   public Money getAmount()
/*     */   {
/* 143 */     if (getString("amount") != null) {
/* 144 */       return new Money(getString("amount"), getCurrencyPrimary());
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount(Money amount)
/*     */   {
/* 155 */     put("amount", amount.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getPrice()
/*     */   {
/* 164 */     if (getString("price") != null) {
/* 165 */       return new Money(getString("price"), getCurrencySecondary());
/*     */     }
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPrice(Money price)
/*     */   {
/* 172 */     if (price != null)
/* 173 */       put("price", price.getValue().toPlainString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.TradeOffer
 * JD-Core Version:    0.6.0
 */