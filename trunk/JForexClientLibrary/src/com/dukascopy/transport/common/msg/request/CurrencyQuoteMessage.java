/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class CurrencyQuoteMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "cqm";
/*     */   public static final String INSTRUMENT = "i";
/*     */   public static final String BID = "b";
/*     */   public static final String ASK = "a";
/*     */ 
/*     */   public CurrencyQuoteMessage()
/*     */   {
/*  28 */     setType("cqm");
/*     */   }
/*     */ 
/*     */   public CurrencyQuoteMessage(ProtocolMessage message)
/*     */   {
/*  37 */     super(message);
/*     */ 
/*  39 */     setType("cqm");
/*     */ 
/*  41 */     setInstrument(message.getString("i"));
/*  42 */     put("b", message.getString("b"));
/*  43 */     put("a", message.getString("a"));
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  47 */     put("i", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  51 */     return getString("i");
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getBid()
/*     */   {
/*  61 */     CurrencyOffer bid = null;
/*  62 */     if (getString("b") != null) {
/*  63 */       StringTokenizer bidsArray = new StringTokenizer(getString("b"), ",");
/*  64 */       while (bidsArray.hasMoreTokens()) {
/*  65 */         bid = new CurrencyOffer(bidsArray.nextToken(), "" + new BigDecimal(bidsArray.nextToken()).multiply(ONE_MILLION).doubleValue(), getInstrument(), "BID");
/*     */       }
/*     */     }
/*  68 */     return bid;
/*     */   }
/*     */ 
/*     */   public void setBid(CurrencyOffer bid)
/*     */   {
/*  77 */     StringBuffer sb = new StringBuffer();
/*  78 */     if ((bid != null) && (bid.getString("price") != null) && (bid.getAmount() != null)) {
/*  79 */       sb.append(bid.getString("price"));
/*  80 */       sb.append(",").append(bid.getAmount().getValue().divide(ONE_MILLION).doubleValue());
/*  81 */       put("b", sb.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getAsk()
/*     */   {
/*  92 */     CurrencyOffer ask = null;
/*  93 */     if (getString("a") != null) {
/*  94 */       StringTokenizer asksArray = new StringTokenizer(getString("a"), ",");
/*  95 */       while (asksArray.hasMoreTokens()) {
/*  96 */         ask = new CurrencyOffer(asksArray.nextToken(), "" + new BigDecimal(asksArray.nextToken()).multiply(ONE_MILLION).doubleValue(), getInstrument(), "BID");
/*     */       }
/*     */     }
/*  99 */     return ask;
/*     */   }
/*     */ 
/*     */   public void setAsk(CurrencyOffer ask)
/*     */   {
/* 108 */     StringBuffer sb = new StringBuffer();
/* 109 */     if ((ask != null) && (ask.getString("price") != null) && (ask.getAmount() != null)) {
/* 110 */       sb.append(ask.getString("price"));
/* 111 */       sb.append(",").append(ask.getAmount().getValue().divide(ONE_MILLION).doubleValue());
/* 112 */       put("a", sb.toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CurrencyQuoteMessage
 * JD-Core Version:    0.6.0
 */