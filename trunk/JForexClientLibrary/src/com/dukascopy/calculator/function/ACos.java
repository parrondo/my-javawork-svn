/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class ACos extends Trig
/*    */ {
/* 52 */   private static final String[] fname = { "c", "o", "s", "<sup>&#8722;</sup>", "<sup>1</sup>", " " };
/*    */ 
/*    */   public ACos(AngleType angleType)
/*    */   {
/* 17 */     super(angleType);
/* 18 */     this.ftooltip = "sc.calculator.inverse.cosine.function";
/* 19 */     this.fshortcut = 'c';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 28 */     return Math.acos(x) * this.iscale;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 36 */     if ((x instanceof Complex)) {
/* 37 */       Complex c = (Complex)x;
/* 38 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.imaginary()) > 1.0E-006D))
/* 39 */         throw new RuntimeException("Error");
/* 40 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.real()) > 1.0D))
/* 41 */         throw new RuntimeException("Error");
/* 42 */       return c.acos().scale(this.iscale);
/*    */     }
/* 44 */     return x.acos(this.angleType);
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 49 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.ACos
 * JD-Core Version:    0.6.0
 */