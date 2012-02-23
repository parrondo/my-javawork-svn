/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public final class UnicodeUtil
/*     */ {
/*     */   public static final int UNI_SUR_HIGH_START = 55296;
/*     */   public static final int UNI_SUR_HIGH_END = 56319;
/*     */   public static final int UNI_SUR_LOW_START = 56320;
/*     */   public static final int UNI_SUR_LOW_END = 57343;
/*     */   public static final int UNI_REPLACEMENT_CHAR = 65533;
/*     */   private static final long UNI_MAX_BMP = 65535L;
/*     */   private static final int HALF_BASE = 65536;
/*     */   private static final long HALF_SHIFT = 10L;
/*     */   private static final long HALF_MASK = 1023L;
/*     */   private static final int SURROGATE_OFFSET = -56613888;
/*     */   private static final int LEAD_SURROGATE_SHIFT_ = 10;
/*     */   private static final int TRAIL_SURROGATE_MASK_ = 1023;
/*     */   private static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
/*     */   private static final int LEAD_SURROGATE_MIN_VALUE = 55296;
/*     */   private static final int SUPPLEMENTARY_MIN_VALUE = 65536;
/*     */   private static final int LEAD_SURROGATE_OFFSET_ = 55232;
/*     */ 
/*     */   public static int UTF16toUTF8WithHash(char[] source, int offset, int length, BytesRef result)
/*     */   {
/* 155 */     int hash = 0;
/* 156 */     int upto = 0;
/* 157 */     int i = offset;
/* 158 */     int end = offset + length;
/* 159 */     byte[] out = result.bytes;
/*     */ 
/* 161 */     int maxLen = length * 4;
/* 162 */     if (out.length < maxLen)
/* 163 */       out = result.bytes = new byte[ArrayUtil.oversize(maxLen, 1)];
/* 164 */     result.offset = 0;
/*     */ 
/* 166 */     while (i < end)
/*     */     {
/* 168 */       int code = source[(i++)];
/*     */ 
/* 170 */       if (code < 128) {
/* 171 */         hash = 31 * hash + (out[(upto++)] = (byte)code);
/* 172 */       } else if (code < 2048) {
/* 173 */         hash = 31 * hash + (out[(upto++)] = (byte)(0xC0 | code >> 6));
/* 174 */         hash = 31 * hash + (out[(upto++)] = (byte)(0x80 | code & 0x3F));
/* 175 */       } else if ((code < 55296) || (code > 57343)) {
/* 176 */         hash = 31 * hash + (out[(upto++)] = (byte)(0xE0 | code >> 12));
/* 177 */         hash = 31 * hash + (out[(upto++)] = (byte)(0x80 | code >> 6 & 0x3F));
/* 178 */         hash = 31 * hash + (out[(upto++)] = (byte)(0x80 | code & 0x3F));
/*     */       }
/*     */       else
/*     */       {
/* 182 */         if ((code < 56320) && (i < end)) {
/* 183 */           int utf32 = source[i];
/*     */ 
/* 185 */           if ((utf32 >= 56320) && (utf32 <= 57343)) {
/* 186 */             utf32 = (code << 10) + utf32 + -56613888;
/* 187 */             i++;
/* 188 */             hash = 31 * hash + (out[(upto++)] = (byte)(0xF0 | utf32 >> 18));
/* 189 */             hash = 31 * hash + (out[(upto++)] = (byte)(0x80 | utf32 >> 12 & 0x3F));
/* 190 */             hash = 31 * hash + (out[(upto++)] = (byte)(0x80 | utf32 >> 6 & 0x3F));
/* 191 */             hash = 31 * hash + (out[(upto++)] = (byte)(0x80 | utf32 & 0x3F));
/* 192 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 197 */         hash = 31 * hash + (out[(upto++)] = -17);
/* 198 */         hash = 31 * hash + (out[(upto++)] = -65);
/* 199 */         hash = 31 * hash + (out[(upto++)] = -67);
/*     */       }
/*     */     }
/*     */ 
/* 203 */     result.length = upto;
/* 204 */     return hash;
/*     */   }
/*     */ 
/*     */   public static void UTF16toUTF8(char[] source, int offset, UTF8Result result)
/*     */   {
/* 212 */     int upto = 0;
/* 213 */     int i = offset;
/* 214 */     byte[] out = result.result;
/*     */     while (true)
/*     */     {
/* 218 */       int code = source[(i++)];
/*     */ 
/* 220 */       if (upto + 4 > out.length) {
/* 221 */         out = result.result = ArrayUtil.grow(out, upto + 4);
/*     */       }
/* 223 */       if (code < 128) {
/* 224 */         out[(upto++)] = (byte)code;
/* 225 */       } else if (code < 2048) {
/* 226 */         out[(upto++)] = (byte)(0xC0 | code >> 6);
/* 227 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/* 228 */       } else if ((code < 55296) || (code > 57343)) {
/* 229 */         if (code == 65535) {
/*     */           break;
/*     */         }
/* 232 */         out[(upto++)] = (byte)(0xE0 | code >> 12);
/* 233 */         out[(upto++)] = (byte)(0x80 | code >> 6 & 0x3F);
/* 234 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 238 */         if ((code < 56320) && (source[i] != 65535)) {
/* 239 */           int utf32 = source[i];
/*     */ 
/* 241 */           if ((utf32 >= 56320) && (utf32 <= 57343)) {
/* 242 */             utf32 = (code - 55232 << 10) + (utf32 & 0x3FF);
/* 243 */             i++;
/* 244 */             out[(upto++)] = (byte)(0xF0 | utf32 >> 18);
/* 245 */             out[(upto++)] = (byte)(0x80 | utf32 >> 12 & 0x3F);
/* 246 */             out[(upto++)] = (byte)(0x80 | utf32 >> 6 & 0x3F);
/* 247 */             out[(upto++)] = (byte)(0x80 | utf32 & 0x3F);
/* 248 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 253 */         out[(upto++)] = -17;
/* 254 */         out[(upto++)] = -65;
/* 255 */         out[(upto++)] = -67;
/*     */       }
/*     */     }
/*     */ 
/* 259 */     result.length = upto;
/*     */   }
/*     */ 
/*     */   public static void UTF16toUTF8(char[] source, int offset, int length, UTF8Result result)
/*     */   {
/* 267 */     int upto = 0;
/* 268 */     int i = offset;
/* 269 */     int end = offset + length;
/* 270 */     byte[] out = result.result;
/*     */ 
/* 272 */     while (i < end)
/*     */     {
/* 274 */       int code = source[(i++)];
/*     */ 
/* 276 */       if (upto + 4 > out.length) {
/* 277 */         out = result.result = ArrayUtil.grow(out, upto + 4);
/*     */       }
/* 279 */       if (code < 128) {
/* 280 */         out[(upto++)] = (byte)code;
/* 281 */       } else if (code < 2048) {
/* 282 */         out[(upto++)] = (byte)(0xC0 | code >> 6);
/* 283 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/* 284 */       } else if ((code < 55296) || (code > 57343)) {
/* 285 */         out[(upto++)] = (byte)(0xE0 | code >> 12);
/* 286 */         out[(upto++)] = (byte)(0x80 | code >> 6 & 0x3F);
/* 287 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 291 */         if ((code < 56320) && (i < end) && (source[i] != 65535)) {
/* 292 */           int utf32 = source[i];
/*     */ 
/* 294 */           if ((utf32 >= 56320) && (utf32 <= 57343)) {
/* 295 */             utf32 = (code - 55232 << 10) + (utf32 & 0x3FF);
/* 296 */             i++;
/* 297 */             out[(upto++)] = (byte)(0xF0 | utf32 >> 18);
/* 298 */             out[(upto++)] = (byte)(0x80 | utf32 >> 12 & 0x3F);
/* 299 */             out[(upto++)] = (byte)(0x80 | utf32 >> 6 & 0x3F);
/* 300 */             out[(upto++)] = (byte)(0x80 | utf32 & 0x3F);
/* 301 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 306 */         out[(upto++)] = -17;
/* 307 */         out[(upto++)] = -65;
/* 308 */         out[(upto++)] = -67;
/*     */       }
/*     */     }
/*     */ 
/* 312 */     result.length = upto;
/*     */   }
/*     */ 
/*     */   public static void UTF16toUTF8(String s, int offset, int length, UTF8Result result)
/*     */   {
/* 319 */     int end = offset + length;
/*     */ 
/* 321 */     byte[] out = result.result;
/*     */ 
/* 323 */     int upto = 0;
/* 324 */     for (int i = offset; i < end; i++) {
/* 325 */       int code = s.charAt(i);
/*     */ 
/* 327 */       if (upto + 4 > out.length) {
/* 328 */         out = result.result = ArrayUtil.grow(out, upto + 4);
/*     */       }
/* 330 */       if (code < 128) {
/* 331 */         out[(upto++)] = (byte)code;
/* 332 */       } else if (code < 2048) {
/* 333 */         out[(upto++)] = (byte)(0xC0 | code >> 6);
/* 334 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/* 335 */       } else if ((code < 55296) || (code > 57343)) {
/* 336 */         out[(upto++)] = (byte)(0xE0 | code >> 12);
/* 337 */         out[(upto++)] = (byte)(0x80 | code >> 6 & 0x3F);
/* 338 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 342 */         if ((code < 56320) && (i < end - 1)) {
/* 343 */           int utf32 = s.charAt(i + 1);
/*     */ 
/* 345 */           if ((utf32 >= 56320) && (utf32 <= 57343)) {
/* 346 */             utf32 = (code - 55232 << 10) + (utf32 & 0x3FF);
/* 347 */             i++;
/* 348 */             out[(upto++)] = (byte)(0xF0 | utf32 >> 18);
/* 349 */             out[(upto++)] = (byte)(0x80 | utf32 >> 12 & 0x3F);
/* 350 */             out[(upto++)] = (byte)(0x80 | utf32 >> 6 & 0x3F);
/* 351 */             out[(upto++)] = (byte)(0x80 | utf32 & 0x3F);
/* 352 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 357 */         out[(upto++)] = -17;
/* 358 */         out[(upto++)] = -65;
/* 359 */         out[(upto++)] = -67;
/*     */       }
/*     */     }
/*     */ 
/* 363 */     result.length = upto;
/*     */   }
/*     */ 
/*     */   public static void UTF16toUTF8(CharSequence s, int offset, int length, BytesRef result)
/*     */   {
/* 370 */     int end = offset + length;
/*     */ 
/* 372 */     byte[] out = result.bytes;
/* 373 */     result.offset = 0;
/*     */ 
/* 375 */     int maxLen = length * 4;
/* 376 */     if (out.length < maxLen) {
/* 377 */       out = result.bytes = new byte[maxLen];
/*     */     }
/* 379 */     int upto = 0;
/* 380 */     for (int i = offset; i < end; i++) {
/* 381 */       int code = s.charAt(i);
/*     */ 
/* 383 */       if (code < 128) {
/* 384 */         out[(upto++)] = (byte)code;
/* 385 */       } else if (code < 2048) {
/* 386 */         out[(upto++)] = (byte)(0xC0 | code >> 6);
/* 387 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/* 388 */       } else if ((code < 55296) || (code > 57343)) {
/* 389 */         out[(upto++)] = (byte)(0xE0 | code >> 12);
/* 390 */         out[(upto++)] = (byte)(0x80 | code >> 6 & 0x3F);
/* 391 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 395 */         if ((code < 56320) && (i < end - 1)) {
/* 396 */           int utf32 = s.charAt(i + 1);
/*     */ 
/* 398 */           if ((utf32 >= 56320) && (utf32 <= 57343)) {
/* 399 */             utf32 = (code << 10) + utf32 + -56613888;
/* 400 */             i++;
/* 401 */             out[(upto++)] = (byte)(0xF0 | utf32 >> 18);
/* 402 */             out[(upto++)] = (byte)(0x80 | utf32 >> 12 & 0x3F);
/* 403 */             out[(upto++)] = (byte)(0x80 | utf32 >> 6 & 0x3F);
/* 404 */             out[(upto++)] = (byte)(0x80 | utf32 & 0x3F);
/* 405 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 410 */         out[(upto++)] = -17;
/* 411 */         out[(upto++)] = -65;
/* 412 */         out[(upto++)] = -67;
/*     */       }
/*     */     }
/*     */ 
/* 416 */     result.length = upto;
/*     */   }
/*     */ 
/*     */   public static void UTF16toUTF8(char[] source, int offset, int length, BytesRef result)
/*     */   {
/* 424 */     int upto = 0;
/* 425 */     int i = offset;
/* 426 */     int end = offset + length;
/* 427 */     byte[] out = result.bytes;
/*     */ 
/* 429 */     int maxLen = length * 4;
/* 430 */     if (out.length < maxLen)
/* 431 */       out = result.bytes = new byte[maxLen];
/* 432 */     result.offset = 0;
/*     */ 
/* 434 */     while (i < end)
/*     */     {
/* 436 */       int code = source[(i++)];
/*     */ 
/* 438 */       if (code < 128) {
/* 439 */         out[(upto++)] = (byte)code;
/* 440 */       } else if (code < 2048) {
/* 441 */         out[(upto++)] = (byte)(0xC0 | code >> 6);
/* 442 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/* 443 */       } else if ((code < 55296) || (code > 57343)) {
/* 444 */         out[(upto++)] = (byte)(0xE0 | code >> 12);
/* 445 */         out[(upto++)] = (byte)(0x80 | code >> 6 & 0x3F);
/* 446 */         out[(upto++)] = (byte)(0x80 | code & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 450 */         if ((code < 56320) && (i < end)) {
/* 451 */           int utf32 = source[i];
/*     */ 
/* 453 */           if ((utf32 >= 56320) && (utf32 <= 57343)) {
/* 454 */             utf32 = (code << 10) + utf32 + -56613888;
/* 455 */             i++;
/* 456 */             out[(upto++)] = (byte)(0xF0 | utf32 >> 18);
/* 457 */             out[(upto++)] = (byte)(0x80 | utf32 >> 12 & 0x3F);
/* 458 */             out[(upto++)] = (byte)(0x80 | utf32 >> 6 & 0x3F);
/* 459 */             out[(upto++)] = (byte)(0x80 | utf32 & 0x3F);
/* 460 */             continue;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 465 */         out[(upto++)] = -17;
/* 466 */         out[(upto++)] = -65;
/* 467 */         out[(upto++)] = -67;
/*     */       }
/*     */     }
/*     */ 
/* 471 */     result.length = upto;
/*     */   }
/*     */ 
/*     */   public static void UTF8toUTF16(byte[] utf8, int offset, int length, UTF16Result result)
/*     */   {
/* 480 */     int end = offset + length;
/* 481 */     char[] out = result.result;
/* 482 */     if (result.offsets.length <= end) {
/* 483 */       result.offsets = ArrayUtil.grow(result.offsets, end + 1);
/*     */     }
/* 485 */     int[] offsets = result.offsets;
/*     */ 
/* 489 */     int upto = offset;
/* 490 */     while (offsets[upto] == -1) {
/* 491 */       upto--;
/*     */     }
/* 493 */     int outUpto = offsets[upto];
/*     */ 
/* 496 */     if (outUpto + length >= out.length) {
/* 497 */       out = result.result = ArrayUtil.grow(out, outUpto + length + 1);
/*     */     }
/*     */ 
/* 500 */     while (upto < end)
/*     */     {
/* 502 */       int b = utf8[upto] & 0xFF;
/*     */ 
/* 505 */       offsets[(upto++)] = outUpto;
/*     */       int ch;
/*     */       int ch;
/* 507 */       if (b < 192) {
/* 508 */         assert (b < 128);
/* 509 */         ch = b;
/* 510 */       } else if (b < 224) {
/* 511 */         int ch = ((b & 0x1F) << 6) + (utf8[upto] & 0x3F);
/* 512 */         offsets[(upto++)] = -1;
/* 513 */       } else if (b < 240) {
/* 514 */         int ch = ((b & 0xF) << 12) + ((utf8[upto] & 0x3F) << 6) + (utf8[(upto + 1)] & 0x3F);
/* 515 */         offsets[(upto++)] = -1;
/* 516 */         offsets[(upto++)] = -1;
/*     */       } else {
/* 518 */         assert (b < 248);
/* 519 */         ch = ((b & 0x7) << 18) + ((utf8[upto] & 0x3F) << 12) + ((utf8[(upto + 1)] & 0x3F) << 6) + (utf8[(upto + 2)] & 0x3F);
/* 520 */         offsets[(upto++)] = -1;
/* 521 */         offsets[(upto++)] = -1;
/* 522 */         offsets[(upto++)] = -1;
/*     */       }
/*     */ 
/* 525 */       if (ch <= 65535L)
/*     */       {
/* 527 */         out[(outUpto++)] = (char)ch;
/*     */       }
/*     */       else {
/* 530 */         int chHalf = ch - 65536;
/* 531 */         out[(outUpto++)] = (char)((chHalf >> 10) + 55296);
/* 532 */         out[(outUpto++)] = (char)(int)((chHalf & 0x3FF) + 56320L);
/*     */       }
/*     */     }
/*     */ 
/* 536 */     offsets[upto] = outUpto;
/* 537 */     result.length = outUpto;
/*     */   }
/*     */ 
/*     */   public static String newString(int[] codePoints, int offset, int count)
/*     */   {
/* 666 */     if (count < 0) {
/* 667 */       throw new IllegalArgumentException();
/*     */     }
/* 669 */     char[] chars = new char[count];
/* 670 */     int w = 0;
/* 671 */     int r = offset; for (int e = offset + count; r < e; r++) {
/* 672 */       int cp = codePoints[r];
/* 673 */       if ((cp < 0) || (cp > 1114111))
/* 674 */         throw new IllegalArgumentException();
/*     */       while (true) {
/*     */         try
/*     */         {
/* 678 */           if (cp < 65536) {
/* 679 */             chars[w] = (char)cp;
/* 680 */             w++;
/*     */           } else {
/* 682 */             chars[w] = (char)(55232 + (cp >> 10));
/* 683 */             chars[(w + 1)] = (char)(56320 + (cp & 0x3FF));
/* 684 */             w += 2;
/*     */           }
/*     */         }
/*     */         catch (IndexOutOfBoundsException ex) {
/* 688 */           int newlen = (int)Math.ceil(codePoints.length * (w + 2) / (r - offset + 1));
/*     */ 
/* 690 */           char[] temp = new char[newlen];
/* 691 */           System.arraycopy(chars, 0, temp, 0, w);
/* 692 */           chars = temp;
/*     */         }
/*     */       }
/*     */     }
/* 696 */     return new String(chars, 0, w);
/*     */   }
/*     */ 
/*     */   public static void UTF8toUTF16(byte[] utf8, int offset, int length, CharsRef chars)
/*     */   {
/* 708 */     int out_offset = chars.offset = 0;
/* 709 */     char[] out = chars.chars = ArrayUtil.grow(chars.chars, length);
/* 710 */     int limit = offset + length;
/* 711 */     while (offset < limit) {
/* 712 */       int b = utf8[(offset++)] & 0xFF;
/* 713 */       if (b < 192) {
/* 714 */         assert (b < 128);
/* 715 */         out[(out_offset++)] = (char)b;
/* 716 */       } else if (b < 224) {
/* 717 */         out[(out_offset++)] = (char)(((b & 0x1F) << 6) + (utf8[(offset++)] & 0x3F));
/* 718 */       } else if (b < 240) {
/* 719 */         out[(out_offset++)] = (char)(((b & 0xF) << 12) + ((utf8[offset] & 0x3F) << 6) + (utf8[(offset + 1)] & 0x3F));
/* 720 */         offset += 2;
/*     */       } else {
/* 722 */         assert (b < 248);
/* 723 */         int ch = ((b & 0x7) << 18) + ((utf8[offset] & 0x3F) << 12) + ((utf8[(offset + 1)] & 0x3F) << 6) + (utf8[(offset + 2)] & 0x3F);
/* 724 */         offset += 3;
/* 725 */         if (ch < 65535L) {
/* 726 */           out[(out_offset++)] = (char)ch;
/*     */         } else {
/* 728 */           int chHalf = ch - 65536;
/* 729 */           out[(out_offset++)] = (char)((chHalf >> 10) + 55296);
/* 730 */           out[(out_offset++)] = (char)(int)((chHalf & 0x3FF) + 56320L);
/*     */         }
/*     */       }
/*     */     }
/* 734 */     chars.length = (out_offset - chars.offset);
/*     */   }
/*     */ 
/*     */   public static void UTF8toUTF16(BytesRef bytesRef, CharsRef chars)
/*     */   {
/* 742 */     UTF8toUTF16(bytesRef.bytes, bytesRef.offset, bytesRef.length, chars);
/*     */   }
/*     */ 
/*     */   public static final class UTF16Result
/*     */   {
/* 135 */     public char[] result = new char[10];
/* 136 */     public int[] offsets = new int[10];
/*     */     public int length;
/*     */ 
/*     */     public void setLength(int newLength)
/*     */     {
/* 140 */       if (this.result.length < newLength) {
/* 141 */         this.result = ArrayUtil.grow(this.result, newLength);
/*     */       }
/* 143 */       this.length = newLength;
/*     */     }
/*     */ 
/*     */     public void copyText(UTF16Result other) {
/* 147 */       setLength(other.length);
/* 148 */       System.arraycopy(other.result, 0, this.result, 0, this.length);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class UTF8Result
/*     */   {
/* 120 */     public byte[] result = new byte[10];
/*     */     public int length;
/*     */ 
/*     */     public void setLength(int newLength)
/*     */     {
/* 124 */       if (this.result.length < newLength) {
/* 125 */         this.result = ArrayUtil.grow(this.result, newLength);
/*     */       }
/* 127 */       this.length = newLength;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.UnicodeUtil
 * JD-Core Version:    0.6.0
 */