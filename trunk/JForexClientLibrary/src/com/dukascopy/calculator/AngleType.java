/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ public enum AngleType
/*    */ {
/*  8 */   DEGREES, RADIANS;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 16 */     switch (1.$SwitchMap$com$dukascopy$calculator$AngleType[ordinal()]) {
/*    */     case 1:
/* 18 */       return "Degrees";
/*    */     case 2:
/* 20 */       return "Radians";
/*    */     }
/* 22 */     throw new AssertionError("Unknown AngleType");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.AngleType
 * JD-Core Version:    0.6.0
 */