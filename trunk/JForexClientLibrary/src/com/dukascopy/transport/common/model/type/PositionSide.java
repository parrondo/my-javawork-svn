/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum PositionSide
/*    */ {
/* 11 */   LONG("LONG"), 
/* 12 */   SHORT("SHORT");
/*    */ 
/*    */   private String value;
/*    */ 
/* 17 */   private PositionSide(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 21 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static PositionSide fromString(String value) {
/* 25 */     if (value.equals(LONG.asString()))
/* 26 */       return LONG;
/* 27 */     if (value.equals(SHORT.asString())) {
/* 28 */       return SHORT;
/*    */     }
/* 30 */     throw new IllegalArgumentException("Invalid position side: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.PositionSide
 * JD-Core Version:    0.6.0
 */