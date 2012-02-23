/*      */ package com.dukascopy.calculator.complex;
/*      */ 
/*      */ import com.dukascopy.calculator.Base;
/*      */ import java.io.PrintStream;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class Format
/*      */ {
/*      */   private static final double SMALLIMAGINARY = 5.E-005D;
/*      */   private final Complex z;
/*      */   private DoubleFormat real;
/*      */   private DoubleFormat imaginary;
/*      */   private DoubleFormat abs;
/*      */   private DoubleFormat argument;
/*      */   boolean realSign;
/*      */   boolean imaginarySign;
/*      */ 
/*      */   public Format(Complex z, Base b)
/*      */   {
/*   13 */     this.z = z;
/*   14 */     this.real = new DoubleFormat(z.real(), b);
/*   15 */     this.imaginary = new DoubleFormat(Math.abs(z.imaginary()), b);
/*   16 */     this.abs = new DoubleFormat(z.abs(), b);
/*   17 */     this.argument = new DoubleFormat(z.arg(), b);
/*   18 */     this.imaginarySign = (z.imaginary() < 0.0D);
/*   19 */     this.realSign = (z.real() < 0.0D);
/*      */   }
/*      */ 
/*      */   private static String correctMinus(String s)
/*      */   {
/*   36 */     return s;
/*      */   }
/*      */ 
/*      */   public String formatStandard(Base base, int sigDigits, int maxLength, boolean showComplex, boolean rectangularComplex)
/*      */   {
/*   54 */     if (rectangularComplex)
/*      */     {
/*   56 */       int rDigits = sigDigits;
/*   57 */       int p = this.real.precision(1000);
/*   58 */       if (p < rDigits) {
/*   59 */         rDigits = p;
/*      */       }
/*   61 */       int iDigits = sigDigits;
/*   62 */       int p = this.imaginary.precision(1000);
/*   63 */       if (p < iDigits) {
/*   64 */         iDigits = p;
/*      */       }
/*   66 */       this.real = new DoubleFormat(this.z.real(), base);
/*   67 */       this.real.round(rDigits);
/*   68 */       this.imaginary = new DoubleFormat(Math.abs(this.z.imaginary()), base);
/*   69 */       this.imaginary.round(iDigits);
/*   70 */       if (showComplex)
/*      */       {
/*   72 */         int ml = maxLength - 2;
/*   73 */         int il = ml / 2;
/*   74 */         int rl = il;
/*   75 */         if ((il + rl < ml) && (this.realSign)) rl++;
/*   76 */         Notation ifm = Notation.STANDARD;
/*   77 */         if (this.imaginary.precision(il) < iDigits) {
/*   78 */           if (this.imaginary.scientificPrecision(il) < iDigits) {
/*   79 */             if (base == Base.BINARY) {
/*   80 */               if (this.imaginary.scientificPrecision(il) < iDigits)
/*   81 */                 ifm = Notation.NONE;
/*      */               else
/*   83 */                 ifm = Notation.SCIENTIFICB;
/*      */             }
/*      */             else
/*   86 */               ifm = Notation.NONE;
/*      */           }
/*   88 */           else ifm = Notation.SCIENTIFIC;
/*      */         }
/*   90 */         Notation rfm = Notation.STANDARD;
/*      */ 
/*   94 */         if (this.real.precision(rl) < rDigits) {
/*   95 */           if (this.real.scientificPrecision(rl) < rDigits) {
/*   96 */             if (base == Base.BINARY) {
/*   97 */               if (this.real.scientificPrecision(rl) < rDigits)
/*   98 */                 rfm = Notation.NONE;
/*      */               else
/*  100 */                 rfm = Notation.SCIENTIFICB;
/*      */             }
/*      */             else
/*  103 */               rfm = Notation.NONE;
/*      */           }
/*  105 */           else rfm = Notation.SCIENTIFIC;
/*      */         }
/*      */ 
/*  108 */         while ((rfm == Notation.NONE) || (ifm == Notation.NONE)) {
/*  109 */           if ((rfm == Notation.NONE) && (ifm == Notation.NONE))
/*      */           {
/*  111 */             rDigits--;
/*  112 */             iDigits--;
/*  113 */             if (base == Base.BINARY) {
/*  114 */               if (this.imaginary.scientificBPrecision(il) >= iDigits)
/*  115 */                 ifm = Notation.SCIENTIFICB;
/*  116 */               if (this.real.scientificBPrecision(rl) >= rDigits) {
/*  117 */                 rfm = Notation.SCIENTIFICB; continue;
/*      */               }
/*      */             }
/*  119 */             if (this.imaginary.scientificPrecision(il) >= iDigits)
/*  120 */               ifm = Notation.SCIENTIFIC;
/*  121 */             if (this.real.scientificPrecision(rl) >= rDigits) {
/*  122 */               rfm = Notation.SCIENTIFIC; continue;
/*      */             }
/*      */           }
/*  124 */           if (rfm == Notation.NONE)
/*      */           {
/*  126 */             if (base == Base.BINARY) {
/*  127 */               while (this.imaginary.scientificBPrecision(il - 1) >= iDigits)
/*  128 */                 il--;
/*      */             }
/*  130 */             while (this.imaginary.scientificPrecision(il - 1) >= iDigits) {
/*  131 */               il--;
/*      */             }
/*  133 */             if (rl + il < ml)
/*      */             {
/*  135 */               rl = ml - il;
/*  136 */               if (base == Base.BINARY)
/*  137 */                 if (this.real.scientificBPrecision(rl) >= rDigits) {
/*  138 */                   rfm = Notation.SCIENTIFICB; continue;
/*      */                 }
/*  140 */               if (this.real.scientificPrecision(rl) >= rDigits) {
/*  141 */                 rfm = Notation.SCIENTIFIC; continue;
/*      */               }
/*      */             }
/*      */ 
/*  145 */             if (iDigits == rDigits) {
/*  146 */               rDigits--; continue;
/*      */             }
/*  148 */             iDigits--; continue;
/*      */           }
/*      */ 
/*  153 */           if (base == Base.BINARY) {
/*  154 */             while (this.real.scientificBPrecision(rl - 1) >= rDigits)
/*  155 */               rl--;
/*      */           }
/*  157 */           while (this.real.scientificPrecision(rl - 1) >= rDigits) {
/*  158 */             rl--;
/*      */           }
/*  160 */           if (rl + il < ml)
/*      */           {
/*  162 */             il = ml - rl;
/*  163 */             if (base == Base.BINARY)
/*  164 */               if (this.imaginary.scientificBPrecision(il) >= iDigits) {
/*  165 */                 ifm = Notation.SCIENTIFICB; continue;
/*      */               }
/*  167 */             if (this.imaginary.scientificPrecision(il) >= iDigits) {
/*  168 */               ifm = Notation.SCIENTIFIC; continue;
/*      */             }
/*      */           }
/*      */ 
/*  172 */           if (iDigits == rDigits) {
/*  173 */             iDigits--; continue;
/*      */           }
/*  175 */           rDigits--;
/*      */         }
/*      */ 
/*  181 */         this.real.round(rDigits);
/*  182 */         String realString = null;
/*  183 */         switch (1.$SwitchMap$com$dukascopy$calculator$complex$Format$Notation[rfm.ordinal()]) {
/*      */         case 1:
/*  185 */           realString = this.real.standard();
/*  186 */           break;
/*      */         case 2:
/*  188 */           realString = this.real.scientific();
/*  189 */           break;
/*      */         case 3:
/*  191 */           realString = this.real.scientificB();
/*  192 */           break;
/*      */         default:
/*  194 */           realString = "*";
/*      */         }
/*      */ 
/*  197 */         this.imaginary.round(iDigits);
/*  198 */         String imaginaryString = null;
/*  199 */         switch (1.$SwitchMap$com$dukascopy$calculator$complex$Format$Notation[ifm.ordinal()]) {
/*      */         case 1:
/*  201 */           imaginaryString = this.imaginary.standard();
/*  202 */           break;
/*      */         case 2:
/*  204 */           imaginaryString = this.imaginary.scientific();
/*  205 */           break;
/*      */         case 3:
/*  207 */           imaginaryString = this.imaginary.scientificB();
/*  208 */           break;
/*      */         default:
/*  210 */           imaginaryString = "*";
/*      */         }
/*      */ 
/*  214 */         if ((this.z.imaginary() == 1.0D) || (this.z.imaginary() == -1.0D))
/*  215 */           imaginaryString = new String();
/*  216 */         if (this.imaginarySign) {
/*  217 */           return correctMinus(realString + "-" + imaginaryString + "i");
/*      */         }
/*  219 */         return correctMinus(realString + "+" + imaginaryString + "i");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  226 */       this.abs = new DoubleFormat(this.z.abs(), base);
/*  227 */       this.abs.round(sigDigits);
/*  228 */       this.argument = new DoubleFormat(this.z.arg(), base);
/*  229 */       this.argument.round(sigDigits);
/*      */     }
/*  231 */     return null;
/*      */   }
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*  267 */     Complex c = new Complex(4.0D, 5.0D);
/*  268 */     Format f = new Format(c, Base.DECIMAL);
/*  269 */     System.out.println("--------------------");
/*  270 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  271 */     c = new Complex(0.0D, 0.0D);
/*  272 */     f = new Format(c, Base.DECIMAL);
/*  273 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  274 */     c = new Complex(0.0D, 1.0D);
/*  275 */     f = new Format(c, Base.DECIMAL);
/*  276 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  277 */     c = new Complex(1.0D, 0.0D);
/*  278 */     f = new Format(c, Base.DECIMAL);
/*  279 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  280 */     c = new Complex(0.002D, -1.0D);
/*  281 */     f = new Format(c, Base.DECIMAL);
/*  282 */     System.out.println("--------------------");
/*  283 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  284 */     c = new Complex(-1.0D, 0.0D);
/*  285 */     f = new Format(c, Base.DECIMAL);
/*  286 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  287 */     c = new Complex(-12.0D, 0.0D);
/*  288 */     f = new Format(c, Base.DECIMAL);
/*  289 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  290 */     c = new Complex(123.0D, 0.0D);
/*  291 */     f = new Format(c, Base.DECIMAL);
/*  292 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  293 */     c = new Complex(1234.0D, 0.0D);
/*  294 */     f = new Format(c, Base.DECIMAL);
/*  295 */     System.out.println(f.formatStandard(Base.DECIMAL, 5, 20, true, true));
/*  296 */     c = new Complex(12345.0D, 0.0D);
/*  297 */     f = new Format(c, Base.DECIMAL);
/*  298 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  299 */     c = new Complex(123456.0D, 0.0D);
/*  300 */     f = new Format(c, Base.DECIMAL);
/*  301 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  302 */     c = new Complex(1234567.0D, 0.0D);
/*  303 */     f = new Format(c, Base.DECIMAL);
/*  304 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  305 */     c = new Complex(12345678.0D, 0.0D);
/*  306 */     f = new Format(c, Base.DECIMAL);
/*  307 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  308 */     c = new Complex(123456789.0D, 0.0D);
/*  309 */     f = new Format(c, Base.DECIMAL);
/*  310 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  311 */     c = new Complex(1234567891.0D, 0.0D);
/*  312 */     f = new Format(c, Base.DECIMAL);
/*  313 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  314 */     c = new Complex(12345678912.0D, 0.0D);
/*  315 */     f = new Format(c, Base.DECIMAL);
/*  316 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  317 */     c = new Complex(123456789123.0D, 0.0D);
/*  318 */     f = new Format(c, Base.DECIMAL);
/*  319 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  320 */     c = new Complex(1234567891234.0D, 0.0D);
/*  321 */     f = new Format(c, Base.DECIMAL);
/*  322 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  323 */     c = new Complex(12345678912345.0D, 0.0D);
/*  324 */     f = new Format(c, Base.DECIMAL);
/*  325 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  326 */     c = new Complex(1234.0D, -4321.0D);
/*  327 */     f = new Format(c, Base.DECIMAL);
/*  328 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  329 */     c = new Complex(1234.0D, -4321.0D);
/*  330 */     f = new Format(c, Base.DECIMAL);
/*  331 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  332 */     c = new Complex(12345.0D, -54321.0D);
/*  333 */     f = new Format(c, Base.DECIMAL);
/*  334 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  335 */     c = new Complex(123456.0D, -654321.0D);
/*  336 */     f = new Format(c, Base.DECIMAL);
/*  337 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  338 */     c = new Complex(1234567.0D, -7654321.0D);
/*  339 */     f = new Format(c, Base.DECIMAL);
/*  340 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  341 */     c = new Complex(12345678.0D, -87654321.0D);
/*  342 */     f = new Format(c, Base.DECIMAL);
/*  343 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  344 */     c = new Complex(123456789.0D, -987654321.0D);
/*  345 */     f = new Format(c, Base.DECIMAL);
/*  346 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  347 */     c = new Complex(234567891.0D, -1987654321.0D);
/*  348 */     f = new Format(c, Base.DECIMAL);
/*  349 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  350 */     c = new Complex(1234567891.0D, -987654321.0D);
/*  351 */     f = new Format(c, Base.DECIMAL);
/*  352 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*  353 */     c = new Complex(1234567891.0D, -1987654321.0D);
/*  354 */     f = new Format(c, Base.DECIMAL);
/*  355 */     System.out.println(f.formatStandard(Base.DECIMAL, 9, 20, true, true));
/*      */   }
/*      */ 
/*      */   private static class DoubleFormat
/*      */   {
/*      */     public boolean negative;
/*      */     public Vector<Integer> digits;
/*      */     public boolean exponentNegative;
/*      */     public Vector<Integer> exponentDigits;
/*      */     public int exponent;
/*      */     public boolean infinity;
/*      */     public boolean NaN;
/*      */     public boolean zero;
/*      */     public int base;
/*      */     public static final long BIAS = 1023L;
/*      */     public static final long E_MAX = 1023L;
/*      */     public static final long E_MIN = -1022L;
/*      */ 
/*      */     DoubleFormat(double number, Base base)
/*      */     {
/*  401 */       this.digits = new Vector();
/*  402 */       this.exponentDigits = new Vector();
/*  403 */       this.infinity = Double.isInfinite(number);
/*  404 */       this.NaN = Double.isNaN(number);
/*  405 */       this.zero = (number == 0.0D);
/*  406 */       switch (Format.1.$SwitchMap$com$dukascopy$calculator$Base[base.ordinal()]) {
/*      */       case 1:
/*  408 */         this.base = 2;
/*  409 */         formatBin(number);
/*  410 */         break;
/*      */       case 2:
/*  412 */         this.base = 8;
/*  413 */         formatOct(number);
/*  414 */         break;
/*      */       case 3:
/*  416 */         this.base = 16;
/*  417 */         formatHex(number);
/*  418 */         break;
/*      */       default:
/*  420 */         this.base = 10;
/*  421 */         formatDec(number);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void round(int sigDigits)
/*      */     {
/*  432 */       if (sigDigits >= this.digits.size()) return;
/*      */ 
/*  434 */       boolean carry = false;
/*  435 */       for (int i = this.digits.size() - 1; i >= 0; i--) {
/*  436 */         if (i > sigDigits) {
/*  437 */           this.digits.removeElementAt(i);
/*  438 */         } else if (i == sigDigits) {
/*  439 */           int digit = ((Integer)this.digits.elementAt(i)).intValue();
/*  440 */           this.digits.removeElementAt(i);
/*  441 */           if (2 * digit < this.base) break;
/*      */         } else {
/*  443 */           int digit = ((Integer)this.digits.elementAt(i)).intValue();
/*  444 */           digit = (digit + 1) % this.base;
/*  445 */           this.digits.setElementAt(Integer.valueOf(digit), i);
/*  446 */           if (digit != 0) break;
/*  447 */           carry = i == 0;
/*      */         }
/*      */       }
/*      */ 
/*  451 */       if (carry)
/*      */       {
/*  453 */         this.digits.add(0, Integer.valueOf(1));
/*  454 */         this.digits.removeElementAt(sigDigits);
/*  455 */         if (this.exponentNegative) {
/*  456 */           for (int i = this.exponentDigits.size() - 1; i >= 0; i--) {
/*  457 */             int digit = ((Integer)this.exponentDigits.elementAt(i)).intValue();
/*  458 */             digit = (digit - 1) % this.base;
/*  459 */             this.exponentDigits.setElementAt(Integer.valueOf(digit), i);
/*  460 */             if (digit == 0) break;
/*  461 */             carry = i == 0;
/*      */           }
/*  463 */           if (carry)
/*  464 */             this.exponentDigits.removeElementAt(0);
/*      */         } else {
/*  466 */           for (int i = this.exponentDigits.size() - 1; i >= 0; i--) {
/*  467 */             int digit = ((Integer)this.exponentDigits.elementAt(i)).intValue();
/*  468 */             digit = (digit + 1) % this.base;
/*  469 */             this.exponentDigits.setElementAt(Integer.valueOf(digit), i);
/*  470 */             if (digit != 0) break;
/*  471 */             carry = i == 0;
/*      */           }
/*      */         }
/*  473 */         if (carry)
/*  474 */           this.exponentDigits.insertElementAt(Integer.valueOf(1), 0);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int precision(int maxLength)
/*      */     {
/*  486 */       if (this.NaN)
/*  487 */         return maxLength > 2 ? 1 : 0;
/*  488 */       if (this.zero)
/*  489 */         return maxLength > 0 ? 1 : 0;
/*  490 */       if (this.infinity) {
/*  491 */         if (this.negative) {
/*  492 */           return maxLength > 1 ? 1 : 0;
/*      */         }
/*  494 */         return maxLength > 0 ? 1 : 0;
/*  495 */       }int length = this.digits.size();
/*  496 */       for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  497 */         length--;
/*  498 */       if (this.exponentNegative)
/*      */       {
/*  500 */         length += this.exponent + 1;
/*  501 */         if (this.negative) length++;
/*  502 */         int result = this.digits.size() - (length - maxLength);
/*  503 */         if (result > this.digits.size()) result = this.digits.size();
/*  504 */         if (result < 0) result = 0;
/*  505 */         return result;
/*      */       }
/*  507 */       if (length > this.exponent + 1) {
/*  508 */         int basedigits = length - (this.exponent + 1);
/*  509 */         length++;
/*  510 */         if (this.negative) length++;
/*  511 */         if (length <= maxLength)
/*  512 */           return this.digits.size();
/*  513 */         if (length - maxLength > basedigits + 1)
/*  514 */           return 0;
/*  515 */         if (length - maxLength == basedigits + 1) {
/*  516 */           return this.digits.size() - basedigits;
/*      */         }
/*  518 */         return this.digits.size() - (length - maxLength);
/*      */       }
/*  520 */       length = this.exponent + 1;
/*  521 */       if (this.negative) length++;
/*  522 */       if (length <= maxLength) {
/*  523 */         return this.digits.size();
/*      */       }
/*  525 */       return 0;
/*      */     }
/*      */ 
/*      */     public int scientificPrecision(int maxLength)
/*      */     {
/*  538 */       if (this.NaN)
/*  539 */         return maxLength > 2 ? 1 : 0;
/*  540 */       if (this.zero)
/*  541 */         return maxLength > 0 ? 1 : 0;
/*  542 */       if (this.infinity) {
/*  543 */         if (this.negative) {
/*  544 */           return maxLength > 1 ? 1 : 0;
/*      */         }
/*  546 */         return maxLength > 0 ? 1 : 0;
/*  547 */       }int z = this.digits.size();
/*  548 */       for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  549 */         z--;
/*  550 */       int length = scientificLength();
/*      */ 
/*  555 */       if (maxLength >= length)
/*  556 */         return z;
/*  557 */       if (length - maxLength < z - 1)
/*  558 */         return z - (length - maxLength);
/*  559 */       if (length - maxLength <= z) {
/*  560 */         return 1;
/*      */       }
/*  562 */       return 0;
/*      */     }
/*      */ 
/*      */     public int scientificBPrecision(int maxLength)
/*      */     {
/*  573 */       if (this.NaN)
/*  574 */         return maxLength > 2 ? 1 : 0;
/*  575 */       if (this.zero)
/*  576 */         return maxLength > 0 ? 1 : 0;
/*  577 */       if (this.infinity) {
/*  578 */         if (this.negative) {
/*  579 */           return maxLength > 1 ? 1 : 0;
/*      */         }
/*  581 */         return maxLength > 0 ? 1 : 0;
/*  582 */       }int z = this.digits.size();
/*  583 */       for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  584 */         z--;
/*  585 */       int length = scientificBLength();
/*      */ 
/*  590 */       if (maxLength >= length)
/*  591 */         return z;
/*  592 */       if (length - maxLength < z - 1)
/*  593 */         return z - (length - maxLength);
/*  594 */       if (length - maxLength <= z) {
/*  595 */         return 1;
/*      */       }
/*  597 */       return 0;
/*      */     }
/*      */ 
/*      */     public int length()
/*      */     {
/*  605 */       if (this.NaN)
/*  606 */         return 3;
/*  607 */       if (this.zero)
/*  608 */         return 1;
/*  609 */       if (this.infinity) {
/*  610 */         if (this.negative) {
/*  611 */           return 2;
/*      */         }
/*  613 */         return 1;
/*  614 */       }int length = this.digits.size();
/*  615 */       for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  616 */         length--;
/*  617 */       if (this.exponentNegative)
/*      */       {
/*  619 */         length += this.exponent + 1;
/*      */       }
/*  621 */       else if (length > this.exponent + 1)
/*  622 */         length++;
/*      */       else {
/*  624 */         length = this.exponent + 1;
/*      */       }
/*  626 */       if (this.negative) length++;
/*  627 */       return length;
/*      */     }
/*      */ 
/*      */     public int scientificLength()
/*      */     {
/*  635 */       if (this.NaN)
/*  636 */         return 3;
/*  637 */       if (this.zero)
/*  638 */         return 1;
/*  639 */       if (this.infinity) {
/*  640 */         if (this.negative) {
/*  641 */           return 2;
/*      */         }
/*  643 */         return 1;
/*  644 */       }int length = 4;
/*  645 */       if (this.negative)
/*  646 */         length++;
/*  647 */       if (this.exponentNegative)
/*  648 */         length++;
/*  649 */       int z = this.digits.size();
/*  650 */       for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  651 */         z--;
/*  652 */       length += z;
/*  653 */       if (z == 1)
/*  654 */         length--;
/*  655 */       z = 0;
/*  656 */       int i = 0;
/*  657 */       for (; (i < this.exponentDigits.size()) && (((Integer)this.exponentDigits.elementAt(i)).intValue() == 0); i++)
/*  658 */         z++;
/*  659 */       length += this.exponentDigits.size() - z;
/*  660 */       return length;
/*      */     }
/*      */ 
/*      */     public int exponentDigits()
/*      */     {
/*  669 */       int z = 0;
/*  670 */       int i = 0;
/*  671 */       for (; (i < this.exponentDigits.size()) && (((Integer)this.exponentDigits.elementAt(i)).intValue() == 0); i++)
/*  672 */         z++;
/*  673 */       return this.exponentDigits.size() - z;
/*      */     }
/*      */ 
/*      */     public String standard()
/*      */     {
/*  682 */       if (this.NaN)
/*  683 */         return "NaN";
/*  684 */       if (this.zero)
/*  685 */         return "0";
/*  686 */       if (this.infinity) {
/*  687 */         if (this.negative) {
/*  688 */           return "-&#8734;";
/*      */         }
/*  690 */         return "&#8734;";
/*  691 */       }StringBuilder stringBuffer = new StringBuilder();
/*      */ 
/*  697 */       int z = this.digits.size();
/*  698 */       if (z > 0)
/*  699 */         for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  700 */           z--;
/*  701 */       if (this.negative)
/*  702 */         stringBuffer.append("-");
/*      */       int i;
/*  703 */       if (this.exponentNegative)
/*      */       {
/*  706 */         stringBuffer.append("0.");
/*  707 */         for (int i = 0; i < this.exponent - 1; i++)
/*  708 */           stringBuffer.append("0");
/*  709 */         for (int i = 0; i < z; i++)
/*  710 */           stringBuffer.append(getDigit(((Integer)this.digits.elementAt(i)).intValue()));
/*      */       } else {
/*  712 */         for (i = 0; i < Math.max(this.exponent, this.digits.size()); )
/*      */         {
/*  714 */           if (i >= Math.max(this.exponent + 1, z)) break;
/*  715 */           if (i == this.exponent + 1)
/*  716 */             stringBuffer.append(".");
/*  717 */           if (i < this.digits.size())
/*  718 */             stringBuffer.append(getDigit(((Integer)this.digits.elementAt(i)).intValue()));
/*      */           else
/*  720 */             stringBuffer.append("0");
/*  713 */           i++;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  723 */       return stringBuffer.toString();
/*      */     }
/*      */ 
/*      */     public String scientific()
/*      */     {
/*  733 */       if (this.NaN)
/*  734 */         return "NaN";
/*  735 */       if (this.zero)
/*  736 */         return "0&#215;10<sup>0</sup>";
/*  737 */       if (this.infinity) {
/*  738 */         if (this.negative) {
/*  739 */           return "-&#8734;";
/*      */         }
/*  741 */         return "&#8734;";
/*  742 */       }StringBuilder stringBuffer = new StringBuilder();
/*  743 */       int z = this.digits.size();
/*  744 */       for (int i = this.digits.size() - 1; ((Integer)this.digits.elementAt(i)).intValue() == 0; i--)
/*  745 */         z--;
/*  746 */       if (this.negative)
/*  747 */         stringBuffer.append("-");
/*  748 */       stringBuffer.append(getDigit(((Integer)this.digits.elementAt(0)).intValue()));
/*  749 */       if (z > 1)
/*  750 */         stringBuffer.append(".");
/*  751 */       for (int i = 1; i < z; i++)
/*  752 */         stringBuffer.append(getDigit(((Integer)this.digits.elementAt(i)).intValue()));
/*  753 */       stringBuffer.append("&#215;10<sup>");
/*  754 */       if (this.exponent == 0) {
/*  755 */         stringBuffer.append("0");
/*      */       } else {
/*  757 */         if (this.exponentNegative) {
/*  758 */           stringBuffer.append("-");
/*      */         }
/*  760 */         z = 0;
/*  761 */         for (int i = 0; ((Integer)this.exponentDigits.elementAt(i)).intValue() == 0; i++)
/*  762 */           z++;
/*  763 */         for (int i = z; i < this.exponentDigits.size(); i++)
/*  764 */           stringBuffer.append(getDigit(((Integer)this.exponentDigits.elementAt(i)).intValue()));
/*      */       }
/*  766 */       stringBuffer.append("</sup>");
/*  767 */       return stringBuffer.toString();
/*      */     }
/*      */ 
/*      */     public String scientificB()
/*      */     {
/*  778 */       if (this.base != 2)
/*  779 */         return scientific();
/*  780 */       String s = scientific();
/*  781 */       String t = "";
/*  782 */       int i = 0;
/*  783 */       for (; s.charAt(i) != ';'; i++)
/*  784 */         t = new StringBuilder().append(t).append(s.charAt(i)).toString();
/*  785 */       t = new StringBuilder().append(t).append(";2<sup>").toString();
/*  786 */       i++;
/*  787 */       i++;
/*  788 */       i++;
/*  789 */       i++;
/*  790 */       i++;
/*  791 */       i++;
/*  792 */       i++;
/*  793 */       i++; if (s.charAt(i) == '-')
/*  794 */         i++;
/*  795 */       int exp = 0;
/*  796 */       for (; s.charAt(i) != '<'; i++) {
/*  797 */         exp <<= 1;
/*  798 */         if (s.charAt(i) != '1') continue; exp |= 1;
/*      */       }
/*  800 */       t = new StringBuilder().append(t).append(Integer.toString(exp)).toString();
/*  801 */       t = new StringBuilder().append(t).append("</sup>").toString();
/*  802 */       return t;
/*      */     }
/*      */ 
/*      */     public int scientificBLength()
/*      */     {
/*  810 */       if (this.base != 2)
/*  811 */         return scientificLength();
/*  812 */       int l = scientificLength();
/*  813 */       String s = scientific();
/*  814 */       int b = 0;
/*  815 */       int i = 0;
/*  816 */       while (s.charAt(i) != ';') i++;
/*      */ 
/*  818 */       i++;
/*  819 */       i++;
/*  820 */       i++;
/*  821 */       i++;
/*  822 */       i++;
/*  823 */       i++;
/*  824 */       i++;
/*  825 */       i++; if (s.charAt(i) == '-')
/*  826 */         i++;
/*  827 */       int exp = 0;
/*  828 */       for (; s.charAt(i) != '<'; i++) {
/*  829 */         b++;
/*  830 */         exp <<= 1;
/*  831 */         if (s.charAt(i) != '1') continue; exp |= 1;
/*      */       }
/*  833 */       int d = (int)Math.floor(Math.log10(exp));
/*      */ 
/*  835 */       return l - b + d - 1;
/*      */     }
/*      */ 
/*      */     private void formatDec(double number)
/*      */     {
/*  844 */       if (Double.isNaN(number)) {
/*  845 */         this.NaN = true;
/*  846 */         return;
/*      */       }
/*  848 */       if (Double.isInfinite(number)) {
/*  849 */         this.infinity = true;
/*  850 */         long bits = Double.doubleToRawLongBits(number);
/*      */ 
/*  852 */         this.negative = ((bits & 0x0) != 0L);
/*  853 */         return;
/*      */       }
/*  855 */       if (number == 0.0D) {
/*  856 */         this.zero = true;
/*  857 */         return;
/*      */       }
/*  859 */       String s = Double.toString(number);
/*      */ 
/*  861 */       boolean readingSignificand = true;
/*  862 */       boolean readPoint = false;
/*  863 */       boolean firstDigitRead = false;
/*  864 */       int leadingDigits = 0;
/*  865 */       this.exponent = 0;
/*  866 */       this.exponentNegative = false;
/*      */ 
/*  868 */       this.negative = false;
/*  869 */       char[] arr$ = s.toCharArray(); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Character c = Character.valueOf(arr$[i$]);
/*  870 */         if (readingSignificand) {
/*  871 */           if (c.charValue() == '-') {
/*  872 */             this.negative = true;
/*  873 */           } else if ((!readPoint) && (c.charValue() == '.')) {
/*  874 */             readPoint = true;
/*  875 */           } else if (c.charValue() == 'E') {
/*  876 */             readingSignificand = false;
/*      */           } else {
/*  878 */             int digit = getInt(c.charValue());
/*  879 */             if (firstDigitRead) {
/*  880 */               firstDigitRead = true;
/*  881 */               if (digit != 0)
/*  882 */                 leadingDigits++;
/*      */             }
/*  884 */             else if (!readPoint) {
/*  885 */               leadingDigits++;
/*      */             }
/*  887 */             this.digits.add(Integer.valueOf(digit));
/*      */           }
/*      */         }
/*  890 */         else if (c.charValue() == '-') {
/*  891 */           this.exponentNegative = true;
/*      */         } else {
/*  893 */           int digit = getInt(c.charValue());
/*      */ 
/*  895 */           this.exponent *= 10;
/*  896 */           this.exponent += digit;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  901 */       this.exponent += leadingDigits - 1;
/*  902 */       char[] arr$ = Integer.toString(this.exponent).toCharArray(); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Character c = Character.valueOf(arr$[i$]);
/*      */ 
/*  904 */         this.exponentDigits.add(Integer.valueOf(getInt(c.charValue())));
/*      */       }
/*      */     }
/*      */ 
/*      */     private void formatHex(double number)
/*      */     {
/*  915 */       long bits = Double.doubleToRawLongBits(number);
/*      */ 
/*  917 */       this.negative = ((bits & 0x0) != 0L);
/*  918 */       long exponent = (bits & 0x0) >>> 52;
/*  919 */       long significand = bits & 0xFFFFFFFF;
/*  920 */       exponent -= 1023L;
/*      */ 
/*  922 */       boolean denormalised = exponent == -1023L;
/*  923 */       this.exponentNegative = (exponent < 0L);
/*  924 */       if (this.exponentNegative) {
/*  925 */         exponent = -exponent;
/*      */       }
/*      */ 
/*  928 */       if (!denormalised) {
/*  929 */         significand |= 4503599627370496L;
/*      */       }
/*      */       else {
/*  932 */         significand <<= 1;
/*  933 */         if (significand != 0L)
/*  934 */           while ((significand & 0x0) == 0L) {
/*  935 */             exponent += 1L;
/*  936 */             significand <<= 1;
/*      */           }
/*      */       }
/*  939 */       while (exponent % 4L != 0L) {
/*  940 */         significand <<= 1;
/*  941 */         if (this.exponentNegative) {
/*  942 */           exponent += 1L; continue;
/*      */         }
/*  944 */         exponent -= 1L;
/*      */       }
/*  946 */       exponent >>>= 2;
/*  947 */       exponent = (int)exponent;
/*  948 */       for (int i = 0; i < 14; i++) {
/*  949 */         int digit = (int)(significand & 0xF);
/*  950 */         this.digits.add(0, Integer.valueOf(digit));
/*  951 */         significand >>>= 4;
/*      */       }
/*  953 */       for (int i = 0; i < 3; i++) {
/*  954 */         int digit = (int)(exponent & 0xF);
/*  955 */         this.exponentDigits.add(0, Integer.valueOf(digit));
/*  956 */         exponent >>>= 4;
/*      */       }
/*      */     }
/*      */ 
/*      */     private void formatOct(double number)
/*      */     {
/*  965 */       long bits = Double.doubleToRawLongBits(number);
/*      */ 
/*  967 */       this.negative = ((bits & 0x0) != 0L);
/*  968 */       long exponent = (bits & 0x0) >>> 52;
/*  969 */       long significand = bits & 0xFFFFFFFF;
/*  970 */       exponent -= 1023L;
/*  971 */       boolean denormalised = exponent == -1023L;
/*  972 */       this.exponentNegative = (exponent < 0L);
/*  973 */       if (this.exponentNegative)
/*  974 */         exponent = -exponent;
/*  975 */       if (!denormalised) {
/*  976 */         significand |= 4503599627370496L;
/*      */       } else {
/*  978 */         significand <<= 1;
/*  979 */         if (significand != 0L)
/*  980 */           while ((significand & 0x0) == 0L) {
/*  981 */             exponent += 1L;
/*  982 */             significand <<= 1;
/*      */           }
/*      */       }
/*  985 */       while (exponent % 3L != 0L) {
/*  986 */         significand <<= 1;
/*  987 */         if (this.exponentNegative) {
/*  988 */           exponent += 1L; continue;
/*      */         }
/*  990 */         exponent -= 1L;
/*      */       }
/*  992 */       exponent /= 3L;
/*  993 */       exponent = (int)exponent;
/*  994 */       significand <<= 2;
/*  995 */       for (int i = 0; i < 19; i++) {
/*  996 */         int digit = (int)(significand & 0x7);
/*  997 */         this.digits.add(0, Integer.valueOf(digit));
/*  998 */         significand >>>= 3;
/*      */       }
/* 1000 */       for (int i = 0; i < 4; i++) {
/* 1001 */         int digit = (int)(exponent & 0x7);
/* 1002 */         this.exponentDigits.add(0, Integer.valueOf(digit));
/* 1003 */         exponent >>>= 3;
/*      */       }
/*      */     }
/*      */ 
/*      */     private void formatBin(double number)
/*      */     {
/* 1012 */       long bits = Double.doubleToRawLongBits(number);
/*      */ 
/* 1014 */       this.negative = ((bits & 0x0) != 0L);
/* 1015 */       long exponent = (bits & 0x0) >>> 52;
/* 1016 */       long significand = bits & 0xFFFFFFFF;
/* 1017 */       exponent -= 1023L;
/* 1018 */       boolean denormalised = exponent == -1023L;
/* 1019 */       this.exponentNegative = (exponent < 0L);
/* 1020 */       if (this.exponentNegative)
/* 1021 */         exponent = -exponent;
/* 1022 */       if (!denormalised) {
/* 1023 */         significand |= 4503599627370496L;
/*      */       } else {
/* 1025 */         significand <<= 1;
/* 1026 */         if (significand != 0L)
/* 1027 */           while ((significand & 0x0) == 0L) {
/* 1028 */             exponent += 1L;
/* 1029 */             significand <<= 1;
/*      */           }
/*      */       }
/* 1032 */       exponent = (int)exponent;
/* 1033 */       for (int i = 0; i < 53; i++) {
/* 1034 */         int digit = (int)(significand & 1L);
/* 1035 */         this.digits.add(0, Integer.valueOf(digit));
/* 1036 */         significand >>>= 1;
/*      */       }
/* 1038 */       for (int i = 0; i < 9; i++) {
/* 1039 */         int digit = (int)(exponent & 1L);
/* 1040 */         this.exponentDigits.add(0, Integer.valueOf(digit));
/* 1041 */         exponent >>>= 1;
/*      */       }
/*      */     }
/*      */ 
/*      */     private static int getInt(char c)
/*      */     {
/* 1052 */       switch (c) {
/*      */       case '0':
/* 1054 */         return 0;
/*      */       case '1':
/* 1056 */         return 1;
/*      */       case '2':
/* 1058 */         return 2;
/*      */       case '3':
/* 1060 */         return 3;
/*      */       case '4':
/* 1062 */         return 4;
/*      */       case '5':
/* 1064 */         return 5;
/*      */       case '6':
/* 1066 */         return 6;
/*      */       case '7':
/* 1068 */         return 7;
/*      */       case '8':
/* 1070 */         return 8;
/*      */       case '9':
/* 1072 */         return 9;
/*      */       }
/* 1074 */       return -1;
/*      */     }
/*      */ 
/*      */     private static char getDigit(int d)
/*      */     {
/* 1084 */       switch (d) {
/*      */       case 0:
/* 1086 */         return '0';
/*      */       case 1:
/* 1088 */         return '1';
/*      */       case 2:
/* 1090 */         return '2';
/*      */       case 3:
/* 1092 */         return '3';
/*      */       case 4:
/* 1094 */         return '4';
/*      */       case 5:
/* 1096 */         return '5';
/*      */       case 6:
/* 1098 */         return '6';
/*      */       case 7:
/* 1100 */         return '7';
/*      */       case 8:
/* 1102 */         return '8';
/*      */       case 9:
/* 1104 */         return '9';
/*      */       case 10:
/* 1106 */         return 'A';
/*      */       case 11:
/* 1108 */         return 'B';
/*      */       case 12:
/* 1110 */         return 'C';
/*      */       case 13:
/* 1112 */         return 'D';
/*      */       case 14:
/* 1114 */         return 'E';
/*      */       case 15:
/* 1116 */         return 'F';
/*      */       }
/* 1118 */       return '?';
/*      */     }
/*      */   }
/*      */ 
/*      */   public static enum Notation
/*      */   {
/*   27 */     STANDARD, SCIENTIFIC, SCIENTIFICB, NONE;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.complex.Format
 * JD-Core Version:    0.6.0
 */