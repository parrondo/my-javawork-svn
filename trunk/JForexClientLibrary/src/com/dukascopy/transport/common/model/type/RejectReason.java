/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum RejectReason
/*    */ {
/* 17 */   REJECT_MARGIN("MARGIN"), 
/* 18 */   REJECT_LIQUIDITY("LIQUIDITY"), 
/* 19 */   REJECT_ERROR("ERROR"), 
/* 20 */   REJECT_INVALID_ORDER("INVALID_ORDER"), 
/* 21 */   REJECT_PROVIDER_CONNECTION("CONNECTION"), 
/* 22 */   REJECT_EXPIRED("REJECT_EXPIRED");
/*    */ 
/*    */   private String value;
/*    */ 
/* 27 */   private RejectReason(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 31 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static RejectReason fromString(String value) {
/* 35 */     if (REJECT_MARGIN.asString().equals(value))
/* 36 */       return REJECT_MARGIN;
/* 37 */     if (REJECT_LIQUIDITY.asString().equals(value))
/* 38 */       return REJECT_LIQUIDITY;
/* 39 */     if (REJECT_ERROR.asString().equals(value))
/* 40 */       return REJECT_ERROR;
/* 41 */     if (REJECT_INVALID_ORDER.asString().equals(value))
/* 42 */       return REJECT_INVALID_ORDER;
/* 43 */     if (REJECT_PROVIDER_CONNECTION.asString().equals(value))
/* 44 */       return REJECT_PROVIDER_CONNECTION;
/* 45 */     if (REJECT_EXPIRED.asString().equals(value)) {
/* 46 */       return REJECT_EXPIRED;
/*    */     }
/* 48 */     throw new IllegalArgumentException("Invalid reject reason: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.RejectReason
 * JD-Core Version:    0.6.0
 */