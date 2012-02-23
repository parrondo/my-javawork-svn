/*     */ package com.dukascopy.transport.common.msg.quote;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.executor.DealType;
/*     */ import com.dukascopy.transport.common.msg.executor.ExecutorOrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class QuoteDealMessage extends ExecutorOrderMessage
/*     */ {
/*     */   public static final String TYPE = "qdeal";
/*     */   private static final String QUOTE_ID = "quoteId";
/*     */   private static final String DEAL_TYPE = "dealType";
/*     */   private static final String CURRENCY = "ord_curr";
/*     */   private static final String VALUE_DATE = "value_date";
/*     */   private static final String SIDE2 = "side2";
/*     */   private static final String PRICE2 = "price2";
/*     */   private static final String AMOUNT2 = "amount2";
/*     */   private static final String VALUE_DATE2 = "value_date2";
/*     */ 
/*     */   public QuoteDealMessage()
/*     */   {
/*  33 */     setType("qdeal");
/*     */   }
/*     */ 
/*     */   public QuoteDealMessage(ProtocolMessage message) {
/*  37 */     super(message);
/*  38 */     setType("qdeal");
/*  39 */     put("quoteId", message.getString("quoteId"));
/*  40 */     put("dealType", message.getString("dealType"));
/*  41 */     put("ord_curr", message.getString("ord_curr"));
/*  42 */     put("value_date", message.getString("value_date"));
/*  43 */     put("side2", message.getString("side2"));
/*  44 */     put("price2", message.getString("price2"));
/*  45 */     put("amount2", message.getString("amount2"));
/*  46 */     put("value_date2", message.getString("value_date2"));
/*  47 */     put("exec", message.getString("exec"));
/*  48 */     put("trans_time", message.getString("trans_time"));
/*     */   }
/*     */ 
/*     */   public void setQuoteId(String quoteId) {
/*  52 */     if (quoteId != null)
/*  53 */       put("quoteId", quoteId);
/*     */   }
/*     */ 
/*     */   public String getQuoteId()
/*     */   {
/*  58 */     return getString("quoteId");
/*     */   }
/*     */ 
/*     */   public void setDealType(DealType dealType) {
/*  62 */     if (dealType != null)
/*  63 */       put("dealType", dealType.toString());
/*     */   }
/*     */ 
/*     */   public DealType getDealType()
/*     */   {
/*  68 */     String type = getString("dealType");
/*  69 */     if (type != null) {
/*  70 */       return DealType.valueOf(type);
/*     */     }
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCurrency(String currency) {
/*  76 */     if (currency != null)
/*  77 */       put("ord_curr", currency);
/*     */   }
/*     */ 
/*     */   public String getCurrency()
/*     */   {
/*  82 */     return getString("ord_curr");
/*     */   }
/*     */ 
/*     */   public void setValueDate(Date valueDate) {
/*  86 */     putDate("value_date", valueDate);
/*     */   }
/*     */ 
/*     */   public Date getValueDate() {
/*  90 */     return getDate("value_date");
/*     */   }
/*     */ 
/*     */   public void setOrderSide2(OrderSide side2) {
/*  94 */     if (side2 != null)
/*  95 */       put("side2", side2.asString());
/*     */   }
/*     */ 
/*     */   public OrderSide getOrderSide2()
/*     */   {
/* 100 */     String sideString2 = getString("side2");
/* 101 */     if (sideString2 != null) {
/* 102 */       return OrderSide.fromString(sideString2);
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPrice2(BigDecimal price2) {
/* 108 */     if (price2 != null)
/* 109 */       put("price2", price2.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice2()
/*     */   {
/* 114 */     String priceStr2 = getString("price2");
/* 115 */     if (priceStr2 != null) {
/* 116 */       return new BigDecimal(priceStr2);
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount2(BigDecimal amount2) {
/* 122 */     if (amount2 != null)
/* 123 */       put("amount2", amount2.stripTrailingZeros().toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount2()
/*     */   {
/* 128 */     String amountStr2 = getString("amount2");
/* 129 */     if (amountStr2 != null) {
/* 130 */       return new BigDecimal(amountStr2);
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   public void setValueDate2(Date valueDate2) {
/* 136 */     putDate("value_date2", valueDate2);
/*     */   }
/*     */ 
/*     */   public Date getValueDate2() {
/* 140 */     return getDate("value_date2");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.quote.QuoteDealMessage
 * JD-Core Version:    0.6.0
 */