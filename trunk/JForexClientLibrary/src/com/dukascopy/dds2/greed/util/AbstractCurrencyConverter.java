/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Collections;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class AbstractCurrencyConverter
/*     */ {
/*     */   protected static Map<Currency, Instrument> pairs;
/*     */   protected static Set<Currency> majors;
/*     */   protected static Currency usdCurrency;
/*     */ 
/*     */   public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*     */   {
/*  69 */     return convert(amount, sourceCurrency, targetCurrency, side, 2);
/*     */   }
/*     */ 
/*     */   public BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side, int scale) {
/*  73 */     assert (amount != null);
/*  74 */     double result = convert(amount.doubleValue(), sourceCurrency, targetCurrency, side, scale);
/*  75 */     if (!Double.isNaN(result)) {
/*  76 */       return new BigDecimal(result).setScale(scale, RoundingMode.HALF_EVEN);
/*     */     }
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   public double convert(double amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*     */   {
/*  90 */     return convert(amount, sourceCurrency, targetCurrency, side, 2);
/*     */   }
/*     */ 
/*     */   public double convert(double amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side, int scale)
/*     */   {
/* 103 */     double result = convertUnrounded(amount, sourceCurrency, targetCurrency, side);
/* 104 */     if (!Double.isNaN(result)) {
/* 105 */       result = StratUtils.roundHalfEven(result, scale);
/*     */     }
/* 107 */     return result;
/*     */   }
/*     */ 
/*     */   public double convertUnrounded(double amount, Currency sourceCurrency, Currency targetCurrency, OfferSide side)
/*     */   {
/* 119 */     assert (sourceCurrency != null);
/* 120 */     assert (targetCurrency != null);
/*     */ 
/* 123 */     if (targetCurrency.equals(sourceCurrency))
/* 124 */       return amount;
/*     */     double dollarValue;
/*     */     double dollarValue;
/* 137 */     if (sourceCurrency.equals(usdCurrency)) {
/* 138 */       dollarValue = amount;
/*     */     } else {
/* 140 */       Instrument helperSourceCurrencyPair = (Instrument)pairs.get(sourceCurrency);
/* 141 */       if (helperSourceCurrencyPair == null) {
/* 142 */         throw new IllegalArgumentException("Conversion Error: No currency pair found for " + sourceCurrency);
/*     */       }
/*     */ 
/* 145 */       double helperSourceCurrencyPrice = getLastMarketPrice(helperSourceCurrencyPair, side);
/* 146 */       if (Double.isNaN(helperSourceCurrencyPrice)) {
/* 147 */         return (0.0D / 0.0D);
/*     */       }
/*     */ 
/* 150 */       dollarValue = helperSourceCurrencyPair.getPrimaryCurrency().equals(usdCurrency) ? amount / helperSourceCurrencyPrice : amount * helperSourceCurrencyPrice;
/*     */     }
/*     */ 
/* 157 */     if (targetCurrency.equals(usdCurrency)) {
/* 158 */       return dollarValue;
/*     */     }
/*     */ 
/* 163 */     Instrument pair = (Instrument)pairs.get(targetCurrency);
/* 164 */     double price = getLastMarketPrice(pair, side);
/* 165 */     if (Double.isNaN(price)) {
/* 166 */       return (0.0D / 0.0D);
/*     */     }
/*     */ 
/* 169 */     return pair.getPrimaryCurrency().equals(usdCurrency) ? dollarValue * price : dollarValue / price;
/*     */   }
/*     */ 
/*     */   protected abstract double getLastMarketPrice(Instrument paramInstrument, OfferSide paramOfferSide);
/*     */ 
/*     */   public static Set<Instrument> getConversionDeps(Currency fromCurrency, Currency targetCurrency)
/*     */   {
/* 179 */     assert (fromCurrency != null);
/* 180 */     assert (targetCurrency != null);
/* 181 */     Set result = new HashSet();
/*     */ 
/* 184 */     if (targetCurrency.equals(fromCurrency)) {
/* 185 */       return result;
/*     */     }
/*     */ 
/* 189 */     if (!fromCurrency.equals(usdCurrency))
/*     */     {
/* 192 */       result.add(pairs.get(fromCurrency));
/*     */     }
/*     */ 
/* 197 */     if (targetCurrency.equals(usdCurrency)) {
/* 198 */       return result;
/*     */     }
/*     */ 
/* 201 */     result.add(pairs.get(targetCurrency));
/*     */ 
/* 203 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<String> getConversionStringDeps(Currency fromCurrency, Currency targetCurency) {
/* 207 */     Set conversionDeps = getConversionDeps(fromCurrency, targetCurency);
/* 208 */     Set stringDeps = new HashSet();
/* 209 */     for (Instrument instrument : conversionDeps) {
/* 210 */       stringDeps.add(instrument.toString());
/*     */     }
/* 212 */     return stringDeps;
/*     */   }
/*     */ 
/*     */   public static Map<Currency, Instrument> getConversionPairs() {
/* 216 */     return Collections.unmodifiableMap(pairs);
/*     */   }
/*     */ 
/*     */   public static Set<Currency> getMajors() {
/* 220 */     return Collections.unmodifiableSet(majors);
/*     */   }
/*     */ 
/*     */   public static Currency findMajor(String currencyCode)
/*     */   {
/* 229 */     for (Currency currency : majors) {
/* 230 */       if (currency.getCurrencyCode().equals(currencyCode)) {
/* 231 */         return currency;
/*     */       }
/*     */     }
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  21 */     pairs = new HashMap();
/*  22 */     majors = new HashSet();
/*     */ 
/*  24 */     usdCurrency = Currency.getInstance("USD");
/*     */ 
/*  27 */     pairs.put(Currency.getInstance("AUD"), Instrument.AUDUSD);
/*  28 */     pairs.put(Currency.getInstance("CAD"), Instrument.USDCAD);
/*  29 */     pairs.put(Currency.getInstance("CHF"), Instrument.USDCHF);
/*  30 */     pairs.put(Currency.getInstance("DKK"), Instrument.USDDKK);
/*  31 */     pairs.put(Currency.getInstance("EUR"), Instrument.EURUSD);
/*  32 */     pairs.put(Currency.getInstance("GBP"), Instrument.GBPUSD);
/*  33 */     pairs.put(Currency.getInstance("HKD"), Instrument.USDHKD);
/*  34 */     pairs.put(Currency.getInstance("JPY"), Instrument.USDJPY);
/*  35 */     pairs.put(Currency.getInstance("MXN"), Instrument.USDMXN);
/*  36 */     pairs.put(Currency.getInstance("NOK"), Instrument.USDNOK);
/*  37 */     pairs.put(Currency.getInstance("NZD"), Instrument.NZDUSD);
/*  38 */     pairs.put(Currency.getInstance("SEK"), Instrument.USDSEK);
/*  39 */     pairs.put(Currency.getInstance("SGD"), Instrument.USDSGD);
/*  40 */     pairs.put(Currency.getInstance("TRY"), Instrument.USDTRY);
/*  41 */     pairs.put(Currency.getInstance("XAU"), Instrument.XAUUSD);
/*  42 */     pairs.put(Currency.getInstance("XAG"), Instrument.XAGUSD);
/*  43 */     pairs.put(Currency.getInstance("HUF"), Instrument.USDHUF);
/*  44 */     pairs.put(Currency.getInstance("PLN"), Instrument.USDPLN);
/*  45 */     pairs.put(Currency.getInstance("CZK"), Instrument.USDCZK);
/*  46 */     pairs.put(Currency.getInstance("RON"), Instrument.USDRON);
/*  47 */     pairs.put(Currency.getInstance("RUB"), Instrument.USDRUB);
/*  48 */     pairs.put(Currency.getInstance("ZAR"), Instrument.USDZAR);
/*  49 */     pairs.put(Currency.getInstance("BRL"), Instrument.USDBRL);
/*     */ 
/*  52 */     majors.add(Currency.getInstance("USD"));
/*  53 */     majors.add(Currency.getInstance("JPY"));
/*  54 */     majors.add(Currency.getInstance("CHF"));
/*  55 */     majors.add(Currency.getInstance("GBP"));
/*  56 */     majors.add(Currency.getInstance("EUR"));
/*  57 */     majors.add(Currency.getInstance("CAD"));
/*  58 */     majors.add(Currency.getInstance("AUD"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.AbstractCurrencyConverter
 * JD-Core Version:    0.6.0
 */