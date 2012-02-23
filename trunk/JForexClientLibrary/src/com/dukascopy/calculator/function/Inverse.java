/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Inverse extends LFunction
/*    */ {
/* 44 */   private static final String[] fname = { "<sup>&#8722;</sup>", "<sup>1</sup>" };
/*    */ 
/*    */   public Inverse()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.the.inverse.of.x";
/* 15 */     this.fshortcut = 'i';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return 1.0D / x;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.inverse();
/*    */   }
/*    */ 
/*    */   public String shortName() {
/* 37 */     return "<i>x</i><sup>&#8722;1</sup>";
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 41 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Inverse
 * JD-Core Version:    0.6.0
 */