/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.RequestMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ 
/*     */ public class CurrencyOffer extends RequestMessage
/*     */ {
/*     */   public static final String TYPE = "offer";
/*     */ 
/*     */   public CurrencyOffer(String primaryCurrency, String secondaryCurrency, OfferSide side, Money amount, Money price)
/*     */   {
/*  31 */     setType("offer");
/*  32 */     put("side", side.toString());
/*  33 */     put("price", price.getValue().toPlainString());
/*  34 */     setAmount(amount);
/*  35 */     setInstrument(primaryCurrency + "/" + secondaryCurrency);
/*     */   }
/*     */ 
/*     */   public CurrencyOffer(ProtocolMessage message) {
/*  39 */     if ("offer".equals(message.getType())) {
/*  40 */       put("side", message.getString("side"));
/*  41 */       put("price", message.getString("price"));
/*  42 */       put("ovner", message.getString("ovner"));
/*  43 */       put("amount", message.getString("amount"));
/*  44 */       put("fokAmount", message.getString("fokAmount"));
/*  45 */       setInstrument(message.getString("instrument"));
/*     */     } else {
/*  47 */       throw new IllegalArgumentException("Unable to construct CurrencyOffer from " + message.toProtocolString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public CurrencyOffer(String s) throws ParseException {
/*  52 */     super(s);
/*     */   }
/*     */ 
/*     */   public CurrencyOffer(String price, String amount, String instrument, String side) {
/*  56 */     setType("offer");
/*  57 */     put("side", side);
/*  58 */     put("price", price);
/*  59 */     put("amount", amount);
/*  60 */     setInstrument(instrument);
/*     */   }
/*     */ 
/*     */   public String getOfferMarketPlace()
/*     */   {
/*  69 */     return getString("ovner");
/*     */   }
/*     */ 
/*     */   public void setOfferMarketPlace(String marketPlace)
/*     */   {
/*  78 */     put("ovner", marketPlace);
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument)
/*     */   {
/*  89 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/*  99 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 108 */     return getString("instrument").substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/* 117 */     return getString("instrument").substring(4);
/*     */   }
/*     */ 
/*     */   public OfferSide getSide()
/*     */   {
/* 126 */     return OfferSide.fromString(getString("side"));
/*     */   }
/*     */ 
/*     */   public Money getAmount()
/*     */   {
/* 135 */     if (getString("amount") != null) {
/* 136 */       return new Money(getString("amount"), getCurrencyPrimary());
/*     */     }
/* 138 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount(Money amount)
/*     */   {
/* 147 */     put("amount", amount.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getFokAmount()
/*     */   {
/* 154 */     if (getString("fokAmount") != null) {
/* 155 */       return new Money(getString("fokAmount"), getCurrencyPrimary());
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFokAmount(Money fokAmount)
/*     */   {
/* 165 */     put("fokAmount", fokAmount.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getPrice()
/*     */   {
/* 174 */     if (getString("price") != null) {
/* 175 */       return new Money(getString("price"), getCurrencySecondary());
/*     */     }
/* 177 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CurrencyOffer
 * JD-Core Version:    0.6.0
 */