/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class DFWeekendsRequestMessage extends AbstractDFSMessage
/*    */ {
/*    */   public static final String TYPE = "df_weekends_request";
/*    */   public static final String INSTRUMENT = "instrument";
/*    */   public static final String FROM = "from";
/*    */   public static final String TO = "to";
/*    */ 
/*    */   public DFWeekendsRequestMessage()
/*    */   {
/* 30 */     setType("df_weekends_request");
/*    */   }
/*    */ 
/*    */   public DFWeekendsRequestMessage(ProtocolMessage message) {
/* 34 */     super(message);
/*    */ 
/* 36 */     setType("df_weekends_request");
/* 37 */     setInstrument(message.getString("instrument"));
/* 38 */     setFrom(toLong(message.get("from")));
/* 39 */     setTo(toLong(message.get("to")));
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 43 */     put("instrument", instrument);
/*    */   }
/*    */ 
/*    */   public String getInstrument() {
/* 47 */     return getString("instrument");
/*    */   }
/*    */ 
/*    */   public void setFrom(Long from) {
/* 51 */     put("from", toStr(from));
/*    */   }
/*    */ 
/*    */   public Long getFrom() {
/* 55 */     Object fromObject = get("from");
/* 56 */     Long value = toLong(fromObject);
/* 57 */     return value;
/*    */   }
/*    */ 
/*    */   public void setTo(Long to) {
/* 61 */     put("to", toStr(to));
/*    */   }
/*    */ 
/*    */   public Long getTo() {
/* 65 */     Object toObject = get("to");
/* 66 */     Long value = toLong(toObject);
/* 67 */     return value;
/*    */   }
/*    */ 
/*    */   private Long toLong(Object obj) {
/* 71 */     Long value = obj == null ? null : Long.valueOf(obj.toString());
/* 72 */     return value;
/*    */   }
/*    */ 
/*    */   private String toStr(Long value) {
/* 76 */     return value == null ? null : value.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.DFWeekendsRequestMessage
 * JD-Core Version:    0.6.0
 */