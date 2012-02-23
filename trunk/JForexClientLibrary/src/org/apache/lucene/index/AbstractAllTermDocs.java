/*    */ package org.apache.lucene.index;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class AbstractAllTermDocs
/*    */   implements TermDocs
/*    */ {
/*    */   protected int maxDoc;
/* 32 */   protected int doc = -1;
/*    */ 
/*    */   protected AbstractAllTermDocs(int maxDoc) {
/* 35 */     this.maxDoc = maxDoc;
/*    */   }
/*    */ 
/*    */   public void seek(Term term) throws IOException {
/* 39 */     if (term == null)
/* 40 */       this.doc = -1;
/*    */     else
/* 42 */       throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public void seek(TermEnum termEnum) throws IOException
/*    */   {
/* 47 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public int doc() {
/* 51 */     return this.doc;
/*    */   }
/*    */ 
/*    */   public int freq() {
/* 55 */     return 1;
/*    */   }
/*    */ 
/*    */   public boolean next() throws IOException {
/* 59 */     return skipTo(this.doc + 1);
/*    */   }
/*    */ 
/*    */   public int read(int[] docs, int[] freqs) throws IOException {
/* 63 */     int length = docs.length;
/* 64 */     int i = 0;
/* 65 */     while ((i < length) && (this.doc < this.maxDoc)) {
/* 66 */       if (!isDeleted(this.doc)) {
/* 67 */         docs[i] = this.doc;
/* 68 */         freqs[i] = 1;
/* 69 */         i++;
/*    */       }
/* 71 */       this.doc += 1;
/*    */     }
/* 73 */     return i;
/*    */   }
/*    */ 
/*    */   public boolean skipTo(int target) throws IOException {
/* 77 */     this.doc = target;
/* 78 */     while (this.doc < this.maxDoc) {
/* 79 */       if (!isDeleted(this.doc)) {
/* 80 */         return true;
/*    */       }
/* 82 */       this.doc += 1;
/*    */     }
/* 84 */     return false;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public abstract boolean isDeleted(int paramInt);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.AbstractAllTermDocs
 * JD-Core Version:    0.6.0
 */