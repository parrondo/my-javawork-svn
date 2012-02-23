/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class Root extends DFunction
/*    */ {
/* 53 */   private static final String[] fname = { "<sup><i>x</i></sup>", "&#8730;" };
/*    */ 
/*    */   public Root()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.y.th.root.of.x";
/* 15 */     this.fshortcut = '^';
/*    */   }
/*    */ 
/*    */   public double function(double x, double y)
/*    */   {
/* 25 */     if (y >= 0.0D) {
/* 26 */       return Math.pow(y, 1.0D / x);
/*    */     }
/* 28 */     return 1.0D / Math.pow(y, 1.0D / -x);
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x, OObject y)
/*    */   {
/* 38 */     return y.root(x);
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 42 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 46 */     Root o = new Root();
/* 47 */     StringBuilder s = new StringBuilder("<html>");
/* 48 */     s.append(o.name());
/* 49 */     s.append("</html>");
/* 50 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Root
 * JD-Core Version:    0.6.0
 */