/*     */ package com.dukascopy.transport.common.msg.candle;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class CandleMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "candle";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String BID_FLAG = "bidFlag";
/*     */   public static final String HIGH_PRICE = "highPrice";
/*     */   public static final String LOW_PRICE = "lowPrice";
/*     */   public static final String OPEN_PRICE = "openPrice";
/*     */   public static final String CLOSE_PRICE = "closePrice";
/*     */   public static final String VOLUME = "volume";
/*     */   public static final String PERIOD = "period";
/*     */   public static final String TIME = "time";
/*     */ 
/*     */   public CandleMessage()
/*     */   {
/*  41 */     setType("candle");
/*     */   }
/*     */ 
/*     */   public CandleMessage(ProtocolMessage message)
/*     */   {
/*  51 */     super(message);
/*     */ 
/*  53 */     setType("candle");
/*     */ 
/*  55 */     setInstrument(message.getString("instrument"));
/*  56 */     setBidFlag(Boolean.valueOf(message.getBoolean("bidFlag")));
/*  57 */     setHighPrice(message.getBigDecimal("highPrice"));
/*  58 */     setLowPrice(message.getBigDecimal("lowPrice"));
/*  59 */     setOpenPrice(message.getBigDecimal("openPrice"));
/*  60 */     setClosePrice(message.getBigDecimal("closePrice"));
/*  61 */     setVolume(message.getBigDecimal("volume"));
/*  62 */     setPeriod(message.getLong("period"));
/*  63 */     setTime(message.getLong("time"));
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  67 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  71 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setHighPrice(BigDecimal highPrice) {
/*  75 */     put("highPrice", highPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getHighPrice() {
/*  79 */     return getBigDecimal("highPrice");
/*     */   }
/*     */ 
/*     */   public void setLowPrice(BigDecimal lowPrice) {
/*  83 */     put("lowPrice", lowPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getLowPrice() {
/*  87 */     return getBigDecimal("lowPrice");
/*     */   }
/*     */ 
/*     */   public void setOpenPrice(BigDecimal openPrice) {
/*  91 */     put("openPrice", openPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getOpenPrice() {
/*  95 */     return getBigDecimal("openPrice");
/*     */   }
/*     */ 
/*     */   public void setClosePrice(BigDecimal closePrice) {
/*  99 */     put("closePrice", closePrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getClosePrice() {
/* 103 */     return getBigDecimal("closePrice");
/*     */   }
/*     */ 
/*     */   public void setVolume(BigDecimal volume) {
/* 107 */     put("volume", volume);
/*     */   }
/*     */ 
/*     */   public BigDecimal getVolume() {
/* 111 */     return getBigDecimal("volume");
/*     */   }
/*     */ 
/*     */   public void setBidFlag(Boolean bidFlag) {
/* 115 */     put("bidFlag", bidFlag);
/*     */   }
/*     */ 
/*     */   public Boolean getBidFlag() {
/* 119 */     return Boolean.valueOf(getBoolean("bidFlag"));
/*     */   }
/*     */ 
/*     */   public Long getPeriod() {
/* 123 */     return getLong("period");
/*     */   }
/*     */ 
/*     */   public void setPeriod(Long period) {
/* 127 */     put("period", period);
/*     */   }
/*     */ 
/*     */   public Long getTime() {
/* 131 */     return getLong("time");
/*     */   }
/*     */ 
/*     */   public void setTime(Long time) {
/* 135 */     if (null != time)
/* 136 */       put("time", time.toString());
/*     */     else
/* 138 */       put("time", null);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.candle.CandleMessage
 * JD-Core Version:    0.6.0
 */