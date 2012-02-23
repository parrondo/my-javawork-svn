/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class RAMInputStream extends IndexInput
/*     */   implements Cloneable
/*     */ {
/*     */   static final int BUFFER_SIZE = 1024;
/*     */   private RAMFile file;
/*     */   private long length;
/*     */   private byte[] currentBuffer;
/*     */   private int currentBufferIndex;
/*     */   private int bufferPosition;
/*     */   private long bufferStart;
/*     */   private int bufferLength;
/*     */ 
/*     */   public RAMInputStream(RAMFile f)
/*     */     throws IOException
/*     */   {
/*  39 */     this.file = f;
/*  40 */     this.length = this.file.length;
/*  41 */     if (this.length / 1024L >= 2147483647L) {
/*  42 */       throw new IOException("Too large RAMFile! " + this.length);
/*     */     }
/*     */ 
/*  47 */     this.currentBufferIndex = -1;
/*  48 */     this.currentBuffer = null;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public long length()
/*     */   {
/*  58 */     return this.length;
/*     */   }
/*     */ 
/*     */   public byte readByte() throws IOException
/*     */   {
/*  63 */     if (this.bufferPosition >= this.bufferLength) {
/*  64 */       this.currentBufferIndex += 1;
/*  65 */       switchCurrentBuffer(true);
/*     */     }
/*  67 */     return this.currentBuffer[(this.bufferPosition++)];
/*     */   }
/*     */ 
/*     */   public void readBytes(byte[] b, int offset, int len) throws IOException
/*     */   {
/*  72 */     while (len > 0) {
/*  73 */       if (this.bufferPosition >= this.bufferLength) {
/*  74 */         this.currentBufferIndex += 1;
/*  75 */         switchCurrentBuffer(true);
/*     */       }
/*     */ 
/*  78 */       int remainInBuffer = this.bufferLength - this.bufferPosition;
/*  79 */       int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
/*  80 */       System.arraycopy(this.currentBuffer, this.bufferPosition, b, offset, bytesToCopy);
/*  81 */       offset += bytesToCopy;
/*  82 */       len -= bytesToCopy;
/*  83 */       this.bufferPosition += bytesToCopy;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void switchCurrentBuffer(boolean enforceEOF) throws IOException {
/*  88 */     this.bufferStart = (1024L * this.currentBufferIndex);
/*  89 */     if (this.currentBufferIndex >= this.file.numBuffers())
/*     */     {
/*  91 */       if (enforceEOF) {
/*  92 */         throw new IOException("Read past EOF");
/*     */       }
/*     */ 
/*  95 */       this.currentBufferIndex -= 1;
/*  96 */       this.bufferPosition = 1024;
/*     */     }
/*     */     else {
/*  99 */       this.currentBuffer = this.file.getBuffer(this.currentBufferIndex);
/* 100 */       this.bufferPosition = 0;
/* 101 */       long buflen = this.length - this.bufferStart;
/* 102 */       this.bufferLength = (buflen > 1024L ? 1024 : (int)buflen);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void copyBytes(IndexOutput out, long numBytes) throws IOException
/*     */   {
/* 108 */     assert (numBytes >= 0L) : ("numBytes=" + numBytes);
/*     */ 
/* 110 */     long left = numBytes;
/* 111 */     while (left > 0L) {
/* 112 */       if (this.bufferPosition == this.bufferLength) {
/* 113 */         this.currentBufferIndex += 1;
/* 114 */         switchCurrentBuffer(true);
/*     */       }
/*     */ 
/* 117 */       int bytesInBuffer = this.bufferLength - this.bufferPosition;
/* 118 */       int toCopy = (int)(bytesInBuffer < left ? bytesInBuffer : left);
/* 119 */       out.writeBytes(this.currentBuffer, this.bufferPosition, toCopy);
/* 120 */       this.bufferPosition += toCopy;
/* 121 */       left -= toCopy;
/*     */     }
/*     */ 
/* 124 */     assert (left == 0L) : ("Insufficient bytes to copy: numBytes=" + numBytes + " copied=" + (numBytes - left));
/*     */   }
/*     */ 
/*     */   public long getFilePointer()
/*     */   {
/* 129 */     return this.currentBufferIndex < 0 ? 0L : this.bufferStart + this.bufferPosition;
/*     */   }
/*     */ 
/*     */   public void seek(long pos) throws IOException
/*     */   {
/* 134 */     if ((this.currentBuffer == null) || (pos < this.bufferStart) || (pos >= this.bufferStart + 1024L)) {
/* 135 */       this.currentBufferIndex = (int)(pos / 1024L);
/* 136 */       switchCurrentBuffer(false);
/*     */     }
/* 138 */     this.bufferPosition = (int)(pos % 1024L);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.RAMInputStream
 * JD-Core Version:    0.6.0
 */