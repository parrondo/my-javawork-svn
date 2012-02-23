/*    */ package com.dukascopy.calculator.expression;
/*    */ 
/*    */ import com.dukascopy.calculator.OObject;
/*    */ import com.dukascopy.calculator.complex.Complex;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedList;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ public abstract class SumOrProduct extends Expression
/*    */ {
/*    */   protected Complex complex;
/*    */   protected LinkedList<Expression> expressionList;
/*    */ 
/*    */   public final Complex getComplex()
/*    */   {
/* 18 */     return this.complex;
/*    */   }
/*    */ 
/*    */   public final LinkedList<Expression> getExpressionList()
/*    */   {
/* 26 */     return this.expressionList;
/*    */   }
/*    */ 
/*    */   public boolean isZero() {
/* 30 */     if (!this.complex.isZero()) return false;
/* 31 */     if (this.expressionList.isEmpty()) {
/* 32 */       return true;
/*    */     }
/* 34 */     ListIterator i = this.expressionList.listIterator();
/* 35 */     while (i.hasNext()) {
/* 36 */       if (!((Expression)i.next()).isZero()) return false;
/*    */     }
/*    */ 
/* 39 */     return true;
/*    */   }
/*    */ 
/*    */   public void sort()
/*    */   {
/* 46 */     for (Expression e : this.expressionList)
/* 47 */       e.sort();
/* 48 */     Collections.sort(this.expressionList);
/*    */   }
/*    */ 
/*    */   public OObject auto_simplify()
/*    */   {
/* 56 */     return this;
/*    */   }
/*    */ 
/*    */   public static int compare(LinkedList<Expression> expressionList1, LinkedList<Expression> expressionList2)
/*    */   {
/* 70 */     ListIterator i = expressionList1.listIterator();
/* 71 */     ListIterator j = expressionList2.listIterator();
/* 72 */     while ((i.hasNext()) && (j.hasNext())) {
/* 73 */       Expression e = (Expression)i.next();
/* 74 */       Expression f = (Expression)j.next();
/* 75 */       int r = e.compareTo(f);
/* 76 */       if (r != 0) return r;
/*    */     }
/*    */ 
/* 79 */     if (i.hasNext())
/* 80 */       return -1;
/* 81 */     if (j.hasNext()) {
/* 82 */       return 1;
/*    */     }
/* 84 */     return 0;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.SumOrProduct
 * JD-Core Version:    0.6.0
 */