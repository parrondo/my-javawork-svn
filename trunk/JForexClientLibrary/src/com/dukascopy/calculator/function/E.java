/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class E extends DFunction
/*    */ {
/*    */   private int base;
/* 74 */   private static final String[] fname = { "e" };
/*    */ 
/*    */   public E()
/*    */   {
/* 18 */     this.ftooltip = "sc.calculator.e.number.tooltip";
/* 19 */     this.fshortcut = 'e';
/* 20 */     base(10);
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 30 */     return x * Math.exp(y * Math.log(this.base));
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 40 */     return x.multiply(y.multiply(new Complex(this.base, 0.0D).log()).exp());
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 44 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 48 */     E o = new E();
/* 49 */     StringBuilder s = new StringBuilder("<html>");
/* 50 */     s.append(o.name());
/* 51 */     s.append("</html>");
/* 52 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ 
/*    */   public int base()
/*    */   {
/* 60 */     return this.base;
/*    */   }
/*    */ 
/*    */   public void base(int base)
/*    */   {
/* 68 */     this.base = base;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.E
 * JD-Core Version:    0.6.0
 */