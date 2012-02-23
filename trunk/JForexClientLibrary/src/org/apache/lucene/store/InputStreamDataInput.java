/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class InputStreamDataInput extends DataInput
/*    */   implements Closeable
/*    */ {
/*    */   private final InputStream is;
/*    */ 
/*    */   public InputStreamDataInput(InputStream is)
/*    */   {
/* 31 */     this.is = is;
/*    */   }
/*    */ 
/*    */   public byte readByte() throws IOException
/*    */   {
/* 36 */     int v = this.is.read();
/* 37 */     if (v == -1) throw new EOFException();
/* 38 */     return (byte)v;
/*    */   }
/*    */ 
/*    */   public void readBytes(byte[] b, int offset, int len) throws IOException
/*    */   {
/* 43 */     while (len > 0) {
/* 44 */       int cnt = this.is.read(b, offset, len);
/* 45 */       if (cnt < 0)
/*    */       {
/* 47 */         throw new EOFException();
/*    */       }
/* 49 */       len -= cnt;
/* 50 */       offset += cnt;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 56 */     this.is.close();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.InputStreamDataInput
 * JD-Core Version:    0.6.0
 */