/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.util.Currency;
/*    */ import java.util.HashMap;
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class CurrencyMapper
/*    */ {
/* 15 */   private static final Map<Currency, Locale> map = new HashMap();
/*    */ 
/*    */   public static Locale getLocaleByCurrency(Currency currency)
/*    */   {
/* 35 */     return (Locale)map.get(currency);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 18 */     map.put(Currency.getInstance("USD"), Locale.US);
/* 19 */     map.put(Currency.getInstance("CHF"), new Locale("fr", "CH"));
/* 20 */     map.put(Currency.getInstance("EUR"), new Locale("de", "DE"));
/* 21 */     map.put(Currency.getInstance("GBP"), Locale.UK);
/* 22 */     map.put(Currency.getInstance("JPY"), Locale.JAPANESE);
/*    */ 
/* 24 */     map.put(Currency.getInstance("AUD"), new Locale("en", "AU"));
/* 25 */     map.put(Currency.getInstance("CAD"), Locale.CANADA);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.CurrencyMapper
 * JD-Core Version:    0.6.0
 */