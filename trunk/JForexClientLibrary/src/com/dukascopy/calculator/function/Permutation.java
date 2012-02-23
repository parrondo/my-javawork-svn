/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class Permutation extends DFunction
/*    */ {
/* 63 */   private static final String[] fname = { "<b>P</b>" };
/*    */ 
/*    */   public Permutation()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.permutation.function.tooltip";
/* 15 */     this.fshortcut = 'P';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 26 */     if ((x < 0.0D) || (Math.round(x) - x != 0.0D))
/* 27 */       throw new ArithmeticException("Permutation error");
/* 28 */     if ((y < 0.0D) || (y > x) || (Math.round(y) - y != 0.0D))
/* 29 */       throw new ArithmeticException("Permutation error");
/* 30 */     if (y == 0.0D) {
/* 31 */       return 1.0D;
/*    */     }
/* 33 */     return x * function(x - 1.0D, y - 1.0D);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 44 */     return x.permutation(y);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 48 */     return fname;
/*    */   }
/*    */ 
/*    */   public String shortName() {
/* 52 */     return "<i>n</i>P<i>r</i>";
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 56 */     Permutation o = new Permutation();
/* 57 */     StringBuilder s = new StringBuilder("<html>");
/* 58 */     s.append(o.name());
/* 59 */     s.append("</html>");
/* 60 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Permutation
 * JD-Core Version:    0.6.0
 */