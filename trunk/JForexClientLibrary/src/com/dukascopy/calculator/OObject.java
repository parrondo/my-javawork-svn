/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import com.dukascopy.calculator.expression.ACos;
/*     */ import com.dukascopy.calculator.expression.ASin;
/*     */ import com.dukascopy.calculator.expression.ATan;
/*     */ import com.dukascopy.calculator.expression.And;
/*     */ import com.dukascopy.calculator.expression.Combination;
/*     */ import com.dukascopy.calculator.expression.Conjugate;
/*     */ import com.dukascopy.calculator.expression.Cos;
/*     */ import com.dukascopy.calculator.expression.Exp;
/*     */ import com.dukascopy.calculator.expression.Expression;
/*     */ import com.dukascopy.calculator.expression.Factorial;
/*     */ import com.dukascopy.calculator.expression.Ln;
/*     */ import com.dukascopy.calculator.expression.Log;
/*     */ import com.dukascopy.calculator.expression.Or;
/*     */ import com.dukascopy.calculator.expression.Permutation;
/*     */ import com.dukascopy.calculator.expression.Power;
/*     */ import com.dukascopy.calculator.expression.Product;
/*     */ import com.dukascopy.calculator.expression.Sin;
/*     */ import com.dukascopy.calculator.expression.Sum;
/*     */ import com.dukascopy.calculator.expression.Tan;
/*     */ import com.dukascopy.calculator.expression.Variable;
/*     */ import com.dukascopy.calculator.expression.Xor;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Vector;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class OObject extends GObject
/*     */ {
/*  14 */   protected static final Logger LOGGER = LoggerFactory.getLogger(OObject.class);
/*     */ 
/* 784 */   private static final Complex LOG10INV = new Complex(1.0D / StrictMath.log(10.0D));
/*     */ 
/*     */   public boolean isNegative()
/*     */   {
/*  20 */     return false;
/*     */   }
/*     */ 
/*     */   public Error function(OObject x)
/*     */   {
/*  29 */     return new Error("OObject function( x ) error");
/*     */   }
/*     */ 
/*     */   public Error function(OObject x, OObject y)
/*     */   {
/*  39 */     return new Error("OObject function( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject inverse()
/*     */   {
/*  47 */     if ((this instanceof Expression)) {
/*  48 */       return new Power((Expression)this, new Complex(-1.0D));
/*     */     }
/*  50 */     return new Error("OObject inverse( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject square()
/*     */   {
/*  58 */     if ((this instanceof Expression)) {
/*  59 */       return new Power((Expression)this, new Complex(2.0D));
/*     */     }
/*  61 */     return new Error("OObject square( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject cube()
/*     */   {
/*  69 */     if ((this instanceof Expression)) {
/*  70 */       return new Power((Expression)this, new Complex(3.0D));
/*     */     }
/*  72 */     return new Error("OObject cube( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject factorial()
/*     */   {
/*  80 */     if ((this instanceof Expression)) {
/*  81 */       return new Factorial((Expression)this);
/*     */     }
/*  83 */     return new Error("OObject factorial( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject negate()
/*     */   {
/*  91 */     return new Error("OObject negate( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject add(OObject x)
/*     */   {
/* 100 */     if (((this instanceof Error)) || ((x instanceof Error)))
/* 101 */       return new Error("OObject add( x ) error");
/* 102 */     if ((this instanceof Complex)) {
/* 103 */       if ((x instanceof Complex))
/* 104 */         return ((Complex)this).add((Complex)x);
/* 105 */       if ((x instanceof Variable)) {
/* 106 */         Sum s = new Sum((Variable)x);
/* 107 */         return s.add((Complex)this);
/* 108 */       }if ((x instanceof Sum))
/* 109 */         return ((Sum)x).add((Complex)this);
/* 110 */       if ((x instanceof Product)) {
/* 111 */         Sum s = new Sum((Product)x);
/* 112 */         return s.add((Complex)this);
/* 113 */       }if ((x instanceof Expression)) {
/* 114 */         Sum s = new Sum((Expression)x);
/* 115 */         return s.add((Complex)this);
/*     */       }
/* 117 */     } else if ((this instanceof Variable)) {
/* 118 */       if ((x instanceof Complex)) {
/* 119 */         Sum s = new Sum((Variable)this);
/* 120 */         return s.add((Complex)x);
/* 121 */       }if ((x instanceof Variable)) {
/* 122 */         Sum s = new Sum((Variable)this);
/* 123 */         return s.add(x);
/* 124 */       }if ((x instanceof Sum)) {
/* 125 */         Sum s = new Sum((Variable)this);
/* 126 */         return s.add((Sum)x);
/* 127 */       }if ((x instanceof Product)) {
/* 128 */         Sum s = new Sum((Variable)this);
/* 129 */         return s.add((Product)x);
/* 130 */       }if ((x instanceof Expression)) {
/* 131 */         Sum s = new Sum((Variable)this);
/* 132 */         return s.add(new Product((Expression)x, false));
/*     */       }
/* 134 */     } else if ((this instanceof Sum)) {
/* 135 */       if ((x instanceof Complex))
/* 136 */         return ((Sum)this).add((Complex)x);
/* 137 */       if ((x instanceof Variable)) {
/* 138 */         Product s = new Product((Variable)x, false);
/* 139 */         return ((Sum)this).add(s);
/* 140 */       }if ((x instanceof Product))
/* 141 */         return ((Sum)this).add((Product)x);
/* 142 */       if ((x instanceof Sum))
/* 143 */         return ((Sum)this).add((Sum)x);
/* 144 */       if ((x instanceof Expression))
/* 145 */         return ((Sum)this).add(new Product((Expression)x, false));
/*     */     }
/* 147 */     else if ((this instanceof Product)) {
/* 148 */       if ((x instanceof Complex)) {
/* 149 */         Sum s = new Sum((Product)this);
/* 150 */         return s.add((Complex)x);
/* 151 */       }if ((x instanceof Variable)) {
/* 152 */         Sum s = new Sum((Product)this);
/* 153 */         Product p = new Product((Variable)x, false);
/* 154 */         return s.add(p);
/* 155 */       }if ((x instanceof Sum)) {
/* 156 */         Sum s = new Sum((Product)this);
/* 157 */         return s.add((Sum)x);
/* 158 */       }if ((x instanceof Product)) {
/* 159 */         Sum s = new Sum((Product)this);
/* 160 */         return s.add((Product)x);
/* 161 */       }if ((x instanceof Expression)) {
/* 162 */         Sum s = new Sum((Product)this);
/* 163 */         return s.add(new Product((Expression)x, false));
/*     */       }
/* 165 */     } else if ((this instanceof Expression)) {
/* 166 */       Sum s = new Sum(new Product((Expression)this, false));
/* 167 */       if ((x instanceof Complex))
/* 168 */         return s.add((Complex)x);
/* 169 */       if ((x instanceof Sum))
/* 170 */         return s.add((Sum)x);
/* 171 */       if ((x instanceof Product))
/* 172 */         return s.add((Product)x);
/* 173 */       if ((x instanceof Expression)) {
/* 174 */         return s.add(new Product((Expression)x, false));
/*     */       }
/*     */     }
/*     */ 
/* 178 */     return new Error("OObject add( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject subtract(OObject x)
/*     */   {
/* 187 */     if (((this instanceof Error)) || ((x instanceof Error)))
/* 188 */       return new Error("OObject multiply( x ) error");
/* 189 */     if (((this instanceof Complex)) && ((x instanceof Complex)))
/* 190 */       return ((Complex)this).subtract((Complex)x);
/* 191 */     if ((x instanceof Complex))
/* 192 */       return add(((Complex)x).negate());
/* 193 */     if ((x instanceof Expression)) {
/* 194 */       return add(((Expression)x).negate());
/*     */     }
/* 196 */     return new Error("OObject subtract( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject multiply(OObject x)
/*     */   {
/* 205 */     if (((this instanceof Error)) || ((x instanceof Error)))
/* 206 */       return new Error("OObject multiply( x ) error");
/* 207 */     if ((this instanceof Complex)) {
/* 208 */       if ((x instanceof Complex))
/* 209 */         return ((Complex)this).multiply((Complex)x);
/* 210 */       if ((x instanceof Variable)) {
/* 211 */         Product p = new Product((Variable)x, false);
/* 212 */         return p.multiply((Complex)this);
/* 213 */       }if ((x instanceof Sum)) {
/* 214 */         Product p = new Product((Expression)x, false);
/* 215 */         return p.multiply(this);
/* 216 */       }if ((x instanceof Product)) {
/* 217 */         Product p = (Product)x;
/* 218 */         return p.multiply((Complex)this);
/* 219 */       }if ((x instanceof Expression)) {
/* 220 */         Product p = new Product((Expression)x, false);
/* 221 */         return p.multiply((Complex)this);
/*     */       }
/* 223 */     } else if ((this instanceof Variable)) {
/* 224 */       if ((x instanceof Complex)) {
/* 225 */         Product p = new Product((Variable)this, false);
/* 226 */         return p.multiply((Complex)x);
/* 227 */       }if ((x instanceof Variable)) {
/* 228 */         Product p = new Product((Variable)this, false);
/* 229 */         Product q = new Product((Variable)x, false);
/* 230 */         return p.multiply(q);
/* 231 */       }if ((x instanceof Sum)) {
/* 232 */         Product p = new Product((Variable)this, false);
/* 233 */         return p.multiply((Expression)this);
/* 234 */       }if ((x instanceof Product)) {
/* 235 */         Product p = new Product((Variable)this, false);
/* 236 */         return p.multiply((Product)x);
/* 237 */       }if ((x instanceof Expression)) {
/* 238 */         Product p = new Product((Variable)this, false);
/* 239 */         return p.multiply((Expression)x);
/*     */       }
/* 241 */     } else if ((this instanceof Sum)) {
/* 242 */       if ((x instanceof Complex)) {
/* 243 */         Product p = new Product((Expression)this, false);
/* 244 */         return p.multiply((Complex)x);
/* 245 */       }if ((x instanceof Expression)) {
/* 246 */         Product p = new Product((Expression)x, false);
/* 247 */         return p.multiply((Expression)this);
/*     */       }
/* 249 */     } else if ((this instanceof Product)) {
/* 250 */       if ((x instanceof Complex)) {
/* 251 */         Product p = (Product)this;
/* 252 */         return p.multiply((Complex)x);
/* 253 */       }if ((x instanceof Product))
/* 254 */         return ((Product)this).multiply((Product)x);
/* 255 */       if ((x instanceof Expression))
/* 256 */         return ((Product)this).multiply(new Product((Expression)x, false));
/*     */     }
/* 258 */     else if ((this instanceof Expression)) {
/* 259 */       if ((x instanceof Complex)) {
/* 260 */         Product p = new Product((Expression)this, false);
/* 261 */         return p.multiply((Complex)x);
/* 262 */       }if ((x instanceof Product))
/* 263 */         return new Product((Expression)this, false).multiply((Product)x);
/* 264 */       if ((x instanceof Expression)) {
/* 265 */         Product p = new Product((Expression)this, false);
/* 266 */         return p.multiply((Expression)x);
/*     */       }
/*     */     }
/* 269 */     return new Error("OObject multiply( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject divide(OObject x)
/*     */   {
/* 278 */     if (((this instanceof Error)) || ((x instanceof Error)))
/* 279 */       return new Error("OObject multiply( x ) error");
/* 280 */     if ((this instanceof Complex)) {
/* 281 */       if ((x instanceof Complex))
/* 282 */         return ((Complex)this).divide((Complex)x);
/* 283 */       if ((x instanceof Variable)) {
/* 284 */         Product p = new Product((Variable)x, true);
/* 285 */         return p.multiply((Complex)this);
/* 286 */       }if ((x instanceof Sum)) {
/* 287 */         Product p = new Product((Expression)x, true);
/* 288 */         return p.multiply(this);
/* 289 */       }if ((x instanceof Product)) {
/* 290 */         Product p = new Product((Expression)x, true);
/* 291 */         return p.multiply((Complex)this);
/* 292 */       }if ((x instanceof Expression)) {
/* 293 */         Product p = new Product((Expression)x, true);
/* 294 */         return p.multiply((Complex)this);
/*     */       }
/* 296 */     } else if ((this instanceof Variable)) {
/* 297 */       if ((x instanceof Complex)) {
/* 298 */         Product p = new Product((Variable)this, false);
/* 299 */         return p.divide((Complex)x);
/* 300 */       }if ((x instanceof Variable)) {
/* 301 */         Product p = new Product((Variable)this, false);
/* 302 */         Product q = new Product((Variable)x, true);
/* 303 */         return p.multiply(q);
/* 304 */       }if ((x instanceof Sum)) {
/* 305 */         Product p = new Product((Variable)this, false);
/* 306 */         Product q = new Product((Sum)x, true);
/* 307 */         return p.multiply(q);
/* 308 */       }if ((x instanceof Product)) {
/* 309 */         Product p = new Product((Variable)this, false);
/* 310 */         return p.divide((Product)x);
/* 311 */       }if ((x instanceof Expression)) {
/* 312 */         Product p = new Product((Variable)this, false);
/* 313 */         Product q = new Product((Expression)x, true);
/* 314 */         return p.multiply(q);
/*     */       }
/* 316 */     } else if ((this instanceof Sum)) {
/* 317 */       if ((x instanceof Complex)) {
/* 318 */         Product p = new Product((Expression)this, false);
/* 319 */         return p.multiply((Complex)x);
/* 320 */       }if ((x instanceof Expression)) {
/* 321 */         Product p = new Product((Expression)x, false);
/* 322 */         Product q = new Product((Expression)x, true);
/* 323 */         return p.multiply(q);
/*     */       }
/* 325 */     } else if ((this instanceof Product)) {
/* 326 */       if ((x instanceof Complex)) {
/* 327 */         Product p = (Product)this;
/* 328 */         return p.divide((Complex)x);
/* 329 */       }if ((x instanceof Product))
/* 330 */         return ((Product)this).divide((Product)x);
/* 331 */       if ((x instanceof Expression)) {
/* 332 */         Product q = new Product((Expression)x, true);
/* 333 */         return ((Product)this).multiply(q);
/*     */       }
/* 335 */     } else if ((this instanceof Expression)) {
/* 336 */       if ((x instanceof Complex)) {
/* 337 */         Product p = new Product((Expression)this, false);
/* 338 */         return p.divide((Complex)x);
/* 339 */       }if ((x instanceof Product)) {
/* 340 */         Product p = new Product((Expression)this, false);
/* 341 */         return p.divide((Product)x);
/* 342 */       }if ((x instanceof Expression)) {
/* 343 */         Product p = new Product((Expression)this, false);
/* 344 */         Product q = new Product((Expression)x, true);
/* 345 */         return p.multiply(q);
/*     */       }
/*     */     }
/* 348 */     return new Error("OObject divide( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject exp()
/*     */   {
/* 356 */     if ((this instanceof Expression)) {
/* 357 */       return new Exp((Expression)this);
/*     */     }
/* 359 */     return new Error("OObject exp() error");
/*     */   }
/*     */ 
/*     */   public OObject combination(OObject x)
/*     */   {
/* 368 */     if (((this instanceof Complex)) && ((x instanceof Complex)))
/* 369 */       return ((Complex)this).combination((Complex)x);
/* 370 */     if ((!(this instanceof Error)) && (!(x instanceof Error))) {
/* 371 */       return new Combination(this, x);
/*     */     }
/* 373 */     return new Error("OObject combination( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject permutation(OObject x)
/*     */   {
/* 382 */     if (((this instanceof Complex)) && ((x instanceof Complex)))
/* 383 */       return ((Complex)this).permutation((Complex)x);
/* 384 */     if ((!(this instanceof Error)) && (!(x instanceof Error))) {
/* 385 */       return new Permutation(this, x);
/*     */     }
/* 387 */     return new Error("OObject permutation( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject pow(OObject y)
/*     */   {
/* 396 */     if (((this instanceof Complex)) && ((y instanceof Complex)))
/* 397 */       return ((Complex)this).pow((Complex)y);
/* 398 */     if ((!(this instanceof Error)) && (!(y instanceof Error))) {
/* 399 */       return new Power(this, y);
/*     */     }
/* 401 */     return new Error("OObject pow( y ) error");
/*     */   }
/*     */ 
/*     */   public OObject root(OObject y)
/*     */   {
/* 410 */     if (((this instanceof Complex)) && ((y instanceof Complex)))
/* 411 */       return ((Complex)this).root((Complex)y);
/* 412 */     if (!(this instanceof Error)) {
/* 413 */       if ((y instanceof Complex))
/* 414 */         return new Power(this, ((Complex)y).inverse());
/* 415 */       if ((y instanceof Expression)) {
/* 416 */         Product p = new Product((Expression)y, true);
/* 417 */         return new Power(this, p);
/*     */       }
/*     */     }
/* 420 */     return new Error("OObject root( y ) error");
/*     */   }
/*     */ 
/*     */   public OObject sin(AngleType angleType)
/*     */   {
/* 429 */     if ((this instanceof Expression)) {
/* 430 */       return new Sin((Expression)this, angleType);
/*     */     }
/* 432 */     return new Error("OObject sin( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject asin(AngleType angleType)
/*     */   {
/* 441 */     if ((this instanceof Expression)) {
/* 442 */       return new ASin((Expression)this, angleType);
/*     */     }
/* 444 */     return new Error("OObject asin( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject cos(AngleType angleType)
/*     */   {
/* 453 */     if ((this instanceof Expression)) {
/* 454 */       return new Cos((Expression)this, angleType);
/*     */     }
/* 456 */     return new Error("OObject cos( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject acos(AngleType angleType)
/*     */   {
/* 465 */     if ((this instanceof Expression)) {
/* 466 */       return new ACos((Expression)this, angleType);
/*     */     }
/* 468 */     return new Error("OObject acos( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject tan(AngleType angleType)
/*     */   {
/* 477 */     if ((this instanceof Expression)) {
/* 478 */       return new Tan((Expression)this, angleType);
/*     */     }
/* 480 */     return new Error("OObject tan( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject atan(AngleType angleType)
/*     */   {
/* 489 */     if ((this instanceof Expression)) {
/* 490 */       return new ATan((Expression)this, angleType);
/*     */     }
/* 492 */     return new Error("OObject atan( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject log10()
/*     */   {
/* 500 */     if ((this instanceof Expression)) {
/* 501 */       return new Log((Expression)this);
/*     */     }
/* 503 */     return new Error("OObject log10( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject log()
/*     */   {
/* 511 */     if ((this instanceof Expression)) {
/* 512 */       return new Ln((Expression)this);
/*     */     }
/* 514 */     return new Error("OObject log( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject sqrt()
/*     */   {
/* 522 */     if ((this instanceof Expression)) {
/* 523 */       return new Power((Expression)this, new Complex(0.5D));
/*     */     }
/* 525 */     return new Error("OObject sqrt( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject cuberoot()
/*     */   {
/* 533 */     if ((this instanceof Expression)) {
/* 534 */       return new Power((Expression)this, new Complex(0.3333333333333333D));
/*     */     }
/*     */ 
/* 537 */     return new Error("OObject cuberoot( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject tenx()
/*     */   {
/* 545 */     if ((this instanceof Expression)) {
/* 546 */       return new Power(new Complex(10.0D), (Expression)this);
/*     */     }
/* 548 */     return new Error("OObject tenx( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject conjugate()
/*     */   {
/* 556 */     if ((this instanceof Expression)) {
/* 557 */       return new Conjugate((Expression)this);
/*     */     }
/* 559 */     return new Error("OObject tan( x ) error");
/*     */   }
/*     */ 
/*     */   public OObject and(OObject z)
/*     */   {
/* 568 */     if (((this instanceof Complex)) && ((z instanceof Complex)))
/* 569 */       return ((Complex)this).and((Complex)z);
/* 570 */     if ((!(this instanceof Error)) && (!(z instanceof Error))) {
/* 571 */       return new And(this, z);
/*     */     }
/* 573 */     return new Error("OObject and( z ) error");
/*     */   }
/*     */ 
/*     */   public OObject or(OObject z)
/*     */   {
/* 582 */     if (((this instanceof Complex)) && ((z instanceof Complex)))
/* 583 */       return ((Complex)this).or((Complex)z);
/* 584 */     if ((!(this instanceof Error)) && (!(z instanceof Error))) {
/* 585 */       return new Or(this, z);
/*     */     }
/* 587 */     return new Error("OObject or( z ) error");
/*     */   }
/*     */ 
/*     */   public OObject xor(OObject z)
/*     */   {
/* 596 */     if (((this instanceof Complex)) && ((z instanceof Complex)))
/* 597 */       return ((Complex)this).xor((Complex)z);
/* 598 */     if ((!(this instanceof Error)) && (!(z instanceof Error))) {
/* 599 */       return new Xor(this, z);
/*     */     }
/* 601 */     return new Error("OObject xor( z ) error");
/*     */   }
/*     */ 
/*     */   public boolean isZero()
/*     */   {
/* 609 */     return false;
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLStringVector(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 630 */     StringArray v = toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/* 631 */     return v;
/*     */   }
/*     */ 
/*     */   public String toHTMLString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 662 */     Vector v = toHTMLSubString(maxChars, precision, base, notation, polarFactor);
/* 663 */     StringBuilder s = new StringBuilder("<html>");
/*     */ 
/* 665 */     for (ListIterator i = v.listIterator(); i.hasNext(); )
/*     */     {
/* 667 */       for (j = ((Vector)i.next()).listIterator(); j.hasNext(); )
/* 668 */         s.append((String)j.next());
/*     */     }
/*     */     ListIterator j;
/* 671 */     s.append("</html>");
/* 672 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLSubString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 695 */     StringArray v = new StringArray();
/* 696 */     String[] error = { "E", "r", "r", "o", "r" };
/* 697 */     v.add(error);
/* 698 */     return v;
/*     */   }
/*     */ 
/*     */   public StringArray toHTMLParenString(int maxChars, int precision, Base base, Notation notation, double polarFactor)
/*     */   {
/* 722 */     StringArray s = new StringArray();
/* 723 */     s.add("(");
/* 724 */     s.addAll(toHTMLSubString(maxChars, precision, base, notation, polarFactor));
/* 725 */     s.add(")");
/* 726 */     return s;
/*     */   }
/*     */ 
/*     */   public int compareTo(OObject o)
/*     */   {
/* 735 */     if ((this instanceof Error)) {
/* 736 */       if ((o instanceof Error)) {
/* 737 */         return 0;
/*     */       }
/* 739 */       return -1;
/*     */     }
/* 741 */     if ((this instanceof Complex)) {
/* 742 */       if ((o instanceof Complex)) {
/* 743 */         return ((Complex)this).compareTo((Complex)o);
/*     */       }
/* 745 */       return -1;
/*     */     }
/* 747 */     if ((this instanceof Expression)) {
/* 748 */       if (!(o instanceof Expression)) {
/* 749 */         return 1;
/*     */       }
/* 751 */       return ((Expression)this).compareTo((Expression)o);
/*     */     }
/*     */ 
/* 754 */     return 0;
/*     */   }
/*     */ 
/*     */   public void sort()
/*     */   {
/*     */   }
/*     */ 
/*     */   public OObject auto_simplify()
/*     */   {
/* 769 */     return this;
/*     */   }
/*     */ 
/*     */   public OObject substitute(Substitution substitution)
/*     */   {
/* 778 */     return this;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.OObject
 * JD-Core Version:    0.6.0
 */