/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class CandleSubscribeRequestMessage extends AbstractDFSMessage
/*    */ {
/*    */   public static final String TYPE = "candle_subsc";
/*    */   public static final String INSTRUMENT = "instrument";
/*    */   public static final String START_TIME = "from";
/*    */   public static final String END_TIME = "to";
/*    */   public static final String PERIOD = "period";
/*    */   public static final String LAST_CANDLE_REQ = "lcr";
/*    */   public static final String VOLUMES_DOUBLE = "vd";
/*    */ 
/*    */   public CandleSubscribeRequestMessage()
/*    */   {
/* 17 */     setType("candle_subsc");
/*    */   }
/*    */ 
/*    */   public CandleSubscribeRequestMessage(ProtocolMessage message) {
/* 21 */     super(message);
/*    */ 
/* 23 */     setType("candle_subsc");
/* 24 */     put("instrument", message.getString("instrument"));
/* 25 */     put("from", message.getString("from"));
/* 26 */     put("to", message.getString("to"));
/* 27 */     put("period", message.getString("period"));
/* 28 */     put("lcr", message.getString("lcr"));
/* 29 */     put("vd", message.getString("vd"));
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 33 */     put("instrument", instrument);
/*    */   }
/*    */ 
/*    */   public String getInstrument() {
/* 37 */     return getString("instrument");
/*    */   }
/*    */ 
/*    */   public void setStartTime(Long start) {
/* 41 */     put("from", start.toString());
/*    */   }
/*    */ 
/*    */   public Long getStartTime() {
/* 45 */     return getLong("from");
/*    */   }
/*    */ 
/*    */   public void setEndTime(Long start) {
/* 49 */     put("to", start.toString());
/*    */   }
/*    */ 
/*    */   public Long getEndTime() {
/* 53 */     return getLong("to");
/*    */   }
/*    */ 
/*    */   public void setPeriod(Long step) {
/* 57 */     put("period", step.toString());
/*    */   }
/*    */ 
/*    */   public Long getPeriod() {
/* 61 */     return getLong("period");
/*    */   }
/*    */ 
/*    */   public boolean isLastCandleRequest() {
/* 65 */     return getBoolean("lcr");
/*    */   }
/*    */ 
/*    */   public void setLastCandleRequest(boolean value) {
/* 69 */     put("lcr", value);
/*    */   }
/*    */ 
/*    */   public boolean isVolumesInDouble() {
/* 73 */     return getBoolean("vd");
/*    */   }
/*    */ 
/*    */   public void setVolumesInDouble(boolean value) {
/* 77 */     put("vd", value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.CandleSubscribeRequestMessage
 * JD-Core Version:    0.6.0
 */