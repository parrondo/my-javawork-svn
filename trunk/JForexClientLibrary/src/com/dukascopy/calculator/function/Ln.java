/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Ln extends RFunction
/*    */ {
/* 40 */   private static final String[] fname = { "l", "n", " " };
/*    */ 
/*    */   public Ln()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.natural.logarithm";
/* 15 */     this.fshortcut = 'L';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return Math.log(x);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.log();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Ln
 * JD-Core Version:    0.6.0
 */