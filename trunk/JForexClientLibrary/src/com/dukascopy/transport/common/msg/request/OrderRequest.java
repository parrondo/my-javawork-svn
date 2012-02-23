/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.Iterator;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class OrderRequest extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "orderReq";
/* 15 */   private String ACTION = "action";
/* 16 */   private String MESSAGE = "message";
/*    */ 
/*    */   public OrderRequest(String action)
/*    */   {
/* 20 */     setType("orderReq");
/* 21 */     put(this.ACTION, action);
/*    */   }
/*    */ 
/*    */   public OrderRequest(ProtocolMessage message)
/*    */   {
/* 26 */     setType("orderReq");
/* 27 */     put(this.ACTION, message.get(this.ACTION));
/* 28 */     for (Iterator i = message.keys(); i.hasNext(); ) {
/* 29 */       String key = (String)i.next();
/* 30 */       if (!has(key))
/* 31 */         put(key, message.get(key));
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getAction()
/*    */   {
/* 37 */     return (String)get(this.ACTION);
/*    */   }
/*    */ 
/*    */   public void setMessage(ProtocolMessage message) {
/* 41 */     put(this.MESSAGE, message);
/*    */   }
/*    */ 
/*    */   public ProtocolMessage getMessge()
/*    */   {
/* 50 */     ProtocolMessage ret = null;
/*    */     try {
/* 52 */       JSONObject msg = getJSONObject(this.MESSAGE);
/* 53 */       ret = ProtocolMessage.parse(msg.toString());
/*    */     } catch (Exception e) {
/* 55 */       e.printStackTrace();
/*    */     }
/* 57 */     return ret;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.OrderRequest
 * JD-Core Version:    0.6.0
 */