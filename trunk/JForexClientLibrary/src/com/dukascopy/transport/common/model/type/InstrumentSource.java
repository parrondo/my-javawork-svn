/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum InstrumentSource
/*    */ {
/* 11 */   INTERBANK("IB"), 
/* 12 */   MARKET_MAKING("MM");
/*    */ 
/*    */   public String value;
/*    */ 
/* 17 */   private InstrumentSource(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 21 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static InstrumentSource fromString(String value) {
/* 25 */     if (INTERBANK.asString().equals(value))
/* 26 */       return INTERBANK;
/* 27 */     if (MARKET_MAKING.asString().equals(value)) {
/* 28 */       return MARKET_MAKING;
/*    */     }
/* 30 */     throw new IllegalArgumentException("Invalid instrument source: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.InstrumentSource
 * JD-Core Version:    0.6.0
 */