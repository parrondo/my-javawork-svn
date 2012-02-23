/*   */ package com.dukascopy.calculator.expression;
/*   */ 
/*   */ import com.dukascopy.calculator.AngleType;
/*   */ 
/*   */ public class Cos extends Monadic
/*   */ {
/*   */   public Cos(Expression expression, AngleType angleType)
/*   */   {
/* 7 */     super(new com.dukascopy.calculator.function.Cos(angleType), expression);
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Cos
 * JD-Core Version:    0.6.0
 */