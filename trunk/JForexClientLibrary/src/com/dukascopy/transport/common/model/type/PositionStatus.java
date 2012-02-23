/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum PositionStatus
/*    */ {
/*  8 */   OPEN("OPEN"), 
/*  9 */   CLOSE("CLOSE");
/*    */ 
/*    */   private String value;
/*    */ 
/* 14 */   private PositionStatus(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 18 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static PositionStatus fromString(String value) {
/* 22 */     if (value.equals(OPEN.asString()))
/* 23 */       return OPEN;
/* 24 */     if (value.equals(CLOSE.asString())) {
/* 25 */       return CLOSE;
/*    */     }
/* 27 */     throw new IllegalArgumentException("Invalid position side: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.PositionStatus
 * JD-Core Version:    0.6.0
 */