/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class LParen extends PObject
/*    */ {
/* 28 */   private static final String[] fname = { "(" };
/*    */ 
/*    */   public LParen()
/*    */   {
/* 12 */     this.ftooltip = "sc.calculator.left.parenthesis.bracket";
/* 13 */     this.fshortcut = '(';
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 17 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 21 */     PObject o = new LParen();
/* 22 */     StringBuilder s = new StringBuilder("<html>");
/* 23 */     s.append(o.name());
/* 24 */     s.append("</html>");
/* 25 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.LParen
 * JD-Core Version:    0.6.0
 */