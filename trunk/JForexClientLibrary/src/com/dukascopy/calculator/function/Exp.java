/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Exp extends RFunction
/*    */ {
/* 40 */   private static final String[] fname = { "e", "x", "p", " " };
/*    */ 
/*    */   public Exp()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.exponential.function";
/* 15 */     this.fshortcut = 'e';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return Math.exp(x);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.exp();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Exp
 * JD-Core Version:    0.6.0
 */