/*   */ package com.dukascopy.calculator.expression;
/*   */ 
/*   */ import com.dukascopy.calculator.AngleType;
/*   */ 
/*   */ public class ACos extends Monadic
/*   */ {
/*   */   public ACos(Expression expression, AngleType angleType)
/*   */   {
/* 7 */     super(new com.dukascopy.calculator.function.ACos(angleType), expression);
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.ACos
 * JD-Core Version:    0.6.0
 */