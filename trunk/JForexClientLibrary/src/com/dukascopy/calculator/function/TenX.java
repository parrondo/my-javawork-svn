/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class TenX extends RFunction
/*    */ {
/* 40 */   private static final String[] fname = { "1", "0", "<sup><i>x</i></sup>" };
/*    */ 
/*    */   public TenX()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.ten.x.function";
/* 15 */     this.fshortcut = 'l';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return Math.exp(x * Math.log(10.0D));
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.tenx();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 37 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.TenX
 * JD-Core Version:    0.6.0
 */