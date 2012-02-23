/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class Cube extends LFunction
/*    */ {
/* 52 */   private static final String[] fname = { "<sup>3</sup>" };
/*    */ 
/*    */   public Cube()
/*    */   {
/* 14 */     this.ftooltip = "sc.calculator.cube.of.x";
/* 15 */     this.fshortcut = 'u';
/*    */   }
/*    */ 
/*    */   public double function(double x)
/*    */   {
/* 24 */     return x * x * x;
/*    */   }
/*    */ 
/*    */   public OObject function(OObject x)
/*    */   {
/* 33 */     return x.cube();
/*    */   }
/*    */ 
/*    */   public String shortName() {
/* 37 */     return "<i>x</i><sup>3</sup>";
/*    */   }
/*    */ 
/*    */   public String[] name_array() {
/* 41 */     return fname;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 45 */     PObject o = new Cube();
/* 46 */     StringBuilder s = new StringBuilder("<html>");
/* 47 */     s.append(o.name());
/* 48 */     s.append("</html>");
/* 49 */     JOptionPane.showMessageDialog(null, s.toString());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Cube
 * JD-Core Version:    0.6.0
 */