/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum ExecutorProperty
/*    */ {
/*  5 */   ACCEPT_ORDERS("ACCEPT_ORDERS"), 
/*  6 */   MAX_EXPOSURE("MAX_EXPOSURE"), 
/*  7 */   MIN_EXPOSURE("MIN_EXPOSURE"), 
/*  8 */   CUR_EXPOSURE("CUR_EXPOSURE"), 
/*  9 */   MAX_ORDER_VOLUME("MAX_ORDER_VOLUME"), 
/* 10 */   BALANCE("BALANCE"), 
/* 11 */   TURNOVER("TURNOVER"), 
/* 12 */   USD_BALANCE("USD_BALANCE"), 
/* 13 */   BALANCE_PRIMARY("BALANCE_PRIMARY"), 
/* 14 */   BALANCE_SECONDARY("BALANCE_SECONDARY"), 
/* 15 */   INSTRUMENT_LOCKED("INSTRUMENT_LOCKED"), 
/* 16 */   INSTRUMENT_MARGIN("INSTRUMENT_MARGIN"), 
/* 17 */   INSTRUMENT_LEVERAGE("INSTRUMENT_LEVERAGE"), 
/* 18 */   OFFLINE_INTERVAL("OFFLINE_INTERVAL"), 
/* 19 */   CRITIC_PL_VALUE("CRITIC_PL_VALUE");
/*    */ 
/*    */   private String value;
/*    */ 
/* 24 */   private ExecutorProperty(String value) { this.value = value; }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 28 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static ExecutorProperty fromString(String value) {
/* 32 */     if (value.equals(ACCEPT_ORDERS.toString()))
/* 33 */       return ACCEPT_ORDERS;
/* 34 */     if (value.equals(MAX_EXPOSURE.toString()))
/* 35 */       return MAX_EXPOSURE;
/* 36 */     if (value.equals(MIN_EXPOSURE.toString()))
/* 37 */       return MIN_EXPOSURE;
/* 38 */     if (value.equals(CUR_EXPOSURE.toString()))
/* 39 */       return CUR_EXPOSURE;
/* 40 */     if (value.equals(MAX_ORDER_VOLUME.toString()))
/* 41 */       return MAX_ORDER_VOLUME;
/* 42 */     if (value.equals(BALANCE.toString()))
/* 43 */       return BALANCE;
/* 44 */     if (value.equals(TURNOVER.toString()))
/* 45 */       return TURNOVER;
/* 46 */     if (value.equals(USD_BALANCE.toString()))
/* 47 */       return USD_BALANCE;
/* 48 */     if (value.equals(BALANCE_PRIMARY.toString()))
/* 49 */       return BALANCE_PRIMARY;
/* 50 */     if (value.equals(BALANCE_SECONDARY.toString()))
/* 51 */       return BALANCE_SECONDARY;
/* 52 */     if (value.equals(INSTRUMENT_LOCKED.toString()))
/* 53 */       return INSTRUMENT_LOCKED;
/* 54 */     if (value.equals(INSTRUMENT_MARGIN.toString()))
/* 55 */       return INSTRUMENT_MARGIN;
/* 56 */     if (value.equals(INSTRUMENT_LEVERAGE.toString()))
/* 57 */       return INSTRUMENT_LEVERAGE;
/* 58 */     if (value.equals(OFFLINE_INTERVAL.toString()))
/* 59 */       return OFFLINE_INTERVAL;
/* 60 */     if (value.equals(CRITIC_PL_VALUE.toString())) {
/* 61 */       return CRITIC_PL_VALUE;
/*    */     }
/* 63 */     throw new IllegalArgumentException("Invalid executor property: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.ExecutorProperty
 * JD-Core Version:    0.6.0
 */