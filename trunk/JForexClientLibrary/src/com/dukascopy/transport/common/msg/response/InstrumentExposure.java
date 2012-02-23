/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class InstrumentExposure extends RequestMessage
/*    */ {
/*    */   private static final String AMOUNT_SECONDARY = "amountSecondary";
/*    */   private static final String AMOUNT_PRIMARY = "amountPrimary";
/*    */   private static final String INSTRUMENT = "instrument";
/*    */   public static final String TYPE = "brokerInstrumentExpo";
/*    */ 
/*    */   public InstrumentExposure(ProtocolMessage message)
/*    */   {
/* 19 */     super(message);
/* 20 */     put("instrument", message.getString("instrument"));
/* 21 */     put("amountPrimary", message.getString("amountPrimary"));
/* 22 */     put("amountSecondary", message.getString("amountSecondary"));
/*    */   }
/*    */ 
/*    */   public InstrumentExposure(String instrument, String amountPrimary, String amountSecondary) {
/* 26 */     setType("brokerInstrumentExpo");
/* 27 */     put("instrument", instrument);
/* 28 */     put("amountPrimary", amountPrimary);
/* 29 */     put("amountSecondary", amountSecondary);
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument)
/*    */   {
/* 40 */     put("instrument", instrument);
/*    */   }
/*    */ 
/*    */   public String getInstrument()
/*    */   {
/* 50 */     return getString("instrument");
/*    */   }
/*    */ 
/*    */   public String getCurrencyPrimary()
/*    */   {
/* 59 */     return getString("instrument").substring(0, 3);
/*    */   }
/*    */ 
/*    */   public String getCurrencySecondary()
/*    */   {
/* 68 */     return getString("instrument").substring(4);
/*    */   }
/*    */ 
/*    */   public Money getAmountPrimary()
/*    */   {
/* 77 */     if (getString("amountPrimary") != null) {
/* 78 */       return new Money(getString("amountPrimary"), getCurrencyPrimary());
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ 
/*    */   public Money getAmountSecondary()
/*    */   {
/* 90 */     if (getString("amountSecondary") != null) {
/* 91 */       return new Money(getString("amountSecondary"), getCurrencyPrimary());
/*    */     }
/* 93 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.InstrumentExposure
 * JD-Core Version:    0.6.0
 */