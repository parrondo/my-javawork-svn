/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Notation
/*     */ {
/*     */   private int value;
/*     */   public static final int SCIENTIFIC = 1;
/*     */   public static final int POLAR = 2;
/*     */   public static final int COMPLEX = 4;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  12 */     Notation t = new Notation();
/*  13 */     System.out.println(new StringBuilder().append("t is ").append(t.scientific() ? "scientific" : "standard").toString());
/*  14 */     System.out.println(new StringBuilder().append("t is ").append(t.standard() ? "standard" : "scientific").toString());
/*  15 */     t.setScientific();
/*  16 */     System.out.println(new StringBuilder().append("t is ").append(t.standard() ? "standard" : "scientific").toString());
/*  17 */     t.setStandard();
/*  18 */     System.out.println(new StringBuilder().append("t is ").append(t.standard() ? "standard" : "scientific").toString());
/*  19 */     t.toggle(1);
/*  20 */     System.out.println(new StringBuilder().append("t is ").append(t.standard() ? "standard" : "scientific").toString());
/*     */   }
/*     */ 
/*     */   public Notation()
/*     */   {
/*  26 */     this.value = 0;
/*     */   }
/*     */ 
/*     */   public boolean scientific()
/*     */   {
/*  34 */     return !standard();
/*     */   }
/*     */ 
/*     */   public boolean standard()
/*     */   {
/*  41 */     return (this.value & 0x1) == 0;
/*     */   }
/*     */ 
/*     */   public void setScientific()
/*     */   {
/*  47 */     this.value |= 1;
/*     */   }
/*     */ 
/*     */   public void setStandard()
/*     */   {
/*  53 */     this.value &= -2;
/*     */   }
/*     */ 
/*     */   public boolean polar()
/*     */   {
/*  60 */     return !rectangular();
/*     */   }
/*     */ 
/*     */   public boolean rectangular()
/*     */   {
/*  67 */     return (this.value & 0x2) == 0;
/*     */   }
/*     */ 
/*     */   public void setPolar()
/*     */   {
/*  73 */     this.value |= 2;
/*     */   }
/*     */ 
/*     */   public void setRectangular()
/*     */   {
/*  79 */     this.value &= -3;
/*     */   }
/*     */ 
/*     */   public boolean complex()
/*     */   {
/*  86 */     return !nonComplex();
/*     */   }
/*     */ 
/*     */   public boolean nonComplex()
/*     */   {
/*  93 */     return (this.value & 0x4) == 0;
/*     */   }
/*     */ 
/*     */   public void setComplex()
/*     */   {
/*  99 */     this.value |= 4;
/*     */   }
/*     */ 
/*     */   public void setNonComplex()
/*     */   {
/* 105 */     this.value &= -5;
/*     */   }
/*     */ 
/*     */   public void toggle(int v)
/*     */   {
/* 113 */     switch (v) {
/*     */     case 1:
/*     */     case 2:
/*     */     case 4:
/* 117 */       this.value ^= v;
/* 118 */       return;
/*     */     case 3:
/* 120 */     }System.out.println("Warning: unknown toggle");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.Notation
 * JD-Core Version:    0.6.0
 */