/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class TimeSyncResponseMessage extends ResponseMessage
/*    */ {
/*    */   public static final String TYPE = "timeSync";
/*    */ 
/*    */   public TimeSyncResponseMessage()
/*    */   {
/* 22 */     setType("timeSync");
/*    */   }
/*    */ 
/*    */   public TimeSyncResponseMessage(ProtocolMessage message)
/*    */   {
/* 31 */     super(message);
/*    */ 
/* 33 */     setType("timeSync");
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 37 */     TimeSyncResponseMessage tsrm = new TimeSyncResponseMessage();
/* 38 */     tsrm.setTimeSyncMs(Long.valueOf(System.currentTimeMillis()));
/* 39 */     System.out.println(tsrm.toProtocolString());
/* 40 */     System.out.println(tsrm.getTimeSyncMs());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.TimeSyncResponseMessage
 * JD-Core Version:    0.6.0
 */