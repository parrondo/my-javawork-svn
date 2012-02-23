/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public final class NumericUtils
/*     */ {
/*     */   public static final int PRECISION_STEP_DEFAULT = 4;
/*     */   public static final char SHIFT_START_LONG = ' ';
/*     */   public static final int BUF_SIZE_LONG = 11;
/*     */   public static final char SHIFT_START_INT = '`';
/*     */   public static final int BUF_SIZE_INT = 6;
/*     */ 
/*     */   public static int longToPrefixCoded(long val, int shift, char[] buffer)
/*     */   {
/* 108 */     if ((shift > 63) || (shift < 0))
/* 109 */       throw new IllegalArgumentException("Illegal shift value, must be 0..63");
/* 110 */     int nChars = (63 - shift) / 7 + 1; int len = nChars + 1;
/* 111 */     buffer[0] = (char)(32 + shift);
/* 112 */     long sortableBits = val ^ 0x0;
/* 113 */     sortableBits >>>= shift;
/* 114 */     while (nChars >= 1)
/*     */     {
/* 118 */       buffer[(nChars--)] = (char)(int)(sortableBits & 0x7F);
/* 119 */       sortableBits >>>= 7;
/*     */     }
/* 121 */     return len;
/*     */   }
/*     */ 
/*     */   public static String longToPrefixCoded(long val, int shift)
/*     */   {
/* 131 */     char[] buffer = new char[11];
/* 132 */     int len = longToPrefixCoded(val, shift, buffer);
/* 133 */     return new String(buffer, 0, len);
/*     */   }
/*     */ 
/*     */   public static String longToPrefixCoded(long val)
/*     */   {
/* 143 */     return longToPrefixCoded(val, 0);
/*     */   }
/*     */ 
/*     */   public static int intToPrefixCoded(int val, int shift, char[] buffer)
/*     */   {
/* 156 */     if ((shift > 31) || (shift < 0))
/* 157 */       throw new IllegalArgumentException("Illegal shift value, must be 0..31");
/* 158 */     int nChars = (31 - shift) / 7 + 1; int len = nChars + 1;
/* 159 */     buffer[0] = (char)(96 + shift);
/* 160 */     int sortableBits = val ^ 0x80000000;
/* 161 */     sortableBits >>>= shift;
/* 162 */     while (nChars >= 1)
/*     */     {
/* 166 */       buffer[(nChars--)] = (char)(sortableBits & 0x7F);
/* 167 */       sortableBits >>>= 7;
/*     */     }
/* 169 */     return len;
/*     */   }
/*     */ 
/*     */   public static String intToPrefixCoded(int val, int shift)
/*     */   {
/* 179 */     char[] buffer = new char[6];
/* 180 */     int len = intToPrefixCoded(val, shift, buffer);
/* 181 */     return new String(buffer, 0, len);
/*     */   }
/*     */ 
/*     */   public static String intToPrefixCoded(int val)
/*     */   {
/* 191 */     return intToPrefixCoded(val, 0);
/*     */   }
/*     */ 
/*     */   public static long prefixCodedToLong(String prefixCoded)
/*     */   {
/* 203 */     int shift = prefixCoded.charAt(0) - ' ';
/* 204 */     if ((shift > 63) || (shift < 0))
/* 205 */       throw new NumberFormatException("Invalid shift value in prefixCoded string (is encoded value really a LONG?)");
/* 206 */     long sortableBits = 0L;
/* 207 */     int i = 1; for (int len = prefixCoded.length(); i < len; i++) {
/* 208 */       sortableBits <<= 7;
/* 209 */       char ch = prefixCoded.charAt(i);
/* 210 */       if (ch > '') {
/* 211 */         throw new NumberFormatException("Invalid prefixCoded numerical value representation (char " + Integer.toHexString(ch) + " at position " + i + " is invalid)");
/*     */       }
/*     */ 
/* 216 */       sortableBits |= ch;
/*     */     }
/* 218 */     return sortableBits << shift ^ 0x0;
/*     */   }
/*     */ 
/*     */   public static int prefixCodedToInt(String prefixCoded)
/*     */   {
/* 230 */     int shift = prefixCoded.charAt(0) - '`';
/* 231 */     if ((shift > 31) || (shift < 0))
/* 232 */       throw new NumberFormatException("Invalid shift value in prefixCoded string (is encoded value really an INT?)");
/* 233 */     int sortableBits = 0;
/* 234 */     int i = 1; for (int len = prefixCoded.length(); i < len; i++) {
/* 235 */       sortableBits <<= 7;
/* 236 */       char ch = prefixCoded.charAt(i);
/* 237 */       if (ch > '') {
/* 238 */         throw new NumberFormatException("Invalid prefixCoded numerical value representation (char " + Integer.toHexString(ch) + " at position " + i + " is invalid)");
/*     */       }
/*     */ 
/* 243 */       sortableBits |= ch;
/*     */     }
/* 245 */     return sortableBits << shift ^ 0x80000000;
/*     */   }
/*     */ 
/*     */   public static long doubleToSortableLong(double val)
/*     */   {
/* 256 */     long f = Double.doubleToRawLongBits(val);
/* 257 */     if (f < 0L) f ^= 9223372036854775807L;
/* 258 */     return f;
/*     */   }
/*     */ 
/*     */   public static String doubleToPrefixCoded(double val)
/*     */   {
/* 266 */     return longToPrefixCoded(doubleToSortableLong(val));
/*     */   }
/*     */ 
/*     */   public static double sortableLongToDouble(long val)
/*     */   {
/* 274 */     if (val < 0L) val ^= 9223372036854775807L;
/* 275 */     return Double.longBitsToDouble(val);
/*     */   }
/*     */ 
/*     */   public static double prefixCodedToDouble(String val)
/*     */   {
/* 283 */     return sortableLongToDouble(prefixCodedToLong(val));
/*     */   }
/*     */ 
/*     */   public static int floatToSortableInt(float val)
/*     */   {
/* 294 */     int f = Float.floatToRawIntBits(val);
/* 295 */     if (f < 0) f ^= 2147483647;
/* 296 */     return f;
/*     */   }
/*     */ 
/*     */   public static String floatToPrefixCoded(float val)
/*     */   {
/* 304 */     return intToPrefixCoded(floatToSortableInt(val));
/*     */   }
/*     */ 
/*     */   public static float sortableIntToFloat(int val)
/*     */   {
/* 312 */     if (val < 0) val ^= 2147483647;
/* 313 */     return Float.intBitsToFloat(val);
/*     */   }
/*     */ 
/*     */   public static float prefixCodedToFloat(String val)
/*     */   {
/* 321 */     return sortableIntToFloat(prefixCodedToInt(val));
/*     */   }
/*     */ 
/*     */   public static void splitLongRange(LongRangeBuilder builder, int precisionStep, long minBound, long maxBound)
/*     */   {
/* 335 */     splitRange(builder, 64, precisionStep, minBound, maxBound);
/*     */   }
/*     */ 
/*     */   public static void splitIntRange(IntRangeBuilder builder, int precisionStep, int minBound, int maxBound)
/*     */   {
/* 349 */     splitRange(builder, 32, precisionStep, minBound, maxBound);
/*     */   }
/*     */ 
/*     */   private static void splitRange(Object builder, int valSize, int precisionStep, long minBound, long maxBound)
/*     */   {
/* 357 */     if (precisionStep < 1)
/* 358 */       throw new IllegalArgumentException("precisionStep must be >=1");
/* 359 */     if (minBound > maxBound) return;
/* 360 */     for (int shift = 0; ; shift += precisionStep)
/*     */     {
/* 362 */       long diff = 1L << shift + precisionStep;
/* 363 */       long mask = (1L << precisionStep) - 1L << shift;
/*     */ 
/* 365 */       boolean hasLower = (minBound & mask) != 0L;
/* 366 */       boolean hasUpper = (maxBound & mask) != mask;
/*     */ 
/* 368 */       long nextMinBound = (hasLower ? minBound + diff : minBound) & (mask ^ 0xFFFFFFFF);
/* 369 */       long nextMaxBound = (hasUpper ? maxBound - diff : maxBound) & (mask ^ 0xFFFFFFFF);
/*     */ 
/* 371 */       boolean lowerWrapped = nextMinBound < minBound;
/* 372 */       boolean upperWrapped = nextMaxBound > maxBound;
/*     */ 
/* 374 */       if ((shift + precisionStep >= valSize) || (nextMinBound > nextMaxBound) || (lowerWrapped) || (upperWrapped))
/*     */       {
/* 376 */         addRange(builder, valSize, minBound, maxBound, shift);
/*     */ 
/* 378 */         break;
/*     */       }
/*     */ 
/* 381 */       if (hasLower)
/* 382 */         addRange(builder, valSize, minBound, minBound | mask, shift);
/* 383 */       if (hasUpper) {
/* 384 */         addRange(builder, valSize, maxBound & (mask ^ 0xFFFFFFFF), maxBound, shift);
/*     */       }
/*     */ 
/* 387 */       minBound = nextMinBound;
/* 388 */       maxBound = nextMaxBound;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void addRange(Object builder, int valSize, long minBound, long maxBound, int shift)
/*     */   {
/* 402 */     maxBound |= (1L << shift) - 1L;
/*     */ 
/* 404 */     switch (valSize) {
/*     */     case 64:
/* 406 */       ((LongRangeBuilder)builder).addRange(minBound, maxBound, shift);
/* 407 */       break;
/*     */     case 32:
/* 409 */       ((IntRangeBuilder)builder).addRange((int)minBound, (int)maxBound, shift);
/* 410 */       break;
/*     */     default:
/* 413 */       throw new IllegalArgumentException("valSize must be 32 or 64.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class IntRangeBuilder
/*     */   {
/*     */     public void addRange(String minPrefixCoded, String maxPrefixCoded)
/*     */     {
/* 456 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void addRange(int min, int max, int shift)
/*     */     {
/* 464 */       addRange(NumericUtils.intToPrefixCoded(min, shift), NumericUtils.intToPrefixCoded(max, shift));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class LongRangeBuilder
/*     */   {
/*     */     public void addRange(String minPrefixCoded, String maxPrefixCoded)
/*     */     {
/* 430 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void addRange(long min, long max, int shift)
/*     */     {
/* 438 */       addRange(NumericUtils.longToPrefixCoded(min, shift), NumericUtils.longToPrefixCoded(max, shift));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.NumericUtils
 * JD-Core Version:    0.6.0
 */