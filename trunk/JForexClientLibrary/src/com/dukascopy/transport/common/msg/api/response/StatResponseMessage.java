/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class StatResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "stat_response";
/*    */ 
/*    */   public StatResponseMessage()
/*    */   {
/* 20 */     setType("stat_response");
/*    */   }
/*    */ 
/*    */   public StatResponseMessage(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("stat_response");
/* 32 */     setData(message.getJSONArray("data"));
/*    */   }
/*    */ 
/*    */   public void setData(JSONArray data) {
/* 36 */     put("data", data);
/*    */   }
/*    */ 
/*    */   public JSONArray getData()
/*    */   {
/* 41 */     return getJSONArray("data");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.StatResponseMessage
 * JD-Core Version:    0.6.0
 */