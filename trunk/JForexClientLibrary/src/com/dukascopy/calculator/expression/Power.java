/*     */ package com.dukascopy.calculator.expression;
/*     */ 
/*     */ import com.dukascopy.calculator.Base;
/*     */ import com.dukascopy.calculator.Error;
/*     */ import com.dukascopy.calculator.Notation;
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.StringArray;
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import com.dukascopy.calculator.function.DFunction;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Power extends Dyadic<DFunction>
/*     */ {
/*     */   public Power(OObject expression1, OObject expression2)
/*     */   {
/*  15 */     super(new com.dukascopy.calculator.function.Power(), expression1, expression2);
/*     */   }
/*     */ 
/*     */   public Product negate() {
/*  19 */     Product p = new Product(this, false);
/*  20 */     return p.negate();
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/*  38 */     StringArray s = new StringArray();
/*  39 */     s.addAll(this.expression1.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*     */ 
/*  42 */     StringArray t = this.expression2.toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/*  45 */     int l = ((Vector)t.lastElement()).size();
/*  46 */     if (l > 0) {
/*  47 */       String sup = "<sup>";
/*  48 */       ((Vector)t.firstElement()).set(0, sup.concat((String)((Vector)t.firstElement()).firstElement()));
/*  49 */       ((Vector)t.lastElement()).set(l - 1, ((String)((Vector)t.lastElement()).lastElement()).concat("</sup>"));
/*     */     }
/*     */ 
/*  52 */     s.addAll(t);
/*  53 */     return s;
/*     */   }
/*     */ 
/*     */   public OObject auto_simplify()
/*     */   {
/*  61 */     this.expression1 = this.expression1.auto_simplify();
/*  62 */     this.expression2 = this.expression2.auto_simplify();
/*  63 */     if (((this.expression1 instanceof Error)) || ((this.expression2 instanceof Error)))
/*     */     {
/*  65 */       return new Error("Power error");
/*     */     }
/*  67 */     if ((this.expression1 instanceof Complex)) {
/*  68 */       Complex v = (Complex)this.expression1;
/*  69 */       if ((this.expression2 instanceof Complex)) {
/*  70 */         Complex w = (Complex)this.expression2;
/*  71 */         return v.pow(w);
/*     */       }
/*  73 */       Long l = v.isInteger();
/*  74 */       if (l != null) {
/*  75 */         long i = l.longValue();
/*  76 */         if (i == 0L)
/*  77 */           return new Error("Power error");
/*  78 */         if (i == 1L) {
/*  79 */           return new Complex(1.0D);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  85 */     if ((this.expression2 instanceof Complex)) {
/*  86 */       Complex w = (Complex)this.expression2;
/*  87 */       Long m = w.isInteger();
/*  88 */       if (m != null) {
/*  89 */         long n = m.longValue();
/*     */ 
/*  92 */         if (n == 0L) return new Complex(1.0D);
/*  93 */         if (n == 1L) return this.expression1;
/*  94 */         if ((this.expression1 instanceof Power)) {
/*  95 */           Power q = (Power)this.expression1;
/*  96 */           OObject o = null;
/*  97 */           if ((q.expression2 instanceof Expression)) {
/*  98 */             Product p = new Product((Expression)(Expression)q.expression2, false);
/*  99 */             o = p.multiply(new Complex(n));
/* 100 */           } else if ((q.expression2 instanceof Complex)) {
/* 101 */             o = q.expression2.multiply(new Complex(n));
/*     */           } else {
/* 103 */             return new Error("Power error");
/* 104 */           }return new Power(q.expression1, o);
/* 105 */         }if ((this.expression1 instanceof Product)) {
/* 106 */           Product p = (Product)this.expression1;
/* 107 */           return p.integer_power(n).auto_simplify();
/*     */         }
/*     */       }
/*     */     }
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */   public int compareTo(Power power)
/*     */   {
/* 120 */     if ((this.expression1 instanceof Error)) {
/* 121 */       if ((power.expression1 instanceof Error)) {
/* 122 */         return 0;
/*     */       }
/* 124 */       return -1;
/*     */     }
/* 126 */     if ((power.expression1 instanceof Error))
/* 127 */       return 1;
/* 128 */     if ((this.expression1 instanceof Expression)) {
/* 129 */       if ((power.expression1 instanceof Expression)) {
/* 130 */         int compare = ((Expression)this.expression1).compareTo((Expression)power.expression1);
/*     */ 
/* 132 */         if (compare == 0) {
/* 133 */           if ((this.expression2 instanceof Error)) {
/* 134 */             if ((power.expression2 instanceof Error)) {
/* 135 */               return 0;
/*     */             }
/* 137 */             return -1;
/*     */           }
/* 139 */           if ((power.expression2 instanceof Error))
/* 140 */             return 1;
/* 141 */           if ((this.expression2 instanceof Expression)) {
/* 142 */             if ((power.expression2 instanceof Expression)) {
/* 143 */               compare = ((Expression)this.expression2).compareTo((Expression)power.expression2);
/*     */             }
/*     */             else {
/* 146 */               compare = -1;
/*     */             }
/*     */           }
/* 149 */           else if ((power.expression2 instanceof Expression))
/* 150 */             compare = 1;
/*     */           else {
/* 152 */             compare = -((Complex)this.expression2).compareTo((Complex)power.expression2);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 157 */         return compare;
/*     */       }
/* 159 */       return -1;
/*     */     }
/*     */ 
/* 163 */     if ((power.expression1 instanceof Expression)) {
/* 164 */       return 1;
/*     */     }
/* 166 */     return ((Complex)this.expression1).compareTo((Complex)power.expression1);
/*     */   }
/*     */ 
/*     */   OObject base()
/*     */   {
/* 177 */     return this.expression1;
/*     */   }
/*     */ 
/*     */   OObject exponent()
/*     */   {
/* 185 */     return this.expression2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Power
 * JD-Core Version:    0.6.0
 */