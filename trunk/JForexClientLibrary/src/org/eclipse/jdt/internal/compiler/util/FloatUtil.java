/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ public class FloatUtil
/*     */ {
/*     */   private static final int DOUBLE_FRACTION_WIDTH = 52;
/*     */   private static final int DOUBLE_PRECISION = 53;
/*     */   private static final int MAX_DOUBLE_EXPONENT = 1023;
/*     */   private static final int MIN_NORMALIZED_DOUBLE_EXPONENT = -1022;
/*     */   private static final int MIN_UNNORMALIZED_DOUBLE_EXPONENT = -1075;
/*     */   private static final int DOUBLE_EXPONENT_BIAS = 1023;
/*     */   private static final int DOUBLE_EXPONENT_SHIFT = 52;
/*     */   private static final int SINGLE_FRACTION_WIDTH = 23;
/*     */   private static final int SINGLE_PRECISION = 24;
/*     */   private static final int MAX_SINGLE_EXPONENT = 127;
/*     */   private static final int MIN_NORMALIZED_SINGLE_EXPONENT = -126;
/*     */   private static final int MIN_UNNORMALIZED_SINGLE_EXPONENT = -150;
/*     */   private static final int SINGLE_EXPONENT_BIAS = 127;
/*     */   private static final int SINGLE_EXPONENT_SHIFT = 23;
/*     */ 
/*     */   public static float valueOfHexFloatLiteral(char[] source)
/*     */   {
/*  73 */     long bits = convertHexFloatingPointLiteralToBits(source);
/*  74 */     return Float.intBitsToFloat((int)bits);
/*     */   }
/*     */ 
/*     */   public static double valueOfHexDoubleLiteral(char[] source)
/*     */   {
/* 100 */     long bits = convertHexFloatingPointLiteralToBits(source);
/* 101 */     return Double.longBitsToDouble(bits);
/*     */   }
/*     */ 
/*     */   private static long convertHexFloatingPointLiteralToBits(char[] source)
/*     */   {
/* 119 */     int length = source.length;
/* 120 */     long mantissa = 0L;
/*     */ 
/* 123 */     int next = 0;
/* 124 */     char nextChar = source[next];
/* 125 */     nextChar = source[next];
/* 126 */     if (nextChar == '0')
/* 127 */       next++;
/*     */     else {
/* 129 */       throw new NumberFormatException();
/*     */     }
/* 131 */     nextChar = source[next];
/* 132 */     if ((nextChar == 'X') || (nextChar == 'x'))
/* 133 */       next++;
/*     */     else {
/* 135 */       throw new NumberFormatException();
/*     */     }
/*     */ 
/* 139 */     int binaryPointPosition = -1;
/*     */     while (true) {
/* 141 */       nextChar = source[next];
/* 142 */       switch (nextChar) {
/*     */       case '0':
/* 144 */         next++;
/* 145 */         break;
/*     */       case '.':
/* 147 */         binaryPointPosition = next;
/* 148 */         next++;
/*     */       case '/':
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 157 */     int mantissaBits = 0;
/* 158 */     int leadingDigitPosition = -1;
/*     */     while (true) {
/* 160 */       nextChar = source[next];
/*     */       int hexdigit;
/*     */       int hexdigit;
/*     */       int hexdigit;
/* 162 */       switch (nextChar) {
/*     */       case '0':
/*     */       case '1':
/*     */       case '2':
/*     */       case '3':
/*     */       case '4':
/*     */       case '5':
/*     */       case '6':
/*     */       case '7':
/*     */       case '8':
/*     */       case '9':
/* 173 */         hexdigit = nextChar - '0';
/* 174 */         break;
/*     */       case 'a':
/*     */       case 'b':
/*     */       case 'c':
/*     */       case 'd':
/*     */       case 'e':
/*     */       case 'f':
/* 181 */         hexdigit = nextChar - 'a' + 10;
/* 182 */         break;
/*     */       case 'A':
/*     */       case 'B':
/*     */       case 'C':
/*     */       case 'D':
/*     */       case 'E':
/*     */       case 'F':
/* 189 */         hexdigit = nextChar - 'A' + 10;
/* 190 */         break;
/*     */       case '.':
/* 192 */         binaryPointPosition = next;
/* 193 */         next++;
/* 194 */         break;
/*     */       case '/':
/*     */       case ':':
/*     */       case ';':
/*     */       case '<':
/*     */       case '=':
/*     */       case '>':
/*     */       case '?':
/*     */       case '@':
/*     */       case 'G':
/*     */       case 'H':
/*     */       case 'I':
/*     */       case 'J':
/*     */       case 'K':
/*     */       case 'L':
/*     */       case 'M':
/*     */       case 'N':
/*     */       case 'O':
/*     */       case 'P':
/*     */       case 'Q':
/*     */       case 'R':
/*     */       case 'S':
/*     */       case 'T':
/*     */       case 'U':
/*     */       case 'V':
/*     */       case 'W':
/*     */       case 'X':
/*     */       case 'Y':
/*     */       case 'Z':
/*     */       case '[':
/*     */       case '\\':
/*     */       case ']':
/*     */       case '^':
/*     */       case '_':
/*     */       case '`':
/*     */       default:
/* 196 */         if (binaryPointPosition >= 0) break label487;
/* 198 */         binaryPointPosition = next;
/*     */ 
/* 200 */         break label487;
/*     */         int hexdigit;
/* 202 */         if (mantissaBits == 0)
/*     */         {
/* 205 */           leadingDigitPosition = next;
/* 206 */           mantissa = hexdigit;
/* 207 */           mantissaBits = 4;
/* 208 */         } else if (mantissaBits < 60)
/*     */         {
/* 210 */           mantissa <<= 4;
/* 211 */           mantissa |= hexdigit;
/* 212 */           mantissaBits += 4;
/*     */         }
/*     */ 
/* 217 */         next++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 222 */     label487: nextChar = source[next];
/* 223 */     if ((nextChar == 'P') || (nextChar == 'p'))
/* 224 */       next++;
/*     */     else {
/* 226 */       throw new NumberFormatException();
/*     */     }
/*     */ 
/* 230 */     int exponent = 0;
/* 231 */     int exponentSign = 1;
/* 232 */     while (next < length) {
/* 233 */       nextChar = source[next];
/* 234 */       switch (nextChar) {
/*     */       case '+':
/* 236 */         exponentSign = 1;
/* 237 */         next++;
/* 238 */         break;
/*     */       case '-':
/* 240 */         exponentSign = -1;
/* 241 */         next++;
/* 242 */         break;
/*     */       case '0':
/*     */       case '1':
/*     */       case '2':
/*     */       case '3':
/*     */       case '4':
/*     */       case '5':
/*     */       case '6':
/*     */       case '7':
/*     */       case '8':
/*     */       case '9':
/* 253 */         int digit = nextChar - '0';
/* 254 */         exponent = exponent * 10 + digit;
/* 255 */         next++;
/* 256 */         break;
/*     */       case ',':
/*     */       case '.':
/*     */       case '/':
/*     */       default:
/* 258 */         break label662;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 263 */     label662: boolean doublePrecision = true;
/* 264 */     if (next < length) {
/* 265 */       nextChar = source[next];
/* 266 */       switch (nextChar) {
/*     */       case 'F':
/*     */       case 'f':
/* 269 */         doublePrecision = false;
/* 270 */         next++;
/* 271 */         break;
/*     */       case 'D':
/*     */       case 'd':
/* 274 */         doublePrecision = true;
/* 275 */         next++;
/* 276 */         break;
/*     */       default:
/* 278 */         throw new NumberFormatException();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 284 */     if (mantissa == 0L) {
/* 285 */       return 0L;
/*     */     }
/*     */ 
/* 291 */     int scaleFactorCompensation = 0;
/* 292 */     long top = mantissa >>> mantissaBits - 4;
/* 293 */     if ((top & 0x8) == 0L) {
/* 294 */       mantissaBits--;
/* 295 */       scaleFactorCompensation++;
/* 296 */       if ((top & 0x4) == 0L) {
/* 297 */         mantissaBits--;
/* 298 */         scaleFactorCompensation++;
/* 299 */         if ((top & 0x2) == 0L) {
/* 300 */           mantissaBits--;
/* 301 */           scaleFactorCompensation++;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 307 */     long result = 0L;
/* 308 */     if (doublePrecision)
/*     */     {
/*     */       long fraction;
/* 310 */       if (mantissaBits > 53)
/*     */       {
/* 312 */         int extraBits = mantissaBits - 53;
/*     */ 
/* 314 */         long fraction = mantissa >>> extraBits - 1;
/* 315 */         long lowBit = fraction & 1L;
/* 316 */         fraction += lowBit;
/* 317 */         fraction >>>= 1;
/* 318 */         if ((fraction & 0x0) != 0L) {
/* 319 */           fraction >>>= 1;
/* 320 */           scaleFactorCompensation--;
/*     */         }
/*     */       }
/*     */       else {
/* 324 */         fraction = mantissa << 53 - mantissaBits;
/*     */       }
/*     */ 
/* 327 */       int scaleFactor = 0;
/* 328 */       if (mantissaBits > 0) {
/* 329 */         if (leadingDigitPosition < binaryPointPosition)
/*     */         {
/* 331 */           scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
/*     */ 
/* 333 */           scaleFactor -= scaleFactorCompensation;
/*     */         }
/*     */         else {
/* 336 */           scaleFactor = -4 * (
/* 337 */             leadingDigitPosition - binaryPointPosition - 1);
/*     */ 
/* 339 */           scaleFactor -= scaleFactorCompensation;
/*     */         }
/*     */       }
/*     */ 
/* 343 */       int e = exponentSign * exponent + scaleFactor;
/* 344 */       if (e - 1 > 1023)
/*     */       {
/* 346 */         result = Double.doubleToLongBits((1.0D / 0.0D));
/* 347 */       } else if (e - 1 >= -1022)
/*     */       {
/* 350 */         long biasedExponent = e - 1 + 1023;
/* 351 */         result = fraction & 0xFFFFFFFF;
/* 352 */         result |= biasedExponent << 52;
/* 353 */       } else if (e - 1 > -1075)
/*     */       {
/* 355 */         long biasedExponent = 0L;
/* 356 */         result = fraction >>> -1022 - e + 1;
/* 357 */         result |= biasedExponent << 52;
/*     */       }
/*     */       else {
/* 360 */         result = Double.doubleToLongBits((0.0D / 0.0D));
/*     */       }
/* 362 */       return result;
/*     */     }
/*     */     long fraction;
/* 367 */     if (mantissaBits > 24)
/*     */     {
/* 369 */       int extraBits = mantissaBits - 24;
/*     */ 
/* 371 */       long fraction = mantissa >>> extraBits - 1;
/* 372 */       long lowBit = fraction & 1L;
/* 373 */       fraction += lowBit;
/* 374 */       fraction >>>= 1;
/* 375 */       if ((fraction & 0x1000000) != 0L) {
/* 376 */         fraction >>>= 1;
/* 377 */         scaleFactorCompensation--;
/*     */       }
/*     */     }
/*     */     else {
/* 381 */       fraction = mantissa << 24 - mantissaBits;
/*     */     }
/*     */ 
/* 384 */     int scaleFactor = 0;
/* 385 */     if (mantissaBits > 0) {
/* 386 */       if (leadingDigitPosition < binaryPointPosition)
/*     */       {
/* 388 */         scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
/*     */ 
/* 390 */         scaleFactor -= scaleFactorCompensation;
/*     */       }
/*     */       else {
/* 393 */         scaleFactor = -4 * (
/* 394 */           leadingDigitPosition - binaryPointPosition - 1);
/*     */ 
/* 396 */         scaleFactor -= scaleFactorCompensation;
/*     */       }
/*     */     }
/*     */ 
/* 400 */     int e = exponentSign * exponent + scaleFactor;
/* 401 */     if (e - 1 > 127)
/*     */     {
/* 403 */       result = Float.floatToIntBits((1.0F / 1.0F));
/* 404 */     } else if (e - 1 >= -126)
/*     */     {
/* 407 */       long biasedExponent = e - 1 + 127;
/* 408 */       result = fraction & 0xFF7FFFFF;
/* 409 */       result |= biasedExponent << 23;
/* 410 */     } else if (e - 1 > -150)
/*     */     {
/* 412 */       long biasedExponent = 0L;
/* 413 */       result = fraction >>> -126 - e + 1;
/* 414 */       result |= biasedExponent << 23;
/*     */     }
/*     */     else {
/* 417 */       result = Float.floatToIntBits((0.0F / 0.0F));
/*     */     }
/* 419 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.FloatUtil
 * JD-Core Version:    0.6.0
 */