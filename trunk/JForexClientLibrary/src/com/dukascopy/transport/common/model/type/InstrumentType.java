/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum InstrumentType
/*    */ {
/* 11 */   FOREX("FOREX"), 
/* 12 */   STOCK_CFD("STOCK"), 
/* 13 */   INDEX_CFD("INDEX");
/*    */ 
/*    */   private String value;
/*    */ 
/* 18 */   private InstrumentType(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 22 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static InstrumentType fromString(String value) {
/* 26 */     if (value.equals(FOREX.asString()))
/* 27 */       return FOREX;
/* 28 */     if (value.equals(STOCK_CFD.asString()))
/* 29 */       return STOCK_CFD;
/* 30 */     if (value.equals(INDEX_CFD.asString())) {
/* 31 */       return INDEX_CFD;
/*    */     }
/* 33 */     throw new IllegalArgumentException("Invalid instrument type: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.InstrumentType
 * JD-Core Version:    0.6.0
 */