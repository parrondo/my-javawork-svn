/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class ASin extends Trig
/*    */ {
/* 53 */   private static final String[] fname = { "s", "i", "n", "<sup>&#8722;</sup>", "<sup>1</sup>", " " };
/*    */ 
/*    */   public ASin(AngleType angleType)
/*    */   {
/* 17 */     super(angleType);
/* 18 */     this.ftooltip = "sc.calculator.inverse.sine.function";
/* 19 */     this.fshortcut = 's';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 28 */     return Math.asin(x) * this.iscale;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 37 */     if ((x instanceof Complex)) {
/* 38 */       Complex c = (Complex)x;
/* 39 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.imaginary()) > 1.0E-006D))
/* 40 */         throw new RuntimeException("Error");
/* 41 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.real()) > 1.0D))
/* 42 */         throw new RuntimeException("Error");
/* 43 */       return c.asin().scale(this.iscale);
/*    */     }
/* 45 */     return x.asin(this.angleType);
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 50 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.ASin
 * JD-Core Version:    0.6.0
 */