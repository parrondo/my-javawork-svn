/*     */ package com.dukascopy.transport.common.msg.datafeed;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ 
/*     */ public class DFCandleMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "df_candle";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String IS_BID = "isBid";
/*     */   public static final String HIGH_PRICE = "highPrice";
/*     */   public static final String LOW_PRICE = "lowPrice";
/*     */   public static final String OPEN_PRICE = "openPrice";
/*     */   public static final String CLOSE_PRICE = "closePrice";
/*     */   public static final String VOLUME = "volume";
/*     */   public static final String PERIOD = "period";
/*     */   public static final String TIME = "time";
/*     */ 
/*     */   public DFCandleMessage()
/*     */   {
/*  38 */     setType("df_candle");
/*     */   }
/*     */ 
/*     */   public DFCandleMessage(ProtocolMessage message)
/*     */   {
/*  48 */     super(message);
/*     */ 
/*  50 */     setType("df_candle");
/*     */ 
/*  52 */     setInstrument(message.getString("instrument"));
/*  53 */     setBidFlag(Boolean.valueOf(message.getBoolean("isBid")));
/*  54 */     setHighPrice(message.getString("highPrice"));
/*  55 */     setLowPrice(message.getString("lowPrice"));
/*  56 */     setOpenPrice(message.getString("openPrice"));
/*  57 */     setClosePrice(message.getString("closePrice"));
/*  58 */     setVolume(message.getString("volume"));
/*  59 */     setPeriod(message.getString("period"));
/*  60 */     setTime(message.getString("time"));
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  64 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  68 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setHighPrice(String highPrice) {
/*  72 */     put("highPrice", highPrice);
/*     */   }
/*     */ 
/*     */   public String getHighPrice() {
/*  76 */     return getString("highPrice");
/*     */   }
/*     */ 
/*     */   public void setLowPrice(String lowPrice) {
/*  80 */     put("lowPrice", lowPrice);
/*     */   }
/*     */ 
/*     */   public String getLowPrice() {
/*  84 */     return getString("lowPrice");
/*     */   }
/*     */ 
/*     */   public void setOpenPrice(String openPrice) {
/*  88 */     put("openPrice", openPrice);
/*     */   }
/*     */ 
/*     */   public String getOpenPrice() {
/*  92 */     return getString("openPrice");
/*     */   }
/*     */ 
/*     */   public void setClosePrice(String closePrice) {
/*  96 */     put("closePrice", closePrice);
/*     */   }
/*     */ 
/*     */   public String getClosePrice() {
/* 100 */     return getString("closePrice");
/*     */   }
/*     */ 
/*     */   public void setVolume(String volume) {
/* 104 */     put("volume", volume);
/*     */   }
/*     */ 
/*     */   public String getVolume() {
/* 108 */     return getString("volume");
/*     */   }
/*     */ 
/*     */   public void setBidFlag(Boolean bidFlag) {
/* 112 */     put("isBid", bidFlag);
/*     */   }
/*     */ 
/*     */   public Boolean getBidFlag() {
/* 116 */     return Boolean.valueOf(getBoolean("isBid"));
/*     */   }
/*     */ 
/*     */   public String getPeriod() {
/* 120 */     return getString("period");
/*     */   }
/*     */ 
/*     */   public void setPeriod(String period) {
/* 124 */     put("period", period);
/*     */   }
/*     */ 
/*     */   public String getTime() {
/* 128 */     return getString("time");
/*     */   }
/*     */ 
/*     */   public void setTime(String time) {
/* 132 */     put("time", time);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFCandleMessage
 * JD-Core Version:    0.6.0
 */