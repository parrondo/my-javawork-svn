/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class PositionHistoryResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "pos_history_statement";
/*    */ 
/*    */   public PositionHistoryResponseMessage()
/*    */   {
/* 20 */     setType("pos_history_statement");
/*    */   }
/*    */ 
/*    */   public PositionHistoryResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("pos_history_statement");
/*    */ 
/* 33 */     setHistory(message.getJSONArray("history"));
/*    */   }
/*    */ 
/*    */   public void setHistory(JSONArray array) {
/* 37 */     put("history", array);
/*    */   }
/*    */ 
/*    */   public JSONArray getHistory() {
/* 41 */     return getJSONArray("history");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.PositionHistoryResponseMessage
 * JD-Core Version:    0.6.0
 */