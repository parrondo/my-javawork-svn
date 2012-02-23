/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class DFHistoryStartResponseMessage extends AbstractDFSMessage
/*    */ {
/*    */   public static final String TYPE = "df_history_start_response";
/*    */   public static final String HISTORY_START = "historyStart";
/*    */ 
/*    */   public DFHistoryStartResponseMessage()
/*    */   {
/* 20 */     setType("df_history_start_response");
/*    */   }
/*    */ 
/*    */   public DFHistoryStartResponseMessage(ProtocolMessage message) {
/* 24 */     super(message);
/*    */ 
/* 26 */     setType("df_history_start_response");
/* 27 */     setHistoryStart(message.getString("historyStart"));
/*    */   }
/*    */ 
/*    */   public void setHistoryStart(String historyStart)
/*    */   {
/* 32 */     put("historyStart", historyStart);
/*    */   }
/*    */ 
/*    */   public String getHistoryStart() {
/* 36 */     return getString("historyStart");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFHistoryStartResponseMessage
 * JD-Core Version:    0.6.0
 */