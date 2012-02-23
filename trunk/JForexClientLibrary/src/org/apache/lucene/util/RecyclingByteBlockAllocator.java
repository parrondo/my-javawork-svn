/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ public final class RecyclingByteBlockAllocator extends ByteBlockPool.Allocator
/*     */ {
/*     */   private byte[][] freeByteBlocks;
/*     */   private final int maxBufferedBlocks;
/*  34 */   private int freeBlocks = 0;
/*     */   private final AtomicLong bytesUsed;
/*     */   public static final int DEFAULT_BUFFERED_BLOCKS = 64;
/*     */ 
/*     */   public RecyclingByteBlockAllocator(int blockSize, int maxBufferedBlocks, AtomicLong bytesUsed)
/*     */   {
/*  52 */     super(blockSize);
/*  53 */     this.freeByteBlocks = new byte[Math.min(10, maxBufferedBlocks)][];
/*  54 */     this.maxBufferedBlocks = maxBufferedBlocks;
/*  55 */     this.bytesUsed = bytesUsed;
/*     */   }
/*     */ 
/*     */   public RecyclingByteBlockAllocator(int blockSize, int maxBufferedBlocks)
/*     */   {
/*  68 */     this(blockSize, maxBufferedBlocks, new AtomicLong());
/*     */   }
/*     */ 
/*     */   public RecyclingByteBlockAllocator()
/*     */   {
/*  79 */     this(32768, 64, new AtomicLong());
/*     */   }
/*     */ 
/*     */   public synchronized byte[] getByteBlock()
/*     */   {
/*  84 */     if (this.freeBlocks == 0) {
/*  85 */       this.bytesUsed.addAndGet(this.blockSize);
/*  86 */       return new byte[this.blockSize];
/*     */     }
/*  88 */     byte[] b = this.freeByteBlocks[(--this.freeBlocks)];
/*  89 */     this.freeByteBlocks[this.freeBlocks] = null;
/*  90 */     return b;
/*     */   }
/*     */ 
/*     */   public synchronized void recycleByteBlocks(byte[][] blocks, int start, int end)
/*     */   {
/*  95 */     int numBlocks = Math.min(this.maxBufferedBlocks - this.freeBlocks, end - start);
/*  96 */     int size = this.freeBlocks + numBlocks;
/*  97 */     if (size >= this.freeByteBlocks.length) {
/*  98 */       byte[][] newBlocks = new byte[ArrayUtil.oversize(size, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
/*     */ 
/* 100 */       System.arraycopy(this.freeByteBlocks, 0, newBlocks, 0, this.freeBlocks);
/* 101 */       this.freeByteBlocks = newBlocks;
/*     */     }
/* 103 */     int stop = start + numBlocks;
/* 104 */     for (int i = start; i < stop; i++) {
/* 105 */       this.freeByteBlocks[(this.freeBlocks++)] = blocks[i];
/* 106 */       blocks[i] = null;
/*     */     }
/* 108 */     for (int i = stop; i < end; i++) {
/* 109 */       blocks[i] = null;
/*     */     }
/* 111 */     this.bytesUsed.addAndGet(-(end - stop) * this.blockSize);
/* 112 */     assert (this.bytesUsed.get() >= 0L);
/*     */   }
/*     */ 
/*     */   public synchronized int numBufferedBlocks()
/*     */   {
/* 119 */     return this.freeBlocks;
/*     */   }
/*     */ 
/*     */   public long bytesUsed()
/*     */   {
/* 126 */     return this.bytesUsed.get();
/*     */   }
/*     */ 
/*     */   public int maxBufferedBlocks()
/*     */   {
/* 133 */     return this.maxBufferedBlocks;
/*     */   }
/*     */ 
/*     */   public synchronized int freeBlocks(int num)
/*     */   {
/* 144 */     assert (num >= 0);
/*     */     int count;
/*     */     int stop;
/*     */     int count;
/* 147 */     if (num > this.freeBlocks) {
/* 148 */       int stop = 0;
/* 149 */       count = this.freeBlocks;
/*     */     } else {
/* 151 */       stop = this.freeBlocks - num;
/* 152 */       count = num;
/*     */     }
/* 154 */     while (this.freeBlocks > stop) {
/* 155 */       this.freeByteBlocks[(--this.freeBlocks)] = null;
/*     */     }
/* 157 */     this.bytesUsed.addAndGet(-count * this.blockSize);
/* 158 */     assert (this.bytesUsed.get() >= 0L);
/* 159 */     return count;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.RecyclingByteBlockAllocator
 * JD-Core Version:    0.6.0
 */