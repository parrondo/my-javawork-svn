/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum OrderSide
/*    */ {
/* 11 */   BUY("BUY"), 
/* 12 */   SELL("SELL");
/*    */ 
/*    */   public String value;
/*    */ 
/* 17 */   private OrderSide(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 21 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static OrderSide fromString(String value) {
/* 25 */     if (value.equals(BUY.asString()))
/* 26 */       return BUY;
/* 27 */     if (value.equals(SELL.asString())) {
/* 28 */       return SELL;
/*    */     }
/* 30 */     throw new IllegalArgumentException("Invalid order side: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.OrderSide
 * JD-Core Version:    0.6.0
 */