/*     */ package com.dukascopy.transport.common.msg.blp;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class BloombergSwapSubscribeRequestMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "blp_swap_subsc";
/*     */   public static final String SECURITY = "s";
/*     */   public static final String INSTRUMENT = "i";
/*     */   public static final String PERIOD = "p";
/*     */   public static final String REQUEST_ID = "id";
/*     */ 
/*     */   public BloombergSwapSubscribeRequestMessage()
/*     */   {
/*  20 */     setType("blp_swap_subsc");
/*     */   }
/*     */ 
/*     */   public BloombergSwapSubscribeRequestMessage(ProtocolMessage message) {
/*  24 */     super(message);
/*  25 */     setType("blp_swap_subsc");
/*  26 */     setInstrument(message.getString("i"));
/*  27 */     setSecurity(message.getString("s"));
/*  28 */     setPeriod(message.getLong("p"));
/*  29 */     setRequestId(message.getInteger("id"));
/*     */   }
/*     */ 
/*     */   public Long getPeriod() {
/*  33 */     return getLong("p");
/*     */   }
/*     */ 
/*     */   public void setPeriod() {
/*  37 */     setPeriod(null);
/*     */   }
/*     */   public void setPeriod(Long period) {
/*  40 */     if (period == null) {
/*  41 */       period = new Long("1");
/*     */     }
/*  43 */     put("p", period.toString());
/*     */   }
/*     */ 
/*     */   public void setSecurity(String security) {
/*  47 */     put("s", security);
/*  48 */     if ((security != null) && (!security.isEmpty()) && (security.contains("Curncy")))
/*  49 */       setInstrument(security);
/*     */   }
/*     */ 
/*     */   public String getSecurity()
/*     */   {
/*  54 */     return getString("s");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  58 */     put("i", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  62 */     return getString("i");
/*     */   }
/*     */   public void setRequestId(Integer count) {
/*  65 */     put("id", count);
/*     */   }
/*     */ 
/*     */   public Integer getRequestId() {
/*  69 */     return getInteger("id");
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments)
/*     */   {
/*  77 */     StringBuffer sb = new StringBuffer();
/*     */ 
/*  79 */     for (String instrument : instruments) {
/*  80 */       sb.append(instrument).append(",");
/*     */     }
/*  82 */     if (sb.length() != 0)
/*     */     {
/*  84 */       sb.setLength(sb.length() - 1);
/*     */     }
/*  86 */     put("i", sb.toString().trim());
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments()
/*     */   {
/*  95 */     String instruments = getString("i");
/*  96 */     if (instruments == null) {
/*  97 */       return null;
/*     */     }
/*     */ 
/* 100 */     List instrumentsList = new ArrayList();
/*     */ 
/* 102 */     StringTokenizer st = new StringTokenizer(instruments, ",");
/* 103 */     while (st.hasMoreTokens()) {
/* 104 */       instrumentsList.add(st.nextToken());
/*     */     }
/*     */ 
/* 107 */     return instrumentsList;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BloombergSwapSubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */