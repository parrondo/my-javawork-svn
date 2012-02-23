/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class Pi extends Container
/*    */ {
/*    */   public static final String ITALIC_PI = "<i>&#960;</i>";
/* 27 */   private static final String[] fname = { "<i>&#960;</i>" };
/*    */ 
/*    */   public Pi()
/*    */   {
/* 17 */     this.d = new Double(3.141592653589793D).doubleValue();
/* 18 */     this.c = new Complex(3.141592653589793D);
/* 19 */     this.ftooltip = "<i>&#960;</i>";
/* 20 */     this.fshortcut = 'p';
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 24 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Pi
 * JD-Core Version:    0.6.0
 */