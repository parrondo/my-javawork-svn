/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public abstract class BufferedIndexOutput extends IndexOutput
/*     */ {
/*     */   static final int BUFFER_SIZE = 16384;
/*  26 */   private final byte[] buffer = new byte[16384];
/*  27 */   private long bufferStart = 0L;
/*  28 */   private int bufferPosition = 0;
/*     */ 
/*     */   public void writeByte(byte b)
/*     */     throws IOException
/*     */   {
/*  35 */     if (this.bufferPosition >= 16384)
/*  36 */       flush();
/*  37 */     this.buffer[(this.bufferPosition++)] = b;
/*     */   }
/*     */ 
/*     */   public void writeBytes(byte[] b, int offset, int length)
/*     */     throws IOException
/*     */   {
/*  47 */     int bytesLeft = 16384 - this.bufferPosition;
/*     */ 
/*  49 */     if (bytesLeft >= length)
/*     */     {
/*  51 */       System.arraycopy(b, offset, this.buffer, this.bufferPosition, length);
/*  52 */       this.bufferPosition += length;
/*     */ 
/*  54 */       if (16384 - this.bufferPosition == 0) {
/*  55 */         flush();
/*     */       }
/*     */     }
/*  58 */     else if (length > 16384)
/*     */     {
/*  60 */       if (this.bufferPosition > 0) {
/*  61 */         flush();
/*     */       }
/*  63 */       flushBuffer(b, offset, length);
/*  64 */       this.bufferStart += length;
/*     */     }
/*     */     else {
/*  67 */       int pos = 0;
/*     */ 
/*  69 */       while (pos < length) {
/*  70 */         int pieceLength = length - pos < bytesLeft ? length - pos : bytesLeft;
/*  71 */         System.arraycopy(b, pos + offset, this.buffer, this.bufferPosition, pieceLength);
/*  72 */         pos += pieceLength;
/*  73 */         this.bufferPosition += pieceLength;
/*     */ 
/*  75 */         bytesLeft = 16384 - this.bufferPosition;
/*  76 */         if (bytesLeft == 0) {
/*  77 */           flush();
/*  78 */           bytesLeft = 16384;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*  88 */     flushBuffer(this.buffer, this.bufferPosition);
/*  89 */     this.bufferStart += this.bufferPosition;
/*  90 */     this.bufferPosition = 0;
/*     */   }
/*     */ 
/*     */   private void flushBuffer(byte[] b, int len)
/*     */     throws IOException
/*     */   {
/*  99 */     flushBuffer(b, 0, len);
/*     */   }
/*     */ 
/*     */   protected abstract void flushBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 113 */     flush();
/*     */   }
/*     */ 
/*     */   public long getFilePointer()
/*     */   {
/* 122 */     return this.bufferStart + this.bufferPosition;
/*     */   }
/*     */ 
/*     */   public void seek(long pos)
/*     */     throws IOException
/*     */   {
/* 130 */     flush();
/* 131 */     this.bufferStart = pos;
/*     */   }
/*     */ 
/*     */   public abstract long length()
/*     */     throws IOException;
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.BufferedIndexOutput
 * JD-Core Version:    0.6.0
 */