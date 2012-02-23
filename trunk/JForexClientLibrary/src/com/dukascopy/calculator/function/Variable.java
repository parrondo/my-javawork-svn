/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ public class Variable extends PObject
/*    */ {
/*    */   private final char c;
/*    */ 
/*    */   public Variable(char c)
/*    */   {
/* 16 */     this.c = c;
/* 17 */     this.ftooltip = "";
/* 18 */     this.fshortcut = c;
/*    */   }
/*    */ 
/*    */   public char get()
/*    */   {
/* 26 */     return this.c;
/*    */   }
/*    */ 
/*    */   public String name()
/*    */   {
/* 34 */     return Character.toString(get());
/*    */   }
/*    */ 
/*    */   public String[] name_array()
/*    */   {
/* 42 */     String[] string = new String[2];
/* 43 */     string[0] = ("<i>" + Character.toString(this.c));
/* 44 */     string[1] = "</i><font color=\"white\" size=\"-1\">.</font>";
/* 45 */     return string;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Variable
 * JD-Core Version:    0.6.0
 */