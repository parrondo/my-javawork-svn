/*     */ package com.dukascopy.calculator.expression;
/*     */ 
/*     */ import com.dukascopy.calculator.Base;
/*     */ import com.dukascopy.calculator.Error;
/*     */ import com.dukascopy.calculator.Notation;
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.StringArray;
/*     */ import com.dukascopy.calculator.Substitution;
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Sum extends SumOrProduct
/*     */   implements Cloneable
/*     */ {
/*     */   public Sum(Expression expression)
/*     */   {
/*  14 */     if ((expression instanceof Sum)) {
/*  15 */       Sum sum = (Sum)expression;
/*  16 */       this.complex = sum.complex;
/*  17 */       this.expressionList = sum.expressionList;
/*     */     }
/*  19 */     this.complex = new Complex();
/*  20 */     this.expressionList = new LinkedList();
/*  21 */     this.expressionList.add(expression);
/*     */   }
/*     */ 
/*     */   public Sum()
/*     */   {
/*  28 */     this.complex = new Complex();
/*  29 */     this.expressionList = new LinkedList();
/*     */   }
/*     */ 
/*     */   public Sum add(Complex z)
/*     */   {
/*  38 */     Sum sum = clone();
/*  39 */     sum.complex = this.complex.add(z);
/*  40 */     return sum;
/*     */   }
/*     */ 
/*     */   public Sum clone()
/*     */   {
/*  48 */     Sum copy = new Sum();
/*  49 */     copy.complex = new Complex(this.complex.real(), this.complex.imaginary());
/*  50 */     copy.expressionList = new LinkedList();
/*     */ 
/*  52 */     for (ListIterator i = getExpressionList().listIterator(); i.hasNext(); ) {
/*  53 */       copy.expressionList.add(i.next());
/*     */     }
/*  55 */     return copy;
/*     */   }
/*     */ 
/*     */   public boolean isNegative() {
/*  59 */     if (this.complex.isZero()) {
/*  60 */       if (this.expressionList.isEmpty()) return false;
/*  61 */       return ((Expression)this.expressionList.getFirst()).isNegative();
/*     */     }
/*  63 */     return this.complex.isNegative();
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/*  81 */     StringArray s = new StringArray();
/*  82 */     boolean displayPlus = false;
/*     */ 
/*  84 */     ListIterator i = this.expressionList.listIterator();
/*  85 */     while (i.hasNext()) {
/*  86 */       Expression expression = (Expression)i.next();
/*     */ 
/*  88 */       StringArray u = expression.toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/*  90 */       if ((displayPlus) && ((!expression.isNegative()) || ((u.size() > 0) && (((String)((Vector)u.firstElement()).firstElement()).equals("(")))))
/*     */       {
/*  93 */         s.add("+");
/*     */       }
/*  95 */       s.addAll(u);
/*  96 */       displayPlus = true;
/*     */     }
/*     */ 
/*  99 */     StringArray t = this.complex.toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/* 101 */     if (!t.isZero())
/*     */     {
/* 103 */       if ((displayPlus) && (!this.complex.isNegative())) {
/* 104 */         s.add("+");
/*     */       }
/* 106 */       s.addAll(t);
/*     */     }
/* 108 */     return s;
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLParenString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 114 */     if (getExpressionList().isEmpty()) {
/* 115 */       return this.complex.toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */     }
/* 117 */     if ((this.complex.isZero()) && (getExpressionList().size() == 1)) {
/* 118 */       return ((Expression)getExpressionList().getFirst()).toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */     }
/*     */ 
/* 121 */     StringArray s = new StringArray();
/* 122 */     s.add("(");
/* 123 */     s.addAll(toHTMLSubString(maxChars, precision, base, notation, polarFactor));
/*     */ 
/* 125 */     s.add(")");
/* 126 */     return s;
/*     */   }
/*     */ 
/*     */   public OObject add(Product x)
/*     */   {
/* 137 */     System.out.println("Adding product to sum: FIXME");
/*     */ 
/* 144 */     Sum sum = clone();
/* 145 */     System.out.println("Clone complete: FIXME");
/*     */ 
/* 147 */     OObject o = x.unBox();
/* 148 */     if ((o instanceof Complex))
/* 149 */       sum.complex = sum.complex.add((Complex)o);
/* 150 */     else if ((o instanceof Expression))
/* 151 */       sum.expressionList.add((Expression)o);
/*     */     else
/* 153 */       return new Error("Sum.add() error");
/* 154 */     return sum;
/*     */   }
/*     */ 
/*     */   public OObject add(Sum x)
/*     */   {
/* 164 */     Sum s = clone();
/*     */ 
/* 166 */     s.complex = s.complex.add(x.complex);
/* 167 */     OObject sum = s;
/* 168 */     ListIterator i = x.expressionList.listIterator();
/* 169 */     while (i.hasNext()) {
/* 170 */       sum = sum.add((OObject)i.next());
/*     */     }
/* 172 */     return sum;
/*     */   }
/*     */ 
/*     */   public Sum negate()
/*     */   {
/* 180 */     Sum p = new Sum();
/* 181 */     p.complex = this.complex.negate();
/* 182 */     ListIterator i = this.expressionList.listIterator();
/* 183 */     while (i.hasNext()) {
/* 184 */       p.expressionList.add(((Expression)i.next()).negate());
/*     */     }
/* 186 */     return p;
/*     */   }
/*     */ 
/*     */   private OObject rMultiply(OObject o)
/*     */   {
/* 195 */     if ((!(o instanceof Complex)) && (!(o instanceof Expression)))
/* 196 */       return new Error("Error in Sum.rMultiply");
/* 197 */     Sum s = new Sum();
/* 198 */     OObject p = o.multiply(this.complex);
/* 199 */     if ((p instanceof Complex))
/* 200 */       s.complex = s.complex.add((Complex)p);
/* 201 */     else if ((p instanceof Expression))
/* 202 */       s.expressionList.add((Expression)p);
/*     */     else
/* 204 */       return new Error("Error in Sum.lMultiply");
/* 205 */     ListIterator i = getExpressionList().listIterator();
/* 206 */     while (i.hasNext()) {
/* 207 */       p = ((Expression)i.next()).multiply(o);
/* 208 */       if ((p instanceof Complex)) {
/* 209 */         s.complex = s.complex.add((Complex)p); continue;
/* 210 */       }if ((p instanceof Expression)) {
/* 211 */         s.expressionList.add((Expression)p); continue;
/*     */       }
/* 213 */       return new Error("Error in Sum.rMultiply");
/*     */     }
/* 215 */     return s;
/*     */   }
/*     */ 
/*     */   OObject multiply(Sum t)
/*     */   {
/* 224 */     Sum s = new Sum();
/* 225 */     OObject p = rMultiply(t.complex);
/* 226 */     if ((p instanceof Complex))
/* 227 */       s.complex = s.complex.add((Complex)p);
/* 228 */     else if ((p instanceof Expression))
/* 229 */       s.expressionList.add((Expression)p);
/*     */     else
/* 231 */       return new Error("Error in Sum.multiply");
/* 232 */     ListIterator i = t.getExpressionList().listIterator();
/* 233 */     while (i.hasNext()) {
/* 234 */       Expression e = (Expression)i.next();
/*     */ 
/* 236 */       p = rMultiply(e);
/* 237 */       if ((p instanceof Complex))
/* 238 */         s.complex = s.complex.add((Complex)p);
/* 239 */       else if ((p instanceof Expression))
/* 240 */         s.expressionList.add((Expression)p);
/*     */       else
/* 242 */         return new Error("Error in Sum.multiply");
/*     */     }
/* 244 */     return s;
/*     */   }
/*     */ 
/*     */   public OObject unBox()
/*     */   {
/* 253 */     if ((this.expressionList.size() == 1) && (this.complex.isZero()))
/* 254 */       return (OObject)this.expressionList.getFirst();
/* 255 */     if (this.expressionList.isEmpty()) {
/* 256 */       return this.complex;
/*     */     }
/* 258 */     return this;
/*     */   }
/*     */ 
/*     */   public int compareTo(Sum sum)
/*     */   {
/* 269 */     sort();
/* 270 */     int r = compare(this.expressionList, sum.expressionList);
/* 271 */     if (r != 0) {
/* 272 */       return r;
/*     */     }
/* 274 */     return this.complex.compareTo(sum.complex);
/*     */   }
/*     */ 
/*     */   public OObject auto_simplify()
/*     */   {
/* 283 */     ListIterator i = this.expressionList.listIterator();
/* 284 */     while (i.hasNext()) {
/* 285 */       OObject o = ((Expression)i.next()).auto_simplify();
/* 286 */       if ((o instanceof Complex)) {
/* 287 */         this.complex = this.complex.multiply((Complex)o);
/* 288 */         i.remove();
/* 289 */       } else if ((o instanceof Expression)) {
/* 290 */         i.set((Expression)o);
/*     */       } else {
/* 292 */         return new Error("Product.auto_simplify() error");
/*     */       }
/*     */     }
/* 295 */     sort();
/* 296 */     if (this.expressionList.isEmpty()) return unBox();
/*     */ 
/* 298 */     ListIterator i = this.expressionList.listIterator();
/* 299 */     Expression f = null;
/* 300 */     for (Expression e = (Expression)i.next(); i.hasNext(); e = f) {
/* 301 */       Product product_e = null;
/* 302 */       if ((e instanceof Product))
/* 303 */         product_e = (Product)e;
/*     */       else {
/* 305 */         product_e = new Product(e, false);
/*     */       }
/* 307 */       f = (Expression)i.next();
/* 308 */       Product product_f = null;
/* 309 */       if ((f instanceof Product))
/* 310 */         product_f = (Product)f;
/*     */       else {
/* 312 */         product_f = new Product(f, false);
/*     */       }
/* 314 */       if (product_e.compareTo(product_f) == 0) {
/* 315 */         System.out.println("Adding comparable expressions");
/* 316 */         Product product = new Product(product_e, false);
/* 317 */         product.setComplex(product_e.getComplex().add(product_f.getComplex()));
/*     */ 
/* 319 */         OObject o = product.unBox();
/* 320 */         if ((o instanceof Complex)) {
/* 321 */           this.complex = this.complex.add((Complex)o);
/* 322 */           i.remove();
/* 323 */           i.previous();
/* 324 */           i.remove();
/* 325 */           if (!i.hasNext()) continue; e = (Expression)i.next();
/* 326 */         } else if ((o instanceof Expression)) {
/* 327 */           i.remove();
/* 328 */           i.previous();
/* 329 */           i.set((Expression)o);
/* 330 */           f = (Expression)i.next();
/*     */         } else {
/* 332 */           return new Error("Sum.auto_simplify() error");
/*     */         }
/*     */       }
/*     */     }
/* 336 */     return unBox();
/*     */   }
/*     */   public OObject substitute(Substitution substitution) {
/* 339 */     Sum s = clone();
/*     */ 
/* 341 */     for (ListIterator i = s.expressionList.listIterator(); i.hasNext(); ) {
/* 342 */       Expression expression = (Expression)i.next();
/* 343 */       OObject o = expression.substitute(substitution);
/* 344 */       if ((o instanceof Complex)) {
/* 345 */         i.remove();
/* 346 */         s.complex = s.complex.add((Complex)o);
/* 347 */       } else if ((o instanceof Expression)) {
/* 348 */         i.set((Expression)o);
/*     */       } else {
/* 350 */         return new Error("Product.substitution() Error");
/*     */       }
/*     */     }
/* 353 */     return s.auto_simplify();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 358 */     Variable x = new Variable(new com.dukascopy.calculator.function.Variable('x'));
/*     */ 
/* 360 */     Sum s = new Sum(x);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Sum
 * JD-Core Version:    0.6.0
 */