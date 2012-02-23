/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Factorial extends LFunction
/*    */ {
/* 49 */   private static final String[] fname = { "!" };
/*    */ 
/*    */   public Factorial()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.factorial.function";
/* 15 */     this.fshortcut = '!';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     if ((x < 0.0D) || (Math.round(x) - x != 0.0D))
/* 25 */       throw new ArithmeticException("Factorial error");
/* 26 */     if (x == 0.0D) {
/* 27 */       return 1.0D;
/*    */     }
/* 29 */     return x * function(x - 1.0D);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 38 */     return x.factorial();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 42 */     return fname;
/*    */   }
/*    */ 
/*    */   public String shortName() {
/* 46 */     return "<i>x</i>!";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Factorial
 * JD-Core Version:    0.6.0
 */