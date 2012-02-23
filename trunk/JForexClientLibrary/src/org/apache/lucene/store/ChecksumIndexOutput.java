/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Checksum;
/*     */ 
/*     */ public class ChecksumIndexOutput extends IndexOutput
/*     */ {
/*     */   IndexOutput main;
/*     */   Checksum digest;
/*     */ 
/*     */   public ChecksumIndexOutput(IndexOutput main)
/*     */   {
/*  34 */     this.main = main;
/*  35 */     this.digest = new CRC32();
/*     */   }
/*     */ 
/*     */   public void writeByte(byte b) throws IOException
/*     */   {
/*  40 */     this.digest.update(b);
/*  41 */     this.main.writeByte(b);
/*     */   }
/*     */ 
/*     */   public void writeBytes(byte[] b, int offset, int length) throws IOException
/*     */   {
/*  46 */     this.digest.update(b, offset, length);
/*  47 */     this.main.writeBytes(b, offset, length);
/*     */   }
/*     */ 
/*     */   public long getChecksum() {
/*  51 */     return this.digest.getValue();
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException
/*     */   {
/*  56 */     this.main.flush();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  61 */     this.main.close();
/*     */   }
/*     */ 
/*     */   public long getFilePointer()
/*     */   {
/*  66 */     return this.main.getFilePointer();
/*     */   }
/*     */ 
/*     */   public void seek(long pos)
/*     */   {
/*  71 */     throw new RuntimeException("not allowed");
/*     */   }
/*     */ 
/*     */   public void prepareCommit()
/*     */     throws IOException
/*     */   {
/*  81 */     long checksum = getChecksum();
/*     */ 
/*  87 */     long pos = this.main.getFilePointer();
/*  88 */     this.main.writeLong(checksum - 1L);
/*  89 */     this.main.flush();
/*  90 */     this.main.seek(pos);
/*     */   }
/*     */ 
/*     */   public void finishCommit() throws IOException
/*     */   {
/*  95 */     this.main.writeLong(getChecksum());
/*     */   }
/*     */ 
/*     */   public long length() throws IOException
/*     */   {
/* 100 */     return this.main.length();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.ChecksumIndexOutput
 * JD-Core Version:    0.6.0
 */