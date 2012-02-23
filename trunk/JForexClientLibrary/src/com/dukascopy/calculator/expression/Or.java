/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.function.BoolFunction;
/*    */ 
/*    */ public class Or extends Dyadic<BoolFunction>
/*    */ {
/*    */   public Or(OObject expression1, OObject expression2)
/*    */   {
/* 12 */     super(new com.dukascopy.calculator.function.Or(), expression1, expression2);
/*    */   }
/*    */ 
/*    */   public Product negate() {
/* 16 */     Product p = new Product(this, false);
/* 17 */     return p.negate();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Or
 * JD-Core Version:    0.6.0
 */