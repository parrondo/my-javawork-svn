/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ @Deprecated
/*     */ public class NumberTools
/*     */ {
/*     */   private static final int RADIX = 36;
/*     */   private static final char NEGATIVE_PREFIX = '-';
/*     */   private static final char POSITIVE_PREFIX = '0';
/*     */   public static final String MIN_STRING_VALUE = "-0000000000000";
/*     */   public static final String MAX_STRING_VALUE = "01y2p0ij32e8e7";
/*  73 */   public static final int STR_SIZE = "-0000000000000".length();
/*     */ 
/*     */   public static String longToString(long l)
/*     */   {
/*  80 */     if (l == -9223372036854775808L)
/*     */     {
/*  82 */       return "-0000000000000";
/*     */     }
/*     */ 
/*  85 */     StringBuilder buf = new StringBuilder(STR_SIZE);
/*     */ 
/*  87 */     if (l < 0L) {
/*  88 */       buf.append('-');
/*  89 */       l = 9223372036854775807L + l + 1L;
/*     */     } else {
/*  91 */       buf.append('0');
/*     */     }
/*  93 */     String num = Long.toString(l, 36);
/*     */ 
/*  95 */     int padLen = STR_SIZE - num.length() - buf.length();
/*  96 */     while (padLen-- > 0) {
/*  97 */       buf.append('0');
/*     */     }
/*  99 */     buf.append(num);
/*     */ 
/* 101 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static long stringToLong(String str)
/*     */   {
/* 115 */     if (str == null) {
/* 116 */       throw new NullPointerException("string cannot be null");
/*     */     }
/* 118 */     if (str.length() != STR_SIZE) {
/* 119 */       throw new NumberFormatException("string is the wrong size");
/*     */     }
/*     */ 
/* 122 */     if (str.equals("-0000000000000")) {
/* 123 */       return -9223372036854775808L;
/*     */     }
/*     */ 
/* 126 */     char prefix = str.charAt(0);
/* 127 */     long l = Long.parseLong(str.substring(1), 36);
/*     */ 
/* 129 */     if (prefix != '0')
/*     */     {
/* 131 */       if (prefix == '-')
/* 132 */         l = l - 9223372036854775807L - 1L;
/*     */       else {
/* 134 */         throw new NumberFormatException("string does not begin with the correct prefix");
/*     */       }
/*     */     }
/*     */ 
/* 138 */     return l;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.NumberTools
 * JD-Core Version:    0.6.0
 */