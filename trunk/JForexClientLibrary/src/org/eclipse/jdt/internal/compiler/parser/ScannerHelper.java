/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*     */ 
/*     */ public class ScannerHelper
/*     */ {
/*  23 */   public static final long[] Bits = { 
/*  24 */     1L, 2L, 4L, 8L, 16L, 32L, 
/*  25 */     64L, 128L, 256L, 512L, 1024L, 2048L, 
/*  26 */     4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 
/*  27 */     262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 
/*  28 */     16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L, 
/*  29 */     1073741824L, -2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 
/*  30 */     68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 
/*  31 */     4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 
/*  32 */     281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 
/*  33 */     18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 
/*  34 */     1152921504606846976L, 2305843009213693952L, 4611686018427387904L, -9223372036854775808L };
/*     */   private static final int START_INDEX = 0;
/*     */   private static final int PART_INDEX = 1;
/*     */   private static long[][][] Tables;
/*     */   public static final int MAX_OBVIOUS = 128;
/*  43 */   public static final int[] OBVIOUS_IDENT_CHAR_NATURES = new int[''];
/*     */   public static final int C_JLS_SPACE = 256;
/*     */   public static final int C_SPECIAL = 128;
/*     */   public static final int C_IDENT_START = 64;
/*     */   public static final int C_UPPER_LETTER = 32;
/*     */   public static final int C_LOWER_LETTER = 16;
/*     */   public static final int C_IDENT_PART = 8;
/*     */   public static final int C_DIGIT = 4;
/*     */   public static final int C_SEPARATOR = 2;
/*     */   public static final int C_SPACE = 1;
/*     */ 
/*     */   static
/*     */   {
/*  56 */     OBVIOUS_IDENT_CHAR_NATURES[0] = 8;
/*  57 */     OBVIOUS_IDENT_CHAR_NATURES[1] = 8;
/*  58 */     OBVIOUS_IDENT_CHAR_NATURES[2] = 8;
/*  59 */     OBVIOUS_IDENT_CHAR_NATURES[3] = 8;
/*  60 */     OBVIOUS_IDENT_CHAR_NATURES[4] = 8;
/*  61 */     OBVIOUS_IDENT_CHAR_NATURES[5] = 8;
/*  62 */     OBVIOUS_IDENT_CHAR_NATURES[6] = 8;
/*  63 */     OBVIOUS_IDENT_CHAR_NATURES[7] = 8;
/*  64 */     OBVIOUS_IDENT_CHAR_NATURES[8] = 8;
/*  65 */     OBVIOUS_IDENT_CHAR_NATURES[14] = 8;
/*  66 */     OBVIOUS_IDENT_CHAR_NATURES[15] = 8;
/*  67 */     OBVIOUS_IDENT_CHAR_NATURES[16] = 8;
/*  68 */     OBVIOUS_IDENT_CHAR_NATURES[17] = 8;
/*  69 */     OBVIOUS_IDENT_CHAR_NATURES[18] = 8;
/*  70 */     OBVIOUS_IDENT_CHAR_NATURES[19] = 8;
/*  71 */     OBVIOUS_IDENT_CHAR_NATURES[20] = 8;
/*  72 */     OBVIOUS_IDENT_CHAR_NATURES[21] = 8;
/*  73 */     OBVIOUS_IDENT_CHAR_NATURES[22] = 8;
/*  74 */     OBVIOUS_IDENT_CHAR_NATURES[23] = 8;
/*  75 */     OBVIOUS_IDENT_CHAR_NATURES[24] = 8;
/*  76 */     OBVIOUS_IDENT_CHAR_NATURES[25] = 8;
/*  77 */     OBVIOUS_IDENT_CHAR_NATURES[26] = 8;
/*  78 */     OBVIOUS_IDENT_CHAR_NATURES[27] = 8;
/*  79 */     OBVIOUS_IDENT_CHAR_NATURES[127] = 8;
/*     */ 
/*  81 */     for (int i = 48; i <= 57; i++) {
/*  82 */       OBVIOUS_IDENT_CHAR_NATURES[i] = 12;
/*     */     }
/*  84 */     for (int i = 97; i <= 122; i++)
/*  85 */       OBVIOUS_IDENT_CHAR_NATURES[i] = 88;
/*  86 */     for (int i = 65; i <= 90; i++) {
/*  87 */       OBVIOUS_IDENT_CHAR_NATURES[i] = 104;
/*     */     }
/*  89 */     OBVIOUS_IDENT_CHAR_NATURES[95] = 200;
/*  90 */     OBVIOUS_IDENT_CHAR_NATURES[36] = 200;
/*     */ 
/*  92 */     OBVIOUS_IDENT_CHAR_NATURES[9] = 257;
/*  93 */     OBVIOUS_IDENT_CHAR_NATURES[10] = 257;
/*  94 */     OBVIOUS_IDENT_CHAR_NATURES[11] = 1;
/*  95 */     OBVIOUS_IDENT_CHAR_NATURES[12] = 257;
/*  96 */     OBVIOUS_IDENT_CHAR_NATURES[13] = 257;
/*  97 */     OBVIOUS_IDENT_CHAR_NATURES[28] = 1;
/*  98 */     OBVIOUS_IDENT_CHAR_NATURES[29] = 1;
/*  99 */     OBVIOUS_IDENT_CHAR_NATURES[30] = 1;
/* 100 */     OBVIOUS_IDENT_CHAR_NATURES[31] = 1;
/* 101 */     OBVIOUS_IDENT_CHAR_NATURES[32] = 257;
/*     */ 
/* 103 */     OBVIOUS_IDENT_CHAR_NATURES[46] = 2;
/* 104 */     OBVIOUS_IDENT_CHAR_NATURES[58] = 2;
/* 105 */     OBVIOUS_IDENT_CHAR_NATURES[59] = 2;
/* 106 */     OBVIOUS_IDENT_CHAR_NATURES[44] = 2;
/* 107 */     OBVIOUS_IDENT_CHAR_NATURES[91] = 2;
/* 108 */     OBVIOUS_IDENT_CHAR_NATURES[93] = 2;
/* 109 */     OBVIOUS_IDENT_CHAR_NATURES[40] = 2;
/* 110 */     OBVIOUS_IDENT_CHAR_NATURES[41] = 2;
/* 111 */     OBVIOUS_IDENT_CHAR_NATURES[123] = 2;
/* 112 */     OBVIOUS_IDENT_CHAR_NATURES[125] = 2;
/* 113 */     OBVIOUS_IDENT_CHAR_NATURES[43] = 2;
/* 114 */     OBVIOUS_IDENT_CHAR_NATURES[45] = 2;
/* 115 */     OBVIOUS_IDENT_CHAR_NATURES[42] = 2;
/* 116 */     OBVIOUS_IDENT_CHAR_NATURES[47] = 2;
/* 117 */     OBVIOUS_IDENT_CHAR_NATURES[61] = 2;
/* 118 */     OBVIOUS_IDENT_CHAR_NATURES[38] = 2;
/* 119 */     OBVIOUS_IDENT_CHAR_NATURES[124] = 2;
/* 120 */     OBVIOUS_IDENT_CHAR_NATURES[63] = 2;
/* 121 */     OBVIOUS_IDENT_CHAR_NATURES[60] = 2;
/* 122 */     OBVIOUS_IDENT_CHAR_NATURES[62] = 2;
/* 123 */     OBVIOUS_IDENT_CHAR_NATURES[33] = 2;
/* 124 */     OBVIOUS_IDENT_CHAR_NATURES[37] = 2;
/* 125 */     OBVIOUS_IDENT_CHAR_NATURES[94] = 2;
/* 126 */     OBVIOUS_IDENT_CHAR_NATURES[126] = 2;
/* 127 */     OBVIOUS_IDENT_CHAR_NATURES[34] = 2;
/* 128 */     OBVIOUS_IDENT_CHAR_NATURES[39] = 2;
/*     */ 
/* 132 */     Tables = new long[2][][];
/* 133 */     Tables[0] = new long[2][];
/* 134 */     Tables[1] = new long[3][];
/*     */     try {
/* 136 */       DataInputStream inputStream = new DataInputStream(ScannerHelper.class.getResourceAsStream("start1.rsc"));
/* 137 */       long[] readValues = new long[1024];
/* 138 */       for (int i = 0; i < 1024; i++) {
/* 139 */         readValues[i] = inputStream.readLong();
/*     */       }
/* 141 */       inputStream.close();
/* 142 */       Tables[0][0] = readValues;
/*     */     } catch (FileNotFoundException e) {
/* 144 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 146 */       e.printStackTrace();
/*     */     }
/*     */     try {
/* 149 */       DataInputStream inputStream = new DataInputStream(ScannerHelper.class.getResourceAsStream("start2.rsc"));
/* 150 */       long[] readValues = new long[1024];
/* 151 */       for (int i = 0; i < 1024; i++) {
/* 152 */         readValues[i] = inputStream.readLong();
/*     */       }
/* 154 */       inputStream.close();
/* 155 */       Tables[0][1] = readValues;
/*     */     } catch (FileNotFoundException e) {
/* 157 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 159 */       e.printStackTrace();
/*     */     }
/*     */     try {
/* 162 */       DataInputStream inputStream = new DataInputStream(ScannerHelper.class.getResourceAsStream("part1.rsc"));
/* 163 */       long[] readValues = new long[1024];
/* 164 */       for (int i = 0; i < 1024; i++) {
/* 165 */         readValues[i] = inputStream.readLong();
/*     */       }
/* 167 */       inputStream.close();
/* 168 */       Tables[1][0] = readValues;
/*     */     } catch (FileNotFoundException e) {
/* 170 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 172 */       e.printStackTrace();
/*     */     }
/*     */     try {
/* 175 */       DataInputStream inputStream = new DataInputStream(ScannerHelper.class.getResourceAsStream("part2.rsc"));
/* 176 */       long[] readValues = new long[1024];
/* 177 */       for (int i = 0; i < 1024; i++) {
/* 178 */         readValues[i] = inputStream.readLong();
/*     */       }
/* 180 */       inputStream.close();
/* 181 */       Tables[1][1] = readValues;
/*     */     } catch (FileNotFoundException e) {
/* 183 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 185 */       e.printStackTrace();
/*     */     }
/*     */     try {
/* 188 */       DataInputStream inputStream = new DataInputStream(ScannerHelper.class.getResourceAsStream("part14.rsc"));
/* 189 */       long[] readValues = new long[1024];
/* 190 */       for (int i = 0; i < 1024; i++) {
/* 191 */         readValues[i] = inputStream.readLong();
/*     */       }
/* 193 */       inputStream.close();
/* 194 */       Tables[1][2] = readValues;
/*     */     } catch (FileNotFoundException e) {
/* 196 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 198 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final boolean isBitSet(long[] values, int i) {
/*     */     try {
/* 204 */       return (values[(i / 64)] & Bits[(i % 64)]) != 0L; } catch (NullPointerException localNullPointerException) {
/*     */     }
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isJavaIdentifierPart(char c) {
/* 210 */     if (c < '') {
/* 211 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x8) != 0;
/*     */     }
/* 213 */     return Character.isJavaIdentifierPart(c);
/*     */   }
/*     */   public static boolean isJavaIdentifierPart(char high, char low) {
/* 216 */     int codePoint = toCodePoint(high, low);
/* 217 */     switch ((codePoint & 0x1F0000) >> 16) {
/*     */     case 0:
/* 219 */       return Character.isJavaIdentifierPart((char)codePoint);
/*     */     case 1:
/* 221 */       return isBitSet(Tables[1][0], codePoint & 0xFFFF);
/*     */     case 2:
/* 223 */       return isBitSet(Tables[1][1], codePoint & 0xFFFF);
/*     */     case 14:
/* 225 */       return isBitSet(Tables[1][2], codePoint & 0xFFFF);
/*     */     }
/* 227 */     return false;
/*     */   }
/*     */   public static boolean isJavaIdentifierStart(char c) {
/* 230 */     if (c < '') {
/* 231 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0;
/*     */     }
/* 233 */     return Character.isJavaIdentifierStart(c);
/*     */   }
/*     */   public static boolean isJavaIdentifierStart(char high, char low) {
/* 236 */     int codePoint = toCodePoint(high, low);
/* 237 */     switch ((codePoint & 0x1F0000) >> 16) {
/*     */     case 0:
/* 239 */       return Character.isJavaIdentifierStart((char)codePoint);
/*     */     case 1:
/* 241 */       return isBitSet(Tables[0][0], codePoint & 0xFFFF);
/*     */     case 2:
/* 243 */       return isBitSet(Tables[0][1], codePoint & 0xFFFF);
/*     */     }
/* 245 */     return false;
/*     */   }
/*     */ 
/*     */   private static int toCodePoint(char high, char low) {
/* 249 */     return (high - 55296) * 1024 + (low - 56320) + 65536;
/*     */   }
/*     */   public static boolean isDigit(char c) throws InvalidInputException {
/* 252 */     if (c < '') {
/* 253 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x4) != 0;
/*     */     }
/* 255 */     if (Character.isDigit(c)) {
/* 256 */       throw new InvalidInputException("Invalid_Digit");
/*     */     }
/* 258 */     return false;
/*     */   }
/*     */   public static int digit(char c, int radix) {
/* 261 */     if (c < '') {
/* 262 */       switch (radix) {
/*     */       case 8:
/* 264 */         if ((c >= '0') && (c <= '7')) {
/* 265 */           return c - '0';
/*     */         }
/* 267 */         return -1;
/*     */       case 10:
/* 269 */         if ((c >= '0') && (c <= '9')) {
/* 270 */           return c - '0';
/*     */         }
/* 272 */         return -1;
/*     */       case 16:
/* 274 */         if ((c >= '0') && (c <= '9')) {
/* 275 */           return c - '0';
/*     */         }
/* 277 */         if ((c >= 'A') && (c <= 'F')) {
/* 278 */           return c - 'A' + 10;
/*     */         }
/* 280 */         if ((c >= 'a') && (c <= 'f')) {
/* 281 */           return c - 'a' + 10;
/*     */         }
/* 283 */         return -1;
/*     */       }
/*     */     }
/* 286 */     return Character.digit(c, radix);
/*     */   }
/*     */   public static int getNumericValue(char c) {
/* 289 */     if (c < '') {
/* 290 */       switch (OBVIOUS_IDENT_CHAR_NATURES[c]) {
/*     */       case 4:
/* 292 */         return c - '0';
/*     */       case 16:
/* 294 */         return '\n' + c - 97;
/*     */       case 32:
/* 296 */         return '\n' + c - 65;
/*     */       }
/*     */     }
/* 299 */     return Character.getNumericValue(c);
/*     */   }
/*     */   public static char toUpperCase(char c) {
/* 302 */     if (c < '') {
/* 303 */       if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0)
/* 304 */         return c;
/* 305 */       if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0) {
/* 306 */         return (char)(c - ' ');
/*     */       }
/*     */     }
/* 309 */     return Character.toUpperCase(c);
/*     */   }
/*     */   public static char toLowerCase(char c) {
/* 312 */     if (c < '') {
/* 313 */       if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0)
/* 314 */         return c;
/* 315 */       if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0) {
/* 316 */         return (char)(' ' + c);
/*     */       }
/*     */     }
/* 319 */     return Character.toLowerCase(c);
/*     */   }
/*     */   public static boolean isLowerCase(char c) {
/* 322 */     if (c < '') {
/* 323 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0;
/*     */     }
/* 325 */     return Character.isLowerCase(c);
/*     */   }
/*     */   public static boolean isUpperCase(char c) {
/* 328 */     if (c < '') {
/* 329 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0;
/*     */     }
/* 331 */     return Character.isUpperCase(c);
/*     */   }
/*     */ 
/*     */   public static boolean isWhitespace(char c)
/*     */   {
/* 339 */     if (c < '') {
/* 340 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x1) != 0;
/*     */     }
/* 342 */     return Character.isWhitespace(c);
/*     */   }
/*     */   public static boolean isLetter(char c) {
/* 345 */     if (c < '') {
/* 346 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x30) != 0;
/*     */     }
/* 348 */     return Character.isLetter(c);
/*     */   }
/*     */   public static boolean isLetterOrDigit(char c) {
/* 351 */     if (c < '') {
/* 352 */       return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x34) != 0;
/*     */     }
/* 354 */     return Character.isLetterOrDigit(c);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.ScannerHelper
 * JD-Core Version:    0.6.0
 */