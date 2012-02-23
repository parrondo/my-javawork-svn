/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class Cos extends Trig
/*    */ {
/* 50 */   private static final String[] fname = { "c", "o", "s", " " };
/*    */ 
/*    */   public Cos(AngleType angleType)
/*    */   {
/* 16 */     super(angleType);
/* 17 */     this.ftooltip = "sc.calculator.cosine.function";
/* 18 */     this.fshortcut = 'c';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 27 */     return Math.cos(x * this.scale);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 36 */     if ((x instanceof Complex)) {
/* 37 */       Complex c = (Complex)x;
/* 38 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.imaginary()) > 1.0E-006D))
/* 39 */         throw new RuntimeException("Error");
/* 40 */       return c.scale(this.scale).cos();
/*    */     }
/* 42 */     return x.cos(this.angleType);
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 47 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Cos
 * JD-Core Version:    0.6.0
 */