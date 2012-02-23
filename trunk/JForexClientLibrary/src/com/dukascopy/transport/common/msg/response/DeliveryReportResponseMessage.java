/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class DeliveryReportResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "deliv_rept";
/*    */   public static final int STATE_DELIVERED = 1;
/*    */   public static final int STATE_NOTDELIVERED = 2;
/*    */   public static final String STATE = "s";
/*    */ 
/*    */   public DeliveryReportResponseMessage()
/*    */   {
/* 26 */     setType("deliv_rept");
/*    */   }
/*    */ 
/*    */   public DeliveryReportResponseMessage(ProtocolMessage message)
/*    */   {
/* 35 */     super(message);
/* 36 */     setType("deliv_rept");
/* 37 */     setState(message.getInteger("s"));
/*    */   }
/*    */ 
/*    */   public void setState(Integer state) {
/* 41 */     put("s", state);
/*    */   }
/*    */ 
/*    */   public Integer getState() {
/* 45 */     return getInteger("s");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.DeliveryReportResponseMessage
 * JD-Core Version:    0.6.0
 */