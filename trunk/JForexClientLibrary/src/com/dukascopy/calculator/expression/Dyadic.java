/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.Base;
/*    */ import com.dukascopy.calculator.Error;
/*    */ import com.dukascopy.calculator.Notation;
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.StringArray;
/*    */ import com.dukascopy.calculator.Substitution;
/*    */ import com.dukascopy.calculator.function.DFunction;
/*    */ import com.dukascopy.calculator.function.PObject;
/*    */ 
/*    */ public abstract class Dyadic<E extends PObject> extends Expression
/*    */ {
/*    */   protected E function;
/*    */   protected OObject expression1;
/*    */   protected OObject expression2;
/*    */ 
/*    */   public Dyadic(E function, OObject expression1, OObject expression2)
/*    */   {
/*  9 */     this.function = function;
/* 10 */     this.expression1 = expression1;
/* 11 */     this.expression2 = expression2;
/*    */   }
/*    */ 
/*    */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*    */   {
/* 28 */     StringArray s = new StringArray();
/* 29 */     s.addAll(this.expression1.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*    */ 
/* 31 */     s.add(this.function.name_array());
/* 32 */     s.addAll(this.expression2.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*    */ 
/* 34 */     return s;
/*    */   }
/*    */ 
/*    */   public OObject auto_simplify()
/*    */   {
/* 42 */     this.expression1 = this.expression1.auto_simplify();
/* 43 */     this.expression2 = this.expression2.auto_simplify();
/* 44 */     if (((this.expression1 instanceof Error)) || ((this.expression1 instanceof Error)))
/*    */     {
/* 46 */       return new Error("Function auto_simplify() error");
/*    */     }
/* 48 */     return this;
/*    */   }
/*    */ 
/*    */   public OObject substitute(Substitution substitution) {
/* 52 */     if ((this.function instanceof DFunction)) {
/* 53 */       DFunction d = (DFunction)this.function;
/* 54 */       return d.function(this.expression1.substitute(substitution), this.expression2.substitute(substitution));
/*    */     }
/*    */ 
/* 57 */     return new Error("Dyadic.substitute Error");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Dyadic
 * JD-Core Version:    0.6.0
 */