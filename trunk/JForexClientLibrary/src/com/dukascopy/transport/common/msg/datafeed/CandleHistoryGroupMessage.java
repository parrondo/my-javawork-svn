/*     */ package com.dukascopy.transport.common.msg.datafeed;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class CandleHistoryGroupMessage extends AbstractDFSMessage
/*     */ {
/*     */   public static final String TYPE = "ch";
/*     */   public static final String PERIOD = "p";
/*     */   public static final String CANDLES = "c";
/*     */   public static final String HISTORY_FINISHED = "hf";
/*     */   public static final String LAST_CANDLE_PERIOD = "lcp";
/*     */   public static final String GAPS = "gaps";
/*     */   public static final String MSG_ORDER = "ord";
/*     */ 
/*     */   public CandleHistoryGroupMessage()
/*     */   {
/*  35 */     setType("ch");
/*     */   }
/*     */ 
/*     */   public CandleHistoryGroupMessage(ProtocolMessage message)
/*     */   {
/*  45 */     super(message);
/*     */ 
/*  47 */     setType("ch");
/*  48 */     setCandles(message.getString("c"));
/*  49 */     setPeriod(message.getLong("p"));
/*  50 */     setHistoryFinished(Boolean.valueOf(message.getBoolean("hf")));
/*  51 */     setGaps(message.getJSONArray("gaps"));
/*  52 */     setMessageOrder(message.getInteger("ord"));
/*  53 */     setLastCandlePeriod(message.getLong("lcp"));
/*     */   }
/*     */ 
/*     */   public void setPeriod(Long step) {
/*  57 */     put("p", step);
/*     */   }
/*     */ 
/*     */   public Long getPeriod() {
/*  61 */     return getLong("p");
/*     */   }
/*     */ 
/*     */   public void setCandles(String candles) {
/*  65 */     put("c", candles);
/*     */   }
/*     */ 
/*     */   public String getCandles() {
/*  69 */     return getString("c");
/*     */   }
/*     */ 
/*     */   public Boolean isHistoryFinished() {
/*  73 */     return Boolean.valueOf(getBoolean("hf"));
/*     */   }
/*     */ 
/*     */   public void setHistoryFinished(Boolean finished) {
/*  77 */     put("hf", finished);
/*     */   }
/*     */ 
/*     */   public Integer getMessageOrder() {
/*  81 */     return getInteger("ord");
/*     */   }
/*     */ 
/*     */   public void setMessageOrder(Integer msgOrder) {
/*  85 */     put("ord", msgOrder);
/*     */   }
/*     */ 
/*     */   public void setGaps(JSONArray gaps) {
/*  89 */     put("gaps", gaps);
/*     */   }
/*     */ 
/*     */   public JSONArray getGaps() {
/*  93 */     return getJSONArray("gaps");
/*     */   }
/*     */ 
/*     */   public void setLastCandlePeriod(Long period) {
/*  97 */     put("lcp", period);
/*     */   }
/*     */ 
/*     */   public Long getLastCandlePeriod() {
/* 101 */     return getLong("lcp");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.CandleHistoryGroupMessage
 * JD-Core Version:    0.6.0
 */