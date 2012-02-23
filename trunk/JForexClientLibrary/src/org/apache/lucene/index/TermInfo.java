/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ class TermInfo
/*    */ {
/* 24 */   int docFreq = 0;
/*    */ 
/* 26 */   long freqPointer = 0L;
/* 27 */   long proxPointer = 0L;
/*    */   int skipOffset;
/*    */ 
/*    */   TermInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   TermInfo(int df, long fp, long pp)
/*    */   {
/* 33 */     this.docFreq = df;
/* 34 */     this.freqPointer = fp;
/* 35 */     this.proxPointer = pp;
/*    */   }
/*    */ 
/*    */   TermInfo(TermInfo ti) {
/* 39 */     this.docFreq = ti.docFreq;
/* 40 */     this.freqPointer = ti.freqPointer;
/* 41 */     this.proxPointer = ti.proxPointer;
/* 42 */     this.skipOffset = ti.skipOffset;
/*    */   }
/*    */ 
/*    */   final void set(int docFreq, long freqPointer, long proxPointer, int skipOffset)
/*    */   {
/* 47 */     this.docFreq = docFreq;
/* 48 */     this.freqPointer = freqPointer;
/* 49 */     this.proxPointer = proxPointer;
/* 50 */     this.skipOffset = skipOffset;
/*    */   }
/*    */ 
/*    */   final void set(TermInfo ti) {
/* 54 */     this.docFreq = ti.docFreq;
/* 55 */     this.freqPointer = ti.freqPointer;
/* 56 */     this.proxPointer = ti.proxPointer;
/* 57 */     this.skipOffset = ti.skipOffset;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermInfo
 * JD-Core Version:    0.6.0
 */