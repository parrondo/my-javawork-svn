/*     */ package com.dukascopy.transport.common.msg.quote;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.executor.DealType;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class QuoteMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "qt";
/*     */   public static final String QUOTE_REQ_ID = "qreq_id";
/*     */   public static final String QUOTE_ID = "quote_id";
/*     */   public static final String DEAL_TYPE = "d_type";
/*     */   public static final String INSTRUMENT = "inst";
/*     */   public static final String CURRENCY = "curr";
/*     */   public static final String SPOT_BID = "bid";
/*     */   public static final String SPOT_ASK = "ask";
/*     */   public static final String BID_SIZE = "bid_size";
/*     */   public static final String ASK_SIZE = "ask_size";
/*     */   public static final String BID_FORWARD_POINTS = "bid_fwp";
/*     */   public static final String ASK_FORWARD_POINTS = "ask_fwp";
/*     */   public static final String VALUE_DATE = "value_date";
/*     */   public static final String BID_FORWARD_POINTS2 = "bid_fwp2";
/*     */   public static final String ASK_FORWARD_POINTS2 = "ask_fwp2";
/*     */   public static final String VALUE_DATE2 = "value_date2";
/*     */   public static final String VALID_TILL_DATE = "valid_till";
/*     */   public static final String NOTES = "notes";
/*     */ 
/*     */   public QuoteMessage()
/*     */   {
/*  54 */     setType("qt");
/*     */   }
/*     */ 
/*     */   public QuoteMessage(ProtocolMessage message) {
/*  58 */     super(message);
/*     */ 
/*  60 */     setType("qt");
/*  61 */     setQuoteRequestId(message.getString("qreq_id"));
/*  62 */     setQuoteId(message.getString("quote_id"));
/*  63 */     put("d_type", message.getString("d_type"));
/*  64 */     setInstrument(message.getString("inst"));
/*  65 */     setCurrency(message.getString("curr"));
/*  66 */     setSpotBid(message.getBigDecimal("bid"));
/*  67 */     setSpotAsk(message.getBigDecimal("ask"));
/*  68 */     setBidSize(message.getBigDecimal("bid_size"));
/*  69 */     setAskSize(message.getBigDecimal("ask_size"));
/*  70 */     setBidForwardPoints(message.getBigDecimal("bid_fwp"));
/*  71 */     setAskForwardPoints(message.getBigDecimal("ask_fwp"));
/*  72 */     setValueDate(message.getDate("value_date"));
/*  73 */     setBidForwardPoints2(message.getBigDecimal("bid_fwp2"));
/*  74 */     setAskForwardPoints2(message.getBigDecimal("ask_fwp2"));
/*  75 */     setValueDate2(message.getDate("value_date2"));
/*  76 */     setValidTillDate(message.getDate("value_date"));
/*  77 */     setNotes(message.getString("notes"));
/*     */   }
/*     */ 
/*     */   public void setQuoteRequestId(String quoteRequestId) {
/*  81 */     put("qreq_id", quoteRequestId);
/*     */   }
/*     */ 
/*     */   public String getQuoteRequestId() {
/*  85 */     return getString("qreq_id");
/*     */   }
/*     */ 
/*     */   public void setQuoteId(String quoteId) {
/*  89 */     put("quote_id", quoteId);
/*     */   }
/*     */ 
/*     */   public String getQuoteId() {
/*  93 */     return getString("quote_id");
/*     */   }
/*     */ 
/*     */   public void setDealType(DealType dealType) {
/*  97 */     if (dealType != null)
/*  98 */       put("d_type", dealType.toString());
/*     */   }
/*     */ 
/*     */   public DealType getDealType()
/*     */   {
/* 103 */     String type = getString("d_type");
/* 104 */     if (type != null) {
/* 105 */       return DealType.valueOf(type);
/*     */     }
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 111 */     put("inst", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 115 */     return getString("inst");
/*     */   }
/*     */ 
/*     */   public void setCurrency(String currency) {
/* 119 */     put("curr", currency);
/*     */   }
/*     */ 
/*     */   public String getCurrency() {
/* 123 */     return getString("curr");
/*     */   }
/*     */ 
/*     */   public void setValueDate(Date valueDate) {
/* 127 */     putDate("value_date", valueDate);
/*     */   }
/*     */ 
/*     */   public Date getValueDate() {
/* 131 */     return getDate("value_date");
/*     */   }
/*     */ 
/*     */   public void setSpotBid(BigDecimal bid) {
/* 135 */     put("bid", bid);
/*     */   }
/*     */ 
/*     */   public BigDecimal getSpotBid() {
/* 139 */     return getBigDecimal("bid");
/*     */   }
/*     */ 
/*     */   public void setSpotAsk(BigDecimal ask) {
/* 143 */     put("ask", ask);
/*     */   }
/*     */ 
/*     */   public BigDecimal getSpotAsk() {
/* 147 */     return getBigDecimal("ask");
/*     */   }
/*     */ 
/*     */   public void setBidSize(BigDecimal bidSize) {
/* 151 */     put("bid_size", bidSize);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBidSize() {
/* 155 */     return getBigDecimal("bid_size");
/*     */   }
/*     */ 
/*     */   public void setAskSize(BigDecimal askSize) {
/* 159 */     put("ask_size", askSize);
/*     */   }
/*     */ 
/*     */   public BigDecimal getAskSize() {
/* 163 */     return getBigDecimal("ask_size");
/*     */   }
/*     */ 
/*     */   public void setBidForwardPoints(BigDecimal bidForwardPoints) {
/* 167 */     put("bid_fwp", bidForwardPoints);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBidForwardPoints() {
/* 171 */     return getBigDecimal("bid_fwp");
/*     */   }
/*     */ 
/*     */   public void setAskForwardPoints(BigDecimal askForwardPoints) {
/* 175 */     put("ask_fwp", askForwardPoints);
/*     */   }
/*     */ 
/*     */   public BigDecimal getAskForwardPoints() {
/* 179 */     return getBigDecimal("ask_fwp");
/*     */   }
/*     */ 
/*     */   public void setValueDate2(Date valueDate2) {
/* 183 */     putDate("value_date2", valueDate2);
/*     */   }
/*     */ 
/*     */   public Date getValueDate2() {
/* 187 */     return getDate("value_date2");
/*     */   }
/*     */ 
/*     */   public void setValidTillDate(Date validTillDate) {
/* 191 */     putDate("valid_till", validTillDate);
/*     */   }
/*     */ 
/*     */   public Date getValidTillDate() {
/* 195 */     return getDate("valid_till");
/*     */   }
/*     */ 
/*     */   public void setBidForwardPoints2(BigDecimal bidForwardPoints2) {
/* 199 */     put("bid_fwp2", bidForwardPoints2);
/*     */   }
/*     */ 
/*     */   public BigDecimal getBidForwardPoints2() {
/* 203 */     return getBigDecimal("bid_fwp2");
/*     */   }
/*     */ 
/*     */   public void setAskForwardPoints2(BigDecimal askForwardPoints2) {
/* 207 */     put("ask_fwp2", askForwardPoints2);
/*     */   }
/*     */ 
/*     */   public BigDecimal getAskForwardPoints2() {
/* 211 */     return getBigDecimal("ask_fwp2");
/*     */   }
/*     */ 
/*     */   public void setNotes(String notes) {
/* 215 */     put("notes", notes);
/*     */   }
/*     */ 
/*     */   public String getNotes() {
/* 219 */     return getString("notes");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.quote.QuoteMessage
 * JD-Core Version:    0.6.0
 */