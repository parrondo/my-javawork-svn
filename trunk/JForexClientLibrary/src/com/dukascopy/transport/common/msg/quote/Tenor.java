/*    */ package com.dukascopy.transport.common.msg.quote;
/*    */ 
/*    */ public enum Tenor
/*    */ {
/*  6 */   TODAY("TOD"), 
/*  7 */   TOMMOROW("TOM"), 
/*  8 */   SPOT("SPOT"), 
/*  9 */   SPOT_PLUS("SPOT+1"), 
/* 10 */   ONE_WEEK("1 W"), 
/* 11 */   TWO_WEEKS("2 W"), 
/* 12 */   THREE_WEEKS("3 W"), 
/* 13 */   ONE_MONTH("1 M"), 
/* 14 */   TWO_MONTHS("2 M"), 
/* 15 */   THREE_MONTHS("3 M"), 
/* 16 */   FOUR_MONTHS("4 M"), 
/* 17 */   FIVE_MONTHS("5 M"), 
/* 18 */   SIX_MONTHS("6 M"), 
/* 19 */   BROKEN("BROKEN");
/*    */ 
/*    */   private String code;
/*    */ 
/* 24 */   private Tenor(String code) { this.code = code; }
/*    */ 
/*    */   public static String[] getCodes()
/*    */   {
/* 28 */     return new String[] { TODAY.code, TOMMOROW.code, SPOT.code, SPOT_PLUS.code, ONE_WEEK.code, TWO_WEEKS.code, THREE_WEEKS.code, ONE_MONTH.code, TWO_MONTHS.code, THREE_MONTHS.code, FOUR_MONTHS.code, FIVE_MONTHS.code, SIX_MONTHS.code, BROKEN.code };
/*    */   }
/*    */ 
/*    */   public static Tenor getTenor(String tenorCode) {
/* 32 */     if (TODAY.code.equals(tenorCode)) {
/* 33 */       return TODAY;
/*    */     }
/* 35 */     if (TOMMOROW.code.equals(tenorCode)) {
/* 36 */       return TOMMOROW;
/*    */     }
/* 38 */     if (SPOT.code.equals(tenorCode)) {
/* 39 */       return SPOT;
/*    */     }
/* 41 */     if (SPOT_PLUS.code.equals(tenorCode)) {
/* 42 */       return SPOT_PLUS;
/*    */     }
/* 44 */     if (ONE_WEEK.code.equals(tenorCode)) {
/* 45 */       return ONE_WEEK;
/*    */     }
/* 47 */     if (TWO_WEEKS.code.equals(tenorCode)) {
/* 48 */       return TWO_WEEKS;
/*    */     }
/* 50 */     if (THREE_WEEKS.code.equals(tenorCode)) {
/* 51 */       return THREE_WEEKS;
/*    */     }
/* 53 */     if (ONE_MONTH.code.equals(tenorCode)) {
/* 54 */       return ONE_MONTH;
/*    */     }
/* 56 */     if (TWO_MONTHS.code.equals(tenorCode)) {
/* 57 */       return TWO_MONTHS;
/*    */     }
/* 59 */     if (THREE_MONTHS.code.equals(tenorCode)) {
/* 60 */       return THREE_MONTHS;
/*    */     }
/* 62 */     if (FOUR_MONTHS.code.equals(tenorCode)) {
/* 63 */       return FOUR_MONTHS;
/*    */     }
/* 65 */     if (FIVE_MONTHS.code.equals(tenorCode)) {
/* 66 */       return FIVE_MONTHS;
/*    */     }
/* 68 */     if (SIX_MONTHS.code.equals(tenorCode)) {
/* 69 */       return SIX_MONTHS;
/*    */     }
/* 71 */     if (BROKEN.code.equals(tenorCode)) {
/* 72 */       return BROKEN;
/*    */     }
/* 74 */     throw new IllegalArgumentException("Illegal tenor code: " + tenorCode);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.quote.Tenor
 * JD-Core Version:    0.6.0
 */