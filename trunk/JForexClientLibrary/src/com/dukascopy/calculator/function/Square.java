/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Square extends LFunction
/*    */ {
/* 44 */   private static final String[] fname = { "<sup>2</sup>" };
/*    */ 
/*    */   public Square()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.square.of.x";
/* 15 */     this.fshortcut = 'q';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return x * x;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.square();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ 
/*    */   public String shortName() {
/* 41 */     return "<i>x</i><sup>2</sup>";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Square
 * JD-Core Version:    0.6.0
 */