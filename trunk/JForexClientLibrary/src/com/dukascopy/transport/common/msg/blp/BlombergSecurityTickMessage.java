/*     */ package com.dukascopy.transport.common.msg.blp;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class BlombergSecurityTickMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "bstm";
/*     */   public static final String ASK = "ask";
/*     */   public static final String BID = "bid";
/*     */   public static final String ASK_VOLUME = "ask_volume";
/*     */   public static final String BID_VOLUME = "bid_volume";
/*     */   public static final String SECURITY = "security";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String VOLUME = "volume";
/*     */   public static final String PERIOD = "period";
/*     */   public static final String TIME = "time";
/*     */ 
/*     */   public BlombergSecurityTickMessage()
/*     */   {
/*  21 */     setType("bstm");
/*  22 */     setSecurity("unknown");
/*  23 */     setInstrument("unknown");
/*  24 */     setVolume(new BigDecimal(0.0D));
/*  25 */     setTime(new Long(0L));
/*  26 */     setAsk(new BigDecimal(0.0D));
/*  27 */     setBid(new BigDecimal(0.0D));
/*  28 */     setAskVolume(new BigDecimal(0.0D));
/*  29 */     setBidVolume(new BigDecimal(0.0D));
/*     */   }
/*     */ 
/*     */   public BlombergSecurityTickMessage(ProtocolMessage message) {
/*  33 */     super(message);
/*  34 */     setType("bstm");
/*  35 */     setSecurity(message.getString("security"));
/*  36 */     setInstrument(message.getString("instrument"));
/*  37 */     setVolume(message.getBigDecimal("volume"));
/*  38 */     setTime(message.getLong("time"));
/*  39 */     setAsk(message.getBigDecimal("ask"));
/*  40 */     setBid(message.getBigDecimal("bid"));
/*  41 */     setAskVolume(message.getBigDecimal("ask_volume"));
/*  42 */     setBidVolume(message.getBigDecimal("bid_volume"));
/*     */   }
/*     */   public void setSecurity(String security) {
/*  45 */     put("security", security);
/*     */   }
/*     */   public String getSecurity() {
/*  48 */     return getString("security");
/*     */   }
/*     */   public void setInstrument(String instrument) {
/*  51 */     put("instrument", instrument);
/*     */   }
/*     */   public String getInstrument() {
/*  54 */     return getString("instrument");
/*     */   }
/*     */   public void setVolume(BigDecimal volume) {
/*  57 */     put("volume", volume);
/*     */   }
/*     */   public BigDecimal getVolume() {
/*  60 */     return getBigDecimal("volume");
/*     */   }
/*     */   public Long getPeriod() {
/*  63 */     return getLong("period");
/*     */   }
/*     */   public void setPeriod(Long period) {
/*  66 */     put("period", period);
/*     */   }
/*     */   public Long getTime() {
/*  69 */     return getLong("time");
/*     */   }
/*     */ 
/*     */   public void setTime(Long time) {
/*  73 */     if (null != time)
/*  74 */       put("time", time.toString());
/*     */     else
/*  76 */       put("time", null);
/*     */   }
/*     */ 
/*     */   public void setAsk(BigDecimal ask) {
/*  80 */     put("ask", ask);
/*     */   }
/*     */   public BigDecimal getAsk() {
/*  83 */     return getBigDecimal("ask");
/*     */   }
/*     */   public void setBid(BigDecimal bid) {
/*  86 */     put("bid", bid);
/*     */   }
/*     */   public BigDecimal getBid() {
/*  89 */     return getBigDecimal("bid");
/*     */   }
/*     */   public void setAskVolume(BigDecimal ask_volume) {
/*  92 */     put("ask_volume", ask_volume);
/*     */   }
/*     */   public BigDecimal getAskVolume() {
/*  95 */     return getBigDecimal("ask_volume");
/*     */   }
/*     */   public void setBidVolume(BigDecimal bid_volume) {
/*  98 */     put("bid_volume", bid_volume);
/*     */   }
/*     */   public BigDecimal getBidVolume() {
/* 101 */     return getBigDecimal("bid_volume");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.blp.BlombergSecurityTickMessage
 * JD-Core Version:    0.6.0
 */