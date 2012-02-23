/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileChannel.MapMode;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import org.apache.lucene.util.Constants;
/*     */ 
/*     */ public class MMapDirectory extends FSDirectory
/*     */ {
/*  81 */   private boolean useUnmapHack = UNMAP_SUPPORTED;
/*     */   public static final int DEFAULT_MAX_BUFF;
/*     */   private int chunkSizePower;
/*     */   public static final boolean UNMAP_SUPPORTED;
/*     */ 
/*     */   public MMapDirectory(File path, LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/*  93 */     super(path, lockFactory);
/*  94 */     setMaxChunkSize(DEFAULT_MAX_BUFF);
/*     */   }
/*     */ 
/*     */   public MMapDirectory(File path)
/*     */     throws IOException
/*     */   {
/* 103 */     super(path, null);
/* 104 */     setMaxChunkSize(DEFAULT_MAX_BUFF);
/*     */   }
/*     */ 
/*     */   public void setUseUnmap(boolean useUnmapHack)
/*     */   {
/* 137 */     if ((useUnmapHack) && (!UNMAP_SUPPORTED))
/* 138 */       throw new IllegalArgumentException("Unmap hack not supported on this platform!");
/* 139 */     this.useUnmapHack = useUnmapHack;
/*     */   }
/*     */ 
/*     */   public boolean getUseUnmap()
/*     */   {
/* 147 */     return this.useUnmapHack;
/*     */   }
/*     */ 
/*     */   final void cleanMapping(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 156 */     if (this.useUnmapHack)
/*     */       try {
/* 158 */         AccessController.doPrivileged(new PrivilegedExceptionAction(buffer) {
/*     */           public Object run() throws Exception {
/* 160 */             Method getCleanerMethod = this.val$buffer.getClass().getMethod("cleaner", new Class[0]);
/*     */ 
/* 162 */             getCleanerMethod.setAccessible(true);
/* 163 */             Object cleaner = getCleanerMethod.invoke(this.val$buffer, new Object[0]);
/* 164 */             if (cleaner != null) {
/* 165 */               cleaner.getClass().getMethod("clean", new Class[0]).invoke(cleaner, new Object[0]);
/*     */             }
/*     */ 
/* 168 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException e) {
/* 172 */         IOException ioe = new IOException("unable to unmap the mapped buffer");
/* 173 */         ioe.initCause(e.getCause());
/* 174 */         throw ioe;
/*     */       }
/*     */   }
/*     */ 
/*     */   public final void setMaxChunkSize(int maxChunkSize)
/*     */   {
/* 193 */     if (maxChunkSize <= 0) {
/* 194 */       throw new IllegalArgumentException("Maximum chunk size for mmap must be >0");
/*     */     }
/* 196 */     this.chunkSizePower = (31 - Integer.numberOfLeadingZeros(maxChunkSize));
/* 197 */     assert ((this.chunkSizePower >= 0) && (this.chunkSizePower <= 30));
/*     */   }
/*     */ 
/*     */   public final int getMaxChunkSize()
/*     */   {
/* 206 */     return 1 << this.chunkSizePower;
/*     */   }
/*     */ 
/*     */   public IndexInput openInput(String name, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 212 */     ensureOpen();
/* 213 */     File f = new File(getDirectory(), name);
/* 214 */     RandomAccessFile raf = new RandomAccessFile(f, "r");
/*     */     try {
/* 216 */       localMMapIndexInput = new MMapIndexInput(raf, this.chunkSizePower);
/*     */     }
/*     */     finally
/*     */     {
/*     */       MMapIndexInput localMMapIndexInput;
/* 218 */       raf.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  82 */     DEFAULT_MAX_BUFF = Constants.JRE_IS_64BIT ? 1073741824 : 268435456;
/*     */     boolean v;
/*     */     try
/*     */     {
/* 114 */       Class.forName("sun.misc.Cleaner");
/* 115 */       Class.forName("java.nio.DirectByteBuffer").getMethod("cleaner", new Class[0]);
/*     */ 
/* 117 */       v = true;
/*     */     } catch (Exception e) {
/* 119 */       v = false;
/*     */     }
/* 121 */     UNMAP_SUPPORTED = v;
/*     */   }
/*     */ 
/*     */   private final class MMapIndexInput extends IndexInput
/*     */   {
/*     */     private ByteBuffer[] buffers;
/*     */     private final long length;
/*     */     private final long chunkSizeMask;
/*     */     private final long chunkSize;
/*     */     private final int chunkSizePower;
/*     */     private int curBufIndex;
/*     */     private ByteBuffer curBuf;
/* 236 */     private boolean isClone = false;
/*     */ 
/*     */     MMapIndexInput(RandomAccessFile raf, int chunkSizePower) throws IOException {
/* 239 */       this.length = raf.length();
/* 240 */       this.chunkSizePower = chunkSizePower;
/* 241 */       this.chunkSize = (1L << chunkSizePower);
/* 242 */       this.chunkSizeMask = (this.chunkSize - 1L);
/*     */ 
/* 244 */       if ((chunkSizePower < 0) || (chunkSizePower > 30)) {
/* 245 */         throw new IllegalArgumentException("Invalid chunkSizePower used for ByteBuffer size: " + chunkSizePower);
/*     */       }
/* 247 */       if (this.length >>> chunkSizePower >= 2147483647L) {
/* 248 */         throw new IllegalArgumentException("RandomAccessFile too big for chunk size: " + raf.toString());
/*     */       }
/*     */ 
/* 251 */       int nrBuffers = (int)(this.length >>> chunkSizePower) + 1;
/*     */ 
/* 255 */       this.buffers = new ByteBuffer[nrBuffers];
/*     */ 
/* 257 */       long bufferStart = 0L;
/* 258 */       FileChannel rafc = raf.getChannel();
/* 259 */       for (int bufNr = 0; bufNr < nrBuffers; bufNr++) {
/* 260 */         int bufSize = (int)(this.length > bufferStart + this.chunkSize ? this.chunkSize : this.length - bufferStart);
/*     */ 
/* 264 */         this.buffers[bufNr] = rafc.map(FileChannel.MapMode.READ_ONLY, bufferStart, bufSize);
/* 265 */         bufferStart += bufSize;
/*     */       }
/* 267 */       seek(0L);
/*     */     }
/*     */ 
/*     */     public byte readByte() throws IOException
/*     */     {
/*     */       try {
/* 273 */         return this.curBuf.get();
/*     */       } catch (BufferUnderflowException e) {
/*     */         do {
/* 276 */           this.curBufIndex += 1;
/* 277 */           if (this.curBufIndex >= this.buffers.length)
/* 278 */             throw new IOException("read past EOF");
/* 279 */           this.curBuf = this.buffers[this.curBufIndex];
/* 280 */           this.curBuf.position(0);
/* 281 */         }while (!this.curBuf.hasRemaining());
/* 282 */       }return this.curBuf.get();
/*     */     }
/*     */ 
/*     */     public void readBytes(byte[] b, int offset, int len) throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 289 */         this.curBuf.get(b, offset, len);
/*     */       } catch (BufferUnderflowException e) {
/* 291 */         int curAvail = this.curBuf.remaining();
/* 292 */         while (len > curAvail) {
/* 293 */           this.curBuf.get(b, offset, curAvail);
/* 294 */           len -= curAvail;
/* 295 */           offset += curAvail;
/* 296 */           this.curBufIndex += 1;
/* 297 */           if (this.curBufIndex >= this.buffers.length)
/* 298 */             throw new IOException("read past EOF");
/* 299 */           this.curBuf = this.buffers[this.curBufIndex];
/* 300 */           this.curBuf.position(0);
/* 301 */           curAvail = this.curBuf.remaining();
/*     */         }
/* 303 */         this.curBuf.get(b, offset, len);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int readInt() throws IOException
/*     */     {
/*     */       try {
/* 310 */         return this.curBuf.getInt(); } catch (BufferUnderflowException e) {
/*     */       }
/* 312 */       return super.readInt();
/*     */     }
/*     */ 
/*     */     public long readLong() throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 319 */         return this.curBuf.getLong(); } catch (BufferUnderflowException e) {
/*     */       }
/* 321 */       return super.readLong();
/*     */     }
/*     */ 
/*     */     public long getFilePointer()
/*     */     {
/* 327 */       return (this.curBufIndex << this.chunkSizePower) + this.curBuf.position();
/*     */     }
/*     */ 
/*     */     public void seek(long pos)
/*     */       throws IOException
/*     */     {
/* 333 */       int bi = (int)(pos >> this.chunkSizePower);
/*     */       try {
/* 335 */         ByteBuffer b = this.buffers[bi];
/* 336 */         b.position((int)(pos & this.chunkSizeMask));
/*     */ 
/* 338 */         this.curBufIndex = bi;
/* 339 */         this.curBuf = b;
/*     */       } catch (ArrayIndexOutOfBoundsException aioobe) {
/* 341 */         if (pos < 0L)
/* 342 */           throw new IllegalArgumentException("Seeking to negative position");
/* 343 */         throw new IOException("seek past EOF");
/*     */       } catch (IllegalArgumentException iae) {
/* 345 */         if (pos < 0L)
/* 346 */           throw new IllegalArgumentException("Seeking to negative position");
/* 347 */         throw new IOException("seek past EOF");
/*     */       }
/*     */     }
/*     */ 
/*     */     public long length()
/*     */     {
/* 353 */       return this.length;
/*     */     }
/*     */ 
/*     */     public Object clone()
/*     */     {
/* 358 */       if (this.buffers == null)
/* 359 */         throw new AlreadyClosedException("MMapIndexInput already closed");
/* 360 */       MMapIndexInput clone = (MMapIndexInput)super.clone();
/* 361 */       clone.isClone = true;
/* 362 */       clone.buffers = new ByteBuffer[this.buffers.length];
/*     */ 
/* 365 */       for (int bufNr = 0; bufNr < this.buffers.length; bufNr++)
/* 366 */         clone.buffers[bufNr] = this.buffers[bufNr].duplicate();
/*     */       try
/*     */       {
/* 369 */         clone.seek(getFilePointer());
/*     */       } catch (IOException ioe) {
/* 371 */         throw new RuntimeException("Should never happen", ioe);
/*     */       }
/* 373 */       return clone;
/*     */     }
/*     */ 
/*     */     public void close() throws IOException
/*     */     {
/*     */       try {
/* 379 */         if ((this.isClone) || (this.buffers == null)) jsr 70;
/* 380 */         for (int bufNr = 0; bufNr < this.buffers.length; bufNr++)
/*     */           try
/*     */           {
/* 383 */             MMapDirectory.this.cleanMapping(this.buffers[bufNr]);
/*     */           } finally {
/* 385 */             this.buffers[bufNr] = null;
/*     */           }
/*     */       }
/*     */       finally {
/* 389 */         this.buffers = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.MMapDirectory
 * JD-Core Version:    0.6.0
 */