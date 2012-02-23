/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class Mean extends Container
/*    */ {
/* 47 */   private static final String[] fname = { "m", "e", "a", "n" };
/*    */ 
/*    */   public Mean()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.the.mean.of.the.numbers.stored.in.statistics.memory";
/* 15 */     this.fshortcut = 'm';
/*    */   }
/*    */ 
/*    */   public void setError(boolean error)
/*    */   {
/* 23 */     this.error = error;
/*    */   }
/*    */ 
/*    */   public void setValue(double d)
/*    */   {
/* 31 */     this.d = d;
/*    */   }
/*    */ 
/*    */   public void setValue(Complex c)
/*    */   {
/* 39 */     this.c = c;
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 43 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Mean
 * JD-Core Version:    0.6.0
 */