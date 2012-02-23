/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.RequestMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class QuoteUnsubscribeRequestMessage extends RequestMessage
/*     */ {
/*     */   public static final String TYPE = "quote_unsubsc";
/*     */   public static final String EXECUTOR = "execId";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String QUOTES_ONLY = "bestFlag";
/*     */ 
/*     */   public QuoteUnsubscribeRequestMessage()
/*     */   {
/*  28 */     setType("quote_unsubsc");
/*     */   }
/*     */ 
/*     */   public QuoteUnsubscribeRequestMessage(ProtocolMessage message)
/*     */   {
/*  37 */     super(message);
/*     */ 
/*  39 */     setType("quote_unsubsc");
/*     */ 
/*  41 */     put("execId", message.getString("execId"));
/*  42 */     put("instrument", message.getString("instrument"));
/*  43 */     setQuotesOnly(message.getBool("bestFlag"));
/*     */   }
/*     */ 
/*     */   public void setExecutorId(String executorId) {
/*  47 */     put("execId", executorId);
/*     */   }
/*     */ 
/*     */   public String getExecutorId() {
/*  51 */     return getString("execId");
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setInstrument(String instrument)
/*     */   {
/*  62 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public String getInstrument()
/*     */   {
/*  72 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments)
/*     */   {
/*  81 */     StringBuffer sb = new StringBuffer();
/*     */ 
/*  83 */     for (String instrument : instruments) {
/*  84 */       sb.append(instrument).append(",");
/*     */     }
/*     */ 
/*  87 */     String s = sb.toString().trim();
/*  88 */     s = s.substring(0, s.length() - 1);
/*  89 */     put("instrument", s);
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments()
/*     */   {
/*  98 */     String instruments = getString("instrument");
/*  99 */     if (instruments == null) {
/* 100 */       return null;
/*     */     }
/*     */ 
/* 103 */     List instrumentsList = new ArrayList();
/*     */ 
/* 105 */     StringTokenizer st = new StringTokenizer(instruments, ", ");
/* 106 */     while (st.hasMoreTokens()) {
/* 107 */       instrumentsList.add(st.nextToken());
/*     */     }
/*     */ 
/* 110 */     return instrumentsList;
/*     */   }
/*     */ 
/*     */   public void setQuotesOnly(Boolean bestFlag)
/*     */   {
/* 119 */     put("bestFlag", bestFlag);
/*     */   }
/*     */ 
/*     */   public Boolean getQuotesOnly()
/*     */   {
/* 128 */     return getBool("bestFlag");
/*     */   }
/*     */ 
/*     */   public boolean isInstrumentPresents(String instrument)
/*     */   {
/* 138 */     boolean result = false;
/*     */ 
/* 140 */     List instruments = getInstruments();
/* 141 */     if (instruments != null) {
/* 142 */       for (String i : instruments) {
/* 143 */         if (instrument.equalsIgnoreCase(i)) {
/* 144 */           result = true;
/* 145 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 150 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.QuoteUnsubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */