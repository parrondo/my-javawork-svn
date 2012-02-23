/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import org.apache.lucene.util.BytesRef;
/*    */ 
/*    */ public class ByteArrayDataOutput extends DataOutput
/*    */ {
/*    */   private byte[] bytes;
/*    */   private int pos;
/*    */   private int limit;
/*    */ 
/*    */   public ByteArrayDataOutput(byte[] bytes)
/*    */   {
/* 15 */     reset(bytes);
/*    */   }
/*    */ 
/*    */   public ByteArrayDataOutput(byte[] bytes, int offset, int len) {
/* 19 */     reset(bytes, offset, len);
/*    */   }
/*    */ 
/*    */   public ByteArrayDataOutput() {
/* 23 */     reset(BytesRef.EMPTY_BYTES);
/*    */   }
/*    */ 
/*    */   public void reset(byte[] bytes) {
/* 27 */     reset(bytes, 0, bytes.length);
/*    */   }
/*    */ 
/*    */   public void reset(byte[] bytes, int offset, int len) {
/* 31 */     this.bytes = bytes;
/* 32 */     this.pos = offset;
/* 33 */     this.limit = (offset + len);
/*    */   }
/*    */ 
/*    */   public int getPosition() {
/* 37 */     return this.pos;
/*    */   }
/*    */ 
/*    */   public void writeByte(byte b)
/*    */   {
/* 42 */     assert (this.pos < this.limit);
/* 43 */     this.bytes[(this.pos++)] = b;
/*    */   }
/*    */ 
/*    */   public void writeBytes(byte[] b, int offset, int length)
/*    */   {
/* 48 */     assert (this.pos + length <= this.limit);
/* 49 */     System.arraycopy(b, offset, this.bytes, this.pos, length);
/* 50 */     this.pos += length;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.ByteArrayDataOutput
 * JD-Core Version:    0.6.0
 */