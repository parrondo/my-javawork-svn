/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import javax.swing.text.NavigationFilter;
/*     */ import javax.swing.text.NavigationFilter.FilterBypass;
/*     */ import javax.swing.text.Position.Bias;
/*     */ 
/*     */ abstract class Navigator extends NavigationFilter
/*     */ {
/*     */   protected Vector<Integer> dots;
/* 128 */   protected final int MAX = 34;
/*     */ 
/*     */   public Navigator()
/*     */   {
/*  16 */     this.dots = new Vector();
/*  17 */     this.dots.add(Integer.valueOf(1));
/*     */   }
/*     */ 
/*     */   public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
/*     */   {
/*  29 */     setDot(fb, dot, bias);
/*     */   }
/*     */ 
/*     */   public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
/*     */   {
/*  42 */     if ((this.dots.size() == 1) && (((Integer)this.dots.firstElement()).intValue() == 1) && ((dot > 34) || (dot == 1))) {
/*  43 */       fb.setDot(dot, bias);
/*  44 */       return;
/*     */     }
/*     */ 
/*  47 */     int l = 0;
/*  48 */     ListIterator i = this.dots.listIterator(this.dots.size());
/*  49 */     while (i.hasPrevious()) {
/*  50 */       l = ((Integer)i.previous()).intValue();
/*  51 */       if (l > dot) continue;
/*     */     }
/*  53 */     int u = 0;
/*  54 */     for (Integer i : this.dots) {
/*  55 */       u = i.intValue();
/*  56 */       if (u >= dot) break;
/*     */     }
/*  58 */     dot = dot - l > u - dot ? u : l;
/*  59 */     fb.setDot(dot, bias);
/*     */   }
/*     */ 
/*     */   public int getNextVisualPositionFrom(JTextComponent text, int pos, Position.Bias bias, int direction, Position.Bias[] biasRet)
/*     */     throws BadLocationException
/*     */   {
/*  82 */     int p = super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet);
/*  83 */     if (p < 1)
/*  84 */       return 1;
/*  85 */     if (p > ((Integer)this.dots.lastElement()).intValue())
/*  86 */       return ((Integer)this.dots.lastElement()).intValue();
/*  87 */     if (direction == 7)
/*     */     {
/*  89 */       ListIterator i = this.dots.listIterator(this.dots.size());
/*  90 */       while (i.hasPrevious()) {
/*  91 */         int q = ((Integer)i.previous()).intValue();
/*  92 */         if (q <= p)
/*  93 */           return q;
/*     */       }
/*  95 */     } else if (direction == 3)
/*     */     {
/*  97 */       for (Integer q : this.dots) {
/*  98 */         if (q.intValue() >= p) {
/*  99 */           return q.intValue();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 104 */     return p;
/*     */   }
/*     */ 
/*     */   public Vector<Integer> dots()
/*     */   {
/* 112 */     return this.dots;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.Navigator
 * JD-Core Version:    0.6.0
 */