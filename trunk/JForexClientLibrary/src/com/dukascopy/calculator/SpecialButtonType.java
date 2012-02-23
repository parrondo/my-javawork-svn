/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ public enum SpecialButtonType
/*    */ {
/* 12 */   NONE, SHIFT, STAT, SHIFT_STAT, HEX, SHIFT_HEX;
/*    */ 
/*    */   public String toString() {
/* 15 */     switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[ordinal()]) {
/*    */     case 1:
/* 17 */       return "NONE";
/*    */     case 2:
/* 19 */       return "STAT";
/*    */     case 3:
/*    */     case 4:
/*    */     case 5:
/* 23 */       return "SHIFT";
/*    */     case 6:
/* 25 */       return "HEX";
/*    */     }
/* 27 */     throw new AssertionError("Unknown SpecialButtonType");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.SpecialButtonType
 * JD-Core Version:    0.6.0
 */