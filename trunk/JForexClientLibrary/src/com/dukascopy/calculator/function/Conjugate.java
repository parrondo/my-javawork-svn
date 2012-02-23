/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Conjugate extends RFunction
/*    */ {
/* 40 */   private static final String[] fname = { "c", "o", "n", "j", " " };
/*    */ 
/*    */   public Conjugate()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.complex.conjugate.of.x";
/* 15 */     this.fshortcut = '_';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return x;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.conjugate();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Conjugate
 * JD-Core Version:    0.6.0
 */