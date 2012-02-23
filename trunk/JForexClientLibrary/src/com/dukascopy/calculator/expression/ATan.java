/*   */ package com.dukascopy.calculator.expression;
/*   */ 
/*   */ import com.dukascopy.calculator.AngleType;
/*   */ 
/*   */ public class ATan extends Monadic
/*   */ {
/*   */   public ATan(Expression expression, AngleType angleType)
/*   */   {
/* 7 */     super(new com.dukascopy.calculator.function.ATan(angleType), expression);
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.ATan
 * JD-Core Version:    0.6.0
 */