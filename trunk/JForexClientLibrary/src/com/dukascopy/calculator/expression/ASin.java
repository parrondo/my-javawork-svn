/*   */ package com.dukascopy.calculator.expression;
/*   */ 
/*   */ import com.dukascopy.calculator.AngleType;
/*   */ 
/*   */ public class ASin extends Monadic
/*   */ {
/*   */   public ASin(Expression expression, AngleType angleType)
/*   */   {
/* 7 */     super(new com.dukascopy.calculator.function.ASin(angleType), expression);
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.ASin
 * JD-Core Version:    0.6.0
 */