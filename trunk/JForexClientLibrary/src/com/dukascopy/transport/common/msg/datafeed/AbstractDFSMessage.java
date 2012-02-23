/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class AbstractDFSMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String SESSION_ID = "sessid";
/*    */   public static final String REQUEST_ID = "id";
/*    */ 
/*    */   public AbstractDFSMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public AbstractDFSMessage(ProtocolMessage message)
/*    */   {
/* 27 */     super(message);
/* 28 */     setSessionId(message.getString("sessid"));
/* 29 */     setRequestId(message.getInteger("id"));
/*    */   }
/*    */ 
/*    */   public void setSessionId(String sessionId) {
/* 33 */     put("sessid", sessionId);
/*    */   }
/*    */ 
/*    */   public String getSessionId() {
/* 37 */     return getString("sessid");
/*    */   }
/*    */ 
/*    */   public void setRequestId(Integer count) {
/* 41 */     put("id", count);
/*    */   }
/*    */ 
/*    */   public Integer getRequestId() {
/* 45 */     return getInteger("id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.AbstractDFSMessage
 * JD-Core Version:    0.6.0
 */