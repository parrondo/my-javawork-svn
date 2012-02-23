/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.RequestMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class QuoteSubscribeRequestMessage extends RequestMessage
/*     */ {
/*     */   public static final String TYPE = "quote_subsc";
/*     */   public static final String EXECUTOR = "execs";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String QUOTES_ONLY = "bestFlag";
/*     */   public static final String COMPRESED_BUNCH_ONLY = "cbo";
/*     */ 
/*     */   public QuoteSubscribeRequestMessage()
/*     */   {
/*  30 */     setType("quote_subsc");
/*     */   }
/*     */ 
/*     */   public QuoteSubscribeRequestMessage(ProtocolMessage message)
/*     */   {
/*  39 */     super(message);
/*     */ 
/*  41 */     setType("quote_subsc");
/*     */ 
/*  43 */     put("execs", message.getString("execs"));
/*  44 */     put("instrument", message.getString("instrument"));
/*  45 */     setQuotesOnly(message.getBool("bestFlag"));
/*  46 */     setCompressedBunchOnly(message.getBool("cbo"));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setInstrument(String instrument)
/*     */   {
/*  57 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public String getInstrument()
/*     */   {
/*  67 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments)
/*     */   {
/*  76 */     StringBuffer sb = new StringBuffer();
/*     */ 
/*  78 */     for (String instrument : instruments) {
/*  79 */       sb.append(instrument).append(",");
/*     */     }
/*  81 */     if (sb.length() != 0)
/*     */     {
/*  83 */       sb.setLength(sb.length() - 1);
/*     */     }
/*  85 */     put("instrument", sb.toString().trim());
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments()
/*     */   {
/*  94 */     String instruments = getString("instrument");
/*  95 */     if (instruments == null) {
/*  96 */       return null;
/*     */     }
/*     */ 
/*  99 */     List instrumentsList = new ArrayList();
/*     */ 
/* 101 */     StringTokenizer st = new StringTokenizer(instruments, ",");
/* 102 */     while (st.hasMoreTokens()) {
/* 103 */       instrumentsList.add(st.nextToken());
/*     */     }
/*     */ 
/* 106 */     return instrumentsList;
/*     */   }
/*     */ 
/*     */   public void setExecutors(List<String> executors) {
/* 110 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 112 */     for (String executor : executors) {
/* 113 */       sb.append(executor).append(",");
/*     */     }
/* 115 */     if (sb.length() != 0)
/*     */     {
/* 117 */       sb.setLength(sb.length() - 1);
/*     */     }
/* 119 */     put("execs", sb.toString().trim());
/*     */   }
/*     */ 
/*     */   public List<String> getExecutors() {
/* 123 */     String executors = getString("execs");
/* 124 */     if (executors == null) {
/* 125 */       return null;
/*     */     }
/*     */ 
/* 128 */     List executorList = new ArrayList();
/*     */ 
/* 130 */     StringTokenizer st = new StringTokenizer(executors, ", ");
/* 131 */     while (st.hasMoreTokens()) {
/* 132 */       executorList.add(st.nextToken());
/*     */     }
/*     */ 
/* 135 */     return executorList;
/*     */   }
/*     */ 
/*     */   public void setQuotesOnly(Boolean bestFlag)
/*     */   {
/* 145 */     put("bestFlag", bestFlag);
/*     */   }
/*     */ 
/*     */   public Boolean getQuotesOnly()
/*     */   {
/* 154 */     return getBool("bestFlag");
/*     */   }
/*     */ 
/*     */   public void setCompressedBunchOnly(Boolean flag) {
/* 158 */     put("cbo", flag);
/*     */   }
/*     */ 
/*     */   public Boolean getCompressedBunchOnly()
/*     */   {
/* 167 */     return getBool("cbo");
/*     */   }
/*     */ 
/*     */   public boolean isInstrumentPresents(String instrument)
/*     */   {
/* 177 */     boolean result = false;
/*     */ 
/* 179 */     List instruments = getInstruments();
/* 180 */     if (instruments != null) {
/* 181 */       for (String i : instruments) {
/* 182 */         if (instrument.equalsIgnoreCase(i)) {
/* 183 */           result = true;
/* 184 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 189 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */