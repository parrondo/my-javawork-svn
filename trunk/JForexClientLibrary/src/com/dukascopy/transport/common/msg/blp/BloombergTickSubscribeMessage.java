/*     */ package com.dukascopy.transport.common.msg.blp;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class BloombergTickSubscribeMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "blp_tick_subsc";
/*     */   public static final String REQUEST_ID = "id";
/*     */   public static final String SECURITY = "s";
/*     */   public static final String INSTRUMENT = "i";
/*     */   public static final String PERIOD = "p";
/*     */   public static final String START_TIMESTAMP = "st";
/*     */   public static final String END_TIMESTAMP = "et";
/*     */ 
/*     */   public BloombergTickSubscribeMessage()
/*     */   {
/*  20 */     setType("blp_tick_subsc");
/*     */   }
/*     */ 
/*     */   public BloombergTickSubscribeMessage(ProtocolMessage message) {
/*  24 */     super(message);
/*  25 */     setType("blp_tick_subsc");
/*  26 */     setInstrument(message.getString("i"));
/*  27 */     setSecurity(message.getString("s"));
/*  28 */     setPeriod(message.getLong("p"));
/*  29 */     setRequestId(message.getInteger("id"));
/*  30 */     setStartTimestamp(message.getLong("st"));
/*  31 */     setEndTimestamp(message.getLong("et"));
/*     */   }
/*     */ 
/*     */   public void setStartTimestamp(Long startTimestamp) {
/*  35 */     if (null != startTimestamp)
/*  36 */       put("st", startTimestamp.toString());
/*     */     else
/*  38 */       put("st", null);
/*     */   }
/*     */ 
/*     */   public Long getStartTimestamp()
/*     */   {
/*  43 */     return getLong("st");
/*     */   }
/*     */ 
/*     */   public void setEndTimestamp(Long endTimestamp) {
/*  47 */     if (null != endTimestamp)
/*  48 */       put("et", endTimestamp.toString());
/*     */     else
/*  50 */       put("et", null);
/*     */   }
/*     */ 
/*     */   public Long getEndTimestamp()
/*     */   {
/*  55 */     return getLong("et");
/*     */   }
/*     */ 
/*     */   public Long getPeriod() {
/*  59 */     return getLong("p");
/*     */   }
/*     */ 
/*     */   public void setPeriod() {
/*  63 */     setPeriod(null);
/*     */   }
/*     */   public void setPeriod(Long period) {
/*  66 */     if (period == null) {
/*  67 */       period = new Long("1");
/*     */     }
/*  69 */     put("p", period.toString());
/*     */   }
/*     */ 
/*     */   public void setSecurity(String security) {
/*  73 */     put("s", security);
/*  74 */     if ((security != null) && (!security.isEmpty()) && (security.contains("Curncy")))
/*  75 */       setInstrument(security);
/*     */   }
/*     */ 
/*     */   public String getSecurity()
/*     */   {
/*  80 */     return getString("s");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  84 */     put("i", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  88 */     return getString("i");
/*     */   }
/*     */   public void setRequestId(Integer count) {
/*  91 */     put("id", count);
/*     */   }
/*     */ 
/*     */   public Integer getRequestId() {
/*  95 */     return getInteger("id");
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments)
/*     */   {
/* 103 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 105 */     for (String instrument : instruments) {
/* 106 */       sb.append(instrument).append(",");
/*     */     }
/* 108 */     if (sb.length() != 0)
/*     */     {
/* 110 */       sb.setLength(sb.length() - 1);
/*     */     }
/* 112 */     put("i", sb.toString().trim());
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments()
/*     */   {
/* 121 */     String instruments = getString("i");
/* 122 */     if (instruments == null) {
/* 123 */       return null;
/*     */     }
/*     */ 
/* 126 */     List instrumentsList = new ArrayList();
/*     */ 
/* 128 */     StringTokenizer st = new StringTokenizer(instruments, ",");
/* 129 */     while (st.hasMoreTokens()) {
/* 130 */       instrumentsList.add(st.nextToken());
/*     */     }
/*     */ 
/* 133 */     return instrumentsList;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergTickSubscribeMessage
 * JD-Core Version:    0.6.0
 */