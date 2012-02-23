/*     */ package com.dukascopy.dds2.calc;
/*     */ 
/*     */ import com.dukascopy.transport.common.QuoteView;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyQuoteMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CurrencyQuoteConverter
/*     */ {
/*     */   private QuoteView quoteView;
/*     */   private static Map<Currency, String> pairs;
/*     */ 
/*     */   public CurrencyQuoteConverter(QuoteView quoteView)
/*     */   {
/*  45 */     this.quoteView = quoteView;
/*     */   }
/*     */ 
/*     */   public Money convert(Money amount, Currency currency) {
/*  49 */     return convert(amount, currency, null);
/*     */   }
/*     */ 
/*     */   public Money convert(Money amount, Currency currency, OfferSide side) {
/*  53 */     assert (amount != null);
/*  54 */     assert (currency != null);
/*     */ 
/*  57 */     if (currency.equals(amount.getCurrency()))
/*  58 */       return amount;
/*     */     BigDecimal dollarValue;
/*     */     BigDecimal dollarValue;
/*  69 */     if (amount.getCurrency().equals(Currency.getInstance("USD"))) {
/*  70 */       dollarValue = amount.getValue();
/*     */     }
/*     */     else {
/*  73 */       String pair = (String)pairs.get(amount.getCurrency());
/*  74 */       if (pair == null)
/*  75 */         throw new IllegalArgumentException("No currency pair found for " + amount.getCurrency());
/*     */       Money price;
/*     */       Money price;
/*  78 */       if (side == null)
/*     */       {
/*     */         Money price;
/*  79 */         if (amount.getValue().compareTo(BigDecimal.ZERO) > 0)
/*  80 */           price = getPrice(pair, OfferSide.ASK);
/*     */         else {
/*  82 */           price = getPrice(pair, OfferSide.BID);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  89 */         price = getPrice(pair, side);
/*     */       }
/*     */ 
/*  92 */       int usdIndex = pair.indexOf("USD");
/*  93 */       boolean reverse = usdIndex == 0;
/*  94 */       dollarValue = reverse ? amount.getValue().divide(price.getValue(), RoundingMode.HALF_EVEN) : amount.getValue().multiply(price.getValue());
/*     */     }
/*     */ 
/* 101 */     if (currency.equals(Currency.getInstance("USD"))) {
/* 102 */       return new Money(dollarValue, Currency.getInstance("USD"));
/*     */     }
/*     */ 
/* 107 */     String pair = (String)pairs.get(currency);
/*     */     Money price;
/*     */     Money price;
/* 109 */     if (side == null)
/*     */     {
/*     */       Money price;
/* 110 */       if (amount.getValue().compareTo(BigDecimal.ZERO) > 0)
/* 111 */         price = getPrice(pair, OfferSide.ASK);
/*     */       else {
/* 113 */         price = getPrice(pair, OfferSide.BID);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 120 */       price = getPrice(pair, side);
/*     */     }
/* 122 */     int usdIndex = pair.indexOf("USD");
/* 123 */     boolean reverse = usdIndex == 0;
/*     */ 
/* 125 */     BigDecimal result = reverse ? dollarValue.multiply(price.getValue()) : dollarValue.divide(price.getValue(), RoundingMode.HALF_EVEN);
/*     */ 
/* 129 */     return new Money(result, currency);
/*     */   }
/*     */ 
/*     */   private Money getPrice(String pair, OfferSide side) {
/* 133 */     CurrencyQuoteMessage quote = this.quoteView.getQuote(pair);
/* 134 */     if (quote == null) {
/* 135 */       throw new IllegalStateException("No quote found for currency pair: " + pair);
/*     */     }
/* 137 */     return side == OfferSide.BID ? quote.getBid().getPrice() : quote.getAsk().getPrice();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  19 */     pairs = new HashMap();
/*     */ 
/*  23 */     pairs.put(Currency.getInstance("EUR"), "EUR/USD");
/*  24 */     pairs.put(Currency.getInstance("CHF"), "USD/CHF");
/*  25 */     pairs.put(Currency.getInstance("GBP"), "GBP/USD");
/*  26 */     pairs.put(Currency.getInstance("JPY"), "USD/JPY");
/*  27 */     pairs.put(Currency.getInstance("SGD"), "USD/SGD");
/*  28 */     pairs.put(Currency.getInstance("SEK"), "USD/SEK");
/*  29 */     pairs.put(Currency.getInstance("NZD"), "NZD/USD");
/*  30 */     pairs.put(Currency.getInstance("AUD"), "AUD/USD");
/*  31 */     pairs.put(Currency.getInstance("NZD"), "NZD/USD");
/*  32 */     pairs.put(Currency.getInstance("NOK"), "USD/NOK");
/*  33 */     pairs.put(Currency.getInstance("CAD"), "USD/CAD");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.calc.CurrencyQuoteConverter
 * JD-Core Version:    0.6.0
 */