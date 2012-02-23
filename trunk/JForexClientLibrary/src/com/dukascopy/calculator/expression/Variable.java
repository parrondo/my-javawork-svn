/*     */ package com.dukascopy.calculator.expression;
/*     */ 
/*     */ import com.dukascopy.calculator.Base;
/*     */ import com.dukascopy.calculator.Notation;
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.StringArray;
/*     */ import com.dukascopy.calculator.Substitution;
/*     */ import com.dukascopy.calculator.Substitution.Pair;
/*     */ import com.dukascopy.calculator.function.PObject;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class Variable extends Expression
/*     */ {
/*     */   private com.dukascopy.calculator.function.Variable variable;
/*     */ 
/*     */   public Variable(com.dukascopy.calculator.function.Variable variable)
/*     */   {
/*  13 */     this.variable = variable;
/*     */   }
/*     */ 
/*     */   public PObject pObject()
/*     */   {
/*  21 */     return this.variable;
/*     */   }
/*     */ 
/*     */   public char get()
/*     */   {
/*  29 */     return this.variable.get();
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/*  37 */     return this.variable.name();
/*     */   }
/*     */ 
/*     */   public String[] name_array()
/*     */   {
/*  45 */     return this.variable.name_array();
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/*  63 */     StringArray r = new StringArray();
/*  64 */     r.add(name_array());
/*  65 */     return r;
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLParenString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/*  71 */     return toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/*     */   }
/*     */ 
/*     */   public Product negate() {
/*  75 */     Product p = new Product(this, false);
/*  76 */     return p.negate();
/*     */   }
/*     */ 
/*     */   public int compareTo(Variable variable)
/*     */   {
/*  85 */     return get() == variable.get() ? 0 : get() < variable.get() ? -1 : 1;
/*     */   }
/*     */ 
/*     */   public OObject substitute(Substitution substitution)
/*     */   {
/*  97 */     for (Iterator i = substitution.getSubstitutions().iterator(); i.hasNext(); ) {
/*  98 */       Substitution.Pair pair = (Substitution.Pair)i.next();
/*  99 */       if (pair.variable.name().equals(name()))
/*     */       {
/* 101 */         return pair.oobject;
/*     */       }
/*     */     }
/*     */ 
/* 105 */     return this;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Variable
 * JD-Core Version:    0.6.0
 */