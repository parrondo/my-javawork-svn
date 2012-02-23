/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.search.DocIdSet;
/*     */ import org.apache.lucene.search.DocIdSetIterator;
/*     */ 
/*     */ public final class FixedBitSet extends DocIdSet
/*     */ {
/*     */   private final long[] bits;
/*     */   private int numBits;
/*     */ 
/*     */   public static int bits2words(int numBits)
/*     */   {
/*  45 */     int numLong = numBits >>> 6;
/*  46 */     if ((numBits & 0x3F) != 0) {
/*  47 */       numLong++;
/*     */     }
/*  49 */     return numLong;
/*     */   }
/*     */ 
/*     */   public FixedBitSet(int numBits) {
/*  53 */     this.numBits = numBits;
/*  54 */     this.bits = new long[bits2words(numBits)];
/*     */   }
/*     */ 
/*     */   public FixedBitSet(FixedBitSet other)
/*     */   {
/*  59 */     this.bits = new long[other.bits.length];
/*  60 */     System.arraycopy(other.bits, 0, this.bits, 0, this.bits.length);
/*  61 */     this.numBits = other.numBits;
/*     */   }
/*     */ 
/*     */   public DocIdSetIterator iterator()
/*     */   {
/*  66 */     return new OpenBitSetIterator(this.bits, this.bits.length);
/*     */   }
/*     */ 
/*     */   public int length() {
/*  70 */     return this.numBits;
/*     */   }
/*     */ 
/*     */   public boolean isCacheable()
/*     */   {
/*  76 */     return true;
/*     */   }
/*     */ 
/*     */   public long[] getBits()
/*     */   {
/*  81 */     return this.bits;
/*     */   }
/*     */ 
/*     */   public int cardinality()
/*     */   {
/*  88 */     return (int)BitUtil.pop_array(this.bits, 0, this.bits.length);
/*     */   }
/*     */ 
/*     */   public boolean get(int index) {
/*  92 */     assert ((index >= 0) && (index < this.numBits));
/*  93 */     int i = index >> 6;
/*     */ 
/*  96 */     int bit = index & 0x3F;
/*  97 */     long bitmask = 1L << bit;
/*  98 */     return (this.bits[i] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public void set(int index) {
/* 102 */     assert ((index >= 0) && (index < this.numBits));
/* 103 */     int wordNum = index >> 6;
/* 104 */     int bit = index & 0x3F;
/* 105 */     long bitmask = 1L << bit;
/* 106 */     this.bits[wordNum] |= bitmask;
/*     */   }
/*     */ 
/*     */   public boolean getAndSet(int index) {
/* 110 */     assert ((index >= 0) && (index < this.numBits));
/* 111 */     int wordNum = index >> 6;
/* 112 */     int bit = index & 0x3F;
/* 113 */     long bitmask = 1L << bit;
/* 114 */     boolean val = (this.bits[wordNum] & bitmask) != 0L;
/* 115 */     this.bits[wordNum] |= bitmask;
/* 116 */     return val;
/*     */   }
/*     */ 
/*     */   public void clear(int index) {
/* 120 */     assert ((index >= 0) && (index < this.numBits));
/* 121 */     int wordNum = index >> 6;
/* 122 */     int bit = index & 0x3F;
/* 123 */     long bitmask = 1L << bit;
/* 124 */     this.bits[wordNum] &= (bitmask ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public boolean getAndClear(int index) {
/* 128 */     assert ((index >= 0) && (index < this.numBits));
/* 129 */     int wordNum = index >> 6;
/* 130 */     int bit = index & 0x3F;
/* 131 */     long bitmask = 1L << bit;
/* 132 */     boolean val = (this.bits[wordNum] & bitmask) != 0L;
/* 133 */     this.bits[wordNum] &= (bitmask ^ 0xFFFFFFFF);
/* 134 */     return val;
/*     */   }
/*     */ 
/*     */   public int nextSetBit(int index)
/*     */   {
/* 141 */     assert ((index >= 0) && (index < this.numBits));
/* 142 */     int i = index >> 6;
/* 143 */     int subIndex = index & 0x3F;
/* 144 */     long word = this.bits[i] >> subIndex;
/*     */ 
/* 146 */     if (word != 0L)
/* 147 */       return (i << 6) + subIndex + BitUtil.ntz(word);
/*     */     while (true)
/*     */     {
/* 150 */       i++; if (i >= this.bits.length) break;
/* 151 */       word = this.bits[i];
/* 152 */       if (word != 0L) {
/* 153 */         return (i << 6) + BitUtil.ntz(word);
/*     */       }
/*     */     }
/*     */ 
/* 157 */     return -1;
/*     */   }
/*     */ 
/*     */   public int prevSetBit(int index) {
/* 161 */     assert ((index >= 0) && (index < this.numBits)) : ("index=" + index + " numBits=" + this.numBits);
/* 162 */     int i = index >> 6;
/*     */ 
/* 165 */     int subIndex = index & 0x3F;
/* 166 */     long word = this.bits[i] << 63 - subIndex;
/*     */ 
/* 168 */     if (word != 0L)
/* 169 */       return (i << 6) + subIndex - Long.numberOfLeadingZeros(word);
/*     */     while (true)
/*     */     {
/* 172 */       i--; if (i < 0) break;
/* 173 */       word = this.bits[i];
/* 174 */       if (word != 0L) {
/* 175 */         return (i << 6) + 63 - Long.numberOfLeadingZeros(word);
/*     */       }
/*     */     }
/*     */ 
/* 179 */     return -1;
/*     */   }
/*     */ 
/*     */   public void or(DocIdSetIterator iter)
/*     */     throws IOException
/*     */   {
/*     */     int doc;
/* 186 */     while ((doc = iter.nextDoc()) != 2147483647)
/* 187 */       set(doc);
/*     */   }
/*     */ 
/*     */   public void or(FixedBitSet other)
/*     */   {
/* 192 */     long[] thisArr = this.bits;
/* 193 */     long[] otherArr = other.bits;
/* 194 */     int pos = Math.min(thisArr.length, otherArr.length);
/*     */     while (true) { pos--; if (pos < 0) break;
/* 196 */       thisArr[pos] |= otherArr[pos];
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flip(int startIndex, int endIndex)
/*     */   {
/* 210 */     assert ((startIndex >= 0) && (startIndex < this.numBits));
/* 211 */     assert ((endIndex >= 0) && (endIndex <= this.numBits));
/* 212 */     if (endIndex <= startIndex) {
/* 213 */       return;
/*     */     }
/*     */ 
/* 216 */     int startWord = startIndex >> 6;
/* 217 */     int endWord = endIndex - 1 >> 6;
/*     */ 
/* 226 */     long startmask = -1L << startIndex;
/* 227 */     long endmask = -1L >>> -endIndex;
/*     */ 
/* 229 */     if (startWord == endWord) {
/* 230 */       this.bits[startWord] ^= startmask & endmask;
/* 231 */       return;
/*     */     }
/*     */ 
/* 234 */     this.bits[startWord] ^= startmask;
/*     */ 
/* 236 */     for (int i = startWord + 1; i < endWord; i++) {
/* 237 */       this.bits[i] ^= -1L;
/*     */     }
/*     */ 
/* 240 */     this.bits[endWord] ^= endmask;
/*     */   }
/*     */ 
/*     */   public void set(int startIndex, int endIndex)
/*     */   {
/* 249 */     assert ((startIndex >= 0) && (startIndex < this.numBits));
/* 250 */     assert ((endIndex >= 0) && (endIndex <= this.numBits));
/* 251 */     if (endIndex <= startIndex) {
/* 252 */       return;
/*     */     }
/*     */ 
/* 255 */     int startWord = startIndex >> 6;
/* 256 */     int endWord = endIndex - 1 >> 6;
/*     */ 
/* 258 */     long startmask = -1L << startIndex;
/* 259 */     long endmask = -1L >>> -endIndex;
/*     */ 
/* 261 */     if (startWord == endWord) {
/* 262 */       this.bits[startWord] |= startmask & endmask;
/* 263 */       return;
/*     */     }
/*     */ 
/* 266 */     this.bits[startWord] |= startmask;
/* 267 */     Arrays.fill(this.bits, startWord + 1, endWord, -1L);
/* 268 */     this.bits[endWord] |= endmask;
/*     */   }
/*     */ 
/*     */   public void clear(int startIndex, int endIndex)
/*     */   {
/* 277 */     assert ((startIndex >= 0) && (startIndex < this.numBits));
/* 278 */     assert ((endIndex >= 0) && (endIndex <= this.numBits));
/* 279 */     if (endIndex <= startIndex) {
/* 280 */       return;
/*     */     }
/*     */ 
/* 283 */     int startWord = startIndex >> 6;
/* 284 */     int endWord = endIndex - 1 >> 6;
/*     */ 
/* 286 */     long startmask = -1L << startIndex;
/* 287 */     long endmask = -1L >>> -endIndex;
/*     */ 
/* 290 */     startmask ^= -1L;
/* 291 */     endmask ^= -1L;
/*     */ 
/* 293 */     if (startWord == endWord) {
/* 294 */       this.bits[startWord] &= (startmask | endmask);
/* 295 */       return;
/*     */     }
/*     */ 
/* 298 */     this.bits[startWord] &= startmask;
/* 299 */     Arrays.fill(this.bits, startWord + 1, endWord, 0L);
/* 300 */     this.bits[endWord] &= endmask;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 305 */     return new FixedBitSet(this);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 311 */     if (this == o) {
/* 312 */       return true;
/*     */     }
/* 314 */     if (!(o instanceof FixedBitSet)) {
/* 315 */       return false;
/*     */     }
/* 317 */     FixedBitSet other = (FixedBitSet)o;
/* 318 */     if (this.numBits != other.length()) {
/* 319 */       return false;
/*     */     }
/* 321 */     return Arrays.equals(this.bits, other.bits);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 326 */     long h = 0L;
/* 327 */     int i = this.bits.length;
/*     */     while (true) { i--; if (i < 0) break;
/* 328 */       h ^= this.bits[i];
/* 329 */       h = h << 1 | h >>> 63;
/*     */     }
/*     */ 
/* 333 */     return (int)(h >> 32 ^ h) + -1737092556;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.FixedBitSet
 * JD-Core Version:    0.6.0
 */