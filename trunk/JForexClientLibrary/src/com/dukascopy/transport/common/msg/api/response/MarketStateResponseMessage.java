/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.math.BigDecimal;
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.Format;
/*    */ import javax.swing.text.NumberFormatter;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class MarketStateResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "market_state_response";
/*    */ 
/*    */   public MarketStateResponseMessage()
/*    */   {
/* 26 */     setType("market_state_response");
/*    */   }
/*    */ 
/*    */   public MarketStateResponseMessage(ProtocolMessage message)
/*    */   {
/* 35 */     super(message);
/*    */ 
/* 37 */     setType("market_state_response");
/*    */ 
/* 39 */     setData(message.getJSONArray("data"));
/*    */   }
/*    */ 
/*    */   public void setData(JSONArray data) {
/* 43 */     put("data", data);
/*    */   }
/*    */ 
/*    */   public JSONArray getData() {
/* 47 */     return getJSONArray("data");
/*    */   }
/*    */ 
/*    */   public static JSONObject getQuoteObject(int id, int type, String status, String name, BigDecimal bid, BigDecimal ask, boolean bidUp, boolean askUp, BigDecimal pipPrice)
/*    */   {
/* 66 */     String pattern = pipPrice.toString();
/* 67 */     int i = pattern.indexOf('1');
/* 68 */     if (i != -1) {
/* 69 */       pattern = pattern.substring(0, i) + "00";
/*    */     }
/*    */ 
/* 72 */     NumberFormatter nf = new NumberFormatter(new DecimalFormat(pattern));
/* 73 */     Format f = nf.getFormat();
/*    */ 
/* 75 */     JSONObject result = new JSONObject();
/* 76 */     result.put("id", id);
/* 77 */     result.put("type", type);
/* 78 */     result.put("status", status);
/* 79 */     result.put("name", name);
/* 80 */     result.put("bid", f.format(bid));
/* 81 */     result.put("ask", f.format(ask));
/* 82 */     result.put("bid_dir", bidUp ? "up" : "down");
/* 83 */     result.put("ask_dir", askUp ? "up" : "down");
/* 84 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.MarketStateResponseMessage
 * JD-Core Version:    0.6.0
 */