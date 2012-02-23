/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class DFWeekendsResponseMessage extends AbstractDFSMessage
/*    */ {
/*    */   public static final String TYPE = "df_weekends_response";
/*    */   public static final String WEEKENDS = "weekends";
/*    */ 
/*    */   public DFWeekendsResponseMessage()
/*    */   {
/* 21 */     setType("df_weekends_response");
/*    */   }
/*    */ 
/*    */   public DFWeekendsResponseMessage(ProtocolMessage message) {
/* 25 */     super(message);
/*    */ 
/* 27 */     setType("df_weekends_response");
/* 28 */     setWeekends(message.getString("weekends"));
/*    */   }
/*    */ 
/*    */   public void setWeekends(String weekends) {
/* 32 */     put("weekends", weekends);
/*    */   }
/*    */ 
/*    */   public String getWeekends() {
/* 36 */     return getString("weekends");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFWeekendsResponseMessage
 * JD-Core Version:    0.6.0
 */