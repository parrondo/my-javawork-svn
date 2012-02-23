/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.Base;
/*    */ import com.dukascopy.calculator.Error;
/*    */ import com.dukascopy.calculator.Notation;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.StringArray;
/*    */ import com.dukascopy.calculator.Substitution;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import com.dukascopy.calculator.function.SFunction;
/*    */ 
/*    */ public abstract class Monadic extends Expression
/*    */ {
/*    */   protected final SFunction function;
/*    */   protected Expression expression;
/*    */ 
/*    */   public Monadic(SFunction function, Expression expression)
/*    */   {
/* 13 */     this.function = function;
/* 14 */     this.expression = expression;
/*    */   }
/*    */ 
/*    */   public Product negate() {
/* 18 */     Product p = new Product(this, false);
/* 19 */     return p.negate();
/*    */   }
/*    */ 
/*    */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*    */   {
/* 36 */     StringArray s = new StringArray();
/* 37 */     s.add(this.function.name_array());
/* 38 */     s.addAll(this.expression.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*    */ 
/* 40 */     return s;
/*    */   }
/*    */ 
/*    */   public OObject auto_simplify()
/*    */   {
/* 47 */     OObject o = this.expression.auto_simplify();
/* 48 */     if ((o instanceof Complex))
/* 49 */       return this.function.function((Complex)o);
/* 50 */     if ((o instanceof Expression)) {
/* 51 */       return this;
/*    */     }
/* 53 */     return new Error("Function.auto_simplify() error");
/*    */   }
/*    */ 
/*    */   public OObject substitute(Substitution substitution) {
/* 57 */     return this.function.function(this.expression.substitute(substitution));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Monadic
 * JD-Core Version:    0.6.0
 */