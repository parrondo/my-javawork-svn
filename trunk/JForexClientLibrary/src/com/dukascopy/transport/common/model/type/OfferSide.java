/*    */ package com.dukascopy.transport.common.model.type;
/*    */ 
/*    */ public enum OfferSide
/*    */ {
/* 11 */   BID("BID"), 
/* 12 */   ASK("ASK");
/*    */ 
/*    */   private String value;
/*    */ 
/* 17 */   private OfferSide(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 21 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static OfferSide fromString(String value) {
/* 25 */     if (value.equals(BID.asString()))
/* 26 */       return BID;
/* 27 */     if (value.equals(ASK.asString())) {
/* 28 */       return ASK;
/*    */     }
/* 30 */     throw new IllegalArgumentException("Invalid quote side: " + value);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.OfferSide
 * JD-Core Version:    0.6.0
 */