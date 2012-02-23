/*    */ package com.dukascopy.dds2.greed.gui.settings.utils;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class FastByteArrayInputStream extends InputStream
/*    */ {
/* 11 */   protected byte[] buf = null;
/* 12 */   protected int count = 0;
/* 13 */   protected int pos = 0;
/*    */ 
/*    */   public FastByteArrayInputStream(byte[] buf, int count) {
/* 16 */     this.buf = buf;
/* 17 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public final int available() {
/* 21 */     return this.count - this.pos;
/*    */   }
/*    */ 
/*    */   public final int read() {
/* 25 */     return this.pos < this.count ? this.buf[(this.pos++)] & 0xFF : -1;
/*    */   }
/*    */ 
/*    */   public final int read(byte[] b, int off, int len) {
/* 29 */     if (this.pos >= this.count) {
/* 30 */       return -1;
/*    */     }
/*    */ 
/* 33 */     if (this.pos + len > this.count) {
/* 34 */       len = this.count - this.pos;
/*    */     }
/*    */ 
/* 37 */     System.arraycopy(this.buf, this.pos, b, off, len);
/* 38 */     this.pos += len;
/* 39 */     return len;
/*    */   }
/*    */ 
/*    */   public final long skip(long n) {
/* 43 */     if (this.pos + n > this.count) {
/* 44 */       n = this.count - this.pos;
/*    */     }
/* 46 */     if (n < 0L) {
/* 47 */       return 0L;
/*    */     }
/* 49 */     this.pos = (int)(this.pos + n);
/* 50 */     return n;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.utils.FastByteArrayInputStream
 * JD-Core Version:    0.6.0
 */