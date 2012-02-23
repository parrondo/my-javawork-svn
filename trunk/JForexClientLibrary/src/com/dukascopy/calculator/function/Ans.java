/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class Ans extends Container
/*    */ {
/* 51 */   private static final String[] fname = { "A", "N", "S" };
/*    */ 
/*    */   public Ans()
/*    */   {
/* 19 */     this.d = new Double(0.0D).doubleValue();
/* 20 */     this.c = new Complex();
/*    */   }
/*    */ 
/*    */   public void setValue(double d)
/*    */   {
/* 28 */     this.d = d;
/*    */   }
/*    */ 
/*    */   public void setValue(OObject c)
/*    */   {
/* 36 */     this.c = c;
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 40 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 44 */     Ans o = new Ans();
/* 45 */     StringBuilder s = new StringBuilder("<html>");
/* 46 */     s.append(o.name());
/* 47 */     s.append("</html>");
/* 48 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Ans
 * JD-Core Version:    0.6.0
 */