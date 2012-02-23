/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class BestCurrencyMarketMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "bcm";
/*     */   public static final String INSTRUMENT = "i";
/*     */   public static final String BID = "b";
/*     */   public static final String ASK = "a";
/*     */ 
/*     */   public BestCurrencyMarketMessage()
/*     */   {
/*  28 */     setType("bcm");
/*     */   }
/*     */ 
/*     */   public BestCurrencyMarketMessage(ProtocolMessage message)
/*     */   {
/*  38 */     super(message);
/*     */ 
/*  40 */     setType("bcm");
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
/*  78 */     StringBuffer sb = new StringBuffer();
/*  79 */     if ((bid.getString("price") != null) && (bid.getAmount() != null)) {
/*  80 */       sb.append(bid.getString("price"));
/*  81 */       sb.append(",").append(bid.getAmount().getValue().divide(ONE_MILLION).doubleValue());
/*  82 */       put("b", sb.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getAsks()
/*     */   {
/*  93 */     CurrencyOffer ask = null;
/*  94 */     if (getString("a") != null) {
/*  95 */       StringTokenizer asksArray = new StringTokenizer(getString("a"), ",");
/*  96 */       while (asksArray.hasMoreTokens()) {
/*  97 */         ask = new CurrencyOffer(asksArray.nextToken(), "" + new BigDecimal(asksArray.nextToken()).multiply(ONE_MILLION).doubleValue(), getInstrument(), "BID");
/*     */       }
/*     */     }
/* 100 */     return ask;
/*     */   }
/*     */ 
/*     */   public void setAsk(CurrencyOffer ask)
/*     */   {
/* 110 */     StringBuffer sb = new StringBuffer();
/* 111 */     if ((ask.getString("price") != null) && (ask.getAmount() != null)) {
/* 112 */       sb.append(ask.getString("price"));
/* 113 */       sb.append(",").append(ask.getAmount().getValue().divide(ONE_MILLION).doubleValue());
/* 114 */       put("a", sb.toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.BestCurrencyMarketMessage
 * JD-Core Version:    0.6.0
 */