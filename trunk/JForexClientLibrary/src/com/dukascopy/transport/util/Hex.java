/*     */ package com.dukascopy.transport.util;
/*     */ 
/*     */ public class Hex
/*     */ {
/*  16 */   private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*  21 */   private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */   public static byte[] decodeHex(char[] data)
/*     */     throws IllegalArgumentException
/*     */   {
/*  36 */     int len = data.length;
/*     */ 
/*  38 */     if ((len & 0x1) != 0) {
/*  39 */       throw new IllegalArgumentException("Odd number of characters.");
/*     */     }
/*     */ 
/*  42 */     byte[] out = new byte[len >> 1];
/*     */ 
/*  45 */     int i = 0; for (int j = 0; j < len; i++) {
/*  46 */       int f = toDigit(data[j], j) << 4;
/*  47 */       j++;
/*  48 */       f |= toDigit(data[j], j);
/*  49 */       j++;
/*  50 */       out[i] = (byte)(f & 0xFF);
/*     */     }
/*     */ 
/*  53 */     return out;
/*     */   }
/*     */ 
/*     */   public static char[] encodeHex(byte[] data)
/*     */   {
/*  66 */     return encodeHex(data, true);
/*     */   }
/*     */ 
/*     */   public static char[] encodeHex(byte[] data, boolean toLowerCase)
/*     */   {
/*  82 */     return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
/*     */   }
/*     */ 
/*     */   protected static char[] encodeHex(byte[] data, char[] toDigits)
/*     */   {
/*  98 */     int l = data.length;
/*  99 */     char[] out = new char[l << 1];
/*     */ 
/* 101 */     int i = 0; for (int j = 0; i < l; i++) {
/* 102 */       out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
/* 103 */       out[(j++)] = toDigits[(0xF & data[i])];
/*     */     }
/* 105 */     return out;
/*     */   }
/*     */ 
/*     */   public static String encodeHexString(byte[] data)
/*     */   {
/* 118 */     return new String(encodeHex(data));
/*     */   }
/*     */ 
/*     */   protected static int toDigit(char ch, int index)
/*     */     throws IllegalArgumentException
/*     */   {
/* 133 */     int digit = Character.digit(ch, 16);
/* 134 */     if (digit == -1) {
/* 135 */       throw new IllegalArgumentException("Illegal hexadecimal charcter " + ch + " at index " + index);
/*     */     }
/* 137 */     return digit;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.util.Hex
 * JD-Core Version:    0.6.0
 */