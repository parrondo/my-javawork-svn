/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum SignalType
/*    */ {
/* 13 */   TRADE_BY_BIG_BUTTON("tbbb"), 
/* 14 */   TRADE_BY_SUBMIT("tbsb"), 
/* 15 */   TRADE_BY_MENU("tbcm");
/*    */ 
/*    */   public String value;
/*    */ 
/* 20 */   private SignalType(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 24 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static SignalType fromString(String value) {
/* 28 */     if (value.equals(TRADE_BY_BIG_BUTTON.asString()))
/* 29 */       return TRADE_BY_BIG_BUTTON;
/* 30 */     if (value.equals(TRADE_BY_SUBMIT.asString()))
/* 31 */       return TRADE_BY_SUBMIT;
/* 32 */     if (value.equals(TRADE_BY_MENU.asString())) {
/* 33 */       return TRADE_BY_MENU;
/*    */     }
/* 35 */     throw new IllegalArgumentException("Invalid signal type: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.SignalType
 * JD-Core Version:    0.6.0
 */