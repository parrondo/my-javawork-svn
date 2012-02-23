/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ public class RAMFile
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 28 */   protected ArrayList<byte[]> buffers = new ArrayList();
/*    */   long length;
/*    */   RAMDirectory directory;
/*    */   protected long sizeInBytes;
/* 34 */   private long lastModified = System.currentTimeMillis();
/*    */ 
/*    */   public RAMFile() {
/*    */   }
/*    */ 
/*    */   RAMFile(RAMDirectory directory) {
/* 40 */     this.directory = directory;
/*    */   }
/*    */ 
/*    */   public synchronized long getLength()
/*    */   {
/* 45 */     return this.length;
/*    */   }
/*    */ 
/*    */   protected synchronized void setLength(long length) {
/* 49 */     this.length = length;
/*    */   }
/*    */ 
/*    */   public synchronized long getLastModified()
/*    */   {
/* 54 */     return this.lastModified;
/*    */   }
/*    */ 
/*    */   protected synchronized void setLastModified(long lastModified) {
/* 58 */     this.lastModified = lastModified;
/*    */   }
/*    */ 
/*    */   protected final byte[] addBuffer(int size) {
/* 62 */     byte[] buffer = newBuffer(size);
/* 63 */     synchronized (this) {
/* 64 */       this.buffers.add(buffer);
/* 65 */       this.sizeInBytes += size;
/*    */     }
/*    */ 
/* 68 */     if (this.directory != null) {
/* 69 */       this.directory.sizeInBytes.getAndAdd(size);
/*    */     }
/* 71 */     return buffer;
/*    */   }
/*    */ 
/*    */   protected final synchronized byte[] getBuffer(int index) {
/* 75 */     return (byte[])this.buffers.get(index);
/*    */   }
/*    */ 
/*    */   protected final synchronized int numBuffers() {
/* 79 */     return this.buffers.size();
/*    */   }
/*    */ 
/*    */   protected byte[] newBuffer(int size)
/*    */   {
/* 89 */     return new byte[size];
/*    */   }
/*    */ 
/*    */   public synchronized long getSizeInBytes() {
/* 93 */     return this.sizeInBytes;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.RAMFile
 * JD-Core Version:    0.6.0
 */