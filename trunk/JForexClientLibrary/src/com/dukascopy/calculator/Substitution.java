/*    */ package com.dukascopy.calculator;
/*    */ 
/*    */ import com.dukascopy.calculator.expression.Variable;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public class Substitution
/*    */ {
/*    */   private LinkedList<Pair> substitutions;
/*    */ 
/*    */   public Substitution()
/*    */   {
/*  5 */     this.substitutions = new LinkedList();
/*    */   }
/*    */   public void add(Variable variable, OObject oobject) {
/*  8 */     if (variable == null) return;
/*  9 */     for (Iterator i = this.substitutions.iterator(); i.hasNext(); ) {
/* 10 */       Pair pair = (Pair)i.next();
/* 11 */       if (pair.variable.name().equals(variable.name()))
/*    */       {
/* 13 */         if (oobject == null)
/* 14 */           i.remove();
/*    */         else {
/* 16 */           pair.oobject = oobject;
/*    */         }
/* 18 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 22 */     Pair pair = new Pair();
/* 23 */     pair.variable = variable;
/* 24 */     pair.oobject = oobject;
/* 25 */     this.substitutions.add(pair);
/*    */   }
/*    */ 
/*    */   public final LinkedList<Pair> getSubstitutions() {
/* 29 */     return this.substitutions;
/*    */   }
/*    */ 
/*    */   public class Pair
/*    */   {
/*    */     public Variable variable;
/*    */     public OObject oobject;
/*    */ 
/*    */     public Pair()
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.Substitution
 * JD-Core Version:    0.6.0
 */