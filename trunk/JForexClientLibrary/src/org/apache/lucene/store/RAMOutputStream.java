/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class RAMOutputStream extends IndexOutput
/*     */ {
/*     */   static final int BUFFER_SIZE = 1024;
/*     */   private RAMFile file;
/*     */   private byte[] currentBuffer;
/*     */   private int currentBufferIndex;
/*     */   private int bufferPosition;
/*     */   private long bufferStart;
/*     */   private int bufferLength;
/*     */ 
/*     */   public RAMOutputStream()
/*     */   {
/*  41 */     this(new RAMFile());
/*     */   }
/*     */ 
/*     */   public RAMOutputStream(RAMFile f) {
/*  45 */     this.file = f;
/*     */ 
/*  49 */     this.currentBufferIndex = -1;
/*  50 */     this.currentBuffer = null;
/*     */   }
/*     */ 
/*     */   public void writeTo(IndexOutput out) throws IOException
/*     */   {
/*  55 */     flush();
/*  56 */     long end = this.file.length;
/*  57 */     long pos = 0L;
/*  58 */     int buffer = 0;
/*  59 */     while (pos < end) {
/*  60 */       int length = 1024;
/*  61 */       long nextPos = pos + length;
/*  62 */       if (nextPos > end) {
/*  63 */         length = (int)(end - pos);
/*     */       }
/*  65 */       out.writeBytes(this.file.getBuffer(buffer++), length);
/*  66 */       pos = nextPos;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  72 */     this.currentBuffer = null;
/*  73 */     this.currentBufferIndex = -1;
/*  74 */     this.bufferPosition = 0;
/*  75 */     this.bufferStart = 0L;
/*  76 */     this.bufferLength = 0;
/*  77 */     this.file.setLength(0L);
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  82 */     flush();
/*     */   }
/*     */ 
/*     */   public void seek(long pos)
/*     */     throws IOException
/*     */   {
/*  89 */     setFileLength();
/*  90 */     if ((pos < this.bufferStart) || (pos >= this.bufferStart + this.bufferLength)) {
/*  91 */       this.currentBufferIndex = (int)(pos / 1024L);
/*  92 */       switchCurrentBuffer();
/*     */     }
/*     */ 
/*  95 */     this.bufferPosition = (int)(pos % 1024L);
/*     */   }
/*     */ 
/*     */   public long length()
/*     */   {
/* 100 */     return this.file.length;
/*     */   }
/*     */ 
/*     */   public void writeByte(byte b) throws IOException
/*     */   {
/* 105 */     if (this.bufferPosition == this.bufferLength) {
/* 106 */       this.currentBufferIndex += 1;
/* 107 */       switchCurrentBuffer();
/*     */     }
/* 109 */     this.currentBuffer[(this.bufferPosition++)] = b;
/*     */   }
/*     */ 
/*     */   public void writeBytes(byte[] b, int offset, int len) throws IOException
/*     */   {
/* 114 */     assert (b != null);
/* 115 */     while (len > 0) {
/* 116 */       if (this.bufferPosition == this.bufferLength) {
/* 117 */         this.currentBufferIndex += 1;
/* 118 */         switchCurrentBuffer();
/*     */       }
/*     */ 
/* 121 */       int remainInBuffer = this.currentBuffer.length - this.bufferPosition;
/* 122 */       int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
/* 123 */       System.arraycopy(b, offset, this.currentBuffer, this.bufferPosition, bytesToCopy);
/* 124 */       offset += bytesToCopy;
/* 125 */       len -= bytesToCopy;
/* 126 */       this.bufferPosition += bytesToCopy;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void switchCurrentBuffer() throws IOException {
/* 131 */     if (this.currentBufferIndex == this.file.numBuffers())
/* 132 */       this.currentBuffer = this.file.addBuffer(1024);
/*     */     else {
/* 134 */       this.currentBuffer = this.file.getBuffer(this.currentBufferIndex);
/*     */     }
/* 136 */     this.bufferPosition = 0;
/* 137 */     this.bufferStart = (1024L * this.currentBufferIndex);
/* 138 */     this.bufferLength = this.currentBuffer.length;
/*     */   }
/*     */ 
/*     */   private void setFileLength() {
/* 142 */     long pointer = this.bufferStart + this.bufferPosition;
/* 143 */     if (pointer > this.file.length)
/* 144 */       this.file.setLength(pointer);
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 150 */     this.file.setLastModified(System.currentTimeMillis());
/* 151 */     setFileLength();
/*     */   }
/*     */ 
/*     */   public long getFilePointer()
/*     */   {
/* 156 */     return this.currentBufferIndex < 0 ? 0L : this.bufferStart + this.bufferPosition;
/*     */   }
/*     */ 
/*     */   public long sizeInBytes()
/*     */   {
/* 161 */     return this.file.numBuffers() * 1024;
/*     */   }
/*     */ 
/*     */   public void copyBytes(DataInput input, long numBytes) throws IOException
/*     */   {
/* 166 */     assert (numBytes >= 0L) : ("numBytes=" + numBytes);
/*     */ 
/* 168 */     while (numBytes > 0L) {
/* 169 */       if (this.bufferPosition == this.bufferLength) {
/* 170 */         this.currentBufferIndex += 1;
/* 171 */         switchCurrentBuffer();
/*     */       }
/*     */ 
/* 174 */       int toCopy = this.currentBuffer.length - this.bufferPosition;
/* 175 */       if (numBytes < toCopy) {
/* 176 */         toCopy = (int)numBytes;
/*     */       }
/* 178 */       input.readBytes(this.currentBuffer, this.bufferPosition, toCopy, false);
/* 179 */       numBytes -= toCopy;
/* 180 */       this.bufferPosition += toCopy;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.RAMOutputStream
 * JD-Core Version:    0.6.0
 */