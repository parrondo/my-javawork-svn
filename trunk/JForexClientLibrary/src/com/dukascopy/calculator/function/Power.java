/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Power extends DFunction
/*    */ {
/* 45 */   private static final String[] fname = { "^" };
/*    */ 
/*    */   public Power()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.power.function";
/* 15 */     this.fshortcut = '^';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 25 */     if (y >= 0.0D) {
/* 26 */       return Math.pow(x, y);
/*    */     }
/* 28 */     return 1.0D / Math.pow(x, -y);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 38 */     return x.pow(y);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 42 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Power
 * JD-Core Version:    0.6.0
 */