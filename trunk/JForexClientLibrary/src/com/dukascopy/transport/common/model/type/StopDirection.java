/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum StopDirection
/*    */ {
/* 11 */   BID_GREATER("GREATER_BID"), 
/* 12 */   ASK_GREATER("GREATER_ASK"), 
/* 13 */   BID_LESS("LESS_BID"), 
/* 14 */   ASK_LESS("LESS_ASK"), 
/* 15 */   BID_EQUALS("EQUALS_BID"), 
/* 16 */   ASK_EQUALS("EQUALS_ASK");
/*    */ 
/*    */   private String value;
/*    */ 
/* 21 */   private StopDirection(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 25 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static StopDirection fromString(String value) {
/* 29 */     if (value.equals(BID_GREATER.asString()))
/* 30 */       return BID_GREATER;
/* 31 */     if (value.equals(ASK_GREATER.asString()))
/* 32 */       return ASK_GREATER;
/* 33 */     if (value.equals(BID_LESS.asString()))
/* 34 */       return BID_LESS;
/* 35 */     if (value.equals(ASK_LESS.asString()))
/* 36 */       return ASK_LESS;
/* 37 */     if (BID_EQUALS.asString().equals(value))
/* 38 */       return BID_EQUALS;
/* 39 */     if (ASK_EQUALS.asString().equals(value)) {
/* 40 */       return ASK_EQUALS;
/*    */     }
/* 42 */     throw new IllegalArgumentException("Invalid stop direction: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.StopDirection
 * JD-Core Version:    0.6.0
 */