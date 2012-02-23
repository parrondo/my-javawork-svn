/*     */ package com.dukascopy.transport.common.mina;
/*     */ 
/*     */ public class Base64Encoder
/*     */ {
/*   6 */   private static char[] map1 = new char[64];
/*     */   private static byte[] map2;
/*     */ 
/*     */   public static String encodeString(String s)
/*     */   {
/*  40 */     int a = 0;
/*  41 */     return new String(encode(s.getBytes()));
/*     */   }
/*     */ 
/*     */   public static char[] encode(byte[] in)
/*     */   {
/*  53 */     int a = 0;
/*  54 */     return encode(in, in.length);
/*     */   }
/*     */ 
/*     */   public static char[] encode(byte[] in, int iLen)
/*     */   {
/*  68 */     int a = 0;
/*  69 */     int oDataLen = (iLen * 4 + 2) / 3;
/*  70 */     int oLen = (iLen + 2) / 3 * 4;
/*  71 */     char[] out = new char[oLen];
/*  72 */     int ip = 0;
/*  73 */     int op = 0;
/*  74 */     while (ip < iLen) {
/*  75 */       int i0 = in[(ip++)] & 0xFF;
/*  76 */       int i1 = ip < iLen ? in[(ip++)] & 0xFF : 0;
/*  77 */       int i2 = ip < iLen ? in[(ip++)] & 0xFF : 0;
/*  78 */       int o0 = i0 >>> 2;
/*  79 */       int o1 = (i0 & 0x3) << 4 | i1 >>> 4;
/*  80 */       int o2 = (i1 & 0xF) << 2 | i2 >>> 6;
/*  81 */       int o3 = i2 & 0x3F;
/*  82 */       out[(op++)] = map1[o0];
/*  83 */       out[(op++)] = map1[o1];
/*  84 */       out[op] = (op < oDataLen ? map1[o2] : '=');
/*  85 */       op++;
/*  86 */       out[op] = (op < oDataLen ? map1[o3] : '=');
/*  87 */       op++;
/*     */     }
/*  89 */     return out;
/*     */   }
/*     */ 
/*     */   public static String decodeString(String s)
/*     */   {
/* 102 */     int a = 0;
/* 103 */     return new String(decode(s));
/*     */   }
/*     */ 
/*     */   public static byte[] decode(String s)
/*     */   {
/* 116 */     int a = 0;
/* 117 */     return decode(s.toCharArray());
/*     */   }
/*     */ 
/*     */   public static byte[] decode(char[] in)
/*     */   {
/* 131 */     int a = 0;
/* 132 */     int iLen = in.length;
/* 133 */     if (iLen % 4 != 0) {
/* 134 */       throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
/*     */     }
/* 136 */     while ((iLen > 0) && (in[(iLen - 1)] == '='))
/* 137 */       iLen--;
/* 138 */     int oLen = iLen * 3 / 4;
/* 139 */     byte[] out = new byte[oLen];
/* 140 */     int ip = 0;
/* 141 */     int op = 0;
/* 142 */     while (ip < iLen) {
/* 143 */       int i0 = in[(ip++)];
/* 144 */       int i1 = in[(ip++)];
/* 145 */       int i2 = ip < iLen ? in[(ip++)] : 65;
/* 146 */       int i3 = ip < iLen ? in[(ip++)] : 65;
/* 147 */       if ((i0 > 127) || (i1 > 127) || (i2 > 127) || (i3 > 127)) {
/* 148 */         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
/*     */       }
/* 150 */       int b0 = map2[i0];
/* 151 */       int b1 = map2[i1];
/* 152 */       int b2 = map2[i2];
/* 153 */       int b3 = map2[i3];
/* 154 */       if ((b0 < 0) || (b1 < 0) || (b2 < 0) || (b3 < 0)) {
/* 155 */         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
/*     */       }
/* 157 */       int o0 = b0 << 2 | b1 >>> 4;
/* 158 */       int o1 = (b1 & 0xF) << 4 | b2 >>> 2;
/* 159 */       int o2 = (b2 & 0x3) << 6 | b3;
/* 160 */       out[(op++)] = (byte)o0;
/* 161 */       if (op < oLen)
/* 162 */         out[(op++)] = (byte)o1;
/* 163 */       if (op < oLen)
/* 164 */         out[(op++)] = (byte)o2;
/*     */     }
/* 166 */     return out;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*   9 */     int i = 0;
/*     */ 
/*  11 */     for (char c = 'A'; c <= 'Z'; c = (char)(c + '\001'))
/*  12 */       map1[(i++)] = c;
/*  13 */     for (char c = 'a'; c <= 'z'; c = (char)(c + '\001'))
/*  14 */       map1[(i++)] = c;
/*  15 */     for (char c = '0'; c <= '9'; c = (char)(c + '\001'))
/*  16 */       map1[(i++)] = c;
/*  17 */     map1[(i++)] = '+';
/*  18 */     map1[(i++)] = '/';
/*     */ 
/*  22 */     map2 = new byte[''];
/*     */ 
/*  24 */     int a = 0;
/*  25 */     for (int i = 0; i < map2.length; i++)
/*  26 */       map2[i] = -1;
/*  27 */     for (int i = 0; i < 64; i++)
/*  28 */       map2[map1[i]] = (byte)i;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.Base64Encoder
 * JD-Core Version:    0.6.0
 */