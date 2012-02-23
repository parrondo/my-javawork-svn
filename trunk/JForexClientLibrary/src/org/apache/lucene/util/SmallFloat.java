/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public class SmallFloat
/*     */ {
/*     */   public static byte floatToByte(float f, int numMantissaBits, int zeroExp)
/*     */   {
/*  39 */     int fzero = 63 - zeroExp << numMantissaBits;
/*  40 */     int bits = Float.floatToRawIntBits(f);
/*  41 */     int smallfloat = bits >> 24 - numMantissaBits;
/*  42 */     if (smallfloat <= fzero) {
/*  43 */       return bits <= 0 ? 0 : 1;
/*     */     }
/*     */ 
/*  46 */     if (smallfloat >= fzero + 256) {
/*  47 */       return -1;
/*     */     }
/*  49 */     return (byte)(smallfloat - fzero);
/*     */   }
/*     */ 
/*     */   public static float byteToFloat(byte b, int numMantissaBits, int zeroExp)
/*     */   {
/*  57 */     if (b == 0) return 0.0F;
/*  58 */     int bits = (b & 0xFF) << 24 - numMantissaBits;
/*  59 */     bits += (63 - zeroExp << 24);
/*  60 */     return Float.intBitsToFloat(bits);
/*     */   }
/*     */ 
/*     */   public static byte floatToByte315(float f)
/*     */   {
/*  76 */     int bits = Float.floatToRawIntBits(f);
/*  77 */     int smallfloat = bits >> 21;
/*  78 */     if (smallfloat <= 384) {
/*  79 */       return bits <= 0 ? 0 : 1;
/*     */     }
/*  81 */     if (smallfloat >= 640) {
/*  82 */       return -1;
/*     */     }
/*  84 */     return (byte)(smallfloat - 384);
/*     */   }
/*     */ 
/*     */   public static float byte315ToFloat(byte b)
/*     */   {
/*  91 */     if (b == 0) return 0.0F;
/*  92 */     int bits = (b & 0xFF) << 21;
/*  93 */     bits += 805306368;
/*  94 */     return Float.intBitsToFloat(bits);
/*     */   }
/*     */ 
/*     */   public static byte floatToByte52(float f)
/*     */   {
/* 104 */     int bits = Float.floatToRawIntBits(f);
/* 105 */     int smallfloat = bits >> 19;
/* 106 */     if (smallfloat <= 1952) {
/* 107 */       return bits <= 0 ? 0 : 1;
/*     */     }
/* 109 */     if (smallfloat >= 2208) {
/* 110 */       return -1;
/*     */     }
/* 112 */     return (byte)(smallfloat - 1952);
/*     */   }
/*     */ 
/*     */   public static float byte52ToFloat(byte b)
/*     */   {
/* 119 */     if (b == 0) return 0.0F;
/* 120 */     int bits = (b & 0xFF) << 19;
/* 121 */     bits += 1023410176;
/* 122 */     return Float.intBitsToFloat(bits);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.SmallFloat
 * JD-Core Version:    0.6.0
 */