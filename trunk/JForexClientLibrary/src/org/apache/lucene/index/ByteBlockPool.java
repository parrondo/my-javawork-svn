/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ final class ByteBlockPool
/*     */ {
/*  50 */   public byte[][] buffers = new byte[10][];
/*     */ 
/*  52 */   int bufferUpto = -1;
/*  53 */   public int byteUpto = 32768;
/*     */   public byte[] buffer;
/*  56 */   public int byteOffset = -32768;
/*     */   private final Allocator allocator;
/* 115 */   static final int[] nextLevelArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 9 };
/* 116 */   static final int[] levelSizeArray = { 5, 14, 20, 30, 40, 40, 80, 80, 120, 200 };
/* 117 */   static final int FIRST_LEVEL_SIZE = levelSizeArray[0];
/*     */ 
/*     */   public ByteBlockPool(Allocator allocator)
/*     */   {
/*  61 */     this.allocator = allocator;
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  65 */     if (this.bufferUpto != -1)
/*     */     {
/*  68 */       for (int i = 0; i < this.bufferUpto; i++)
/*     */       {
/*  70 */         Arrays.fill(this.buffers[i], 0);
/*     */       }
/*     */ 
/*  73 */       Arrays.fill(this.buffers[this.bufferUpto], 0, this.byteUpto, 0);
/*     */ 
/*  75 */       if (this.bufferUpto > 0)
/*     */       {
/*  77 */         this.allocator.recycleByteBlocks(this.buffers, 1, 1 + this.bufferUpto);
/*     */       }
/*     */ 
/*  80 */       this.bufferUpto = 0;
/*  81 */       this.byteUpto = 0;
/*  82 */       this.byteOffset = 0;
/*  83 */       this.buffer = this.buffers[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   public void nextBuffer() {
/*  88 */     if (1 + this.bufferUpto == this.buffers.length) {
/*  89 */       byte[][] newBuffers = new byte[ArrayUtil.oversize(this.buffers.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
/*     */ 
/*  91 */       System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
/*  92 */       this.buffers = newBuffers;
/*     */     }
/*  94 */     this.buffer = (this.buffers[(1 + this.bufferUpto)] =  = this.allocator.getByteBlock());
/*  95 */     this.bufferUpto += 1;
/*     */ 
/*  97 */     this.byteUpto = 0;
/*  98 */     this.byteOffset += 32768;
/*     */   }
/*     */ 
/*     */   public int newSlice(int size) {
/* 102 */     if (this.byteUpto > 32768 - size)
/* 103 */       nextBuffer();
/* 104 */     int upto = this.byteUpto;
/* 105 */     this.byteUpto += size;
/* 106 */     this.buffer[(this.byteUpto - 1)] = 16;
/* 107 */     return upto;
/*     */   }
/*     */ 
/*     */   public int allocSlice(byte[] slice, int upto)
/*     */   {
/* 121 */     int level = slice[upto] & 0xF;
/* 122 */     int newLevel = nextLevelArray[level];
/* 123 */     int newSize = levelSizeArray[newLevel];
/*     */ 
/* 126 */     if (this.byteUpto > 32768 - newSize) {
/* 127 */       nextBuffer();
/*     */     }
/* 129 */     int newUpto = this.byteUpto;
/* 130 */     int offset = newUpto + this.byteOffset;
/* 131 */     this.byteUpto += newSize;
/*     */ 
/* 135 */     this.buffer[newUpto] = slice[(upto - 3)];
/* 136 */     this.buffer[(newUpto + 1)] = slice[(upto - 2)];
/* 137 */     this.buffer[(newUpto + 2)] = slice[(upto - 1)];
/*     */ 
/* 140 */     slice[(upto - 3)] = (byte)(offset >>> 24);
/* 141 */     slice[(upto - 2)] = (byte)(offset >>> 16);
/* 142 */     slice[(upto - 1)] = (byte)(offset >>> 8);
/* 143 */     slice[upto] = (byte)offset;
/*     */ 
/* 146 */     this.buffer[(this.byteUpto - 1)] = (byte)(0x10 | newLevel);
/*     */ 
/* 148 */     return newUpto + 3;
/*     */   }
/*     */ 
/*     */   static abstract class Allocator
/*     */   {
/*     */     abstract void recycleByteBlocks(byte[][] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */     abstract void recycleByteBlocks(List<byte[]> paramList);
/*     */ 
/*     */     abstract byte[] getByteBlock();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ByteBlockPool
 * JD-Core Version:    0.6.0
 */