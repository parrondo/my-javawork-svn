/*    */ package com.dukascopy.transport.common.msg.executor;
/*    */ 
/*    */ public enum TimeInForce
/*    */ {
/*  5 */   IMMEDIATE_OR_CANCEL("IOC"), 
/*  6 */   GOOD_TILL_CANCEL("GTC"), 
/*  7 */   FILL_OR_KILL("FOK");
/*    */ 
/*    */   private String value;
/*    */ 
/* 12 */   private TimeInForce(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 16 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static TimeInForce fromString(String value) {
/* 20 */     if (value.equals(IMMEDIATE_OR_CANCEL.asString()))
/* 21 */       return IMMEDIATE_OR_CANCEL;
/* 22 */     if (value.equals(GOOD_TILL_CANCEL.asString()))
/* 23 */       return GOOD_TILL_CANCEL;
/* 24 */     if (value.equals(FILL_OR_KILL.asString())) {
/* 25 */       return FILL_OR_KILL;
/*    */     }
/* 27 */     throw new IllegalArgumentException("Invalid order type: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.executor.TimeInForce
 * JD-Core Version:    0.6.0
 */