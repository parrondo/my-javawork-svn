/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class RCL extends Container
/*    */ {
/* 37 */   private static final String[] fname = { "R", "C", "L" };
/*    */ 
/*    */   public void setValue(double d)
/*    */   {
/* 22 */     this.d = d;
/*    */   }
/*    */ 
/*    */   public void setValue(OObject c)
/*    */   {
/* 30 */     this.c = c;
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 34 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.RCL
 * JD-Core Version:    0.6.0
 */