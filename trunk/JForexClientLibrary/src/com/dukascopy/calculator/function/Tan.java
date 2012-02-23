/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class Tan extends Trig
/*    */ {
/* 58 */   private static final String[] fname = { "t", "a", "n", " " };
/*    */ 
/*    */   public Tan(AngleType angleType)
/*    */   {
/* 16 */     super(angleType);
/* 17 */     this.ftooltip = "sc.calculator.tangent.function";
/* 18 */     this.fshortcut = 't';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 27 */     return Math.tan(x * this.scale);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 36 */     if ((x instanceof Complex)) {
/* 37 */       Complex c = (Complex)x;
/* 38 */       if ((this.scale != 1.0D) && (StrictMath.abs(c.imaginary()) > 1.0E-006D))
/* 39 */         throw new RuntimeException("Error");
/* 40 */       return c.scale(this.scale).tan();
/*    */     }
/* 42 */     return x.tan(this.angleType);
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 47 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 51 */     PObject o = new Tan(AngleType.DEGREES);
/* 52 */     StringBuilder s = new StringBuilder("<html>");
/* 53 */     s.append(o.name());
/* 54 */     s.append("</html>");
/* 55 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Tan
 * JD-Core Version:    0.6.0
 */