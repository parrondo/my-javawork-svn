/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class Info extends PObject
/*    */ {
/* 20 */   private static final String[] fname = { "About" };
/*    */ 
/*    */   public Info()
/*    */   {
/*  7 */     this.ftooltip = "sc.calculator.about";
/*  8 */     this.fshortcut = '@';
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 12 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 16 */     Info o = new Info();
/* 17 */     JOptionPane.showMessageDialog(null, o.name());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Info
 * JD-Core Version:    0.6.0
 */