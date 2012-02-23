/*     */ package com.dukascopy.transport.common.msg.monitor;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class FeedHistoryRequest extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "feedHistReq";
/*     */   private static final String FORMAT = "format";
/*     */   private static final String MD_TYPE = "md_type";
/*     */   private static final String INSTRUMENT = "instr";
/*     */   private static final String SIDE = "side";
/*     */   private static final String STEP = "step";
/*     */   private static final String FROM_DATE = "from";
/*     */   private static final String TO_DATE = "to";
/*     */   private static final String SOURCES = "src";
/*     */ 
/*     */   public FeedHistoryRequest()
/*     */   {
/*  37 */     setType("feedHistReq");
/*     */   }
/*     */ 
/*     */   public FeedHistoryRequest(ProtocolMessage message)
/*     */   {
/*  42 */     setType("feedHistReq");
/*  43 */     put("format", message.getString("format"));
/*  44 */     put("md_type", message.getString("md_type"));
/*  45 */     put("instr", message.getString("instr"));
/*  46 */     put("side", message.getString("side"));
/*  47 */     put("step", message.getString("step"));
/*  48 */     put("from", message.getString("from"));
/*  49 */     put("to", message.getString("to"));
/*  50 */     put("src", message.getString("src"));
/*     */   }
/*     */ 
/*     */   public Format getFormat() {
/*  54 */     String format = getString("format");
/*  55 */     if (format != null) {
/*  56 */       return Format.valueOf(format);
/*     */     }
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFormat(Format format) {
/*  62 */     if (format != null)
/*  63 */       put("format", format.toString());
/*     */   }
/*     */ 
/*     */   public void setMarketDataType(String mdType)
/*     */   {
/*  68 */     put("md_type", mdType);
/*     */   }
/*     */ 
/*     */   public String getMarketDataType() {
/*  72 */     return getString("md_type");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  76 */     put("instr", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  80 */     return getString("instr");
/*     */   }
/*     */ 
/*     */   public void setSide(String side) {
/*  84 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public String getSide() {
/*  88 */     return getString("side");
/*     */   }
/*     */ 
/*     */   public void setStep(BigDecimal step) {
/*  92 */     put("step", step);
/*     */   }
/*     */ 
/*     */   public BigDecimal getStep() {
/*  96 */     return getBigDecimal("step");
/*     */   }
/*     */ 
/*     */   public Date getFromDate() {
/* 100 */     return getDate("from");
/*     */   }
/*     */ 
/*     */   public void setFromDate(Date date) {
/* 104 */     putDate("from", date);
/*     */   }
/*     */ 
/*     */   public Date getToDate() {
/* 108 */     return getDate("to");
/*     */   }
/*     */ 
/*     */   public void setToDate(Date date) {
/* 112 */     putDate("to", date);
/*     */   }
/*     */ 
/*     */   public void setSources(List<String> instruments) {
/* 116 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 118 */     for (String instrument : instruments) {
/* 119 */       sb.append(instrument).append(",");
/*     */     }
/* 121 */     if (sb.length() != 0)
/*     */     {
/* 123 */       sb.setLength(sb.length() - 1);
/*     */     }
/* 125 */     put("src", sb.toString().trim());
/*     */   }
/*     */ 
/*     */   public List<String> getSources() {
/* 129 */     String instruments = getString("src");
/* 130 */     if (instruments == null) {
/* 131 */       return null;
/*     */     }
/*     */ 
/* 134 */     List instrumentsList = new ArrayList();
/*     */ 
/* 136 */     StringTokenizer st = new StringTokenizer(instruments, ", ");
/* 137 */     while (st.hasMoreTokens()) {
/* 138 */       instrumentsList.add(st.nextToken());
/*     */     }
/*     */ 
/* 141 */     return instrumentsList;
/*     */   }
/*     */ 
/*     */   public static enum Format
/*     */   {
/*  14 */     CSV, CHART;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.monitor.FeedHistoryRequest
 * JD-Core Version:    0.6.0
 */