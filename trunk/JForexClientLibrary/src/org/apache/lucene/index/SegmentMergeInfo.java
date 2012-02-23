/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ final class SegmentMergeInfo
/*    */ {
/*    */   Term term;
/*    */   int base;
/*    */   int ord;
/*    */   TermEnum termEnum;
/*    */   IndexReader reader;
/*    */   int delCount;
/*    */   private TermPositions postings;
/*    */   private int[] docMap;
/*    */   PayloadProcessorProvider.DirPayloadProcessor dirPayloadProcessor;
/*    */ 
/*    */   SegmentMergeInfo(int b, TermEnum te, IndexReader r)
/*    */     throws IOException
/*    */   {
/* 37 */     this.base = b;
/* 38 */     this.reader = r;
/* 39 */     this.termEnum = te;
/* 40 */     this.term = te.term();
/*    */   }
/*    */ 
/*    */   int[] getDocMap()
/*    */   {
/* 45 */     if (this.docMap == null) {
/* 46 */       this.delCount = 0;
/*    */ 
/* 48 */       if (this.reader.hasDeletions()) {
/* 49 */         int maxDoc = this.reader.maxDoc();
/* 50 */         this.docMap = new int[maxDoc];
/* 51 */         int j = 0;
/* 52 */         for (int i = 0; i < maxDoc; i++)
/* 53 */           if (this.reader.isDeleted(i)) {
/* 54 */             this.delCount += 1;
/* 55 */             this.docMap[i] = -1;
/*    */           } else {
/* 57 */             this.docMap[i] = (j++);
/*    */           }
/*    */       }
/*    */     }
/* 61 */     return this.docMap;
/*    */   }
/*    */ 
/*    */   TermPositions getPositions() throws IOException {
/* 65 */     if (this.postings == null) {
/* 66 */       this.postings = this.reader.termPositions();
/*    */     }
/* 68 */     return this.postings;
/*    */   }
/*    */ 
/*    */   final boolean next() throws IOException {
/* 72 */     if (this.termEnum.next()) {
/* 73 */       this.term = this.termEnum.term();
/* 74 */       return true;
/*    */     }
/* 76 */     this.term = null;
/* 77 */     return false;
/*    */   }
/*    */ 
/*    */   final void close() throws IOException
/*    */   {
/*    */     try {
/* 83 */       this.termEnum.close();
/*    */     } finally {
/* 85 */       if (this.postings != null)
/* 86 */         this.postings.close();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentMergeInfo
 * JD-Core Version:    0.6.0
 */