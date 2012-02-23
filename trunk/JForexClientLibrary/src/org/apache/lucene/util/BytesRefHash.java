/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ public final class BytesRefHash
/*     */ {
/*     */   public static final int DEFAULT_CAPACITY = 16;
/*     */   final ByteBlockPool pool;
/*     */   int[] bytesStart;
/*  55 */   private final BytesRef scratch1 = new BytesRef();
/*     */   private int hashSize;
/*     */   private int hashHalfSize;
/*     */   private int hashMask;
/*     */   private int count;
/*  60 */   private int lastCount = -1;
/*     */   private int[] ords;
/*     */   private final BytesStartArray bytesStartArray;
/*     */   private AtomicLong bytesUsed;
/*     */ 
/*     */   public BytesRefHash()
/*     */   {
/*  70 */     this(new ByteBlockPool(new ByteBlockPool.DirectAllocator()));
/*     */   }
/*     */ 
/*     */   public BytesRefHash(ByteBlockPool pool)
/*     */   {
/*  77 */     this(pool, 16, new DirectBytesStartArray(16));
/*     */   }
/*     */ 
/*     */   public BytesRefHash(ByteBlockPool pool, int capacity, BytesStartArray bytesStartArray)
/*     */   {
/*  85 */     this.hashSize = capacity;
/*  86 */     this.hashHalfSize = (this.hashSize >> 1);
/*  87 */     this.hashMask = (this.hashSize - 1);
/*  88 */     this.pool = pool;
/*  89 */     this.ords = new int[this.hashSize];
/*  90 */     Arrays.fill(this.ords, -1);
/*  91 */     this.bytesStartArray = bytesStartArray;
/*  92 */     this.bytesStart = bytesStartArray.init();
/*  93 */     this.bytesUsed = (bytesStartArray.bytesUsed() == null ? new AtomicLong(0L) : bytesStartArray.bytesUsed());
/*  94 */     this.bytesUsed.addAndGet(this.hashSize * 4);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 103 */     return this.count;
/*     */   }
/*     */ 
/*     */   public BytesRef get(int ord, BytesRef ref)
/*     */   {
/* 119 */     assert (this.bytesStart != null) : "bytesStart is null - not initialized";
/* 120 */     assert (ord < this.bytesStart.length) : ("ord exceeds byteStart len: " + this.bytesStart.length);
/* 121 */     return this.pool.setBytesRef(ref, this.bytesStart[ord]);
/*     */   }
/*     */ 
/*     */   public int[] compact()
/*     */   {
/* 133 */     assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
/* 134 */     int upto = 0;
/* 135 */     for (int i = 0; i < this.hashSize; i++) {
/* 136 */       if (this.ords[i] != -1) {
/* 137 */         if (upto < i) {
/* 138 */           this.ords[upto] = this.ords[i];
/* 139 */           this.ords[i] = -1;
/*     */         }
/* 141 */         upto++;
/*     */       }
/*     */     }
/*     */ 
/* 145 */     assert (upto == this.count);
/* 146 */     this.lastCount = this.count;
/* 147 */     return this.ords;
/*     */   }
/*     */ 
/*     */   public int[] sort(Comparator<BytesRef> comp)
/*     */   {
/* 161 */     int[] compact = compact();
/* 162 */     new SorterTemplate(compact, comp)
/*     */     {
/* 193 */       private final BytesRef pivot = new BytesRef(); private final BytesRef scratch1 = new BytesRef(); private final BytesRef scratch2 = new BytesRef();
/*     */ 
/*     */       protected void swap(int i, int j)
/*     */       {
/* 165 */         int o = this.val$compact[i];
/* 166 */         this.val$compact[i] = this.val$compact[j];
/* 167 */         this.val$compact[j] = o;
/*     */       }
/*     */ 
/*     */       protected int compare(int i, int j)
/*     */       {
/* 172 */         int ord1 = this.val$compact[i]; int ord2 = this.val$compact[j];
/* 173 */         assert ((BytesRefHash.this.bytesStart.length > ord1) && (BytesRefHash.this.bytesStart.length > ord2));
/* 174 */         return this.val$comp.compare(BytesRefHash.this.pool.setBytesRef(this.scratch1, BytesRefHash.this.bytesStart[ord1]), BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[ord2]));
/*     */       }
/*     */ 
/*     */       protected void setPivot(int i)
/*     */       {
/* 180 */         int ord = this.val$compact[i];
/* 181 */         assert (BytesRefHash.this.bytesStart.length > ord);
/* 182 */         BytesRefHash.this.pool.setBytesRef(this.pivot, BytesRefHash.this.bytesStart[ord]);
/*     */       }
/*     */ 
/*     */       protected int comparePivot(int j)
/*     */       {
/* 187 */         int ord = this.val$compact[j];
/* 188 */         assert (BytesRefHash.this.bytesStart.length > ord);
/* 189 */         return this.val$comp.compare(this.pivot, BytesRefHash.this.pool.setBytesRef(this.scratch2, BytesRefHash.this.bytesStart[ord]));
/*     */       }
/*     */     }
/* 162 */     .quickSort(0, this.count - 1);
/*     */ 
/* 196 */     return compact;
/*     */   }
/*     */ 
/*     */   private boolean equals(int ord, BytesRef b) {
/* 200 */     return this.pool.setBytesRef(this.scratch1, this.bytesStart[ord]).bytesEquals(b);
/*     */   }
/*     */ 
/*     */   private boolean shrink(int targetSize)
/*     */   {
/* 206 */     int newSize = this.hashSize;
/* 207 */     while ((newSize >= 8) && (newSize / 4 > targetSize)) {
/* 208 */       newSize /= 2;
/*     */     }
/* 210 */     if (newSize != this.hashSize) {
/* 211 */       this.bytesUsed.addAndGet(4 * -(this.hashSize - newSize));
/*     */ 
/* 213 */       this.hashSize = newSize;
/* 214 */       this.ords = new int[this.hashSize];
/* 215 */       Arrays.fill(this.ords, -1);
/* 216 */       this.hashHalfSize = (newSize / 2);
/* 217 */       this.hashMask = (newSize - 1);
/* 218 */       return true;
/*     */     }
/* 220 */     return false;
/*     */   }
/*     */ 
/*     */   public void clear(boolean resetPool)
/*     */   {
/* 228 */     this.lastCount = this.count;
/* 229 */     this.count = 0;
/* 230 */     if (resetPool) {
/* 231 */       this.pool.dropBuffersAndReset();
/*     */     }
/* 233 */     this.bytesStart = this.bytesStartArray.clear();
/* 234 */     if ((this.lastCount != -1) && (shrink(this.lastCount)))
/*     */     {
/* 236 */       return;
/*     */     }
/* 238 */     Arrays.fill(this.ords, -1);
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 242 */     clear(true);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 249 */     clear(true);
/* 250 */     this.ords = null;
/* 251 */     this.bytesUsed.addAndGet(4 * -this.hashSize);
/*     */   }
/*     */ 
/*     */   public int add(BytesRef bytes)
/*     */   {
/* 270 */     return add(bytes, bytes.hashCode());
/*     */   }
/*     */ 
/*     */   public int add(BytesRef bytes, int code)
/*     */   {
/* 301 */     assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
/* 302 */     int length = bytes.length;
/*     */ 
/* 304 */     int hashPos = code & this.hashMask;
/* 305 */     int e = this.ords[hashPos];
/* 306 */     if ((e != -1) && (!equals(e, bytes)))
/*     */     {
/* 309 */       int inc = (code >> 8) + code | 0x1;
/*     */       do {
/* 311 */         code += inc;
/* 312 */         hashPos = code & this.hashMask;
/* 313 */         e = this.ords[hashPos];
/* 314 */       }while ((e != -1) && (!equals(e, bytes)));
/*     */     }
/*     */ 
/* 317 */     if (e == -1)
/*     */     {
/* 319 */       int len2 = 2 + bytes.length;
/* 320 */       if (len2 + this.pool.byteUpto > 32768) {
/* 321 */         if (len2 > 32768) {
/* 322 */           throw new MaxBytesLengthExceededException("bytes can be at most 32766 in length; got " + bytes.length);
/*     */         }
/*     */ 
/* 325 */         this.pool.nextBuffer();
/*     */       }
/* 327 */       byte[] buffer = this.pool.buffer;
/* 328 */       int bufferUpto = this.pool.byteUpto;
/* 329 */       if (this.count >= this.bytesStart.length) {
/* 330 */         this.bytesStart = this.bytesStartArray.grow();
/* 331 */         assert (this.count < this.bytesStart.length + 1) : ("count: " + this.count + " len: " + this.bytesStart.length);
/*     */       }
/*     */ 
/* 334 */       e = this.count++;
/*     */ 
/* 336 */       this.bytesStart[e] = (bufferUpto + this.pool.byteOffset);
/*     */ 
/* 342 */       if (length < 128)
/*     */       {
/* 344 */         buffer[bufferUpto] = (byte)length;
/* 345 */         this.pool.byteUpto += length + 1;
/* 346 */         assert (length >= 0) : ("Length must be positive: " + length);
/* 347 */         System.arraycopy(bytes.bytes, bytes.offset, buffer, bufferUpto + 1, length);
/*     */       }
/*     */       else
/*     */       {
/* 351 */         buffer[bufferUpto] = (byte)(0x80 | length & 0x7F);
/* 352 */         buffer[(bufferUpto + 1)] = (byte)(length >> 7 & 0xFF);
/* 353 */         this.pool.byteUpto += length + 2;
/* 354 */         System.arraycopy(bytes.bytes, bytes.offset, buffer, bufferUpto + 2, length);
/*     */       }
/*     */ 
/* 357 */       assert (this.ords[hashPos] == -1);
/* 358 */       this.ords[hashPos] = e;
/*     */ 
/* 360 */       if (this.count == this.hashHalfSize) {
/* 361 */         rehash(2 * this.hashSize, true);
/*     */       }
/* 363 */       return e;
/*     */     }
/* 365 */     return -(e + 1);
/*     */   }
/*     */ 
/*     */   public int addByPoolOffset(int offset) {
/* 369 */     assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
/*     */ 
/* 371 */     int code = offset;
/* 372 */     int hashPos = offset & this.hashMask;
/* 373 */     int e = this.ords[hashPos];
/* 374 */     if ((e != -1) && (this.bytesStart[e] != offset))
/*     */     {
/* 377 */       int inc = (code >> 8) + code | 0x1;
/*     */       do {
/* 379 */         code += inc;
/* 380 */         hashPos = code & this.hashMask;
/* 381 */         e = this.ords[hashPos];
/* 382 */       }while ((e != -1) && (this.bytesStart[e] != offset));
/*     */     }
/* 384 */     if (e == -1)
/*     */     {
/* 386 */       if (this.count >= this.bytesStart.length) {
/* 387 */         this.bytesStart = this.bytesStartArray.grow();
/* 388 */         assert (this.count < this.bytesStart.length + 1) : ("count: " + this.count + " len: " + this.bytesStart.length);
/*     */       }
/*     */ 
/* 391 */       e = this.count++;
/* 392 */       this.bytesStart[e] = offset;
/* 393 */       assert (this.ords[hashPos] == -1);
/* 394 */       this.ords[hashPos] = e;
/*     */ 
/* 396 */       if (this.count == this.hashHalfSize) {
/* 397 */         rehash(2 * this.hashSize, false);
/*     */       }
/* 399 */       return e;
/*     */     }
/* 401 */     return -(e + 1);
/*     */   }
/*     */ 
/*     */   private void rehash(int newSize, boolean hashOnData)
/*     */   {
/* 409 */     int newMask = newSize - 1;
/* 410 */     this.bytesUsed.addAndGet(4 * newSize);
/* 411 */     int[] newHash = new int[newSize];
/* 412 */     Arrays.fill(newHash, -1);
/* 413 */     for (int i = 0; i < this.hashSize; i++) {
/* 414 */       int e0 = this.ords[i];
/* 415 */       if (e0 == -1)
/*     */         continue;
/*     */       int code;
/* 417 */       if (hashOnData) {
/* 418 */         int off = this.bytesStart[e0];
/* 419 */         int start = off & 0x7FFF;
/* 420 */         byte[] bytes = this.pool.buffers[(off >> 15)];
/* 421 */         int code = 0;
/*     */         int pos;
/*     */         int len;
/*     */         int pos;
/* 424 */         if ((bytes[start] & 0x80) == 0)
/*     */         {
/* 426 */           int len = bytes[start];
/* 427 */           pos = start + 1;
/*     */         } else {
/* 429 */           len = (bytes[start] & 0x7F) + ((bytes[(start + 1)] & 0xFF) << 7);
/* 430 */           pos = start + 2;
/*     */         }
/*     */ 
/* 433 */         int endPos = pos + len;
/* 434 */         while (pos < endPos)
/* 435 */           code = 31 * code + bytes[(pos++)];
/*     */       }
/*     */       else {
/* 438 */         code = this.bytesStart[e0];
/*     */       }
/*     */ 
/* 441 */       int hashPos = code & newMask;
/* 442 */       assert (hashPos >= 0);
/* 443 */       if (newHash[hashPos] != -1) {
/* 444 */         int inc = (code >> 8) + code | 0x1;
/*     */         do {
/* 446 */           code += inc;
/* 447 */           hashPos = code & newMask;
/* 448 */         }while (newHash[hashPos] != -1);
/*     */       }
/* 450 */       newHash[hashPos] = e0;
/*     */     }
/*     */ 
/* 454 */     this.hashMask = newMask;
/* 455 */     this.bytesUsed.addAndGet(4 * -this.ords.length);
/* 456 */     this.ords = newHash;
/* 457 */     this.hashSize = newSize;
/* 458 */     this.hashHalfSize = (newSize / 2);
/*     */   }
/*     */ 
/*     */   public void reinit()
/*     */   {
/* 467 */     if (this.bytesStart == null) {
/* 468 */       this.bytesStart = this.bytesStartArray.init();
/*     */     }
/*     */ 
/* 471 */     if (this.ords == null) {
/* 472 */       this.ords = new int[this.hashSize];
/* 473 */       this.bytesUsed.addAndGet(4 * this.hashSize);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int byteStart(int ord)
/*     */   {
/* 487 */     assert (this.bytesStart != null) : "Bytesstart is null - not initialized";
/* 488 */     assert ((ord >= 0) && (ord < this.count)) : ord;
/* 489 */     return this.bytesStart[ord];
/*     */   }
/*     */ 
/*     */   public static class DirectBytesStartArray extends BytesRefHash.BytesStartArray
/*     */   {
/*     */     protected final int initSize;
/*     */     private int[] bytesStart;
/*     */     private final AtomicLong bytesUsed;
/*     */ 
/*     */     public DirectBytesStartArray(int initSize)
/*     */     {
/* 586 */       this.bytesUsed = new AtomicLong(0L);
/* 587 */       this.initSize = initSize;
/*     */     }
/*     */ 
/*     */     public int[] clear()
/*     */     {
/* 593 */       return this.bytesStart = null;
/*     */     }
/*     */ 
/*     */     public int[] grow()
/*     */     {
/* 598 */       assert (this.bytesStart != null);
/* 599 */       return this.bytesStart = ArrayUtil.grow(this.bytesStart, this.bytesStart.length + 1);
/*     */     }
/*     */ 
/*     */     public int[] init()
/*     */     {
/* 604 */       return this.bytesStart = new int[ArrayUtil.oversize(this.initSize, 4)];
/*     */     }
/*     */ 
/*     */     public AtomicLong bytesUsed()
/*     */     {
/* 610 */       return this.bytesUsed;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class TrackingDirectBytesStartArray extends BytesRefHash.BytesStartArray
/*     */   {
/*     */     protected final int initSize;
/*     */     private int[] bytesStart;
/*     */     protected final AtomicLong bytesUsed;
/*     */ 
/*     */     public TrackingDirectBytesStartArray(int initSize, AtomicLong bytesUsed)
/*     */     {
/* 545 */       this.initSize = initSize;
/* 546 */       this.bytesUsed = bytesUsed;
/*     */     }
/*     */ 
/*     */     public int[] clear()
/*     */     {
/* 551 */       if (this.bytesStart != null) {
/* 552 */         this.bytesUsed.addAndGet(-this.bytesStart.length * 4);
/*     */       }
/* 554 */       return this.bytesStart = null;
/*     */     }
/*     */ 
/*     */     public int[] grow()
/*     */     {
/* 559 */       assert (this.bytesStart != null);
/* 560 */       int oldSize = this.bytesStart.length;
/* 561 */       this.bytesStart = ArrayUtil.grow(this.bytesStart, this.bytesStart.length + 1);
/* 562 */       this.bytesUsed.addAndGet((this.bytesStart.length - oldSize) * 4);
/* 563 */       return this.bytesStart;
/*     */     }
/*     */ 
/*     */     public int[] init()
/*     */     {
/* 568 */       this.bytesStart = new int[ArrayUtil.oversize(this.initSize, 4)];
/*     */ 
/* 570 */       this.bytesUsed.addAndGet(this.bytesStart.length * 4);
/* 571 */       return this.bytesStart;
/*     */     }
/*     */ 
/*     */     public AtomicLong bytesUsed()
/*     */     {
/* 576 */       return this.bytesUsed;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class BytesStartArray
/*     */   {
/*     */     public abstract int[] init();
/*     */ 
/*     */     public abstract int[] grow();
/*     */ 
/*     */     public abstract int[] clear();
/*     */ 
/*     */     public abstract AtomicLong bytesUsed();
/*     */   }
/*     */ 
/*     */   public static class MaxBytesLengthExceededException extends RuntimeException
/*     */   {
/*     */     MaxBytesLengthExceededException(String message)
/*     */     {
/* 499 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.BytesRefHash
 * JD-Core Version:    0.6.0
 */