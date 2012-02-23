/*    */ package com.dukascopy.transport.common.msg.esignal;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class ESignalTickSubscribeRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "esignal_tick_subsc";
/*    */   public static final String SYMBOL = "symbol";
/*    */   public static final String INSTRUMENT = "instrument";
/*    */   public static final String PERIOD = "period";
/*    */   public static final String ESIGNAL_TYPE = "esignal_type";
/*    */   public static final String START_TIMESTAMP = "start_timestamp";
/*    */   public static final String END_TIMESTAMP = "end_timestamp";
/*    */   public static final String REQUEST_ID = "id";
/*    */ 
/*    */   public ESignalTickSubscribeRequestMessage()
/*    */   {
/* 18 */     setType("esignal_tick_subsc");
/* 19 */     setStartTimestamp(Long.valueOf(System.currentTimeMillis()));
/* 20 */     setEndTimestamp(Long.valueOf(System.currentTimeMillis()));
/*    */   }
/*    */ 
/*    */   public ESignalTickSubscribeRequestMessage(ProtocolMessage message) {
/* 24 */     super(message);
/* 25 */     setType("esignal_tick_subsc");
/* 26 */     setInstrument(message.getString("instrument"));
/* 27 */     setSymbol(message.getString("symbol"));
/* 28 */     setStartTimestamp(message.getLong("start_timestamp"));
/* 29 */     setEndTimestamp(message.getLong("end_timestamp"));
/* 30 */     setPeriod(message.getLong("period"));
/* 31 */     setRequestId(message.getInteger("id"));
/*    */   }
/*    */ 
/*    */   public void setStartTimestamp(Long startTimestamp) {
/* 35 */     if (null != startTimestamp)
/* 36 */       put("start_timestamp", startTimestamp.toString());
/*    */     else
/* 38 */       put("start_timestamp", null);
/*    */   }
/*    */ 
/*    */   public Long getStartTimestamp()
/*    */   {
/* 43 */     return getLong("start_timestamp");
/*    */   }
/*    */ 
/*    */   public void setEndTimestamp(Long endTimestamp) {
/* 47 */     if (null != endTimestamp)
/* 48 */       put("end_timestamp", endTimestamp.toString());
/*    */     else
/* 50 */       put("end_timestamp", null);
/*    */   }
/*    */ 
/*    */   public Long getEndTimestamp()
/*    */   {
/* 55 */     return getLong("end_timestamp");
/*    */   }
/*    */ 
/*    */   public Long getPeriod() {
/* 59 */     return getLong("period");
/*    */   }
/*    */ 
/*    */   public void setPeriod(Long period) {
/* 63 */     if (period == null) {
/* 64 */       period = new Long("1");
/*    */     }
/* 66 */     put("period", period.toString());
/*    */   }
/*    */ 
/*    */   public void setSymbol(String symbol) {
/* 70 */     put("symbol", symbol);
/* 71 */     if ((symbol != null) && (!symbol.isEmpty()) && (symbol.contains("Curncy")))
/* 72 */       setInstrument(symbol);
/*    */   }
/*    */ 
/*    */   public String getSymbol()
/*    */   {
/* 77 */     return getString("symbol");
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 81 */     put("instrument", instrument);
/*    */   }
/*    */ 
/*    */   public String getInstrument() {
/* 85 */     return getString("instrument");
/*    */   }
/*    */   public void setRequestId(Integer count) {
/* 88 */     put("id", count);
/*    */   }
/*    */ 
/*    */   public Integer getRequestId() {
/* 92 */     return getInteger("id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.esignal.ESignalTickSubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */