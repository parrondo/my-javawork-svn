/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class IndexInput extends DataInput
/*    */   implements Cloneable, Closeable
/*    */ {
/* 29 */   protected byte[] copyBuf = null;
/*    */ 
/*    */   @Deprecated
/*    */   public void skipChars(int length)
/*    */     throws IOException
/*    */   {
/* 44 */     for (int i = 0; i < length; i++) {
/* 45 */       byte b = readByte();
/* 46 */       if ((b & 0x80) == 0)
/*    */         continue;
/* 48 */       if ((b & 0xE0) != 224) {
/* 49 */         readByte();
/*    */       }
/*    */       else {
/* 52 */         readByte();
/* 53 */         readByte();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public abstract void close()
/*    */     throws IOException;
/*    */ 
/*    */   public abstract long getFilePointer();
/*    */ 
/*    */   public abstract void seek(long paramLong)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract long length();
/*    */ 
/*    */   public void copyBytes(IndexOutput out, long numBytes)
/*    */     throws IOException
/*    */   {
/* 87 */     assert (numBytes >= 0L) : ("numBytes=" + numBytes);
/*    */ 
/* 89 */     if (this.copyBuf == null) {
/* 90 */       this.copyBuf = new byte[1024];
/*    */     }
/*    */ 
/* 93 */     while (numBytes > 0L) {
/* 94 */       int toCopy = (int)(numBytes > this.copyBuf.length ? this.copyBuf.length : numBytes);
/* 95 */       readBytes(this.copyBuf, 0, toCopy);
/* 96 */       out.writeBytes(this.copyBuf, 0, toCopy);
/* 97 */       numBytes -= toCopy;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.IndexInput
 * JD-Core Version:    0.6.0
 */