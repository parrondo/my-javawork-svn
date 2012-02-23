/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public abstract class BufferedIndexInput extends IndexInput
/*     */ {
/*     */   public static final int BUFFER_SIZE = 1024;
/*  28 */   private int bufferSize = 1024;
/*     */   protected byte[] buffer;
/*  32 */   private long bufferStart = 0L;
/*  33 */   private int bufferLength = 0;
/*  34 */   private int bufferPosition = 0;
/*     */ 
/*     */   public byte readByte() throws IOException
/*     */   {
/*  38 */     if (this.bufferPosition >= this.bufferLength)
/*  39 */       refill();
/*  40 */     return this.buffer[(this.bufferPosition++)];
/*     */   }
/*     */ 
/*     */   public BufferedIndexInput() {
/*     */   }
/*     */ 
/*     */   public BufferedIndexInput(int bufferSize) {
/*  47 */     checkBufferSize(bufferSize);
/*  48 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int newSize)
/*     */   {
/*  53 */     if ((!$assertionsDisabled) && (this.buffer != null) && (this.bufferSize != this.buffer.length)) throw new AssertionError("buffer=" + this.buffer + " bufferSize=" + this.bufferSize + " buffer.length=" + (this.buffer != null ? this.buffer.length : 0));
/*  54 */     if (newSize != this.bufferSize) {
/*  55 */       checkBufferSize(newSize);
/*  56 */       this.bufferSize = newSize;
/*  57 */       if (this.buffer != null)
/*     */       {
/*  61 */         byte[] newBuffer = new byte[newSize];
/*  62 */         int leftInBuffer = this.bufferLength - this.bufferPosition;
/*     */         int numToCopy;
/*     */         int numToCopy;
/*  64 */         if (leftInBuffer > newSize)
/*  65 */           numToCopy = newSize;
/*     */         else
/*  67 */           numToCopy = leftInBuffer;
/*  68 */         System.arraycopy(this.buffer, this.bufferPosition, newBuffer, 0, numToCopy);
/*  69 */         this.bufferStart += this.bufferPosition;
/*  70 */         this.bufferPosition = 0;
/*  71 */         this.bufferLength = numToCopy;
/*  72 */         newBuffer(newBuffer);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void newBuffer(byte[] newBuffer)
/*     */   {
/*  79 */     this.buffer = newBuffer;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/*  84 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   private void checkBufferSize(int bufferSize) {
/*  88 */     if (bufferSize <= 0)
/*  89 */       throw new IllegalArgumentException("bufferSize must be greater than 0 (got " + bufferSize + ")");
/*     */   }
/*     */ 
/*     */   public void readBytes(byte[] b, int offset, int len) throws IOException
/*     */   {
/*  94 */     readBytes(b, offset, len, true);
/*     */   }
/*     */ 
/*     */   public void readBytes(byte[] b, int offset, int len, boolean useBuffer)
/*     */     throws IOException
/*     */   {
/* 100 */     if (len <= this.bufferLength - this.bufferPosition)
/*     */     {
/* 102 */       if (len > 0)
/* 103 */         System.arraycopy(this.buffer, this.bufferPosition, b, offset, len);
/* 104 */       this.bufferPosition += len;
/*     */     }
/*     */     else {
/* 107 */       int available = this.bufferLength - this.bufferPosition;
/* 108 */       if (available > 0) {
/* 109 */         System.arraycopy(this.buffer, this.bufferPosition, b, offset, available);
/* 110 */         offset += available;
/* 111 */         len -= available;
/* 112 */         this.bufferPosition += available;
/*     */       }
/*     */ 
/* 115 */       if ((useBuffer) && (len < this.bufferSize))
/*     */       {
/* 119 */         refill();
/* 120 */         if (this.bufferLength < len)
/*     */         {
/* 122 */           System.arraycopy(this.buffer, 0, b, offset, this.bufferLength);
/* 123 */           throw new IOException("read past EOF");
/*     */         }
/* 125 */         System.arraycopy(this.buffer, 0, b, offset, len);
/* 126 */         this.bufferPosition = len;
/*     */       }
/*     */       else
/*     */       {
/* 136 */         long after = this.bufferStart + this.bufferPosition + len;
/* 137 */         if (after > length())
/* 138 */           throw new IOException("read past EOF");
/* 139 */         readInternal(b, offset, len);
/* 140 */         this.bufferStart = after;
/* 141 */         this.bufferPosition = 0;
/* 142 */         this.bufferLength = 0;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int readInt() throws IOException
/*     */   {
/* 149 */     if (4 <= this.bufferLength - this.bufferPosition) {
/* 150 */       return (this.buffer[(this.bufferPosition++)] & 0xFF) << 24 | (this.buffer[(this.bufferPosition++)] & 0xFF) << 16 | (this.buffer[(this.bufferPosition++)] & 0xFF) << 8 | this.buffer[(this.bufferPosition++)] & 0xFF;
/*     */     }
/*     */ 
/* 153 */     return super.readInt();
/*     */   }
/*     */ 
/*     */   public long readLong()
/*     */     throws IOException
/*     */   {
/* 159 */     if (8 <= this.bufferLength - this.bufferPosition) {
/* 160 */       int i1 = (this.buffer[(this.bufferPosition++)] & 0xFF) << 24 | (this.buffer[(this.bufferPosition++)] & 0xFF) << 16 | (this.buffer[(this.bufferPosition++)] & 0xFF) << 8 | this.buffer[(this.bufferPosition++)] & 0xFF;
/*     */ 
/* 162 */       int i2 = (this.buffer[(this.bufferPosition++)] & 0xFF) << 24 | (this.buffer[(this.bufferPosition++)] & 0xFF) << 16 | (this.buffer[(this.bufferPosition++)] & 0xFF) << 8 | this.buffer[(this.bufferPosition++)] & 0xFF;
/*     */ 
/* 164 */       return i1 << 32 | i2 & 0xFFFFFFFF;
/*     */     }
/* 166 */     return super.readLong();
/*     */   }
/*     */ 
/*     */   public int readVInt()
/*     */     throws IOException
/*     */   {
/* 172 */     if (5 <= this.bufferLength - this.bufferPosition) {
/* 173 */       byte b = this.buffer[(this.bufferPosition++)];
/* 174 */       int i = b & 0x7F;
/* 175 */       for (int shift = 7; (b & 0x80) != 0; shift += 7) {
/* 176 */         b = this.buffer[(this.bufferPosition++)];
/* 177 */         i |= (b & 0x7F) << shift;
/*     */       }
/* 179 */       return i;
/*     */     }
/* 181 */     return super.readVInt();
/*     */   }
/*     */ 
/*     */   public long readVLong()
/*     */     throws IOException
/*     */   {
/* 187 */     if (9 <= this.bufferLength - this.bufferPosition) {
/* 188 */       byte b = this.buffer[(this.bufferPosition++)];
/* 189 */       long i = b & 0x7F;
/* 190 */       for (int shift = 7; (b & 0x80) != 0; shift += 7) {
/* 191 */         b = this.buffer[(this.bufferPosition++)];
/* 192 */         i |= (b & 0x7F) << shift;
/*     */       }
/* 194 */       return i;
/*     */     }
/* 196 */     return super.readVLong();
/*     */   }
/*     */ 
/*     */   private void refill() throws IOException
/*     */   {
/* 201 */     long start = this.bufferStart + this.bufferPosition;
/* 202 */     long end = start + this.bufferSize;
/* 203 */     if (end > length())
/* 204 */       end = length();
/* 205 */     int newLength = (int)(end - start);
/* 206 */     if (newLength <= 0) {
/* 207 */       throw new IOException("read past EOF");
/*     */     }
/* 209 */     if (this.buffer == null) {
/* 210 */       newBuffer(new byte[this.bufferSize]);
/* 211 */       seekInternal(this.bufferStart);
/*     */     }
/* 213 */     readInternal(this.buffer, 0, newLength);
/* 214 */     this.bufferLength = newLength;
/* 215 */     this.bufferStart = start;
/* 216 */     this.bufferPosition = 0;
/*     */   }
/*     */ 
/*     */   protected abstract void readInternal(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws IOException;
/*     */ 
/*     */   public long getFilePointer()
/*     */   {
/* 229 */     return this.bufferStart + this.bufferPosition;
/*     */   }
/*     */ 
/*     */   public void seek(long pos) throws IOException {
/* 233 */     if ((pos >= this.bufferStart) && (pos < this.bufferStart + this.bufferLength)) {
/* 234 */       this.bufferPosition = (int)(pos - this.bufferStart);
/*     */     } else {
/* 236 */       this.bufferStart = pos;
/* 237 */       this.bufferPosition = 0;
/* 238 */       this.bufferLength = 0;
/* 239 */       seekInternal(pos);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void seekInternal(long paramLong)
/*     */     throws IOException;
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 251 */     BufferedIndexInput clone = (BufferedIndexInput)super.clone();
/*     */ 
/* 253 */     clone.buffer = null;
/* 254 */     clone.bufferLength = 0;
/* 255 */     clone.bufferPosition = 0;
/* 256 */     clone.bufferStart = getFilePointer();
/*     */ 
/* 258 */     return clone;
/*     */   }
/*     */ 
/*     */   protected int flushBuffer(IndexOutput out, long numBytes)
/*     */     throws IOException
/*     */   {
/* 271 */     int toCopy = this.bufferLength - this.bufferPosition;
/* 272 */     if (toCopy > numBytes) {
/* 273 */       toCopy = (int)numBytes;
/*     */     }
/* 275 */     if (toCopy > 0) {
/* 276 */       out.writeBytes(this.buffer, this.bufferPosition, toCopy);
/* 277 */       this.bufferPosition += toCopy;
/*     */     }
/* 279 */     return toCopy;
/*     */   }
/*     */ 
/*     */   public void copyBytes(IndexOutput out, long numBytes) throws IOException
/*     */   {
/* 284 */     assert (numBytes >= 0L) : ("numBytes=" + numBytes);
/*     */ 
/* 286 */     while (numBytes > 0L) {
/* 287 */       if (this.bufferLength == this.bufferPosition) {
/* 288 */         refill();
/*     */       }
/* 290 */       numBytes -= flushBuffer(out, numBytes);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.BufferedIndexInput
 * JD-Core Version:    0.6.0
 */