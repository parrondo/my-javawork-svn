/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class DeliveryReportRequestMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "deliv_req";
/*    */ 
/*    */   public DeliveryReportRequestMessage()
/*    */   {
/* 21 */     setType("deliv_req");
/*    */   }
/*    */ 
/*    */   public DeliveryReportRequestMessage(ProtocolMessage message)
/*    */   {
/* 30 */     super(message);
/* 31 */     setType("deliv_req");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.DeliveryReportRequestMessage
 * JD-Core Version:    0.6.0
 */