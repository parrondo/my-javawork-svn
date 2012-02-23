/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Combination extends DFunction
/*    */ {
/* 55 */   private static final String[] fname = { "<b>C</b>" };
/*    */ 
/*    */   public Combination()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.combination.function.tooltip";
/* 15 */     this.fshortcut = 'P';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 26 */     if ((x < 0.0D) || (Math.round(x) - x != 0.0D))
/* 27 */       throw new ArithmeticException("Combination error");
/* 28 */     if ((y < 0.0D) || (y > x) || (Math.round(y) - y != 0.0D))
/* 29 */       throw new ArithmeticException("Combination error");
/* 30 */     if (y == 0.0D) {
/* 31 */       return 1.0D;
/*    */     }
/* 33 */     return x / y * function(x - 1.0D, y - 1.0D);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 44 */     return x.combination(y);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 48 */     return fname;
/*    */   }
/*    */ 
/*    */   public String shortName() {
/* 52 */     return "<i>n</i>C<i>r</i>";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Combination
 * JD-Core Version:    0.6.0
 */