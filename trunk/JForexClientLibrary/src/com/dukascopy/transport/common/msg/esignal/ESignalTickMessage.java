/*    */ package com.dukascopy.transport.common.msg.esignal;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class ESignalTickMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "esignal_tm";
/*    */   public static final String ASK = "a";
/*    */   public static final String BID = "b";
/*    */   public static final String ASK_VOLUME = "av";
/*    */   public static final String BID_VOLUME = "bv";
/*    */   public static final String INSTRUMENT = "i";
/*    */   public static final String TIME = "t";
/*    */ 
/*    */   public ESignalTickMessage()
/*    */   {
/* 18 */     setType("esignal_tm");
/* 19 */     setInstrument("unknown");
/* 20 */     setTime(new Long(0L));
/* 21 */     setAsk(new BigDecimal(0.0D));
/* 22 */     setBid(new BigDecimal(0.0D));
/* 23 */     setAskVolume(new BigDecimal(0.0D));
/* 24 */     setBidVolume(new BigDecimal(0.0D));
/*    */   }
/*    */ 
/*    */   public ESignalTickMessage(ProtocolMessage message) {
/* 28 */     super(message);
/* 29 */     setType("esignal_tm");
/* 30 */     setInstrument(message.getString("i"));
/* 31 */     setTime(message.getLong("t"));
/* 32 */     setAsk(message.getBigDecimal("a"));
/* 33 */     setBid(message.getBigDecimal("b"));
/* 34 */     setAskVolume(message.getBigDecimal("av"));
/* 35 */     setBidVolume(message.getBigDecimal("bv"));
/*    */   }
/*    */   public void setInstrument(String instrument) {
/* 38 */     put("i", instrument);
/*    */   }
/*    */   public String getInstrument() {
/* 41 */     return getString("i");
/*    */   }
/*    */   public Long getTime() {
/* 44 */     return getLong("t");
/*    */   }
/*    */ 
/*    */   public void setTime(Long time) {
/* 48 */     if (null != time)
/* 49 */       put("t", time.toString());
/*    */     else
/* 51 */       put("t", null);
/*    */   }
/*    */ 
/*    */   public void setAsk(BigDecimal ask) {
/* 55 */     put("a", ask);
/*    */   }
/*    */   public BigDecimal getAsk() {
/* 58 */     return getBigDecimal("a");
/*    */   }
/*    */   public void setBid(BigDecimal bid) {
/* 61 */     put("b", bid);
/*    */   }
/*    */   public BigDecimal getBid() {
/* 64 */     return getBigDecimal("b");
/*    */   }
/*    */   public void setAskVolume(BigDecimal ask_volume) {
/* 67 */     put("av", ask_volume);
/*    */   }
/*    */   public BigDecimal getAskVolume() {
/* 70 */     return getBigDecimal("av");
/*    */   }
/*    */   public void setBidVolume(BigDecimal bid_volume) {
/* 73 */     put("bv", bid_volume);
/*    */   }
/*    */   public BigDecimal getBidVolume() {
/* 76 */     return getBigDecimal("bv");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.esignal.ESignalTickMessage
 * JD-Core Version:    0.6.0
 */