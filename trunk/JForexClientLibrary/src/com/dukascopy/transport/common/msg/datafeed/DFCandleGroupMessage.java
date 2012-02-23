/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class DFCandleGroupMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "df_candle_group";
/*    */   public static final String CANDLE_GROUP_TIME = "candleGroupTime";
/*    */   public static final String CANDLE_GROUP_PROVIDER = "provider";
/*    */   public static final String CANDLES = "candles";
/*    */ 
/*    */   public DFCandleGroupMessage()
/*    */   {
/* 25 */     setType("df_candle_group");
/*    */   }
/*    */ 
/*    */   public DFCandleGroupMessage(ProtocolMessage message) {
/* 29 */     super(message);
/* 30 */     setType("df_candle_group");
/* 31 */     setCandleGroupTime(message.getString("candleGroupTime"));
/* 32 */     setCandles(message.getJSONArray("candles"));
/* 33 */     setCandleGroupProvider(message.getString("provider"));
/*    */   }
/*    */ 
/*    */   public void setCandleGroupTime(String timestamp) {
/* 37 */     put("candleGroupTime", timestamp);
/*    */   }
/*    */ 
/*    */   public String getCandleGroupTime() {
/* 41 */     return getString("candleGroupTime");
/*    */   }
/*    */ 
/*    */   public void setCandleGroupProvider(String provider) {
/* 45 */     put("provider", provider);
/*    */   }
/*    */ 
/*    */   public String getCandleGroupProvider() {
/* 49 */     return getString("provider");
/*    */   }
/*    */ 
/*    */   public void setCandles(JSONArray candles) {
/* 53 */     put("candles", candles);
/*    */   }
/*    */ 
/*    */   public JSONArray getCandles() {
/* 57 */     return getJSONArray("candles");
/*    */   }
/*    */ 
/*    */   public void setCandleList(List<DFCandleMessage> candles) {
/* 61 */     JSONArray a = new JSONArray();
/* 62 */     for (DFCandleMessage candle : candles) {
/* 63 */       a.put(candle);
/*    */     }
/* 65 */     put("candles", a);
/*    */   }
/*    */ 
/*    */   public List<DFCandleMessage> getCandleList() {
/* 69 */     JSONArray a = getJSONArray("candles");
/* 70 */     List set = new ArrayList(a.length());
/*    */ 
/* 72 */     for (int i = 0; i < a.length(); i++) {
/* 73 */       DFCandleMessage candle = (DFCandleMessage)ProtocolMessage.parse(a.getString(i));
/* 74 */       if (candle == null) {
/* 75 */         return null;
/*    */       }
/* 77 */       set.add(candle);
/*    */     }
/*    */ 
/* 80 */     return set;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFCandleGroupMessage
 * JD-Core Version:    0.6.0
 */