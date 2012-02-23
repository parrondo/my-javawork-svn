/*     */ package com.dukascopy.transport.common.msg.quote;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.executor.DealType;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class QuoteRequestMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "qreq";
/*     */   public static final String QUOTE_REQ_ID = "qreq_id";
/*     */   public static final String DEAL_TYPE = "d_type";
/*     */   public static final String INSTRUMENT = "inst";
/*     */   public static final String CURRENCY = "curr";
/*     */   public static final String TENOR = "tenor";
/*     */   public static final String VALUE_DATE = "value_date";
/*     */   public static final String ORDER_QTY = "qty";
/*     */   public static final String TENOR2 = "tenor2";
/*     */   public static final String VALUE_DATE2 = "value_date2";
/*     */   public static final String ORDER_QTY2 = "qty2";
/*     */   private static final String PARTY_ID = "partyId";
/*     */ 
/*     */   public QuoteRequestMessage()
/*     */   {
/*  42 */     setType("qreq");
/*     */   }
/*     */ 
/*     */   public QuoteRequestMessage(ProtocolMessage message) {
/*  46 */     super(message);
/*     */ 
/*  48 */     setType("qreq");
/*  49 */     setQuoteRequestId(message.getString("qreq_id"));
/*  50 */     put("d_type", message.getString("d_type"));
/*  51 */     setInstrument(message.getString("inst"));
/*  52 */     setCurrency(message.getString("curr"));
/*  53 */     put("tenor", message.getString("tenor"));
/*  54 */     setValueDate(message.getDate("value_date"));
/*  55 */     setOrderQty(message.getBigDecimal("qty"));
/*  56 */     put("tenor2", message.getString("tenor2"));
/*  57 */     setValueDate2(message.getDate("value_date2"));
/*  58 */     setOrderQty2(message.getBigDecimal("qty2"));
/*  59 */     setPartyId(message.getString("partyId"));
/*     */   }
/*     */ 
/*     */   public void setQuoteRequestId(String quoteId) {
/*  63 */     put("qreq_id", quoteId);
/*     */   }
/*     */ 
/*     */   public String getQuoteRequestId() {
/*  67 */     return getString("qreq_id");
/*     */   }
/*     */ 
/*     */   public void setDealType(DealType dealType) {
/*  71 */     if (dealType != null)
/*  72 */       put("d_type", dealType.toString());
/*     */   }
/*     */ 
/*     */   public DealType getDealType()
/*     */   {
/*  77 */     String type = getString("d_type");
/*  78 */     if (type != null) {
/*  79 */       return DealType.valueOf(type);
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  85 */     put("inst", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  89 */     return getString("inst");
/*     */   }
/*     */ 
/*     */   public void setCurrency(String currency) {
/*  93 */     put("curr", currency);
/*     */   }
/*     */ 
/*     */   public String getCurrency() {
/*  97 */     return getString("curr");
/*     */   }
/*     */ 
/*     */   public void setTenor(Tenor tenor) {
/* 101 */     if (tenor != null)
/* 102 */       put("tenor", tenor);
/*     */   }
/*     */ 
/*     */   public Tenor getTenor()
/*     */   {
/* 107 */     String tenor = getString("tenor");
/* 108 */     if (tenor != null) {
/* 109 */       return Tenor.valueOf(tenor);
/*     */     }
/* 111 */     return null;
/*     */   }
/*     */ 
/*     */   public void setValueDate(Date valueDate) {
/* 115 */     putDate("value_date", valueDate);
/*     */   }
/*     */ 
/*     */   public Date getValueDate() {
/* 119 */     return getDate("value_date");
/*     */   }
/*     */ 
/*     */   public void setOrderQty(BigDecimal orderQty) {
/* 123 */     put("qty", orderQty);
/*     */   }
/*     */ 
/*     */   public BigDecimal getOrderQty() {
/* 127 */     return getBigDecimal("qty");
/*     */   }
/*     */ 
/*     */   public void setTenor2(Tenor tenor2) {
/* 131 */     if (tenor2 != null)
/* 132 */       put("tenor2", tenor2);
/*     */   }
/*     */ 
/*     */   public Tenor getTenor2()
/*     */   {
/* 137 */     String tenor2 = getString("tenor2");
/* 138 */     if (tenor2 != null) {
/* 139 */       return Tenor.valueOf(tenor2);
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   public void setValueDate2(Date valueDate2) {
/* 145 */     putDate("value_date2", valueDate2);
/*     */   }
/*     */ 
/*     */   public Date getValueDate2() {
/* 149 */     return getDate("value_date2");
/*     */   }
/*     */ 
/*     */   public void setOrderQty2(BigDecimal orderQty2) {
/* 153 */     put("qty2", orderQty2);
/*     */   }
/*     */ 
/*     */   public BigDecimal getOrderQty2() {
/* 157 */     return getBigDecimal("qty2");
/*     */   }
/*     */ 
/*     */   public void setPartyId(String partyId) {
/* 161 */     if (partyId != null)
/* 162 */       put("partyId", partyId);
/*     */   }
/*     */ 
/*     */   public String getPartyId()
/*     */   {
/* 167 */     return getString("partyId");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.quote.QuoteRequestMessage
 * JD-Core Version:    0.6.0
 */