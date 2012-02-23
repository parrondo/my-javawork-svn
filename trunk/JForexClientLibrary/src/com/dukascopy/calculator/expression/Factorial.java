/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.Base;
/*    */ import com.dukascopy.calculator.Notation;
/*    */ import com.dukascopy.calculator.StringArray;
/*    */ import com.dukascopy.calculator.function.SFunction;
/*    */ 
/*    */ public class Factorial extends Monadic
/*    */ {
/*    */   public Factorial(Expression expression)
/*    */   {
/* 12 */     super(new com.dukascopy.calculator.function.Factorial(), expression);
/*    */   }
/*    */ 
/*    */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*    */   {
/* 18 */     StringArray s = new StringArray();
/* 19 */     s.addAll(this.expression.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*    */ 
/* 21 */     s.add(this.function.name_array());
/* 22 */     return s;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Factorial
 * JD-Core Version:    0.6.0
 */