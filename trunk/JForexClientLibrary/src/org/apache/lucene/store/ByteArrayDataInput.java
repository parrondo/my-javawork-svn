/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import org.apache.lucene.util.BytesRef;
/*     */ 
/*     */ public final class ByteArrayDataInput extends DataInput
/*     */ {
/*     */   private byte[] bytes;
/*     */   private int pos;
/*     */   private int limit;
/*     */ 
/*     */   public ByteArrayDataInput(byte[] bytes)
/*     */   {
/*  31 */     reset(bytes);
/*     */   }
/*     */ 
/*     */   public ByteArrayDataInput(byte[] bytes, int offset, int len) {
/*  35 */     reset(bytes, offset, len);
/*     */   }
/*     */ 
/*     */   public ByteArrayDataInput() {
/*  39 */     reset(BytesRef.EMPTY_BYTES);
/*     */   }
/*     */ 
/*     */   public void reset(byte[] bytes) {
/*  43 */     reset(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public int getPosition() {
/*  47 */     return this.pos;
/*     */   }
/*     */ 
/*     */   public void reset(byte[] bytes, int offset, int len) {
/*  51 */     this.bytes = bytes;
/*  52 */     this.pos = offset;
/*  53 */     this.limit = (offset + len);
/*     */   }
/*     */ 
/*     */   public boolean eof() {
/*  57 */     return this.pos == this.limit;
/*     */   }
/*     */ 
/*     */   public void skipBytes(int count) {
/*  61 */     this.pos += count;
/*  62 */     assert (this.pos <= this.limit);
/*     */   }
/*     */ 
/*     */   public short readShort()
/*     */   {
/*  67 */     return (short)((this.bytes[(this.pos++)] & 0xFF) << 8 | this.bytes[(this.pos++)] & 0xFF);
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */   {
/*  72 */     assert (this.pos + 4 <= this.limit);
/*  73 */     return (this.bytes[(this.pos++)] & 0xFF) << 24 | (this.bytes[(this.pos++)] & 0xFF) << 16 | (this.bytes[(this.pos++)] & 0xFF) << 8 | this.bytes[(this.pos++)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public long readLong()
/*     */   {
/*  79 */     assert (this.pos + 8 <= this.limit);
/*  80 */     int i1 = (this.bytes[(this.pos++)] & 0xFF) << 24 | (this.bytes[(this.pos++)] & 0xFF) << 16 | (this.bytes[(this.pos++)] & 0xFF) << 8 | this.bytes[(this.pos++)] & 0xFF;
/*     */ 
/*  82 */     int i2 = (this.bytes[(this.pos++)] & 0xFF) << 24 | (this.bytes[(this.pos++)] & 0xFF) << 16 | (this.bytes[(this.pos++)] & 0xFF) << 8 | this.bytes[(this.pos++)] & 0xFF;
/*     */ 
/*  84 */     return i1 << 32 | i2 & 0xFFFFFFFF;
/*     */   }
/*     */ 
/*     */   public int readVInt()
/*     */   {
/*  89 */     checkBounds();
/*  90 */     byte b = this.bytes[(this.pos++)];
/*  91 */     int i = b & 0x7F;
/*  92 */     for (int shift = 7; (b & 0x80) != 0; shift += 7) {
/*  93 */       checkBounds();
/*  94 */       b = this.bytes[(this.pos++)];
/*  95 */       i |= (b & 0x7F) << shift;
/*     */     }
/*  97 */     return i;
/*     */   }
/*     */ 
/*     */   public long readVLong()
/*     */   {
/* 102 */     checkBounds();
/* 103 */     byte b = this.bytes[(this.pos++)];
/* 104 */     long i = b & 0x7F;
/* 105 */     for (int shift = 7; (b & 0x80) != 0; shift += 7) {
/* 106 */       checkBounds();
/* 107 */       b = this.bytes[(this.pos++)];
/* 108 */       i |= (b & 0x7F) << shift;
/*     */     }
/* 110 */     return i;
/*     */   }
/*     */ 
/*     */   public byte readByte()
/*     */   {
/* 116 */     checkBounds();
/* 117 */     return this.bytes[(this.pos++)];
/*     */   }
/*     */ 
/*     */   public void readBytes(byte[] b, int offset, int len)
/*     */   {
/* 123 */     assert (this.pos + len <= this.limit);
/* 124 */     System.arraycopy(this.bytes, this.pos, b, offset, len);
/* 125 */     this.pos += len;
/*     */   }
/*     */ 
/*     */   private boolean checkBounds() {
/* 129 */     assert (this.pos < this.limit);
/* 130 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.ByteArrayDataInput
 * JD-Core Version:    0.6.0
 */