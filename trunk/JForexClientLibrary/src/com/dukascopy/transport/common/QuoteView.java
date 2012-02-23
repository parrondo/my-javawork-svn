/*    */ package com.dukascopy.transport.common;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.request.CurrencyQuoteMessage;
/*    */ import java.util.Date;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class QuoteView
/*    */   implements QuoteListener
/*    */ {
/* 18 */   private Map<String, CurrencyQuoteMessage> currencyQuotes = new HashMap();
/*    */ 
/* 20 */   private Map<String, Date> lastReceivedTimes = new HashMap();
/*    */ 
/*    */   public synchronized void onQuote(CurrencyQuoteMessage quote)
/*    */   {
/* 33 */     String instrument = quote.getInstrument();
/* 34 */     setQuote(instrument, quote);
/* 35 */     setLastReceivedTime(instrument, new Date());
/*    */   }
/*    */ 
/*    */   public Set<String> getInstruments()
/*    */   {
/* 43 */     return this.currencyQuotes.keySet();
/*    */   }
/*    */ 
/*    */   public synchronized CurrencyQuoteMessage getQuote(String instrument)
/*    */   {
/* 53 */     return (CurrencyQuoteMessage)this.currencyQuotes.get(instrument);
/*    */   }
/*    */ 
/*    */   public void setLastReceivedTime(String instrument, Date date)
/*    */   {
/* 62 */     this.lastReceivedTimes.put(instrument, date);
/*    */   }
/*    */ 
/*    */   public Date getLastReceivedTime(String instrument)
/*    */   {
/* 71 */     return (Date)this.lastReceivedTimes.get(instrument);
/*    */   }
/*    */ 
/*    */   public synchronized void setQuote(String instrument, CurrencyQuoteMessage quote)
/*    */   {
/* 81 */     this.currencyQuotes.put(instrument, quote);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.QuoteView
 * JD-Core Version:    0.6.0
 */