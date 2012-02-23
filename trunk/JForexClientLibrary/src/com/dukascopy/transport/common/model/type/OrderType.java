/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum OrderType
/*    */ {
/* 11 */   MARKET("MKT"), 
/* 12 */   LIMIT("LIMIT"), 
/* 13 */   STOP("STOP"), 
/* 14 */   STOP_LIMIT("STOP_LIMIT");
/*    */ 
/*    */   private String value;
/*    */ 
/* 19 */   private OrderType(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 23 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static OrderType fromString(String value) {
/* 27 */     if (value.equals(MARKET.asString()))
/* 28 */       return MARKET;
/* 29 */     if (value.equals(LIMIT.asString()))
/* 30 */       return LIMIT;
/* 31 */     if (value.equals(STOP.asString()))
/* 32 */       return STOP;
/* 33 */     if (value.equals(STOP_LIMIT.asString())) {
/* 34 */       return STOP_LIMIT;
/*    */     }
/* 36 */     throw new IllegalArgumentException("Invalid order type: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.OrderType
 * JD-Core Version:    0.6.0
 */