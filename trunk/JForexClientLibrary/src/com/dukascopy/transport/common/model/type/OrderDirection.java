/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum OrderDirection
/*    */ {
/* 12 */   OPEN("OPEN"), 
/* 13 */   CLOSE("CLOSE");
/*    */ 
/*    */   private String value;
/*    */ 
/* 18 */   private OrderDirection(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 22 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static OrderDirection fromString(String value) {
/* 26 */     if (value.equals(OPEN.asString()))
/* 27 */       return OPEN;
/* 28 */     if (value.equals(CLOSE.asString())) {
/* 29 */       return CLOSE;
/*    */     }
/* 31 */     throw new IllegalArgumentException("Invalid order direction: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.OrderDirection
 * JD-Core Version:    0.6.0
 */