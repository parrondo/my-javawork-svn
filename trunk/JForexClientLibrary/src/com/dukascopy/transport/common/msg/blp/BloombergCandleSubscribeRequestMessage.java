/*     */ package com.dukascopy.transport.common.msg.blp;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class BloombergCandleSubscribeRequestMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "blp_candle_subsc";
/*     */   public static final String INSTRUMENT = "i";
/*     */   public static final String PERIOD = "p";
/*     */   public static final String BLOOMBERG_TYPE = "bt";
/*     */   public static final String START_TIMESTAMP = "st";
/*     */   public static final String END_TIMESTAMP = "et";
/*     */   public static final String REQUEST_ID = "id";
/*     */ 
/*     */   public BloombergCandleSubscribeRequestMessage()
/*     */   {
/*  21 */     setType("blp_candle_subsc");
/*  22 */     setStartTimestamp(Long.valueOf(System.currentTimeMillis()));
/*  23 */     setEndTimestamp(Long.valueOf(System.currentTimeMillis()));
/*     */   }
/*     */ 
/*     */   public BloombergCandleSubscribeRequestMessage(ProtocolMessage message) {
/*  27 */     super(message);
/*  28 */     setType("blp_candle_subsc");
/*  29 */     setInstrument(message.getString("i"));
/*  30 */     setStartTimestamp(message.getLong("st"));
/*  31 */     setEndTimestamp(message.getLong("et"));
/*  32 */     setPeriod(message.getLong("p"));
/*  33 */     setRequestId(message.getInteger("id"));
/*     */   }
/*     */ 
/*     */   public void setStartTimestamp(Long startTimestamp) {
/*  37 */     if (null != startTimestamp)
/*  38 */       put("st", startTimestamp.toString());
/*     */     else
/*  40 */       put("st", null);
/*     */   }
/*     */ 
/*     */   public Long getStartTimestamp()
/*     */   {
/*  45 */     return getLong("st");
/*     */   }
/*     */ 
/*     */   public void setEndTimestamp(Long endTimestamp) {
/*  49 */     if (null != endTimestamp)
/*  50 */       put("et", endTimestamp.toString());
/*     */     else
/*  52 */       put("et", null);
/*     */   }
/*     */ 
/*     */   public Long getEndTimestamp()
/*     */   {
/*  57 */     return getLong("et");
/*     */   }
/*     */ 
/*     */   public Long getPeriod() {
/*  61 */     return getLong("p");
/*     */   }
/*     */ 
/*     */   public void setPeriod(Long period) {
/*  65 */     if (period == null) {
/*  66 */       period = new Long("1");
/*     */     }
/*  68 */     put("p", period.toString());
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  72 */     put("i", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  76 */     return getString("i");
/*     */   }
/*     */   public void setRequestId(Integer count) {
/*  79 */     put("id", count);
/*     */   }
/*     */ 
/*     */   public Integer getRequestId() {
/*  83 */     return getInteger("id");
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments)
/*     */   {
/*  92 */     StringBuffer sb = new StringBuffer();
/*     */ 
/*  94 */     for (String instrument : instruments) {
/*  95 */       sb.append(instrument).append(",");
/*     */     }
/*  97 */     if (sb.length() != 0)
/*     */     {
/*  99 */       sb.setLength(sb.length() - 1);
/*     */     }
/* 101 */     put("i", sb.toString().trim());
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments()
/*     */   {
/* 110 */     String instruments = getString("i");
/* 111 */     if (instruments == null) {
/* 112 */       return null;
/*     */     }
/*     */ 
/* 115 */     List instrumentsList = new ArrayList();
/*     */ 
/* 117 */     StringTokenizer st = new StringTokenizer(instruments, ",");
/* 118 */     while (st.hasMoreTokens()) {
/* 119 */       instrumentsList.add(st.nextToken());
/*     */     }
/*     */ 
/* 122 */     return instrumentsList;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergCandleSubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */