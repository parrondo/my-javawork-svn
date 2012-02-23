/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.function.DFunction;
/*    */ 
/*    */ public class Permutation extends Dyadic<DFunction>
/*    */ {
/*    */   public Permutation(OObject expression1, OObject expression2)
/*    */   {
/* 12 */     super(new com.dukascopy.calculator.function.Permutation(), expression1, expression2);
/*    */   }
/*    */ 
/*    */   public Product negate() {
/* 16 */     Product p = new Product(this, false);
/* 17 */     return p.negate();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Permutation
 * JD-Core Version:    0.6.0
 */