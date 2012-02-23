/*    */ package com.dukascopy.calculator.function;
/*    */ 
/*    */ public class Numeral extends PObject
/*    */ {
/*    */   private final char c;
/*    */ 
/*    */   public Numeral(char c)
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
/* 42 */     String[] string = new String[1];
/* 43 */     string[0] = Character.toString(this.c);
/* 44 */     return string;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.function.Numeral
 * JD-Core Version:    0.6.0
 */