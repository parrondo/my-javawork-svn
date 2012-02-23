/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileDescriptor;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ 
/*     */ public class SimpleFSDirectory extends FSDirectory
/*     */ {
/*     */   public SimpleFSDirectory(File path, LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/*  40 */     super(path, lockFactory);
/*     */   }
/*     */ 
/*     */   public SimpleFSDirectory(File path)
/*     */     throws IOException
/*     */   {
/*  49 */     super(path, null);
/*     */   }
/*     */ 
/*     */   public IndexInput openInput(String name, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  55 */     ensureOpen();
/*  56 */     return new SimpleFSIndexInput(new File(this.directory, name), bufferSize, getReadChunkSize());
/*     */   }
/*     */ 
/*     */   protected static class SimpleFSIndexInput extends BufferedIndexInput
/*     */   {
/*     */     protected final Descriptor file;
/*     */     boolean isClone;
/*     */     protected final int chunkSize;
/*     */ 
/*     */     public SimpleFSIndexInput(File path, int bufferSize, int chunkSize)
/*     */       throws IOException
/*     */     {
/*  89 */       super();
/*  90 */       this.file = new Descriptor(path, "r");
/*  91 */       this.chunkSize = chunkSize;
/*     */     }
/*     */ 
/*     */     protected void readInternal(byte[] b, int offset, int len)
/*     */       throws IOException
/*     */     {
/*  98 */       synchronized (this.file) {
/*  99 */         long position = getFilePointer();
/* 100 */         if (position != this.file.position) {
/* 101 */           this.file.seek(position);
/* 102 */           this.file.position = position;
/*     */         }
/* 104 */         int total = 0;
/*     */         try
/*     */         {
/*     */           do
/*     */           {
/*     */             int readLength;
/*     */             int readLength;
/* 109 */             if (total + this.chunkSize > len) {
/* 110 */               readLength = len - total;
/*     */             }
/*     */             else {
/* 113 */               readLength = this.chunkSize;
/*     */             }
/* 115 */             int i = this.file.read(b, offset + total, readLength);
/* 116 */             if (i == -1) {
/* 117 */               throw new IOException("read past EOF");
/*     */             }
/* 119 */             this.file.position += i;
/* 120 */             total += i;
/* 121 */           }while (total < len);
/*     */         }
/*     */         catch (OutOfMemoryError e)
/*     */         {
/* 125 */           OutOfMemoryError outOfMemoryError = new OutOfMemoryError("OutOfMemoryError likely caused by the Sun VM Bug described in https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize with a value smaller than the current chunk size (" + this.chunkSize + ")");
/*     */ 
/* 129 */           outOfMemoryError.initCause(e);
/* 130 */           throw outOfMemoryError;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 138 */       if (!this.isClone) this.file.close();
/*     */     }
/*     */ 
/*     */     protected void seekInternal(long position)
/*     */     {
/*     */     }
/*     */ 
/*     */     public long length()
/*     */     {
/* 147 */       return this.file.length;
/*     */     }
/*     */ 
/*     */     public Object clone()
/*     */     {
/* 152 */       SimpleFSIndexInput clone = (SimpleFSIndexInput)super.clone();
/* 153 */       clone.isClone = true;
/* 154 */       return clone;
/*     */     }
/*     */ 
/*     */     boolean isFDValid()
/*     */       throws IOException
/*     */     {
/* 161 */       return this.file.getFD().valid();
/*     */     }
/*     */ 
/*     */     public void copyBytes(IndexOutput out, long numBytes) throws IOException
/*     */     {
/* 166 */       numBytes -= flushBuffer(out, numBytes);
/*     */ 
/* 168 */       out.copyBytes(this, numBytes);
/*     */     }
/*     */ 
/*     */     protected static class Descriptor extends RandomAccessFile
/*     */     {
/*     */       protected volatile boolean isOpen;
/*     */       long position;
/*     */       final long length;
/*     */ 
/*     */       public Descriptor(File file, String mode)
/*     */         throws IOException
/*     */       {
/*  69 */         super(mode);
/*  70 */         this.isOpen = true;
/*  71 */         this.length = length();
/*     */       }
/*     */ 
/*     */       public void close() throws IOException
/*     */       {
/*  76 */         if (this.isOpen) {
/*  77 */           this.isOpen = false;
/*  78 */           super.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.SimpleFSDirectory
 * JD-Core Version:    0.6.0
 */