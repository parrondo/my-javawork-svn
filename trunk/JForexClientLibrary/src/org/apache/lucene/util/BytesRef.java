/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public final class BytesRef
/*     */   implements Comparable<BytesRef>
/*     */ {
/*     */   static final int HASH_PRIME = 31;
/*     */   public static final byte[] EMPTY_BYTES;
/*     */   public byte[] bytes;
/*     */   public int offset;
/*     */   public int length;
/*     */   private static final Comparator<BytesRef> utf8SortedAsUnicodeSortOrder;
/*     */   private static final Comparator<BytesRef> utf8SortedAsUTF16SortOrder;
/*     */ 
/*     */   public BytesRef()
/*     */   {
/*  42 */     this.bytes = EMPTY_BYTES;
/*     */   }
/*     */ 
/*     */   public BytesRef(byte[] bytes, int offset, int length)
/*     */   {
/*  49 */     assert (bytes != null);
/*  50 */     this.bytes = bytes;
/*  51 */     this.offset = offset;
/*  52 */     this.length = length;
/*     */   }
/*     */ 
/*     */   public BytesRef(byte[] bytes)
/*     */   {
/*  58 */     assert (bytes != null);
/*  59 */     this.bytes = bytes;
/*  60 */     this.offset = 0;
/*  61 */     this.length = bytes.length;
/*     */   }
/*     */ 
/*     */   public BytesRef(int capacity) {
/*  65 */     this.bytes = new byte[capacity];
/*     */   }
/*     */ 
/*     */   public BytesRef(CharSequence text)
/*     */   {
/*  74 */     this();
/*  75 */     copy(text);
/*     */   }
/*     */ 
/*     */   public BytesRef(char[] text, int offset, int length)
/*     */   {
/*  84 */     this(length * 4);
/*  85 */     copy(text, offset, length);
/*     */   }
/*     */ 
/*     */   public BytesRef(BytesRef other) {
/*  89 */     this();
/*  90 */     copy(other);
/*     */   }
/*     */ 
/*     */   public void copy(CharSequence text)
/*     */   {
/* 113 */     UnicodeUtil.UTF16toUTF8(text, 0, text.length(), this);
/*     */   }
/*     */ 
/*     */   public void copy(char[] text, int offset, int length)
/*     */   {
/* 123 */     UnicodeUtil.UTF16toUTF8(text, offset, length, this);
/*     */   }
/*     */ 
/*     */   public boolean bytesEquals(BytesRef other) {
/* 127 */     if (this.length == other.length) {
/* 128 */       int otherUpto = other.offset;
/* 129 */       byte[] otherBytes = other.bytes;
/* 130 */       int end = this.offset + this.length;
/* 131 */       for (int upto = this.offset; upto < end; otherUpto++) {
/* 132 */         if (this.bytes[upto] != otherBytes[otherUpto])
/* 133 */           return false;
/* 131 */         upto++;
/*     */       }
/*     */ 
/* 136 */       return true;
/*     */     }
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 144 */     return new BytesRef(this);
/*     */   }
/*     */ 
/*     */   private boolean sliceEquals(BytesRef other, int pos) {
/* 148 */     if ((pos < 0) || (this.length - pos < other.length)) {
/* 149 */       return false;
/*     */     }
/* 151 */     int i = this.offset + pos;
/* 152 */     int j = other.offset;
/* 153 */     int k = other.offset + other.length;
/*     */ 
/* 155 */     while (j < k) {
/* 156 */       if (this.bytes[(i++)] != other.bytes[(j++)]) {
/* 157 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean startsWith(BytesRef other) {
/* 165 */     return sliceEquals(other, 0);
/*     */   }
/*     */ 
/*     */   public boolean endsWith(BytesRef other) {
/* 169 */     return sliceEquals(other, this.length - other.length);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 183 */     int result = 0;
/* 184 */     int end = this.offset + this.length;
/* 185 */     for (int i = this.offset; i < end; i++) {
/* 186 */       result = 31 * result + this.bytes[i];
/*     */     }
/* 188 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 193 */     if (other == null) {
/* 194 */       return false;
/*     */     }
/* 196 */     return bytesEquals((BytesRef)other);
/*     */   }
/*     */ 
/*     */   public String utf8ToString()
/*     */   {
/*     */     try
/*     */     {
/* 203 */       return new String(this.bytes, this.offset, this.length, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException uee) {
/*     */     }
/* 207 */     throw new RuntimeException(uee);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     StringBuilder sb = new StringBuilder();
/* 215 */     sb.append('[');
/* 216 */     int end = this.offset + this.length;
/* 217 */     for (int i = this.offset; i < end; i++) {
/* 218 */       if (i > this.offset) {
/* 219 */         sb.append(' ');
/*     */       }
/* 221 */       sb.append(Integer.toHexString(this.bytes[i] & 0xFF));
/*     */     }
/* 223 */     sb.append(']');
/* 224 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public void copy(BytesRef other) {
/* 228 */     if (this.bytes.length < other.length) {
/* 229 */       this.bytes = new byte[other.length];
/*     */     }
/* 231 */     System.arraycopy(other.bytes, other.offset, this.bytes, 0, other.length);
/* 232 */     this.length = other.length;
/* 233 */     this.offset = 0;
/*     */   }
/*     */ 
/*     */   public void append(BytesRef other) {
/* 237 */     int newLen = this.length + other.length;
/* 238 */     if (this.bytes.length < newLen) {
/* 239 */       byte[] newBytes = new byte[newLen];
/* 240 */       System.arraycopy(this.bytes, this.offset, newBytes, 0, this.length);
/* 241 */       this.offset = 0;
/* 242 */       this.bytes = newBytes;
/*     */     }
/* 244 */     System.arraycopy(other.bytes, other.offset, this.bytes, this.length + this.offset, other.length);
/* 245 */     this.length = newLen;
/*     */   }
/*     */ 
/*     */   public void grow(int newLength) {
/* 249 */     this.bytes = ArrayUtil.grow(this.bytes, newLength);
/*     */   }
/*     */ 
/*     */   public int compareTo(BytesRef other)
/*     */   {
/* 254 */     if (this == other) return 0;
/*     */ 
/* 256 */     byte[] aBytes = this.bytes;
/* 257 */     int aUpto = this.offset;
/* 258 */     byte[] bBytes = other.bytes;
/* 259 */     int bUpto = other.offset;
/*     */ 
/* 261 */     int aStop = aUpto + Math.min(this.length, other.length);
/*     */ 
/* 263 */     while (aUpto < aStop) {
/* 264 */       int aByte = aBytes[(aUpto++)] & 0xFF;
/* 265 */       int bByte = bBytes[(bUpto++)] & 0xFF;
/* 266 */       int diff = aByte - bByte;
/* 267 */       if (diff != 0) return diff;
/*     */ 
/*     */     }
/*     */ 
/* 271 */     return this.length - other.length;
/*     */   }
/*     */ 
/*     */   public static Comparator<BytesRef> getUTF8SortedAsUnicodeComparator()
/*     */   {
/* 277 */     return utf8SortedAsUnicodeSortOrder;
/*     */   }
/*     */ 
/*     */   public static Comparator<BytesRef> getUTF8SortedAsUTF16Comparator()
/*     */   {
/* 315 */     return utf8SortedAsUTF16SortOrder;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  30 */     EMPTY_BYTES = new byte[0];
/*     */ 
/* 274 */     utf8SortedAsUnicodeSortOrder = new UTF8SortedAsUnicodeComparator(null);
/*     */ 
/* 312 */     utf8SortedAsUTF16SortOrder = new UTF8SortedAsUTF16Comparator(null);
/*     */   }
/*     */ 
/*     */   private static class UTF8SortedAsUTF16Comparator
/*     */     implements Comparator<BytesRef>
/*     */   {
/*     */     public int compare(BytesRef a, BytesRef b)
/*     */     {
/* 324 */       byte[] aBytes = a.bytes;
/* 325 */       int aUpto = a.offset;
/* 326 */       byte[] bBytes = b.bytes;
/* 327 */       int bUpto = b.offset;
/*     */       int aStop;
/*     */       int aStop;
/* 330 */       if (a.length < b.length)
/* 331 */         aStop = aUpto + a.length;
/*     */       else {
/* 333 */         aStop = aUpto + b.length;
/*     */       }
/*     */ 
/* 336 */       while (aUpto < aStop) {
/* 337 */         int aByte = aBytes[(aUpto++)] & 0xFF;
/* 338 */         int bByte = bBytes[(bUpto++)] & 0xFF;
/*     */ 
/* 340 */         if (aByte != bByte)
/*     */         {
/* 352 */           if ((aByte >= 238) && (bByte >= 238)) {
/* 353 */             if ((aByte & 0xFE) == 238) {
/* 354 */               aByte += 14;
/*     */             }
/* 356 */             if ((bByte & 0xFE) == 238) {
/* 357 */               bByte += 14;
/*     */             }
/*     */           }
/* 360 */           return aByte - bByte;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 365 */       return a.length - b.length;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class UTF8SortedAsUnicodeComparator
/*     */     implements Comparator<BytesRef>
/*     */   {
/*     */     public int compare(BytesRef a, BytesRef b)
/*     */     {
/* 285 */       byte[] aBytes = a.bytes;
/* 286 */       int aUpto = a.offset;
/* 287 */       byte[] bBytes = b.bytes;
/* 288 */       int bUpto = b.offset;
/*     */       int aStop;
/*     */       int aStop;
/* 291 */       if (a.length < b.length)
/* 292 */         aStop = aUpto + a.length;
/*     */       else {
/* 294 */         aStop = aUpto + b.length;
/*     */       }
/*     */ 
/* 297 */       while (aUpto < aStop) {
/* 298 */         int aByte = aBytes[(aUpto++)] & 0xFF;
/* 299 */         int bByte = bBytes[(bUpto++)] & 0xFF;
/*     */ 
/* 301 */         int diff = aByte - bByte;
/* 302 */         if (diff != 0) {
/* 303 */           return diff;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 308 */       return a.length - b.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.BytesRef
 * JD-Core Version:    0.6.0
 */