/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ 
/*    */ public class CubeRoot extends RFunction
/*    */ {
/* 41 */   private static final String[] fname = { "&#179;", "&#8730;" };
/*    */ 
/*    */   public CubeRoot()
/*    */   {
/* 15 */     this.ftooltip = "sc.calculator.cube.root.of.x";
/* 16 */     this.fshortcut = 'v';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 25 */     return Math.exp(Math.log(x) / 3.0D);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 34 */     return x.cuberoot();
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 38 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.CubeRoot
 * JD-Core Version:    0.6.0
 */