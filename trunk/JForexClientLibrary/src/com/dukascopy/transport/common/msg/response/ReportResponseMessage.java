/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ 
/*    */ public class ReportResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "report";
/*    */ 
/*    */   public ReportResponseMessage()
/*    */   {
/* 19 */     setType("report");
/*    */   }
/*    */ 
/*    */   public ReportResponseMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("report");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ReportResponseMessage
 * JD-Core Version:    0.6.0
 */