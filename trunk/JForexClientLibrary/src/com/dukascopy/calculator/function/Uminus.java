/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class Uminus extends RFunction
/*    */ {
/* 38 */   private static final String[] fname = { "(&#8722;)" };
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 22 */     return -x;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 31 */     return x.negate();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 35 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Uminus
 * JD-Core Version:    0.6.0
 */