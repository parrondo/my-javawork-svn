/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum SignalSide
/*    */ {
/* 12 */   BUY("BUY"), 
/* 13 */   SELL("SELL");
/*    */ 
/*    */   public String value;
/*    */ 
/* 18 */   private SignalSide(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 22 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static SignalSide fromString(String value) {
/* 26 */     if (value.equals(BUY.asString()))
/* 27 */       return BUY;
/* 28 */     if (value.equals(SELL.asString())) {
/* 29 */       return SELL;
/*    */     }
/* 31 */     throw new IllegalArgumentException("Invalid signal side: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.SignalSide
 * JD-Core Version:    0.6.0
 */