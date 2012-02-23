/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ 
/*     */ public class NIOFSDirectory extends FSDirectory
/*     */ {
/*     */   public NIOFSDirectory(File path, LockFactory lockFactory)
/*     */     throws IOException
/*     */   {
/*  62 */     super(path, lockFactory);
/*     */   }
/*     */ 
/*     */   public NIOFSDirectory(File path)
/*     */     throws IOException
/*     */   {
/*  71 */     super(path, null);
/*     */   }
/*     */ 
/*     */   public IndexInput openInput(String name, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  77 */     ensureOpen();
/*  78 */     return new NIOFSIndexInput(new File(getDirectory(), name), bufferSize, getReadChunkSize());
/*     */   }
/*     */   protected static class NIOFSIndexInput extends SimpleFSDirectory.SimpleFSIndexInput {
/*     */     private ByteBuffer byteBuf;
/*     */     private byte[] otherBuffer;
/*     */     private ByteBuffer otherByteBuf;
/*     */     final FileChannel channel;
/*     */ 
/*     */     public NIOFSIndexInput(File path, int bufferSize, int chunkSize) throws IOException {
/*  91 */       super(bufferSize, chunkSize);
/*  92 */       this.channel = this.file.getChannel();
/*     */     }
/*     */ 
/*     */     protected void newBuffer(byte[] newBuffer)
/*     */     {
/*  97 */       super.newBuffer(newBuffer);
/*  98 */       this.byteBuf = ByteBuffer.wrap(newBuffer);
/*     */     }
/*     */ 
/*     */     public void close() throws IOException
/*     */     {
/* 103 */       if ((!this.isClone) && (this.file.isOpen))
/*     */         try
/*     */         {
/* 106 */           this.channel.close();
/*     */         } finally {
/* 108 */           this.file.close();
/*     */         }
/*     */     }
/*     */ 
/*     */     protected void readInternal(byte[] b, int offset, int len)
/*     */       throws IOException
/*     */     {
/*     */       ByteBuffer bb;
/*     */       ByteBuffer bb;
/* 119 */       if ((b == this.buffer) && (0 == offset))
/*     */       {
/* 121 */         assert (this.byteBuf != null);
/* 122 */         this.byteBuf.clear();
/* 123 */         this.byteBuf.limit(len);
/* 124 */         bb = this.byteBuf;
/*     */       }
/*     */       else
/*     */       {
/*     */         ByteBuffer bb;
/* 126 */         if (offset == 0) {
/* 127 */           if (this.otherBuffer != b)
/*     */           {
/* 132 */             this.otherBuffer = b;
/* 133 */             this.otherByteBuf = ByteBuffer.wrap(b);
/*     */           } else {
/* 135 */             this.otherByteBuf.clear();
/* 136 */           }this.otherByteBuf.limit(len);
/* 137 */           bb = this.otherByteBuf;
/*     */         }
/*     */         else {
/* 140 */           bb = ByteBuffer.wrap(b, offset, len);
/*     */         }
/*     */       }
/*     */ 
/* 144 */       int readOffset = bb.position();
/* 145 */       int readLength = bb.limit() - readOffset;
/* 146 */       assert (readLength == len);
/*     */ 
/* 148 */       long pos = getFilePointer();
/*     */       try
/*     */       {
/* 151 */         while (readLength > 0)
/*     */         {
/*     */           int limit;
/*     */           int limit;
/* 153 */           if (readLength > this.chunkSize)
/*     */           {
/* 156 */             limit = readOffset + this.chunkSize;
/*     */           }
/* 158 */           else limit = readOffset + readLength;
/*     */ 
/* 160 */           bb.limit(limit);
/* 161 */           int i = this.channel.read(bb, pos);
/* 162 */           if (i == -1) {
/* 163 */             throw new IOException("read past EOF");
/*     */           }
/* 165 */           pos += i;
/* 166 */           readOffset += i;
/* 167 */           readLength -= i;
/*     */         }
/*     */       }
/*     */       catch (OutOfMemoryError e)
/*     */       {
/* 172 */         OutOfMemoryError outOfMemoryError = new OutOfMemoryError("OutOfMemoryError likely caused by the Sun VM Bug described in https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize with a value smaller than the current chunk size (" + this.chunkSize + ")");
/*     */ 
/* 176 */         outOfMemoryError.initCause(e);
/* 177 */         throw outOfMemoryError;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.NIOFSDirectory
 * JD-Core Version:    0.6.0
 */