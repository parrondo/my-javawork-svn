/*     */ package com.dukascopy.dds2.greed.agent.compiler;
/*     */ 
/*     */ public class Base32
/*     */ {
/*     */   private static final String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
/*   6 */   private static final int[] base32Lookup = { 255, 255, 26, 27, 28, 29, 30, 31, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 255, 255, 255, 255, 255, 255, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 255, 255, 255, 255, 255 };
/*     */ 
/*     */   public static String encode(byte[] bytes)
/*     */   {
/*  49 */     int i = 0; int index = 0; int digit = 0;
/*     */ 
/*  51 */     StringBuffer base32 = new StringBuffer((bytes.length + 7) * 8 / 5);
/*     */ 
/*  53 */     while (i < bytes.length) {
/*  54 */       int currByte = bytes[i] >= 0 ? bytes[i] : bytes[i] + 256;
/*     */ 
/*  57 */       if (index > 3)
/*     */       {
/*     */         int nextByte;
/*     */         int nextByte;
/*  58 */         if (i + 1 < bytes.length)
/*  59 */           nextByte = bytes[(i + 1)] >= 0 ? bytes[(i + 1)] : bytes[(i + 1)] + 256;
/*     */         else {
/*  61 */           nextByte = 0;
/*     */         }
/*  63 */         digit = currByte & 255 >> index;
/*  64 */         index = (index + 5) % 8;
/*  65 */         digit <<= index;
/*  66 */         digit |= nextByte >> 8 - index;
/*  67 */         i++;
/*     */       } else {
/*  69 */         digit = currByte >> 8 - (index + 5) & 0x1F;
/*  70 */         index = (index + 5) % 8;
/*  71 */         if (index == 0)
/*  72 */           i++;
/*     */       }
/*  74 */       base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit));
/*     */     }
/*     */ 
/*  77 */     return base32.toString();
/*     */   }
/*     */ 
/*     */   public static byte[] decode(String base32)
/*     */   {
/*  87 */     byte[] bytes = new byte[base32.length() * 5 / 8];
/*     */ 
/*  89 */     int i = 0; int index = 0; for (int offset = 0; i < base32.length(); i++) {
/*  90 */       int lookup = base32.charAt(i) - '0';
/*     */ 
/*  93 */       if ((lookup < 0) || (lookup >= base32Lookup.length)) {
/*     */         continue;
/*     */       }
/*  96 */       int digit = base32Lookup[lookup];
/*     */ 
/*  99 */       if (digit == 255) {
/*     */         continue;
/*     */       }
/* 102 */       if (index <= 3) {
/* 103 */         index = (index + 5) % 8;
/* 104 */         if (index == 0)
/*     */         {
/*     */           int tmp90_88 = offset;
/*     */           byte[] tmp90_86 = bytes; tmp90_86[tmp90_88] = (byte)(tmp90_86[tmp90_88] | digit);
/* 106 */           offset++;
/* 107 */           if (offset >= bytes.length)
/* 108 */             break;
/*     */         }
/*     */         else
/*     */         {
/*     */           int tmp115_113 = offset;
/*     */           byte[] tmp115_111 = bytes; tmp115_111[tmp115_113] = (byte)(tmp115_111[tmp115_113] | digit << 8 - index);
/*     */         }
/*     */       } else {
/* 112 */         index = (index + 5) % 8;
/*     */         int tmp141_139 = offset;
/*     */         byte[] tmp141_137 = bytes; tmp141_137[tmp141_139] = (byte)(tmp141_137[tmp141_139] | digit >>> index);
/* 114 */         offset++;
/*     */ 
/* 116 */         if (offset >= bytes.length)
/*     */           break;
/*     */         int tmp168_166 = offset;
/*     */         byte[] tmp168_164 = bytes; tmp168_164[tmp168_166] = (byte)(tmp168_164[tmp168_166] | digit << 8 - index);
/*     */       }
/*     */     }
/* 121 */     return bytes;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.Base32
 * JD-Core Version:    0.6.0
 */