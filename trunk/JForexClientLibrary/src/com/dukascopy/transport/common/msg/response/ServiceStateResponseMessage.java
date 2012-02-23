/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ /** @deprecated */
/*    */ public class ServiceStateResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "srv_state";
/*    */ 
/*    */   public ServiceStateResponseMessage()
/*    */   {
/* 30 */     setType("srv_state");
/*    */   }
/*    */ 
/*    */   public ServiceStateResponseMessage(ProtocolMessage message)
/*    */   {
/* 39 */     super(message);
/* 40 */     setType("srv_state");
/* 41 */     put("keys", message.getJSONArray("keys"));
/* 42 */     put("values", message.getJSONArray("values"));
/*    */   }
/*    */ 
/*    */   public Map getStates() {
/* 46 */     Map res = new HashMap();
/* 47 */     JSONArray keys = getJSONArray("keys");
/* 48 */     JSONArray values = getJSONArray("values");
/* 49 */     if ((keys != null) && (values != null)) {
/* 50 */       for (int i = 0; i < keys.length(); i++) {
/* 51 */         res.put(keys.getString(i), values.getString(i));
/*    */       }
/*    */     }
/* 54 */     return res;
/*    */   }
/*    */ 
/*    */   public void setStates(Map states) {
/* 58 */     put("keys", new JSONArray());
/* 59 */     JSONArray keys = getJSONArray("keys");
/* 60 */     put("values", new JSONArray());
/* 61 */     JSONArray values = getJSONArray("values");
/* 62 */     Iterator i = states.keySet().iterator();
/* 63 */     while (i.hasNext()) {
/* 64 */       String key = i.next().toString();
/* 65 */       keys.put(key);
/* 66 */       values.put(states.get(key));
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ServiceStateResponseMessage
 * JD-Core Version:    0.6.0
 */