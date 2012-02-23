/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ final class ByteSliceWriter
/*    */ {
/*    */   private byte[] slice;
/*    */   private int upto;
/*    */   private final ByteBlockPool pool;
/*    */   int offset0;
/*    */ 
/*    */   public ByteSliceWriter(ByteBlockPool pool)
/*    */   {
/* 36 */     this.pool = pool;
/*    */   }
/*    */ 
/*    */   public void init(int address)
/*    */   {
/* 43 */     this.slice = this.pool.buffers[(address >> 15)];
/* 44 */     assert (this.slice != null);
/* 45 */     this.upto = (address & 0x7FFF);
/* 46 */     this.offset0 = address;
/* 47 */     assert (this.upto < this.slice.length);
/*    */   }
/*    */ 
/*    */   public void writeByte(byte b)
/*    */   {
/* 52 */     assert (this.slice != null);
/* 53 */     if (this.slice[this.upto] != 0) {
/* 54 */       this.upto = this.pool.allocSlice(this.slice, this.upto);
/* 55 */       this.slice = this.pool.buffer;
/* 56 */       this.offset0 = this.pool.byteOffset;
/* 57 */       assert (this.slice != null);
/*    */     }
/* 59 */     this.slice[(this.upto++)] = b;
/* 60 */     assert (this.upto != this.slice.length);
/*    */   }
/*    */ 
/*    */   public void writeBytes(byte[] b, int offset, int len) {
/* 64 */     int offsetEnd = offset + len;
/* 65 */     while (offset < offsetEnd) {
/* 66 */       if (this.slice[this.upto] != 0)
/*    */       {
/* 68 */         this.upto = this.pool.allocSlice(this.slice, this.upto);
/* 69 */         this.slice = this.pool.buffer;
/* 70 */         this.offset0 = this.pool.byteOffset;
/*    */       }
/*    */ 
/* 73 */       this.slice[(this.upto++)] = b[(offset++)];
/* 74 */       if (($assertionsDisabled) || (this.upto != this.slice.length)) continue; throw new AssertionError();
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getAddress() {
/* 79 */     return this.upto + (this.offset0 & 0xFFFF8000);
/*    */   }
/*    */ 
/*    */   public void writeVInt(int i) {
/* 83 */     while ((i & 0xFFFFFF80) != 0) {
/* 84 */       writeByte((byte)(i & 0x7F | 0x80));
/* 85 */       i >>>= 7;
/*    */     }
/* 87 */     writeByte((byte)i);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ByteSliceWriter
 * JD-Core Version:    0.6.0
 */