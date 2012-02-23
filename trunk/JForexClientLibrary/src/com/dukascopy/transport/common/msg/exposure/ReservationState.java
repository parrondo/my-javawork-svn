/*    */ package com.dukascopy.transport.common.msg.exposure;
/*    */ 
/*    */ public enum ReservationState
/*    */ {
/*  5 */   REQUEST("REQUEST"), 
/*  6 */   RESERVED("RESERVED"), 
/*  7 */   CANCELED("CANCELED"), 
/*  8 */   CONFIRMED("CONFIRMED");
/*    */ 
/*    */   private String value;
/*    */ 
/* 13 */   private ReservationState(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 17 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static ReservationState fromString(String value) {
/* 21 */     if (value.equals(REQUEST.asString()))
/* 22 */       return REQUEST;
/* 23 */     if (value.equals(RESERVED.asString()))
/* 24 */       return RESERVED;
/* 25 */     if (value.equals(CANCELED.asString()))
/* 26 */       return CANCELED;
/* 27 */     if (value.equals(CONFIRMED.asString())) {
/* 28 */       return CONFIRMED;
/*    */     }
/* 30 */     throw new IllegalArgumentException("Invalid resetvation state: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.exposure.ReservationState
 * JD-Core Version:    0.6.0
 */