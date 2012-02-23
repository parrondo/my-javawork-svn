/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ public class ChildSocketAuthAcceptorMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "childAcceptor";
/*    */ 
/*    */   public ChildSocketAuthAcceptorMessage()
/*    */   {
/* 13 */     setType("childAcceptor");
/*    */   }
/*    */ 
/*    */   public ChildSocketAuthAcceptorMessage(ProtocolMessage message)
/*    */   {
/* 22 */     super(message);
/* 23 */     setType("childAcceptor");
/* 24 */     put("socketAcceptorId", message.getString("socketAcceptorId"));
/* 25 */     put("parentSessionId", message.getString("parentSessionId"));
/*    */   }
/*    */ 
/*    */   public String getParentSessionId()
/*    */   {
/* 31 */     return getString("parentSessionId");
/*    */   }
/*    */ 
/*    */   public void setParentSessionId(String parentSessionId) {
/* 35 */     put("parentSessionId", parentSessionId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.ChildSocketAuthAcceptorMessage
 * JD-Core Version:    0.6.0
 */