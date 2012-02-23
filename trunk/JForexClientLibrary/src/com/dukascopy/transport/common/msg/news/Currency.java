/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ public enum Currency
/*    */ {
/*  5 */   ARS("Argentine Peso"), 
/*  6 */   AUD("Australian Dollar"), 
/*  7 */   BGN("Bulgarian Lev"), 
/*  8 */   BRL("Brazilian Real"), 
/*  9 */   CAD("Canadian Dollar"), 
/* 10 */   CHF("Swiss Franc"), 
/* 11 */   CNY("Yuan Renminbi"), 
/* 12 */   CZK("Czech Koruna"), 
/* 13 */   DKK("Danish Krone"), 
/* 14 */   EUR("Euro"), 
/* 15 */   GBP("Pound Sterling"), 
/* 16 */   HKD("Hong Kong Dollar"), 
/* 17 */   HUF("Forint"), 
/* 18 */   INR("Indian Rupee"), 
/* 19 */   JPY("Yen"), 
/* 20 */   MXN("Mexican Nuevo Peso"), 
/* 21 */   NOK("Norwegian Krone"), 
/* 22 */   NZD("New Zealand Dollar"), 
/* 23 */   PLN("Zloty"), 
/* 24 */   RON("Romanian New Leu"), 
/* 25 */   RUB("Russian Ruble"), 
/* 26 */   SAR("Saudi Riyal"), 
/* 27 */   SEK("Swedish Krone"), 
/* 28 */   SGD("Singapore Dollar"), 
/* 29 */   TRY("New Turkish Lira"), 
/* 30 */   TWD("New Taiwan Dollar"), 
/* 31 */   USD("U.S. Dollar"), 
/* 32 */   ZAR("Rand");
/*    */ 
/*    */   public static final String PREFIX = "M";
/*    */   private String description;
/*    */ 
/* 39 */   private Currency(String description) { this.description = description; }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 43 */     return this.description;
/*    */   }
/*    */ 
/*    */   public static Currency toCurrency(String value) {
/* 47 */     Currency currency = null;
/*    */     try {
/* 49 */       currency = valueOf(value); } catch (IllegalArgumentException exc) {
/*    */     }
/* 51 */     return currency;
/*    */   }
/*    */ 
/*    */   public String getCode() {
/* 55 */     return "M" + '/' + name();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.Currency
 * JD-Core Version:    0.6.0
 */