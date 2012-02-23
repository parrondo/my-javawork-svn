/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class SquareRoot extends RFunction
/*    */ {
/* 40 */   private static final String[] fname = { "&#8730;" };
/*    */ 
/*    */   public SquareRoot()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.square.root.of.x";
/* 15 */     this.fshortcut = 'r';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return Math.sqrt(x);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.sqrt();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.SquareRoot
 * JD-Core Version:    0.6.0
 */