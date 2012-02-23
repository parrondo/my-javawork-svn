/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class ATan extends Trig
/*    */ {
/* 51 */   private static final String[] fname = { "t", "a", "n", "<sup>&#8722;</sup>", "<sup>1</sup>", " " };
/*    */ 
/*    */   public ATan(AngleType angleType)
/*    */   {
/* 17 */     super(angleType);
/* 18 */     this.ftooltip = "sc.calculator.inverse.tangent.function";
/* 19 */     this.fshortcut = 't';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 28 */     return Math.atan(x) * this.iscale;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 37 */     if ((x instanceof Complex)) {
/* 38 */       Complex c = (Complex)x;
/* 39 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.imaginary()) > 1.0E-006D))
/* 40 */         throw new RuntimeException("Error");
/* 41 */       return c.atan().scale(this.iscale);
/*    */     }
/* 43 */     return x.atan(this.angleType);
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 48 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.ATan
 * JD-Core Version:    0.6.0
 */