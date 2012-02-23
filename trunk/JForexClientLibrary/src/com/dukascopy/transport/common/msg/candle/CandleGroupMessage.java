/*     */ package com.dukascopy.transport.common.msg.candle;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class CandleGroupMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "candleGroup";
/*     */   public static final String CANDLE_GROUP_ART_TIME = "candleGroupArtTimestamp";
/*     */   public static final String CANDLE_GROUP_TIMESTAMP = "candleGroupTimestamp";
/*     */   public static final String CANDLES = "candles";
/*     */ 
/*     */   public CandleGroupMessage()
/*     */   {
/*  28 */     setType("candleGroup");
/*     */   }
/*     */ 
/*     */   public CandleGroupMessage(ProtocolMessage message)
/*     */   {
/*  37 */     super(message);
/*     */ 
/*  39 */     setType("candleGroup");
/*  40 */     setCandleGroupArtTime(message.getLong("candleGroupArtTimestamp"));
/*  41 */     setCandleGroupTimestamp(message.getLong("candleGroupTimestamp"));
/*  42 */     setCandles(message.getJSONArray("candles"));
/*     */   }
/*     */ 
/*     */   public void setCandleGroupArtTime(Long timestamp)
/*     */   {
/*  50 */     if (timestamp != null)
/*  51 */       put("candleGroupArtTimestamp", timestamp.toString());
/*     */   }
/*     */ 
/*     */   public Long getCandleGroupArtTime()
/*     */   {
/*  60 */     return getLong("candleGroupArtTimestamp");
/*     */   }
/*     */ 
/*     */   public void setCandleGroupTimestamp(Long timestamp)
/*     */   {
/*  68 */     if (timestamp != null)
/*  69 */       put("candleGroupTimestamp", timestamp.toString());
/*     */   }
/*     */ 
/*     */   public Long getCandleGroupTimestamp()
/*     */   {
/*  78 */     return getLong("candleGroupTimestamp");
/*     */   }
/*     */ 
/*     */   public void setCandles(JSONArray candles)
/*     */   {
/*  86 */     put("candles", candles);
/*     */   }
/*     */ 
/*     */   public JSONArray getCandles()
/*     */   {
/*  94 */     return getJSONArray("candles");
/*     */   }
/*     */ 
/*     */   public void setCandleList(List<CandleMessage> candles)
/*     */   {
/* 102 */     JSONArray a = new JSONArray();
/*     */ 
/* 104 */     for (CandleMessage candle : candles) {
/* 105 */       a.put(candle);
/*     */     }
/*     */ 
/* 108 */     put("candles", a);
/*     */   }
/*     */ 
/*     */   public List<CandleMessage> getCandleList()
/*     */   {
/* 116 */     JSONArray a = getJSONArray("candles");
/* 117 */     List set = new ArrayList(a.length());
/*     */ 
/* 119 */     for (int i = 0; i < a.length(); i++) {
/* 120 */       CandleMessage candle = (CandleMessage)ProtocolMessage.parse(a.getString(i));
/* 121 */       if (candle == null) {
/* 122 */         return null;
/*     */       }
/* 124 */       set.add(candle);
/*     */     }
/*     */ 
/* 127 */     return set;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 131 */     CandleGroupMessage cgm = new CandleGroupMessage();
/* 132 */     cgm.setCandleGroupTimestamp(Long.valueOf(System.currentTimeMillis()));
/*     */ 
/* 134 */     System.out.println("cgm=" + cgm);
/*     */ 
/* 136 */     CandleGroupMessage cgmParsed = (CandleGroupMessage)ProtocolMessage.parse(cgm.toProtocolString());
/*     */ 
/* 138 */     System.out.println("cgmParsed=" + cgmParsed);
/*     */ 
/* 140 */     System.out.println("timestamp=" + cgmParsed.getCandleGroupTimestamp());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.candle.CandleGroupMessage
 * JD-Core Version:    0.6.0
 */