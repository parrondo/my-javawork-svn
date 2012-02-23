/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class ConnectionCloseNotification extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "ccn";
/*    */ 
/*    */   public ConnectionCloseNotification()
/*    */   {
/* 20 */     setType("ccn");
/*    */   }
/*    */ 
/*    */   public ConnectionCloseNotification(ProtocolMessage message)
/*    */   {
/* 29 */     super(message);
/*    */ 
/* 31 */     setType("ccn");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ConnectionCloseNotification
 * JD-Core Version:    0.6.0
 */