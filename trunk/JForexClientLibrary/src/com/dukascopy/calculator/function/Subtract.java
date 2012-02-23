/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Subtract extends AFunction
/*    */ {
/* 60 */   private static final String[] fname = { "&#8722;" };
/*    */ 
/*    */   public Subtract()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.subtraction.or.minus.sign";
/* 15 */     this.fshortcut = '-';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 25 */     return x - y;
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 34 */     return -x;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 44 */     return x.subtract(y);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 53 */     return x.negate();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 57 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Subtract
 * JD-Core Version:    0.6.0
 */