/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum AccountState
/*    */ {
/* 14 */   OK("OK"), 
/*    */ 
/* 20 */   MARGIN_CLOSING("MARGIN_CLOSING"), 
/*    */ 
/* 25 */   MARGIN_CALL("MARGIN_CALL"), 
/*    */ 
/* 30 */   OK_NO_MARGIN_CALL("OK_NO_MARGIN_CALL"), 
/*    */ 
/* 35 */   DISABLED("DISABLED"), 
/*    */ 
/* 40 */   BLOCKED("BLOCKED");
/*    */ 
/*    */   private String value;
/*    */ 
/*    */   private AccountState(String value) {
/* 46 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public String asString() {
/* 50 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static AccountState fromString(String value) {
/* 54 */     if (value.equals(OK.asString()))
/* 55 */       return OK;
/* 56 */     if (value.equals(MARGIN_CLOSING.asString()))
/* 57 */       return MARGIN_CLOSING;
/* 58 */     if (value.equals(MARGIN_CALL.asString()))
/* 59 */       return MARGIN_CALL;
/* 60 */     if (value.equals(DISABLED.asString()))
/* 61 */       return DISABLED;
/* 62 */     if (value.equals(OK_NO_MARGIN_CALL.asString()))
/* 63 */       return OK_NO_MARGIN_CALL;
/* 64 */     if (value.equals(BLOCKED.asString())) {
/* 65 */       return BLOCKED;
/*    */     }
/* 67 */     throw new IllegalArgumentException("Invalid account state: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.AccountState
 * JD-Core Version:    0.6.0
 */