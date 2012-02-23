/*     */ package com.dukascopy.calculator.expression;
/*     */ 
/*     */ import com.dukascopy.calculator.Base;
/*     */ import com.dukascopy.calculator.Error;
/*     */ import com.dukascopy.calculator.Notation;
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.StringArray;
/*     */ import com.dukascopy.calculator.Substitution;
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import com.dukascopy.calculator.complex.DoubleFormat;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Product extends SumOrProduct
/*     */   implements Cloneable
/*     */ {
/*     */   protected LinkedList<Expression> divisorList;
/*     */ 
/*     */   public Product(Expression expression, boolean inverse)
/*     */   {
/*  11 */     if ((expression instanceof Product)) {
/*  12 */       Product product = (Product)expression;
/*  13 */       if (inverse) {
/*  14 */         this.complex = new Complex(1.0D);
/*  15 */         this.complex = this.complex.divide(product.complex);
/*  16 */         this.expressionList = product.divisorList;
/*  17 */         this.divisorList = product.expressionList;
/*     */       } else {
/*  19 */         this.complex = product.complex;
/*  20 */         this.expressionList = product.expressionList;
/*  21 */         this.divisorList = product.divisorList;
/*     */       }
/*     */     } else {
/*  24 */       this.complex = new Complex(1.0D);
/*  25 */       if (inverse) {
/*  26 */         this.expressionList = new LinkedList();
/*  27 */         this.divisorList = new LinkedList();
/*  28 */         this.divisorList.add(expression);
/*     */       } else {
/*  30 */         this.expressionList = new LinkedList();
/*  31 */         this.expressionList.add(expression);
/*  32 */         this.divisorList = new LinkedList();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void setComplex(Complex z)
/*     */   {
/*  42 */     this.complex = z;
/*     */   }
/*     */ 
/*     */   public Product()
/*     */   {
/*  50 */     this.complex = new Complex();
/*  51 */     this.expressionList = new LinkedList();
/*  52 */     this.divisorList = new LinkedList();
/*     */   }
/*     */ 
/*     */   public Product clone()
/*     */   {
/*  60 */     Product copy = new Product();
/*  61 */     copy.complex = new Complex(this.complex.real(), this.complex.imaginary());
/*  62 */     copy.expressionList = new LinkedList();
/*     */ 
/*  64 */     for (ListIterator i = getExpressionList().listIterator(); i.hasNext(); ) {
/*  65 */       copy.expressionList.add(i.next());
/*     */     }
/*  67 */     copy.divisorList = new LinkedList();
/*     */ 
/*  69 */     for (ListIterator i = this.divisorList.listIterator(); i.hasNext(); ) {
/*  70 */       copy.divisorList.add(i.next());
/*     */     }
/*  72 */     return copy;
/*     */   }
/*     */ 
/*     */   public OObject multiply(Complex z)
/*     */   {
/*  81 */     Product product = clone();
/*  82 */     product.complex = this.complex.multiply(z);
/*  83 */     return product;
/*     */   }
/*     */ 
/*     */   public OObject divide(Complex z)
/*     */   {
/*  92 */     Product product = clone();
/*  93 */     product.complex = this.complex.divide(z);
/*  94 */     return product;
/*     */   }
/*     */ 
/*     */   public Product negate()
/*     */   {
/* 102 */     Product p = clone();
/* 103 */     p.complex = this.complex.negate();
/* 104 */     return p;
/*     */   }
/*     */ 
/*     */   public OObject multiply(Product x)
/*     */   {
/* 114 */     System.out.println("Multiplying two products");
/* 115 */     Product s = clone();
/*     */ 
/* 117 */     s.complex = s.complex.multiply(x.complex);
/* 118 */     ListIterator i = x.expressionList.listIterator();
/* 119 */     while (i.hasNext()) {
/* 120 */       s.expressionList.add(i.next());
/*     */     }
/* 122 */     ListIterator i = x.divisorList.listIterator();
/* 123 */     while (i.hasNext()) {
/* 124 */       s.divisorList.add(i.next());
/*     */     }
/* 126 */     return s;
/*     */   }
/*     */ 
/*     */   public OObject divide(Product x)
/*     */   {
/* 136 */     System.out.println("Dividing one product by another");
/* 137 */     Product s = clone();
/*     */ 
/* 139 */     s.complex = s.complex.divide(x.complex);
/* 140 */     ListIterator i = x.divisorList.listIterator();
/* 141 */     while (i.hasNext()) {
/* 142 */       s.expressionList.add(i.next());
/*     */     }
/* 144 */     ListIterator i = x.expressionList.listIterator();
/* 145 */     while (i.hasNext()) {
/* 146 */       s.divisorList.add(i.next());
/*     */     }
/* 148 */     return s;
/*     */   }
/*     */ 
/*     */   public boolean isNegative() {
/* 152 */     if (this.complex.isZero()) return false;
/* 153 */     boolean negative = this.complex.isNegative();
/* 154 */     ListIterator i = this.expressionList.listIterator();
/* 155 */     while (i.hasNext()) {
/* 156 */       Expression e = (Expression)i.next();
/* 157 */       if (e.isZero()) return false;
/* 158 */       boolean enegative = e.isNegative();
/* 159 */       negative = negative ? false : !enegative ? true : enegative;
/*     */     }
/* 161 */     return negative;
/*     */   }
/*     */ 
/*     */   public OObject unBox()
/*     */   {
/* 170 */     if ((this.divisorList.isEmpty()) && (this.expressionList.size() == 1) && (this.complex.subtract(new Complex(1.0D)).isZero()))
/*     */     {
/* 172 */       return (OObject)this.expressionList.getFirst();
/* 173 */     }if ((this.divisorList.isEmpty()) && (this.expressionList.isEmpty())) {
/* 174 */       return this.complex;
/*     */     }
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   public int compareTo(Product product)
/*     */   {
/* 187 */     sort();
/* 188 */     int r = compare(this.expressionList, product.expressionList);
/* 189 */     if (r != 0) {
/* 190 */       return r;
/*     */     }
/* 192 */     return compare(this.divisorList, product.divisorList);
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 210 */     StringArray s = new StringArray();
/*     */ 
/* 212 */     StringArray t = this.complex.toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/* 214 */     boolean unity = false;
/* 215 */     if (!this.expressionList.isEmpty()) {
/* 216 */       if (t.isMinusOne())
/* 217 */         s.add((String)DoubleFormat.minus.firstElement());
/* 218 */       else if (t.isOne()) {
/* 219 */         unity = true;
/*     */       }
/*     */       else
/* 222 */         s.addAll(t);
/*     */     }
/*     */     else {
/* 225 */       s.addAll(t);
/*     */     }
/*     */ 
/* 228 */     if ((unity == true) && (this.expressionList.size() == 1) && (this.divisorList.isEmpty())) {
/* 229 */       Expression expression = (Expression)this.expressionList.getFirst();
/* 230 */       return expression.toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/*     */     }
/*     */ 
/* 233 */     ListIterator i = this.expressionList.listIterator();
/* 234 */     while (i.hasNext()) {
/* 235 */       Expression expression = (Expression)i.next();
/* 236 */       if ((expression instanceof Power)) {
/* 237 */         s.addAll(expression.toHTMLSubString(maxChars, precision, base, notation, polarFactor));
/*     */       }
/* 239 */       else if (expression != null) {
/* 240 */         s.addAll(expression.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*     */       }
/*     */     }
/*     */ 
/* 244 */     if (!this.divisorList.isEmpty()) {
/* 245 */       s.add("&#247;");
/* 246 */       if (this.divisorList.size() == 1) {
/* 247 */         s.addAll(((Expression)this.divisorList.getFirst()).toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*     */       }
/*     */       else
/*     */       {
/* 251 */         s.add("(");
/* 252 */         ListIterator i = this.divisorList.listIterator();
/* 253 */         while (i.hasNext()) {
/* 254 */           Expression expression = (Expression)i.next();
/* 255 */           s.addAll(expression.toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*     */         }
/*     */ 
/* 258 */         s.add(")");
/*     */       }
/*     */     }
/* 261 */     return s;
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLParenString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 268 */     StringArray t = this.complex.toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/* 270 */     if ((getExpressionList().isEmpty()) && (this.divisorList.isEmpty()))
/* 271 */       return t;
/* 272 */     if ((getExpressionList().size() == 1) && (this.divisorList.isEmpty()) && (t.isOne()))
/*     */     {
/* 275 */       return ((Expression)getExpressionList().getFirst()).toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */     }
/*     */ 
/* 278 */     if ((getExpressionList().size() == 1) && (this.divisorList.isEmpty()) && (t.isMinusOne()))
/*     */     {
/* 281 */       StringArray s = new StringArray();
/* 282 */       s.add((String)DoubleFormat.minus.firstElement());
/* 283 */       s.addAll(((Expression)getExpressionList().getFirst()).toHTMLParenString(maxChars, precision, base, notation, polarFactor));
/*     */ 
/* 286 */       return s;
/*     */     }
/* 288 */     StringArray s = new StringArray();
/* 289 */     s.add("(");
/* 290 */     s.addAll(toHTMLSubString(maxChars, precision, base, notation, polarFactor));
/*     */ 
/* 292 */     s.add(")");
/* 293 */     return s;
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLParenStringL(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 321 */     StringArray t = this.complex.toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/* 323 */     if ((this.expressionList.isEmpty()) && (this.divisorList.isEmpty()))
/* 324 */       return t;
/* 325 */     if ((this.expressionList.size() == 1) && (this.divisorList.isEmpty())) {
/* 326 */       if (t.isOne()) {
/* 327 */         return ((Expression)this.expressionList.getFirst()).toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */       }
/* 329 */       if (t.isMinusOne()) {
/* 330 */         StringArray u = ((Expression)this.expressionList.getFirst()).toHTMLParenString(maxChars, precision, base, notation, polarFactor);
/*     */ 
/* 333 */         if ((u.size() > 0) && (!((String)((Vector)u.firstElement()).firstElement()).equals("("))) {
/* 334 */           StringArray v = new StringArray();
/* 335 */           v.add((String)DoubleFormat.minus.firstElement());
/* 336 */           v.addAll(u);
/* 337 */           return v;
/*     */         }
/*     */       }
/*     */     }
/* 341 */     StringArray s = new StringArray();
/* 342 */     s.add("(");
/* 343 */     s.addAll(toHTMLSubString(maxChars, precision, base, notation, polarFactor));
/* 344 */     s.add(")");
/* 345 */     return s;
/*     */   }
/*     */ 
/*     */   OObject integer_power(long n)
/*     */   {
/* 356 */     Complex z = new Complex(n);
/* 357 */     Product result = new Product();
/* 358 */     result.complex = this.complex.pow(z);
/* 359 */     ListIterator i = this.expressionList.listIterator();
/* 360 */     while (i.hasNext()) {
/* 361 */       Expression e = (Expression)i.next();
/* 362 */       result.expressionList.add(new Power(e, z));
/*     */     }
/* 364 */     ListIterator i = this.divisorList.listIterator();
/* 365 */     while (i.hasNext()) {
/* 366 */       Expression e = (Expression)i.next();
/* 367 */       result.divisorList.add(new Power(e, z));
/*     */     }
/* 369 */     return result;
/*     */   }
/*     */ 
/*     */   public void sort()
/*     */   {
/* 376 */     super.sort();
/* 377 */     for (Expression e : this.divisorList)
/* 378 */       e.sort();
/* 379 */     Collections.sort(this.divisorList);
/*     */   }
/*     */ 
/*     */   public OObject auto_simplify()
/*     */   {
/* 388 */     ListIterator i = this.divisorList.listIterator();
/* 389 */     while (i.hasNext()) {
/* 390 */       Expression e = (Expression)i.next();
/* 391 */       OObject o = new Power(e, new Complex(-1.0D)).auto_simplify();
/* 392 */       if ((o instanceof Complex))
/* 393 */         this.complex = this.complex.divide((Complex)o);
/* 394 */       else if ((o instanceof Expression))
/* 395 */         this.expressionList.add((Expression)o);
/*     */       else {
/* 397 */         return new Error("Product.auto_simplify() error");
/*     */       }
/*     */     }
/* 400 */     this.divisorList.clear();
/*     */ 
/* 402 */     ListIterator i = this.expressionList.listIterator();
/* 403 */     while (i.hasNext()) {
/* 404 */       OObject o = ((Expression)i.next()).auto_simplify();
/* 405 */       if ((o instanceof Complex)) {
/* 406 */         this.complex = this.complex.multiply((Complex)o);
/* 407 */         i.remove();
/* 408 */       } else if ((o instanceof Expression)) {
/* 409 */         i.set((Expression)o);
/*     */       } else {
/* 411 */         return new Error("Product.auto_simplify() error");
/*     */       }
/*     */     }
/* 414 */     sort();
/* 415 */     if (this.expressionList.isEmpty()) return unBox();
/*     */ 
/* 417 */     ListIterator i = this.expressionList.listIterator();
/* 418 */     Expression f = null;
/* 419 */     for (Expression e = (Expression)i.next(); i.hasNext(); e = f) {
/* 420 */       OObject base_e = e;
/* 421 */       OObject exponent_e = new Complex(1.0D);
/* 422 */       if ((e instanceof Power)) {
/* 423 */         base_e = ((Power)e).base();
/* 424 */         exponent_e = ((Power)e).exponent();
/*     */       }
/* 426 */       f = (Expression)i.next();
/* 427 */       OObject base_f = f;
/* 428 */       OObject exponent_f = new Complex(1.0D);
/* 429 */       if ((f instanceof Power)) {
/* 430 */         base_f = ((Power)f).base();
/* 431 */         exponent_f = ((Power)f).exponent();
/*     */       }
/* 433 */       if (base_e.compareTo(base_f) == 0) {
/* 434 */         OObject exponent = exponent_e.add(exponent_f);
/* 435 */         if ((exponent instanceof Expression))
/* 436 */           exponent = ((Expression)exponent).auto_simplify();
/* 437 */         OObject expression = new Power(base_e, exponent).auto_simplify();
/* 438 */         if ((expression instanceof Complex)) {
/* 439 */           this.complex = this.complex.multiply((Complex)expression);
/* 440 */           i.remove();
/* 441 */           i.previous();
/* 442 */           i.remove();
/* 443 */           if (!i.hasNext()) continue; e = (Expression)i.next();
/* 444 */         } else if ((expression instanceof Expression)) {
/* 445 */           i.remove();
/* 446 */           i.previous();
/* 447 */           i.set((Expression)expression);
/* 448 */           f = (Expression)i.next();
/*     */         } else {
/* 450 */           return new Error("Product.auto_simplify() error");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 455 */     return unBox();
/*     */   }
/*     */ 
/*     */   public OObject substitute(Substitution substitution) {
/* 459 */     Product s = clone();
/*     */ 
/* 461 */     for (ListIterator i = s.expressionList.listIterator(); i.hasNext(); ) {
/* 462 */       Expression expression = (Expression)i.next();
/* 463 */       OObject o = expression.substitute(substitution);
/* 464 */       if ((o instanceof Complex)) {
/* 465 */         i.remove();
/* 466 */         s.complex = s.complex.multiply((Complex)o);
/* 467 */       } else if ((o instanceof Expression)) {
/* 468 */         i.set((Expression)o);
/*     */       } else {
/* 470 */         return new Error("Product.substitution() Error");
/*     */       }
/*     */     }
/* 473 */     ListIterator i = s.divisorList.listIterator();
/* 474 */     while (i.hasNext()) {
/* 475 */       Expression expression = (Expression)i.next();
/* 476 */       OObject o = expression.substitute(substitution);
/* 477 */       if ((o instanceof Complex)) {
/* 478 */         i.remove();
/* 479 */         s.complex = s.complex.divide((Complex)o);
/* 480 */       } else if ((o instanceof Expression)) {
/* 481 */         i.set((Expression)o);
/*     */       } else {
/* 483 */         return new Error("Product.substitution() Error");
/*     */       }
/*     */     }
/* 486 */     return s.auto_simplify();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.expression.Product
 * JD-Core Version:    0.6.0
 */