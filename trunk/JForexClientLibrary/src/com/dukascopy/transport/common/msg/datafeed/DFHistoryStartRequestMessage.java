/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class DFHistoryStartRequestMessage extends AbstractDFSMessage
/*    */ {
/*    */   public static final String TYPE = "df_history_start_request";
/*    */   public static final String INSTRUMENTS = "instruments";
/*    */ 
/*    */   public DFHistoryStartRequestMessage()
/*    */   {
/* 24 */     setType("df_history_start_request");
/*    */   }
/*    */ 
/*    */   public DFHistoryStartRequestMessage(ProtocolMessage message) {
/* 28 */     super(message);
/*    */ 
/* 30 */     setType("df_history_start_request");
/* 31 */     setInstruments(message.getString("instruments"));
/*    */   }
/*    */ 
/*    */   public void setInstruments(String instruments)
/*    */   {
/* 36 */     put("instruments", instruments);
/*    */   }
/*    */ 
/*    */   public String getInstruments() {
/* 40 */     return getString("instruments");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFHistoryStartRequestMessage
 * JD-Core Version:    0.6.0
 */