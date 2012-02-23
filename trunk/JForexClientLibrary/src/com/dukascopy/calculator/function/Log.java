/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Log extends RFunction
/*    */ {
/* 40 */   private static final String[] fname = { "l", "o", "g", " " };
/*    */ 
/*    */   public Log()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.base.10.logarithm";
/* 15 */     this.fshortcut = 'l';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return Math.log(x) / Math.log(10.0D);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.log10();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Log
 * JD-Core Version:    0.6.0
 */