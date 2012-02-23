/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.zip.CRC32;
/*    */ import java.util.zip.Checksum;
/*    */ 
/*    */ public class ChecksumIndexInput extends IndexInput
/*    */ {
/*    */   IndexInput main;
/*    */   Checksum digest;
/*    */ 
/*    */   public ChecksumIndexInput(IndexInput main)
/*    */   {
/* 34 */     this.main = main;
/* 35 */     this.digest = new CRC32();
/*    */   }
/*    */ 
/*    */   public byte readByte() throws IOException
/*    */   {
/* 40 */     byte b = this.main.readByte();
/* 41 */     this.digest.update(b);
/* 42 */     return b;
/*    */   }
/*    */ 
/*    */   public void readBytes(byte[] b, int offset, int len)
/*    */     throws IOException
/*    */   {
/* 48 */     this.main.readBytes(b, offset, len);
/* 49 */     this.digest.update(b, offset, len);
/*    */   }
/*    */ 
/*    */   public long getChecksum()
/*    */   {
/* 54 */     return this.digest.getValue();
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 59 */     this.main.close();
/*    */   }
/*    */ 
/*    */   public long getFilePointer()
/*    */   {
/* 64 */     return this.main.getFilePointer();
/*    */   }
/*    */ 
/*    */   public void seek(long pos)
/*    */   {
/* 69 */     throw new RuntimeException("not allowed");
/*    */   }
/*    */ 
/*    */   public long length()
/*    */   {
/* 74 */     return this.main.length();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.ChecksumIndexInput
 * JD-Core Version:    0.6.0
 */