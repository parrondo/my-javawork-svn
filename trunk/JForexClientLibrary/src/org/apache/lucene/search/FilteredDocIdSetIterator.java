/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class FilteredDocIdSetIterator extends DocIdSetIterator
/*    */ {
/*    */   protected DocIdSetIterator _innerIter;
/*    */   private int doc;
/*    */ 
/*    */   public FilteredDocIdSetIterator(DocIdSetIterator innerIter)
/*    */   {
/* 37 */     if (innerIter == null) {
/* 38 */       throw new IllegalArgumentException("null iterator");
/*    */     }
/* 40 */     this._innerIter = innerIter;
/* 41 */     this.doc = -1;
/*    */   }
/*    */ 
/*    */   protected abstract boolean match(int paramInt)
/*    */     throws IOException;
/*    */ 
/*    */   public int docID()
/*    */   {
/* 54 */     return this.doc;
/*    */   }
/*    */ 
/*    */   public int nextDoc() throws IOException
/*    */   {
/* 59 */     while ((this.doc = this._innerIter.nextDoc()) != 2147483647) {
/* 60 */       if (match(this.doc)) {
/* 61 */         return this.doc;
/*    */       }
/*    */     }
/* 64 */     return this.doc;
/*    */   }
/*    */ 
/*    */   public int advance(int target) throws IOException
/*    */   {
/* 69 */     this.doc = this._innerIter.advance(target);
/* 70 */     if (this.doc != 2147483647) {
/* 71 */       if (match(this.doc)) {
/* 72 */         return this.doc;
/*    */       }
/* 74 */       while ((this.doc = this._innerIter.nextDoc()) != 2147483647) {
/* 75 */         if (match(this.doc)) {
/* 76 */           return this.doc;
/*    */         }
/*    */       }
/* 79 */       return this.doc;
/*    */     }
/*    */ 
/* 82 */     return this.doc;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FilteredDocIdSetIterator
 * JD-Core Version:    0.6.0
 */