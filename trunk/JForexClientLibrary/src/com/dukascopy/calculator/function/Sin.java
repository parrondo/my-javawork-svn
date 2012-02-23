/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ 
/*    */ public class Sin extends Trig
/*    */ {
/* 50 */   private static final String[] fname = { "s", "i", "n", " " };
/*    */ 
/*    */   public Sin(AngleType angleType)
/*    */   {
/* 16 */     super(angleType);
/* 17 */     this.ftooltip = "sc.calculator.sine.function";
/* 18 */     this.fshortcut = 's';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 27 */     return Math.sin(x * this.scale);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 36 */     if ((x instanceof Complex)) {
/* 37 */       Complex c = (Complex)x;
/* 38 */       if ((this.scale != 1.0D) && (Math.abs(c.imaginary()) > 1.0E-006D))
/* 39 */         throw new RuntimeException("Error");
/* 40 */       return c.scale(this.scale).sin();
/*    */     }
/* 42 */     return x.sin(this.angleType);
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 47 */     return fname;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Sin
 * JD-Core Version:    0.6.0
 */