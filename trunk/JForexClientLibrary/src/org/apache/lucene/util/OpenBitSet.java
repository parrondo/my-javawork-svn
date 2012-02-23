/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import J;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.search.DocIdSet;
/*     */ import org.apache.lucene.search.DocIdSetIterator;
/*     */ 
/*     */ public class OpenBitSet extends DocIdSet
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   protected long[] bits;
/*     */   protected int wlen;
/*     */   private long numBits;
/*     */ 
/*     */   public OpenBitSet(long numBits)
/*     */   {
/*  90 */     this.numBits = numBits;
/*  91 */     this.bits = new long[bits2words(numBits)];
/*  92 */     this.wlen = this.bits.length;
/*     */   }
/*     */ 
/*     */   public OpenBitSet() {
/*  96 */     this(64L);
/*     */   }
/*     */ 
/*     */   public OpenBitSet(long[] bits, int numWords)
/*     */   {
/* 113 */     this.bits = bits;
/* 114 */     this.wlen = numWords;
/* 115 */     this.numBits = (this.wlen * 64);
/*     */   }
/*     */ 
/*     */   public DocIdSetIterator iterator()
/*     */   {
/* 120 */     return new OpenBitSetIterator(this.bits, this.wlen);
/*     */   }
/*     */ 
/*     */   public boolean isCacheable()
/*     */   {
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   public long capacity() {
/* 130 */     return this.bits.length << 6;
/*     */   }
/*     */ 
/*     */   public long size()
/*     */   {
/* 137 */     return capacity();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 141 */     return cardinality() == 0L;
/*     */   }
/*     */   public long[] getBits() {
/* 144 */     return this.bits;
/*     */   }
/*     */   public void setBits(long[] bits) {
/* 147 */     this.bits = bits;
/*     */   }
/*     */   public int getNumWords() {
/* 150 */     return this.wlen;
/*     */   }
/*     */   public void setNumWords(int nWords) {
/* 153 */     this.wlen = nWords;
/*     */   }
/*     */ 
/*     */   public boolean get(int index)
/*     */   {
/* 159 */     int i = index >> 6;
/*     */ 
/* 162 */     if (i >= this.bits.length) return false;
/*     */ 
/* 164 */     int bit = index & 0x3F;
/* 165 */     long bitmask = 1L << bit;
/* 166 */     return (this.bits[i] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public boolean fastGet(int index)
/*     */   {
/* 174 */     assert ((index >= 0) && (index < this.numBits));
/* 175 */     int i = index >> 6;
/*     */ 
/* 178 */     int bit = index & 0x3F;
/* 179 */     long bitmask = 1L << bit;
/* 180 */     return (this.bits[i] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public boolean get(long index)
/*     */   {
/* 188 */     int i = (int)(index >> 6);
/* 189 */     if (i >= this.bits.length) return false;
/* 190 */     int bit = (int)index & 0x3F;
/* 191 */     long bitmask = 1L << bit;
/* 192 */     return (this.bits[i] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public boolean fastGet(long index)
/*     */   {
/* 199 */     assert ((index >= 0L) && (index < this.numBits));
/* 200 */     int i = (int)(index >> 6);
/* 201 */     int bit = (int)index & 0x3F;
/* 202 */     long bitmask = 1L << bit;
/* 203 */     return (this.bits[i] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public int getBit(int index)
/*     */   {
/* 223 */     assert ((index >= 0) && (index < this.numBits));
/* 224 */     int i = index >> 6;
/* 225 */     int bit = index & 0x3F;
/* 226 */     return (int)(this.bits[i] >>> bit) & 0x1;
/*     */   }
/*     */ 
/*     */   public void set(long index)
/*     */   {
/* 241 */     int wordNum = expandingWordNum(index);
/* 242 */     int bit = (int)index & 0x3F;
/* 243 */     long bitmask = 1L << bit;
/* 244 */     this.bits[wordNum] |= bitmask;
/*     */   }
/*     */ 
/*     */   public void fastSet(int index)
/*     */   {
/* 252 */     assert ((index >= 0) && (index < this.numBits));
/* 253 */     int wordNum = index >> 6;
/* 254 */     int bit = index & 0x3F;
/* 255 */     long bitmask = 1L << bit;
/* 256 */     this.bits[wordNum] |= bitmask;
/*     */   }
/*     */ 
/*     */   public void fastSet(long index)
/*     */   {
/* 263 */     assert ((index >= 0L) && (index < this.numBits));
/* 264 */     int wordNum = (int)(index >> 6);
/* 265 */     int bit = (int)index & 0x3F;
/* 266 */     long bitmask = 1L << bit;
/* 267 */     this.bits[wordNum] |= bitmask;
/*     */   }
/*     */ 
/*     */   public void set(long startIndex, long endIndex)
/*     */   {
/* 276 */     if (endIndex <= startIndex) return;
/*     */ 
/* 278 */     int startWord = (int)(startIndex >> 6);
/*     */ 
/* 282 */     int endWord = expandingWordNum(endIndex - 1L);
/*     */ 
/* 284 */     long startmask = -1L << (int)startIndex;
/* 285 */     long endmask = -1L >>> (int)(-endIndex);
/*     */ 
/* 287 */     if (startWord == endWord) {
/* 288 */       this.bits[startWord] |= startmask & endmask;
/* 289 */       return;
/*     */     }
/*     */ 
/* 292 */     this.bits[startWord] |= startmask;
/* 293 */     Arrays.fill(this.bits, startWord + 1, endWord, -1L);
/* 294 */     this.bits[endWord] |= endmask;
/*     */   }
/*     */ 
/*     */   protected int expandingWordNum(long index)
/*     */   {
/* 300 */     int wordNum = (int)(index >> 6);
/* 301 */     if (wordNum >= this.wlen) {
/* 302 */       ensureCapacity(index + 1L);
/* 303 */       this.wlen = (wordNum + 1);
/*     */     }
/* 305 */     assert ((this.numBits = Math.max(this.numBits, index + 1L)) >= 0L);
/* 306 */     return wordNum;
/*     */   }
/*     */ 
/*     */   public void fastClear(int index)
/*     */   {
/* 314 */     assert ((index >= 0) && (index < this.numBits));
/* 315 */     int wordNum = index >> 6;
/* 316 */     int bit = index & 0x3F;
/* 317 */     long bitmask = 1L << bit;
/* 318 */     this.bits[wordNum] &= (bitmask ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public void fastClear(long index)
/*     */   {
/* 332 */     assert ((index >= 0L) && (index < this.numBits));
/* 333 */     int wordNum = (int)(index >> 6);
/* 334 */     int bit = (int)index & 0x3F;
/* 335 */     long bitmask = 1L << bit;
/* 336 */     this.bits[wordNum] &= (bitmask ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public void clear(long index)
/*     */   {
/* 341 */     int wordNum = (int)(index >> 6);
/* 342 */     if (wordNum >= this.wlen) return;
/* 343 */     int bit = (int)index & 0x3F;
/* 344 */     long bitmask = 1L << bit;
/* 345 */     this.bits[wordNum] &= (bitmask ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public void clear(int startIndex, int endIndex)
/*     */   {
/* 354 */     if (endIndex <= startIndex) return;
/*     */ 
/* 356 */     int startWord = startIndex >> 6;
/* 357 */     if (startWord >= this.wlen) return;
/*     */ 
/* 361 */     int endWord = endIndex - 1 >> 6;
/*     */ 
/* 363 */     long startmask = -1L << startIndex;
/* 364 */     long endmask = -1L >>> -endIndex;
/*     */ 
/* 367 */     startmask ^= -1L;
/* 368 */     endmask ^= -1L;
/*     */ 
/* 370 */     if (startWord == endWord) {
/* 371 */       this.bits[startWord] &= (startmask | endmask);
/* 372 */       return;
/*     */     }
/*     */ 
/* 375 */     this.bits[startWord] &= startmask;
/*     */ 
/* 377 */     int middle = Math.min(this.wlen, endWord);
/* 378 */     Arrays.fill(this.bits, startWord + 1, middle, 0L);
/* 379 */     if (endWord < this.wlen)
/* 380 */       this.bits[endWord] &= endmask;
/*     */   }
/*     */ 
/*     */   public void clear(long startIndex, long endIndex)
/*     */   {
/* 391 */     if (endIndex <= startIndex) return;
/*     */ 
/* 393 */     int startWord = (int)(startIndex >> 6);
/* 394 */     if (startWord >= this.wlen) return;
/*     */ 
/* 398 */     int endWord = (int)(endIndex - 1L >> 6);
/*     */ 
/* 400 */     long startmask = -1L << (int)startIndex;
/* 401 */     long endmask = -1L >>> (int)(-endIndex);
/*     */ 
/* 404 */     startmask ^= -1L;
/* 405 */     endmask ^= -1L;
/*     */ 
/* 407 */     if (startWord == endWord) {
/* 408 */       this.bits[startWord] &= (startmask | endmask);
/* 409 */       return;
/*     */     }
/*     */ 
/* 412 */     this.bits[startWord] &= startmask;
/*     */ 
/* 414 */     int middle = Math.min(this.wlen, endWord);
/* 415 */     Arrays.fill(this.bits, startWord + 1, middle, 0L);
/* 416 */     if (endWord < this.wlen)
/* 417 */       this.bits[endWord] &= endmask;
/*     */   }
/*     */ 
/*     */   public boolean getAndSet(int index)
/*     */   {
/* 427 */     assert ((index >= 0) && (index < this.numBits));
/* 428 */     int wordNum = index >> 6;
/* 429 */     int bit = index & 0x3F;
/* 430 */     long bitmask = 1L << bit;
/* 431 */     boolean val = (this.bits[wordNum] & bitmask) != 0L;
/* 432 */     this.bits[wordNum] |= bitmask;
/* 433 */     return val;
/*     */   }
/*     */ 
/*     */   public boolean getAndSet(long index)
/*     */   {
/* 440 */     assert ((index >= 0L) && (index < this.numBits));
/* 441 */     int wordNum = (int)(index >> 6);
/* 442 */     int bit = (int)index & 0x3F;
/* 443 */     long bitmask = 1L << bit;
/* 444 */     boolean val = (this.bits[wordNum] & bitmask) != 0L;
/* 445 */     this.bits[wordNum] |= bitmask;
/* 446 */     return val;
/*     */   }
/*     */ 
/*     */   public void fastFlip(int index)
/*     */   {
/* 453 */     assert ((index >= 0) && (index < this.numBits));
/* 454 */     int wordNum = index >> 6;
/* 455 */     int bit = index & 0x3F;
/* 456 */     long bitmask = 1L << bit;
/* 457 */     this.bits[wordNum] ^= bitmask;
/*     */   }
/*     */ 
/*     */   public void fastFlip(long index)
/*     */   {
/* 464 */     assert ((index >= 0L) && (index < this.numBits));
/* 465 */     int wordNum = (int)(index >> 6);
/* 466 */     int bit = (int)index & 0x3F;
/* 467 */     long bitmask = 1L << bit;
/* 468 */     this.bits[wordNum] ^= bitmask;
/*     */   }
/*     */ 
/*     */   public void flip(long index)
/*     */   {
/* 473 */     int wordNum = expandingWordNum(index);
/* 474 */     int bit = (int)index & 0x3F;
/* 475 */     long bitmask = 1L << bit;
/* 476 */     this.bits[wordNum] ^= bitmask;
/*     */   }
/*     */ 
/*     */   public boolean flipAndGet(int index)
/*     */   {
/* 483 */     assert ((index >= 0) && (index < this.numBits));
/* 484 */     int wordNum = index >> 6;
/* 485 */     int bit = index & 0x3F;
/* 486 */     long bitmask = 1L << bit;
/* 487 */     this.bits[wordNum] ^= bitmask;
/* 488 */     return (this.bits[wordNum] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public boolean flipAndGet(long index)
/*     */   {
/* 495 */     assert ((index >= 0L) && (index < this.numBits));
/* 496 */     int wordNum = (int)(index >> 6);
/* 497 */     int bit = (int)index & 0x3F;
/* 498 */     long bitmask = 1L << bit;
/* 499 */     this.bits[wordNum] ^= bitmask;
/* 500 */     return (this.bits[wordNum] & bitmask) != 0L;
/*     */   }
/*     */ 
/*     */   public void flip(long startIndex, long endIndex)
/*     */   {
/* 509 */     if (endIndex <= startIndex) return;
/* 510 */     int startWord = (int)(startIndex >> 6);
/*     */ 
/* 514 */     int endWord = expandingWordNum(endIndex - 1L);
/*     */ 
/* 523 */     long startmask = -1L << (int)startIndex;
/* 524 */     long endmask = -1L >>> (int)(-endIndex);
/*     */ 
/* 526 */     if (startWord == endWord) {
/* 527 */       this.bits[startWord] ^= startmask & endmask;
/* 528 */       return;
/*     */     }
/*     */ 
/* 531 */     this.bits[startWord] ^= startmask;
/*     */ 
/* 533 */     for (int i = startWord + 1; i < endWord; i++) {
/* 534 */       this.bits[i] ^= -1L;
/*     */     }
/*     */ 
/* 537 */     this.bits[endWord] ^= endmask;
/*     */   }
/*     */ 
/*     */   public long cardinality()
/*     */   {
/* 566 */     return BitUtil.pop_array(this.bits, 0, this.wlen);
/*     */   }
/*     */ 
/*     */   public static long intersectionCount(OpenBitSet a, OpenBitSet b)
/*     */   {
/* 573 */     return BitUtil.pop_intersect(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
/*     */   }
/*     */ 
/*     */   public static long unionCount(OpenBitSet a, OpenBitSet b)
/*     */   {
/* 580 */     long tot = BitUtil.pop_union(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
/* 581 */     if (a.wlen < b.wlen)
/* 582 */       tot += BitUtil.pop_array(b.bits, a.wlen, b.wlen - a.wlen);
/* 583 */     else if (a.wlen > b.wlen) {
/* 584 */       tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
/*     */     }
/* 586 */     return tot;
/*     */   }
/*     */ 
/*     */   public static long andNotCount(OpenBitSet a, OpenBitSet b)
/*     */   {
/* 594 */     long tot = BitUtil.pop_andnot(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
/* 595 */     if (a.wlen > b.wlen) {
/* 596 */       tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
/*     */     }
/* 598 */     return tot;
/*     */   }
/*     */ 
/*     */   public static long xorCount(OpenBitSet a, OpenBitSet b)
/*     */   {
/* 605 */     long tot = BitUtil.pop_xor(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
/* 606 */     if (a.wlen < b.wlen)
/* 607 */       tot += BitUtil.pop_array(b.bits, a.wlen, b.wlen - a.wlen);
/* 608 */     else if (a.wlen > b.wlen) {
/* 609 */       tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
/*     */     }
/* 611 */     return tot;
/*     */   }
/*     */ 
/*     */   public int nextSetBit(int index)
/*     */   {
/* 619 */     int i = index >> 6;
/* 620 */     if (i >= this.wlen) return -1;
/* 621 */     int subIndex = index & 0x3F;
/* 622 */     long word = this.bits[i] >> subIndex;
/*     */ 
/* 624 */     if (word != 0L)
/* 625 */       return (i << 6) + subIndex + BitUtil.ntz(word);
/*     */     while (true)
/*     */     {
/* 628 */       i++; if (i >= this.wlen) break;
/* 629 */       word = this.bits[i];
/* 630 */       if (word != 0L) return (i << 6) + BitUtil.ntz(word);
/*     */     }
/*     */ 
/* 633 */     return -1;
/*     */   }
/*     */ 
/*     */   public long nextSetBit(long index)
/*     */   {
/* 640 */     int i = (int)(index >>> 6);
/* 641 */     if (i >= this.wlen) return -1L;
/* 642 */     int subIndex = (int)index & 0x3F;
/* 643 */     long word = this.bits[i] >>> subIndex;
/*     */ 
/* 645 */     if (word != 0L)
/* 646 */       return (i << 6) + (subIndex + BitUtil.ntz(word));
/*     */     while (true)
/*     */     {
/* 649 */       i++; if (i >= this.wlen) break;
/* 650 */       word = this.bits[i];
/* 651 */       if (word != 0L) return (i << 6) + BitUtil.ntz(word);
/*     */     }
/*     */ 
/* 654 */     return -1L;
/*     */   }
/*     */ 
/*     */   public int prevSetBit(int index)
/*     */   {
/* 663 */     int i = index >> 6;
/*     */     long word;
/*     */     int subIndex;
/*     */     long word;
/* 666 */     if (i >= this.wlen) {
/* 667 */       i = this.wlen - 1;
/* 668 */       if (i < 0) return -1;
/* 669 */       int subIndex = 63;
/* 670 */       word = this.bits[i];
/*     */     } else {
/* 672 */       if (i < 0) return -1;
/* 673 */       subIndex = index & 0x3F;
/* 674 */       word = this.bits[i] << 63 - subIndex;
/*     */     }
/*     */ 
/* 677 */     if (word != 0L)
/* 678 */       return (i << 6) + subIndex - Long.numberOfLeadingZeros(word);
/*     */     while (true)
/*     */     {
/* 681 */       i--; if (i < 0) break;
/* 682 */       word = this.bits[i];
/* 683 */       if (word != 0L) {
/* 684 */         return (i << 6) + 63 - Long.numberOfLeadingZeros(word);
/*     */       }
/*     */     }
/*     */ 
/* 688 */     return -1;
/*     */   }
/*     */ 
/*     */   public long prevSetBit(long index)
/*     */   {
/* 696 */     int i = (int)(index >> 6);
/*     */     long word;
/*     */     int subIndex;
/*     */     long word;
/* 699 */     if (i >= this.wlen) {
/* 700 */       i = this.wlen - 1;
/* 701 */       if (i < 0) return -1L;
/* 702 */       int subIndex = 63;
/* 703 */       word = this.bits[i];
/*     */     } else {
/* 705 */       if (i < 0) return -1L;
/* 706 */       subIndex = (int)index & 0x3F;
/* 707 */       word = this.bits[i] << 63 - subIndex;
/*     */     }
/*     */ 
/* 710 */     if (word != 0L)
/* 711 */       return (i << 6) + subIndex - Long.numberOfLeadingZeros(word);
/*     */     while (true)
/*     */     {
/* 714 */       i--; if (i < 0) break;
/* 715 */       word = this.bits[i];
/* 716 */       if (word != 0L) {
/* 717 */         return (i << 6) + 63L - Long.numberOfLeadingZeros(word);
/*     */       }
/*     */     }
/*     */ 
/* 721 */     return -1L;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try {
/* 727 */       OpenBitSet obs = (OpenBitSet)super.clone();
/* 728 */       obs.bits = ((long[])obs.bits.clone());
/* 729 */       return obs; } catch (CloneNotSupportedException e) {
/*     */     }
/* 731 */     throw new RuntimeException(e);
/*     */   }
/*     */ 
/*     */   public void intersect(OpenBitSet other)
/*     */   {
/* 737 */     int newLen = Math.min(this.wlen, other.wlen);
/* 738 */     long[] thisArr = this.bits;
/* 739 */     long[] otherArr = other.bits;
/*     */ 
/* 741 */     int pos = newLen;
/*     */     while (true) { pos--; if (pos < 0) break;
/* 743 */       thisArr[pos] &= otherArr[pos];
/*     */     }
/* 745 */     if (this.wlen > newLen)
/*     */     {
/* 747 */       Arrays.fill(this.bits, newLen, this.wlen, 0L);
/*     */     }
/* 749 */     this.wlen = newLen;
/*     */   }
/*     */ 
/*     */   public void union(OpenBitSet other)
/*     */   {
/* 754 */     int newLen = Math.max(this.wlen, other.wlen);
/* 755 */     ensureCapacityWords(newLen);
/* 756 */     assert ((this.numBits = Math.max(other.numBits, this.numBits)) >= 0L);
/*     */ 
/* 758 */     long[] thisArr = this.bits;
/* 759 */     long[] otherArr = other.bits;
/* 760 */     int pos = Math.min(this.wlen, other.wlen);
/*     */     while (true) { pos--; if (pos < 0) break;
/* 762 */       thisArr[pos] |= otherArr[pos];
/*     */     }
/* 764 */     if (this.wlen < newLen) {
/* 765 */       System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen - this.wlen);
/*     */     }
/* 767 */     this.wlen = newLen;
/*     */   }
/*     */ 
/*     */   public void remove(OpenBitSet other)
/*     */   {
/* 773 */     int idx = Math.min(this.wlen, other.wlen);
/* 774 */     long[] thisArr = this.bits;
/* 775 */     long[] otherArr = other.bits;
/*     */     while (true) { idx--; if (idx < 0) break;
/* 777 */       thisArr[idx] &= (otherArr[idx] ^ 0xFFFFFFFF);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void xor(OpenBitSet other)
/*     */   {
/* 783 */     int newLen = Math.max(this.wlen, other.wlen);
/* 784 */     ensureCapacityWords(newLen);
/* 785 */     assert ((this.numBits = Math.max(other.numBits, this.numBits)) >= 0L);
/*     */ 
/* 787 */     long[] thisArr = this.bits;
/* 788 */     long[] otherArr = other.bits;
/* 789 */     int pos = Math.min(this.wlen, other.wlen);
/*     */     while (true) { pos--; if (pos < 0) break;
/* 791 */       thisArr[pos] ^= otherArr[pos];
/*     */     }
/* 793 */     if (this.wlen < newLen) {
/* 794 */       System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen - this.wlen);
/*     */     }
/* 796 */     this.wlen = newLen;
/*     */   }
/*     */ 
/*     */   public void and(OpenBitSet other)
/*     */   {
/* 804 */     intersect(other);
/*     */   }
/*     */ 
/*     */   public void or(OpenBitSet other)
/*     */   {
/* 809 */     union(other);
/*     */   }
/*     */ 
/*     */   public void andNot(OpenBitSet other)
/*     */   {
/* 814 */     remove(other);
/*     */   }
/*     */ 
/*     */   public boolean intersects(OpenBitSet other)
/*     */   {
/* 819 */     int pos = Math.min(this.wlen, other.wlen);
/* 820 */     long[] thisArr = this.bits;
/* 821 */     long[] otherArr = other.bits;
/*     */     while (true) { pos--; if (pos < 0) break;
/* 823 */       if ((thisArr[pos] & otherArr[pos]) != 0L) return true;
/*     */     }
/* 825 */     return false;
/*     */   }
/*     */ 
/*     */   public void ensureCapacityWords(int numWords)
/*     */   {
/* 834 */     if (this.bits.length < numWords)
/* 835 */       this.bits = ArrayUtil.grow(this.bits, numWords);
/*     */   }
/*     */ 
/*     */   public void ensureCapacity(long numBits)
/*     */   {
/* 843 */     ensureCapacityWords(bits2words(numBits));
/*     */   }
/*     */ 
/*     */   public void trimTrailingZeros()
/*     */   {
/* 850 */     int idx = this.wlen - 1;
/* 851 */     while ((idx >= 0) && (this.bits[idx] == 0L)) idx--;
/* 852 */     this.wlen = (idx + 1);
/*     */   }
/*     */ 
/*     */   public static int bits2words(long numBits)
/*     */   {
/* 857 */     return (int)((numBits - 1L >>> 6) + 1L);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 864 */     if (this == o) return true;
/* 865 */     if (!(o instanceof OpenBitSet)) return false;
/*     */ 
/* 867 */     OpenBitSet b = (OpenBitSet)o;
/*     */     OpenBitSet a;
/* 869 */     if (b.wlen > this.wlen) {
/* 870 */       OpenBitSet a = b; b = this;
/*     */     } else {
/* 872 */       a = this;
/*     */     }
/*     */ 
/* 876 */     for (int i = a.wlen - 1; i >= b.wlen; i--) {
/* 877 */       if (a.bits[i] != 0L) return false;
/*     */     }
/*     */ 
/* 880 */     for (int i = b.wlen - 1; i >= 0; i--) {
/* 881 */       if (a.bits[i] != b.bits[i]) return false;
/*     */     }
/*     */ 
/* 884 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 892 */     long h = 0L;
/* 893 */     int i = this.bits.length;
/*     */     while (true) { i--; if (i < 0) break;
/* 894 */       h ^= this.bits[i];
/* 895 */       h = h << 1 | h >>> 63;
/*     */     }
/*     */ 
/* 899 */     return (int)(h >> 32 ^ h) + -1737092556;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.OpenBitSet
 * JD-Core Version:    0.6.0
 */