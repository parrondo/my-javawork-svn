/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ 
/*    */ public class Sin extends Monadic
/*    */ {
/*    */   public Sin(Expression expression, AngleType angleType)
/*    */   {
/* 12 */     super(new com.dukascopy.calculator.function.Sin(angleType), expression);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Sin
 * JD-Core Version:    0.6.0
 */