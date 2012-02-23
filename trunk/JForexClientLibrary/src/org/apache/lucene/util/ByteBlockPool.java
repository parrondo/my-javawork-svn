/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.lucene.store.DataOutput;
/*     */ 
/*     */ public final class ByteBlockPool
/*     */ {
/*     */   public static final int BYTE_BLOCK_SHIFT = 15;
/*     */   public static final int BYTE_BLOCK_SIZE = 32768;
/*     */   public static final int BYTE_BLOCK_MASK = 32767;
/* 114 */   public byte[][] buffers = new byte[10][];
/*     */ 
/* 116 */   int bufferUpto = -1;
/* 117 */   public int byteUpto = 32768;
/*     */   public byte[] buffer;
/* 120 */   public int byteOffset = -32768;
/*     */   private final Allocator allocator;
/*     */   public static final int[] nextLevelArray;
/*     */   public static final int[] levelSizeArray;
/*     */   public static final int FIRST_LEVEL_SIZE;
/*     */ 
/*     */   public ByteBlockPool(Allocator allocator)
/*     */   {
/* 125 */     this.allocator = allocator;
/*     */   }
/*     */ 
/*     */   public void dropBuffersAndReset() {
/* 129 */     if (this.bufferUpto != -1)
/*     */     {
/* 131 */       this.allocator.recycleByteBlocks(this.buffers, 0, 1 + this.bufferUpto);
/*     */ 
/* 134 */       this.bufferUpto = -1;
/* 135 */       this.byteUpto = 32768;
/* 136 */       this.byteOffset = -32768;
/* 137 */       this.buffers = new byte[10][];
/* 138 */       this.buffer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 143 */     if (this.bufferUpto != -1)
/*     */     {
/* 146 */       for (int i = 0; i < this.bufferUpto; i++)
/*     */       {
/* 148 */         Arrays.fill(this.buffers[i], 0);
/*     */       }
/*     */ 
/* 151 */       Arrays.fill(this.buffers[this.bufferUpto], 0, this.byteUpto, 0);
/*     */ 
/* 153 */       if (this.bufferUpto > 0)
/*     */       {
/* 155 */         this.allocator.recycleByteBlocks(this.buffers, 1, 1 + this.bufferUpto);
/*     */       }
/*     */ 
/* 158 */       this.bufferUpto = 0;
/* 159 */       this.byteUpto = 0;
/* 160 */       this.byteOffset = 0;
/* 161 */       this.buffer = this.buffers[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   public void nextBuffer() {
/* 166 */     if (1 + this.bufferUpto == this.buffers.length) {
/* 167 */       byte[][] newBuffers = new byte[ArrayUtil.oversize(this.buffers.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
/*     */ 
/* 169 */       System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
/* 170 */       this.buffers = newBuffers;
/*     */     }
/* 172 */     this.buffer = (this.buffers[(1 + this.bufferUpto)] =  = this.allocator.getByteBlock());
/* 173 */     this.bufferUpto += 1;
/*     */ 
/* 175 */     this.byteUpto = 0;
/* 176 */     this.byteOffset += 32768;
/*     */   }
/*     */ 
/*     */   public int newSlice(int size) {
/* 180 */     if (this.byteUpto > 32768 - size)
/* 181 */       nextBuffer();
/* 182 */     int upto = this.byteUpto;
/* 183 */     this.byteUpto += size;
/* 184 */     this.buffer[(this.byteUpto - 1)] = 16;
/* 185 */     return upto;
/*     */   }
/*     */ 
/*     */   public int allocSlice(byte[] slice, int upto)
/*     */   {
/* 200 */     int level = slice[upto] & 0xF;
/* 201 */     int newLevel = nextLevelArray[level];
/* 202 */     int newSize = levelSizeArray[newLevel];
/*     */ 
/* 205 */     if (this.byteUpto > 32768 - newSize) {
/* 206 */       nextBuffer();
/*     */     }
/* 208 */     int newUpto = this.byteUpto;
/* 209 */     int offset = newUpto + this.byteOffset;
/* 210 */     this.byteUpto += newSize;
/*     */ 
/* 214 */     this.buffer[newUpto] = slice[(upto - 3)];
/* 215 */     this.buffer[(newUpto + 1)] = slice[(upto - 2)];
/* 216 */     this.buffer[(newUpto + 2)] = slice[(upto - 1)];
/*     */ 
/* 219 */     slice[(upto - 3)] = (byte)(offset >>> 24);
/* 220 */     slice[(upto - 2)] = (byte)(offset >>> 16);
/* 221 */     slice[(upto - 1)] = (byte)(offset >>> 8);
/* 222 */     slice[upto] = (byte)offset;
/*     */ 
/* 225 */     this.buffer[(this.byteUpto - 1)] = (byte)(0x10 | newLevel);
/*     */ 
/* 227 */     return newUpto + 3;
/*     */   }
/*     */ 
/*     */   public final BytesRef setBytesRef(BytesRef term, int textStart)
/*     */   {
/* 233 */     byte[] bytes = term.bytes = this.buffers[(textStart >> 15)];
/* 234 */     int pos = textStart & 0x7FFF;
/* 235 */     if ((bytes[pos] & 0x80) == 0)
/*     */     {
/* 237 */       term.length = bytes[pos];
/* 238 */       term.offset = (pos + 1);
/*     */     }
/*     */     else {
/* 241 */       term.length = ((bytes[pos] & 0x7F) + ((bytes[(pos + 1)] & 0xFF) << 7));
/* 242 */       term.offset = (pos + 2);
/*     */     }
/* 244 */     assert (term.length >= 0);
/* 245 */     return term;
/*     */   }
/*     */ 
/*     */   public final void copy(BytesRef bytes)
/*     */   {
/* 253 */     int length = bytes.length;
/* 254 */     int offset = bytes.offset;
/* 255 */     int overflow = length + this.byteUpto - 32768;
/*     */     while (true) {
/* 257 */       if (overflow <= 0) {
/* 258 */         System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, length);
/* 259 */         this.byteUpto += length;
/* 260 */         break;
/*     */       }
/* 262 */       int bytesToCopy = length - overflow;
/* 263 */       System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, bytesToCopy);
/* 264 */       offset += bytesToCopy;
/* 265 */       length -= bytesToCopy;
/* 266 */       nextBuffer();
/* 267 */       overflow -= 32768;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void writePool(DataOutput out)
/*     */     throws IOException
/*     */   {
/* 276 */     int bytesOffset = this.byteOffset;
/* 277 */     int block = 0;
/* 278 */     while (bytesOffset > 0) {
/* 279 */       out.writeBytes(this.buffers[(block++)], 32768);
/* 280 */       bytesOffset -= 32768;
/*     */     }
/* 282 */     out.writeBytes(this.buffers[block], this.byteUpto);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 194 */     nextLevelArray = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 9 };
/* 195 */     levelSizeArray = new int[] { 5, 14, 20, 30, 40, 40, 80, 80, 120, 200 };
/* 196 */     FIRST_LEVEL_SIZE = levelSizeArray[0];
/*     */   }
/*     */ 
/*     */   public static class DirectTrackingAllocator extends ByteBlockPool.Allocator
/*     */   {
/*     */     private final AtomicLong bytesUsed;
/*     */ 
/*     */     public DirectTrackingAllocator(AtomicLong bytesUsed)
/*     */     {
/*  91 */       this(32768, bytesUsed);
/*     */     }
/*     */ 
/*     */     public DirectTrackingAllocator(int blockSize, AtomicLong bytesUsed) {
/*  95 */       super();
/*  96 */       this.bytesUsed = bytesUsed;
/*     */     }
/*     */ 
/*     */     public byte[] getByteBlock() {
/* 100 */       this.bytesUsed.addAndGet(this.blockSize);
/* 101 */       return new byte[this.blockSize];
/*     */     }
/*     */ 
/*     */     public void recycleByteBlocks(byte[][] blocks, int start, int end) {
/* 105 */       this.bytesUsed.addAndGet(-((end - start) * this.blockSize));
/* 106 */       for (int i = start; i < end; i++)
/* 107 */         blocks[i] = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class DirectAllocator extends ByteBlockPool.Allocator
/*     */   {
/*     */     public DirectAllocator()
/*     */     {
/*  74 */       this(32768);
/*     */     }
/*     */ 
/*     */     public DirectAllocator(int blockSize) {
/*  78 */       super();
/*     */     }
/*     */ 
/*     */     public void recycleByteBlocks(byte[][] blocks, int start, int end)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class Allocator
/*     */   {
/*     */     protected final int blockSize;
/*     */ 
/*     */     public Allocator(int blockSize)
/*     */     {
/*  56 */       this.blockSize = blockSize;
/*     */     }
/*     */     public abstract void recycleByteBlocks(byte[][] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */     public void recycleByteBlocks(List<byte[]> blocks) {
/*  62 */       byte[][] b = (byte[][])blocks.toArray(new byte[blocks.size()][]);
/*  63 */       recycleByteBlocks(b, 0, b.length);
/*     */     }
/*     */ 
/*     */     public byte[] getByteBlock() {
/*  67 */       return new byte[this.blockSize];
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.ByteBlockPool
 * JD-Core Version:    0.6.0
 */