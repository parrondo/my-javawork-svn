/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.function.BoolFunction;
/*    */ 
/*    */ public class And extends Dyadic<BoolFunction>
/*    */ {
/*    */   public And(OObject expression1, OObject expression2)
/*    */   {
/*  7 */     super(new com.dukascopy.calculator.function.And(), expression1, expression2);
/*    */   }
/*    */ 
/*    */   public Product negate() {
/* 11 */     Product p = new Product(this, false);
/* 12 */     return p.negate();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.And
 * JD-Core Version:    0.6.0
 */