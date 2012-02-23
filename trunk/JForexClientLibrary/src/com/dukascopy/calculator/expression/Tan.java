/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.AngleType;
/*    */ 
/*    */ public class Tan extends Monadic
/*    */ {
/*    */   public Tan(Expression expression, AngleType angleType)
/*    */   {
/* 12 */     super(new com.dukascopy.calculator.function.Tan(angleType), expression);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Tan
 * JD-Core Version:    0.6.0
 */