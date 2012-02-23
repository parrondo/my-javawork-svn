/*    */ package com.dukascopy.dds2.greed.gui.settings.utils;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class FastByteArrayOutputStream extends OutputStream
/*    */ {
/* 11 */   protected byte[] buf = null;
/* 12 */   protected int size = 0;
/*    */ 
/*    */   public FastByteArrayOutputStream() {
/* 15 */     this(5120);
/*    */   }
/*    */ 
/*    */   public FastByteArrayOutputStream(int initSize) {
/* 19 */     this.size = 0;
/* 20 */     this.buf = new byte[initSize];
/*    */   }
/*    */ 
/*    */   private void verifyBufferSize(int sz) {
/* 24 */     if (sz > this.buf.length) {
/* 25 */       byte[] old = this.buf;
/* 26 */       this.buf = new byte[Math.max(sz, 2 * this.buf.length)];
/* 27 */       System.arraycopy(old, 0, this.buf, 0, old.length);
/* 28 */       old = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getSize() {
/* 33 */     return this.size;
/*    */   }
/*    */ 
/*    */   public byte[] getByteArray() {
/* 37 */     return this.buf;
/*    */   }
/*    */ 
/*    */   public final void write(byte[] b) {
/* 41 */     verifyBufferSize(this.size + b.length);
/* 42 */     System.arraycopy(b, 0, this.buf, this.size, b.length);
/* 43 */     this.size += b.length;
/*    */   }
/*    */ 
/*    */   public final void write(byte[] b, int off, int len) {
/* 47 */     verifyBufferSize(this.size + len);
/* 48 */     System.arraycopy(b, off, this.buf, this.size, len);
/* 49 */     this.size += len;
/*    */   }
/*    */ 
/*    */   public final void write(int b) {
/* 53 */     verifyBufferSize(this.size + 1);
/* 54 */     this.buf[(this.size++)] = (byte)b;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 58 */     this.size = 0;
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream() {
/* 62 */     return new FastByteArrayInputStream(this.buf, this.size);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.utils.FastByteArrayOutputStream
 * JD-Core Version:    0.6.0
 */