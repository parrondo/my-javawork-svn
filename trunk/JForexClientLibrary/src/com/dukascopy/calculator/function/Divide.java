/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Divide extends MFunction
/*    */ {
/* 42 */   private static final String[] fname = { "&#247;" };
/*    */ 
/*    */   public Divide()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.division";
/* 15 */     this.fshortcut = '/';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 25 */     return x / y;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 35 */     return x.divide(y);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 39 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Divide
 * JD-Core Version:    0.6.0
 */