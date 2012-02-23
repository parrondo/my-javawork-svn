/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ 
/*     */ final class ByteSliceReader extends IndexInput
/*     */ {
/*     */   ByteBlockPool pool;
/*     */   int bufferUpto;
/*     */   byte[] buffer;
/*     */   public int upto;
/*     */   int limit;
/*     */   int level;
/*     */   public int bufferOffset;
/*     */   public int endIndex;
/*     */ 
/*     */   public void init(ByteBlockPool pool, int startIndex, int endIndex)
/*     */   {
/*  42 */     assert (endIndex - startIndex >= 0);
/*  43 */     assert (startIndex >= 0);
/*  44 */     assert (endIndex >= 0);
/*     */ 
/*  46 */     this.pool = pool;
/*  47 */     this.endIndex = endIndex;
/*     */ 
/*  49 */     this.level = 0;
/*  50 */     this.bufferUpto = (startIndex / 32768);
/*  51 */     this.bufferOffset = (this.bufferUpto * 32768);
/*  52 */     this.buffer = pool.buffers[this.bufferUpto];
/*  53 */     this.upto = (startIndex & 0x7FFF);
/*     */ 
/*  55 */     int firstSize = ByteBlockPool.levelSizeArray[0];
/*     */ 
/*  57 */     if (startIndex + firstSize >= endIndex)
/*     */     {
/*  59 */       this.limit = (endIndex & 0x7FFF);
/*     */     }
/*  61 */     else this.limit = (this.upto + firstSize - 4); 
/*     */   }
/*     */ 
/*     */   public boolean eof()
/*     */   {
/*  65 */     assert (this.upto + this.bufferOffset <= this.endIndex);
/*  66 */     return this.upto + this.bufferOffset == this.endIndex;
/*     */   }
/*     */ 
/*     */   public byte readByte()
/*     */   {
/*  71 */     assert (!eof());
/*  72 */     assert (this.upto <= this.limit);
/*  73 */     if (this.upto == this.limit)
/*  74 */       nextSlice();
/*  75 */     return this.buffer[(this.upto++)];
/*     */   }
/*     */ 
/*     */   public long writeTo(IndexOutput out) throws IOException {
/*  79 */     long size = 0L;
/*     */     while (true) {
/*  81 */       if (this.limit + this.bufferOffset == this.endIndex) {
/*  82 */         assert (this.endIndex - this.bufferOffset >= this.upto);
/*  83 */         out.writeBytes(this.buffer, this.upto, this.limit - this.upto);
/*  84 */         size += this.limit - this.upto;
/*  85 */         break;
/*     */       }
/*  87 */       out.writeBytes(this.buffer, this.upto, this.limit - this.upto);
/*  88 */       size += this.limit - this.upto;
/*  89 */       nextSlice();
/*     */     }
/*     */ 
/*  93 */     return size;
/*     */   }
/*     */ 
/*     */   public void nextSlice()
/*     */   {
/*  99 */     int nextIndex = ((this.buffer[this.limit] & 0xFF) << 24) + ((this.buffer[(1 + this.limit)] & 0xFF) << 16) + ((this.buffer[(2 + this.limit)] & 0xFF) << 8) + (this.buffer[(3 + this.limit)] & 0xFF);
/*     */ 
/* 101 */     this.level = ByteBlockPool.nextLevelArray[this.level];
/* 102 */     int newSize = ByteBlockPool.levelSizeArray[this.level];
/*     */ 
/* 104 */     this.bufferUpto = (nextIndex / 32768);
/* 105 */     this.bufferOffset = (this.bufferUpto * 32768);
/*     */ 
/* 107 */     this.buffer = this.pool.buffers[this.bufferUpto];
/* 108 */     this.upto = (nextIndex & 0x7FFF);
/*     */ 
/* 110 */     if (nextIndex + newSize >= this.endIndex)
/*     */     {
/* 112 */       assert (this.endIndex - nextIndex > 0);
/* 113 */       this.limit = (this.endIndex - this.bufferOffset);
/*     */     }
/*     */     else
/*     */     {
/* 117 */       this.limit = (this.upto + newSize - 4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readBytes(byte[] b, int offset, int len)
/*     */   {
/* 123 */     while (len > 0) {
/* 124 */       int numLeft = this.limit - this.upto;
/* 125 */       if (numLeft < len)
/*     */       {
/* 127 */         System.arraycopy(this.buffer, this.upto, b, offset, numLeft);
/* 128 */         offset += numLeft;
/* 129 */         len -= numLeft;
/* 130 */         nextSlice();
/*     */       }
/*     */       else {
/* 133 */         System.arraycopy(this.buffer, this.upto, b, offset, len);
/* 134 */         this.upto += len;
/* 135 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getFilePointer() {
/* 141 */     throw new RuntimeException("not implemented");
/*     */   }
/* 143 */   public long length() { throw new RuntimeException("not implemented"); } 
/*     */   public void seek(long pos) {
/* 145 */     throw new RuntimeException("not implemented");
/*     */   }
/* 147 */   public void close() { throw new RuntimeException("not implemented");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ByteSliceReader
 * JD-Core Version:    0.6.0
 */