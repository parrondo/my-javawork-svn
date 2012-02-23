/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.TermPositions;
/*    */ 
/*    */ final class PhrasePositions
/*    */ {
/*    */   int doc;
/*    */   int position;
/*    */   int count;
/*    */   int offset;
/*    */   final int ord;
/*    */   TermPositions tp;
/*    */   PhrasePositions next;
/*    */   boolean repeats;
/*    */ 
/*    */   PhrasePositions(TermPositions t, int o, int ord)
/*    */   {
/* 37 */     this.tp = t;
/* 38 */     this.offset = o;
/* 39 */     this.ord = ord;
/*    */   }
/*    */ 
/*    */   final boolean next() throws IOException {
/* 43 */     if (!this.tp.next()) {
/* 44 */       this.tp.close();
/* 45 */       this.doc = 2147483647;
/* 46 */       return false;
/*    */     }
/* 48 */     this.doc = this.tp.doc();
/* 49 */     this.position = 0;
/* 50 */     return true;
/*    */   }
/*    */ 
/*    */   final boolean skipTo(int target) throws IOException {
/* 54 */     if (!this.tp.skipTo(target)) {
/* 55 */       this.tp.close();
/* 56 */       this.doc = 2147483647;
/* 57 */       return false;
/*    */     }
/* 59 */     this.doc = this.tp.doc();
/* 60 */     this.position = 0;
/* 61 */     return true;
/*    */   }
/*    */ 
/*    */   final void firstPosition() throws IOException
/*    */   {
/* 66 */     this.count = this.tp.freq();
/* 67 */     nextPosition();
/*    */   }
/*    */ 
/*    */   final boolean nextPosition()
/*    */     throws IOException
/*    */   {
/* 77 */     if (this.count-- > 0) {
/* 78 */       this.position = (this.tp.nextPosition() - this.offset);
/* 79 */       return true;
/*    */     }
/* 81 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PhrasePositions
 * JD-Core Version:    0.6.0
 */