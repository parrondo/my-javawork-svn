/*     */ package com.dukascopy.calculator.complex;
/*     */ 
/*     */ import com.dukascopy.calculator.Base;
/*     */ import com.dukascopy.calculator.Notation;
/*     */ import com.dukascopy.calculator.StringArray;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import java.util.Vector;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DoubleFormat
/*     */ {
/*  11 */   private static final Logger LOGGER = LoggerFactory.getLogger(DoubleFormat.class);
/*     */   private final double number;
/*     */   private Base base;
/*     */   private Notation notation;
/*     */   private int precision;
/*     */   private Vector<Integer> digits;
/*     */   private long exponent;
/*     */   public static final long BIAS = 1023L;
/*     */   public static final long E_MAX = 1023L;
/*     */   public static final long E_MIN = -1022L;
/* 703 */   public static final Vector<String> basePoint = new Vector(Arrays.asList(new String[] { "." }));
/*     */   public static final String startExponent = "<sup>";
/*     */   public static final String endExponent = "</sup>";
/* 722 */   public static final Vector<String> minus = new Vector(Arrays.asList(new String[] { "&#8722;" }));
/*     */ 
/* 729 */   public static final Vector<String> NaN = new Vector(Arrays.asList(new String[] { "N", "a", "N" }));
/*     */ 
/* 736 */   public static final Vector<String> infinity = new Vector(Arrays.asList(new String[] { "&#8734;" }));
/*     */ 
/* 743 */   public static final Vector<String> minusInfinity = new Vector(Arrays.asList(new String[] { "&#8722;", "&#8734;" }));
/*     */   public static final String startHTML = "<html>";
/*     */   public static final String endHTML = "</html>";
/* 762 */   public static final Vector<String> imPrefix = new Vector(Arrays.asList(new String[] { "i" }));
/*     */ 
/* 769 */   public static final Vector<String> argumentPrefix = new Vector(Arrays.asList(new String[] { "e" }));
/*     */ 
/* 776 */   public static final Vector<String> plus = new Vector(Arrays.asList(new String[] { "+" }));
/*     */ 
/* 783 */   public final HTMLStringRepresentation NullRepresentation = new HTMLStringRepresentation(new StringArray(), 0, null);
/*     */ 
/*     */   public DoubleFormat(double number, Base base)
/*     */   {
/*  18 */     this.number = number;
/*  19 */     this.base = base;
/*  20 */     this.notation = new Notation();
/*  21 */     setPrecision(maxPrecision());
/*     */   }
/*     */ 
/*     */   public void setPrecision(int precision)
/*     */   {
/*  31 */     if (precision == this.precision) return;
/*  32 */     this.precision = precision;
/*  33 */     reset();
/*  34 */     if (this.digits.size() <= precision) return;
/*     */ 
/*  36 */     int lastDigit = ((Integer)this.digits.get(precision)).intValue();
/*  37 */     this.digits.setSize(precision);
/*     */ 
/*  39 */     if (lastDigit < midDigit(this.base))
/*     */     {
/*  41 */       while ((this.digits.size() > 0) && (((Integer)this.digits.lastElement()).intValue() == 0))
/*  42 */         this.digits.setSize(this.digits.size() - 1);
/*  43 */       return;
/*     */     }
/*     */ 
/*  46 */     if (roundUp()) return;
/*  47 */     this.exponent += 1L;
/*     */   }
/*     */ 
/*     */   public void setBase(Base base)
/*     */   {
/*  55 */     this.base = base;
/*     */   }
/*     */ 
/*     */   public void setNotation(Notation notation)
/*     */   {
/*  63 */     this.notation = notation;
/*     */   }
/*     */ 
/*     */   private boolean roundUp()
/*     */   {
/*  73 */     int position = this.digits.size() - 1;
/*  74 */     if (position == -1)
/*     */     {
/*  77 */       this.digits.add(Integer.valueOf(1));
/*  78 */       return false;
/*     */     }
/*  80 */     int digit = ((Integer)this.digits.get(position)).intValue();
/*  81 */     digit++; if (digit == baseInt(this.base)) {
/*  82 */       this.digits.remove(position);
/*  83 */       return roundUp();
/*     */     }
/*  85 */     this.digits.set(position, Integer.valueOf(digit));
/*  86 */     return true;
/*     */   }
/*     */ 
/*     */   public HTMLStringRepresentation representation()
/*     */   {
/* 183 */     return new HTMLStringRepresentation();
/*     */   }
/*     */ 
/*     */   private HTMLStringRepresentation standard()
/*     */   {
/* 192 */     Vector stringBuffer = new Vector();
/* 193 */     int length = 0;
/* 194 */     if (this.number < 0.0D) {
/* 195 */       stringBuffer.addAll(minus);
/* 196 */       length++;
/*     */     }
/*     */     int i;
/* 198 */     if (this.exponent < 0L) {
/* 199 */       stringBuffer.add("0");
/* 200 */       length++;
/* 201 */       stringBuffer.addAll(basePoint);
/* 202 */       length++;
/* 203 */       for (int i = 0; i < -this.exponent - 1L; i++) {
/* 204 */         stringBuffer.add("0");
/* 205 */         length++;
/*     */       }
/* 207 */       for (int i = 0; i < this.digits.size(); i++) {
/* 208 */         stringBuffer.add(Character.toString(getDigit(((Integer)this.digits.elementAt(i)).intValue())));
/*     */ 
/* 210 */         length++;
/*     */       }
/*     */     } else {
/* 213 */       for (i = 0; i < StrictMath.max(this.exponent + 1L, this.digits.size()); )
/*     */       {
/* 215 */         if (i >= StrictMath.max(this.exponent + 1L, this.digits.size())) break;
/* 216 */         if (i == this.exponent + 1L) {
/* 217 */           stringBuffer.addAll(basePoint);
/* 218 */           length++;
/*     */         }
/* 220 */         if (i < this.digits.size()) {
/* 221 */           stringBuffer.add(Character.toString(getDigit(((Integer)this.digits.elementAt(i)).intValue())));
/*     */         }
/*     */         else {
/* 224 */           stringBuffer.add("0");
/*     */         }
/* 226 */         length++;
/*     */ 
/* 214 */         i++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 229 */     StringArray t = new StringArray();
/* 230 */     t.add(stringBuffer);
/* 231 */     return new HTMLStringRepresentation(t, length, null);
/*     */   }
/*     */ 
/*     */   public HTMLStringRepresentation scientific()
/*     */   {
/* 241 */     Vector stringBuffer = new Vector();
/* 242 */     int length = 0;
/*     */ 
/* 244 */     if (this.number < 0.0D) {
/* 245 */       stringBuffer.addAll(minus);
/* 246 */       length++;
/*     */     }
/* 248 */     boolean first = this.digits.size() > 1;
/* 249 */     for (Integer i : this.digits) {
/* 250 */       stringBuffer.add(Character.toString(getDigit(i.intValue())));
/* 251 */       length++;
/* 252 */       if (first) {
/* 253 */         stringBuffer.addAll(basePoint);
/* 254 */         length++;
/* 255 */         first = false;
/*     */       }
/*     */     }
/*     */ 
/* 259 */     stringBuffer.add(getEString());
/* 260 */     length += getEStringLength();
/*     */ 
/* 262 */     int e = (int)this.exponent;
/* 263 */     String q = new String("<sup>");
/* 264 */     if (this.exponent < 0L)
/*     */     {
/* 266 */       q = q.concat((String)minus.firstElement());
/* 267 */       stringBuffer.add(q);
/* 268 */       length++;
/* 269 */       e = -e;
/*     */     }
/* 271 */     String s = null;
/* 272 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[this.base.ordinal()]) {
/*     */     case 1:
/* 274 */       s = Integer.toOctalString(e);
/* 275 */       break;
/*     */     case 2:
/* 277 */       s = Integer.toHexString(e).toUpperCase();
/* 278 */       break;
/*     */     default:
/* 280 */       s = Integer.toString(e);
/*     */     }
/* 282 */     for (int i = 0; i < s.length(); i++) {
/* 283 */       String t = Character.toString(s.charAt(i));
/* 284 */       if (this.exponent >= 0L)
/* 285 */         t = q.concat(t);
/* 286 */       if (i == s.length() - 1)
/* 287 */         t = t.concat("</sup>");
/* 288 */       stringBuffer.add(s);
/*     */     }
/* 290 */     length += s.length();
/* 291 */     StringArray t = new StringArray();
/* 292 */     t.add(stringBuffer);
/* 293 */     return new HTMLStringRepresentation(t, length, null);
/*     */   }
/*     */ 
/*     */   private void reset()
/*     */   {
/* 302 */     this.digits = new Vector();
/*     */ 
/* 304 */     if (this.number == 0.0D) {
/* 305 */       this.digits.add(Integer.valueOf(0));
/* 306 */       this.exponent = 0L;
/* 307 */       return;
/*     */     }
/* 309 */     if ((Double.isInfinite(this.number)) || (Double.isNaN(this.number))) return;
/* 310 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[this.base.ordinal()]) {
/*     */     case 3:
/* 312 */       setupBin();
/* 313 */       break;
/*     */     case 1:
/* 315 */       setupOct();
/* 316 */       break;
/*     */     case 2:
/* 318 */       setupHex();
/* 319 */       break;
/*     */     default:
/* 321 */       setupDec();
/*     */     }
/* 323 */     if (StrictMath.abs(this.number) < 1.0D)
/* 324 */       this.exponent = (-this.exponent);
/*     */   }
/*     */ 
/*     */   private void setupDec()
/*     */   {
/* 331 */     BigDecimal b = new BigDecimal(this.number < 0.0D ? -this.number : this.number);
/*     */ 
/* 333 */     this.exponent = (b.precision() - b.scale() - 1);
/* 334 */     int exponentSign = (int)StrictMath.signum((float)this.exponent);
/* 335 */     if (exponentSign < 0)
/* 336 */       this.exponent = (-1L * this.exponent);
/* 337 */     String s = b.unscaledValue().toString();
/* 338 */     char[] arr$ = s.toCharArray(); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Character c = Character.valueOf(arr$[i$]);
/* 339 */       this.digits.add(Integer.valueOf(getInt(c.charValue())));
/*     */     }
/* 341 */     while ((this.digits.size() > 0) && (((Integer)this.digits.lastElement()).intValue() == 0))
/* 342 */       this.digits.setSize(this.digits.size() - 1);
/*     */   }
/*     */ 
/*     */   private void setupHex()
/*     */   {
/* 348 */     long bits = Double.doubleToRawLongBits(this.number);
/* 349 */     this.exponent = ((bits & 0x0) >>> 52);
/* 350 */     long significand = bits & 0xFFFFFFFF;
/* 351 */     this.exponent -= 1023L;
/* 352 */     boolean denormalised = this.exponent == -1023L;
/* 353 */     int exponentSign = (int)StrictMath.signum((float)this.exponent);
/* 354 */     if (exponentSign == -1) {
/* 355 */       this.exponent = (-this.exponent);
/*     */     }
/* 357 */     if (!denormalised) {
/* 358 */       significand |= 4503599627370496L;
/*     */     } else {
/* 360 */       significand <<= 1;
/* 361 */       if (significand != 0L)
/* 362 */         while ((significand & 0x0) == 0L) {
/* 363 */           this.exponent += 1L;
/* 364 */           significand <<= 1;
/*     */         }
/*     */     }
/* 367 */     while (this.exponent % 4L != 0L) {
/* 368 */       significand <<= 1;
/* 369 */       if (exponentSign == -1) {
/* 370 */         this.exponent += 1L; continue;
/*     */       }
/* 372 */       this.exponent -= 1L;
/*     */     }
/* 374 */     this.exponent >>>= 2;
/* 375 */     for (int i = 0; i < 14; i++) {
/* 376 */       int digit = (int)(significand & 0xF);
/* 377 */       this.digits.add(0, Integer.valueOf(digit));
/* 378 */       significand >>>= 4;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setupOct()
/*     */   {
/* 385 */     long bits = Double.doubleToRawLongBits(this.number);
/* 386 */     this.exponent = ((bits & 0x0) >>> 52);
/* 387 */     long significand = bits & 0xFFFFFFFF;
/* 388 */     this.exponent -= 1023L;
/* 389 */     boolean denormalised = this.exponent == -1023L;
/* 390 */     int exponentSign = (int)StrictMath.signum((float)this.exponent);
/* 391 */     if (exponentSign == -1)
/* 392 */       this.exponent = (-this.exponent);
/* 393 */     if (!denormalised) {
/* 394 */       significand |= 4503599627370496L;
/*     */     } else {
/* 396 */       significand <<= 1;
/* 397 */       if (significand != 0L)
/* 398 */         while ((significand & 0x0) == 0L) {
/* 399 */           this.exponent += 1L;
/* 400 */           significand <<= 1;
/*     */         }
/*     */     }
/* 403 */     while (this.exponent % 3L != 0L) {
/* 404 */       significand <<= 1;
/* 405 */       if (exponentSign == -1) {
/* 406 */         this.exponent += 1L; continue;
/*     */       }
/* 408 */       this.exponent -= 1L;
/*     */     }
/* 410 */     this.exponent /= 3L;
/* 411 */     significand <<= 2;
/* 412 */     for (int i = 0; i < 19; i++) {
/* 413 */       int digit = (int)(significand & 0x7);
/* 414 */       this.digits.add(0, Integer.valueOf(digit));
/* 415 */       significand >>>= 3;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setupBin()
/*     */   {
/* 422 */     long bits = Double.doubleToRawLongBits(this.number);
/* 423 */     this.exponent = ((bits & 0x0) >>> 52);
/* 424 */     long significand = bits & 0xFFFFFFFF;
/* 425 */     this.exponent -= 1023L;
/* 426 */     boolean denormalised = this.exponent == -1023L;
/* 427 */     int exponentSign = (int)StrictMath.signum((float)this.exponent);
/* 428 */     if (exponentSign == -1)
/* 429 */       this.exponent = (-this.exponent);
/* 430 */     if (!denormalised) {
/* 431 */       significand |= 4503599627370496L;
/*     */     } else {
/* 433 */       significand <<= 1;
/* 434 */       if (significand != 0L)
/* 435 */         while ((significand & 0x0) == 0L) {
/* 436 */           this.exponent += 1L;
/* 437 */           significand <<= 1;
/*     */         }
/*     */     }
/* 440 */     for (int i = 0; i < 53; i++) {
/* 441 */       int digit = (int)(significand & 1L);
/* 442 */       this.digits.add(0, Integer.valueOf(digit));
/* 443 */       significand >>>= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public double getNumber()
/*     */   {
/* 451 */     return this.number;
/*     */   }
/*     */ 
/*     */   public double getAbsNumber()
/*     */   {
/* 458 */     return StrictMath.abs(this.number);
/*     */   }
/*     */ 
/*     */   private static char getDigit(int d)
/*     */   {
/* 468 */     switch (d) {
/*     */     case 0:
/* 470 */       return '0';
/*     */     case 1:
/* 472 */       return '1';
/*     */     case 2:
/* 474 */       return '2';
/*     */     case 3:
/* 476 */       return '3';
/*     */     case 4:
/* 478 */       return '4';
/*     */     case 5:
/* 480 */       return '5';
/*     */     case 6:
/* 482 */       return '6';
/*     */     case 7:
/* 484 */       return '7';
/*     */     case 8:
/* 486 */       return '8';
/*     */     case 9:
/* 488 */       return '9';
/*     */     case 10:
/* 490 */       return 'A';
/*     */     case 11:
/* 492 */       return 'B';
/*     */     case 12:
/* 494 */       return 'C';
/*     */     case 13:
/* 496 */       return 'D';
/*     */     case 14:
/* 498 */       return 'E';
/*     */     case 15:
/* 500 */       return 'F';
/*     */     }
/* 502 */     return '?';
/*     */   }
/*     */ 
/*     */   private static int getInt(char c)
/*     */   {
/* 512 */     switch (c) {
/*     */     case '0':
/* 514 */       return 0;
/*     */     case '1':
/* 516 */       return 1;
/*     */     case '2':
/* 518 */       return 2;
/*     */     case '3':
/* 520 */       return 3;
/*     */     case '4':
/* 522 */       return 4;
/*     */     case '5':
/* 524 */       return 5;
/*     */     case '6':
/* 526 */       return 6;
/*     */     case '7':
/* 528 */       return 7;
/*     */     case '8':
/* 530 */       return 8;
/*     */     case '9':
/* 532 */       return 9;
/*     */     }
/* 534 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int midDigit(Base base)
/*     */   {
/* 546 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[base.ordinal()]) {
/*     */     case 3:
/* 548 */       return 1;
/*     */     case 1:
/* 550 */       return 4;
/*     */     case 2:
/* 552 */       return 8;
/*     */     }
/* 554 */     return 5;
/*     */   }
/*     */ 
/*     */   public static int baseInt(Base base)
/*     */   {
/* 567 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[base.ordinal()]) {
/*     */     case 3:
/* 569 */       return 2;
/*     */     case 1:
/* 571 */       return 8;
/*     */     case 2:
/* 573 */       return 16;
/*     */     }
/* 575 */     return 10;
/*     */   }
/*     */ 
/*     */   private int maxPrecision()
/*     */   {
/* 587 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[this.base.ordinal()]) {
/*     */     case 3:
/* 589 */       return 52;
/*     */     case 1:
/* 591 */       return 17;
/*     */     case 2:
/* 593 */       return 13;
/*     */     }
/* 595 */     return 14;
/*     */   }
/*     */ 
/*     */   public String getEString()
/*     */   {
/* 607 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[this.base.ordinal()]) {
/*     */     case 3:
/* 609 */       return "";
/*     */     case 1:
/* 611 */       return "";
/*     */     case 2:
/* 613 */       return "";
/*     */     }
/* 615 */     return "";
/*     */   }
/*     */ 
/*     */   public int getEStringLength()
/*     */   {
/* 626 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[this.base.ordinal()]) {
/*     */     case 3:
/* 628 */       return 0;
/*     */     case 1:
/* 630 */       return 0;
/*     */     case 2:
/* 632 */       return 0;
/*     */     }
/* 634 */     return 0;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 643 */     DoubleFormat d = new DoubleFormat(-4.203549222288432E-246D, Base.DECIMAL);
/* 644 */     Notation n = new Notation();
/* 645 */     n.setScientific();
/* 646 */     d.setNotation(n);
/* 647 */     HTMLStringRepresentation s = d.representation();
/* 648 */     LOGGER.debug(s.string + " (length = ");
/* 649 */     LOGGER.debug(String.valueOf(s.length));
/* 650 */     System.out.println(")");
/* 651 */     for (int p = 14; p > 0; p--) {
/* 652 */       LOGGER.debug(String.valueOf(p));
/* 653 */       LOGGER.debug(": ");
/* 654 */       LOGGER.debug(String.valueOf(p));
/* 655 */       s = d.representation();
/* 656 */       LOGGER.debug(s.string + " (length = ");
/* 657 */       LOGGER.debug(String.valueOf(s.length));
/* 658 */       System.out.println(")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class HTMLStringRepresentation
/*     */   {
/*     */     public final StringArray string;
/*     */     public final int length;
/*     */ 
/*     */     private HTMLStringRepresentation(StringArray string, int length)
/*     */     {
/* 107 */       this.string = string;
/* 108 */       this.length = length;
/*     */     }
/*     */ 
/*     */     public HTMLStringRepresentation()
/*     */     {
/*     */       String[] nan;
/* 114 */       if (Double.isNaN(DoubleFormat.this.number)) {
/* 115 */         nan = new String[] { "N", "a", "N" };
/* 116 */         this.string = new StringArray();
/* 117 */         this.string.add(nan);
/* 118 */         this.length = 3;
/* 119 */       } else if (Double.isInfinite(DoubleFormat.this.number)) {
/* 120 */         this.string = new StringArray();
/* 121 */         if (DoubleFormat.this.number < 0.0D) {
/* 122 */           this.string.add(DoubleFormat.minusInfinity);
/* 123 */           this.length = 2;
/*     */         } else {
/* 125 */           this.string.add(DoubleFormat.infinity);
/* 126 */           this.length = 1;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */         HTMLStringRepresentation h;
/*     */         HTMLStringRepresentation h;
/* 130 */         if (DoubleFormat.this.notation.standard())
/* 131 */           h = DoubleFormat.this.standard();
/*     */         else {
/* 133 */           h = DoubleFormat.this.scientific();
/*     */         }
/* 135 */         this.string = h.string;
/* 136 */         this.length = h.length;
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isOne()
/*     */     {
/* 145 */       if (this.string.size() != 1) return false;
/* 146 */       if (((Vector)this.string.firstElement()).size() != 1) return false;
/* 147 */       return ((Vector)this.string.firstElement()).elementAt(0) == "1";
/*     */     }
/*     */ 
/*     */     public boolean isMinusOne()
/*     */     {
/* 157 */       if (this.string.size() != 1) return false;
/* 158 */       if (((Vector)this.string.firstElement()).size() != 2) return false;
/* 159 */       if (((Vector)this.string.firstElement()).elementAt(0) != DoubleFormat.minus.elementAt(0))
/* 160 */         return false;
/* 161 */       return ((Vector)this.string.firstElement()).elementAt(1) == "1";
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.complex.DoubleFormat
 * JD-Core Version:    0.6.0
 */