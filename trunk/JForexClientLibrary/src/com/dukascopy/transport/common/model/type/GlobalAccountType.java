/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum GlobalAccountType
/*    */ {
/* 18 */   NOT_GLOBAL("0"), 
/* 19 */   GLOBAL_FIX("1"), 
/* 20 */   GLOBAL_FUND("2"), 
/* 21 */   GLOBAL_SYS_EXEC("3"), 
/* 22 */   GLOBAL_BANK_ACC("4"), 
/* 23 */   GLOBAL_DUKAS_ACC("5");
/*    */ 
/*    */   public String value;
/*    */ 
/*    */   private GlobalAccountType(String value) {
/* 29 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public String asString() {
/* 33 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static GlobalAccountType fromString(String value) {
/* 37 */     if (NOT_GLOBAL.asString().equals(value))
/* 38 */       return NOT_GLOBAL;
/* 39 */     if (GLOBAL_FIX.asString().equals(value))
/* 40 */       return GLOBAL_FIX;
/* 41 */     if (GLOBAL_FUND.asString().equals(value))
/* 42 */       return GLOBAL_FUND;
/* 43 */     if (GLOBAL_SYS_EXEC.asString().equals(value))
/* 44 */       return GLOBAL_SYS_EXEC;
/* 45 */     if (GLOBAL_BANK_ACC.asString().equals(value))
/* 46 */       return GLOBAL_BANK_ACC;
/* 47 */     if (GLOBAL_DUKAS_ACC.asString().equals(value)) {
/* 48 */       return GLOBAL_DUKAS_ACC;
/*    */     }
/* 50 */     throw new IllegalArgumentException("Invalid GlobalAccountType: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.GlobalAccountType
 * JD-Core Version:    0.6.0
 */