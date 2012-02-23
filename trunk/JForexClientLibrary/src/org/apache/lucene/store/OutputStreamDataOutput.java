/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class OutputStreamDataOutput extends DataOutput
/*    */   implements Closeable
/*    */ {
/*    */   private final OutputStream os;
/*    */ 
/*    */   public OutputStreamDataOutput(OutputStream os)
/*    */   {
/* 29 */     this.os = os;
/*    */   }
/*    */ 
/*    */   public void writeByte(byte b) throws IOException
/*    */   {
/* 34 */     this.os.write(b);
/*    */   }
/*    */ 
/*    */   public void writeBytes(byte[] b, int offset, int length) throws IOException
/*    */   {
/* 39 */     this.os.write(b, offset, length);
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 44 */     this.os.close();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.OutputStreamDataOutput
 * JD-Core Version:    0.6.0
 */