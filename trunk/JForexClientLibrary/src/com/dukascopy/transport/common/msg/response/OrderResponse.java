/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class OrderResponse<E extends ProtocolMessage> extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "orderResp";
/* 17 */   private String ACTION = "action";
/* 18 */   private String LIST_RESPONSE = "list_response";
/*    */ 
/*    */   public OrderResponse(String action)
/*    */   {
/* 22 */     setType("orderResp");
/* 23 */     put(this.ACTION, action);
/*    */   }
/*    */ 
/*    */   public OrderResponse(ProtocolMessage message)
/*    */   {
/* 28 */     setType("orderResp");
/* 29 */     put(this.ACTION, message.get(this.ACTION));
/* 30 */     for (Iterator i = message.keys(); i.hasNext(); ) {
/* 31 */       String key = (String)i.next();
/* 32 */       if (!has(key))
/* 33 */         put(key, message.get(key));
/*    */     }
/*    */   }
/*    */ 
/*    */   public List<E> getListResponse()
/*    */   {
/* 39 */     List response = new ArrayList();
/* 40 */     JSONArray tradesArray = getJSONArray(this.LIST_RESPONSE);
/* 41 */     if (tradesArray != null) {
/* 42 */       for (int i = 0; i < tradesArray.length(); i++) {
/* 43 */         response.add(ProtocolMessage.parse(tradesArray.getJSONObject(i).toString()));
/*    */       }
/*    */     }
/* 46 */     return response;
/*    */   }
/*    */ 
/*    */   public void setListResponse(List<E> messages) {
/* 50 */     put(this.LIST_RESPONSE, new JSONArray());
/* 51 */     JSONArray messagesArray = getJSONArray(this.LIST_RESPONSE);
/* 52 */     for (ProtocolMessage message : messages)
/* 53 */       messagesArray.put(message);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.OrderResponse
 * JD-Core Version:    0.6.0
 */