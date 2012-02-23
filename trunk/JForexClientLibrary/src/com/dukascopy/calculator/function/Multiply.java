/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Multiply extends MFunction
/*    */ {
/* 42 */   private static final String[] fname = { "&#215;" };
/*    */ 
/*    */   public Multiply()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.multiplication";
/* 15 */     this.fshortcut = '*';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 25 */     return x * y;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 35 */     return x.multiply(y);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 39 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Multiply
 * JD-Core Version:    0.6.0
 */