/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import com.dukascopy.calculator.complex.Complex;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ class ParseBase
/*     */ {
/*     */   public static final long BIAS = 1023L;
/*     */   public static final long E_MAX = 1023L;
/*     */   public static final long E_MIN = -1022L;
/*     */ 
/*     */   private static long getDigit(char c)
/*     */   {
/* 226 */     switch (c) {
/*     */     case '0':
/* 228 */       return 0L;
/*     */     case '1':
/* 230 */       return 1L;
/*     */     case '2':
/* 232 */       return 2L;
/*     */     case '3':
/* 234 */       return 3L;
/*     */     case '4':
/* 236 */       return 4L;
/*     */     case '5':
/* 238 */       return 5L;
/*     */     case '6':
/* 240 */       return 6L;
/*     */     case '7':
/* 242 */       return 7L;
/*     */     case '8':
/* 244 */       return 8L;
/*     */     case '9':
/* 246 */       return 9L;
/*     */     case 'A':
/* 248 */       return 10L;
/*     */     case 'B':
/* 250 */       return 11L;
/*     */     case 'C':
/* 252 */       return 12L;
/*     */     case 'D':
/* 254 */       return 13L;
/*     */     case 'E':
/* 256 */       return 14L;
/*     */     case 'F':
/* 258 */       return 15L;
/*     */     case ':':
/*     */     case ';':
/*     */     case '<':
/*     */     case '=':
/*     */     case '>':
/*     */     case '?':
/* 260 */     case '@': } return -1L;
/*     */   }
/*     */ 
/*     */   public static Complex parseString(String s, Base b)
/*     */   {
/* 273 */     return new Complex(dparseString(s, b));
/*     */   }
/*     */ 
/*     */   public static double dparseString(String s, Base b)
/*     */   {
/* 286 */     if (b == Base.DECIMAL) {
/* 287 */       Matcher m = Number.pattern.matcher(s);
/* 288 */       m.matches();
/* 289 */       return new BigDecimal(s).doubleValue();
/*     */     }
/*     */ 
/* 292 */     Number number = Number.parseString(s, b);
/* 293 */     if (number.significand == 0L) {
/* 294 */       return 0.0D;
/*     */     }
/* 296 */     switch (1.$SwitchMap$com$dukascopy$calculator$Base[b.ordinal()]) {
/*     */     case 3:
/* 298 */       number.significand >>>= 4;
/* 299 */       break;
/*     */     case 2:
/* 301 */       number.significand >>>= 5;
/* 302 */       break;
/*     */     default:
/* 304 */       number.significand >>>= 7;
/*     */     }
/* 306 */     for (; (number.significand & 0x0) != 0L; number.exponent += 1L) {
/* 307 */       number.significand >>>= 1;
/*     */     }
/* 309 */     if (number.exponent > 1023L) {
/* 310 */       if (number.negative) {
/* 311 */         return (-1.0D / 0.0D);
/*     */       }
/* 313 */       return (1.0D / 0.0D);
/* 314 */     }if (number.exponent < -1022L)
/*     */     {
/* 316 */       if (number.exponent < -1074L) {
/* 317 */         return 0.0D;
/*     */       }
/* 319 */       for (; number.exponent < -1022L; number.exponent += 1L)
/* 320 */         number.significand >>>= 1;
/* 321 */       number.exponent = -1023L;
/*     */     }
/*     */ 
/* 324 */     long result = number.exponent + 1023L;
/*     */ 
/* 326 */     result <<= 52;
/*     */ 
/* 328 */     if (number.negative)
/* 329 */       result |= -9223372036854775808L;
/*     */     else
/* 331 */       result &= 9223372036854775807L;
/* 332 */     result |= number.significand & 0xFFFFFFFF;
/*     */ 
/* 335 */     return Double.longBitsToDouble(result);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 339 */     System.out.println(parseString("777", Base.OCTAL).real());
/* 340 */     System.out.println(parseString("77.7", Base.OCTAL).real());
/* 341 */     System.out.println(parseString("177", Base.OCTAL).real());
/* 342 */     System.out.println(parseString("077", Base.OCTAL).real());
/* 343 */     System.out.println(parseString("0.01", Base.HEXADECIMAL).real());
/* 344 */     System.out.println(parseString("0.01", Base.OCTAL).real());
/* 345 */     System.out.println(parseString("0.01", Base.BINARY).real());
/* 346 */     System.out.println(parseString("-0.01", Base.HEXADECIMAL).real());
/* 347 */     System.out.println(parseString("-0.01", Base.OCTAL).real());
/* 348 */     System.out.println(parseString("-0.01", Base.BINARY).real());
/* 349 */     System.out.println(parseString("1e3", Base.HEXADECIMAL).real());
/* 350 */     System.out.println(parseString("1e3", Base.OCTAL).real());
/* 351 */     System.out.println(parseString("1e11", Base.BINARY).real());
/* 352 */     System.out.println(parseString("1e-3", Base.HEXADECIMAL).real());
/* 353 */     System.out.println(parseString("1e-3", Base.OCTAL).real());
/* 354 */     System.out.println(parseString("-1e-11", Base.BINARY).real());
/* 355 */     System.out.println(parseString("1e-13", Base.OCTAL).real());
/* 356 */     System.out.println(parseString("1e+1111111111", Base.BINARY).real());
/* 357 */     System.out.println(parseString("1e+10000000000", Base.BINARY).real());
/* 358 */     System.out.println(parseString("1e-10000000000", Base.BINARY).real());
/* 359 */     System.out.println(parseString("1e-10000000000", Base.BINARY).real());
/* 360 */     System.out.println(parseString("1e-100000000000", Base.BINARY).real());
/* 361 */     System.out.println(parseString("1e-1000000000000", Base.BINARY).real());
/* 362 */     System.out.println(parseString("8e+FF", Base.HEXADECIMAL).real());
/* 363 */     System.out.println(parseString("1e+100", Base.HEXADECIMAL).real());
/* 364 */     System.out.println(parseString("8e2", Base.HEXADECIMAL).real());
/* 365 */     System.out.println(parseString("8e-FF", Base.HEXADECIMAL).real());
/* 366 */     System.out.println(parseString("1e-108", Base.HEXADECIMAL).real());
/* 367 */     System.out.println(parseString("1e-109", Base.HEXADECIMAL).real());
/* 368 */     System.out.println(parseString("1e-109", Base.HEXADECIMAL).real());
/* 369 */     System.out.println(parseString("1e-10A", Base.HEXADECIMAL).real());
/* 370 */     System.out.println(parseString("1e-10C", Base.HEXADECIMAL).real());
/* 371 */     System.out.println(parseString("1e+8", Base.HEXADECIMAL).real());
/*     */   }
/*     */ 
/*     */   private static class Number
/*     */   {
/*     */     public long significand;
/*     */     public long exponent;
/*     */     public boolean negative;
/*     */     public boolean exponentNegative;
/*     */     public Base base;
/* 210 */     private static Pattern pattern = Pattern.compile("([-|+]?[0-9A-F]+|[-|+]?[0-9A-F]+[.][0-9A-F]*|[-|+]?[0-9A-F]*[.][0-9A-F]+)(e[-|+]?[0-9A-F]+|)");
/*     */ 
/*     */     public Number(Base b)
/*     */     {
/*  33 */       this.base = b;
/*  34 */       this.significand = (this.exponent = 0L);
/*     */     }
/*     */ 
/*     */     public static Number parseSignificand(String s, Base b)
/*     */     {
/*  64 */       Number number = new Number(b);
/*  65 */       int end = 15;
/*  66 */       int shift = 4;
/*  67 */       if (b == Base.BINARY) {
/*  68 */         end = 60;
/*  69 */         shift = 1;
/*     */       }
/*  71 */       else if (b == Base.OCTAL) {
/*  72 */         end = 20;
/*  73 */         shift = 3;
/*     */       }
/*  75 */       number.negative = (s.charAt(0) == '-');
/*  76 */       int offset = number.negative ? 1 : 0;
/*  77 */       boolean leading = true;
/*  78 */       for (int i = 0; i < end; i++) {
/*  79 */         long digit = 0L;
/*  80 */         if (i + offset < s.length())
/*  81 */           digit = ParseBase.access$000(s.charAt(i + offset));
/*  82 */         if (digit < 0L) {
/*  83 */           offset++;
/*  84 */           i--;
/*     */         }
/*  86 */         else if (leading) {
/*  87 */           if ((digit == 0L) && (offset < end)) {
/*  88 */             offset++;
/*  89 */             i--;
/*     */           }
/*     */           else {
/*  92 */             leading = false;
/*     */           } } else {
/*  93 */           number.significand <<= shift;
/*  94 */           number.significand |= digit;
/*     */         }
/*     */       }
/*  96 */       return number;
/*     */     }
/*     */ 
/*     */     private void parseExponent(String s)
/*     */     {
/* 105 */       int shift = 0;
/* 106 */       switch (ParseBase.1.$SwitchMap$com$dukascopy$calculator$Base[this.base.ordinal()]) {
/*     */       case 1:
/* 108 */         shift = 1;
/* 109 */         break;
/*     */       case 2:
/* 111 */         shift = 3;
/* 112 */         break;
/*     */       case 3:
/*     */       default:
/* 115 */         shift = 4;
/*     */       }
/* 117 */       this.exponentNegative = false;
/* 118 */       char[] arr$ = s.toCharArray(); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Character c = Character.valueOf(arr$[i$]);
/* 119 */         if (c.charValue() == '-')
/* 120 */           this.exponentNegative = (!this.exponentNegative);
/* 121 */         long digit = ParseBase.access$000(c.charValue());
/* 122 */         if (digit >= 0L) {
/* 123 */           this.exponent <<= shift;
/* 124 */           if ((this.exponent & 0x1000) != 0L)
/*     */           {
/*     */             break;
/*     */           }
/*     */ 
/* 133 */           this.exponent += digit * shift;
/*     */         } }
/* 135 */       if (this.exponentNegative)
/* 136 */         this.exponent = (-this.exponent);
/*     */     }
/*     */ 
/*     */     private void parsePoint(String s)
/*     */     {
/* 146 */       boolean fraction = false;
/* 147 */       int leadingZeros = 0;
/* 148 */       int fractionalZeros = 0;
/* 149 */       char[] arr$ = s.toCharArray(); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Character c = Character.valueOf(arr$[i$]);
/* 150 */         if (c.charValue() == '.') {
/* 151 */           fraction = true; } else {
/* 152 */           if (c.charValue() != '0') break;
/* 153 */           if (fraction)
/* 154 */             fractionalZeros++;
/*     */           else
/* 156 */             leadingZeros++;
/*     */         } }
/* 158 */       int leadingDigits = 0;
/* 159 */       char[] arr$ = s.toCharArray(); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Character c = Character.valueOf(arr$[i$]);
/* 160 */         if (c.charValue() == '.') break;
/* 161 */         leadingDigits++;
/*     */       }
/* 163 */       int count = leadingDigits - leadingZeros - fractionalZeros - 1;
/*     */ 
/* 167 */       if (this.base == Base.HEXADECIMAL)
/* 168 */         count *= 4;
/* 169 */       else if (this.base == Base.OCTAL)
/* 170 */         count *= 3;
/* 171 */       this.exponent += count;
/*     */     }
/*     */ 
/*     */     private static Number parseStrings(String s, String e, Base b)
/*     */     {
/* 185 */       Number number = parseSignificand(s, b);
/* 186 */       if (e.length() > 0)
/* 187 */         number.parseExponent(e);
/* 188 */       number.parsePoint(s);
/*     */ 
/* 191 */       return number;
/*     */     }
/*     */ 
/*     */     public static Number parseString(String s, Base b)
/*     */     {
/* 201 */       Matcher m = pattern.matcher(s);
/* 202 */       m.matches();
/* 203 */       return parseStrings(m.group(1), m.group(2), b);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ParseBase
 * JD-Core Version:    0.6.0
 */