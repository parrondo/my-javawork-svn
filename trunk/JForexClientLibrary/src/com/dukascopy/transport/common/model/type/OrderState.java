/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum OrderState
/*    */ {
/* 11 */   CREATED("CREATED"), 
/* 12 */   PENDING("PENDING"), 
/* 13 */   EXECUTING("EXECUTING"), 
/* 14 */   ERROR("ERROR"), 
/* 15 */   FILLED("FILLED"), 
/* 16 */   REJECTED("REJECTED"), 
/* 17 */   CANCELLED("CANCELLED"), 
/* 18 */   CORRECTION_ORDER("CORRECTION_ORDER"), 
/* 19 */   EXPOSURE_TRANSFER("EXPOSURE_TRANSFER");
/*    */ 
/*    */   private String value;
/*    */ 
/* 24 */   private OrderState(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 28 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static OrderState fromString(String value) {
/* 32 */     if (value.equals(CREATED.asString()))
/* 33 */       return CREATED;
/* 34 */     if (value.equals(PENDING.asString()))
/* 35 */       return PENDING;
/* 36 */     if (value.equals(EXECUTING.asString()))
/* 37 */       return EXECUTING;
/* 38 */     if (value.equals(FILLED.asString()))
/* 39 */       return FILLED;
/* 40 */     if (value.equals(REJECTED.asString()))
/* 41 */       return REJECTED;
/* 42 */     if (value.equals(CANCELLED.asString()))
/* 43 */       return CANCELLED;
/* 44 */     if (value.equals(ERROR.asString()))
/* 45 */       return ERROR;
/* 46 */     if (value.equals(CORRECTION_ORDER.asString()))
/* 47 */       return CORRECTION_ORDER;
/* 48 */     if (value.equals(EXPOSURE_TRANSFER.asString())) {
/* 49 */       return EXPOSURE_TRANSFER;
/*    */     }
/* 51 */     throw new IllegalArgumentException("Invalid order state: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.OrderState
 * JD-Core Version:    0.6.0
 */