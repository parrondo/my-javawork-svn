/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.Reader;
/*    */ 
/*    */ final class ReusableStringReader extends Reader
/*    */ {
/*    */   int upto;
/*    */   int left;
/*    */   String s;
/*    */ 
/*    */   void init(String s)
/*    */   {
/* 30 */     this.s = s;
/* 31 */     this.left = s.length();
/* 32 */     this.upto = 0;
/*    */   }
/*    */ 
/*    */   public int read(char[] c) {
/* 36 */     return read(c, 0, c.length);
/*    */   }
/*    */ 
/*    */   public int read(char[] c, int off, int len) {
/* 40 */     if (this.left > len) {
/* 41 */       this.s.getChars(this.upto, this.upto + len, c, off);
/* 42 */       this.upto += len;
/* 43 */       this.left -= len;
/* 44 */       return len;
/* 45 */     }if (0 == this.left)
/*    */     {
/* 47 */       this.s = null;
/* 48 */       return -1;
/*    */     }
/* 50 */     this.s.getChars(this.upto, this.upto + this.left, c, off);
/* 51 */     int r = this.left;
/* 52 */     this.left = 0;
/* 53 */     this.upto = this.s.length();
/* 54 */     return r;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.ReusableStringReader
 * JD-Core Version:    0.6.0
 */