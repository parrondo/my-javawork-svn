/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ 
/*    */ public class TSSMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "tss_message";
/*    */   public static final String CONSUMER_ID = "consumer_id";
/*    */   public static final String SS_ID = "ss_id";
/*    */   public static final String ORDER_GROUP = "ordGr";
/*    */   public static final String TEXT = "text";
/*    */ 
/*    */   public TSSMessage()
/*    */   {
/* 21 */     setType("tss_message");
/*    */   }
/*    */ 
/*    */   public TSSMessage(ProtocolMessage message) {
/* 25 */     super(message);
/* 26 */     setType("tss_message");
/* 27 */     setConsumerId(message.getString("consumer_id"));
/* 28 */     setSsId(message.getString("ss_id"));
/* 29 */     setText(message.getString("text"));
/* 30 */     setOrderGroup((OrderGroupMessage)message.get("ordGr"));
/*    */   }
/*    */ 
/*    */   public void setConsumerId(String consumerId) {
/* 34 */     put("consumer_id", consumerId);
/*    */   }
/*    */ 
/*    */   public void setSsId(String ssId) {
/* 38 */     put("ss_id", ssId);
/*    */   }
/*    */ 
/*    */   public void setText(String text) {
/* 42 */     put("text", text);
/*    */   }
/*    */ 
/*    */   public void setOrderGroup(OrderGroupMessage orderGroup) {
/* 46 */     put("ordGr", orderGroup);
/*    */   }
/*    */ 
/*    */   public String getSsId() {
/* 50 */     return getString("ss_id");
/*    */   }
/*    */ 
/*    */   public String getText() {
/* 54 */     return getString("text");
/*    */   }
/*    */ 
/*    */   public OrderGroupMessage getOrderGroup() {
/* 58 */     return (OrderGroupMessage)get("ordGr");
/*    */   }
/*    */ 
/*    */   public String getConsumerId() {
/* 62 */     return getString("consumer_id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.TSSMessage
 * JD-Core Version:    0.6.0
 */