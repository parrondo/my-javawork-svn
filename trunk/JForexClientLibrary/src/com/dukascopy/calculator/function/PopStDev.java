/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class PopStDev extends Container
/*    */ {
/* 46 */   private static final String[] fname = { "<i>&#963;</i>", "<sub><i>n</i></sub>" };
/*    */ 
/*    */   public PopStDev()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.pop.st.dev.function";
/* 15 */     this.fshortcut = 'N';
/*    */   }
/*    */ 
/*    */   public void setValue(double d)
/*    */   {
/* 23 */     this.d = d;
/*    */   }
/*    */ 
/*    */   public void setValue(Complex c)
/*    */   {
/* 31 */     this.c = c;
/*    */   }
/*    */ 
/*    */   public void setError(boolean error)
/*    */   {
/* 39 */     this.error = error;
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 43 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.PopStDev
 * JD-Core Version:    0.6.0
 */