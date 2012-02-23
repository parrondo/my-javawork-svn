/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum ServiceCommand
/*    */ {
/* 12 */   SERVICE_LOCK("SERVICE_LOCK"), 
/* 13 */   SERVICE_UNLOCK("SERVICE_UNLOCK"), 
/* 14 */   QUOTE_LOCK("QUOTE_LOCK"), 
/* 15 */   QUOTE_UNLOCK("QUOTE_UNLOCK"), 
/* 16 */   SRV_STATE("SRV_STATE"), 
/* 17 */   EXEC_RESET_BALANCE("EXEC_RESET_BALANCE"), 
/* 18 */   RESET_EXPOSURE("RESET_EXPOSURE"), 
/* 19 */   SET_MAX_ORDER_VOLUME("SET_MAX_ORDER_VOLUME"), 
/* 20 */   SET_MAX_EXPOSURE("SET_MAX_EXPOSURE"), 
/* 21 */   SET_MIN_EXPOSURE("SET_MIN_EXPOSURE"), 
/* 22 */   SET_LEVERAGE("SET_LEVERAGE"), 
/* 23 */   SET_OFFLINE_INTERVAL("SET_OFFLINE_INTERVAL"), 
/* 24 */   CORRECTION_TRADE("CORRECTION_TRADE"), 
/* 25 */   SET_CRITIC_PL("SET_CRITIC_PL");
/*    */ 
/*    */   private String value;
/*    */ 
/*    */   private ServiceCommand(String value) {
/* 31 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public String asString() {
/* 35 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static ServiceCommand fromString(String value) {
/* 39 */     if (value.equals(SERVICE_LOCK.asString()))
/* 40 */       return SERVICE_LOCK;
/* 41 */     if (value.equals(SERVICE_UNLOCK.asString()))
/* 42 */       return SERVICE_UNLOCK;
/* 43 */     if (value.equals(SET_CRITIC_PL.asString()))
/* 44 */       return SET_CRITIC_PL;
/* 45 */     if (value.equals(SERVICE_LOCK.asString()))
/* 46 */       return SERVICE_LOCK;
/* 47 */     if (value.equals(QUOTE_LOCK.asString()))
/* 48 */       return QUOTE_LOCK;
/* 49 */     if (value.equals(QUOTE_UNLOCK.asString()))
/* 50 */       return QUOTE_UNLOCK;
/* 51 */     if (value.equals(SRV_STATE.asString()))
/* 52 */       return SRV_STATE;
/* 53 */     if (value.equals(EXEC_RESET_BALANCE.asString()))
/* 54 */       return EXEC_RESET_BALANCE;
/* 55 */     if (value.equals(RESET_EXPOSURE.asString()))
/* 56 */       return RESET_EXPOSURE;
/* 57 */     if (value.equals(SET_MAX_EXPOSURE.asString()))
/* 58 */       return SET_MAX_EXPOSURE;
/* 59 */     if (value.equals(SET_MIN_EXPOSURE.asString()))
/* 60 */       return SET_MIN_EXPOSURE;
/* 61 */     if (value.equals(SET_MAX_ORDER_VOLUME.asString()))
/* 62 */       return SET_MAX_ORDER_VOLUME;
/* 63 */     if (value.equals(SET_LEVERAGE.asString()))
/* 64 */       return SET_LEVERAGE;
/* 65 */     if (value.equals(SET_OFFLINE_INTERVAL.asString()))
/* 66 */       return SET_OFFLINE_INTERVAL;
/* 67 */     if (value.equals(CORRECTION_TRADE.asString())) {
/* 68 */       return CORRECTION_TRADE;
/*    */     }
/* 70 */     throw new IllegalArgumentException("Invalid service command: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.ServiceCommand
 * JD-Core Version:    0.6.0
 */