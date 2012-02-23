/*     */ package com.dukascopy.dds2.greed.connector.impl;
/*     */ 
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IHistory;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CurrConverter
/*     */ {
/*  36 */   protected static Map<Currency, Instrument> pairs = new HashMap();
/*     */ 
/*  38 */   protected static Set<Currency> majors = new HashSet();
/*     */ 
/*  66 */   private IHistory history = null;
/*     */ 
/*     */   public CurrConverter(IContext context) {
/*  69 */     this.history = context.getHistory();
/*     */   }
/*     */ 
/*     */   public CurrConverter(IHistory history) {
/*  73 */     this.history = history;
/*     */   }
/*     */ 
/*     */   double convert(double amount, Currency sourceCurrency, Currency targetCurrency)
/*     */     throws JFException
/*     */   {
/*  86 */     if (targetCurrency.equals(sourceCurrency))
/*  87 */       return amount;
/*     */     double dollarValue;
/*     */     double dollarValue;
/*  92 */     if (sourceCurrency.equals(Currency.getInstance("USD"))) {
/*  93 */       dollarValue = amount;
/*     */     } else {
/*  95 */       Instrument helperSourceCurrencyPair = (Instrument)pairs.get(sourceCurrency);
/*  96 */       if (helperSourceCurrencyPair == null) {
/*  97 */         throw new IllegalArgumentException("Conversion Error: No currency pair found for " + sourceCurrency);
/*     */       }
/*     */ 
/* 100 */       double helperSourceCurrencyPrice = getLastMarketPrice(helperSourceCurrencyPair, OfferSide.BID);
/* 101 */       if (0.0D == helperSourceCurrencyPrice) {
/* 102 */         return 0.0D;
/*     */       }
/* 104 */       dollarValue = helperSourceCurrencyPair.getPrimaryCurrency() == Currency.getInstance("USD") ? amount / helperSourceCurrencyPrice : amount * helperSourceCurrencyPrice;
/*     */     }
/*     */ 
/* 109 */     if (targetCurrency.equals(Currency.getInstance("USD"))) {
/* 110 */       return dollarValue;
/*     */     }
/*     */ 
/* 115 */     Instrument pair = (Instrument)pairs.get(targetCurrency);
/* 116 */     double price = getLastMarketPrice(pair, OfferSide.BID);
/* 117 */     if (0.0D == price) {
/* 118 */       return 0.0D;
/*     */     }
/* 120 */     double result = pair.getPrimaryCurrency() == Currency.getInstance("USD") ? dollarValue * price : dollarValue / price;
/*     */ 
/* 122 */     return result;
/*     */   }
/*     */ 
/*     */   public double getLastMarketPrice(Instrument pair, OfferSide side) throws JFException {
/* 126 */     double rc = 0.0D;
/* 127 */     ITick tick = this.history.getLastTick(pair);
/*     */ 
/* 129 */     if (tick != null) {
/* 130 */       rc = side == OfferSide.ASK ? tick.getAsk() : tick.getBid();
/*     */     }
/* 132 */     return rc;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  42 */     pairs.put(Currency.getInstance("AUD"), Instrument.AUDUSD);
/*  43 */     pairs.put(Currency.getInstance("CAD"), Instrument.USDCAD);
/*  44 */     pairs.put(Currency.getInstance("CHF"), Instrument.USDCHF);
/*  45 */     pairs.put(Currency.getInstance("DKK"), Instrument.USDDKK);
/*  46 */     pairs.put(Currency.getInstance("EUR"), Instrument.EURUSD);
/*  47 */     pairs.put(Currency.getInstance("GBP"), Instrument.GBPUSD);
/*  48 */     pairs.put(Currency.getInstance("HKD"), Instrument.USDHKD);
/*  49 */     pairs.put(Currency.getInstance("JPY"), Instrument.USDJPY);
/*  50 */     pairs.put(Currency.getInstance("MXN"), Instrument.USDMXN);
/*  51 */     pairs.put(Currency.getInstance("NOK"), Instrument.USDNOK);
/*  52 */     pairs.put(Currency.getInstance("NZD"), Instrument.NZDUSD);
/*  53 */     pairs.put(Currency.getInstance("SEK"), Instrument.USDSEK);
/*  54 */     pairs.put(Currency.getInstance("SGD"), Instrument.USDSGD);
/*  55 */     pairs.put(Currency.getInstance("TRY"), Instrument.USDTRY);
/*     */ 
/*  57 */     majors.add(Currency.getInstance("USD"));
/*  58 */     majors.add(Currency.getInstance("JPY"));
/*  59 */     majors.add(Currency.getInstance("CHF"));
/*  60 */     majors.add(Currency.getInstance("GBP"));
/*  61 */     majors.add(Currency.getInstance("EUR"));
/*  62 */     majors.add(Currency.getInstance("CAD"));
/*  63 */     majors.add(Currency.getInstance("AUD"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.impl.CurrConverter
 * JD-Core Version:    0.6.0
 */